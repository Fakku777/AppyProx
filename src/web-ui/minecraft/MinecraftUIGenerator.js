/**
 * Minecraft UI Texture Generator
 * Generates authentic Minecraft-style UI elements using CSS and patterns
 */

class MinecraftUIGenerator {
  constructor() {
    // Minecraft UI color palette (based on actual textures)
    this.colors = {
      // Button states
      buttonNormal: '#c6c6c6',
      buttonHover: '#ffffa0', 
      buttonPressed: '#a0a0a0',
      buttonDisabled: '#666666',
      
      // Panel colors
      panelDark: '#373737',
      panelMid: '#5b5b5b', 
      panelLight: '#8b8b8b',
      panelBorder: '#000000',
      
      // Window frame
      windowFrame: '#c6c6c6',
      windowBorder: '#373737',
      windowHeader: '#5b5b5b',
      
      // Text colors
      textWhite: '#ffffff',
      textYellow: '#ffff55',
      textGray: '#aaaaaa',
      textDark: '#373737',
      
      // Progress/health bars
      healthRed: '#ce2029',
      healthDark: '#5a0e13',
      hungerOrange: '#cc8e35',
      hungerDark: '#553519',
      experienceGreen: '#8cc33c',
      experienceDark: '#2e4731',
      
      // Inventory slots
      slotNormal: '#8b8b8b',
      slotSelected: '#ffffff',
      slotHover: '#a0cfff'
    };
    
    // UI element templates (initialized lazily)
    this.templates = null;
  }
  
  /**
   * Initialize templates safely
   */
  initializeTemplates() {
    if (this.templates === null) {
      this.templates = {
        button: this.generateButtonTemplate(),
        panel: this.generatePanelTemplate(),
        window: this.generateWindowTemplate(),
        slot: this.generateSlotTemplate(),
        scrollbar: this.generateScrollbarTemplate(),
        progressBar: this.generateProgressBarTemplate(),
        tooltip: this.generateTooltipTemplate()
      };
    }
  }
  
  /**
   * Generate complete Minecraft UI CSS (alias for compatibility)
   */
  generateCompleteCSS() {
    this.initializeTemplates();
    return this.generateMinecraftCSS();
  }
  
