/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.region;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import xaero.map.MapProcessor;
import xaero.map.file.MapSaveLoad;
import xaero.map.misc.Misc;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapLayer;
import xaero.map.region.MapRegion;
import xaero.map.region.texture.BranchRegionTexture;
import xaero.map.region.texture.RegionTexture;
import xaero.map.world.MapDimension;

public class BranchLeveledRegion
extends LeveledRegion<BranchRegionTexture> {
    public static final int CHILD_LENGTH_IN_TEXTURES = 4;
    public static final int MAX_COORD_WITHIN_CHILD = 3;
    private boolean loaded;
    private boolean freed;
    private boolean readyForUpdates;
    private BranchRegionTexture[][] textures;
    private LeveledRegion<?>[][] children = new LeveledRegion[2][2];
    private boolean shouldCheckForUpdates;
    private boolean downloading;
    private long lastUpdateTime;
    private int updateCountSinceSave;

    public BranchLeveledRegion(String worldId, String dimId, String mwId, MapDimension dim, int level, int leveledX, int leveledZ, int caveLayer, BranchLeveledRegion parent) {
        super(worldId, dimId, mwId, dim, level, leveledX, leveledZ, caveLayer, parent);
        this.reset();
    }

    private void reset() {
        this.shouldCache = false;
        this.recacheHasBeenRequested = false;
        this.reloadHasBeenRequested = false;
        this.metaLoaded = false;
        this.loaded = false;
        this.freed = false;
        this.textures = null;
        this.downloading = false;
        this.updateCountSinceSave = 0;
        this.lastUpdateTime = 0L;
        this.readyForUpdates = false;
        this.resetBiomePalette();
    }

    private boolean checkAndTrackRegionExistence(MapProcessor mapProcessor, int x, int z) {
        MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        MapLayer mapLayer = mapDimension.getLayeredMapRegions().getLayer(this.caveLayer);
        if (mapLayer.regionDetectionExists(x, z)) {
            return true;
        }
        if (mapProcessor.getMapSaveLoad().isRegionDetectionComplete() && mapDimension.getHighlightHandler().shouldApplyRegionHighlights(x, z, false)) {
            mapLayer.getRegionHighlightExistenceTracker().track(x, z);
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void checkForUpdates(MapProcessor mapProcessor, boolean prevWaitingForBranchCache, boolean[] waitingForBranchCache, ArrayList<BranchLeveledRegion> branchRegionBuffer, int viewedLevel, int minViewedLeafX, int minViewedLeafZ, int maxViewedLeafX, int maxViewedLeafZ) {
        super.checkForUpdates(mapProcessor, prevWaitingForBranchCache, waitingForBranchCache, branchRegionBuffer, viewedLevel, minViewedLeafX, minViewedLeafZ, maxViewedLeafX, maxViewedLeafZ);
        if (!this.isLoaded()) {
            if (this.parent != null) {
                this.parent.setShouldCheckForUpdatesRecursive(true);
            }
            if (this.level == viewedLevel) {
                waitingForBranchCache[0] = true;
            }
            if (!this.recacheHasBeenRequested() && !this.reloadHasBeenRequested()) {
                this.calculateSortingDistance();
                Misc.addToListOfSmallest(10, branchRegionBuffer, this);
            }
            return;
        }
        if (!this.readyForUpdates || prevWaitingForBranchCache) {
            if (this.parent != null) {
                this.parent.setShouldCheckForUpdatesRecursive(true);
            }
            return;
        }
        BranchLeveledRegion branchLeveledRegion = this;
        synchronized (branchLeveledRegion) {
            if (this.downloading || this.recacheHasBeenRequested) {
                if (this.parent != null) {
                    this.parent.setShouldCheckForUpdatesRecursive(true);
                }
                return;
            }
            if (!this.shouldCheckForUpdates) {
                return;
            }
            this.shouldCheckForUpdates = false;
            boolean shouldRevisitParent = false;
            boolean outdated = false;
            int level = this.level;
            int regionX = this.regionX;
            int regionZ = this.regionZ;
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    BranchLeveledRegion childRegion = this.children[i][j];
                    int globalChildRegionX = regionX << 1 | i;
                    int globalChildRegionZ = regionZ << 1 | j;
                    int textureOffsetX = i * 4;
                    int textureOffsetY = j * 4;
                    boolean outdatedWithChild = false;
                    boolean outdatedWithLeaves = false;
                    boolean childRegionIsLoaded = childRegion != null && ((LeveledRegion)childRegion).isLoaded() || childRegion == null && level == 1 && !this.checkAndTrackRegionExistence(mapProcessor, globalChildRegionX, globalChildRegionZ);
                    for (int o = 0; o < 4; ++o) {
                        for (int p = 0; p < 4; ++p) {
                            int textureX = textureOffsetX + o;
                            int textureY = textureOffsetY + p;
                            BranchRegionTexture texture = this.getTexture(textureX, textureY);
                            int textureVersion = 0;
                            if (texture != null && (textureVersion = texture.getTextureVersion()) == -1) {
                                textureVersion = texture.getBufferedTextureVersion();
                            }
                            boolean leavesLoaded = true;
                            int leafTextureSum = -1;
                            int minLeafTextureX = (regionX << 3) + textureX << level;
                            int minLeafTextureZ = (regionZ << 3) + textureY << level;
                            int maxLeafTextureX = minLeafTextureX + (1 << level) - 1;
                            int maxLeafTextureZ = minLeafTextureZ + (1 << level) - 1;
                            int minLeafRegX = minLeafTextureX >> 3;
                            int minLeafRegZ = minLeafTextureZ >> 3;
                            int maxLeafRegX = maxLeafTextureX >> 3;
                            int maxLeafRegZ = maxLeafTextureZ >> 3;
                            block10: for (int leafRegX = minLeafRegX; leafRegX <= maxLeafRegX; ++leafRegX) {
                                for (int leafRegZ = minLeafRegZ; leafRegZ <= maxLeafRegZ; ++leafRegZ) {
                                    MapRegion leafRegion = mapProcessor.getLeafMapRegion(this.caveLayer, leafRegX, leafRegZ, false);
                                    if (leafRegion == null && this.checkAndTrackRegionExistence(mapProcessor, leafRegX, leafRegZ)) {
                                        leavesLoaded = false;
                                        break block10;
                                    }
                                    if (leafRegion == null) continue;
                                    MapRegion mapRegion = leafRegion;
                                    synchronized (mapRegion) {
                                        if (leafRegion.isMetaLoaded() || leafRegion.isLoaded()) {
                                            if (leafTextureSum == -1) {
                                                leafTextureSum = this.leafTextureVersionSum[textureX][textureY];
                                            }
                                        } else {
                                            leavesLoaded = false;
                                            break block10;
                                        }
                                        continue;
                                    }
                                }
                            }
                            if (leavesLoaded && leafTextureSum == -1) {
                                leafTextureSum = 0;
                                if (textureVersion != 0 && level > 1) {
                                    if (childRegion == null) {
                                        BranchLeveledRegion branchLeveledRegion2 = new BranchLeveledRegion(this.worldId, this.dimId, this.mwId, this.dim, level - 1, globalChildRegionX, globalChildRegionZ, this.caveLayer, this);
                                        this.children[i][j] = branchLeveledRegion2;
                                        childRegion = branchLeveledRegion2;
                                        this.dim.getLayeredMapRegions().addListRegion(childRegion);
                                        childRegionIsLoaded = false;
                                    }
                                    ((BranchLeveledRegion)childRegion).setShouldCheckForUpdatesRecursive(true);
                                }
                            }
                            if (leavesLoaded && textureVersion != leafTextureSum) {
                                outdatedWithLeaves = true;
                            }
                            if (!childRegionIsLoaded) continue;
                            int childTextureOffsetX = o << 1;
                            int childTextureOffsetY = p << 1;
                            RegionTexture<?> childTopLeft = null;
                            RegionTexture<?> childTopRight = null;
                            RegionTexture<?> childBottomLeft = null;
                            RegionTexture<?> childBottomRight = null;
                            if (childRegion != null) {
                                childTopLeft = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX, childTextureOffsetY);
                                childTopRight = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX + 1, childTextureOffsetY);
                                childBottomLeft = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX, childTextureOffsetY + 1);
                                childBottomRight = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX + 1, childTextureOffsetY + 1);
                            }
                            if (childTopLeft != null || childTopRight != null || childBottomLeft != null || childBottomRight != null) {
                                boolean newTexture;
                                boolean bl = newTexture = texture == null;
                                if (newTexture) {
                                    texture = new BranchRegionTexture(this);
                                }
                                if (!texture.checkForUpdates(childTopLeft, childTopRight, childBottomLeft, childBottomRight, childRegion)) continue;
                                outdatedWithChild = true;
                                if (!newTexture) continue;
                                this.putTexture(textureX, textureY, texture);
                                continue;
                            }
                            if (texture == null) continue;
                            this.putTexture(textureX, textureY, (BranchRegionTexture)null);
                            texture.deleteTexturesAndBuffers();
                            this.countTextureUpdate();
                            outdatedWithChild = true;
                            shouldRevisitParent = true;
                        }
                    }
                    if ((outdatedWithLeaves || outdatedWithChild) && childRegion != null) {
                        ((LeveledRegion)childRegion).checkForUpdates(mapProcessor, prevWaitingForBranchCache, waitingForBranchCache, branchRegionBuffer, viewedLevel, minViewedLeafX, minViewedLeafZ, maxViewedLeafX, maxViewedLeafZ);
                    }
                    if (!outdatedWithChild) continue;
                    outdated = true;
                }
            }
            if (outdated && this.freed) {
                this.freed = false;
                mapProcessor.addToProcess(this);
            }
            if (shouldRevisitParent && this.parent != null) {
                this.parent.setShouldCheckForUpdatesRecursive(true);
            }
        }
    }

    @Override
    public void putTexture(int x, int y, BranchRegionTexture texture) {
        this.textures[x][y] = texture;
    }

    @Override
    public BranchRegionTexture getTexture(int x, int y) {
        return this.textures[x][y];
    }

    @Override
    public boolean hasTextures() {
        return this.textures != null;
    }

    public boolean isEmpty() {
        for (int i = 0; i < this.children.length; ++i) {
            for (int j = 0; j < this.children.length; ++j) {
                if (this.children[i][j] == null) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public void preCacheLoad() {
        this.textures = new BranchRegionTexture[8][8];
        this.freed = false;
    }

    @Override
    protected void putLeaf(int X, int Z, MapRegion leaf) {
        int childLevel = this.level - 1;
        int childLevelX = X >> childLevel;
        int childLevelZ = Z >> childLevel;
        int localChildLevelX = childLevelX & 1;
        int localChildLevelZ = childLevelZ & 1;
        if (this.level == 1) {
            if (this.children[localChildLevelX][localChildLevelZ] == null) {
                leaf.setParent(this);
                this.children[localChildLevelX][localChildLevelZ] = leaf;
            }
            return;
        }
        BranchLeveledRegion childBranch = this.children[localChildLevelX][localChildLevelZ];
        if (childBranch == null) {
            BranchLeveledRegion branchLeveledRegion = new BranchLeveledRegion(leaf.getWorldId(), leaf.getDimId(), leaf.getMwId(), this.dim, childLevel, childLevelX, childLevelZ, this.caveLayer, this);
            this.children[localChildLevelX][localChildLevelZ] = branchLeveledRegion;
            childBranch = branchLeveledRegion;
            this.dim.getLayeredMapRegions().addListRegion(childBranch);
        }
        ((LeveledRegion)childBranch).putLeaf(X, Z, leaf);
    }

    @Override
    protected LeveledRegion<?> get(int leveledX, int leveledZ, int level) {
        if (this.level == level) {
            return this;
        }
        int childLevel = this.level - 1;
        if (level > childLevel) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        int childLevelX = leveledX >> childLevel - level;
        int localChildLevelX = childLevelX & 1;
        int childLevelZ = leveledZ >> childLevel - level;
        int localChildLevelZ = childLevelZ & 1;
        LeveledRegion<?> childBranch = this.children[localChildLevelX][localChildLevelZ];
        if (childBranch == null) {
            return null;
        }
        return childBranch.get(leveledX, leveledZ, level);
    }

    @Override
    protected boolean remove(int leveledX, int leveledZ, int level) {
        int childLevel = this.level - 1;
        if (level > childLevel) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        int childLevelX = leveledX >> childLevel - level;
        int childLevelZ = leveledZ >> childLevel - level;
        int localChildLevelX = childLevelX & 1;
        int localChildLevelZ = childLevelZ & 1;
        LeveledRegion<?> childRegion = this.children[localChildLevelX][localChildLevelZ];
        if (level == childLevel) {
            if (childRegion != null) {
                this.children[localChildLevelX][localChildLevelZ] = null;
                return true;
            }
            return false;
        }
        if (childRegion == null) {
            return false;
        }
        return childRegion.remove(leveledX, leveledZ, level);
    }

    @Override
    public boolean loadingAnimation() {
        return !this.loaded;
    }

    @Override
    public void addDebugLines(List<String> debugLines, MapProcessor mapProcessor, int textureX, int textureY) {
        super.addDebugLines(debugLines, mapProcessor, textureX, textureY);
        debugLines.add("loaded: " + this.loaded);
        debugLines.add("children: tl " + (this.children[0][0] != null) + " tr " + (this.children[1][0] != null) + " bl " + (this.children[0][1] != null) + " br " + (this.children[1][1] != null));
        debugLines.add("freed: " + this.freed + " shouldCheckForUpdates: " + this.shouldCheckForUpdates + " hasTextures: " + this.hasTextures());
        debugLines.add("updateCountSinceSave: " + this.updateCountSinceSave);
    }

    @Override
    public boolean shouldEndProcessingAfterUpload() {
        return this.loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }

    @Override
    public boolean cleanAndCacheRequestsBlocked() {
        return this.downloading || this.updateCountSinceSave > 0 && !this.recacheHasBeenRequested;
    }

    @Override
    public void onProcessingEnd() {
        super.onProcessingEnd();
        this.freed = true;
        this.readyForUpdates = true;
    }

    @Override
    public boolean shouldBeProcessed() {
        return this.loaded && !this.freed;
    }

    @Override
    public void preCache() {
    }

    @Override
    public void postCache(File permFile, MapSaveLoad mapSaveLoad, boolean successfullySaved) throws IOException {
        this.lastSaveTime = System.currentTimeMillis();
        this.updateCountSinceSave = 0;
    }

    @Override
    public boolean skipCaching(MapProcessor mapProcessor) {
        return false;
    }

    @Override
    public File findCacheFile(MapSaveLoad mapSaveLoad) throws IOException {
        Path subFolder = mapSaveLoad.getMWSubFolder(this.worldId, this.dimId, this.mwId);
        Path layerFolder = mapSaveLoad.getCaveLayerFolder(this.caveLayer, subFolder);
        Path rootCacheFolder = layerFolder.resolve("cache");
        Path levelCacheFolder = rootCacheFolder.resolve("" + this.level);
        Files.createDirectories(levelCacheFolder, new FileAttribute[0]);
        return levelCacheFolder.resolve(this.regionX + "_" + this.regionZ + ".xwmc").toFile();
    }

    @Override
    public void onCurrentDimFinish(MapSaveLoad mapSaveLoad, MapProcessor mapProcessor) {
    }

    @Override
    public void onLimiterRemoval(MapProcessor mapProcessor) {
        mapProcessor.removeMapRegion(this);
    }

    @Override
    public void afterLimiterRemoval(MapProcessor mapProcessor) {
        this.reset();
    }

    @Override
    public BranchRegionTexture createTexture(int x, int y) {
        BranchRegionTexture branchRegionTexture = new BranchRegionTexture(this);
        this.textures[x][y] = branchRegionTexture;
        return branchRegionTexture;
    }

    public void setShouldCheckForUpdatesRecursive(boolean shouldCheckForUpdates) {
        this.shouldCheckForUpdates = shouldCheckForUpdates;
        if (this.parent != null) {
            this.parent.setShouldCheckForUpdatesRecursive(shouldCheckForUpdates);
        }
    }

    public void setShouldCheckForUpdatesSingle(boolean shouldCheckForUpdates) {
        this.shouldCheckForUpdates = shouldCheckForUpdates;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startDownloadingTexturesForCache(MapProcessor mapProcessor) {
        BranchLeveledRegion branchLeveledRegion = this;
        synchronized (branchLeveledRegion) {
            this.recacheHasBeenRequested = true;
            this.shouldCache = true;
            this.downloading = true;
        }
        boolean hasSomething = false;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                BranchRegionTexture regionTexture = this.textures[i][j];
                if (regionTexture == null) continue;
                hasSomething = true;
                if (regionTexture.shouldUpload() || regionTexture.isCachePrepared()) continue;
                regionTexture.requestDownload();
            }
        }
        if (this.freed) {
            this.freed = false;
            mapProcessor.addToProcess(this);
        }
        BranchLeveledRegion branchLeveledRegion2 = this;
        synchronized (branchLeveledRegion2) {
            if (!hasSomething) {
                this.setAllCachePrepared(true);
            }
            this.downloading = false;
            this.updateCountSinceSave = 0;
        }
    }

    public void postTextureUpdate() {
        if (this.parent != null) {
            this.parent.setShouldCheckForUpdatesRecursive(true);
        }
        this.countTextureUpdate();
    }

    private void countTextureUpdate() {
        this.lastUpdateTime = System.currentTimeMillis();
        ++this.updateCountSinceSave;
    }

    public boolean eligibleForSaving(long currentTime) {
        return this.updateCountSinceSave > 0 && (this.updateCountSinceSave >= 64 || currentTime - this.lastUpdateTime > 1000L);
    }

    @Override
    protected void onCacheLoadFailed(boolean[][] textureLoaded) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                BranchRegionTexture texture = this.getTexture(i, j);
                if (texture == null || textureLoaded[i][j]) continue;
                this.textures[i][j] = null;
                texture.deleteTexturesAndBuffers();
            }
        }
    }
}

