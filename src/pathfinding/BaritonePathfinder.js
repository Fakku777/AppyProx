/**
 * BaritonePathfinder - Advanced pathfinding and movement automation
 * Integrates mineflayer-pathfinder for sophisticated navigation and goal-oriented movement
 */

const EventEmitter = require('events');
const { pathfinder, Movements, goals } = require('mineflayer-pathfinder');

class BaritonePathfinder extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      // Pathfinding settings
      allowBreak: options.allowBreak !== false,
      allowPlace: options.allowPlace !== false,
      allowParkour: options.allowParkour !== false,
      allowSprint: options.allowSprint !== false,
      allowEntityPush: options.allowEntityPush !== false,
      
      // Movement settings
      canDig: options.canDig !== false,
      canPlaceOn: options.canPlaceOn || [],
      blocksCantBreak: options.blocksCantBreak || [],
      blocksToAvoid: options.blocksToAvoid || [],
      
      // Performance settings
      maxDropDown: options.maxDropDown || 256,
      maxCost: options.maxCost || 1000000,
      timeout: options.timeout || 60000,
      searchRadius: options.searchRadius || 64,
      
      // Goal settings
      goalReachedDistance: options.goalReachedDistance || 1,
      precisionThreshold: options.precisionThreshold || 0.1,
      
      ...options.config
    };
    
    this.logger = options.logger || console;
    this.bot = null;
    this.movements = null;
    this.isInitialized = false;
    
    // State tracking
    this.currentGoal = null;
    this.isPathfinding = false;
    this.pathHistory = [];
    this.goalQueue = [];
    this.isProcessingQueue = false;
    
    // Performance tracking
    this.stats = {
      pathsCalculated: 0,
      pathsCompleted: 0,
      pathsFailed: 0,
      totalDistance: 0,
      totalTime: 0,
      averageSpeed: 0
    };
  }
  
  /**
   * Initialize pathfinder with bot instance
   */
  async initialize(bot) {
    if (this.isInitialized && this.bot === bot) {
      return;
    }
    
    try {
      this.bot = bot;
      
      // Load pathfinder plugin
      if (!this.bot.pathfinder) {
        this.bot.loadPlugin(pathfinder);
      }
      
      // Initialize movements
      this.movements = new Movements(this.bot, this.bot.mcData || this.bot.registry);
      this.applyMovementSettings();
      
      // Setup event handlers
      this.setupEventHandlers();
      
      this.isInitialized = true;
      this.logger.info(`[${this.bot.username}] Baritone pathfinder initialized`);
      this.emit('initialized', { bot: this.bot.username });
      
    } catch (error) {
      this.logger.error(`[${this.bot.username}] Failed to initialize pathfinder:`, error.message);
      throw error;
    }
  }
  
  /**
   * Apply movement settings to pathfinder
   */
  applyMovementSettings() {
    if (!this.movements) return;
    
    // Basic movement capabilities
    this.movements.canDig = this.config.canDig;
    this.movements.allow1by1towers = this.config.allowPlace;
    this.movements.allowFreeMotion = this.config.allowParkour;
    this.movements.allowParkour = this.config.allowParkour;
    this.movements.allowSprinting = this.config.allowSprint;
    this.movements.allowEntityPush = this.config.allowEntityPush;
    
    // Block restrictions
    if (this.config.canPlaceOn.length > 0) {
      this.movements.canPlaceOn = new Set(this.config.canPlaceOn);
    }
    
    if (this.config.blocksCantBreak.length > 0) {
      this.movements.blocksCantBreak = new Set(this.config.blocksCantBreak);
    }
    
    if (this.config.blocksToAvoid.length > 0) {
      this.movements.blocksToAvoid = new Set(this.config.blocksToAvoid);
    }
    
    // Performance settings
    this.movements.maxDropDown = this.config.maxDropDown;
    
    // Set movements for pathfinder
    this.bot.pathfinder.setMovements(this.movements);
    
    this.logger.debug(`[${this.bot.username}] Applied movement settings`);
  }
  
  /**
   * Setup event handlers for pathfinder
   */
  setupEventHandlers() {
    if (!this.bot || !this.bot.pathfinder) return;
    
    this.bot.pathfinder.on('goalReached', (goal) => {
      this.isPathfinding = false;
      this.currentGoal = null;
      this.stats.pathsCompleted++;
      
      this.logger.info(`[${this.bot.username}] Goal reached: ${this.goalToString(goal)}`);
      this.emit('goalReached', { goal, bot: this.bot.username });
      
      // Process next goal in queue
      this.processGoalQueue();
    });
    
    this.bot.pathfinder.on('goalUpdated', (goal, dynamic) => {
      this.logger.debug(`[${this.bot.username}] Goal updated: ${this.goalToString(goal)}`);
      this.emit('goalUpdated', { goal, dynamic, bot: this.bot.username });
    });
    
    this.bot.pathfinder.on('pathPartFound', (path) => {
      this.logger.debug(`[${this.bot.username}] Partial path found: ${path.length} nodes`);
      this.emit('pathPartFound', { pathLength: path.length, bot: this.bot.username });
    });
    
    this.bot.pathfinder.on('pathFound', (path) => {
      const distance = this.calculatePathDistance(path);
      this.stats.pathsCalculated++;
      this.stats.totalDistance += distance;
      
      this.logger.info(`[${this.bot.username}] Path found: ${path.length} nodes, ${distance.toFixed(2)} blocks`);
      this.emit('pathFound', { 
        pathLength: path.length, 
        distance: distance,
        bot: this.bot.username 
      });
    });
    
    this.bot.pathfinder.on('pathTimeout', (results) => {
      this.isPathfinding = false;
      this.stats.pathsFailed++;
      
      this.logger.warn(`[${this.bot.username}] Path timeout - partial results: ${results.length} nodes`);
      this.emit('pathTimeout', { results, bot: this.bot.username });
      
      // Process next goal in queue
      this.processGoalQueue();
    });
    
    this.bot.pathfinder.on('pathReset', (reason) => {
      this.isPathfinding = false;
      
      this.logger.info(`[${this.bot.username}] Path reset: ${reason}`);
      this.emit('pathReset', { reason, bot: this.bot.username });
    });
    
    this.bot.pathfinder.on('stuckWarning', () => {
      this.logger.warn(`[${this.bot.username}] Bot appears to be stuck`);
      this.emit('stuckWarning', { bot: this.bot.username });
    });
  }
  
  /**
   * Move to a specific position
   */
  async moveTo(x, y, z, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    const goal = new goals.GoalBlock(x, y, z);
    return this.setGoal(goal, options);
  }
  
  /**
   * Move near a position within a specified range
   */
  async moveNear(x, y, z, range = 1, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    const goal = new goals.GoalNear(x, y, z, range);
    return this.setGoal(goal, options);
  }
  
  /**
   * Move to interact with a block
   */
  async moveToBlock(x, y, z, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    const goal = new goals.GoalLookAtBlock(x, y, z);
    return this.setGoal(goal, options);
  }
  
  /**
   * Follow an entity
   */
  async followEntity(entity, range = 3, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    if (!entity || !entity.position) {
      throw new Error('Invalid entity provided');
    }
    
    const goal = new goals.GoalFollow(entity, range);
    return this.setGoal(goal, { ...options, dynamic: true });
  }
  
  /**
   * Move to a specific Y level
   */
  async moveToY(y, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    const goal = new goals.GoalY(y);
    return this.setGoal(goal, options);
  }
  
  /**
   * Move to get line of sight to a position
   */
  async moveForLineOfSight(x, y, z, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    const goal = new goals.GoalLookAtBlock(x, y, z);
    return this.setGoal(goal, options);
  }
  
  /**
   * Move to avoid entities in an area
   */
  async moveToAvoidEntities(entities, safeDistance = 8, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    // Find a safe position away from entities
    const botPos = this.bot.entity.position;
    let bestPos = null;
    let maxDistance = 0;
    
    // Search in a radius around current position
    for (let dx = -safeDistance; dx <= safeDistance; dx++) {
      for (let dz = -safeDistance; dz <= safeDistance; dz++) {
        const testPos = {
          x: botPos.x + dx,
          y: botPos.y,
          z: botPos.z + dz
        };
        
        let minEntityDistance = Infinity;
        for (const entity of entities) {
          const dist = Math.sqrt(
            (testPos.x - entity.position.x) ** 2 + 
            (testPos.z - entity.position.z) ** 2
          );
          minEntityDistance = Math.min(minEntityDistance, dist);
        }
        
        if (minEntityDistance > maxDistance) {
          maxDistance = minEntityDistance;
          bestPos = testPos;
        }
      }
    }
    
    if (bestPos) {
      return this.moveTo(bestPos.x, bestPos.y, bestPos.z, options);
    } else {
      throw new Error('Could not find safe position');
    }
  }
  
  /**
   * Set a goal for pathfinding
   */
  async setGoal(goal, options = {}) {
    if (!this.isInitialized) {
      throw new Error('Pathfinder not initialized');
    }
    
    const startTime = Date.now();
    
    try {
      // Add to queue if already pathfinding and not forcing immediate execution
      if (this.isPathfinding && !options.immediate) {
        return this.addToGoalQueue(goal, options);
      }
      
      this.currentGoal = goal;
      this.isPathfinding = true;
      
      // Set pathfinding options
      const pathOptions = {
        timeout: options.timeout || this.config.timeout,
        searchRadius: options.searchRadius || this.config.searchRadius,
        ...options.pathOptions
      };
      
      this.logger.info(`[${this.bot.username}] Setting goal: ${this.goalToString(goal)}`);
      this.emit('goalSet', { goal, options, bot: this.bot.username });
      
      // Set the goal
      this.bot.pathfinder.setGoal(goal, options.dynamic || false);
      
      // Return promise that resolves when goal is reached or fails
      return new Promise((resolve, reject) => {
        const timeout = setTimeout(() => {
          this.bot.pathfinder.setGoal(null);
          reject(new Error(`Pathfinding timeout after ${pathOptions.timeout}ms`));
        }, pathOptions.timeout);
        
        const onGoalReached = (data) => {
          if (data.bot === this.bot.username) {
            clearTimeout(timeout);
            this.removeListener('goalReached', onGoalReached);
            this.removeListener('pathTimeout', onPathTimeout);
            this.removeListener('pathReset', onPathReset);
            
            const duration = Date.now() - startTime;
            this.stats.totalTime += duration;
            this.updateAverageSpeed();
            
            resolve({
              success: true,
              goal: data.goal,
              duration,
              finalPosition: this.bot.entity.position
            });
          }
        };
        
        const onPathTimeout = (data) => {
          if (data.bot === this.bot.username) {
            clearTimeout(timeout);
            this.removeListener('goalReached', onGoalReached);
            this.removeListener('pathTimeout', onPathTimeout);
            this.removeListener('pathReset', onPathReset);
            
            reject(new Error('Pathfinding timed out'));
          }
        };
        
        const onPathReset = (data) => {
          if (data.bot === this.bot.username && data.reason !== 'goal_reached') {
            clearTimeout(timeout);
            this.removeListener('goalReached', onGoalReached);
            this.removeListener('pathTimeout', onPathTimeout);
            this.removeListener('pathReset', onPathReset);
            
            reject(new Error(`Path was reset: ${data.reason}`));
          }
        };
        
        this.on('goalReached', onGoalReached);
        this.on('pathTimeout', onPathTimeout);
        this.on('pathReset', onPathReset);
      });
      
    } catch (error) {
      this.isPathfinding = false;
      this.currentGoal = null;
      this.stats.pathsFailed++;
      
      this.logger.error(`[${this.bot.username}] Failed to set goal:`, error.message);
      throw error;
    }
  }
  
  /**
   * Add goal to processing queue
   */
  addToGoalQueue(goal, options = {}) {
    const queueItem = {
      goal,
      options,
      timestamp: Date.now(),
      id: Math.random().toString(36).substr(2, 9)
    };
    
    // Add priority support
    if (options.priority === 'high') {
      this.goalQueue.unshift(queueItem);
    } else {
      this.goalQueue.push(queueItem);
    }
    
    this.logger.debug(`[${this.bot.username}] Added goal to queue (${this.goalQueue.length} pending)`);
    this.emit('goalQueued', { goal, queueLength: this.goalQueue.length, bot: this.bot.username });
    
    return new Promise((resolve, reject) => {
      queueItem.resolve = resolve;
      queueItem.reject = reject;
    });
  }
  
  /**
   * Process the goal queue
   */
  async processGoalQueue() {
    if (this.isProcessingQueue || this.goalQueue.length === 0 || this.isPathfinding) {
      return;
    }
    
    this.isProcessingQueue = true;
    
    while (this.goalQueue.length > 0) {
      const queueItem = this.goalQueue.shift();
      
      try {
        const result = await this.setGoal(queueItem.goal, { 
          ...queueItem.options, 
          immediate: true 
        });
        
        if (queueItem.resolve) {
          queueItem.resolve(result);
        }
      } catch (error) {
        if (queueItem.reject) {
          queueItem.reject(error);
        }
      }
    }
    
    this.isProcessingQueue = false;
  }
  
  /**
   * Stop current pathfinding
   */
  stop() {
    if (!this.isInitialized || !this.bot.pathfinder) {
      return;
    }
    
    this.bot.pathfinder.setGoal(null);
    this.isPathfinding = false;
    this.currentGoal = null;
    
    this.logger.info(`[${this.bot.username}] Pathfinding stopped`);
    this.emit('pathfindingStopped', { bot: this.bot.username });
  }
  
  /**
   * Clear goal queue
   */
  clearQueue() {
    const clearedCount = this.goalQueue.length;
    
    // Reject all pending promises
    for (const item of this.goalQueue) {
      if (item.reject) {
        item.reject(new Error('Goal queue cleared'));
      }
    }
    
    this.goalQueue = [];
    
    this.logger.info(`[${this.bot.username}] Cleared ${clearedCount} queued goals`);
    this.emit('queueCleared', { clearedCount, bot: this.bot.username });
  }
  
  /**
   * Check if bot is currently pathfinding
   */
  isMoving() {
    return this.isPathfinding;
  }
  
  /**
   * Get current goal
   */
  getCurrentGoal() {
    return this.currentGoal;
  }
  
  /**
   * Get queue status
   */
  getQueueStatus() {
    return {
      length: this.goalQueue.length,
      processing: this.isProcessingQueue,
      items: this.goalQueue.map(item => ({
        id: item.id,
        goal: this.goalToString(item.goal),
        timestamp: item.timestamp,
        priority: item.options.priority || 'normal'
      }))
    };
  }
  
  /**
   * Update movement configuration
   */
  updateConfig(newConfig) {
    this.config = { ...this.config, ...newConfig };
    
    if (this.movements) {
      this.applyMovementSettings();
      this.logger.info(`[${this.bot.username}] Updated pathfinding configuration`);
      this.emit('configUpdated', { config: this.config, bot: this.bot.username });
    }
  }
  
  /**
   * Get pathfinding statistics
   */
  getStats() {
    return {
      ...this.stats,
      currentlyPathfinding: this.isPathfinding,
      queueLength: this.goalQueue.length,
      successRate: this.stats.pathsCalculated > 0 ? 
        (this.stats.pathsCompleted / this.stats.pathsCalculated) : 0
    };
  }
  
  /**
   * Reset statistics
   */
  resetStats() {
    this.stats = {
      pathsCalculated: 0,
      pathsCompleted: 0,
      pathsFailed: 0,
      totalDistance: 0,
      totalTime: 0,
      averageSpeed: 0
    };
    
    this.logger.info(`[${this.bot.username}] Reset pathfinding statistics`);
    this.emit('statsReset', { bot: this.bot.username });
  }
  
  /**
   * Calculate path distance
   */
  calculatePathDistance(path) {
    let distance = 0;
    for (let i = 1; i < path.length; i++) {
      const prev = path[i - 1];
      const curr = path[i];
      distance += Math.sqrt(
        (curr.x - prev.x) ** 2 + 
        (curr.y - prev.y) ** 2 + 
        (curr.z - prev.z) ** 2
      );
    }
    return distance;
  }
  
  /**
   * Update average speed calculation
   */
  updateAverageSpeed() {
    if (this.stats.totalTime > 0 && this.stats.totalDistance > 0) {
      // Convert to blocks per second
      this.stats.averageSpeed = (this.stats.totalDistance / this.stats.totalTime) * 1000;
    }
  }
  
  /**
   * Convert goal to string for logging
   */
  goalToString(goal) {
    if (!goal) return 'null';
    
    if (goal.x !== undefined && goal.y !== undefined && goal.z !== undefined) {
      return `(${goal.x}, ${goal.y}, ${goal.z})`;
    } else if (goal.entity) {
      return `follow ${goal.entity.username || goal.entity.name || 'entity'}`;
    } else if (goal.y !== undefined) {
      return `Y=${goal.y}`;
    } else {
      return goal.constructor.name;
    }
  }
  
  /**
   * Get detailed status
   */
  getDetailedStatus() {
    return {
      initialized: this.isInitialized,
      pathfinding: this.isPathfinding,
      currentGoal: this.currentGoal ? this.goalToString(this.currentGoal) : null,
      queue: this.getQueueStatus(),
      stats: this.getStats(),
      config: this.config,
      bot: this.bot ? this.bot.username : null
    };
  }
  
  /**
   * Shutdown pathfinder
   */
  shutdown() {
    this.stop();
    this.clearQueue();
    this.removeAllListeners();
    
    this.isInitialized = false;
    this.bot = null;
    this.movements = null;
    
    this.logger.info('Baritone pathfinder shutdown complete');
  }
}

module.exports = BaritonePathfinder;