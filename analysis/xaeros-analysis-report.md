# Xaeros WorldMap & Minimap Analysis Report

## Overview

After successfully decompiling and analyzing the Xaeros WorldMap (v1.39.13) and Minimap (v25.2.12) mods for Minecraft 1.21.8, this report provides a comprehensive breakdown of their architecture, rendering systems, and core features that can be adapted for AppyProx's web-based interface.

## Core Architecture

### 1. **Data Structure & Organization**

```
MapRegion (512x512 blocks)
├── MapTileChunk[8][8] (64x64 blocks each)
│   └── MapTile[4][4] (16x16 blocks each)
│       └── MapBlock (individual block data)
└── RegionTexture (64x64 pixel OpenGL texture)
```

**Key Findings:**
- World divided into 512x512 block regions
- Each region contains 8x8 chunks (64 chunks total)
- Each chunk contains 4x4 tiles (16 tiles per chunk)
- Each tile represents 16x16 blocks
- Rendered as 64x64 pixel textures (1 pixel per block at base zoom)

### 2. **Texture & Rendering System**

**RegionTexture.java Analysis:**
- Uses OpenGL textures with RGBA8 format
- 64x64 pixel base resolution per region
- Supports multiple mipmap levels for zoom
- Implements Pixel Buffer Objects (PBO) for efficient texture uploads
- Height data stored in ConsistentBitArray (13 bits, 4096 elements)
- Biome data stored separately in RegionTextureBiomes

**Rendering Pipeline:**
1. **MapTileChunk.updateBuffers()** - Generates color data for 64x64 pixels
2. **RegionTexture.uploadBuffer()** - Uploads to GPU via PBO
3. **GuiMap.render()** - Renders textured quads with matrix transformations
4. **MapRenderHelper** - Utility functions for drawing UI elements

### 3. **Multi-Level Zoom System**

**LeveledRegion Hierarchy:**
- Level 0: Base resolution (1 block = 1 pixel)
- Level 1: 2x2 blocks = 1 pixel  
- Level 2: 4x4 blocks = 1 pixel
- Level 3: 8x8 blocks = 1 pixel

**BranchLeveledRegion:**
- Manages multiple zoom levels
- Caches pre-rendered textures for performance
- Handles texture loading/unloading based on zoom level

### 4. **Configuration & Settings System**

**ModSettings.java Features:**
```java
// Visual Settings
public boolean lighting = true;
public boolean terrainSlopes = 2; // Off, Legacy, 3D, 2D
public boolean terrainDepth = true;
public boolean biomeBlending = true;
public boolean flowers = true;
public boolean coordinates = true;
public boolean hoveredBiome = true;

// Map Behavior  
public boolean loadChunks = true;
public boolean updateChunks = true;
public boolean caveMapsAllowed = true;
public int caveModeDepth = 30;
public boolean footsteps = true;

// UI Elements
public boolean renderArrow = true;
public boolean displayZoom = true;
public boolean zoomButtons = true;
public boolean waypointBackgrounds = true;
public float worldmapWaypointsScale = 1.0f;
```

## Key Systems for AppyProx Integration

### 1. **Chunk-Based Rendering**

**Implementation Strategy:**
```javascript
class XaerosChunkRenderer {
    constructor() {
        this.chunkCache = new Map(); // chunkKey -> ChunkTexture
        this.regionSize = 512; // blocks per region
        this.textureSize = 64; // pixels per region texture
    }
    
    renderChunk(chunkX, chunkZ, zoomLevel) {
        const regionX = Math.floor(chunkX / 8);
        const regionZ = Math.floor(chunkZ / 8);
        const regionKey = `${regionX}_${regionZ}_${zoomLevel}`;
        
        if (!this.chunkCache.has(regionKey)) {
            this.loadRegionTexture(regionX, regionZ, zoomLevel);
        }
        
        return this.chunkCache.get(regionKey);
    }
}
```

### 2. **Texture Management System**

**From RegionTexture.java Analysis:**
```javascript
class TextureManager {
    createRegionTexture(width = 64, height = 64) {
        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        return canvas.getContext('2d');
    }
    
    updateTextureData(texture, blockData, biomeData, heightData) {
        // Convert Minecraft block data to RGBA pixels
        const imageData = texture.createImageData(64, 64);
        
        for (let y = 0; y < 64; y++) {
            for (let x = 0; x < 64; x++) {
                const blockId = blockData[y * 64 + x];
                const biome = biomeData[y * 64 + x];
                const height = heightData[y * 64 + x];
                
                const color = this.getBlockColor(blockId, biome, height);
                const pixelIndex = (y * 64 + x) * 4;
                
                imageData.data[pixelIndex] = color.r;
                imageData.data[pixelIndex + 1] = color.g; 
                imageData.data[pixelIndex + 2] = color.b;
                imageData.data[pixelIndex + 3] = color.a;
            }
        }
        
        texture.putImageData(imageData, 0, 0);
    }
}
```

### 3. **Camera & Zoom System**