  /**
   * Generate complete Minecraft UI CSS
   */
  generateMinecraftCSS() {
    return `
/* Minecraft UI System - Authentic Textures */

/* Import Minecraft font (placeholder - should be loaded from assets) */
@font-face {
  font-family: 'Minecraft';
  src: url('/static/minecraft-ui/minecraft.woff2') format('woff2'),
       url('/static/minecraft-ui/minecraft.woff') format('woff');
  font-weight: normal;
  font-style: normal;
}

/* Base minecraft styling */
.minecraft-ui {
  font-family: 'Minecraft', 'Courier New', monospace;
  font-size: 8px;
  image-rendering: pixelated;
  image-rendering: -moz-crisp-edges;
  image-rendering: crisp-edges;
}

/* Minecraft Button */
.minecraft-button {
  ${this.templates.button}
  font-family: 'Minecraft', monospace;
  font-size: 10px;
  color: ${this.colors.textWhite};
  text-shadow: 2px 2px 0px ${this.colors.textDark};
  border: none;
  cursor: pointer;
  user-select: none;
  transition: none;
  image-rendering: pixelated;
  min-height: 20px;
  padding: 4px 8px;
  position: relative;
  overflow: hidden;
}

.minecraft-button:hover {
  background-image: 
    linear-gradient(to bottom, ${this.colors.buttonHover} 0%, ${this.colors.buttonHover} 50%, ${this.colors.buttonNormal} 50%, ${this.colors.buttonNormal} 100%),
    repeating-linear-gradient(90deg, transparent, transparent 1px, rgba(0,0,0,0.1) 1px, rgba(0,0,0,0.1) 2px);
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.3),
    inset -2px -2px 0px rgba(0,0,0,0.3);
}

.minecraft-button:active {
  background-image: 
    linear-gradient(to bottom, ${this.colors.buttonPressed} 0%, ${this.colors.buttonPressed} 50%, ${this.colors.panelMid} 50%, ${this.colors.panelMid} 100%);
  box-shadow: 
    inset -1px -1px 0px rgba(255,255,255,0.2),
    inset 1px 1px 0px rgba(0,0,0,0.4);
  transform: translate(1px, 1px);
}

.minecraft-button:disabled {
  background-image: 
    linear-gradient(to bottom, ${this.colors.buttonDisabled} 0%, ${this.colors.buttonDisabled} 50%, ${this.colors.panelDark} 50%, ${this.colors.panelDark} 100%);
  color: ${this.colors.textGray};
  cursor: not-allowed;
}

/* Minecraft Panel */
.minecraft-panel {
  ${this.templates.panel}
  position: relative;
  overflow: hidden;
}

.minecraft-panel-header {
  background: linear-gradient(to bottom, ${this.colors.windowHeader} 0%, ${this.colors.panelDark} 100%);
  border-bottom: 2px solid ${this.colors.panelBorder};
  padding: 4px 8px;
  font-weight: bold;
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  font-size: 10px;
}

/* Minecraft Window */
.minecraft-window {
  ${this.templates.window}
  position: relative;
  min-width: 176px; /* Standard MC GUI width */
}

.minecraft-window-title {
  position: absolute;
  top: 6px;
  left: 8px;
  color: ${this.colors.textDark};
  font-size: 10px;
  font-weight: bold;
  text-shadow: none;
}

.minecraft-window-content {
  padding: 20px 8px 8px 8px;
}

/* Minecraft Inventory Slot */
.minecraft-slot {
  ${this.templates.slot}
  width: 18px;
  height: 18px;
  position: relative;
  display: inline-block;
  cursor: pointer;
  image-rendering: pixelated;
}

.minecraft-slot:hover {
  box-shadow: 
    inset 0 0 0 1px ${this.colors.slotHover},
    inset 2px 2px 0px rgba(255,255,255,0.4);
}

.minecraft-slot.selected {
  box-shadow: 
    inset 0 0 0 1px ${this.colors.slotSelected},
    inset 2px 2px 0px rgba(255,255,255,0.6);
}

.minecraft-slot-item {
  width: 16px;
  height: 16px;
  position: absolute;
  top: 1px;
  left: 1px;
  image-rendering: pixelated;
}

.minecraft-slot-count {
  position: absolute;
  bottom: 1px;
  right: 1px;
  font-size: 6px;
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  pointer-events: none;
}

/* Minecraft Scrollbar */
.minecraft-scrollbar {
  ${this.templates.scrollbar}
}

.minecraft-scrollbar-track {
  width: 12px;
  background: ${this.colors.panelDark};
  border: 1px solid ${this.colors.panelBorder};
  position: relative;
}

.minecraft-scrollbar-thumb {
  background: linear-gradient(to right, ${this.colors.panelLight} 0%, ${this.colors.panelMid} 100%);
  border: 1px solid ${this.colors.panelBorder};
  width: 10px;
  cursor: pointer;
  position: relative;
}

.minecraft-scrollbar-thumb:hover {
  background: linear-gradient(to right, ${this.colors.buttonHover} 0%, ${this.colors.buttonNormal} 100%);
}

/* Minecraft Progress Bar (Health/Hunger/XP) */
.minecraft-progress {
  ${this.templates.progressBar}
  display: flex;
  align-items: center;
  height: 12px;
  position: relative;
}

.minecraft-progress-background {
  background: ${this.colors.panelDark};
  border: 1px solid ${this.colors.panelBorder};
  width: 100%;
  height: 10px;
  position: relative;
  overflow: hidden;
}

.minecraft-progress-fill {
  height: 100%;
  transition: width 0.3s ease;
  image-rendering: pixelated;
}

.minecraft-progress-fill.health {
  background: linear-gradient(to bottom, ${this.colors.healthRed} 0%, ${this.colors.healthDark} 100%);
  background-image: repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(0,0,0,0.1) 2px, rgba(0,0,0,0.1) 4px);
}

.minecraft-progress-fill.hunger {
  background: linear-gradient(to bottom, ${this.colors.hungerOrange} 0%, ${this.colors.hungerDark} 100%);
  background-image: repeating-linear-gradient(90deg, transparent, transparent 2px, rgba(0,0,0,0.1) 2px, rgba(0,0,0,0.1) 4px);
}

.minecraft-progress-fill.experience {
  background: linear-gradient(to bottom, ${this.colors.experienceGreen} 0%, ${this.colors.experienceDark} 100%);
  background-image: repeating-linear-gradient(90deg, transparent, transparent 1px, rgba(0,0,0,0.1) 1px, rgba(0,0,0,0.1) 2px);
}

/* Minecraft Tooltip */
.minecraft-tooltip {
  ${this.templates.tooltip}
  position: absolute;
  z-index: 9999;
  pointer-events: none;
  font-size: 8px;
  line-height: 1.2;
  max-width: 200px;
  word-wrap: break-word;
}

.minecraft-tooltip-content {
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.minecraft-tooltip-title {
  color: ${this.colors.textYellow};
  font-weight: bold;
  margin-bottom: 2px;
}

/* Minecraft List Items */
.minecraft-list-item {
  background: linear-gradient(to right, ${this.colors.panelMid} 0%, ${this.colors.panelDark} 100%);
  border: 1px solid ${this.colors.panelBorder};
  margin-bottom: 1px;
  padding: 4px 6px;
  cursor: pointer;
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  font-size: 8px;
  user-select: none;
}

.minecraft-list-item:hover {
  background: linear-gradient(to right, ${this.colors.buttonHover} 0%, ${this.colors.buttonNormal} 100%);
  color: ${this.colors.textDark};
  text-shadow: none;
}

.minecraft-list-item.selected {
  background: linear-gradient(to right, ${this.colors.slotSelected} 0%, ${this.colors.slotHover} 100%);
  color: ${this.colors.textDark};
  text-shadow: none;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.5);
}

/* Minecraft Input Fields */
.minecraft-input {
  background: ${this.colors.panelDark};
  border: 2px inset ${this.colors.panelMid};
  color: ${this.colors.textWhite};
  font-family: 'Minecraft', monospace;
  font-size: 8px;
  padding: 4px;
  outline: none;
  image-rendering: pixelated;
}

.minecraft-input:focus {
  border-color: ${this.colors.buttonHover};
  box-shadow: inset 0 0 0 1px ${this.colors.buttonHover};
}

/* Minecraft Tabs */
.minecraft-tabs {
  display: flex;
  border-bottom: 2px solid ${this.colors.panelBorder};
}

.minecraft-tab {
  background: linear-gradient(to bottom, ${this.colors.panelMid} 0%, ${this.colors.panelDark} 100%);
  border: 1px solid ${this.colors.panelBorder};
  border-bottom: none;
  padding: 4px 8px;
  cursor: pointer;
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  font-size: 8px;
  margin-right: 1px;
  user-select: none;
}

.minecraft-tab:hover {
  background: linear-gradient(to bottom, ${this.colors.buttonHover} 0%, ${this.colors.buttonNormal} 100%);
  color: ${this.colors.textDark};
  text-shadow: none;
}

.minecraft-tab.active {
  background: linear-gradient(to bottom, ${this.colors.windowFrame} 0%, ${this.colors.panelLight} 100%);
  color: ${this.colors.textDark};
  text-shadow: none;
  border-bottom: 2px solid ${this.colors.windowFrame};
  margin-bottom: -2px;
}

/* Health/Hunger Icons (heart and food icons) */
.minecraft-health-icon,
.minecraft-hunger-icon {
  width: 12px;
  height: 12px;
  display: inline-block;
  image-rendering: pixelated;
  background-size: contain;
}

.minecraft-health-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M6 2C4 0 0 2 0 6c0 4 6 6 6 6s6-2 6-6c0-4-4-6-6-4z' fill='%23ce2029'/%3E%3C/svg%3E");
}

.minecraft-health-icon.empty {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M6 2C4 0 0 2 0 6c0 4 6 6 6 6s6-2 6-6c0-4-4-6-6-4z' fill='none' stroke='%235a0e13' stroke-width='1'/%3E%3C/svg%3E");
}

.minecraft-hunger-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M2 3h8l-1 6H3z' fill='%23cc8e35'/%3E%3C/svg%3E");
}

.minecraft-hunger-icon.empty {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M2 3h8l-1 6H3z' fill='none' stroke='%23553519' stroke-width='1'/%3E%3C/svg%3E");
}

/* Hotbar styling */
.minecraft-hotbar {
  display: flex;
  gap: 1px;
  background: linear-gradient(to bottom, ${this.colors.panelMid} 0%, ${this.colors.panelDark} 100%);
  border: 2px solid ${this.colors.panelBorder};
  padding: 4px;
  border-radius: 0;
  image-rendering: pixelated;
}

.minecraft-hotbar-slot {
  width: 20px;
  height: 20px;
  background: ${this.colors.panelDark};
  border: 1px solid ${this.colors.panelBorder};
  position: relative;
  cursor: pointer;
}

.minecraft-hotbar-slot.selected {
  border: 2px solid ${this.colors.slotSelected};
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.3);
}

/* Xaeros-style map UI elements */
.xaeros-button {
  width: 20px;
  height: 20px;
  background: linear-gradient(to bottom, ${this.colors.buttonNormal} 0%, ${this.colors.panelMid} 100%);
  border: 1px solid ${this.colors.panelBorder};
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: ${this.colors.textDark};
  user-select: none;
  image-rendering: pixelated;
}

.xaeros-button:hover {
  background: linear-gradient(to bottom, ${this.colors.buttonHover} 0%, ${this.colors.buttonNormal} 100%);
  box-shadow: inset 1px 1px 0px rgba(255,255,255,0.3);
}

.xaeros-button:active {
  background: linear-gradient(to bottom, ${this.colors.buttonPressed} 0%, ${this.colors.panelDark} 100%);
  box-shadow: inset -1px -1px 0px rgba(255,255,255,0.1);
}

.xaeros-tooltip {
  background: rgba(0, 0, 0, 0.8);
  border: 1px solid ${this.colors.buttonHover};
  color: ${this.colors.textWhite};
  padding: 4px 6px;
  font-size: 8px;
  border-radius: 0;
  position: absolute;
  z-index: 1000;
  pointer-events: none;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

/* Animation classes */
@keyframes minecraft-button-press {
  0% { transform: scale(1); }
  50% { transform: scale(0.95); }
  100% { transform: scale(1); }
}

.minecraft-button-animate {
  animation: minecraft-button-press 0.1s ease;
}

${this.generatePlayerVisualizationCSS()}

${this.generateGroupsVisualizationCSS()}
`;
  }
  
