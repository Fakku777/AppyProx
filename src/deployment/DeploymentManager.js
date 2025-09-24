const EventEmitter = require('events');
const fs = require('fs').promises;
const path = require('path');
const { spawn, exec } = require('child_process');
const cluster = require('cluster');
const os = require('os');

/**
 * Comprehensive Deployment and Scaling System
 * Manages multi-server coordination, load balancing, and production deployment
 */
class DeploymentManager extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('DeploymentManager') : logger;
    
    // Deployment configuration
    this.deploymentConfig = {
      mode: 'standalone', // 'standalone', 'cluster', 'distributed'
      instances: os.cpus().length,
      ports: {
        proxy: this.config.proxy?.port || 25565,
        api: this.config.api?.port || 3000,
        centralNode: this.config.centralNode?.webPort || 8080,
        websocket: this.config.centralNode?.websocketPort || 8081
      },
      loadBalancer: {
        enabled: false,
        algorithm: 'round_robin', // 'round_robin', 'least_connections', 'ip_hash'
        healthCheck: {
          interval: 30000,
          timeout: 5000,
          retries: 3
        }
      },
      scaling: {
        autoScaling: false,
        minInstances: 1,
        maxInstances: os.cpus().length * 2,
        targetCpuPercent: 70,
        targetMemoryPercent: 80,
        scaleUpCooldown: 300000, // 5 minutes
        scaleDownCooldown: 600000 // 10 minutes
      },
      ...this.config.deployment
    };
    
    // Process management
    this.workers = new Map();
    this.masterProcess = cluster.isMaster;
    this.instances = new Map();
    this.loadBalancer = null;
    
    // Health monitoring
    this.healthChecks = new Map();
    this.healthCheckInterval = null;
    
    // Scaling metrics
    this.scalingMetrics = {
      cpuUsage: [],
      memoryUsage: [],
      requestRate: [],
      responseTime: [],
      lastScaleAction: null,
      cooldownActive: false
    };
    
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing deployment manager...');
    
    try {
      // Create deployment directories
      await this.setupDeploymentDirectories();
      
      // Initialize based on deployment mode
      switch (this.deploymentConfig.mode) {
        case 'cluster':
          await this.initializeClusterMode();
          break;
        case 'distributed':
          await this.initializeDistributedMode();
          break;
        case 'standalone':
        default:
          await this.initializeStandaloneMode();
          break;
      }
      
      // Setup health monitoring
      await this.setupHealthMonitoring();
      
      // Setup scaling if enabled
      if (this.deploymentConfig.scaling.autoScaling) {
        await this.setupAutoScaling();
      }
      
      this.initialized = true;
      this.logger.info(`Deployment manager initialized in ${this.deploymentConfig.mode} mode`);
      
    } catch (error) {
      this.logger.error('Failed to initialize deployment manager:', error);
      throw error;
    }
  }

  async setupDeploymentDirectories() {
    const dirs = [
      './deployments',
      './deployments/configs',
      './deployments/logs',
      './deployments/scripts',
      './deployments/docker'
    ];
    
    for (const dir of dirs) {
      try {
        await fs.mkdir(dir, { recursive: true });
      } catch (error) {
        if (error.code !== 'EEXIST') throw error;
      }
    }
  }

  // ==========================================
  // DEPLOYMENT MODES
  // ==========================================

  async initializeStandaloneMode() {
    this.logger.info('Initializing standalone mode...');
    
    // Single instance deployment
    this.instances.set('primary', {
      id: 'primary',
      type: 'standalone',
      status: 'running',
      port: this.deploymentConfig.ports.proxy,
      pid: process.pid,
      startTime: Date.now(),
      config: this.config
    });
  }

  async initializeClusterMode() {
    this.logger.info(`Initializing cluster mode with ${this.deploymentConfig.instances} workers...`);
    
    if (this.masterProcess) {
      // Master process: manage workers
      await this.setupMasterProcess();
      
      // Start load balancer if enabled
      if (this.deploymentConfig.loadBalancer.enabled) {
        await this.startLoadBalancer();
      }
      
    } else {
      // Worker process: run AppyProx instance
      await this.setupWorkerProcess();
    }
  }

  async initializeDistributedMode() {
    this.logger.info('Initializing distributed mode...');
    
    // Setup distributed coordination
    await this.setupDistributedCoordination();
    
    // Start service discovery
    await this.startServiceDiscovery();
    
    // Setup cross-server communication
    await this.setupInterServerCommunication();
  }

  async setupMasterProcess() {
    this.logger.info('Setting up master process...');
    
    // Fork workers
    for (let i = 0; i < this.deploymentConfig.instances; i++) {
      const worker = cluster.fork({
        WORKER_ID: i,
        WORKER_PORT: this.deploymentConfig.ports.proxy + i + 1
      });
      
      this.workers.set(worker.id, {
        id: worker.id,
        worker: worker,
        port: this.deploymentConfig.ports.proxy + i + 1,
        startTime: Date.now(),
        status: 'starting',
        requests: 0,
        connections: 0
      });
      
      this.logger.info(`Started worker ${worker.id} on port ${this.deploymentConfig.ports.proxy + i + 1}`);
    }
    
    // Handle worker events
    cluster.on('exit', (worker, code, signal) => {
      this.logger.warn(`Worker ${worker.id} died (${signal || code}). Restarting...`);
      this.restartWorker(worker.id);
    });
    
    cluster.on('online', (worker) => {
      const workerInfo = this.workers.get(worker.id);
      if (workerInfo) {
        workerInfo.status = 'online';
        this.logger.info(`Worker ${worker.id} is online`);
      }
    });
  }

  async setupWorkerProcess() {
    this.logger.info(`Setting up worker process ${process.env.WORKER_ID}`);
    
    // Worker-specific configuration
    const workerConfig = {
      ...this.config,
      proxy: {
        ...this.config.proxy,
        port: parseInt(process.env.WORKER_PORT)
      }
    };
    
    // Store worker instance info
    this.instances.set(process.env.WORKER_ID, {
      id: process.env.WORKER_ID,
      type: 'worker',
      status: 'running',
      port: parseInt(process.env.WORKER_PORT),
      pid: process.pid,
      startTime: Date.now(),
      config: workerConfig
    });
    
    // Send ready signal to master
    process.send && process.send({ cmd: 'worker_ready', workerId: process.env.WORKER_ID });
  }

  async restartWorker(workerId) {
    const workerInfo = this.workers.get(workerId);
    if (!workerInfo) return;
    
    // Remove dead worker
    this.workers.delete(workerId);
    
    // Fork new worker
    const newWorker = cluster.fork({
      WORKER_ID: workerId,
      WORKER_PORT: workerInfo.port
    });
    
    this.workers.set(newWorker.id, {
      ...workerInfo,
      id: newWorker.id,
      worker: newWorker,
      startTime: Date.now(),
      status: 'restarting'
    });
    
    this.logger.info(`Restarted worker ${newWorker.id} on port ${workerInfo.port}`);
  }

  // ==========================================
  // LOAD BALANCING
  // ==========================================

  async startLoadBalancer() {
    this.logger.info('Starting load balancer...');
    
    const LoadBalancer = require('./LoadBalancer');
    this.loadBalancer = new LoadBalancer({
      algorithm: this.deploymentConfig.loadBalancer.algorithm,
      healthCheck: this.deploymentConfig.loadBalancer.healthCheck,
      upstream: Array.from(this.workers.values()).map(worker => ({
        id: worker.id,
        host: '127.0.0.1',
        port: worker.port,
        weight: 1
      }))
    }, this.logger);
    
    await this.loadBalancer.start(this.deploymentConfig.ports.proxy);
    
    this.logger.info(`Load balancer started on port ${this.deploymentConfig.ports.proxy}`);
  }

  // ==========================================
  // HEALTH MONITORING
  // ==========================================

  async setupHealthMonitoring() {
    this.logger.info('Setting up health monitoring...');
    
    this.healthCheckInterval = setInterval(() => {
      this.performHealthChecks();
    }, this.deploymentConfig.loadBalancer.healthCheck.interval);
  }

  async performHealthChecks() {
    for (const [instanceId, instance] of this.instances) {
      try {
        const health = await this.checkInstanceHealth(instance);
        
        this.healthChecks.set(instanceId, {
          instanceId: instanceId,
          healthy: health.healthy,
          responseTime: health.responseTime,
          lastCheck: Date.now(),
          consecutiveFailures: health.healthy ? 0 : (this.healthChecks.get(instanceId)?.consecutiveFailures || 0) + 1
        });
        
        // Handle unhealthy instances
        const healthInfo = this.healthChecks.get(instanceId);
        if (healthInfo.consecutiveFailures >= this.deploymentConfig.loadBalancer.healthCheck.retries) {
          await this.handleUnhealthyInstance(instanceId);
        }
        
      } catch (error) {
        this.logger.error(`Health check failed for instance ${instanceId}:`, error.message);
      }
    }
  }

  async checkInstanceHealth(instance) {
    return new Promise((resolve) => {
      const startTime = Date.now();
      
      // For workers, check via HTTP health endpoint
      if (instance.type === 'worker') {
        const http = require('http');
        const req = http.request({
          hostname: '127.0.0.1',
          port: instance.port + 1000, // Health check port offset
          path: '/health',
          timeout: this.deploymentConfig.loadBalancer.healthCheck.timeout
        }, (res) => {
          const responseTime = Date.now() - startTime;
          resolve({
            healthy: res.statusCode === 200,
            responseTime: responseTime
          });
        });
        
        req.on('error', () => {
          resolve({
            healthy: false,
            responseTime: Date.now() - startTime
          });
        });
        
        req.on('timeout', () => {
          req.destroy();
          resolve({
            healthy: false,
            responseTime: this.deploymentConfig.loadBalancer.healthCheck.timeout
          });
        });
        
        req.end();
      } else {
        // For standalone, just check if process is alive
        resolve({
          healthy: true,
          responseTime: Date.now() - startTime
        });
      }
    });
  }

  async handleUnhealthyInstance(instanceId) {
    const instance = this.instances.get(instanceId);
    if (!instance) return;
    
    this.logger.warn(`Instance ${instanceId} is unhealthy. Taking action...`);
    
    if (instance.type === 'worker') {
      // Restart worker
      await this.restartWorker(instanceId);
    } else {
      // For standalone or distributed, emit warning
      this.emit('instance_unhealthy', { instanceId, instance });
    }
  }

  // ==========================================
  // AUTO-SCALING
  // ==========================================

  async setupAutoScaling() {
    this.logger.info('Setting up auto-scaling...');
    
    // Collect metrics every 30 seconds
    setInterval(() => {
      this.collectScalingMetrics();
    }, 30000);
    
    // Evaluate scaling every 2 minutes
    setInterval(() => {
      this.evaluateScaling();
    }, 120000);
  }

  async collectScalingMetrics() {
    const now = Date.now();
    
    // Collect CPU usage
    const cpuUsage = process.cpuUsage();
    const cpuPercent = ((cpuUsage.user + cpuUsage.system) / 1000000) / 30; // Convert to percentage
    
    this.scalingMetrics.cpuUsage.push({
      timestamp: now,
      value: cpuPercent
    });
    
    // Collect memory usage
    const memUsage = process.memoryUsage();
    const memPercent = (memUsage.heapUsed / memUsage.heapTotal) * 100;
    
    this.scalingMetrics.memoryUsage.push({
      timestamp: now,
      value: memPercent
    });
    
    // Keep only last 20 measurements (10 minutes)
    this.scalingMetrics.cpuUsage = this.scalingMetrics.cpuUsage.slice(-20);
    this.scalingMetrics.memoryUsage = this.scalingMetrics.memoryUsage.slice(-20);
    this.scalingMetrics.requestRate = this.scalingMetrics.requestRate.slice(-20);
    this.scalingMetrics.responseTime = this.scalingMetrics.responseTime.slice(-20);
  }

  async evaluateScaling() {
    if (this.scalingMetrics.cooldownActive) {
      return; // Wait for cooldown to finish
    }
    
    const avgCpu = this.calculateAverage(this.scalingMetrics.cpuUsage);
    const avgMemory = this.calculateAverage(this.scalingMetrics.memoryUsage);
    const currentInstances = this.workers.size;
    
    this.logger.debug(`Scaling evaluation - CPU: ${avgCpu.toFixed(1)}%, Memory: ${avgMemory.toFixed(1)}%, Instances: ${currentInstances}`);
    
    // Scale up conditions
    if ((avgCpu > this.deploymentConfig.scaling.targetCpuPercent || 
         avgMemory > this.deploymentConfig.scaling.targetMemoryPercent) &&
        currentInstances < this.deploymentConfig.scaling.maxInstances) {
      
      await this.scaleUp();
      
    }
    // Scale down conditions
    else if (avgCpu < this.deploymentConfig.scaling.targetCpuPercent * 0.5 &&
             avgMemory < this.deploymentConfig.scaling.targetMemoryPercent * 0.5 &&
             currentInstances > this.deploymentConfig.scaling.minInstances) {
      
      await this.scaleDown();
    }
  }

  calculateAverage(metrics) {
    if (metrics.length === 0) return 0;
    const sum = metrics.reduce((total, metric) => total + metric.value, 0);
    return sum / metrics.length;
  }

  async scaleUp() {
    this.logger.info('Scaling up - adding new worker instance');
    
    const newPort = Math.max(...Array.from(this.workers.values()).map(w => w.port)) + 1;
    const worker = cluster.fork({
      WORKER_ID: `scaled-${Date.now()}`,
      WORKER_PORT: newPort
    });
    
    this.workers.set(worker.id, {
      id: worker.id,
      worker: worker,
      port: newPort,
      startTime: Date.now(),
      status: 'starting',
      requests: 0,
      connections: 0
    });
    
    // Update load balancer
    if (this.loadBalancer) {
      this.loadBalancer.addUpstream({
        id: worker.id,
        host: '127.0.0.1',
        port: newPort,
        weight: 1
      });
    }
    
    this.scalingMetrics.lastScaleAction = {
      action: 'scale_up',
      timestamp: Date.now(),
      instancesBefore: this.workers.size - 1,
      instancesAfter: this.workers.size
    };
    
    // Start cooldown
    this.startScalingCooldown(this.deploymentConfig.scaling.scaleUpCooldown);
    
    this.emit('scaled_up', { newWorkerCount: this.workers.size });
  }

  async scaleDown() {
    if (this.workers.size <= this.deploymentConfig.scaling.minInstances) {
      return;
    }
    
    this.logger.info('Scaling down - removing worker instance');
    
    // Find worker with least connections
    let leastBusyWorker = null;
    let minConnections = Infinity;
    
    for (const worker of this.workers.values()) {
      if (worker.connections < minConnections) {
        minConnections = worker.connections;
        leastBusyWorker = worker;
      }
    }
    
    if (leastBusyWorker) {
      // Remove from load balancer first
      if (this.loadBalancer) {
        this.loadBalancer.removeUpstream(leastBusyWorker.id);
      }
      
      // Gracefully terminate worker
      leastBusyWorker.worker.disconnect();
      setTimeout(() => {
        leastBusyWorker.worker.kill();
      }, 30000); // Give 30 seconds for graceful shutdown
      
      this.workers.delete(leastBusyWorker.id);
      
      this.scalingMetrics.lastScaleAction = {
        action: 'scale_down',
        timestamp: Date.now(),
        instancesBefore: this.workers.size + 1,
        instancesAfter: this.workers.size
      };
      
      // Start cooldown
      this.startScalingCooldown(this.deploymentConfig.scaling.scaleDownCooldown);
      
      this.emit('scaled_down', { newWorkerCount: this.workers.size });
    }
  }

  startScalingCooldown(duration) {
    this.scalingMetrics.cooldownActive = true;
    setTimeout(() => {
      this.scalingMetrics.cooldownActive = false;
      this.logger.debug('Scaling cooldown ended');
    }, duration);
  }

  // ==========================================
  // PROCESS MANAGEMENT
  // ==========================================

  async generateProcessScripts() {
    this.logger.info('Generating process management scripts...');
    
    // Create systemd service file
    const systemdService = `[Unit]
Description=AppyProx Minecraft Proxy Server
After=network.target
Wants=network.target

[Service]
Type=simple
User=appyprox
Group=appyprox
WorkingDirectory=/opt/appyprox
ExecStart=/usr/bin/node src/proxy/main.js
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
Environment=NODE_ENV=production
Environment=LOG_LEVEL=info

# Resource limits
LimitNOFILE=65536
LimitNPROC=4096

[Install]
WantedBy=multi-user.target
`;
    
    // Create PM2 ecosystem file
    const pm2Config = `module.exports = {
  apps: [{
    name: 'appyprox-main',
    script: 'src/proxy/main.js',
    instances: 1,
    exec_mode: 'fork',
    env: {
      NODE_ENV: 'production',
      LOG_LEVEL: 'info'
    },
    log_file: './logs/appyprox.log',
    error_file: './logs/appyprox-error.log',
    out_file: './logs/appyprox-out.log',
    pid_file: './logs/appyprox.pid',
    max_memory_restart: '1G',
    restart_delay: 4000,
    autorestart: true,
    watch: false
  }, {
    name: 'appyprox-cluster',
    script: 'src/proxy/main.js',
    instances: 'max',
    exec_mode: 'cluster',
    env: {
      NODE_ENV: 'production',
      LOG_LEVEL: 'info',
      CLUSTER_MODE: 'true'
    },
    log_file: './logs/appyprox-cluster.log',
    error_file: './logs/appyprox-cluster-error.log',
    out_file: './logs/appyprox-cluster-out.log',
    max_memory_restart: '800M',
    restart_delay: 4000,
    autorestart: true,
    watch: false,
    kill_timeout: 30000
  }]
};
`;
    
    // Create startup script
    const startupScript = `#!/bin/bash
# AppyProx Startup Script

SCRIPT_DIR="$(cd "$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$(dirname "$SCRIPT_DIR")"

cd "$APP_DIR"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "Error: Node.js is not installed"
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "Error: npm is not installed"
    exit 1
fi

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install --production
fi

# Create necessary directories
mkdir -p logs
mkdir -p data

# Set permissions
chmod +x scripts/*.sh 2>/dev/null || true

echo "Starting AppyProx..."

# Determine deployment mode
DEPLOY_MODE=\${1:-standalone}

case $DEPLOY_MODE in
    "standalone")
        echo "Starting in standalone mode..."
        node src/proxy/main.js
        ;;
    "cluster")
        echo "Starting in cluster mode..."
        if command -v pm2 &> /dev/null; then
            pm2 start ecosystem.config.js --only appyprox-cluster
        else
            echo "PM2 not found, starting with Node.js cluster module..."
            NODE_ENV=production CLUSTER_MODE=true node src/proxy/main.js
        fi
        ;;
    "daemon")
        echo "Starting as daemon..."
        if command -v pm2 &> /dev/null; then
            pm2 start ecosystem.config.js --only appyprox-main
            pm2 save
        else
            echo "PM2 not found, starting in background..."
            nohup node src/proxy/main.js > logs/appyprox.log 2>&1 &
            echo $! > logs/appyprox.pid
            echo "AppyProx started with PID $(cat logs/appyprox.pid)"
        fi
        ;;
    *)
        echo "Usage: $0 {standalone|cluster|daemon}"
        exit 1
        ;;
esac
`;
    
    // Create stop script
    const stopScript = `#!/bin/bash
# AppyProx Stop Script

SCRIPT_DIR="$(cd "$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$(dirname "$SCRIPT_DIR")"

cd "$APP_DIR"

echo "Stopping AppyProx..."

# Try PM2 first
if command -v pm2 &> /dev/null; then
    pm2 stop appyprox-main appyprox-cluster 2>/dev/null || true
    pm2 delete appyprox-main appyprox-cluster 2>/dev/null || true
    echo "Stopped PM2 processes"
fi

# Stop by PID file
if [ -f "logs/appyprox.pid" ]; then
    PID=$(cat logs/appyprox.pid)
    if kill -0 $PID 2>/dev/null; then
        echo "Stopping process $PID..."
        kill -TERM $PID
        sleep 5
        if kill -0 $PID 2>/dev/null; then
            echo "Force killing process $PID..."
            kill -KILL $PID
        fi
    fi
    rm -f logs/appyprox.pid
fi

# Kill any remaining AppyProx processes
pkill -f "appyprox|src/proxy/main.js" 2>/dev/null || true

echo "AppyProx stopped"
`;
    
    // Create directories
    await fs.mkdir('./deployments/scripts', { recursive: true });
    await fs.mkdir('./deployments/systemd', { recursive: true });
    
    // Write files
    await fs.writeFile('./deployments/systemd/appyprox.service', systemdService);
    await fs.writeFile('./ecosystem.config.js', pm2Config);
    await fs.writeFile('./scripts/start.sh', startupScript);
    await fs.writeFile('./scripts/stop.sh', stopScript);
    
    // Make scripts executable
    await fs.chmod('./scripts/start.sh', 0o755);
    await fs.chmod('./scripts/stop.sh', 0o755);
    
    this.logger.info('Process management scripts generated');
  }

  // ==========================================
  // DEPLOYMENT OPERATIONS
  // ==========================================

  async deploy(environment = 'production') {
    this.logger.info(`Starting deployment to ${environment}...`);
    
    try {
      // Pre-deployment checks
      await this.preDeploymentChecks();
      
      // Stop existing instances gracefully
      await this.gracefulShutdown();
      
      // Start new instances
      await this.startInstances();
      
      // Post-deployment verification
      await this.postDeploymentVerification();
      
      this.logger.info('Deployment completed successfully');
      this.emit('deployment_completed', { environment });
      
    } catch (error) {
      this.logger.error('Deployment failed:', error);
      
      // Attempt rollback
      await this.rollback();
      
      this.emit('deployment_failed', { environment, error });
      throw error;
    }
  }

  async preDeploymentChecks() {
    this.logger.info('Performing pre-deployment checks...');
    
    // Check configuration validity
    if (!this.config) {
      throw new Error('Configuration not found');
    }
    
    // Check required ports are available
    const portsToCheck = [
      this.deploymentConfig.ports.proxy,
      this.deploymentConfig.ports.api,
      this.deploymentConfig.ports.centralNode
    ];
    
    for (const port of portsToCheck) {
      const available = await this.isPortAvailable(port);
      if (!available) {
        this.logger.warn(`Port ${port} is not available - may be in use by existing instance`);
      }
    }
    
    // Check system resources
    const freeMemory = os.freemem();
    const totalMemory = os.totalmem();
    const memoryUsage = ((totalMemory - freeMemory) / totalMemory) * 100;
    
    if (memoryUsage > 90) {
      throw new Error(`System memory usage too high: ${memoryUsage.toFixed(1)}%`);
    }
    
    this.logger.info('Pre-deployment checks passed');
  }

  async gracefulShutdown() {
    this.logger.info('Performing graceful shutdown of existing instances...');
    
    if (this.deploymentConfig.mode === 'cluster' && this.masterProcess) {
      // Shutdown all workers
      for (const [workerId, workerInfo] of this.workers) {
        this.logger.info(`Shutting down worker ${workerId}...`);
        
        workerInfo.worker.disconnect();
        
        // Wait for graceful shutdown
        await new Promise((resolve) => {
          const timeout = setTimeout(() => {
            workerInfo.worker.kill();
            resolve();
          }, 30000);
          
          workerInfo.worker.on('exit', () => {
            clearTimeout(timeout);
            resolve();
          });
        });
      }
      
      this.workers.clear();
    }
    
    // Stop load balancer
    if (this.loadBalancer) {
      await this.loadBalancer.stop();
      this.loadBalancer = null;
    }
    
    this.logger.info('Graceful shutdown completed');
  }

  async startInstances() {
    this.logger.info('Starting new instances...');
    
    // Re-initialize based on mode
    await this.initialize();
    
    this.logger.info('New instances started successfully');
  }

  async postDeploymentVerification() {
    this.logger.info('Performing post-deployment verification...');
    
    // Wait for instances to be ready
    await this.sleep(5000);
    
    // Verify all instances are healthy
    await this.performHealthChecks();
    
    const healthyInstances = Array.from(this.healthChecks.values()).filter(h => h.healthy);
    const totalInstances = this.healthChecks.size;
    
    if (healthyInstances.length < totalInstances) {
      throw new Error(`Only ${healthyInstances.length}/${totalInstances} instances are healthy`);
    }
    
    this.logger.info('Post-deployment verification passed');
  }

  async rollback() {
    this.logger.warn('Initiating rollback...');
    
    // This is a simplified rollback - in production you'd restore from backup
    try {
      await this.gracefulShutdown();
      
      // Restore previous configuration
      // await this.restorePreviousConfiguration();
      
      await this.startInstances();
      
      this.logger.info('Rollback completed');
      this.emit('rollback_completed');
      
    } catch (error) {
      this.logger.error('Rollback failed:', error);
      this.emit('rollback_failed', { error });
    }
  }

  // ==========================================
  // UTILITY METHODS
  // ==========================================

  async isPortAvailable(port) {
    return new Promise((resolve) => {
      const net = require('net');
      const server = net.createServer();
      
      server.listen(port, '127.0.0.1', () => {
        server.once('close', () => resolve(true));
        server.close();
      });
      
      server.on('error', () => {
        resolve(false);
      });
    });
  }

  async sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  // ==========================================
  // STATUS AND MONITORING
  // ==========================================

  getDeploymentStatus() {
    return {
      mode: this.deploymentConfig.mode,
      instances: this.instances.size,
      workers: this.workers.size,
      healthyInstances: Array.from(this.healthChecks.values()).filter(h => h.healthy).length,
      loadBalancer: {
        enabled: this.deploymentConfig.loadBalancer.enabled,
        active: this.loadBalancer !== null
      },
      scaling: {
        enabled: this.deploymentConfig.scaling.autoScaling,
        currentInstances: this.workers.size,
        minInstances: this.deploymentConfig.scaling.minInstances,
        maxInstances: this.deploymentConfig.scaling.maxInstances,
        lastScaleAction: this.scalingMetrics.lastScaleAction,
        cooldownActive: this.scalingMetrics.cooldownActive
      },
      uptime: Date.now() - (this.scalingMetrics.lastScaleAction?.timestamp || Date.now()),
      processManagement: {
        systemd: true,
        pm2: false,
        pid: process.pid
      }
    };
  }

  getScalingMetrics() {
    return {
      cpuUsage: this.scalingMetrics.cpuUsage,
      memoryUsage: this.scalingMetrics.memoryUsage,
      requestRate: this.scalingMetrics.requestRate,
      responseTime: this.scalingMetrics.responseTime,
      averages: {
        cpu: this.calculateAverage(this.scalingMetrics.cpuUsage),
        memory: this.calculateAverage(this.scalingMetrics.memoryUsage),
        requestRate: this.calculateAverage(this.scalingMetrics.requestRate),
        responseTime: this.calculateAverage(this.scalingMetrics.responseTime)
      }
    };
  }
}

module.exports = DeploymentManager;