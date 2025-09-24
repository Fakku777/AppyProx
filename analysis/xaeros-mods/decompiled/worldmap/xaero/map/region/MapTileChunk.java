/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2378
 *  net.minecraft.class_2874
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package xaero.map.region;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2874;
import net.minecraft.class_310;
import net.minecraft.class_638;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.file.IOHelper;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapBlock;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.OverlayManager;
import xaero.map.region.texture.LeafRegionTexture;
import xaero.map.world.MapDimension;

public class MapTileChunk {
    public static final int SIDE_LENGTH = 4;
    private MapRegion inRegion;
    private byte loadState = 0;
    private int X;
    private int Z;
    private MapTile[][] tiles = new MapTile[4][4];
    private byte[][] tileGridsCache = new byte[this.tiles.length][this.tiles.length];
    private LeafRegionTexture leafTexture;
    private boolean toUpdateBuffers;
    private boolean changed;
    private boolean includeInSave;
    private boolean hasHadTerrain;
    private boolean hasHighlights;
    private boolean hasHighlightsIfUndiscovered;

    public MapTileChunk(MapRegion r, int x, int z) {
        this.X = x;
        this.Z = z;
        this.inRegion = r;
        this.leafTexture = this.createLeafTexture();
    }

    protected LeafRegionTexture createLeafTexture() {
        return new LeafRegionTexture(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateBuffers(MapProcessor mapProcessor, BlockTintProvider blockTintProvider, OverlayManager overlayManager, boolean detailedDebug, BlockStateShortShapeCache blockStateShortShapeCache) {
        if (!class_310.method_1551().method_18854()) {
            throw new RuntimeException("Wrong thread!");
        }
        if (detailedDebug) {
            WorldMap.LOGGER.info("Updating buffers: " + this.X + " " + this.Z + " " + this.loadState);
        }
        class_638 world = mapProcessor.getWorld();
        class_2378<class_2248> blockRegistry = mapProcessor.getWorldBlockRegistry();
        class_2378<class_1959> biomeRegistry = mapProcessor.worldBiomeRegistry;
        class_2378<class_2874> dimensionTypes = mapProcessor.getWorldDimensionTypeRegistry();
        LeafRegionTexture leafTexture = this.getLeafTexture();
        leafTexture.resetTimer();
        MapRegion mapRegion = this.inRegion;
        synchronized (mapRegion) {
            leafTexture.setCachePrepared(false);
            leafTexture.setShouldDownloadFromPBO(false);
            this.inRegion.setAllCachePrepared(false);
        }
        leafTexture.prepareBuffer();
        int[] result = this.inRegion.getPixelResultBuffer();
        boolean hasLight = false;
        class_2338.class_2339 mutableGlobalPos = this.inRegion.getMutableGlobalPos();
        MapTileChunk prevTileChunk = this.getNeighbourTileChunk(0, -1, mapProcessor, false);
        MapTileChunk prevTileChunkDiagonal = this.getNeighbourTileChunk(-1, -1, mapProcessor, false);
        MapTileChunk prevTileChunkHorisontal = this.getNeighbourTileChunk(-1, 0, mapProcessor, false);
        MapDimension dim = mapProcessor.getMapWorld().getCurrentDimension();
        float shadowR = dim.getShadowR();
        float shadowG = dim.getShadowG();
        float shadowB = dim.getShadowB();
        ByteBuffer colorBuffer = leafTexture.getDirectColorBuffer();
        mapProcessor.getBiomeColorCalculator().prepare(WorldMap.settings.biomeBlending);
        for (int o = 0; o < this.tiles.length; ++o) {
            int offX = o * 16;
            for (int p = 0; p < this.tiles.length; ++p) {
                MapTile tile = this.tiles[o][p];
                if (tile == null || !tile.isLoaded()) continue;
                int caveStart = tile.getWrittenCaveStart();
                int caveDepth = tile.getWrittenCaveDepth();
                int offZ = p * 16;
                for (int z = 0; z < 16; ++z) {
                    int pixelZ = offZ + z;
                    for (int x = 0; x < 16; ++x) {
                        int pixelX = offX + x;
                        int effectiveHeight = leafTexture.getHeight(pixelX, pixelZ);
                        int effectiveTopHeight = leafTexture.getTopHeight(pixelX, pixelZ);
                        tile.getBlock(x, z).getPixelColour(result, mapProcessor.getMapWriter(), (class_1937)world, dim, blockRegistry, this, prevTileChunk, prevTileChunkDiagonal, prevTileChunkHorisontal, tile, x, z, caveStart, caveDepth, mutableGlobalPos, biomeRegistry, dimensionTypes, shadowR, shadowG, shadowB, blockTintProvider, mapProcessor, overlayManager, effectiveHeight, effectiveTopHeight, blockStateShortShapeCache);
                        this.putColour(pixelX, pixelZ, result[0], result[1], result[2], result[3], colorBuffer, 64);
                        if (result[3] == 0) continue;
                        hasLight = true;
                    }
                }
            }
        }
        leafTexture.postBufferUpdate(hasLight);
        this.toUpdateBuffers = false;
        leafTexture.setToUpload(true);
    }

    public void putColour(int x, int y, int red, int green, int blue, int alpha, ByteBuffer buffer, int size) {
        int pos = (y * size + x) * 4;
        buffer.putInt(pos, blue << 24 | green << 16 | red << 8 | alpha);
    }

    public MapTileChunk getNeighbourTileChunk(int directionX, int directionZ, MapProcessor mapProcessor, boolean crossRegion) {
        MapRegion prevTileChunkSrc;
        int maxCoord = 7;
        int chunkXInsideRegion = this.X & maxCoord;
        int chunkZInsideRegion = this.Z & maxCoord;
        MapTileChunk prevTileChunk = null;
        int chunkXInsideRegionPrev = chunkXInsideRegion + directionX;
        int chunkZInsideRegionPrev = chunkZInsideRegion + directionZ;
        int regDirectionX = 0;
        int regDirectionZ = 0;
        if (chunkXInsideRegionPrev < 0 || chunkXInsideRegionPrev > maxCoord) {
            regDirectionX = directionX;
            chunkXInsideRegionPrev &= maxCoord;
        }
        if (chunkZInsideRegionPrev < 0 || chunkZInsideRegionPrev > maxCoord) {
            regDirectionZ = directionZ;
            chunkZInsideRegionPrev &= maxCoord;
        }
        if ((prevTileChunkSrc = regDirectionX != 0 || regDirectionZ != 0 ? (!crossRegion ? null : mapProcessor.getLeafMapRegion(this.inRegion.getCaveLayer(), this.inRegion.getRegionX() + regDirectionX, this.inRegion.getRegionZ() + regDirectionZ, false)) : this.inRegion) != null) {
            prevTileChunk = prevTileChunkSrc.getChunk(chunkXInsideRegionPrev, chunkZInsideRegionPrev);
        }
        return prevTileChunk;
    }

    public void clean(MapProcessor mapProcessor) {
        for (int o = 0; o < 4; ++o) {
            for (int p = 0; p < 4; ++p) {
                MapTile tile = this.tiles[o][p];
                if (tile == null) continue;
                mapProcessor.getTilePool().addToPool(tile);
                this.tiles[o][p] = null;
            }
        }
        this.toUpdateBuffers = false;
        this.includeInSave = false;
    }

    public int getX() {
        return this.X;
    }

    public int getZ() {
        return this.Z;
    }

    public byte[][] getTileGridsCache() {
        return this.tileGridsCache;
    }

    public int getLoadState() {
        return this.loadState;
    }

    public void setLoadState(byte loadState) {
        this.loadState = loadState;
    }

    public MapTile getTile(int x, int z) {
        return this.tiles[x][z];
    }

    public void setTile(int x, int z, MapTile tile, BlockStateShortShapeCache blockStateShortShapeCache) {
        LeafRegionTexture leafTexture = this.leafTexture;
        if (tile != null) {
            boolean tileWasLoadedWithTopHeightValues = tile.getWorldInterpretationVersion() > 0;
            this.includeInSave = true;
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    int destX = x * 16 + i;
                    int destZ = z * 16 + j;
                    MapBlock mapBlock = tile.getBlock(i, j);
                    boolean subtractOneFromHeight = WorldMap.settings.adjustHeightForCarpetLikeBlocks && blockStateShortShapeCache.isShort(mapBlock.getState());
                    leafTexture.putHeight(destX, destZ, mapBlock.getEffectiveHeight(subtractOneFromHeight));
                    if (mapBlock.getState() == null || mapBlock.getState().method_26215() && mapBlock.getNumberOfOverlays() == 0 || !tileWasLoadedWithTopHeightValues && !mapBlock.getState().method_26215() && mapBlock.getNumberOfOverlays() > 0) {
                        leafTexture.removeTopHeight(destX, destZ);
                    } else {
                        leafTexture.putTopHeight(destX, destZ, mapBlock.getEffectiveTopHeight(subtractOneFromHeight));
                    }
                    leafTexture.setBiome(destX, destZ, mapBlock.getBiome());
                }
            }
        } else if (this.tiles[x][z] != null) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    int destX = x * 16 + i;
                    int destZ = z * 16 + j;
                    leafTexture.removeHeight(destX, destZ);
                    leafTexture.removeTopHeight(destX, destZ);
                    leafTexture.setBiome(destX, destZ, null);
                }
            }
        }
        this.tiles[x][z] = tile;
    }

    public MapRegion getInRegion() {
        return this.inRegion;
    }

    public boolean wasChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getTimer() {
        return this.leafTexture.getTimer();
    }

    public void decTimer() {
        this.leafTexture.decTimer();
    }

    public boolean includeInSave() {
        return this.includeInSave;
    }

    public void unincludeInSave() {
        this.includeInSave = false;
    }

    public void resetHeights() {
        this.leafTexture.resetHeights();
    }

    public boolean getToUpdateBuffers() {
        return this.toUpdateBuffers;
    }

    public void setToUpdateBuffers(boolean toUpdateBuffers) {
        this.toUpdateBuffers = toUpdateBuffers;
    }

    public LeafRegionTexture getLeafTexture() {
        return this.leafTexture;
    }

    public void writeCacheData(DataOutputStream output, byte[] usableBuffer, byte[] integerByteBuffer, LeveledRegion<LeafRegionTexture> inRegion2) throws IOException {
    }

    public void readCacheData(int minorSaveVersion, int majorSaveVersion, DataInputStream input, byte[] usableBuffer, byte[] integerByteBuffer, MapProcessor mapProcessor, int x, int y) throws IOException {
        if (minorSaveVersion == 4) {
            boolean hasBottomHeightValues;
            boolean bl = hasBottomHeightValues = input.read() == 1;
            if (hasBottomHeightValues) {
                input.readByte();
                byte[] bottomHeights = new byte[64];
                IOHelper.readToBuffer(bottomHeights, 64, input);
                LeafRegionTexture leafTexture = this.leafTexture;
                for (int i = 0; i < 64; ++i) {
                    leafTexture.putHeight(i, 63, bottomHeights[i]);
                }
            }
        } else if (minorSaveVersion >= 5 && minorSaveVersion < 13) {
            input.readInt();
            byte[] heights = new byte[64];
            LeafRegionTexture leafTexture = this.leafTexture;
            for (int hx = 0; hx < 64; ++hx) {
                IOHelper.readToBuffer(heights, 64, input);
                for (int hz = 0; hz < 64; ++hz) {
                    leafTexture.putHeight(hx, hz, heights[hz]);
                }
            }
        }
        if (minorSaveVersion >= 4 && minorSaveVersion < 10 && (this.Z & 7) == 0) {
            input.readByte();
        }
        this.loadState = (byte)2;
    }

    public String toString() {
        return this.getX() + " " + this.getZ();
    }

    public boolean hasHadTerrain() {
        return this.hasHadTerrain;
    }

    public void setHasHadTerrain() {
        this.hasHadTerrain = true;
        this.inRegion.setHasHadTerrain();
    }

    public void unsetHasHadTerrain() {
        this.hasHadTerrain = false;
    }

    public boolean hasHighlights() {
        return this.hasHighlights;
    }

    public void setHasHighlights(boolean hasHighlights) {
        this.hasHighlights = hasHighlights;
    }

    public boolean hasHighlightsIfUndiscovered() {
        return this.hasHighlightsIfUndiscovered;
    }

    public void setHasHighlightsIfUndiscovered(boolean hasHighlightsIfUndiscovered) {
        this.hasHighlightsIfUndiscovered = hasHighlightsIfUndiscovered;
    }
}

