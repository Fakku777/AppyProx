/**
 * Authentic Minecraft UI using real game textures and proper layout
 */

class AuthenticMinecraftUIV2 {
  constructor() {
    this.textureBasePath = '/static/textures';
  }
  
  generateCompleteCSS() {
    return `
/* Minecraft Font Import */
@font-face {
  font-family: 'Minecraft';
  src: url('/static/minecraft.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
  font-display: block;
}

/* Google Fonts fallback */
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

/* Ensure all elements use Minecraft font */
*, *::before, *::after {
  font-family: 'Minecraft', 'Press Start 2P', 'Courier New', monospace !important;
  font-smooth: never;
  -webkit-font-smoothing: none;
  -moz-osx-font-smoothing: unset;
}

/* Reset and base styles - removed to avoid conflicts */

body {
  margin: 0;
  padding: 0;
  font-size: 12px;
  background: url('${this.textureBasePath}/menu_background.png') repeat;
  color: #404040;
  overflow: hidden;
  height: 100vh;
  width: 100vw;
  box-sizing: border-box;
  image-rendering: pixelated;
  image-rendering: -moz-crisp-edges;
  image-rendering: crisp-edges;
}

/* Main layout - Left: Management, Center: Map, Right: Terminal */
.minecraft-interface {
  display: grid;
  grid-template-areas: "management map terminal";
  grid-template-columns: 280px 1fr 300px;
  height: 100vh;
  gap: 8px;
  padding: 8px;
}

/* Left Panel - Management */
.management-panel {
  grid-area: management;
  background: url('${this.textureBasePath}/menu_list_background.png') repeat;
  border: 2px solid;
  border-color: #FFFFFF #555555 #555555 #FFFFFF;
  padding: 8px;
  overflow-y: auto;
}

/* Center - Xaeros World Map (Perfect Square) */
.map-panel {
  grid-area: map;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('${this.textureBasePath}/menu_list_background.png') repeat;
  border: 2px solid;
  border-color: #FFFFFF #555555 #555555 #FFFFFF;
  padding: 8px;
}

.map-container {
  width: min(calc(100% - 16px), calc(100vh - 140px));
  height: min(calc(100% - 16px), calc(100vh - 140px));
  aspect-ratio: 1;
  background: #2C2C2C;
  border: 2px solid;
  border-color: #555555 #FFFFFF #FFFFFF #555555;
  position: relative;
  overflow: hidden;
}

#map-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.map-overlay {
  position: absolute;
  top: 4px;
  left: 4px;
  background: rgba(0, 0, 0, 0.7);
  padding: 4px 6px;
  font-size: 10px;
  color: #FFFFFF;
  border: 1px solid #555555;
  font-family: 'Minecraft', monospace;
}

/* Right Panel - Terminal */
.terminal-panel {
  grid-area: terminal;
  background: url('${this.textureBasePath}/menu_list_background.png') repeat;
  border: 2px solid;
  border-color: #FFFFFF #555555 #555555 #FFFFFF;
  padding: 8px;
  display: flex;
  flex-direction: column;
}

/* Minecraft Windows */
.minecraft-window {
  background: url('${this.textureBasePath}/demo_background.png') repeat;
  border: 2px solid;
  border-color: #FFFFFF #555555 #555555 #FFFFFF;
  margin-bottom: 8px;
}

.minecraft-window-header {
  background: url('${this.textureBasePath}/tab_header_background.png') repeat-x;
  padding: 4px 8px;
  font-size: 10px;
  color: #404040;
  text-shadow: 1px 1px 0px #FFFFFF;
  border-bottom: 1px solid #555555;
  font-family: 'Minecraft', monospace;
}

.minecraft-window-content {
  padding: 8px;
  background: rgba(195, 195, 195, 0.9);
}

/* Authentic Minecraft Buttons */
.minecraft-button {
  background: url('${this.textureBasePath}/sprites/widget/button.png');
  background-size: 200px 20px;
  border: none;
  color: #404040;
  font-family: 'Minecraft', monospace;
  font-size: 10px;
  padding: 0;
  width: 200px;
  height: 20px;
  cursor: pointer;
  text-align: center;
  line-height: 20px;
  margin: 2px;
  display: inline-block;
  text-shadow: 1px 1px 0px #FFFFFF;
}

.minecraft-button:hover {
  background-image: url('${this.textureBasePath}/sprites/widget/button_highlighted.png');
  color: #FFFFA0;
  text-shadow: 1px 1px 0px #3F3F00;
}

.minecraft-button:active {
  background-image: url('${this.textureBasePath}/sprites/widget/button_disabled.png');
}

.minecraft-button:disabled {
  background-image: url('${this.textureBasePath}/sprites/widget/button_disabled.png');
  color: #808080;
  cursor: not-allowed;
}

/* Smaller buttons for compact areas */
.minecraft-button-small {
  width: 100px;
  background-size: 100px 20px;
  font-size: 8px;
}

/* Text Input */
.minecraft-input {
  background: url('${this.textureBasePath}/sprites/widget/text_field.png');
  background-size: 200px 20px;
  border: none;
  color: #E0E0E0;
  font-family: 'Minecraft', monospace;
  font-size: 10px;
  padding: 4px 8px;
  width: 200px;
  height: 20px;
  margin: 2px 0;
}

.minecraft-input:focus {
  background-image: url('${this.textureBasePath}/sprites/widget/text_field_highlighted.png');
  outline: none;
  color: #FFFFFF;
}

/* Select Dropdown */
.minecraft-select {
  background: url('${this.textureBasePath}/sprites/widget/button.png');
  background-size: 200px 20px;
  border: none;
  color: #404040;
  font-family: 'Minecraft', monospace;
  font-size: 10px;
  padding: 0 8px;
  width: 200px;
  height: 20px;
  cursor: pointer;
  text-shadow: 1px 1px 0px #FFFFFF;
}

/* Boss Bar Progress Bars */
.minecraft-progress {
  width: 182px;
  height: 5px;
  background: url('${this.textureBasePath}/sprites/boss_bar/green_background.png');
  background-size: 182px 5px;
  position: relative;
  margin: 4px 0;
}

.minecraft-progress-fill {
  height: 100%;
  background: url('${this.textureBasePath}/sprites/boss_bar/green_progress.png');
  background-size: 182px 5px;
  background-repeat: no-repeat;
  transition: width 0.3s ease;
}

.minecraft-progress-fill.cpu {
  background-image: url('${this.textureBasePath}/sprites/boss_bar/blue_progress.png');
}

.minecraft-progress.cpu {
  background-image: url('${this.textureBasePath}/sprites/boss_bar/blue_background.png');
}

.minecraft-progress-fill.memory {
  background-image: url('${this.textureBasePath}/sprites/boss_bar/yellow_progress.png');
}

.minecraft-progress.memory {
  background-image: url('${this.textureBasePath}/sprites/boss_bar/yellow_background.png');
}

.minecraft-progress-fill.task {
  background-image: url('${this.textureBasePath}/sprites/boss_bar/green_progress.png');
}

/* Lists */
.minecraft-list {
  background: #2C2C2C;
  border: 2px solid;
  border-color: #555555 #FFFFFF #FFFFFF #555555;
  max-height: 120px;
  overflow-y: auto;
  margin: 4px 0;
}

.minecraft-list-item {
  padding: 4px 8px;
  font-size: 10px;
  color: #FFFFFF;
  cursor: pointer;
  border-bottom: 1px solid #404040;
  font-family: 'Minecraft', monospace;
  display: flex;
  align-items: center;
  gap: 8px;
}

.minecraft-list-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.minecraft-list-item:last-child {
  border-bottom: none;
}

/* Player avatars using namemc API */
.player-avatar {
  width: 16px;
  height: 16px;
  image-rendering: pixelated;
  background: #555555;
  border: 1px solid #404040;
}

/* Group color indicators */
.group-color {
  width: 12px;
  height: 12px;
  border: 1px solid #404040;
  display: inline-block;
  image-rendering: pixelated;
}

/* Status badges */
.minecraft-badge {
  background: url('${this.textureBasePath}/sprites/widget/button.png');
  background-size: contain;
  padding: 2px 6px;
  font-size: 8px;
  color: #404040;
  text-shadow: 1px 1px 0px #FFFFFF;
  display: inline-block;
  min-width: 40px;
  height: 16px;
  line-height: 12px;
  text-align: center;
}

.minecraft-badge.online {
  color: #00AA00;
}

.minecraft-badge.offline {
  color: #AA0000;
}

/* Terminal */
.minecraft-terminal {
  background: #000000;
  border: 2px solid;
  border-color: #555555 #FFFFFF #FFFFFF #555555;
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-top: 8px;
}

.terminal-header {
  background: url('${this.textureBasePath}/tab_header_background.png') repeat-x;
  padding: 4px 8px;
  font-size: 10px;
  color: #404040;
  text-shadow: 1px 1px 0px #FFFFFF;
  border-bottom: 1px solid #555555;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.terminal-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.terminal-output {
  flex: 1;
  padding: 4px;
  overflow-y: auto;
  font-size: 10px;
  line-height: 1.3;
  color: #FFFFFF;
  font-family: 'Courier New', monospace;
  background: rgba(0, 0, 0, 0.9);
}

.terminal-input-container {
  padding: 4px;
  border-top: 1px solid #555555;
  display: flex;
  align-items: center;
  gap: 4px;
  background: #111111;
}

.terminal-prompt {
  color: #55FF55;
  font-size: 10px;
  font-family: 'Courier New', monospace;
}

.terminal-input {
  flex: 1;
  background: transparent;
  border: none;
  color: #FFFFFF;
  font-family: 'Courier New', monospace;
  font-size: 10px;
  outline: none;
}

/* Terminal text colors */
.terminal-text-system { color: #AAAAAA; }
.terminal-text-success { color: #55FF55; }
.terminal-text-error { color: #FF5555; }
.terminal-text-warning { color: #FFFF55; }
.terminal-text-info { color: #5555FF; }
.terminal-text-command { color: #FFFFFF; }

/* Scrollbars with Minecraft styling */
.minecraft-scrollbar::-webkit-scrollbar {
  width: 12px;
  background: url('${this.textureBasePath}/sprites/widget/scroller_background.png') repeat-y;
}

.minecraft-scrollbar::-webkit-scrollbar-thumb {
  background: url('${this.textureBasePath}/sprites/widget/scroller.png') no-repeat center;
  background-size: contain;
}

.minecraft-scrollbar::-webkit-scrollbar-corner {
  background: #2C2C2C;
}

/* Modal */
.minecraft-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.minecraft-modal {
  background: url('${this.textureBasePath}/demo_background.png') repeat;
  border: 2px solid;
  border-color: #FFFFFF #555555 #555555 #FFFFFF;
  padding: 16px;
  min-width: 300px;
  max-width: 500px;
  color: #404040;
  font-family: 'Minecraft', monospace;
}

.minecraft-modal h3 {
  margin-bottom: 12px;
  font-size: 12px;
  text-shadow: 1px 1px 0px #FFFFFF;
}

/* Toast notifications */
.minecraft-toast-container {
  position: fixed;
  top: 16px;
  right: 16px;
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.toast {
  background: url('${this.textureBasePath}/demo_background.png') repeat;
  border: 2px solid;
  border-color: #FFFFFF #555555 #555555 #FFFFFF;
  padding: 8px 12px;
  min-width: 200px;
  color: #404040;
  font-size: 10px;
  font-family: 'Minecraft', monospace;
  animation: slideInRight 0.3s ease;
}

.toast.success {
  border-color: #00AA00 #005500 #005500 #00AA00;
}

.toast.error {
  border-color: #AA0000 #550000 #550000 #AA0000;
}

.toast.warning {
  border-color: #AAAA00 #555500 #555500 #AAAA00;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* Utility classes */
.hidden {
  display: none !important;
}

.text-center {
  text-align: center;
}

.mt-4 {
  margin-top: 4px;
}

.mb-4 {
  margin-bottom: 4px;
}

.flex {
  display: flex;
}

.flex-column {
  flex-direction: column;
}

.justify-between {
  justify-content: space-between;
}

.align-center {
  align-items: center;
}

.gap-4 {
  gap: 4px;
}

/* Responsive adjustments */
@media (max-width: 1200px) {
  .minecraft-interface {
    grid-template-columns: 250px 1fr 280px;
  }
}

@media (max-width: 1000px) {
  .minecraft-interface {
    grid-template-areas: 
      "map"
      "management" 
      "terminal";
    grid-template-columns: 1fr;
    grid-template-rows: 1fr auto auto;
  }
  
  .management-panel,
  .terminal-panel {
    max-height: 200px;
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
    <link rel="stylesheet" href="/style.css">
    <link rel="icon" type="image/x-icon" href="${this.textureBasePath}/sprites/item/diamond.png">
</head>
<body>
    <div class="minecraft-interface">
        <!-- Left Panel - Management Menu -->
        <div class="management-panel minecraft-scrollbar">
            <div class="minecraft-window">
                <div class="minecraft-window-header">🎮 Group Management</div>
                <div class="minecraft-window-content">
                    <button class="minecraft-button minecraft-button-small" onclick="createNewGroup()">+ New Group</button>
                    <div id="groups-list" class="minecraft-list minecraft-scrollbar"></div>
                </div>
            </div>
            
            <div class="minecraft-window">
                <div class="minecraft-window-header">👥 Connected Players</div>
                <div class="minecraft-window-content">
                    <div id="clients-list" class="minecraft-list minecraft-scrollbar"></div>
                </div>
            </div>
            
            <div class="minecraft-window">
                <div class="minecraft-window-header">📊 System Status</div>
                <div class="minecraft-window-content">
                    <div class="mb-4">
                        <div style="font-size: 9px; margin-bottom: 2px;">CPU Usage:</div>
                        <div class="minecraft-progress cpu">
                            <div id="cpu-progress" class="minecraft-progress-fill cpu" style="width: 45%;"></div>
                        </div>
                        <div style="font-size: 8px; color: #666;">45%</div>
                    </div>
                    <div class="mb-4">
                        <div style="font-size: 9px; margin-bottom: 2px;">Memory Usage:</div>
                        <div class="minecraft-progress memory">
                            <div id="memory-progress" class="minecraft-progress-fill memory" style="width: 68%;"></div>
                        </div>
                        <div style="font-size: 8px; color: #666;">68%</div>
                    </div>
                    <div>
                        Status: <span id="system-status" class="minecraft-badge online">Online</span>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Center Panel - Xaeros World Map (Perfect Square) -->
        <div class="map-panel">
            <div class="map-container">
                <canvas id="map-canvas"></canvas>
                <div class="map-overlay">
                    <div id="coordinates">X: 0, Z: 0</div>
                    <div id="biome-info">Plains</div>
                    <div id="zoom-info">Zoom: 1.0x</div>
                </div>
            </div>
        </div>
        
        <!-- Right Panel - Terminal -->
        <div class="terminal-panel">
            <div class="minecraft-window">
                <div class="minecraft-window-header">⚡ Quick Actions</div>
                <div class="minecraft-window-content text-center">
                    <button class="minecraft-button minecraft-button-small" onclick="startAllBots()">Start All</button>
                    <button class="minecraft-button minecraft-button-small" onclick="stopAllBots()">Stop All</button>
                    <button class="minecraft-button minecraft-button-small" onclick="emergencyStop()">Emergency</button>
                </div>
            </div>
            
            <div class="minecraft-window">
                <div class="minecraft-window-header">🎯 Active Tasks</div>
                <div class="minecraft-window-content">
                    <div id="tasks-list" class="minecraft-list minecraft-scrollbar"></div>
                    <select class="minecraft-select" id="task-type" style="width: 100%; font-size: 8px;">
                        <option value="gather">Gather Resources</option>
                        <option value="mine">Mine Ores</option>
                        <option value="build">Build Structure</option>
                        <option value="farm">Farm Crops</option>
                    </select>
                    <div class="text-center mt-4">
                        <button class="minecraft-button minecraft-button-small" onclick="createTask()">Create Task</button>
                    </div>
                </div>
            </div>
            
            <!-- Command Terminal -->
            <div class="minecraft-terminal">
                <div class="terminal-header">
                    <div style="font-family: 'Minecraft', monospace;">💻 Command Terminal</div>
                    <div style="font-size: 8px;">Ready</div>
                </div>
                <div class="terminal-body">
                    <div id="terminal-output" class="terminal-output">
                        <div class="terminal-text-system">[INFO] AppyProx Command Terminal v2.0</div>
                        <div class="terminal-text-info">[INFO] Type 'help' for available commands</div>
                        <div class="terminal-text-success">[READY] Terminal initialized</div>
                    </div>
                    <div class="terminal-input-container">
                        <span class="terminal-prompt">appyprox$</span>
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

module.exports = AuthenticMinecraftUIV2;