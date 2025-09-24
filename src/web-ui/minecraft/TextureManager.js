/**
 * Minecraft Texture Manager
 * Handles loading, caching, and rendering of Minecraft textures
 */

const fs = require('fs').promises;
const path = require('path');
const https = require('https');
const sharp = require('sharp');

class TextureManager {
  constructor(logger) {
    this.logger = logger.child ? logger.child('TextureManager') : logger;
    
    // Texture storage
    this.textures = new Map();
    this.atlases = new Map();
    this.textureCache = new Map();
    
    // Minecraft texture mappings
    this.blockTextures = new Map();
    this.itemTextures = new Map();
    this.entityTextures = new Map();
    
    // Asset paths
    this.assetsDir = path.join(__dirname, '../assets');
    this.texturesDir = path.join(this.assetsDir, 'textures');
    this.spritesDir = path.join(this.assetsDir, 'sprites');
    
    // Minecraft version and texture pack info
    this.minecraftVersion = '1.20.4';
    this.texturePackUrl = 'https://github.com/InventivetalentDev/minecraft-assets';
    
    // Texture atlas configuration
    this.atlasSize = 2048; // 2048x2048 texture atlas
    this.tileSize = 16; // Standard Minecraft texture size
    this.tilesPerRow = this.atlasSize / this.tileSize;
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing Minecraft texture manager...');
    
    try {
      // Ensure asset directories exist
      await this.ensureDirectories();
      
      // Load texture mappings
      await this.loadTextureMappings();
      
      // Download missing textures
      await this.downloadTextures();
      
      // Create texture atlases
      await this.createTextureAtlases();
      
      this.initialized = true;
      this.logger.info(`Texture manager initialized with ${this.textures.size} textures`);
      
    } catch (error) {
      this.logger.error('Failed to initialize texture manager:', error);
      throw error;
    }
  }

  async ensureDirectories() {
    const dirs = [
      this.assetsDir,
      this.texturesDir,
      this.spritesDir,
      path.join(this.texturesDir, 'blocks'),
      path.join(this.texturesDir, 'items'),
      path.join(this.texturesDir, 'entities'),
      path.join(this.spritesDir, 'gui')
    ];
    
    for (const dir of dirs) {
      try {
        await fs.mkdir(dir, { recursive: true });
      } catch (error) {
        if (error.code !== 'EEXIST') throw error;
      }
    }
  }

  async loadTextureMappings() {
    // Define essential block textures for map rendering
    const blockMappings = {
      // Terrain blocks
      'grass_block': { top: 'grass_block_top.png', side: 'grass_block_side.png' },
      'dirt': 'dirt.png',
      'stone': 'stone.png',
      'cobblestone': 'cobblestone.png',
      'bedrock': 'bedrock.png',
      'sand': 'sand.png',
      'gravel': 'gravel.png',
      'water': 'water_still.png',
      'lava': 'lava_still.png',
      
      // Ores
      'coal_ore': 'coal_ore.png',
      'iron_ore': 'iron_ore.png',
      'gold_ore': 'gold_ore.png',
      'diamond_ore': 'diamond_ore.png',
      'redstone_ore': 'redstone_ore.png',
      
      // Wood types
      'oak_log': { top: 'oak_log_top.png', side: 'oak_log.png' },
      'oak_planks': 'oak_planks.png',
      'oak_leaves': 'oak_leaves.png',
      
      // Common building blocks
      'glass': 'glass.png',
      'white_wool': 'white_wool.png',
      'black_wool': 'black_wool.png',
      'red_wool': 'red_wool.png'
    };

    // Define item textures
    const itemMappings = {
      'diamond': 'diamond.png',
      'iron_ingot': 'iron_ingot.png',
      'gold_ingot': 'gold_ingot.png',
      'coal': 'coal.png',
      'stick': 'stick.png',
      'apple': 'apple.png',
      'bread': 'bread.png'
    };

    // Define entity textures
    const entityMappings = {
      'player': 'steve.png',
      'zombie': 'zombie.png',
      'skeleton': 'skeleton.png',
      'creeper': 'creeper.png',
      'spider': 'spider.png',
      'cow': 'cow.png',
      'pig': 'pig.png',
      'chicken': 'chicken.png'
    };

    // Store mappings
    for (const [id, texture] of Object.entries(blockMappings)) {
      this.blockTextures.set(id, texture);
    }
    
    for (const [id, texture] of Object.entries(itemMappings)) {
      this.itemTextures.set(id, texture);
    }
    
    for (const [id, texture] of Object.entries(entityMappings)) {
      this.entityTextures.set(id, texture);
    }

    this.logger.info(`Loaded ${this.blockTextures.size} block textures, ${this.itemTextures.size} item textures, ${this.entityTextures.size} entity textures`);
  }

