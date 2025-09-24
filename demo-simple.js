#!/usr/bin/env node

/**
 * Simplified Demo for AppyProx Xaeros Interface
 * Standalone version that doesn't depend on the full AppyProx system
 */

const http = require('http');
const url = require('url');
const fs = require('fs');
const path = require('path');

// Mock data for demonstration
const mockData = {
  startTime: Date.now(),
  players: [
    {
      clientId: 'client_1',
      username: 'MiningBot_Alpha',
      status: 'mining',
      health: 20,
      position: { x: -150, y: 64, z: 200 },
      rotation: { yaw: 45, pitch: 0 },
      lastUpdate: new Date().toISOString(),
      connected: true
    },
    {
      clientId: 'client_2', 
      username: 'BuilderBot_Beta',
      status: 'building',
      health: 18,
      position: { x: 300, y: 80, z: -100 },
      rotation: { yaw: 180, pitch: 0 },
      lastUpdate: new Date().toISOString(),
      connected: true
    },
    {
      clientId: 'client_3',
      username: 'ExplorerBot_Gamma', 
      status: 'exploring',
      health: 20,
      position: { x: -500, y: 72, z: -300 },
      rotation: { yaw: 270, pitch: 0 },
      lastUpdate: new Date().toISOString(),
      connected: true
    },
    {
      clientId: 'client_4',
      username: 'FarmBot_Delta',
      status: 'farming', 
      health: 19,
      position: { x: 50, y: 65, z: 150 },
      rotation: { yaw: 90, pitch: 0 },
      lastUpdate: new Date().toISOString(),
      connected: true
    }
  ],
  tasks: [
    {
      taskId: 'task_mining_001',
      type: 'gather_resource',
      status: 'running',
      progress: 65,
      assignedBot: 'MiningBot_Alpha',
      target: { resource: 'diamond_ore', quantity: 64 }
    },
    {
      taskId: 'task_build_002', 
      type: 'build_structure',
      status: 'running',
      progress: 40,
      assignedBot: 'BuilderBot_Beta',
      target: { structure: 'castle', size: '50x50' }
    },
    {
      taskId: 'task_explore_003',
      type: 'explore_area',
      status: 'running', 
      progress: 85,
      assignedBot: 'ExplorerBot_Gamma',
      target: { area: 'new_biome', radius: 1000 }
    }
  ],
  logs: [
    {
      timestamp: new Date().toISOString(),
      level: 'INFO',
      message: 'Demo interface started with mock data',
      component: 'DemoServer'
    },
    {
      timestamp: new Date(Date.now() - 30000).toISOString(),
      level: 'INFO',
      message: 'MiningBot_Alpha found diamond ore at -150, 64, 200',
      component: 'AutomationEngine'
    },
    {
      timestamp: new Date(Date.now() - 60000).toISOString(),
      level: 'WARN',
      message: 'BuilderBot_Beta inventory 80% full',
      component: 'InventoryManager'
    }
  ]
};

