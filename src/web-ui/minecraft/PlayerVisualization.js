/**
 * Minecraft Player Visualization System
 * Handles authentic Minecraft-style player rendering, health bars, inventory, and interactions
 */

class PlayerVisualization {
  constructor(textureManager, logger) {
    this.textureManager = textureManager;
    this.logger = logger;
    
    // Player skin cache
    this.playerSkins = new Map();
    
    // Minecraft health/hunger constants
    this.MAX_HEALTH = 20;
    this.MAX_HUNGER = 20;
    this.MAX_EXPERIENCE = 100;
    
    // UI element dimensions (based on Minecraft's actual UI)
    this.HEALTH_HEART_SIZE = 9;
    this.HUNGER_DRUMSTICK_SIZE = 9;
    this.HOTBAR_SLOT_SIZE = 20;
    this.INVENTORY_SLOT_SIZE = 18;
    
    // Player avatar dimensions
    this.PLAYER_HEAD_SIZE = 32;
    this.PLAYER_BODY_SIZE = 64;
    
    // Initialize UI elements (only if DOM is available)
    this.isDOMAvailable = typeof document !== 'undefined';
    if (this.isDOMAvailable) {
      this.initializePlayerElements();
    }
  }
  
  /**
   * Initialize player UI elements and event listeners
   */
  initializePlayerElements() {
    // Create player info overlay container
    this.createPlayerOverlay();
    
    // Create hotbar element
    this.createHotbar();
    
    // Create inventory modal
    this.createInventoryModal();
    
    // Setup event listeners
    this.setupEventListeners();
  }
  
  /**
   * Create player information overlay
   */
  createPlayerOverlay() {
    // Remove existing overlay if present
    const existing = document.getElementById('player-info-overlay');
    if (existing) existing.remove();
    
    const overlay = document.createElement('div');
    overlay.id = 'player-info-overlay';
    overlay.className = 'player-info-overlay hidden';
    
    overlay.innerHTML = `
      <div class="player-info-header minecraft-panel">
        <div class="player-avatar-container">
          <canvas id="player-avatar-canvas" width="32" height="32"></canvas>
        </div>
        <div class="player-basic-info">
          <h3 id="player-name" class="minecraft-text">Player</h3>
          <p id="player-coordinates" class="minecraft-text-small">X: 0, Y: 64, Z: 0</p>
          <p id="player-dimension" class="minecraft-text-small">Overworld</p>
        </div>
      </div>
      
      <div class="player-stats minecraft-window">
        <!-- Health Bar -->
        <div class="stat-row">
          <label class="minecraft-label">Health:</label>
          <div class="health-bar" id="player-health-bar">
            ${this.generateHealthHearts()}
          </div>
          <span id="player-health-text" class="minecraft-text-small">20/20</span>
        </div>
        
        <!-- Hunger Bar -->
        <div class="stat-row">
          <label class="minecraft-label">Hunger:</label>
          <div class="hunger-bar" id="player-hunger-bar">
            ${this.generateHungerDrumsticks()}
          </div>
          <span id="player-hunger-text" class="minecraft-text-small">20/20</span>
        </div>
        
        <!-- Experience Bar -->
        <div class="stat-row">
          <label class="minecraft-label">XP:</label>
          <div class="experience-bar minecraft-progress-bar">
            <div id="player-xp-fill" class="minecraft-progress-fill" style="width: 0%"></div>
          </div>
          <span id="player-level-text" class="minecraft-text-small">Level 0</span>
        </div>
        
        <!-- Additional Stats -->
        <div class="stat-row">
          <label class="minecraft-label">Armor:</label>
          <div class="armor-bar" id="player-armor-bar">
            ${this.generateArmorIcons()}
          </div>
        </div>
      </div>
      
      <!-- Action Buttons -->
      <div class="player-actions">
        <button id="show-inventory-btn" class="minecraft-button">View Inventory</button>
        <button id="show-hotbar-btn" class="minecraft-button">Show Hotbar</button>
        <button id="follow-player-btn" class="minecraft-button-primary">Follow Player</button>
      </div>
    `;
    
    document.body.appendChild(overlay);
  }
  
