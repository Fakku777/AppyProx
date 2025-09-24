/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.class_1047
 *  net.minecraft.class_1058
 *  net.minecraft.class_1059
 *  net.minecraft.class_10735
 *  net.minecraft.class_1087
 *  net.minecraft.class_10889
 *  net.minecraft.class_11515
 *  net.minecraft.class_1657
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_1944
 *  net.minecraft.class_1959
 *  net.minecraft.class_2189
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2320
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2356
 *  net.minecraft.class_2378
 *  net.minecraft.class_2404
 *  net.minecraft.class_2464
 *  net.minecraft.class_2521
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_2688
 *  net.minecraft.class_2806
 *  net.minecraft.class_2812
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_3481
 *  net.minecraft.class_3610
 *  net.minecraft.class_3619
 *  net.minecraft.class_3620
 *  net.minecraft.class_4696
 *  net.minecraft.class_5321
 *  net.minecraft.class_5819
 *  net.minecraft.class_638
 *  net.minecraft.class_773
 *  net.minecraft.class_777
 *  net.minecraft.class_8923
 */
package xaero.map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.class_1047;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10735;
import net.minecraft.class_1087;
import net.minecraft.class_10889;
import net.minecraft.class_11515;
import net.minecraft.class_1657;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_1959;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2320;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2356;
import net.minecraft.class_2378;
import net.minecraft.class_2404;
import net.minecraft.class_2464;
import net.minecraft.class_2521;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2688;
import net.minecraft.class_2806;
import net.minecraft.class_2812;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2902;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3481;
import net.minecraft.class_3610;
import net.minecraft.class_3619;
import net.minecraft.class_3620;
import net.minecraft.class_4696;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_638;
import net.minecraft.class_773;
import net.minecraft.class_777;
import net.minecraft.class_8923;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.biome.BiomeColorCalculator;
import xaero.map.biome.BiomeGetter;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.core.XaeroWorldMapCore;
import xaero.map.exception.SilentException;
import xaero.map.gui.GuiMap;
import xaero.map.misc.CachedFunction;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapBlock;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.OverlayBuilder;
import xaero.map.region.OverlayManager;

public abstract class MapWriter {
    public static final int NO_Y_VALUE = Short.MAX_VALUE;
    public static final int MAX_TRANSPARENCY_BLEND_DEPTH = 5;
    public static final String[] DEFAULT_RESOURCE = new String[]{"minecraft", ""};
    private int X;
    private int Z;
    private int playerChunkX;
    private int playerChunkZ;
    private int loadDistance;
    private int startTileChunkX;
    private int startTileChunkZ;
    private int endTileChunkX;
    private int endTileChunkZ;
    private int insideX;
    private int insideZ;
    private long updateCounter;
    private int caveStart;
    private int writingLayer = Integer.MAX_VALUE;
    private int writtenCaveStart = Integer.MAX_VALUE;
    private boolean clearCachedColours;
    private MapBlock loadingObject;
    private OverlayBuilder overlayBuilder;
    private final class_2338.class_2339 mutableLocalPos;
    private final class_2338.class_2339 mutableGlobalPos;
    protected final class_5819 usedRandom = class_5819.method_43049((long)0L);
    private long lastWrite = -1L;
    private long lastWriteTry = -1L;
    private int workingFrameCount;
    private long framesFreedTime = -1L;
    public long writeFreeSinceLastWrite = -1L;
    private int writeFreeSizeTiles;
    private int writeFreeFullUpdateTargetTime;
    private MapProcessor mapProcessor;
    private ArrayList<class_2680> buggedStates;
    private BlockStateShortShapeCache blockStateShortShapeCache;
    private int topH;
    private final CachedFunction<class_2688<?, ?>, Boolean> transparentCache;
    private int firstTransparentStateY;
    private final class_2338.class_2339 mutableBlockPos3;
    private CachedFunction<class_3610, class_2680> fluidToBlock;
    private BiomeGetter biomeGetter;
    private ArrayList<MapRegion> regionBuffer = new ArrayList();
    private MapTileChunk rightChunk = null;
    private MapTileChunk bottomRightChunk = null;
    private HashMap<String, Integer> textureColours;
    private HashMap<class_2680, Integer> blockColours;
    private final Object2IntMap<class_2680> blockTintIndices;
    private long lastLayerSwitch;
    protected List<class_10889> reusableBlockModelPartList;
    private class_2680 lastBlockStateForTextureColor = null;
    private int lastBlockStateForTextureColorResult = -1;

    public MapWriter(OverlayManager overlayManager, BlockStateShortShapeCache blockStateShortShapeCache, BiomeGetter biomeGetter) {
        this.loadingObject = new MapBlock();
        this.textureColours = new HashMap();
        this.blockColours = new HashMap();
        this.overlayBuilder = new OverlayBuilder(overlayManager);
        this.mutableLocalPos = new class_2338.class_2339();
        this.mutableGlobalPos = new class_2338.class_2339();
        this.buggedStates = new ArrayList();
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.transparentCache = new CachedFunction<class_2688, Boolean>(state -> this.shouldOverlay((class_2688<?, ?>)state));
        this.mutableBlockPos3 = new class_2338.class_2339();
        this.fluidToBlock = new CachedFunction<class_3610, class_2680>(class_3610::method_15759);
        this.biomeGetter = biomeGetter;
        this.blockTintIndices = new Object2IntOpenHashMap();
        this.reusableBlockModelPartList = new ArrayList<class_10889>();
    }