// Simulate live data updates
setInterval(() => {
  // Update player positions
  mockData.players.forEach(player => {
    if (player.connected) {
      const moveDistance = Math.random() * 10;
      const angle = Math.random() * 2 * Math.PI;
      
      player.position.x += Math.cos(angle) * moveDistance;
      player.position.z += Math.sin(angle) * moveDistance;
      player.rotation.yaw = (player.rotation.yaw + (Math.random() - 0.5) * 45) % 360;
      player.lastUpdate = new Date().toISOString();
    }
  });
  
  // Update task progress
  mockData.tasks.forEach(task => {
    if (task.status === 'running' && task.progress < 100) {
      task.progress += Math.random() * 3;
      task.progress = Math.min(100, task.progress);
      
      if (task.progress >= 100) {
        task.status = 'completed';
      }
    }
  });
  
  // Add occasional log entry
  if (Math.random() < 0.3) {
    const messages = [
      'System health check completed',
      'Player coordination updated',
      'Resource gathering in progress',
      'Building construction advancing',
      'Exploration discovered new area'
    ];
    
    mockData.logs.unshift({
      timestamp: new Date().toISOString(),
      level: Math.random() < 0.8 ? 'INFO' : 'WARN',
      message: messages[Math.floor(Math.random() * messages.length)],
      component: 'DemoSystem'
    });
    
    // Keep only last 20 logs
    if (mockData.logs.length > 20) {
      mockData.logs.pop();
    }
  }
}, 3000);

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const { pathname } = parsedUrl;
  
  // CORS headers for local development
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
  
  if (pathname === '/' || pathname === '/index.html') {
    // Serve the HTML
    res.setHeader('Content-Type', 'text/html');
    res.writeHead(200);
    res.end(generateHTML());
  } else if (pathname === '/style.css') {
    // Serve CSS from the main CentralNode file
    try {
      const centralNodePath = path.join(__dirname, 'src/central-node/CentralNode.js');
      const centralNodeContent = fs.readFileSync(centralNodePath, 'utf8');
      
      // Extract CSS from the file
      const cssMatch = centralNodeContent.match(/generateCSS\(\) \{[\s\S]*?return `([\s\S]*?)`;[\s\S]*?\}/);
      const css = cssMatch ? cssMatch[1] : '';
      
      res.setHeader('Content-Type', 'text/css');
      res.writeHead(200);
      res.end(css);
    } catch (error) {
      res.writeHead(500);
      res.end('Error loading CSS');
    }
  } else if (pathname === '/script.js') {
    // Serve JavaScript from the main CentralNode file
    try {
      const centralNodePath = path.join(__dirname, 'src/central-node/CentralNode.js');
      const centralNodeContent = fs.readFileSync(centralNodePath, 'utf8');
      
      // Extract JavaScript from the file
      const jsMatch = centralNodeContent.match(/generateJavaScript\(\) \{[\s\S]*?return `([\s\S]*?)`;[\s\S]*?\}/);
      const js = jsMatch ? jsMatch[1] : '';
      
      res.setHeader('Content-Type', 'application/javascript');
      res.writeHead(200);
      res.end(js);
    } catch (error) {
      res.writeHead(500);
      res.end('Error loading JavaScript');
    }
  } else if (pathname === '/api/status') {
    res.setHeader('Content-Type', 'application/json');
    res.writeHead(200);
    res.end(JSON.stringify({
      isRunning: true,
      uptime: Date.now() - mockData.startTime,
      connectedAccounts: mockData.players.filter(p => p.connected).length,
      activeTasks: mockData.tasks.filter(t => t.status === 'running').length,
      totalConnections: mockData.players.length,
      activeConnections: mockData.players.filter(p => p.connected).length,
      webInterface: { port: 25577, enabled: true }
    }));
  } else if (pathname === '/api/accounts') {
    res.setHeader('Content-Type', 'application/json');
    res.writeHead(200);
    res.end(JSON.stringify(mockData.players));
  } else if (pathname === '/api/tasks') {
    res.setHeader('Content-Type', 'application/json');
    res.writeHead(200);
    res.end(JSON.stringify(mockData.tasks));
  } else if (pathname === '/api/logs') {
    res.setHeader('Content-Type', 'application/json');
    res.writeHead(200);
    res.end(JSON.stringify(mockData.logs));
  } else {
    res.writeHead(404);
    res.end('Not Found');
  }
});

