/**
 * AI-Powered Task Conversion System
 * Converts natural language task descriptions into structured Minecraft tasks
 */

class TaskAI {
  constructor(logger) {
    this.logger = logger.child ? logger.child('TaskAI') : logger;
    
    // Minecraft knowledge base for task dependencies
    this.minecraftKnowledge = {
      items: {
        'diamond_sword': { 
          requires: ['diamond', 'stick'], 
          crafting_table: true,
          recipe: 'sword'
        },
        'diamond_armor': {
          requires: ['diamond_helmet', 'diamond_chestplate', 'diamond_leggings', 'diamond_boots']
        },
        'diamond_helmet': { requires: ['diamond'], crafting_table: true, count: 5 },
        'diamond_chestplate': { requires: ['diamond'], crafting_table: true, count: 8 },
        'diamond_leggings': { requires: ['diamond'], crafting_table: true, count: 7 },
        'diamond_boots': { requires: ['diamond'], crafting_table: true, count: 4 },
        'enchanted_book_protection_4': { 
          requires: ['enchanting_table', 'lapis_lazuli', 'experience'],
          enchantment: 'protection',
          level: 4
        },
        'dragon_breath': { 
          location: 'end',
          source: 'ender_dragon_breath_attack',
          container: 'glass_bottle'
        },
        'dragon_egg': {
          location: 'end',
          source: 'ender_dragon_death',
          unique: true
        }
      },
      
      locations: {
        'end': {
          access_method: 'end_portal',
          requires: ['eye_of_ender'],
          boss: 'ender_dragon'
        },
        'nether': {
          access_method: 'nether_portal',
          requires: ['obsidian', 'flint_and_steel']
        }
      },
      
      bosses: {
        'ender_dragon': {
          location: 'end',
          health: 200,
          phases: ['perching', 'flying'],
          drops: ['dragon_egg', 'experience'],
          mechanics: ['breath_attack', 'charge_attack']
        }
      },
      
      enchantments: {
        'protection': { max_level: 4, applies_to: ['armor'] },
        'sharpness': { max_level: 5, applies_to: ['sword'] },
        'efficiency': { max_level: 5, applies_to: ['tools'] }
      }
    };
    
    // Task patterns for natural language processing
    this.taskPatterns = [
      {
        pattern: /defeat\s+(?:the\s+)?(\w+)/i,
        type: 'combat',
        handler: 'parseCombatTask'
      },
      {
        pattern: /collect\s+(\d+)?\s*(\w+)/i,
        type: 'collection',
        handler: 'parseCollectionTask'
      },
      {
        pattern: /craft\s+(\d+)?\s*(\w+)/i,
        type: 'crafting',
        handler: 'parseCraftingTask'
      },
      {
        pattern: /go\s+to\s+(-?\d+)\s+(-?\d+)\s+(-?\d+)/i,
        type: 'movement',
        handler: 'parseMovementTask'
      },
      {
        pattern: /place\s+(\w+)\s+at\s+(-?\d+)\s+(-?\d+)\s+(-?\d+)/i,
        type: 'placement',
        handler: 'parsePlacementTask'
      },
      {
        pattern: /with\s+([\w\s]+)\s+armor/i,
        type: 'equipment',
        handler: 'parseEquipmentRequirement'
      },
      {
        pattern: /with\s+(\w+)\s+(\d+)/i,
        type: 'enchantment',
        handler: 'parseEnchantmentRequirement'
      }
    ];
  }

  /**
   * Main method to convert natural language to structured tasks
   */
  async convertToTasks(naturalLanguageInput, options = {}) {
    this.logger.info('Converting natural language task:', naturalLanguageInput);
    
    const context = {
      input: naturalLanguageInput.toLowerCase(),
      originalInput: naturalLanguageInput,
      tasks: [],
      requirements: [],
      location: options.currentLocation || 'overworld',
      inventory: options.inventory || [],
      groupSize: options.groupSize || 1
    };
    
    try {
      // Parse the input for different task components
      await this.parseTaskComponents(context);
      
      // Generate dependency tree
      await this.generateDependencies(context);
      
      // Create structured task sequence
      const structuredTasks = await this.createTaskSequence(context);
      
      this.logger.info(`Generated ${structuredTasks.length} structured tasks`);
      return {
        success: true,
        originalInput: naturalLanguageInput,
        tasks: structuredTasks,
        estimatedTime: this.estimateCompletionTime(structuredTasks),
        resourceRequirements: this.calculateResourceRequirements(structuredTasks),
        groupRecommendation: this.recommendGroupSize(structuredTasks)
      };
      
    } catch (error) {
      this.logger.error('Failed to convert natural language task:', error);
      return {
        success: false,
        error: error.message,
        suggestions: this.generateSuggestions(naturalLanguageInput)
      };
    }
  }

