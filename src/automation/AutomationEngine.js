const EventEmitter = require('events');
const { v4: uuidv4 } = require('uuid');
const WikiScraper = require('./WikiScraper');
const TaskPlanner = require('./TaskPlanner');
const BaritoneInterface = require('./BaritoneInterface');
const RecipeAnalyzer = require('./RecipeAnalyzer');
const LitematicaManager = require('./LitematicaManager');
const TaskAI = require('../ai/TaskAI');

/**
 * Intelligent automation engine that uses Minecraft Wiki data to plan and execute tasks
 */
class AutomationEngine extends EventEmitter {
  constructor(config, logger) {
    super();
    this.config = config;
    this.logger = logger.child ? logger.child('AutomationEngine') : logger;
    
    // Core components
    this.wikiScraper = new WikiScraper(config, this.logger);
    this.taskPlanner = new TaskPlanner(config, this.logger);
    this.baritone = new BaritoneInterface(config, this.logger);
    this.recipeAnalyzer = new RecipeAnalyzer(this.wikiScraper, this.logger);
    this.litematicaManager = new LitematicaManager(config, this.logger);
    this.taskAI = new TaskAI(this.logger);
    
    // Task management
    this.activeTasks = new Map(); // taskId -> task info
    this.taskQueue = [];
    this.clusterStatus = new Map(); // clusterId -> status
    
    // Performance tracking
    this.stats = {
      totalTasks: 0,
      completedTasks: 0,
      failedTasks: 0,
      totalResourcesGathered: new Map(),
      averageTaskTime: 0,
      startTime: null
    };
    
    this.isRunning = false;
    this.processingInterval = null;
  }

  async start() {
    if (this.isRunning) return;

    this.logger.info('Starting automation engine...');
    
    try {
      // Initialize components
      await this.wikiScraper.initialize();
      await this.taskPlanner.initialize();
      await this.baritone.initialize();
      await this.litematicaManager.initialize();
      // TaskAI doesn't need initialization
      
      // Start task processing loop
      this.processingInterval = setInterval(() => {
        this.processTaskQueue();
      }, 1000);
      
      this.isRunning = true;
      this.stats.startTime = Date.now();
      
      this.logger.info('Automation engine started successfully');
    } catch (error) {
      this.logger.error('Failed to start automation engine:', error);
      throw error;
    }
  }

  async stop() {
    if (!this.isRunning) return;

    this.logger.info('Stopping automation engine...');
    
    if (this.processingInterval) {
      clearInterval(this.processingInterval);
      this.processingInterval = null;
    }
    
    // Cancel all active tasks
    for (const [taskId, task] of this.activeTasks) {
      await this.cancelTask(taskId, 'Engine shutting down');
    }
    
    this.isRunning = false;
    this.logger.info('Automation engine stopped');
  }

  updateClusterStatus(clusterData) {
    this.clusterStatus.set(clusterData.id, {
      status: clusterData.status,
      memberCount: clusterData.members ? clusterData.members.length : 0,
      lastUpdate: Date.now(),
      currentTask: clusterData.currentTask
    });
  }

  /**
   * Convert natural language into structured task definitions
   * @param {string} naturalLanguage - Natural language task description
   * @param {Object} options - Additional options for task conversion
   * @returns {Object} Structured task definition
   */
  async convertNaturalLanguageTask(naturalLanguage, options = {}) {
    this.logger.info(`Converting natural language task: ${naturalLanguage}`);
    
    try {
      const result = await this.taskAI.convertToTasks(naturalLanguage, options);
      
      if (!result.success) {
        this.logger.warn(`Failed to convert natural language task: ${result.error}`);
        return result;
      }
      
      this.logger.info(`Successfully converted natural language to ${result.tasks.length} structured tasks`);
      return result;
    } catch (error) {
      this.logger.error('Error converting natural language task:', error);
      throw error;
    }
  }
  
