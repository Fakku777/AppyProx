/**
 * Enhanced Minecraft UI Generator - V2 with Authentic Textures
 * Creates pixel-perfect Minecraft-style interfaces using extracted game textures
 */

class MinecraftUIGeneratorV2 {
  constructor() {
    this.textureBasePath = '/static/textures';
    
    // Minecraft GUI sprite dimensions and coordinates (from widgets.png)
    this.sprites = {
      // Button states (200x20 each)
      button_normal: { x: 0, y: 66, width: 200, height: 20 },
      button_hover: { x: 0, y: 86, width: 200, height: 20 },
      button_disabled: { x: 0, y: 46, width: 200, height: 20 },
      
      // Panel backgrounds
      panel_background: { x: 0, y: 0, width: 256, height: 256 },
      inventory_slot: { x: 7, y: 7, width: 18, height: 18 },
      inventory_slot_selected: { x: 7, y: 25, width: 18, height: 18 },
      
      // Progress bars
      progress_bar_empty: { x: 182, y: 0, width: 64, height: 5 },
      progress_bar_full: { x: 182, y: 5, width: 64, height: 5 },
      
      // Text field
      text_field: { x: 0, y: 106, width: 200, height: 20 },
      
      // Scrollbar
      scrollbar_track: { x: 12, y: 0, width: 12, height: 15 },
      scrollbar_thumb: { x: 0, y: 0, width: 12, height: 15 },
      
      // Window frame
      window_frame: { x: 0, y: 0, width: 256, height: 256 },
      window_corner: { x: 0, y: 0, width: 4, height: 4 }
    };
    
    // Color palette matching Minecraft's GUI
    this.colors = {
      // Dark stone gray (GUI background)
      darkStone: '#383838',
      lightStone: '#555555',
      
      // Button colors
      buttonNormal: '#8B8B8B',
      buttonHover: '#FFFFA0',
      buttonDisabled: '#404040',
      
      // Text colors
      white: '#FFFFFF',
      lightGray: '#AAAAAA',
      darkGray: '#555555',
      black: '#000000',
      
      // Status colors
      green: '#55FF55',
      yellow: '#FFFF55',
      red: '#FF5555',
      blue: '#5555FF',
      
      // Panel colors
      panelBg: '#C0C0C0',
      panelBorder: '#373737',
      panelBorderLight: '#FFFFFF',
      
      // Inventory colors
      slotBg: '#8B8B8B',
      slotBorder: '#373737',
      slotSelected: '#FFFFFF'
    };
    
    this.fonts = {
      minecraft: 'minecraft-font, "Courier New", monospace',
      size: {
        small: '12px',
        normal: '14px',
        large: '18px',
        title: '24px'
      }
    };
  }
  
