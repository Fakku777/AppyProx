/**
 * Minecraft Chunk-based Rendering System
 * Handles rendering of Minecraft world chunks with proper block textures
 */

const EventEmitter = require('events');

class ChunkRenderer extends EventEmitter {
  constructor(logger, textureManager) {
    super();
    this.logger = logger.child ? logger.child('ChunkRenderer') : logger;
    this.textureManager = textureManager;
    
    // Chunk management
    this.loadedChunks = new Map(); // Map<chunkId, ChunkData>
    this.chunkCache = new Map(); // Rendered chunk cache
    this.renderQueue = new Set(); // Chunks pending render
    
    // Minecraft world constants
    this.CHUNK_SIZE = 16; // 16x16 blocks per chunk
    this.WORLD_HEIGHT = 384; // Y -64 to 320 in 1.18+
    this.SEA_LEVEL = 63;
    this.MIN_Y = -64;
    this.MAX_Y = 320;
    
    // Rendering settings
    this.viewDistance = 8; // Chunks to render around player
    this.renderDistance = 16; // Maximum chunk render distance
    this.blockSize = 16; // Pixels per block on map
    
    // Layer rendering
    this.currentLayer = this.SEA_LEVEL; // Y level to display
    this.layerMode = 'surface'; // 'surface', 'cave', 'fixed'
    this.showStructures = true;
    this.showEntities = true;
    this.showOres = true;
    
    // Performance settings
    this.maxRenderTime = 16; // Max ms per frame for rendering
    this.batchSize = 4; // Chunks to render per batch
    this.enableLOD = true; // Level of detail
    
    // Biome colors
    this.biomeColors = new Map([
      ['ocean', '#0077be'],
      ['plains', '#91bd59'],
      ['desert', '#fad5a5'],
      ['forest', '#477a3e'],
      ['taiga', '#598f42'],
      ['mountains', '#888888'],
      ['swamp', '#6a5d43'],
      ['jungle', '#3f6f43'],
      ['nether', '#8b0000'],
      ['end', '#d4c0d4']
    ]);
    
    this.initialized = false;
  }

  async initialize() {
    if (this.initialized) return;
    
    this.logger.info('Initializing chunk renderer...');
    
    try {
      // Ensure texture manager is ready
      if (!this.textureManager.initialized) {
        await this.textureManager.initialize();
      }
      
      // Setup rendering pipeline
      this.setupRenderingPipeline();
      
      this.initialized = true;
      this.logger.info('Chunk renderer initialized');
      
    } catch (error) {
      this.logger.error('Failed to initialize chunk renderer:', error);
      throw error;
    }
  }

  setupRenderingPipeline() {
    // Start background rendering loop
    this.startRenderLoop();
    
    // Setup chunk cleanup
    setInterval(() => {
      this.cleanupDistantChunks();
    }, 10000); // Cleanup every 10 seconds
  }

  // Load chunk data from server
  loadChunk(chunkX, chunkZ, chunkData) {
    const chunkId = this.getChunkId(chunkX, chunkZ);
    
    // Process and store chunk data
    const processedChunk = this.processChunkData(chunkX, chunkZ, chunkData);
    this.loadedChunks.set(chunkId, processedChunk);
    
    // Queue for rendering
    this.renderQueue.add(chunkId);
    
    this.logger.debug(`Loaded chunk ${chunkX}, ${chunkZ}`);
    this.emit('chunk_loaded', { chunkX, chunkZ, chunkId });
  }

  processChunkData(chunkX, chunkZ, rawData) {
    const chunk = {
      x: chunkX,
      z: chunkZ,
      id: this.getChunkId(chunkX, chunkZ),
      blocks: new Map(), // Map<blockPos, blockData>
      entities: [],
      structures: [],
      biome: rawData.biome || 'plains',
      heightMap: new Array(256), // 16x16 height map
      lastUpdate: Date.now(),
      rendered: false
    };
    
    // Process block data
    if (rawData.blocks) {
      for (let x = 0; x < 16; x++) {
        for (let z = 0; z < 16; z++) {
          for (let y = this.MIN_Y; y <= this.MAX_Y; y++) {
            const blockData = this.getBlockAt(rawData.blocks, x, y, z);
            if (blockData && blockData.type !== 'air') {
              const pos = this.getBlockPosition(x, y, z);
              chunk.blocks.set(pos, {
                type: blockData.type,
                x, y, z,
                properties: blockData.properties || {}
              });
            }
          }
          
          // Calculate height map
          chunk.heightMap[z * 16 + x] = this.calculateSurfaceHeight(rawData.blocks, x, z);
        }
      }
    }
    
    // Process entities
    if (rawData.entities) {
      chunk.entities = rawData.entities.map(entity => ({
        id: entity.id,
        type: entity.type,
        x: entity.x,
        y: entity.y,
        z: entity.z,
        yaw: entity.yaw || 0,
        pitch: entity.pitch || 0,
        metadata: entity.metadata || {}
      }));
    }
    
    return chunk;
  }

  getBlockAt(blocks, x, y, z) {
    // Implementation depends on chunk data format
    // This is a simplified version
    const index = y * 256 + z * 16 + x;
    return blocks[index] || { type: 'air' };
  }

