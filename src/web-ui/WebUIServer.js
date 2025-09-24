const express = require('express');
const WebSocket = require('ws');
const http = require('http');
const path = require('path');
const TextureManager = require('./minecraft/TextureManager');
const TPSManager = require('./minecraft/TPSManager');
const ChunkRenderer = require('./minecraft/ChunkRenderer');
const MinecraftUIGenerator = require('./minecraft/AuthenticMinecraftUIV2');
const PlayerVisualization = require('./minecraft/PlayerVisualization');
const CommandTerminal = require('./minecraft/CommandTerminal');
const fs = require('fs').promises;
const EventEmitter = require('events');

/**
 * Xaeros-Style Web UI Server for AppyProx
 * Provides real-time visualization of proxy activities, cluster management, and system monitoring
 */
class WebUIServer extends EventEmitter {
  constructor(config, logger, components = {}) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('WebUI') : logger;
    this.components = components;
    
    // Server configuration
    this.port = config.webUI?.port || 25577;
    this.host = config.webUI?.host || 'localhost';
    
    // Express app and HTTP server
    this.app = express();
    this.server = null;
    this.wss = null;
    
    // Connected WebSocket clients
    this.wsClients = new Set();
    
    // UI State
    this.uiState = {
      mapData: {
        zoom: 1,
        centerX: 0,
        centerZ: 0,
        players: new Map(),
        waypoints: new Map(),
        structures: new Map(),
        chunks: new Map()
      },
      systemStats: {
        uptime: Date.now(),
        connectedAccounts: 0,
        activeTasks: 0,
        totalConnections: 0,
        activeConnections: 0,
        errorRate: 0,
        healthStatus: 'unknown'
      },
      tasks: new Map(),
      clusters: new Map(),
      logs: []
    };
    
    this.isRunning = false;
    this.updateInterval = null;
    
    // Initialize Minecraft UI generator immediately
    this.minecraftUI = new MinecraftUIGenerator();
    
    // Initialize player visualization (will be properly set up after texture manager is ready)
    this.playerVisualization = null;
    
    // Initialize groups visualization if available
    this.groupsIntegration = components.groupsIntegration || null;
    this.groupsVisualization = null;
    
