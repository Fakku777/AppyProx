/**
 * TPS (Ticks Per Second) Management System
 * Monitors Minecraft server TPS and synchronizes WebUI update rates
 */

const EventEmitter = require('events');

class TPSManager extends EventEmitter {
  constructor(logger, proxyServer) {
    super();
    this.logger = logger.child ? logger.child('TPSManager') : logger;
    this.proxyServer = proxyServer;
    
    // TPS tracking
    this.serverTPS = new Map(); // Map<serverAddress, TPSData>
    this.targetTPS = 20.0; // Minecraft's target TPS
    this.currentUpdateRate = 20; // Updates per second
    this.minUpdateRate = 1; // Minimum 1 update per second
    this.maxUpdateRate = 30; // Maximum 30 updates per second
    
    // Update intervals
    this.updateInterval = null;
    this.tpsCheckInterval = null;
    this.baseTickInterval = 50; // 50ms = 20 TPS
    
    // TPS calculation
    this.tickHistory = new Map(); // Server tick history
    this.tickWindowSize = 100; // Number of ticks to average over
    
    // Performance metrics
    this.performance = {
      averageTPS: 20.0,
      lagSpikes: 0,
      skipTicks: 0,
      totalTicks: 0,
      uptime: Date.now()
    };
    
    // Sync settings
    this.syncEnabled = true;
    this.adaptiveSync = true; // Adjust update rate based on server performance
    this.tpsThreshold = 15.0; // Below this TPS, reduce update rate
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing TPS manager...');
    
    try {
      // Setup TPS monitoring
      this.setupTPSMonitoring();
      
      // Start update loops
      this.startUpdateLoop();
      this.startTPSChecking();
      
      this.initialized = true;
      this.logger.info(`TPS manager initialized with target ${this.targetTPS} TPS`);
      
    } catch (error) {
      this.logger.error('Failed to initialize TPS manager:', error);
      throw error;
    }
  }

  setupTPSMonitoring() {
    if (!this.proxyServer) return;
    
    // Listen for server connection events
    this.proxyServer.on('server_connected', (serverInfo) => {
      this.logger.info(`Monitoring TPS for server: ${serverInfo.host}:${serverInfo.port}`);
      this.initializeServerTPS(serverInfo);
    });
    
    // Listen for server packets to calculate TPS
    this.proxyServer.on('server_packet', (data) => {
      this.processServerPacket(data);
    });
    
    // Listen for player tick events
    this.proxyServer.on('player_tick', (tickData) => {
      this.processPlayerTick(tickData);
    });
  }

  initializeServerTPS(serverInfo) {
    const serverKey = `${serverInfo.host}:${serverInfo.port}`;
    
    this.serverTPS.set(serverKey, {
      address: serverKey,
      currentTPS: 20.0,
      averageTPS: 20.0,
      lastTickTime: Date.now(),
      tickTimes: [],
      lagSpikes: 0,
      connected: true,
      lastUpdate: Date.now()
    });
    
    this.tickHistory.set(serverKey, []);
  }

  processServerPacket(data) {
    const { serverAddress, packet, timestamp } = data;
    
    // Look for specific packets that indicate server ticks
    switch (packet.name) {
      case 'keep_alive':
        this.recordServerTick(serverAddress, timestamp);
        break;
      case 'time_update':
        this.recordServerTick(serverAddress, timestamp);
        break;
      case 'player_position':
        this.recordServerTick(serverAddress, timestamp);
        break;
    }
  }

  processPlayerTick(tickData) {
    const { serverAddress, tickTime, players } = tickData;
    
    // Record tick for TPS calculation
    this.recordServerTick(serverAddress, tickTime);
    
    // Update player positions at current rate
    this.emit('player_update', {
      serverAddress,
      players,
      tps: this.getCurrentTPS(serverAddress),
      updateRate: this.currentUpdateRate
    });
  }

  recordServerTick(serverAddress, timestamp) {
    const serverData = this.serverTPS.get(serverAddress);
    if (!serverData) return;
    
    const now = timestamp || Date.now();
    const timeSinceLastTick = now - serverData.lastTickTime;
    
    // Store tick time
    serverData.tickTimes.push(timeSinceLastTick);
    if (serverData.tickTimes.length > this.tickWindowSize) {
      serverData.tickTimes.shift();
    }
    
    // Calculate current TPS
    const averageTickTime = serverData.tickTimes.reduce((a, b) => a + b, 0) / serverData.tickTimes.length;
    const currentTPS = Math.min(1000 / averageTickTime, 20.0);
    
    serverData.currentTPS = currentTPS;
    serverData.averageTPS = this.calculateMovingAverage(serverData.averageTPS, currentTPS, 0.1);
    serverData.lastTickTime = now;
    serverData.lastUpdate = now;
    
    // Detect lag spikes
    if (timeSinceLastTick > 100) { // More than 100ms between ticks
      serverData.lagSpikes++;
      this.performance.lagSpikes++;
      this.emit('lag_spike', { serverAddress, delay: timeSinceLastTick, tps: currentTPS });
    }
    
    // Update global performance metrics
    this.performance.totalTicks++;
    this.performance.averageTPS = this.calculateMovingAverage(this.performance.averageTPS, currentTPS, 0.05);
    
    // Emit TPS update
    this.emit('tps_update', {
      serverAddress,
      currentTPS,
      averageTPS: serverData.averageTPS,
      lagSpikes: serverData.lagSpikes
    });
  }

