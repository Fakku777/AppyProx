/**
 * HeadlessMinecraftClient - Core headless Minecraft client implementation
 * Handles connection lifecycle, authentication, and basic bot functionality
 */

const EventEmitter = require('events');
const mineflayer = require('mineflayer');
const { v4: uuidv4 } = require('uuid');
const ChatSystem = require('../chat/ChatSystem');
const CommandExecutor = require('../chat/CommandExecutor');

class HeadlessMinecraftClient extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.id = options.id || uuidv4();
    this.username = options.username || `Bot_${this.id.slice(0, 8)}`;
    this.server = options.server || { host: 'localhost', port: 25565 };
    this.version = options.version || '1.20.1';
    
    // Client state
    this.bot = null;
    this.isConnected = false;
    this.isReady = false;
    this.connectionAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 5000;
    
    // Authentication
    this.auth = options.auth || 'offline';
    this.credentials = options.credentials || {};
    this.authManager = options.authManager || null;
    this.accountIdentifier = options.accountIdentifier || null;
    this.authType = options.authType || null;
    
    // Pathfinding integration
    this.pathfinder = null;
    this.pathfindingEnabled = options.pathfindingEnabled !== false;
    
    // Chat system integration
    this.chatManager = null;
    this.chatEnabled = options.chatEnabled !== false;
    
    // Configuration
    this.config = {
      autoReconnect: options.autoReconnect !== false,
      hideErrors: options.hideErrors || false,
      chatTtl: options.chatTtl || 300000, // 5 minutes
      viewDistance: options.viewDistance || 'far',
      checkTimeoutInterval: options.checkTimeoutInterval || 30000,
      ...options.config
    };
    
    // State tracking
    this.position = { x: 0, y: 0, z: 0 };
    this.health = 20;
    this.food = 20;
    this.experience = { level: 0, points: 0, progress: 0 };
    this.gamemode = 'survival';
    this.dimension = 'overworld';
    
    // Chat and command history
    this.chatHistory = [];
    this.commandQueue = [];
    this.isExecutingCommand = false;
    
    // Movement and pathfinding
    this.currentGoal = null;
    this.isMoving = false;
    this.movementLocked = false;
    
    // Task management
    this.currentTask = null;
    this.taskQueue = [];
    
    this.logger = options.logger || console;
    
    // Initialize chat components
    this.initializeChatSystem();
  }
  
  /**
   * Initialize chat system components
   */
  initializeChatSystem() {
    if (!this.chatEnabled) {
      return;
    }
    
    // Initialize chat system
    this.chatSystem = new ChatSystem({
      logger: this.logger
    });
    
    // Initialize command executor
    this.commandExecutor = new CommandExecutor({
      logger: this.logger,
      getBotById: () => this.bot,
      getAllBots: () => this.bot ? [this.bot] : []
    });
    
    // Forward chat events from chat system
    this.chatSystem.on('messageFiltered', (data) => {
      this.emit('messageFiltered', data);
    });
    
    this.chatSystem.on('rateLimitTriggered', (data) => {
      this.emit('rateLimitTriggered', data);
    });
    
    this.chatSystem.on('keywordMatch', (data) => {
      this.emit('keywordMatch', data);
    });
    
    this.chatSystem.on('commandDetected', (data) => {
      this.emit('commandDetected', data);
    });
    
    this.chatSystem.on('mentionDetected', (data) => {
      this.emit('mentionDetected', data);
    });
    
    // Forward command events
    this.commandExecutor.on('commandExecuted', (data) => {
      this.emit('commandExecuted', data);
    });
    
    this.commandExecutor.on('responseReceived', (data) => {
      this.emit('commandResponseReceived', data);
    });
    
    this.commandExecutor.on('commandTimeout', (data) => {
      this.emit('commandTimeout', data);
    });
    
    this.commandExecutor.on('commandError', (data) => {
      this.emit('commandError', data);
    });
    
    this.commandExecutor.on('commandCancelled', (data) => {
      this.emit('commandCancelled', data);
    });
    
    this.logger.debug(`[${this.username}] Chat system initialized`);
  }
  
  /**
   * Update authentication credentials from AuthManager
   */
  async updateAuthentication() {
    if (!this.authManager || !this.accountIdentifier) {
      return;
    }
    
    try {
      const authData = await this.authManager.getAuthData(this.accountIdentifier, {
        authType: this.authType,
        validate: true
      });
      
      this.credentials = {
        accessToken: authData.accessToken,
        clientToken: authData.clientToken
      };
      
      // Update auth type based on what was actually used
      this.authType = authData.authType;
      
      // Update username if available from profile
      if (authData.profile && authData.profile.name) {
        this.username = authData.profile.name;
      }
      
      this.logger.debug(`[${this.username}] Updated authentication (${this.authType})`);
      
    } catch (error) {
      this.logger.error(`[${this.username}] Failed to update authentication:`, error.message);
      throw error;
    }
  }
  
  /**
   * Connect to the Minecraft server
   */
  async connect() {
    if (this.isConnected) {
      this.logger.warn(`[${this.username}] Already connected`);
      return;
    }
    
    try {
      // Update authentication credentials if using AuthManager
      if (this.authManager) {
        await this.updateAuthentication();
      }
      
      this.logger.info(`[${this.username}] Connecting to ${this.server.host}:${this.server.port}...`);
      
      const botOptions = {
        host: this.server.host,
        port: this.server.port,
        username: this.username,
        version: this.version,
        auth: this.auth,
        hideErrors: this.config.hideErrors,
        viewDistance: this.config.viewDistance,
        checkTimeoutInterval: this.config.checkTimeoutInterval
      };
      
      // Add authentication if provided
      if (this.auth !== 'offline' && this.credentials.accessToken) {
        botOptions.accessToken = this.credentials.accessToken;
        botOptions.clientToken = this.credentials.clientToken;
        
        // Set auth type for mineflayer
        if (this.authType === 'altening') {
          botOptions.auth = 'microsoft'; // Altening tokens work with microsoft auth
        } else {
          botOptions.auth = this.auth;
        }
      }
      
      this.bot = mineflayer.createBot(botOptions);
      
      this.setupEventHandlers();
      
      return new Promise((resolve, reject) => {
        const timeout = setTimeout(() => {
          reject(new Error('Connection timeout'));
        }, 30000);
        
        this.bot.once('login', () => {
          clearTimeout(timeout);
          this.isConnected = true;
          this.connectionAttempts = 0;
          this.logger.info(`[${this.username}] Connected successfully`);
          this.emit('connected');
          resolve();
        });
        
        this.bot.once('error', (error) => {
          clearTimeout(timeout);
          this.logger.error(`[${this.username}] Connection error:`, error.message);
          reject(error);
        });
      });
      
    } catch (error) {
      this.logger.error(`[${this.username}] Failed to connect:`, error.message);
      this.emit('error', error);
      throw error;
    }
  }
  
  /**
   * Disconnect from the server
   */
  async disconnect(reason = 'Manual disconnect') {
    if (!this.isConnected || !this.bot) {
      return;
    }
    
    this.logger.info(`[${this.username}] Disconnecting: ${reason}`);
    
    try {
      this.isConnected = false;
      this.isReady = false;
      this.bot.quit(reason);
      this.bot = null;
      this.emit('disconnected', reason);
    } catch (error) {
      this.logger.error(`[${this.username}] Error during disconnect:`, error.message);
    }
  }
  
  /**
   * Setup event handlers for the mineflayer bot
   */
  setupEventHandlers() {
    if (!this.bot) return;
    
    // Connection events
    this.bot.on('spawn', () => {
      this.isReady = true;
      this.updatePosition();
      this.logger.info(`[${this.username}] Spawned in game`);
      this.emit('ready');
    });
    
    this.bot.on('error', (error) => {
      this.logger.error(`[${this.username}] Bot error:`, error.message);
      this.emit('error', error);
    });
    
    this.bot.on('end', (reason) => {
      this.isConnected = false;
      this.isReady = false;
      this.logger.info(`[${this.username}] Connection ended:`, reason);
      this.emit('disconnected', reason);
      
      if (this.config.autoReconnect && this.connectionAttempts < this.maxReconnectAttempts) {
        this.scheduleReconnect();
      }
    });
    
    this.bot.on('kicked', (reason) => {
      this.logger.warn(`[${this.username}] Kicked from server:`, reason);
      this.emit('kicked', reason);
    });
    
    // Game state events
    this.bot.on('health', () => {
      this.health = this.bot.health;
      this.food = this.bot.food;
      this.emit('healthUpdate', { health: this.health, food: this.food });
    });
    
    this.bot.on('experience', () => {
      this.experience = {
        level: this.bot.experience.level,
        points: this.bot.experience.points,
        progress: this.bot.experience.progress
      };
      this.emit('experienceUpdate', this.experience);
    });
    
    this.bot.on('move', () => {
      this.updatePosition();
    });
    
    this.bot.on('forcedMove', () => {
      this.updatePosition();
    });
    
    this.bot.on('chat', (username, message) => {
      this.handleChatMessage(username, message);
    });
    
    this.bot.on('whisper', (username, message) => {
      this.handleWhisperMessage(username, message);
    });
    
    // World events
    this.bot.on('time', () => {
      this.emit('timeUpdate', this.bot.time);
    });
    
    this.bot.on('weather', () => {
      this.emit('weatherUpdate', {
        isRaining: this.bot.isRaining,
        rainState: this.bot.rainState,
        thunderState: this.bot.thunderState
      });
    });
  }
  
  /**
   * Schedule a reconnection attempt
   */
  scheduleReconnect() {
    this.connectionAttempts++;
    const delay = this.reconnectDelay * this.connectionAttempts;
    
    this.logger.info(`[${this.username}] Scheduling reconnect attempt ${this.connectionAttempts}/${this.maxReconnectAttempts} in ${delay}ms`);
    
    setTimeout(async () => {
      try {
        await this.connect();
      } catch (error) {
        this.logger.error(`[${this.username}] Reconnect attempt ${this.connectionAttempts} failed:`, error.message);
      }
    }, delay);
  }
  
  /**
   * Update position tracking
   */
  updatePosition() {
    if (this.bot && this.bot.entity) {
      this.position = {
        x: this.bot.entity.position.x,
        y: this.bot.entity.position.y,
        z: this.bot.entity.position.z
      };
      this.emit('positionUpdate', this.position);
    }
  }
  
  /**
   * Handle chat messages
   */
  handleChatMessage(username, message) {
    const chatEntry = {
      timestamp: Date.now(),
      type: 'chat',
      username,
      message,
      id: uuidv4()
    };
    
    this.chatHistory.push(chatEntry);
    this.cleanupChatHistory();
    
    // Process through chat system if enabled
    if (this.chatSystem) {
      this.chatSystem.processMessage(chatEntry);
    }
    
    // Forward to command executor for response tracking
    if (this.commandExecutor) {
      this.commandExecutor.processMessage(chatEntry);
    }
    
    this.emit('chat', chatEntry);
  }
  
  /**
   * Handle whisper messages
   */
  handleWhisperMessage(username, message) {
    const whisperEntry = {
      timestamp: Date.now(),
      type: 'whisper',
      username,
      message,
      id: uuidv4()
    };
    
    this.chatHistory.push(whisperEntry);
    this.cleanupChatHistory();
    
    // Process through chat system if enabled
    if (this.chatSystem) {
      this.chatSystem.processMessage(whisperEntry);
    }
    
    // Forward to command executor for response tracking
    if (this.commandExecutor) {
      this.commandExecutor.processMessage(whisperEntry);
    }
    
    this.emit('whisper', whisperEntry);
  }
  
  /**
   * Clean up old chat messages
   */
  cleanupChatHistory() {
    const cutoff = Date.now() - this.config.chatTtl;
    this.chatHistory = this.chatHistory.filter(entry => entry.timestamp > cutoff);
  }
  
  /**
   * Send a chat message
   */
  async sendChat(message) {
    if (!this.isReady || !this.bot) {
      throw new Error('Bot not ready to send chat');
    }
    
    try {
      this.bot.chat(message);
      this.logger.debug(`[${this.username}] Sent chat: ${message}`);
      this.emit('chatSent', message);
    } catch (error) {
      this.logger.error(`[${this.username}] Failed to send chat:`, error.message);
      throw error;
    }
  }
  
  /**
   * Send a whisper message
   */
  async sendWhisper(target, message) {
    return this.sendChat(`/msg ${target} ${message}`);
  }
  
  /**
   * Execute a command with enhanced tracking
   */
  async executeCommand(command, options = {}) {
    if (!this.isReady || !this.bot) {
      throw new Error('Bot not ready to execute commands');
    }
    
    // Use command executor if available for enhanced tracking
    if (this.commandExecutor) {
      return this.commandExecutor.executeCommand(command, {
        botId: this.id,
        timeout: options.timeout || 5000,
        priority: options.priority || 'normal',
        expectedResponse: options.expectedResponse,
        retryCount: options.retryCount || 0,
        ...options
      });
    }
    
    // Fallback to basic command execution
    const timeout = options.timeout || 5000;
    return new Promise((resolve, reject) => {
      const timeoutId = setTimeout(() => {
        reject(new Error(`Command timeout: ${command}`));
      }, timeout);
      
      try {
        this.bot.chat(command);
        this.logger.debug(`[${this.username}] Executed command: ${command}`);
        
        // Clear timeout on successful execution
        clearTimeout(timeoutId);
        resolve();
      } catch (error) {
        clearTimeout(timeoutId);
        this.logger.error(`[${this.username}] Failed to execute command:`, error.message);
        reject(error);
      }
    });
  }
  
  /**
   * Add chat filter
   */
  addChatFilter(filter) {
    if (this.chatSystem) {
      return this.chatSystem.addFilter(filter);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Remove chat filter
   */
  removeChatFilter(filterId) {
    if (this.chatSystem) {
      return this.chatSystem.removeFilter(filterId);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Block a user from chat processing
   */
  blockUser(username) {
    if (this.chatSystem) {
      return this.chatSystem.blockUser(username);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Unblock a user
   */
  unblockUser(username) {
    if (this.chatSystem) {
      return this.chatSystem.unblockUser(username);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Allow a user (add to allowlist)
   */
  allowUser(username) {
    if (this.chatSystem) {
      return this.chatSystem.allowUser(username);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Remove user from allowlist
   */
  disallowUser(username) {
    if (this.chatSystem) {
      return this.chatSystem.disallowUser(username);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Add keyword to monitor
   */
  addKeyword(keyword, options = {}) {
    if (this.chatSystem) {
      return this.chatSystem.addKeyword(keyword, options);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Remove keyword
   */
  removeKeyword(keywordId) {
    if (this.chatSystem) {
      return this.chatSystem.removeKeyword(keywordId);
    }
    throw new Error('Chat system not initialized');
  }
  
  /**
   * Get chat statistics
   */
  getChatStats() {
    if (this.chatSystem) {
      return this.chatSystem.getStats();
    }
    return null;
  }
  
  /**
   * Get command execution statistics
   */
  getCommandStats() {
    if (this.commandExecutor) {
      return this.commandExecutor.getStats();
    }
    return null;
  }
  
  /**
   * Get command history
   */
  getCommandHistory(limit = 50) {
    if (this.commandExecutor) {
      return this.commandExecutor.getCommandHistory(limit);
    }
    return [];
  }
  
  /**
   * Cancel a pending command
   */
  cancelCommand(commandId) {
    if (this.commandExecutor) {
      return this.commandExecutor.cancelCommand(commandId);
    }
    return false;
  }
  
  /**
   * Get pending commands in queue
   */
  getPendingCommands() {
    if (this.commandExecutor) {
      return this.commandExecutor.getPendingCommands();
    }
    return [];
  }
  
  /**
   * Clear command queue
   */
  clearCommandQueue() {
    if (this.commandExecutor) {
      return this.commandExecutor.clearQueue();
    }
  }
  
  /**
   * Get current bot status
   */
  getStatus() {
    const status = {
      id: this.id,
      username: this.username,
      isConnected: this.isConnected,
      isReady: this.isReady,
      server: this.server,
      position: this.position,
      health: this.health,
      food: this.food,
      experience: this.experience,
      gamemode: this.gamemode,
      dimension: this.dimension,
      connectionAttempts: this.connectionAttempts,
      chatHistorySize: this.chatHistory.length,
      currentTask: this.currentTask?.id || null,
      taskQueueSize: this.taskQueue.length,
      chatEnabled: this.chatEnabled,
      pathfindingEnabled: this.pathfindingEnabled
    };
    
    // Add chat system status if available
    if (this.chatSystem) {
      status.chatStats = this.chatSystem.getStats();
    }
    
    // Add command executor status if available
    if (this.commandExecutor) {
      status.commandStats = this.commandExecutor.getStats();
      status.pendingCommands = this.commandExecutor.getPendingCommands().length;
    }
    
    // Add pathfinding status if available
    if (this.pathfinder) {
      status.isMoving = this.pathfinder.isMoving();
      status.currentGoal = this.pathfinder.getCurrentGoal();
      status.pathfindingStats = this.pathfinder.getStats();
    }
    
    return status;
  }
  
  /**
   * Get chat history
   */
  getChatHistory(limit = 50) {
    return this.chatHistory.slice(-limit);
  }
  
  /**
   * Set pathfinder instance
   */
  setPathfinder(pathfinder) {
    this.pathfinder = pathfinder;
    this.logger.debug(`[${this.username}] Pathfinder instance set`);
  }
  
  /**
   * Move to a specific position using pathfinding
   */
  async moveTo(x, y, z, options = {}) {
    if (!this.pathfinder) {
      throw new Error('Pathfinder not available');
    }
    
    if (!this.isReady || !this.bot) {
      throw new Error('Bot not ready for movement');
    }
    
    try {
      const result = await this.pathfinder.moveTo(x, y, z, options);
      this.logger.info(`[${this.username}] Movement completed to (${x}, ${y}, ${z})`);
      this.emit('movementCompleted', { destination: { x, y, z }, result });
      return result;
    } catch (error) {
      this.logger.error(`[${this.username}] Movement failed to (${x}, ${y}, ${z}):`, error.message);
      this.emit('movementFailed', { destination: { x, y, z }, error });
      throw error;
    }
  }
  
  /**
   * Move near a position within a specified range
   */
  async moveNear(x, y, z, range = 1, options = {}) {
    if (!this.pathfinder) {
      throw new Error('Pathfinder not available');
    }
    
    if (!this.isReady || !this.bot) {
      throw new Error('Bot not ready for movement');
    }
    
    try {
      const result = await this.pathfinder.moveNear(x, y, z, range, options);
      this.logger.info(`[${this.username}] Movement completed near (${x}, ${y}, ${z}) within ${range} blocks`);
      this.emit('movementCompleted', { destination: { x, y, z, range }, result });
      return result;
    } catch (error) {
      this.logger.error(`[${this.username}] Movement failed near (${x}, ${y}, ${z}):`, error.message);
      this.emit('movementFailed', { destination: { x, y, z, range }, error });
      throw error;
    }
  }
  
  /**
   * Follow an entity
   */
  async followEntity(entity, range = 3, options = {}) {
    if (!this.pathfinder) {
      throw new Error('Pathfinder not available');
    }
    
    if (!this.isReady || !this.bot) {
      throw new Error('Bot not ready for movement');
    }
    
    try {
      const result = await this.pathfinder.followEntity(entity, range, options);
      this.logger.info(`[${this.username}] Following entity ${entity.username || entity.name}`);
      this.emit('followingStarted', { entity, range, result });
      return result;
    } catch (error) {
      this.logger.error(`[${this.username}] Failed to follow entity:`, error.message);
      this.emit('followingFailed', { entity, error });
      throw error;
    }
  }
  
  /**
   * Stop current pathfinding
   */
  stopMovement() {
    if (this.pathfinder) {
      this.pathfinder.stop();
      this.logger.info(`[${this.username}] Movement stopped`);
      this.emit('movementStopped');
    }
  }
  
  /**
   * Check if bot is currently moving
   */
  isMoving() {
    return this.pathfinder ? this.pathfinder.isMoving() : false;
  }
  
  /**
   * Get current pathfinding goal
   */
  getCurrentGoal() {
    return this.pathfinder ? this.pathfinder.getCurrentGoal() : null;
  }
  
  /**
   * Get pathfinding statistics
   */
  getPathfindingStats() {
    return this.pathfinder ? this.pathfinder.getStats() : null;
  }
  
  /**
   * Clean up resources
   */
  destroy() {
    this.logger.info(`[${this.username}] Destroying client`);
    
    // Stop any active pathfinding
    if (this.pathfinder) {
      this.pathfinder.stop();
      this.pathfinder = null;
    }
    
    // Clean up chat system components
    if (this.commandExecutor) {
      this.commandExecutor.clearQueue();
      this.commandExecutor.removeAllListeners();
      this.commandExecutor = null;
    }
    
    if (this.chatSystem) {
      this.chatSystem.removeAllListeners();
      this.chatSystem = null;
    }
    
    if (this.bot) {
      this.bot.removeAllListeners();
    }
    
    this.disconnect('Client destroyed');
    this.removeAllListeners();
    this.chatHistory = [];
    this.commandQueue = [];
    this.taskQueue = [];
  }
}

module.exports = HeadlessMinecraftClient;