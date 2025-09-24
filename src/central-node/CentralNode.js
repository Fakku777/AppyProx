const EventEmitter = require('events');
const http = require('http');
const path = require('path');
const fs = require('fs');
const WebSocketManager = require('./WebSocketManager');

/**
 * Central management node for monitoring and controlling clusters
 * Provides web interface for real-time monitoring and control
 */
class CentralNode extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('CentralNode') : logger;
    this.isRunning = false;
    this.server = null;
    
    // WebSocket manager for real-time updates
    this.wsManager = new WebSocketManager(this.config.websocket_port || 8081, this.logger);
    
    // Account tracking
    this.accountStatuses = new Map();
    this.taskProgress = new Map();
    this.clusterStatuses = new Map();
    
    // Comprehensive system monitoring
    this.systemStats = {
      startTime: null,
      uptime: 0,
      totalConnections: 0,
      activeConnections: 0,
      memoryUsage: process.memoryUsage(),
      cpuUsage: process.cpuUsage()
    };
    
    // Performance metrics and analytics
    this.performanceMetrics = {
      taskCompletionRates: new Map(),
      clusterEfficiency: new Map(),
      resourceUtilization: new Map(),
      errorRates: new Map(),
      responseTimeHistory: [],
      throughputHistory: []
    };
    
    // Real-time monitoring data
    this.realTimeData = {
      activeTasks: 0,
      completedTasksHour: 0,
      failedTasksHour: 0,
      averageResponseTime: 0,
      networkTraffic: { in: 0, out: 0 },
      resourceGathered: { total: 0, hourly: 0 },
      connectionHealth: { healthy: 0, degraded: 0, failed: 0 }
    };
    
    // Analytics intervals
    this.metricsInterval = null;
    this.cleanupInterval = null;
    
    // Alert system
    this.alerts = [];
    this.alertThresholds = {
      errorRate: 0.1, // 10% error rate triggers alert
      responseTime: 5000, // 5 second response time triggers alert
      memoryUsage: 0.9, // 90% memory usage triggers alert
      taskFailureRate: 0.2 // 20% task failure rate triggers alert
    };
  }

  async start() {
    if (this.isRunning) return;
    
    this.logger.info('Starting central management node...');
    
    // Start WebSocket server for real-time updates
    await this.wsManager.start();
    
    // Start web server
    await this.startWebServer();
    
    // Initialize monitoring systems
    this.startMonitoring();
    
    this.systemStats.startTime = Date.now();
    this.isRunning = true;
    
    this.logger.info(`Central node web interface started on port ${this.config.web_interface_port}`);
    this.logger.info(`WebSocket server started on port ${this.config.websocket_port || 8081}`);
  }

  async stop() {
    if (!this.isRunning) return;
    
    this.logger.info('Stopping central management node...');
    
    // Stop monitoring intervals
    if (this.metricsInterval) {
      clearInterval(this.metricsInterval);
      this.metricsInterval = null;
    }
    
    if (this.cleanupInterval) {
      clearInterval(this.cleanupInterval);
      this.cleanupInterval = null;
    }
    
    // Stop WebSocket server
    if (this.wsManager) {
      await this.wsManager.stop();
    }
    
    // Stop web server
    if (this.server) {
      await new Promise((resolve) => {
        this.server.close(resolve);
      });
      this.server = null;
    }
    
    this.isRunning = false;
    this.logger.info('Central node stopped');
  }

  updateAccountStatus(status) {
    this.accountStatuses.set(status.clientId, {
      ...status,
      lastUpdate: Date.now()
    });
    
    this.logger.debug(`Updated status for account ${status.clientId}`);
  }

  updateTaskProgress(progress) {
    this.taskProgress.set(progress.taskId, {
      ...progress,
      lastUpdate: Date.now()
    });
    
    this.logger.debug(`Updated progress for task ${progress.taskId}: ${progress.progress}%`);
  }

  async startWebServer() {
    const port = this.config.web_interface_port;
    
    this.server = http.createServer((req, res) => {
      this.handleRequest(req, res);
    });
    
    return new Promise((resolve, reject) => {
      this.server.listen(port, '0.0.0.0', (err) => {
        if (err) {
          reject(err);
        } else {
          resolve();
        }
      });
    });
  }
  
  handleRequest(req, res) {
    const url = req.url === '/' ? '/index.html' : req.url;
    
    // API endpoints
    if (url.startsWith('/api/')) {
      return this.handleApiRequest(req, res);
    }
    
    // Serve static content
    this.serveStaticContent(url, res);
  }
  
  handleApiRequest(req, res) {
    const path = req.url.replace('/api', '');
    res.setHeader('Content-Type', 'application/json');
    res.setHeader('Access-Control-Allow-Origin', '*');
    
    switch (path) {
      case '/status':
        res.writeHead(200);
        res.end(JSON.stringify(this.getEnhancedSystemStatus()));
        break;
      case '/accounts':
        res.writeHead(200);
        res.end(JSON.stringify(Array.from(this.accountStatuses.values())));
        break;
      case '/tasks':
        res.writeHead(200);
        res.end(JSON.stringify(Array.from(this.taskProgress.values())));
        break;
      case '/monitoring':
        res.writeHead(200);
        res.end(JSON.stringify(this.getMonitoringData()));
        break;
      case '/performance':
        res.writeHead(200);
        res.end(JSON.stringify(this.getPerformanceReport()));
        break;
      case '/health':
        res.writeHead(200);
        res.end(JSON.stringify(this.getSystemHealth()));
        break;
      case '/alerts':
        res.writeHead(200);
        res.end(JSON.stringify(this.alerts.filter(a => !a.acknowledged)));
        break;
      case '/clusters':
        res.writeHead(200);
        res.end(JSON.stringify(Array.from(this.clusterStatuses.values())));
        break;
      default:
        res.writeHead(404);
        res.end(JSON.stringify({ error: 'API endpoint not found' }));
    }
  }
  
  serveStaticContent(url, res) {
    // Generate HTML content
    if (url === '/index.html' || url === '/') {
      const html = this.generateDashboardHTML();
      res.setHeader('Content-Type', 'text/html');
      res.writeHead(200);
      res.end(html);
    } else if (url === '/style.css') {
      const css = this.generateCSS();
      res.setHeader('Content-Type', 'text/css');
      res.writeHead(200);
      res.end(css);
    } else if (url === '/script.js') {
      const js = this.generateJavaScript();
      res.setHeader('Content-Type', 'application/javascript');
      res.writeHead(200);
      res.end(js);
    } else {
      res.writeHead(404);
      res.end('Not Found');
    }
  }
  
  generateDashboardHTML() {
    const status = this.getSystemStatus();
    return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AppyProx - Xaeros World Map Interface</title>
    <link rel="stylesheet" href="/style.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Minecraft:wght@400&family=JetBrains+Mono:wght@300;400;500&display=swap" rel="stylesheet">
    <script src="/script.js" defer></script>
</head>
<body>
    <!-- Top Status Bar -->
    <div id="status-bar">
        <div class="status-left">
            <div class="server-status ${status.isRunning ? 'online' : 'offline'}">
                <div class="status-dot"></div>
                <span>AppyProx ${status.isRunning ? 'Online' : 'Offline'}</span>
            </div>
            <div class="world-info">
                <span>🌍 Overworld</span>
                <span id="coords">X: 0 Z: 0</span>
                <span id="players-online">${status.connectedAccounts} Players</span>
            </div>
        </div>
        <div class="status-right">
            <div class="system-stats">
                <span id="uptime">⏱ ${this.formatUptime(status.uptime)}</span>
                <span id="tasks-count">📋 ${status.activeTasks} Tasks</span>
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
                    ${this.generatePlayersList()}
                </div>
            </div>

            <!-- Waypoints Panel -->
            <div id="waypoints-panel">
                <div class="panel-header">
                    <h3>📍 Waypoints</h3>
                    <button onclick="toggleWaypointsPanel()">×</button>
                </div>
                <div id="waypoints-list" class="scrollable-list">
                    ${this.generateWaypointsList()}
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
                        <span class="terminal-prompt">appyprox@central:~$</span>
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
                            ${this.generateTasksList()}
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
                                <input type="text" id="api-endpoint" placeholder="/api/endpoint" value="/status">
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
        <div class="menu-item" onclick="contextAction('info')">ℹ Block Info</div>
        <div class="menu-separator"></div>
        <div class="menu-item" onclick="contextAction('center')">🎯 Center View</div>
    </div>

    <!-- Tooltip -->
    <div id="tooltip" class="tooltip"></div>
    
    <!-- Connection Status -->
    <div id="connection-status" class="connection-indicator connected">
        <div class="connection-dot"></div>
    </div>
</body>
</html>`;
  }
  
  generateCSS() {
    return `/* Xaeros World Map Style Interface */

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html, body {
    height: 100%;
    overflow: hidden;
}

body {
    font-family: 'JetBrains Mono', monospace;
    background: #1a1a1a;
    color: #ffffff;
    position: relative;
}

/* Top Status Bar */
#status-bar {
    height: 32px;
    background: linear-gradient(180deg, #383838 0%, #2d2d2d 100%);
    border-bottom: 2px solid #555555;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    font-size: 11px;
    z-index: 1000;
    box-shadow: inset 1px 1px 0 rgba(255,255,255,0.1);
}

.status-left, .status-right {
    display: flex;
    align-items: center;
    gap: 12px;
}

.server-status {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 2px 8px;
    background: rgba(0,0,0,0.3);
    border: 1px solid #555;
    border-radius: 3px;
}

.status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #4ade80;
    box-shadow: 0 0 4px #4ade80;
}

.server-status.offline .status-dot {
    background: #ef4444;
    box-shadow: 0 0 4px #ef4444;
}

.world-info span {
    color: #cccccc;
    margin-right: 8px;
}

.system-stats {
    display: flex;
    align-items: center;
    gap: 8px;
}

#fullscreen-btn {
    background: none;
    border: 1px solid #555;
    color: #ccc;
    padding: 2px 6px;
    font-size: 10px;
    cursor: pointer;
}

