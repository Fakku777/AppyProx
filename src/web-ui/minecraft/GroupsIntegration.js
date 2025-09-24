/**
 * Groups Integration for WebUI
 * Connects GroupManager and GroupVisualization to WebUIServer
 */

const GroupManager = require('../../groups/GroupManager');
const GroupVisualization = require('./GroupVisualization');

class GroupsIntegration {
  constructor(webUIServer, config, logger) {
    this.webUIServer = webUIServer;
    this.config = config;
    this.logger = logger?.child ? logger.child('GroupsIntegration') : logger;
    
    // Initialize group manager
    this.groupManager = new GroupManager(config, logger);
    
    // Initialize group visualization 
    this.groupVisualization = new GroupVisualization(this.groupManager, logger);
    
    // Set up event handlers
    this.setupEventHandlers();
  }

  async initialize() {
    this.logger.info('Initializing Groups integration...');
    
    // Start group manager
    await this.groupManager.start();
    
    // Register WebSocket handlers
    this.registerWebSocketHandlers();
    
    // Register HTTP endpoints  
    this.registerHttpEndpoints();
    
    this.logger.info('Groups integration initialized');
  }

  async shutdown() {
    this.logger.info('Shutting down Groups integration...');
    
    if (this.groupManager) {
      await this.groupManager.stop();
    }
    
    this.logger.info('Groups integration shut down');
  }

