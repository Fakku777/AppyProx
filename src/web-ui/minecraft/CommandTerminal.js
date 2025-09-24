/**
 * AppyProx Command Terminal - Minecraft-style terminal interface
 * Features: command autocompletion, resource monitoring, group-specific execution
 */

const TerminalCommands = require('./TerminalCommands');

class CommandTerminal {
  constructor(webUIServer, groupsIntegration, logger) {
    this.webUIServer = webUIServer;
    this.groupsIntegration = groupsIntegration;
    this.logger = logger?.child ? logger.child('CommandTerminal') : logger;
    
    // Mix in command implementations
    Object.assign(this, TerminalCommands);
    
    // Terminal state
    this.isVisible = false;
    this.currentInput = '';
    this.commandHistory = [];
    this.historyIndex = -1;
    this.autoCompleteIndex = -1;
    this.autoCompleteSuggestions = [];
    this.selectedGroup = null;
    
    // Command output history
    this.outputHistory = [];
    this.maxOutputLines = 1000;
    
    // Resource monitoring
    this.resourceMonitor = {
      enabled: false,
      interval: null,
      updateFrequency: 5000 // 5 seconds
    };
    
    // Command registry
    this.commands = new Map();
    this.aliases = new Map();
    
    // DOM elements
    this.terminalElement = null;
    this.inputElement = null;
    this.outputElement = null;
    this.suggestionElement = null;
    
    // Initialize if DOM is available
    this.isDOMAvailable = typeof document !== 'undefined';
    if (this.isDOMAvailable) {
      this.initializeTerminal();
      this.registerCommands();
      this.setupEventListeners();
    }
  }

  initializeTerminal() {
    this.createTerminalInterface();
    this.attachToRightPanel();
    this.logger?.info('Command terminal initialized');
  }

  createTerminalInterface() {
    // Remove existing terminal if present
    const existing = document.getElementById('command-terminal');
    if (existing) existing.remove();

    this.terminalElement = document.createElement('div');
    this.terminalElement.id = 'command-terminal';
    this.terminalElement.className = 'minecraft-terminal hidden';
    
    this.terminalElement.innerHTML = `
      <div class="terminal-header minecraft-panel">
        <div class="terminal-title">
          <h3 class="minecraft-title">Command Terminal</h3>
          <div class="terminal-status">
            <span id="selected-group-indicator" class="minecraft-badge">All Groups</span>
            <span id="resource-monitor-status" class="minecraft-badge">Monitor: OFF</span>
          </div>
        </div>
        <div class="terminal-controls">
          <button id="clear-terminal" class="minecraft-button" title="Clear Terminal">🗑️</button>
          <button id="resource-monitor-toggle" class="minecraft-button" title="Toggle Resource Monitor">📊</button>
          <button id="terminal-settings" class="minecraft-button" title="Terminal Settings">⚙️</button>
          <button id="close-terminal" class="minecraft-button" title="Close Terminal">×</button>
        </div>
      </div>
      
      <div class="terminal-body">
        <div id="terminal-output" class="terminal-output">
          <div class="welcome-message">
            <span class="minecraft-text-green">AppyProx Command Terminal v2.0</span>
            <span class="minecraft-text-gray">Type 'help' for available commands</span>
            <span class="minecraft-text-gray">Use Tab for autocompletion, ↑↓ for command history</span>
          </div>
        </div>
        
        <div class="terminal-input-container">
          <span class="terminal-prompt">
            <span class="minecraft-text-green">appyprox</span>
            <span class="minecraft-text-gray">@</span>
            <span id="prompt-context" class="minecraft-text-yellow">global</span>
            <span class="minecraft-text-gray">$</span>
          </span>
          <input type="text" id="terminal-input" class="terminal-input" 
                 placeholder="Enter command..." autocomplete="off" spellcheck="false">
        </div>
        
        <div id="autocomplete-suggestions" class="autocomplete-suggestions hidden">
          <!-- Suggestions populated dynamically -->
        </div>
      </div>
      
      <div class="terminal-footer">
        <div class="terminal-stats">
          <span class="stat">Commands: <span id="commands-executed">0</span></span>
          <span class="stat">Groups: <span id="active-groups">0</span></span>
          <span class="stat">Clients: <span id="active-clients">0</span></span>
        </div>
      </div>
    `;

    // Cache important elements
    this.outputElement = this.terminalElement.querySelector('#terminal-output');
    this.inputElement = this.terminalElement.querySelector('#terminal-input');
    this.suggestionElement = this.terminalElement.querySelector('#autocomplete-suggestions');
  }