/* Main Container */
#main-container {
    display: flex;
    height: calc(100vh - 32px);
}

/* Map Container */
#map-container {
    flex: 1;
    position: relative;
    background: #0d1117;
    overflow: hidden;
}

/* Map Controls */
#map-controls {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 100;
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.control-group {
    background: rgba(0, 0, 0, 0.8);
    border: 2px solid #555;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: inset 1px 1px 0 rgba(255,255,255,0.1);
}

.control-group button {
    background: linear-gradient(180deg, #5a5a5a 0%, #3d3d3d 100%);
    border: none;
    color: #ffffff;
    padding: 6px 12px;
    cursor: pointer;
    font-size: 12px;
    font-family: 'JetBrains Mono', monospace;
    border-right: 1px solid #666;
    transition: all 0.1s;
}

.control-group button:last-child {
    border-right: none;
}

.control-group button:hover {
    background: linear-gradient(180deg, #6a6a6a 0%, #4d4d4d 100%);
    box-shadow: inset 0 1px 2px rgba(255,255,255,0.2);
}

.control-group button:active {
    background: linear-gradient(180deg, #3d3d3d 0%, #5a5a5a 100%);
    box-shadow: inset 0 1px 3px rgba(0,0,0,0.3);
}

.layers select {
    background: linear-gradient(180deg, #5a5a5a 0%, #3d3d3d 100%);
    border: none;
    color: #ffffff;
    padding: 6px 8px;
    font-size: 11px;
    font-family: 'JetBrains Mono', monospace;
}

/* World Map Canvas */
#world-map {
    width: 100%;
    height: 100%;
    display: block;
    background: 
        radial-gradient(circle at 25% 25%, #1a472a 0%, transparent 50%),
        radial-gradient(circle at 75% 75%, #1a365d 0%, transparent 50%),
        linear-gradient(45deg, #0f172a 0%, #1e293b 100%);
    cursor: crosshair;
}

/* Minimap */
#minimap-container {
    position: absolute;
    top: 10px;
    right: 10px;
    width: 140px;
    height: 140px;
    background: rgba(0, 0, 0, 0.8);
    border: 2px solid #555;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: inset 1px 1px 0 rgba(255,255,255,0.1);
}

#minimap {
    width: 120px;
    height: 120px;
    margin: 10px;
    display: block;
    background: 
        linear-gradient(45deg, #2d3748 25%, transparent 25%),
        linear-gradient(-45deg, #2d3748 25%, transparent 25%),
        linear-gradient(45deg, transparent 75%, #2d3748 75%),
        linear-gradient(-45deg, transparent 75%, #2d3748 75%);
    background-size: 8px 8px;
    background-position: 0 0, 0 4px, 4px -4px, -4px 0px;
}

#minimap-overlay {
    position: absolute;
    bottom: 2px;
    left: 2px;
    right: 2px;
    text-align: center;
}

.minimap-coords {
    background: rgba(0, 0, 0, 0.7);
    color: #fff;
    font-size: 9px;
    padding: 2px 4px;
    border-radius: 2px;
}

/* Overlays */
#players-overlay, #waypoints-panel {
    position: absolute;
    top: 60px;
    left: 10px;
    width: 250px;
    max-height: 300px;
    background: rgba(0, 0, 0, 0.9);
    border: 2px solid #555;
    border-radius: 4px;
    overflow: hidden;
    display: none;
    box-shadow: inset 1px 1px 0 rgba(255,255,255,0.1);
}

.overlay-header, .panel-header {
    background: linear-gradient(180deg, #5a5a5a 0%, #3d3d3d 100%);
    padding: 6px 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #666;
}

.overlay-header h3, .panel-header h3 {
    font-size: 12px;
    color: #ffffff;
}

.overlay-header button, .panel-header button {
    background: none;
    border: none;
    color: #ccc;
    cursor: pointer;
    font-size: 14px;
    padding: 0 4px;
}

.scrollable-list {
    max-height: 200px;
    overflow-y: auto;
    padding: 8px;
}

.panel-footer {
    padding: 6px 8px;
    border-top: 1px solid #666;
    background: rgba(0, 0, 0, 0.3);
}

.panel-footer button {
    width: 100%;
    background: linear-gradient(180deg, #4a5568 0%, #2d3748 100%);
    border: 1px solid #555;
    color: #fff;
    padding: 4px;
    font-size: 11px;
    cursor: pointer;
}

/* Terminal Container */
#terminal-container {
    width: 400px;
    background: #0d1117;
    border-left: 2px solid #555;
    display: flex;
    flex-direction: column;
    min-height: 100%;
}

#terminal-header {
    height: 32px;
    background: linear-gradient(180deg, #383838 0%, #2d2d2d 100%);
    border-bottom: 1px solid #555;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: inset 1px 1px 0 rgba(255,255,255,0.1);
}

.terminal-tabs {
    display: flex;
    height: 100%;
}

.tab {
    padding: 0 12px;
    background: #2d2d2d;
    border-right: 1px solid #555;
    color: #888;
    cursor: pointer;
    font-size: 11px;
    display: flex;
    align-items: center;
    transition: all 0.2s;
}

.tab:hover {
    background: #3d3d3d;
    color: #ccc;
}

.tab.active {
    background: #0d1117;
    color: #fff;
    border-bottom: 2px solid #4ade80;
}

.terminal-controls {
    display: flex;
    gap: 4px;
    padding: 0 8px;
}

.terminal-controls button {
    background: none;
    border: 1px solid #555;
    color: #ccc;
    padding: 2px 6px;
    font-size: 10px;
    cursor: pointer;
}

/* Terminal Content */
#terminal-content {
    flex: 1;
    overflow: hidden;
}

.tab-content {
    display: none;
    height: 100%;
    flex-direction: column;
}

.tab-content.active {
    display: flex;
}

.terminal-output {
    flex: 1;
    overflow-y: auto;
    padding: 8px;
    font-family: 'JetBrains Mono', monospace;
    font-size: 11px;
    line-height: 1.4;
    background: #0d1117;
    color: #c9d1d9;
}

#terminal-input-container {
    display: flex;
    align-items: center;
    padding: 8px;
    background: #161b22;
    border-top: 1px solid #555;
}

.terminal-prompt {
    color: #4ade80;
    margin-right: 6px;
    font-size: 11px;
}

#terminal-input {
    flex: 1;
    background: transparent;
    border: none;
    color: #c9d1d9;
    font-family: 'JetBrains Mono', monospace;
    font-size: 11px;
    outline: none;
}

/* Log Filters */
.log-filters {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 0 8px 0;
    border-bottom: 1px solid #333;
    margin-bottom: 8px;
}

.log-filters select {
    background: #161b22;
    border: 1px solid #555;
    color: #c9d1d9;
    padding: 2px 4px;
    font-size: 10px;
}

.log-filters button {
    background: #238636;
    border: 1px solid #2ea043;
    color: #fff;
    padding: 2px 6px;
    font-size: 10px;
    cursor: pointer;
}

/* Tasks Header */
.tasks-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 0 8px 0;
    border-bottom: 1px solid #333;
    margin-bottom: 8px;
}

.tasks-header h4 {
    font-size: 12px;
    color: #c9d1d9;
}

.tasks-header button {
    background: none;
    border: 1px solid #555;
    color: #ccc;
    padding: 2px 6px;
    font-size: 10px;
    cursor: pointer;
}

/* API Tester */
.api-tester {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.api-input-group {
    display: flex;
    gap: 4px;
}

.api-input-group select,
.api-input-group input {
    background: #161b22;
    border: 1px solid #555;
    color: #c9d1d9;
    padding: 4px;
    font-size: 10px;
    font-family: 'JetBrains Mono', monospace;
}

.api-input-group input {
    flex: 1;
}

.api-input-group button {
    background: #0969da;
    border: 1px solid #1f6feb;
    color: #fff;
    padding: 4px 8px;
    font-size: 10px;
    cursor: pointer;
}

#api-body {
    background: #161b22;
    border: 1px solid #555;
    color: #c9d1d9;
    font-family: 'JetBrains Mono', monospace;
    font-size: 10px;
    padding: 6px;
    resize: vertical;
}

#api-response {
    background: #0d1117;
    border: 1px solid #333;
    padding: 8px;
    font-size: 10px;
    max-height: 200px;
    overflow-y: auto;
    white-space: pre-wrap;
}

/* Context Menu */
.context-menu {
    position: absolute;
    background: rgba(0, 0, 0, 0.95);
    border: 2px solid #555;
    border-radius: 4px;
    padding: 4px 0;
    z-index: 1000;
    display: none;
    box-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.menu-item {
    padding: 6px 12px;
    cursor: pointer;
    font-size: 11px;
    color: #c9d1d9;
    transition: background 0.1s;
}

.menu-item:hover {
    background: #264f78;
}

.menu-separator {
    height: 1px;
    background: #555;
    margin: 2px 0;
}

/* Tooltip */
.tooltip {
    position: absolute;
    background: rgba(0, 0, 0, 0.9);
    color: #fff;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 11px;
    pointer-events: none;
    z-index: 1001;
    display: none;
    border: 1px solid #555;
}

/* Connection Indicator */
.connection-indicator {
    position: fixed;
    bottom: 10px;
    right: 10px;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    z-index: 1000;
}

.connection-dot {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: #4ade80;
    box-shadow: 0 0 6px #4ade80;
    animation: pulse 2s infinite;
}

.connection-indicator.disconnected .connection-dot {
    background: #ef4444;
    box-shadow: 0 0 6px #ef4444;
}

@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

/* Player/Task Items */
.player-item, .task-item {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid #333;
    border-radius: 3px;
    padding: 6px;
    margin-bottom: 4px;
    font-size: 10px;
}

.player-item h5, .task-item h5 {
    color: #4ade80;
    margin-bottom: 2px;
    font-size: 11px;
}

.player-item p, .task-item p {
    color: #8b949e;
    margin-bottom: 1px;
    font-size: 9px;
}

/* Progress bars */
.progress-bar {
    width: 100%;
    height: 4px;
    background: #333;
    border-radius: 2px;
    overflow: hidden;
    margin-top: 2px;
}

.progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #4ade80 0%, #22c55e 100%);
    transition: width 0.3s ease;
}

/* Scrollbar styling */
::-webkit-scrollbar {
    width: 8px;
}

::-webkit-scrollbar-track {
    background: #0d1117;
}

::-webkit-scrollbar-thumb {
    background: #555;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
    background: #666;
}

/* Responsive */
@media (max-width: 1024px) {
    #terminal-container {
        width: 300px;
    }
}

@media (max-width: 768px) {
    #main-container {
        flex-direction: column;
    }
    
    #terminal-container {
        width: 100%;
        height: 40vh;
    }
    
    #map-container {
        height: 60vh;
    }
}`;
  }
  
  generateJavaScript() {
    return `/* Xaeros World Map Interface JavaScript */

