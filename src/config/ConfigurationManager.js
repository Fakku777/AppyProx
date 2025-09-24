const EventEmitter = require('events');
const fs = require('fs').promises;
const path = require('path');
const { v4: uuidv4 } = require('uuid');

/**
 * Dynamic Configuration Management System
 * Allows runtime adjustment of automation parameters, cluster coordination, and system behavior
 */
class ConfigurationManager extends EventEmitter {
  constructor(logger) {
    super();
    this.logger = logger.child ? logger.child('ConfigManager') : logger;
    
    // Configuration storage
    this.configs = new Map();
    this.configPaths = new Map();
    this.watchers = new Map();
    this.validationSchemas = new Map();
    this.configHistory = new Map();
    
    // Runtime parameters
    this.runtimeOverrides = new Map();
    this.activeProfiles = new Set();
    
    // Configuration categories
    this.categories = {
      proxy: 'proxy',
      automation: 'automation', 
      clustering: 'clustering',
      centralNode: 'centralNode',
      api: 'api',
      system: 'system'
    };
    
    // Default configuration paths
    this.configDir = path.join(__dirname, '../../configs');
    this.defaultConfigFiles = {
      [this.categories.proxy]: 'default.json',
      [this.categories.automation]: 'automation.json',
      [this.categories.clustering]: 'clusters.json',
      [this.categories.centralNode]: 'central-node.json',
      [this.categories.api]: 'api.json',
      [this.categories.system]: 'system.json'
    };
    
    // Validation rules
    this.initializeValidationSchemas();
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing configuration manager...');
    
    try {
      // Load all configuration files
      await this.loadAllConfigurations();
      
      // Start file watchers for hot reloading
      await this.setupFileWatchers();
      
      // Initialize default profiles
      await this.loadConfigurationProfiles();
      
      this.initialized = true;
      this.logger.info('Configuration manager initialized successfully');
      this.emit('initialized');
      
    } catch (error) {
      this.logger.error('Failed to initialize configuration manager:', error);
      throw error;
    }
  }

  async shutdown() {
    if (!this.initialized) return;
    
    this.logger.info('Shutting down configuration manager...');
    
    // Save current configurations
    await this.saveAllConfigurations();
    
    // Stop file watchers
    for (const [path, watcher] of this.watchers) {
      try {
        await watcher.close();
      } catch (error) {
        this.logger.warn(`Failed to close watcher for ${path}:`, error.message);
      }
    }
    this.watchers.clear();
    
    this.initialized = false;
    this.logger.info('Configuration manager shut down');
  }

  initializeValidationSchemas() {
    // Proxy configuration schema
    this.validationSchemas.set(this.categories.proxy, {
      host: { type: 'string', required: true },
      port: { type: 'number', min: 1, max: 65535, required: true },
      maxClients: { type: 'number', min: 1, max: 1000, default: 100 },
      timeout: { type: 'number', min: 1000, max: 300000, default: 30000 },
      enableCompression: { type: 'boolean', default: true }
    });

    // Automation configuration schema
    this.validationSchemas.set(this.categories.automation, {
      enabled: { type: 'boolean', default: true },
      maxConcurrentTasks: { type: 'number', min: 1, max: 100, default: 10 },
      taskTimeout: { type: 'number', min: 5000, max: 3600000, default: 300000 },
      retryAttempts: { type: 'number', min: 0, max: 10, default: 3 },
      baritoneEnabled: { type: 'boolean', default: true },
      litematicaEnabled: { type: 'boolean', default: true },
      wikiScrapingEnabled: { type: 'boolean', default: true },
      resourceOptimization: { type: 'boolean', default: true }
    });

    // Clustering configuration schema
    this.validationSchemas.set(this.categories.clustering, {
      enabled: { type: 'boolean', default: true },
      maxClusterSize: { type: 'number', min: 1, max: 100, default: 10 },
      leaderElection: { 
        type: 'string', 
        enum: ['simple', 'raft', 'raft_simplified'],
        default: 'raft_simplified' 
      },
      heartbeatInterval: { type: 'number', min: 1000, max: 60000, default: 5000 },
      maxReconnectAttempts: { type: 'number', min: 1, max: 50, default: 10 },
      autoRebalancing: { type: 'boolean', default: true },
      loadBalancing: { type: 'boolean', default: true }
    });

    // Central Node configuration schema
    this.validationSchemas.set(this.categories.centralNode, {
      enabled: { type: 'boolean', default: true },
      webPort: { type: 'number', min: 1024, max: 65535, default: 8080 },
      websocketPort: { type: 'number', min: 1024, max: 65535, default: 8081 },
      enableVisualization: { type: 'boolean', default: true },
      enableAnalytics: { type: 'boolean', default: true },
      updateInterval: { type: 'number', min: 100, max: 10000, default: 1000 }
    });

    // API configuration schema
    this.validationSchemas.set(this.categories.api, {
      enabled: { type: 'boolean', default: true },
      port: { type: 'number', min: 1024, max: 65535, default: 3000 },
      enableCors: { type: 'boolean', default: true },
      rateLimiting: { type: 'boolean', default: true },
      maxRequestsPerMinute: { type: 'number', min: 10, max: 10000, default: 1000 },
      enableSwagger: { type: 'boolean', default: true }
    });

    // System configuration schema
    this.validationSchemas.set(this.categories.system, {
      logLevel: { 
        type: 'string', 
        enum: ['error', 'warn', 'info', 'debug'], 
        default: 'info' 
      },
      maxLogFiles: { type: 'number', min: 1, max: 100, default: 10 },
      logRotationSize: { type: 'number', min: 1048576, max: 104857600, default: 10485760 },
      enableMetrics: { type: 'boolean', default: true },
      metricsInterval: { type: 'number', min: 1000, max: 300000, default: 30000 }
    });
  }