    // Initialize command terminal
    this.commandTerminal = null;
  }

  async initialize() {
    if (this.isRunning) return;
    
    this.logger.info('Initializing WebUI server...');
    
    try {
      // Setup Express middleware
      this.setupMiddleware();
      
      // Setup routes
      this.setupRoutes();
      
      // Create HTTP server
      this.server = http.createServer(this.app);
      
      // Setup WebSocket server
      this.setupWebSocket();
      
      // Setup component event listeners
      this.setupEventListeners();
      
    // Initialize Minecraft systems
    await this.initializeMinecraftSystems();
      
      // Start the server
      await this.start();
      
      this.logger.info(`WebUI server initialized on http://${this.host}:${this.port}`);
      
    } catch (error) {
      this.logger.error('Failed to initialize WebUI server:', error);
      throw error;
    }
  }

  setupMiddleware() {
    // Serve static files
    this.app.use('/static', express.static(path.join(__dirname, 'public')));
    
    // Parse JSON bodies
    this.app.use(express.json());
    
    // CORS for development
    this.app.use((req, res, next) => {
      res.header('Access-Control-Allow-Origin', '*');
      res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
      res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
      next();
    });
    
    // Request logging
    this.app.use((req, res, next) => {
      this.logger.debug(`${req.method} ${req.path} - ${req.ip}`);
      next();
    });
  }

  setupRoutes() {
    // Main page
    this.app.get('/', (req, res) => {
      this.serveMainPage(req, res);
    });
    
    // CSS styles
    this.app.get('/style.css', (req, res) => {
      this.serveCSS(req, res);
    });
    
    // JavaScript client
    this.app.get('/script.js', (req, res) => {
      this.serveJavaScript(req, res);
    });
    
    // API Routes
    this.app.get('/api/status', (req, res) => {
      res.json({
        isRunning: true,
        uptime: Date.now() - this.uiState.systemStats.uptime,
        ...this.uiState.systemStats
      });
    });
    
    this.app.get('/api/players', (req, res) => {
      const players = Array.from(this.uiState.mapData.players.values());
      res.json(players);
    });
    
    this.app.get('/api/clusters', (req, res) => {
      const clusters = Array.from(this.uiState.clusters.values());
      res.json(clusters);
    });
    
    this.app.get('/api/tasks', (req, res) => {
      const tasks = Array.from(this.uiState.tasks.values());
      res.json(tasks);
    });
    
    this.app.get('/api/map-data', (req, res) => {
      res.json({
        zoom: this.uiState.mapData.zoom,
        centerX: this.uiState.mapData.centerX,
        centerZ: this.uiState.mapData.centerZ,
        players: Array.from(this.uiState.mapData.players.values()),
        waypoints: Array.from(this.uiState.mapData.waypoints.values()),
        structures: Array.from(this.uiState.mapData.structures.values())
      });
    });
    
    this.app.get('/api/health', (req, res) => {
      if (this.components.healthMonitor) {
        const health = this.components.healthMonitor.getHealthSummary();
        res.json(health);
      } else {
        res.json({ status: 'unknown', message: 'Health monitoring not available' });
      }
    });
    
    this.app.get('/api/errors', (req, res) => {
      if (this.components.errorRecovery) {
        const stats = this.components.errorRecovery.getErrorStatistics();
        res.json(stats);
      } else {
        res.json({ message: 'Error recovery not available' });
      }
    });
    
    this.app.get('/api/circuit-breakers', (req, res) => {
      if (this.components.circuitBreaker) {
        const breakers = this.components.circuitBreaker.getAllBreakers();
        res.json(breakers);
      } else {
        res.json([]);
      }
    });
    
    // TPS and performance endpoints
    this.app.get('/api/tps', (req, res) => {
      if (this.tpsManager) {
        const metrics = this.tpsManager.getPerformanceMetrics();
        res.json(metrics);
      } else {
        res.json({ message: 'TPS monitoring not available' });
      }
    });
    
    this.app.post('/api/tps/set-rate', (req, res) => {
      if (this.tpsManager) {
        const { rate } = req.body;
        this.tpsManager.setUpdateRate(rate);
        res.json({ success: true, newRate: this.tpsManager.currentUpdateRate });
      } else {
        res.status(400).json({ error: 'TPS manager not available' });
      }
    });
    
    this.app.post('/api/tps/force-tps', (req, res) => {
      if (this.tpsManager) {
        const { tps } = req.body;
        this.tpsManager.forceTPS(tps);
        res.json({ success: true, targetTPS: this.tpsManager.targetTPS });
      } else {
        res.status(400).json({ error: 'TPS manager not available' });
      }
    });
    
    // Chunk and rendering endpoints
    this.app.get('/api/chunks', (req, res) => {
      if (this.chunkRenderer) {
        const stats = this.chunkRenderer.getStats();
        res.json(stats);
      } else {
        res.json({ message: 'Chunk renderer not available' });
      }
    });
    
    this.app.post('/api/chunks/set-layer', (req, res) => {
      if (this.chunkRenderer) {
        const { layer } = req.body;
        this.chunkRenderer.setRenderLayer(layer);
        res.json({ success: true, currentLayer: this.chunkRenderer.currentLayer });
      } else {
        res.status(400).json({ error: 'Chunk renderer not available' });
      }
    });
    
    this.app.post('/api/chunks/set-mode', (req, res) => {
      if (this.chunkRenderer) {
        const { mode } = req.body;
        this.chunkRenderer.setLayerMode(mode);
        res.json({ success: true, layerMode: this.chunkRenderer.layerMode });
      } else {
        res.status(400).json({ error: 'Chunk renderer not available' });
      }
    });
    
    // Control endpoints
    this.app.post('/api/backup', async (req, res) => {
      try {
        if (this.components.errorRecovery) {
          const { label } = req.body;
          const backupId = await this.components.errorRecovery.createBackup(label || 'Manual-WebUI-Backup');
          res.json({ success: true, backupId });
        } else {
          res.status(503).json({ error: 'Error recovery not available' });
        }
      } catch (error) {
        res.status(500).json({ error: error.message });
      }
    });
    
    this.app.post('/api/rollback', async (req, res) => {
      try {
        if (this.components.errorRecovery) {
          const { label } = req.body;
          await this.components.errorRecovery.rollback(label);
          res.json({ success: true });
        } else {
          res.status(503).json({ error: 'Error recovery not available' });
        }
      } catch (error) {
        res.status(500).json({ error: error.message });
      }
    });
    
    // Task control
    this.app.post('/api/tasks/:taskId/cancel', (req, res) => {
      const { taskId } = req.params;
      if (this.components.automationEngine) {
        // Implement task cancellation
        res.json({ success: true, message: `Task ${taskId} cancellation requested` });
      } else {
        res.status(503).json({ error: 'Automation engine not available' });
      }
    });
    
    this.app.post('/api/tasks', (req, res) => {
      const { type, parameters, clusterId } = req.body;
      if (this.components.automationEngine) {
        // Implement task creation
        const taskId = `task-${Date.now()}`;
        res.json({ success: true, taskId });
      } else {
        res.status(503).json({ error: 'Automation engine not available' });
      }
    });
    
    // Groups API endpoints (legacy support - main functionality is in GroupsIntegration)
    this.app.get('/api/groups', (req, res) => {
      if (this.components.groupsIntegration) {
        try {
          const groups = this.components.groupsIntegration.getAllGroups();
          res.json({ success: true, groups });
        } catch (error) {
          res.status(500).json({ success: false, error: error.message });
        }
      } else {
        res.status(503).json({ success: false, error: 'Groups system not available' });
      }
    });
    
    this.app.get('/api/groups-status', (req, res) => {
      if (this.components.groupsIntegration) {
        try {
          const groupManager = this.components.groupsIntegration.getGroupManager();
          res.json({
            success: true,
            status: {
              isRunning: groupManager.isRunning,
              totalGroups: this.components.groupsIntegration.getAllGroups().length,
              totalClients: groupManager.clients.size,
              activeTasks: Array.from(groupManager.groupTasks.values()).flat().length
            }
          });
        } catch (error) {
          res.status(500).json({ success: false, error: error.message });
        }
      } else {
        res.status(503).json({ success: false, error: 'Groups system not available' });
      }
    });
  }

  setupWebSocket() {
    this.wss = new WebSocket.Server({ server: this.server });
    this.wsServer = this.wss; // Alias for GroupsIntegration compatibility
    
    this.wss.on('connection', (ws, req) => {
      this.logger.info(`WebSocket client connected from ${req.connection.remoteAddress}`);
      this.wsClients.add(ws);
      
      // Send initial state
      ws.send(JSON.stringify({
        type: 'initial_state',
        data: {
          mapData: this.uiState.mapData,
          systemStats: this.uiState.systemStats,
          tasks: Array.from(this.uiState.tasks.values()),
          clusters: Array.from(this.uiState.clusters.values())
        }
      }));
      
      ws.on('message', (message) => {
        try {
          const data = JSON.parse(message);
          this.handleWebSocketMessage(ws, data);
        } catch (error) {
          this.logger.warn('Invalid WebSocket message:', error.message);
        }
      });
      
      ws.on('close', () => {
        this.wsClients.delete(ws);
        this.logger.debug('WebSocket client disconnected');
      });
      
      ws.on('error', (error) => {
        this.logger.warn('WebSocket error:', error);
        this.wsClients.delete(ws);
      });
    });
  }

  handleWebSocketMessage(ws, data) {
    switch (data.type) {
      case 'map_center':
        this.uiState.mapData.centerX = data.x;
        this.uiState.mapData.centerZ = data.z;
        this.broadcastToClients({
          type: 'map_update',
          data: { centerX: data.x, centerZ: data.z }
        });
        break;
        
      case 'map_zoom':
        this.uiState.mapData.zoom = data.zoom;
        this.broadcastToClients({
          type: 'map_update',
          data: { zoom: data.zoom }
        });
        break;
        
      case 'request_player_info':
        // Send detailed player information
        const player = this.uiState.mapData.players.get(data.playerId);
        if (player) {
          ws.send(JSON.stringify({
            type: 'player_info',
            data: player
          }));
        }
        break;
        
      case 'set_waypoint':
        this.addWaypoint(data.name, data.x, data.z, data.dimension);
        break;
        
      case 'terminal_command':
        this.handleTerminalCommand(ws, data);
        break;
        
      default:
        this.logger.debug(`Unknown WebSocket message type: ${data.type}`);
    }
  }
  
  handleTerminalCommand(ws, data) {
    const command = data.command || '';
    const args = command.toLowerCase().split(' ');
    const cmd = args[0];
    
    let output = '';
    let success = true;
    
    try {
      switch (cmd) {
        case 'help':
          output = this.getHelpText();
          break;
          
        case 'status':
          output = this.getSystemStatus();
          break;
          
        case 'groups':
          output = this.getGroupsList();
          break;
          
        case 'clients':
          output = this.getClientsList();
          break;
          
        case 'tasks':
          output = this.getTasksList();
          break;
          
        case 'clear':
          output = 'Terminal cleared';
          break;
          
        case 'version':
          output = 'AppyProx Terminal v2.0\nMinecraft Proxy System';
          break;
          
        case 'uptime':
          const uptimeMs = Date.now() - this.uiState.systemStats.uptime;
          const uptimeHours = Math.floor(uptimeMs / (1000 * 60 * 60));
          const uptimeMinutes = Math.floor((uptimeMs % (1000 * 60 * 60)) / (1000 * 60));
          output = `System uptime: ${uptimeHours}h ${uptimeMinutes}m`;
          break;
          
        default:
          if (cmd) {
            output = `Unknown command: ${cmd}\nType 'help' for available commands`;
            success = false;
          } else {
            output = 'Enter a command';
            success = false;
          }
      }
    } catch (error) {
      output = `Command error: ${error.message}`;
      success = false;
      this.logger.error('Terminal command error:', error);
    }
    
    // Send response back to client
    ws.send(JSON.stringify({
      type: 'terminal_response',
      command: command,
      output: output,
      success: success,
      timestamp: Date.now()
    }));
  }
  
  getHelpText() {
    return `Available Commands:
  help     - Show this help message
  status   - Show system status
  groups   - List all groups
  clients  - List connected clients
  tasks    - List active tasks
  clear    - Clear terminal output
  version  - Show version information
  uptime   - Show system uptime`;
  }
  
  getSystemStatus() {
    return `System Status:
  Groups: ${this.uiState.clusters.size}
  Clients: ${this.wsClients.size} WebSocket connections
  Tasks: ${this.uiState.tasks.size}
  Health: ${this.uiState.systemStats.healthStatus || 'unknown'}
  TPS: ${this.uiState.systemStats.currentTPS || 'N/A'}`;
  }
  
  getGroupsList() {
    if (this.uiState.clusters.size === 0) {
      return 'No groups found';
    }
    
    let output = 'Groups:\n';
    for (const [id, cluster] of this.uiState.clusters) {
      output += `  ${cluster.name} (${cluster.members?.length || 0} members) - ${cluster.status}\n`;
    }
    return output.trim();
  }
  
  getClientsList() {
    if (this.wsClients.size === 0) {
      return 'No clients connected';
    }
    
    return `Connected Clients: ${this.wsClients.size} WebSocket connections`;
  }
  
  getTasksList() {
    if (this.uiState.tasks.size === 0) {
      return 'No active tasks';
    }
    
    let output = 'Active Tasks:\n';
    for (const [id, task] of this.uiState.tasks) {
      output += `  ${task.type} - ${task.status} (${task.progress || 0}%)\n`;
    }
    return output.trim();
  }

  setupEventListeners() {
    // Listen to component events for real-time updates
    if (this.components.proxyServer) {
      this.components.proxyServer.on('client_connected', (client) => {
        this.updatePlayerPosition(client.username, client.position?.x || 0, client.position?.z || 0, 'overworld');
        this.updateSystemStats();
        this.broadcastUpdate('player_joined', { player: client.username });
      });
      
      this.components.proxyServer.on('client_disconnected', (client) => {
        this.removePlayer(client.username);
        this.updateSystemStats();
        this.broadcastUpdate('player_left', { player: client.username });
      });
      
      this.components.proxyServer.on('player_move', (data) => {
        this.updatePlayerPosition(data.username, data.x, data.z, data.dimension);
      });
    }
    
    if (this.components.automationEngine) {
      this.components.automationEngine.on('task_started', (task) => {
        this.uiState.tasks.set(task.id, {
          id: task.id,
          type: task.type,
          status: 'running',
          progress: 0,
          startTime: Date.now(),
          clusterId: task.clusterId,
          parameters: task.parameters
        });
        this.broadcastUpdate('task_started', { task: task.id });
      });
      
      this.components.automationEngine.on('task_progress', (progress) => {
        const task = this.uiState.tasks.get(progress.taskId);
        if (task) {
          task.progress = progress.percentage;
          task.currentAction = progress.action;
          this.broadcastUpdate('task_progress', progress);
        }
      });
      
      this.components.automationEngine.on('task_completed', (task) => {
        const taskData = this.uiState.tasks.get(task.id);
        if (taskData) {
          taskData.status = 'completed';
          taskData.endTime = Date.now();
          this.broadcastUpdate('task_completed', { task: task.id });
        }
      });
    }
    
    if (this.components.clusterManager) {
      this.components.clusterManager.on('cluster_update', (cluster) => {
        this.uiState.clusters.set(cluster.id, {
          id: cluster.id,
          name: cluster.name,
          members: cluster.members,
          status: cluster.status,
          currentTask: cluster.currentTask,
          leader: cluster.leader
        });
        this.broadcastUpdate('cluster_update', cluster);
      });
    }
    
    if (this.components.healthMonitor) {
      this.components.healthMonitor.on('health_alert', (alert) => {
        this.broadcastUpdate('health_alert', alert);
        this.addLogEntry('health', `${alert.severity.toUpperCase()}: ${alert.message}`);
      });
    }
    
    if (this.components.errorRecovery) {
      this.components.errorRecovery.on('error_occurred', (errorRecord) => {
        this.addLogEntry('error', `Error: ${errorRecord.type} - ${errorRecord.message}`);
        this.broadcastUpdate('error_occurred', { 
          type: errorRecord.type, 
          message: errorRecord.message,
          severity: errorRecord.severity 
        });
      });
      
      this.components.errorRecovery.on('recovery_successful', ({ errorId, recoveryResult }) => {
        this.addLogEntry('recovery', `Recovery successful: ${recoveryResult.action}`);
        this.broadcastUpdate('recovery_success', { errorId, action: recoveryResult.action });
      });
    }
    
    if (this.components.circuitBreaker) {
      this.components.circuitBreaker.on('breaker_opened', ({ name, recentFailures }) => {
        this.addLogEntry('circuit', `Circuit breaker opened: ${name} (${recentFailures} failures)`);
        this.broadcastUpdate('circuit_opened', { name, failures: recentFailures });
      });
      
      this.components.circuitBreaker.on('breaker_closed', ({ name }) => {
        this.addLogEntry('circuit', `Circuit breaker closed: ${name}`);
        this.broadcastUpdate('circuit_closed', { name });
      });
    }
    
    // Groups system event listeners
    if (this.components.groupsIntegration) {
      const groupManager = this.components.groupsIntegration.getGroupManager();
      
      groupManager.on('group_created', (group) => {
        this.addLogEntry('groups', `New group created: ${group.name}`);
        this.broadcastUpdate('groups_updated', {
          type: 'group_created',
          group
        });
      });
      
      groupManager.on('group_deleted', (data) => {
        this.addLogEntry('groups', `Group deleted: ${data.groupName}`);
        this.broadcastUpdate('groups_updated', {
          type: 'group_deleted',
          groupId: data.groupId,
          groupName: data.groupName
        });
      });
      
      groupManager.on('client_assigned_to_group', (data) => {
        const client = groupManager.clients.get(data.clientId);
        const clientName = client ? client.username : data.clientId;
        this.addLogEntry('groups', `${clientName} assigned to group ${data.group.name}`);
        this.broadcastUpdate('groups_updated', {
          type: 'client_assigned',
          clientId: data.clientId,
          groupId: data.groupId,
          group: data.group
        });
      });
      
      groupManager.on('client_removed_from_group', (data) => {
        const client = groupManager.clients.get(data.clientId);
        const clientName = client ? client.username : data.clientId;
        this.addLogEntry('groups', `${clientName} removed from group ${data.group.name}`);
        this.broadcastUpdate('groups_updated', {
          type: 'client_removed',
          clientId: data.clientId,
          groupId: data.groupId,
          group: data.group
        });
      });
      
      groupManager.on('task_assigned_to_group', (data) => {
        this.addLogEntry('groups', `Task ${data.task.type} assigned to group`);
        this.broadcastUpdate('groups_updated', {
          type: 'task_assigned',
          groupId: data.groupId,
          task: data.task
        });
      });
      
      groupManager.on('task_completed', (task) => {
        this.addLogEntry('groups', `Task ${task.type} completed`);
        this.broadcastUpdate('groups_updated', {
          type: 'task_completed',
          groupId: task.groupId,
          taskId: task.id
        });
      });
      
      groupManager.on('client_health_changed', (data) => {
        // Update player visualization with health data
        if (this.playerVisualization) {
          const client = groupManager.clients.get(data.clientId);
          if (client && client.health) {
            this.updatePlayerPosition(
              client.username,
              client.health.position?.x || 0,
              client.health.position?.z || 0,
              'overworld',
              {
                y: client.health.position?.y || 64,
                health: client.health.health || 20,
                hunger: client.health.food || 20
              }
            );
          }
        }
      });
    }
  }

  async start() {
    return new Promise((resolve, reject) => {
      this.server.listen(this.port, this.host, (error) => {
        if (error) {
          reject(error);
          return;
        }
        
        this.isRunning = true;
        
        // Start periodic updates
        this.updateInterval = setInterval(() => {
          this.updateSystemStats();
          this.broadcastSystemStats();
        }, 5000); // Update every 5 seconds
        
        this.logger.info(`WebUI server started on http://${this.host}:${this.port}`);
        resolve();
      });
    });
  }

  async stop() {
    if (!this.isRunning) return;
    
    this.logger.info('Stopping WebUI server...');
    
    // Clear update interval
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
      this.updateInterval = null;
    }
    
    // Close all WebSocket connections
    this.wsClients.forEach(ws => {
      if (ws.readyState === WebSocket.OPEN) {
        ws.close();
      }
    });
    this.wsClients.clear();
    
    // Close WebSocket server
    if (this.wss) {
      this.wss.close();
    }
    
    // Close HTTP server
    if (this.server) {
      return new Promise((resolve) => {
        this.server.close(() => {
          this.isRunning = false;
          this.logger.info('WebUI server stopped');
          resolve();
        });
      });
    }
    
    this.isRunning = false;
  }

  // UI State Management
  updatePlayerPosition(username, x, z, dimension = 'overworld', additionalData = {}) {
    const player = this.uiState.mapData.players.get(username) || {
      username,
      online: true,
      dimension,
      health: 20,
      hunger: 20,
      level: 0,
      xp: 0,
      inventory: {
        main: new Array(27).fill(null),
        hotbar: new Array(9).fill(null),
        armor: { helmet: null, chestplate: null, leggings: null, boots: null }
      },
      selectedSlot: 0
    };
    
    // Update position
    player.x = x;
    player.z = z;
    player.y = additionalData.y || 64;
    player.dimension = dimension;
    player.lastUpdate = Date.now();
    
    // Update additional player data if provided
    if (additionalData.health !== undefined) player.health = additionalData.health;
    if (additionalData.hunger !== undefined) player.hunger = additionalData.hunger;
    if (additionalData.level !== undefined) player.level = additionalData.level;
    if (additionalData.xp !== undefined) player.xp = additionalData.xp;
    if (additionalData.inventory) player.inventory = additionalData.inventory;
    if (additionalData.selectedSlot !== undefined) player.selectedSlot = additionalData.selectedSlot;
    
    this.uiState.mapData.players.set(username, player);
    
    this.broadcastToClients({
      type: 'player_update',
      data: player
    });
  }

  removePlayer(username) {
    this.uiState.mapData.players.delete(username);
    this.broadcastToClients({
      type: 'player_removed',
      data: { username }
    });
  }

  addWaypoint(name, x, z, dimension = 'overworld') {
    const waypoint = {
      id: `waypoint-${Date.now()}`,
      name,
      x,
      z,
      dimension,
      created: Date.now()
    };
    
    this.uiState.mapData.waypoints.set(waypoint.id, waypoint);
    this.broadcastToClients({
      type: 'waypoint_added',
      data: waypoint
    });
  }

  addLogEntry(type, message) {
    const entry = {
      type,
      message,
      timestamp: Date.now()
    };
    
    this.uiState.logs.push(entry);
    
    // Keep only last 100 log entries
    if (this.uiState.logs.length > 100) {
      this.uiState.logs.shift();
    }
  }

  updateSystemStats() {
    this.uiState.systemStats.connectedAccounts = this.uiState.mapData.players.size;
    this.uiState.systemStats.activeTasks = Array.from(this.uiState.tasks.values())
      .filter(task => task.status === 'running').length;
    
    if (this.components.healthMonitor) {
      const health = this.components.healthMonitor.getCurrentHealth();
      this.uiState.systemStats.healthStatus = health.status;
    }
    
    if (this.components.errorRecovery) {
      const errorStats = this.components.errorRecovery.getErrorStatistics();
      this.uiState.systemStats.errorRate = errorStats.recentErrors || 0;
    }
  }

  broadcastSystemStats() {
    this.broadcastToClients({
      type: 'system_stats',
      data: this.uiState.systemStats
    });
  }

  broadcastUpdate(type, data) {
    this.broadcastToClients({
      type: 'update',
      updateType: type,
      data
    });
  }

  broadcastToClients(message) {
    const messageString = JSON.stringify(message);
    this.wsClients.forEach(ws => {
      if (ws.readyState === WebSocket.OPEN) {
        try {
          ws.send(messageString);
        } catch (error) {
          this.logger.warn('Failed to send WebSocket message:', error);
          this.wsClients.delete(ws);
        }
      }
    });
  }
  
  // Public method for external components to broadcast messages
  broadcast(type, data) {
    this.broadcastToClients({
      type: 'update',
      updateType: type,
      data
    });
  }

  // File serving methods
  async serveMainPage(req, res) {
    const html = this.generateMainPageHTML();
    res.setHeader('Content-Type', 'text/html');
    res.send(html);
  }

  async serveCSS(req, res) {
    const css = this.generateCSS();
    res.setHeader('Content-Type', 'text/css');
    res.send(css);
  }

  async serveJavaScript(req, res) {
    const js = this.generateJavaScript();
    res.setHeader('Content-Type', 'application/javascript');
    res.send(js);
  }

  generateMainPageHTML() {
    return this.minecraftUI.generateMainPageHTML();
  }

  generateCSS() {
    return this.minecraftUI.generateCompleteCSS();
  }


  generateJavaScript() {
    // Serve the MinecraftClientV2 JavaScript
    const fs = require('fs');
    const path = require('path');
    
    try {
      const clientPath = path.join(__dirname, 'public', 'script.js');
      return fs.readFileSync(clientPath, 'utf8');
    } catch (error) {
      this.logger.error('Failed to load client JavaScript:', error);
      return `console.error('Failed to load AppyProx client');`;
    }
  }

  generateJavaScriptOld() {
    return `/* AppyProx Xaeros-Style Web Interface JavaScript */

// Global state and configuration
const AppyProxUI = {
    websocket: null,
    reconnectAttempts: 0,
    maxReconnectAttempts: 5,
    reconnectDelay: 2000,
    
    // Map state
    map: {
        canvas: null,
        ctx: null,
        zoom: 1,
        centerX: 0,
        centerZ: 0,
        isDragging: false,
        lastMouseX: 0,
        lastMouseY: 0,
        players: new Map(),
        waypoints: new Map(),
        structures: new Map(),
        chunks: new Map(),
        needsRender: true,
        lowPerformanceMode: false,
        blockSize: 16
    },
    
    // UI state
    ui: {
        systemStats: {},
        tasks: new Map(),
        clusters: new Map(),
        logs: [],
        healthMetrics: {}
    },
    
    // Initialize the interface
    init() {
        console.log('Initializing AppyProx Web Interface...');
        
        // Setup DOM elements
        this.setupDOM();
        
        // Setup map canvas
        this.setupMapCanvas();
        
        // Setup WebSocket connection
        this.connectWebSocket();
        
        // Setup event listeners
        this.setupEventListeners();
        
        // Load texture atlas
        this.loadTextureAtlas();
        
        // Start periodic updates
        this.startPeriodicUpdates();
        
        // Initialize Groups system
        this.initializeGroupsSystem();
        
        console.log('AppyProx Web Interface initialized');
    },
    
    setupDOM() {
        // Cache frequently used elements
        this.elements = {
            // Status bar
            connectionStatus: document.getElementById('connection-status'),
            playerCount: document.getElementById('player-count'),
            taskCount: document.getElementById('task-count'),
            healthStatus: document.getElementById('health-status'),
            uptime: document.getElementById('uptime'),
            tpsValue: document.getElementById('tps-value'),
            updateRateValue: document.getElementById('update-rate-value'),
            
            // Map controls
            zoomSlider: document.getElementById('zoom-slider'),
            zoomValue: document.getElementById('zoom-value'),
            centerMapBtn: document.getElementById('center-map'),
            resetViewBtn: document.getElementById('reset-view'),
            
            // Lists
            playerList: document.getElementById('player-list'),
            clusterList: document.getElementById('cluster-list'),
            taskList: document.getElementById('task-list'),
            activityLog: document.getElementById('activity-log'),
            breakerList: document.getElementById('breaker-list'),
            
            // Controls
            createClusterBtn: document.getElementById('create-cluster'),
            createTaskBtn: document.getElementById('create-task'),
            taskTypeSelect: document.getElementById('task-type'),
            createBackupBtn: document.getElementById('create-backup'),
            showRollbackBtn: document.getElementById('show-rollback'),
            clearLogBtn: document.getElementById('clear-log'),
            
            // Health metrics
            cpuBar: document.getElementById('cpu-bar'),
            cpuValue: document.getElementById('cpu-value'),
            memoryBar: document.getElementById('memory-bar'),
            memoryValue: document.getElementById('memory-value'),
            errorBar: document.getElementById('error-bar'),
            errorValue: document.getElementById('error-value'),
            
            // Map
            mapCanvas: document.getElementById('map-canvas'),
            mapOverlay: document.getElementById('map-overlay'),
            coordinates: document.getElementById('coordinates'),
            biomeInfo: document.getElementById('biome-info'),
            
            // Modal and toast
            modalOverlay: document.getElementById('modal-overlay'),
            modalContent: document.getElementById('modal-content'),
            toastContainer: document.getElementById('toast-container')
        };
    },
    
    setupMapCanvas() {
        this.map.canvas = this.elements.mapCanvas;
        this.map.ctx = this.map.canvas.getContext('2d');
        
        // Set canvas size
        this.resizeCanvas();
        
        // Initial map render
        this.renderMap();
    },
    
    resizeCanvas() {
        const container = this.map.canvas.parentElement;
        const rect = container.getBoundingClientRect();
        
        this.map.canvas.width = rect.width;
        this.map.canvas.height = rect.height;
        
        // Re-render after resize
        this.renderMap();
    },
    
    connectWebSocket() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = \`\${protocol}//\${window.location.host}\`;
        
        try {
            this.websocket = new WebSocket(wsUrl);
            
            this.websocket.onopen = () => {
                console.log('WebSocket connected');
                this.reconnectAttempts = 0;
                this.updateConnectionStatus(true);
            };
            
            this.websocket.onmessage = (event) => {
                this.handleWebSocketMessage(JSON.parse(event.data));
            };
            
            this.websocket.onclose = () => {
                console.log('WebSocket disconnected');
                this.updateConnectionStatus(false);
                this.attemptReconnect();
            };
            
            this.websocket.onerror = (error) => {
                console.error('WebSocket error:', error);
                this.updateConnectionStatus(false);
            };
            
        } catch (error) {
            console.error('Failed to connect WebSocket:', error);
            this.updateConnectionStatus(false);
        }
    },
    
    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(\`Attempting to reconnect (\${this.reconnectAttempts}/\${this.maxReconnectAttempts})...\`);
            
            setTimeout(() => {
                this.connectWebSocket();
            }, this.reconnectDelay * this.reconnectAttempts);
        } else {
            console.error('Max reconnection attempts reached');
            this.showToast('Connection lost', 'Unable to reconnect to server', 'error');
        }
    },
    
    handleWebSocketMessage(data) {
        switch (data.type) {
            case 'initial_state':
                this.loadInitialState(data.data);
                break;
                
            case 'system_stats':
                this.updateSystemStats(data.data);
                break;
                
            case 'player_update':
                this.updatePlayer(data.data);
                break;
                
            case 'player_removed':
                this.removePlayer(data.data.username);
                break;
                
            case 'task_progress':
                this.updateTaskProgress(data.data);
                break;
                
            case 'update':
                this.handleUpdate(data.updateType, data.data);
                break;
                
            case 'health_alert':
                this.handleHealthAlert(data.data);
                break;
                
            case 'map_update':
                this.updateMapView(data.data);
                break;
                
            case 'tps_update':
                this.updateTPS(data.data);
                break;
                
            case 'system_update':
                this.updateSystemPerformance(data.data);
                break;
                
            case 'lag_spike':
                this.handleLagSpike(data.data);
                break;
                
            case 'chunk_loaded':
                this.handleChunkLoaded(data.data);
                break;
                
            case 'chunk_rendered':
                this.handleChunkRendered(data.data);
                break;
                
            case 'groups_updated':
                this.handleGroupsUpdate(data.data);
                break;
                
            case 'groups_data':
                this.loadGroupsData(data.groups);
                break;
                
            default:
                console.log('Unknown message type:', data.type);
        }
    },
    
    setupEventListeners() {
        // Window resize
        window.addEventListener('resize', () => {
            this.resizeCanvas();
        });
        
        // Map controls
        this.elements.zoomSlider.addEventListener('input', (e) => {
            this.setZoom(parseFloat(e.target.value));
        });
        
        this.elements.centerMapBtn.addEventListener('click', () => {
            this.centerOnPlayers();
        });
        
        this.elements.resetViewBtn.addEventListener('click', () => {
            this.resetMapView();
        });
        
        // Map canvas interactions
        this.setupMapInteractions();
        
        // Control buttons
        this.elements.createClusterBtn.addEventListener('click', () => {
            this.showCreateClusterModal();
        });
        
        this.elements.createTaskBtn.addEventListener('click', () => {
            this.createTask();
        });
        
        this.elements.createBackupBtn.addEventListener('click', () => {
            this.createBackup();
        });
        
        this.elements.showRollbackBtn.addEventListener('click', () => {
            this.showRollbackModal();
        });
        
        this.elements.clearLogBtn.addEventListener('click', () => {
            this.clearActivityLog();
        });
        
        // Modal close
        this.elements.modalOverlay.addEventListener('click', (e) => {
            if (e.target === this.elements.modalOverlay) {
                this.hideModal();
            }
        });
    },
    
    setupMapInteractions() {
        const canvas = this.map.canvas;
        
        // Mouse wheel for zoom
        canvas.addEventListener('wheel', (e) => {
            e.preventDefault();
            const zoomFactor = e.deltaY > 0 ? 0.9 : 1.1;
            this.setZoom(this.map.zoom * zoomFactor);
        });
        
        // Player click handling
        canvas.addEventListener('click', (e) => {
            const coords = this.screenToWorld(e.offsetX, e.offsetY);
            const clickedPlayer = this.findPlayerAtPosition(coords.x, coords.z);
            
            if (clickedPlayer) {
                this.showPlayerInfo(clickedPlayer);
            }
        });
        
        // Mouse drag for panning
        canvas.addEventListener('mousedown', (e) => {
            this.map.isDragging = true;
            this.map.lastMouseX = e.clientX;
            this.map.lastMouseY = e.clientY;
            canvas.style.cursor = 'grabbing';
        });
        
        canvas.addEventListener('mousemove', (e) => {
            if (this.map.isDragging) {
                const deltaX = e.clientX - this.map.lastMouseX;
                const deltaY = e.clientY - this.map.lastMouseY;
                
                this.map.centerX -= deltaX / this.map.zoom;
                this.map.centerZ -= deltaY / this.map.zoom;
                
                this.map.lastMouseX = e.clientX;
                this.map.lastMouseY = e.clientY;
                
                this.renderMap();
                this.sendMapUpdate();
            } else {
                // Update coordinate display
                this.updateCoordinateDisplay(e);
            }
        });
        
        canvas.addEventListener('mouseup', () => {
            this.map.isDragging = false;
            canvas.style.cursor = 'crosshair';
        });
        
        canvas.addEventListener('mouseleave', () => {
            this.map.isDragging = false;
            canvas.style.cursor = 'crosshair';
        });
        
        // Right click for waypoint
        canvas.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            const coords = this.screenToWorld(e.offsetX, e.offsetY);
            this.showWaypointDialog(coords.x, coords.z);
        });
    },
    
    // Map rendering methods
    renderMap() {
        const ctx = this.map.ctx;
        const canvas = this.map.canvas;
        
        // Clear canvas
        ctx.fillStyle = '#0f1419';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        // Draw grid
        this.drawGrid();
        
        // Draw chunks (if any)
        this.drawChunks();
        
        // Draw structures
        this.drawStructures();
        
        // Draw waypoints
        this.drawWaypoints();
        
        // Draw players
        this.drawPlayers();
        
        // Draw paths/routes (if any)
        this.drawPaths();
    },
    
    drawGrid() {
        const ctx = this.map.ctx;
        const canvas = this.map.canvas;
        
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
        ctx.lineWidth = 1;
        
        const gridSize = 16 * this.map.zoom; // Minecraft chunk size
        const offsetX = (this.map.centerX * this.map.zoom) % gridSize;
        const offsetY = (this.map.centerZ * this.map.zoom) % gridSize;
        
        // Vertical lines
        for (let x = -offsetX; x < canvas.width + gridSize; x += gridSize) {
            ctx.beginPath();
            ctx.moveTo(x, 0);
            ctx.lineTo(x, canvas.height);
            ctx.stroke();
        }
        
        // Horizontal lines
        for (let y = -offsetY; y < canvas.height + gridSize; y += gridSize) {
            ctx.beginPath();
            ctx.moveTo(0, y);
            ctx.lineTo(canvas.width, y);
            ctx.stroke();
        }
    },
    
    drawPlayers() {
        const ctx = this.map.ctx;
        
        for (const [username, player] of this.map.players.entries()) {
            if (player.dimension !== 'overworld') continue; // Only show overworld for now
            
            const screenPos = this.worldToScreen(player.x, player.z);
            const playerRadius = Math.max(4, 6 * this.map.zoom);
            
            // Health-based color
            const health = player.health || 20;
            let playerColor;
            if (health > 15) {
                playerColor = '#10b981'; // Green - Healthy
            } else if (health > 8) {
                playerColor = '#f59e0b'; // Yellow - Injured
            } else {
                playerColor = '#ef4444'; // Red - Critical
            }
            
            // Player body (larger circle)
            ctx.fillStyle = player.online ? playerColor : '#6b7280';
            ctx.beginPath();
            ctx.arc(screenPos.x, screenPos.y, playerRadius, 0, 2 * Math.PI);
            ctx.fill();
            
            // Player border
            ctx.strokeStyle = player.online ? '#ffffff' : '#999999';
            ctx.lineWidth = Math.max(1, 2 * this.map.zoom);
            ctx.stroke();
            
            // Player head (smaller circle on top)
            ctx.fillStyle = '#FDBCB4'; // Skin color
            ctx.beginPath();
            ctx.arc(screenPos.x, screenPos.y - playerRadius * 0.7, playerRadius * 0.6, 0, 2 * Math.PI);
            ctx.fill();
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = Math.max(1, 1 * this.map.zoom);
            ctx.stroke();
            
            // Health bar above player (if damaged)
            if (health < 20 && this.map.zoom > 0.5) {
                const barWidth = playerRadius * 2;
                const barHeight = Math.max(2, 3 * this.map.zoom);
                const barY = screenPos.y - playerRadius * 1.8;
                
                // Background
                ctx.fillStyle = '#333333';
                ctx.fillRect(screenPos.x - barWidth/2, barY, barWidth, barHeight);
                
                // Health fill
                ctx.fillStyle = '#ce2029';
                const healthWidth = (health / 20) * barWidth;
                ctx.fillRect(screenPos.x - barWidth/2, barY, healthWidth, barHeight);
                
                // Border
                ctx.strokeStyle = '#ffffff';
                ctx.lineWidth = 1;
                ctx.strokeRect(screenPos.x - barWidth/2, barY, barWidth, barHeight);
            }
            
            // Player name
            ctx.fillStyle = player.online ? '#ffffff' : '#aaaaaa';
            const fontSize = Math.max(8, 10 * this.map.zoom);
            ctx.font = \`\${fontSize}px 'JetBrains Mono'\`;
            ctx.textAlign = 'center';
            
            // Name background for better readability
            const textY = screenPos.y + playerRadius + fontSize + 4;
            ctx.strokeStyle = '#000000';
            ctx.lineWidth = 3;
            ctx.strokeText(username, screenPos.x, textY);
            ctx.fillText(username, screenPos.x, textY);
            
            // Activity indicator (small dot showing recent activity)
            const timeSinceUpdate = Date.now() - (player.lastUpdate || 0);
            if (timeSinceUpdate < 5000) { // Active in last 5 seconds
                ctx.fillStyle = '#4ade80';
                ctx.beginPath();
                ctx.arc(screenPos.x + playerRadius * 0.7, screenPos.y - playerRadius * 0.7, Math.max(2, 3 * this.map.zoom), 0, 2 * Math.PI);
                ctx.fill();
            }
        }
    },
    
    drawWaypoints() {
        const ctx = this.map.ctx;
        
        for (const [id, waypoint] of this.map.waypoints.entries()) {
            const screenPos = this.worldToScreen(waypoint.x, waypoint.z);
            
            // Waypoint diamond
            ctx.fillStyle = '#f59e0b';
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = 2;
            
            ctx.beginPath();
            const size = 8 * this.map.zoom;
            ctx.moveTo(screenPos.x, screenPos.y - size);
            ctx.lineTo(screenPos.x + size, screenPos.y);
            ctx.lineTo(screenPos.x, screenPos.y + size);
            ctx.lineTo(screenPos.x - size, screenPos.y);
            ctx.closePath();
            ctx.fill();
            ctx.stroke();
            
            // Waypoint name
            ctx.fillStyle = '#ffffff';
            ctx.font = \`\${Math.max(9, 9 * this.map.zoom)}px 'JetBrains Mono'\`;
            ctx.textAlign = 'center';
            ctx.fillText(waypoint.name, screenPos.x, screenPos.y + 20 * this.map.zoom);
        }
    },
    
    drawStructures() {
        const ctx = this.map.ctx;
        
        for (const [id, structure] of this.map.structures.entries()) {
            const screenPos = this.worldToScreen(structure.x, structure.z);
            
            // Structure square
            ctx.fillStyle = '#a78bfa';
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = 1;
            
            const size = 4 * this.map.zoom;
            ctx.fillRect(screenPos.x - size, screenPos.y - size, size * 2, size * 2);
            ctx.strokeRect(screenPos.x - size, screenPos.y - size, size * 2, size * 2);
        }
    },
    
    drawChunks() {
        if (!this.map.chunks || this.map.chunks.size === 0) return;
        
        const ctx = this.map.ctx;
        const canvas = this.map.canvas;
        
        // Calculate visible chunk range
        const viewBounds = this.getViewBounds();
        const chunkStartX = Math.floor(viewBounds.minX / 16);
        const chunkEndX = Math.ceil(viewBounds.maxX / 16);
        const chunkStartZ = Math.floor(viewBounds.minZ / 16);
        const chunkEndZ = Math.ceil(viewBounds.maxZ / 16);
        
        // Render visible chunks
        for (let chunkX = chunkStartX; chunkX <= chunkEndX; chunkX++) {
            for (let chunkZ = chunkStartZ; chunkZ <= chunkEndZ; chunkZ++) {
                const chunkId = \`\${chunkX},\${chunkZ}\`;
                const chunk = this.map.chunks.get(chunkId);
                
                if (chunk && chunk.rendered) {
                    this.drawChunk(ctx, chunk);
                }
            }
        }
    },
    
    drawChunk(ctx, chunk) {
        const blockSize = Math.max(1, 16 * this.map.zoom / 16); // Size of each block in pixels
        
        // Draw biome background
        const chunkScreenX = (chunk.x * 16 - this.map.centerX) * this.map.zoom + this.map.canvas.width / 2;
        const chunkScreenZ = (chunk.z * 16 - this.map.centerZ) * this.map.zoom + this.map.canvas.height / 2;
        
        // Draw biome color as chunk background
        const biomeColor = this.getBiomeColor(chunk.biome);
        ctx.fillStyle = biomeColor;
        ctx.fillRect(chunkScreenX, chunkScreenZ, 16 * blockSize, 16 * blockSize);
        
        // Draw blocks if zoom level is high enough
        if (this.map.zoom > 0.5 && chunk.blocks) {
            // Load texture atlas if available
            if (this.textureAtlas) {
                this.drawChunkWithTextures(ctx, chunk, chunkScreenX, chunkScreenZ, blockSize);
            } else {
                this.drawChunkSimplified(ctx, chunk, chunkScreenX, chunkScreenZ, blockSize);
            }
        }
        
        // Draw entities in chunk
        if (chunk.entities && this.map.zoom > 1) {
            this.drawChunkEntities(ctx, chunk, chunkScreenX, chunkScreenZ, blockSize);
        }
    },
    
    drawChunkWithTextures(ctx, chunk, screenX, screenZ, blockSize) {
        // Enhanced texture rendering (when texture atlas is loaded)
        for (let x = 0; x < 16; x++) {
            for (let z = 0; z < 16; z++) {
                const blockPos = \`\${x},\${chunk.heightMap[z * 16 + x]},\${z}\`;
                const block = chunk.blocks && chunk.blocks.get ? chunk.blocks.get(blockPos) : null;
                
                if (block && block.type !== 'air') {
                    const blockScreenX = screenX + x * blockSize;
                    const blockScreenZ = screenZ + z * blockSize;
                    
                    // Draw from texture atlas if available
                    if (this.textureAtlas && this.textureAtlas[block.type]) {
                        const texture = this.textureAtlas[block.type];
                        ctx.drawImage(
                            this.textureAtlasImage,
                            texture.u * this.textureAtlasSize, texture.v * this.textureAtlasSize,
                            texture.width, texture.height,
                            blockScreenX, blockScreenZ, blockSize, blockSize
                        );
                    } else {
                        // Fallback to colored rectangles
                        ctx.fillStyle = this.getBlockColor(block.type);
                        ctx.fillRect(blockScreenX, blockScreenZ, blockSize, blockSize);
                    }
                }
            }
        }
    },
    
    drawChunkSimplified(ctx, chunk, screenX, screenZ, blockSize) {
        // Simplified rendering without textures
        for (let x = 0; x < 16; x++) {
            for (let z = 0; z < 16; z++) {
                const height = chunk.heightMap[z * 16 + x];
                if (height > 0) {
                    const blockScreenX = screenX + x * blockSize;
                    const blockScreenZ = screenZ + z * blockSize;
                    
                    // Use height-based coloring
                    const heightRatio = (height - 64) / 64; // Normalize around sea level
                    const color = this.getHeightBasedColor(heightRatio);
                    
                    ctx.fillStyle = color;
                    ctx.fillRect(blockScreenX, blockScreenZ, blockSize, blockSize);
                }
            }
        }
    },
    
    drawChunkEntities(ctx, chunk, screenX, screenZ, blockSize) {
        chunk.entities.forEach(entity => {
            const entityX = (entity.x % 16) * blockSize + screenX;
            const entityZ = (entity.z % 16) * blockSize + screenZ;
            
            // Draw entity dot
            ctx.fillStyle = this.getEntityColor(entity.type);
            ctx.beginPath();
            ctx.arc(entityX, entityZ, Math.max(2, blockSize * 0.3), 0, 2 * Math.PI);
            ctx.fill();
            
            // Entity border
            ctx.strokeStyle = '#ffffff';
            ctx.lineWidth = 1;
            ctx.stroke();
        });
    },
    
    getBiomeColor(biome) {
        const biomeColors = {
            'ocean': '#0077be',
            'plains': '#91bd59',
            'desert': '#fad5a5',
            'forest': '#477a3e',
            'taiga': '#598f42',
            'mountains': '#888888',
            'swamp': '#6a5d43',
            'jungle': '#3f6f43',
            'nether': '#8b0000',
            'end': '#d4c0d4'
        };
        return biomeColors[biome] || '#91bd59';
    },
    
    getBlockColor(blockType) {
        const blockColors = {
            'grass_block': '#7cb518',
            'dirt': '#8b4513',
            'stone': '#808080',
            'cobblestone': '#696969',
            'bedrock': '#404040',
            'sand': '#eecfa1',
            'gravel': '#a0a0a0',
            'water': '#0077be',
            'lava': '#ff5722',
            'coal_ore': '#363636',
            'iron_ore': '#d8af93',
            'diamond_ore': '#5cdbd3',
            'oak_log': '#654321',
            'oak_leaves': '#4f6f2a'
        };
        return blockColors[blockType] || '#808080';
    },
    
    getEntityColor(entityType) {
        const entityColors = {
            'player': '#10b981',
            'zombie': '#8b0000',
            'skeleton': '#f5f5dc',
            'creeper': '#32cd32',
            'spider': '#8b0000',
            'cow': '#654321',
            'pig': '#ffc0cb',
            'chicken': '#ffffff'
        };
        return entityColors[entityType] || '#ffff00';
    },
    
    getHeightBasedColor(heightRatio) {
        // Generate colors based on height (green = low, brown = high)
        const r = Math.floor(100 + heightRatio * 100);
        const g = Math.floor(150 - Math.abs(heightRatio) * 50);
        const b = Math.floor(50 + heightRatio * 50);
        
        return 'rgb(' + Math.max(0, Math.min(255, r)) + ', ' + Math.max(0, Math.min(255, g)) + ', ' + Math.max(0, Math.min(255, b)) + ')'
    },
    
    // Texture atlas loading
    async loadTextureAtlas() {
        try {
            // Load texture metadata
            const textureData = await this.apiRequest('/api/textures');
            
            if (textureData && textureData.mappings && textureData.mappings.blocks) {
                this.textureAtlas = textureData.mappings.blocks;
                this.textureAtlasSize = textureData.atlases.blocks.size;
                
                // Load texture atlas image
                this.textureAtlasImage = new Image();
                this.textureAtlasImage.onload = () => {
                    console.log('Texture atlas loaded successfully');
                    this.renderMap(); // Re-render with textures
                };
                
                this.textureAtlasImage.onerror = (error) => {
                    console.warn('Failed to load texture atlas image:', error);
                    this.textureAtlas = null;
                };
                
                this.textureAtlasImage.src = textureData.atlases.blocks.path;
            }
            
        } catch (error) {
            console.warn('Failed to load texture atlas:', error);
            this.textureAtlas = null;
        }
    },
    
    getViewBounds() {
        const canvas = this.map.canvas;
        const halfWidth = canvas.width / (2 * this.map.zoom);
        const halfHeight = canvas.height / (2 * this.map.zoom);
        
        return {
            minX: this.map.centerX - halfWidth,
            maxX: this.map.centerX + halfWidth,
            minZ: this.map.centerZ - halfHeight,
            maxZ: this.map.centerZ + halfHeight
        };
    },
    
    drawPaths() {
        // TODO: Implement path rendering for active tasks
    },
    
    // Coordinate transformation
    worldToScreen(worldX, worldZ) {
        const canvas = this.map.canvas;
        return {
            x: (worldX - this.map.centerX) * this.map.zoom + canvas.width / 2,
            y: (worldZ - this.map.centerZ) * this.map.zoom + canvas.height / 2
        };
    },
    
    screenToWorld(screenX, screenY) {
        const canvas = this.map.canvas;
        return {
            x: (screenX - canvas.width / 2) / this.map.zoom + this.map.centerX,
            z: (screenY - canvas.height / 2) / this.map.zoom + this.map.centerZ
        };
    },
    
    // Map control methods
    setZoom(newZoom) {
        this.map.zoom = Math.max(0.1, Math.min(5, newZoom));
        this.elements.zoomSlider.value = this.map.zoom;
        this.elements.zoomValue.textContent = \`\${this.map.zoom.toFixed(1)}x\`;
        this.renderMap();
        this.sendMapUpdate();
    },
    
    centerOnPlayers() {
        if (this.map.players.size === 0) return;
        
        let totalX = 0, totalZ = 0, count = 0;
        
        for (const [username, player] of this.map.players.entries()) {
            if (player.online && player.dimension === 'overworld') {
                totalX += player.x;
                totalZ += player.z;
                count++;
            }
        }
        
        if (count > 0) {
            this.map.centerX = totalX / count;
            this.map.centerZ = totalZ / count;
            this.renderMap();
            this.sendMapUpdate();
        }
    },
    
    resetMapView() {
        this.map.centerX = 0;
        this.map.centerZ = 0;
        this.setZoom(1);
    },
    
    // UI Update methods
    updateConnectionStatus(connected) {
        const status = this.elements.connectionStatus;
        if (connected) {
            status.textContent = 'Connected';
            status.className = 'status-value online';
        } else {
            status.textContent = 'Disconnected';
            status.className = 'status-value offline';
        }
    },
    
    updateSystemStats(stats) {
        this.ui.systemStats = stats;
        
        this.elements.playerCount.textContent = stats.connectedAccounts || 0;
        this.elements.taskCount.textContent = stats.activeTasks || 0;
        this.elements.healthStatus.textContent = stats.healthStatus || 'Unknown';
        
        // Update uptime
        if (stats.uptime) {
            const uptime = new Date(stats.uptime);
            const hours = Math.floor(uptime / (1000 * 60 * 60));
            const minutes = Math.floor((uptime % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((uptime % (1000 * 60)) / 1000);
            this.elements.uptime.textContent = \`\${hours.toString().padStart(2, '0')}:\${minutes.toString().padStart(2, '0')}:\${seconds.toString().padStart(2, '0')}\`;
        }
    },
    
    updatePlayer(playerData) {
        this.map.players.set(playerData.username, playerData);
        this.renderMap();
        this.updatePlayerList();
    },
    
    removePlayer(username) {
        this.map.players.delete(username);
        this.renderMap();
        this.updatePlayerList();
    },
    
    updatePlayerList() {
        const list = this.elements.playerList;
        list.innerHTML = '';
        
        for (const [username, player] of this.map.players.entries()) {
            const item = document.createElement('div');
            item.className = 'player-item';
            item.innerHTML = \`
                <div class="player-status \${player.online ? 'online' : 'offline'}"></div>
                <div class="player-info">
                    <div class="player-name">\${username}</div>
                    <div class="player-details">\${Math.round(player.x)}, \${Math.round(player.z)} (\${player.dimension})</div>
                </div>
            \`;
            
            item.addEventListener('click', () => {
                // Center map on player
                this.map.centerX = player.x;
                this.map.centerZ = player.z;
                this.renderMap();
                this.sendMapUpdate();
            });
            
            list.appendChild(item);
        }
    },
    
    updateTaskProgress(progressData) {
        const task = this.ui.tasks.get(progressData.taskId);
        if (task) {
            task.progress = progressData.percentage;
            task.currentAction = progressData.action;
            this.updateTaskList();
        }
    },
    
    updateTaskList() {
        const list = this.elements.taskList;
        list.innerHTML = '';
        
        for (const [taskId, task] of this.ui.tasks.entries()) {
            const item = document.createElement('div');
            item.className = 'task-item';
            
            const statusClass = task.status === 'running' ? 'running' : 
                               task.status === 'completed' ? 'completed' : 'failed';
            
            item.innerHTML = \`
                <div class="task-status \${statusClass}"></div>
                <div class="task-info">
                    <div class="task-name">\${task.type} (\${task.id})</div>
                    <div class="task-details">\${task.currentAction || 'Initializing...'}</div>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: \${task.progress || 0}%"></div>
                    </div>
                </div>
            \`;
            
            item.addEventListener('click', () => {
                this.showTaskDetails(task);
            });
            
            list.appendChild(item);
        }
    },
    
    addLogEntry(type, message) {
        const timestamp = new Date().toLocaleTimeString();
        const entry = { type, message, timestamp };
        
        this.ui.logs.push(entry);
        
        // Keep only last 50 entries in UI
        if (this.ui.logs.length > 50) {
            this.ui.logs.shift();
        }
        
        this.updateActivityLog();
    },
    
    updateActivityLog() {
        const log = this.elements.activityLog;
        log.innerHTML = '';
        
        this.ui.logs.slice().reverse().forEach(entry => {
            const item = document.createElement('div');
            item.className = 'log-entry';
            item.innerHTML = \`
                <div class="log-timestamp">\${entry.timestamp}</div>
                <div class="log-type \${entry.type}">\${entry.type}</div>
                <div class="log-message">\${entry.message}</div>
            \`;
            log.appendChild(item);
        });
        
        // Scroll to top (newest entries)
        log.scrollTop = 0;
    },
    
    updateHealthMetrics(healthData) {
        if (healthData.system) {
            const cpu = healthData.system.cpu;
            const memory = healthData.system.memory;
            
            if (cpu) {
                this.elements.cpuBar.style.width = \`\${cpu.current}%\`;
                this.elements.cpuValue.textContent = \`\${cpu.current.toFixed(1)}%\`;
                this.elements.cpuBar.className = \`metric-fill \${cpu.current > 80 ? 'danger' : cpu.current > 60 ? 'warning' : ''}\`;
            }
            
            if (memory) {
                this.elements.memoryBar.style.width = \`\${memory.current}%\`;
                this.elements.memoryValue.textContent = \`\${memory.current.toFixed(1)}%\`;
                this.elements.memoryBar.className = \`metric-fill \${memory.current > 85 ? 'danger' : memory.current > 70 ? 'warning' : ''}\`;
            }
        }
        
        if (healthData.alerts) {
            const errorRate = Math.min(healthData.alerts.active * 10, 100); // Scale for display
            this.elements.errorBar.style.width = \`\${errorRate}%\`;
            this.elements.errorValue.textContent = \`\${healthData.alerts.active}/hr\`;
            this.elements.errorBar.className = \`metric-fill \${errorRate > 50 ? 'danger' : errorRate > 20 ? 'warning' : ''}\`;
        }
    },
    
    updateCircuitBreakers(breakers) {
        const list = this.elements.breakerList;
        list.innerHTML = '';
        
        breakers.forEach(breaker => {
            const item = document.createElement('div');
            item.className = 'breaker-item';
            
            const statusClass = breaker.state.toLowerCase().replace('_', '-');
            
            item.innerHTML = \`
                <div class="breaker-status \${statusClass}"></div>
                <div class="breaker-name">\${breaker.name}</div>
                <div class="breaker-stats">\${breaker.stats.successRate.toFixed(1)}%</div>
            \`;
            
            list.appendChild(item);
        });
    },
    
    // Event handling methods
    handleUpdate(updateType, data) {
        switch (updateType) {
            case 'player_joined':
                this.addLogEntry('info', \`Player \${data.player} joined\`);
                this.showToast('Player Joined', \`\${data.player} connected\`, 'info');
                break;
                
            case 'player_left':
                this.addLogEntry('info', \`Player \${data.player} left\`);
                break;
                
            case 'task_started':
                this.addLogEntry('info', \`Task \${data.task} started\`);
                break;
                
            case 'task_completed':
                this.addLogEntry('success', \`Task \${data.task} completed\`);
                this.showToast('Task Completed', \`Task \${data.task} finished successfully\`, 'success');
                break;
                
            case 'error_occurred':
                this.addLogEntry('error', \`Error: \${data.message}\`);
                if (data.severity === 'critical') {
                    this.showToast('Critical Error', data.message, 'error');
                }
                break;
                
            case 'recovery_success':
                this.addLogEntry('success', \`Recovery: \${data.action}\`);
                this.showToast('Recovery Successful', data.action, 'success');
                break;
                
            case 'circuit_opened':
                this.addLogEntry('warning', \`Circuit breaker opened: \${data.name}\`);
                this.showToast('Circuit Breaker', \`\${data.name} circuit opened\`, 'warning');
                break;
                
            default:
                console.log('Unhandled update type:', updateType, data);
        }
    },
    
    handleHealthAlert(alert) {
        this.addLogEntry('warning', alert.message);
        this.showToast('Health Alert', alert.message, alert.severity === 'critical' ? 'error' : 'warning');
    },
    
    // Enhanced TPS and performance handling
    updateTPS(data) {
        // Update TPS display in status bar
        if (this.elements.tpsValue) {
            this.elements.tpsValue.textContent = data.currentTPS.toFixed(1) + ' TPS';
        }
        
        // Add TPS indicator if performance is poor
        if (data.currentTPS < 15) {
            this.addLogEntry('warning', 'Low TPS detected: ' + data.currentTPS.toFixed(1) + ' on ' + data.serverAddress);
        }
        
        // Update map rendering frequency based on TPS
        this.adjustRenderFrequency(data.updateRate);
    },
    
    updateSystemPerformance(data) {
        // Update performance metrics in UI
        this.ui.systemStats.currentTPS = data.tps;
        this.ui.systemStats.updateRate = data.updateRate;
        
        // Update status bar
        if (this.elements.updateRateValue) {
            this.elements.updateRateValue.textContent = data.updateRate + '/s';
        }
        
        // Adjust rendering performance
        this.adjustMapPerformance(data.performance);
    },
    
    handleLagSpike(data) {
        const delay = Math.round(data.delay);
        this.addLogEntry('warning', 'Lag spike: ' + delay + 'ms delay on ' + data.serverAddress);
        this.showToast('Performance Warning', delay + 'ms lag spike detected', 'warning');
        
        // Temporarily reduce update frequency during lag
        this.temporaryPerformanceReduction();
    },
    
    handleChunkLoaded(data) {
        // Add chunk to map data
        this.map.chunks.set(data.chunkId, data);
        
        // Re-render map if chunk is in view
        if (this.isChunkInView(data.chunkX, data.chunkZ)) {
            this.renderMap();
        }
    },
    
    handleChunkRendered(data) {
        // Update chunk rendering status
        const chunk = this.map.chunks.get(data.chunkId);
        if (chunk) {
            chunk.rendered = true;
            chunk.biome = data.biome;
        }
        
        // Re-render map with new chunk data
        if (this.isChunkInView(data.x, data.z)) {
            this.renderMap();
        }
    },
    
    // Performance optimization methods
    adjustRenderFrequency(updateRate) {
        // Adjust map rendering based on server update rate
        const targetFPS = Math.min(updateRate, 60);
        const frameTime = 1000 / targetFPS;
        
        if (this.renderInterval) {
            clearInterval(this.renderInterval);
        }
        
        this.renderInterval = setInterval(() => {
            if (this.map.needsRender) {
                this.renderMap();
                this.map.needsRender = false;
            }
        }, frameTime);
    },
    
    adjustMapPerformance(performance) {
        // Reduce rendering quality if performance is poor
        if (performance.averageTPS < 15 || performance.lagSpikes > 5) {
            this.map.lowPerformanceMode = true;
            this.map.blockSize = Math.max(this.map.blockSize * 0.8, 4); // Reduce block size
        } else {
            this.map.lowPerformanceMode = false;
            this.map.blockSize = Math.min(this.map.blockSize * 1.1, 16); // Restore block size
        }
    },
    
    temporaryPerformanceReduction() {
        // Temporarily reduce update frequency for 10 seconds
        const originalUpdateRate = this.ui.updateRate || 20;
        this.ui.updateRate = Math.max(originalUpdateRate * 0.5, 5);
        
        setTimeout(() => {
            this.ui.updateRate = originalUpdateRate;
        }, 10000);
    },
    
    isChunkInView(chunkX, chunkZ) {
        // Check if chunk is visible in current map view
        const viewRange = 8; // Chunks to check around center
        const centerChunkX = Math.floor(this.map.centerX / 16);
        const centerChunkZ = Math.floor(this.map.centerZ / 16);
        
        return Math.abs(chunkX - centerChunkX) <= viewRange && 
               Math.abs(chunkZ - centerChunkZ) <= viewRange;
    },
    
    // WebSocket communication
    sendMapUpdate() {
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(JSON.stringify({
                type: 'map_center',
                x: this.map.centerX,
                z: this.map.centerZ
            }));
            
            this.websocket.send(JSON.stringify({
                type: 'map_zoom',
                zoom: this.map.zoom
            }));
        }
    },
    
    sendMessage(message) {
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(JSON.stringify(message));
        }
    },
    
    // API methods
    async apiRequest(endpoint, method = 'GET', body = null) {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        if (body) {
            options.body = JSON.stringify(body);
        }
        
        try {
            const response = await fetch(endpoint, options);
            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    },
    
    // Action methods
    async createTask() {
        const taskType = this.elements.taskTypeSelect.value;
        
        try {
            const result = await this.apiRequest('/api/tasks', 'POST', {
                type: taskType,
                parameters: {},
                clusterId: null
            });
            
            if (result.success) {
                this.showToast('Task Created', \`Task \${result.taskId} created\`, 'success');
            }
        } catch (error) {
            this.showToast('Error', 'Failed to create task', 'error');
        }
    },
    
    async createBackup() {
        const label = prompt('Enter backup label (optional):') || 'Manual-Backup';
        
        try {
            const result = await this.apiRequest('/api/backup', 'POST', { label });
            
            if (result.success) {
                this.showToast('Backup Created', \`Backup \${result.backupId} created\`, 'success');
            }
        } catch (error) {
            this.showToast('Error', 'Failed to create backup', 'error');
        }
    },
    
    clearActivityLog() {
        this.ui.logs = [];
        this.updateActivityLog();
    },
    
    // UI utility methods
    showToast(title, message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = \`toast \${type}\`;
        toast.innerHTML = \`
            <div class="toast-title">\${title}</div>
            <div class="toast-message">\${message}</div>
        \`;
        
        this.elements.toastContainer.appendChild(toast);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 5000);
    },
    
    showModal(content) {
        this.elements.modalContent.innerHTML = content;
        this.elements.modalOverlay.classList.remove('hidden');
    },
    
    hideModal() {
        this.elements.modalOverlay.classList.add('hidden');
    },
    
    showCreateClusterModal() {
        const content = \`
            <h3>Create New Cluster</h3>
            <div style="margin: 20px 0;">
                <label>Cluster Name:</label><br>
                <input type="text" id="cluster-name" style="width: 100%; padding: 8px; margin: 8px 0; background: #2a2a2a; color: white; border: 1px solid #404040; border-radius: 4px;">
            </div>
            <div style="margin: 20px 0;">
                <label>Max Size:</label><br>
                <input type="number" id="cluster-size" value="5" min="1" max="20" style="width: 100%; padding: 8px; margin: 8px 0; background: #2a2a2a; color: white; border: 1px solid #404040; border-radius: 4px;">
            </div>
            <div style="display: flex; gap: 10px; margin-top: 20px;">
                <button onclick="AppyProxUI.createCluster()" class="panel-button" style="flex: 1; margin: 0;">Create</button>
                <button onclick="AppyProxUI.hideModal()" style="flex: 1; margin: 0;">Cancel</button>
            </div>
        \`;
        
        this.showModal(content);
    },
    
    showRollbackModal() {
        // This would show available rollback points
        const content = \`
            <h3>System Rollback</h3>
            <p>Rollback functionality would be implemented here.</p>
            <div style="margin-top: 20px;">
                <button onclick="AppyProxUI.hideModal()" class="panel-button">Close</button>
            </div>
        \`;
        
        this.showModal(content);
    },
    
    showWaypointDialog(x, z) {
        const name = prompt(\`Create waypoint at \${Math.round(x)}, \${Math.round(z)}:\`);
        if (name) {
            this.sendMessage({
                type: 'set_waypoint',
                name,
                x,
                z,
                dimension: 'overworld'
            });
        }
    },
    
    // Player interaction methods
    findPlayerAtPosition(worldX, worldZ) {
        const clickRadius = 8 / this.map.zoom; // Larger click area when zoomed out
        
        for (const [username, player] of this.map.players.entries()) {
            if (player.dimension !== 'overworld') continue;
            
            const distance = Math.sqrt(
                Math.pow(player.x - worldX, 2) + Math.pow(player.z - worldZ, 2)
            );
            
            if (distance <= clickRadius) {
                return player;
            }
        }
        return null;
    },
    
    showPlayerInfo(playerData) {
        // Use simple player info display for now
        this.showSimplePlayerInfo(playerData);
    },
    
    initializePlayerVisualization() {
        // Initialize player visualization on demand
        if (typeof PlayerVisualization !== 'undefined') {
            this.playerVisualization = new PlayerVisualization(null, console);
        } else {
            // Fallback: create a simple player info display
            this.createSimplePlayerInfo();
        }
    },
    
    createSimplePlayerInfo() {
        // Simple fallback player info overlay
        const overlay = document.createElement('div');
        overlay.id = 'simple-player-info';
        overlay.style.cssText = \`
            position: fixed;
            top: 80px;
            left: 20px;
            background: rgba(0,0,0,0.8);
            color: white;
            padding: 16px;
            border-radius: 4px;
            border: 2px solid #555;
            z-index: 1500;
            min-width: 200px;
            font-family: monospace;
            font-size: 12px;
        \`;
        document.body.appendChild(overlay);
        
        this.simplePlayerOverlay = overlay;
    },
    
    showSimplePlayerInfo(playerData) {
        if (!this.simplePlayerOverlay) {
            this.createSimplePlayerInfo();
        }
        
        this.simplePlayerOverlay.innerHTML = \`
            <h3 style="margin: 0 0 8px 0; color: #4ade80;">\${playerData.username}</h3>
            <p style="margin: 2px 0;">Position: \${Math.round(playerData.x)}, \${Math.round(playerData.y || 64)}, \${Math.round(playerData.z)}</p>
            <p style="margin: 2px 0;">Dimension: \${playerData.dimension || 'overworld'}</p>
            <p style="margin: 2px 0;">Health: \${playerData.health || 20}/20</p>
            <p style="margin: 2px 0;">Hunger: \${playerData.hunger || 20}/20</p>
            <p style="margin: 2px 0;">Level: \${playerData.level || 0}</p>
            <button onclick="AppyProxUI.closeSimplePlayerInfo()" style="margin-top: 8px; padding: 4px 8px;">Close</button>
        \`;
        
        this.simplePlayerOverlay.style.display = 'block';
    },
    
    closeSimplePlayerInfo() {
        if (this.simplePlayerOverlay) {
            this.simplePlayerOverlay.style.display = 'none';
        }
    },
    
    // Utility methods
    updateCoordinateDisplay(e) {
        const coords = this.screenToWorld(e.offsetX, e.offsetY);
        this.elements.coordinates.textContent = \`X: \${Math.round(coords.x)}, Z: \${Math.round(coords.z)}\`;
    },
    
    loadInitialState(data) {
        // Load map data
        if (data.mapData) {
            this.map.zoom = data.mapData.zoom || 1;
            this.map.centerX = data.mapData.centerX || 0;
            this.map.centerZ = data.mapData.centerZ || 0;
            
            // Load players
            if (data.mapData.players) {
                this.map.players.clear();
                Object.values(data.mapData.players).forEach(player => {
                    this.map.players.set(player.username, player);
                });
            }
        }
        
        // Load system stats
        if (data.systemStats) {
            this.updateSystemStats(data.systemStats);
        }
        
        // Load tasks
        if (data.tasks) {
            this.ui.tasks.clear();
            data.tasks.forEach(task => {
                this.ui.tasks.set(task.id, task);
            });
        }
        
        // Load clusters
        if (data.clusters) {
            this.ui.clusters.clear();
            data.clusters.forEach(cluster => {
                this.ui.clusters.set(cluster.id, cluster);
            });
        }
        
        // Update all UI elements
        this.renderMap();
        this.updatePlayerList();
        this.updateTaskList();
    },
    
    startPeriodicUpdates() {
        // Fetch health data every 10 seconds
        setInterval(async () => {
            try {
                const healthData = await this.apiRequest('/api/health');
                this.updateHealthMetrics(healthData);
            } catch (error) {
                console.log('Failed to fetch health data:', error);
            }
        }, 10000);
        
        // Fetch circuit breaker status every 15 seconds
        setInterval(async () => {
            try {
                const breakers = await this.apiRequest('/api/circuit-breakers');
                this.updateCircuitBreakers(breakers);
            } catch (error) {
                console.log('Failed to fetch circuit breakers:', error);
            }
        }, 15000);
    },
    
    // Groups system methods
    initializeGroupsSystem() {
        console.log('Initializing Groups system...');
        
        // Request initial groups data
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(JSON.stringify({
                type: 'groups_get_all'
            }));
        }
        
        // Groups will be initialized by the GroupVisualization component
        // when it receives the groups data
    },
    
    handleGroupsUpdate(data) {
        console.log('Groups update:', data.type, data);
        
        switch (data.type) {
            case 'group_created':
                this.showToast('Group Created', \`New group '\${data.group.name}' has been created\`, 'success');
                break;
                
            case 'group_deleted':
                this.showToast('Group Deleted', \`Group '\${data.groupName}' has been deleted\`, 'info');
                break;
                
            case 'client_assigned':
                this.showToast('Member Added', \`Client assigned to group\`, 'info');
                break;
                
            case 'client_removed':
                this.showToast('Member Removed', \`Client removed from group\`, 'info');
                break;
                
            case 'task_assigned':
                this.showToast('Task Assigned', \`Task '\${data.task.type}' assigned to group\`, 'success');
                break;
                
            case 'task_completed':
                this.showToast('Task Completed', \`Group task completed\`, 'success');
                break;
                
            case 'client_moved':
                this.showToast('Member Moved', \`Client moved to different group\`, 'info');
                break;
                
            default:
                console.log('Unknown groups update type:', data.type);
        }
    },
    
    loadGroupsData(groups) {
        console.log('Loading groups data:', groups);
        
        // The GroupVisualization component will handle rendering
        // We just update our local state here
        this.ui.groups = groups || [];
        
        // Update group count in status bar if element exists
        const groupCountElement = document.getElementById('groups-count');
        if (groupCountElement) {
            groupCountElement.textContent = this.ui.groups.length;
        }
        
        // Trigger custom event for GroupVisualization to handle
        document.dispatchEvent(new CustomEvent('groupsDataLoaded', {
            detail: { groups: this.ui.groups }
        }));
    },
    
    sendGroupsMessage(messageType, data = {}) {
        if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(JSON.stringify({
                type: messageType,
                ...data
            }));
        } else {
            console.warn('WebSocket not available for groups message:', messageType);
        }
    },
    
    // API methods for groups (fallback to HTTP if WebSocket unavailable)
    async createGroup(name, options) {
        try {
            const response = await this.apiRequest('/api/groups', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, options })
            });
            return response;
        } catch (error) {
            console.error('Failed to create group:', error);
            throw error;
        }
    },
    
    async deleteGroup(groupId) {
        try {
            const response = await this.apiRequest(\`/api/groups/\${groupId}\`, {
                method: 'DELETE'
            });
            return response;
        } catch (error) {
            console.error('Failed to delete group:', error);
            throw error;
        }
    },
    
    async assignClientToGroup(clientId, groupId) {
        try {
            const response = await this.apiRequest(\`/api/groups/\${groupId}/clients\`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ clientId })
            });
            return response;
        } catch (error) {
            console.error('Failed to assign client to group:', error);
            throw error;
        }
    },
    
    async assignTaskToGroup(groupId, taskConfig) {
        try {
            const response = await this.apiRequest(\`/api/groups/\${groupId}/tasks\`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ taskConfig })
            });
            return response;
        } catch (error) {
            console.error('Failed to assign task to group:', error);
            throw error;
        }
    }
};

// Initialize when DOM is loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        AppyProxUI.init();
    });
} else {
    AppyProxUI.init();
}

// Make AppyProxUI globally available for debugging
window.AppyProxUI = AppyProxUI;`;
  }

  async initializeMinecraftSystems() {
    this.logger.info('Initializing Minecraft rendering systems...');
    
    try {
      // Initialize texture manager
      this.textureManager = new TextureManager(this.logger);
      await this.textureManager.initialize();
      
      // Initialize TPS manager
      this.tpsManager = new TPSManager(this.logger, this.components.proxyServer);
      await this.tpsManager.initialize();
      
      // Initialize chunk renderer
      this.chunkRenderer = new ChunkRenderer(this.logger, this.textureManager);
      await this.chunkRenderer.initialize();
      
      // Setup texture routes
      this.textureManager.setupRoutes(this.app);
      
      // Setup TPS event listeners
      this.setupTPSEventListeners();
      
      // Setup chunk event listeners
      this.setupChunkEventListeners();
      
      // Initialize player visualization system
      this.playerVisualization = new PlayerVisualization(this.textureManager, this.logger);
      this.setupPlayerVisualizationEvents();
      
      // Initialize groups visualization if available
      if (this.groupsIntegration) {
        this.groupsVisualization = this.groupsIntegration.getGroupVisualization();
        this.logger.info('Groups visualization system integrated');
      }
      
      // Initialize command terminal
      this.commandTerminal = new CommandTerminal(this, this.groupsIntegration, this.logger);
      this.logger.info('Command terminal system initialized');
      
      this.logger.info('Minecraft rendering systems initialized');
      
    } catch (error) {
      this.logger.error('Failed to initialize Minecraft systems:', error);
      throw error;
    }
  }
  
  setupTPSEventListeners() {
    if (!this.tpsManager) return;
    
    // Listen for TPS updates
    this.tpsManager.on('tps_update', (data) => {
      // Broadcast TPS updates to clients at high frequency
      this.broadcastToClients({
        type: 'tps_update',
        data: {
          serverAddress: data.serverAddress,
          currentTPS: data.currentTPS,
          averageTPS: data.averageTPS,
          updateRate: this.tpsManager.currentUpdateRate
        }
      });
    });
    
    // Listen for periodic updates (20 TPS)
    this.tpsManager.on('periodic_update', (data) => {
      // Update UI state
      this.uiState.systemStats.currentTPS = data.tps;
      this.uiState.systemStats.updateRate = data.updateRate;
      
      // Broadcast to clients
      this.broadcastToClients({
        type: 'system_update',
        data: {
          tps: data.tps,
          updateRate: data.updateRate,
          performance: data.performance
        }
      });
    });
    
    // Listen for lag spikes
    this.tpsManager.on('lag_spike', (data) => {
      this.broadcastToClients({
        type: 'lag_spike',
        data: {
          serverAddress: data.serverAddress,
          delay: data.delay,
          tps: data.tps
        }
      });
    });
  }
  
  setupChunkEventListeners() {
    if (!this.chunkRenderer) return;
    
    // Listen for chunk loading
    this.chunkRenderer.on('chunk_loaded', (data) => {
      this.broadcastToClients({
        type: 'chunk_loaded',
        data
      });
    });
    
    // Listen for chunk rendering
    this.chunkRenderer.on('chunk_rendered', (data) => {
      this.broadcastToClients({
        type: 'chunk_rendered',
        data: {
          chunkId: data.chunkId,
          x: data.chunk.x,
          z: data.chunk.z,
          biome: data.chunk.biome
        }
      });
    });
  }
  
  setupPlayerVisualizationEvents() {
    if (!this.playerVisualization) return;
    
    // Set up event handlers for player visualization
    this.playerVisualization.onFollowPlayer = () => {
      this.broadcastToClients({
        type: 'follow_player_request'
      });
    };
    
    this.playerVisualization.onSortInventory = () => {
      this.broadcastToClients({
        type: 'sort_inventory_request'
      });
    };
    
    this.playerVisualization.onExportInventory = () => {
      this.broadcastToClients({
        type: 'export_inventory_request'
      });
    };
  }
  
  getStatus() {
    const baseStats = {
      running: this.isRunning,
      port: this.port,
      host: this.host,
      connectedClients: this.wsClients.size,
      players: this.uiState.mapData.players.size,
      tasks: this.uiState.tasks.size,
      clusters: this.uiState.clusters.size
    };
    
    // Add Minecraft system stats if available
    if (this.tpsManager) {
      baseStats.tps = this.tpsManager.getPerformanceMetrics();
    }
    
    if (this.chunkRenderer) {
      baseStats.chunks = this.chunkRenderer.getStats();
    }
    
    return baseStats;
  }
}

module.exports = WebUIServer;