/**
 * PathfindingAutomation - Advanced pathfinding automation system
 * Integrates with task execution, resource gathering, and group coordination
 */

const EventEmitter = require('events');
const BaritonePathfinder = require('./BaritonePathfinder');

class PathfindingAutomation extends EventEmitter {
  constructor(options = {}) {
    super();
    
    this.config = {
      // Automation settings
      maxConcurrentPaths: options.maxConcurrentPaths || 5,
      coordinationMode: options.coordinationMode || 'spread', // 'spread', 'follow', 'formation'
      safetyDistance: options.safetyDistance || 2,
      regroupDistance: options.regroupDistance || 10,
      
      // Task-specific settings
      gatheringFormation: options.gatheringFormation || 'spread',
      buildingFormation: options.buildingFormation || 'circle',
      miningFormation: options.miningFormation || 'line',
      
      // Performance settings
      pathCacheSize: options.pathCacheSize || 100,
      coordinationUpdateInterval: options.coordinationUpdateInterval || 5000,
      
      ...options.config
    };
    
    this.logger = options.logger || console;
    
    // Pathfinder instances for each bot
    this.pathfinders = new Map(); // botId -> BaritonePathfinder
    this.botProxies = new Map(); // botId -> HeadlessMinecraftClient
    
    // Group coordination
    this.groups = new Map(); // groupId -> group info
    this.formations = new Map(); // groupId -> formation data
    
    // Path coordination
    this.pathCache = new Map(); // pathKey -> cached path data
    this.activeCoordination = new Map(); // coordinationId -> coordination task
    
    // Task integration
    this.taskPathMap = new Map(); // taskId -> pathfinding operations
    
    // Statistics
    this.stats = {
      totalPathsExecuted: 0,
      coordinatedMovements: 0,
      formationChanges: 0,
      averagePathTime: 0,
      collisionAvoidance: 0
    };
    
    // Start coordination loop
    this.startCoordinationLoop();
  }
  
  /**
   * Register a bot for pathfinding automation
   */
  async registerBot(botId, botProxy, pathfindingOptions = {}) {
    try {
      if (this.pathfinders.has(botId)) {
        this.logger.warn(`Bot ${botId} already registered for pathfinding`);
        return;
      }
      
      // Create pathfinder instance
      const pathfinder = new BaritonePathfinder({
        ...this.config,
        ...pathfindingOptions,
        logger: this.logger
      });
      
      // Initialize with bot
      await pathfinder.initialize(botProxy.bot);
      
      // Store references
      this.pathfinders.set(botId, pathfinder);
      this.botProxies.set(botId, botProxy);
      
      // Setup event forwarding
      pathfinder.on('goalReached', (data) => {
        this.handleBotGoalReached(botId, data);
      });
      
      pathfinder.on('pathTimeout', (data) => {
        this.handleBotPathTimeout(botId, data);
      });
      
      pathfinder.on('stuckWarning', (data) => {
        this.handleBotStuck(botId, data);
      });
      
      this.logger.info(`Registered bot ${botId} for pathfinding automation`);
      this.emit('botRegistered', { botId, pathfinder });
      
    } catch (error) {
      this.logger.error(`Failed to register bot ${botId}:`, error.message);
      throw error;
    }
  }
  
  /**
   * Unregister a bot from pathfinding automation
   */
  async unregisterBot(botId) {
    const pathfinder = this.pathfinders.get(botId);
    if (pathfinder) {
      pathfinder.shutdown();
      this.pathfinders.delete(botId);
    }
    
    this.botProxies.delete(botId);
    
    // Remove from any groups
    for (const [groupId, group] of this.groups) {
      const memberIndex = group.members.indexOf(botId);
      if (memberIndex > -1) {
        group.members.splice(memberIndex, 1);
        if (group.members.length === 0) {
          this.groups.delete(groupId);
          this.formations.delete(groupId);
        }
      }
    }
    
    this.logger.info(`Unregistered bot ${botId} from pathfinding automation`);
    this.emit('botUnregistered', { botId });
  }
  
  /**
   * Create a coordinated group for pathfinding
   */
  createGroup(groupId, memberBots, options = {}) {
    if (this.groups.has(groupId)) {
      throw new Error(`Group ${groupId} already exists`);
    }
    
    // Validate all bots are registered
    for (const botId of memberBots) {
      if (!this.pathfinders.has(botId)) {
        throw new Error(`Bot ${botId} is not registered for pathfinding`);
      }
    }
    
    const group = {
      id: groupId,
      members: [...memberBots],
      leader: options.leader || memberBots[0],
      formation: options.formation || this.config.coordinationMode,
      safetyDistance: options.safetyDistance || this.config.safetyDistance,
      currentTask: null,
      status: 'idle',
      created: Date.now()
    };
    
    this.groups.set(groupId, group);
    this.initializeFormation(groupId, group.formation);
    
    this.logger.info(`Created pathfinding group ${groupId} with ${memberBots.length} members`);
    this.emit('groupCreated', { group });
    
    return group;
  }
  
