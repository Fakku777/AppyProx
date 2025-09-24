/**
 * AlteningAuthManager - Handles Altening token-based authentication for Minecraft
 * Provides token validation, account switching, and session management using Altening service
 */

const EventEmitter = require('events');
const fetch = require('node-fetch');
const crypto = require('crypto');
const fs = require('fs').promises;
const path = require('path');

class AlteningAuthManager extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      // Altening API endpoints
      baseUrl: options.baseUrl || 'http://api.thealtening.com',
      apiKey: options.apiKey || null,
      
      // Token management
      tokenStorePath: options.tokenStorePath || path.join(process.cwd(), 'data', 'altening-tokens'),
      sessionStorePath: options.sessionStorePath || path.join(process.cwd(), 'data', 'altening-sessions'),
      encryptionKey: options.encryptionKey || this.generateEncryptionKey(),
      
      // Session settings
      maxSessions: options.maxSessions || 50,
      sessionTimeout: options.sessionTimeout || 30 * 60 * 1000, // 30 minutes
      retryAttempts: options.retryAttempts || 3,
      retryDelay: options.retryDelay || 2000,
      
      // Rate limiting
      requestDelay: options.requestDelay || 1000,
      
      ...options.config
    };
    
    this.logger = options.logger || console;
    this.activeTokens = new Map();
    this.activeSessions = new Map();
    this.tokenQueue = [];
    this.isProcessingQueue = false;
    this.rateLimitState = {
      lastRequest: 0,
      requestCount: 0,
      resetTime: 0
    };
    
    // Initialize token and session cleanup
    this.startSessionCleanup();
  }
  
  /**
   * Generate a random encryption key
   */
  generateEncryptionKey() {
    return crypto.randomBytes(32).toString('hex');
  }
  
  /**
   * Encrypt sensitive data
   */
  encrypt(text) {
    const algorithm = 'aes-256-gcm';
    const key = Buffer.from(this.config.encryptionKey, 'hex');
    const iv = crypto.randomBytes(16);
    
    const cipher = crypto.createCipheriv(algorithm, key, iv);
    
    let encrypted = cipher.update(text, 'utf8', 'hex');
    encrypted += cipher.final('hex');
    
    const authTag = cipher.getAuthTag();
    
    return {
      encrypted,
      iv: iv.toString('hex'),
      authTag: authTag.toString('hex')
    };
  }
  
  /**
   * Decrypt sensitive data
   */
  decrypt(encryptedData) {
    const algorithm = 'aes-256-gcm';
    const key = Buffer.from(this.config.encryptionKey, 'hex');
    const iv = Buffer.from(encryptedData.iv, 'hex');
    
    const decipher = crypto.createDecipheriv(algorithm, key, iv);
    decipher.setAuthTag(Buffer.from(encryptedData.authTag, 'hex'));
    
    let decrypted = decipher.update(encryptedData.encrypted, 'hex', 'utf8');
    decrypted += decipher.final('utf8');
    
    return decrypted;
  }
  
  /**
   * Make rate-limited API request to Altening
   */
  async makeAPIRequest(endpoint, options = {}) {
    // Respect rate limiting
    await this.respectRateLimit();
    
    const url = `${this.config.baseUrl}${endpoint}`;
    const requestOptions = {
      method: options.method || 'GET',
      headers: {
        'Content-Type': 'application/json',
        'User-Agent': 'AppyProx/0.0.1',
        ...options.headers
      },
      ...options
    };
    
    // Add API key if provided
    if (this.config.apiKey) {
      requestOptions.headers['Authorization'] = `Bearer ${this.config.apiKey}`;
    }
    
    let lastError;
    for (let attempt = 0; attempt < this.config.retryAttempts; attempt++) {
      try {
        this.logger.debug(`Making Altening API request: ${endpoint} (attempt ${attempt + 1})`);
        
        const response = await fetch(url, requestOptions);
        
        // Update rate limiting info from response headers
        this.updateRateLimitState(response);
        
        if (!response.ok) {
          throw new Error(`Altening API error: ${response.status} ${response.statusText}`);
        }
        
        const data = await response.json();
        return data;
        
      } catch (error) {
        lastError = error;
        this.logger.warn(`Altening API request failed (attempt ${attempt + 1}):`, error.message);
        
        if (attempt < this.config.retryAttempts - 1) {
          await this.delay(this.config.retryDelay * (attempt + 1));
        }
      }
    }
    
    throw lastError;
  }
  
  /**
   * Respect rate limiting by waiting if necessary
   */
  async respectRateLimit() {
    const now = Date.now();
    const timeSinceLastRequest = now - this.rateLimitState.lastRequest;
    
    if (timeSinceLastRequest < this.config.requestDelay) {
      const waitTime = this.config.requestDelay - timeSinceLastRequest;
      await this.delay(waitTime);
    }
    
    this.rateLimitState.lastRequest = Date.now();
  }
  
  /**
   * Update rate limit state from API response headers
   */
  updateRateLimitState(response) {
    const remaining = response.headers.get('X-RateLimit-Remaining');
    const reset = response.headers.get('X-RateLimit-Reset');
    
    if (remaining !== null) {
      this.rateLimitState.requestCount = parseInt(remaining, 10);
    }
    
    if (reset !== null) {
      this.rateLimitState.resetTime = parseInt(reset, 10) * 1000;
    }
  }
  
  /**
   * Utility delay function
   */
  delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
  
  /**
   * Generate a new Altening token
   */
  async generateToken(options = {}) {
    try {
      const endpoint = '/generate';
      const requestOptions = {
        method: 'POST',
        body: JSON.stringify({
          plan: options.plan || 'basic',
          duration: options.duration || '1h',
          limit: options.limit || 1
        })
      };
      
      const response = await this.makeAPIRequest(endpoint, requestOptions);
      
      if (!response.success) {
        throw new Error(`Failed to generate token: ${response.message || 'Unknown error'}`);
      }
      
      const tokenData = {
        token: response.token,
        username: response.username,
        uuid: response.uuid || crypto.randomUUID(),
        plan: options.plan || 'basic',
        expiresAt: Date.now() + this.parseDuration(options.duration || '1h'),
        generatedAt: Date.now(),
        type: 'altening'
      };
      
      // Store the token
      await this.storeToken(tokenData.username, tokenData);
      this.activeTokens.set(tokenData.username, tokenData);
      
      this.logger.info(`Generated Altening token for ${tokenData.username}`);
      this.emit('tokenGenerated', tokenData);
      
      return tokenData;
      
    } catch (error) {
      this.logger.error('Failed to generate Altening token:', error.message);
      throw error;
    }
  }
  
  /**
   * Validate an Altening token
   */
  async validateToken(token) {
    try {
      const endpoint = '/validate';
      const requestOptions = {
        method: 'POST',
        body: JSON.stringify({ token })
      };
      
      const response = await this.makeAPIRequest(endpoint, requestOptions);
      
      return {
        valid: response.valid === true,
        username: response.username,
        uuid: response.uuid,
        expiresAt: response.expires ? new Date(response.expires).getTime() : null,
        plan: response.plan,
        info: response
      };
      
    } catch (error) {
      this.logger.error('Failed to validate Altening token:', error.message);
      return { valid: false, error: error.message };
    }
  }
  
  /**
   * Get account information for a token
   */
  async getAccountInfo(token) {
    try {
      const endpoint = '/account';
      const requestOptions = {
        method: 'POST',
        body: JSON.stringify({ token })
      };
      
      const response = await this.makeAPIRequest(endpoint, requestOptions);
      
      return {
        username: response.username,
        uuid: response.uuid,
        skin: response.skin,
        cape: response.cape,
        plan: response.plan,
        expiresAt: response.expires ? new Date(response.expires).getTime() : null
      };
      
    } catch (error) {
      this.logger.error('Failed to get account info:', error.message);
      throw error;
    }
  }
  
  /**
   * Create a session for a token
   */
  async createSession(tokenData, options = {}) {
    try {
      // First validate the token is still active
      const validation = await this.validateToken(tokenData.token);
      
      if (!validation.valid) {
        throw new Error(`Token is invalid: ${validation.error || 'Token expired or revoked'}`);
      }
      
      const sessionId = crypto.randomUUID();
      const sessionData = {
        id: sessionId,
        token: tokenData.token,
        username: tokenData.username,
        uuid: tokenData.uuid || validation.uuid,
        plan: tokenData.plan || validation.plan,
        createdAt: Date.now(),
        expiresAt: Date.now() + this.config.sessionTimeout,
        lastActive: Date.now(),
        server: options.server || null,
        clientId: options.clientId || null,
        type: 'altening'
      };
      
      // Store session
      await this.storeSession(sessionId, sessionData);
      this.activeSessions.set(sessionId, sessionData);
      
      this.logger.info(`Created Altening session ${sessionId} for ${sessionData.username}`);
      this.emit('sessionCreated', sessionData);
      
      return sessionData;
      
    } catch (error) {
      this.logger.error('Failed to create Altening session:', error.message);
      throw error;
    }
  }
  
  /**
   * Get session by ID
   */
  async getSession(sessionId) {
    let session = this.activeSessions.get(sessionId);
    
    if (!session) {
      // Try to load from storage
      session = await this.loadStoredSession(sessionId);
      if (session) {
        this.activeSessions.set(sessionId, session);
      }
    }
    
    if (!session) {
      throw new Error(`Session ${sessionId} not found`);
    }
    
    // Check if session is expired
    if (session.expiresAt < Date.now()) {
      await this.removeSession(sessionId);
      throw new Error(`Session ${sessionId} has expired`);
    }
    
    // Update last active time
    session.lastActive = Date.now();
    await this.storeSession(sessionId, session);
    
    return session;
  }
  
  /**
   * Update session activity
   */
  async updateSessionActivity(sessionId) {
    const session = await this.getSession(sessionId);
    session.lastActive = Date.now();
    await this.storeSession(sessionId, session);
    
    this.emit('sessionActivity', { sessionId, lastActive: session.lastActive });
  }
  
  /**
   * Remove a session
   */
  async removeSession(sessionId) {
    const session = this.activeSessions.get(sessionId);
    
    this.activeSessions.delete(sessionId);
    await this.removeStoredSession(sessionId);
    
    if (session) {
      this.logger.info(`Removed session ${sessionId} for ${session.username}`);
      this.emit('sessionRemoved', session);
    }
  }
  
  /**
   * Get authentication data for Minecraft client
   */
  async getAuthData(identifier, options = {}) {
    let tokenData;
    
    // Try to find by username first
    tokenData = this.activeTokens.get(identifier);
    
    // If not found, try to load from storage
    if (!tokenData) {
      tokenData = await this.loadStoredToken(identifier);
      if (tokenData) {
        this.activeTokens.set(identifier, tokenData);
      }
    }
    
    if (!tokenData) {
      throw new Error(`No Altening token found for ${identifier}`);
    }
    
    // Check if token is expired
    if (tokenData.expiresAt && tokenData.expiresAt < Date.now()) {
      this.logger.warn(`Token for ${identifier} has expired`);
      await this.removeToken(identifier);
      throw new Error(`Token for ${identifier} has expired`);
    }
    
    // Validate token with Altening service if requested
    if (options.validate !== false) {
      const validation = await this.validateToken(tokenData.token);
      if (!validation.valid) {
        this.logger.warn(`Token for ${identifier} failed validation`);
        await this.removeToken(identifier);
        throw new Error(`Token for ${identifier} is no longer valid`);
      }
    }
    
    // Create session if requested
    let session = null;
    if (options.createSession !== false) {
      session = await this.createSession(tokenData, options);
    }
    
    return {
      accessToken: tokenData.token,
      clientToken: crypto.randomUUID(),
      profile: {
        id: tokenData.uuid,
        name: tokenData.username,
        properties: []
      },
      session,
      type: 'altening',
      expiresAt: tokenData.expiresAt
    };
  }
  
  /**
   * Switch account for a client
   */
  async switchAccount(currentIdentifier, newIdentifier) {
    try {
      // Remove current session if exists
      const currentTokenData = this.activeTokens.get(currentIdentifier);
      if (currentTokenData && currentTokenData.sessionId) {
        await this.removeSession(currentTokenData.sessionId);
      }
      
      // Get new auth data
      const newAuthData = await this.getAuthData(newIdentifier);
      
      this.logger.info(`Switched account from ${currentIdentifier} to ${newIdentifier}`);
      this.emit('accountSwitched', { from: currentIdentifier, to: newIdentifier });
      
      return newAuthData;
      
    } catch (error) {
      this.logger.error(`Failed to switch account from ${currentIdentifier} to ${newIdentifier}:`, error.message);
      throw error;
    }
  }
  
  /**
   * Store token securely to disk
   */
  async storeToken(identifier, tokenData) {
    try {
      await fs.mkdir(this.config.tokenStorePath, { recursive: true });
      
      const encryptedData = this.encrypt(JSON.stringify(tokenData));
      const filePath = path.join(this.config.tokenStorePath, `${identifier}.json`);
      
      await fs.writeFile(filePath, JSON.stringify(encryptedData, null, 2));
      
      this.logger.debug(`Stored Altening token for ${identifier}`);
      
    } catch (error) {
      this.logger.error(`Failed to store token for ${identifier}:`, error.message);
      throw error;
    }
  }
  
  /**
   * Load token from storage
   */
  async loadStoredToken(identifier) {
    try {
      const filePath = path.join(this.config.tokenStorePath, `${identifier}.json`);
      const encryptedData = JSON.parse(await fs.readFile(filePath, 'utf8'));
      const decryptedData = this.decrypt(encryptedData);
      
      const tokenData = JSON.parse(decryptedData);
      
      this.logger.debug(`Loaded Altening token for ${identifier}`);
      return tokenData;
      
    } catch (error) {
      if (error.code !== 'ENOENT') {
        this.logger.error(`Failed to load token for ${identifier}:`, error.message);
      }
      return null;
    }
  }
  
  /**
   * Remove stored token
   */
  async removeStoredToken(identifier) {
    try {
      const filePath = path.join(this.config.tokenStorePath, `${identifier}.json`);
      await fs.unlink(filePath);
      
      this.logger.debug(`Removed stored token for ${identifier}`);
      
    } catch (error) {
      if (error.code !== 'ENOENT') {
        this.logger.error(`Failed to remove token for ${identifier}:`, error.message);
      }
    }
  }
  
  /**
   * Store session to disk
   */
  async storeSession(sessionId, sessionData) {
    try {
      await fs.mkdir(this.config.sessionStorePath, { recursive: true });
      
      const encryptedData = this.encrypt(JSON.stringify(sessionData));
      const filePath = path.join(this.config.sessionStorePath, `${sessionId}.json`);
      
      await fs.writeFile(filePath, JSON.stringify(encryptedData, null, 2));
      
    } catch (error) {
      this.logger.error(`Failed to store session ${sessionId}:`, error.message);
      throw error;
    }
  }
  
  /**
   * Load session from storage
   */
  async loadStoredSession(sessionId) {
    try {
      const filePath = path.join(this.config.sessionStorePath, `${sessionId}.json`);
      const encryptedData = JSON.parse(await fs.readFile(filePath, 'utf8'));
      const decryptedData = this.decrypt(encryptedData);
      
      return JSON.parse(decryptedData);
      
    } catch (error) {
      if (error.code !== 'ENOENT') {
        this.logger.error(`Failed to load session ${sessionId}:`, error.message);
      }
      return null;
    }
  }
  
  /**
   * Remove stored session
   */
  async removeStoredSession(sessionId) {
    try {
      const filePath = path.join(this.config.sessionStorePath, `${sessionId}.json`);
      await fs.unlink(filePath);
      
    } catch (error) {
      if (error.code !== 'ENOENT') {
        this.logger.error(`Failed to remove session ${sessionId}:`, error.message);
      }
    }
  }
  
  /**
   * Remove a token
   */
  async removeToken(identifier) {
    this.activeTokens.delete(identifier);
    await this.removeStoredToken(identifier);
    
    this.logger.info(`Removed Altening token for ${identifier}`);
    this.emit('tokenRemoved', identifier);
  }
  
  /**
   * Get all stored token identifiers
   */
  async getStoredTokens() {
    try {
      const files = await fs.readdir(this.config.tokenStorePath);
      return files
        .filter(file => file.endsWith('.json'))
        .map(file => file.replace('.json', ''));
    } catch (error) {
      if (error.code === 'ENOENT') {
        return [];
      }
      throw error;
    }
  }
  
  /**
   * Load all stored tokens into memory
   */
  async loadAllStoredTokens() {
    const identifiers = await this.getStoredTokens();
    
    for (const identifier of identifiers) {
      try {
        const tokenData = await this.loadStoredToken(identifier);
        if (tokenData) {
          this.activeTokens.set(identifier, tokenData);
          this.logger.info(`Loaded Altening token: ${identifier}`);
        }
      } catch (error) {
        this.logger.error(`Failed to load token ${identifier}:`, error.message);
      }
    }
    
    this.logger.info(`Loaded ${this.activeTokens.size} Altening tokens`);
  }
  
  /**
   * Get all active tokens
   */
  getActiveTokens() {
    const tokens = [];
    
    for (const [identifier, tokenData] of this.activeTokens) {
      tokens.push({
        identifier,
        username: tokenData.username,
        uuid: tokenData.uuid,
        plan: tokenData.plan,
        type: tokenData.type,
        expiresAt: tokenData.expiresAt,
        isExpired: tokenData.expiresAt && tokenData.expiresAt < Date.now()
      });
    }
    
    return tokens;
  }
  
  /**
   * Get all active sessions
   */
  getActiveSessions() {
    const sessions = [];
    
    for (const [sessionId, sessionData] of this.activeSessions) {
      sessions.push({
        id: sessionId,
        username: sessionData.username,
        uuid: sessionData.uuid,
        plan: sessionData.plan,
        createdAt: sessionData.createdAt,
        expiresAt: sessionData.expiresAt,
        lastActive: sessionData.lastActive,
        server: sessionData.server,
        clientId: sessionData.clientId,
        isExpired: sessionData.expiresAt < Date.now()
      });
    }
    
    return sessions;
  }
  
  /**
   * Parse duration string to milliseconds
   */
  parseDuration(duration) {
    const units = {
      's': 1000,
      'm': 60 * 1000,
      'h': 60 * 60 * 1000,
      'd': 24 * 60 * 60 * 1000
    };
    
    const match = duration.match(/^(\d+)([smhd])$/);
    if (!match) {
      return 60 * 60 * 1000; // Default 1 hour
    }
    
    const [, amount, unit] = match;
    return parseInt(amount, 10) * units[unit];
  }
  
  /**
   * Clean up expired tokens and sessions
   */
  async cleanupExpired() {
    const now = Date.now();
    let removedTokens = 0;
    let removedSessions = 0;
    
    // Clean up expired tokens
    for (const [identifier, tokenData] of this.activeTokens) {
      if (tokenData.expiresAt && tokenData.expiresAt < now) {
        await this.removeToken(identifier);
        removedTokens++;
      }
    }
    
    // Clean up expired sessions
    for (const [sessionId, sessionData] of this.activeSessions) {
      if (sessionData.expiresAt < now) {
        await this.removeSession(sessionId);
        removedSessions++;
      }
    }
    
    if (removedTokens > 0 || removedSessions > 0) {
      this.logger.info(`Cleaned up ${removedTokens} expired tokens and ${removedSessions} expired sessions`);
    }
    
    return { removedTokens, removedSessions };
  }
  
  /**
   * Start periodic cleanup of expired tokens and sessions
   */
  startSessionCleanup(intervalMs = 5 * 60 * 1000) { // 5 minutes
    this.cleanupInterval = setInterval(async () => {
      try {
        await this.cleanupExpired();
      } catch (error) {
        this.logger.error('Cleanup failed:', error.message);
      }
    }, intervalMs);
  }
  
  /**
   * Stop cleanup interval
   */
  stopSessionCleanup() {
    if (this.cleanupInterval) {
      clearInterval(this.cleanupInterval);
      this.cleanupInterval = null;
    }
  }
  
  /**
   * Shutdown and cleanup
   */
  async shutdown() {
    this.logger.info('Shutting down Altening Auth Manager');
    
    this.stopSessionCleanup();
    this.activeTokens.clear();
    this.activeSessions.clear();
    this.removeAllListeners();
  }
}

module.exports = AlteningAuthManager;