  /**
   * Create hotbar display element
   */
  createHotbar() {
    const existing = document.getElementById('player-hotbar');
    if (existing) existing.remove();
    
    const hotbar = document.createElement('div');
    hotbar.id = 'player-hotbar';
    hotbar.className = 'player-hotbar minecraft-hotbar hidden';
    
    // Create 9 hotbar slots
    let slotsHTML = '';
    for (let i = 0; i < 9; i++) {
      slotsHTML += `
        <div class="hotbar-slot minecraft-slot" data-slot="${i}">
          <canvas class="slot-item-canvas" width="16" height="16"></canvas>
          <span class="item-count"></span>
        </div>
      `;
    }
    
    hotbar.innerHTML = `
      <div class="hotbar-container">
        ${slotsHTML}
      </div>
      <div class="selected-slot-indicator" id="hotbar-selected"></div>
    `;
    
    document.body.appendChild(hotbar);
  }
  
  /**
   * Create inventory modal
   */
  createInventoryModal() {
    const existing = document.getElementById('player-inventory-modal');
    if (existing) existing.remove();
    
    const modal = document.createElement('div');
    modal.id = 'player-inventory-modal';
    modal.className = 'minecraft-modal-overlay hidden';
    
    modal.innerHTML = `
      <div class="minecraft-inventory minecraft-window">
        <div class="inventory-header">
          <h3 class="minecraft-title">Player Inventory</h3>
          <button id="close-inventory-btn" class="close-btn">&times;</button>
        </div>
        
        <div class="inventory-content">
          <!-- Armor Slots -->
          <div class="armor-section">
            <h4 class="minecraft-subtitle">Armor</h4>
            <div class="armor-slots">
              <div class="armor-slot minecraft-slot" data-type="helmet">
                <canvas width="16" height="16"></canvas>
              </div>
              <div class="armor-slot minecraft-slot" data-type="chestplate">
                <canvas width="16" height="16"></canvas>
              </div>
              <div class="armor-slot minecraft-slot" data-type="leggings">
                <canvas width="16" height="16"></canvas>
              </div>
              <div class="armor-slot minecraft-slot" data-type="boots">
                <canvas width="16" height="16"></canvas>
              </div>
            </div>
          </div>
          
          <!-- Main Inventory (27 slots) -->
          <div class="main-inventory">
            <h4 class="minecraft-subtitle">Inventory</h4>
            <div class="inventory-grid">
              ${this.generateInventorySlots(27)}
            </div>
          </div>
          
          <!-- Hotbar in inventory -->
          <div class="inventory-hotbar">
            <h4 class="minecraft-subtitle">Hotbar</h4>
            <div class="hotbar-grid">
              ${this.generateInventorySlots(9, 'hotbar-')}
            </div>
          </div>
        </div>
        
        <div class="inventory-footer">
          <button id="sort-inventory-btn" class="minecraft-button">Sort Items</button>
          <button id="export-inventory-btn" class="minecraft-button">Export</button>
        </div>
      </div>
    `;
    
    document.body.appendChild(modal);
  }
  
  /**
   * Generate health hearts HTML
   */
  generateHealthHearts() {
    let heartsHTML = '';
    for (let i = 0; i < 10; i++) { // 10 hearts = 20 health
      heartsHTML += `<div class="health-heart" data-heart="${i}"></div>`;
    }
    return heartsHTML;
  }
  
  /**
   * Generate hunger drumsticks HTML
   */
  generateHungerDrumsticks() {
    let drumstickHTML = '';
    for (let i = 0; i < 10; i++) { // 10 drumsticks = 20 hunger
      drumstickHTML += `<div class="hunger-drumstick" data-drumstick="${i}"></div>`;
    }
    return drumstickHTML;
  }
  
  /**
   * Generate armor icons HTML
   */
  generateArmorIcons() {
    let armorHTML = '';
    for (let i = 0; i < 10; i++) { // 10 armor points
      armorHTML += `<div class="armor-icon" data-armor="${i}"></div>`;
    }
    return armorHTML;
  }
  
  /**
   * Generate inventory slots HTML
   */
  generateInventorySlots(count, prefix = '') {
    let slotsHTML = '';
    for (let i = 0; i < count; i++) {
      slotsHTML += `
        <div class="inventory-slot minecraft-slot" data-slot="${prefix}${i}">
          <canvas class="slot-item-canvas" width="16" height="16"></canvas>
          <span class="item-count"></span>
        </div>
      `;
    }
    return slotsHTML;
  }
  