  attachToRightPanel() {
    // Find or create terminal toggle button
    this.createTerminalToggle();
    
    // Append terminal to body (overlay style)
    document.body.appendChild(this.terminalElement);
  }

  createTerminalToggle() {
    // Add terminal toggle to right panel if it doesn't exist
    let rightPanel = document.getElementById('right-panel');
    if (!rightPanel) {
      // Create right panel if it doesn't exist
      rightPanel = document.createElement('div');
      rightPanel.id = 'right-panel';
      rightPanel.className = 'minecraft-panel';
      
      const mainContainer = document.getElementById('main-container');
      if (mainContainer) {
        mainContainer.appendChild(rightPanel);
      }
    }

    // Add terminal toggle button
    const existing = document.getElementById('terminal-toggle');
    if (!existing) {
      const terminalToggle = document.createElement('div');
      terminalToggle.className = 'minecraft-window';
      terminalToggle.innerHTML = `
        <h3 class="minecraft-title">Terminal</h3>
        <button id="terminal-toggle" class="minecraft-button-primary">Open Terminal</button>
      `;
      
      rightPanel.insertBefore(terminalToggle, rightPanel.firstChild);
    }
  }

  setupEventListeners() {
    // Terminal toggle
    document.addEventListener('click', (e) => {
      if (e.target.id === 'terminal-toggle') {
        this.toggleTerminal();
      } else if (e.target.id === 'close-terminal') {
        this.hideTerminal();
      } else if (e.target.id === 'clear-terminal') {
        this.clearTerminal();
      } else if (e.target.id === 'resource-monitor-toggle') {
        this.toggleResourceMonitor();
      }
    });

    // Terminal input handling
    if (this.inputElement) {
      this.inputElement.addEventListener('keydown', (e) => {
        this.handleKeyDown(e);
      });

      this.inputElement.addEventListener('input', (e) => {
        this.handleInput(e);
      });

      this.inputElement.addEventListener('blur', () => {
        // Hide suggestions when input loses focus
        setTimeout(() => {
          this.hideSuggestions();
        }, 200);
      });
    }

    // Global keyboard shortcuts
    document.addEventListener('keydown', (e) => {
      // Ctrl+` or Ctrl+~ to toggle terminal
      if ((e.ctrlKey || e.metaKey) && (e.key === '`' || e.key === '~')) {
        e.preventDefault();
        this.toggleTerminal();
      }
      
      // ESC to close terminal
      if (e.key === 'Escape' && this.isVisible) {
        this.hideTerminal();
      }
    });

    // Click outside to hide suggestions
    document.addEventListener('click', (e) => {
      if (!this.terminalElement?.contains(e.target)) {
        this.hideSuggestions();
      }
    });
  }

  registerCommands() {
    // System commands
    this.registerCommand('help', 'Show available commands', this.helpCommand.bind(this));
    this.registerCommand('clear', 'Clear terminal output', this.clearCommand.bind(this));
    this.registerCommand('history', 'Show command history', this.historyCommand.bind(this));
    this.registerCommand('status', 'Show system status', this.statusCommand.bind(this));
    this.registerCommand('version', 'Show AppyProx version', this.versionCommand.bind(this));
    
    // Group commands
    this.registerCommand('groups', 'List all groups', this.groupsCommand.bind(this));
    this.registerCommand('group', 'Group management commands', this.groupCommand.bind(this));
    this.registerCommand('select', 'Select active group', this.selectCommand.bind(this));
    this.registerCommand('create-group', 'Create new group', this.createGroupCommand.bind(this));
    this.registerCommand('delete-group', 'Delete group', this.deleteGroupCommand.bind(this));
    
    // Client commands
    this.registerCommand('clients', 'List all clients', this.clientsCommand.bind(this));
    this.registerCommand('assign', 'Assign client to group', this.assignCommand.bind(this));
    this.registerCommand('kick', 'Remove client from group', this.kickCommand.bind(this));
    
    // Task commands
    this.registerCommand('tasks', 'List active tasks', this.tasksCommand.bind(this));
    this.registerCommand('task', 'Create new task', this.taskCommand.bind(this));
    this.registerCommand('cancel', 'Cancel task', this.cancelCommand.bind(this));
    this.registerCommand('loop', 'Create looping task', this.loopCommand.bind(this));
    
    // Monitoring commands  
    this.registerCommand('monitor', 'Toggle resource monitoring', this.monitorCommand.bind(this));
    this.registerCommand('stats', 'Show detailed statistics', this.statsCommand.bind(this));
    this.registerCommand('health', 'Show health status', this.healthCommand.bind(this));
    this.registerCommand('log', 'Show system logs', this.logCommand.bind(this));
    
    // Server commands
    this.registerCommand('restart', 'Restart components', this.restartCommand.bind(this));
    this.registerCommand('backup', 'Create system backup', this.backupCommand.bind(this));
    this.registerCommand('config', 'Configuration management', this.configCommand.bind(this));
    
    // Utility commands
    this.registerCommand('echo', 'Echo text', this.echoCommand.bind(this));
    this.registerCommand('time', 'Show current time', this.timeCommand.bind(this));
    this.registerCommand('uptime', 'Show system uptime', this.uptimeCommand.bind(this));
    
    // Command aliases
    this.registerAlias('ls', 'groups');
    this.registerAlias('ps', 'clients');
    this.registerAlias('top', 'monitor');
    this.registerAlias('cls', 'clear');
    this.registerAlias('exit', 'close');
    this.registerAlias('quit', 'close');
  }