    protected abstract boolean blockStateHasTranslucentRenderType(class_2680 var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onRender(BiomeColorCalculator biomeColorCalculator, OverlayManager overlayManager) {
        block36: {
            long before = System.nanoTime();
            try {
                if (WorldMap.crashHandler.getCrashedBy() != null) break block36;
                Object object = this.mapProcessor.renderThreadPauseSync;
                synchronized (object) {
                    if (!this.mapProcessor.isWritingPaused() && !this.mapProcessor.isWaitingForWorldUpdate() && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete() && this.mapProcessor.isCurrentMultiworldWritable()) {
                        if (this.mapProcessor.getWorld() == null || this.mapProcessor.isCurrentMapLocked() || this.mapProcessor.getMapWorld().isCacheOnlyMode()) {
                            return;
                        }
                        if (this.mapProcessor.getCurrentWorldId() != null && !this.mapProcessor.ignoreWorld((class_1937)this.mapProcessor.getWorld()) && (WorldMap.settings.updateChunks || WorldMap.settings.loadChunks || this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave())) {
                            Object blockTintProvider;
                            long passed;
                            double playerZ;
                            double playerY;
                            double playerX;
                            Object object2 = this.mapProcessor.mainStuffSync;
                            synchronized (object2) {
                                if (this.mapProcessor.mainWorld != this.mapProcessor.getWorld()) {
                                    return;
                                }
                                if (this.mapProcessor.getWorld().method_27983() != this.mapProcessor.getMapWorld().getCurrentDimensionId()) {
                                    return;
                                }
                                playerX = this.mapProcessor.mainPlayerX;
                                playerY = this.mapProcessor.mainPlayerY;
                                playerZ = this.mapProcessor.mainPlayerZ;
                            }
                            XaeroWorldMapCore.ensureField();
                            int lengthX = this.endTileChunkX - this.startTileChunkX + 1;
                            int lengthZ = this.endTileChunkZ - this.startTileChunkZ + 1;
                            if (this.lastWriteTry == -1L) {
                                lengthX = 3;
                                lengthZ = 3;
                            }
                            int sizeTileChunks = lengthX * lengthZ;
                            int sizeTiles = sizeTileChunks * 4 * 4;
                            int sizeBasedTargetTime = sizeTiles * 1000 / 1500;
                            int fullUpdateTargetTime = Math.max(100, sizeBasedTargetTime);
                            long time = System.currentTimeMillis();
                            long l = passed = this.lastWrite == -1L ? 0L : time - this.lastWrite;
                            if (this.lastWriteTry == -1L || this.writeFreeSizeTiles != sizeTiles || this.writeFreeFullUpdateTargetTime != fullUpdateTargetTime || this.workingFrameCount > 30) {
                                this.framesFreedTime = time;
                                this.writeFreeSizeTiles = sizeTiles;
                                this.writeFreeFullUpdateTargetTime = fullUpdateTargetTime;
                                this.workingFrameCount = 0;
                            }
                            long sinceLastWrite = Math.min(passed, this.writeFreeSinceLastWrite);
                            if (this.framesFreedTime != -1L) {
                                sinceLastWrite = time - this.framesFreedTime;
                            }
                            long tilesToUpdate = Math.min(sinceLastWrite * (long)sizeTiles / (long)fullUpdateTargetTime, 100L);
                            if (this.lastWrite == -1L || tilesToUpdate != 0L) {
                                this.lastWrite = time;
                            }
                            if (tilesToUpdate != 0L) {
                                if (this.framesFreedTime == -1L) {
                                    int timeLimit = (int)(Math.min(sinceLastWrite, 50L) * 86960L);
                                    long writeStartNano = System.nanoTime();
                                    class_2378<class_1959> biomeRegistry = this.mapProcessor.worldBiomeRegistry;
                                    boolean loadChunks = WorldMap.settings.loadChunks || this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave();
                                    boolean updateChunks = WorldMap.settings.updateChunks || this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave();
                                    boolean ignoreHeightmaps = this.mapProcessor.getMapWorld().isIgnoreHeightmaps();
                                    boolean flowers = WorldMap.settings.flowers;
                                    boolean detailedDebug = WorldMap.settings.detailed_debug;
                                    int caveDepth = WorldMap.settings.caveModeDepth;
                                    class_2338.class_2339 mutableBlockPos3 = this.mutableBlockPos3;
                                    blockTintProvider = this.mapProcessor.getWorldBlockTintProvider();
                                    class_638 world = this.mapProcessor.getWorld();
                                    class_2378<class_2248> blockRegistry = this.mapProcessor.getWorldBlockRegistry();
                                    int i = 0;
                                    while ((long)i < tilesToUpdate) {
                                        if (this.writeMap((class_1937)world, blockRegistry, playerX, playerY, playerZ, biomeRegistry, biomeColorCalculator, overlayManager, loadChunks, updateChunks, ignoreHeightmaps, flowers, detailedDebug, mutableBlockPos3, (BlockTintProvider)blockTintProvider, caveDepth)) {
                                            --i;
                                        }
                                        if (System.nanoTime() - writeStartNano >= (long)timeLimit) break;
                                        ++i;
                                    }
                                    ++this.workingFrameCount;
                                } else {
                                    this.writeFreeSinceLastWrite = sinceLastWrite;
                                    this.framesFreedTime = -1L;
                                }
                            }
                            this.lastWriteTry = time;
                            int startRegionX = this.startTileChunkX >> 3;
                            int startRegionZ = this.startTileChunkZ >> 3;
                            int endRegionX = this.endTileChunkX >> 3;
                            int endRegionZ = this.endTileChunkZ >> 3;
                            boolean shouldRequestLoading = false;
                            LeveledRegion<?> nextToLoad = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
                            shouldRequestLoading = nextToLoad != null ? nextToLoad.shouldAllowAnotherRegionToLoad() : true;
                            this.regionBuffer.clear();
                            int comparisonChunkX = this.playerChunkX - 16;
                            int comparisonChunkZ = this.playerChunkZ - 16;
                            LeveledRegion.setComparison(comparisonChunkX, comparisonChunkZ, 0, comparisonChunkX, comparisonChunkZ);
                            for (int visitRegionX = startRegionX; visitRegionX <= endRegionX; ++visitRegionX) {
                                for (int visitRegionZ = startRegionZ; visitRegionZ <= endRegionZ; ++visitRegionZ) {
                                    MapRegion visitRegion = this.mapProcessor.getLeafMapRegion(this.writingLayer, visitRegionX, visitRegionZ, true);
                                    if (visitRegion != null && visitRegion.getLoadState() == 2) {
                                        visitRegion.registerVisit();
                                    }
                                    blockTintProvider = visitRegion;
                                    synchronized (blockTintProvider) {
                                        if (visitRegion.isResting() && shouldRequestLoading && visitRegion.canRequestReload_unsynced() && visitRegion.getLoadState() != 2) {
                                            visitRegion.calculateSortingChunkDistance();
                                            Misc.addToListOfSmallest(10, this.regionBuffer, visitRegion);
                                        }
                                        continue;
                                    }
                                }
                            }
                            int toRequest = 1;
                            int counter = 0;
                            for (int i = 0; i < this.regionBuffer.size() && counter < toRequest; ++i) {
                                MapRegion region = this.regionBuffer.get(i);
                                if (region == nextToLoad && this.regionBuffer.size() > 1) continue;
                                MapRegion mapRegion = region;
                                synchronized (mapRegion) {
                                    if (!region.canRequestReload_unsynced() || region.getLoadState() == 2) {
                                        continue;
                                    }
                                    region.setBeingWritten(true);
                                    this.mapProcessor.getMapSaveLoad().requestLoad(region, "writing");
                                    if (counter == 0) {
                                        this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing(region);
                                    }
                                    ++counter;
                                    if (region.getLoadState() == 4) {
                                        break;
                                    }
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            catch (Throwable e) {
                WorldMap.crashHandler.setCrashedBy(e);
            }
        }
    }

    private int getWriteDistance() {
        int limit;
        int n = limit = this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave() ? Integer.MAX_VALUE : WorldMap.settings.mapWritingDistance;
        if (limit < 0) {
            limit = Integer.MAX_VALUE;
        }
        return Math.min(limit, Math.min(32, (Integer)class_310.method_1551().field_1690.method_42503().method_41753()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeMap(class_1937 world, class_2378<class_2248> blockRegistry, double playerX, double playerY, double playerZ, class_2378<class_1959> biomeRegistry, BiomeColorCalculator biomeColorCalculator, OverlayManager overlayManager, boolean loadChunks, boolean updateChunks, boolean ignoreHeightmaps, boolean flowers, boolean detailedDebug, class_2338.class_2339 mutableBlockPos3, BlockTintProvider blockTintProvider, int caveDepth) {
        boolean onlyLoad = loadChunks && (!updateChunks || this.updateCounter % 5L != 0L);
        class_1937 class_19372 = world;
        synchronized (class_19372) {
            if (this.insideX == 0 && this.insideZ == 0) {
                if (this.X == 0 && this.Z == 0) {
                    this.writtenCaveStart = this.caveStart;
                }
                this.mapProcessor.updateCaveStart();
                int newWritingLayer = this.mapProcessor.getCurrentCaveLayer();
                if (this.writingLayer != newWritingLayer && System.currentTimeMillis() - this.lastLayerSwitch > 300L) {
                    this.writingLayer = newWritingLayer;
                    this.lastLayerSwitch = System.currentTimeMillis();
                }
                this.loadDistance = this.getWriteDistance();
                if (this.writingLayer != Integer.MAX_VALUE && !(class_310.method_1551().field_1755 instanceof GuiMap)) {
                    this.loadDistance = Math.min(16, this.loadDistance);
                }
                this.caveStart = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().getLayer(this.writingLayer).getCaveStart();
                if (this.caveStart != this.writtenCaveStart) {
                    this.loadDistance = Math.min(4, this.loadDistance);
                }
                this.playerChunkX = (int)Math.floor(playerX) >> 4;
                this.playerChunkZ = (int)Math.floor(playerZ) >> 4;
                this.startTileChunkX = this.playerChunkX - this.loadDistance >> 2;
                this.startTileChunkZ = this.playerChunkZ - this.loadDistance >> 2;
                this.endTileChunkX = this.playerChunkX + this.loadDistance >> 2;
                this.endTileChunkZ = this.playerChunkZ + this.loadDistance >> 2;
            }
            // MONITOREXIT @DISABLED, blocks:[0, 1] lbl25 : MonitorExitStatement: MONITOREXIT : var21_18
            int tileChunkX = this.startTileChunkX + this.X;
            int tileChunkZ = this.startTileChunkZ + this.Z;
            int tileChunkLocalX = tileChunkX & 7;
            int tileChunkLocalZ = tileChunkZ & 7;
            int chunkX = tileChunkX * 4 + this.insideX;
            int chunkZ = tileChunkZ * 4 + this.insideZ;
            boolean wasSkipped = this.writeChunk(world, blockRegistry, this.loadDistance, onlyLoad, biomeRegistry, overlayManager, loadChunks, updateChunks, ignoreHeightmaps, flowers, detailedDebug, mutableBlockPos3, blockTintProvider, caveDepth, this.caveStart, this.writingLayer, tileChunkX, tileChunkZ, tileChunkLocalX, tileChunkLocalZ, chunkX, chunkZ);
            return wasSkipped && (Math.abs(chunkX - this.playerChunkX) > 8 || Math.abs(chunkZ - this.playerChunkZ) > 8);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean writeChunk(class_1937 world, class_2378<class_2248> blockRegistry, int distance, boolean onlyLoad, class_2378<class_1959> biomeRegistry, OverlayManager overlayManager, boolean loadChunks, boolean updateChunks, boolean ignoreHeightmaps, boolean flowers, boolean detailedDebug, class_2338.class_2339 mutableBlockPos3, BlockTintProvider blockTintProvider, int caveDepth, int caveStart, int layerToWrite, int tileChunkX, int tileChunkZ, int tileChunkLocalX, int tileChunkLocalZ, int chunkX, int chunkZ) {
        int regionX = tileChunkX >> 3;
        int regionZ = tileChunkZ >> 3;
        MapTileChunk tileChunk = null;
        this.rightChunk = null;
        MapTileChunk bottomChunk = null;
        this.bottomRightChunk = null;
        int worldBottomY = world.method_31607();
        int worldTopY = world.method_31600() + 1;
        MapRegion region = this.mapProcessor.getLeafMapRegion(layerToWrite, regionX, regionZ, true);
        boolean wasSkipped = true;
        Object object = region.writerThreadPauseSync;
        synchronized (object) {
            if (!region.isWritingPaused()) {
                boolean regionIsResting;
                boolean isProperLoadState;
                boolean createdTileChunk = false;
                MapRegion mapRegion = region;
                synchronized (mapRegion) {
                    boolean bl = isProperLoadState = region.getLoadState() == 2;
                    if (isProperLoadState) {
                        region.registerVisit();
                    }
                    if (regionIsResting = region.isResting()) {
                        region.setBeingWritten(true);
                        tileChunk = region.getChunk(tileChunkLocalX, tileChunkLocalZ);
                        if (isProperLoadState && tileChunk == null) {
                            tileChunk = new MapTileChunk(region, tileChunkX, tileChunkZ);
                            region.setChunk(tileChunkLocalX, tileChunkLocalZ, tileChunk);
                            tileChunk.setLoadState((byte)2);
                            region.setAllCachePrepared(false);
                            createdTileChunk = true;
                        }
                        if (!region.isNormalMapData()) {
                            region.getDim().getLayeredMapRegions().applyToEachLoadedLayer((i, layer) -> {
                                if (i.intValue() != region.getCaveLayer()) {
                                    MapRegion sameRegionAnotherLayer = this.mapProcessor.getLeafMapRegion((int)i, regionX, regionZ, true);
                                    sameRegionAnotherLayer.setOutdatedWithOtherLayers(true);
                                    sameRegionAnotherLayer.setHasHadTerrain();
                                }
                            });
                        }
                    }
                }
                if (regionIsResting && isProperLoadState) {
                    if (tileChunk != null && tileChunk.getLoadState() == 2) {
                        if (!tileChunk.getLeafTexture().shouldUpload()) {
                            boolean cave = caveStart != Integer.MAX_VALUE;
                            boolean fullCave = caveStart == Integer.MIN_VALUE;
                            int lowH = worldBottomY;
                            if (cave && !fullCave && (lowH = caveStart + 1 - caveDepth) < worldBottomY) {
                                lowH = worldBottomY;
                            }
                            if (chunkX >= this.playerChunkX - distance && chunkX <= this.playerChunkX + distance && chunkZ >= this.playerChunkZ - distance && chunkZ <= this.playerChunkZ + distance) {
                                class_2818 chunk = (class_2818)world.method_8402(chunkX, chunkZ, class_2806.field_12803, false);
                                MapTile mapTile = tileChunk.getTile(this.insideX, this.insideZ);
                                boolean chunkUpdated = false;
                                try {
                                    chunkUpdated = chunk != null && (mapTile == null || mapTile.getWrittenCaveStart() != caveStart || mapTile.getWrittenCaveDepth() != caveDepth || (Boolean)XaeroWorldMapCore.chunkCleanField.get(chunk) == false);
                                }
                                catch (IllegalAccessException | IllegalArgumentException e) {
                                    throw new RuntimeException(e);
                                }
                                if (chunkUpdated && !(chunk instanceof class_2812)) {
                                    boolean edgeChunk = false;
                                    block8: for (int i2 = -1; i2 < 2; ++i2) {
                                        for (int j = -1; j < 2; ++j) {
                                            class_2818 neighbor;
                                            if (i2 == 0 && j == 0 || (neighbor = world.method_8497(chunkX + i2, chunkZ + j)) != null && !(neighbor instanceof class_2812)) continue;
                                            edgeChunk = true;
                                            break block8;
                                        }
                                    }
                                    if (!edgeChunk && (mapTile == null && loadChunks || mapTile != null && updateChunks && (!onlyLoad || mapTile.getWrittenCaveStart() != caveStart || mapTile.getWrittenCaveDepth() != caveDepth))) {
                                        wasSkipped = false;
                                        if (mapTile == null) {
                                            mapTile = this.mapProcessor.getTilePool().get(this.mapProcessor.getCurrentDimension(), chunkX, chunkZ);
                                            tileChunk.setChanged(true);
                                        }
                                        MapTileChunk prevTileChunk = tileChunk.getNeighbourTileChunk(0, -1, this.mapProcessor, false);
                                        MapTileChunk prevTileChunkDiagonal = tileChunk.getNeighbourTileChunk(-1, -1, this.mapProcessor, false);
                                        MapTileChunk prevTileChunkHorisontal = tileChunk.getNeighbourTileChunk(-1, 0, this.mapProcessor, false);
                                        int sectionBasedHeight = this.getSectionBasedHeight(chunk, 64);
                                        class_2902.class_2903 typeWorldSurface = class_2902.class_2903.field_13202;
                                        MapTile bottomTile = this.insideZ < 3 ? tileChunk.getTile(this.insideX, this.insideZ + 1) : null;
                                        MapTile rightTile = this.insideX < 3 ? tileChunk.getTile(this.insideX + 1, this.insideZ) : null;
                                        boolean triedFetchingBottomChunk = false;
                                        boolean triedFetchingRightChunk = false;
                                        for (int x = 0; x < 16; ++x) {
                                            for (int z = 0; z < 16; ++z) {
                                                boolean xEdge;
                                                int mappedHeight = chunk.method_12005(typeWorldSurface, x, z);
                                                int startHeight = cave && !fullCave ? caveStart : (ignoreHeightmaps || mappedHeight < worldBottomY ? sectionBasedHeight : mappedHeight);
                                                if (startHeight >= worldTopY) {
                                                    startHeight = worldTopY - 1;
                                                }
                                                MapBlock currentPixel = mapTile.isLoaded() ? mapTile.getBlock(x, z) : null;
                                                this.loadPixel(world, blockRegistry, this.loadingObject, currentPixel, chunk, x, z, startHeight, lowH, cave, fullCave, mappedHeight, mapTile.wasWrittenOnce(), ignoreHeightmaps, biomeRegistry, flowers, worldBottomY, mutableBlockPos3);
                                                this.loadingObject.fixHeightType(x, z, mapTile, tileChunk, prevTileChunk, prevTileChunkDiagonal, prevTileChunkHorisontal, this.loadingObject.getEffectiveHeight(this.blockStateShortShapeCache), true, this.blockStateShortShapeCache);
                                                boolean equalsSlopesExcluded = this.loadingObject.equalsSlopesExcluded(currentPixel);
                                                boolean fullyEqual = this.loadingObject.equals(currentPixel, equalsSlopesExcluded);
                                                if (fullyEqual) continue;
                                                MapBlock loadedBlock = this.loadingObject;
                                                mapTile.setBlock(x, z, loadedBlock);
                                                this.loadingObject = currentPixel != null ? currentPixel : new MapBlock();
                                                if (equalsSlopesExcluded) continue;
                                                tileChunk.setChanged(true);
                                                boolean zEdge = z == 15;
                                                boolean bl = xEdge = x == 15;
                                                if (!zEdge && !xEdge || currentPixel != null && currentPixel.getEffectiveHeight(this.blockStateShortShapeCache) == loadedBlock.getEffectiveHeight(this.blockStateShortShapeCache)) continue;
                                                if (zEdge) {
                                                    if (!triedFetchingBottomChunk && bottomTile == null && this.insideZ == 3 && tileChunkLocalZ < 7) {
                                                        bottomChunk = region.getChunk(tileChunkLocalX, tileChunkLocalZ + 1);
                                                        triedFetchingBottomChunk = true;
                                                        MapTile mapTile2 = bottomTile = bottomChunk != null ? bottomChunk.getTile(this.insideX, 0) : null;
                                                        if (bottomTile != null) {
                                                            bottomChunk.setChanged(true);
                                                        }
                                                    }
                                                    if (bottomTile != null && bottomTile.isLoaded()) {
                                                        bottomTile.getBlock(x, 0).setSlopeUnknown(true);
                                                        if (!xEdge) {
                                                            bottomTile.getBlock(x + 1, 0).setSlopeUnknown(true);
                                                        }
                                                    }
                                                    if (!xEdge) continue;
                                                    this.updateBottomRightTile(region, tileChunk, bottomChunk, tileChunkLocalX, tileChunkLocalZ);
                                                    continue;
                                                }
                                                if (!xEdge) continue;
                                                if (!triedFetchingRightChunk && rightTile == null && this.insideX == 3 && tileChunkLocalX < 7) {
                                                    this.rightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ);
                                                    triedFetchingRightChunk = true;
                                                    MapTile mapTile3 = rightTile = this.rightChunk != null ? this.rightChunk.getTile(0, this.insideZ) : null;
                                                    if (rightTile != null) {
                                                        this.rightChunk.setChanged(true);
                                                    }
                                                }
                                                if (rightTile == null || !rightTile.isLoaded()) continue;
                                                rightTile.getBlock(0, z + 1).setSlopeUnknown(true);
                                            }
                                        }
                                        mapTile.setWorldInterpretationVersion(1);
                                        if (mapTile.getWrittenCaveStart() != caveStart) {
                                            tileChunk.setChanged(true);
                                        }
                                        mapTile.setWrittenCave(caveStart, caveDepth);
                                        tileChunk.setTile(this.insideX, this.insideZ, mapTile, this.blockStateShortShapeCache);
                                        mapTile.setWrittenOnce(true);
                                        mapTile.setLoaded(true);
                                        Misc.setReflectFieldValue(chunk, XaeroWorldMapCore.chunkCleanField, true);
                                    }
                                }
                            }
                        }
                        if (createdTileChunk) {
                            if (tileChunk.includeInSave()) {
                                tileChunk.setHasHadTerrain();
                            }
                            this.mapProcessor.getMapRegionHighlightsPreparer().prepare(region, tileChunkLocalX, tileChunkLocalZ, false);
                            if (!tileChunk.includeInSave() && !tileChunk.hasHighlightsIfUndiscovered()) {
                                region.setChunk(tileChunkLocalX, tileChunkLocalZ, null);
                                tileChunk = null;
                            }
                        }
                    }
                    if (tileChunk != null && this.insideX == 3 && this.insideZ == 3 && tileChunk.wasChanged()) {
                        tileChunk.updateBuffers(this.mapProcessor, blockTintProvider, overlayManager, detailedDebug, this.blockStateShortShapeCache);
                        if (bottomChunk == null && tileChunkLocalZ < 7) {
                            bottomChunk = region.getChunk(tileChunkLocalX, tileChunkLocalZ + 1);
                        }
                        if (this.rightChunk == null && tileChunkLocalX < 7) {
                            this.rightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ);
                        }
                        if (this.bottomRightChunk == null && tileChunkLocalX < 7 && tileChunkLocalZ < 7) {
                            this.bottomRightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ + 1);
                        }
                        if (bottomChunk != null && bottomChunk.wasChanged()) {
                            bottomChunk.updateBuffers(this.mapProcessor, blockTintProvider, overlayManager, detailedDebug, this.blockStateShortShapeCache);
                            bottomChunk.setChanged(false);
                        }
                        if (this.rightChunk != null && this.rightChunk.wasChanged()) {
                            this.rightChunk.setToUpdateBuffers(true);
                            this.rightChunk.setChanged(false);
                        }
                        if (this.bottomRightChunk != null && this.bottomRightChunk.wasChanged()) {
                            this.bottomRightChunk.setToUpdateBuffers(true);
                            this.bottomRightChunk.setChanged(false);
                        }
                        tileChunk.setChanged(false);
                    }
                }
            } else {
                this.insideX = 3;
                this.insideZ = 3;
            }
        }
        ++this.insideZ;
        if (this.insideZ > 3) {
            this.insideZ = 0;
            ++this.insideX;
            if (this.insideX > 3) {
                this.insideX = 0;
                ++this.Z;
                if (this.Z > this.endTileChunkZ - this.startTileChunkZ) {
                    this.Z = 0;
                    ++this.X;
                    if (this.X > this.endTileChunkX - this.startTileChunkX) {
                        this.X = 0;
                        ++this.updateCounter;
                    }
                }
            }
        }
        return wasSkipped;
    }

    public void updateBottomRightTile(MapRegion region, MapTileChunk tileChunk, MapTileChunk bottomChunk, int tileChunkLocalX, int tileChunkLocalZ) {
        MapTile bottomRightTile;
        MapTile mapTile = bottomRightTile = this.insideX < 3 && this.insideZ < 3 ? tileChunk.getTile(this.insideX + 1, this.insideZ + 1) : null;
        if (bottomRightTile == null) {
            if (this.insideX == 3 && tileChunkLocalX < 7) {
                if (this.insideZ == 3) {
                    if (tileChunkLocalZ < 7) {
                        this.bottomRightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ + 1);
                    }
                    MapTile mapTile2 = bottomRightTile = this.bottomRightChunk != null ? this.bottomRightChunk.getTile(0, 0) : null;
                    if (bottomRightTile != null) {
                        this.bottomRightChunk.setChanged(true);
                    }
                } else {
                    if (this.rightChunk == null) {
                        this.rightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ);
                    }
                    MapTile mapTile3 = bottomRightTile = this.rightChunk != null ? this.rightChunk.getTile(0, this.insideZ + 1) : null;
                    if (bottomRightTile != null) {
                        this.rightChunk.setChanged(true);
                    }
                }
            } else if (this.insideX != 3 && this.insideZ == 3 && tileChunkLocalZ < 7) {
                MapTile mapTile4 = bottomRightTile = bottomChunk != null ? bottomChunk.getTile(this.insideX + 1, 0) : null;
                if (bottomRightTile != null) {
                    bottomChunk.setChanged(true);
                }
            }
        }
        if (bottomRightTile != null && bottomRightTile.isLoaded()) {
            bottomRightTile.getBlock(0, 0).setSlopeUnknown(true);
        }
    }

    public int getSectionBasedHeight(class_2818 bchunk, int startY) {
        class_2826 searchedSection;
        int i;
        class_2826[] sections = bchunk.method_12006();
        if (sections.length == 0) {
            return 0;
        }
        int chunkBottomY = bchunk.method_31607();
        int playerSection = Math.min(startY - chunkBottomY >> 4, sections.length - 1);
        if (playerSection < 0) {
            playerSection = 0;
        }
        int result = 0;
        for (i = playerSection; i < sections.length; ++i) {
            searchedSection = sections[i];
            if (searchedSection.method_38292()) continue;
            result = chunkBottomY + (i << 4) + 15;
        }
        if (playerSection > 0 && result == 0) {
            for (i = playerSection - 1; i >= 0; --i) {
                searchedSection = sections[i];
                if (searchedSection.method_38292()) continue;
                result = chunkBottomY + (i << 4) + 15;
                break;
            }
        }
        return result;
    }

    public boolean isGlowing(class_2680 state) {
        return (double)state.method_26213() >= 0.5;
    }

    private boolean shouldOverlayCached(class_2688<?, ?> state) {
        return this.transparentCache.apply(state);
    }

    public boolean shouldOverlay(class_2688<?, ?> state) {
        if (state instanceof class_2680) {
            class_2680 blockState = (class_2680)state;
            if (blockState.method_26204() instanceof class_2189 || blockState.method_26204() instanceof class_8923) {
                return true;
            }
            return this.blockStateHasTranslucentRenderType(blockState);
        }
        class_3610 fluidState = (class_3610)state;
        return class_4696.method_23680((class_3610)fluidState) == class_11515.field_60926;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isInvisible(class_2680 state, class_2248 b, boolean flowers) {
        boolean isFlower;
        if (!(b instanceof class_2404) && state.method_26217() == class_2464.field_11455) {
            return true;
        }
        if (b == class_2246.field_10336) {
            return true;
        }
        if (b == class_2246.field_10479) {
            return true;
        }
        if (b == class_2246.field_10033 || b == class_2246.field_10285) {
            return true;
        }
        boolean bl = isFlower = b instanceof class_2521 || b instanceof class_2356 || b instanceof class_10735 && state.method_26164(class_3481.field_20339);
        if (b instanceof class_2320 && !isFlower) {
            return true;
        }
        if (isFlower && !flowers) {
            return true;
        }
        ArrayList<class_2680> arrayList = this.buggedStates;
        synchronized (arrayList) {
            if (this.buggedStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasVanillaColor(class_2680 state, class_1937 world, class_2378<class_2248> blockRegistry, class_2338 pos) {
        class_3620 materialColor = null;
        try {
            materialColor = state.method_26205((class_1922)world, pos);
        }
        catch (Throwable t) {
            ArrayList<class_2680> arrayList = this.buggedStates;
            synchronized (arrayList) {
                this.buggedStates.add(state);
            }
            WorldMap.LOGGER.info("Broken vanilla map color definition found: " + String.valueOf(blockRegistry.method_10221((Object)state.method_26204())));
        }
        return materialColor != null && materialColor.field_16011 != 0;
    }

    private class_2680 unpackFramedBlocks(class_2680 original, class_1937 world, class_2338 globalPos) {
        class_2586 tileEntity;
        if (original.method_26204() instanceof class_2189) {
            return original;
        }
        class_2680 result = original;
        if (SupportMods.framedBlocks() && SupportMods.supportFramedBlocks.isFrameBlock(world, null, original) && (tileEntity = world.method_8321(globalPos)) != null && ((result = SupportMods.supportFramedBlocks.unpackFramedBlock(world, null, original, tileEntity)) == null || result.method_26204() instanceof class_2189)) {
            result = original;
        }
        return result;
    }

    public void loadPixel(class_1937 world, class_2378<class_2248> blockRegistry, MapBlock pixel, MapBlock currentPixel, class_2818 bchunk, int insideX, int insideZ, int highY, int lowY, boolean cave, boolean fullCave, int mappedHeight, boolean canReuseBiomeColours, boolean ignoreHeightmaps, class_2378<class_1959> biomeRegistry, boolean flowers, int worldBottomY, class_2338.class_2339 mutableBlockPos3) {
        pixel.prepareForWriting(worldBottomY);
        this.overlayBuilder.startBuilding();
        boolean underair = !cave || fullCave;
        boolean shouldEnterGround = fullCave;
        class_2680 opaqueState = null;
        byte workingLight = -1;
        boolean worldHasSkyLight = world.method_8597().comp_642();
        byte workingSkyLight = worldHasSkyLight ? (byte)15 : 0;
        this.topH = lowY;
        this.mutableGlobalPos.method_10103((bchunk.method_12004().field_9181 << 4) + insideX, lowY - 1, (bchunk.method_12004().field_9180 << 4) + insideZ);
        boolean shouldExtendTillTheBottom = false;
        int transparentSkipY = 0;
        int h = highY;
        while (h >= lowY) {
            class_2248 b;
            this.mutableLocalPos.method_10103(insideX, h, insideZ);
            class_2680 state = bchunk.method_8320((class_2338)this.mutableLocalPos);
            if (state == null) {
                state = class_2246.field_10124.method_9564();
            }
            this.mutableGlobalPos.method_33098(h);
            state = this.unpackFramedBlocks(state, world, (class_2338)this.mutableGlobalPos);
            class_3610 fluidFluidState = state.method_26227();
            boolean bl = shouldExtendTillTheBottom = !shouldExtendTillTheBottom && !this.overlayBuilder.isEmpty() && this.firstTransparentStateY - h >= 5;
            if (shouldExtendTillTheBottom) {
                for (transparentSkipY = h - 1; transparentSkipY >= lowY; --transparentSkipY) {
                    class_3610 traceFluidState;
                    class_2680 traceState = bchunk.method_8320((class_2338)mutableBlockPos3.method_10103(insideX, transparentSkipY, insideZ));
                    if (traceState == null) {
                        traceState = class_2246.field_10124.method_9564();
                    }
                    if (!(traceFluidState = traceState.method_26227()).method_15769()) {
                        if (!this.shouldOverlayCached((class_2688<?, ?>)traceFluidState)) break;
                        if (!(traceState.method_26204() instanceof class_2189) && traceState.method_26204() == this.fluidToBlock.apply(traceFluidState).method_26204()) continue;
                    }
                    if (!this.shouldOverlayCached((class_2688<?, ?>)traceState)) break;
                }
            }
            this.mutableGlobalPos.method_33098(h + 1);
            workingLight = (byte)world.method_8314(class_1944.field_9282, (class_2338)this.mutableGlobalPos);
            if (cave && workingLight < 15 && worldHasSkyLight) {
                workingSkyLight = !ignoreHeightmaps && !fullCave && highY >= mappedHeight ? (byte)15 : (byte)((byte)world.method_8314(class_1944.field_9284, (class_2338)this.mutableGlobalPos));
            }
            this.mutableGlobalPos.method_33098(h);
            if (!(fluidFluidState.method_15769() || cave && shouldEnterGround)) {
                underair = true;
                class_2680 fluidState = this.fluidToBlock.apply(fluidFluidState);
                if (this.loadPixelHelp(pixel, currentPixel, world, blockRegistry, fluidState, workingLight, workingSkyLight, bchunk, insideX, insideZ, h, canReuseBiomeColours, cave, fluidFluidState, biomeRegistry, transparentSkipY, shouldExtendTillTheBottom, flowers, underair)) {
                    opaqueState = state;
                    break;
                }
            }
            if ((b = state.method_26204()) instanceof class_2189) {
                underair = true;
            } else if (underair && state.method_26204() != this.fluidToBlock.apply(fluidFluidState).method_26204()) {
                if (cave && shouldEnterGround) {
                    if (!(state.method_50011() || state.method_45474() || state.method_26223() == class_3619.field_15971 || this.shouldOverlayCached((class_2688<?, ?>)state))) {
                        underair = false;
                        shouldEnterGround = false;
                    }
                } else if (this.loadPixelHelp(pixel, currentPixel, world, blockRegistry, state, workingLight, workingSkyLight, bchunk, insideX, insideZ, h, canReuseBiomeColours, cave, null, biomeRegistry, transparentSkipY, shouldExtendTillTheBottom, flowers, underair)) {
                    opaqueState = state;
                    break;
                }
            }
            h = shouldExtendTillTheBottom ? transparentSkipY : h - 1;
        }
        if (h < lowY) {
            h = lowY;
        }
        class_5321<class_1959> blockBiome = null;
        class_2680 state = opaqueState == null ? class_2246.field_10124.method_9564() : opaqueState;
        this.overlayBuilder.finishBuilding(pixel);
        byte light = 0;
        if (opaqueState != null) {
            light = workingLight;
            if (cave && light < 15 && pixel.getNumberOfOverlays() == 0 && workingSkyLight > light) {
                light = workingSkyLight;
            }
        } else {
            h = worldBottomY;
        }
        if (!canReuseBiomeColours || currentPixel == null || currentPixel.getState() != state || currentPixel.getTopHeight() != this.topH) {
            this.mutableGlobalPos.method_33098(this.topH);
            blockBiome = this.biomeGetter.getBiome(world, (class_2338)this.mutableGlobalPos, biomeRegistry);
            this.mutableGlobalPos.method_33098(h);
        } else {
            blockBiome = currentPixel.getBiome();
        }
        if (this.overlayBuilder.getOverlayBiome() != null) {
            blockBiome = this.overlayBuilder.getOverlayBiome();
        }
        boolean glowing = this.isGlowing(state);
        pixel.write(state, h, this.topH, blockBiome, light, glowing, cave);
    }

    private boolean loadPixelHelp(MapBlock pixel, MapBlock currentPixel, class_1937 world, class_2378<class_2248> blockRegistry, class_2680 state, byte light, byte skyLight, class_2818 bchunk, int insideX, int insideZ, int h, boolean canReuseBiomeColours, boolean cave, class_3610 fluidFluidState, class_2378<class_1959> biomeRegistry, int transparentSkipY, boolean shouldExtendTillTheBottom, boolean flowers, boolean underair) {
        class_2248 b = state.method_26204();
        if (this.isInvisible(state, b, flowers)) {
            return false;
        }
        if (this.shouldOverlayCached((class_2688<?, ?>)(fluidFluidState == null ? state : fluidFluidState))) {
            if (cave && !underair) {
                return false;
            }
            if (h > this.topH) {
                this.topH = h;
            }
            byte overlayLight = light;
            if (this.overlayBuilder.isEmpty()) {
                this.firstTransparentStateY = h;
                if (cave && skyLight > overlayLight) {
                    overlayLight = skyLight;
                }
            }
            if (shouldExtendTillTheBottom) {
                this.overlayBuilder.getCurrentOverlay().increaseOpacity(this.overlayBuilder.getCurrentOverlay().getState().method_26193() * (h - transparentSkipY));
            } else {
                class_5321<class_1959> overlayBiome = this.overlayBuilder.getOverlayBiome();
                if (overlayBiome == null) {
                    overlayBiome = canReuseBiomeColours && currentPixel != null && currentPixel.getNumberOfOverlays() > 0 && currentPixel.getOverlays().get(0).getState() == state ? currentPixel.getBiome() : this.biomeGetter.getBiome(world, (class_2338)this.mutableGlobalPos, biomeRegistry);
                }
                this.overlayBuilder.build(state, state.method_26193(), overlayLight, this.mapProcessor, overlayBiome);
            }
            return false;
        }
        if (!this.hasVanillaColor(state, world, blockRegistry, (class_2338)this.mutableGlobalPos)) {
            return false;
        }
        if (cave && !underair) {
            return true;
        }
        if (h > this.topH) {
            this.topH = h;
        }
        return true;
    }

    protected abstract List<class_777> getQuads(class_1087 var1, class_1937 var2, class_2338 var3, class_2680 var4, class_2350 var5);

    protected abstract class_1058 getParticleIcon(class_773 var1, class_1087 var2, class_1937 var3, class_2338 var4, class_2680 var5);

    public int loadBlockColourFromTexture(class_2680 state, boolean convert, class_1937 world, class_2378<class_2248> blockRegistry, class_2338 globalPos) {
        if (this.clearCachedColours) {
            this.textureColours.clear();
            this.blockColours.clear();
            this.blockTintIndices.clear();
            this.lastBlockStateForTextureColor = null;
            this.lastBlockStateForTextureColorResult = -1;
            this.clearCachedColours = false;
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Xaero's World Map cache cleared!");
            }
        }
        if (state == this.lastBlockStateForTextureColor) {
            return this.lastBlockStateForTextureColorResult;
        }
        Integer c = this.blockColours.get(state);
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        class_2248 b = state.method_26204();
        if (c == null) {
            String name = null;
            int tintIndex = -1;
            try {
                Integer cachedColour;
                class_1058 texture;
                List<class_777> upQuads = null;
                class_773 bms = class_310.method_1551().method_1541().method_3351();
                class_1087 model = bms.method_3335(state);
                if (convert) {
                    upQuads = this.getQuads(model, world, globalPos, state, class_2350.field_11036);
                }
                class_1058 missingTexture = class_310.method_1551().method_1554().method_24153(class_1059.field_5275).method_4608(class_1047.method_4539());
                if (upQuads == null || upQuads.isEmpty() || upQuads.get(0).comp_3724() == missingTexture) {
                    texture = this.getParticleIcon(bms, model, world, globalPos, state);
                    tintIndex = 0;
                } else {
                    texture = upQuads.get(0).comp_3724();
                    tintIndex = upQuads.get(0).comp_3722();
                }
                if (texture == null) {
                    throw new SilentException("No texture for " + String.valueOf(state));
                }
                c = -1;
                name = String.valueOf(texture.method_45851().method_45816()) + ".png";
                String[] args = name.split(":");
                if (args.length < 2) {
                    MapWriter.DEFAULT_RESOURCE[1] = args[0];
                    args = DEFAULT_RESOURCE;
                }
                if ((cachedColour = this.textureColours.get(name)) == null) {
                    class_2960 location = class_2960.method_60655((String)args[0], (String)("textures/" + args[1]));
                    class_3298 resource = class_310.method_1551().method_1478().method_14486(location).orElse(null);
                    if (resource == null) {
                        throw new SilentException("No texture " + String.valueOf(location));
                    }
                    InputStream input = resource.method_14482();
                    BufferedImage img = ImageIO.read(input);
                    red = 0;
                    green = 0;
                    blue = 0;
                    int total = 0;
                    int ts = Math.min(img.getWidth(), img.getHeight());
                    if (ts > 0) {
                        int diff = Math.max(1, Math.min(4, ts / 8));
                        int parts = ts / diff;
                        Raster raster = img.getData();
                        int[] colorHolder = null;
                        for (int i = 0; i < parts; ++i) {
                            for (int j = 0; j < parts; ++j) {
                                int rgb;
                                if (img.getColorModel().getNumComponents() < 3) {
                                    colorHolder = raster.getPixel(i * diff, j * diff, colorHolder);
                                    int sample = colorHolder[0] & 0xFF;
                                    int a = 255;
                                    if (colorHolder.length > 1) {
                                        a = colorHolder[1];
                                    }
                                    rgb = a << 24 | sample << 16 | sample << 8 | sample;
                                } else {
                                    rgb = img.getRGB(i * diff, j * diff);
                                }
                                int a = rgb >> 24 & 0xFF;
                                if (rgb == 0 || a == 0) continue;
                                red += rgb >> 16 & 0xFF;
                                green += rgb >> 8 & 0xFF;
                                blue += rgb & 0xFF;
                                alpha += a;
                                ++total;
                            }
                        }
                    }
                    input.close();
                    if (total == 0) {
                        total = 1;
                    }
                    alpha /= total;
                    if (convert && (red /= total) == 0 && (green /= total) == 0 && (blue /= total) == 0) {
                        throw new SilentException("Black texture " + ts);
                    }
                    c = alpha << 24 | red << 16 | green << 8 | blue;
                    this.textureColours.put(name, c);
                } else {
                    c = cachedColour;
                }
            }
            catch (FileNotFoundException e) {
                if (convert) {
                    return this.loadBlockColourFromTexture(state, false, world, blockRegistry, globalPos);
                }
                WorldMap.LOGGER.info("Block file not found: " + String.valueOf(blockRegistry.method_10221((Object)b)));
                c = 0;
                if (state != null && state.method_26205((class_1922)world, globalPos) != null) {
                    c = state.method_26205((class_1922)world, (class_2338)globalPos).field_16011;
                }
                if (name != null) {
                    this.textureColours.put(name, c);
                }
            }
            catch (Exception e) {
                WorldMap.LOGGER.info("Exception when loading " + String.valueOf(blockRegistry.method_10221((Object)b)) + " texture, using material colour.");
                c = 0;
                if (state.method_26205((class_1922)world, globalPos) != null) {
                    c = state.method_26205((class_1922)world, (class_2338)globalPos).field_16011;
                }
                if (name != null) {
                    this.textureColours.put(name, c);
                }
                if (e instanceof SilentException) {
                    WorldMap.LOGGER.info(e.getMessage());
                }
                WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            }
            if (c != null) {
                this.blockColours.put(state, c);
                this.blockTintIndices.put((Object)state, tintIndex);
            }
        }
        this.lastBlockStateForTextureColor = state;
        this.lastBlockStateForTextureColorResult = c;
        return c;
    }

    public long getUpdateCounter() {
        return this.updateCounter;
    }

    public void resetPosition() {
        this.X = 0;
        this.Z = 0;
        this.insideX = 0;
        this.insideZ = 0;
    }

    public void requestCachedColoursClear() {
        this.clearCachedColours = true;
    }

    public void setMapProcessor(MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }

    public void setDirtyInWriteDistance(class_1657 player, class_1937 level) {
        int writeDistance = this.getWriteDistance();
        int playerChunkX = player.method_24515().method_10263() >> 4;
        int playerChunkZ = player.method_24515().method_10260() >> 4;
        int startChunkX = playerChunkX - writeDistance;
        int startChunkZ = playerChunkZ - writeDistance;
        int endChunkX = playerChunkX + writeDistance;
        int endChunkZ = playerChunkZ + writeDistance;
        for (int x = startChunkX; x < endChunkX; ++x) {
            for (int z = startChunkZ; z < endChunkZ; ++z) {
                class_2818 chunk = level.method_8497(x, z);
                if (chunk == null) continue;
                try {
                    XaeroWorldMapCore.chunkCleanField.set(chunk, false);
                    continue;
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public int getBlockTintIndex(class_2680 state) {
        return this.blockTintIndices.getInt((Object)state);
    }
}