  /**
   * Setup event listeners for player interactions
   */
  setupEventListeners() {
    // Player info overlay events
    document.addEventListener('click', (e) => {
      if (e.target.id === 'show-inventory-btn') {
        this.showInventory();
      } else if (e.target.id === 'show-hotbar-btn') {
        this.toggleHotbar();
      } else if (e.target.id === 'follow-player-btn') {
        this.followPlayer();
      } else if (e.target.id === 'close-inventory-btn') {
        this.hideInventory();
      } else if (e.target.id === 'sort-inventory-btn') {
        this.sortInventory();
      } else if (e.target.id === 'export-inventory-btn') {
        this.exportInventory();
      }
    });
    
    // Close modals on overlay click
    document.addEventListener('click', (e) => {
      if (e.target.classList.contains('minecraft-modal-overlay')) {
        this.hideAllModals();
      }
    });
    
    // Keyboard shortcuts
    document.addEventListener('keydown', (e) => {
      switch(e.key) {
        case 'E':
        case 'e':
          if (e.ctrlKey) this.toggleInventory();
          break;
        case 'H':
        case 'h':
          if (e.ctrlKey) this.toggleHotbar();
          break;
        case 'Escape':
          this.hideAllModals();
          break;
      }
    });
  }
  
  /**
   * Update player information display
   */
  updatePlayer(playerData) {
    // Update basic info
    const nameEl = document.getElementById('player-name');
    const coordsEl = document.getElementById('player-coordinates');
    const dimensionEl = document.getElementById('player-dimension');
    
    if (nameEl) nameEl.textContent = playerData.username || 'Unknown Player';
    if (coordsEl) coordsEl.textContent = `X: ${Math.round(playerData.x || 0)}, Y: ${Math.round(playerData.y || 64)}, Z: ${Math.round(playerData.z || 0)}`;
    if (dimensionEl) dimensionEl.textContent = this.formatDimension(playerData.dimension || 'overworld');
    
    // Update health
    this.updateHealth(playerData.health || 20);
    
    // Update hunger
    this.updateHunger(playerData.hunger || 20);
    
    // Update experience
    this.updateExperience(playerData.level || 0, playerData.xp || 0);
    
    // Update inventory if available
    if (playerData.inventory) {
      this.updateInventory(playerData.inventory);
    }
    
    // Update hotbar if available
    if (playerData.hotbar) {
      this.updateHotbar(playerData.hotbar, playerData.selectedSlot || 0);
    }
    
    // Update player avatar
    this.updatePlayerAvatar(playerData);
  }
  
  /**
   * Update health display
   */
  updateHealth(health) {
    const hearts = document.querySelectorAll('.health-heart');
    const healthText = document.getElementById('player-health-text');
    
    if (healthText) healthText.textContent = `${health}/${this.MAX_HEALTH}`;
    
    hearts.forEach((heart, index) => {
      const heartValue = (index + 1) * 2; // Each heart = 2 health points
      
      if (health >= heartValue) {
        heart.className = 'health-heart full';
      } else if (health >= heartValue - 1) {
        heart.className = 'health-heart half';
      } else {
        heart.className = 'health-heart empty';
      }
    });
  }
  
  /**
   * Update hunger display
   */
  updateHunger(hunger) {
    const drumsticks = document.querySelectorAll('.hunger-drumstick');
    const hungerText = document.getElementById('player-hunger-text');
    
    if (hungerText) hungerText.textContent = `${hunger}/${this.MAX_HUNGER}`;
    
    drumsticks.forEach((drumstick, index) => {
      const drumstickValue = (index + 1) * 2; // Each drumstick = 2 hunger points
      
      if (hunger >= drumstickValue) {
        drumstick.className = 'hunger-drumstick full';
      } else if (hunger >= drumstickValue - 1) {
        drumstick.className = 'hunger-drumstick half';
      } else {
        drumstick.className = 'hunger-drumstick empty';
      }
    });
  }
  
  /**
   * Update experience display
   */
  updateExperience(level, xpPercent) {
    const xpFill = document.getElementById('player-xp-fill');
    const levelText = document.getElementById('player-level-text');
    
    if (xpFill) xpFill.style.width = `${Math.min(100, Math.max(0, xpPercent))}%`;
    if (levelText) levelText.textContent = `Level ${level}`;
  }
  
  /**
   * Update hotbar display
   */
  updateHotbar(hotbarItems, selectedSlot = 0) {
    const slots = document.querySelectorAll('.hotbar-slot');
    const selectedIndicator = document.getElementById('hotbar-selected');
    
    slots.forEach((slot, index) => {
      const item = hotbarItems[index];
      const canvas = slot.querySelector('.slot-item-canvas');
      const countSpan = slot.querySelector('.item-count');
      
      if (item && item.type !== 'air') {
        this.renderItemInSlot(canvas, item);
        if (countSpan) {
          countSpan.textContent = item.count > 1 ? item.count : '';
        }
      } else {
        this.clearSlot(canvas);
        if (countSpan) countSpan.textContent = '';
      }
      
      // Update selection
      slot.classList.toggle('selected', index === selectedSlot);
    });
    
    // Move selection indicator
    if (selectedIndicator && slots[selectedSlot]) {
      const selectedSlotEl = slots[selectedSlot];
      const rect = selectedSlotEl.getBoundingClientRect();
      selectedIndicator.style.left = `${rect.left}px`;
      selectedIndicator.style.top = `${rect.top}px`;
    }
  }
  
