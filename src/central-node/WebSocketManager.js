const WebSocket = require('ws');
const EventEmitter = require('events');

/**
 * WebSocket Manager for real-time updates to the Xaeros interface
 * Provides live data streaming without polling
 */
class WebSocketManager extends EventEmitter {
  constructor(port = 25578) {
    super();
    this.port = port;
    this.wss = null;
    this.clients = new Set();
    this.updateInterval = null;
    this.logger = require('../utils/Logger').createLogger('WebSocketManager');
  }

  /**
   * Start the WebSocket server
   */
  start() {
    try {
      this.wss = new WebSocket.Server({ 
        port: this.port,
        perMessageDeflate: false
      });

      this.wss.on('connection', (ws, req) => {
        const clientId = `client_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
        ws.clientId = clientId;
        this.clients.add(ws);
        
        this.logger.info(`WebSocket client connected: ${clientId} (${this.clients.size} total)`);

        // Send initial data
        this.sendToClient(ws, {
          type: 'welcome',
          clientId: clientId,
          timestamp: new Date().toISOString(),
          data: {
            server: 'AppyProx Central Node',
            version: '1.0.0'
          }
        });

        // Handle client messages
        ws.on('message', (message) => {
          try {
            const data = JSON.parse(message);
            this.handleClientMessage(ws, data);
          } catch (error) {
            this.logger.warn(`Invalid message from ${clientId}: ${error.message}`);
          }
        });

        // Handle client disconnect
        ws.on('close', () => {
          this.clients.delete(ws);
          this.logger.info(`WebSocket client disconnected: ${clientId} (${this.clients.size} total)`);
        });

        // Handle errors
        ws.on('error', (error) => {
          this.logger.error(`WebSocket error for ${clientId}: ${error.message}`);
          this.clients.delete(ws);
        });
      });

      // Start periodic updates
      this.startPeriodicUpdates();

      this.logger.info(`WebSocket server started on port ${this.port}`);
      this.emit('started', { port: this.port });

    } catch (error) {
      this.logger.error(`Failed to start WebSocket server: ${error.message}`);
      throw error;
    }
  }

  /**
   * Stop the WebSocket server
   */
  stop() {
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
      this.updateInterval = null;
    }

    if (this.wss) {
      this.wss.close();
      this.wss = null;
    }

    this.clients.clear();
    this.logger.info('WebSocket server stopped');
    this.emit('stopped');
  }

  /**
   * Handle messages from clients
   */
  handleClientMessage(ws, data) {
    const { type, payload } = data;

    switch (type) {
      case 'ping':
        this.sendToClient(ws, {
          type: 'pong',
          timestamp: new Date().toISOString()
        });
        break;

      case 'subscribe':
        // Handle subscription requests for specific data types
        ws.subscriptions = ws.subscriptions || new Set();
        if (payload && payload.channels) {
          payload.channels.forEach(channel => ws.subscriptions.add(channel));
          this.logger.debug(`Client ${ws.clientId} subscribed to: ${payload.channels.join(', ')}`);
        }
        break;

      case 'unsubscribe':
        if (ws.subscriptions && payload && payload.channels) {
          payload.channels.forEach(channel => ws.subscriptions.delete(channel));
          this.logger.debug(`Client ${ws.clientId} unsubscribed from: ${payload.channels.join(', ')}`);
        }
        break;

      case 'map_view_update':
        // Client is updating their map view (useful for synchronized viewing)
        if (payload) {
          this.broadcastToOthers(ws, {
            type: 'peer_map_update',
            clientId: ws.clientId,
            data: payload
          });
        }
        break;

      default:
        this.logger.warn(`Unknown message type from ${ws.clientId}: ${type}`);
    }
  }

  /**
   * Start periodic updates to all clients
   */
  startPeriodicUpdates() {
    this.updateInterval = setInterval(() => {
      this.broadcastSystemUpdate();
    }, 2000); // Update every 2 seconds for real-time feel
  }

  /**
   * Broadcast system updates to all connected clients
   */
  broadcastSystemUpdate() {
    if (this.clients.size === 0) return;

    // Emit event to get current data from components
    this.emit('requestUpdate', (updateData) => {
      const message = {
        type: 'system_update',
        timestamp: new Date().toISOString(),
        data: updateData
      };

      this.broadcast(message);
    });
  }

  /**
   * Send message to specific client
   */
  sendToClient(ws, message) {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(message));
    }
  }

  /**
   * Broadcast message to all clients
   */
  broadcast(message, filter = null) {
    const messageStr = JSON.stringify(message);
    
    this.clients.forEach(ws => {
      if (ws.readyState === WebSocket.OPEN) {
        // Apply filter if provided
        if (filter && !filter(ws)) return;
        
        // Check subscriptions if message has channels
        if (message.channels && ws.subscriptions) {
          const hasSubscription = message.channels.some(channel => 
            ws.subscriptions.has(channel)
          );
          if (!hasSubscription) return;
        }

        ws.send(messageStr);
      }
    });
  }

  /**
   * Broadcast to all clients except the sender
   */
  broadcastToOthers(senderWs, message) {
    this.broadcast(message, ws => ws !== senderWs);
  }

  /**
   * Send player update to all clients
   */
  broadcastPlayerUpdate(playerData) {
    this.broadcast({
      type: 'player_update',
      timestamp: new Date().toISOString(),
      channels: ['players'],
      data: playerData
    });
  }

  /**
   * Send task update to all clients
   */
  broadcastTaskUpdate(taskData) {
    this.broadcast({
      type: 'task_update',
      timestamp: new Date().toISOString(),
      channels: ['tasks'],
      data: taskData
    });
  }

  /**
   * Send log message to all clients
   */
  broadcastLogMessage(logData) {
    this.broadcast({
      type: 'log_message',
      timestamp: new Date().toISOString(),
      channels: ['logs'],
      data: logData
    });
  }

  /**
   * Send waypoint update to all clients
   */
  broadcastWaypointUpdate(waypointData) {
    this.broadcast({
      type: 'waypoint_update',
      timestamp: new Date().toISOString(),
      channels: ['waypoints'],
      data: waypointData
    });
  }

  /**
   * Get current status
   */
  getStatus() {
    return {
      running: this.wss !== null,
      port: this.port,
      clientCount: this.clients.size,
      clients: Array.from(this.clients).map(ws => ({
        id: ws.clientId,
        subscriptions: ws.subscriptions ? Array.from(ws.subscriptions) : []
      }))
    };
  }
}

module.exports = WebSocketManager;