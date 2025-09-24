/**
 * Intelligent task planner that creates optimized execution plans
 * for complex Minecraft automation tasks
 */
class TaskPlanner {
  constructor(config, logger, wikiScraper = null) {
    this.config = config;
    this.logger = logger.child ? logger.child('TaskPlanner') : logger;
    this.wikiScraper = wikiScraper;
    this.initialized = false;
    
    // Task execution templates
    this.taskTemplates = new Map();
    this.resourceLocations = new Map();
    this.toolRequirements = new Map();
    
    // Performance metrics for optimization
    this.performanceHistory = new Map();
    this.locationDatabase = new Map();
  }

  async initialize() {
    if (this.initialized) return;
    
    await this.loadTaskTemplates();
    await this.initializeResourceDatabase();
    
    this.logger.info('Task planner initialized with comprehensive execution logic');
    this.initialized = true;
  }
  
  async loadTaskTemplates() {
    // Load predefined task templates for common operations
    const templates = {
      'gather_wood': this.createWoodGatheringTemplate(),
      'gather_stone': this.createStoneGatheringTemplate(),
      'gather_iron': this.createIronGatheringTemplate(),
      'gather_diamond': this.createDiamondGatheringTemplate(),
      'gather_food': this.createFoodGatheringTemplate(),
      'build_house': this.createHouseBuildingTemplate(),
      'build_bridge': this.createBridgeBuildingTemplate(),
      'explore_cave': this.createCaveExplorationTemplate(),
      'establish_base': this.createBaseEstablishmentTemplate(),
      'farm_animals': this.createAnimalFarmingTemplate(),
      'enchant_equipment': this.createEnchantingTemplate()
    };
    
    for (const [taskType, template] of Object.entries(templates)) {
      this.taskTemplates.set(taskType, template);
    }
  }
  
  async initializeResourceDatabase() {
    // Initialize known resource locations and generation patterns
    this.resourceLocations.set('diamond', {
      optimalY: 11,
      commonBiomes: ['any'],
      generationPattern: 'ore_veins',
      averageVeinSize: 3,
      rarity: 'very_rare'
    });
    
    this.resourceLocations.set('iron', {
      optimalY: 32,
      commonBiomes: ['any'],
      generationPattern: 'ore_veins',
      averageVeinSize: 6,
      rarity: 'common'
    });
    
    this.resourceLocations.set('coal', {
      optimalY: 50,
      commonBiomes: ['any'],
      generationPattern: 'ore_veins',
      averageVeinSize: 12,
      rarity: 'very_common'
    });
    
    // Tool requirements database
    this.toolRequirements.set('diamond', ['iron_pickaxe', 'diamond_pickaxe']);
    this.toolRequirements.set('iron', ['stone_pickaxe', 'iron_pickaxe', 'diamond_pickaxe']);
    this.toolRequirements.set('stone', ['pickaxe']);
    this.toolRequirements.set('wood', ['axe']);
  }

  async createExecutionPlan(taskType, parameters, accountCapabilities = {}) {
    this.logger.debug(`Creating execution plan for task: ${taskType}`);
    
    // Validate task parameters
    const validationResult = await this.validateTaskParameters(taskType, parameters);
    if (!validationResult.valid) {
      throw new Error(`Invalid task parameters: ${validationResult.errors.join(', ')}`);
    }
    
    // Check if we have a template for this task
    let basePlan;
    if (this.taskTemplates.has(taskType)) {
      basePlan = this.taskTemplates.get(taskType);
    } else {
      // Generate plan based on task type patterns
      basePlan = await this.generateDynamicPlan(taskType, parameters);
    }
    
    // Optimize plan based on account capabilities and current conditions
    const optimizedPlan = await this.optimizePlan(basePlan, parameters, accountCapabilities);
    
    // Add resource gathering prerequisites if needed
    const planWithPrerequisites = await this.addPrerequisites(optimizedPlan, parameters);
    
    // Calculate accurate time estimates
    const finalPlan = await this.calculateTimeEstimates(planWithPrerequisites);
    
    this.logger.info(`Created execution plan for ${taskType} with ${finalPlan.steps.length} steps, estimated duration: ${finalPlan.estimatedDuration}ms`);
    return finalPlan;
  }
  