  async loadAllConfigurations() {
    this.logger.info('Loading all configurations...');
    
    for (const [category, filename] of Object.entries(this.defaultConfigFiles)) {
      const configPath = path.join(this.configDir, filename);
      this.configPaths.set(category, configPath);
      
      try {
        await this.loadConfiguration(category, configPath);
      } catch (error) {
        this.logger.warn(`Failed to load ${category} config from ${configPath}, using defaults:`, error.message);
        this.configs.set(category, this.getDefaultConfiguration(category));
      }
    }
  }

  async loadConfiguration(category, configPath) {
    try {
      const configData = await fs.readFile(configPath, 'utf8');
      const config = JSON.parse(configData);
      
      // Validate configuration
      const validatedConfig = this.validateConfiguration(category, config);
      
      this.configs.set(category, validatedConfig);
      this.logger.debug(`Loaded configuration for ${category} from ${configPath}`);
      
      return validatedConfig;
    } catch (error) {
      if (error.code === 'ENOENT') {
        // File doesn't exist, use defaults
        const defaultConfig = this.getDefaultConfiguration(category);
        this.configs.set(category, defaultConfig);
        
        // Create default configuration file
        await this.saveConfiguration(category);
        
        return defaultConfig;
      }
      throw error;
    }
  }

  validateConfiguration(category, config) {
    const schema = this.validationSchemas.get(category);
    if (!schema) {
      this.logger.warn(`No validation schema for category: ${category}`);
      return config;
    }

    const validatedConfig = {};
    
    for (const [key, rules] of Object.entries(schema)) {
      let value = config[key];
      
      // Use default if value is missing
      if (value === undefined && rules.default !== undefined) {
        value = rules.default;
      }
      
      // Required field validation
      if (rules.required && value === undefined) {
        throw new Error(`Required configuration field missing: ${category}.${key}`);
      }
      
      if (value !== undefined) {
        // Type validation
        if (rules.type && typeof value !== rules.type) {
          throw new Error(`Invalid type for ${category}.${key}: expected ${rules.type}, got ${typeof value}`);
        }
        
        // Numeric range validation
        if (rules.type === 'number') {
          if (rules.min !== undefined && value < rules.min) {
            throw new Error(`Value too small for ${category}.${key}: ${value} < ${rules.min}`);
          }
          if (rules.max !== undefined && value > rules.max) {
            throw new Error(`Value too large for ${category}.${key}: ${value} > ${rules.max}`);
          }
        }
        
        // Enum validation
        if (rules.enum && !rules.enum.includes(value)) {
          throw new Error(`Invalid value for ${category}.${key}: ${value}. Must be one of: ${rules.enum.join(', ')}`);
        }
        
        validatedConfig[key] = value;
      }
    }
    
    // Add any additional fields not in schema (for flexibility)
    for (const [key, value] of Object.entries(config)) {
      if (!schema[key]) {
        validatedConfig[key] = value;
      }
    }
    
    return validatedConfig;
  }

  getDefaultConfiguration(category) {
    const schema = this.validationSchemas.get(category);
    if (!schema) return {};
    
    const defaultConfig = {};
    for (const [key, rules] of Object.entries(schema)) {
      if (rules.default !== undefined) {
        defaultConfig[key] = rules.default;
      }
    }
    
    return defaultConfig;
  }

