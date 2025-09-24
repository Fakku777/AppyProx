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
// const CentralNode = require('../central-node/CentralNode'); // Temporarily disabled
const AppyProxAPI = require('../api/index');
const ProxyClientBridge = require('./ProxyClientBridge');
const Logger = require('./utils/Logger');
const ConfigurationManager = require('../config/ConfigurationManager');
const ErrorRecoverySystem = require('../error-handling/ErrorRecoverySystem');
const { CircuitBreakerManager } = require('../error-handling/CircuitBreaker');
const HealthMonitor = require('../error-handling/HealthMonitor');
const DeploymentManager = require('../deployment/DeploymentManager');
const WebUIServer = require('../web-ui/WebUIServer');
const GroupsIntegration = require('../web-ui/minecraft/GroupsIntegration');

class AppyProx {
  constructor() {
    this.configPath = path.join(__dirname, '../../configs/config.json');
    this.config = this.loadConfig();
    this.logger = new Logger(this.config.logging);
    
    // Core components
    this.configManager = null;
    this.proxyServer = null;
    this.clusterManager = null;
    this.automationEngine = null;
    this.centralNode = null;
    this.apiServer = null;
    this.proxyClientBridge = null;
    this.deploymentManager = null;
    
    // Error handling components
    this.errorRecovery = null;
    this.circuitBreaker = null;
    this.healthMonitor = null;
    
    // Web UI server
    this.webUIServer = null;
    
    // Groups system
    this.groupsIntegration = null;
    
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

      // Initialize configuration manager
      this.configManager = new ConfigurationManager(this.logger);
      await this.configManager.initialize();
      
      // Initialize deployment manager
      this.deploymentManager = new DeploymentManager(this.config, this.logger);
      await this.deploymentManager.initialize();
      
      // Initialize circuit breaker manager
      this.circuitBreaker = new CircuitBreakerManager(this.logger);
      this.circuitBreaker.initializeDefaultBreakers();

      // Initialize core proxy server
      this.proxyServer = new ProxyServer(this.config.proxy, this.logger);
      
      // Initialize cluster manager
      this.clusterManager = new ClusterManager(this.config.clustering, this.logger);
      
      // Initialize automation engine
      this.automationEngine = new AutomationEngine(this.config.automation, this.logger);
      
      // Create component references for error handling
      const components = {
        proxyServer: this.proxyServer,
        clusterManager: this.clusterManager,
        automationEngine: this.automationEngine,
        configManager: this.configManager,
        deploymentManager: this.deploymentManager,
        circuitBreaker: this.circuitBreaker
      };
      
      // Initialize central management node if enabled
      // if (this.config.central_node && this.config.central_node.enabled) {
      //   this.centralNode = new CentralNode(this.config.central_node, this.logger, {
      //     proxyServer: this.proxyServer,
      //     clusterManager: this.clusterManager,
      //     automationEngine: this.automationEngine
      //   });
      //   components.centralNode = this.centralNode;
      // }

      // Initialize API server
      this.apiServer = new AppyProxAPI(this.config.api, this.logger, this);
      
      // Initialize Proxy Client Bridge
      if (this.config.proxy_client_bridge && this.config.proxy_client_bridge.enabled) {
        this.proxyClientBridge = new ProxyClientBridge(this.config.proxy_client_bridge, this.logger);
      }
      
      // Initialize error handling systems
      this.healthMonitor = new HealthMonitor(this.config, this.logger, components);
      components.healthMonitor = this.healthMonitor;
      
      this.errorRecovery = new ErrorRecoverySystem(this.config, this.logger, components);
      components.errorRecovery = this.errorRecovery;
      
      await this.errorRecovery.initialize();
      await this.healthMonitor.initialize();
      
      // Initialize Groups integration
      this.groupsIntegration = new GroupsIntegration(null, this.config, this.logger);
      components.groupsIntegration = this.groupsIntegration;
      
      // Initialize Web UI server
      this.webUIServer = new WebUIServer(this.config, this.logger, components);
      
      // Set the webUIServer reference in groups integration
      this.groupsIntegration.webUIServer = this.webUIServer;
      
      await this.webUIServer.initialize();
      
      // Initialize Groups integration after WebUI is ready
      await this.groupsIntegration.initialize();
      
      // Setup error handling event listeners
      this.setupErrorHandlingEvents();

      // Connect components
      this.connectComponents();

      this.logger.info('AppyProx initialization completed');
    } catch (error) {
      this.logger.error('Failed to initialize AppyProx:', error);
      throw error;
    }
  }

  connectComponents() {
    // Connect proxy server events to both cluster manager and groups system
    this.proxyServer.on('client_connected', (client) => {
      // Keep existing cluster manager for backward compatibility
      this.clusterManager.registerClient(client);
      
      // Register with new groups system
      if (this.groupsIntegration) {
        this.groupsIntegration.handleClientRegistration(client);
      }
    });

    this.proxyServer.on('client_disconnected', (client) => {
      // Keep existing cluster manager for backward compatibility
      this.clusterManager.unregisterClient(client);
      
      // Unregister from new groups system
      if (this.groupsIntegration) {
        this.groupsIntegration.handleClientDisconnection(client);
      }
    });

    // Connect cluster manager to automation engine (keep existing)
    this.clusterManager.on('cluster_update', (clusterData) => {
      this.automationEngine.updateClusterStatus(clusterData);
    });
    
    // Connect Groups system to automation engine
    if (this.groupsIntegration) {
      const groupManager = this.groupsIntegration.getGroupManager();
      groupManager.on('group_update', (groupData) => {
        // Forward group updates to automation engine
        if (this.automationEngine && this.automationEngine.updateGroupStatus) {
          this.automationEngine.updateGroupStatus(groupData);
        }
      });
      
      groupManager.on('task_assigned_to_group', (taskData) => {
        // Forward task assignments to automation engine
        if (this.automationEngine && this.automationEngine.handleGroupTask) {
          this.automationEngine.handleGroupTask(taskData);
        }
      });
    }

    // Connect to central node if enabled
    // if (this.centralNode) {
    //   this.clusterManager.on('account_status_update', (status) => {
    //     this.centralNode.updateAccountStatus(status);
    //   });
    // 
    //   this.automationEngine.on('task_progress', (progress) => {
    //     this.centralNode.updateTaskProgress(progress);
    //   });
    // }
    
    // Connect Proxy Client Bridge to other components
    if (this.proxyClientBridge) {
      this.proxyClientBridge.setClusterManagerInterface(this.clusterManager);
      this.proxyClientBridge.setAutomationEngineInterface(this.automationEngine);
      // if (this.centralNode) {
      //   this.proxyClientBridge.setCentralNodeInterface(this.centralNode);
      // }
      
      // Forward events from bridge to other components
      this.proxyClientBridge.on('client_connected', (client) => {
        this.logger.info(`Proxy client connected via bridge: ${client.username}`);
      });
      
      this.proxyClientBridge.on('client_disconnected', (client) => {
        this.logger.info(`Proxy client disconnected via bridge: ${client.id}`);
      });
      
      this.proxyClientBridge.on('task_progress', (progress) => {
        this.logger.debug(`Task progress from bridge: ${progress.taskId}`);
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
      
      // Groups system is already initialized and started via webUIServer initialization

      // Start automation engine
      await this.automationEngine.start();
      this.logger.info('Automation engine started');

      // Start central node
      // if (this.centralNode) {
      //   await this.centralNode.start();
      //   this.logger.info(`Central node started on port ${this.config.central_node.web_interface_port}`);
      // }

      // Start API server
      if (this.apiServer) {
        await this.apiServer.start();
        this.logger.info(`API server started on port ${this.config.api.port}`);
      }
      
      // Start Proxy Client Bridge
      if (this.proxyClientBridge) {
        await this.proxyClientBridge.start();
        this.logger.info('Proxy Client Bridge started successfully');
      }
      
      // Initialize and start Advanced Automation Integration
      if (this.advancedIntegrator) {
        await this.advancedIntegrator.initialize();
        this.logger.info('Advanced Automation Integration initialized and ready');
      }

      this.isRunning = true;
      this.logger.info('🚀 AppyProx is now running!');
      
      if (this.advancedIntegrator) {
        this.logger.info('🎯 Advanced automation features are active!');
      }

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
      if (this.advancedIntegrator) {
        await this.advancedIntegrator.shutdown();
        this.logger.info('Advanced Automation Integration stopped');
      }
      
      if (this.proxyClientBridge) {
        await this.proxyClientBridge.stop();
        this.logger.info('Proxy Client Bridge stopped');
      }
      
      if (this.apiServer) {
        await this.apiServer.stop();
        this.logger.info('API server stopped');
      }

      // if (this.centralNode) {
      //   await this.centralNode.stop();
      //   this.logger.info('Central node stopped');
      // }

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
      
      // Stop Groups integration
      if (this.groupsIntegration) {
        await this.groupsIntegration.shutdown();
        this.logger.info('Groups integration stopped');
      }
      
      // Stop Web UI server
      if (this.webUIServer) {
        await this.webUIServer.stop();
        this.logger.info('Web UI server stopped');
      }
      
      // Stop error handling systems
      if (this.healthMonitor) {
        await this.healthMonitor.stop();
        this.logger.info('Health monitoring stopped');
      }
      
      if (this.errorRecovery) {
        // ErrorRecoverySystem doesn't have a stop method, but we can clean up
        this.errorRecovery.removeAllListeners();
        this.logger.info('Error recovery system cleaned up');
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
      centralNode: this.centralNode ? this.centralNode.getStatus() : null,
      proxyClientBridge: this.proxyClientBridge ? this.proxyClientBridge.getStatus() : null,
      deploymentManager: this.deploymentManager ? this.deploymentManager.getStatus() : null,
      webUI: this.webUIServer ? this.webUIServer.getStatus() : null,
      groups: this.groupsIntegration ? {
        enabled: true,
        totalGroups: this.groupsIntegration.getAllGroups().length,
        groupManager: this.groupsIntegration.getGroupManager().isRunning
      } : { enabled: false },
      errorHandling: {
        errorRecovery: this.errorRecovery ? this.errorRecovery.getErrorStatistics() : null,
        circuitBreaker: this.circuitBreaker ? this.circuitBreaker.getGlobalStats() : null,
        healthMonitor: this.healthMonitor ? this.healthMonitor.getHealthSummary() : null
      },
      api: {
        enabled: this.config.api && this.config.api.enabled,
        port: this.config.api ? this.config.api.port : null
      }
    };
  }
  
  // Additional method for advanced automation features
  getAdvancedComponent(componentName) {
    return this.advancedIntegrator ? this.advancedIntegrator.getComponent(componentName) : null;
  }
  
  async executeTemplateTask(templateId, parameters = {}, options = {}) {
    if (!this.advancedIntegrator) {
      throw new Error('Advanced automation integration not available');
    }
    return await this.advancedIntegrator.executeTemplateTask(templateId, parameters, options);
  }
  
  async getAdvancedAnalytics() {
    if (!this.advancedIntegrator) {
      return null;
    }
    return await this.advancedIntegrator.getAdvancedAnalytics();
  }
  
  setupErrorHandlingEvents() {
    if (!this.errorRecovery || !this.circuitBreaker || !this.healthMonitor) return;
    
    // Error recovery events
    this.errorRecovery.on('error_occurred', (errorRecord) => {
      this.logger.debug(`Error tracked: ${errorRecord.type} - ${errorRecord.message}`);
    });
    
    this.errorRecovery.on('recovery_successful', ({ errorId, recoveryResult }) => {
      this.logger.info(`Error recovery successful: ${errorId} - ${recoveryResult.action}`);
    });
    
    this.errorRecovery.on('recovery_failed', ({ errorId }) => {
      this.logger.warn(`Error recovery failed: ${errorId}`);
    });
    
    this.errorRecovery.on('critical_error', ({ errorId, type }) => {
      this.logger.error(`Critical error detected: ${type} (${errorId})`);
    });
    
    this.errorRecovery.on('backup_created', ({ backupId }) => {
      this.logger.info(`System backup created: ${backupId}`);
    });
    
    this.errorRecovery.on('rollback_completed', ({ label }) => {
      this.logger.info(`System rollback completed: ${label}`);
    });
    
    this.errorRecovery.on('error_pattern_detected', ({ type, frequency }) => {
      this.logger.warn(`Error pattern detected: ${type} occurring ${frequency.toFixed(1)} times/hour`);
    });
    
    this.errorRecovery.on('preventive_suggestion', ({ errorType, suggestion }) => {
      this.logger.info(`Preventive suggestion for ${errorType}: ${suggestion}`);
    });
    
    // Circuit breaker events
    this.circuitBreaker.on('breaker_opened', ({ name, recentFailures }) => {
      this.logger.warn(`Circuit breaker opened: ${name} (${recentFailures} failures)`);
    });
    
    this.circuitBreaker.on('breaker_closed', ({ name }) => {
      this.logger.info(`Circuit breaker closed: ${name}`);
    });
    
    this.circuitBreaker.on('request_rejected', ({ name }) => {
      this.logger.debug(`Circuit breaker rejected request: ${name}`);
    });
    
    // Health monitor events
    this.healthMonitor.on('health_alert', (alert) => {
      this.logger.warn(`Health alert: ${alert.message}`);
      
      // Create rollback point on critical health alerts
      if (alert.severity === 'critical') {
        this.errorRecovery.createRollbackPoint('critical-health-alert');
      }
    });
    
    this.healthMonitor.on('monitoring_started', () => {
      this.logger.info('Health monitoring started');
    });
    
    this.healthMonitor.on('monitoring_stopped', () => {
      this.logger.info('Health monitoring stopped');
    });
  }
  
  // Public methods for error handling system
  async createBackup(label) {
    if (!this.errorRecovery) {
      throw new Error('Error recovery system not initialized');
    }
    return await this.errorRecovery.createBackup(label);
  }
  
  async rollback(label) {
    if (!this.errorRecovery) {
      throw new Error('Error recovery system not initialized');
    }
    return await this.errorRecovery.rollback(label);
  }
  
  getHealthStatus() {
    if (!this.healthMonitor) {
      return { status: 'unknown', message: 'Health monitoring not available' };
    }
    return this.healthMonitor.getCurrentHealth();
  }
  
  getErrorRecoveryStats() {
    if (!this.errorRecovery) {
      return null;
    }
    return this.errorRecovery.getErrorStatistics();
  }
  
  async executeWithCircuitBreaker(operationName, operation, fallback = null) {
    if (!this.circuitBreaker) {
      throw new Error('Circuit breaker not initialized');
    }
    return await this.circuitBreaker.execute(operationName, operation, fallback);
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