  async validateTaskParameters(taskType, parameters) {
    const errors = [];
    
    switch (taskType) {
      case 'gather_resource':
        if (!parameters.resource) errors.push('Missing resource parameter');
        if (!parameters.quantity || parameters.quantity <= 0) errors.push('Invalid quantity parameter');
        break;
      case 'build_structure':
        if (!parameters.location) errors.push('Missing location parameter');
        if (!parameters.schematic && !parameters.blueprint) errors.push('Missing schematic or blueprint');
        break;
      case 'explore_area':
        if (!parameters.area && !parameters.radius) errors.push('Missing area or radius parameter');
        break;
    }
    
    return {
      valid: errors.length === 0,
      errors
    };
  }
  
  async generateDynamicPlan(taskType, parameters) {
    // Generate plans for unknown task types
    if (taskType.startsWith('gather_')) {
      const resource = taskType.replace('gather_', '');
      return this.createAdvancedGatheringPlan(resource, parameters.quantity || 1, parameters);
    }
    
    if (taskType.startsWith('build_')) {
      const structure = taskType.replace('build_', '');
      return this.createAdvancedBuildingPlan(structure, parameters);
    }
    
    if (taskType.startsWith('explore_')) {
      const area = taskType.replace('explore_', '');
      return this.createExplorationPlan(area, parameters);
    }
    
    return this.createGenericPlan(taskType, parameters);
  }

  async createAdvancedGatheringPlan(resource, quantity, parameters = {}) {
    const resourceInfo = this.resourceLocations.get(resource);
    const requiredTools = this.toolRequirements.get(resource) || ['hand'];
    
    const steps = [];
    
    // Step 1: Ensure proper tools are available
    if (requiredTools[0] !== 'hand') {
      steps.push({
        action: 'ensure_tool_availability',
        parameters: { tools: requiredTools },
        estimatedTime: 30000 // 30 seconds
      });
    }
    
    // Step 2: Navigate to optimal gathering location
    const optimalLocation = this.calculateOptimalGatheringLocation(resource, parameters.currentLocation);
    steps.push({
      action: 'navigate_to_location',
      parameters: { 
        location: optimalLocation,
        pathfinding: true,
        avoidDanger: true
      },
      estimatedTime: this.calculateTravelTime(parameters.currentLocation, optimalLocation)
    });
    
    // Step 3: Prepare gathering area (lighting, safety)
    if (resourceInfo && resourceInfo.optimalY < 20) {
      steps.push({
        action: 'secure_mining_area',
        parameters: { placeLight: true, clearHostiles: true },
        estimatedTime: 60000 // 1 minute
      });
    }
    
    // Step 4: Execute gathering with efficiency optimization
    const gatheringMethod = this.selectOptimalGatheringMethod(resource, quantity, resourceInfo);
    steps.push({
      action: 'execute_gathering',
      parameters: {
        resource,
        quantity,
        method: gatheringMethod.method,
        efficiency: gatheringMethod.efficiency,
        pattern: gatheringMethod.pattern
      },
      estimatedTime: this.calculateGatheringTime(resource, quantity, gatheringMethod)
    });
    
    // Step 5: Return to base if requested
    if (parameters.returnToBase) {
      steps.push({
        action: 'return_to_base',
        parameters: { baseLocation: parameters.baseLocation },
        estimatedTime: this.calculateTravelTime(optimalLocation, parameters.baseLocation)
      });
    }
    
    return {
      taskType: `gather_${resource}`,
      priority: this.calculateTaskPriority(resource, quantity),
      steps: steps,
      estimatedDuration: steps.reduce((total, step) => total + step.estimatedTime, 0),
      requiredResources: this.calculateResourceRequirements(steps),
      riskLevel: this.assessTaskRisk(resource, optimalLocation)
    };
  }
  
  async createAdvancedBuildingPlan(structure, parameters) {
    const steps = [];
    
    // Step 1: Analyze building requirements
    const buildingAnalysis = await this.analyzeBuildingRequirements(structure, parameters);
    
    // Step 2: Gather required materials
    if (buildingAnalysis.requiredMaterials) {
      for (const [material, quantity] of Object.entries(buildingAnalysis.requiredMaterials)) {
        const gatheringPlan = await this.createAdvancedGatheringPlan(material, quantity, parameters);
        steps.push({
          action: 'execute_subplan',
          parameters: { subplan: gatheringPlan },
          estimatedTime: gatheringPlan.estimatedDuration
        });
      }
    }
    
    // Step 3: Prepare building site
    steps.push({
      action: 'prepare_building_site',
      parameters: {
        location: parameters.location,
        dimensions: buildingAnalysis.dimensions,
        clearTerrain: parameters.clearTerrain || false
      },
      estimatedTime: buildingAnalysis.sitePreparationTime
    });
    
    // Step 4: Execute building in phases
    const buildingPhases = this.createBuildingPhases(structure, buildingAnalysis);
    for (const phase of buildingPhases) {
      steps.push({
        action: 'execute_building_phase',
        parameters: phase,
        estimatedTime: phase.estimatedTime
      });
    }
    
    return {
      taskType: `build_${structure}`,
      priority: parameters.priority || 'medium',
      steps: steps,
      estimatedDuration: steps.reduce((total, step) => total + step.estimatedTime, 0),
      requiredResources: buildingAnalysis.requiredMaterials,
      riskLevel: 'low'
    };
  }
  