  registerCommand(name, description, handler, options = {}) {
    this.commands.set(name, {
      name,
      description,
      handler,
      usage: options.usage || `${name} ${options.args || ''}`.trim(),
      category: options.category || 'general',
      adminOnly: options.adminOnly || false,
      groupRequired: options.groupRequired || false
    });
  }

  registerAlias(alias, command) {
    this.aliases.set(alias, command);
  }

  // Terminal visibility controls
  toggleTerminal() {
    if (this.isVisible) {
      this.hideTerminal();
    } else {
      this.showTerminal();
    }
  }

  showTerminal() {
    if (!this.terminalElement) return;
    
    this.terminalElement.classList.remove('hidden');
    this.isVisible = true;
    
    // Focus input
    setTimeout(() => {
      this.inputElement?.focus();
    }, 100);
    
    // Update toggle button text
    const toggleBtn = document.getElementById('terminal-toggle');
    if (toggleBtn) toggleBtn.textContent = 'Close Terminal';
    
    this.writeOutput('Terminal opened', 'system');
  }

  hideTerminal() {
    if (!this.terminalElement) return;
    
    this.terminalElement.classList.add('hidden');
    this.isVisible = false;
    this.hideSuggestions();
    
    // Update toggle button text
    const toggleBtn = document.getElementById('terminal-toggle');
    if (toggleBtn) toggleBtn.textContent = 'Open Terminal';
  }

  // Input handling
  handleKeyDown(e) {
    switch (e.key) {
      case 'Enter':
        e.preventDefault();
        this.executeCommand();
        break;
        
      case 'Tab':
        e.preventDefault();
        this.handleTabComplete();
        break;
        
      case 'ArrowUp':
        e.preventDefault();
        this.navigateHistory(-1);
        break;
        
      case 'ArrowDown':
        e.preventDefault();
        this.navigateHistory(1);
        break;
        
      case 'ArrowLeft':
      case 'ArrowRight':
        this.hideSuggestions();
        break;
        
      case 'Escape':
        this.hideSuggestions();
        this.inputElement.value = '';
        break;
    }
  }

  handleInput(e) {
    this.currentInput = e.target.value;
    this.updateAutoComplete();
  }

  executeCommand() {
    const input = this.currentInput.trim();
    if (!input) return;
    
    // Add to history
    this.commandHistory.push(input);
    this.historyIndex = -1;
    
    // Show command in output
    this.writeOutput(`$ ${input}`, 'command');
    
    // Parse and execute
    this.parseAndExecute(input);
    
    // Clear input
    this.inputElement.value = '';
    this.currentInput = '';
    this.hideSuggestions();
    
    // Update stats
    this.updateCommandStats();
  }

  parseAndExecute(input) {
    const args = this.parseArgs(input);
    const commandName = args.shift()?.toLowerCase();
    
    if (!commandName) return;
    
    // Check for alias
    const resolvedCommand = this.aliases.get(commandName) || commandName;
    const command = this.commands.get(resolvedCommand);
    
    if (!command) {
      this.writeOutput(`Command not found: ${commandName}. Type 'help' for available commands.`, 'error');
      return;
    }
    
    // Check group requirement
    if (command.groupRequired && !this.selectedGroup) {
      this.writeOutput(`Command '${commandName}' requires a group to be selected. Use 'select <group-id>' first.`, 'error');
      return;
    }
    
    try {
      command.handler(args, { input, command: commandName });
    } catch (error) {
      this.writeOutput(`Error executing command: ${error.message}`, 'error');
      this.logger?.error('Command execution error:', error);
    }
  }