  /**
   * Create a task from natural language description
   * @param {string} naturalLanguage - Natural language task description
   * @param {Object} options - Additional options
   * @param {string} assignedCluster - Cluster ID to assign the task to
   * @returns {string} Created task ID
   */
  async createTaskFromNaturalLanguage(naturalLanguage, options = {}, assignedCluster = null) {
    const taskId = uuidv4();
    
    this.logger.info(`Creating task from natural language: "${naturalLanguage}"`, { taskId });

    try {
      // Convert natural language to structured tasks
      const conversionResult = await this.convertNaturalLanguageTask(naturalLanguage, options);
      
      if (!conversionResult.success) {
        throw new Error(`Failed to parse natural language: ${conversionResult.error}`);
      }
      
      // Create a complex execution plan based on the AI-generated tasks
      const executionPlan = {
        type: 'natural_language',
        steps: conversionResult.tasks.map(task => ({
          action: task.action,
          parameters: task.parameters,
          estimatedTime: task.estimatedTime
        })),
        estimatedDuration: conversionResult.estimatedTime,
        resourceRequirements: conversionResult.resourceRequirements,
        recommendedGroupSize: conversionResult.groupRecommendation
      };
      
      const task = {
        id: taskId,
        type: 'natural_language',
        complexity: 'advanced',
        naturalLanguage: naturalLanguage,
        parameters: options,
        assignedCluster: assignedCluster || (conversionResult.groupRecommendation > 2 ? 'auto_assign' : null),
        executionPlan: executionPlan,
        status: 'pending',
        progress: 0,
        created: Date.now(),
        started: null,
        completed: null,
        error: null,
        estimatedDuration: conversionResult.estimatedTime || 600000, // 10 minutes default
        priority: options.priority || 7, // Fairly high priority
        results: {},
        aiGenerated: true
      };

      this.activeTasks.set(taskId, task);
      this.taskQueue.push(taskId);
      this.stats.totalTasks++;

      this.logger.info(`Natural language task created: ${naturalLanguage} (${taskId})`);
      this.emit('task_created', task);

      return taskId;
    } catch (error) {
      this.logger.error(`Failed to create task from natural language "${naturalLanguage}":`, error);
      throw error;
    }
  }
  
  /**
   * Create a complex task using advanced recipe analysis
   * Example: createComplexTask('gather', { item: 'diamond_block', quantity: 256 }, 'mining_cluster')
   */
  async createComplexTask(taskType, parameters, assignedCluster = null) {
    const taskId = uuidv4();
    
    this.logger.info(`Creating complex task: ${taskType}`, { taskId, parameters });

    try {
      let executionPlan;
      
      if (taskType === 'gather' && parameters.item && parameters.quantity) {
        // Use RecipeAnalyzer for complex gathering tasks
        const analysisResult = await this.recipeAnalyzer.analyzeComplexGoal(
          parameters.item, 
          parameters.quantity, 
          {
            maxClusterSize: 10,
            timeLimit: parameters.timeLimit || 7200000, // 2 hours default
            assignedCluster: assignedCluster
          }
        );
        
        executionPlan = {
          type: 'complex_analysis',
          steps: analysisResult.executionSteps,
          estimatedDuration: analysisResult.estimatedTime,
          resourceRequirements: analysisResult.resourceRequirements,
          optimizedPlan: analysisResult.recommendedPlan
        };
        
        this.logger.info(`Complex analysis complete for ${parameters.quantity}x ${parameters.item}:`, {
          totalMethods: analysisResult.totalMethods,
          estimatedTime: Math.round(analysisResult.estimatedTime / 1000) + 's',
          resourceTypes: Object.keys(analysisResult.resourceRequirements).length
        });
      } else {
        // Fall back to regular task planning
        executionPlan = await this.taskPlanner.createExecutionPlan(taskType, parameters);
      }

      const task = {
        id: taskId,
        type: taskType,
        complexity: 'advanced',
        parameters: parameters,
        assignedCluster: assignedCluster,
        executionPlan: executionPlan,
        status: 'pending',
        progress: 0,
        created: Date.now(),
        started: null,
        completed: null,
        error: null,
        estimatedDuration: executionPlan.estimatedDuration || 300000,
        priority: parameters.priority || 8, // Higher priority for complex tasks
        results: {}
      };

      this.activeTasks.set(taskId, task);
      this.taskQueue.push(taskId);
      this.stats.totalTasks++;

      this.logger.info(`Complex task created: ${taskType} (${taskId})`);
      this.emit('task_created', task);

      return taskId;
    } catch (error) {
      this.logger.error(`Failed to create complex task ${taskType}:`, error);
      throw error;
    }
  }