  generateCompleteCSS() {
    return `
/* Import Minecraft font */
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

/* Base variables */
:root {
  --mc-dark-stone: ${this.colors.darkStone};
  --mc-light-stone: ${this.colors.lightStone};
  --mc-button-normal: ${this.colors.buttonNormal};
  --mc-button-hover: ${this.colors.buttonHover};
  --mc-button-disabled: ${this.colors.buttonDisabled};
  --mc-white: ${this.colors.white};
  --mc-light-gray: ${this.colors.lightGray};
  --mc-dark-gray: ${this.colors.darkGray};
  --mc-black: ${this.colors.black};
  --mc-green: ${this.colors.green};
  --mc-yellow: ${this.colors.yellow};
  --mc-red: ${this.colors.red};
  --mc-blue: ${this.colors.blue};
  --mc-panel-bg: ${this.colors.panelBg};
  --mc-panel-border: ${this.colors.panelBorder};
  --mc-panel-border-light: ${this.colors.panelBorderLight};
}

/* Global styles */
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Press Start 2P', ${this.fonts.minecraft};
  font-size: 12px;
  background: #2F2F2F url('${this.textureBasePath}/demo_background.png') repeat;
  color: var(--mc-white);
  overflow: hidden;
}

/* Main layout */
.minecraft-interface {
  display: grid;
  grid-template-areas: 
    "left-panel map-area right-panel";
  grid-template-columns: 300px 1fr 350px;
  grid-template-rows: 100vh;
  gap: 10px;
  padding: 10px;
  height: 100vh;
  width: 100vw;
}

/* Panel styling with Minecraft textures */
.minecraft-panel {
  background: var(--mc-panel-bg);
  border: 3px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  padding: 15px;
  position: relative;
  overflow-y: auto;
}

.minecraft-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('${this.textureBasePath}/menu_list_background.png') repeat;
  opacity: 0.1;
  pointer-events: none;
}

.minecraft-panel > * {
  position: relative;
  z-index: 1;
}

/* Left panel - Management */
.left-panel {
  grid-area: left-panel;
}

/* Map area - centered square */
.map-area {
  grid-area: map-area;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--mc-dark-stone);
  border: 3px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  position: relative;
}

.map-container {
  width: min(100%, 100vh - 140px);
  height: min(100%, 100vh - 140px);
  aspect-ratio: 1;
  background: #1e1e1e;
  border: 2px solid var(--mc-panel-border);
  position: relative;
  overflow: hidden;
}

#map-canvas {
  width: 100%;
  height: 100%;
  image-rendering: pixelated;
  cursor: crosshair;
}

.map-overlay {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(0, 0, 0, 0.7);
  padding: 8px 12px;
  border: 1px solid var(--mc-panel-border);
  border-radius: 2px;
  font-size: 10px;
  color: var(--mc-white);
  pointer-events: none;
}

/* Right panel - Terminal and controls */
.right-panel {
  grid-area: right-panel;
}

/* Window styling */
.minecraft-window {
  background: var(--mc-panel-bg);
  border: 2px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  margin: 0 0 15px 0;
  position: relative;
}

.minecraft-window-header {
  background: var(--mc-light-stone);
  padding: 8px 12px;
  border-bottom: 2px solid var(--mc-panel-border);
  font-size: 11px;
  color: var(--mc-white);
  text-shadow: 1px 1px 0 var(--mc-black);
}

.minecraft-window-content {
  padding: 12px;
  max-height: 200px;
  overflow-y: auto;
}

/* Authentic Minecraft buttons */
.minecraft-button {
  background: linear-gradient(to bottom, var(--mc-button-normal) 0%, #6A6A6A 100%);
  border: 2px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  color: var(--mc-white);
  font-family: 'Press Start 2P', monospace;
  font-size: 10px;
  text-shadow: 1px 1px 0 var(--mc-black);
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.1s;
  min-width: 80px;
  margin: 2px;
  text-align: center;
  position: relative;
}

.minecraft-button:hover {
  background: linear-gradient(to bottom, var(--mc-button-hover) 0%, #B3B300 100%);
  border-color: #FFFF80 #B3B300 #B3B300 #FFFF80;
}

.minecraft-button:active {
  background: linear-gradient(to bottom, #707070 0%, var(--mc-button-normal) 100%);
  border-color: var(--mc-panel-border) var(--mc-panel-border-light) var(--mc-panel-border-light) var(--mc-panel-border);
}

.minecraft-button:disabled {
  background: linear-gradient(to bottom, var(--mc-button-disabled) 0%, #2A2A2A 100%);
  border-color: #606060 #2A2A2A #2A2A2A #606060;
  color: var(--mc-dark-gray);
  cursor: not-allowed;
}

.minecraft-button-primary {
  background: linear-gradient(to bottom, var(--mc-green) 0%, #40C040 100%);
  border-color: #80FF80 #40C040 #40C040 #80FF80;
}

.minecraft-button-primary:hover {
  background: linear-gradient(to bottom, #80FF80 0%, var(--mc-green) 100%);
}

.minecraft-button-danger {
  background: linear-gradient(to bottom, var(--mc-red) 0%, #C04040 100%);
  border-color: #FF8080 #C04040 #C04040 #FF8080;
}

.minecraft-button-danger:hover {
  background: linear-gradient(to bottom, #FF8080 0%, var(--mc-red) 100%);
}

/* Text input fields */
.minecraft-input {
  background: var(--mc-black);
  border: 2px solid;
  border-color: var(--mc-panel-border) var(--mc-panel-border-light) var(--mc-panel-border-light) var(--mc-panel-border);
  color: var(--mc-white);
  font-family: 'Press Start 2P', monospace;
  font-size: 10px;
  padding: 8px;
  width: 100%;
  margin: 4px 0;
}

.minecraft-input:focus {
  outline: 2px solid var(--mc-blue);
  outline-offset: -2px;
}

/* Dropdown/Select */
.minecraft-select {
  background: var(--mc-button-normal);
  border: 2px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  color: var(--mc-white);
  font-family: 'Press Start 2P', monospace;
  font-size: 10px;
  padding: 8px;
  width: 100%;
  margin: 4px 0;
  cursor: pointer;
}

/* Progress bars */
.minecraft-progress {
  background: var(--mc-black);
  border: 1px solid var(--mc-panel-border);
  height: 10px;
  width: 100%;
  margin: 4px 0;
  overflow: hidden;
}

.minecraft-progress-fill {
  background: linear-gradient(to right, var(--mc-green) 0%, #40C040 100%);
  height: 100%;
  transition: width 0.3s ease;
}

.minecraft-progress-fill.warning {
  background: linear-gradient(to right, var(--mc-yellow) 0%, #C0C040 100%);
}

.minecraft-progress-fill.danger {
  background: linear-gradient(to right, var(--mc-red) 0%, #C04040 100%);
}

/* Status badges */
.minecraft-badge {
  background: var(--mc-dark-stone);
  border: 1px solid var(--mc-panel-border);
  padding: 4px 8px;
  font-size: 9px;
  color: var(--mc-white);
  margin: 2px;
  display: inline-block;
  text-shadow: 1px 1px 0 var(--mc-black);
}

.minecraft-badge.online {
  background: var(--mc-green);
  color: var(--mc-black);
  text-shadow: none;
}

.minecraft-badge.offline {
  background: var(--mc-red);
}

.minecraft-badge.warning {
  background: var(--mc-yellow);
  color: var(--mc-black);
  text-shadow: none;
}

/* Lists */
.minecraft-list {
  background: var(--mc-black);
  border: 2px solid;
  border-color: var(--mc-panel-border) var(--mc-panel-border-light) var(--mc-panel-border-light) var(--mc-panel-border);
  max-height: 150px;
  overflow-y: auto;
  margin: 8px 0;
}

.minecraft-list-item {
  padding: 8px 12px;
  border-bottom: 1px solid var(--mc-dark-gray);
  cursor: pointer;
  transition: background 0.1s;
  font-size: 10px;
}

.minecraft-list-item:hover {
  background: var(--mc-dark-gray);
}

.minecraft-list-item:last-child {
  border-bottom: none;
}

.minecraft-list-item.selected {
  background: var(--mc-blue);
}

/* Command terminal styling */
.minecraft-terminal {
  background: var(--mc-black);
  border: 3px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  color: var(--mc-white);
  font-family: 'Courier New', monospace;
  font-size: 12px;
  height: 400px;
  display: flex;
  flex-direction: column;
  margin-top: 15px;
}

.terminal-header {
  background: var(--mc-dark-stone);
  padding: 8px 12px;
  border-bottom: 2px solid var(--mc-panel-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.terminal-title {
  font-size: 11px;
  color: var(--mc-white);
  text-shadow: 1px 1px 0 var(--mc-black);
}

.terminal-controls {
  display: flex;
  gap: 4px;
}

.terminal-controls button {
  background: var(--mc-button-normal);
  border: 1px solid var(--mc-panel-border);
  color: var(--mc-white);
  padding: 2px 6px;
  font-size: 8px;
  cursor: pointer;
}

.terminal-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.terminal-output {
  flex: 1;
  padding: 8px;
  overflow-y: auto;
  font-size: 11px;
  line-height: 1.4;
  background: rgba(0, 0, 0, 0.5);
}

.terminal-input-container {
  padding: 8px;
  border-top: 1px solid var(--mc-panel-border);
  display: flex;
  align-items: center;
  gap: 8px;
}

.terminal-prompt {
  color: var(--mc-green);
  font-weight: bold;
  font-size: 11px;
}

.terminal-input {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--mc-white);
  font-family: inherit;
  font-size: 11px;
  outline: none;
}

/* Terminal text colors */
.terminal-text-system { color: var(--mc-light-gray); }
.terminal-text-success { color: var(--mc-green); }
.terminal-text-error { color: var(--mc-red); }
.terminal-text-warning { color: var(--mc-yellow); }
.terminal-text-info { color: var(--mc-blue); }

/* Scrollbar styling */
.minecraft-scrollbar::-webkit-scrollbar {
  width: 12px;
}

.minecraft-scrollbar::-webkit-scrollbar-track {
  background: var(--mc-dark-stone);
  border: 1px solid var(--mc-panel-border);
}

.minecraft-scrollbar::-webkit-scrollbar-thumb {
  background: var(--mc-button-normal);
  border: 1px solid var(--mc-panel-border);
}

.minecraft-scrollbar::-webkit-scrollbar-thumb:hover {
  background: var(--mc-button-hover);
}

/* Group management specific styles */
.group-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px;
  margin: 4px 0;
  background: var(--mc-dark-stone);
  border: 1px solid var(--mc-panel-border);
  transition: all 0.2s;
}

.group-item:hover {
  background: var(--mc-light-stone);
  transform: translateX(2px);
}

.group-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.group-color {
  width: 12px;
  height: 12px;
  border: 1px solid var(--mc-panel-border);
  display: inline-block;
}

.group-name {
  font-size: 10px;
  color: var(--mc-white);
}

.group-members {
  font-size: 9px;
  color: var(--mc-light-gray);
}

.group-controls {
  display: flex;
  gap: 4px;
}

/* Responsive design */
@media (max-width: 1200px) {
  .minecraft-interface {
    grid-template-columns: 250px 1fr 300px;
  }
}

@media (max-width: 1000px) {
  .minecraft-interface {
    grid-template-areas: 
      "map-area"
      "left-panel"
      "right-panel";
    grid-template-columns: 1fr;
    grid-template-rows: 60vh auto auto;
  }
  
  .map-container {
    width: 100%;
    height: 100%;
  }
}

/* Hidden state */
.hidden {
  display: none !important;
}

/* Modal overlay */
.minecraft-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.minecraft-modal {
  background: var(--mc-panel-bg);
  border: 3px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  padding: 20px;
  min-width: 300px;
  max-width: 500px;
  color: var(--mc-black);
}

.minecraft-modal h3 {
  margin: 0 0 15px 0;
  color: var(--mc-black);
  text-shadow: 1px 1px 0 rgba(255, 255, 255, 0.5);
}

/* Toast notifications */
.minecraft-toast-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.toast {
  background: var(--mc-panel-bg);
  border: 2px solid;
  border-color: var(--mc-panel-border-light) var(--mc-panel-border) var(--mc-panel-border) var(--mc-panel-border-light);
  padding: 12px;
  min-width: 200px;
  color: var(--mc-black);
  animation: slideIn 0.3s ease;
}

.toast.success {
  border-color: var(--mc-green) #40C040 #40C040 var(--mc-green);
  background: #E6FFE6;
}

.toast.error {
  border-color: var(--mc-red) #C04040 #C04040 var(--mc-red);
  background: #FFE6E6;
}

.toast.warning {
  border-color: var(--mc-yellow) #C0C040 #C0C040 var(--mc-yellow);
  background: #FFFFE6;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}`;
  }
  