  parseArgs(input) {
    // Simple argument parsing - handles quoted strings
    const args = [];
    let current = '';
    let inQuotes = false;
    let quoteChar = '';
    
    for (let i = 0; i < input.length; i++) {
      const char = input[i];
      
      if ((char === '"' || char === "'") && !inQuotes) {
        inQuotes = true;
        quoteChar = char;
      } else if (char === quoteChar && inQuotes) {
        inQuotes = false;
        quoteChar = '';
      } else if (char === ' ' && !inQuotes) {
        if (current) {
          args.push(current);
          current = '';
        }
      } else {
        current += char;
      }
    }
    
    if (current) {
      args.push(current);
    }
    
    return args;
  }

  // Autocompletion system
  updateAutoComplete() {
    if (!this.currentInput.trim()) {
      this.hideSuggestions();
      return;
    }
    
    const suggestions = this.generateSuggestions(this.currentInput);
    this.showSuggestions(suggestions);
  }

  generateSuggestions(input) {
    const args = this.parseArgs(input);
    const commandName = args[0]?.toLowerCase();
    
    if (args.length === 1) {
      // Command name completion
      const suggestions = [];
      
      // Add matching commands
      for (const [name, cmd] of this.commands.entries()) {
        if (name.startsWith(commandName)) {
          suggestions.push({
            text: name,
            description: cmd.description,
            type: 'command'
          });
        }
      }
      
      // Add matching aliases
      for (const [alias, cmd] of this.aliases.entries()) {
        if (alias.startsWith(commandName)) {
          suggestions.push({
            text: alias,
            description: `Alias for '${cmd}'`,
            type: 'alias'
          });
        }
      }
      
      return suggestions.slice(0, 10); // Limit to 10 suggestions
    }
    
    // Context-specific suggestions
    return this.getContextualSuggestions(commandName, args.slice(1));
  }

  getContextualSuggestions(command, args) {
    const suggestions = [];
    
    switch (command) {
      case 'group':
      case 'select':
      case 'delete-group':
        // Group ID suggestions
        if (this.groupsIntegration) {
          const groups = this.groupsIntegration.getAllGroups();
          groups.forEach(group => {
            if (!args[0] || group.id.startsWith(args[0]) || group.name.toLowerCase().includes(args[0].toLowerCase())) {
              suggestions.push({
                text: group.id,
                description: `Group: ${group.name} (${group.members.length} members)`,
                type: 'group-id'
              });
            }
          });
        }
        break;
        
      case 'assign':
      case 'kick':
        // Client ID suggestions
        if (this.groupsIntegration && args.length === 1) {
          const groupManager = this.groupsIntegration.getGroupManager();
          for (const [clientId, client] of groupManager.clients.entries()) {
            if (!args[0] || clientId.startsWith(args[0]) || client.username.toLowerCase().includes(args[0].toLowerCase())) {
              suggestions.push({
                text: clientId,
                description: `Client: ${client.username}`,
                type: 'client-id'
              });
            }
          }
        }
        break;
        
      case 'task':
        // Task type suggestions
        const taskTypes = ['gather', 'mine', 'farm', 'build', 'explore', 'follow'];
        taskTypes.forEach(type => {
          if (!args[0] || type.startsWith(args[0])) {
            suggestions.push({
              text: type,
              description: `Task type: ${type}`,
              type: 'task-type'
            });
          }
        });
        break;
        
      case 'config':
        // Config key suggestions
        const configKeys = ['proxy', 'groups', 'automation', 'logging', 'webui'];
        configKeys.forEach(key => {
          if (!args[0] || key.startsWith(args[0])) {
            suggestions.push({
              text: key,
              description: `Config section: ${key}`,
              type: 'config-key'
            });
          }
        });
        break;
    }
    
    return suggestions.slice(0, 8);
  }