  /**
   * Initialize formation for a group
   */
  initializeFormation(groupId, formationType) {
    const group = this.groups.get(groupId);
    if (!group) return;
    
    const formation = {
      type: formationType,
      positions: this.calculateFormationPositions(group.members, formationType),
      spacing: this.config.safetyDistance,
      lastUpdate: Date.now()
    };
    
    this.formations.set(groupId, formation);
    this.logger.debug(`Initialized ${formationType} formation for group ${groupId}`);
  }
  
  /**
   * Calculate formation positions for group members
   */
  calculateFormationPositions(members, formationType) {
    const positions = new Map();
    const memberCount = members.length;
    const spacing = this.config.safetyDistance;
    
    switch (formationType) {
      case 'line':
        members.forEach((botId, index) => {
          positions.set(botId, {
            offsetX: index * spacing,
            offsetZ: 0,
            role: index === 0 ? 'leader' : 'follower'
          });
        });
        break;
        
      case 'spread':
        members.forEach((botId, index) => {
          const angle = (2 * Math.PI * index) / memberCount;
          const radius = spacing * Math.max(1, Math.floor(memberCount / 4));
          positions.set(botId, {
            offsetX: Math.cos(angle) * radius,
            offsetZ: Math.sin(angle) * radius,
            role: index === 0 ? 'leader' : 'follower'
          });
        });
        break;
        
      case 'circle':
        members.forEach((botId, index) => {
          const angle = (2 * Math.PI * index) / memberCount;
          const radius = spacing * 2;
          positions.set(botId, {
            offsetX: Math.cos(angle) * radius,
            offsetZ: Math.sin(angle) * radius,
            role: index === 0 ? 'center' : 'perimeter'
          });
        });
        break;
        
      case 'wedge':
        members.forEach((botId, index) => {
          const row = Math.floor(index / 2);
          const side = index % 2 === 0 ? -1 : 1;
          positions.set(botId, {
            offsetX: side * (row + 1) * spacing,
            offsetZ: -row * spacing,
            role: index === 0 ? 'leader' : 'follower'
          });
        });
        break;
        
      default:
        // Default to line formation
        members.forEach((botId, index) => {
          positions.set(botId, {
            offsetX: index * spacing,
            offsetZ: 0,
            role: index === 0 ? 'leader' : 'follower'
          });
        });
    }
    
    return positions;
  }
  
  /**
   * Execute coordinated movement for a group
   */
  async executeGroupMovement(groupId, destination, options = {}) {
    const group = this.groups.get(groupId);
    if (!group) {
      throw new Error(`Group ${groupId} not found`);
    }
    
    const formation = this.formations.get(groupId);
    if (!formation) {
      throw new Error(`Formation not found for group ${groupId}`);
    }
    
    try {
      group.status = 'moving';
      this.stats.coordinatedMovements++;
      
      const movePromises = [];
      const leader = group.leader;
      const leaderPathfinder = this.pathfinders.get(leader);
      
      // Move leader first
      if (leaderPathfinder) {
        const leaderPromise = leaderPathfinder.moveTo(
          destination.x,
          destination.y,
          destination.z,
          { priority: 'high', ...options }
        );
        movePromises.push({ botId: leader, promise: leaderPromise });
      }
      
      // Move followers to formation positions relative to destination
      for (const botId of group.members) {
        if (botId === leader) continue;
        
        const pathfinder = this.pathfinders.get(botId);
        const position = formation.positions.get(botId);
        
        if (pathfinder && position) {
          const targetPos = {
            x: destination.x + position.offsetX,
            y: destination.y,
            z: destination.z + position.offsetZ
          };
          
          const followerPromise = pathfinder.moveTo(
            targetPos.x,
            targetPos.y,
            targetPos.z,
            { ...options, timeout: (options.timeout || 60000) + 5000 } // Extra time for followers
          );
          
          movePromises.push({ botId, promise: followerPromise });
        }
      }
      
      // Wait for all movements to complete
      const results = await Promise.allSettled(
        movePromises.map(({ botId, promise }) => 
          promise.then(result => ({ botId, result }))
            .catch(error => ({ botId, error }))
        )
      );
      
      // Process results
      const successful = [];
      const failed = [];
      
      for (const result of results) {
        if (result.status === 'fulfilled') {
          if (result.value.error) {
            failed.push(result.value);
          } else {
            successful.push(result.value);
          }
        } else {
          failed.push({ botId: 'unknown', error: result.reason });
        }
      }
      
      group.status = 'idle';
      
      const movementResult = {
        success: failed.length === 0,
        groupId,
        destination,
        successful: successful.length,
        failed: failed.length,
        details: { successful, failed }
      };
      
      this.logger.info(`Group movement completed: ${successful.length}/${group.members.length} successful`);
      this.emit('groupMovementCompleted', movementResult);
      
      return movementResult;
      
    } catch (error) {
      group.status = 'error';
      this.logger.error(`Group movement failed for ${groupId}:`, error.message);
      throw error;
    }
  }