  async setupFileWatchers() {
    if (process.env.NODE_ENV === 'test') {
      this.logger.debug('Skipping file watchers in test environment');
      return;
    }
    
    this.logger.info('Setting up configuration file watchers...');
    
    for (const [category, configPath] of this.configPaths) {
      try {
        const fs = require('fs');
        const watcher = fs.watch(configPath, { persistent: false }, (eventType, filename) => {
          if (eventType === 'change') {
            this.logger.info(`Configuration file changed: ${configPath}`);
            this.handleConfigurationChange(category, configPath);
          }
        });
        
        this.watchers.set(configPath, watcher);
        this.logger.debug(`Watching configuration file: ${configPath}`);
      } catch (error) {
        this.logger.warn(`Failed to watch configuration file ${configPath}:`, error.message);
      }
    }
  }

  async handleConfigurationChange(category, configPath) {
    try {
      this.logger.info(`Reloading configuration for category: ${category}`);
      
      const oldConfig = this.getConfiguration(category);
      const newConfig = await this.loadConfiguration(category, configPath);
      
      // Store in history
      this.addToHistory(category, oldConfig, 'file_change');
      
      this.logger.info(`Configuration reloaded for ${category}`);
      this.emit('configuration_changed', { category, oldConfig, newConfig });
      
    } catch (error) {
      this.logger.error(`Failed to reload configuration for ${category}:`, error);
      this.emit('configuration_error', { category, error });
    }
  }

  async loadConfigurationProfiles() {
    const profilesPath = path.join(this.configDir, 'profiles.json');
    
    try {
      const profilesData = await fs.readFile(profilesPath, 'utf8');
      const profiles = JSON.parse(profilesData);
      
      this.logger.info(`Loaded ${Object.keys(profiles).length} configuration profiles`);
      this.emit('profiles_loaded', profiles);
      
    } catch (error) {
      this.logger.debug('No configuration profiles found, using defaults');
      await this.createDefaultProfiles();
    }
  }

  async createDefaultProfiles() {
    const defaultProfiles = {
      development: {
        name: 'Development',
        description: 'Development environment settings',
        overrides: {
          system: { logLevel: 'debug' },
          automation: { taskTimeout: 60000, retryAttempts: 1 },
          clustering: { heartbeatInterval: 2000 }
        }
      },
      production: {
        name: 'Production', 
        description: 'Production environment settings',
        overrides: {
          system: { logLevel: 'warn' },
          automation: { taskTimeout: 600000, retryAttempts: 5 },
          clustering: { heartbeatInterval: 10000 }
        }
      },
      testing: {
        name: 'Testing',
        description: 'Testing environment settings',
        overrides: {
          system: { logLevel: 'error' },
          automation: { taskTimeout: 10000, retryAttempts: 0 },
          clustering: { heartbeatInterval: 1000 }
        }
      }
    };
    
    const profilesPath = path.join(this.configDir, 'profiles.json');
    await fs.writeFile(profilesPath, JSON.stringify(defaultProfiles, null, 2));
    
    this.logger.info('Created default configuration profiles');
  }

  // Public API Methods

  getConfiguration(category) {
    const config = this.configs.get(category);
    if (!config) {
      this.logger.warn(`Configuration category not found: ${category}`);
      return this.getDefaultConfiguration(category);
    }
    
    // Apply runtime overrides
    const overrides = this.runtimeOverrides.get(category) || {};
    return { ...config, ...overrides };
  }

  async setConfiguration(category, updates, source = 'api') {
    const currentConfig = this.getConfiguration(category);
    const newConfig = { ...currentConfig, ...updates };
    
    try {
      // Validate the new configuration
      const validatedConfig = this.validateConfiguration(category, newConfig);
      
      // Store in history
      this.addToHistory(category, currentConfig, source);
      
      // Update configuration
      this.configs.set(category, validatedConfig);
      
      // Save to file
      await this.saveConfiguration(category);
      
      this.logger.info(`Configuration updated for ${category}:`, updates);
      this.emit('configuration_updated', { category, updates, source });
      
      return validatedConfig;
    } catch (error) {
      this.logger.error(`Failed to update configuration for ${category}:`, error);
      throw error;
    }
  }

  setRuntimeOverride(category, key, value, temporary = true) {
    if (!this.runtimeOverrides.has(category)) {
      this.runtimeOverrides.set(category, {});
    }
    
    const overrides = this.runtimeOverrides.get(category);
    const oldValue = overrides[key];
    
    overrides[key] = value;
    
    this.logger.info(`Runtime override set: ${category}.${key} = ${value} (temporary: ${temporary})`);
    this.emit('runtime_override_set', { category, key, value, oldValue, temporary });
    
    // If not temporary, save to persistent configuration
    if (!temporary) {
      this.setConfiguration(category, { [key]: value }, 'runtime_override');
    }
  }

  removeRuntimeOverride(category, key) {
    const overrides = this.runtimeOverrides.get(category);
    if (overrides && overrides[key] !== undefined) {
      const oldValue = overrides[key];
      delete overrides[key];
      
      this.logger.info(`Runtime override removed: ${category}.${key}`);
      this.emit('runtime_override_removed', { category, key, oldValue });
    }
  }