// Global state
let mapData = {
    zoom: 1,
    centerX: 0,
    centerZ: 0,
    players: [],
    waypoints: [],
    chunks: new Map(),
    dimension: 'overworld'
};

let mapCanvas, mapCtx, minimapCanvas, minimapCtx;
let isDragging = false;
let dragStart = { x: 0, z: 0 };
let refreshInterval, terminalHistory = [];
let currentTab = 'console';

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeInterface();
    setupEventListeners();
    startDataRefresh();
    initializeTerminal();
});

function initializeInterface() {
    // Initialize map canvas
    mapCanvas = document.getElementById('world-map');
    mapCtx = mapCanvas.getContext('2d');
    
    minimapCanvas = document.getElementById('minimap');
    minimapCtx = minimapCanvas.getContext('2d');
    
    // Set canvas size to match container
    resizeCanvases();
    
    // Initial map render
    renderMap();
    renderMinimap();
    
    // Load initial data
    refreshData();
}

function setupEventListeners() {
    // Map interactions
    mapCanvas.addEventListener('mousedown', handleMapMouseDown);
    mapCanvas.addEventListener('mousemove', handleMapMouseMove);
    mapCanvas.addEventListener('mouseup', handleMapMouseUp);
    mapCanvas.addEventListener('wheel', handleMapWheel);
    mapCanvas.addEventListener('contextmenu', handleContextMenu);
    
    // Window resize
    window.addEventListener('resize', resizeCanvases);
    
    // Hide context menu on click elsewhere
    document.addEventListener('click', hideContextMenu);
    
    // Terminal input
    const terminalInput = document.getElementById('terminal-input');
    if (terminalInput) {
        terminalInput.addEventListener('keydown', handleTerminalInput);
    }
}

function resizeCanvases() {
    const mapContainer = document.getElementById('map-container');
    if (mapContainer && mapCanvas) {
        mapCanvas.width = mapContainer.clientWidth;
        mapCanvas.height = mapContainer.clientHeight;
        renderMap();
    }
}

// ==========================================
// MAP RENDERING
// ==========================================

function renderMap() {
    if (!mapCtx) return;
    
    const width = mapCanvas.width;
    const height = mapCanvas.height;
    
    // Clear canvas
    mapCtx.fillStyle = getDimensionColor();
    mapCtx.fillRect(0, 0, width, height);
    
    // Draw grid if enabled
    if (mapData.showGrid) {
        drawGrid();
    }
    
    // Draw chunks
    drawChunks();
    
    // Draw waypoints
    drawWaypoints();
    
    // Draw players
    drawPlayers();
    
    // Update coordinates display
    updateCoordinatesDisplay();
}

function getDimensionColor() {
    switch (mapData.dimension) {
        case 'nether': return '#2d1b1b';
        case 'end': return '#1b1b2d';
        default: return '#0f172a';
    }
}

function drawGrid() {
    const gridSize = 16 * mapData.zoom; // 16 blocks per chunk
    if (gridSize < 4) return; // Don't draw grid if too small
    
    mapCtx.strokeStyle = 'rgba(255, 255, 255, 0.1)';
    mapCtx.lineWidth = 1;
    
    const width = mapCanvas.width;
    const height = mapCanvas.height;
    
    // Vertical lines
    for (let x = 0; x < width; x += gridSize) {
        mapCtx.beginPath();
        mapCtx.moveTo(x, 0);
        mapCtx.lineTo(x, height);
        mapCtx.stroke();
    }
    
    // Horizontal lines  
    for (let y = 0; y < height; y += gridSize) {
        mapCtx.beginPath();
        mapCtx.moveTo(0, y);
        mapCtx.lineTo(width, y);
        mapCtx.stroke();
    }
}

function drawChunks() {
    // Simulate chunk data with different biome colors
    const chunkSize = 16 * mapData.zoom;
    const colors = ['#1a472a', '#1a365d', '#472a1a', '#2d1a47'];
    
    for (let x = -10; x < 10; x++) {
        for (let z = -10; z < 10; z++) {
            const screenX = (x * chunkSize) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
            const screenZ = (z * chunkSize) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
            
            if (screenX < mapCanvas.width && screenX + chunkSize > 0 && 
                screenZ < mapCanvas.height && screenZ + chunkSize > 0) {
                
                const colorIndex = Math.abs(x + z) % colors.length;
                mapCtx.fillStyle = colors[colorIndex];
                mapCtx.fillRect(screenX, screenZ, chunkSize, chunkSize);
            }
        }
    }
}

