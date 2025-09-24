const EventEmitter = require('events');
const fs = require('fs').promises;
const path = require('path');
const { v4: uuidv4 } = require('uuid');

/**
 * Advanced Error Handling and Recovery System
 * Provides sophisticated error handling with automatic recovery, rollback mechanisms, and failure analysis
 */
class ErrorRecoverySystem extends EventEmitter {
  constructor(config, logger, components = {}) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('ErrorRecovery') : logger;
    this.components = components;
    
    // Error tracking
    this.errors = new Map(); // errorId -> error details
    this.errorHistory = [];
    this.errorPatterns = new Map();
    this.recoveryAttempts = new Map();
    
    // Recovery strategies
    this.recoveryStrategies = new Map();
    this.backupStates = new Map();
    this.rollbackPoints = new Map();
    
    // Configuration
    this.errorConfig = {
      maxRetryAttempts: 3,
      retryBackoffMultiplier: 2,
      initialRetryDelay: 1000,
      maxRetryDelay: 60000,
      errorHistoryLimit: 1000,
      patternAnalysisWindow: 3600000, // 1 hour
      autoRecoveryEnabled: true,
      rollbackEnabled: true,
      backupRetentionDays: 7,
      ...this.config.errorHandling
    };
    
    // Recovery statistics
    this.stats = {
      totalErrors: 0,
      recoveredErrors: 0,
      failedRecoveries: 0,
      rollbacksExecuted: 0,
      averageRecoveryTime: 0,
      criticalErrors: 0
    };
    
    // Initialize recovery strategies
    this.initializeRecoveryStrategies();
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing error recovery system...');
    