  showSuggestions(suggestions) {
    if (!suggestions.length) {
      this.hideSuggestions();
      return;
    }
    
    this.autoCompleteSuggestions = suggestions;
    this.autoCompleteIndex = -1;
    
    const html = suggestions.map((suggestion, index) => `
      <div class="suggestion-item ${index === this.autoCompleteIndex ? 'selected' : ''}" 
           data-index="${index}">
        <span class="suggestion-text minecraft-text-${this.getSuggestionColor(suggestion.type)}">${suggestion.text}</span>
        <span class="suggestion-desc minecraft-text-gray">${suggestion.description}</span>
      </div>
    `).join('');
    
    this.suggestionElement.innerHTML = html;
    this.suggestionElement.classList.remove('hidden');
    
    // Add click handlers
    this.suggestionElement.querySelectorAll('.suggestion-item').forEach((item, index) => {
      item.addEventListener('click', () => {
        this.applySuggestion(index);
      });
    });
  }

  getSuggestionColor(type) {
    switch (type) {
      case 'command': return 'green';
      case 'alias': return 'yellow';
      case 'group-id': return 'blue';
      case 'client-id': return 'cyan';
      case 'task-type': return 'purple';
      default: return 'white';
    }
  }

  hideSuggestions() {
    if (this.suggestionElement) {
      this.suggestionElement.classList.add('hidden');
    }
    this.autoCompleteSuggestions = [];
    this.autoCompleteIndex = -1;
  }

  handleTabComplete() {
    if (!this.autoCompleteSuggestions.length) return;
    
    if (this.autoCompleteIndex < 0) {
      this.autoCompleteIndex = 0;
    } else {
      this.autoCompleteIndex = (this.autoCompleteIndex + 1) % this.autoCompleteSuggestions.length;
    }
    
    this.applySuggestion(this.autoCompleteIndex);
    this.updateSuggestionSelection();
  }

  applySuggestion(index) {
    if (index < 0 || index >= this.autoCompleteSuggestions.length) return;
    
    const suggestion = this.autoCompleteSuggestions[index];
    const args = this.parseArgs(this.currentInput);
    
    if (args.length <= 1) {
      // Replace command name
      this.inputElement.value = suggestion.text + ' ';
    } else {
      // Replace last argument
      args[args.length - 1] = suggestion.text;
      this.inputElement.value = args.join(' ') + ' ';
    }
    
    this.currentInput = this.inputElement.value;
    this.hideSuggestions();
    this.inputElement.focus();
    
    // Position cursor at end
    this.inputElement.setSelectionRange(this.inputElement.value.length, this.inputElement.value.length);
  }

  updateSuggestionSelection() {
    const items = this.suggestionElement.querySelectorAll('.suggestion-item');
    items.forEach((item, index) => {
      item.classList.toggle('selected', index === this.autoCompleteIndex);
    });
  }

  // Command history navigation
  navigateHistory(direction) {
    if (!this.commandHistory.length) return;
    
    if (this.historyIndex < 0) {
      this.historyIndex = this.commandHistory.length;
    }
    
    this.historyIndex += direction;
    
    if (this.historyIndex < 0) {
      this.historyIndex = 0;
    } else if (this.historyIndex >= this.commandHistory.length) {
      this.historyIndex = this.commandHistory.length;
      this.inputElement.value = '';
      this.currentInput = '';
      return;
    }
    
    this.inputElement.value = this.commandHistory[this.historyIndex];
    this.currentInput = this.inputElement.value;
    
    // Position cursor at end
    this.inputElement.setSelectionRange(this.inputElement.value.length, this.inputElement.value.length);
  }

  // Output management
  writeOutput(message, type = 'info', metadata = {}) {
    const outputLine = {
      message,
      type,
      timestamp: Date.now(),
      metadata
    };
    
    this.outputHistory.push(outputLine);
    
    // Limit history size
    if (this.outputHistory.length > this.maxOutputLines) {
      this.outputHistory.shift();
    }
    
    // Add to DOM
    this.appendOutputToDom(outputLine);
    
    // Auto-scroll to bottom
    this.scrollToBottom();
  }

  appendOutputToDom(outputLine) {
    if (!this.outputElement) return;
    
    const outputDiv = document.createElement('div');
    outputDiv.className = `output-line output-${outputLine.type}`;
    
    const timestamp = new Date(outputLine.timestamp).toLocaleTimeString();
    
    outputDiv.innerHTML = `
      <span class="output-timestamp minecraft-text-gray">[${timestamp}]</span>
      <span class="output-content minecraft-text-${this.getOutputColor(outputLine.type)}">${this.escapeHtml(outputLine.message)}</span>
    `;
    
    this.outputElement.appendChild(outputDiv);
  }