  createExplorationPlan(area, parameters) {
    const steps = [];
    
    // Step 1: Prepare for exploration
    steps.push({
      action: 'prepare_exploration',
      parameters: {
        equipment: ['food', 'torches', 'pickaxe', 'sword'],
        duration: parameters.maxDuration || 3600000 // 1 hour default
      },
      estimatedTime: 120000 // 2 minutes
    });
    
    // Step 2: Navigate to exploration area
    steps.push({
      action: 'navigate_to_exploration_start',
      parameters: {
        area: parameters.area,
        startPoint: parameters.startPoint
      },
      estimatedTime: this.calculateTravelTime(parameters.currentLocation, parameters.startPoint)
    });
    
    // Step 3: Execute systematic exploration
    const explorationPattern = this.generateExplorationPattern(area, parameters);
    steps.push({
      action: 'execute_systematic_exploration',
      parameters: {
        pattern: explorationPattern,
        objectives: parameters.objectives || ['map_area', 'gather_resources'],
        safetyProtocol: true
      },
      estimatedTime: parameters.maxDuration || 3600000
    });
    
    return {
      taskType: `explore_${area}`,
      priority: parameters.priority || 'low',
      steps: steps,
      estimatedDuration: steps.reduce((total, step) => total + step.estimatedTime, 0),
      requiredResources: { food: 10, torches: 64 },
      riskLevel: 'medium'
    };
  }
  
  // Utility methods for plan optimization and calculation
  
  calculateOptimalGatheringLocation(resource, currentLocation) {
    const resourceInfo = this.resourceLocations.get(resource);
    if (!resourceInfo) {
      return { x: 0, y: 64, z: 0 }; // Surface default
    }
    
    // Calculate optimal location based on resource characteristics
    return {
      x: currentLocation ? currentLocation.x : 0,
      y: resourceInfo.optimalY,
      z: currentLocation ? currentLocation.z : 0
    };
  }
  
  calculateTravelTime(from, to) {
    if (!from || !to) return 60000; // 1 minute default
    
    const distance = Math.sqrt(
      Math.pow(to.x - from.x, 2) +
      Math.pow(to.y - from.y, 2) +
      Math.pow(to.z - from.z, 2)
    );
    
    // Assume 4.3 blocks/second walking speed
    return Math.max(distance / 4.3 * 1000, 5000); // Minimum 5 seconds
  }
  
  selectOptimalGatheringMethod(resource, quantity, resourceInfo) {
    if (!resourceInfo) {
      return { method: 'manual', efficiency: 'medium', pattern: 'random' };
    }
    
    const methods = {
      'strip_mining': { efficiency: 'high', pattern: 'systematic', bestFor: ['diamond', 'gold'] },
      'branch_mining': { efficiency: 'very_high', pattern: 'systematic', bestFor: ['diamond', 'iron'] },
      'surface_gathering': { efficiency: 'medium', pattern: 'opportunistic', bestFor: ['wood', 'stone'] },
      'cave_exploration': { efficiency: 'medium', pattern: 'adaptive', bestFor: ['iron', 'coal'] }
    };
    
    // Select best method based on resource and quantity
    if (quantity > 64 && ['diamond', 'iron'].includes(resource)) {
      return { method: 'branch_mining', ...methods.branch_mining };
    }
    
    if (resourceInfo.optimalY < 20) {
      return { method: 'strip_mining', ...methods.strip_mining };
    }
    
    return { method: 'surface_gathering', ...methods.surface_gathering };
  }
  
  calculateGatheringTime(resource, quantity, method) {
    const baseTimePerItem = {
      'diamond': 15000, // 15 seconds
      'iron': 8000,     // 8 seconds
      'coal': 3000,     // 3 seconds
      'stone': 2000,    // 2 seconds
      'wood': 1000      // 1 second
    };
    
    const methodMultipliers = {
      'branch_mining': 0.7,
      'strip_mining': 0.8,
      'cave_exploration': 1.2,
      'surface_gathering': 1.0
    };
    
    const baseTime = baseTimePerItem[resource] || 5000;
    const multiplier = methodMultipliers[method.method] || 1.0;
    
    return Math.floor(baseTime * quantity * multiplier);
  }
  
