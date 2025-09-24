const EventEmitter = require('events');
const os = require('os');
const fs = require('fs').promises;
const path = require('path');

/**
 * Health Monitor System
 * Proactive monitoring to prevent errors before they occur
 */
class HealthMonitor extends EventEmitter {
  constructor(config, logger, components = {}) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('HealthMonitor') : logger;
    this.components = components;
    
    // Health configuration
    this.healthConfig = {
      checkInterval: 30000,           // Health check interval (30s)
      alertThresholds: {
        cpu: 85,                      // CPU usage percentage
        memory: 90,                   // Memory usage percentage
        disk: 95,                     // Disk usage percentage
        responseTime: 5000,           // Response time threshold (ms)
        errorRate: 10,                // Error rate percentage
        connectionCount: 1000         // Max connections
      },
      degradationThresholds: {
        cpu: 70,
        memory: 75,
        disk: 80,
        responseTime: 2000,
        errorRate: 5
      },
      historySize: 100,               // Number of health records to keep
      alertCooldown: 300000,          // 5 minutes between similar alerts
      ...this.config.healthMonitoring
    };
    
    // Health state
    this.healthHistory = [];
    this.alerts = new Map();
    this.lastAlerts = new Map();
    this.systemMetrics = {
      cpu: { current: 0, average: 0, peak: 0 },
      memory: { current: 0, average: 0, peak: 0 },
      disk: { current: 0, average: 0, peak: 0 },
      network: { bytesIn: 0, bytesOut: 0 },
      uptime: 0
    };
    
    // Component health states
    this.componentHealth = new Map();
    
    // Health check functions
    this.healthChecks = new Map();
    this.initializeDefaultHealthChecks();
    
