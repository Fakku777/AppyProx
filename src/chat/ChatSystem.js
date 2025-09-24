/**
 * ChatSystem - Enhanced chat monitoring and management system
 * Provides real-time chat monitoring, filtering, message queuing, and event forwarding
 */

const EventEmitter = require('events');
const { v4: uuidv4 } = require('uuid');

class ChatSystem extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      // Message storage
      maxChatHistory: options.maxChatHistory || 1000,
      chatTtl: options.chatTtl || 600000, // 10 minutes
      
      // Filtering
      enableFiltering: options.enableFiltering !== false,
      filterPatterns: options.filterPatterns || [],
      blockedUsers: options.blockedUsers || [],
      allowedUsers: options.allowedUsers || [], // If set, only these users can send messages
      
      // Rate limiting
      enableRateLimit: options.enableRateLimit !== false,
      rateLimitWindow: options.rateLimitWindow || 60000, // 1 minute
      maxMessagesPerWindow: options.maxMessagesPerWindow || 10,
      
      // Command detection
      commandPrefixes: options.commandPrefixes || ['/', '!', '.'],
      enableCommandDetection: options.enableCommandDetection !== false,
      
      // Monitoring
      enableMentionDetection: options.enableMentionDetection !== false,
      enableKeywordDetection: options.enableKeywordDetection !== false,
      keywords: options.keywords || [],
      
      ...options.config
    };
    
    this.logger = options.logger || console;
    
    // Chat storage and tracking
    this.chatHistory = [];
    this.messageQueue = [];
    this.userStats = new Map(); // username -> stats
    this.channelStats = new Map(); // channel -> stats
    
    // Filtering and moderation
    this.filterCache = new Map(); // message hash -> filter result
    this.rateLimitTracker = new Map(); // user -> rate limit data
    
    // Message patterns for filtering
    this.filterRegexes = this.compileFilterPatterns();
    
    // Statistics
    this.stats = {
      totalMessages: 0,
      filteredMessages: 0,
      commandMessages: 0,
      mentionMessages: 0,
      keywordMatches: 0,
      rateLimitViolations: 0,
      startTime: Date.now()
    };
    
    // Start cleanup intervals
    this.startCleanupTasks();
  }
  
  /**
   * Compile filter patterns into regex objects
   */
  compileFilterPatterns() {
    const regexes = [];
    
    for (const pattern of this.config.filterPatterns) {
      try {
        if (typeof pattern === 'string') {
          regexes.push(new RegExp(pattern, 'i'));
        } else if (pattern instanceof RegExp) {
          regexes.push(pattern);
        }
      } catch (error) {
        this.logger.warn(`Invalid filter pattern: ${pattern}`, error.message);
      }
    }
    
    return regexes;
  }
  
  /**
   * Process an incoming chat message
   */
  processMessage(message, source = 'unknown') {
    const processedMessage = this.createMessageObject(message, source);
    
    try {
      // Update statistics
      this.stats.totalMessages++;
      this.updateUserStats(processedMessage.username);
      
      // Apply filters
      const filterResult = this.applyFilters(processedMessage);
      if (!filterResult.allowed) {
        this.stats.filteredMessages++;
        this.emit('messageFiltered', { message: processedMessage, reason: filterResult.reason });
        return { processed: false, reason: filterResult.reason };
      }
      
      // Check rate limits
      const rateLimitResult = this.checkRateLimit(processedMessage.username);
      if (!rateLimitResult.allowed) {
        this.stats.rateLimitViolations++;
        this.emit('rateLimitViolation', { message: processedMessage, rateLimitData: rateLimitResult });
        return { processed: false, reason: 'Rate limit exceeded' };
      }
      
      // Detect special message types
      this.detectSpecialMessages(processedMessage);
      
      // Store message
      this.storeMessage(processedMessage);
      
      // Emit processed message event
      this.emit('messageProcessed', processedMessage);
      
      return { processed: true, message: processedMessage };
      
    } catch (error) {
      this.logger.error('Error processing message:', error.message);
      this.emit('messageProcessingError', { message: processedMessage, error });
      return { processed: false, reason: 'Processing error' };
    }
  }
  
  /**
   * Create standardized message object
   */
  createMessageObject(message, source) {
    const timestamp = Date.now();
    const messageId = uuidv4();
    
    // Handle different message formats
    let parsedMessage;
    if (typeof message === 'string') {
      parsedMessage = {
        content: message,
        username: 'unknown',
        channel: null
      };
    } else {
      parsedMessage = {
        content: message.message || message.content || '',
        username: message.username || message.sender || message.from || 'unknown',
        channel: message.channel || null,
        extra: message.extra || null
      };
    }
    
    return {
      id: messageId,
      timestamp,
      source,
      ...parsedMessage,
      raw: message,
      processed: {
        isCommand: false,
        isMention: false,
        hasKeywords: [],
        sentiment: null
      }
    };
  }
  
  /**
   * Apply content filters to message
   */
  applyFilters(message) {
    if (!this.config.enableFiltering) {
      return { allowed: true };
    }
    
    // Check blocked users
    if (this.config.blockedUsers.includes(message.username.toLowerCase())) {
      return { allowed: false, reason: 'User blocked' };
    }
    
    // Check allowed users (if whitelist is enabled)
    if (this.config.allowedUsers.length > 0) {
      if (!this.config.allowedUsers.includes(message.username.toLowerCase())) {
        return { allowed: false, reason: 'User not in allowlist' };
      }
    }
    
    // Check content filters
    const content = message.content.toLowerCase();
    for (const regex of this.filterRegexes) {
      if (regex.test(content)) {
        return { allowed: false, reason: `Content filter matched: ${regex.source}` };
      }
    }
    
    return { allowed: true };
  }
  
  /**
   * Check rate limiting for user
   */
  checkRateLimit(username) {
    if (!this.config.enableRateLimit) {
      return { allowed: true };
    }
    
    const now = Date.now();
    const windowStart = now - this.config.rateLimitWindow;
    
    if (!this.rateLimitTracker.has(username)) {
      this.rateLimitTracker.set(username, []);
    }
    
    const userMessages = this.rateLimitTracker.get(username);
    
    // Remove old messages outside the window
    const recentMessages = userMessages.filter(timestamp => timestamp > windowStart);
    
    // Check if limit exceeded
    if (recentMessages.length >= this.config.maxMessagesPerWindow) {
      return { 
        allowed: false, 
        count: recentMessages.length, 
        limit: this.config.maxMessagesPerWindow,
        windowStart: windowStart
      };
    }
    
    // Add current message timestamp
    recentMessages.push(now);
    this.rateLimitTracker.set(username, recentMessages);
    
    return { allowed: true, count: recentMessages.length };
  }
  
  /**
   * Detect special message types (commands, mentions, keywords)
   */
  detectSpecialMessages(message) {
    const content = message.content.trim();
    
    // Detect commands
    if (this.config.enableCommandDetection) {
      for (const prefix of this.config.commandPrefixes) {
        if (content.startsWith(prefix)) {
          message.processed.isCommand = true;
          message.processed.command = this.parseCommand(content);
          this.stats.commandMessages++;
          this.emit('commandDetected', message);
          break;
        }
      }
    }
    
    // Detect mentions (basic @ detection)
    if (this.config.enableMentionDetection) {
      const mentionRegex = /@(\w+)/g;
      const mentions = [];
      let match;
      
      while ((match = mentionRegex.exec(content)) !== null) {
        mentions.push(match[1]);
      }
      
      if (mentions.length > 0) {
        message.processed.isMention = true;
        message.processed.mentions = mentions;
        this.stats.mentionMessages++;
        this.emit('mentionDetected', message);
      }
    }
    
    // Detect keywords
    if (this.config.enableKeywordDetection && this.config.keywords.length > 0) {
      const contentLower = content.toLowerCase();
      const foundKeywords = [];
      
      for (const keyword of this.config.keywords) {
        if (contentLower.includes(keyword.toLowerCase())) {
          foundKeywords.push(keyword);
        }
      }
      
      if (foundKeywords.length > 0) {
        message.processed.hasKeywords = foundKeywords;
        this.stats.keywordMatches++;
        this.emit('keywordDetected', message);
      }
    }
  }
  
  /**
   * Parse command from message content
   */
  parseCommand(content) {
    const parts = content.trim().split(/\s+/);
    const commandWithPrefix = parts[0];
    const prefix = commandWithPrefix[0];
    const command = commandWithPrefix.slice(1);
    const args = parts.slice(1);
    
    return {
      prefix,
      command,
      args,
      raw: content
    };
  }
  
  /**
   * Store message in chat history
   */
  storeMessage(message) {
    this.chatHistory.push(message);
    
    // Maintain history size limit
    if (this.chatHistory.length > this.config.maxChatHistory) {
      this.chatHistory = this.chatHistory.slice(-this.config.maxChatHistory);
    }
  }
  
  /**
   * Update user statistics
   */
  updateUserStats(username) {
    if (!this.userStats.has(username)) {
      this.userStats.set(username, {
        username,
        messageCount: 0,
        firstSeen: Date.now(),
        lastSeen: Date.now(),
        commandCount: 0,
        mentionCount: 0
      });
    }
    
    const stats = this.userStats.get(username);
    stats.messageCount++;
    stats.lastSeen = Date.now();
  }
  
  /**
   * Get chat history with optional filtering
   */
  getChatHistory(options = {}) {
    let messages = [...this.chatHistory];
    
    // Apply filters
    if (options.username) {
      messages = messages.filter(msg => msg.username.toLowerCase() === options.username.toLowerCase());
    }
    
    if (options.channel) {
      messages = messages.filter(msg => msg.channel === options.channel);
    }
    
    if (options.since) {
      messages = messages.filter(msg => msg.timestamp >= options.since);
    }
    
    if (options.commandsOnly) {
      messages = messages.filter(msg => msg.processed.isCommand);
    }
    
    if (options.mentionsOnly) {
      messages = messages.filter(msg => msg.processed.isMention);
    }
    
    // Apply limit
    if (options.limit) {
      messages = messages.slice(-options.limit);
    }
    
    return messages;
  }
  
  /**
   * Search chat history
   */
  searchChatHistory(query, options = {}) {
    const queryLower = query.toLowerCase();
    let results = this.chatHistory.filter(message => {
      const contentMatch = message.content.toLowerCase().includes(queryLower);
      const usernameMatch = message.username.toLowerCase().includes(queryLower);
      
      return contentMatch || usernameMatch;
    });
    
    // Apply additional filters
    if (options.username) {
      results = results.filter(msg => msg.username.toLowerCase() === options.username.toLowerCase());
    }
    
    if (options.since) {
      results = results.filter(msg => msg.timestamp >= options.since);
    }
    
    // Sort by relevance (content matches first, then username matches)
    results.sort((a, b) => {
      const aContentMatch = a.content.toLowerCase().includes(queryLower);
      const bContentMatch = b.content.toLowerCase().includes(queryLower);
      
      if (aContentMatch && !bContentMatch) return -1;
      if (!aContentMatch && bContentMatch) return 1;
      
      // If both or neither match content, sort by timestamp (newest first)
      return b.timestamp - a.timestamp;
    });
    
    return options.limit ? results.slice(0, options.limit) : results;
  }
  
  /**
   * Get user statistics
   */
  getUserStats(username = null) {
    if (username) {
      return this.userStats.get(username.toLowerCase()) || null;
    }
    
    // Return all user stats sorted by message count
    return Array.from(this.userStats.values())
      .sort((a, b) => b.messageCount - a.messageCount);
  }
  
  /**
   * Get chat statistics
   */
  getChatStats() {
    const uptime = Date.now() - this.stats.startTime;
    const messagesPerMinute = this.stats.totalMessages / (uptime / 60000);
    
    return {
      ...this.stats,
      uptime,
      messagesPerMinute: parseFloat(messagesPerMinute.toFixed(2)),
      uniqueUsers: this.userStats.size,
      averageMessageLength: this.calculateAverageMessageLength(),
      topUsers: this.getTopUsers(5),
      recentActivity: this.getRecentActivity()
    };
  }
  
  /**
   * Calculate average message length
   */
  calculateAverageMessageLength() {
    if (this.chatHistory.length === 0) return 0;
    
    const totalLength = this.chatHistory.reduce((sum, msg) => sum + msg.content.length, 0);
    return parseFloat((totalLength / this.chatHistory.length).toFixed(2));
  }
  
  /**
   * Get top users by message count
   */
  getTopUsers(limit = 10) {
    return Array.from(this.userStats.values())
      .sort((a, b) => b.messageCount - a.messageCount)
      .slice(0, limit)
      .map(user => ({
        username: user.username,
        messageCount: user.messageCount,
        lastSeen: user.lastSeen
      }));
  }
  
  /**
   * Get recent activity summary
   */
  getRecentActivity() {
    const fiveMinutesAgo = Date.now() - 300000; // 5 minutes
    const recentMessages = this.chatHistory.filter(msg => msg.timestamp >= fiveMinutesAgo);
    
    return {
      recentMessageCount: recentMessages.length,
      recentUniqueUsers: new Set(recentMessages.map(msg => msg.username)).size,
      recentCommands: recentMessages.filter(msg => msg.processed.isCommand).length
    };
  }
  
  /**
   * Add filter pattern
   */
  addFilterPattern(pattern) {
    try {
      const regex = new RegExp(pattern, 'i');
      this.filterRegexes.push(regex);
      this.config.filterPatterns.push(pattern);
      
      this.logger.info(`Added filter pattern: ${pattern}`);
      this.emit('filterPatternAdded', pattern);
      
      return true;
    } catch (error) {
      this.logger.error(`Invalid filter pattern: ${pattern}`, error.message);
      return false;
    }
  }
  
  /**
   * Remove filter pattern
   */
  removeFilterPattern(pattern) {
    const index = this.config.filterPatterns.indexOf(pattern);
    if (index > -1) {
      this.config.filterPatterns.splice(index, 1);
      this.filterRegexes = this.compileFilterPatterns(); // Recompile
      
      this.logger.info(`Removed filter pattern: ${pattern}`);
      this.emit('filterPatternRemoved', pattern);
      
      return true;
    }
    return false;
  }
  
  /**
   * Block a user
   */
  blockUser(username) {
    const usernameLower = username.toLowerCase();
    if (!this.config.blockedUsers.includes(usernameLower)) {
      this.config.blockedUsers.push(usernameLower);
      
      this.logger.info(`Blocked user: ${username}`);
      this.emit('userBlocked', username);
      
      return true;
    }
    return false;
  }
  
  /**
   * Unblock a user
   */
  unblockUser(username) {
    const usernameLower = username.toLowerCase();
    const index = this.config.blockedUsers.indexOf(usernameLower);
    
    if (index > -1) {
      this.config.blockedUsers.splice(index, 1);
      
      this.logger.info(`Unblocked user: ${username}`);
      this.emit('userUnblocked', username);
      
      return true;
    }
    return false;
  }
  
  /**
   * Clear chat history
   */
  clearChatHistory() {
    const clearedCount = this.chatHistory.length;
    this.chatHistory = [];
    
    this.logger.info(`Cleared ${clearedCount} messages from chat history`);
    this.emit('chatHistoryCleared', { clearedCount });
    
    return clearedCount;
  }
  
  /**
   * Export chat history
   */
  exportChatHistory(format = 'json') {
    switch (format.toLowerCase()) {
      case 'json':
        return JSON.stringify(this.chatHistory, null, 2);
        
      case 'csv':
        const headers = 'timestamp,username,channel,content,isCommand,isMention\n';
        const rows = this.chatHistory.map(msg => {
          const timestamp = new Date(msg.timestamp).toISOString();
          const content = `"${msg.content.replace(/"/g, '""')}"`;
          const username = msg.username;
          const channel = msg.channel || '';
          const isCommand = msg.processed.isCommand;
          const isMention = msg.processed.isMention;
          
          return `${timestamp},${username},${channel},${content},${isCommand},${isMention}`;
        }).join('\n');
        
        return headers + rows;
        
      case 'txt':
        return this.chatHistory.map(msg => {
          const timestamp = new Date(msg.timestamp).toLocaleString();
          const channel = msg.channel ? `[${msg.channel}] ` : '';
          return `[${timestamp}] ${channel}<${msg.username}> ${msg.content}`;
        }).join('\n');
        
      default:
        throw new Error(`Unsupported export format: ${format}`);
    }
  }
  
  /**
   * Start cleanup tasks
   */
  startCleanupTasks() {
    // Clean expired messages every 5 minutes
    setInterval(() => {
      this.cleanupExpiredMessages();
    }, 300000);
    
    // Clean rate limit tracker every minute
    setInterval(() => {
      this.cleanupRateLimitTracker();
    }, 60000);
  }
  
  /**
   * Clean up expired messages
   */
  cleanupExpiredMessages() {
    const cutoffTime = Date.now() - this.config.chatTtl;
    const initialCount = this.chatHistory.length;
    
    this.chatHistory = this.chatHistory.filter(msg => msg.timestamp > cutoffTime);
    
    const removedCount = initialCount - this.chatHistory.length;
    if (removedCount > 0) {
      this.logger.debug(`Cleaned up ${removedCount} expired messages`);
    }
  }
  
  /**
   * Clean up rate limit tracker
   */
  cleanupRateLimitTracker() {
    const windowStart = Date.now() - this.config.rateLimitWindow;
    
    for (const [username, timestamps] of this.rateLimitTracker.entries()) {
      const recentTimestamps = timestamps.filter(ts => ts > windowStart);
      
      if (recentTimestamps.length === 0) {
        this.rateLimitTracker.delete(username);
      } else {
        this.rateLimitTracker.set(username, recentTimestamps);
      }
    }
  }
  
  /**
   * Get comprehensive status
   */
  getStatus() {
    return {
      chatHistory: this.chatHistory.length,
      userStats: this.userStats.size,
      rateLimitTracker: this.rateLimitTracker.size,
      filterPatterns: this.config.filterPatterns.length,
      blockedUsers: this.config.blockedUsers.length,
      stats: this.getChatStats(),
      config: {
        maxChatHistory: this.config.maxChatHistory,
        enableFiltering: this.config.enableFiltering,
        enableRateLimit: this.config.enableRateLimit,
        maxMessagesPerWindow: this.config.maxMessagesPerWindow,
        rateLimitWindow: this.config.rateLimitWindow
      }
    };
  }
  
  /**
   * Shutdown and cleanup
   */
  shutdown() {
    this.logger.info('Shutting down chat system');
    
    this.chatHistory = [];
    this.userStats.clear();
    this.rateLimitTracker.clear();
    this.filterCache.clear();
    this.removeAllListeners();
    
    this.logger.info('Chat system shutdown complete');
  }
}

module.exports = ChatSystem;