  calculateTaskPriority(resource, quantity) {
    const resourcePriorities = {
      'diamond': 'very_high',
      'iron': 'high',
      'food': 'high',
      'wood': 'medium',
      'stone': 'medium',
      'coal': 'low'
    };
    
    const basePriority = resourcePriorities[resource] || 'medium';
    
    // Increase priority for large quantities
    if (quantity > 128) return 'very_high';
    if (quantity > 64) return 'high';
    
    return basePriority;
  }
  
  calculateResourceRequirements(steps) {
    const requirements = {};
    
    for (const step of steps) {
      if (step.action === 'secure_mining_area') {
        requirements.torches = (requirements.torches || 0) + 10;
      }
      if (step.action === 'prepare_exploration') {
        requirements.food = (requirements.food || 0) + 10;
        requirements.torches = (requirements.torches || 0) + 32;
      }
    }
    
    return requirements;
  }
  
  assessTaskRisk(resource, location) {
    if (location.y < 16) return 'high';    // Deep underground
    if (location.y < 32) return 'medium';  // Underground
    return 'low';                          // Surface
  }
  
  async optimizePlan(plan, parameters, accountCapabilities) {
    // Optimize based on account capabilities
    if (accountCapabilities.hasElytra) {
      // Reduce travel times for accounts with elytra
      plan.steps.forEach(step => {
        if (step.action === 'navigate_to_location') {
          step.estimatedTime = Math.floor(step.estimatedTime * 0.3);
        }
      });
    }
    
    if (accountCapabilities.hasEnchantedTools) {
      // Reduce gathering times for accounts with efficiency enchants
      plan.steps.forEach(step => {
        if (step.action === 'execute_gathering') {
          step.estimatedTime = Math.floor(step.estimatedTime * 0.6);
        }
      });
    }
    
    return plan;
  }
  
  async addPrerequisites(plan, parameters) {
    const prerequisites = [];
    
    // Check if tools are needed
    const requiredTools = new Set();
    plan.steps.forEach(step => {
      if (step.parameters && step.parameters.tools) {
        step.parameters.tools.forEach(tool => requiredTools.add(tool));
      }
    });
    
    // Add tool gathering prerequisites
    for (const tool of requiredTools) {
      if (!parameters.availableTools || !parameters.availableTools.includes(tool)) {
        prerequisites.push({
          action: 'craft_or_gather_tool',
          parameters: { tool },
          estimatedTime: 300000 // 5 minutes
        });
      }
    }
    
    return {
      ...plan,
      steps: [...prerequisites, ...plan.steps]
    };
  }
  
  async calculateTimeEstimates(plan) {
    // Recalculate total duration with more accurate estimates
    let totalDuration = 0;
    
    for (const step of plan.steps) {
      totalDuration += step.estimatedTime || 30000; // 30 second default
    }
    
    // Add buffer time for unexpected delays (20%)
    totalDuration = Math.floor(totalDuration * 1.2);
    
    return {
      ...plan,
      estimatedDuration: totalDuration,
      estimatedDurationHuman: this.formatDuration(totalDuration)
    };
  }
  
  formatDuration(milliseconds) {
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    
    if (hours > 0) {
      return `${hours}h ${minutes % 60}m`;
    } else if (minutes > 0) {
      return `${minutes}m ${seconds % 60}s`;
    } else {
      return `${seconds}s`;
    }
  }
  
  async analyzeBuildingRequirements(structure, parameters) {
    // Analyze what materials and time are needed for building
    const buildingDatabase = {
      'house': {
        requiredMaterials: { wood: 200, stone: 100, glass: 20 },
        dimensions: { width: 10, height: 6, depth: 10 },
        sitePreparationTime: 300000 // 5 minutes
      },
      'bridge': {
        requiredMaterials: { stone: 150, wood: 50 },
        dimensions: { width: 3, height: 2, depth: parameters.length || 20 },
        sitePreparationTime: 180000 // 3 minutes
      },
      'tower': {
        requiredMaterials: { stone: 500, wood: 100 },
        dimensions: { width: 5, height: parameters.height || 20, depth: 5 },
        sitePreparationTime: 240000 // 4 minutes
      }
    };
    
    return buildingDatabase[structure] || {
      requiredMaterials: { stone: 100 },
      dimensions: { width: 5, height: 5, depth: 5 },
      sitePreparationTime: 300000
    };
  }
  
