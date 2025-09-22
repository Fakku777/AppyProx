const EventEmitter = require('events');
const minecraft = require('minecraft-protocol');
const { v4: uuidv4 } = require('uuid');
const ViaVersionManager = require('./ViaVersionManager');

/**
 * Core proxy server that handles Minecraft client connections
 * and forwards packets between clients and target servers
 */
class ProxyServer extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger;
    
    this.server = null;
    this.clients = new Map(); // Map of client connections
    this.viaVersionManager = null;
    this.isRunning = false;
    
    // Connection statistics
    this.stats = {
      totalConnections: 0,
      activeConnections: 0,
      packetsForwarded: 0,
      bytesTransferred: 0,
      startTime: null
    };
  }

  async start() {
    return new Promise(async (resolve, reject) => {
      try {
        // Initialize ViaVersion manager if enabled
        if (this.config.viaversion?.enabled) {
          this.viaVersionManager = new ViaVersionManager(this.config, this.logger);
          await this.viaVersionManager.initialize();
          this.logger.info('ViaVersion manager initialized');
        }
        this.server = minecraft.createServer({
          'online-mode': this.config.online_mode,
          encryption: this.config.encryption,
          host: this.config.host,
          port: this.config.port,
          version: this.config.version,
          maxPlayers: 1000, // High limit for proxy
          motd: '§6AppyProx §7- §bAdvanced Minecraft Proxy\\n§7Clustering • Automation • Management'
        });

        this.setupServerEvents();
        
        this.server.on('listening', () => {
          this.isRunning = true;
          this.stats.startTime = Date.now();
          this.logger.info(`Proxy server listening on ${this.config.host}:${this.config.port}`);
          resolve();
        });

        this.server.on('error', (error) => {
          this.logger.error('Proxy server error:', error);
          reject(error);
        });

      } catch (error) {
        reject(error);
      }
    });
  }

  async stop() {
    if (!this.isRunning) return;

    return new Promise((resolve) => {
      // Disconnect all clients gracefully
      for (const [clientId, client] of this.clients) {
        this.disconnectClient(clientId, 'Server shutting down');
      }

      this.server.close(() => {
        this.isRunning = false;
        this.logger.info('Proxy server stopped');
        resolve();
      });
    });
  }

  setupServerEvents() {
    this.server.on('login', (client) => {
      const clientId = uuidv4();
      const clientInfo = {
        id: clientId,
        client: client,
        username: client.username,
        uuid: client.uuid,
        remoteAddress: client.socket.remoteAddress,
        connectedAt: Date.now(),
        targetServer: null,
        isConnected: true,
        stats: {
          packetsReceived: 0,
          packetsSent: 0,
          bytesReceived: 0,
          bytesSent: 0
        }
      };

      this.clients.set(clientId, clientInfo);
      this.stats.totalConnections++;
      this.stats.activeConnections++;

      this.logger.info(`Client connected: ${client.username} (${clientId}) from ${client.socket.remoteAddress}`);
      
      // Register with ViaVersion manager if available
      if (this.viaVersionManager) {
        this.viaVersionManager.registerClient(clientInfo);
      }
      
      this.setupClientEvents(clientId, clientInfo);
      this.emit('client_connected', clientInfo);
    });
  }

  setupClientEvents(clientId, clientInfo) {
    const client = clientInfo.client;

    // Handle client disconnection
    client.on('end', () => {
      this.handleClientDisconnect(clientId, 'Client disconnected');
    });

    client.on('error', (error) => {
      this.logger.warn(`Client error for ${clientInfo.username}:`, error.message);
      this.handleClientDisconnect(clientId, 'Client error');
    });

    // Handle incoming packets
    client.on('packet', (data, meta) => {
      this.handleClientPacket(clientId, data, meta);
    });

    // Send initial configuration
    this.sendInitialData(clientInfo);
  }

  sendInitialData(clientInfo) {
    const client = clientInfo.client;
    
    // Send join game packet
    try {
      client.write('login', {
        entityId: 1,
        isHardcore: false,
        gameMode: 0,
        previousGameMode: 0,
        worldNames: ['minecraft:overworld'],
        dimensionCodec: {
          type: 'compound',
          name: '',
          value: {
            'minecraft:dimension_type': {
              type: 'compound',
              value: {
                type: { type: 'string', value: 'minecraft:dimension_type' },
                value: []
              }
            },
            'minecraft:worldgen/biome': {
              type: 'compound', 
              value: {
                type: { type: 'string', value: 'minecraft:worldgen/biome' },
                value: []
              }
            }
          }
        },
        dimension: 'minecraft:overworld',
        worldName: 'minecraft:overworld',
        hashedSeed: [0, 0],
        maxPlayers: 1000,
        viewDistance: 10,
        simulationDistance: 10,
        reducedDebugInfo: false,
        enableRespawnScreen: true,
        isDebug: false,
        isFlat: false
      });

      // Send player position
      client.write('position', {
        x: 0,
        y: 64,
        z: 0,
        yaw: 0,
        pitch: 0,
        flags: 0x00,
        teleportId: 1
      });

      this.logger.debug(`Sent initial data to ${clientInfo.username}`);
    } catch (error) {
      this.logger.error(`Failed to send initial data to ${clientInfo.username}:`, error);
    }
  }

  handleClientPacket(clientId, data, meta) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    clientInfo.stats.packetsReceived++;
    clientInfo.stats.bytesReceived += JSON.stringify(data).length;

    // Forward packet to target server if connected
    if (clientInfo.targetServer) {
      this.forwardPacketToServer(clientId, data, meta);
    }

    // Handle specific packet types
    switch (meta.name) {
      case 'chat_message':
      case 'chat_command':
        this.handleChatPacket(clientId, data, meta);
        break;
      case 'position':
      case 'position_look':
        this.handleMovementPacket(clientId, data, meta);
        break;
      case 'block_dig':
      case 'block_place':
        this.handleBlockInteraction(clientId, data, meta);
        break;
    }

    this.stats.packetsForwarded++;
  }

  handleChatPacket(clientId, data, meta) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    const message = data.message || data.command;
    
    // Check if it's a proxy command
    if (message.startsWith('/appyprox') || message.startsWith('/ap')) {
      this.handleProxyCommand(clientId, message);
      return;
    }

    this.logger.debug(`Chat from ${clientInfo.username}: ${message}`);
    this.emit('client_chat', { clientId, clientInfo, message, meta });
  }

  handleMovementPacket(clientId, data, meta) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    // Update client position
    if (data.x !== undefined) clientInfo.position = { x: data.x, y: data.y, z: data.z };
    if (data.yaw !== undefined) clientInfo.rotation = { yaw: data.yaw, pitch: data.pitch };

    this.emit('client_movement', { clientId, clientInfo, position: data, meta });
  }

  handleBlockInteraction(clientId, data, meta) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    this.emit('client_block_interaction', { clientId, clientInfo, blockData: data, meta });
  }

  handleProxyCommand(clientId, command) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    const args = command.split(' ').slice(1);
    const subCommand = args[0];

    let response = '';

    switch (subCommand) {
      case 'status':
        response = this.getStatusMessage();
        break;
      case 'connect':
        const serverAddress = args[1];
        if (serverAddress) {
          this.connectToServer(clientId, serverAddress);
          response = `§aConnecting to ${serverAddress}...`;
        } else {
          response = '§cUsage: /ap connect <server:port>';
        }
        break;
      case 'disconnect':
        this.disconnectFromServer(clientId);
        response = '§aDisconnected from target server';
        break;
      case 'help':
        response = this.getHelpMessage();
        break;
      default:
        response = '§cUnknown command. Use /ap help for available commands.';
    }

    this.sendMessageToClient(clientId, response);
  }

  async connectToServer(clientId, serverAddress) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    try {
      const [host, port] = serverAddress.split(':');
      const targetPort = port ? parseInt(port) : 25565;

      // Create connection to target server
      const serverConnection = minecraft.createClient({
        host: host,
        port: targetPort,
        username: clientInfo.username,
        version: this.config.version
      });

      serverConnection.on('login', () => {
        clientInfo.targetServer = serverConnection;
        this.logger.info(`${clientInfo.username} connected to ${serverAddress}`);
        this.sendMessageToClient(clientId, `§aConnected to ${serverAddress}`);
        
        // Setup packet forwarding
        this.setupServerForwarding(clientId, serverConnection);
      });

      serverConnection.on('error', (error) => {
        this.logger.error(`Connection failed for ${clientInfo.username} to ${serverAddress}:`, error);
        this.sendMessageToClient(clientId, `§cFailed to connect to ${serverAddress}: ${error.message}`);
      });

    } catch (error) {
      this.logger.error(`Invalid server address for ${clientInfo.username}:`, error);
      this.sendMessageToClient(clientId, '§cInvalid server address format');
    }
  }

  setupServerForwarding(clientId, serverConnection) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    // Forward packets from server to client
    serverConnection.on('packet', (data, meta) => {
      try {
        clientInfo.client.write(meta.name, data);
        clientInfo.stats.packetsSent++;
        clientInfo.stats.bytesSent += JSON.stringify(data).length;
      } catch (error) {
        this.logger.warn(`Failed to forward packet ${meta.name} to ${clientInfo.username}:`, error);
      }
    });

    serverConnection.on('end', () => {
      this.logger.info(`Server connection ended for ${clientInfo.username}`);
      clientInfo.targetServer = null;
      this.sendMessageToClient(clientId, '§eServer connection ended');
    });
  }

  forwardPacketToServer(clientId, data, meta) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo || !clientInfo.targetServer) return;

    try {
      clientInfo.targetServer.write(meta.name, data);
    } catch (error) {
      this.logger.warn(`Failed to forward packet ${meta.name} from ${clientInfo.username}:`, error);
    }
  }

  disconnectFromServer(clientId) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo || !clientInfo.targetServer) return;

    clientInfo.targetServer.end();
    clientInfo.targetServer = null;
  }

  sendMessageToClient(clientId, message) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    try {
      clientInfo.client.write('system_chat', {
        content: JSON.stringify({ text: message }),
        overlay: false
      });
    } catch (error) {
      this.logger.warn(`Failed to send message to ${clientInfo.username}:`, error);
    }
  }

  handleClientDisconnect(clientId, reason) {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    this.logger.info(`Client disconnected: ${clientInfo.username} (${clientId}) - ${reason}`);
    
    // Disconnect from target server if connected
    if (clientInfo.targetServer) {
      clientInfo.targetServer.end();
    }

    this.clients.delete(clientId);
    this.stats.activeConnections--;
    
    this.emit('client_disconnected', clientInfo);
  }

  disconnectClient(clientId, reason = 'Disconnected by proxy') {
    const clientInfo = this.clients.get(clientId);
    if (!clientInfo) return;

    try {
      clientInfo.client.end(reason);
    } catch (error) {
      this.logger.warn(`Error disconnecting client ${clientInfo.username}:`, error);
    }
  }

  getStatusMessage() {
    const uptime = this.stats.startTime ? Math.floor((Date.now() - this.stats.startTime) / 1000) : 0;
    const uptimeStr = this.formatUptime(uptime);

    return [
      '§6§l=== AppyProx Status ===',
      `§7Active Connections: §a${this.stats.activeConnections}`,
      `§7Total Connections: §a${this.stats.totalConnections}`,
      `§7Packets Forwarded: §a${this.stats.packetsForwarded.toLocaleString()}`,
      `§7Uptime: §a${uptimeStr}`,
      '§6§l======================'
    ].join('\n');
  }

  getHelpMessage() {
    return [
      '§6§l=== AppyProx Commands ===',
      '§e/ap status §7- Show proxy status',
      '§e/ap connect <server:port> §7- Connect to server',
      '§e/ap disconnect §7- Disconnect from server', 
      '§e/ap help §7- Show this help message',
      '§6§l========================='
    ].join('\n');
  }

  formatUptime(seconds) {
    const days = Math.floor(seconds / 86400);
    const hours = Math.floor((seconds % 86400) / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (days > 0) return `${days}d ${hours}h ${minutes}m`;
    if (hours > 0) return `${hours}h ${minutes}m`;
    if (minutes > 0) return `${minutes}m ${secs}s`;
    return `${secs}s`;
  }

  getStatus() {
    return {
      isRunning: this.isRunning,
      activeConnections: this.stats.activeConnections,
      totalConnections: this.stats.totalConnections,
      packetsForwarded: this.stats.packetsForwarded,
      uptime: this.stats.startTime ? Date.now() - this.stats.startTime : 0,
      clients: Array.from(this.clients.values()).map(client => ({
        id: client.id,
        username: client.username,
        remoteAddress: client.remoteAddress,
        connectedAt: client.connectedAt,
        hasTargetServer: !!client.targetServer,
        stats: client.stats
      }))
    };
  }

  // Get client by ID
  getClient(clientId) {
    return this.clients.get(clientId);
  }

  // Get all clients
  getClients() {
    return Array.from(this.clients.values());
  }

  // Broadcast message to all clients
  broadcast(message) {
    for (const clientInfo of this.clients.values()) {
      this.sendMessageToClient(clientInfo.id, message);
    }
  }
}

module.exports = ProxyServer;