  generateButtonTemplate() {
    return `
  background: linear-gradient(to bottom, ${this.colors.buttonNormal} 0%, ${this.colors.buttonNormal} 50%, ${this.colors.panelMid} 50%, ${this.colors.panelMid} 100%);
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.25),
    inset -2px -2px 0px rgba(0,0,0,0.25);
  border: 1px solid ${this.colors.panelBorder};
  `;
  }
  
  generatePanelTemplate() {
    return `
  background: linear-gradient(135deg, ${this.colors.panelMid} 0%, ${this.colors.panelDark} 100%);
  border: 2px solid ${this.colors.panelBorder};
  box-shadow: 
    inset 1px 1px 0px rgba(255,255,255,0.1),
    inset -1px -1px 0px rgba(0,0,0,0.2);
  `;
  }
  
  generateWindowTemplate() {
    return `
  background: linear-gradient(to bottom, ${this.colors.windowFrame} 0%, ${this.colors.panelLight} 100%);
  border: 2px solid ${this.colors.windowBorder};
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.3),
    inset -2px -2px 0px rgba(0,0,0,0.3),
    0 4px 8px rgba(0,0,0,0.3);
  `;
  }
  
  generateSlotTemplate() {
    return `
  background: ${this.colors.slotNormal};
  border: 1px solid ${this.colors.panelBorder};
  box-shadow: 
    inset 1px 1px 0px rgba(0,0,0,0.3),
    inset -1px -1px 0px rgba(255,255,255,0.2);
  `;
  }
  
  generateScrollbarTemplate() {
    return `
  image-rendering: pixelated;
  `;
  }
  
  generateProgressBarTemplate() {
    return `
  image-rendering: pixelated;
  `;
  }
  
