/**
 * AuthManager - Unified authentication manager for Minecraft
 * Handles both Microsoft OAuth and Altening token authentication
 * Provides seamless switching between authentication methods
 */

const EventEmitter = require('events');
const path = require('path');
const MicrosoftAuthManager = require('./MicrosoftAuthManager');
const AlteningAuthManager = require('./AlteningAuthManager');

class AuthManager extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      defaultAuthType: options.defaultAuthType || 'microsoft',
      enableMicrosoft: options.enableMicrosoft !== false,
      enableAltening: options.enableAltening !== false,
      dataPath: options.dataPath || path.join(process.cwd(), 'data'),
      ...options.config
    };
    
    this.logger = options.logger || console;
    this.authProviders = new Map();
    this.accountRegistry = new Map(); // Maps account IDs to auth type
    
    // Initialize authentication providers
    this.initializeProviders(options);
    
    // Setup provider event forwarding
    this.setupEventForwarding();
  }
  
  /**
   * Initialize authentication providers
   */
  initializeProviders(options) {
    // Microsoft Authentication
    if (this.config.enableMicrosoft) {
      const microsoftOptions = {
        tokenStorePath: path.join(this.config.dataPath, 'microsoft-tokens'),
        logger: this.logger,
        ...options.microsoft
      };
      
      this.authProviders.set('microsoft', new MicrosoftAuthManager(microsoftOptions));
      this.logger.info('Initialized Microsoft authentication provider');
    }
    
    // Altening Authentication
    if (this.config.enableAltening) {
      const alteningOptions = {
        tokenStorePath: path.join(this.config.dataPath, 'altening-tokens'),
        sessionStorePath: path.join(this.config.dataPath, 'altening-sessions'),
        logger: this.logger,
        ...options.altening
      };
      
      this.authProviders.set('altening', new AlteningAuthManager(alteningOptions));
      this.logger.info('Initialized Altening authentication provider');
    }
    
    if (this.authProviders.size === 0) {
      throw new Error('No authentication providers enabled');
    }
  }
  
  /**
   * Setup event forwarding from providers
   */
  setupEventForwarding() {
    for (const [type, provider] of this.authProviders) {
      // Forward all provider events with type prefix
      const originalEmit = provider.emit.bind(provider);
      provider.emit = (eventName, ...args) => {
        // Emit on provider
        originalEmit(eventName, ...args);
        
        // Forward to unified manager with type prefix
        this.emit(`${type}:${eventName}`, ...args);
        this.emit('auth', { type, event: eventName, data: args[0] });
      };
      
      // Setup specific event handlers
      provider.on('error', (error) => {
        this.logger.error(`${type} auth error:`, error.message);
      });
    }
  }
  
  /**
   * Get authentication provider by type
   */
  getProvider(type) {
    const provider = this.authProviders.get(type);
    if (!provider) {
      throw new Error(`Authentication provider '${type}' not available`);
    }
    return provider;
  }
  
  /**
   * Determine auth type for an identifier
   */
  determineAuthType(identifier) {
    // Check if we have this account registered
    if (this.accountRegistry.has(identifier)) {
      return this.accountRegistry.get(identifier);
    }
    
    // Try to determine from identifier format
    if (identifier.includes('@') || identifier.length === 32) {
      // Email or UUID format - likely Microsoft
      return 'microsoft';
    }
    
    // Default fallback
    return this.config.defaultAuthType;
  }
  
  /**
   * Register an account with specific auth type
   */
  registerAccount(identifier, authType) {
    if (!this.authProviders.has(authType)) {
      throw new Error(`Authentication type '${authType}' not available`);
    }
    
    this.accountRegistry.set(identifier, authType);
    this.logger.info(`Registered account ${identifier} with ${authType} authentication`);
  }
  
  /**
   * Authenticate using Microsoft OAuth
   */
  async authenticateMicrosoft(options = {}) {
    const provider = this.getProvider('microsoft');
    const authData = await provider.startDeviceCodeFlow();
    
    // Register the account
    this.registerAccount(authData.profile.id, 'microsoft');
    
    return authData;
  }
  
  /**
   * Generate an Altening token
   */
  async generateAlteningToken(options = {}) {
    const provider = this.getProvider('altening');
    const tokenData = await provider.generateToken(options);
    
    // Register the account
    this.registerAccount(tokenData.username, 'altening');
    
    return tokenData;
  }
  
  /**
   * Add existing Altening token
   */
  async addAlteningToken(token, username) {
    const provider = this.getProvider('altening');
    
    // Validate the token first
    const validation = await provider.validateToken(token);
    if (!validation.valid) {
      throw new Error(`Invalid Altening token: ${validation.error}`);
    }
    
    const tokenData = {
      token,
      username: username || validation.username,
      uuid: validation.uuid,
      plan: validation.plan,
      expiresAt: validation.expiresAt,
      generatedAt: Date.now(),
      type: 'altening'
    };
    
    // Store the token
    await provider.storeToken(tokenData.username, tokenData);
    provider.activeTokens.set(tokenData.username, tokenData);
    
    // Register the account
    this.registerAccount(tokenData.username, 'altening');
    
    return tokenData;
  }
  
  /**
   * Get authentication data for any account
   */
  async getAuthData(identifier, options = {}) {
    let authType = options.authType;
    
    if (!authType) {
      authType = this.determineAuthType(identifier);
    }
    
    const provider = this.getProvider(authType);
    
    try {
      const authData = await provider.getAuthData(identifier, options);
      
      // Ensure we have the auth type recorded
      if (!this.accountRegistry.has(identifier)) {
        this.registerAccount(identifier, authType);
      }
      
      return {
        ...authData,
        authType,
        identifier
      };
      
    } catch (error) {
      // If primary auth type fails, try fallback if not explicitly specified
      if (!options.authType && authType !== 'microsoft' && this.authProviders.has('microsoft')) {
        try {
          const fallbackAuth = await this.authProviders.get('microsoft').getAuthData(identifier, options);
          this.registerAccount(identifier, 'microsoft');
          
          return {
            ...fallbackAuth,
            authType: 'microsoft',
            identifier
          };
        } catch (fallbackError) {
          // Ignore fallback error, throw original
        }
      }
      
      throw error;
    }
  }
  
  /**
   * Switch authentication method for an account
   */
  async switchAuthMethod(identifier, newAuthType, options = {}) {
    if (!this.authProviders.has(newAuthType)) {
      throw new Error(`Authentication type '${newAuthType}' not available`);
    }
    
    const oldAuthType = this.accountRegistry.get(identifier);
    
    try {
      // Try to get auth data with new method
      const authData = await this.getAuthData(identifier, { 
        ...options, 
        authType: newAuthType 
      });
      
      // If successful, remove old auth data
      if (oldAuthType && oldAuthType !== newAuthType) {
        const oldProvider = this.getProvider(oldAuthType);
        if (oldProvider.removeAccount) {
          await oldProvider.removeAccount(identifier);
        }
      }
      
      this.registerAccount(identifier, newAuthType);
      
      this.logger.info(`Switched auth method for ${identifier}: ${oldAuthType} -> ${newAuthType}`);
      this.emit('authMethodSwitched', { identifier, from: oldAuthType, to: newAuthType });
      
      return authData;
      
    } catch (error) {
      this.logger.error(`Failed to switch auth method for ${identifier}:`, error.message);
      throw error;
    }
  }
  
  /**
   * Validate account authentication
   */
  async validateAccount(identifier, options = {}) {
    const authType = options.authType || this.determineAuthType(identifier);
    const provider = this.getProvider(authType);
    
    try {
      if (authType === 'microsoft') {
        return provider.isTokenValid(identifier);
      } else if (authType === 'altening') {
        const tokenData = provider.activeTokens.get(identifier) || 
                         await provider.loadStoredToken(identifier);
        
        if (!tokenData) {
          return false;
        }
        
        // Check expiration
        if (tokenData.expiresAt && tokenData.expiresAt < Date.now()) {
          return false;
        }
        
        // Validate with Altening service if requested
        if (options.validateRemote !== false) {
          const validation = await provider.validateToken(tokenData.token);
          return validation.valid;
        }
        
        return true;
      }
      
      return false;
      
    } catch (error) {
      this.logger.error(`Failed to validate account ${identifier}:`, error.message);
      return false;
    }
  }
  
  /**
   * Remove an account from all providers
   */
  async removeAccount(identifier) {
    const authType = this.accountRegistry.get(identifier);
    let removed = false;
    
    if (authType) {
      try {
        const provider = this.getProvider(authType);
        if (provider.removeAccount) {
          await provider.removeAccount(identifier);
        } else if (provider.removeToken) {
          await provider.removeToken(identifier);
        }
        removed = true;
      } catch (error) {
        this.logger.error(`Failed to remove account ${identifier} from ${authType}:`, error.message);
      }
    } else {
      // Try to remove from all providers
      for (const [type, provider] of this.authProviders) {
        try {
          if (provider.removeAccount) {
            await provider.removeAccount(identifier);
            removed = true;
          } else if (provider.removeToken) {
            await provider.removeToken(identifier);
            removed = true;
          }
        } catch (error) {
          // Ignore errors for accounts that don't exist in this provider
        }
      }
    }
    
    this.accountRegistry.delete(identifier);
    
    if (removed) {
      this.logger.info(`Removed account ${identifier}`);
      this.emit('accountRemoved', { identifier, authType });
    }
    
    return removed;
  }
  
  /**
   * Get all available accounts across all providers
   */
  async getAllAccounts() {
    const accounts = [];
    
    for (const [type, provider] of this.authProviders) {
      try {
        let providerAccounts = [];
        
        if (type === 'microsoft') {
          providerAccounts = provider.getActiveAccounts();
        } else if (type === 'altening') {
          providerAccounts = provider.getActiveTokens();
        }
        
        for (const account of providerAccounts) {
          accounts.push({
            ...account,
            authType: type,
            identifier: account.id || account.identifier || account.username
          });
        }
        
      } catch (error) {
        this.logger.error(`Failed to get accounts from ${type} provider:`, error.message);
      }
    }
    
    return accounts;
  }
  
  /**
   * Load all stored accounts from all providers
   */
  async loadAllStoredAccounts() {
    for (const [type, provider] of this.authProviders) {
      try {
        if (provider.loadAllStoredAccounts) {
          await provider.loadAllStoredAccounts();
        } else if (provider.loadAllStoredTokens) {
          await provider.loadAllStoredTokens();
        }
        
        // Register accounts with their auth type
        if (type === 'microsoft') {
          for (const [accountId] of provider.activeTokens) {
            this.registerAccount(accountId, 'microsoft');
          }
        } else if (type === 'altening') {
          for (const [username] of provider.activeTokens) {
            this.registerAccount(username, 'altening');
          }
        }
        
      } catch (error) {
        this.logger.error(`Failed to load stored accounts from ${type} provider:`, error.message);
      }
    }
    
    this.logger.info(`Loaded accounts from ${this.authProviders.size} authentication providers`);
  }
  
  /**
   * Perform cleanup on all providers
   */
  async performCleanup() {
    let totalCleaned = 0;
    
    for (const [type, provider] of this.authProviders) {
      try {
        let cleaned = 0;
        
        if (provider.cleanupExpiredTokens) {
          cleaned = await provider.cleanupExpiredTokens();
        } else if (provider.cleanupExpired) {
          const result = await provider.cleanupExpired();
          cleaned = (result.removedTokens || 0) + (result.removedSessions || 0);
        }
        
        totalCleaned += cleaned;
        
        if (cleaned > 0) {
          this.logger.info(`Cleaned up ${cleaned} expired items from ${type} provider`);
        }
        
      } catch (error) {
        this.logger.error(`Cleanup failed for ${type} provider:`, error.message);
      }
    }
    
    return totalCleaned;
  }
  
  /**
   * Get authentication statistics
   */
  getAuthStats() {
    const stats = {
      providers: {},
      totalAccounts: 0,
      registeredAccounts: this.accountRegistry.size
    };
    
    for (const [type, provider] of this.authProviders) {
      try {
        let accounts = [];
        let sessions = 0;
        
        if (type === 'microsoft') {
          accounts = provider.getActiveAccounts();
        } else if (type === 'altening') {
          accounts = provider.getActiveTokens();
          try {
            sessions = provider.getActiveSessions().length;
          } catch (error) {
            // Ignore session count errors
          }
        }
        
        stats.providers[type] = {
          accounts: accounts.length,
          sessions,
          validAccounts: accounts.filter(acc => !acc.isExpired && acc.isValid !== false).length
        };
        
        stats.totalAccounts += accounts.length;
        
      } catch (error) {
        this.logger.error(`Failed to get stats from ${type} provider:`, error.message);
        stats.providers[type] = { accounts: 0, sessions: 0, validAccounts: 0 };
      }
    }
    
    return stats;
  }
  
  /**
   * Shutdown all authentication providers
   */
  async shutdown() {
    this.logger.info('Shutting down Authentication Manager');
    
    for (const [type, provider] of this.authProviders) {
      try {
        if (provider.shutdown) {
          await provider.shutdown();
        }
      } catch (error) {
        this.logger.error(`Failed to shutdown ${type} provider:`, error.message);
      }
    }
    
    this.authProviders.clear();
    this.accountRegistry.clear();
    this.removeAllListeners();
  }
}

module.exports = AuthManager;