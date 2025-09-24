/**
 * ClientManager - Manages multiple headless Minecraft clients
 * Handles client lifecycle, coordination, and communication
 */

const EventEmitter = require('events');
const HeadlessMinecraftClient = require('./HeadlessMinecraftClient');
const { v4: uuidv4 } = require('uuid');

class ClientManager extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.clients = new Map();
    this.config = {
      maxClients: options.maxClients || 50,
      defaultServer: options.defaultServer || { host: 'localhost', port: 25565 },
      clientDefaults: options.clientDefaults || {},
      ...options
    };
    
    this.logger = options.logger || console;
    this.isRunning = false;
    
    // Statistics
    this.stats = {
      totalConnected: 0,
      totalDisconnected: 0,
      totalErrors: 0,
      uptime: Date.now()
    };
  }
  
  /**
   * Start the client manager
   */
  async start() {
    if (this.isRunning) {
      this.logger.warn('ClientManager already running');
      return;
    }
    
    this.isRunning = true;
    this.stats.uptime = Date.now();
    
    this.logger.info('ClientManager started');
    this.emit('started');
  }
  
  /**
   * Stop the client manager and disconnect all clients
   */
  async stop() {
    if (!this.isRunning) {
      return;
    }
    
    this.logger.info('Stopping ClientManager...');
    this.isRunning = false;
    
    // Disconnect all clients
    const disconnectPromises = Array.from(this.clients.values()).map(client =>
      client.disconnect('Manager shutdown')
    );
    
    await Promise.allSettled(disconnectPromises);
    
    this.clients.clear();
    this.logger.info('ClientManager stopped');
    this.emit('stopped');
  }
  
  /**
   * Create a new client
   */
  async createClient(options = {}) {
    if (this.clients.size >= this.config.maxClients) {
      throw new Error(`Maximum client limit reached: ${this.config.maxClients}`);
    }
    
    const clientId = options.id || uuidv4();
    
    if (this.clients.has(clientId)) {
      throw new Error(`Client with ID ${clientId} already exists`);
    }
    
    const clientOptions = {
      ...this.config.clientDefaults,
      ...options,
      id: clientId,
      server: options.server || this.config.defaultServer,
      logger: this.logger
    };
    
    const client = new HeadlessMinecraftClient(clientOptions);
    
    // Setup event forwarding
    this.setupClientEventHandlers(client);
    
    this.clients.set(clientId, client);
    
    this.logger.info(`Created client: ${client.username} (${clientId})`);
    this.emit('clientCreated', client);
    
    return client;
  }
  
  /**
   * Remove and disconnect a client
   */
  async removeClient(clientId) {
    const client = this.clients.get(clientId);
    
    if (!client) {
      throw new Error(`Client ${clientId} not found`);
    }
    
    await client.disconnect('Removed from manager');
    client.destroy();
    
    this.clients.delete(clientId);
    
    this.logger.info(`Removed client: ${client.username} (${clientId})`);
    this.emit('clientRemoved', clientId);
  }
  
  /**
   * Connect a client to its server
   */
  async connectClient(clientId) {
    const client = this.clients.get(clientId);
    
    if (!client) {
      throw new Error(`Client ${clientId} not found`);
    }
    
    await client.connect();
    return client;
  }
  
  /**
   * Disconnect a client
   */
  async disconnectClient(clientId, reason = 'Manual disconnect') {
    const client = this.clients.get(clientId);
    
    if (!client) {
      throw new Error(`Client ${clientId} not found`);
    }
    
    await client.disconnect(reason);
    return client;
  }
  
  /**
   * Get a client by ID
   */
  getClient(clientId) {
    return this.clients.get(clientId);
  }
  
  /**
   * Get all clients
   */
  getAllClients() {
    return Array.from(this.clients.values());
  }
  
  /**
   * Get connected clients
   */
  getConnectedClients() {
    return Array.from(this.clients.values()).filter(client => client.isConnected);
  }
  
  /**
   * Get ready clients (connected and spawned)
   */
  getReadyClients() {
    return Array.from(this.clients.values()).filter(client => client.isReady);
  }
  
  /**
   * Broadcast a chat message to all ready clients
   */
  async broadcastChat(message) {
    const readyClients = this.getReadyClients();
    
    const promises = readyClients.map(client => 
      client.sendChat(message).catch(error => {
        this.logger.warn(`Failed to send chat to ${client.username}:`, error.message);
      })
    );
    
    await Promise.allSettled(promises);
    
    this.logger.debug(`Broadcasted chat to ${readyClients.length} clients: ${message}`);
  }
  
  /**
   * Execute a command on all ready clients
   */
  async broadcastCommand(command, timeout = 5000) {
    const readyClients = this.getReadyClients();
    
    const promises = readyClients.map(client => 
      client.executeCommand(command, timeout).catch(error => {
        this.logger.warn(`Failed to execute command on ${client.username}:`, error.message);
      })
    );
    
    await Promise.allSettled(promises);
    
    this.logger.debug(`Executed command on ${readyClients.length} clients: ${command}`);
  }
  
  /**
   * Execute a command on specific clients
   */
  async executeCommandOnClients(clientIds, command, timeout = 5000) {
    const promises = clientIds.map(async (clientId) => {
      const client = this.getClient(clientId);
      if (client && client.isReady) {
        try {
          await client.executeCommand(command, timeout);
        } catch (error) {
          this.logger.warn(`Failed to execute command on ${client.username}:`, error.message);
        }
      }
    });
    
    await Promise.allSettled(promises);
  }
  
  /**
   * Get manager statistics
   */
  getStats() {
    const clients = Array.from(this.clients.values());
    
    return {
      ...this.stats,
      totalClients: this.clients.size,
      connectedClients: clients.filter(c => c.isConnected).length,
      readyClients: clients.filter(c => c.isReady).length,
      uptimeMs: Date.now() - this.stats.uptime,
      clientStatuses: clients.map(c => c.getStatus())
    };
  }
  
  /**
   * Setup event handlers for a client
   */
  setupClientEventHandlers(client) {
    // Forward important events
    client.on('connected', () => {
      this.stats.totalConnected++;
      this.emit('clientConnected', client);
    });
    
    client.on('disconnected', (reason) => {
      this.stats.totalDisconnected++;
      this.emit('clientDisconnected', client, reason);
    });
    
    client.on('ready', () => {
      this.emit('clientReady', client);
    });
    
    client.on('error', (error) => {
      this.stats.totalErrors++;
      this.emit('clientError', client, error);
    });
    
    client.on('kicked', (reason) => {
      this.emit('clientKicked', client, reason);
    });
    
    client.on('chat', (chatEntry) => {
      this.emit('clientChat', client, chatEntry);
    });
    
    client.on('whisper', (whisperEntry) => {
      this.emit('clientWhisper', client, whisperEntry);
    });
    
    client.on('positionUpdate', (position) => {
      this.emit('clientPositionUpdate', client, position);
    });
    
    client.on('healthUpdate', (health) => {
      this.emit('clientHealthUpdate', client, health);
    });
  }
  
  /**
   * Create multiple clients at once
   */
  async createMultipleClients(count, baseOptions = {}) {
    const clients = [];
    
    for (let i = 0; i < count; i++) {
      const options = {
        ...baseOptions,
        username: baseOptions.username ? `${baseOptions.username}_${i + 1}` : undefined
      };
      
      try {
        const client = await this.createClient(options);
        clients.push(client);
      } catch (error) {
        this.logger.error(`Failed to create client ${i + 1}:`, error.message);
        break;
      }
    }
    
    return clients;
  }
  
  /**
   * Connect all clients
   */
  async connectAllClients() {
    const clients = Array.from(this.clients.values());
    const promises = clients.map(client => 
      client.connect().catch(error => {
        this.logger.warn(`Failed to connect ${client.username}:`, error.message);
      })
    );
    
    await Promise.allSettled(promises);
    
    const connected = this.getConnectedClients().length;
    this.logger.info(`Connected ${connected}/${clients.length} clients`);
  }
  
  /**
   * Disconnect all clients
   */
  async disconnectAllClients(reason = 'Manager disconnect') {
    const clients = Array.from(this.clients.values());
    const promises = clients.map(client => 
      client.disconnect(reason).catch(error => {
        this.logger.warn(`Failed to disconnect ${client.username}:`, error.message);
      })
    );
    
    await Promise.allSettled(promises);
    
    this.logger.info(`Disconnected ${clients.length} clients`);
  }
  
  /**
   * Find clients by criteria
   */
  findClients(criteria) {
    const clients = Array.from(this.clients.values());
    
    return clients.filter(client => {
      for (const [key, value] of Object.entries(criteria)) {
        if (client[key] !== value) {
          return false;
        }
      }
      return true;
    });
  }
  
  /**
   * Clean up and destroy all clients
   */
  destroy() {
    this.logger.info('Destroying ClientManager');
    
    for (const client of this.clients.values()) {
      client.destroy();
    }
    
    this.clients.clear();
    this.removeAllListeners();
    this.isRunning = false;
  }
}

module.exports = ClientManager;