  generateMainPageHTML() {
    return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AppyProx - Minecraft Proxy Control Panel</title>
    <style id="minecraft-styles"></style>
</head>
<body>
    <div class="minecraft-interface">
        <!-- Left Panel - Group Management -->
        <div class="left-panel minecraft-panel minecraft-scrollbar">
            <div class="minecraft-window">
                <div class="minecraft-window-header">
                    🎮 Group Management
                </div>
                <div class="minecraft-window-content">
                    <button class="minecraft-button minecraft-button-primary" onclick="createNewGroup()">
                        + New Group
                    </button>
                    <div id="groups-list" class="minecraft-list">
                        <!-- Groups will be populated here -->
                    </div>
                </div>
            </div>
            
            <div class="minecraft-window">
                <div class="minecraft-window-header">
                    👥 Connected Clients
                </div>
                <div class="minecraft-window-content">
                    <div id="clients-list" class="minecraft-list">
                        <!-- Clients will be populated here -->
                    </div>
                </div>
            </div>
            
            <div class="minecraft-window">
                <div class="minecraft-window-header">
                    📊 System Status
                </div>
                <div class="minecraft-window-content">
                    <div class="status-grid">
                        <div>
                            <span>CPU:</span>
                            <div class="minecraft-progress">
                                <div id="cpu-progress" class="minecraft-progress-fill" style="width: 45%;"></div>
                            </div>
                            <span id="cpu-value">45%</span>
                        </div>
                        <div style="margin-top: 10px;">
                            <span>Memory:</span>
                            <div class="minecraft-progress">
                                <div id="memory-progress" class="minecraft-progress-fill warning" style="width: 68%;"></div>
                            </div>
                            <span id="memory-value">68%</span>
                        </div>
                        <div style="margin-top: 10px;">
                            <span>Status:</span>
                            <span id="system-status" class="minecraft-badge online">Online</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Map Area - Centered Square -->
        <div class="map-area">
            <div class="map-container">
                <canvas id="map-canvas"></canvas>
                <div class="map-overlay">
                    <div id="coordinates">X: 0, Z: 0</div>
                    <div id="biome-info">Biome: Plains</div>
                </div>
            </div>
        </div>
        
        <!-- Right Panel - Terminal and Controls -->
        <div class="right-panel minecraft-panel minecraft-scrollbar">
            <div class="minecraft-window">
                <div class="minecraft-window-header">
                    ⚡ Quick Actions
                </div>
                <div class="minecraft-window-content">
                    <button class="minecraft-button" onclick="startAllBots()">Start All Bots</button>
                    <button class="minecraft-button" onclick="stopAllBots()">Stop All Bots</button>
                    <button class="minecraft-button minecraft-button-danger" onclick="emergencyStop()">Emergency Stop</button>
                </div>
            </div>
            
            <div class="minecraft-window">
                <div class="minecraft-window-header">
                    🎯 Active Tasks
                </div>
                <div class="minecraft-window-content">
                    <div id="tasks-list" class="minecraft-list">
                        <!-- Tasks will be populated here -->
                    </div>
                    <select class="minecraft-select" id="task-type">
                        <option value="gather">Gather Resources</option>
                        <option value="mine">Mine Ores</option>
                        <option value="build">Build Structure</option>
                        <option value="farm">Farm Crops</option>
                    </select>
                    <button class="minecraft-button minecraft-button-primary" onclick="createTask()">Create Task</button>
                </div>
            </div>
            
            <!-- Command Terminal -->
            <div class="minecraft-terminal">
                <div class="terminal-header">
                    <div class="terminal-title">💻 Command Terminal</div>
                    <div class="terminal-controls">
                        <button onclick="clearTerminal()">Clear</button>
                        <button onclick="toggleTerminalSize()">📏</button>
                        <button onclick="minimizeTerminal()">_</button>
                    </div>
                </div>
                <div class="terminal-body">
                    <div id="terminal-output" class="terminal-output">
                        <div class="terminal-text-system">AppyProx Command Terminal v2.0</div>
                        <div class="terminal-text-info">Type 'help' for available commands</div>
                        <div class="terminal-text-system">Ready.</div>
                    </div>
                    <div class="terminal-input-container">
                        <span class="terminal-prompt">appyprox@server:~$</span>
                        <input type="text" class="terminal-input" id="terminal-input" placeholder="Enter command..." autocomplete="off">
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Modal Overlay -->
    <div id="modal-overlay" class="minecraft-modal-overlay hidden">
        <div class="minecraft-modal" id="modal-content">
            <!-- Dynamic modal content -->
        </div>
    </div>
    
    <!-- Toast Container -->
    <div id="toast-container" class="minecraft-toast-container"></div>
    
    <script src="/script.js"></script>
</body>
</html>`;
  }
}

module.exports = MinecraftUIGeneratorV2;