/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package xaero.map.region.texture;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.class_310;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.exception.OpenGLException;
import xaero.map.graphics.TextureUploader;
import xaero.map.highlight.DimensionHighlighterHandler;
import xaero.map.misc.Misc;
import xaero.map.pool.buffer.PoolTextureDirectBufferUnit;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.OverlayManager;
import xaero.map.region.texture.BranchTextureRenderer;
import xaero.map.region.texture.RegionTexture;

public class LeafRegionTexture
extends RegionTexture<LeafRegionTexture> {
    private MapTileChunk tileChunk;
    protected PoolTextureDirectBufferUnit highlitColorBuffer;

    public LeafRegionTexture(MapTileChunk tileChunk) {
        super(tileChunk.getInRegion());
        this.tileChunk = tileChunk;
    }

    public void postBufferUpdate(boolean hasLight) {
        this.colorBufferFormat = null;
        this.bufferHasLight = hasLight;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void preUpload(MapProcessor mapProcessor, BlockTintProvider blockTintProvider, OverlayManager overlayManager, LeveledRegion<LeafRegionTexture> leveledRegion, boolean detailedDebug, BlockStateShortShapeCache blockStateShortShapeCache) {
        MapRegion region = (MapRegion)leveledRegion;
        if (this.tileChunk.getToUpdateBuffers() && !mapProcessor.isWritingPaused()) {
            Object object = region.writerThreadPauseSync;
            synchronized (object) {
                if (!region.isWritingPaused()) {
                    this.tileChunk.updateBuffers(mapProcessor, blockTintProvider, overlayManager, detailedDebug, blockStateShortShapeCache);
                }
            }
        }
    }

    @Override
    public void postUpload(MapProcessor mapProcessor, LeveledRegion<LeafRegionTexture> leveledRegion, boolean cleanAndCacheRequestsBlocked) {
        MapRegion region = (MapRegion)leveledRegion;
        if (!(region.getLoadState() < 2 || region.getLoadState() != 3 && (region.isBeingWritten() || region.getLastVisited() != 0L && region.getTimeSinceVisit() <= 1000L) || cleanAndCacheRequestsBlocked || this.tileChunk.getToUpdateBuffers() || this.tileChunk.getLoadState() == 3)) {
            region.setLoadState((byte)3);
            this.tileChunk.setLoadState((byte)3);
            this.tileChunk.clean(mapProcessor);
        }
    }

    @Override
    public boolean canUpload() {
        return this.tileChunk.getLoadState() >= 2;
    }

    @Override
    public boolean isUploaded() {
        return super.isUploaded() && !this.tileChunk.getToUpdateBuffers();
    }

    @Override
    public boolean hasSourceData() {
        return this.tileChunk.getLoadState() != 3;
    }

    @Override
    protected long uploadNonCache(DimensionHighlighterHandler highlighterHandler, TextureUploader textureUploader, BranchTextureRenderer unused) {
        PoolTextureDirectBufferUnit colorBufferToUpload = this.applyHighlights(highlighterHandler, this.colorBuffer, true);
        if (this.textureVersion == -1) {
            this.updateTextureVersion(this.bufferedTextureVersion != -1 ? this.bufferedTextureVersion + 1 : 1 + (int)(Math.random() * 1000000.0));
        } else {
            this.updateTextureVersion(this.textureVersion + 1);
        }
        if (colorBufferToUpload == null) {
            return 0L;
        }
        this.writeToUnpackPBO(0, colorBufferToUpload);
        this.textureHasLight = this.bufferHasLight;
        this.colorBuffer.getDirectBuffer().position(0);
        this.colorBufferFormat = DEFAULT_INTERNAL_FORMAT;
        this.bufferedTextureVersion = this.textureVersion;
        boolean subsequent = this.glColorTexture != null;
        this.bindColorTexture(true);
        OpenGLException.checkGLError();
        if (this.unpackPbo[0] == null) {
            return 0L;
        }
        long totalEstimatedTime = subsequent ? textureUploader.requestSubsequentNormal(this.glColorTexture, this.unpackPbo[0], 0, 64, 64, 0, 0L, 0, 0) : textureUploader.requestNormal(this.glColorTexture, this.unpackPbo[0], 0, DEFAULT_INTERNAL_FORMAT, 64, 64, 0, 0L);
        boolean toUploadImmediately = this.tileChunk.getInRegion().isBeingWritten();
        if (toUploadImmediately) {
            textureUploader.finishNewestRequestImmediately();
        }
        return totalEstimatedTime;
    }

    @Override
    protected PoolTextureDirectBufferUnit applyHighlights(DimensionHighlighterHandler highlighterHandler, PoolTextureDirectBufferUnit colorBuffer, boolean separateBuffer) {
        if (!this.tileChunk.hasHighlights()) {
            return colorBuffer;
        }
        colorBuffer = super.applyHighlights(highlighterHandler, colorBuffer, separateBuffer);
        int startChunkX = this.tileChunk.getX() << 2;
        int startChunkZ = this.tileChunk.getZ() << 2;
        boolean prepared = false;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int chunkX = startChunkX + i;
                int chunkZ = startChunkZ + j;
                boolean discovered = this.getHeight(i << 4, j << 4) != Short.MAX_VALUE;
                PoolTextureDirectBufferUnit result = highlighterHandler.applyChunkHighlightColors(chunkX, chunkZ, i, j, colorBuffer, this.highlitColorBuffer, prepared, discovered, separateBuffer);
                if (result == null || !separateBuffer) continue;
                this.highlitColorBuffer = result;
                prepared = true;
            }
        }
        if (prepared) {
            return this.highlitColorBuffer;
        }
        return colorBuffer;
    }

    @Override
    public void postBufferWrite(PoolTextureDirectBufferUnit buffer) {
        super.postBufferWrite(buffer);
        if (buffer == this.highlitColorBuffer) {
            this.highlitColorBuffer = null;
            if (!WorldMap.textureDirectBufferPool.addToPool(buffer)) {
                WorldMap.bufferDeallocator.deallocate(buffer.getDirectBuffer(), WorldMap.settings.debug);
            }
        }
    }

    @Override
    protected void updateTextureVersion(int newVersion) {
        super.updateTextureVersion(newVersion);
        this.region.updateLeafTextureVersion(this.tileChunk.getX() & 7, this.tileChunk.getZ() & 7, newVersion);
    }

    @Override
    public void addDebugLines(List<String> lines) {
        super.addDebugLines(lines);
        lines.add(this.tileChunk.getX() + " " + this.tileChunk.getZ());
        lines.add("loadState: " + this.tileChunk.getLoadState());
        lines.add(String.format("changed: %s include: %s terrain: %s highlights: %s toUpdateBuffers: %s", this.tileChunk.wasChanged(), this.tileChunk.includeInSave(), this.tileChunk.hasHadTerrain(), this.tileChunk.hasHighlights(), this.tileChunk.getToUpdateBuffers()));
    }

    @Override
    public void writeCacheMapData(DataOutputStream output, byte[] usableBuffer, byte[] integerByteBuffer, LeveledRegion<LeafRegionTexture> inRegion) throws IOException {
        super.writeCacheMapData(output, usableBuffer, integerByteBuffer, inRegion);
        this.tileChunk.writeCacheData(output, usableBuffer, integerByteBuffer, inRegion);
    }

    @Override
    public void readCacheData(int minorSaveVersion, int majorSaveVersion, DataInputStream input, byte[] usableBuffer, byte[] integerByteBuffer, LeveledRegion<LeafRegionTexture> inRegion, MapProcessor mapProcessor, int x, int y, boolean leafShouldAffectBranches) throws IOException {
        super.readCacheData(minorSaveVersion, majorSaveVersion, input, usableBuffer, integerByteBuffer, inRegion, mapProcessor, x, y, leafShouldAffectBranches);
        this.tileChunk.readCacheData(minorSaveVersion, majorSaveVersion, input, usableBuffer, integerByteBuffer, mapProcessor, x, y);
        if (leafShouldAffectBranches) {
            this.colorBufferFormat = null;
        }
        if (this.colorBuffer != null) {
            this.tileChunk.setHasHadTerrain();
        }
    }

    public void resetHeights() {
        Misc.clearHeightsData1024(this.heightValues.getData());
        Misc.clearHeightsData1024(this.topHeightValues.getData());
    }

    @Override
    public boolean shouldBeUsedForBranchUpdate(int usedVersion) {
        return this.tileChunk.getLoadState() != 1 && super.shouldBeUsedForBranchUpdate(usedVersion);
    }

    @Override
    public boolean shouldHaveContentForBranchUpdate() {
        return this.tileChunk.getLoadState() > 0 && super.shouldHaveContentForBranchUpdate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void deleteTexturesAndBuffers() {
        if (!class_310.method_1551().method_18854()) {
            LeveledRegion leveledRegion = this.region.getLevel() == 3 ? this.region : this.region.getParent();
            synchronized (leveledRegion) {
                LeveledRegion leveledRegion2 = this.region;
                synchronized (leveledRegion2) {
                    this.tileChunk.setLoadState((byte)0);
                }
            }
        }
        super.deleteTexturesAndBuffers();
    }

    @Override
    public void prepareBuffer() {
        super.prepareBuffer();
        this.tileChunk.setHasHadTerrain();
    }

    public MapTileChunk getTileChunk() {
        return this.tileChunk;
    }

    @Override
    public boolean shouldIncludeInCache() {
        return this.tileChunk.hasHadTerrain();
    }

    public void requestHighlightOnlyUpload() {
        this.resetBiomes();
        this.colorBufferFormat = DEFAULT_INTERNAL_FORMAT;
        this.bufferedTextureVersion = this.tileChunk.getInRegion().getTargetHighlightsHash();
        this.setToUpload(true);
        if (this.tileChunk.getLoadState() < 2) {
            this.tileChunk.setLoadState((byte)2);
        }
    }
}

