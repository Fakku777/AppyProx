/**
 * Terminal Command Implementations
 * Contains all command handlers for the AppyProx Command Terminal
 */

// Extend CommandTerminal with command implementations
const TerminalCommands = {
  // System commands
  helpCommand(args) {
    if (args.length > 0) {
      // Help for specific command
      const commandName = args[0].toLowerCase();
      const command = this.commands.get(commandName) || this.commands.get(this.aliases.get(commandName));
      
      if (command) {
        this.writeOutput(`Command: ${command.name}`, 'info');
        this.writeOutput(`Description: ${command.description}`, 'info');
        this.writeOutput(`Usage: ${command.usage}`, 'info');
        if (command.adminOnly) this.writeOutput('⚠️  Admin only command', 'warning');
        if (command.groupRequired) this.writeOutput('ℹ️  Requires group selection', 'info');
      } else {
        this.writeOutput(`Command not found: ${commandName}`, 'error');
      }
    } else {
      // General help
      this.writeOutput('Available Commands:', 'success');
      this.writeOutput('', 'info');
      
      const categories = {};
      for (const [name, cmd] of this.commands.entries()) {
        if (!categories[cmd.category]) categories[cmd.category] = [];
        categories[cmd.category].push(cmd);
      }
      
      Object.entries(categories).forEach(([category, commands]) => {
        this.writeOutput(`${category.toUpperCase()}:`, 'info');
        commands.forEach(cmd => {
          const prefix = cmd.adminOnly ? '🔒 ' : cmd.groupRequired ? '👥 ' : '';
          this.writeOutput(`  ${prefix}${cmd.name.padEnd(12)} - ${cmd.description}`, 'info');
        });
        this.writeOutput('', 'info');
      });
      
      this.writeOutput('Aliases: ls (groups), ps (clients), top (monitor), cls (clear)', 'info');
      this.writeOutput('Use "help <command>" for detailed information', 'info');
    }
  },

  clearCommand() {
    this.clearTerminal();
  },

  historyCommand() {
    if (this.commandHistory.length === 0) {
      this.writeOutput('No command history', 'info');
      return;
    }
    
    this.writeOutput('Command History:', 'success');
    this.commandHistory.forEach((cmd, index) => {
      this.writeOutput(`  ${(index + 1).toString().padStart(3)}: ${cmd}`, 'info');
    });
  },

  statusCommand() {
    this.writeOutput('=== AppyProx System Status ===', 'success');
    
    if (this.groupsIntegration) {
      const groups = this.groupsIntegration.getAllGroups();
      const groupManager = this.groupsIntegration.getGroupManager();
      
      this.writeOutput(`Groups: ${groups.length} total, ${groups.filter(g => g.members.length > 0).length} active`, 'info');
      this.writeOutput(`Clients: ${groupManager.clients.size} connected`, 'info');
      
      const totalTasks = groups.reduce((sum, g) => sum + (g.currentTasks?.length || 0), 0);
      this.writeOutput(`Tasks: ${totalTasks} active`, 'info');
      
      const loopingTasks = groups.filter(g => g.loopingTask).length;
      if (loopingTasks > 0) {
        this.writeOutput(`Looping Tasks: ${loopingTasks} running`, 'info');
      }
    }
    
    this.writeOutput(`Terminal: Monitor ${this.resourceMonitor.enabled ? 'ON' : 'OFF'}`, 'info');
    this.writeOutput(`Selected Group: ${this.selectedGroup ? this.selectedGroup.name : 'None'}`, 'info');
    this.writeOutput(`Commands Executed: ${document.getElementById('commands-executed')?.textContent || '0'}`, 'info');
  },

  versionCommand() {
    this.writeOutput('AppyProx Command Terminal v2.0', 'success');
    this.writeOutput('Built for AppyProx Minecraft Proxy System', 'info');
    this.writeOutput('Features: Autocomplete, Group Management, Resource Monitoring', 'info');
  },

  // Group commands
  groupsCommand(args) {
    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const groups = this.groupsIntegration.getAllGroups();
    if (groups.length === 0) {
      this.writeOutput('No groups found', 'info');
      return;
    }

    this.writeOutput('=== Groups ===', 'success');
    this.writeOutput('ID'.padEnd(36) + ' | Name'.padEnd(20) + ' | Members | Color   | Status', 'info');
    this.writeOutput('-'.repeat(80), 'info');

    groups.forEach(group => {
      const id = group.id.substring(0, 8) + '...';
      const name = group.name.padEnd(15);
      const members = `${group.members.length}/${group.maxSize}`.padEnd(7);
      const color = group.color.padEnd(7);
      const status = group.members.length > 0 ? 'ACTIVE' : 'IDLE';
      
      this.writeOutput(`${id.padEnd(11)} | ${name} | ${members} | ${color} | ${status}`, 'info');
      
      if (group.currentTasks && group.currentTasks.length > 0) {
        this.writeOutput(`    └─ Tasks: ${group.currentTasks.length} active`, 'info');
      }
    });
  },

  groupCommand(args) {
    if (!args.length) {
      this.writeOutput('Usage: group <group-id> [action]', 'error');
      this.writeOutput('Actions: info, tasks, members', 'info');
      return;
    }

    const groupId = args[0];
    const action = args[1] || 'info';
    
    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const group = this.groupsIntegration.getGroup(groupId);
    if (!group) {
      this.writeOutput(`Group not found: ${groupId}`, 'error');
      return;
    }

    switch (action.toLowerCase()) {
      case 'info':
        this.showGroupInfo(group);
        break;
      case 'tasks':
        this.showGroupTasks(group);
        break;
      case 'members':
        this.showGroupMembers(group);
        break;
      default:
        this.writeOutput(`Unknown action: ${action}`, 'error');
    }
  },

  showGroupInfo(group) {
    this.writeOutput(`=== Group: ${group.name} ===`, 'success');
    this.writeOutput(`ID: ${group.id}`, 'info');
    this.writeOutput(`Created: ${new Date(group.created).toLocaleString()}`, 'info');
    this.writeOutput(`Size: ${group.members.length}/${group.maxSize}`, 'info');
    this.writeOutput(`Color: ${group.color}`, 'info');
    this.writeOutput(`Icon: ${group.icon}`, 'info');
    this.writeOutput(`Leader: ${group.leader ? group.leader.substring(0, 8) + '...' : 'None'}`, 'info');
    this.writeOutput(`Status: ${group.status}`, 'info');
    
    this.writeOutput('Settings:', 'info');
    Object.entries(group.settings).forEach(([key, value]) => {
      this.writeOutput(`  ${key}: ${value}`, 'info');
    });
  },

  showGroupTasks(group) {
    this.writeOutput(`=== Tasks for ${group.name} ===`, 'success');
    
    if (!group.currentTasks || group.currentTasks.length === 0) {
      this.writeOutput('No active tasks', 'info');
      return;
    }

    group.currentTasks.forEach(task => {
      this.writeOutput(`Task: ${task.type} (${task.id.substring(0, 8)}...)`, 'info');
      this.writeOutput(`  Status: ${task.status}`, 'info');
      this.writeOutput(`  Progress: ${task.progress}%`, 'info');
      this.writeOutput(`  Created: ${new Date(task.created).toLocaleString()}`, 'info');
      if (task.isLooping) {
        this.writeOutput(`  Looping: ${task.loopCount}/${task.maxLoops || '∞'}`, 'info');
      }
    });

    if (group.loopingTask) {
      this.writeOutput(`Looping Task: ${group.loopingTask.type}`, 'success');
    }
  },

  showGroupMembers(group) {
    this.writeOutput(`=== Members of ${group.name} ===`, 'success');
    
    if (group.members.length === 0) {
      this.writeOutput('No members', 'info');
      return;
    }

    const groupManager = this.groupsIntegration.getGroupManager();
    group.members.forEach(clientId => {
      const client = groupManager.clients.get(clientId);
      const name = client ? client.username : 'Unknown';
      const status = client ? client.status : 'offline';
      const isLeader = group.leader === clientId;
      
      this.writeOutput(`${name} (${clientId.substring(0, 8)}...) - ${status}${isLeader ? ' 👑' : ''}`, 'info');
    });
  },

  selectCommand(args) {
    if (!args.length) {
      this.writeOutput('Usage: select <group-id>', 'error');
      this.writeOutput('Use "groups" to list available groups', 'info');
      return;
    }

    const groupId = args[0];
    
    if (this.selectGroup(groupId)) {
      this.writeOutput(`Selected group: ${this.selectedGroup.name}`, 'success');
    } else {
      this.writeOutput(`Failed to select group: ${groupId}`, 'error');
    }
  },

  createGroupCommand(args) {
    if (!args.length) {
      this.writeOutput('Usage: create-group <name> [maxSize] [color] [icon]', 'error');
      return;
    }

    const name = args[0];
    const maxSize = parseInt(args[1]) || 10;
    const color = args[2] || '#10b981';
    const icon = args[3] || 'users';

    const options = { maxSize, color, icon };

    if (this.groupsIntegration) {
      try {
        const groupId = this.groupsIntegration.getGroupManager().createGroup(name, options);
        this.writeOutput(`Created group: ${name} (${groupId})`, 'success');
      } catch (error) {
        this.writeOutput(`Failed to create group: ${error.message}`, 'error');
      }
    } else {
      this.writeOutput('Groups system not available', 'error');
    }
  },

  deleteGroupCommand(args) {
    if (!args.length) {
      this.writeOutput('Usage: delete-group <group-id>', 'error');
      return;
    }

    const groupId = args[0];

    if (this.groupsIntegration) {
      const group = this.groupsIntegration.getGroup(groupId);
      if (!group) {
        this.writeOutput(`Group not found: ${groupId}`, 'error');
        return;
      }

      if (group.members.length > 0) {
        this.writeOutput(`Warning: Group ${group.name} has ${group.members.length} members`, 'warning');
        this.writeOutput('Use "group <id> members" to see them', 'info');
      }

      try {
        this.groupsIntegration.getGroupManager().deleteGroup(groupId);
        this.writeOutput(`Deleted group: ${group.name}`, 'success');
        
        if (this.selectedGroup && this.selectedGroup.id === groupId) {
          this.selectedGroup = null;
          this.updatePromptContext();
        }
      } catch (error) {
        this.writeOutput(`Failed to delete group: ${error.message}`, 'error');
      }
    } else {
      this.writeOutput('Groups system not available', 'error');
    }
  },

  // Client commands
  clientsCommand() {
    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const groupManager = this.groupsIntegration.getGroupManager();
    if (groupManager.clients.size === 0) {
      this.writeOutput('No clients connected', 'info');
      return;
    }

    this.writeOutput('=== Connected Clients ===', 'success');
    this.writeOutput('Username'.padEnd(20) + ' | ID'.padEnd(12) + ' | Status    | Group', 'info');
    this.writeOutput('-'.repeat(60), 'info');

    for (const [clientId, client] of groupManager.clients.entries()) {
      const username = client.username.padEnd(15);
      const id = clientId.substring(0, 8) + '...';
      const status = client.status.padEnd(9);
      const groupId = this.groupsIntegration.getGroupManager().clientToGroup.get(clientId);
      const group = groupId ? this.groupsIntegration.getGroup(groupId) : null;
      const groupName = group ? group.name : 'None';

      this.writeOutput(`${username} | ${id.padEnd(11)} | ${status} | ${groupName}`, 'info');
    }
  },

  assignCommand(args) {
    if (args.length < 2) {
      this.writeOutput('Usage: assign <client-id> <group-id>', 'error');
      return;
    }

    const clientId = args[0];
    const groupId = args[1];

    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const groupManager = this.groupsIntegration.getGroupManager();
    const client = groupManager.clients.get(clientId);
    const group = this.groupsIntegration.getGroup(groupId);

    if (!client) {
      this.writeOutput(`Client not found: ${clientId}`, 'error');
      return;
    }

    if (!group) {
      this.writeOutput(`Group not found: ${groupId}`, 'error');
      return;
    }

    try {
      const success = groupManager.assignClientToGroup(clientId, groupId);
      if (success) {
        this.writeOutput(`Assigned ${client.username} to group ${group.name}`, 'success');
      } else {
        this.writeOutput(`Failed to assign client to group`, 'error');
      }
    } catch (error) {
      this.writeOutput(`Assignment failed: ${error.message}`, 'error');
    }
  },

  kickCommand(args) {
    if (args.length < 1) {
      this.writeOutput('Usage: kick <client-id> [group-id]', 'error');
      return;
    }

    const clientId = args[0];
    let groupId = args[1];

    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const groupManager = this.groupsIntegration.getGroupManager();
    const client = groupManager.clients.get(clientId);

    if (!client) {
      this.writeOutput(`Client not found: ${clientId}`, 'error');
      return;
    }

    // If no group specified, find current group
    if (!groupId) {
      groupId = groupManager.clientToGroup.get(clientId);
      if (!groupId) {
        this.writeOutput(`Client ${client.username} is not in any group`, 'info');
        return;
      }
    }

    const group = this.groupsIntegration.getGroup(groupId);
    if (!group) {
      this.writeOutput(`Group not found: ${groupId}`, 'error');
      return;
    }

    try {
      const success = groupManager.removeClientFromGroup(clientId, groupId);
      if (success) {
        this.writeOutput(`Removed ${client.username} from group ${group.name}`, 'success');
      } else {
        this.writeOutput(`Failed to remove client from group`, 'error');
      }
    } catch (error) {
      this.writeOutput(`Removal failed: ${error.message}`, 'error');
    }
  },

  // Task commands
  tasksCommand() {
    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const groups = this.groupsIntegration.getAllGroups();
    const allTasks = [];

    groups.forEach(group => {
      if (group.currentTasks) {
        group.currentTasks.forEach(task => {
          allTasks.push({ task, group });
        });
      }
    });

    if (allTasks.length === 0) {
      this.writeOutput('No active tasks', 'info');
      return;
    }

    this.writeOutput('=== Active Tasks ===', 'success');
    this.writeOutput('Task ID'.padEnd(12) + ' | Type'.padEnd(10) + ' | Group'.padEnd(15) + ' | Status'.padEnd(10) + ' | Progress', 'info');
    this.writeOutput('-'.repeat(70), 'info');

    allTasks.forEach(({ task, group }) => {
      const id = task.id.substring(0, 8) + '...';
      const type = task.type.padEnd(9);
      const groupName = group.name.substring(0, 12).padEnd(14);
      const status = task.status.padEnd(9);
      const progress = `${task.progress}%`;

      this.writeOutput(`${id.padEnd(11)} | ${type} | ${groupName} | ${status} | ${progress}`, 'info');
    });
  },

  taskCommand(args) {
    if (!this.selectedGroup) {
      this.writeOutput('No group selected. Use "select <group-id>" first', 'error');
      return;
    }

    if (args.length < 1) {
      this.writeOutput('Usage: task <type> [parameters...]', 'error');
      this.writeOutput('Types: gather, mine, farm, build, explore, follow', 'info');
      return;
    }

    const taskType = args[0];
    const parameters = {};

    // Parse parameters based on task type
    switch (taskType) {
      case 'gather':
        parameters.resource = args[1] || 'stone';
        parameters.quantity = parseInt(args[2]) || 64;
        break;
      case 'mine':
        parameters.target = args[1] || 'diamond_ore';
        parameters.depth = args[2] || 'y=-59';
        break;
      case 'farm':
        parameters.crop = args[1] || 'wheat';
        parameters.area = args[2] || '10x10';
        break;
      case 'build':
        parameters.schematic = args[1] || 'house.litematic';
        parameters.position = args[2] || '0,64,0';
        break;
      case 'follow':
        parameters.target = args[1];
        if (!parameters.target) {
          this.writeOutput('Follow task requires target player', 'error');
          return;
        }
        break;
    }

    try {
      const taskId = this.groupsIntegration.getGroupManager().assignTaskToGroup(this.selectedGroup.id, {
        type: taskType,
        parameters,
        isLooping: false
      });

      this.writeOutput(`Created task: ${taskType} (${taskId})`, 'success');
      this.writeOutput(`Assigned to group: ${this.selectedGroup.name}`, 'info');
    } catch (error) {
      this.writeOutput(`Failed to create task: ${error.message}`, 'error');
    }
  },

  loopCommand(args) {
    if (!this.selectedGroup) {
      this.writeOutput('No group selected. Use "select <group-id>" first', 'error');
      return;
    }

    if (args.length < 1) {
      this.writeOutput('Usage: loop <type> [maxLoops] [delay] [parameters...]', 'error');
      return;
    }

    const taskType = args[0];
    const maxLoops = parseInt(args[1]) || 0; // 0 = infinite
    const loopDelay = parseInt(args[2]) || 30000; // 30 seconds
    const parameters = {};

    // Parse remaining parameters based on task type  
    const remainingArgs = args.slice(3);
    switch (taskType) {
      case 'gather':
        parameters.resource = remainingArgs[0] || 'stone';
        parameters.quantity = parseInt(remainingArgs[1]) || 64;
        break;
      case 'mine':
        parameters.target = remainingArgs[0] || 'diamond_ore';
        parameters.depth = remainingArgs[1] || 'y=-59';
        break;
      case 'farm':
        parameters.crop = remainingArgs[0] || 'wheat';
        parameters.area = remainingArgs[1] || '10x10';
        break;
    }

    try {
      const taskId = this.groupsIntegration.getGroupManager().assignTaskToGroup(this.selectedGroup.id, {
        type: taskType,
        parameters,
        isLooping: true,
        maxLoops,
        loopDelay
      });

      this.writeOutput(`Created looping task: ${taskType} (${taskId})`, 'success');
      this.writeOutput(`Max loops: ${maxLoops || 'infinite'}, Delay: ${loopDelay}ms`, 'info');
      this.writeOutput(`Assigned to group: ${this.selectedGroup.name}`, 'info');
    } catch (error) {
      this.writeOutput(`Failed to create looping task: ${error.message}`, 'error');
    }
  },

  cancelCommand(args) {
    if (args.length < 1) {
      this.writeOutput('Usage: cancel <task-id>', 'error');
      return;
    }

    const taskId = args[0];

    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    // Find task across all groups
    const groups = this.groupsIntegration.getAllGroups();
    let targetGroup = null;
    let targetTask = null;

    for (const group of groups) {
      if (group.currentTasks) {
        const task = group.currentTasks.find(t => t.id === taskId || t.id.startsWith(taskId));
        if (task) {
          targetGroup = group;
          targetTask = task;
          break;
        }
      }
    }

    if (!targetTask) {
      this.writeOutput(`Task not found: ${taskId}`, 'error');
      return;
    }

    try {
      const success = this.groupsIntegration.getGroupManager().cancelGroupTask(targetGroup.id, targetTask.id);
      if (success) {
        this.writeOutput(`Cancelled task: ${targetTask.type} from ${targetGroup.name}`, 'success');
      } else {
        this.writeOutput(`Failed to cancel task`, 'error');
      }
    } catch (error) {
      this.writeOutput(`Cancellation failed: ${error.message}`, 'error');
    }
  },

  // Monitoring commands
  monitorCommand() {
    this.toggleResourceMonitor();
  },

  statsCommand() {
    if (!this.groupsIntegration) {
      this.writeOutput('Groups system not available', 'error');
      return;
    }

    const groups = this.groupsIntegration.getAllGroups();
    const groupManager = this.groupsIntegration.getGroupManager();

    this.writeOutput('=== Detailed Statistics ===', 'success');
    
    // Group statistics
    const activeGroups = groups.filter(g => g.members.length > 0);
    const totalMembers = groups.reduce((sum, g) => sum + g.members.length, 0);
    this.writeOutput(`Groups: ${groups.length} total, ${activeGroups.length} active`, 'info');
    this.writeOutput(`Total members: ${totalMembers}`, 'info');

    // Task statistics
    const allTasks = groups.reduce((sum, g) => sum + (g.currentTasks?.length || 0), 0);
    const loopingTasks = groups.filter(g => g.loopingTask).length;
    this.writeOutput(`Tasks: ${allTasks} active, ${loopingTasks} looping`, 'info');

    // Client statistics
    const onlineClients = Array.from(groupManager.clients.values()).filter(c => c.status === 'connected').length;
    this.writeOutput(`Clients: ${groupManager.clients.size} total, ${onlineClients} online`, 'info');

    // Performance statistics
    this.writeOutput(`Commands executed: ${document.getElementById('commands-executed')?.textContent || '0'}`, 'info');
    this.writeOutput(`Resource monitoring: ${this.resourceMonitor.enabled ? 'ON' : 'OFF'}`, 'info');
  },

  healthCommand() {
    this.writeOutput('=== System Health ===', 'success');
    
    if (this.groupsIntegration) {
      const groupManager = this.groupsIntegration.getGroupManager();
      const healthyClients = Array.from(groupManager.clients.values()).filter(c => c.status === 'connected').length;
      const totalClients = groupManager.clients.size;
      
      const healthPercentage = totalClients > 0 ? Math.round((healthyClients / totalClients) * 100) : 100;
      this.writeOutput(`Client Health: ${healthyClients}/${totalClients} online (${healthPercentage}%)`, healthPercentage > 80 ? 'success' : healthPercentage > 50 ? 'warning' : 'error');
    }
    
    this.writeOutput(`Terminal Status: ${this.isVisible ? 'Active' : 'Inactive'}`, 'success');
    this.writeOutput(`Resource Monitor: ${this.resourceMonitor.enabled ? 'Running' : 'Stopped'}`, this.resourceMonitor.enabled ? 'success' : 'info');
  },

  logCommand(args) {
    const lines = parseInt(args[0]) || 10;
    
    this.writeOutput(`=== Recent Log Entries (Last ${lines}) ===`, 'success');
    
    const recentLogs = this.outputHistory.slice(-lines);
    recentLogs.forEach(log => {
      const timestamp = new Date(log.timestamp).toLocaleTimeString();
      this.writeOutput(`[${timestamp}] ${log.message}`, log.type);
    });
  },

  // Utility commands
  echoCommand(args) {
    this.writeOutput(args.join(' '), 'info');
  },

  timeCommand() {
    const now = new Date();
    this.writeOutput(`Current time: ${now.toLocaleString()}`, 'info');
    this.writeOutput(`UTC time: ${now.toISOString()}`, 'info');
  },

  uptimeCommand() {
    if (this.webUIServer) {
      const uptime = Date.now() - (this.webUIServer.uiState?.systemStats?.uptime || Date.now());
      const hours = Math.floor(uptime / (1000 * 60 * 60));
      const minutes = Math.floor((uptime % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((uptime % (1000 * 60)) / 1000);
      
      this.writeOutput(`System uptime: ${hours}h ${minutes}m ${seconds}s`, 'success');
    } else {
      this.writeOutput('Uptime information not available', 'error');
    }
  },

  // Server commands (placeholder implementations)
  restartCommand(args) {
    const component = args[0] || 'all';
    this.writeOutput(`⚠️  Restart command would restart: ${component}`, 'warning');
    this.writeOutput('This is a simulation - actual restart not performed', 'info');
  },

  backupCommand() {
    const timestamp = new Date().toISOString().replace(/:/g, '-').split('.')[0];
    this.writeOutput(`Creating backup: AppyProx-${timestamp}`, 'info');
    this.writeOutput('✅ Backup simulation completed', 'success');
  },

  configCommand(args) {
    if (!args.length) {
      this.writeOutput('Usage: config <section> [key] [value]', 'error');
      this.writeOutput('Sections: proxy, groups, automation, logging, webui', 'info');
      return;
    }

    const section = args[0];
    const key = args[1];
    const value = args[2];

    if (!key) {
      this.writeOutput(`Configuration section: ${section}`, 'info');
      this.writeOutput('(Configuration viewing not implemented)', 'info');
    } else if (!value) {
      this.writeOutput(`${section}.${key} = (value not shown)`, 'info');
    } else {
      this.writeOutput(`Would set ${section}.${key} = ${value}`, 'warning');
      this.writeOutput('Configuration changes not implemented', 'info');
    }
  }
};

// Export the commands object for mixing into CommandTerminal
module.exports = TerminalCommands;