  async downloadTextures() {
    this.logger.info('Checking for missing textures...');
    
    // For now, create placeholder textures
    // In a real implementation, you would download from minecraft-assets repo
    await this.createPlaceholderTextures();
  }

  async createPlaceholderTextures() {
    // Create simple colored placeholder textures
    const placeholders = {
      // Blocks
      'grass_block_top.png': { r: 124, g: 252, b: 0 }, // Bright green
      'grass_block_side.png': { r: 102, g: 204, b: 51 }, // Green
      'dirt.png': { r: 139, g: 69, b: 19 }, // Brown
      'stone.png': { r: 128, g: 128, b: 128 }, // Gray
      'cobblestone.png': { r: 105, g: 105, b: 105 }, // Dark gray
      'bedrock.png': { r: 64, g: 64, b: 64 }, // Very dark gray
      'sand.png': { r: 238, g: 203, b: 173 }, // Sandy
      'gravel.png': { r: 128, g: 128, b: 128 }, // Gray gravel
      'water_still.png': { r: 0, g: 119, b: 190 }, // Blue
      'lava_still.png': { r: 255, g: 87, b: 34 }, // Orange-red
      'coal_ore.png': { r: 54, g: 54, b: 54 }, // Dark with black spots
      'iron_ore.png': { r: 216, g: 175, b: 147 }, // Light brown with gray
      'diamond_ore.png': { r: 92, g: 219, b: 213 }, // Light blue
      'oak_log.png': { r: 101, g: 67, b: 33 }, // Brown bark
      'oak_log_top.png': { r: 222, g: 184, b: 135 }, // Light wood
      'oak_planks.png': { r: 162, g: 130, b: 78 }, // Wood planks
      'oak_leaves.png': { r: 79, g: 111, b: 42 }, // Dark green
      'gold_ore.png': { r: 255, g: 215, b: 0 }, // Gold
      'redstone_ore.png': { r: 255, g: 0, b: 0 }, // Red
      'glass.png': { r: 255, g: 255, b: 255 }, // Transparent white
      'white_wool.png': { r: 255, g: 255, b: 255 }, // White
      'black_wool.png': { r: 30, g: 30, b: 30 }, // Black
      'red_wool.png': { r: 255, g: 0, b: 0 }, // Red
      
      // Items
      'diamond.png': { r: 185, g: 242, b: 255 }, // Light blue
      'iron_ingot.png': { r: 211, g: 211, b: 211 }, // Light gray
      'gold_ingot.png': { r: 255, g: 215, b: 0 }, // Gold
      'coal.png': { r: 54, g: 54, b: 54 }, // Dark gray
      'stick.png': { r: 139, g: 69, b: 19 }, // Brown
      'apple.png': { r: 255, g: 0, b: 0 }, // Red
      'bread.png': { r: 210, g: 180, b: 140 }, // Tan
      
      // Entities
      'steve.png': { r: 204, g: 153, b: 102 }, // Skin tone
      'zombie.png': { r: 0, g: 100, b: 0 }, // Dark green
      'skeleton.png': { r: 245, g: 245, b: 220 }, // Beige
      'creeper.png': { r: 50, g: 205, b: 50 }, // Green
      'spider.png': { r: 139, g: 0, b: 0 }, // Dark red
      'cow.png': { r: 139, g: 69, b: 19 }, // Brown
      'pig.png': { r: 255, g: 192, b: 203 }, // Pink
      'chicken.png': { r: 255, g: 255, b: 255 } // White
    };

    for (const [filename, color] of Object.entries(placeholders)) {
      const filepath = this.getTexturePath(filename);
      
      try {
        // Check if texture already exists
        await fs.access(filepath);
        continue;
      } catch {
        // Create placeholder texture
        await sharp({
          create: {
            width: this.tileSize,
            height: this.tileSize,
            channels: 4,
            background: { r: color.r, g: color.g, b: color.b, alpha: 1 }
          }
        }).png().toFile(filepath);
      }
    }

    this.logger.info('Created placeholder textures');
  }