function generateHTML() {
  const uptime = Date.now() - mockData.startTime;
  const connectedPlayers = mockData.players.filter(p => p.connected).length;
  const activeTasks = mockData.tasks.filter(t => t.status === 'running').length;
  
  return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AppyProx Demo - Xaeros World Map Interface</title>
    <link rel="stylesheet" href="/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@300;400;500&display=swap" rel="stylesheet">
    <script src="/script.js" defer></script>
</head>
<body>
    <!-- Demo Banner -->
    <div id="demo-banner" style="background: linear-gradient(45deg, #ff6b6b, #4ecdc4); padding: 8px; text-align: center; color: white; font-weight: bold; font-size: 14px; box-shadow: 0 2px 4px rgba(0,0,0,0.3);">
        🎮 DEMO MODE - Live simulated data with ${mockData.players.length} bot players, real-time tasks, and interactive features
    </div>
    
    <!-- Top Status Bar -->
    <div id="status-bar">
        <div class="status-left">
            <div class="server-status online">
                <div class="status-dot"></div>
                <span>AppyProx Online</span>
            </div>
            <div class="world-info">
                <span>🌍 Overworld</span>
                <span id="coords">X: 0 Z: 0</span>
                <span id="players-online">${connectedPlayers} Players</span>
            </div>
        </div>
        <div class="status-right">
            <div class="system-stats">
                <span id="uptime">⏱ ${formatUptime(uptime)}</span>
                <span id="tasks-count">📋 ${activeTasks} Tasks</span>
                <button id="fullscreen-btn" onclick="toggleFullscreen()">⛶</button>
            </div>
        </div>
    </div>

    <!-- Main Interface -->
    <div id="main-container">
        <!-- Left Side: Map Interface -->
        <div id="map-container">
            <!-- Map Controls -->
            <div id="map-controls">
                <div class="control-group">
                    <button id="zoom-in" onclick="mapZoom(1)">+</button>
                    <button id="zoom-out" onclick="mapZoom(-1)">-</button>
                </div>
                <div class="control-group">
                    <button id="center-spawn" onclick="centerOnSpawn()">🏠</button>
                    <button id="toggle-grid" onclick="toggleGrid()">#</button>
                    <button id="toggle-waypoints" onclick="toggleWaypoints()">📍</button>
                </div>
                <div class="control-group layers">
                    <select id="dimension-select" onchange="changeDimension()">
                        <option value="overworld">Overworld</option>
                        <option value="nether">Nether</option>
                        <option value="end">End</option>
                    </select>
                </div>
            </div>

            <!-- Main Map Canvas -->
            <canvas id="world-map" width="800" height="600"></canvas>

            <!-- Minimap -->
            <div id="minimap-container">
                <canvas id="minimap" width="120" height="120"></canvas>
                <div id="minimap-overlay">
                    <div class="minimap-coords" id="minimap-coords">X: 0, Z: 0</div>
                </div>
            </div>

            <!-- Player List Overlay -->
            <div id="players-overlay">
                <div class="overlay-header">
                    <h3>🎮 Active Players</h3>
                    <button onclick="togglePlayersOverlay()">×</button>
                </div>
                <div id="players-list" class="scrollable-list">
                    ${generatePlayersList()}
                </div>
            </div>

            <!-- Waypoints Panel -->
            <div id="waypoints-panel">
                <div class="panel-header">
                    <h3>📍 Waypoints</h3>
                    <button onclick="toggleWaypointsPanel()">×</button>
                </div>
                <div id="waypoints-list" class="scrollable-list">
                    <p style="color: #8b949e;">No waypoints</p>
                </div>
                <div class="panel-footer">
                    <button onclick="addWaypoint()">+ Add Waypoint</button>
                </div>
            </div>
        </div>

        <!-- Right Side: Terminal Interface -->
        <div id="terminal-container">
            <div id="terminal-header">
                <div class="terminal-tabs">
                    <div class="tab active" data-tab="console" onclick="switchTab('console')">Console</div>
                    <div class="tab" data-tab="logs" onclick="switchTab('logs')">Logs</div>
                    <div class="tab" data-tab="tasks" onclick="switchTab('tasks')">Tasks</div>
                    <div class="tab" data-tab="api" onclick="switchTab('api')">API</div>
                </div>
                <div class="terminal-controls">
                    <button onclick="clearTerminal()">Clear</button>
                    <button onclick="toggleTerminal()">_</button>
                </div>
            </div>
            
            <div id="terminal-content">
                <!-- Console Tab -->
                <div id="console-tab" class="tab-content active">
                    <div id="terminal-output" class="terminal-output"></div>
                    <div id="terminal-input-container">
                        <span class="terminal-prompt">appyprox@demo:~$</span>
                        <input type="text" id="terminal-input" placeholder="Enter command..." onkeydown="handleTerminalInput(event)">
                    </div>
                </div>
                
                <!-- Logs Tab -->
                <div id="logs-tab" class="tab-content">
                    <div id="logs-output" class="terminal-output">
                        <div class="log-filters">
                            <select id="log-level" onchange="filterLogs()">
                                <option value="all">All Levels</option>
                                <option value="error">Error</option>
                                <option value="warn">Warning</option>
                                <option value="info">Info</option>
                                <option value="debug">Debug</option>
                            </select>
                            <button onclick="exportLogs()">Export</button>
                        </div>
                        <div id="logs-list"></div>
                    </div>
                </div>
                
                <!-- Tasks Tab -->
                <div id="tasks-tab" class="tab-content">
                    <div id="tasks-output" class="terminal-output">
                        <div class="tasks-header">
                            <h4>📋 Active Tasks</h4>
                            <button onclick="refreshTasks()">🔄</button>
                        </div>
                        <div id="active-tasks-list">
                            ${generateTasksList()}
                        </div>
                    </div>
                </div>
                
                <!-- API Tab -->
                <div id="api-tab" class="tab-content">
                    <div id="api-output" class="terminal-output">
                        <div class="api-tester">
                            <div class="api-input-group">
                                <select id="api-method">
                                    <option value="GET">GET</option>
                                    <option value="POST">POST</option>
                                    <option value="PUT">PUT</option>
                                    <option value="DELETE">DELETE</option>
                                </select>
                                <input type="text" id="api-endpoint" placeholder="/api/endpoint" value="/api/status">
                                <button onclick="executeApiCall()">Send</button>
                            </div>
                            <textarea id="api-body" placeholder="Request body (JSON)..." rows="4"></textarea>
                            <div id="api-response"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Context Menu -->
    <div id="context-menu" class="context-menu">
        <div class="menu-item" onclick="contextAction('teleport')">🚀 Teleport Here</div>
        <div class="menu-item" onclick="contextAction('waypoint')">📍 Add Waypoint</div>
        <div class="menu-item" onclick="contextAction('center')">🎯 Center View</div>
    </div>
    
    <!-- Tooltip -->
    <div id="tooltip" class="tooltip"></div>
</body>
</html>`;
}

function generatePlayersList() {
  if (mockData.players.length === 0) {
    return '<p style="color: #8b949e;">No players online</p>';
  }
  
  return mockData.players.map(player => `
    <div class="player-item">
      <h5>${player.username}</h5>
      <p>Status: ${player.status}</p>
      <p>Health: ${player.health}/20</p>
      <p>Position: ${Math.round(player.position.x)}, ${Math.round(player.position.z)}</p>
    </div>
  `).join('');
}

function generateTasksList() {
  if (mockData.tasks.length === 0) {
    return '<p style="color: #8b949e;">No active tasks</p>';
  }
  
  return mockData.tasks.map(task => `
    <div class="task-item">
      <h5>${task.type}</h5>
      <div class="progress-bar">
        <div class="progress-fill" style="width: ${task.progress}%"></div>
      </div>
      <p>Status: ${task.status}</p>
      <p>Progress: ${Math.round(task.progress)}%</p>
    </div>
  `).join('');
}

function formatUptime(uptime) {
  const seconds = Math.floor(uptime / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  
  if (days > 0) {
    return `${days}d ${hours % 24}h ${minutes % 60}m`;
  } else if (hours > 0) {
    return `${hours}h ${minutes % 60}m ${seconds % 60}s`;
  } else if (minutes > 0) {
    return `${minutes}m ${seconds % 60}s`;
  } else {
    return `${seconds}s`;
  }
}

const PORT = 25577;
server.listen(PORT, () => {
  console.log('\n🎮 AppyProx Demo Interface Started!');
  console.log(`🚀 Open: http://localhost:${PORT}`);
  console.log('\n🗺️  Features:');
  console.log('   • Interactive Xaeros-style world map');
  console.log(`   • ${mockData.players.length} simulated bot players`);
  console.log('   • Real-time movement and task updates');
  console.log('   • Integrated terminal with commands');
  console.log('   • Live system monitoring');
  console.log('\n🎯 Terminal Commands:');
  console.log('   help, status, players, tp <x> <z>, waypoint <name> <x> <z>');
  console.log('\n🖱️  Controls:');
  console.log('   Click & Drag: Pan • Mouse Wheel: Zoom • Right Click: Menu');
  console.log('\n✨ Press Ctrl+C to stop');
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\n🛑 Demo stopped');
  server.close();
  process.exit(0);
});

process.on('SIGTERM', () => {
  console.log('\n🛑 Demo stopped');
  server.close();
  process.exit(0);
});