  generateTooltipTemplate() {
    return `
  background: rgba(16, 0, 16, 0.94);
  border: 1px solid ${this.colors.buttonHover};
  padding: 4px 6px;
  border-radius: 0;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.5);
  `;
  }
  
  /**
   * Generate Player Visualization CSS
   */
  generatePlayerVisualizationCSS() {
    return `
/* Player Visualization System */

/* Player Info Overlay */
.player-info-overlay {
  position: fixed;
  top: 80px;
  left: 20px;
  width: 300px;
  background: ${this.colors.panelDark};
  border: 2px solid ${this.colors.panelBorder};
  border-radius: 0;
  padding: 12px;
  z-index: 1500;
  box-shadow: 
    inset 1px 1px 0px rgba(255,255,255,0.1),
    inset -1px -1px 0px rgba(0,0,0,0.2),
    0 4px 8px rgba(0,0,0,0.3);
  image-rendering: pixelated;
}

.player-info-overlay.hidden {
  display: none;
}

.player-info-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 8px;
  background: linear-gradient(to bottom, ${this.colors.windowHeader} 0%, ${this.colors.panelDark} 100%);
  border: 1px solid ${this.colors.panelBorder};
}

.player-avatar-container {
  width: 32px;
  height: 32px;
  border: 1px solid ${this.colors.panelBorder};
  background: ${this.colors.slotNormal};
  display: flex;
  align-items: center;
  justify-content: center;
}

#player-avatar-canvas {
  image-rendering: pixelated;
}

.player-basic-info {
  flex: 1;
}

.player-basic-info h3 {
  color: ${this.colors.textWhite};
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 4px;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.player-basic-info p {
  color: ${this.colors.textGray};
  font-size: 10px;
  margin: 2px 0;
}

/* Player Stats */
.player-stats {
  margin-bottom: 12px;
}

.stat-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding: 4px;
}

.stat-row label {
  min-width: 50px;
  color: ${this.colors.textGray};
  font-size: 10px;
  font-weight: bold;
}

/* Health Hearts */
.health-bar {
  display: flex;
  gap: 1px;
}

.health-heart {
  width: 9px;
  height: 9px;
  background-size: 9px 9px;
  image-rendering: pixelated;
}

.health-heart.full {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M1 3c0-1 1-2 2-2s2 1 2 2c0 0 0-1 2-2s2 1 2 2c0 1-1 2-2 3l-2 2-2-2c-1-1-2-2-2-3z' fill='%23ce2029'/%3E%3C/svg%3E");
}

.health-heart.half {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M1 3c0-1 1-2 2-2s2 1 2 2v5l-2-2c-1-1-2-2-2-3z' fill='%23ce2029'/%3E%3Cpath d='M5 3c0 0 0-1 2-2s2 1 2 2c0 1-1 2-2 3l-2 2V3z' fill='%235a0e13'/%3E%3C/svg%3E");
}

.health-heart.empty {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M1 3c0-1 1-2 2-2s2 1 2 2c0 0 0-1 2-2s2 1 2 2c0 1-1 2-2 3l-2 2-2-2c-1-1-2-2-2-3z' fill='none' stroke='%235a0e13' stroke-width='1'/%3E%3C/svg%3E");
}

/* Hunger Drumsticks */
.hunger-bar {
  display: flex;
  gap: 1px;
}

.hunger-drumstick {
  width: 9px;
  height: 9px;
  background-size: 9px 9px;
  image-rendering: pixelated;
}

.hunger-drumstick.full {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M3 2h3v5H3z' fill='%23cc8e35'/%3E%3Cpath d='M2 7h5v2H2z' fill='%23553519'/%3E%3C/svg%3E");
}

.hunger-drumstick.half {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M3 2h1.5v5H3z' fill='%23cc8e35'/%3E%3Cpath d='M4.5 2h1.5v5H4.5z' fill='%23553519'/%3E%3Cpath d='M2 7h5v2H2z' fill='%23553519'/%3E%3C/svg%3E");
}

.hunger-drumstick.empty {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M3 2h3v5H3z' fill='none' stroke='%23553519' stroke-width='1'/%3E%3Cpath d='M2 7h5v2H2z' fill='%23553519'/%3E%3C/svg%3E");
}

/* Armor Icons */
.armor-bar {
  display: flex;
  gap: 1px;
}

.armor-icon {
  width: 9px;
    height: 9px;
  background-size: 9px 9px;
  image-rendering: pixelated;
}

.armor-icon.full {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M4 1L2 3v4h5V3L5 1z' fill='%23c6c6c6'/%3E%3C/svg%3E");
}

.armor-icon.half {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M4 1L2 3v4h2.5V1z' fill='%23c6c6c6'/%3E%3Cpath d='M4.5 1L7 3v4H4.5z' fill='%23666666'/%3E%3C/svg%3E");
}

.armor-icon.empty {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='9' height='9' viewBox='0 0 9 9'%3E%3Cpath d='M4 1L2 3v4h5V3L5 1z' fill='none' stroke='%23666666' stroke-width='1'/%3E%3C/svg%3E");
}

/* Player Actions */
.player-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.player-actions button {
  width: 100%;
  font-size: 10px;
  padding: 6px 8px;
}

/* Hotbar */
.player-hotbar {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1400;
}

.player-hotbar.hidden {
  display: none;
}

.hotbar-container {
  display: flex;
  gap: 2px;
  background: linear-gradient(to bottom, ${this.colors.panelMid} 0%, ${this.colors.panelDark} 100%);
  border: 2px solid ${this.colors.panelBorder};
  padding: 4px;
  border-radius: 0;
  image-rendering: pixelated;
  box-shadow: 
    inset 1px 1px 0px rgba(255,255,255,0.1),
    inset -1px -1px 0px rgba(0,0,0,0.2);
}

.hotbar-slot {
  width: 20px;
  height: 20px;
  background: ${this.colors.slotNormal};
  border: 1px solid ${this.colors.panelBorder};
  position: relative;
  cursor: pointer;
  box-shadow: 
    inset 1px 1px 0px rgba(0,0,0,0.3),
    inset -1px -1px 0px rgba(255,255,255,0.2);
}

.hotbar-slot.selected {
  border: 2px solid ${this.colors.slotSelected};
  box-shadow: 
    inset 0 0 0 1px rgba(255,255,255,0.4),
    inset 1px 1px 0px rgba(0,0,0,0.3);
}

.hotbar-slot:hover {
  border-color: ${this.colors.slotHover};
}

.selected-slot-indicator {
  position: fixed;
  width: 24px;
  height: 24px;
  border: 2px solid ${this.colors.buttonHover};
  pointer-events: none;
  z-index: 1401;
  transition: all 0.1s ease;
  background: rgba(255, 255, 160, 0.1);
}

.slot-item-canvas {
  width: 16px;
  height: 16px;
  position: absolute;
  top: 2px;
  left: 2px;
  image-rendering: pixelated;
}

.item-count {
  position: absolute;
  bottom: 1px;
  right: 2px;
  color: ${this.colors.textWhite};
  font-size: 8px;
  font-weight: bold;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  pointer-events: none;
}

/* Inventory Modal */
.minecraft-inventory {
  width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  background: linear-gradient(to bottom, ${this.colors.windowFrame} 0%, ${this.colors.panelLight} 100%);
  border: 3px solid ${this.colors.windowBorder};
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.3),
    inset -2px -2px 0px rgba(0,0,0,0.3),
    0 8px 16px rgba(0,0,0,0.4);
}

.inventory-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: linear-gradient(to bottom, ${this.colors.windowHeader} 0%, ${this.colors.panelDark} 100%);
  border-bottom: 2px solid ${this.colors.panelBorder};
}

.inventory-header h3 {
  color: ${this.colors.textDark};
  font-size: 12px;
  font-weight: bold;
  text-shadow: none;
}

.close-btn {
  background: none;
  border: none;
  color: ${this.colors.textDark};
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: ${this.colors.textWhite};
  background: ${this.colors.panelDark};
}

.inventory-content {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.armor-section,
.main-inventory,
.inventory-hotbar {
  margin-bottom: 12px;
}

.armor-section h4,
.main-inventory h4,
.inventory-hotbar h4 {
  color: ${this.colors.textDark};
  font-size: 10px;
  font-weight: bold;
  margin-bottom: 8px;
  text-transform: uppercase;
}

.armor-slots {
  display: flex;
  gap: 4px;
}

.armor-slot {
  width: 18px;
  height: 18px;
  background: ${this.colors.slotNormal};
  border: 1px solid ${this.colors.panelBorder};
  position: relative;
  cursor: pointer;
  box-shadow: 
    inset 1px 1px 0px rgba(0,0,0,0.3),
    inset -1px -1px 0px rgba(255,255,255,0.2);
}

.armor-slot:hover {
  border-color: ${this.colors.slotHover};
}

.inventory-grid,
.hotbar-grid {
  display: grid;
  gap: 2px;
}

.inventory-grid {
  grid-template-columns: repeat(9, 18px);
}

.hotbar-grid {
  grid-template-columns: repeat(9, 18px);
}

.inventory-slot {
  width: 18px;
  height: 18px;
  background: ${this.colors.slotNormal};
  border: 1px solid ${this.colors.panelBorder};
  position: relative;
  cursor: pointer;
  box-shadow: 
    inset 1px 1px 0px rgba(0,0,0,0.3),
    inset -1px -1px 0px rgba(255,255,255,0.2);
}

.inventory-slot:hover {
  border-color: ${this.colors.slotHover};
  box-shadow: 
    inset 0 0 0 1px ${this.colors.slotHover},
    inset 2px 2px 0px rgba(255,255,255,0.4);
}

.inventory-slot .slot-item-canvas {
  width: 16px;
  height: 16px;
  position: absolute;
  top: 1px;
  left: 1px;
  image-rendering: pixelated;
}

.inventory-slot .item-count {
  position: absolute;
  bottom: 1px;
  right: 2px;
  color: ${this.colors.textWhite};
  font-size: 7px;
  font-weight: bold;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  pointer-events: none;
}

.inventory-footer {
  padding: 8px 12px;
  border-top: 2px solid ${this.colors.panelBorder};
  background: ${this.colors.panelDark};
  display: flex;
  gap: 8px;
}

.inventory-footer button {
  flex: 1;
  font-size: 10px;
  padding: 6px 8px;
}

/* Stat text styles */
.minecraft-text {
  color: ${this.colors.textWhite};
  font-size: 12px;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.minecraft-text-small {
  color: ${this.colors.textGray};
  font-size: 10px;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.minecraft-label {
  color: ${this.colors.textGray};
  font-size: 10px;
  font-weight: bold;
  text-transform: uppercase;
}

.minecraft-subtitle {
  color: ${this.colors.textDark};
  font-size: 10px;
  font-weight: bold;
  text-transform: uppercase;
  margin-bottom: 6px;
}

/* Button variants for player actions */
.minecraft-button-primary {
  background: linear-gradient(to bottom, ${this.colors.experienceGreen} 0%, ${this.colors.experienceDark} 100%);
  border: 1px solid ${this.colors.panelBorder};
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.minecraft-button-primary:hover {
  background: linear-gradient(to bottom, ${this.colors.buttonHover} 0%, ${this.colors.experienceGreen} 100%);
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.3),
    inset -2px -2px 0px rgba(0,0,0,0.3);
}

.minecraft-button-warning {
  background: linear-gradient(to bottom, ${this.colors.hungerOrange} 0%, ${this.colors.hungerDark} 100%);
  border: 1px solid ${this.colors.panelBorder};
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.minecraft-button-warning:hover {
  background: linear-gradient(to bottom, ${this.colors.buttonHover} 0%, ${this.colors.hungerOrange} 100%);
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.3),
    inset -2px -2px 0px rgba(0,0,0,0.3);
}
`;
  }
  