  async createTask(taskType, parameters, assignedCluster = null) {
    const taskId = uuidv4();
    
    this.logger.info(`Creating task: ${taskType}`, { taskId, parameters });

    try {
      // Use task planner to create detailed execution plan
      const executionPlan = await this.taskPlanner.createExecutionPlan(taskType, parameters);
      
      const task = {
        id: taskId,
        type: taskType,
        parameters: parameters,
        assignedCluster: assignedCluster,
        executionPlan: executionPlan,
        status: 'pending',
        progress: 0,
        created: Date.now(),
        started: null,
        completed: null,
        error: null,
        estimatedDuration: executionPlan.estimatedDuration || 300000, // 5 minutes default
        priority: parameters.priority || 5,
        results: {}
      };

      this.activeTasks.set(taskId, task);
      this.taskQueue.push(taskId);
      this.stats.totalTasks++;

      this.logger.info(`Task created: ${taskType} (${taskId})`);
      this.emit('task_created', task);

      return taskId;
    } catch (error) {
      this.logger.error(`Failed to create task ${taskType}:`, error);
      throw error;
    }
  }

  async executeTask(taskId) {
    const task = this.activeTasks.get(taskId);
    if (!task) {
      this.logger.warn(`Task not found: ${taskId}`);
      return false;
    }

    if (task.status !== 'pending') {
      this.logger.warn(`Task ${taskId} is not in pending state: ${task.status}`);
      return false;
    }

    this.logger.info(`Starting task execution: ${task.type} (${taskId})`);
    
    task.status = 'running';
    task.started = Date.now();
    this.emit('task_started', task);

    try {
      // Execute each step in the execution plan
      for (let i = 0; i < task.executionPlan.steps.length; i++) {
        const step = task.executionPlan.steps[i];
        
        this.logger.debug(`Executing step ${i + 1}/${task.executionPlan.steps.length}: ${step.action}`);
        
        const stepResult = await this.executeTaskStep(task, step);
        
        if (!stepResult.success) {
          throw new Error(`Step ${i + 1} failed: ${stepResult.error}`);
        }

        // Update progress
        task.progress = Math.round(((i + 1) / task.executionPlan.steps.length) * 100);
        this.emit('task_progress', { taskId, progress: task.progress, step: step });
      }

      // Task completed successfully
      task.status = 'completed';
      task.completed = Date.now();
      task.progress = 100;
      
      const duration = task.completed - task.started;
      this.updateTaskStatistics(task, duration);
      
      this.logger.info(`Task completed: ${task.type} (${taskId}) in ${duration}ms`);
      this.emit('task_completed', task);
      
      return true;

    } catch (error) {
      task.status = 'failed';
      task.error = error.message;
      task.completed = Date.now();
      
      this.stats.failedTasks++;
      this.logger.error(`Task failed: ${task.type} (${taskId}):`, error);
      this.emit('task_failed', { task, error });
      
      return false;
    }
  }