  /**
   * Update full inventory display
   */
  updateInventory(inventoryData) {
    // Update main inventory slots
    this.updateInventorySection('.inventory-grid .inventory-slot', inventoryData.main || []);
    
    // Update armor slots
    this.updateArmorSlots(inventoryData.armor || {});
    
    // Update hotbar in inventory view
    this.updateInventorySection('.hotbar-grid .inventory-slot', inventoryData.hotbar || []);
  }
  
  /**
   * Update inventory section
   */
  updateInventorySection(selector, items) {
    const slots = document.querySelectorAll(selector);
    
    slots.forEach((slot, index) => {
      const item = items[index];
      const canvas = slot.querySelector('.slot-item-canvas');
      const countSpan = slot.querySelector('.item-count');
      
      if (item && item.type !== 'air') {
        this.renderItemInSlot(canvas, item);
        if (countSpan) {
          countSpan.textContent = item.count > 1 ? item.count : '';
        }
      } else {
        this.clearSlot(canvas);
        if (countSpan) countSpan.textContent = '';
      }
    });
  }
  
  /**
   * Update armor slots
   */
  updateArmorSlots(armorData) {
    const armorTypes = ['helmet', 'chestplate', 'leggings', 'boots'];
    
    armorTypes.forEach(type => {
      const slot = document.querySelector(`.armor-slot[data-type="${type}"]`);
      if (slot) {
        const canvas = slot.querySelector('canvas');
        const item = armorData[type];
        
        if (item && item.type !== 'air') {
          this.renderItemInSlot(canvas, item);
        } else {
          this.clearSlot(canvas);
        }
      }
    });
    
    // Update armor points display
    this.updateArmorPoints(this.calculateArmorPoints(armorData));
  }
  
  /**
   * Update armor points display
   */
  updateArmorPoints(armorPoints) {
    const armorIcons = document.querySelectorAll('.armor-icon');
    
    armorIcons.forEach((icon, index) => {
      const iconValue = (index + 1) * 2; // Each icon = 2 armor points
      
      if (armorPoints >= iconValue) {
        icon.className = 'armor-icon full';
      } else if (armorPoints >= iconValue - 1) {
        icon.className = 'armor-icon half';
      } else {
        icon.className = 'armor-icon empty';
      }
    });
  }
  
  /**
   * Render item in slot canvas
   */
  renderItemInSlot(canvas, item) {
    if (!canvas || !item) return;
    
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Try to get item texture
    if (this.textureManager) {
      const texture = this.textureManager.getItemTexture(item.type);
      if (texture) {
        ctx.imageSmoothingEnabled = false; // Keep pixelated look
        ctx.drawImage(texture, 0, 0, 16, 16);
        return;
      }
    }
    
    // Fallback: draw colored square with item initial
    ctx.fillStyle = this.getItemColor(item.type);
    ctx.fillRect(0, 0, 16, 16);
    
    ctx.fillStyle = '#ffffff';
    ctx.font = '8px monospace';
    ctx.textAlign = 'center';
    ctx.fillText(item.type.charAt(0).toUpperCase(), 8, 12);
  }
  
  /**
   * Clear slot canvas
   */
  clearSlot(canvas) {
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
  }
  
  /**
   * Update player avatar
   */
  updatePlayerAvatar(playerData) {
    const canvas = document.getElementById('player-avatar-canvas');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    // Try to load player skin
    this.loadPlayerSkin(playerData.username).then(skinImage => {
      if (skinImage) {
        // Render player head from skin
        ctx.imageSmoothingEnabled = false;
        // Extract head from skin (assuming standard Minecraft skin format)
        ctx.drawImage(skinImage, 8, 8, 8, 8, 0, 0, 32, 32); // Face
        ctx.drawImage(skinImage, 40, 8, 8, 8, 0, 0, 32, 32); // Hat overlay
      } else {
        // Fallback: draw Steve head
        this.drawDefaultPlayerHead(ctx);
      }
    });
  }
  