  /**
   * Generate Groups Visualization CSS
   */
  generateGroupsVisualizationCSS() {
    return `
/* Groups Visualization System */

/* Groups Container */
.minecraft-groups-container {
  font-family: 'Minecraft', monospace;
  color: ${this.colors.textWhite};
  background: ${this.colors.panelDark};
  border: 2px solid ${this.colors.panelBorder};
  border-radius: 0;
  margin-bottom: 20px;
  box-shadow: 
    inset 1px 1px 0px rgba(255,255,255,0.1),
    inset -1px -1px 0px rgba(0,0,0,0.2);
  image-rendering: pixelated;
}

.groups-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: linear-gradient(to bottom, ${this.colors.windowHeader} 0%, ${this.colors.panelDark} 100%);
  border-bottom: 2px solid ${this.colors.panelBorder};
}

.groups-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.groups-title h3 {
  color: ${this.colors.textWhite};
  font-size: 12px;
  font-weight: bold;
  margin: 0;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.minecraft-badge {
  background: linear-gradient(to bottom, ${this.colors.experienceGreen} 0%, ${this.colors.experienceDark} 100%);
  color: ${this.colors.textWhite};
  font-size: 8px;
  font-weight: bold;
  padding: 2px 6px;
  border: 1px solid ${this.colors.panelBorder};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  min-width: 16px;
  text-align: center;
}

.groups-controls {
  display: flex;
  gap: 4px;
}

.groups-controls button {
  font-size: 9px;
  padding: 4px 8px;
  min-height: 18px;
}

/* Groups List */
.groups-list {
  padding: 8px;
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
}

.no-groups {
  text-align: center;
  padding: 20px;
  color: ${this.colors.textGray};
  font-style: italic;
}

/* Group Item */
.group-item {
  margin-bottom: 8px;
  border-radius: 0;
  border-left: 4px solid ${this.colors.experienceGreen};
  transition: all 0.2s ease;
  overflow: hidden;
}

.group-item.collapsed .group-body {
  max-height: 0;
  padding: 0 12px;
  opacity: 0;
  transition: max-height 0.3s ease, padding 0.3s ease, opacity 0.3s ease;
}

.group-item:not(.collapsed) .group-body {
  max-height: 300px;
  padding: 12px;
  opacity: 1;
  transition: max-height 0.3s ease, padding 0.3s ease, opacity 0.3s ease;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.group-header:hover {
  background: linear-gradient(90deg, rgba(255,255,255,0.05) 0%, transparent 100%);
}

.group-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.group-icon {
  font-size: 14px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid ${this.colors.panelBorder};
  background: rgba(255,255,255,0.1);
}

.group-details {
  flex: 1;
}

.group-name {
  font-size: 11px;
  font-weight: bold;
  margin: 0 0 2px 0;
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.group-member-count {
  font-size: 9px;
  color: ${this.colors.textGray};
}

.group-actions {
  display: flex;
  gap: 4px;
}

.group-actions button {
  width: 20px;
  height: 20px;
  padding: 0;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.group-collapse-btn {
  background: none;
  border: 1px solid ${this.colors.panelBorder};
  color: ${this.colors.textGray};
}

.group-collapse-btn:hover {
  background: ${this.colors.panelMid};
  color: ${this.colors.textWhite};
}

.group-task-btn {
  background: linear-gradient(to bottom, ${this.colors.hungerOrange} 0%, ${this.colors.hungerDark} 100%);
  color: ${this.colors.textWhite};
}

.group-task-btn:hover {
  background: linear-gradient(to bottom, ${this.colors.buttonHover} 0%, ${this.colors.hungerOrange} 100%);
}

/* Group Members */
.group-members {
  margin-bottom: 12px;
}

.no-members {
  text-align: center;
  padding: 12px;
  color: ${this.colors.textGray};
  font-style: italic;
  background: rgba(0,0,0,0.1);
  border: 1px dashed ${this.colors.panelBorder};
}

.group-member {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  margin-bottom: 2px;
  background: linear-gradient(to right, ${this.colors.panelMid} 0%, rgba(0,0,0,0.1) 100%);
  border: 1px solid ${this.colors.panelBorder};
  cursor: move;
  user-select: none;
  transition: all 0.2s ease;
}

.group-member:hover {
  background: linear-gradient(to right, ${this.colors.buttonHover} 0%, ${this.colors.buttonNormal} 100%);
  color: ${this.colors.textDark};
  text-shadow: none;
}

.group-member.dragging {
  opacity: 0.5;
  transform: scale(0.95);
}

.group-member.leader {
  background: linear-gradient(to right, ${this.colors.experienceGreen} 0%, ${this.colors.experienceDark} 100%);
  border-color: ${this.colors.experienceGreen};
}

.group-member.offline {
  opacity: 0.6;
  background: linear-gradient(to right, ${this.colors.panelDark} 0%, rgba(0,0,0,0.2) 100%);
}

.member-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: 1px solid ${this.colors.panelBorder};
}

.member-status.online {
  background: ${this.colors.experienceGreen};
  box-shadow: 0 0 4px ${this.colors.experienceGreen};
}

.member-status.offline {
  background: ${this.colors.panelDark};
}

.member-name {
  flex: 1;
  font-size: 10px;
  color: ${this.colors.textWhite};
  text-shadow: 1px 1px 0px ${this.colors.textDark};
}

.leader-badge {
  font-size: 12px;
  filter: drop-shadow(1px 1px 1px rgba(0,0,0,0.5));
}

/* Group Tasks */
.group-tasks,
.group-looping-task {
  margin-bottom: 8px;
  padding: 8px;
  background: rgba(0,0,0,0.2);
  border: 1px solid ${this.colors.panelBorder};
}

.group-tasks h5,
.group-looping-task h5 {
  margin: 0 0 6px 0;
}

.group-task {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px;
  background: ${this.colors.panelMid};
  border: 1px solid ${this.colors.panelBorder};
  margin-bottom: 2px;
}

.task-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.task-type {
  font-size: 9px;
  font-weight: bold;
  color: ${this.colors.textWhite};
  text-transform: uppercase;
  min-width: 60px;
}

.minecraft-progress-bar {
  height: 6px;
  width: 80px;
  background: ${this.colors.panelDark};
  border: 1px solid ${this.colors.panelBorder};
  overflow: hidden;
  position: relative;
}

.minecraft-progress-fill {
  height: 100%;
  background: linear-gradient(to right, ${this.colors.experienceGreen} 0%, ${this.colors.experienceDark} 100%);
  transition: width 0.3s ease;
  image-rendering: pixelated;
}

.cancel-task-btn {
  width: 16px;
  height: 16px;
  background: ${this.colors.healthRed};
  color: ${this.colors.textWhite};
  border: 1px solid ${this.colors.panelBorder};
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: bold;
}

.cancel-task-btn:hover {
  background: ${this.colors.healthDark};
}

.looping-task-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-name {
  font-size: 10px;
  font-weight: bold;
  color: ${this.colors.textWhite};
  text-transform: uppercase;
}

.loop-count {
  font-size: 9px;
  color: ${this.colors.textGray};
}

/* Groups Footer Stats */
.groups-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  background: ${this.colors.panelDark};
  border-top: 2px solid ${this.colors.panelBorder};
}

.groups-stats {
  display: flex;
  gap: 16px;
}

.groups-stats .stat {
  font-size: 9px;
  color: ${this.colors.textGray};
}

.groups-stats .stat span {
  color: ${this.colors.textWhite};
  font-weight: bold;
}

/* Drag and Drop */
.drop-target {
  background: linear-gradient(to right, rgba(140, 195, 60, 0.2) 0%, rgba(140, 195, 60, 0.1) 100%) !important;
  border-color: ${this.colors.experienceGreen} !important;
  box-shadow: inset 0 0 0 1px ${this.colors.experienceGreen};
}

/* Modals */
.minecraft-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.minecraft-modal-overlay.hidden {
  display: none;
}

.minecraft-modal {
  background: linear-gradient(to bottom, ${this.colors.windowFrame} 0%, ${this.colors.panelLight} 100%);
  border: 3px solid ${this.colors.windowBorder};
  min-width: 320px;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 
    inset 2px 2px 0px rgba(255,255,255,0.3),
    inset -2px -2px 0px rgba(0,0,0,0.3),
    0 8px 16px rgba(0,0,0,0.5);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: linear-gradient(to bottom, ${this.colors.windowHeader} 0%, ${this.colors.panelDark} 100%);
  border-bottom: 2px solid ${this.colors.panelBorder};
}

.modal-header h3 {
  color: ${this.colors.textDark};
  font-size: 12px;
  font-weight: bold;
  margin: 0;
  text-shadow: none;
}

.modal-content {
  padding: 16px;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  color: ${this.colors.textDark};
  font-size: 10px;
  font-weight: bold;
  text-transform: uppercase;
}

.minecraft-input,
.minecraft-textarea,
.minecraft-dropdown {
  width: 100%;
  padding: 6px 8px;
  background: ${this.colors.panelDark};
  border: 2px inset ${this.colors.panelMid};
  color: ${this.colors.textWhite};
  font-family: 'Minecraft', monospace;
  font-size: 10px;
  outline: none;
  image-rendering: pixelated;
  box-sizing: border-box;
}

.minecraft-textarea {
  min-height: 60px;
  resize: vertical;
  font-family: 'Courier New', monospace;
}

.minecraft-input:focus,
.minecraft-textarea:focus,
.minecraft-dropdown:focus {
  border-color: ${this.colors.buttonHover};
  box-shadow: inset 0 0 0 1px ${this.colors.buttonHover};
}

/* Color and Icon Pickers */
.color-picker,
.icon-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 8px;
  background: ${this.colors.panelDark};
  border: 1px solid ${this.colors.panelBorder};
}

.color-option {
  width: 24px;
  height: 24px;
  border: 2px solid ${this.colors.panelBorder};
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-option:hover {
  border-color: ${this.colors.buttonHover};
  transform: scale(1.1);
}

.color-option.selected {
  border-color: ${this.colors.textWhite};
  box-shadow: 
    inset 0 0 0 2px ${this.colors.panelBorder},
    0 0 0 2px ${this.colors.textWhite};
}

.icon-option {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: ${this.colors.panelMid};
  border: 2px solid ${this.colors.panelBorder};
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
}

.icon-option:hover {
  background: ${this.colors.buttonHover};
  transform: scale(1.1);
}

.icon-option.selected {
  background: ${this.colors.buttonHover};
  border-color: ${this.colors.textWhite};
  box-shadow: 0 0 0 2px ${this.colors.textWhite};
}

/* Checkboxes */
.minecraft-checkbox {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
  cursor: pointer;
  color: ${this.colors.textDark};
  font-size: 10px;
}

.minecraft-checkbox input[type="checkbox"] {
  width: 12px;
  height: 12px;
  background: ${this.colors.panelDark};
  border: 1px solid ${this.colors.panelBorder};
  cursor: pointer;
}

.minecraft-checkbox input[type="checkbox"]:checked {
  background: ${this.colors.experienceGreen};
  border-color: ${this.colors.experienceDark};
}

.settings-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.modal-footer {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: ${this.colors.panelDark};
  border-top: 2px solid ${this.colors.panelBorder};
}

.modal-footer button {
  flex: 1;
  font-size: 10px;
  padding: 8px 12px;
}

/* Context Menu */
.context-menu {
  position: fixed;
  background: linear-gradient(to bottom, ${this.colors.windowFrame} 0%, ${this.colors.panelLight} 100%);
  border: 2px solid ${this.colors.windowBorder};
  z-index: 2500;
  min-width: 120px;
  box-shadow: 
    inset 1px 1px 0px rgba(255,255,255,0.3),
    inset -1px -1px 0px rgba(0,0,0,0.3),
    0 4px 8px rgba(0,0,0,0.4);
}

.context-menu.hidden {
  display: none;
}

.menu-item {
  padding: 6px 12px;
  cursor: pointer;
  color: ${this.colors.textDark};
  font-size: 10px;
  border-bottom: 1px solid rgba(0,0,0,0.1);
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: ${this.colors.buttonHover};
  color: ${this.colors.textDark};
}

.menu-item.danger {
  color: ${this.colors.healthRed};
}

.menu-item.danger:hover {
  background: ${this.colors.healthRed};
  color: ${this.colors.textWhite};
}

.menu-separator {
  height: 1px;
  background: ${this.colors.panelBorder};
  margin: 2px 0;
}

/* Utility Classes */
.hidden {
  display: none !important;
}

.minecraft-title {
  color: ${this.colors.textWhite};
  font-size: 12px;
  font-weight: bold;
  text-shadow: 1px 1px 0px ${this.colors.textDark};
  margin: 0;
}
`;
  }
  