  calculateSurfaceHeight(blocks, x, z) {
    // Find the highest non-air block
    for (let y = this.MAX_Y; y >= this.MIN_Y; y--) {
      const block = this.getBlockAt(blocks, x, y, z);
      if (block && block.type !== 'air') {
        return y;
      }
    }
    return this.MIN_Y;
  }

  startRenderLoop() {
    const renderFrame = () => {
      const startTime = Date.now();
      let processed = 0;
      
      // Process render queue
      for (const chunkId of this.renderQueue) {
        if (Date.now() - startTime > this.maxRenderTime) break;
        if (processed >= this.batchSize) break;
        
        this.renderChunk(chunkId);
        this.renderQueue.delete(chunkId);
        processed++;
      }
      
      // Continue loop
      setTimeout(renderFrame, 16); // ~60 FPS
    };
    
    renderFrame();
  }

  renderChunk(chunkId) {
    const chunk = this.loadedChunks.get(chunkId);
    if (!chunk) return;
    
    try {
      // Create off-screen canvas for chunk
      const canvas = this.createChunkCanvas();
      const ctx = canvas.getContext('2d');
      
      // Clear canvas
      ctx.fillStyle = this.biomeColors.get(chunk.biome) || '#91bd59';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      
      // Render based on current mode
      switch (this.layerMode) {
        case 'surface':
          this.renderSurfaceView(ctx, chunk);
          break;
        case 'cave':
          this.renderCaveView(ctx, chunk);
          break;
        case 'fixed':
          this.renderFixedLayer(ctx, chunk, this.currentLayer);
          break;
      }
      
      // Render entities if enabled
      if (this.showEntities) {
        this.renderEntities(ctx, chunk);
      }
      
      // Cache rendered chunk
      this.chunkCache.set(chunkId, {
        canvas,
        timestamp: Date.now(),
        layer: this.currentLayer,
        mode: this.layerMode
      });
      
      chunk.rendered = true;
      this.emit('chunk_rendered', { chunkId, chunk });
      
    } catch (error) {
      this.logger.warn(`Failed to render chunk ${chunkId}:`, error.message);
    }
  }

  createChunkCanvas() {
    const canvas = new OffscreenCanvas || document.createElement('canvas');
    canvas.width = this.CHUNK_SIZE * this.blockSize;
    canvas.height = this.CHUNK_SIZE * this.blockSize;
    return canvas;
  }

  renderSurfaceView(ctx, chunk) {
    // Load block texture atlas
    const blockAtlas = this.textureManager.atlases.get('blocks');
    if (!blockAtlas) return;
    
    // Create atlas image
    const atlasImage = new Image();
    atlasImage.src = blockAtlas.path;
    
    atlasImage.onload = () => {
      for (let x = 0; x < 16; x++) {
        for (let z = 0; z < 16; z++) {
          const surfaceY = chunk.heightMap[z * 16 + x];
          const blockPos = this.getBlockPosition(x, surfaceY, z);
          const block = chunk.blocks.get(blockPos);
          
          if (block) {
            this.drawBlock(ctx, atlasImage, blockAtlas, block, x * this.blockSize, z * this.blockSize);
          }
        }
      }
    };
  }

  renderCaveView(ctx, chunk) {
    // Render blocks at or below sea level
    const blockAtlas = this.textureManager.atlases.get('blocks');
    if (!blockAtlas) return;
    
    const atlasImage = new Image();
    atlasImage.src = blockAtlas.path;
    
    atlasImage.onload = () => {
      for (let x = 0; x < 16; x++) {
        for (let z = 0; z < 16; z++) {
          // Find the deepest interesting block (caves, ores)
          for (let y = this.SEA_LEVEL; y >= this.MIN_Y; y--) {
            const blockPos = this.getBlockPosition(x, y, z);
            const block = chunk.blocks.get(blockPos);
            
            if (block && this.isInterestingBlock(block.type)) {
              this.drawBlock(ctx, atlasImage, blockAtlas, block, x * this.blockSize, z * this.blockSize);
              break;
            }
          }
        }
      }
    };
  }

  renderFixedLayer(ctx, chunk, layer) {
    const blockAtlas = this.textureManager.atlases.get('blocks');
    if (!blockAtlas) return;
    
    const atlasImage = new Image();
    atlasImage.src = blockAtlas.path;
    
    atlasImage.onload = () => {
      for (let x = 0; x < 16; x++) {
        for (let z = 0; z < 16; z++) {
          const blockPos = this.getBlockPosition(x, layer, z);
          const block = chunk.blocks.get(blockPos);
          
          if (block && block.type !== 'air') {
            this.drawBlock(ctx, atlasImage, blockAtlas, block, x * this.blockSize, z * this.blockSize);
          }
        }
      }
    };
  }

