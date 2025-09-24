/**
 * CommandExecutor - Advanced command execution system
 * Provides isolated command execution, response tracking, and queue management
 */

const EventEmitter = require('events');
const { v4: uuidv4 } = require('uuid');

class CommandExecutor extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      // Command execution
      maxConcurrentCommands: options.maxConcurrentCommands || 3,
      commandTimeout: options.commandTimeout || 30000, // 30 seconds
      responseTimeout: options.responseTimeout || 5000, // 5 seconds
      
      // Queue management
      maxQueueSize: options.maxQueueSize || 50,
      enableQueuePriority: options.enableQueuePriority !== false,
      
      // Response tracking
      enableResponseTracking: options.enableResponseTracking !== false,
      responsePatterns: options.responsePatterns || [],
      maxResponseHistory: options.maxResponseHistory || 100,
      
      // Rate limiting
      enableRateLimit: options.enableRateLimit !== false,
      rateLimitWindow: options.rateLimitWindow || 60000, // 1 minute
      maxCommandsPerWindow: options.maxCommandsPerWindow || 20,
      
      // Error handling
      maxRetries: options.maxRetries || 2,
      retryDelay: options.retryDelay || 1000,
      
      ...options.config
    };
    
    this.logger = options.logger || console;
    
    // Command execution state
    this.commandQueue = [];
    this.activeCommands = new Map(); // commandId -> command data
    this.commandHistory = [];
    this.responseHistory = [];
    
    // Response tracking
    this.pendingResponses = new Map(); // commandId -> response data
    this.responsePatterns = this.compileResponsePatterns();
    
    // Rate limiting
    this.rateLimitTracker = new Map(); // botId -> command timestamps
    
    // Bot manager reference
    this.botManager = null;
    
    // Statistics
    this.stats = {
      totalCommands: 0,
      successfulCommands: 0,
      failedCommands: 0,
      timedOutCommands: 0,
      queuedCommands: 0,
      rateLimitViolations: 0,
      averageExecutionTime: 0,
      startTime: Date.now()
    };
    
    // Start processing loop
    this.startProcessingLoop();
  }
  
  /**
   * Compile response patterns for tracking
   */
  compileResponsePatterns() {
    const patterns = [];
    
    // Default patterns for common Minecraft responses
    const defaultPatterns = [
      { name: 'success', pattern: /^(Command executed|Success|Done|Completed)/i, type: 'success' },
      { name: 'error', pattern: /^(Error|Failed|Invalid|Unknown command)/i, type: 'error' },
      { name: 'permission', pattern: /^(You don't have permission|Insufficient permissions)/i, type: 'permission' },
      { name: 'syntax', pattern: /^(Incorrect usage|Usage:|Syntax error)/i, type: 'syntax' },
      { name: 'teleport', pattern: /^(Teleported|Successfully teleported)/i, type: 'action' },
      { name: 'give', pattern: /^(Gave|Given .+ to)/i, type: 'action' },
      { name: 'time', pattern: /^(Set time to|Time set)/i, type: 'action' }
    ];
    
    // Combine default and custom patterns
    const allPatterns = [...defaultPatterns, ...this.config.responsePatterns];
    
    for (const patternConfig of allPatterns) {
      try {
        patterns.push({
          name: patternConfig.name,
          regex: new RegExp(patternConfig.pattern, 'i'),
          type: patternConfig.type || 'unknown'
        });
      } catch (error) {
        this.logger.warn(`Invalid response pattern: ${patternConfig.pattern}`, error.message);
      }
    }
    
    return patterns;
  }
  
  /**
   * Set bot manager
   */
  setBotManager(botManager) {
    this.botManager = botManager;
    this.logger.debug('Bot manager set for command executor');
  }
  
  /**
   * Execute a command
   */
  async executeCommand(botId, command, options = {}) {
    const commandId = uuidv4();
    const timestamp = Date.now();
    
    const commandData = {
      id: commandId,
      botId,
      command,
      options,
      timestamp,
      status: 'queued',
      priority: options.priority || 'normal',
      retryCount: 0,
      maxRetries: options.maxRetries || this.config.maxRetries,
      timeout: options.timeout || this.config.commandTimeout,
      responseTimeout: options.responseTimeout || this.config.responseTimeout,
      expectResponse: options.expectResponse !== false,
      responsePattern: options.responsePattern || null,
      metadata: options.metadata || {}
    };
    
    try {
      // Check rate limits
      if (this.config.enableRateLimit) {
        const rateLimitResult = this.checkRateLimit(botId);
        if (!rateLimitResult.allowed) {
          this.stats.rateLimitViolations++;
          throw new Error(`Rate limit exceeded: ${rateLimitResult.count}/${this.config.maxCommandsPerWindow} commands in window`);
        }
      }
      
      // Check queue capacity
      if (this.commandQueue.length >= this.config.maxQueueSize) {
        throw new Error(`Command queue full: ${this.commandQueue.length}/${this.config.maxQueueSize}`);
      }
      
      // Add to queue
      this.addToQueue(commandData);
      
      this.stats.totalCommands++;
      this.stats.queuedCommands++;
      
      this.logger.debug(`Command queued: ${command} (${commandId}) for bot ${botId}`);
      this.emit('commandQueued', commandData);
      
      // Return promise that resolves when command completes
      return new Promise((resolve, reject) => {
        commandData.resolve = resolve;
        commandData.reject = reject;
      });
      
    } catch (error) {
      this.logger.error(`Failed to queue command: ${command}`, error.message);
      this.stats.failedCommands++;
      throw error;
    }
  }
  
  /**
   * Add command to queue with priority sorting
   */
  addToQueue(commandData) {
    this.commandQueue.push(commandData);
    
    if (this.config.enableQueuePriority) {
      this.commandQueue.sort((a, b) => {
        const priorityOrder = { 'urgent': 0, 'high': 1, 'normal': 2, 'low': 3 };
        const aPriority = priorityOrder[a.priority] || 2;
        const bPriority = priorityOrder[b.priority] || 2;
        
        if (aPriority !== bPriority) {
          return aPriority - bPriority;
        }
        
        // Same priority, sort by timestamp (FIFO)
        return a.timestamp - b.timestamp;
      });
    }
  }
  
  /**
   * Check rate limiting for bot
   */
  checkRateLimit(botId) {
    const now = Date.now();
    const windowStart = now - this.config.rateLimitWindow;
    
    if (!this.rateLimitTracker.has(botId)) {
      this.rateLimitTracker.set(botId, []);
    }
    
    const botCommands = this.rateLimitTracker.get(botId);
    const recentCommands = botCommands.filter(timestamp => timestamp > windowStart);
    
    if (recentCommands.length >= this.config.maxCommandsPerWindow) {
      return {
        allowed: false,
        count: recentCommands.length,
        limit: this.config.maxCommandsPerWindow
      };
    }
    
    recentCommands.push(now);
    this.rateLimitTracker.set(botId, recentCommands);
    
    return { allowed: true, count: recentCommands.length };
  }
  
  /**
   * Start command processing loop
   */
  startProcessingLoop() {
    setInterval(() => {
      this.processCommandQueue();
    }, 100); // Process every 100ms
    
    // Cleanup expired commands every 30 seconds
    setInterval(() => {
      this.cleanupExpiredCommands();
    }, 30000);
  }
  
  /**
   * Process command queue
   */
  async processCommandQueue() {
    // Check if we can execute more commands
    if (this.activeCommands.size >= this.config.maxConcurrentCommands) {
      return;
    }
    
    // Get next command from queue
    if (this.commandQueue.length === 0) {
      return;
    }
    
    const commandData = this.commandQueue.shift();
    this.stats.queuedCommands--;
    
    // Start command execution
    this.executeQueuedCommand(commandData);
  }
  
  /**
   * Execute a queued command
   */
  async executeQueuedCommand(commandData) {
    const { id: commandId, botId } = commandData;
    
    try {
      // Move to active commands
      commandData.status = 'executing';
      commandData.startTime = Date.now();
      this.activeCommands.set(commandId, commandData);
      
      this.logger.debug(`Executing command: ${commandData.command} (${commandId})`);
      this.emit('commandStarted', commandData);
      
      // Execute the command
      const result = await this.performCommandExecution(commandData);
      
      // Handle successful execution
      await this.handleCommandSuccess(commandData, result);
      
    } catch (error) {
      // Handle command failure
      await this.handleCommandError(commandData, error);
    }
  }
  
  /**
   * Perform the actual command execution
   */
  async performCommandExecution(commandData) {
    const { botId, command, timeout, expectResponse, responseTimeout } = commandData;
    
    // Get bot reference
    const bot = await this.getBotReference(botId);
    if (!bot) {
      throw new Error(`Bot ${botId} not available`);
    }
    
    // Set up timeout
    const timeoutPromise = new Promise((_, reject) => {
      setTimeout(() => {
        reject(new Error(`Command timeout: ${timeout}ms`));
      }, timeout);
    });
    
    // Execute command
    const executionPromise = this.executeBotCommand(bot, command);
    
    let responsePromise = Promise.resolve(null);
    
    // Set up response tracking if expected
    if (expectResponse && this.config.enableResponseTracking) {
      responsePromise = this.trackCommandResponse(commandData.id, responseTimeout);
    }
    
    // Wait for command execution and optional response
    const [executionResult, responseResult] = await Promise.race([
      Promise.all([executionPromise, responsePromise]),
      timeoutPromise
    ]);
    
    return {
      execution: executionResult,
      response: responseResult,
      duration: Date.now() - commandData.startTime
    };
  }
  
  /**
   * Execute command on bot
   */
  async executeBotCommand(bot, command) {
    // This would interface with the HeadlessMinecraftClient
    if (bot.executeCommand) {
      return await bot.executeCommand(command);
    } else if (bot.sendChat) {
      return await bot.sendChat(command);
    } else {
      throw new Error('Bot does not support command execution');
    }
  }
  
  /**
   * Track command response
   */
  async trackCommandResponse(commandId, timeout) {
    return new Promise((resolve) => {
      const responseData = {
        commandId,
        expectedAt: Date.now(),
        timeout,
        resolve,
        responses: []
      };
      
      this.pendingResponses.set(commandId, responseData);
      
      // Auto-resolve after timeout
      setTimeout(() => {
        if (this.pendingResponses.has(commandId)) {
          this.pendingResponses.delete(commandId);
          resolve(responseData.responses);
        }
      }, timeout);
    });
  }
  
  /**
   * Process incoming message for response tracking
   */
  processMessageForResponse(message, source) {
    // Check all pending responses for matches
    for (const [commandId, responseData] of this.pendingResponses) {
      const isResponse = this.isMessageResponse(message, responseData);
      
      if (isResponse) {
        responseData.responses.push({
          message,
          source,
          timestamp: Date.now(),
          matched: isResponse.matched
        });
        
        // If this looks like a final response, resolve immediately
        if (isResponse.isFinal) {
          this.pendingResponses.delete(commandId);
          responseData.resolve(responseData.responses);
        }
      }
    }
  }
  
  /**
   * Check if message is a response to a command
   */
  isMessageResponse(message, responseData) {
    const content = typeof message === 'string' ? message : (message.content || message.message || '');
    
    // Check against response patterns
    for (const pattern of this.responsePatterns) {
      if (pattern.regex.test(content)) {
        return {
          matched: pattern,
          isFinal: pattern.type !== 'info' // Most responses are final except info messages
        };
      }
    }
    
    // Basic heuristics for responses
    if (content.length > 10 && (
      content.includes('Success') ||
      content.includes('Error') ||
      content.includes('Failed') ||
      content.includes('Complete')
    )) {
      return {
        matched: { name: 'heuristic', type: 'unknown' },
        isFinal: true
      };
    }
    
    return false;
  }
  
  /**
   * Handle successful command execution
   */
  async handleCommandSuccess(commandData, result) {
    const { id: commandId } = commandData;
    
    commandData.status = 'completed';
    commandData.endTime = Date.now();
    commandData.duration = commandData.endTime - commandData.startTime;
    commandData.result = result;
    
    // Update statistics
    this.stats.successfulCommands++;
    this.updateAverageExecutionTime(commandData.duration);
    
    // Add to history
    this.addToCommandHistory(commandData);
    
    // Remove from active commands
    this.activeCommands.delete(commandId);
    
    this.logger.debug(`Command completed successfully: ${commandData.command} (${commandId}) in ${commandData.duration}ms`);
    this.emit('commandCompleted', commandData);
    
    // Resolve promise
    if (commandData.resolve) {
      commandData.resolve(result);
    }
  }
  
  /**
   * Handle command execution error
   */
  async handleCommandError(commandData, error) {
    const { id: commandId, maxRetries, retryCount } = commandData;
    
    commandData.error = error;
    
    // Check if we should retry
    if (retryCount < maxRetries) {
      commandData.retryCount++;
      commandData.status = 'retrying';
      
      this.logger.warn(`Command failed, retrying (${retryCount + 1}/${maxRetries}): ${commandData.command} - ${error.message}`);
      this.emit('commandRetrying', commandData);
      
      // Remove from active commands and re-queue after delay
      this.activeCommands.delete(commandId);
      
      setTimeout(() => {
        this.addToQueue(commandData);
        this.stats.queuedCommands++;
      }, this.config.retryDelay * (retryCount + 1)); // Exponential backoff
      
      return;
    }
    
    // Max retries reached, mark as failed
    commandData.status = 'failed';
    commandData.endTime = Date.now();
    commandData.duration = commandData.endTime - commandData.startTime;
    
    // Update statistics
    if (error.message.includes('timeout')) {
      this.stats.timedOutCommands++;
    } else {
      this.stats.failedCommands++;
    }
    
    // Add to history
    this.addToCommandHistory(commandData);
    
    // Remove from active commands
    this.activeCommands.delete(commandId);
    
    this.logger.error(`Command failed permanently: ${commandData.command} (${commandId}) - ${error.message}`);
    this.emit('commandFailed', commandData);
    
    // Reject promise
    if (commandData.reject) {
      commandData.reject(error);
    }
  }
  
  /**
   * Add command to history
   */
  addToCommandHistory(commandData) {
    // Clean sensitive data before storing
    const historyEntry = {
      id: commandData.id,
      botId: commandData.botId,
      command: commandData.command,
      status: commandData.status,
      timestamp: commandData.timestamp,
      startTime: commandData.startTime,
      endTime: commandData.endTime,
      duration: commandData.duration,
      retryCount: commandData.retryCount,
      priority: commandData.priority,
      error: commandData.error ? commandData.error.message : null,
      result: commandData.result ? {
        success: true,
        responseCount: commandData.result.response ? commandData.result.response.length : 0
      } : null
    };
    
    this.commandHistory.push(historyEntry);
    
    // Maintain history size
    if (this.commandHistory.length > this.config.maxResponseHistory) {
      this.commandHistory = this.commandHistory.slice(-this.config.maxResponseHistory);
    }
  }
  
  /**
   * Update average execution time
   */
  updateAverageExecutionTime(duration) {
    const total = this.stats.successfulCommands + this.stats.failedCommands + this.stats.timedOutCommands;
    if (total > 0) {
      this.stats.averageExecutionTime = (
        (this.stats.averageExecutionTime * (total - 1) + duration) / total
      );
    }
  }
  
  /**
   * Get bot reference
   */
  async getBotReference(botId) {
    if (this.botManager && this.botManager.getBot) {
      return this.botManager.getBot(botId);
    }
    
    // Return null if no bot manager available
    return null;
  }
  
  /**
   * Cancel a command
   */
  async cancelCommand(commandId, reason = 'Cancelled by user') {
    // Check active commands
    if (this.activeCommands.has(commandId)) {
      const commandData = this.activeCommands.get(commandId);
      commandData.status = 'cancelled';
      commandData.error = new Error(reason);
      
      this.activeCommands.delete(commandId);
      this.addToCommandHistory(commandData);
      
      this.logger.info(`Cancelled active command: ${commandData.command} (${commandId})`);
      this.emit('commandCancelled', commandData);
      
      if (commandData.reject) {
        commandData.reject(new Error(reason));
      }
      
      return true;
    }
    
    // Check queued commands
    const queueIndex = this.commandQueue.findIndex(cmd => cmd.id === commandId);
    if (queueIndex > -1) {
      const commandData = this.commandQueue.splice(queueIndex, 1)[0];
      commandData.status = 'cancelled';
      commandData.error = new Error(reason);
      
      this.stats.queuedCommands--;
      this.addToCommandHistory(commandData);
      
      this.logger.info(`Cancelled queued command: ${commandData.command} (${commandId})`);
      this.emit('commandCancelled', commandData);
      
      if (commandData.reject) {
        commandData.reject(new Error(reason));
      }
      
      return true;
    }
    
    return false;
  }
  
  /**
   * Cancel all commands for a bot
   */
  async cancelBotCommands(botId, reason = 'Bot disconnected') {
    let cancelledCount = 0;
    
    // Cancel active commands
    for (const [commandId, commandData] of this.activeCommands) {
      if (commandData.botId === botId) {
        await this.cancelCommand(commandId, reason);
        cancelledCount++;
      }
    }
    
    // Cancel queued commands
    for (let i = this.commandQueue.length - 1; i >= 0; i--) {
      if (this.commandQueue[i].botId === botId) {
        const commandData = this.commandQueue.splice(i, 1)[0];
        commandData.status = 'cancelled';
        commandData.error = new Error(reason);
        
        this.stats.queuedCommands--;
        this.addToCommandHistory(commandData);
        
        if (commandData.reject) {
          commandData.reject(new Error(reason));
        }
        
        cancelledCount++;
      }
    }
    
    if (cancelledCount > 0) {
      this.logger.info(`Cancelled ${cancelledCount} commands for bot ${botId}`);
      this.emit('botCommandsCancelled', { botId, count: cancelledCount, reason });
    }
    
    return cancelledCount;
  }
  
  /**
   * Get command status
   */
  getCommandStatus(commandId) {
    // Check active commands
    if (this.activeCommands.has(commandId)) {
      return this.activeCommands.get(commandId);
    }
    
    // Check queued commands
    const queuedCommand = this.commandQueue.find(cmd => cmd.id === commandId);
    if (queuedCommand) {
      return queuedCommand;
    }
    
    // Check history
    const historicalCommand = this.commandHistory.find(cmd => cmd.id === commandId);
    if (historicalCommand) {
      return historicalCommand;
    }
    
    return null;
  }
  
  /**
   * Get command history with filtering
   */
  getCommandHistory(options = {}) {
    let history = [...this.commandHistory];
    
    if (options.botId) {
      history = history.filter(cmd => cmd.botId === options.botId);
    }
    
    if (options.status) {
      history = history.filter(cmd => cmd.status === options.status);
    }
    
    if (options.since) {
      history = history.filter(cmd => cmd.timestamp >= options.since);
    }
    
    if (options.command) {
      history = history.filter(cmd => cmd.command.includes(options.command));
    }
    
    // Sort by timestamp (newest first)
    history.sort((a, b) => b.timestamp - a.timestamp);
    
    if (options.limit) {
      history = history.slice(0, options.limit);
    }
    
    return history;
  }
  
  /**
   * Get queue status
   */
  getQueueStatus() {
    const queueByBot = new Map();
    const queueByPriority = { urgent: 0, high: 0, normal: 0, low: 0 };
    
    for (const command of this.commandQueue) {
      // Count by bot
      const botCount = queueByBot.get(command.botId) || 0;
      queueByBot.set(command.botId, botCount + 1);
      
      // Count by priority
      queueByPriority[command.priority] = (queueByPriority[command.priority] || 0) + 1;
    }
    
    return {
      totalQueued: this.commandQueue.length,
      maxQueueSize: this.config.maxQueueSize,
      queueUtilization: (this.commandQueue.length / this.config.maxQueueSize) * 100,
      queueByBot: Object.fromEntries(queueByBot),
      queueByPriority,
      oldestQueued: this.commandQueue.length > 0 ? this.commandQueue[0].timestamp : null
    };
  }
  
  /**
   * Get execution statistics
   */
  getExecutionStats() {
    const uptime = Date.now() - this.stats.startTime;
    const totalProcessed = this.stats.successfulCommands + this.stats.failedCommands + this.stats.timedOutCommands;
    
    return {
      ...this.stats,
      uptime,
      totalProcessed,
      successRate: totalProcessed > 0 ? (this.stats.successfulCommands / totalProcessed) * 100 : 0,
      commandsPerMinute: totalProcessed > 0 ? (totalProcessed / (uptime / 60000)) : 0,
      activeCommands: this.activeCommands.size,
      queueLength: this.commandQueue.length,
      pendingResponses: this.pendingResponses.size
    };
  }
  
  /**
   * Clean up expired commands
   */
  cleanupExpiredCommands() {
    const now = Date.now();
    let cleanedCount = 0;
    
    // Clean up expired pending responses
    for (const [commandId, responseData] of this.pendingResponses) {
      if (now - responseData.expectedAt > responseData.timeout) {
        this.pendingResponses.delete(commandId);
        responseData.resolve(responseData.responses);
        cleanedCount++;
      }
    }
    
    // Clean up old rate limit entries
    const windowStart = now - this.config.rateLimitWindow;
    for (const [botId, timestamps] of this.rateLimitTracker) {
      const recentTimestamps = timestamps.filter(ts => ts > windowStart);
      if (recentTimestamps.length === 0) {
        this.rateLimitTracker.delete(botId);
      } else {
        this.rateLimitTracker.set(botId, recentTimestamps);
      }
    }
    
    if (cleanedCount > 0) {
      this.logger.debug(`Cleaned up ${cleanedCount} expired command responses`);
    }
  }
  
  /**
   * Get comprehensive status
   */
  getStatus() {
    return {
      config: {
        maxConcurrentCommands: this.config.maxConcurrentCommands,
        maxQueueSize: this.config.maxQueueSize,
        commandTimeout: this.config.commandTimeout,
        enableRateLimit: this.config.enableRateLimit,
        maxCommandsPerWindow: this.config.maxCommandsPerWindow
      },
      queue: this.getQueueStatus(),
      execution: this.getExecutionStats(),
      responseTracking: {
        pendingResponses: this.pendingResponses.size,
        responsePatterns: this.responsePatterns.length,
        historySize: this.responseHistory.length
      }
    };
  }
  
  /**
   * Emergency stop - cancel all commands
   */
  emergencyStop() {
    this.logger.warn('Emergency stop activated - cancelling all commands');
    
    const cancelledActive = this.activeCommands.size;
    const cancelledQueued = this.commandQueue.length;
    
    // Cancel all active commands
    for (const commandId of this.activeCommands.keys()) {
      this.cancelCommand(commandId, 'Emergency stop');
    }
    
    // Cancel all queued commands
    while (this.commandQueue.length > 0) {
      const commandData = this.commandQueue.shift();
      commandData.status = 'cancelled';
      commandData.error = new Error('Emergency stop');
      
      if (commandData.reject) {
        commandData.reject(new Error('Emergency stop'));
      }
    }
    
    this.stats.queuedCommands = 0;
    
    this.logger.warn(`Emergency stop complete: cancelled ${cancelledActive} active and ${cancelledQueued} queued commands`);
    this.emit('emergencyStop', { cancelledActive, cancelledQueued });
  }
  
  /**
   * Shutdown command executor
   */
  shutdown() {
    this.logger.info('Shutting down command executor');
    
    // Cancel all commands
    this.emergencyStop();
    
    // Clear all data structures
    this.commandQueue = [];
    this.activeCommands.clear();
    this.commandHistory = [];
    this.responseHistory = [];
    this.pendingResponses.clear();
    this.rateLimitTracker.clear();
    
    this.removeAllListeners();
    
    this.logger.info('Command executor shutdown complete');
  }
}

module.exports = CommandExecutor;