  getTexturePath(filename) {
    if (filename.includes('entity') || filename === 'steve.png') {
      return path.join(this.texturesDir, 'entities', filename);
    } else if (filename.includes('item') || ['diamond.png', 'iron_ingot.png', 'coal.png'].includes(filename)) {
      return path.join(this.texturesDir, 'items', filename);
    } else {
      return path.join(this.texturesDir, 'blocks', filename);
    }
  }

  async createTextureAtlases() {
    this.logger.info('Creating texture atlases...');
    
    // Create block atlas
    await this.createAtlas('blocks', this.blockTextures);
    
    // Create item atlas  
    await this.createAtlas('items', this.itemTextures);
    
    // Create entity atlas
    await this.createAtlas('entities', this.entityTextures);
  }

  async createAtlas(type, textureMap) {
    const atlas = sharp({
      create: {
        width: this.atlasSize,
        height: this.atlasSize,
        channels: 4,
        background: { r: 0, g: 0, b: 0, alpha: 0 }
      }
    });

    const composite = [];
    const atlasMapping = new Map();
    
    let x = 0, y = 0;
    let index = 0;

    for (const [id, textureFile] of textureMap) {
      const texturePath = typeof textureFile === 'string' 
        ? this.getTexturePath(textureFile)
        : this.getTexturePath(textureFile.top || textureFile.side || textureFile);

      try {
        // Add texture to composite
        composite.push({
          input: texturePath,
          left: x * this.tileSize,
          top: y * this.tileSize
        });

        // Store atlas coordinates
        atlasMapping.set(id, {
          x: x * this.tileSize,
          y: y * this.tileSize,
          width: this.tileSize,
          height: this.tileSize,
          u: x / this.tilesPerRow,
          v: y / this.tilesPerRow,
          u2: (x + 1) / this.tilesPerRow,
          v2: (y + 1) / this.tilesPerRow
        });

        x++;
        if (x >= this.tilesPerRow) {
          x = 0;
          y++;
        }
        index++;

      } catch (error) {
        this.logger.warn(`Failed to add texture ${id} to atlas:`, error.message);
      }
    }

    // Create the atlas
    const atlasPath = path.join(this.assetsDir, `${type}_atlas.png`);
    await atlas.composite(composite).png().toFile(atlasPath);
    
    // Store atlas info
    this.atlases.set(type, {
      path: atlasPath,
      mapping: atlasMapping,
      size: this.atlasSize,
      tileSize: this.tileSize
    });

    this.logger.info(`Created ${type} atlas with ${atlasMapping.size} textures`);
  }

  // Get texture coordinates for rendering
  getTextureCoords(type, id) {
    const atlas = this.atlases.get(type);
    if (!atlas) return null;
    
    return atlas.mapping.get(id);
  }

  // Get texture data for client-side rendering
  getTextureData() {
    const data = {
      atlases: {},
      mappings: {}
    };

    for (const [type, atlas] of this.atlases) {
      data.atlases[type] = {
        path: `/textures/${type}_atlas.png`,
        size: atlas.size,
        tileSize: atlas.tileSize
      };
      
      data.mappings[type] = {};
      for (const [id, coords] of atlas.mapping) {
        data.mappings[type][id] = coords;
      }
    }

    return data;
  }

  // Serve texture files via Express
  setupRoutes(app) {
    // Serve texture atlases
    app.get('/textures/:atlas', (req, res) => {
      const atlasName = req.params.atlas;
      const atlasPath = path.join(this.assetsDir, atlasName);
      
      res.sendFile(atlasPath, (err) => {
        if (err) {
          this.logger.warn(`Failed to serve texture atlas ${atlasName}:`, err.message);
          res.status(404).send('Texture not found');
        }
      });
    });

    // Serve individual textures (for debugging)
    app.get('/textures/:type/:texture', (req, res) => {
      const { type, texture } = req.params;
      const texturePath = path.join(this.texturesDir, type, texture);
      
      res.sendFile(texturePath, (err) => {
        if (err) {
          this.logger.warn(`Failed to serve texture ${type}/${texture}:`, err.message);
          res.status(404).send('Texture not found');
        }
      });
    });

    // Serve texture metadata
    app.get('/api/textures', (req, res) => {
      res.json(this.getTextureData());
    });
  }
}

module.exports = TextureManager;