  setupEventHandlers() {
    // Forward GroupManager events to WebUI clients
    this.groupManager.on('group_created', (group) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'group_created',
        group
      });
    });

    this.groupManager.on('group_deleted', (data) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'group_deleted',
        groupId: data.groupId,
        groupName: data.groupName
      });
    });

    this.groupManager.on('group_update', (group) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'group_updated',
        group
      });
    });

    this.groupManager.on('client_assigned_to_group', (data) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'client_assigned',
        clientId: data.clientId,
        groupId: data.groupId,
        group: data.group
      });
    });

    this.groupManager.on('client_removed_from_group', (data) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'client_removed',
        clientId: data.clientId,
        groupId: data.groupId,
        group: data.group
      });
    });

    this.groupManager.on('task_assigned_to_group', (data) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'task_assigned',
        groupId: data.groupId,
        task: data.task
      });
    });

    this.groupManager.on('task_completed', (task) => {
      this.webUIServer.broadcast('groups_updated', {
        type: 'task_completed',
        groupId: task.groupId,
        taskId: task.id
      });
    });

    // Handle GroupVisualization events
    this.groupVisualization.setEventHandler((eventName, data) => {
      this.handleGroupVisualizationEvent(eventName, data);
    });
  }

  handleGroupVisualizationEvent(eventName, data) {
    switch (eventName) {
      case 'group_created':
        // Group creation is handled through GroupManager
        break;
        
      case 'task_assigned':
        // Task assignment is handled through GroupManager
        break;
        
      case 'client_moved':
        this.webUIServer.broadcast('groups_updated', {
          type: 'client_moved',
          clientId: data.clientId,
          targetGroupId: data.targetGroupId
        });
        break;
        
      default:
        this.logger.debug(`Unhandled GroupVisualization event: ${eventName}`, data);
    }
  }

  registerWebSocketHandlers() {
    const wsServer = this.webUIServer.wsServer;
    
    if (!wsServer) {
      this.logger.warn('WebSocket server not available for Groups integration');
      return;
    }

    // Handle incoming WebSocket messages for groups
    wsServer.on('connection', (ws) => {
      const originalHandler = ws.on.bind(ws);
      
      ws.on = function(event, handler) {
        if (event === 'message') {
          const wrappedHandler = (message) => {
            try {
              const data = JSON.parse(message);
              
              // Handle groups-specific messages
              if (data.type && data.type.startsWith('groups_')) {
                this.handleWebSocketGroupsMessage(ws, data);
                return;
              }
            } catch (error) {
              // Not JSON or not a groups message, pass through
            }
            
            // Call original handler
            handler(message);
          };
          
          return originalHandler('message', wrappedHandler);
        }
        
        return originalHandler(event, handler);
      }.bind(this);
    });
  }

  handleWebSocketGroupsMessage(ws, data) {
    switch (data.type) {
      case 'groups_get_all':
        ws.send(JSON.stringify({
          type: 'groups_data',
          groups: this.groupManager.getAllGroups()
        }));
        break;
        
      case 'groups_create':
        const groupId = this.groupManager.createGroup(data.name, data.options);
        ws.send(JSON.stringify({
          type: 'groups_created',
          groupId,
          success: !!groupId
        }));
        break;
        
      case 'groups_delete':
        const deleted = this.groupManager.deleteGroup(data.groupId);
        ws.send(JSON.stringify({
          type: 'groups_deleted',
          groupId: data.groupId,
          success: deleted
        }));
        break;
        
      case 'groups_assign_client':
        const assigned = this.groupManager.assignClientToGroup(data.clientId, data.groupId);
        ws.send(JSON.stringify({
          type: 'groups_client_assigned',
          clientId: data.clientId,
          groupId: data.groupId,
          success: assigned
        }));
        break;
        
      case 'groups_remove_client':
        const removed = this.groupManager.removeClientFromGroup(data.clientId, data.groupId);
        ws.send(JSON.stringify({
          type: 'groups_client_removed',
          clientId: data.clientId,
          groupId: data.groupId,
          success: removed
        }));
        break;
        
      case 'groups_move_client':
        const moved = this.groupManager.moveClientToGroup(data.clientId, data.targetGroupId, data.position);
        ws.send(JSON.stringify({
          type: 'groups_client_moved',
          clientId: data.clientId,
          targetGroupId: data.targetGroupId,
          success: moved
        }));
        break;
        
      case 'groups_assign_task':
        const taskId = this.groupManager.assignTaskToGroup(data.groupId, data.taskConfig);
        ws.send(JSON.stringify({
          type: 'groups_task_assigned',
          groupId: data.groupId,
          taskId,
          success: !!taskId
        }));
        break;
        
      case 'groups_cancel_task':
        const cancelled = this.groupManager.cancelGroupTask(data.groupId, data.taskId);
        ws.send(JSON.stringify({
          type: 'groups_task_cancelled',
          groupId: data.groupId,
          taskId: data.taskId,
          success: cancelled
        }));
        break;
        
      case 'groups_toggle_collapse':
        const collapsed = this.groupManager.toggleGroupCollapse(data.groupId);
        ws.send(JSON.stringify({
          type: 'groups_collapse_toggled',
          groupId: data.groupId,
          collapsed,
          success: collapsed !== undefined
        }));
        break;
        
      default:
        this.logger.warn(`Unknown groups WebSocket message type: ${data.type}`);
        ws.send(JSON.stringify({
          type: 'groups_error',
          message: `Unknown message type: ${data.type}`
        }));
    }
  }

  registerHttpEndpoints() {
    const app = this.webUIServer.app;
    
    if (!app) {
      this.logger.warn('Express app not available for Groups HTTP endpoints');
      return;
    }

    // REST API endpoints for groups management
    app.get('/api/groups', (req, res) => {
      try {
        const groups = this.groupManager.getAllGroups();
        res.json({ success: true, groups });
      } catch (error) {
        this.logger.error('Error getting groups:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.post('/api/groups', (req, res) => {
      try {
        const { name, options } = req.body;
        const groupId = this.groupManager.createGroup(name, options);
        res.json({ success: true, groupId });
      } catch (error) {
        this.logger.error('Error creating group:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.delete('/api/groups/:groupId', (req, res) => {
      try {
        const { groupId } = req.params;
        const success = this.groupManager.deleteGroup(groupId);
        res.json({ success });
      } catch (error) {
        this.logger.error('Error deleting group:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.post('/api/groups/:groupId/clients', (req, res) => {
      try {
        const { groupId } = req.params;
        const { clientId } = req.body;
        const success = this.groupManager.assignClientToGroup(clientId, groupId);
        res.json({ success });
      } catch (error) {
        this.logger.error('Error assigning client to group:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.delete('/api/groups/:groupId/clients/:clientId', (req, res) => {
      try {
        const { groupId, clientId } = req.params;
        const success = this.groupManager.removeClientFromGroup(clientId, groupId);
        res.json({ success });
      } catch (error) {
        this.logger.error('Error removing client from group:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.post('/api/groups/:groupId/tasks', (req, res) => {
      try {
        const { groupId } = req.params;
        const { taskConfig } = req.body;
        const taskId = this.groupManager.assignTaskToGroup(groupId, taskConfig);
        res.json({ success: !!taskId, taskId });
      } catch (error) {
        this.logger.error('Error assigning task to group:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.delete('/api/groups/:groupId/tasks/:taskId', (req, res) => {
      try {
        const { groupId, taskId } = req.params;
        const success = this.groupManager.cancelGroupTask(groupId, taskId);
        res.json({ success });
      } catch (error) {
        this.logger.error('Error cancelling group task:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    app.patch('/api/groups/:groupId/collapse', (req, res) => {
      try {
        const { groupId } = req.params;
        const collapsed = this.groupManager.toggleGroupCollapse(groupId);
        res.json({ success: collapsed !== undefined, collapsed });
      } catch (error) {
        this.logger.error('Error toggling group collapse:', error);
        res.status(500).json({ success: false, error: error.message });
      }
    });

    this.logger.info('Registered Groups HTTP endpoints');
  }

  // Public interface for other components
  getGroupManager() {
    return this.groupManager;
  }

  getGroupVisualization() {
    return this.groupVisualization;
  }

  // Handle client registration from ProxyServer
  handleClientRegistration(clientInfo) {
    this.groupManager.registerClient(clientInfo);
  }

  // Handle client disconnection from ProxyServer  
  handleClientDisconnection(clientInfo) {
    this.groupManager.unregisterClient(clientInfo);
  }

  // Update client health from external sources
  updateClientHealth(clientId, healthData) {
    const client = this.groupManager.clients.get(clientId);
    if (client) {
      client.health = { ...client.health, ...healthData };
      client.lastSeen = Date.now();
      
      if (client.status === 'disconnected') {
        client.status = 'connected';
        this.groupManager.emit('client_health_changed', { clientId, status: 'connected' });
      }
    }
  }

  // Get all groups for external access
  getAllGroups() {
    return this.groupManager.getAllGroups();
  }

  // Get group by ID for external access
  getGroup(groupId) {
    return this.groupManager.groups.get(groupId);
  }
}

module.exports = GroupsIntegration;