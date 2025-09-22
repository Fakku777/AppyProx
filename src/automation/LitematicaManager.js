const fs = require('fs');
const path = require('path');

/**
 * Litematica and Schematica integration manager
 * Handles .litematic and .schematic file parsing, building automation
 */
class LitematicaManager {
  constructor(config, logger) {
    this.config = config;
    this.logger = logger.child ? logger.child('LitematicaManager') : logger;
    
    this.schematicDir = path.resolve(this.config.litematica?.schematic_path || './schemas/');
    this.loadedSchematics = new Map(); // name -> schematic data
    this.buildTasks = new Map(); // taskId -> build progress
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing Litematica manager...');
    
    try {
      // Ensure schematic directory exists
      if (!fs.existsSync(this.schematicDir)) {
        fs.mkdirSync(this.schematicDir, { recursive: true });
        this.logger.info(`Created schematic directory: ${this.schematicDir}`);
      }
      
      // Load available schematics
      await this.loadAvailableSchematics();
      
      this.initialized = true;
      this.logger.info(`Litematica manager initialized with ${this.loadedSchematics.size} schematics`);
      
    } catch (error) {
      this.logger.error('Failed to initialize Litematica manager:', error);
      throw error;
    }
  }

  async loadAvailableSchematics() {
    try {
      const files = fs.readdirSync(this.schematicDir);
      const schematicFiles = files.filter(file => 
        file.endsWith('.litematic') || file.endsWith('.schematic') || file.endsWith('.schem')
      );
      
      for (const file of schematicFiles) {
        try {
          const schematic = await this.loadSchematic(file);
          if (schematic) {
            this.loadedSchematics.set(schematic.name, schematic);
            this.logger.debug(`Loaded schematic: ${schematic.name} (${schematic.width}x${schematic.height}x${schematic.length})`);
          }
        } catch (error) {
          this.logger.warn(`Failed to load schematic ${file}:`, error.message);
        }
      }
      
      this.logger.info(`Loaded ${this.loadedSchematics.size} schematics from ${this.schematicDir}`);
      
    } catch (error) {
      this.logger.warn('Failed to load schematics directory:', error.message);
    }
  }

  async loadSchematic(filename) {
    const filePath = path.join(this.schematicDir, filename);
    
    try {
      if (filename.endsWith('.litematic')) {
        return await this.parseLitematicFile(filePath);
      } else if (filename.endsWith('.schematic') || filename.endsWith('.schem')) {
        return await this.parseSchematicFile(filePath);
      }
      
      return null;
    } catch (error) {
      this.logger.error(`Failed to parse schematic file ${filename}:`, error);
      return null;
    }
  }

  async parseLitematicFile(filePath) {
    // Litematica files use NBT format
    this.logger.debug(`Parsing Litematica file: ${filePath}`);
    
    const stats = fs.statSync(filePath);
    const filename = path.basename(filePath, '.litematic');
    
    // Placeholder schematic structure
    const schematic = {
      name: filename,
      type: 'litematica',
      version: '6',
      width: 16, height: 16, length: 16,
      totalBlocks: 256,
      materials: this.analyzeMaterials(filename),
      regions: [{ name: 'main', position: { x: 0, y: 0, z: 0 }, size: { x: 16, y: 16, z: 16 } }],
      metadata: {
        author: 'Unknown',
        description: 'Loaded from .litematic file',
        created: stats.mtime,
        difficulty: this.estimateBuildDifficulty(filename)
      },
      filePath: filePath
    };
    
    return schematic;
  }

  async parseSchematicFile(filePath) {
    // WorldEdit schematic format
    this.logger.debug(`Parsing Schematic file: ${filePath}`);
    
    const stats = fs.statSync(filePath);
    const filename = path.basename(filePath).replace(/\.(schematic|schem)$/, '');
    
    const schematic = {
      name: filename,
      type: 'schematic',
      version: '2',
      width: 32, height: 24, length: 32,
      totalBlocks: 512,
      materials: this.analyzeMaterials(filename),
      metadata: {
        author: 'Unknown',
        description: 'Loaded from .schematic/.schem file',
        created: stats.mtime,
        difficulty: this.estimateBuildDifficulty(filename)
      },
      filePath: filePath
    };
    
    return schematic;
  }

  analyzeMaterials(filename) {
    const commonMaterials = {
      'oak_planks': 64,
      'stone_bricks': 128,
      'glass': 32,
      'oak_log': 16
    };
    
    if (filename.toLowerCase().includes('house')) {
      commonMaterials['oak_planks'] = 256;
      commonMaterials['stone_bricks'] = 192;
    }
    
    if (filename.toLowerCase().includes('castle')) {
      commonMaterials['stone_bricks'] = 512;
      commonMaterials['cobblestone'] = 256;
    }
    
    return commonMaterials;
  }

  estimateBuildDifficulty(filename) {
    const name = filename.toLowerCase();
    if (name.includes('simple') || name.includes('small')) return 'easy';
    if (name.includes('castle') || name.includes('large')) return 'hard';
    if (name.includes('auto') || name.includes('farm')) return 'expert';
    return 'medium';
  }

  async createBuildTask(schematicName, buildOptions = {}) {
    const schematic = this.loadedSchematics.get(schematicName);
    if (!schematic) {
      throw new Error(`Schematic not found: ${schematicName}`);
    }
    
    const buildTask = {
      id: `build_${Date.now()}`,
      schematic: schematic,
      options: {
        location: buildOptions.location || { x: 0, y: 64, z: 0 },
        rotation: buildOptions.rotation || 0,
        clusterId: buildOptions.clusterId || null
      },
      progress: {
        blocksPlaced: 0,
        totalBlocks: schematic.totalBlocks,
        status: 'pending'
      },
      materialRequirements: schematic.materials,
      estimatedTime: this.calculateBuildTime(schematic, buildOptions),
      created: Date.now()
    };
    
    this.buildTasks.set(buildTask.id, buildTask);
    return buildTask;
  }

  calculateBuildTime(schematic, options) {
    let baseTime = schematic.totalBlocks * 2000; // 2 seconds per block
    
    const difficultyMultiplier = { 'easy': 0.8, 'medium': 1.0, 'hard': 1.3, 'expert': 1.6 };
    baseTime *= difficultyMultiplier[schematic.metadata.difficulty] || 1.0;
    
    if (options.clusterId) baseTime *= 0.5; // Multiple builders
    
    return Math.round(baseTime);
  }

  listSchematics() {
    return Array.from(this.loadedSchematics.values()).map(s => ({
      name: s.name,
      type: s.type,
      dimensions: `${s.width}x${s.height}x${s.length}`,
      totalBlocks: s.totalBlocks,
      difficulty: s.metadata.difficulty
    }));
  }

  getStatus() {
    return {
      initialized: this.initialized,
      totalSchematics: this.loadedSchematics.size,
      activeBuildTasks: Array.from(this.buildTasks.values()).filter(t => 
        t.progress.status === 'building' || t.progress.status === 'pending'
      ).length,
      schematicDirectory: this.schematicDir
    };
  }
}

module.exports = LitematicaManager;