  calculateMovingAverage(currentAverage, newValue, alpha) {
    return currentAverage * (1 - alpha) + newValue * alpha;
  }

  getCurrentTPS(serverAddress) {
    const serverData = this.serverTPS.get(serverAddress);
    return serverData ? serverData.currentTPS : 20.0;
  }

  getAverageTPS() {
    if (this.serverTPS.size === 0) return 20.0;
    
    let totalTPS = 0;
    for (const serverData of this.serverTPS.values()) {
      totalTPS += serverData.currentTPS;
    }
    
    return totalTPS / this.serverTPS.size;
  }

  startUpdateLoop() {
    const updateInterval = 1000 / this.currentUpdateRate; // Convert to milliseconds
    
    this.updateInterval = setInterval(() => {
      this.emitPeriodicUpdate();
    }, updateInterval);
    
    this.logger.debug(`Started update loop at ${this.currentUpdateRate} updates/second`);
  }

  startTPSChecking() {
    // Check TPS every 5 seconds and adjust update rate if needed
    this.tpsCheckInterval = setInterval(() => {
      this.adjustUpdateRate();
      this.cleanupOldData();
    }, 5000);
  }

  emitPeriodicUpdate() {
    // Emit update event with current server state
    const updateData = {
      timestamp: Date.now(),
      tps: this.getAverageTPS(),
      updateRate: this.currentUpdateRate,
      servers: this.getServerSummary(),
      performance: { ...this.performance }
    };
    
    this.emit('periodic_update', updateData);
  }

  adjustUpdateRate() {
    if (!this.adaptiveSync) return;
    
    const averageTPS = this.getAverageTPS();
    let newUpdateRate = this.currentUpdateRate;
    
    // Reduce update rate if server TPS is low
    if (averageTPS < this.tpsThreshold) {
      // Scale update rate based on server performance
      const tpsRatio = Math.max(averageTPS / this.targetTPS, 0.1);
      newUpdateRate = Math.max(
        Math.floor(this.targetTPS * tpsRatio),
        this.minUpdateRate
      );
      
      this.logger.debug(`Server TPS low (${averageTPS.toFixed(1)}), reducing update rate to ${newUpdateRate}`);
      
    } else if (averageTPS >= this.targetTPS * 0.9) {
      // Increase update rate if server is performing well
      newUpdateRate = Math.min(this.targetTPS, this.maxUpdateRate);
    }
    
    if (newUpdateRate !== this.currentUpdateRate) {
      this.setUpdateRate(newUpdateRate);
    }
  }

  setUpdateRate(newRate) {
    const oldRate = this.currentUpdateRate;
    this.currentUpdateRate = Math.max(this.minUpdateRate, Math.min(this.maxUpdateRate, newRate));
    
    if (this.currentUpdateRate !== oldRate) {
      this.logger.info(`Update rate changed from ${oldRate} to ${this.currentUpdateRate} updates/second`);
      
      // Restart update loop with new rate
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.startUpdateLoop();
      }
      
      this.emit('update_rate_changed', {
        oldRate,
        newRate: this.currentUpdateRate,
        reason: 'tps_adjustment'
      });
    }
  }

  cleanupOldData() {
    const now = Date.now();
    const maxAge = 30000; // 30 seconds
    
    // Remove old server data
    for (const [serverAddress, serverData] of this.serverTPS.entries()) {
      if (now - serverData.lastUpdate > maxAge) {
        this.logger.debug(`Removing stale TPS data for ${serverAddress}`);
        this.serverTPS.delete(serverAddress);
        this.tickHistory.delete(serverAddress);
      }
    }
  }

  getServerSummary() {
    const summary = [];
    
    for (const [address, data] of this.serverTPS.entries()) {
      summary.push({
        address,
        currentTPS: Math.round(data.currentTPS * 100) / 100,
        averageTPS: Math.round(data.averageTPS * 100) / 100,
        lagSpikes: data.lagSpikes,
        connected: data.connected,
        lastUpdate: data.lastUpdate
      });
    }
    
    return summary;
  }

  getPerformanceMetrics() {
    const uptime = Date.now() - this.performance.uptime;
    
    return {
      ...this.performance,
      uptime,
      currentUpdateRate: this.currentUpdateRate,
      targetTPS: this.targetTPS,
      syncEnabled: this.syncEnabled,
      adaptiveSync: this.adaptiveSync,
      servers: this.getServerSummary()
    };
  }

  // Force sync to specific TPS
  forceTPS(targetTPS) {
    this.targetTPS = Math.max(1, Math.min(30, targetTPS));
    this.setUpdateRate(this.targetTPS);
    this.logger.info(`Forced TPS to ${this.targetTPS}`);
  }

  // Enable/disable adaptive sync
  setAdaptiveSync(enabled) {
    this.adaptiveSync = enabled;
    this.logger.info(`Adaptive sync ${enabled ? 'enabled' : 'disabled'}`);
    
    if (!enabled) {
      // Reset to target TPS when disabled
      this.setUpdateRate(this.targetTPS);
    }
  }

  async shutdown() {
    this.logger.info('Shutting down TPS manager...');
    
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
      this.updateInterval = null;
    }
    
    if (this.tpsCheckInterval) {
      clearInterval(this.tpsCheckInterval);
      this.tpsCheckInterval = null;
    }
    
    this.serverTPS.clear();
    this.tickHistory.clear();
    
    this.initialized = false;
    this.logger.info('TPS manager shut down');
  }
}

module.exports = TPSManager;