  async executeTaskStep(task, step) {
    try {
      switch (step.action) {
        case 'gather_resource':
          return await this.executeGatherResource(task, step);
        case 'craft_item':
          return await this.executeCraftItem(task, step);
        case 'move_to_location':
          return await this.executeMoveToLocation(task, step);
        case 'build_structure':
          return await this.executeBuildStructure(task, step);
        case 'mine_area':
          return await this.executeMineArea(task, step);
        default:
          throw new Error(`Unknown step action: ${step.action}`);
      }
    } catch (error) {
      return { success: false, error: error.message };
    }
  }

  async executeGatherResource(task, step) {
    const { resource, quantity, method } = step.parameters;
    
    this.logger.debug(`Gathering ${quantity}x ${resource} using method: ${method}`);
    
    // Get resource information from wiki
    const resourceInfo = await this.wikiScraper.getResourceInfo(resource);
    if (!resourceInfo) {
      throw new Error(`Resource information not found: ${resource}`);
    }

    // Find the most efficient gathering method
    const gatheringMethod = resourceInfo.gatheringMethods.find(m => m.method === method) 
      || resourceInfo.gatheringMethods[0];

    if (!gatheringMethod) {
      throw new Error(`No gathering method found for resource: ${resource}`);
    }

    // Execute gathering using Baritone
    const gatheringResult = await this.baritone.executeGathering({
      resource: resource,
      quantity: quantity,
      method: gatheringMethod,
      clusterId: task.assignedCluster
    });

    if (gatheringResult.success) {
      this.updateResourceStatistics(resource, gatheringResult.quantityGathered);
      task.results[resource] = (task.results[resource] || 0) + gatheringResult.quantityGathered;
    }

    return gatheringResult;
  }

  async executeCraftItem(task, step) {
    const { item, quantity, workstation } = step.parameters;
    
    this.logger.debug(`Crafting ${quantity}x ${item} at ${workstation}`);
    
    // Get crafting recipe from wiki
    const recipe = await this.wikiScraper.getCraftingRecipe(item);
    if (!recipe) {
      throw new Error(`Crafting recipe not found: ${item}`);
    }

    // Check if we have required materials
    const materialsCheck = await this.checkMaterials(task, recipe.materials, quantity);
    if (!materialsCheck.sufficient) {
      throw new Error(`Insufficient materials: ${materialsCheck.missing.join(', ')}`);
    }

    // Execute crafting using Baritone
    const craftingResult = await this.baritone.executeCrafting({
      item: item,
      quantity: quantity,
      recipe: recipe,
      workstation: workstation,
      clusterId: task.assignedCluster
    });

    return craftingResult;
  }

  async executeMoveToLocation(task, step) {
    const { location, precision = 1.0 } = step.parameters;
    
    this.logger.debug(`Moving to location: ${JSON.stringify(location)}`);
    
    const movementResult = await this.baritone.executeMovement({
      target: location,
      precision: precision,
      clusterId: task.assignedCluster
    });

    return movementResult;
  }

  async executeBuildStructure(task, step) {
    const { schematic, location, materials } = step.parameters;
    
    this.logger.debug(`Building structure: ${schematic} at ${JSON.stringify(location)}`);
    
    // Check materials availability
    const materialsCheck = await this.checkMaterials(task, materials, 1);
    if (!materialsCheck.sufficient) {
      throw new Error(`Insufficient building materials: ${materialsCheck.missing.join(', ')}`);
    }

    const buildResult = await this.baritone.executeBuild({
      schematic: schematic,
      location: location,
      materials: materials,
      clusterId: task.assignedCluster
    });

    return buildResult;
  }

  async executeMineArea(task, step) {
    const { area, depth, materials } = step.parameters;
    
    this.logger.debug(`Mining area: ${JSON.stringify(area)} to depth ${depth}`);
    
    const miningResult = await this.baritone.executeMining({
      area: area,
      depth: depth,
      targetMaterials: materials,
      clusterId: task.assignedCluster
    });

    if (miningResult.success && miningResult.materialsFound) {
      // Update statistics for found materials
      for (const [material, quantity] of Object.entries(miningResult.materialsFound)) {
        this.updateResourceStatistics(material, quantity);
        task.results[material] = (task.results[material] || 0) + quantity;
      }
    }

    return miningResult;
  }