function drawWaypoints() {
    mapData.waypoints.forEach(waypoint => {
        const screenX = (waypoint.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
        const screenZ = (waypoint.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
        
        // Draw waypoint marker
        mapCtx.fillStyle = waypoint.color || '#ff6b6b';
        mapCtx.beginPath();
        mapCtx.arc(screenX, screenZ, 6, 0, 2 * Math.PI);
        mapCtx.fill();
        
        // Draw waypoint name
        mapCtx.fillStyle = '#ffffff';
        mapCtx.font = '10px JetBrains Mono';
        mapCtx.fillText(waypoint.name, screenX + 8, screenZ + 4);
    });
}

function drawPlayers() {
    mapData.players.forEach(player => {
        const screenX = (player.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
        const screenZ = (player.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
        
        // Draw player marker
        mapCtx.fillStyle = player.color || '#4ade80';
        mapCtx.beginPath();
        mapCtx.arc(screenX, screenZ, 4, 0, 2 * Math.PI);
        mapCtx.fill();
        
        // Draw player name
        mapCtx.fillStyle = '#ffffff';
        mapCtx.font = '9px JetBrains Mono';
        mapCtx.fillText(player.name, screenX - 10, screenZ - 8);
        
        // Draw direction indicator
        const angle = (player.yaw || 0) * Math.PI / 180;
        mapCtx.strokeStyle = '#ffffff';
        mapCtx.lineWidth = 2;
        mapCtx.beginPath();
        mapCtx.moveTo(screenX, screenZ);
        mapCtx.lineTo(screenX + Math.cos(angle) * 12, screenZ + Math.sin(angle) * 12);
        mapCtx.stroke();
    });
}

function renderMinimap() {
    if (!minimapCtx) return;
    
    minimapCtx.fillStyle = getDimensionColor();
    minimapCtx.fillRect(0, 0, 120, 120);
    
    // Draw overview of the area
    const scale = 0.1;
    
    // Draw players on minimap
    mapData.players.forEach(player => {
        const x = (player.x * scale) + 60;
        const z = (player.z * scale) + 60;
        
        minimapCtx.fillStyle = player.color || '#4ade80';
        minimapCtx.beginPath();
        minimapCtx.arc(x, z, 2, 0, 2 * Math.PI);
        minimapCtx.fill();
    });
}

// ==========================================
// MAP INTERACTION
// ==========================================

function handleMapMouseDown(e) {
    isDragging = true;
    const rect = mapCanvas.getBoundingClientRect();
    dragStart.x = e.clientX - rect.left;
    dragStart.z = e.clientY - rect.top;
    mapCanvas.style.cursor = 'grabbing';
}

function handleMapMouseMove(e) {
    const rect = mapCanvas.getBoundingClientRect();
    const mouseX = e.clientX - rect.left;
    const mouseZ = e.clientY - rect.top;
    
    if (isDragging) {
        const deltaX = (mouseX - dragStart.x) / mapData.zoom;
        const deltaZ = (mouseZ - dragStart.z) / mapData.zoom;
        
        mapData.centerX -= deltaX;
        mapData.centerZ -= deltaZ;
        
        dragStart.x = mouseX;
        dragStart.z = mouseZ;
        
        renderMap();
    } else {
        // Update tooltip
        updateTooltip(mouseX, mouseZ);
    }
}

function handleMapMouseUp() {
    isDragging = false;
    mapCanvas.style.cursor = 'crosshair';
}

function handleMapWheel(e) {
    e.preventDefault();
    const zoomFactor = e.deltaY > 0 ? 0.9 : 1.1;
    mapData.zoom = Math.max(0.1, Math.min(10, mapData.zoom * zoomFactor));
    renderMap();
}

function handleContextMenu(e) {
    e.preventDefault();
    const contextMenu = document.getElementById('context-menu');
    contextMenu.style.display = 'block';
    contextMenu.style.left = e.clientX + 'px';
    contextMenu.style.top = e.clientY + 'px';
    
    // Store click coordinates for context actions
    const rect = mapCanvas.getBoundingClientRect();
    const worldX = (e.clientX - rect.left - mapCanvas.width / 2) / mapData.zoom + mapData.centerX;
    const worldZ = (e.clientY - rect.top - mapCanvas.height / 2) / mapData.zoom + mapData.centerZ;
    contextMenu.dataset.worldX = worldX;
    contextMenu.dataset.worldZ = worldZ;
}

function hideContextMenu() {
    document.getElementById('context-menu').style.display = 'none';
}

// ==========================================
// MAP CONTROLS
// ==========================================

function mapZoom(delta) {
    const zoomFactor = delta > 0 ? 1.2 : 0.8;
    mapData.zoom = Math.max(0.1, Math.min(10, mapData.zoom * zoomFactor));
    renderMap();
}

function centerOnSpawn() {
    mapData.centerX = 0;
    mapData.centerZ = 0;
    renderMap();
}

function toggleGrid() {
    mapData.showGrid = !mapData.showGrid;
    renderMap();
}

function toggleWaypoints() {
    const panel = document.getElementById('waypoints-panel');
    panel.style.display = panel.style.display === 'block' ? 'none' : 'block';
}

function changeDimension() {
    const select = document.getElementById('dimension-select');
    mapData.dimension = select.value;
    renderMap();
    renderMinimap();
}

function togglePlayersOverlay() {
    const overlay = document.getElementById('players-overlay');
    overlay.style.display = overlay.style.display === 'block' ? 'none' : 'block';
}

function toggleWaypointsPanel() {
    const panel = document.getElementById('waypoints-panel');
    panel.style.display = panel.style.display === 'block' ? 'none' : 'block';
}

function toggleFullscreen() {
    if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen();
    } else {
        document.exitFullscreen();
    }
}

// ==========================================
// TERMINAL FUNCTIONALITY
// ==========================================

function initializeTerminal() {
    addTerminalMessage('AppyProx Central Node Terminal v1.0.0');
    addTerminalMessage('Type "help" for available commands\n');
    
    // Load logs
    loadSystemLogs();
}

function handleTerminalInput(e) {
    if (e.key === 'Enter') {
        const input = e.target.value.trim();
        if (input) {
            executeCommand(input);
            terminalHistory.push(input);
            e.target.value = '';
        }
    }
}

function executeCommand(command) {
    addTerminalMessage('$ ' + command);
    
    const parts = command.split(' ');
    const cmd = parts[0].toLowerCase();
    const args = parts.slice(1);
    
    switch (cmd) {
        case 'help':
            showHelp();
            break;
        case 'status':
            showSystemStatus();
            break;
        case 'players':
            showPlayerList();
            break;
        case 'tasks':
            showTaskList();
            break;
        case 'tp':
        case 'teleport':
            if (args.length >= 2) {
                const x = parseFloat(args[0]);
                const z = parseFloat(args[1]);
                teleportToCoordinates(x, z);
            } else {
                addTerminalMessage('Usage: tp <x> <z>');
            }
            break;
        case 'waypoint':
        case 'wp':
            if (args.length >= 3) {
                addWaypoint(args[0], parseFloat(args[1]), parseFloat(args[2]));
            } else {
                addTerminalMessage('Usage: waypoint <name> <x> <z>');
            }
            break;
        case 'clear':
            clearTerminal();
            break;
        default:
            addTerminalMessage('Unknown command: ' + cmd + '. Type "help" for available commands.');
    }
    
    addTerminalMessage('');
}

function showHelp() {
    const helpText = [
        'Available Commands:',
        '  help                   Show this help message',
        '  status                 Show system status',
        '  players                List connected players',
        '  tasks                  List active tasks',
        '  tp <x> <z>             Teleport map view to coordinates',
        '  waypoint <name> <x> <z> Add waypoint',
        '  clear                  Clear terminal'
    ];
    helpText.forEach(line => addTerminalMessage(line));
}

function showSystemStatus() {
    fetch('/api/status')
        .then(response => response.json())
        .then(data => {
            addTerminalMessage('System Status:');
            addTerminalMessage('  Running: ' + data.isRunning);
            addTerminalMessage('  Uptime: ' + formatUptime(data.uptime));
            addTerminalMessage('  Connected Accounts: ' + data.connectedAccounts);
            addTerminalMessage('  Active Tasks: ' + data.activeTasks);
        })
        .catch(error => addTerminalMessage('Error fetching status: ' + error.message));
}

function showPlayerList() {
    addTerminalMessage('Connected Players:');
    mapData.players.forEach(player => {
        addTerminalMessage('  ' + player.name + ' @ ' + Math.round(player.x) + ', ' + Math.round(player.z));
    });
    if (mapData.players.length === 0) {
        addTerminalMessage('  No players online');
    }
}

function showTaskList() {
    fetch('/api/tasks')
        .then(response => response.json())
        .then(tasks => {
            addTerminalMessage('Active Tasks:');
            tasks.forEach(task => {
                addTerminalMessage('  ' + task.taskId + ': ' + task.type + ' (' + (task.progress || 0) + '%)');
            });
            if (tasks.length === 0) {
                addTerminalMessage('  No active tasks');
            }
        })
        .catch(error => addTerminalMessage('Error fetching tasks: ' + error.message));
}

function teleportToCoordinates(x, z) {
    mapData.centerX = x;
    mapData.centerZ = z;
    renderMap();
    addTerminalMessage('Map view teleported to ' + x + ', ' + z);
}

function addWaypoint(name, x, z) {
    const waypoint = {
        name: name,
        x: x,
        z: z,
        color: '#ff6b6b'
    };
    mapData.waypoints.push(waypoint);
    renderMap();
    addTerminalMessage('Waypoint "' + name + '" added at ' + x + ', ' + z);
    updateWaypointsList();
}

function addTerminalMessage(message) {
    const output = document.getElementById('terminal-output');
    if (output) {
        const div = document.createElement('div');
        div.textContent = message;
        div.style.color = message.startsWith('$') ? '#4ade80' : '#c9d1d9';
        output.appendChild(div);
        output.scrollTop = output.scrollHeight;
    }
}

function clearTerminal() {
    const output = document.getElementById('terminal-output');
    if (output) {
        output.innerHTML = '';
    }
}

// ==========================================
// TAB MANAGEMENT
// ==========================================

function switchTab(tabName) {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active class from all tabs
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Show selected tab content
    document.getElementById(tabName + '-tab').classList.add('active');
    document.querySelector('[data-tab="' + tabName + '"]').classList.add('active');
    
    currentTab = tabName;
    
    // Load tab-specific data
    if (tabName === 'logs') {
        loadSystemLogs();
    } else if (tabName === 'tasks') {
        refreshTasks();
    }
}

function loadSystemLogs() {
    // Simulate loading system logs
    const logsContainer = document.getElementById('logs-list');
    if (logsContainer) {
        const sampleLogs = [
            { time: new Date().toISOString(), level: 'INFO', message: 'Proxy server started on port 25565' },
            { time: new Date(Date.now() - 60000).toISOString(), level: 'INFO', message: 'ClusterManager initialized' },
            { time: new Date(Date.now() - 120000).toISOString(), level: 'WARN', message: 'Health check found 1 unhealthy client' }
        ];
        
        logsContainer.innerHTML = sampleLogs.map(log => \`
            <div class="log-entry log-\${log.level.toLowerCase()}">
                <span class="log-time">\${new Date(log.time).toLocaleTimeString()}</span>
                <span class="log-level">\${log.level}</span>
                <span class="log-message">\${log.message}</span>
            </div>
        \`).join('');
    }
}

function refreshTasks() {
    fetch('/api/tasks')
        .then(response => response.json())
        .then(tasks => {
            const container = document.getElementById('active-tasks-list');
            if (container) {
                if (tasks.length === 0) {
                    container.innerHTML = '<p style="color: #8b949e;">No active tasks</p>';
                } else {
                    container.innerHTML = tasks.map(task => \`
                        <div class="task-item">
                            <h5>\${task.type || task.taskId}</h5>
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: \${task.progress || 0}%"></div>
                            </div>
                            <p>Status: \${task.status || 'Running'}</p>
                            <p>Progress: \${task.progress || 0}%</p>
                        </div>
                    \`).join('');
                }
            }
        })
        .catch(error => console.error('Error fetching tasks:', error));
}

// ==========================================
// API FUNCTIONALITY
// ==========================================

function executeApiCall() {
    const method = document.getElementById('api-method').value;
    const endpoint = document.getElementById('api-endpoint').value;
    const body = document.getElementById('api-body').value;
    const responseContainer = document.getElementById('api-response');
    
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };
    
    if (method !== 'GET' && body) {
        options.body = body;
    }
    
    fetch(endpoint, options)
        .then(response => response.json())
        .then(data => {
            responseContainer.textContent = JSON.stringify(data, null, 2);
            responseContainer.style.color = '#4ade80';
        })
        .catch(error => {
            responseContainer.textContent = 'Error: ' + error.message;
            responseContainer.style.color = '#ef4444';
        });
}

// ==========================================
// DATA REFRESH AND UPDATES
// ==========================================

function startDataRefresh() {
    refreshData();
    refreshInterval = setInterval(refreshData, 5000);
}

function refreshData() {
    // Fetch system status
    fetch('/api/status')
        .then(response => response.json())
        .then(data => {
            updateSystemStatus(data);
        })
        .catch(error => console.error('Error fetching status:', error));
    
    // Fetch accounts (players)
    fetch('/api/accounts')
        .then(response => response.json())
        .then(accounts => {
            updatePlayerData(accounts);
        })
        .catch(error => console.error('Error fetching accounts:', error));
    
    // Refresh current tab if needed
    if (currentTab === 'tasks') {
        refreshTasks();
    }
}

function updateSystemStatus(status) {
    // Update status bar
    const uptimeElement = document.getElementById('uptime');
    if (uptimeElement) {
        uptimeElement.textContent = '⏱ ' + formatUptime(status.uptime);
    }
    
    const tasksElement = document.getElementById('tasks-count');
    if (tasksElement) {
        tasksElement.textContent = '📋 ' + status.activeTasks + ' Tasks';
    }
    
    const playersElement = document.getElementById('players-online');
    if (playersElement) {
        playersElement.textContent = status.connectedAccounts + ' Players';
    }
}

function updatePlayerData(accounts) {
    // Convert account data to player markers
    mapData.players = accounts.map(account => ({
        name: account.username || account.clientId,
        x: account.position?.x || Math.random() * 200 - 100,
        z: account.position?.z || Math.random() * 200 - 100,
        yaw: account.rotation?.yaw || 0,
        color: getPlayerColor(account.status)
    }));
    
    renderMap();
    renderMinimap();
    updatePlayersOverlay(accounts);
}

function getPlayerColor(status) {
    switch (status) {
        case 'mining': return '#ffa500';
        case 'building': return '#4169e1';
        case 'exploring': return '#32cd32';
        default: return '#4ade80';
    }
}

function updatePlayersOverlay(accounts) {
    const container = document.getElementById('players-list');
    if (!container) return;
    
    if (accounts.length === 0) {
        container.innerHTML = '<p style="color: #8b949e;">No players online</p>';
        return;
    }
    
    container.innerHTML = accounts.map(account => \`
        <div class="player-item">
            <h5>\${account.username || account.clientId}</h5>
            <p>Status: \${account.status || 'Online'}</p>
            <p>Health: \${account.health || 20}/20</p>
            <p>Position: \${Math.round(account.position?.x || 0)}, \${Math.round(account.position?.z || 0)}</p>
        </div>
    \`).join('');
}

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

function updateCoordinatesDisplay() {
    const coordsElement = document.getElementById('coords');
    if (coordsElement) {
        coordsElement.textContent = \`X: \${Math.round(mapData.centerX)} Z: \${Math.round(mapData.centerZ)}\`;
    }
    
    const minimapCoords = document.getElementById('minimap-coords');
    if (minimapCoords) {
        minimapCoords.textContent = \`X: \${Math.round(mapData.centerX)}, Z: \${Math.round(mapData.centerZ)}\`;
    }
}

function updateTooltip(mouseX, mouseZ) {
    const tooltip = document.getElementById('tooltip');
    const worldX = (mouseX - mapCanvas.width / 2) / mapData.zoom + mapData.centerX;
    const worldZ = (mouseZ - mapCanvas.height / 2) / mapData.zoom + mapData.centerZ;
    
    tooltip.textContent = \`X: \${Math.round(worldX)}, Z: \${Math.round(worldZ)}\`;
    tooltip.style.left = (mouseX + 10) + 'px';
    tooltip.style.top = (mouseZ - 20) + 'px';
    tooltip.style.display = 'block';
}

function updateWaypointsList() {
    const container = document.getElementById('waypoints-list');
    if (!container) return;
    
    if (mapData.waypoints.length === 0) {
        container.innerHTML = '<p style="color: #8b949e;">No waypoints</p>';
        return;
    }
    
    container.innerHTML = mapData.waypoints.map((wp, index) => \`
        <div class="waypoint-item">
            <h5>\${wp.name}</h5>
            <p>X: \${wp.x}, Z: \${wp.z}</p>
            <button onclick="removeWaypoint(\${index})">Delete</button>
        </div>
    \`).join('');
}

function removeWaypoint(index) {
    mapData.waypoints.splice(index, 1);
    renderMap();
    updateWaypointsList();
}

function contextAction(action) {
    const menu = document.getElementById('context-menu');
    const worldX = parseFloat(menu.dataset.worldX);
    const worldZ = parseFloat(menu.dataset.worldZ);
    
    switch (action) {
        case 'teleport':
            teleportToCoordinates(worldX, worldZ);
            break;
        case 'waypoint':
            const name = prompt('Waypoint name:', 'Waypoint');
            if (name) {
                addWaypoint(name, worldX, worldZ);
            }
            break;
        case 'center':
            mapData.centerX = worldX;
            mapData.centerZ = worldZ;
            renderMap();
            break;
    }
    
    hideContextMenu();
}

function formatUptime(uptime) {
    const seconds = Math.floor(uptime / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    
    if (days > 0) {
        return \`\${days}d \${hours % 24}h \${minutes % 60}m\`;
    } else if (hours > 0) {
        return \`\${hours}h \${minutes % 60}m \${seconds % 60}s\`;
    } else if (minutes > 0) {
        return \`\${minutes}m \${seconds % 60}s\`;
    } else {
        return \`\${seconds}s\`;
    }
}

function toggleTerminal() {
    const terminal = document.getElementById('terminal-container');
    terminal.style.display = terminal.style.display === 'none' ? 'flex' : 'none';
    resizeCanvases();
}

function exportLogs() {
    const logs = document.getElementById('logs-list').innerText;
    const blob = new Blob([logs], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'appyprox-logs-' + new Date().toISOString().split('T')[0] + '.txt';
    a.click();
    URL.revokeObjectURL(url);
}

function filterLogs() {
    // Log filtering functionality would go here
    console.log('Log filtering not yet implemented');
}

// ==========================================
// ENHANCED TASK VISUALIZATION SYSTEM
// ==========================================

// Initialize task visualization system
function initializeTaskVisualization() {
    // Enhanced task visualization storage
    window.taskVisualizations = new Map(); // taskId -> visual elements
    window.pathfindingRoutes = new Map(); // routeId -> route visualization
    window.clusterFormations = new Map(); // clusterId -> formation data
    
    // Visual settings
    window.visualSettings = {
        showTasks: true,
        showPaths: true,
        showClusters: true,
        animationSpeed: 1.0
    };
    
    // Add visual layer controls
    addVisualControls();
    
    console.log('Task visualization system initialized');
}

function addVisualControls() {
    const mapControls = document.getElementById('map-controls');
    if (mapControls) {
        const visualControls = document.createElement('div');
        visualControls.className = 'control-group visual-controls';
        visualControls.innerHTML = `
            <button id="toggle-tasks" onclick="toggleTaskVisuals()" title="Toggle Task Visualization">📋</button>
            <button id="toggle-paths" onclick="togglePathVisuals()" title="Toggle Pathfinding Routes">🛤️</button>
            <button id="toggle-clusters" onclick="toggleClusterVisuals()" title="Toggle Cluster Formations">👥</button>
        `;
        mapControls.appendChild(visualControls);
    }
}

function toggleTaskVisuals() {
    window.visualSettings.showTasks = !window.visualSettings.showTasks;
    const button = document.getElementById('toggle-tasks');
    if (button) {
        button.style.opacity = window.visualSettings.showTasks ? '1.0' : '0.5';
    }
    renderMap();
    addTerminalMessage(window.visualSettings.showTasks ? '📋 Task visuals enabled' : '📋 Task visuals disabled');
}

function togglePathVisuals() {
    window.visualSettings.showPaths = !window.visualSettings.showPaths;
    const button = document.getElementById('toggle-paths');
    if (button) {
        button.style.opacity = window.visualSettings.showPaths ? '1.0' : '0.5';
    }
    renderMap();
    addTerminalMessage(window.visualSettings.showPaths ? '🛤️ Path visuals enabled' : '🛤️ Path visuals disabled');
}

function toggleClusterVisuals() {
    window.visualSettings.showClusters = !window.visualSettings.showClusters;
    const button = document.getElementById('toggle-clusters');
    if (button) {
        button.style.opacity = window.visualSettings.showClusters ? '1.0' : '0.5';
    }
    renderMap();
    addTerminalMessage(window.visualSettings.showClusters ? '👥 Cluster visuals enabled' : '👥 Cluster visuals disabled');
}

// Enhanced task visualization functions
function createTaskVisualization(taskData) {
    const { taskId, elements } = taskData;
    
    // Store task visualization elements
    window.taskVisualizations.set(taskId, {
        elements: elements,
        created: Date.now(),
        status: 'active'
    });
    
    renderMap(); // Re-render with new task
    addTerminalMessage(`🎯 Task visualization created: ${taskId}`);
}

function drawTaskMarker(element) {
    if (!mapCtx) return;
    
    const screenX = (element.position.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
    const screenZ = (element.position.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
    
    // Draw main task marker
    mapCtx.fillStyle = element.style.color;
    mapCtx.beginPath();
    mapCtx.arc(screenX, screenZ, element.style.size === 'large' ? 10 : 8, 0, 2 * Math.PI);
    mapCtx.fill();
    
    // Draw task icon
    mapCtx.fillStyle = '#ffffff';
    mapCtx.font = element.style.size === 'large' ? '14px JetBrains Mono' : '12px JetBrains Mono';
    mapCtx.textAlign = 'center';
    mapCtx.fillText(element.style.icon, screenX, screenZ + 5);
    
    // Draw task label
    if (element.style.label && mapData.zoom > 0.5) {
        mapCtx.fillStyle = '#c9d1d9';
        mapCtx.font = '9px JetBrains Mono';
        mapCtx.fillText(element.style.label, screenX, screenZ + 25);
    }
}

function drawGatheringArea(element) {
    if (!mapCtx) return;
    
    const screenX = (element.position.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
    const screenZ = (element.position.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
    const radius = (element.radius * mapData.zoom);
    
    // Draw gathering area circle
    mapCtx.fillStyle = element.style.color;
    mapCtx.globalAlpha = element.style.opacity || 0.3;
    mapCtx.beginPath();
    mapCtx.arc(screenX, screenZ, radius, 0, 2 * Math.PI);
    mapCtx.fill();
    
    // Draw border with breathing animation
    if (element.style.borderColor) {
        const pulse = element.style.animation === 'breathing' ? 
            Math.sin(Date.now() / 1000) * 0.3 + 1.0 : 1.0;
        
        mapCtx.strokeStyle = element.style.borderColor;
        mapCtx.lineWidth = (element.style.borderWidth || 2) * pulse;
        mapCtx.globalAlpha = 1.0;
        mapCtx.beginPath();
        mapCtx.arc(screenX, screenZ, radius, 0, 2 * Math.PI);
        mapCtx.stroke();
    }
    
    mapCtx.globalAlpha = 1.0;
}

function drawBuildingProgress(element) {
    if (!mapCtx) return;
    
    const screenX = (element.position.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
    const screenZ = (element.position.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
    
    const size = 24 * mapData.zoom;
    
    // Draw building outline
    mapCtx.strokeStyle = element.style.color;
    mapCtx.lineWidth = 2;
    mapCtx.globalAlpha = element.style.opacity || 0.5;
    mapCtx.strokeRect(screenX - size/2, screenZ - size/2, size, size);
    
    // Draw progress fill
    if (element.style.progress > 0) {
        const progressHeight = (size * element.style.progress) / 100;
        mapCtx.fillStyle = element.style.color;
        mapCtx.globalAlpha = 0.4;
        mapCtx.fillRect(screenX - size/2, screenZ + size/2 - progressHeight, size, progressHeight);
    }
    
    // Draw progress text
    if (mapData.zoom > 0.5) {
        mapCtx.fillStyle = '#ffffff';
        mapCtx.globalAlpha = 1.0;
        mapCtx.font = '10px JetBrains Mono';
        mapCtx.textAlign = 'center';
        mapCtx.fillText(Math.round(element.style.progress) + '%', screenX, screenZ + size/2 + 14);
    }
    
    mapCtx.globalAlpha = 1.0;
}

function createPathVisualization(pathData) {
    const { id, points, segments, style, metadata } = pathData;
    
    // Store path visualization
    window.pathfindingRoutes.set(id, {
        points: points,
        segments: segments,
        style: style,
        metadata: metadata,
        created: Date.now(),
        status: 'active'
    });
    
    renderMap(); // Re-render with new path
    const distance = metadata ? metadata.totalDistance.toFixed(1) : 'N/A';
    const time = metadata ? Math.round(metadata.estimatedTime/1000) : 'N/A';
    addTerminalMessage(`🗺️ Path created: ${distance} blocks, ${time}s`);
}

function drawPathfindingRoute(pathData) {
    if (!mapCtx || !pathData.points || pathData.points.length < 2) return;
    
    const { points, style } = pathData;
    
    // Draw path segments
    mapCtx.strokeStyle = style.color;
    mapCtx.lineWidth = (style.width || 3) * Math.max(0.5, mapData.zoom);
    mapCtx.globalAlpha = style.opacity || 0.7;
    
    if (style.animated) {
        const dashOffset = (Date.now() / 100) % 20;
        mapCtx.setLineDash([10, 5]);
        mapCtx.lineDashOffset = dashOffset;
    }
    
    mapCtx.beginPath();
    
    for (let i = 0; i < points.length; i++) {
        const point = points[i];
        const screenX = (point.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
        const screenZ = (point.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
        
        if (i === 0) {
            mapCtx.moveTo(screenX, screenZ);
        } else {
            mapCtx.lineTo(screenX, screenZ);
        }
    }
    
    mapCtx.stroke();
    mapCtx.setLineDash([]);
    mapCtx.lineDashOffset = 0;
    
    // Draw waypoints if enabled and zoomed in enough
    if (style.showWaypoints && mapData.zoom > 0.8) {
        points.forEach((point, index) => {
            const screenX = (point.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
            const screenZ = (point.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
            
            mapCtx.fillStyle = style.color;
            mapCtx.globalAlpha = 0.8;
            mapCtx.beginPath();
            mapCtx.arc(screenX, screenZ, 3 * mapData.zoom, 0, 2 * Math.PI);
            mapCtx.fill();
        });
    }
    
    // Draw direction indicators
    if (style.showDirection && points.length >= 2 && mapData.zoom > 0.6) {
        const startPoint = points[0];
        const endPoint = points[points.length - 1];
        
        // Start marker (rocket)
        const startScreenX = (startPoint.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
        const startScreenZ = (startPoint.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
        
        mapCtx.fillStyle = '#4ade80';
        mapCtx.globalAlpha = 1.0;
        mapCtx.font = '16px JetBrains Mono';
        mapCtx.textAlign = 'center';
        mapCtx.fillText('🚀', startScreenX, startScreenZ + 6);
        
        // End marker (target)
        const endScreenX = (endPoint.x * mapData.zoom) + (mapCanvas.width / 2) - (mapData.centerX * mapData.zoom);
        const endScreenZ = (endPoint.z * mapData.zoom) + (mapCanvas.height / 2) - (mapData.centerZ * mapData.zoom);
        
        mapCtx.fillStyle = '#ef4444';
        mapCtx.fillText('🎯', endScreenX, endScreenZ + 6);
    }
    
    mapCtx.globalAlpha = 1.0;
}

function updateTaskProgress(progressData) {
    const { taskId, progress } = progressData;
    
    const taskViz = window.taskVisualizations.get(taskId);
    if (taskViz) {
        // Update progress on building elements
        taskViz.elements.forEach(element => {
            if (element.type === 'building_progress') {
                element.style.progress = progress;
            }
        });
        
        renderMap(); // Re-render with updated progress
    }
}

// Override the original renderMap function to include visualizations
const originalRenderMap = renderMap;
renderMap = function() {
    // Call original render function first
    originalRenderMap();
    
    // Add enhanced visualizations if enabled
    if (window.visualSettings) {
        if (window.visualSettings.showTasks) {
            renderTaskVisualizations();
        }
        
        if (window.visualSettings.showPaths) {
            renderPathfindingVisualizations();
        }
        
        if (window.visualSettings.showClusters) {
            renderClusterVisualizations();
        }
    }
};

function renderTaskVisualizations() {
    if (!window.taskVisualizations) return;
    
    for (const [taskId, taskViz] of window.taskVisualizations) {
        if (taskViz.status === 'active' || taskViz.status === 'completed') {
            taskViz.elements.forEach(element => {
                switch (element.type) {
                    case 'task_marker':
                        drawTaskMarker(element);
                        break;
                    case 'gathering_area':
                        drawGatheringArea(element);
                        break;
                    case 'building_progress':
                        drawBuildingProgress(element);
                        break;
                }
            });
        }
    }
}

function renderPathfindingVisualizations() {
    if (!window.pathfindingRoutes) return;
    
    for (const [routeId, pathData] of window.pathfindingRoutes) {
        if (pathData.status === 'active') {
            drawPathfindingRoute(pathData);
        }
    }
}

function renderClusterVisualizations() {
    if (!window.clusterFormations) return;
    
    // Render cluster formation connections and indicators
    for (const [clusterId, clusterData] of window.clusterFormations) {
        if (clusterData.style && clusterData.style.coordinationLines) {
            // Draw formation coordination lines
            mapCtx.strokeStyle = clusterData.style.color || '#3b82f6';
            mapCtx.globalAlpha = clusterData.style.opacity || 0.6;
            mapCtx.lineWidth = 1;
            mapCtx.setLineDash([4, 4]);
            
            // Implementation would connect cluster members
            // This is simplified for the demo
            
            mapCtx.setLineDash([]);
            mapCtx.globalAlpha = 1.0;
        }
    }
}

// Simulated real-time updates for demonstration
function simulateTaskUpdates() {
    // Create sample task visualization
    setTimeout(() => {
        createTaskVisualization({
            taskId: 'demo_task_1',
            elements: [{
                type: 'task_marker',
                id: 'task_demo_task_1',
                position: { x: 50, y: 64, z: 100 },
                style: {
                    color: '#fbbf24',
                    size: 'large',
                    icon: '⛏️',
                    label: 'Mining Operation'
                }
            }, {
                type: 'gathering_area',
                id: 'gather_demo_task_1',
                position: { x: 50, y: 64, z: 100 },
                radius: 15,
                style: {
                    color: '#fbbf24',
                    opacity: 0.3,
                    borderColor: '#f59e0b',
                    borderWidth: 2,
                    animation: 'breathing'
                }
            }]
        });
    }, 5000);
    
    // Create sample pathfinding route
    setTimeout(() => {
        createPathVisualization({
            id: 'demo_route_1',
            points: [
                { x: 0, y: 64, z: 0 },
                { x: 25, y: 64, z: 50 },
                { x: 50, y: 64, z: 100 }
            ],
            style: {
                color: '#4ade80',
                width: 3,
                opacity: 0.8,
                animated: true,
                showDirection: true,
                showWaypoints: true
            },
            metadata: {
                totalDistance: 111.8,
                estimatedTime: 25000
            }
        });
    }, 8000);
    
    // Create building progress demo
    setTimeout(() => {
        createTaskVisualization({
            taskId: 'demo_build_1',
            elements: [{
                type: 'building_progress',
                id: 'build_demo_1',
                position: { x: -100, y: 70, z: -50 },
                structure: 'castle',
                style: {
                    color: '#3b82f6',
                    opacity: 0.6,
                    progress: 35,
                    animation: 'construction'
                }
            }]
        });
        
        // Simulate progress updates
        let progress = 35;
        const progressInterval = setInterval(() => {
            progress += Math.random() * 5;
            if (progress >= 100) {
                progress = 100;
                clearInterval(progressInterval);
                addTerminalMessage('✅ Building construction completed!');
            }
            updateTaskProgress({ taskId: 'demo_build_1', progress: progress });
        }, 3000);
    }, 12000);
}

// Initialize task visualization system when DOM loads
if (typeof document !== 'undefined') {
    document.addEventListener('DOMContentLoaded', function() {
        // Add small delay to ensure map is initialized first
        setTimeout(() => {
            initializeTaskVisualization();
            // Start demo updates in demo mode
            if (window.location.href.includes('demo') || 
                document.title.includes('Demo')) {
                simulateTaskUpdates();
            }
        }, 1500);
    });
}

// Cleanup on page unload
window.addEventListener('beforeunload', function() {
    if (refreshInterval) {
        clearInterval(refreshInterval);
    }
});`;
  }
  
  generateAccountsList() {
    if (this.accountStatuses.size === 0) {
      return '<p style="color: #6b7280; font-style: italic;">No accounts currently connected</p>';
    }
    
    return Array.from(this.accountStatuses.values()).map(account => `
      <div class="account-item">
        <h4>${account.username || account.clientId}</h4>
        <p>Status: ${account.status || 'Connected'}</p>
        <p>Last Update: ${new Date(account.lastUpdate).toLocaleTimeString()}</p>
      </div>
    `).join('');
  }
  
  generateTasksList() {
    if (this.taskProgress.size === 0) {
      return '<p style="color: #6b7280; font-style: italic;">No active tasks</p>';
    }
    
    return Array.from(this.taskProgress.values()).map(task => `
      <div class="task-item">
        <h4>Task: ${task.type || task.taskId}</h4>
        <p>Progress: ${task.progress || 0}%</p>
        <p>Status: ${task.status || 'Running'}</p>
        <p>Last Update: ${new Date(task.lastUpdate).toLocaleTimeString()}</p>
      </div>
    `).join('');
  }
  
  formatUptime(uptime) {
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

  generatePlayersList() {
    const accounts = this.proxyServer ? this.proxyServer.getConnectedAccounts() : [];
    if (accounts.length === 0) {
      return '<p style="color: #8b949e;">No players online</p>';
    }
    
    return accounts.map(account => `
      <div class="player-item">
        <h5>${account.username || account.clientId}</h5>
        <p>Status: ${account.status || 'Online'}</p>
        <p>Health: ${account.health || 20}/20</p>
        <p>Position: ${Math.round(account.position?.x || 0)}, ${Math.round(account.position?.z || 0)}</p>
      </div>
    `).join('');
  }

  generateWaypointsList() {
    // Return empty waypoints for now - will be populated via JavaScript
    return '<p style="color: #8b949e;">No waypoints</p>';
  }

  generateTasksList() {
    const tasks = this.automationEngine ? this.automationEngine.getActiveTasks() : [];
    if (tasks.length === 0) {
      return '<p style="color: #8b949e;">No active tasks</p>';
    }
    
    return tasks.map(task => `
      <div class="task-item">
        <h5>${task.type || task.taskId}</h5>
        <div class="progress-bar">
          <div class="progress-fill" style="width: ${task.progress || 0}%"></div>
        </div>
        <p>Status: ${task.status || 'Running'}</p>
        <p>Progress: ${task.progress || 0}%</p>
      </div>
    `).join('');
  }
  
  getSystemStatus() {
    const now = Date.now();
    const uptime = this.systemStats.startTime ? now - this.systemStats.startTime : 0;
    
    return {
      isRunning: this.isRunning,
      uptime: uptime,
      connectedAccounts: this.accountStatuses.size,
      activeTasks: this.taskProgress.size,
      totalConnections: this.systemStats.totalConnections,
      activeConnections: this.systemStats.activeConnections,
      webInterface: {
        port: this.config.web_interface_port,
        enabled: this.config.enabled
      }
    };
  }
  
  getStatus() {
    return this.getSystemStatus();
  }

  // ==========================================
  // COMPREHENSIVE MONITORING & ANALYTICS
  // ==========================================

  startMonitoring() {
    this.logger.info('Starting comprehensive monitoring systems...');
    
    // Start performance metrics collection (every 5 seconds)
    this.metricsInterval = setInterval(() => {
      this.collectPerformanceMetrics();
      this.checkAlertThresholds();
      this.broadcastRealTimeUpdates();
    }, 5000);
    
    // Start data cleanup (every hour)
    this.cleanupInterval = setInterval(() => {
      this.cleanupOldData();
    }, 3600000);
    
    this.logger.info('Monitoring systems started successfully');
  }

  collectPerformanceMetrics() {
    const now = Date.now();
    
    // Update system stats
    this.systemStats.uptime = this.systemStats.startTime ? now - this.systemStats.startTime : 0;
    this.systemStats.memoryUsage = process.memoryUsage();
    this.systemStats.cpuUsage = process.cpuUsage();
    
    // Calculate real-time metrics
    this.updateRealTimeMetrics();
    
    // Update performance history
    this.updatePerformanceHistory();
    
    // Calculate cluster efficiency
    this.calculateClusterEfficiency();
    
    // Update resource utilization
    this.updateResourceUtilization();
  }

  updateRealTimeMetrics() {
    // Count active tasks
    this.realTimeData.activeTasks = this.taskProgress.size;
    
    // Calculate connection health
    let healthy = 0, degraded = 0, failed = 0;
    
    for (const account of this.accountStatuses.values()) {
      const lastSeen = Date.now() - account.lastUpdate;
      if (lastSeen < 30000) { // 30 seconds
        healthy++;
      } else if (lastSeen < 300000) { // 5 minutes
        degraded++;
      } else {
        failed++;
      }
    }
    
    this.realTimeData.connectionHealth = { healthy, degraded, failed };
    
    // Update network traffic (simulated for now)
    this.realTimeData.networkTraffic = {
      in: Math.floor(Math.random() * 1000) + 500,
      out: Math.floor(Math.random() * 800) + 300
    };
    
    // Calculate average response time
    if (this.performanceMetrics.responseTimeHistory.length > 0) {
      const sum = this.performanceMetrics.responseTimeHistory.reduce((a, b) => a + b, 0);
      this.realTimeData.averageResponseTime = sum / this.performanceMetrics.responseTimeHistory.length;
    }
  }

  updatePerformanceHistory() {
    const now = Date.now();
    
    // Add current throughput to history
    this.performanceMetrics.throughputHistory.push({
      timestamp: now,
      activeTasks: this.realTimeData.activeTasks,
      completedTasks: this.realTimeData.completedTasksHour,
      failedTasks: this.realTimeData.failedTasksHour
    });
    
    // Keep only last 100 entries
    if (this.performanceMetrics.throughputHistory.length > 100) {
      this.performanceMetrics.throughputHistory.shift();
    }
    
    // Add response time to history (simulated)
    const responseTime = Math.floor(Math.random() * 2000) + 100;
    this.performanceMetrics.responseTimeHistory.push(responseTime);
    
    // Keep only last 50 entries
    if (this.performanceMetrics.responseTimeHistory.length > 50) {
      this.performanceMetrics.responseTimeHistory.shift();
    }
  }

  calculateClusterEfficiency() {
    for (const [clusterId, clusterStatus] of this.clusterStatuses) {
      const completedTasks = clusterStatus.completedTasks || 0;
      const totalTasks = clusterStatus.totalTasks || 1;
      const memberCount = clusterStatus.memberCount || 1;
      
      const efficiency = completedTasks / totalTasks;
      const utilizationRate = clusterStatus.activeTasks / memberCount;
      
      this.performanceMetrics.clusterEfficiency.set(clusterId, {
        efficiency: efficiency,
        utilization: utilizationRate,
        throughput: completedTasks / (Date.now() - clusterStatus.startTime || 1),
        lastUpdate: Date.now()
      });
    }
  }

  updateResourceUtilization() {
    const memUsage = this.systemStats.memoryUsage;
    const memUtilization = memUsage.heapUsed / memUsage.heapTotal;
    
    this.performanceMetrics.resourceUtilization.set('memory', {
      used: memUsage.heapUsed,
      total: memUsage.heapTotal,
      utilization: memUtilization,
      timestamp: Date.now()
    });
    
    this.performanceMetrics.resourceUtilization.set('cpu', {
      user: this.systemStats.cpuUsage.user,
      system: this.systemStats.cpuUsage.system,
      utilization: (this.systemStats.cpuUsage.user + this.systemStats.cpuUsage.system) / 1000000, // Convert to seconds
      timestamp: Date.now()
    });
  }

  checkAlertThresholds() {
    const now = Date.now();
    
    // Check memory usage
    const memUsage = this.systemStats.memoryUsage;
    const memUtilization = memUsage.heapUsed / memUsage.heapTotal;
    if (memUtilization > this.alertThresholds.memoryUsage) {
      this.createAlert('high_memory_usage', 'warning', 
        `Memory usage at ${(memUtilization * 100).toFixed(1)}%`, { memUtilization });
    }
    
    // Check average response time
    if (this.realTimeData.averageResponseTime > this.alertThresholds.responseTime) {
      this.createAlert('high_response_time', 'warning',
        `Average response time: ${this.realTimeData.averageResponseTime}ms`, 
        { responseTime: this.realTimeData.averageResponseTime });
    }
    
    // Check task failure rate
    const totalTasks = this.realTimeData.completedTasksHour + this.realTimeData.failedTasksHour;
    if (totalTasks > 0) {
      const failureRate = this.realTimeData.failedTasksHour / totalTasks;
      if (failureRate > this.alertThresholds.taskFailureRate) {
        this.createAlert('high_task_failure_rate', 'error',
          `Task failure rate at ${(failureRate * 100).toFixed(1)}%`, { failureRate });
      }
    }
    
    // Check connection health
    const totalConnections = this.realTimeData.connectionHealth.healthy + 
                           this.realTimeData.connectionHealth.degraded + 
                           this.realTimeData.connectionHealth.failed;
    if (totalConnections > 0) {
      const healthyRate = this.realTimeData.connectionHealth.healthy / totalConnections;
      if (healthyRate < 0.8) { // Less than 80% healthy connections
        this.createAlert('connection_health_degraded', 'warning',
          `Only ${(healthyRate * 100).toFixed(1)}% of connections are healthy`, 
          { healthyRate, connectionHealth: this.realTimeData.connectionHealth });
      }
    }
  }

  createAlert(type, severity, message, data = {}) {
    const alert = {
      id: Date.now() + Math.random(),
      type: type,
      severity: severity, // 'info', 'warning', 'error', 'critical'
      message: message,
      data: data,
      timestamp: Date.now(),
      acknowledged: false
    };
    
    // Check if we already have a recent alert of this type
    const recentAlert = this.alerts.find(a => 
      a.type === type && 
      !a.acknowledged && 
      (Date.now() - a.timestamp) < 300000 // 5 minutes
    );
    
    if (!recentAlert) {
      this.alerts.push(alert);
      this.logger.warn(`Alert created [${severity.toUpperCase()}]: ${message}`, data);
      
      // Emit alert event
      this.emit('alert_created', alert);
      
      // Broadcast to WebSocket clients
      this.wsManager.broadcast('alert', alert);
      
      // Keep only last 100 alerts
      if (this.alerts.length > 100) {
        this.alerts.shift();
      }
    }
  }

  acknowledgeAlert(alertId) {
    const alert = this.alerts.find(a => a.id === alertId);
    if (alert) {
      alert.acknowledged = true;
      alert.acknowledgedAt = Date.now();
      this.logger.info(`Alert acknowledged: ${alert.message}`);
      return true;
    }
    return false;
  }

  broadcastRealTimeUpdates() {
    if (!this.wsManager) return;
    
    const updateData = {
      timestamp: Date.now(),
      systemStats: this.systemStats,
      realTimeData: this.realTimeData,
      performanceMetrics: {
        responseTimeHistory: this.performanceMetrics.responseTimeHistory.slice(-20), // Last 20 points
        throughputHistory: this.performanceMetrics.throughputHistory.slice(-20)
      },
      clusterEfficiency: Array.from(this.performanceMetrics.clusterEfficiency.entries()),
      resourceUtilization: Array.from(this.performanceMetrics.resourceUtilization.entries()),
      alerts: this.alerts.filter(a => !a.acknowledged).slice(-10) // Last 10 unacknowledged alerts
    };
    
    this.wsManager.broadcast('realtime_update', updateData);
  }

  cleanupOldData() {
    const now = Date.now();
    const oneHourAgo = now - 3600000;
    const oneDayAgo = now - 86400000;
    
    this.logger.info('Performing data cleanup...');
    
    // Clean up old alerts
    const oldAlertsCount = this.alerts.length;
    this.alerts = this.alerts.filter(alert => 
      (now - alert.timestamp) < oneDayAgo || !alert.acknowledged
    );
    
    // Clean up old performance history
    this.performanceMetrics.responseTimeHistory = 
      this.performanceMetrics.responseTimeHistory.slice(-100);
    this.performanceMetrics.throughputHistory = 
      this.performanceMetrics.throughputHistory.slice(-100);
    
    // Clean up old account statuses
    for (const [clientId, status] of this.accountStatuses.entries()) {
      if ((now - status.lastUpdate) > oneDayAgo) {
        this.accountStatuses.delete(clientId);
      }
    }
    
    // Clean up old task progress
    for (const [taskId, progress] of this.taskProgress.entries()) {
      if ((now - progress.lastUpdate) > oneHourAgo) {
        this.taskProgress.delete(taskId);
      }
    }
    
    this.logger.info(`Data cleanup completed. Removed ${oldAlertsCount - this.alerts.length} old alerts`);
  }

  // Enhanced status methods
  getEnhancedSystemStatus() {
    const basicStatus = this.getSystemStatus();
    
    return {
      ...basicStatus,
      monitoring: {
        metricsCollected: this.performanceMetrics.throughputHistory.length,
        alertsActive: this.alerts.filter(a => !a.acknowledged).length,
        alertsTotal: this.alerts.length,
        lastMetricsUpdate: Date.now()
      },
      performance: {
        averageResponseTime: this.realTimeData.averageResponseTime,
        throughput: this.realTimeData.completedTasksHour,
        failureRate: this.realTimeData.failedTasksHour / 
                    Math.max(1, this.realTimeData.completedTasksHour + this.realTimeData.failedTasksHour)
      },
      resources: {
        memoryUsage: this.systemStats.memoryUsage,
        cpuUsage: this.systemStats.cpuUsage,
        networkTraffic: this.realTimeData.networkTraffic
      },
      clusters: {
        total: this.clusterStatuses.size,
        efficiency: this.calculateOverallClusterEfficiency()
      }
    };
  }

  calculateOverallClusterEfficiency() {
    if (this.performanceMetrics.clusterEfficiency.size === 0) return 0;
    
    let totalEfficiency = 0;
    for (const efficiency of this.performanceMetrics.clusterEfficiency.values()) {
      totalEfficiency += efficiency.efficiency;
    }
    
    return totalEfficiency / this.performanceMetrics.clusterEfficiency.size;
  }

  getPerformanceReport() {
    return {
      timestamp: Date.now(),
      uptime: this.systemStats.uptime,
      throughput: {
        history: this.performanceMetrics.throughputHistory,
        current: this.realTimeData.completedTasksHour,
        average: this.calculateAverageThroughput()
      },
      responseTime: {
        history: this.performanceMetrics.responseTimeHistory,
        current: this.realTimeData.averageResponseTime,
        percentiles: this.calculateResponseTimePercentiles()
      },
      clusters: Array.from(this.performanceMetrics.clusterEfficiency.entries()).map(([id, data]) => ({
        clusterId: id,
        ...data
      })),
      resources: Array.from(this.performanceMetrics.resourceUtilization.entries()).map(([type, data]) => ({
        resourceType: type,
        ...data
      })),
      alerts: this.alerts.slice(-20) // Last 20 alerts
    };
  }

  calculateAverageThroughput() {
    if (this.performanceMetrics.throughputHistory.length === 0) return 0;
    
    const sum = this.performanceMetrics.throughputHistory.reduce(
      (total, entry) => total + entry.completedTasks, 0
    );
    return sum / this.performanceMetrics.throughputHistory.length;
  }

  calculateResponseTimePercentiles() {
    const history = [...this.performanceMetrics.responseTimeHistory].sort((a, b) => a - b);
    if (history.length === 0) return { p50: 0, p90: 0, p95: 0, p99: 0 };
    
    const p50 = history[Math.floor(history.length * 0.5)];
    const p90 = history[Math.floor(history.length * 0.9)];
    const p95 = history[Math.floor(history.length * 0.95)];
    const p99 = history[Math.floor(history.length * 0.99)];
    
    return { p50, p90, p95, p99 };
  }

  // Enhanced update methods
  updateClusterStatus(clusterData) {
    this.clusterStatuses.set(clusterData.id, {
      ...clusterData,
      lastUpdate: Date.now(),
      memberCount: clusterData.members ? clusterData.members.length : 0,
      activeTasks: clusterData.activeTasks || 0,
      completedTasks: clusterData.completedTasks || 0,
      totalTasks: clusterData.totalTasks || 0,
      startTime: clusterData.startTime || Date.now()
    });
    
    this.logger.debug(`Updated cluster status: ${clusterData.id}`);
  }

  updateTaskCompletion(taskData) {
    if (taskData.status === 'completed') {
      this.realTimeData.completedTasksHour++;
      
      // Update task completion rate for the cluster
      if (taskData.clusterId) {
        const rates = this.performanceMetrics.taskCompletionRates.get(taskData.clusterId) || [];
        rates.push({ timestamp: Date.now(), duration: taskData.duration || 0 });
        
        // Keep only last 50 completions
        if (rates.length > 50) rates.shift();
        
        this.performanceMetrics.taskCompletionRates.set(taskData.clusterId, rates);
      }
    } else if (taskData.status === 'failed') {
      this.realTimeData.failedTasksHour++;
      
      // Track error rates
      const errorType = taskData.error?.type || 'unknown';
      const errorCount = this.performanceMetrics.errorRates.get(errorType) || 0;
      this.performanceMetrics.errorRates.set(errorType, errorCount + 1);
    }
  }

  // API endpoints for enhanced monitoring
  getMonitoringData() {
    return {
      realTimeData: this.realTimeData,
      performanceMetrics: {
        responseTimeHistory: this.performanceMetrics.responseTimeHistory,
        throughputHistory: this.performanceMetrics.throughputHistory,
        clusterEfficiency: Array.from(this.performanceMetrics.clusterEfficiency.entries()),
        taskCompletionRates: Array.from(this.performanceMetrics.taskCompletionRates.entries()),
        resourceUtilization: Array.from(this.performanceMetrics.resourceUtilization.entries()),
        errorRates: Array.from(this.performanceMetrics.errorRates.entries())
      },
      alerts: this.alerts.filter(a => !a.acknowledged),
      systemHealth: this.getSystemHealth()
    };
  }

  getSystemHealth() {
    const memUsage = this.systemStats.memoryUsage;
    const memUtilization = memUsage.heapUsed / memUsage.heapTotal;
    
    const health = {
      overall: 'healthy',
      components: {
        memory: memUtilization < 0.8 ? 'healthy' : memUtilization < 0.9 ? 'warning' : 'critical',
        connections: this.realTimeData.connectionHealth.healthy > 0 ? 'healthy' : 'warning',
        tasks: this.realTimeData.activeTasks > 0 ? 'active' : 'idle',
        alerts: this.alerts.filter(a => !a.acknowledged && a.severity === 'critical').length === 0 ? 'healthy' : 'critical'
      },
      scores: {
        performance: this.calculatePerformanceScore(),
        reliability: this.calculateReliabilityScore(),
        efficiency: this.calculateOverallClusterEfficiency()
      }
    };
    
    // Determine overall health
    const criticalComponents = Object.values(health.components).filter(status => status === 'critical');
    const warningComponents = Object.values(health.components).filter(status => status === 'warning');
    
    if (criticalComponents.length > 0) {
      health.overall = 'critical';
    } else if (warningComponents.length > 1) {
      health.overall = 'warning';
    }
    
    return health;
  }

  calculatePerformanceScore() {
    // Base score on response time and throughput
    const responseTimeScore = Math.max(0, 100 - (this.realTimeData.averageResponseTime / 50)); // 0-100 based on response time
    const throughputScore = Math.min(100, this.realTimeData.completedTasksHour * 2); // 0-100 based on throughput
    
    return Math.round((responseTimeScore + throughputScore) / 2);
  }

  calculateReliabilityScore() {
    // Base score on connection health and error rates
    const totalConnections = this.realTimeData.connectionHealth.healthy + 
                           this.realTimeData.connectionHealth.degraded + 
                           this.realTimeData.connectionHealth.failed;
    
    if (totalConnections === 0) return 100;
    
    const connectionScore = (this.realTimeData.connectionHealth.healthy / totalConnections) * 100;
    
    const totalTasks = this.realTimeData.completedTasksHour + this.realTimeData.failedTasksHour;
    const taskSuccessScore = totalTasks === 0 ? 100 : 
                            (this.realTimeData.completedTasksHour / totalTasks) * 100;
    
    return Math.round((connectionScore + taskSuccessScore) / 2);
  }
}

module.exports = CentralNode;