  createBuildingPhases(structure, analysis) {
    const phases = [
      {
        name: 'foundation',
        materials: { stone: Math.floor(analysis.requiredMaterials.stone * 0.3) },
        estimatedTime: 600000 // 10 minutes
      },
      {
        name: 'walls',
        materials: { wood: analysis.requiredMaterials.wood || 0 },
        estimatedTime: 900000 // 15 minutes
      },
      {
        name: 'roof',
        materials: { wood: Math.floor((analysis.requiredMaterials.wood || 0) * 0.3) },
        estimatedTime: 300000 // 5 minutes
      },
      {
        name: 'details',
        materials: { glass: analysis.requiredMaterials.glass || 0 },
        estimatedTime: 180000 // 3 minutes
      }
    ];
    
    return phases;
  }
  
  generateExplorationPattern(area, parameters) {
    const patterns = {
      'cave': 'depth_first_with_safety',
      'surface': 'grid_systematic',
      'nether': 'cautious_bridge_building',
      'ocean': 'depth_limited_search'
    };
    
    return patterns[area] || 'grid_systematic';
  }
  
  createGenericPlan(taskType, parameters) {
    return {
      taskType,
      priority: 'medium',
      steps: [
        {
          action: 'log_task_start',
          parameters: { taskType, parameters },
          estimatedTime: 1000
        },
        {
          action: 'execute_generic_task',
          parameters: parameters,
          estimatedTime: 300000 // 5 minutes default
        }
      ],
      estimatedDuration: 301000,
      requiredResources: {},
      riskLevel: 'medium'
    };
  }
  
  // Template creation methods (stubs for now)
  createWoodGatheringTemplate() {
    return {
      taskType: 'gather_wood',
      steps: [{ action: 'gather_wood', estimatedTime: 60000 }],
      estimatedDuration: 60000
    };
  }
  
  createStoneGatheringTemplate() {
    return {
      taskType: 'gather_stone', 
      steps: [{ action: 'gather_stone', estimatedTime: 120000 }],
      estimatedDuration: 120000
    };
  }
  
  createIronGatheringTemplate() {
    return {
      taskType: 'gather_iron',
      steps: [{ action: 'gather_iron', estimatedTime: 300000 }],
      estimatedDuration: 300000
    };
  }
  
  createDiamondGatheringTemplate() {
    return {
      taskType: 'gather_diamond',
      steps: [{ action: 'gather_diamond', estimatedTime: 900000 }],
      estimatedDuration: 900000
    };
  }
  
  createFoodGatheringTemplate() {
    return {
      taskType: 'gather_food',
      steps: [{ action: 'gather_food', estimatedTime: 180000 }],
      estimatedDuration: 180000
    };
  }
  
  createHouseBuildingTemplate() {
    return {
      taskType: 'build_house',
      steps: [{ action: 'build_house', estimatedTime: 1800000 }],
      estimatedDuration: 1800000
    };
  }
  
  createBridgeBuildingTemplate() {
    return {
      taskType: 'build_bridge',
      steps: [{ action: 'build_bridge', estimatedTime: 600000 }],
      estimatedDuration: 600000
    };
  }
  
  createCaveExplorationTemplate() {
    return {
      taskType: 'explore_cave',
      steps: [{ action: 'explore_cave', estimatedTime: 3600000 }],
      estimatedDuration: 3600000
    };
  }
  
  createBaseEstablishmentTemplate() {
    return {
      taskType: 'establish_base',
      steps: [{ action: 'establish_base', estimatedTime: 1800000 }],
      estimatedDuration: 1800000
    };
  }
  
  createAnimalFarmingTemplate() {
    return {
      taskType: 'farm_animals',
      steps: [{ action: 'farm_animals', estimatedTime: 1200000 }],
      estimatedDuration: 1200000
    };
  }
  
  createEnchantingTemplate() {
    return {
      taskType: 'enchant_equipment',
      steps: [{ action: 'enchant_equipment', estimatedTime: 600000 }],
      estimatedDuration: 600000
    };
  }
  
  getStatus() {
    return {
      initialized: this.initialized,
      taskTemplates: this.taskTemplates.size,
      resourceLocations: this.resourceLocations.size,
      performanceHistory: this.performanceHistory.size
    };
  }
}

module.exports = TaskPlanner;
