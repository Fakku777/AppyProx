const EventEmitter = require('events');
const { spawn, exec } = require('child_process');
const net = require('net');
const fs = require('fs');
const path = require('path');

/**
 * Bridge between AppyProx JavaScript components and Java Proxy Client Management System
 * Handles spawning of the Java system, IPC communication, and event coordination
 */
class ProxyClientBridge extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('ProxyClientBridge') : logger;
    
    // Java process management
    this.javaProcess = null;
    this.isRunning = false;
    this.restartAttempts = 0;
    this.maxRestartAttempts = 5;
    
    // Communication
    this.bridgePort = this.config.bridge_port || 25800;
    this.bridgeServer = null;
    this.bridgeSocket = null;
    this.commandQueue = [];
    this.pendingCommands = new Map();
    this.commandIdCounter = 0;
    
    // State tracking
    this.connectedClients = new Map(); // clientId -> clientInfo
    this.clientStatuses = new Map(); // clientId -> status
    this.clientMetrics = new Map(); // clientId -> metrics
    
    // Integration interfaces for AppyProx components
    this.clusterManagerInterface = null;
    this.automationEngineInterface = null;
    this.centralNodeInterface = null;
    
    this.setupBridgeServer();
  }

  async start() {
    if (this.isRunning) return;

    this.logger.info('Starting Proxy Client Bridge...');

    try {
      // Start bridge server
      await this.startBridgeServer();
      
      // Start Java Proxy Client Management System
      await this.startJavaSystem();
      
      // Wait for Java system to connect
      await this.waitForJavaConnection();
      
      this.isRunning = true;
      this.logger.info('Proxy Client Bridge started successfully');
      
    } catch (error) {
      this.logger.error('Failed to start Proxy Client Bridge:', error);
      throw error;
    }
  }

  async stop() {
    if (!this.isRunning) return;

    this.logger.info('Stopping Proxy Client Bridge...');

    try {
      // Stop Java system first
      if (this.javaProcess) {
        this.sendCommand('SYSTEM_SHUTDOWN', {});
        
        // Give it time to shutdown gracefully
        await new Promise(resolve => setTimeout(resolve, 5000));
        
        if (!this.javaProcess.killed) {
          this.javaProcess.kill('SIGTERM');
        }
        
        this.javaProcess = null;
      }

      // Close bridge server
      if (this.bridgeServer) {
        this.bridgeServer.close();
        this.bridgeServer = null;
      }

      this.isRunning = false;
      this.logger.info('Proxy Client Bridge stopped');
      
    } catch (error) {
      this.logger.error('Error stopping Proxy Client Bridge:', error);
      throw error;
    }
  }

  setupBridgeServer() {
    this.bridgeServer = net.createServer((socket) => {
      this.logger.info('Java system connected to bridge');
      this.bridgeSocket = socket;
      
      socket.on('data', (data) => {
        this.handleJavaMessage(data.toString());
      });
      
      socket.on('close', () => {
        this.logger.warn('Java system disconnected from bridge');
        this.bridgeSocket = null;
        this.handleJavaDisconnection();
      });
      
      socket.on('error', (error) => {
        this.logger.error('Bridge socket error:', error);
      });
      
      this.emit('java_connected');
    });
  }

  async startBridgeServer() {
    return new Promise((resolve, reject) => {
      this.bridgeServer.listen(this.bridgePort, '127.0.0.1', (error) => {
        if (error) {
          reject(error);
        } else {
          this.logger.info(`Bridge server listening on port ${this.bridgePort}`);
          resolve();
        }
      });
    });
  }

  async startJavaSystem() {
    return new Promise(async (resolve, reject) => {
      const javaExecutable = this.config.java_executable || 'java';
      const jarPath = path.join(__dirname, '../../AppyProx-FabricMod/build/libs/appyprox-fabric.jar');
      const configPath = path.join(__dirname, '../../configs');
      
      // Check if JAR exists
      if (!fs.existsSync(jarPath)) {
        this.logger.warn(`JAR not found at ${jarPath}, attempting to build...`);
        try {
          await this.buildJavaSystem();
        } catch (buildError) {
          reject(new Error(`Failed to build Java system: ${buildError.message}`));
          return;
        }
      }

      const javaArgs = [
        '-Xms512m',
        '-Xmx2048m',
        '-Dappyprox.config.path=' + configPath,
        '-Dappyprox.bridge.port=' + this.bridgePort,
        '-Dappyprox.bridge.host=127.0.0.1',
        '-jar',
        jarPath
      ];

      this.logger.info(`Starting Java system: ${javaExecutable} ${javaArgs.join(' ')}`);

      this.javaProcess = spawn(javaExecutable, javaArgs, {
        stdio: ['pipe', 'pipe', 'pipe'],
        env: {
          ...process.env,
          APPYPROX_BRIDGE_PORT: this.bridgePort.toString(),
          APPYPROX_CONFIG_PATH: configPath
        }
      });

      this.javaProcess.stdout.on('data', (data) => {
        const output = data.toString().trim();
        if (output) {
          this.logger.debug(`Java: ${output}`);
          
          // Check for startup completion
          if (output.includes('ProxyClientIntegration initialized')) {
            resolve();
          }
        }
      });

      this.javaProcess.stderr.on('data', (data) => {
        const error = data.toString().trim();
        if (error) {
          this.logger.warn(`Java Error: ${error}`);
        }
      });

      this.javaProcess.on('exit', (code) => {
        this.logger.info(`Java system exited with code ${code}`);
        this.javaProcess = null;
        
        if (this.isRunning && this.restartAttempts < this.maxRestartAttempts) {
          this.logger.info(`Attempting to restart Java system (attempt ${this.restartAttempts + 1})`);
          this.restartAttempts++;
          setTimeout(() => this.startJavaSystem().catch(console.error), 5000);
        }
      });

      // Timeout for startup
      setTimeout(() => {
        if (this.javaProcess && !this.bridgeSocket) {
          reject(new Error('Java system startup timeout'));
        }
      }, 60000); // 60 second timeout
    });
  }

  async buildJavaSystem() {
    return new Promise((resolve, reject) => {
      const fabricModDir = path.join(__dirname, '../../AppyProx-FabricMod');
      
      this.logger.info('Building Java Proxy Client Management System...');
      
      const buildProcess = spawn('./gradlew', ['build'], {
        cwd: fabricModDir,
        stdio: ['pipe', 'pipe', 'pipe']
      });
      
      buildProcess.stdout.on('data', (data) => {
        this.logger.debug(`Build: ${data.toString().trim()}`);
      });
      
      buildProcess.stderr.on('data', (data) => {
        this.logger.warn(`Build Error: ${data.toString().trim()}`);
      });
      
      buildProcess.on('exit', (code) => {
        if (code === 0) {
          this.logger.info('Java system built successfully');
          resolve();
        } else {
          reject(new Error(`Build failed with code ${code}`));
        }
      });
    });
  }

  async waitForJavaConnection() {
    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        reject(new Error('Timeout waiting for Java connection'));
      }, 30000);

      this.once('java_connected', () => {
        clearTimeout(timeout);
        resolve();
      });
    });
  }

  handleJavaMessage(message) {
    try {
      const lines = message.trim().split('\n');
      
      for (const line of lines) {
        if (!line.trim()) continue;
        
        const data = JSON.parse(line);
        this.processJavaMessage(data);
      }
    } catch (error) {
      this.logger.error('Error parsing Java message:', error);
    }
  }

  processJavaMessage(data) {
    const { type, id, payload, error } = data;
    
    switch (type) {
      case 'COMMAND_RESPONSE':
        this.handleCommandResponse(id, payload, error);
        break;
        
      case 'CLIENT_EVENT':
        this.handleClientEvent(payload);
        break;
        
      case 'CLIENT_STATUS_UPDATE':
        this.handleClientStatusUpdate(payload);
        break;
        
      case 'CLIENT_METRICS_UPDATE':
        this.handleClientMetricsUpdate(payload);
        break;
        
      case 'SYSTEM_STATUS':
        this.handleSystemStatus(payload);
        break;
        
      default:
        this.logger.warn(`Unknown message type from Java: ${type}`);
    }
  }

  handleCommandResponse(commandId, payload, error) {
    const pendingCommand = this.pendingCommands.get(commandId);
    if (pendingCommand) {
      this.pendingCommands.delete(commandId);
      
      if (error) {
        pendingCommand.reject(new Error(error));
      } else {
        pendingCommand.resolve(payload);
      }
    }
  }

  handleClientEvent(payload) {
    const { eventType, accountId, message, data } = payload;
    
    this.logger.debug(`Client event: ${eventType} for ${accountId}: ${message}`);
    
    // Forward to appropriate AppyProx components
    switch (eventType) {
      case 'CLIENT_REGISTERED':
        this.connectedClients.set(accountId, data);
        if (this.clusterManagerInterface) {
          this.clusterManagerInterface.registerProxyClient(accountId, data);
        }
        this.emit('client_connected', { id: accountId, ...data });
        break;
        
      case 'CLIENT_UNREGISTERED':
        this.connectedClients.delete(accountId);
        if (this.clusterManagerInterface) {
          this.clusterManagerInterface.removeProxyClient(accountId);
        }
        this.emit('client_disconnected', { id: accountId });
        break;
        
      case 'AUTOMATION_COMPLETED':
      case 'AUTOMATION_FAILED':
        if (this.automationEngineInterface) {
          this.automationEngineInterface.handleTaskResult(accountId, data);
        }
        this.emit('task_progress', { accountId, ...data });
        break;
    }
  }

  handleClientStatusUpdate(payload) {
    const { accountId, status } = payload;
    
    this.clientStatuses.set(accountId, status);
    
    if (this.clusterManagerInterface) {
      this.clusterManagerInterface.updateProxyClientStatus(accountId, status);
    }
    
    this.emit('account_status_update', { clientId: accountId, ...status });
  }

  handleClientMetricsUpdate(payload) {
    const { accountId, metrics } = payload;
    
    this.clientMetrics.set(accountId, metrics);
    
    if (this.centralNodeInterface) {
      this.centralNodeInterface.updateClientMetrics(accountId, metrics);
    }
  }

  handleSystemStatus(payload) {
    this.emit('system_status', payload);
  }

  handleJavaDisconnection() {
    if (this.isRunning && this.restartAttempts < this.maxRestartAttempts) {
      this.logger.warn('Java system disconnected, attempting restart...');
      this.restartAttempts++;
      setTimeout(() => {
        this.startJavaSystem().catch(error => {
          this.logger.error('Failed to restart Java system:', error);
        });
      }, 3000);
    }
  }

  sendCommand(command, parameters) {
    return new Promise((resolve, reject) => {
      if (!this.bridgeSocket) {
        reject(new Error('Java system not connected'));
        return;
      }

      const commandId = ++this.commandIdCounter;
      
      this.pendingCommands.set(commandId, { resolve, reject });
      
      const message = JSON.stringify({
        type: 'COMMAND',
        id: commandId,
        command,
        parameters
      }) + '\n';
      
      this.bridgeSocket.write(message);
      
      // Timeout
      setTimeout(() => {
        if (this.pendingCommands.has(commandId)) {
          this.pendingCommands.delete(commandId);
          reject(new Error('Command timeout'));
        }
      }, 30000);
    });
  }

  // Integration methods for AppyProx components

  setClusterManagerInterface(clusterManager) {
    this.clusterManagerInterface = {
      registerProxyClient: (accountId, clientInfo) => {
        clusterManager.registerClient({ id: accountId, ...clientInfo });
      },
      updateProxyClientStatus: (accountId, status) => {
        // Update internal cluster manager state
        const client = clusterManager.clients.get(accountId);
        if (client) {
          client.status = status;
          client.lastSeen = Date.now();
        }
      },
      removeProxyClient: (accountId) => {
        clusterManager.unregisterClient({ id: accountId });
      }
    };
  }

  setAutomationEngineInterface(automationEngine) {
    this.automationEngineInterface = {
      handleTaskResult: (accountId, taskData) => {
        automationEngine.emit('task_progress', {
          taskId: taskData.taskId,
          progress: taskData.progress || 100,
          status: taskData.success ? 'completed' : 'failed',
          accountId
        });
      }
    };
  }

  setCentralNodeInterface(centralNode) {
    this.centralNodeInterface = {
      updateClientMetrics: (accountId, metrics) => {
        centralNode.updateAccountStatus({
          clientId: accountId,
          ...metrics
        });
      }
    };
  }

  // Public API for AppyProx components

  async startProxyClient(account, config = {}) {
    try {
      const result = await this.sendCommand('START_CLIENT', {
        account: account,
        config: {
          headless: true,
          autoRestart: true,
          ...config
        }
      });
      
      this.logger.info(`Started proxy client for ${account.username}`);
      return result;
    } catch (error) {
      this.logger.error(`Failed to start proxy client for ${account.username}:`, error);
      throw error;
    }
  }

  async stopProxyClient(accountId, graceful = true) {
    try {
      const result = await this.sendCommand('STOP_CLIENT', {
        accountId,
        graceful
      });
      
      this.logger.info(`Stopped proxy client: ${accountId}`);
      return result;
    } catch (error) {
      this.logger.error(`Failed to stop proxy client ${accountId}:`, error);
      throw error;
    }
  }

  async executeAutomationTask(accountId, task) {
    try {
      const result = await this.sendCommand('EXECUTE_TASK', {
        accountId,
        task: {
          type: task.type,
          parameters: task.parameters,
          priority: task.priority || 5
        }
      });
      
      this.logger.info(`Executed automation task for ${accountId}: ${task.type}`);
      return result;
    } catch (error) {
      this.logger.error(`Failed to execute automation task for ${accountId}:`, error);
      throw error;
    }
  }

  async executeClusterAutomation(clusterId, task) {
    try {
      const result = await this.sendCommand('EXECUTE_CLUSTER_TASK', {
        clusterId,
        task
      });
      
      this.logger.info(`Executed cluster automation for ${clusterId}: ${task.type}`);
      return result;
    } catch (error) {
      this.logger.error(`Failed to execute cluster automation for ${clusterId}:`, error);
      throw error;
    }
  }

  async getSystemStatus() {
    try {
      return await this.sendCommand('GET_SYSTEM_STATUS', {});
    } catch (error) {
      this.logger.error('Failed to get system status:', error);
      return {
        connected: false,
        error: error.message
      };
    }
  }

  async getDashboardData() {
    try {
      return await this.sendCommand('GET_DASHBOARD_DATA', {});
    } catch (error) {
      this.logger.error('Failed to get dashboard data:', error);
      return null;
    }
  }

  getConnectedClients() {
    return Array.from(this.connectedClients.values());
  }

  getClientStatus(accountId) {
    return this.clientStatuses.get(accountId) || null;
  }

  getClientMetrics(accountId) {
    return this.clientMetrics.get(accountId) || null;
  }

  isClientConnected(accountId) {
    return this.connectedClients.has(accountId);
  }

  getStatus() {
    return {
      running: this.isRunning,
      javaSystemRunning: this.javaProcess && !this.javaProcess.killed,
      bridgeConnected: !!this.bridgeSocket,
      connectedClients: this.connectedClients.size,
      restartAttempts: this.restartAttempts
    };
  }
}

module.exports = ProxyClientBridge;