#!/usr/bin/env node
/**
 * AppyProx - Advanced Minecraft Proxy
 * Main entry point for the proxy server
 * Author: AprilRenders
 */

const fs = require('fs');
const path = require('path');
const ProxyServer = require('./ProxyServer');
const ClusterManager = require('../clustering/ClusterManager');
const AutomationEngine = require('../automation/AutomationEngine');
const CentralNode = require('../central-node/CentralNode');
const Logger = require('./utils/Logger');

class AppyProx {
  constructor() {
    this.configPath = path.join(__dirname, '../../configs/config.json');
    this.config = this.loadConfig();
    this.logger = new Logger(this.config.logging);
    
    // Core components
    this.proxyServer = null;
    this.clusterManager = null;
    this.automationEngine = null;
    this.centralNode = null;
    
    this.isRunning = false;
  }

  loadConfig() {
    try {
      if (fs.existsSync(this.configPath)) {
        const configData = fs.readFileSync(this.configPath, 'utf8');
        return JSON.parse(configData);
      } else {
        // Use default config if custom config doesn't exist
        const defaultConfigPath = path.join(__dirname, '../../configs/default.json');
        const defaultConfig = JSON.parse(fs.readFileSync(defaultConfigPath, 'utf8'));
        
        // Create config.json from default
        fs.writeFileSync(this.configPath, JSON.stringify(defaultConfig, null, 2));
        console.log('Created config.json from default configuration');
        
        return defaultConfig;
      }
    } catch (error) {
      console.error('Failed to load configuration:', error.message);
      process.exit(1);
    }
  }

  async initialize() {
    try {
      this.logger.info('Initializing AppyProx...');

      // Initialize core proxy server
      this.proxyServer = new ProxyServer(this.config.proxy, this.logger);
      
      // Initialize cluster manager
      this.clusterManager = new ClusterManager(this.config.clustering, this.logger);
      
      // Initialize automation engine
      this.automationEngine = new AutomationEngine(this.config.automation, this.logger);
      
      // Initialize central management node if enabled
      if (this.config.central_node.enabled) {
        this.centralNode = new CentralNode(this.config.central_node, this.logger);
      }

      // Connect components
      this.connectComponents();

      this.logger.info('AppyProx initialization completed');
    } catch (error) {
      this.logger.error('Failed to initialize AppyProx:', error);
      throw error;
    }
  }

  connectComponents() {
    // Connect proxy server events to cluster manager
    this.proxyServer.on('client_connected', (client) => {
      this.clusterManager.registerClient(client);
    });

    this.proxyServer.on('client_disconnected', (client) => {
      this.clusterManager.unregisterClient(client);
    });

    // Connect cluster manager to automation engine
    this.clusterManager.on('cluster_update', (clusterData) => {
      this.automationEngine.updateClusterStatus(clusterData);
    });

    // Connect to central node if enabled
    if (this.centralNode) {
      this.clusterManager.on('account_status_update', (status) => {
        this.centralNode.updateAccountStatus(status);
      });

      this.automationEngine.on('task_progress', (progress) => {
        this.centralNode.updateTaskProgress(progress);
      });
    }

    this.logger.info('Component connections established');
  }

  async start() {
    if (this.isRunning) {
      this.logger.warn('AppyProx is already running');
      return;
    }

    try {
      await this.initialize();

      // Start proxy server
      await this.proxyServer.start();
      this.logger.info(`Proxy server started on ${this.config.proxy.host}:${this.config.proxy.port}`);

      // Start cluster manager
      await this.clusterManager.start();
      this.logger.info('Cluster manager started');

      // Start automation engine
      await this.automationEngine.start();
      this.logger.info('Automation engine started');

      // Start central node
      if (this.centralNode) {
        await this.centralNode.start();
        this.logger.info(`Central node started on port ${this.config.central_node.web_interface_port}`);
      }

      this.isRunning = true;
      this.logger.info('🚀 AppyProx is now running!');

      // Setup graceful shutdown
      this.setupGracefulShutdown();

    } catch (error) {
      this.logger.error('Failed to start AppyProx:', error);
      throw error;
    }
  }

  async stop() {
    if (!this.isRunning) {
      this.logger.warn('AppyProx is not running');
      return;
    }

    try {
      this.logger.info('Shutting down AppyProx...');

      // Stop components in reverse order
      if (this.centralNode) {
        await this.centralNode.stop();
        this.logger.info('Central node stopped');
      }

      if (this.automationEngine) {
        await this.automationEngine.stop();
        this.logger.info('Automation engine stopped');
      }

      if (this.clusterManager) {
        await this.clusterManager.stop();
        this.logger.info('Cluster manager stopped');
      }

      if (this.proxyServer) {
        await this.proxyServer.stop();
        this.logger.info('Proxy server stopped');
      }

      this.isRunning = false;
      this.logger.info('AppyProx shutdown completed');

    } catch (error) {
      this.logger.error('Error during shutdown:', error);
      throw error;
    }
  }

  setupGracefulShutdown() {
    const shutdown = async (signal) => {
      this.logger.info(`Received ${signal}, initiating graceful shutdown...`);
      try {
        await this.stop();
        process.exit(0);
      } catch (error) {
        this.logger.error('Error during graceful shutdown:', error);
        process.exit(1);
      }
    };

    process.on('SIGTERM', () => shutdown('SIGTERM'));
    process.on('SIGINT', () => shutdown('SIGINT'));
    process.on('uncaughtException', (error) => {
      this.logger.error('Uncaught exception:', error);
      shutdown('uncaughtException');
    });
    process.on('unhandledRejection', (reason, promise) => {
      this.logger.error('Unhandled rejection at:', promise, 'reason:', reason);
      shutdown('unhandledRejection');
    });
  }

  getStatus() {
    return {
      running: this.isRunning,
      proxy: this.proxyServer ? this.proxyServer.getStatus() : null,
      clusters: this.clusterManager ? this.clusterManager.getStatus() : null,
      automation: this.automationEngine ? this.automationEngine.getStatus() : null,
      centralNode: this.centralNode ? this.centralNode.getStatus() : null
    };
  }
}

// CLI entry point
if (require.main === module) {
  const appyProx = new AppyProx();
  
  appyProx.start().catch(error => {
    console.error('Failed to start AppyProx:', error);
    process.exit(1);
  });
}

module.exports = AppyProx;