    this.monitoringActive = false;
    this.monitoringInterval = null;
  }

  async initialize() {
    if (this.monitoringActive) return;
    
    this.logger.info('Initializing health monitoring system...');
    
    try {
      // Load historical health data
      await this.loadHealthHistory();
      
      // Start monitoring
      this.startMonitoring();
      
      this.monitoringActive = true;
      this.logger.info('Health monitoring system initialized successfully');
      this.emit('monitoring_started');
      
    } catch (error) {
      this.logger.error('Failed to initialize health monitoring system:', error);
      throw error;
    }
  }

  async stop() {
    if (!this.monitoringActive) return;
    
    this.logger.info('Stopping health monitoring system...');
    
    if (this.monitoringInterval) {
      clearInterval(this.monitoringInterval);
      this.monitoringInterval = null;
    }
    
    // Save health history
    await this.saveHealthHistory();
    
    this.monitoringActive = false;
    this.logger.info('Health monitoring system stopped');
    this.emit('monitoring_stopped');
  }

  startMonitoring() {
    // Initial health check
    this.performHealthCheck();
    
    // Start periodic monitoring
    this.monitoringInterval = setInterval(() => {
      this.performHealthCheck();
    }, this.healthConfig.checkInterval);
  }

  async performHealthCheck() {
    const timestamp = Date.now();
    const healthRecord = {
      timestamp,
      system: await this.checkSystemHealth(),
      components: await this.checkComponentsHealth(),
      overall: 'healthy'
    };
    
    // Determine overall health status
    healthRecord.overall = this.determineOverallHealth(healthRecord);
    
    // Update health history
    this.healthHistory.push(healthRecord);
    if (this.healthHistory.length > this.healthConfig.historySize) {
      this.healthHistory.shift();
    }
    
    // Update system metrics
    this.updateSystemMetrics(healthRecord.system);
    
    // Check for alerts
    await this.checkHealthAlerts(healthRecord);
    
    // Emit health check event
    this.emit('health_check_completed', healthRecord);
    
    return healthRecord;
  }

  async checkSystemHealth() {
    const cpus = os.cpus();
    const totalMem = os.totalmem();
    const freeMem = os.freemem();
    const uptime = os.uptime();
    
    // Calculate CPU usage (simplified)
    const cpuUsage = await this.getCPUUsage();
    
    // Memory usage
    const memoryUsage = ((totalMem - freeMem) / totalMem) * 100;
    
    // Disk usage
    const diskUsage = await this.getDiskUsage();
    
    // Network stats (if available)
    const networkStats = await this.getNetworkStats();
    
    return {
      cpu: {
        usage: parseFloat(cpuUsage.toFixed(2)),
        cores: cpus.length,
        load: os.loadavg()
      },
      memory: {
        total: totalMem,
        free: freeMem,
        used: totalMem - freeMem,
        usage: parseFloat(memoryUsage.toFixed(2))
      },
      disk: {
        usage: diskUsage,
        free: diskUsage.free,
        total: diskUsage.total
      },
      network: networkStats,
      uptime: uptime,
      platform: os.platform(),
      arch: os.arch()
    };
  }

  async checkComponentsHealth() {
    const componentHealth = {};
    
    // Check each registered component
    for (const [name, component] of Object.entries(this.components)) {
      try {
        componentHealth[name] = await this.checkComponentHealth(name, component);
      } catch (error) {
        componentHealth[name] = {
          status: 'unhealthy',
          error: error.message,
          lastCheck: Date.now()
        };
      }
    }
    
    // Run custom health checks
    for (const [name, healthCheck] of this.healthChecks.entries()) {
      try {
        componentHealth[name] = await healthCheck();
      } catch (error) {
        componentHealth[name] = {
          status: 'unhealthy',
          error: error.message,
          lastCheck: Date.now()
        };
      }
    }
    
    return componentHealth;
  }

  async checkComponentHealth(name, component) {
    const healthInfo = {
      status: 'healthy',
      lastCheck: Date.now(),
      details: {}
    };
    
    // Use component's built-in health check if available
    if (component.getHealth && typeof component.getHealth === 'function') {
      try {
        const componentHealthData = await component.getHealth();
        return {
          ...healthInfo,
          ...componentHealthData
        };
      } catch (error) {
        return {
          ...healthInfo,
          status: 'unhealthy',
          error: error.message
        };
      }
    }
    
    // Default health checks based on component type
    switch (name) {
      case 'proxyServer':
        return this.checkProxyServerHealth(component);
      
      case 'clusterManager':
        return this.checkClusterManagerHealth(component);
      
      case 'automationEngine':
        return this.checkAutomationEngineHealth(component);
      
      case 'centralNode':
        return this.checkCentralNodeHealth(component);
      
      case 'configManager':
        return this.checkConfigManagerHealth(component);
      
      default:
        return healthInfo;
    }
  }

  async checkProxyServerHealth(proxyServer) {
    const health = {
      status: 'healthy',
      lastCheck: Date.now(),
      details: {
        activeConnections: 0,
        totalConnections: 0,
        errorRate: 0,
        averageResponseTime: 0
      }
    };
    
    try {
      // Get proxy server stats if available
      if (proxyServer.getStats && typeof proxyServer.getStats === 'function') {
        const stats = await proxyServer.getStats();
        health.details = { ...health.details, ...stats };
        
        // Check thresholds
        if (stats.errorRate > this.healthConfig.alertThresholds.errorRate) {
          health.status = 'degraded';
          health.reason = `High error rate: ${stats.errorRate}%`;
        }
        
        if (stats.averageResponseTime > this.healthConfig.alertThresholds.responseTime) {
          health.status = 'degraded';
          health.reason = `High response time: ${stats.averageResponseTime}ms`;
        }
      }
    } catch (error) {
      health.status = 'unhealthy';
      health.error = error.message;
    }
    
    return health;
  }

  async checkClusterManagerHealth(clusterManager) {
    const health = {
      status: 'healthy',
      lastCheck: Date.now(),
      details: {
        activeClusters: 0,
        totalMembers: 0,
        healthyClusters: 0,
        failedClusters: 0
      }
    };
    
    try {
      if (clusterManager.getStats && typeof clusterManager.getStats === 'function') {
        const stats = await clusterManager.getStats();
        health.details = { ...health.details, ...stats };
        
        // Check cluster health
        const healthyRatio = stats.activeClusters > 0 ? 
          (stats.healthyClusters / stats.activeClusters) : 1;
        
        if (healthyRatio < 0.8) {
          health.status = 'degraded';
          health.reason = `Low cluster health ratio: ${(healthyRatio * 100).toFixed(1)}%`;
        }
      }
    } catch (error) {
      health.status = 'unhealthy';
      health.error = error.message;
    }
    
    return health;
  }

  async checkAutomationEngineHealth(automationEngine) {
    const health = {
      status: 'healthy',
      lastCheck: Date.now(),
      details: {
        activeTasks: 0,
        completedTasks: 0,
        failedTasks: 0,
        taskSuccessRate: 100
      }
    };
    
    try {
      if (automationEngine.getStats && typeof automationEngine.getStats === 'function') {
        const stats = await automationEngine.getStats();
        health.details = { ...health.details, ...stats };
        
        // Check task success rate
        if (stats.taskSuccessRate < 90) {
          health.status = 'degraded';
          health.reason = `Low task success rate: ${stats.taskSuccessRate}%`;
        }
      }
    } catch (error) {
      health.status = 'unhealthy';
      health.error = error.message;
    }
    
    return health;
  }

  async checkCentralNodeHealth(centralNode) {
    const health = {
      status: 'healthy',
      lastCheck: Date.now(),
      details: {
        webServerStatus: 'unknown',
        websocketStatus: 'unknown',
        connectedClients: 0
      }
    };
    
    try {
      if (centralNode.getHealthStatus && typeof centralNode.getHealthStatus === 'function') {
        const healthData = await centralNode.getHealthStatus();
        health.details = { ...health.details, ...healthData };
        
        if (healthData.webServerStatus !== 'running') {
          health.status = 'degraded';
          health.reason = 'Web server not running';
        }
      }
    } catch (error) {
      health.status = 'unhealthy';
      health.error = error.message;
    }
    
    return health;
  }

  async checkConfigManagerHealth(configManager) {
    const health = {
      status: 'healthy',
      lastCheck: Date.now(),
      details: {
        configurationValid: true,
        lastUpdate: null,
        watchedFiles: 0
      }
    };
    
    try {
      if (configManager.getHealthInfo && typeof configManager.getHealthInfo === 'function') {
        const healthData = await configManager.getHealthInfo();
        health.details = { ...health.details, ...healthData };
        
        if (!healthData.configurationValid) {
          health.status = 'unhealthy';
          health.reason = 'Invalid configuration detected';
        }
      }
    } catch (error) {
      health.status = 'unhealthy';
      health.error = error.message;
    }
    
    return health;
  }

  initializeDefaultHealthChecks() {
    // Database connection health check
    this.healthChecks.set('database_connection', async () => {
      // This would check database connectivity if database is used
      return {
        status: 'healthy',
        lastCheck: Date.now(),
        details: { connectionPool: 'active' }
      };
    });
    
    // File system health check
    this.healthChecks.set('file_system', async () => {
      const health = {
        status: 'healthy',
        lastCheck: Date.now(),
        details: { writeable: true, readable: true }
      };
      
      try {
        // Test write access
        const testFile = path.join('.', 'health-test.tmp');
        await fs.writeFile(testFile, 'test');
        await fs.unlink(testFile);
      } catch (error) {
        health.status = 'unhealthy';
        health.error = 'File system not writable';
        health.details.writeable = false;
      }
      
      return health;
    });
    
    // Memory leak detection
    this.healthChecks.set('memory_leak_detection', async () => {
      const health = {
        status: 'healthy',
        lastCheck: Date.now(),
        details: { memoryTrend: 'stable' }
      };
      
      // Simple memory trend analysis
      if (this.healthHistory.length >= 10) {
        const recent = this.healthHistory.slice(-10);
        const memoryUsages = recent.map(h => h.system.memory.usage);
        const trend = this.calculateTrend(memoryUsages);
        
        health.details.memoryTrend = trend > 5 ? 'increasing' : trend < -5 ? 'decreasing' : 'stable';
        
        if (trend > 10) {
          health.status = 'warning';
          health.reason = 'Memory usage steadily increasing';
        }
      }
      
      return health;
    });
  }

  async getCPUUsage() {
    return new Promise((resolve) => {
      const startUsage = process.cpuUsage();
      const startTime = Date.now();
      
      setTimeout(() => {
        const endUsage = process.cpuUsage(startUsage);
        const endTime = Date.now();
        
        const totalTime = (endTime - startTime) * 1000; // Convert to microseconds
        const cpuPercent = ((endUsage.user + endUsage.system) / totalTime) * 100;
        
        resolve(Math.min(100, Math.max(0, cpuPercent)));
      }, 100);
    });
  }

  async getDiskUsage() {
    try {
      const stats = await fs.stat('.');
      // This is a simplified version - in production you'd want to use a proper disk usage library
      return {
        usage: 0, // Placeholder
        free: 1000000000,
        total: 1000000000
      };
    } catch (error) {
      return {
        usage: 0,
        free: 0,
        total: 0,
        error: error.message
      };
    }
  }

  async getNetworkStats() {
    // Simplified network stats - in production you'd want proper network monitoring
    return {
      bytesIn: 0,
      bytesOut: 0,
      connections: 0
    };
  }

  updateSystemMetrics(systemHealth) {
    // Update current values
    this.systemMetrics.cpu.current = systemHealth.cpu.usage;
    this.systemMetrics.memory.current = systemHealth.memory.usage;
    this.systemMetrics.disk.current = systemHealth.disk.usage;
    this.systemMetrics.uptime = systemHealth.uptime;
    
    // Calculate averages from history
    if (this.healthHistory.length > 0) {
      const cpuValues = this.healthHistory.map(h => h.system.cpu.usage);
      const memoryValues = this.healthHistory.map(h => h.system.memory.usage);
      const diskValues = this.healthHistory.map(h => h.system.disk.usage);
      
      this.systemMetrics.cpu.average = this.calculateAverage(cpuValues);
      this.systemMetrics.cpu.peak = Math.max(...cpuValues);
      
      this.systemMetrics.memory.average = this.calculateAverage(memoryValues);
      this.systemMetrics.memory.peak = Math.max(...memoryValues);
      
      this.systemMetrics.disk.average = this.calculateAverage(diskValues);
      this.systemMetrics.disk.peak = Math.max(...diskValues);
    }
  }

  determineOverallHealth(healthRecord) {
    const { system, components } = healthRecord;
    
    // Check system health
    if (system.cpu.usage > this.healthConfig.alertThresholds.cpu ||
        system.memory.usage > this.healthConfig.alertThresholds.memory ||
        system.disk.usage > this.healthConfig.alertThresholds.disk) {
      return 'unhealthy';
    }
    
    if (system.cpu.usage > this.healthConfig.degradationThresholds.cpu ||
        system.memory.usage > this.healthConfig.degradationThresholds.memory ||
        system.disk.usage > this.healthConfig.degradationThresholds.disk) {
      return 'degraded';
    }
    
    // Check component health
    const componentStatuses = Object.values(components).map(c => c.status);
    
    if (componentStatuses.includes('unhealthy')) {
      return 'unhealthy';
    }
    
    if (componentStatuses.includes('degraded')) {
      return 'degraded';
    }
    
    return 'healthy';
  }

  async checkHealthAlerts(healthRecord) {
    const now = Date.now();
    const newAlerts = [];
    
    // System alerts
    if (healthRecord.system.cpu.usage > this.healthConfig.alertThresholds.cpu) {
      newAlerts.push({
        type: 'system',
        severity: 'high',
        metric: 'cpu',
        value: healthRecord.system.cpu.usage,
        threshold: this.healthConfig.alertThresholds.cpu,
        message: `High CPU usage: ${healthRecord.system.cpu.usage}%`
      });
    }
    
    if (healthRecord.system.memory.usage > this.healthConfig.alertThresholds.memory) {
      newAlerts.push({
        type: 'system',
        severity: 'high',
        metric: 'memory',
        value: healthRecord.system.memory.usage,
        threshold: this.healthConfig.alertThresholds.memory,
        message: `High memory usage: ${healthRecord.system.memory.usage}%`
      });
    }
    
    if (healthRecord.system.disk.usage > this.healthConfig.alertThresholds.disk) {
      newAlerts.push({
        type: 'system',
        severity: 'critical',
        metric: 'disk',
        value: healthRecord.system.disk.usage,
        threshold: this.healthConfig.alertThresholds.disk,
        message: `High disk usage: ${healthRecord.system.disk.usage}%`
      });
    }
    
    // Component alerts
    for (const [componentName, componentHealth] of Object.entries(healthRecord.components)) {
      if (componentHealth.status === 'unhealthy') {
        newAlerts.push({
          type: 'component',
          severity: 'high',
          component: componentName,
          message: `Component unhealthy: ${componentName} - ${componentHealth.error || componentHealth.reason || 'Unknown error'}`
        });
      } else if (componentHealth.status === 'degraded') {
        newAlerts.push({
          type: 'component',
          severity: 'medium',
          component: componentName,
          message: `Component degraded: ${componentName} - ${componentHealth.reason || 'Performance degradation'}`
        });
      }
    }
    
    // Process alerts (with cooldown)
    for (const alert of newAlerts) {
      const alertKey = `${alert.type}_${alert.metric || alert.component}`;
      const lastAlert = this.lastAlerts.get(alertKey);
      
      if (!lastAlert || (now - lastAlert) > this.healthConfig.alertCooldown) {
        this.alerts.set(alert.type + '_' + now, { ...alert, timestamp: now });
        this.lastAlerts.set(alertKey, now);
        
        this.logger.warn(`Health alert: ${alert.message}`);
        this.emit('health_alert', alert);
      }
    }
  }

  calculateAverage(values) {
    if (values.length === 0) return 0;
    return values.reduce((sum, val) => sum + val, 0) / values.length;
  }

  calculateTrend(values) {
    if (values.length < 2) return 0;
    
    // Simple linear trend calculation
    const n = values.length;
    const sumX = (n * (n - 1)) / 2;
    const sumY = values.reduce((sum, val) => sum + val, 0);
    const sumXY = values.reduce((sum, val, i) => sum + (i * val), 0);
    const sumXX = (n * (n - 1) * (2 * n - 1)) / 6;
    
    const slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
    return slope;
  }

  async loadHealthHistory() {
    try {
      const historyPath = path.join('./logs', 'health-history.json');
      const historyData = await fs.readFile(historyPath, 'utf8');
      const history = JSON.parse(historyData);
      
      // Keep only recent history
      const cutoffTime = Date.now() - (24 * 60 * 60 * 1000); // 24 hours
      this.healthHistory = history.filter(record => record.timestamp > cutoffTime);
      
      this.logger.info(`Loaded ${this.healthHistory.length} health history records`);
    } catch (error) {
      this.logger.debug('No health history found, starting fresh');
    }
  }

  async saveHealthHistory() {
    try {
      const historyPath = path.join('./logs', 'health-history.json');
      await fs.writeFile(historyPath, JSON.stringify(this.healthHistory, null, 2));
    } catch (error) {
      this.logger.warn('Failed to save health history:', error.message);
    }
  }

  // Public API methods
  getCurrentHealth() {
    if (this.healthHistory.length === 0) {
      return { status: 'unknown', message: 'No health data available' };
    }
    
    const latestHealth = this.healthHistory[this.healthHistory.length - 1];
    return {
      status: latestHealth.overall,
      timestamp: latestHealth.timestamp,
      system: latestHealth.system,
      components: latestHealth.components
    };
  }

  getSystemMetrics() {
    return { ...this.systemMetrics };
  }

  getHealthHistory(hours = 1) {
    const cutoffTime = Date.now() - (hours * 60 * 60 * 1000);
    return this.healthHistory.filter(record => record.timestamp > cutoffTime);
  }

  getActiveAlerts() {
    const activeAlerts = [];
    const cutoffTime = Date.now() - (60 * 60 * 1000); // 1 hour
    
    for (const [alertId, alert] of this.alerts.entries()) {
      if (alert.timestamp > cutoffTime) {
        activeAlerts.push({ id: alertId, ...alert });
      }
    }
    
    return activeAlerts.sort((a, b) => b.timestamp - a.timestamp);
  }

  addCustomHealthCheck(name, checkFunction) {
    this.healthChecks.set(name, checkFunction);
    this.logger.info(`Added custom health check: ${name}`);
  }

  removeCustomHealthCheck(name) {
    const removed = this.healthChecks.delete(name);
    if (removed) {
      this.logger.info(`Removed custom health check: ${name}`);
    }
    return removed;
  }

  // Get health summary for dashboards
  getHealthSummary() {
    const currentHealth = this.getCurrentHealth();
    const metrics = this.getSystemMetrics();
    const activeAlerts = this.getActiveAlerts();
    
    return {
      overall: currentHealth.status,
      timestamp: currentHealth.timestamp,
      system: {
        cpu: {
          current: metrics.cpu.current,
          average: metrics.cpu.average,
          status: metrics.cpu.current > this.healthConfig.alertThresholds.cpu ? 'alert' : 
                  metrics.cpu.current > this.healthConfig.degradationThresholds.cpu ? 'warning' : 'ok'
        },
        memory: {
          current: metrics.memory.current,
          average: metrics.memory.average,
          status: metrics.memory.current > this.healthConfig.alertThresholds.memory ? 'alert' : 
                  metrics.memory.current > this.healthConfig.degradationThresholds.memory ? 'warning' : 'ok'
        },
        disk: {
          current: metrics.disk.current,
          average: metrics.disk.average,
          status: metrics.disk.current > this.healthConfig.alertThresholds.disk ? 'alert' : 
                  metrics.disk.current > this.healthConfig.degradationThresholds.disk ? 'warning' : 'ok'
        }
      },
      components: currentHealth.components,
      alerts: {
        active: activeAlerts.length,
        critical: activeAlerts.filter(a => a.severity === 'critical').length,
        high: activeAlerts.filter(a => a.severity === 'high').length,
        medium: activeAlerts.filter(a => a.severity === 'medium').length
      },
      uptime: metrics.uptime
    };
  }
}

module.exports = HealthMonitor;