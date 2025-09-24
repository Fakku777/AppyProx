const EventEmitter = require('events');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');

/**
 * Enhanced Group Manager - Replaces ClusterManager with modern group functionality
 * Features: drag-drop, color coding, collapse/expand, looping tasks, and visual organization
 */
class GroupManager extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('GroupManager') : logger;
    
    // Data structures
    this.clients = new Map(); // clientId -> client info
    this.groups = new Map(); // groupId -> group info
    this.clientToGroup = new Map(); // clientId -> groupId
    
    // Group colors and themes
    this.groupColors = [
      '#10b981', '#3b82f6', '#8b5cf6', '#f59e0b', 
      '#ef4444', '#06b6d4', '#84cc16', '#f97316',
      '#ec4899', '#6366f1', '#14b8a6', '#eab308'
    ];
    this.usedColors = new Set();
    
    // Configuration paths
    this.groupsConfigPath = path.join(__dirname, '../../configs/groups.json');
    this.accountsConfigPath = path.join(__dirname, '../../configs/accounts.json');
    
    // UI State
    this.collapsedGroups = new Set(); // Groups that are collapsed in UI
    this.groupPositions = new Map(); // groupId -> {x, y} for UI positioning
    
    // Task management
    this.groupTasks = new Map(); // groupId -> active tasks
    this.loopingTasks = new Map(); // groupId -> looping task configuration
    
    // Status tracking
    this.isRunning = false;
    this.healthCheckInterval = null;
    this.syncInterval = null;
    this.taskCheckInterval = null;
    
    // Load persisted configurations
    this.loadGroupsConfig();
    this.loadAccountsConfig();
  }

  async start() {
    if (this.isRunning) return;

    this.logger.info('Starting group manager...');
    
    // Start health check interval
    this.healthCheckInterval = setInterval(() => {
      this.performHealthCheck();
    }, this.config.health_check_interval || 30000);
    
    // Start sync interval for group coordination
    this.syncInterval = setInterval(() => {
      this.syncGroupStates();
    }, this.config.sync_interval || 10000);
    
    // Start task management interval
    this.taskCheckInterval = setInterval(() => {
      this.processGroupTasks();
    }, this.config.task_check_interval || 5000);
    
    this.isRunning = true;
    this.logger.info('Group manager started');
  }

  async stop() {
    if (!this.isRunning) return;

    this.logger.info('Stopping group manager...');
    
    if (this.healthCheckInterval) {
      clearInterval(this.healthCheckInterval);
      this.healthCheckInterval = null;
    }
    
    if (this.syncInterval) {
      clearInterval(this.syncInterval);
      this.syncInterval = null;
    }
    
    if (this.taskCheckInterval) {
      clearInterval(this.taskCheckInterval);
      this.taskCheckInterval = null;
    }
    
    // Save current state
    this.saveGroupsConfig();
    
    this.isRunning = false;
    this.logger.info('Group manager stopped');
  }

  // Client Management
  registerClient(clientInfo) {
    this.clients.set(clientInfo.id, {
      ...clientInfo,
      status: 'connected',
      lastSeen: Date.now(),
      health: {
        ping: 0,
        tps: 20,
        health: 20,
        food: 20,
        position: { x: 0, y: 64, z: 0 }
      }
    });

    this.logger.info(`Registered client: ${clientInfo.username} (${clientInfo.id})`);
    
    // Auto-assign to group if configured
    this.autoAssignToGroup(clientInfo.id);
    
    this.emit('client_registered', clientInfo);
  }

  unregisterClient(clientInfo) {
    const client = this.clients.get(clientInfo.id);
    if (!client) return;

    // Remove from group
    const groupId = this.clientToGroup.get(clientInfo.id);
    if (groupId) {
      this.removeClientFromGroup(clientInfo.id, groupId);
    }

    this.clients.delete(clientInfo.id);
    this.logger.info(`Unregistered client: ${clientInfo.username} (${clientInfo.id})`);
    
    this.emit('client_unregistered', clientInfo);
  }

  // Group Management
  createGroup(groupName, options = {}) {
    const groupId = uuidv4();
    const color = this.getNextAvailableColor();
    
    const group = {
      id: groupId,
      name: groupName,
      created: Date.now(),
      maxSize: options.maxSize || this.config.max_accounts_per_group || 10,
      color: options.color || color,
      icon: options.icon || 'users',
      autoReconnect: options.autoReconnect !== false,
      reconnectInterval: options.reconnectInterval || 30000,
      members: [],
      leader: null,
      status: 'idle',
      currentTasks: [],
      loopingTask: null,
      
      // UI Properties
      collapsed: false,
      position: options.position || { x: 0, y: 0 },
      
      // Group Settings
      settings: {
        followLeader: options.followLeader || false,
        shareInventory: options.shareInventory || false,
        syncMovement: options.syncMovement || false,
        autoRespawn: options.autoRespawn || true,
        chatRelay: options.chatRelay || false,
        ...options.settings
      },
      
      // Statistics
      stats: {
        totalOnlineTime: 0,
        tasksCompleted: 0,
        itemsGathered: 0,
        blocksPlaced: 0,
        distanceTraveled: 0
      }
    };

    this.groups.set(groupId, group);
    this.usedColors.add(color);
    this.logger.info(`Created group: ${groupName} (${groupId}) with color ${color}`);
    
    this.saveGroupsConfig();
    this.emit('group_created', group);
    
    return groupId;
  }

  deleteGroup(groupId) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    // Remove all members from group
    [...group.members].forEach(clientId => {
      this.removeClientFromGroup(clientId, groupId);
    });

    // Free up color
    this.usedColors.delete(group.color);
    
    // Remove from collapsed set
    this.collapsedGroups.delete(groupId);
    
    // Remove position
    this.groupPositions.delete(groupId);
    
    // Cancel any looping tasks
    this.cancelLoopingTask(groupId);
    
    this.groups.delete(groupId);
    this.logger.info(`Deleted group: ${group.name} (${groupId})`);
    
    this.saveGroupsConfig();
    this.emit('group_deleted', { groupId, groupName: group.name });
    
    return true;
  }

  assignClientToGroup(clientId, groupIdOrName) {
    const client = this.clients.get(clientId);
    if (!client) {
      this.logger.warn(`Cannot assign non-existent client ${clientId} to group`);
      return false;
    }

    // Find group by ID or name
    let group = this.groups.get(groupIdOrName);
    if (!group) {
      group = Array.from(this.groups.values()).find(g => g.name === groupIdOrName);
    }

    if (!group) {
      this.logger.warn(`Group not found: ${groupIdOrName}`);
      return false;
    }

    // Check if group is full
    if (group.members.length >= group.maxSize) {
      this.logger.warn(`Group ${group.name} is full (${group.members.length}/${group.maxSize})`);
      return false;
    }

    // Remove from current group if assigned
    const currentGroupId = this.clientToGroup.get(clientId);
    if (currentGroupId) {
      this.removeClientFromGroup(clientId, currentGroupId);
    }

    // Add to new group
    group.members.push(clientId);
    this.clientToGroup.set(clientId, group.id);

    // Set as leader if first member
    if (group.members.length === 1) {
      group.leader = clientId;
    }

    this.logger.info(`Assigned ${client.username} to group ${group.name}`);
    
    this.saveGroupsConfig();
    this.emit('client_assigned_to_group', { clientId, groupId: group.id, group });
    this.emit('group_update', group);
    
    return true;
  }

  removeClientFromGroup(clientId, groupId) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    const memberIndex = group.members.indexOf(clientId);
    if (memberIndex === -1) return false;

    group.members.splice(memberIndex, 1);
    this.clientToGroup.delete(clientId);

    // Reassign leader if necessary
    if (group.leader === clientId && group.members.length > 0) {
      group.leader = group.members[0];
      this.logger.info(`New leader for group ${group.name}: ${this.clients.get(group.leader)?.username}`);
    } else if (group.members.length === 0) {
      group.leader = null;
      group.status = 'idle';
    }

    const client = this.clients.get(clientId);
    this.logger.info(`Removed ${client?.username || clientId} from group ${group.name}`);
    
    this.saveGroupsConfig();
    this.emit('client_removed_from_group', { clientId, groupId, group });
    this.emit('group_update', group);
    
    return true;
  }

  // Drag and Drop Support
  moveClientToGroup(clientId, targetGroupId, position = null) {
    const sourceGroupId = this.clientToGroup.get(clientId);
    
    if (sourceGroupId === targetGroupId) {
      // Same group - just reorder if position is specified
      if (position !== null) {
        this.reorderGroupMember(targetGroupId, clientId, position);
      }
      return true;
    }

    // Move to different group
    return this.assignClientToGroup(clientId, targetGroupId);
  }

  reorderGroupMember(groupId, clientId, newPosition) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    const currentIndex = group.members.indexOf(clientId);
    if (currentIndex === -1) return false;

    // Remove from current position
    group.members.splice(currentIndex, 1);
    
    // Insert at new position
    const insertIndex = Math.min(newPosition, group.members.length);
    group.members.splice(insertIndex, 0, clientId);

    this.emit('group_member_reordered', { groupId, clientId, newPosition: insertIndex });
    this.emit('group_update', group);
    
    return true;
  }

  // UI State Management
  toggleGroupCollapse(groupId) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    if (this.collapsedGroups.has(groupId)) {
      this.collapsedGroups.delete(groupId);
      group.collapsed = false;
    } else {
      this.collapsedGroups.add(groupId);
      group.collapsed = true;
    }

    this.emit('group_collapsed_changed', { groupId, collapsed: group.collapsed });
    return group.collapsed;
  }

  setGroupPosition(groupId, position) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    group.position = { ...position };
    this.groupPositions.set(groupId, position);
    
    this.emit('group_position_changed', { groupId, position });
    return true;
  }

  updateGroupColor(groupId, newColor) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    const oldColor = group.color;
    group.color = newColor;
    
    // Update used colors tracking
    this.usedColors.delete(oldColor);
    this.usedColors.add(newColor);

    this.saveGroupsConfig();
    this.emit('group_color_changed', { groupId, oldColor, newColor });
    
    return true;
  }

  updateGroupIcon(groupId, newIcon) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    group.icon = newIcon;
    
    this.saveGroupsConfig();
    this.emit('group_icon_changed', { groupId, newIcon });
    
    return true;
  }

  // Task Management with Looping Support
  assignTaskToGroup(groupId, taskConfig) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    const taskId = uuidv4();
    const task = {
      id: taskId,
      groupId: groupId,
      type: taskConfig.type,
      parameters: taskConfig.parameters,
      priority: taskConfig.priority || 'normal',
      created: Date.now(),
      status: 'pending',
      progress: 0,
      assignedMembers: taskConfig.assignedMembers || group.members.slice(),
      isLooping: taskConfig.isLooping || false,
      loopCount: 0,
      maxLoops: taskConfig.maxLoops || 0, // 0 = infinite
      loopDelay: taskConfig.loopDelay || 5000
    };

    group.currentTasks.push(task);
    
    if (task.isLooping) {
      this.loopingTasks.set(groupId, task);
    }

    this.logger.info(`Assigned ${task.isLooping ? 'looping ' : ''}task ${task.type} to group ${group.name}`);
    
    this.emit('task_assigned_to_group', { groupId, task });
    this.emit('group_update', group);
    
    return taskId;
  }

  cancelGroupTask(groupId, taskId) {
    const group = this.groups.get(groupId);
    if (!group) return false;

    const taskIndex = group.currentTasks.findIndex(t => t.id === taskId);
    if (taskIndex === -1) return false;

    const task = group.currentTasks[taskIndex];
    group.currentTasks.splice(taskIndex, 1);

    // Remove from looping tasks if applicable
    if (task.isLooping && this.loopingTasks.get(groupId)?.id === taskId) {
      this.loopingTasks.delete(groupId);
    }

    this.logger.info(`Cancelled task ${task.type} for group ${group.name}`);
    
    this.emit('task_cancelled', { groupId, taskId, task });
    this.emit('group_update', group);
    
    return true;
  }

  setLoopingTask(groupId, taskConfig) {
    taskConfig.isLooping = true;
    return this.assignTaskToGroup(groupId, taskConfig);
  }

  cancelLoopingTask(groupId) {
    const loopingTask = this.loopingTasks.get(groupId);
    if (!loopingTask) return false;

    return this.cancelGroupTask(groupId, loopingTask.id);
  }

  // Automated Task Processing
  processGroupTasks() {
    for (const [groupId, group] of this.groups.entries()) {
      // Process regular tasks
      group.currentTasks.forEach(task => {
        if (task.status === 'pending') {
          this.startTask(task);
        }
      });

      // Handle looping tasks
      const loopingTask = this.loopingTasks.get(groupId);
      if (loopingTask && loopingTask.status === 'completed') {
        this.handleLoopingTaskCompletion(loopingTask);
      }
    }
  }

  startTask(task) {
    task.status = 'running';
    task.startTime = Date.now();
    
    this.emit('task_started', task);
  }

  completeTask(task) {
    task.status = 'completed';
    task.endTime = Date.now();
    task.progress = 100;

    const group = this.groups.get(task.groupId);
    if (group) {
      group.stats.tasksCompleted++;
      
      // Remove completed non-looping tasks
      if (!task.isLooping) {
        const taskIndex = group.currentTasks.findIndex(t => t.id === task.id);
        if (taskIndex !== -1) {
          group.currentTasks.splice(taskIndex, 1);
        }
      }
    }

    this.emit('task_completed', task);
    this.emit('group_update', group);
  }

  handleLoopingTaskCompletion(task) {
    task.loopCount++;
    
    // Check if we should continue looping
    if (task.maxLoops === 0 || task.loopCount < task.maxLoops) {
      // Schedule next loop
      setTimeout(() => {
        if (this.loopingTasks.has(task.groupId)) {
          task.status = 'pending';
          task.progress = 0;
          this.emit('looping_task_restarted', task);
        }
      }, task.loopDelay);
    } else {
      // Max loops reached
      this.cancelLoopingTask(task.groupId);
      this.emit('looping_task_finished', task);
    }
  }

  // Health and Sync Management
  performHealthCheck() {
    const now = Date.now();
    
    for (const [clientId, client] of this.clients.entries()) {
      if (now - client.lastSeen > 60000) { // 1 minute timeout
        client.status = 'disconnected';
        this.emit('client_health_changed', { clientId, status: 'disconnected' });
      }
    }

    // Check group health
    for (const [groupId, group] of this.groups.entries()) {
      const onlineMembers = group.members.filter(memberId => {
        const client = this.clients.get(memberId);
        return client && client.status === 'connected';
      });

      if (onlineMembers.length !== group.members.length) {
        this.emit('group_member_status_changed', { groupId, onlineMembers: onlineMembers.length, totalMembers: group.members.length });
      }
    }
  }

  syncGroupStates() {
    // Sync group coordination (follow leader, sync movement, etc.)
    for (const [groupId, group] of this.groups.entries()) {
      if (group.settings.followLeader && group.leader) {
        this.syncGroupToLeader(group);
      }
    }
  }

  syncGroupToLeader(group) {
    const leader = this.clients.get(group.leader);
    if (!leader || leader.status !== 'connected') return;

    const leaderPosition = leader.health.position;
    if (!leaderPosition) return;

    // Send follow commands to other members
    group.members.forEach(memberId => {
      if (memberId !== group.leader) {
        const member = this.clients.get(memberId);
        if (member && member.status === 'connected') {
          this.emit('follow_leader_command', {
            memberId,
            leaderPosition,
            groupId: group.id
          });
        }
      }
    });
  }

  // Utility Methods
  autoAssignToGroup(clientId) {
    const client = this.clients.get(clientId);
    if (!client) return;

    // Check if client should be auto-assigned based on configuration
    const accountConfig = this.findAccountConfig(client.username);
    if (accountConfig && accountConfig.group) {
      this.assignClientToGroup(clientId, accountConfig.group);
    }
  }

  getNextAvailableColor() {
    for (const color of this.groupColors) {
      if (!this.usedColors.has(color)) {
        return color;
      }
    }
    
    // Generate a random color if all predefined colors are used
    return `#${Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0')}`;
  }

  findAccountConfig(username) {
    // Implementation would load from accounts config
    return null;
  }

  // Configuration Management
  loadGroupsConfig() {
    try {
      if (fs.existsSync(this.groupsConfigPath)) {
        const data = fs.readFileSync(this.groupsConfigPath, 'utf8');
        const config = JSON.parse(data);
        
        // Load groups
        if (config.groups) {
          config.groups.forEach(groupData => {
            this.groups.set(groupData.id, {
              ...groupData,
              members: groupData.members || [],
              currentTasks: groupData.currentTasks || [],
              stats: groupData.stats || { totalOnlineTime: 0, tasksCompleted: 0, itemsGathered: 0, blocksPlaced: 0, distanceTraveled: 0 }
            });
            
            if (groupData.color) {
              this.usedColors.add(groupData.color);
            }
            
            if (groupData.collapsed) {
              this.collapsedGroups.add(groupData.id);
            }
            
            if (groupData.position) {
              this.groupPositions.set(groupData.id, groupData.position);
            }
          });
        }
        
        this.logger.info(`Loaded ${this.groups.size} group configurations`);
      }
    } catch (error) {
      this.logger.warn('Failed to load groups configuration:', error.message);
    }
  }

  loadAccountsConfig() {
    try {
      if (fs.existsSync(this.accountsConfigPath)) {
        const data = fs.readFileSync(this.accountsConfigPath, 'utf8');
        const config = JSON.parse(data);
        
        this.accountsConfig = config.accounts || [];
        this.logger.info(`Loaded configuration for ${this.accountsConfig.length} accounts`);
      }
    } catch (error) {
      this.logger.warn('Failed to load accounts configuration:', error.message);
    }
  }

  saveGroupsConfig() {
    try {
      const groupsArray = Array.from(this.groups.values()).map(group => ({
        ...group,
        collapsed: this.collapsedGroups.has(group.id),
        position: this.groupPositions.get(group.id) || group.position
      }));
      
      const config = {
        groups: groupsArray,
        lastUpdated: new Date().toISOString()
      };
      
      fs.writeFileSync(this.groupsConfigPath, JSON.stringify(config, null, 2));
      this.logger.debug('Saved groups configuration');
    } catch (error) {
      this.logger.error('Failed to save groups configuration:', error);
    }
  }

  // Getters
  getGroup(groupId) {
    return this.groups.get(groupId);
  }

  getGroupByName(name) {
    return Array.from(this.groups.values()).find(g => g.name === name);
  }

  getClientGroup(clientId) {
    const groupId = this.clientToGroup.get(clientId);
    return groupId ? this.groups.get(groupId) : null;
  }

  getAllGroups() {
    return Array.from(this.groups.values());
  }

  getGroupStats(groupId) {
    const group = this.groups.get(groupId);
    if (!group) return null;

    const onlineMembers = group.members.filter(memberId => {
      const client = this.clients.get(memberId);
      return client && client.status === 'connected';
    }).length;

    return {
      ...group.stats,
      totalMembers: group.members.length,
      onlineMembers,
      activeTasks: group.currentTasks.length,
      hasLoopingTask: this.loopingTasks.has(groupId)
    };
  }
}

module.exports = GroupManager;