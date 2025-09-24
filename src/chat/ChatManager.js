/**
 * ChatManager - Unified chat and command management system
 * Integrates ChatSystem and CommandExecutor with multi-bot management
 */

const EventEmitter = require('events');
const ChatSystem = require('./ChatSystem');
const CommandExecutor = require('./CommandExecutor');
const { v4: uuidv4 } = require('uuid');

class ChatManager extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.logger = options.logger || console;
    this.bots = new Map(); // Bot registry: botId -> bot instance
    this.botClients = new Map(); // Bot clients: botId -> HeadlessMinecraftClient
    
    // Chat system configuration
    this.chatEnabled = options.chatEnabled !== false;
    this.commandEnabled = options.commandEnabled !== false;
    this.bridgeEnabled = options.bridgeEnabled || false;
    
    // Initialize core components
    this.chatSystem = null;
    this.commandExecutor = null;
    
    if (this.chatEnabled) {
      this.initializeChatSystem(options.chatOptions || {});
    }
    
    if (this.commandEnabled) {
      this.initializeCommandExecutor(options.commandOptions || {});
    }
    
    // Auto-response configuration
    this.autoResponses = new Map();
    this.autoResponseEnabled = options.autoResponseEnabled || false;
    
    // Bridge configuration
    this.bridgeRules = [];
    
    // Emergency stop flag
    this.emergencyStop = false;
    
    this.logger.info('[ChatManager] Initialized');
  }
  
  /**
   * Initialize chat system
   */
  initializeChatSystem(options = {}) {
    this.chatSystem = new ChatSystem({
      logger: this.logger,
      ...options
    });
    
    // Forward all chat system events
    this.chatSystem.on('messageProcessed', (data) => {
      this.emit('messageProcessed', data);
    });
    
    this.chatSystem.on('messageFiltered', (data) => {
      this.emit('messageFiltered', data);
      this.handleFilteredMessage(data);
    });
    
    this.chatSystem.on('rateLimitTriggered', (data) => {
      this.emit('rateLimitTriggered', data);
      this.handleRateLimit(data);
    });
    
    this.chatSystem.on('keywordMatch', (data) => {
      this.emit('keywordMatch', data);
      this.handleKeywordMatch(data);
    });
    
    this.chatSystem.on('commandDetected', (data) => {
      this.emit('commandDetected', data);
      this.handleCommandDetection(data);
    });
    
    this.chatSystem.on('mentionDetected', (data) => {
      this.emit('mentionDetected', data);
      this.handleMentionDetection(data);
    });
    
    this.logger.info('[ChatManager] Chat system initialized');
  }
  
  /**
   * Initialize command executor
   */
  initializeCommandExecutor(options = {}) {
    this.commandExecutor = new CommandExecutor({
      logger: this.logger,
      getBotById: (botId) => this.bots.get(botId),
      getAllBots: () => Array.from(this.bots.values()),
      ...options
    });
    
    // Forward all command executor events
    this.commandExecutor.on('commandQueued', (data) => {
      this.emit('commandQueued', data);
    });
    
    this.commandExecutor.on('commandExecuted', (data) => {
      this.emit('commandExecuted', data);
    });
    
    this.commandExecutor.on('responseReceived', (data) => {
      this.emit('commandResponseReceived', data);
    });
    
    this.commandExecutor.on('commandTimeout', (data) => {
      this.emit('commandTimeout', data);
      this.handleCommandTimeout(data);
    });
    
    this.commandExecutor.on('commandError', (data) => {
      this.emit('commandError', data);
      this.handleCommandError(data);
    });
    
    this.commandExecutor.on('commandCancelled', (data) => {
      this.emit('commandCancelled', data);
    });
    
    this.commandExecutor.on('commandCompleted', (data) => {
      this.emit('commandCompleted', data);
    });
    
    this.logger.info('[ChatManager] Command executor initialized');
  }
  
  /**
   * Register a bot with the chat manager
   */
  registerBot(botId, bot, client = null) {
    if (this.bots.has(botId)) {
      this.logger.warn(`[ChatManager] Bot ${botId} already registered`);
      return;
    }
    
    this.bots.set(botId, bot);
    if (client) {
      this.botClients.set(botId, client);
    }
    
    // Set up bot event forwarding
    this.setupBotEventForwarding(botId, bot, client);
    
    this.logger.info(`[ChatManager] Registered bot: ${botId}`);
    this.emit('botRegistered', { botId, bot, client });
  }
  
  /**
   * Unregister a bot
   */
  unregisterBot(botId) {
    const bot = this.bots.get(botId);
    const client = this.botClients.get(botId);
    
    if (bot) {
      // Remove event listeners
      bot.removeAllListeners('chat');
      bot.removeAllListeners('whisper');
    }
    
    if (client) {
      // Remove client event listeners
      client.removeAllListeners('chat');
      client.removeAllListeners('whisper');
    }
    
    this.bots.delete(botId);
    this.botClients.delete(botId);
    
    this.logger.info(`[ChatManager] Unregistered bot: ${botId}`);
    this.emit('botUnregistered', { botId });
  }
  
  /**
   * Set up event forwarding for a bot
   */
  setupBotEventForwarding(botId, bot, client) {
    // Forward chat events from mineflayer bot
    if (bot) {
      bot.on('chat', (username, message) => {
        this.processChatMessage(botId, {
          username,
          message,
          type: 'chat',
          timestamp: Date.now(),
          id: uuidv4()
        });
      });
      
      bot.on('whisper', (username, message) => {
        this.processChatMessage(botId, {
          username,
          message,
          type: 'whisper',
          timestamp: Date.now(),
          id: uuidv4()
        });
      });
    }
    
    // Forward events from HeadlessMinecraftClient
    if (client) {
      client.on('chat', (chatData) => {
        this.processChatMessage(botId, chatData);
      });
      
      client.on('whisper', (whisperData) => {
        this.processChatMessage(botId, whisperData);
      });
    }
  }
  
  /**
   * Process a chat message through all systems
   */
  processChatMessage(botId, messageData) {
    if (this.emergencyStop) {
      return;
    }
    
    // Add bot information to message
    const enrichedMessage = {
      ...messageData,
      botId,
      processed: true
    };
    
    // Process through chat system
    if (this.chatSystem) {
      this.chatSystem.processMessage(enrichedMessage);
    }
    
    // Forward to command executor for response tracking
    if (this.commandExecutor) {
      this.commandExecutor.processMessage(enrichedMessage);
    }
    
    // Handle auto-responses
    if (this.autoResponseEnabled) {
      this.handleAutoResponse(botId, enrichedMessage);
    }
    
    // Handle chat bridging
    if (this.bridgeEnabled) {
      this.handleChatBridge(botId, enrichedMessage);
    }
    
    this.emit('messageProcessed', enrichedMessage);
  }
  
  /**
   * Send chat message from a specific bot
   */
  async sendChat(botId, message) {
    const client = this.botClients.get(botId);
    const bot = this.bots.get(botId);
    
    if (client && client.sendChat) {
      return await client.sendChat(message);
    } else if (bot && bot.chat) {
      bot.chat(message);
    } else {
      throw new Error(`Bot ${botId} not available for chat`);
    }
    
    this.logger.debug(`[ChatManager] Sent chat from ${botId}: ${message}`);
    this.emit('chatSent', { botId, message });
  }
  
  /**
   * Send whisper from a specific bot
   */
  async sendWhisper(botId, target, message) {
    return this.sendChat(botId, `/msg ${target} ${message}`);
  }
  
  /**
   * Execute command on a specific bot
   */
  async executeCommand(command, options = {}) {
    if (!this.commandExecutor) {
      throw new Error('Command executor not initialized');
    }
    
    return this.commandExecutor.executeCommand(command, options);
  }
  
  /**
   * Broadcast message to all bots
   */
  async broadcastMessage(message, options = {}) {
    const results = [];
    const botIds = options.botIds || Array.from(this.bots.keys());
    
    for (const botId of botIds) {
      try {
        await this.sendChat(botId, message);
        results.push({ botId, success: true });
      } catch (error) {
        results.push({ botId, success: false, error: error.message });
      }
    }
    
    this.emit('messageBroadcast', { message, results });
    return results;
  }
  
  /**
   * Handle filtered messages
   */
  handleFilteredMessage(data) {
    this.logger.debug(`[ChatManager] Message filtered: ${data.reason}`);
  }
  
  /**
   * Handle rate limiting
   */
  handleRateLimit(data) {
    this.logger.warn(`[ChatManager] Rate limit triggered for user: ${data.username}`);
  }
  
  /**
   * Handle keyword matches
   */
  handleKeywordMatch(data) {
    this.logger.info(`[ChatManager] Keyword match: ${data.keyword} by ${data.username}`);
  }
  
  /**
   * Handle command detection
   */
  handleCommandDetection(data) {
    this.logger.info(`[ChatManager] Command detected: ${data.command} by ${data.username}`);
  }
  
  /**
   * Handle mention detection
   */
  handleMentionDetection(data) {
    this.logger.info(`[ChatManager] Mention detected: ${data.mention} by ${data.username}`);
    
    // Handle auto-response to mentions
    if (this.autoResponseEnabled) {
      this.handleMentionResponse(data);
    }
  }
  
  /**
   * Handle command timeouts
   */
  handleCommandTimeout(data) {
    this.logger.warn(`[ChatManager] Command timeout: ${data.command}`);
  }
  
  /**
   * Handle command errors
   */
  handleCommandError(data) {
    this.logger.error(`[ChatManager] Command error: ${data.error}`);
  }
  
  /**
   * Handle auto-responses
   */
  handleAutoResponse(botId, messageData) {
    for (const [pattern, response] of this.autoResponses) {
      if (pattern.test(messageData.message)) {
        const responseText = typeof response === 'function' 
          ? response(messageData) 
          : response;
        
        // Send response after a short delay
        setTimeout(() => {
          this.sendChat(botId, responseText).catch(error => {
            this.logger.error(`[ChatManager] Auto-response failed: ${error.message}`);
          });
        }, 1000);
        
        break; // Only respond to first match
      }
    }
  }
  
  /**
   * Handle mention responses
   */
  handleMentionResponse(data) {
    if (data.botId && this.bots.has(data.botId)) {
      const responseText = `Hello ${data.username}! How can I help?`;
      setTimeout(() => {
        this.sendChat(data.botId, responseText).catch(error => {
          this.logger.error(`[ChatManager] Mention response failed: ${error.message}`);
        });
      }, 1000);
    }
  }
  
  /**
   * Handle chat bridging between bots
   */
  handleChatBridge(sourceBotId, messageData) {
    for (const rule of this.bridgeRules) {
      if (rule.sourceBot === sourceBotId && rule.condition(messageData)) {
        const bridgeMessage = rule.transform ? rule.transform(messageData) : messageData.message;
        
        for (const targetBotId of rule.targetBots) {
          if (targetBotId !== sourceBotId) {
            this.sendChat(targetBotId, bridgeMessage).catch(error => {
              this.logger.error(`[ChatManager] Bridge failed ${sourceBotId} -> ${targetBotId}: ${error.message}`);
            });
          }
        }
      }
    }
  }
  
  /**
   * Add auto-response pattern
   */
  addAutoResponse(pattern, response) {
    const regex = typeof pattern === 'string' ? new RegExp(pattern, 'i') : pattern;
    const id = uuidv4();
    this.autoResponses.set(regex, response);
    
    this.logger.info(`[ChatManager] Added auto-response: ${pattern}`);
    return id;
  }
  
  /**
   * Remove auto-response
   */
  removeAutoResponse(pattern) {
    for (const [regex, response] of this.autoResponses) {
      if (regex.source === pattern || regex === pattern) {
        this.autoResponses.delete(regex);
        this.logger.info(`[ChatManager] Removed auto-response: ${pattern}`);
        return true;
      }
    }
    return false;
  }
  
  /**
   * Add chat bridge rule
   */
  addBridgeRule(rule) {
    const bridgeRule = {
      id: uuidv4(),
      sourceBot: rule.sourceBot,
      targetBots: rule.targetBots || [],
      condition: rule.condition || (() => true),
      transform: rule.transform || null,
      ...rule
    };
    
    this.bridgeRules.push(bridgeRule);
    this.logger.info(`[ChatManager] Added bridge rule: ${bridgeRule.id}`);
    return bridgeRule.id;
  }
  
  /**
   * Remove chat bridge rule
   */
  removeBridgeRule(ruleId) {
    const index = this.bridgeRules.findIndex(rule => rule.id === ruleId);
    if (index !== -1) {
      this.bridgeRules.splice(index, 1);
      this.logger.info(`[ChatManager] Removed bridge rule: ${ruleId}`);
      return true;
    }
    return false;
  }
  
  /**
   * Get comprehensive statistics
   */
  getStats() {
    const stats = {
      botsRegistered: this.bots.size,
      chatEnabled: this.chatEnabled,
      commandEnabled: this.commandEnabled,
      bridgeEnabled: this.bridgeEnabled,
      autoResponseEnabled: this.autoResponseEnabled,
      autoResponses: this.autoResponses.size,
      bridgeRules: this.bridgeRules.length,
      emergencyStop: this.emergencyStop
    };
    
    if (this.chatSystem) {
      stats.chatStats = this.chatSystem.getStats();
    }
    
    if (this.commandExecutor) {
      stats.commandStats = this.commandExecutor.getStats();
    }
    
    return stats;
  }
  
  /**
   * Get all registered bot IDs
   */
  getBotIds() {
    return Array.from(this.bots.keys());
  }
  
  /**
   * Get bot instance by ID
   */
  getBot(botId) {
    return this.bots.get(botId);
  }
  
  /**
   * Get bot client by ID
   */
  getBotClient(botId) {
    return this.botClients.get(botId);
  }
  
  /**
   * Emergency stop - stops all processing
   */
  activateEmergencyStop() {
    this.emergencyStop = true;
    
    if (this.commandExecutor) {
      this.commandExecutor.clearQueue();
    }
    
    this.logger.warn('[ChatManager] Emergency stop activated');
    this.emit('emergencyStop');
  }
  
  /**
   * Deactivate emergency stop
   */
  deactivateEmergencyStop() {
    this.emergencyStop = false;
    this.logger.info('[ChatManager] Emergency stop deactivated');
    this.emit('emergencyStopDeactivated');
  }
  
  /**
   * Enable/disable features
   */
  setAutoResponseEnabled(enabled) {
    this.autoResponseEnabled = enabled;
    this.logger.info(`[ChatManager] Auto-response ${enabled ? 'enabled' : 'disabled'}`);
  }
  
  setBridgeEnabled(enabled) {
    this.bridgeEnabled = enabled;
    this.logger.info(`[ChatManager] Chat bridge ${enabled ? 'enabled' : 'disabled'}`);
  }
  
  /**
   * Delegate chat system methods
   */
  addChatFilter(filter) {
    return this.chatSystem ? this.chatSystem.addFilter(filter) : null;
  }
  
  removeChatFilter(filterId) {
    return this.chatSystem ? this.chatSystem.removeFilter(filterId) : false;
  }
  
  blockUser(username) {
    return this.chatSystem ? this.chatSystem.blockUser(username) : false;
  }
  
  unblockUser(username) {
    return this.chatSystem ? this.chatSystem.unblockUser(username) : false;
  }
  
  allowUser(username) {
    return this.chatSystem ? this.chatSystem.allowUser(username) : false;
  }
  
  disallowUser(username) {
    return this.chatSystem ? this.chatSystem.disallowUser(username) : false;
  }
  
  addKeyword(keyword, options) {
    return this.chatSystem ? this.chatSystem.addKeyword(keyword, options) : null;
  }
  
  removeKeyword(keywordId) {
    return this.chatSystem ? this.chatSystem.removeKeyword(keywordId) : false;
  }
  
  getChatHistory(options) {
    return this.chatSystem ? this.chatSystem.getChatHistory(options) : [];
  }
  
  exportChatHistory(options) {
    return this.chatSystem ? this.chatSystem.exportChatHistory(options) : null;
  }
  
  /**
   * Delegate command executor methods
   */
  cancelCommand(commandId) {
    return this.commandExecutor ? this.commandExecutor.cancelCommand(commandId) : false;
  }
  
  getPendingCommands() {
    return this.commandExecutor ? this.commandExecutor.getPendingCommands() : [];
  }
  
  getCommandHistory(limit) {
    return this.commandExecutor ? this.commandExecutor.getCommandHistory(limit) : [];
  }
  
  clearCommandQueue() {
    return this.commandExecutor ? this.commandExecutor.clearQueue() : 0;
  }
  
  /**
   * Clean shutdown
   */
  async shutdown() {
    this.logger.info('[ChatManager] Shutting down...');
    
    // Activate emergency stop
    this.activateEmergencyStop();
    
    // Clear all queues
    if (this.commandExecutor) {
      this.commandExecutor.clearQueue();
      this.commandExecutor.removeAllListeners();
    }
    
    if (this.chatSystem) {
      this.chatSystem.removeAllListeners();
    }
    
    // Unregister all bots
    for (const botId of Array.from(this.bots.keys())) {
      this.unregisterBot(botId);
    }
    
    // Clear all data
    this.autoResponses.clear();
    this.bridgeRules.length = 0;
    
    this.removeAllListeners();
    this.logger.info('[ChatManager] Shutdown complete');
  }
}

module.exports = ChatManager;