/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.file;

import java.io.File;
import xaero.map.MapProcessor;
import xaero.map.file.MapRegionInfo;
import xaero.map.region.MapRegion;
import xaero.map.region.texture.LeafRegionTexture;
import xaero.map.util.linked.ILinkedChainNode;

public class RegionDetection
implements MapRegionInfo,
ILinkedChainNode<RegionDetection> {
    private int initialVersion;
    private String worldId;
    private String dimId;
    private String mwId;
    private int regionX;
    private int regionZ;
    private final boolean hasHadTerrain;
    private boolean lookedForCache;
    private boolean removed;
    private boolean shouldCache;
    private File cacheFile;
    private File regionFile;
    private int[][] cachedTextureVersions;
    private int cacheHashCode;
    private int reloadVersion;
    private int highlightsHash;
    private int caveStart;
    private boolean outdatedWithOtherLayers;
    private RegionDetection next;
    private RegionDetection previous;
    private boolean destroyed;

    public RegionDetection(String worldId, String dimId, String mwId, int regionX, int regionZ, File regionFile, int globalVersion, boolean hasHadTerrain) {
        this.worldId = worldId;
        this.dimId = dimId;
        this.mwId = mwId;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.regionFile = regionFile;
        this.initialVersion = globalVersion;
        this.hasHadTerrain = hasHadTerrain;
    }

    public int getInitialVersion() {
        return this.initialVersion;
    }

    @Override
    public boolean shouldCache() {
        return this.shouldCache;
    }

    @Override
    public File getCacheFile() {
        return this.cacheFile;
    }

    @Override
    public void setShouldCache(boolean shouldCache, String fsdfs) {
        this.shouldCache = shouldCache;
    }

    @Override
    public void setCacheFile(File cacheFile) {
        this.cacheFile = cacheFile;
    }

    @Override
    public String getWorldId() {
        return this.worldId;
    }

    @Override
    public String getDimId() {
        return this.dimId;
    }

    @Override
    public String getMwId() {
        return this.mwId;
    }

    @Override
    public int getRegionX() {
        return this.regionX;
    }

    @Override
    public int getRegionZ() {
        return this.regionZ;
    }

    public void transferInfoTo(MapRegion to) {
        to.setShouldCache(this.shouldCache, "transfer");
        to.setCacheFile(this.cacheFile);
        to.setInitialVersion(this.initialVersion);
        to.setRegionFile(this.regionFile);
        to.setLookedForCache(this.lookedForCache);
        if (this.hasHadTerrain) {
            to.setHasHadTerrain();
        }
    }

    public void transferInfoPostAddTo(MapRegion to, MapProcessor mapProcessor) {
        to.restoreMetaData(this.cachedTextureVersions, this.cacheHashCode, this.reloadVersion, this.highlightsHash, this.caveStart, this.outdatedWithOtherLayers, mapProcessor);
    }

    public void transferInfoFrom(MapRegion from) {
        this.shouldCache = from.shouldCache();
        this.cacheFile = from.getCacheFile();
        this.initialVersion = from.hasVersion() ? from.getVersion() : from.getInitialVersion();
        this.regionFile = from.getRegionFile();
        this.lookedForCache = from.hasLookedForCache();
        if (from.getLoadState() >= 4) {
            this.cacheHashCode = from.getCacheHashCode();
            this.reloadVersion = from.getReloadVersion();
            this.highlightsHash = from.getHighlightsHash();
            this.caveStart = from.getCaveStart();
            this.outdatedWithOtherLayers = from.isOutdatedWithOtherLayers();
            this.cachedTextureVersions = new int[8][8];
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    LeafRegionTexture regTex = from.getTexture(i, j);
                    if (regTex == null) continue;
                    this.cachedTextureVersions[i][j] = regTex.getTextureVersion();
                }
            }
        }
    }

    @Override
    public File getRegionFile() {
        return this.regionFile;
    }

    public boolean isHasHadTerrain() {
        return this.hasHadTerrain;
    }

    @Override
    public boolean hasLookedForCache() {
        return this.lookedForCache;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    @Override
    public void setNext(RegionDetection element) {
        this.next = element;
    }

    @Override
    public void setPrevious(RegionDetection element) {
        this.previous = element;
    }

    @Override
    public RegionDetection getNext() {
        return this.next;
    }

    @Override
    public RegionDetection getPrevious() {
        return this.previous;
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    @Override
    public void onDestroyed() {
        this.destroyed = true;
    }
}

