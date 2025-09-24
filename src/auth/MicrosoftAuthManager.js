/**
 * MicrosoftAuthManager - Handles Microsoft Account authentication for Minecraft
 * Provides OAuth flow, token management, and profile fetching
 */

const { PublicClientApplication } = require('@azure/msal-node');
const { Authenticator } = require('prismarine-auth');
const EventEmitter = require('events');
const fs = require('fs').promises;
const path = require('path');
const crypto = require('crypto');

class MicrosoftAuthManager extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      clientId: options.clientId || '00000000-0000-0000-0000-000000000000', // Default Xbox app ID
      authority: 'https://login.microsoftonline.com/consumers',
      redirectUri: options.redirectUri || 'http://localhost:3000/auth/callback',
      scopes: ['XboxLive.signin', 'offline_access'],
      tokenStorePath: options.tokenStorePath || path.join(process.cwd(), 'data', 'tokens'),
      encryptionKey: options.encryptionKey || this.generateEncryptionKey(),
      ...options.config
    };
    
    this.logger = options.logger || console;
    this.msalClient = null;
    this.activeTokens = new Map();
    
    // Initialize MSAL client
    this.initializeMSALClient();
  }
  
  /**
   * Initialize the MSAL client
   */
  initializeMSALClient() {
    const clientConfig = {
      auth: {
        clientId: this.config.clientId,
        authority: this.config.authority
      },
      system: {
        loggerOptions: {
          loggerCallback: (level, message, containsPii) => {
            if (level === 'Error') {
              this.logger.error('MSAL Error:', message);
            } else if (level === 'Warning') {
              this.logger.warn('MSAL Warning:', message);
            }
          },
          piiLoggingEnabled: false,
          logLevel: 'Error'
        }
      }
    };
    
    this.msalClient = new PublicClientApplication(clientConfig);
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
   * Start the OAuth device code flow
   */
  async startDeviceCodeFlow() {
    try {
      const deviceCodeRequest = {
        scopes: this.config.scopes,
        deviceCodeCallback: (response) => {
          this.logger.info('Microsoft Authentication Required:');
          this.logger.info(`Please visit: ${response.verificationUri}`);
          this.logger.info(`And enter code: ${response.userCode}`);
          
          this.emit('deviceCodeReceived', {
            verificationUri: response.verificationUri,
            userCode: response.userCode,
            expiresIn: response.expiresIn
          });
        }
      };
      
      const response = await this.msalClient.acquireTokenByDeviceCode(deviceCodeRequest);
      
      this.logger.info('Successfully authenticated with Microsoft!');
      
      // Convert to Minecraft token
      const minecraftAuth = await this.convertToMinecraftAuth(response);
      
      return minecraftAuth;
      
    } catch (error) {
      this.logger.error('Device code flow failed:', error.message);
      throw error;
    }
  }
  
  /**
   * Convert Microsoft token to Minecraft authentication
   */
  async convertToMinecraftAuth(msalResponse) {
    try {
      const authenticator = new Authenticator();
      
      // Use the access token to get Xbox Live and Minecraft tokens
      const xboxAuth = await authenticator.getXboxToken(msalResponse.accessToken);
      const minecraftAuth = await authenticator.getMinecraftToken(xboxAuth.token);
      
      // Get Minecraft profile
      const profile = await authenticator.fetchProfile(minecraftAuth.access_token);
      
      const authData = {
        accessToken: minecraftAuth.access_token,
        clientToken: crypto.randomUUID(),
        profile: {
          id: profile.id,
          name: profile.name,
          properties: profile.properties || []
        },
        refreshToken: msalResponse.refreshToken,
        expiresAt: Date.now() + (minecraftAuth.expires_in * 1000),
        type: 'microsoft'
      };
      
      // Store the tokens securely
      await this.storeTokens(profile.id, authData);
      
      this.activeTokens.set(profile.id, authData);
      
      this.emit('authenticationComplete', authData);
      
      return authData;
      
    } catch (error) {
      this.logger.error('Failed to convert to Minecraft auth:', error.message);
      throw error;
    }
  }
  
  /**
   * Refresh an expired token
   */
  async refreshToken(accountId) {
    const tokenData = this.activeTokens.get(accountId);
    
    if (!tokenData || !tokenData.refreshToken) {
      throw new Error(`No refresh token available for account ${accountId}`);
    }
    
    try {
      const refreshRequest = {
        scopes: this.config.scopes,
        refreshToken: tokenData.refreshToken
      };
      
      const response = await this.msalClient.acquireTokenSilent(refreshRequest);
      
      // Convert to Minecraft auth again
      const minecraftAuth = await this.convertToMinecraftAuth(response);
      
      this.logger.info(`Successfully refreshed token for ${minecraftAuth.profile.name}`);
      
      return minecraftAuth;
      
    } catch (error) {
      this.logger.error(`Failed to refresh token for ${accountId}:`, error.message);
      
      // If refresh fails, remove the invalid token
      this.activeTokens.delete(accountId);
      await this.removeStoredTokens(accountId);
      
      throw error;
    }
  }
  
  /**
   * Validate if a token is still valid
   */
  isTokenValid(accountId) {
    const tokenData = this.activeTokens.get(accountId);
    
    if (!tokenData) {
      return false;
    }
    
    // Check if token expires within the next 5 minutes
    const bufferTime = 5 * 60 * 1000; // 5 minutes
    return tokenData.expiresAt > (Date.now() + bufferTime);
  }
  
  /**
   * Get authentication data for an account
   */
  async getAuthData(accountId, autoRefresh = true) {
    let tokenData = this.activeTokens.get(accountId);
    
    // Try to load from storage if not in memory
    if (!tokenData) {
      tokenData = await this.loadStoredTokens(accountId);
      if (tokenData) {
        this.activeTokens.set(accountId, tokenData);
      }
    }
    
    if (!tokenData) {
      throw new Error(`No authentication data found for account ${accountId}`);
    }
    
    // Auto-refresh if needed
    if (autoRefresh && !this.isTokenValid(accountId)) {
      this.logger.info(`Token expired for ${accountId}, refreshing...`);
      tokenData = await this.refreshToken(accountId);
    }
    
    return tokenData;
  }
  
  /**
   * Store tokens securely to disk
   */
  async storeTokens(accountId, tokenData) {
    try {
      // Ensure token directory exists
      await fs.mkdir(this.config.tokenStorePath, { recursive: true });
      
      // Encrypt sensitive data
      const encryptedData = this.encrypt(JSON.stringify(tokenData));
      
      const filePath = path.join(this.config.tokenStorePath, `${accountId}.json`);
      await fs.writeFile(filePath, JSON.stringify(encryptedData, null, 2));
      
      this.logger.debug(`Stored tokens for account ${accountId}`);
      
    } catch (error) {
      this.logger.error(`Failed to store tokens for ${accountId}:`, error.message);
      throw error;
    }
  }
  
  /**
   * Load tokens from storage
   */
  async loadStoredTokens(accountId) {
    try {
      const filePath = path.join(this.config.tokenStorePath, `${accountId}.json`);
      
      const encryptedData = JSON.parse(await fs.readFile(filePath, 'utf8'));
      const decryptedData = this.decrypt(encryptedData);
      
      const tokenData = JSON.parse(decryptedData);
      
      this.logger.debug(`Loaded tokens for account ${accountId}`);
      
      return tokenData;
      
    } catch (error) {
      if (error.code !== 'ENOENT') {
        this.logger.error(`Failed to load tokens for ${accountId}:`, error.message);
      }
      return null;
    }
  }
  
  /**
   * Remove stored tokens
   */
  async removeStoredTokens(accountId) {
    try {
      const filePath = path.join(this.config.tokenStorePath, `${accountId}.json`);
      await fs.unlink(filePath);
      
      this.logger.debug(`Removed stored tokens for account ${accountId}`);
      
    } catch (error) {
      if (error.code !== 'ENOENT') {
        this.logger.error(`Failed to remove tokens for ${accountId}:`, error.message);
      }
    }
  }
  
  /**
   * Get all stored account IDs
   */
  async getStoredAccounts() {
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
   * Load all stored accounts into memory
   */
  async loadAllStoredAccounts() {
    const accountIds = await this.getStoredAccounts();
    
    for (const accountId of accountIds) {
      try {
        const tokenData = await this.loadStoredTokens(accountId);
        if (tokenData) {
          this.activeTokens.set(accountId, tokenData);
          this.logger.info(`Loaded account: ${tokenData.profile.name} (${accountId})`);
        }
      } catch (error) {
        this.logger.error(`Failed to load account ${accountId}:`, error.message);
      }
    }
    
    this.logger.info(`Loaded ${this.activeTokens.size} Microsoft accounts`);
  }
  
  /**
   * Remove an account from storage and memory
   */
  async removeAccount(accountId) {
    this.activeTokens.delete(accountId);
    await this.removeStoredTokens(accountId);
    
    this.logger.info(`Removed account ${accountId}`);
    this.emit('accountRemoved', accountId);
  }
  
  /**
   * Get all active accounts
   */
  getActiveAccounts() {
    const accounts = [];
    
    for (const [accountId, tokenData] of this.activeTokens) {
      accounts.push({
        id: accountId,
        name: tokenData.profile.name,
        type: tokenData.type,
        isValid: this.isTokenValid(accountId),
        expiresAt: tokenData.expiresAt
      });
    }
    
    return accounts;
  }
  
  /**
   * Clean up expired tokens
   */
  async cleanupExpiredTokens() {
    const expiredAccounts = [];
    
    for (const [accountId, tokenData] of this.activeTokens) {
      if (tokenData.expiresAt < Date.now()) {
        expiredAccounts.push(accountId);
      }
    }
    
    for (const accountId of expiredAccounts) {
      this.logger.info(`Cleaning up expired token for ${accountId}`);
      await this.removeAccount(accountId);
    }
    
    return expiredAccounts.length;
  }
  
  /**
   * Start periodic cleanup of expired tokens
   */
  startTokenCleanup(intervalMs = 60000) { // 1 minute
    setInterval(async () => {
      try {
        await this.cleanupExpiredTokens();
      } catch (error) {
        this.logger.error('Token cleanup failed:', error.message);
      }
    }, intervalMs);
  }
  
  /**
   * Shutdown and cleanup
   */
  async shutdown() {
    this.logger.info('Shutting down Microsoft Auth Manager');
    this.activeTokens.clear();
    this.removeAllListeners();
  }
}

module.exports = MicrosoftAuthManager;
