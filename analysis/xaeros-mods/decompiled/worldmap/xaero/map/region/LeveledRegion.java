/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1959
 *  net.minecraft.class_2378
 *  net.minecraft.class_2960
 *  net.minecraft.class_5321
 *  net.minecraft.class_7924
 */
package xaero.map.region;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.minecraft.class_1959;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.file.MapSaveLoad;
import xaero.map.file.OldFormatSupport;
import xaero.map.palette.FastPalette;
import xaero.map.palette.Paletted2DFastBitArrayIntStorage;
import xaero.map.region.BranchLeveledRegion;
import xaero.map.region.MapRegion;
import xaero.map.region.texture.RegionTexture;
import xaero.map.world.MapDimension;

public abstract class LeveledRegion<T extends RegionTexture<T>>
implements Comparable<LeveledRegion<T>> {
    public static final int SIDE_LENGTH = 8;
    private static int comparisonX = 0;
    private static int comparisonZ = 0;
    protected static int comparisonLevel;
    private static int comparisonLeafX;
    private static int comparisonLeafZ;
    protected BranchLeveledRegion parent;
    protected int caveLayer;
    protected int regionX;
    protected int regionZ;
    protected int level;
    private boolean allCachePrepared;
    protected boolean shouldCache;
    protected boolean recacheHasBeenRequested;
    protected boolean reloadHasBeenRequested;
    protected File cacheFile = null;
    protected String worldId;
    protected String dimId;
    protected String mwId;
    protected MapDimension dim;
    public int activeBranchUpdateReferences;
    public int[][] leafTextureVersionSum = new int[8][8];
    protected int[][] cachedTextureVersions = new int[8][8];
    protected boolean metaLoaded;
    private int distanceFromPlayerCache;
    private int leafDistanceFromPlayerCache;
    protected long lastSaveTime;
    private FastPalette<class_5321<class_1959>> biomePalette;

    public LeveledRegion(String worldId, String dimId, String mwId, MapDimension dim, int level, int leveledX, int leveledZ, int caveLayer, BranchLeveledRegion parent) {
        this.worldId = worldId;
        this.dimId = dimId;
        this.mwId = mwId;
        this.dim = dim;
        this.level = level;
        this.regionX = leveledX;
        this.regionZ = leveledZ;
        this.caveLayer = caveLayer;
        this.parent = parent;
    }

    public void onDimensionClear(MapProcessor mapProcessor) {
        this.deleteTexturesAndBuffers();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteTexturesAndBuffers() {
        LeveledRegion leveledRegion = this;
        synchronized (leveledRegion) {
            this.setAllCachePrepared(false);
        }
        if (this.hasTextures()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    T texture = this.getTexture(i, j);
                    if (texture == null) continue;
                    LeveledRegion leveledRegion2 = this;
                    synchronized (leveledRegion2) {
                        this.setAllCachePrepared(false);
                        ((RegionTexture)texture).setCachePrepared(false);
                    }
                    ((RegionTexture)texture).deleteTexturesAndBuffers();
                    if (this.level <= 0) continue;
                    this.putTexture(i, j, null);
                }
            }
        }
    }

    public boolean hasTextures() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteBuffers() {
        LeveledRegion leveledRegion = this;
        synchronized (leveledRegion) {
            this.setAllCachePrepared(false);
        }
        if (this.hasTextures()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    T texture = this.getTexture(i, j);
                    if (texture == null || ((RegionTexture)texture).getColorBuffer() == null) continue;
                    LeveledRegion leveledRegion2 = this;
                    synchronized (leveledRegion2) {
                        this.setAllCachePrepared(false);
                        ((RegionTexture)texture).setCachePrepared(false);
                    }
                    ((RegionTexture)texture).setToUpload(false);
                    ((RegionTexture)texture).deleteColorBuffer();
                }
            }
        }
    }

    public void deleteGLBuffers() {
        if (this.hasTextures()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    T texture = this.getTexture(i, j);
                    if (texture == null) continue;
                    ((RegionTexture)texture).deletePBOs();
                }
            }
        }
    }

    public boolean isAllCachePrepared() {
        return this.allCachePrepared;
    }

    public void setAllCachePrepared(boolean allCachePrepared) {
        if (this.allCachePrepared && !allCachePrepared && WorldMap.settings.detailed_debug) {
            WorldMap.LOGGER.info("Cancelling cache: " + String.valueOf(this));
        }
        this.allCachePrepared = allCachePrepared;
    }

    public int getRegionX() {
        return this.regionX;
    }

    public int getRegionZ() {
        return this.regionZ;
    }

    public boolean shouldCache() {
        return this.shouldCache;
    }

    public int getLevel() {
        return this.level;
    }

    public void setShouldCache(boolean shouldCache, String by) {
        this.shouldCache = shouldCache;
        if (WorldMap.settings.detailed_debug) {
            WorldMap.LOGGER.info("shouldCache set to " + shouldCache + " by " + by + " for " + String.valueOf(this));
        }
    }

    public boolean recacheHasBeenRequested() {
        return this.recacheHasBeenRequested;
    }

    public void setRecacheHasBeenRequested(boolean recacheHasBeenRequested, String by) {
        if (WorldMap.settings.detailed_debug && recacheHasBeenRequested != this.recacheHasBeenRequested) {
            WorldMap.LOGGER.info("Recache set to " + recacheHasBeenRequested + " by " + by + " for " + String.valueOf(this));
        }
        this.recacheHasBeenRequested = recacheHasBeenRequested;
    }

    public File getCacheFile() {
        return this.cacheFile;
    }

    public void setCacheFile(File cacheFile) {
        this.cacheFile = cacheFile;
    }

    public MapDimension getDim() {
        return this.dim;
    }

    public String toString() {
        return "(" + this.caveLayer + ") " + this.regionX + "_" + this.regionZ + " L" + this.level + " " + super.toString();
    }

    public boolean reloadHasBeenRequested() {
        return this.reloadHasBeenRequested;
    }

    public void setReloadHasBeenRequested(boolean reloadHasBeenRequested, String by) {
        if (WorldMap.settings.detailed_debug && reloadHasBeenRequested != this.reloadHasBeenRequested) {
            WorldMap.LOGGER.info("Reload set to " + reloadHasBeenRequested + " by " + by + " for " + String.valueOf(this));
        }
        this.reloadHasBeenRequested = reloadHasBeenRequested;
    }

    public static void setComparison(int x, int z, int level, int leafX, int leafZ) {
        comparisonX = x;
        comparisonZ = z;
        comparisonLevel = level;
        comparisonLeafX = leafX;
        comparisonLeafZ = leafZ;
    }

    protected int distanceFromPlayer() {
        int toRegionX = (this.regionX << this.level >> comparisonLevel) - comparisonX;
        int toRegionZ = (this.regionZ << this.level >> comparisonLevel) - comparisonZ;
        return (int)Math.sqrt(toRegionX * toRegionX + toRegionZ * toRegionZ);
    }

    protected int leafDistanceFromPlayer() {
        int toRegionX = (this.regionX << this.level) - comparisonLeafX;
        int toRegionZ = (this.regionZ << this.level) - comparisonLeafZ;
        return (int)Math.sqrt(toRegionX * toRegionX + toRegionZ * toRegionZ);
    }

    public void calculateSortingDistance() {
        this.distanceFromPlayerCache = this.distanceFromPlayer();
        this.leafDistanceFromPlayerCache = this.leafDistanceFromPlayer();
    }

    protected int chunkDistanceFromPlayer() {
        int toRegionX = (this.regionX << this.level << 5) - comparisonX;
        int toRegionZ = (this.regionZ << this.level << 5) - comparisonZ;
        return (int)Math.sqrt(toRegionX * toRegionX + toRegionZ * toRegionZ);
    }

    public void calculateSortingChunkDistance() {
        this.leafDistanceFromPlayerCache = this.distanceFromPlayerCache = this.chunkDistanceFromPlayer();
    }

    @Override
    public int compareTo(LeveledRegion<T> arg0) {
        if (this.level == 3 && arg0.level != 3) {
            return -1;
        }
        if (arg0.level == 3 && this.level != 3) {
            return 1;
        }
        if (this.level == comparisonLevel && arg0.level != comparisonLevel) {
            return -1;
        }
        if (arg0.level == comparisonLevel && this.level != comparisonLevel) {
            return 1;
        }
        int toRegion = this.distanceFromPlayerCache;
        int toRegion2 = arg0.distanceFromPlayerCache;
        if (toRegion > toRegion2) {
            return 1;
        }
        if (toRegion == toRegion2) {
            toRegion = this.leafDistanceFromPlayerCache;
            toRegion2 = arg0.leafDistanceFromPlayerCache;
            if (toRegion > toRegion2) {
                return 1;
            }
            if (toRegion == toRegion2) {
                return 0;
            }
            return -1;
        }
        return -1;
    }

    public void onProcessingEnd() {
    }

    public void addDebugLines(List<String> debugLines, MapProcessor mapProcessor, int textureX, int textureY) {
        debugLines.add("processed: " + mapProcessor.isProcessed(this));
        debugLines.add(String.format("recache: %s reload: %s metaLoaded: %s", this.recacheHasBeenRequested(), this.reloadHasBeenRequested(), this.metaLoaded));
        debugLines.add("shouldCache: " + this.shouldCache() + " allCachePrepared: " + this.allCachePrepared);
        debugLines.add("activeBranchUpdateReferences: " + this.activeBranchUpdateReferences);
        debugLines.add("leafTextureVersionSum: " + this.leafTextureVersionSum[textureX][textureY] + " cachedTextureVersions: " + this.cachedTextureVersions[textureX][textureY] + " [" + textureX + "," + textureY + "]");
        if (this.biomePalette != null) {
            Object biomePaletteLine = "";
            for (int i = 0; i < this.getBiomePaletteSize(); ++i) {
                class_5321<class_1959> biomeKey;
                if (i > 0) {
                    biomePaletteLine = (String)biomePaletteLine + ", ";
                }
                int count = (biomeKey = this.getBiomeKey(i)) == null ? 0 : this.biomePalette.getCount(i);
                String biomeString = biomeKey == null ? "-" : biomeKey.method_29177().toString() + ":" + count;
                biomePaletteLine = (String)biomePaletteLine + (biomeKey == null ? biomeString : biomeString.toString().substring(biomeString.indexOf(58) + 1));
            }
            debugLines.add((String)biomePaletteLine);
        }
    }

    protected void writeCacheMetaData(DataOutputStream output, byte[] usableBuffer, byte[] integerByteBuffer) throws IOException {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                T texture = this.getTexture(i, j);
                if (texture == null || !((RegionTexture)texture).shouldIncludeInCache()) continue;
                if (!((RegionTexture)texture).isCachePrepared()) {
                    throw new RuntimeException("Trying to save cache but " + i + " " + j + " in " + String.valueOf(this) + " is not prepared.");
                }
                output.write(i << 4 | j);
                int bufferedTextureVersion = ((RegionTexture)texture).getBufferedTextureVersion();
                output.writeInt(bufferedTextureVersion);
            }
        }
        output.write(255);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveCacheTextures(File tempFile, int extraAttempts) {
        boolean success;
        block26: {
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Saving cache: " + String.valueOf(this));
            }
            success = false;
            try (ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
                 DataOutputStream output2 = new DataOutputStream(zipOutput);){
                ZipEntry e = new ZipEntry("cache.xaero");
                zipOutput.putNextEntry(e);
                byte[] usableBuffer = new byte[16384];
                byte[] integerByteBuffer = new byte[4];
                int currentFullVersion = 131096;
                output2.writeInt(currentFullVersion);
                this.writeCacheMetaData(output2, usableBuffer, integerByteBuffer);
                this.saveBiomePalette(output2);
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        T texture = this.getTexture(i, j);
                        if (texture == null || !((RegionTexture)texture).shouldIncludeInCache()) continue;
                        if (!((RegionTexture)texture).isCachePrepared()) {
                            throw new RuntimeException("Trying to save cache but " + i + " " + j + " in " + String.valueOf(this) + " is not prepared.");
                        }
                        output2.write(i << 4 | j);
                        ((RegionTexture)texture).writeCacheMapData(output2, usableBuffer, integerByteBuffer, this);
                    }
                }
                output2.write(255);
                zipOutput.closeEntry();
                success = true;
            }
            catch (IOException ioe) {
                WorldMap.LOGGER.info("IO exception while trying to save cache textures for " + String.valueOf(this), (Throwable)ioe);
                if (extraAttempts <= 0) break block26;
                WorldMap.LOGGER.info("Retrying...");
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException output2) {
                    // empty catch block
                }
                return this.saveCacheTextures(tempFile, extraAttempts - 1);
            }
        }
        LeveledRegion ioe = this;
        synchronized (ioe) {
            this.setAllCachePrepared(false);
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                T texture = this.getTexture(i, j);
                if (texture == null || !((RegionTexture)texture).shouldIncludeInCache()) continue;
                ((RegionTexture)texture).deleteColorBuffer();
                LeveledRegion leveledRegion = this;
                synchronized (leveledRegion) {
                    ((RegionTexture)texture).setCachePrepared(false);
                    this.setAllCachePrepared(false);
                    continue;
                }
            }
        }
        return success;
    }

    protected void readCacheMetaData(DataInputStream input, int minorSaveVersion, int majorSaveVersion, byte[] usableBuffer, byte[] integerByteBuffer, boolean[][] textureLoaded, MapProcessor mapProcessor) throws IOException {
        if (minorSaveVersion == 8 || minorSaveVersion >= 12) {
            this.readCacheInput(true, input, minorSaveVersion, majorSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, false, mapProcessor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    public boolean loadCacheTextures(MapProcessor mapProcessor, class_2378<class_1959> biomeRegistry, boolean justMetaData, boolean[][] textureLoaded, int targetHighlightsHash, boolean[] leafShouldAffectBranchesDest, boolean[] metaLoadedDest, int extraAttempts, OldFormatSupport oldFormatSupport) {
        if (this.cacheFile == null) {
            return false;
        }
        if (this.cacheFile.exists()) {
            try {
                try {
                    DataInputStream input;
                    ZipInputStream zipInput;
                    block36: {
                        int currentFullVersion;
                        int majorSaveVersion;
                        int minorSaveVersion;
                        int cacheFullSaveVersion;
                        byte[] integerByteBuffer;
                        block37: {
                            zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.cacheFile)));
                            input = new DataInputStream(zipInput);
                            ZipEntry entry = zipInput.getNextEntry();
                            if (entry == null) break block36;
                            integerByteBuffer = new byte[4];
                            cacheFullSaveVersion = input.readInt();
                            minorSaveVersion = cacheFullSaveVersion & 0xFFFF;
                            majorSaveVersion = cacheFullSaveVersion >> 16 & 0xFFFF;
                            currentFullVersion = 131096;
                            if (majorSaveVersion <= 2 && minorSaveVersion <= 24 && cacheFullSaveVersion != 7 && minorSaveVersion != 21) break block37;
                            input.close();
                            WorldMap.LOGGER.info("Trying to load newer region cache " + String.valueOf(this) + " using an older version of Xaero's World Map!");
                            mapProcessor.getMapSaveLoad().backupFile(this.cacheFile, cacheFullSaveVersion);
                            this.cacheFile = null;
                            this.shouldCache = true;
                            boolean bl = false;
                            input.close();
                            zipInput.close();
                            return bl;
                        }
                        if (cacheFullSaveVersion < currentFullVersion) {
                            this.shouldCache = true;
                        }
                        this.biomePalette = null;
                        byte[] usableBuffer = new byte[16384];
                        if (minorSaveVersion >= 8) {
                            this.readCacheMetaData(input, minorSaveVersion, majorSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, mapProcessor);
                            metaLoadedDest[0] = true;
                            if (justMetaData && (minorSaveVersion == 8 || minorSaveVersion >= 12)) {
                                boolean i = true;
                                return i;
                            }
                        }
                        this.preCacheLoad();
                        this.loadBiomePalette(input, minorSaveVersion, majorSaveVersion, mapProcessor, biomeRegistry, oldFormatSupport);
                        boolean leafShouldAffectBranches = this.shouldLeafAffectCache(targetHighlightsHash);
                        if (leafShouldAffectBranchesDest != null) {
                            leafShouldAffectBranchesDest[0] = leafShouldAffectBranches;
                        }
                        this.readCacheInput(false, input, minorSaveVersion, majorSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, leafShouldAffectBranches, mapProcessor);
                        metaLoadedDest[0] = true;
                        zipInput.closeEntry();
                        boolean bl = false;
                        return bl;
                    }
                    input.close();
                    {
                        catch (Throwable entry) {
                            throw entry;
                        }
                    }
                    {
                        finally {
                            input.close();
                        }
                    }
                    zipInput.close();
                    finally {
                        zipInput.close();
                    }
                }
                finally {
                    for (int i = 0; i < 8; ++i) {
                        for (int j = 0; j < 8; ++j) {
                            T texture = this.getTexture(i, j);
                            if (texture == null || ((RegionTexture)texture).getBiomes() == null || ((RegionTexture)texture).getBiomes().getRegionBiomePalette() == this.biomePalette) continue;
                            ((RegionTexture)texture).resetBiomes();
                        }
                    }
                }
            }
            catch (IOException ioe) {
                WorldMap.LOGGER.error("IO exception while trying to load cache for region " + String.valueOf(this) + "! " + String.valueOf(this.cacheFile), (Throwable)ioe);
                if (extraAttempts > 0) {
                    WorldMap.LOGGER.info("Retrying...");
                    try {
                        Thread.sleep(20L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    metaLoadedDest[0] = false;
                    return this.loadCacheTextures(mapProcessor, biomeRegistry, justMetaData, textureLoaded, targetHighlightsHash, leafShouldAffectBranchesDest, metaLoadedDest, extraAttempts - 1, oldFormatSupport);
                }
                this.cacheFile = null;
                this.shouldCache = true;
                this.onCacheLoadFailed(textureLoaded);
            }
            catch (Throwable e) {
                this.cacheFile = null;
                this.shouldCache = true;
                WorldMap.LOGGER.error("Failed to load cache for region " + String.valueOf(this) + "! " + String.valueOf(this.cacheFile), e);
                this.onCacheLoadFailed(textureLoaded);
            }
        } else {
            this.cacheFile = null;
            this.shouldCache = true;
        }
        return false;
    }

    protected abstract void onCacheLoadFailed(boolean[][] var1);

    public void saveBiomePalette(DataOutputStream output) throws IOException {
        int paletteSize = 0;
        if (this.biomePalette != null) {
            paletteSize = this.biomePalette.getSize();
        }
        output.writeInt(paletteSize);
        if (this.biomePalette != null) {
            for (int i = 0; i < paletteSize; ++i) {
                class_5321<class_1959> paletteKey = this.biomePalette.get(i);
                if (paletteKey == null) {
                    output.write(255);
                    continue;
                }
                output.write(0);
                output.writeUTF(paletteKey.method_29177().toString());
            }
        }
    }

    private void loadBiomePalette(DataInputStream input, int minorSaveVersion, int majorSaveVersion, MapProcessor mapProcessor, class_2378<class_1959> biomeRegistry, OldFormatSupport oldFormatSupport) throws IOException {
        if (minorSaveVersion >= 19) {
            int paletteSize = input.readInt();
            if (paletteSize > 0) {
                this.ensureBiomePalette();
                for (int i = 0; i < paletteSize; ++i) {
                    class_2960 biomeResourceLocation;
                    int paletteElementType = input.read();
                    if (paletteElementType == 255) {
                        this.biomePalette.addNull();
                        continue;
                    }
                    if (paletteElementType == 0) {
                        biomeResourceLocation = class_2960.method_60654((String)input.readUTF());
                    } else {
                        int biomeInt = input.readInt();
                        Object biomeString = oldFormatSupport.fixBiome(biomeInt, 5, null);
                        if (biomeString == null) {
                            biomeString = "xaeroworldmap:unknown_biome_" + biomeInt;
                        }
                        biomeResourceLocation = class_2960.method_60654((String)((String)biomeString + "_old_xaero"));
                    }
                    class_5321 biomeKey = class_5321.method_29179((class_5321)class_7924.field_41236, (class_2960)biomeResourceLocation);
                    if (minorSaveVersion <= 20) {
                        input.readShort();
                    }
                    this.biomePalette.append((class_5321<class_1959>)biomeKey, 0);
                }
            } else if (paletteSize == -1) {
                this.shouldCache = true;
            }
        }
    }

    protected boolean shouldLeafAffectCache(int targetHighlightsHash) {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void readCacheInput(boolean isMeta, DataInputStream input, int minorSaveVersion, int majorSaveVersion, byte[] usableBuffer, byte[] integerByteBuffer, boolean[][] textureLoaded, boolean leafShouldAffectBranches, MapProcessor mapProcessor) throws IOException {
        int textureCoords = input.read();
        while (textureCoords != -1 && textureCoords != 255) {
            int x = textureCoords >> 4;
            int y = textureCoords & 0xF;
            if (isMeta) {
                int cachedTextureVersion;
                this.cachedTextureVersions[x][y] = cachedTextureVersion = input.readInt();
                this.updateLeafTextureVersion(x, y, cachedTextureVersion);
            } else {
                RegionTexture texture;
                RegionTexture regionTexture = texture = this.hasTextures() ? (RegionTexture)this.getTexture(x, y) : null;
                if (texture == null) {
                    texture = (RegionTexture)this.createTexture(x, y);
                    if (this.level == 0) {
                        LeveledRegion leveledRegion = this;
                        synchronized (leveledRegion) {
                            this.setAllCachePrepared(false);
                        }
                    }
                }
                texture.readCacheData(minorSaveVersion, majorSaveVersion, input, usableBuffer, integerByteBuffer, this, mapProcessor, x, y, leafShouldAffectBranches);
            }
            if (textureLoaded != null) {
                textureLoaded[x][y] = true;
            }
            textureCoords = input.read();
        }
    }

    public int getAndResetCachedTextureVersion(int x, int y) {
        int result = this.cachedTextureVersions[x][y];
        this.cachedTextureVersions[x][y] = -1;
        return result;
    }

    public BranchLeveledRegion getParent() {
        return this.parent;
    }

    public boolean shouldAffectLoadingRequestFrequency() {
        return this.shouldBeProcessed();
    }

    protected void preCacheLoad() {
    }

    public void processWhenLoadedChunksExist(int globalRegionCacheHashCode) {
    }

    public boolean isMetaLoaded() {
        return this.metaLoaded;
    }

    public void confirmMetaLoaded() {
        this.metaLoaded = true;
    }

    public LeveledRegion<?> getRootRegion() {
        LeveledRegion<?> result = this;
        if (this.parent != null) {
            result = this.parent.getRootRegion();
        }
        return result;
    }

    public void checkForUpdates(MapProcessor mapProcessor, boolean prevWaitingForBranchCache, boolean[] waitingForBranchCache, ArrayList<BranchLeveledRegion> branchRegionBuffer, int viewedLevel, int minViewedLeafX, int minViewedLeafZ, int maxViewedLeafX, int maxViewedLeafZ) {
    }

    public void ensureBiomePalette() {
        if (this.biomePalette == null) {
            this.biomePalette = FastPalette.Builder.begin().setMaxCountPerElement(64).build();
        }
    }

    public class_5321<class_1959> getBiomeKey(int paletteIndex) {
        if (this.biomePalette == null) {
            return null;
        }
        return this.biomePalette.get(paletteIndex);
    }

    public int getBiomePaletteIndex(class_5321<class_1959> biome) {
        if (this.biomePalette == null) {
            return -1;
        }
        return this.biomePalette.getIndex(biome);
    }

    public int onBiomeAddedToTexture(class_5321<class_1959> biome) {
        this.ensureBiomePalette();
        int paletteIndex = this.biomePalette.add(biome);
        this.biomePalette.count(paletteIndex, true);
        return paletteIndex;
    }

    public void onBiomeRemovedFromTexture(int paletteIndex) {
        if (paletteIndex >= this.biomePalette.getSize() || this.biomePalette.get(paletteIndex) == null) {
            return;
        }
        int count = this.biomePalette.count(paletteIndex, false);
        if (count == 0) {
            this.biomePalette.remove(paletteIndex);
        }
    }

    public void uncountTextureBiomes(RegionTexture<?> texture) {
        if (texture != null && texture.getBiomes() != null) {
            Paletted2DFastBitArrayIntStorage biomeStorage = texture.getBiomes().getBiomeIndexStorage();
            int chunkPaletteSize = biomeStorage.getPaletteSize();
            for (int i = 0; i < chunkPaletteSize; ++i) {
                int biomeIndex = biomeStorage.getPaletteElement(i);
                if (biomeIndex == -1) continue;
                this.onBiomeRemovedFromTexture(biomeIndex);
            }
        }
    }

    public int getBiomePaletteSize() {
        if (this.biomePalette == null) {
            return 0;
        }
        return this.biomePalette.getSize();
    }

    public FastPalette<class_5321<class_1959>> getBiomePalette() {
        return this.biomePalette;
    }

    public void resetBiomePalette() {
        this.biomePalette = null;
    }

    public boolean isRefreshing() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean shouldAllowAnotherRegionToLoad() {
        LeveledRegion leveledRegion = this;
        synchronized (leveledRegion) {
            if (!(this.reloadHasBeenRequested() || this.hasRemovableSourceData() || this.isRefreshing())) {
                return true;
            }
        }
        return false;
    }

    public abstract boolean shouldEndProcessingAfterUpload();

    public abstract T createTexture(int var1, int var2);

    public abstract void putTexture(int var1, int var2, T var3);

    public abstract T getTexture(int var1, int var2);

    protected abstract void putLeaf(int var1, int var2, MapRegion var3);

    protected abstract boolean remove(int var1, int var2, int var3);

    protected abstract LeveledRegion<?> get(int var1, int var2, int var3);

    public abstract boolean loadingAnimation();

    public abstract boolean cleanAndCacheRequestsBlocked();

    public abstract boolean shouldBeProcessed();

    public abstract boolean isLoaded();

    public abstract void preCache();

    public abstract void postCache(File var1, MapSaveLoad var2, boolean var3) throws IOException;

    public abstract boolean skipCaching(MapProcessor var1);

    public abstract File findCacheFile(MapSaveLoad var1) throws IOException;

    public abstract void onCurrentDimFinish(MapSaveLoad var1, MapProcessor var2);

    public abstract void onLimiterRemoval(MapProcessor var1);

    public abstract void afterLimiterRemoval(MapProcessor var1);

    public String getExtraInfo() {
        return "";
    }

    public void updateLeafTextureVersion(int localTextureX, int localTextureZ, int newVersion) {
    }

    public boolean hasRemovableSourceData() {
        return false;
    }

    public int getCaveLayer() {
        return this.caveLayer;
    }

    static {
        comparisonLeafX = 0;
        comparisonLeafZ = 0;
    }
}