  getOutputColor(type) {
    switch (type) {
      case 'command': return 'white';
      case 'success': return 'green';
      case 'error': return 'red';
      case 'warning': return 'yellow';
      case 'info': return 'cyan';
      case 'system': return 'gray';
      default: return 'white';
    }
  }

  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  scrollToBottom() {
    if (this.outputElement) {
      this.outputElement.scrollTop = this.outputElement.scrollHeight;
    }
  }

  clearTerminal() {
    if (this.outputElement) {
      this.outputElement.innerHTML = `
        <div class="welcome-message">
          <span class="minecraft-text-green">AppyProx Command Terminal v2.0</span>
          <span class="minecraft-text-gray">Type 'help' for available commands</span>
        </div>
      `;
    }
    this.outputHistory = [];
    this.writeOutput('Terminal cleared', 'system');
  }

  // Resource monitoring
  toggleResourceMonitor() {
    if (this.resourceMonitor.enabled) {
      this.stopResourceMonitor();
    } else {
      this.startResourceMonitor();
    }
  }

  startResourceMonitor() {
    this.resourceMonitor.enabled = true;
    this.resourceMonitor.interval = setInterval(() => {
      this.updateResourceStats();
    }, this.resourceMonitor.updateFrequency);
    
    this.writeOutput('Resource monitoring started', 'success');
    this.updateMonitorStatus();
  }

  stopResourceMonitor() {
    this.resourceMonitor.enabled = false;
    if (this.resourceMonitor.interval) {
      clearInterval(this.resourceMonitor.interval);
      this.resourceMonitor.interval = null;
    }
    
    this.writeOutput('Resource monitoring stopped', 'info');
    this.updateMonitorStatus();
  }

  updateMonitorStatus() {
    const statusElement = document.getElementById('resource-monitor-status');
    if (statusElement) {
      statusElement.textContent = `Monitor: ${this.resourceMonitor.enabled ? 'ON' : 'OFF'}`;
      statusElement.className = `minecraft-badge ${this.resourceMonitor.enabled ? 'online' : ''}`;
    }
  }

  updateResourceStats() {
    // This would integrate with actual system monitoring
    // For now, we'll show basic stats
    if (this.groupsIntegration) {
      const groups = this.groupsIntegration.getAllGroups();
      const groupManager = this.groupsIntegration.getGroupManager();
      
      const activeGroups = groups.filter(g => g.members.length > 0).length;
      const totalClients = groupManager.clients.size;
      const activeTasks = groups.reduce((sum, g) => sum + (g.currentTasks?.length || 0), 0);
      
      // Update footer stats
      this.updateTerminalStats(activeGroups, totalClients, activeTasks);
    }
  }

  updateTerminalStats(groups, clients, tasks) {
    const commandsEl = document.getElementById('commands-executed');
    const groupsEl = document.getElementById('active-groups');
    const clientsEl = document.getElementById('active-clients');
    
    if (groupsEl) groupsEl.textContent = groups;
    if (clientsEl) clientsEl.textContent = clients;
  }

  updateCommandStats() {
    const commandsEl = document.getElementById('commands-executed');
    if (commandsEl) {
      const currentCount = parseInt(commandsEl.textContent) || 0;
      commandsEl.textContent = currentCount + 1;
    }
  }

  // Context management
  selectGroup(groupId) {
    if (this.groupsIntegration) {
      const group = this.groupsIntegration.getGroup(groupId);
      if (group) {
        this.selectedGroup = group;
        this.updatePromptContext(group.name);
        return true;
      }
    }
    return false;
  }

  updatePromptContext(context) {
    const contextElement = document.getElementById('prompt-context');
    if (contextElement) {
      contextElement.textContent = context || 'global';
    }
    
    const groupIndicator = document.getElementById('selected-group-indicator');
    if (groupIndicator) {
      groupIndicator.textContent = context ? `Group: ${context}` : 'All Groups';
    }
  }

  // Public API
  executeCommandProgrammatically(command) {
    if (this.isDOMAvailable) {
      this.parseAndExecute(command);
    }
  }

  addCustomCommand(name, description, handler, options = {}) {
    this.registerCommand(name, description, handler, options);
  }

  getCommandHistory() {
    return [...this.commandHistory];
  }

  isTerminalVisible() {
    return this.isVisible;
  }
}

module.exports = CommandTerminal;