  async parseTaskComponents(context) {
    const sentences = context.input.split(/[.!;]/);
    
    for (const sentence of sentences) {
      if (sentence.trim().length === 0) continue;
      
      for (const pattern of this.taskPatterns) {
        const match = sentence.match(pattern.pattern);
        if (match) {
          const component = await this[pattern.handler](match, sentence, context);
          if (component) {
            if (pattern.type === 'equipment' || pattern.type === 'enchantment') {
              context.requirements.push(component);
            } else {
              context.tasks.push(component);
            }
          }
        }
      }
    }
  }

  parseCombatTask(match, sentence, context) {
    const target = match[1];
    const boss = this.minecraftKnowledge.bosses[target.replace(/\s+/g, '_')];
    
    if (boss) {
      return {
        type: 'combat',
        action: 'defeat_boss',
        target: target,
        location: boss.location,
        difficulty: 'high',
        requirements: this.getBossRequirements(target),
        priority: 10
      };
    }
    
    return {
      type: 'combat',
      action: 'defeat_entity',
      target: target,
      difficulty: 'medium',
      priority: 5
    };
  }

  parseCollectionTask(match, sentence, context) {
    const quantity = parseInt(match[1]) || 1;
    const item = match[2].replace(/\s+/g, '_');
    
    // Handle special collection cases
    if (item.includes('dragon_breath')) {
      return {
        type: 'collection',
        action: 'collect_dragon_breath',
        item: 'dragon_breath',
        quantity: quantity,
        location: 'end',
        requires: ['glass_bottle'],
        method: 'breath_attack_collection',
        priority: 8
      };
    }
    
    return {
      type: 'collection',
      action: 'collect_item',
      item: item,
      quantity: quantity,
      method: this.getCollectionMethod(item),
      priority: 3
    };
  }

  parseCraftingTask(match, sentence, context) {
    const quantity = parseInt(match[1]) || 1;
    const item = match[2].replace(/\s+/g, '_');
    
    return {
      type: 'crafting',
      action: 'craft_item',
      item: item,
      quantity: quantity,
      requirements: this.getCraftingRequirements(item),
      priority: 4
    };
  }

  parseMovementTask(match, sentence, context) {
    return {
      type: 'movement',
      action: 'travel_to',
      coordinates: {
        x: parseInt(match[1]),
        y: parseInt(match[2]),
        z: parseInt(match[3])
      },
      priority: 2
    };
  }

  parsePlacementTask(match, sentence, context) {
    const item = match[1].replace(/\s+/g, '_');
    
    return {
      type: 'placement',
      action: 'place_block',
      item: item,
      coordinates: {
        x: parseInt(match[2]),
        y: parseInt(match[3]),
        z: parseInt(match[4])
      },
      priority: 1
    };
  }

  parseEquipmentRequirement(match, sentence, context) {
    const armorType = match[1].replace(/\s+/g, '_');
    
    return {
      type: 'equipment',
      category: 'armor',
      items: this.getArmorSet(armorType),
      required: true
    };
  }

  parseEnchantmentRequirement(match, sentence, context) {
    const enchantment = match[1];
    const level = parseInt(match[2]);
    
    return {
      type: 'enchantment',
      enchantment: enchantment,
      level: level,
      required: true
    };
  }

  async generateDependencies(context) {
    // Sort tasks by priority (higher priority first)
    context.tasks.sort((a, b) => (b.priority || 0) - (a.priority || 0));
    
    // Generate dependencies for each task
    for (const task of context.tasks) {
      task.dependencies = await this.calculateTaskDependencies(task, context);
    }
  }

  async calculateTaskDependencies(task, context) {
    const dependencies = [];
    
    // Add location dependencies
    if (task.location && task.location !== context.location) {
      dependencies.push({
        type: 'travel',
        action: 'travel_to_dimension',
        destination: task.location,
        requirements: this.getTravelRequirements(task.location)
      });
    }
    
    // Add item requirements
    if (task.requires) {
      for (const requirement of task.requires) {
        dependencies.push({
          type: 'collection',
          action: 'obtain_item',
          item: requirement,
          method: this.getCollectionMethod(requirement)
        });
      }
    }
    
    // Add equipment requirements from context
    for (const req of context.requirements) {
      if (req.type === 'equipment' && req.required) {
        for (const item of req.items) {
          dependencies.push({
            type: 'equipment',
            action: 'equip_item',
            item: item,
            slot: this.getEquipmentSlot(item)
          });
        }
      }
    }
    
    return dependencies;
  }