    try {
      // Setup error directories
      await this.setupErrorDirectories();
      
      // Load error history
      await this.loadErrorHistory();
      
      // Setup global error handlers
      this.setupGlobalErrorHandlers();
      
      // Start periodic tasks
      this.startPeriodicTasks();
      
      this.initialized = true;
      this.logger.info('Error recovery system initialized successfully');
      this.emit('system_ready');
      
    } catch (error) {
      this.logger.error('Failed to initialize error recovery system:', error);
      throw error;
    }
  }

  async setupErrorDirectories() {
    const dirs = [
      './logs/errors',
      './backups/states',
      './backups/configs',
      './recovery/rollback-points'
    ];
    
    for (const dir of dirs) {
      try {
        await fs.mkdir(dir, { recursive: true });
      } catch (error) {
        if (error.code !== 'EEXIST') throw error;
      }
    }
  }

  setupGlobalErrorHandlers() {
    // Unhandled promise rejections
    process.on('unhandledRejection', (reason, promise) => {
      this.handleCriticalError('unhandled_rejection', reason, {
        promise: promise.toString(),
        stack: reason?.stack
      });
    });
    
    // Uncaught exceptions
    process.on('uncaughtException', (error) => {
      this.handleCriticalError('uncaught_exception', error, {
        stack: error.stack,
        fatal: true
      });
    });
    
    // Memory warnings
    process.on('warning', (warning) => {
      if (warning.name === 'MaxListenersExceededWarning') {
        this.handleError('memory_warning', warning, { severity: 'warning' });
      }
    });
    
    // Component error handlers
    this.setupComponentErrorHandlers();
  }

  setupComponentErrorHandlers() {
    // Proxy server errors
    if (this.components.proxyServer) {
      this.components.proxyServer.on('error', (error, context) => {
        this.handleError('proxy_server_error', error, { component: 'proxyServer', ...context });
      });
      
      this.components.proxyServer.on('client_error', (error, clientId) => {
        this.handleError('client_connection_error', error, { clientId, recoverable: true });
      });
    }
    
    // Cluster manager errors
    if (this.components.clusterManager) {
      this.components.clusterManager.on('error', (error, context) => {
        this.handleError('cluster_manager_error', error, { component: 'clusterManager', ...context });
      });
      
      this.components.clusterManager.on('cluster_failure', (error, clusterId) => {
        this.handleError('cluster_failure', error, { clusterId, recoverable: true });
      });
    }
    
    // Automation engine errors
    if (this.components.automationEngine) {
      this.components.automationEngine.on('error', (error, context) => {
        this.handleError('automation_error', error, { component: 'automationEngine', ...context });
      });
      
      this.components.automationEngine.on('task_failure', (error, taskId) => {
        this.handleError('task_execution_error', error, { taskId, recoverable: true });
      });
    }
    
    // Central node errors
    if (this.components.centralNode) {
      this.components.centralNode.on('error', (error, context) => {
        this.handleError('central_node_error', error, { component: 'centralNode', ...context });
      });
    }
    
    // Configuration manager errors
    if (this.components.configManager) {
      this.components.configManager.on('configuration_error', ({ category, error }) => {
        this.handleError('configuration_error', error, { category, recoverable: true });
      });
    }
    
    // Deployment manager errors
    if (this.components.deploymentManager) {
      this.components.deploymentManager.on('deployment_failed', ({ environment, error }) => {
        this.handleError('deployment_failure', error, { environment, critical: true });
      });
    }
  }

  initializeRecoveryStrategies() {
    // Restart component strategy
    this.recoveryStrategies.set('restart_component', async (error, context) => {
      if (!context.component || !this.components[context.component]) {
        return { success: false, reason: 'Component not found' };
      }
      
      const component = this.components[context.component];
      
      try {
        // Stop component
        if (component.stop && typeof component.stop === 'function') {
          await component.stop();
        }
        
        // Wait before restart
        await this.sleep(2000);
        
        // Start component
        if (component.start && typeof component.start === 'function') {
          await component.start();
        }
        
        return { success: true, action: 'component_restarted' };
      } catch (restartError) {
        return { success: false, reason: restartError.message };
      }
    });
    
    // Reconnect client strategy
    this.recoveryStrategies.set('reconnect_client', async (error, context) => {
      if (!context.clientId || !this.components.proxyServer) {
        return { success: false, reason: 'Client ID or proxy server not available' };
      }
      
      try {
        // Attempt to reconnect the client
        await this.components.proxyServer.reconnectClient(context.clientId);
        return { success: true, action: 'client_reconnected' };
      } catch (reconnectError) {
        return { success: false, reason: reconnectError.message };
      }
    });
    
    // Retry task strategy
    this.recoveryStrategies.set('retry_task', async (error, context) => {
      if (!context.taskId || !this.components.automationEngine) {
        return { success: false, reason: 'Task ID or automation engine not available' };
      }
      
      try {
        // Retry the failed task
        await this.components.automationEngine.retryTask(context.taskId);
        return { success: true, action: 'task_retried' };
      } catch (retryError) {
        return { success: false, reason: retryError.message };
      }
    });
    
    // Rebuild cluster strategy
    this.recoveryStrategies.set('rebuild_cluster', async (error, context) => {
      if (!context.clusterId || !this.components.clusterManager) {
        return { success: false, reason: 'Cluster ID or cluster manager not available' };
      }
      
      try {
        // Rebuild the failed cluster
        await this.components.clusterManager.rebuildCluster(context.clusterId);
        return { success: true, action: 'cluster_rebuilt' };
      } catch (rebuildError) {
        return { success: false, reason: rebuildError.message };
      }
    });
    
    // Reset configuration strategy
    this.recoveryStrategies.set('reset_configuration', async (error, context) => {
      if (!context.category || !this.components.configManager) {
        return { success: false, reason: 'Configuration category not specified' };
      }
      
      try {
        // Reset to default configuration
        const defaultConfig = this.components.configManager.getDefaultConfiguration(context.category);
        await this.components.configManager.setConfiguration(context.category, defaultConfig, 'error_recovery');
        
        return { success: true, action: 'configuration_reset' };
      } catch (resetError) {
        return { success: false, reason: resetError.message };
      }
    });
    
    // Rollback deployment strategy
    this.recoveryStrategies.set('rollback_deployment', async (error, context) => {
      if (!this.components.deploymentManager) {
        return { success: false, reason: 'Deployment manager not available' };
      }
      
      try {
        // Execute rollback
        await this.components.deploymentManager.rollback();
        return { success: true, action: 'deployment_rolled_back' };
      } catch (rollbackError) {
        return { success: false, reason: rollbackError.message };
      }
    });
  }

  // ==========================================
  // ERROR HANDLING
  // ==========================================

  async handleError(type, error, context = {}) {
    const errorId = uuidv4();
    const timestamp = Date.now();
    
    const errorRecord = {
      id: errorId,
      type: type,
      message: error.message || error.toString(),
      stack: error.stack,
      timestamp: timestamp,
      context: context,
      severity: this.determineSeverity(type, error, context),
      recoverable: context.recoverable !== false,
      attempts: 0,
      recovered: false,
      recoveryTime: null
    };
    
    // Store error
    this.errors.set(errorId, errorRecord);
    this.errorHistory.push(errorRecord);
    this.stats.totalErrors++;
    
    // Update error patterns
    this.updateErrorPatterns(type, errorRecord);
    
    // Log error
    this.logError(errorRecord);
    
    // Emit error event
    this.emit('error_occurred', errorRecord);
    
    // Attempt recovery if enabled and error is recoverable
    if (this.errorConfig.autoRecoveryEnabled && errorRecord.recoverable) {
      await this.attemptRecovery(errorId);
    }
    
    // Save error to file for persistence
    await this.persistError(errorRecord);
    
    return errorId;
  }

  async handleCriticalError(type, error, context = {}) {
    this.stats.criticalErrors++;
    
    // Create backup before handling critical error
    await this.createEmergencyBackup();
    
    const errorId = await this.handleError(type, error, { 
      ...context, 
      severity: 'critical',
      recoverable: context.fatal !== true
    });
    
    this.emit('critical_error', { errorId, type, error, context });
    
    // If fatal, initiate graceful shutdown
    if (context.fatal) {
      this.logger.error('Fatal error detected, initiating graceful shutdown...');
      this.emit('fatal_error', { errorId, type, error });
      
      // Give components time to cleanup
      setTimeout(() => {
        process.exit(1);
      }, 10000);
    }
    
    return errorId;
  }

  determineSeverity(type, error, context) {
    if (context.severity) return context.severity;
    if (context.critical) return 'critical';
    if (context.fatal) return 'fatal';
    
    // Determine severity based on error type
    const criticalTypes = [
      'uncaught_exception',
      'unhandled_rejection',
      'deployment_failure',
      'system_failure'
    ];
    
    if (criticalTypes.includes(type)) return 'critical';
    
    const warningTypes = [
      'memory_warning',
      'performance_degradation',
      'configuration_error'
    ];
    
    if (warningTypes.includes(type)) return 'warning';
    
    return 'error';
  }

  logError(errorRecord) {
    const { id, type, message, severity, context } = errorRecord;
    
    const logMethod = severity === 'critical' || severity === 'fatal' ? 'error' :
                     severity === 'warning' ? 'warn' : 'error';
    
    this.logger[logMethod](`[${id}] ${type}: ${message}`, {
      severity,
      context,
      recoverable: errorRecord.recoverable
    });
  }

  updateErrorPatterns(type, errorRecord) {
    const now = Date.now();
    const windowStart = now - this.errorConfig.patternAnalysisWindow;
    
    // Initialize pattern tracking for this type
    if (!this.errorPatterns.has(type)) {
      this.errorPatterns.set(type, {
        occurrences: [],
        totalCount: 0,
        frequency: 0,
        lastOccurrence: null
      });
    }
    
    const pattern = this.errorPatterns.get(type);
    
    // Add current occurrence
    pattern.occurrences.push(now);
    pattern.totalCount++;
    pattern.lastOccurrence = now;
    
    // Remove old occurrences outside the window
    pattern.occurrences = pattern.occurrences.filter(time => time >= windowStart);
    
    // Calculate frequency (occurrences per hour)
    pattern.frequency = (pattern.occurrences.length / this.errorConfig.patternAnalysisWindow) * 3600000;
    
    // Detect high frequency patterns
    if (pattern.frequency > 10) { // More than 10 per hour
      this.emit('error_pattern_detected', {
        type,
        frequency: pattern.frequency,
        recentOccurrences: pattern.occurrences.length,
        pattern
      });
    }
  }

  // ==========================================
  // RECOVERY MECHANISMS
  // ==========================================

  async attemptRecovery(errorId) {
    const errorRecord = this.errors.get(errorId);
    if (!errorRecord || !errorRecord.recoverable) return;
    
    const recoveryStartTime = Date.now();
    errorRecord.attempts++;
    
    this.logger.info(`Attempting recovery for error ${errorId} (attempt ${errorRecord.attempts})`);
    
    try {
      // Determine recovery strategy
      const strategy = this.selectRecoveryStrategy(errorRecord);
      
      if (!strategy) {
        this.logger.warn(`No recovery strategy found for error type: ${errorRecord.type}`);
        return;
      }
      
      // Calculate retry delay with exponential backoff
      const delay = Math.min(
        this.errorConfig.initialRetryDelay * Math.pow(this.errorConfig.retryBackoffMultiplier, errorRecord.attempts - 1),
        this.errorConfig.maxRetryDelay
      );
      
      await this.sleep(delay);
      
      // Execute recovery strategy
      const recoveryResult = await this.executeRecoveryStrategy(strategy, errorRecord);
      
      if (recoveryResult.success) {
        // Recovery successful
        errorRecord.recovered = true;
        errorRecord.recoveryTime = Date.now() - recoveryStartTime;
        
        this.stats.recoveredErrors++;
        this.stats.averageRecoveryTime = this.calculateAverageRecoveryTime();
        
        this.logger.info(`Recovery successful for error ${errorId}: ${recoveryResult.action}`);
        this.emit('recovery_successful', { errorId, errorRecord, recoveryResult });
        
      } else {
        // Recovery failed
        this.logger.warn(`Recovery failed for error ${errorId}: ${recoveryResult.reason}`);
        
        // Retry if we haven't exceeded max attempts
        if (errorRecord.attempts < this.errorConfig.maxRetryAttempts) {
          setTimeout(() => this.attemptRecovery(errorId), delay);
        } else {
          this.stats.failedRecoveries++;
          this.logger.error(`Max recovery attempts exceeded for error ${errorId}`);
          this.emit('recovery_failed', { errorId, errorRecord });
        }
      }
      
    } catch (recoveryError) {
      this.logger.error(`Recovery attempt failed for error ${errorId}:`, recoveryError);
      this.stats.failedRecoveries++;
    }
  }

  selectRecoveryStrategy(errorRecord) {
    const { type, context } = errorRecord;
    
    // Strategy selection based on error type and context
    const strategyMap = {
      'proxy_server_error': 'restart_component',
      'cluster_manager_error': 'restart_component',
      'automation_error': 'restart_component',
      'central_node_error': 'restart_component',
      'client_connection_error': 'reconnect_client',
      'task_execution_error': 'retry_task',
      'cluster_failure': 'rebuild_cluster',
      'configuration_error': 'reset_configuration',
      'deployment_failure': 'rollback_deployment'
    };
    
    const strategyName = strategyMap[type];
    if (strategyName && this.recoveryStrategies.has(strategyName)) {
      return {
        name: strategyName,
        handler: this.recoveryStrategies.get(strategyName)
      };
    }
    
    // Default strategy based on component
    if (context.component && this.components[context.component]) {
      return {
        name: 'restart_component',
        handler: this.recoveryStrategies.get('restart_component')
      };
    }
    
    return null;
  }

  async executeRecoveryStrategy(strategy, errorRecord) {
    try {
      return await strategy.handler(errorRecord, errorRecord.context);
    } catch (error) {
      return {
        success: false,
        reason: `Strategy execution failed: ${error.message}`
      };
    }
  }

  // ==========================================
  // BACKUP AND ROLLBACK
  // ==========================================

  async createBackup(label = null) {
    const backupId = uuidv4();
    const timestamp = Date.now();
    
    this.logger.info(`Creating system backup: ${backupId}`);
    
    const backupData = {
      id: backupId,
      timestamp: timestamp,
      label: label || `Backup-${new Date().toISOString()}`,
      states: {},
      configurations: {}
    };
    
    try {
      // Backup component states
      for (const [name, component] of Object.entries(this.components)) {
        if (component.getState && typeof component.getState === 'function') {
          backupData.states[name] = await component.getState();
        }
      }
      
      // Backup configurations
      if (this.components.configManager) {
        const categories = this.components.configManager.categories;
        for (const category of Object.values(categories)) {
          backupData.configurations[category] = this.components.configManager.getConfiguration(category);
        }
      }
      
      // Save backup to file
      const backupPath = path.join('./backups/states', `backup-${backupId}.json`);
      await fs.writeFile(backupPath, JSON.stringify(backupData, null, 2));
      
      // Store backup reference
      this.backupStates.set(backupId, {
        ...backupData,
        path: backupPath
      });
      
      this.logger.info(`Backup created successfully: ${backupId}`);
      this.emit('backup_created', { backupId, backupData });
      
      // Cleanup old backups
      await this.cleanupOldBackups();
      
      return backupId;
      
    } catch (error) {
      this.logger.error('Failed to create backup:', error);
      throw error;
    }
  }

  async createEmergencyBackup() {
    try {
      const backupId = await this.createBackup('Emergency-Backup');
      this.logger.info(`Emergency backup created: ${backupId}`);
      return backupId;
    } catch (error) {
      this.logger.error('Failed to create emergency backup:', error);
    }
  }

  async restoreFromBackup(backupId) {
    const backup = this.backupStates.get(backupId);
    if (!backup) {
      throw new Error(`Backup ${backupId} not found`);
    }
    
    this.logger.info(`Restoring from backup: ${backupId}`);
    
    try {
      // Restore configurations first
      if (this.components.configManager && backup.configurations) {
        for (const [category, config] of Object.entries(backup.configurations)) {
          await this.components.configManager.setConfiguration(category, config, 'backup_restore');
        }
      }
      
      // Restore component states
      for (const [name, state] of Object.entries(backup.states)) {
        const component = this.components[name];
        if (component && component.restoreState && typeof component.restoreState === 'function') {
          await component.restoreState(state);
        }
      }
      
      this.logger.info(`Backup restored successfully: ${backupId}`);
      this.emit('backup_restored', { backupId, backup });
      
      return true;
      
    } catch (error) {
      this.logger.error(`Failed to restore backup ${backupId}:`, error);
      throw error;
    }
  }

  async createRollbackPoint(label) {
    const rollbackId = await this.createBackup(`Rollback-${label}`);
    
    this.rollbackPoints.set(label, {
      id: rollbackId,
      timestamp: Date.now(),
      label: label
    });
    
    this.logger.info(`Rollback point created: ${label} (${rollbackId})`);
    return rollbackId;
  }

  async rollback(label) {
    const rollbackPoint = this.rollbackPoints.get(label);
    if (!rollbackPoint) {
      throw new Error(`Rollback point ${label} not found`);
    }
    
    this.logger.info(`Rolling back to: ${label}`);
    this.stats.rollbacksExecuted++;
    
    try {
      await this.restoreFromBackup(rollbackPoint.id);
      this.emit('rollback_completed', { label, rollbackPoint });
      return true;
    } catch (error) {
      this.logger.error(`Rollback failed for ${label}:`, error);
      this.emit('rollback_failed', { label, error });
      throw error;
    }
  }

  async cleanupOldBackups() {
    const cutoffTime = Date.now() - (this.errorConfig.backupRetentionDays * 24 * 60 * 60 * 1000);
    
    const backupsToDelete = [];
    for (const [id, backup] of this.backupStates.entries()) {
      if (backup.timestamp < cutoffTime) {
        backupsToDelete.push(id);
      }
    }
    
    for (const backupId of backupsToDelete) {
      const backup = this.backupStates.get(backupId);
      try {
        await fs.unlink(backup.path);
        this.backupStates.delete(backupId);
        this.logger.debug(`Deleted old backup: ${backupId}`);
      } catch (error) {
        this.logger.warn(`Failed to delete backup ${backupId}:`, error.message);
      }
    }
    
    if (backupsToDelete.length > 0) {
      this.logger.info(`Cleaned up ${backupsToDelete.length} old backups`);
    }
  }

  // ==========================================
  // PERIODIC TASKS
  // ==========================================

  startPeriodicTasks() {
    // Error pattern analysis every 5 minutes
    setInterval(() => {
      this.analyzeErrorPatterns();
    }, 300000);
    
    // Cleanup old errors every hour
    setInterval(() => {
      this.cleanupOldErrors();
    }, 3600000);
    
    // Create periodic backups every 6 hours
    setInterval(() => {
      this.createBackup('Periodic-Backup');
    }, 21600000);
    
    // Health check every 30 seconds
    setInterval(() => {
      this.performHealthCheck();
    }, 30000);
  }

  analyzeErrorPatterns() {
    for (const [type, pattern] of this.errorPatterns.entries()) {
      if (pattern.frequency > 5) { // More than 5 per hour
        this.logger.warn(`High error frequency detected for ${type}: ${pattern.frequency.toFixed(1)}/hour`);
        
        // Suggest preventive measures
        this.suggestPreventiveMeasures(type, pattern);
      }
    }
  }

  suggestPreventiveMeasures(errorType, pattern) {
    const suggestions = {
      'client_connection_error': 'Consider increasing connection timeout or implementing connection pooling',
      'task_execution_error': 'Review task parameters and consider resource allocation',
      'cluster_failure': 'Check cluster health and consider load balancing',
      'memory_warning': 'Monitor memory usage and consider increasing heap size',
      'configuration_error': 'Validate configuration files and implement stricter validation'
    };
    
    const suggestion = suggestions[errorType];
    if (suggestion) {
      this.logger.info(`Preventive suggestion for ${errorType}: ${suggestion}`);
      this.emit('preventive_suggestion', { errorType, suggestion, pattern });
    }
  }

  cleanupOldErrors() {
    const cutoffTime = Date.now() - (24 * 60 * 60 * 1000); // 24 hours
    const oldErrors = this.errorHistory.filter(error => error.timestamp < cutoffTime);
    
    if (oldErrors.length > 0) {
      this.errorHistory = this.errorHistory.filter(error => error.timestamp >= cutoffTime);
      
      // Also cleanup from main errors map
      for (const error of oldErrors) {
        this.errors.delete(error.id);
      }
      
      this.logger.debug(`Cleaned up ${oldErrors.length} old error records`);
    }
    
    // Limit error history size
    if (this.errorHistory.length > this.errorConfig.errorHistoryLimit) {
      const excess = this.errorHistory.length - this.errorConfig.errorHistoryLimit;
      this.errorHistory.splice(0, excess);
      this.logger.debug(`Trimmed ${excess} oldest error records`);
    }
  }

  performHealthCheck() {
    const healthStatus = {
      timestamp: Date.now(),
      errorRecoverySystem: 'healthy',
      components: {},
      recentErrors: this.errorHistory.filter(e => Date.now() - e.timestamp < 300000).length,
      recoveryRate: this.stats.totalErrors > 0 ? (this.stats.recoveredErrors / this.stats.totalErrors) : 1
    };
    
    // Check component health
    for (const [name, component] of Object.entries(this.components)) {
      if (component.getHealth && typeof component.getHealth === 'function') {
        try {
          healthStatus.components[name] = component.getHealth();
        } catch (error) {
          healthStatus.components[name] = 'unhealthy';
        }
      }
    }
    
    // Determine overall health
    if (healthStatus.recentErrors > 10) {
      healthStatus.errorRecoverySystem = 'degraded';
    }
    
    if (healthStatus.recoveryRate < 0.8) {
      healthStatus.errorRecoverySystem = 'warning';
    }
    
    this.emit('health_check', healthStatus);
  }

  // ==========================================
  // UTILITIES AND STATUS
  // ==========================================

  calculateAverageRecoveryTime() {
    const recoveredErrors = this.errorHistory.filter(e => e.recovered && e.recoveryTime);
    if (recoveredErrors.length === 0) return 0;
    
    const totalTime = recoveredErrors.reduce((sum, error) => sum + error.recoveryTime, 0);
    return totalTime / recoveredErrors.length;
  }

  async persistError(errorRecord) {
    try {
      const errorLogPath = path.join('./logs/errors', `${errorRecord.timestamp}-${errorRecord.id}.json`);
      await fs.writeFile(errorLogPath, JSON.stringify(errorRecord, null, 2));
    } catch (error) {
      this.logger.warn('Failed to persist error to file:', error.message);
    }
  }

  async loadErrorHistory() {
    try {
      const errorFiles = await fs.readdir('./logs/errors');
      const jsonFiles = errorFiles.filter(file => file.endsWith('.json'));
      
      for (const file of jsonFiles.slice(-100)) { // Load last 100 error files
        try {
          const errorData = await fs.readFile(path.join('./logs/errors', file), 'utf8');
          const errorRecord = JSON.parse(errorData);
          this.errorHistory.push(errorRecord);
        } catch (error) {
          this.logger.debug(`Failed to load error file ${file}:`, error.message);
        }
      }
      
      this.logger.info(`Loaded ${this.errorHistory.length} historical error records`);
    } catch (error) {
      this.logger.debug('Error history directory not found, starting fresh');
    }
  }

  sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  getErrorStatistics() {
    return {
      ...this.stats,
      recentErrors: this.errorHistory.filter(e => Date.now() - e.timestamp < 3600000).length,
      errorTypes: Object.fromEntries(this.errorPatterns.entries()),
      backupsAvailable: this.backupStates.size,
      rollbackPoints: Array.from(this.rollbackPoints.keys())
    };
  }

  getSystemHealth() {
    const recentErrors = this.errorHistory.filter(e => Date.now() - e.timestamp < 300000).length;
    const recoveryRate = this.stats.totalErrors > 0 ? (this.stats.recoveredErrors / this.stats.totalErrors) : 1;
    
    let healthStatus = 'healthy';
    if (recentErrors > 10) healthStatus = 'degraded';
    if (recentErrors > 25) healthStatus = 'unhealthy';
    if (recoveryRate < 0.5) healthStatus = 'critical';
    
    return {
      status: healthStatus,
      recentErrors,
      recoveryRate,
      averageRecoveryTime: this.stats.averageRecoveryTime,
      criticalErrors: this.stats.criticalErrors,
      lastBackup: this.backupStates.size > 0 ? Math.max(...Array.from(this.backupStates.values()).map(b => b.timestamp)) : null
    };
  }
}

module.exports = ErrorRecoverySystem;