  async applyProfile(profileName) {
    const profilesPath = path.join(this.configDir, 'profiles.json');
    
    try {
      const profilesData = await fs.readFile(profilesPath, 'utf8');
      const profiles = JSON.parse(profilesData);
      const profile = profiles[profileName];
      
      if (!profile) {
        throw new Error(`Profile not found: ${profileName}`);
      }
      
      this.logger.info(`Applying configuration profile: ${profileName}`);
      
      // Apply overrides from profile
      for (const [category, overrides] of Object.entries(profile.overrides || {})) {
        await this.setConfiguration(category, overrides, `profile_${profileName}`);
      }
      
      this.activeProfiles.add(profileName);
      this.emit('profile_applied', { profileName, profile });
      
      this.logger.info(`Profile applied successfully: ${profileName}`);
      
    } catch (error) {
      this.logger.error(`Failed to apply profile ${profileName}:`, error);
      throw error;
    }
  }

  async saveConfiguration(category) {
    const config = this.configs.get(category);
    const configPath = this.configPaths.get(category);
    
    if (!config || !configPath) {
      throw new Error(`Cannot save configuration for category: ${category}`);
    }
    
    try {
      await fs.writeFile(configPath, JSON.stringify(config, null, 2));
      this.logger.debug(`Saved configuration for ${category} to ${configPath}`);
    } catch (error) {
      this.logger.error(`Failed to save configuration for ${category}:`, error);
      throw error;
    }
  }

  async saveAllConfigurations() {
    this.logger.info('Saving all configurations...');
    
    const savePromises = Array.from(this.configs.keys()).map(category => 
      this.saveConfiguration(category)
    );
    
    try {
      await Promise.all(savePromises);
      this.logger.info('All configurations saved successfully');
    } catch (error) {
      this.logger.error('Failed to save some configurations:', error);
      throw error;
    }
  }

  addToHistory(category, config, source) {
    if (!this.configHistory.has(category)) {
      this.configHistory.set(category, []);
    }
    
    const history = this.configHistory.get(category);
    history.push({
      timestamp: Date.now(),
      config: JSON.parse(JSON.stringify(config)),
      source: source
    });
    
    // Keep only last 10 entries
    if (history.length > 10) {
      history.shift();
    }
  }

  getConfigurationHistory(category) {
    return this.configHistory.get(category) || [];
  }

  getStatus() {
    return {
      initialized: this.initialized,
      categories: Array.from(this.configs.keys()),
      activeProfiles: Array.from(this.activeProfiles),
      runtimeOverrides: Array.from(this.runtimeOverrides.entries()),
      watchedFiles: Array.from(this.watchers.keys())
    };
  }

  // Utility methods for common configuration patterns

  enableFeature(feature) {
    const updates = { [feature]: true };
    
    // Apply to relevant categories based on feature name
    if (feature.includes('automation')) {
      this.setRuntimeOverride('automation', feature, true);
    } else if (feature.includes('cluster')) {
      this.setRuntimeOverride('clustering', feature, true);  
    } else {
      this.setRuntimeOverride('system', feature, true);
    }
    
    this.logger.info(`Feature enabled: ${feature}`);
  }

  disableFeature(feature) {
    const updates = { [feature]: false };
    
    if (feature.includes('automation')) {
      this.setRuntimeOverride('automation', feature, false);
    } else if (feature.includes('cluster')) {
      this.setRuntimeOverride('clustering', feature, false);
    } else {
      this.setRuntimeOverride('system', feature, false);
    }
    
    this.logger.info(`Feature disabled: ${feature}`);
  }

  adjustPerformanceSettings(level = 'balanced') {
    const settings = {
      low: {
        automation: { maxConcurrentTasks: 2, taskTimeout: 60000 },
        clustering: { maxClusterSize: 3, heartbeatInterval: 10000 }
      },
      balanced: {
        automation: { maxConcurrentTasks: 5, taskTimeout: 300000 },
        clustering: { maxClusterSize: 10, heartbeatInterval: 5000 }
      },
      high: {
        automation: { maxConcurrentTasks: 20, taskTimeout: 600000 },
        clustering: { maxClusterSize: 50, heartbeatInterval: 2000 }
      }
    };
    
    const config = settings[level];
    if (config) {
      for (const [category, updates] of Object.entries(config)) {
        for (const [key, value] of Object.entries(updates)) {
          this.setRuntimeOverride(category, key, value, true);
        }
      }
      
      this.logger.info(`Performance settings adjusted to: ${level}`);
      this.emit('performance_settings_changed', { level, config });
    }
  }
}

module.exports = ConfigurationManager;