  async createTaskSequence(context) {
    const sequence = [];
    
    // Process all tasks and their dependencies
    for (const task of context.tasks) {
      // Add dependencies first
      for (const dep of task.dependencies || []) {
        sequence.push(this.formatTask(dep, context));
      }
      
      // Add the main task
      sequence.push(this.formatTask(task, context));
    }
    
    return this.optimizeTaskSequence(sequence);
  }

  formatTask(task, context) {
    return {
      id: this.generateTaskId(),
      type: task.type,
      action: task.action,
      parameters: {
        target: task.target,
        item: task.item,
        quantity: task.quantity || 1,
        coordinates: task.coordinates,
        location: task.location,
        method: task.method,
        requirements: task.requirements || []
      },
      priority: task.priority || 1,
      estimatedTime: this.estimateTaskTime(task),
      groupSize: this.recommendTaskGroupSize(task),
      difficulty: task.difficulty || 'medium'
    };
  }

  optimizeTaskSequence(sequence) {
    // Remove duplicate tasks
    const seen = new Set();
    const optimized = [];
    
    for (const task of sequence) {
      const key = `${task.type}_${task.action}_${JSON.stringify(task.parameters)}`;
      if (!seen.has(key)) {
        seen.add(key);
        optimized.push(task);
      }
    }
    
    // Sort by logical dependency order
    return optimized.sort((a, b) => {
      const typeOrder = { 'travel': 0, 'collection': 1, 'crafting': 2, 'equipment': 3, 'combat': 4, 'placement': 5, 'movement': 6 };
      return (typeOrder[a.type] || 99) - (typeOrder[b.type] || 99);
    });
  }

  generateTaskId() {
    return `task_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  estimateTaskTime(task) {
    const timeEstimates = {
      'collection': 300, // 5 minutes
      'crafting': 60,    // 1 minute
      'travel': 600,     // 10 minutes
      'combat': 1800,    // 30 minutes for bosses
      'placement': 30,   // 30 seconds
      'equipment': 120   // 2 minutes
    };
    
    return timeEstimates[task.type] || 300;
  }

  recommendTaskGroupSize(task) {
    if (task.type === 'combat' && task.target === 'ender_dragon') return 4;
    if (task.type === 'combat') return 2;
    return 1;
  }

  estimateCompletionTime(tasks) {
    return tasks.reduce((total, task) => total + task.estimatedTime, 0);
  }

  calculateResourceRequirements(tasks) {
    const resources = {};
    
    for (const task of tasks) {
      if (task.parameters.requirements) {
        for (const req of task.parameters.requirements) {
          resources[req] = (resources[req] || 0) + 1;
        }
      }
    }
    
    return resources;
  }

  recommendGroupSize(tasks) {
    return Math.max(...tasks.map(task => task.groupSize));
  }

  // Helper methods
  getBossRequirements(boss) {
    const requirements = {
      'ender_dragon': ['diamond_sword', 'diamond_armor', 'bow', 'arrow', 'food', 'blocks']
    };
    return requirements[boss.replace(/\s+/g, '_')] || [];
  }

  getCollectionMethod(item) {
    const methods = {
      'diamond': 'mining',
      'dragon_breath': 'collection_during_combat',
      'dragon_egg': 'boss_drop',
      'obsidian': 'mining',
      'stick': 'crafting_from_wood'
    };
    return methods[item] || 'unknown';
  }

  getCraftingRequirements(item) {
    return this.minecraftKnowledge.items[item]?.requires || [];
  }

  getTravelRequirements(location) {
    return this.minecraftKnowledge.locations[location]?.requires || [];
  }

  getArmorSet(type) {
    const sets = {
      'diamond': ['diamond_helmet', 'diamond_chestplate', 'diamond_leggings', 'diamond_boots'],
      'iron': ['iron_helmet', 'iron_chestplate', 'iron_leggings', 'iron_boots'],
      'leather': ['leather_helmet', 'leather_chestplate', 'leather_leggings', 'leather_boots']
    };
    return sets[type] || [];
  }

  getEquipmentSlot(item) {
    if (item.includes('helmet')) return 'head';
    if (item.includes('chestplate')) return 'chest';
    if (item.includes('leggings')) return 'legs';
    if (item.includes('boots')) return 'feet';
    if (item.includes('sword')) return 'mainhand';
    return 'inventory';
  }

  generateSuggestions(input) {
    return [
      'Try being more specific about quantities (e.g., "collect 64 diamonds")',
      'Specify locations if needed (e.g., "defeat the ender dragon in the end")',
      'Include equipment requirements (e.g., "with full diamond armor")',
      'Add coordinate destinations (e.g., "place at 100 64 200")'
    ];
  }
}

module.exports = TaskAI;