**From GuiMap.java:**
```javascript
class XaerosCamera {
    constructor() {
        this.cameraX = 0;
        this.cameraZ = 0;
        this.scale = 3.0; // destScale equivalent
        this.userScale = 3.0;
        this.zoomStep = 1.2;
        this.minZoom = 0.0625;
        this.maxZoom = 50.0;
    }
    
    changeZoom(factor, discreteMode = false) {
        if (discreteMode) {
            // Snap to integer scales like Xaeros
            if (this.scale >= 1.0) {
                this.scale = factor > 0 ? Math.ceil(this.scale) : Math.floor(this.scale);
                if (factor > 0) this.scale += 1;
                else this.scale -= 1;
            } else {
                // Handle fractional zooms with powers of 2
                const reversedScale = 1.0 / this.scale;
                const log2 = Math.log2(reversedScale);
                this.scale = 1.0 / Math.pow(2, Math.floor(log2) + (factor > 0 ? -1 : 1));
            }
        } else {
            this.scale *= Math.pow(this.zoomStep, factor);
        }
        
        this.scale = Math.max(this.minZoom, Math.min(this.maxZoom, this.scale));
    }
}
```

### 4. **Waypoint System**

**From Minimap analysis:**
```javascript
class XaerosWaypoint {
    constructor(name, x, y, z, color, symbol) {
        this.name = name;
        this.x = x;
        this.y = y; 
        this.z = z;
        this.color = color;
        this.symbol = symbol;
        this.enabled = true;
        this.showIngame = true;
        this.global = false;
    }
    
    // Distance calculation like Xaeros
    getDistanceFrom(playerX, playerZ) {
        const dx = this.x - playerX;
        const dz = this.z - playerZ;
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    // Render waypoint on map
    renderOnMap(ctx, cameraX, cameraZ, scale) {
        const screenX = (this.x - cameraX) * scale;
        const screenZ = (this.z - cameraZ) * scale;
        
        // Draw waypoint symbol and name
        this.drawSymbol(ctx, screenX, screenZ);
        this.drawLabel(ctx, screenX, screenZ + 15);
    }
}
```

### 5. **UI Layout System**

**From GuiMap.java button layout:**
```javascript
class XaerosUI {
    initButtons(width, height) {
        const buttons = {
            // Right side vertical button stack
            settings: { x: width - 30, y: 0, w: 30, h: 30 },
            waypoints: { x: width - 20, y: height - 20, w: 20, h: 20 },
            players: { x: width - 20, y: height - 40, w: 20, h: 20 },
            radar: { x: width - 20, y: height - 60, w: 20, h: 20 },
            claims: { x: width - 20, y: height - 80, w: 20, h: 20 },
            export: { x: width - 20, y: height - 100, w: 20, h: 20 },
            controls: { x: width - 20, y: height - 120, w: 20, h: 20 },
            
            // Zoom controls (conditional)
            zoomIn: { x: width - 20, y: height - 160, w: 20, h: 20 },
            zoomOut: { x: width - 20, y: height - 140, w: 20, h: 20 },
            
            // Cave mode toggle
            caveMode: { x: 0, y: height - 40, w: 20, h: 20 },
            dimensionToggle: { x: 0, y: height - 60, w: 20, h: 20 }
        };
        
        return buttons;
    }
}
```

## Advanced Features

### 1. **Cave Mode System**

- **Cave Depth Control:** `caveModeDepth = 30` blocks
- **Cave Start Detection:** Automatic Y-level calculation
- **Layered Rendering:** Separate cave layer rendering
- **Toggle Timer:** `caveModeToggleTimer = 1000ms`

### 2. **Lighting & Terrain**

- **Terrain Slopes:** 4 modes (Off, Legacy, 3D, 2D)
- **Terrain Depth:** Height-based shading  
- **Block Lighting:** Light level consideration
- **Biome Blending:** Color blending between biomes

### 3. **Multi-World Support**

- **World Differentiation:** Server address-based separation
- **Dimension Support:** Overworld, Nether, End
- **Multiworld ID:** Custom world identification

### 4. **Performance Optimizations**

- **Texture Caching:** LRU cache for region textures
- **Level-of-Detail:** Zoom-based texture resolution
- **Async Loading:** Background region loading
- **Memory Management:** Automatic texture cleanup

## Implementation Roadmap for AppyProx

### Phase 1: Core Rendering
1. **Chunk-based texture system** - ✅ Partially implemented
2. **Multi-level zoom with LOD** - Needs implementation
3. **Camera controls matching Xaeros** - ✅ Basic version exists
4. **Block color mapping system** - ✅ Implemented with placeholders

### Phase 2: UI Replication  
1. **Authentic button layout** - Not started
2. **Tooltip system** - Basic version exists
3. **Settings panel** - Not started
4. **Context menus** - Not started

### Phase 3: Advanced Features
1. **Waypoint system** - Not started
2. **Player tracking** - Partially implemented
3. **Cave mode** - Not started
4. **Claims visualization** - Not started

### Phase 4: Performance & Polish
1. **Texture atlas optimization** - Basic implementation
2. **WebGL acceleration** - Not started  
3. **Progressive loading** - Not started
4. **Memory optimization** - Not started

## Conclusion

The Xaeros mod analysis reveals a sophisticated, multi-layered rendering system built around efficient texture management and hierarchical world organization. The key architectural patterns - chunk-based regions, multi-level textures, and modular UI components - can be successfully adapted to AppyProx's web-based environment.

The most critical systems to replicate are:
1. **Region-based texture management** (64x64 pixel textures per 512x512 block region)
2. **Multi-level zoom system** with LOD textures
3. **Authentic UI layout** with right-side button stack
4. **Waypoint integration** with player tracking

This analysis provides the foundation for implementing a truly authentic Minecraft mapping experience within AppyProx's web interface.