  drawBlock(ctx, atlasImage, blockAtlas, block, screenX, screenY) {
    const textureCoords = this.textureManager.getTextureCoords('blocks', block.type);
    if (!textureCoords) return;
    
    // Draw block texture from atlas
    ctx.drawImage(
      atlasImage,
      textureCoords.x, textureCoords.y, textureCoords.width, textureCoords.height,
      screenX, screenY, this.blockSize, this.blockSize
    );
    
    // Add block variations/properties
    if (block.properties) {
      this.applyBlockProperties(ctx, block, screenX, screenY);
    }
  }

  applyBlockProperties(ctx, block, screenX, screenY) {
    // Handle block properties like rotation, waterlogged, etc.
    if (block.properties.waterlogged) {
      ctx.globalAlpha = 0.3;
      ctx.fillStyle = '#0077be';
      ctx.fillRect(screenX, screenY, this.blockSize, this.blockSize);
      ctx.globalAlpha = 1.0;
    }
  }

  renderEntities(ctx, chunk) {
    const entityAtlas = this.textureManager.atlases.get('entities');
    if (!entityAtlas) return;
    
    const atlasImage = new Image();
    atlasImage.src = entityAtlas.path;
    
    atlasImage.onload = () => {
      for (const entity of chunk.entities) {
        this.drawEntity(ctx, atlasImage, entityAtlas, entity);
      }
    };
  }

  drawEntity(ctx, atlasImage, entityAtlas, entity) {
    const textureCoords = this.textureManager.getTextureCoords('entities', entity.type);
    if (!textureCoords) return;
    
    // Calculate entity position on chunk
    const chunkX = entity.x % 16;
    const chunkZ = entity.z % 16;
    const screenX = chunkX * this.blockSize;
    const screenY = chunkZ * this.blockSize;
    
    // Draw entity
    ctx.save();
    ctx.translate(screenX + this.blockSize / 2, screenY + this.blockSize / 2);
    ctx.rotate(entity.yaw * Math.PI / 180);
    
    ctx.drawImage(
      atlasImage,
      textureCoords.x, textureCoords.y, textureCoords.width, textureCoords.height,
      -this.blockSize / 2, -this.blockSize / 2, this.blockSize, this.blockSize
    );
    
    ctx.restore();
  }

  isInterestingBlock(blockType) {
    // Blocks that are interesting for cave view
    const interesting = [
      'diamond_ore', 'iron_ore', 'gold_ore', 'coal_ore', 'redstone_ore',
      'lapis_ore', 'copper_ore', 'emerald_ore',
      'spawner', 'chest', 'diamond_block', 'gold_block'
    ];
    
    return interesting.includes(blockType);
  }

  // Utility methods
  getChunkId(chunkX, chunkZ) {
    return `${chunkX},${chunkZ}`;
  }

  getBlockPosition(x, y, z) {
    return `${x},${y},${z}`;
  }

  getChunkCoords(blockX, blockZ) {
    return {
      chunkX: Math.floor(blockX / 16),
      chunkZ: Math.floor(blockZ / 16)
    };
  }

  // Public API methods
  setViewDistance(distance) {
    this.viewDistance = Math.max(1, Math.min(32, distance));
    this.emit('view_distance_changed', this.viewDistance);
  }

  setRenderLayer(layer) {
    this.currentLayer = Math.max(this.MIN_Y, Math.min(this.MAX_Y, layer));
    this.invalidateAllChunks();
    this.emit('layer_changed', this.currentLayer);
  }

  setLayerMode(mode) {
    if (['surface', 'cave', 'fixed'].includes(mode)) {
      this.layerMode = mode;
      this.invalidateAllChunks();
      this.emit('mode_changed', this.layerMode);
    }
  }

  invalidateAllChunks() {
    // Mark all chunks for re-render
    for (const chunkId of this.loadedChunks.keys()) {
      this.renderQueue.add(chunkId);
    }
    this.chunkCache.clear();
  }

  getRenderedChunk(chunkX, chunkZ) {
    const chunkId = this.getChunkId(chunkX, chunkZ);
    return this.chunkCache.get(chunkId);
  }

  cleanupDistantChunks() {
    const now = Date.now();
    const maxAge = 300000; // 5 minutes
    
    for (const [chunkId, chunkData] of this.chunkCache.entries()) {
      if (now - chunkData.timestamp > maxAge) {
        this.chunkCache.delete(chunkId);
      }
    }
    
    // Remove very old loaded chunks
    for (const [chunkId, chunk] of this.loadedChunks.entries()) {
      if (now - chunk.lastUpdate > maxAge * 2) {
        this.loadedChunks.delete(chunkId);
        this.logger.debug(`Unloaded old chunk ${chunkId}`);
      }
    }
  }

  getStats() {
    return {
      loadedChunks: this.loadedChunks.size,
      cachedChunks: this.chunkCache.size,
      renderQueue: this.renderQueue.size,
      viewDistance: this.viewDistance,
      currentLayer: this.currentLayer,
      layerMode: this.layerMode
    };
  }

  async shutdown() {
    this.logger.info('Shutting down chunk renderer...');
    
    this.loadedChunks.clear();
    this.chunkCache.clear();
    this.renderQueue.clear();
    
    this.initialized = false;
    this.logger.info('Chunk renderer shut down');
  }
}

module.exports = ChunkRenderer;