  /**
   * Generate Minecraft UI icons as data URLs
   */
  generateIcons() {
    return {
      settings: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M8 4C6 4 4 6 4 8s2 4 4 4 4-2 4-4-2-4-4-4zm0 6c-1 0-2-1-2-2s1-2 2-2 2 1 2 2-1 2-2 2z' fill='%23373737'/%3E%3C/svg%3E",
      waypoints: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M8 2l2 6-2 6-2-6z' fill='%23373737'/%3E%3C/svg%3E",
      players: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M8 3C7 3 6 4 6 5s1 2 2 2 2-1 2-2-1-2-2-2zM4 13v-1c0-2 2-3 4-3s4 1 4 3v1z' fill='%23373737'/%3E%3C/svg%3E",
      radar: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Ccircle cx='8' cy='8' r='6' fill='none' stroke='%23373737' stroke-width='1'/%3E%3Cpath d='M8 2v12M2 8h12' stroke='%23373737' stroke-width='1'/%3E%3C/svg%3E",
      claims: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M2 2h12v12H2z' fill='none' stroke='%23373737' stroke-width='1'/%3E%3Cpath d='M4 4h8v8H4z' fill='none' stroke='%23373737' stroke-width='1'/%3E%3C/svg%3E",
      export: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M8 2l-3 3h2v4h2V5h2z' fill='%23373737'/%3E%3Cpath d='M3 12h10v2H3z' fill='%23373737'/%3E%3C/svg%3E",
      controls: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M2 4h4v1H2zM8 4h6v1H8zM2 7h6v1H2zM10 7h4v1h-4zM2 10h3v1H2zM7 10h7v1H7z' fill='%23373737'/%3E%3C/svg%3E",
      zoomIn: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M7 3v8M3 7h8' stroke='%23373737' stroke-width='2' stroke-linecap='round'/%3E%3C/svg%3E",
      zoomOut: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M3 7h8' stroke='%23373737' stroke-width='2' stroke-linecap='round'/%3E%3C/svg%3E",
      caveMode: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M2 8c2-3 4-3 6-3s4 0 6 3c-2 3-4 3-6 3s-4 0-6-3z' fill='%23373737'/%3E%3C/svg%3E",
      dimension: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 16 16'%3E%3Cpath d='M8 2L4 6v4l4 4 4-4V6z' fill='none' stroke='%23373737' stroke-width='1'/%3E%3Cpath d='M6 6h4v4H6z' fill='%23373737'/%3E%3C/svg%3E"
    };
  }
  
  /**
   * Get Minecraft UI colors
   */
  getColors() {
    return this.colors;
  }
  
  /**
   * Get specific UI template
   */
  getTemplate(name) {
    this.initializeTemplates();
    return this.templates[name] || '';
  }
}

module.exports = MinecraftUIGenerator;