  /**
   * Load player skin
   */
  async loadPlayerSkin(username) {
    if (this.playerSkins.has(username)) {
      return this.playerSkins.get(username);
    }
    
    try {
      // Try to load from Mojang API or local cache
      const skinUrl = `https://minotar.net/skin/${username}`;
      const img = new Image();
      img.crossOrigin = 'anonymous';
      
      return new Promise((resolve) => {
        img.onload = () => {
          this.playerSkins.set(username, img);
          resolve(img);
        };
        img.onerror = () => resolve(null);
        img.src = skinUrl;
      });
    } catch (error) {
      this.logger?.warn('Failed to load player skin:', error);
      return null;
    }
  }
  
  /**
   * Draw default player head (Steve)
   */
  drawDefaultPlayerHead(ctx) {
    // Simple Steve-like head
    ctx.fillStyle = '#FDBCB4'; // Skin color
    ctx.fillRect(0, 0, 32, 32);
    
    // Eyes
    ctx.fillStyle = '#000000';
    ctx.fillRect(8, 10, 4, 4);
    ctx.fillRect(20, 10, 4, 4);
    
    // Mouth
    ctx.fillRect(14, 20, 4, 2);
  }
  
  // UI Control Methods
  showPlayerInfo(playerData) {
    if (!this.isDOMAvailable) {
      this.logger?.warn('DOM not available - cannot show player info overlay');
      return;
    }
    
    this.updatePlayer(playerData);
    const overlay = document.getElementById('player-info-overlay');
    if (overlay) {
      overlay.classList.remove('hidden');
    }
  }
  
  hidePlayerInfo() {
    const overlay = document.getElementById('player-info-overlay');
    if (overlay) {
      overlay.classList.add('hidden');
    }
  }
  
  showInventory() {
    const modal = document.getElementById('player-inventory-modal');
    if (modal) {
      modal.classList.remove('hidden');
    }
  }
  
  hideInventory() {
    const modal = document.getElementById('player-inventory-modal');
    if (modal) {
      modal.classList.add('hidden');
    }
  }
  
  toggleInventory() {
    const modal = document.getElementById('player-inventory-modal');
    if (modal) {
      modal.classList.toggle('hidden');
    }
  }
  
  toggleHotbar() {
    const hotbar = document.getElementById('player-hotbar');
    if (hotbar) {
      hotbar.classList.toggle('hidden');
    }
  }
  
  hideAllModals() {
    this.hideInventory();
    this.hidePlayerInfo();
  }
  
  followPlayer() {
    // Emit follow player event
    if (this.onFollowPlayer) {
      this.onFollowPlayer();
    }
  }
  
  sortInventory() {
    // Emit sort inventory event
    if (this.onSortInventory) {
      this.onSortInventory();
    }
  }
  
  exportInventory() {
    // Emit export inventory event
    if (this.onExportInventory) {
      this.onExportInventory();
    }
  }
  
  // Utility Methods
  formatDimension(dimension) {
    const dimensionNames = {
      'minecraft:overworld': 'Overworld',
      'minecraft:the_nether': 'The Nether',
      'minecraft:the_end': 'The End',
      'overworld': 'Overworld',
      'nether': 'The Nether',
      'end': 'The End'
    };
    return dimensionNames[dimension] || dimension;
  }
  
  getItemColor(itemType) {
    const itemColors = {
      'diamond': '#5CDBD3',
      'iron': '#D8AF93',
      'gold': '#FCEE4B',
      'stone': '#808080',
      'wood': '#8B4513',
      'dirt': '#8B4513',
      'grass': '#7CB518',
      'cobblestone': '#696969',
      'coal': '#363636'
    };
    
    // Check for partial matches
    for (const [key, color] of Object.entries(itemColors)) {
      if (itemType.includes(key)) {
        return color;
      }
    }
    
    return '#8B4513'; // Default brown
  }
  
  calculateArmorPoints(armorData) {
    const armorValues = {
      'leather': 1,
      'chainmail': 2,
      'iron': 2,
      'diamond': 3,
      'netherite': 3
    };
    
    let totalArmor = 0;
    Object.values(armorData).forEach(item => {
      if (item && item.type !== 'air') {
        for (const [material, value] of Object.entries(armorValues)) {
          if (item.type.includes(material)) {
            totalArmor += value;
            break;
          }
        }
      }
    });
    
    return Math.min(20, totalArmor); // Max 20 armor points
  }
  
  // Event handlers (to be set by parent)
  onFollowPlayer = null;
  onSortInventory = null;
  onExportInventory = null;
}

module.exports = PlayerVisualization;