  async checkMaterials(task, requiredMaterials, quantity) {
    // This would check cluster inventories in a real implementation
    // For now, return success for demonstration
    return {
      sufficient: true,
      missing: []
    };
  }

  async cancelTask(taskId, reason = 'Cancelled by user') {
    const task = this.activeTasks.get(taskId);
    if (!task) {
      this.logger.warn(`Cannot cancel non-existent task: ${taskId}`);
      return false;
    }

    this.logger.info(`Cancelling task: ${task.type} (${taskId}) - ${reason}`);
    
    task.status = 'cancelled';
    task.completed = Date.now();
    task.error = reason;

    // Remove from queue if pending
    const queueIndex = this.taskQueue.indexOf(taskId);
    if (queueIndex > -1) {
      this.taskQueue.splice(queueIndex, 1);
    }

    // Stop any active Baritone operations for this task
    if (task.assignedCluster) {
      await this.baritone.stopClusterOperations(task.assignedCluster);
    }

    this.emit('task_cancelled', { task, reason });
    return true;
  }

  processTaskQueue() {
    if (this.taskQueue.length === 0) return;

    // Process tasks based on priority and available clusters
    this.taskQueue.sort((a, b) => {
      const taskA = this.activeTasks.get(a);
      const taskB = this.activeTasks.get(b);
      return (taskB.priority || 0) - (taskA.priority || 0);
    });

    for (const taskId of this.taskQueue.slice()) {
      const task = this.activeTasks.get(taskId);
      if (!task || task.status !== 'pending') continue;

      // Check if assigned cluster is available
      if (task.assignedCluster) {
        const clusterStatus = this.clusterStatus.get(task.assignedCluster);
        if (!clusterStatus || clusterStatus.status !== 'idle') continue;
      }

      // Remove from queue and execute
      const queueIndex = this.taskQueue.indexOf(taskId);
      if (queueIndex > -1) {
        this.taskQueue.splice(queueIndex, 1);
      }

      // Execute task asynchronously
      this.executeTask(taskId).catch(error => {
        this.logger.error(`Task execution error for ${taskId}:`, error);
      });

      break; // Process one task at a time
    }
  }

  updateTaskStatistics(task, duration) {
    this.stats.completedTasks++;
    
    // Update average task time
    const totalTime = this.stats.averageTaskTime * (this.stats.completedTasks - 1) + duration;
    this.stats.averageTaskTime = Math.round(totalTime / this.stats.completedTasks);
  }

  updateResourceStatistics(resource, quantity) {
    const current = this.stats.totalResourcesGathered.get(resource) || 0;
    this.stats.totalResourcesGathered.set(resource, current + quantity);
  }

  getStatus() {
    return {
      isRunning: this.isRunning,
      activeTasks: this.activeTasks.size,
      queuedTasks: this.taskQueue.length,
      stats: {
        ...this.stats,
        totalResourcesGathered: Object.fromEntries(this.stats.totalResourcesGathered),
        uptime: this.stats.startTime ? Date.now() - this.stats.startTime : 0
      },
      tasks: Array.from(this.activeTasks.values()).map(task => ({
        id: task.id,
        type: task.type,
        status: task.status,
        progress: task.progress,
        assignedCluster: task.assignedCluster,
        created: task.created,
        estimatedDuration: task.estimatedDuration
      }))
    };
  }

  // Public API methods
  getTask(taskId) {
    return this.activeTasks.get(taskId);
  }

  listTasks(status = null) {
    const tasks = Array.from(this.activeTasks.values());
    return status ? tasks.filter(task => task.status === status) : tasks;
  }

  getTasksByCluster(clusterId) {
    return Array.from(this.activeTasks.values())
      .filter(task => task.assignedCluster === clusterId);
  }
}

module.exports = AutomationEngine;