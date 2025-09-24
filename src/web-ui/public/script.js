/**
 * AppyProx Minecraft-Style Web Interface Client
 * Handles all UI interactions, WebSocket communication, and terminal integration
 */

const MinecraftClient = {
  // Connection state
  websocket: null,
  reconnectAttempts: 0,
  maxReconnectAttempts: 5,
  reconnectDelay: 2000,
  
  // UI state
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
    chunks: new Map(),
    needsRender: true
  },
  
  // System data
  groups: new Map(),
  clients: new Map(),
  tasks: new Map(),
  systemStats: {},
  
  // Command terminal
  terminal: null,
  
  // Initialize the interface
  init() {
    console.log('🚀 Initializing AppyProx Minecraft Interface...');
    
    // Cache DOM elements
    this.cacheElements();
    
    // Setup map canvas
    this.setupMapCanvas();
    
    // Setup WebSocket connection
    this.connectWebSocket();
    
    // Setup event listeners
    this.setupEventListeners();
    
    // Initialize command terminal
    this.initializeTerminal();
    
    // Start periodic updates
    this.startPeriodicUpdates();
    
    // Load initial data
    this.loadInitialState();
    
    console.log('✅ AppyProx Minecraft Interface ready!');
  },
  
  cacheElements() {
    this.elements = {
      // Map
      mapCanvas: document.getElementById('map-canvas'),
      coordinates: document.getElementById('coordinates'),
      biomeInfo: document.getElementById('biome-info'),
      
      // Lists
      groupsList: document.getElementById('groups-list'),
      clientsList: document.getElementById('clients-list'),
      tasksList: document.getElementById('tasks-list'),
      
      // Progress bars
      cpuProgress: document.getElementById('cpu-progress'),
      cpuValue: document.getElementById('cpu-value'),
      memoryProgress: document.getElementById('memory-progress'),
      memoryValue: document.getElementById('memory-value'),
      systemStatus: document.getElementById('system-status'),
      
      // Controls
      taskType: document.getElementById('task-type'),
      
      // Terminal
      terminalOutput: document.getElementById('terminal-output'),
      terminalInput: document.getElementById('terminal-input'),
      
      // Modal and toast
      modalOverlay: document.getElementById('modal-overlay'),
      modalContent: document.getElementById('modal-content'),
      toastContainer: document.getElementById('toast-container')
    };
  },
  
  setupMapCanvas() {
    this.map.canvas = this.elements.mapCanvas;
    if (!this.map.canvas) return;
    
    this.map.ctx = this.map.canvas.getContext('2d');
    
    // Set up canvas size to maintain square aspect ratio
    this.resizeCanvas();
    
    // Handle window resize
    window.addEventListener('resize', () => this.resizeCanvas());
    
    // Initial render
    this.renderMap();
  },
  
  resizeCanvas() {
    if (!this.map.canvas) return;
    
    const container = this.map.canvas.parentElement;
    const containerRect = container.getBoundingClientRect();
    
    // Make canvas square based on container size
    const size = Math.min(containerRect.width, containerRect.height);
    
    this.map.canvas.width = size;
    this.map.canvas.height = size;
    
    // Re-render after resize
    this.map.needsRender = true;
    this.renderMap();
  },
  
  connectWebSocket() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}`;
    
    try {
      this.websocket = new WebSocket(wsUrl);
      
      this.websocket.onopen = () => {
        console.log('🔗 WebSocket connected');
        this.reconnectAttempts = 0;
        this.showToast('Connected', 'Connected to AppyProx server', 'success');
      };
      
      this.websocket.onmessage = (event) => {
        this.handleWebSocketMessage(JSON.parse(event.data));
      };
      
      this.websocket.onclose = () => {
        console.log('❌ WebSocket disconnected');
        this.showToast('Disconnected', 'Connection to server lost', 'error');
        this.attemptReconnect();
      };
      
      this.websocket.onerror = (error) => {
        console.error('⚠️ WebSocket error:', error);
      };
      
    } catch (error) {
      console.error('Failed to connect WebSocket:', error);
      this.showToast('Connection Failed', 'Unable to connect to server', 'error');
    }
  },
  
  attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`🔄 Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
      
      setTimeout(() => {
        this.connectWebSocket();
      }, this.reconnectDelay * this.reconnectAttempts);
    } else {
      console.error('❌ Max reconnection attempts reached');
      this.showToast('Connection Failed', 'Unable to reconnect to server', 'error');
    }
  },
  
  handleWebSocketMessage(data) {
    switch (data.type) {
      case 'initial_state':
        this.loadInitialState(data.data);
        break;
      case 'groups_updated':
        this.updateGroups(data.data);
        break;
      case 'clients_updated':
        this.updateClients(data.data);
        break;
      case 'tasks_updated':
        this.updateTasks(data.data);
        break;
      case 'system_stats':
        this.updateSystemStats(data.data);
        break;
      case 'player_update':
        this.updatePlayer(data.data);
        break;
      case 'map_update':
        this.updateMap(data.data);
        break;
      default:
        console.log('Unhandled message type:', data.type);
    }
  },
  
  setupEventListeners() {
    // Map interactions
    if (this.map.canvas) {
      this.map.canvas.addEventListener('mousedown', (e) => this.handleMapMouseDown(e));
      this.map.canvas.addEventListener('mousemove', (e) => this.handleMapMouseMove(e));
      this.map.canvas.addEventListener('mouseup', (e) => this.handleMapMouseUp(e));
      this.map.canvas.addEventListener('wheel', (e) => this.handleMapWheel(e));
      this.map.canvas.addEventListener('contextmenu', (e) => e.preventDefault());
    }
    
    // Global keyboard shortcuts
    document.addEventListener('keydown', (e) => {
      // Ctrl+` to toggle terminal
      if ((e.ctrlKey || e.metaKey) && e.key === '`') {
        e.preventDefault();
        this.toggleTerminal();
      }
    });
  },
  
  initializeTerminal() {
    if (!this.elements.terminalInput) return;
    
    this.elements.terminalInput.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        this.executeCommand();
      } else if (e.key === 'Tab') {
        e.preventDefault();
        this.handleTabComplete();
      }
    });
    
    this.writeToTerminal('AppyProx Command Terminal v2.0 Ready', 'system');
    this.writeToTerminal('Type "help" for available commands', 'info');
  },
  
  executeCommand() {
    const input = this.elements.terminalInput.value.trim();
    if (!input) return;
    
    // Display the command
    this.writeToTerminal(`> ${input}`, 'command');
    
    // Clear input
    this.elements.terminalInput.value = '';
    
    // Process command
    this.processCommand(input);
  },
  
  processCommand(command) {
    const args = command.toLowerCase().split(' ');
    const cmd = args[0];
    
    switch (cmd) {
      case 'help':
        this.showHelp();
        break;
      case 'clear':
        this.clearTerminal();
        break;
      case 'status':
        this.showStatus();
        break;
      case 'groups':
        this.listGroups();
        break;
      case 'clients':
        this.listClients();
        break;
      case 'tasks':
        this.listTasks();
        break;
      default:
        this.writeToTerminal(`Unknown command: ${cmd}`, 'error');
        this.writeToTerminal('Type "help" for available commands', 'info');
    }
  },
  
  showHelp() {
    this.writeToTerminal('Available Commands:', 'success');
    this.writeToTerminal('  help     - Show this help message', 'info');
    this.writeToTerminal('  clear    - Clear terminal output', 'info');
    this.writeToTerminal('  status   - Show system status', 'info');
    this.writeToTerminal('  groups   - List all groups', 'info');
    this.writeToTerminal('  clients  - List connected clients', 'info');
    this.writeToTerminal('  tasks    - List active tasks', 'info');
  },
  
  showStatus() {
    this.writeToTerminal('=== System Status ===', 'success');
    this.writeToTerminal(`Groups: ${this.groups.size}`, 'info');
    this.writeToTerminal(`Clients: ${this.clients.size}`, 'info');
    this.writeToTerminal(`Tasks: ${this.tasks.size}`, 'info');
    this.writeToTerminal(`WebSocket: ${this.websocket?.readyState === WebSocket.OPEN ? 'Connected' : 'Disconnected'}`, 'info');
  },
  
  listGroups() {
    if (this.groups.size === 0) {
      this.writeToTerminal('No groups found', 'warning');
      return;
    }
    
    this.writeToTerminal('=== Groups ===', 'success');
    for (const [id, group] of this.groups) {
      this.writeToTerminal(`${group.name} (${group.members?.length || 0} members)`, 'info');
    }
  },
  
  listClients() {
    if (this.clients.size === 0) {
      this.writeToTerminal('No clients connected', 'warning');
      return;
    }
    
    this.writeToTerminal('=== Connected Clients ===', 'success');
    for (const [id, client] of this.clients) {
      this.writeToTerminal(`${client.username} - ${client.status}`, 'info');
    }
  },
  
  listTasks() {
    if (this.tasks.size === 0) {
      this.writeToTerminal('No active tasks', 'warning');
      return;
    }
    
    this.writeToTerminal('=== Active Tasks ===', 'success');
    for (const [id, task] of this.tasks) {
      this.writeToTerminal(`${task.type} - ${task.status} (${task.progress}%)`, 'info');
    }
  },
  
  writeToTerminal(message, type = 'info') {
    if (!this.elements.terminalOutput) return;
    
    const line = document.createElement('div');
    line.className = `terminal-text-${type}`;
    line.textContent = message;
    
    this.elements.terminalOutput.appendChild(line);
    this.elements.terminalOutput.scrollTop = this.elements.terminalOutput.scrollHeight;
  },
  
  clearTerminal() {
    if (this.elements.terminalOutput) {
      this.elements.terminalOutput.innerHTML = '';
      this.writeToTerminal('Terminal cleared', 'system');
    }
  },
  
  handleTabComplete() {
    // Basic tab completion for commands
    const input = this.elements.terminalInput.value;
    const commands = ['help', 'clear', 'status', 'groups', 'clients', 'tasks'];
    
    const matches = commands.filter(cmd => cmd.startsWith(input.toLowerCase()));
    if (matches.length === 1) {
      this.elements.terminalInput.value = matches[0];
    } else if (matches.length > 1) {
      this.writeToTerminal(`Possible completions: ${matches.join(', ')}`, 'info');
    }
  },
  
  // Map rendering
  renderMap() {
    if (!this.map.ctx || !this.map.needsRender) return;
    
    const ctx = this.map.ctx;
    const canvas = this.map.canvas;
    
    // Clear canvas with dark background
    ctx.fillStyle = '#1a1a1a';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // Draw grid
    this.drawGrid(ctx, canvas);
    
    // Draw chunks
    this.drawChunks(ctx, canvas);
    
    // Draw waypoints
    this.drawWaypoints(ctx, canvas);
    
    // Draw players
    this.drawPlayers(ctx, canvas);
    
    this.map.needsRender = false;
  },
  
  drawGrid(ctx, canvas) {
    const gridSize = 64 * this.map.zoom;
    const offsetX = (this.map.centerX * this.map.zoom) % gridSize;
    const offsetZ = (this.map.centerZ * this.map.zoom) % gridSize;
    
    ctx.strokeStyle = '#333333';
    ctx.lineWidth = 1;
    ctx.setLineDash([2, 2]);
    
    // Vertical lines
    for (let x = -offsetX; x < canvas.width + gridSize; x += gridSize) {
      ctx.beginPath();
      ctx.moveTo(x, 0);
      ctx.lineTo(x, canvas.height);
      ctx.stroke();
    }
    
    // Horizontal lines
    for (let z = -offsetZ; z < canvas.height + gridSize; z += gridSize) {
      ctx.beginPath();
      ctx.moveTo(0, z);
      ctx.lineTo(canvas.width, z);
      ctx.stroke();
    }
    
    ctx.setLineDash([]);
  },
  
  drawChunks(ctx, canvas) {
    // Draw loaded chunks as colored rectangles
    for (const [chunkId, chunk] of this.map.chunks) {
      const screenPos = this.worldToScreen(chunk.x * 16, chunk.z * 16);
      const size = 16 * this.map.zoom;
      
      ctx.fillStyle = chunk.loaded ? '#004400' : '#440000';
      ctx.globalAlpha = 0.3;
      ctx.fillRect(screenPos.x, screenPos.y, size, size);
      ctx.globalAlpha = 1.0;
    }
  },
  
  drawWaypoints(ctx, canvas) {
    for (const [id, waypoint] of this.map.waypoints) {
      const screenPos = this.worldToScreen(waypoint.x, waypoint.z);
      
      // Draw waypoint marker
      ctx.fillStyle = '#FFFF00';
      ctx.beginPath();
      ctx.arc(screenPos.x, screenPos.y, 6, 0, Math.PI * 2);
      ctx.fill();
      
      // Draw waypoint name
      ctx.fillStyle = '#FFFFFF';
      ctx.font = '12px monospace';
      ctx.fillText(waypoint.name, screenPos.x + 10, screenPos.y - 10);
    }
  },
  
  drawPlayers(ctx, canvas) {
    for (const [username, player] of this.map.players) {
      const screenPos = this.worldToScreen(player.x, player.z);
      
      // Draw player dot
      ctx.fillStyle = player.isBot ? '#00FFFF' : '#FF5555';
      ctx.beginPath();
      ctx.arc(screenPos.x, screenPos.y, 4, 0, Math.PI * 2);
      ctx.fill();
      
      // Draw player name
      ctx.fillStyle = '#FFFFFF';
      ctx.font = '11px monospace';
      ctx.fillText(username, screenPos.x + 8, screenPos.y - 8);
    }
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
  
  // Map event handlers
  handleMapMouseDown(e) {
    this.map.isDragging = true;
    this.map.lastMouseX = e.offsetX;
    this.map.lastMouseY = e.offsetY;
    this.map.canvas.style.cursor = 'grabbing';
  },
  
  handleMapMouseMove(e) {
    if (this.map.isDragging) {
      const deltaX = e.offsetX - this.map.lastMouseX;
      const deltaY = e.offsetY - this.map.lastMouseY;
      
      this.map.centerX -= deltaX / this.map.zoom;
      this.map.centerZ -= deltaY / this.map.zoom;
      
      this.map.lastMouseX = e.offsetX;
      this.map.lastMouseY = e.offsetY;
      
      this.map.needsRender = true;
      this.renderMap();
    }
    
    // Update coordinate display
    const worldPos = this.screenToWorld(e.offsetX, e.offsetY);
    if (this.elements.coordinates) {
      this.elements.coordinates.textContent = `X: ${Math.round(worldPos.x)}, Z: ${Math.round(worldPos.z)}`;
    }
  },
  
  handleMapMouseUp(e) {
    this.map.isDragging = false;
    this.map.canvas.style.cursor = 'crosshair';
  },
  
  handleMapWheel(e) {
    e.preventDefault();
    
    const zoomFactor = e.deltaY < 0 ? 1.1 : 0.9;
    const newZoom = Math.max(0.1, Math.min(5, this.map.zoom * zoomFactor));
    
    if (newZoom !== this.map.zoom) {
      this.map.zoom = newZoom;
      this.map.needsRender = true;
      this.renderMap();
    }
  },
  
  // Data update methods
  updateGroups(groups) {
    this.groups.clear();
    groups.forEach(group => {
      this.groups.set(group.id, group);
    });
    this.renderGroupsList();
  },
  
  updateClients(clients) {
    this.clients.clear();
    clients.forEach(client => {
      this.clients.set(client.id, client);
    });
    this.renderClientsList();
  },
  
  updateTasks(tasks) {
    this.tasks.clear();
    tasks.forEach(task => {
      this.tasks.set(task.id, task);
    });
    this.renderTasksList();
  },
  
  updateSystemStats(stats) {
    this.systemStats = stats;
    this.renderSystemStats();
  },
  
  updatePlayer(playerData) {
    this.map.players.set(playerData.username, playerData);
    this.map.needsRender = true;
    this.renderMap();
  },
  
  updateMap(mapData) {
    if (mapData.chunks) {
      mapData.chunks.forEach(chunk => {
        this.map.chunks.set(`${chunk.x},${chunk.z}`, chunk);
      });
    }
    
    if (mapData.waypoints) {
      mapData.waypoints.forEach(waypoint => {
        this.map.waypoints.set(waypoint.id, waypoint);
      });
    }
    
    this.map.needsRender = true;
    this.renderMap();
  },
  
  // UI rendering methods
  renderGroupsList() {
    if (!this.elements.groupsList) return;
    
    this.elements.groupsList.innerHTML = '';
    
    if (this.groups.size === 0) {
      const item = document.createElement('div');
      item.className = 'minecraft-list-item';
      item.textContent = 'No groups available';
      this.elements.groupsList.appendChild(item);
      return;
    }
    
    for (const [id, group] of this.groups) {
      const item = document.createElement('div');
      item.className = 'minecraft-list-item';
      item.innerHTML = `
        <div class="group-info">
          <span class="group-color" style="background-color: ${group.color}"></span>
          <span class="group-name">${group.name}</span>
          <span class="group-members">(${group.members?.length || 0})</span>
        </div>
      `;
      item.addEventListener('click', () => this.selectGroup(group));
      this.elements.groupsList.appendChild(item);
    }
  },
  
  renderClientsList() {
    if (!this.elements.clientsList) return;
    
    this.elements.clientsList.innerHTML = '';
    
    if (this.clients.size === 0) {
      const item = document.createElement('div');
      item.className = 'minecraft-list-item';
      item.textContent = 'No clients connected';
      this.elements.clientsList.appendChild(item);
      return;
    }
    
    for (const [id, client] of this.clients) {
      const item = document.createElement('div');
      item.className = 'minecraft-list-item';
      item.innerHTML = `
        <img class="player-avatar" src="https://namemc.com/avatar/${client.username}/16" alt="${client.username}" onerror="this.style.display='none'">
        <span>${client.username}</span>
        <span class="minecraft-badge ${client.status === 'online' ? 'online' : 'offline'}">${client.status}</span>
      `;
      this.elements.clientsList.appendChild(item);
    }
  },
  
  renderTasksList() {
    if (!this.elements.tasksList) return;
    
    this.elements.tasksList.innerHTML = '';
    
    if (this.tasks.size === 0) {
      const item = document.createElement('div');
      item.className = 'minecraft-list-item';
      item.textContent = 'No active tasks';
      this.elements.tasksList.appendChild(item);
      return;
    }
    
    for (const [id, task] of this.tasks) {
      const item = document.createElement('div');
      item.className = 'minecraft-list-item';
      item.innerHTML = `
        <div style="width: 100%;">
          <div style="display: flex; justify-content: space-between; margin-bottom: 2px;">
            <span style="font-size: 9px;">${task.type}</span>
            <span style="font-size: 8px; color: #AAAAAA;">${task.progress}%</span>
          </div>
          <div class="minecraft-progress task">
            <div class="minecraft-progress-fill task" style="width: ${task.progress}%;"></div>
          </div>
        </div>
      `;
      this.elements.tasksList.appendChild(item);
    }
  },
  
  renderSystemStats() {
    if (this.elements.cpuProgress) {
      this.elements.cpuProgress.style.width = `${this.systemStats.cpu || 0}%`;
    }
    if (this.elements.cpuValue) {
      this.elements.cpuValue.textContent = `${this.systemStats.cpu || 0}%`;
    }
    
    if (this.elements.memoryProgress) {
      this.elements.memoryProgress.style.width = `${this.systemStats.memory || 0}%`;
      this.elements.memoryProgress.className = `minecraft-progress-fill ${this.systemStats.memory > 80 ? 'danger' : this.systemStats.memory > 60 ? 'warning' : ''}`;
    }
    if (this.elements.memoryValue) {
      this.elements.memoryValue.textContent = `${this.systemStats.memory || 0}%`;
    }
    
    if (this.elements.systemStatus) {
      this.elements.systemStatus.textContent = this.systemStats.status || 'Unknown';
      this.elements.systemStatus.className = `minecraft-badge ${this.systemStats.status === 'online' ? 'online' : 'offline'}`;
    }
  },
  
  // Utility methods
  showToast(title, message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
      <div style="font-weight: bold; margin-bottom: 4px;">${title}</div>
      <div>${message}</div>
    `;
    
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
  
  loadInitialState(data) {
    if (data) {
      if (data.groups) this.updateGroups(data.groups);
      if (data.clients) this.updateClients(data.clients);
      if (data.tasks) this.updateTasks(data.tasks);
      if (data.systemStats) this.updateSystemStats(data.systemStats);
      if (data.mapData) this.updateMap(data.mapData);
    }
  },
  
  startPeriodicUpdates() {
    // Request updates every 5 seconds
    setInterval(() => {
      if (this.websocket?.readyState === WebSocket.OPEN) {
        this.websocket.send(JSON.stringify({ type: 'request_update' }));
      }
    }, 5000);
    
    // Render map regularly
    setInterval(() => {
      if (this.map.needsRender) {
        this.renderMap();
      }
    }, 100);
  }
};

// Global functions for button callbacks
function createNewGroup() {
  const name = prompt('Enter group name:');
  if (name) {
    MinecraftClient.websocket?.send(JSON.stringify({
      type: 'create_group',
      name: name,
      options: {}
    }));
  }
}

function startAllBots() {
  MinecraftClient.websocket?.send(JSON.stringify({ type: 'start_all_bots' }));
  MinecraftClient.showToast('Bots Starting', 'All bots are being started...', 'info');
}

function stopAllBots() {
  MinecraftClient.websocket?.send(JSON.stringify({ type: 'stop_all_bots' }));
  MinecraftClient.showToast('Bots Stopping', 'All bots are being stopped...', 'warning');
}

function emergencyStop() {
  if (confirm('Emergency stop will immediately halt all operations. Continue?')) {
    MinecraftClient.websocket?.send(JSON.stringify({ type: 'emergency_stop' }));
    MinecraftClient.showToast('Emergency Stop', 'Emergency stop activated!', 'error');
  }
}

function createTask() {
  const taskType = MinecraftClient.elements.taskType?.value || 'gather';
  MinecraftClient.websocket?.send(JSON.stringify({
    type: 'create_task',
    taskType: taskType
  }));
  MinecraftClient.showToast('Task Created', `New ${taskType} task created`, 'success');
}

function clearTerminal() {
  MinecraftClient.clearTerminal();
}

function toggleTerminalSize() {
  const terminal = document.querySelector('.minecraft-terminal');
  if (terminal) {
    terminal.style.height = terminal.style.height === '600px' ? '400px' : '600px';
  }
}

function minimizeTerminal() {
  const terminal = document.querySelector('.minecraft-terminal');
  if (terminal) {
    terminal.style.display = terminal.style.display === 'none' ? 'flex' : 'none';
  }
}

// Initialize when DOM is loaded
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    MinecraftClient.init();
  });
} else {
  MinecraftClient.init();
}

// Make client globally available
window.MinecraftClient = MinecraftClient;