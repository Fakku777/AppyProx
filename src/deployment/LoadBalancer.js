const EventEmitter = require('events');
const net = require('net');
const http = require('http');

/**
 * Load Balancer for AppyProx
 * Distributes incoming connections across multiple worker instances
 */
class LoadBalancer extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('LoadBalancer') : logger;
    
    // Upstream servers
    this.upstreams = new Map();
    this.healthyUpstreams = new Set();
    
    // Load balancing algorithms
    this.algorithms = {
      round_robin: this.roundRobin.bind(this),
      least_connections: this.leastConnections.bind(this),
      ip_hash: this.ipHash.bind(this)
    };
    
    this.currentAlgorithm = this.algorithms[this.config.algorithm] || this.algorithms.round_robin;
    
    // Round robin state
    this.roundRobinIndex = 0;
    
    // Connection tracking
    this.activeConnections = new Map(); // upstreamId -> connection count
    
    // Health check
    this.healthCheckInterval = null;
    
    this.server = null;
    this.isRunning = false;
    
    // Initialize upstreams
    if (this.config.upstream) {
      this.config.upstream.forEach(upstream => this.addUpstream(upstream));
    }
  }

  async start(port) {
    if (this.isRunning) return;
    
    this.logger.info(`Starting load balancer on port ${port}...`);
    
    this.server = net.createServer((clientSocket) => {
      this.handleConnection(clientSocket);
    });
    
    return new Promise((resolve, reject) => {
      this.server.listen(port, '0.0.0.0', (err) => {
        if (err) {
          reject(err);
        } else {
          this.isRunning = true;
          this.startHealthChecks();
          this.logger.info(`Load balancer started on port ${port}`);
          resolve();
        }
      });
    });
  }

  async stop() {
    if (!this.isRunning) return;
    
    this.logger.info('Stopping load balancer...');
    
    // Stop health checks
    if (this.healthCheckInterval) {
      clearInterval(this.healthCheckInterval);
      this.healthCheckInterval = null;
    }
    
    // Close server
    if (this.server) {
      return new Promise((resolve) => {
        this.server.close(() => {
          this.isRunning = false;
          this.logger.info('Load balancer stopped');
          resolve();
        });
      });
    }
  }

  addUpstream(upstream) {
    this.upstreams.set(upstream.id, {
      id: upstream.id,
      host: upstream.host,
      port: upstream.port,
      weight: upstream.weight || 1,
      healthy: true,
      lastHealthCheck: null,
      consecutiveFailures: 0
    });
    
    this.healthyUpstreams.add(upstream.id);
    this.activeConnections.set(upstream.id, 0);
    
    this.logger.info(`Added upstream: ${upstream.host}:${upstream.port} (${upstream.id})`);
  }

  removeUpstream(upstreamId) {
    this.upstreams.delete(upstreamId);
    this.healthyUpstreams.delete(upstreamId);
    this.activeConnections.delete(upstreamId);
    
    this.logger.info(`Removed upstream: ${upstreamId}`);
  }

  handleConnection(clientSocket) {
    // Select upstream server
    const upstream = this.selectUpstream(clientSocket);
    
    if (!upstream) {
      this.logger.warn('No healthy upstreams available');
      clientSocket.destroy();
      return;
    }
    
    // Create connection to upstream
    const upstreamSocket = net.createConnection(upstream.port, upstream.host);
    
    // Track connection
    const currentConnections = this.activeConnections.get(upstream.id) || 0;
    this.activeConnections.set(upstream.id, currentConnections + 1);
    
    this.logger.debug(`Proxying connection to ${upstream.host}:${upstream.port}`);
    
    // Pipe data between client and upstream
    clientSocket.pipe(upstreamSocket);
    upstreamSocket.pipe(clientSocket);
    
    // Handle connection events
    const cleanup = () => {
      const connections = this.activeConnections.get(upstream.id) || 0;
      this.activeConnections.set(upstream.id, Math.max(0, connections - 1));
    };
    
    clientSocket.on('close', cleanup);
    clientSocket.on('error', (err) => {
      this.logger.debug(`Client socket error: ${err.message}`);
      cleanup();
    });
    
    upstreamSocket.on('close', cleanup);
    upstreamSocket.on('error', (err) => {
      this.logger.debug(`Upstream socket error: ${err.message}`);
      cleanup();
    });
    
    // Handle upstream connection failure
    upstreamSocket.on('error', () => {
      this.markUpstreamUnhealthy(upstream.id);
      clientSocket.destroy();
    });
    
    this.emit('connection_proxied', {
      upstreamId: upstream.id,
      clientAddress: clientSocket.remoteAddress
    });
  }

  selectUpstream(clientSocket) {
    const healthyUpstreams = Array.from(this.healthyUpstreams)
      .map(id => this.upstreams.get(id))
      .filter(upstream => upstream && upstream.healthy);
    
    if (healthyUpstreams.length === 0) {
      return null;
    }
    
    return this.currentAlgorithm(healthyUpstreams, clientSocket);
  }

  // Load balancing algorithms
  roundRobin(upstreams) {
    if (upstreams.length === 0) return null;
    
    const upstream = upstreams[this.roundRobinIndex % upstreams.length];
    this.roundRobinIndex++;
    
    return upstream;
  }

  leastConnections(upstreams) {
    if (upstreams.length === 0) return null;
    
    let leastBusy = upstreams[0];
    let minConnections = this.activeConnections.get(leastBusy.id) || 0;
    
    for (const upstream of upstreams) {
      const connections = this.activeConnections.get(upstream.id) || 0;
      if (connections < minConnections) {
        minConnections = connections;
        leastBusy = upstream;
      }
    }
    
    return leastBusy;
  }

  ipHash(upstreams, clientSocket) {
    if (upstreams.length === 0) return null;
    
    // Simple hash based on client IP
    const clientIP = clientSocket.remoteAddress || '127.0.0.1';
    const hash = this.hashString(clientIP);
    const index = hash % upstreams.length;
    
    return upstreams[index];
  }

  hashString(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32-bit integer
    }
    return Math.abs(hash);
  }

  // Health checking
  startHealthChecks() {
    if (this.healthCheckInterval) return;
    
    this.logger.info('Starting health checks...');
    
    this.healthCheckInterval = setInterval(() => {
      this.performHealthChecks();
    }, this.config.healthCheck.interval);
    
    // Perform initial health check
    this.performHealthChecks();
  }

  async performHealthChecks() {
    for (const [upstreamId, upstream] of this.upstreams) {
      try {
        const healthy = await this.checkUpstreamHealth(upstream);
        
        if (healthy) {
          if (!upstream.healthy) {
            this.logger.info(`Upstream ${upstreamId} recovered`);
            upstream.healthy = true;
            upstream.consecutiveFailures = 0;
            this.healthyUpstreams.add(upstreamId);
          }
        } else {
          upstream.consecutiveFailures++;
          
          if (upstream.healthy && upstream.consecutiveFailures >= this.config.healthCheck.retries) {
            this.logger.warn(`Upstream ${upstreamId} marked as unhealthy`);
            this.markUpstreamUnhealthy(upstreamId);
          }
        }
        
        upstream.lastHealthCheck = Date.now();
        
      } catch (error) {
        this.logger.debug(`Health check error for ${upstreamId}: ${error.message}`);
        upstream.consecutiveFailures++;
        
        if (upstream.healthy && upstream.consecutiveFailures >= this.config.healthCheck.retries) {
          this.markUpstreamUnhealthy(upstreamId);
        }
      }
    }
  }

  async checkUpstreamHealth(upstream) {
    return new Promise((resolve) => {
      const timeout = setTimeout(() => {
        resolve(false);
      }, this.config.healthCheck.timeout);
      
      // Try to connect to the upstream
      const socket = net.createConnection(upstream.port, upstream.host);
      
      socket.on('connect', () => {
        clearTimeout(timeout);
        socket.destroy();
        resolve(true);
      });
      
      socket.on('error', () => {
        clearTimeout(timeout);
        resolve(false);
      });
    });
  }

  markUpstreamUnhealthy(upstreamId) {
    const upstream = this.upstreams.get(upstreamId);
    if (upstream) {
      upstream.healthy = false;
      this.healthyUpstreams.delete(upstreamId);
      
      this.emit('upstream_unhealthy', {
        upstreamId: upstreamId,
        upstream: upstream
      });
    }
  }

  // Status and statistics
  getStatus() {
    const upstreams = Array.from(this.upstreams.values()).map(upstream => ({
      id: upstream.id,
      host: upstream.host,
      port: upstream.port,
      healthy: upstream.healthy,
      connections: this.activeConnections.get(upstream.id) || 0,
      consecutiveFailures: upstream.consecutiveFailures,
      lastHealthCheck: upstream.lastHealthCheck
    }));
    
    return {
      isRunning: this.isRunning,
      algorithm: this.config.algorithm,
      totalUpstreams: this.upstreams.size,
      healthyUpstreams: this.healthyUpstreams.size,
      totalConnections: Array.from(this.activeConnections.values()).reduce((sum, count) => sum + count, 0),
      upstreams: upstreams
    };
  }

  getStatistics() {
    const stats = {
      totalUpstreams: this.upstreams.size,
      healthyUpstreams: this.healthyUpstreams.size,
      totalConnections: Array.from(this.activeConnections.values()).reduce((sum, count) => sum + count, 0),
      connectionDistribution: {},
      averageConnectionsPerUpstream: 0
    };
    
    // Calculate connection distribution
    for (const [upstreamId, connections] of this.activeConnections) {
      const upstream = this.upstreams.get(upstreamId);
      if (upstream) {
        stats.connectionDistribution[upstreamId] = {
          host: upstream.host,
          port: upstream.port,
          connections: connections,
          healthy: upstream.healthy,
          percentage: stats.totalConnections > 0 ? (connections / stats.totalConnections * 100).toFixed(1) : 0
        };
      }
    }
    
    // Calculate average
    stats.averageConnectionsPerUpstream = this.healthyUpstreams.size > 0 ? 
      (stats.totalConnections / this.healthyUpstreams.size).toFixed(1) : 0;
    
    return stats;
  }
}

module.exports = LoadBalancer;