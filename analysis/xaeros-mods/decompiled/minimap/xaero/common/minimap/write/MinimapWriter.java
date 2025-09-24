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
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1920
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_1944
 *  net.minecraft.class_2189
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2286
 *  net.minecraft.class_2320
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2356
 *  net.minecraft.class_2378
 *  net.minecraft.class_2386
 *  net.minecraft.class_2404
 *  net.minecraft.class_2462
 *  net.minecraft.class_2464
 *  net.minecraft.class_2504
 *  net.minecraft.class_2506
 *  net.minecraft.class_2521
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_2688
 *  net.minecraft.class_2791
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
 *  net.minecraft.class_7924
 *  net.minecraft.class_8923
 */
package xaero.common.minimap.write;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import net.minecraft.class_1047;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10735;
import net.minecraft.class_1087;
import net.minecraft.class_10889;
import net.minecraft.class_11515;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1920;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2286;
import net.minecraft.class_2320;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2356;
import net.minecraft.class_2378;
import net.minecraft.class_2386;
import net.minecraft.class_2404;
import net.minecraft.class_2462;
import net.minecraft.class_2464;
import net.minecraft.class_2504;
import net.minecraft.class_2506;
import net.minecraft.class_2521;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2688;
import net.minecraft.class_2791;
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
import net.minecraft.class_7924;
import net.minecraft.class_8923;
import xaero.common.IXaeroMinimap;
import xaero.common.cache.BlockStateShortShapeCache;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.effect.Effects;
import xaero.common.exception.SilentException;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.mcworld.MinimapClientWorldData;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.minimap.region.MinimapChunk;
import xaero.common.minimap.region.MinimapTile;
import xaero.common.minimap.write.MinimapWriterHelper;
import xaero.common.minimap.write.biome.BiomeBlendCalculator;
import xaero.common.misc.CachedFunction;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.common.mods.SupportMods;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;

public abstract class MinimapWriter {
    private static final int VOID_COLOR = -16121833;
    private static final float DEFAULT_AMBIENT_LIGHT = 0.7f;
    private static final float DEFAULT_AMBIENT_LIGHT_COLORED = 0.2f;
    private static final float DEFAULT_AMBIENT_LIGHT_WHITE = 0.5f;
    private static final float DEFAULT_MAX_DIRECT_LIGHT = 0.6666667f;
    private static final float GLOWING_MAX_DIRECT_LIGHT = 0.22222224f;
    private static final String[] dimensionsToIgnore = new String[]{"FZHammer"};
    private static final int UPDATE_EVERY_RUNS = 5;
    private static final int MAXIMUM_OVERLAYS = 5;
    public static final int SUN_MINIMUM = 9;
    public static final int NO_Y_VALUE = Integer.MAX_VALUE;
    private static final int MAX_TRANSPARENCY_BLEND_DEPTH = 5;
    private IXaeroMinimap modMain;
    private MinimapSession minimapSession;
    private MinimapWriterHelper helper;
    private Minimap minimap;
    protected final class_5819 usedRandom = class_5819.method_43049((long)0L);
    private int loadingSideInChunks;
    private int updateRadius;
    private MinimapChunk[][] loadingBlocks;
    private int loadingMapChunkX;
    private int loadingMapChunkZ;
    private int loadingStartX;
    private int loadingStartZ;
    private int loadingEndX;
    private int loadingEndZ;
    private int loadingCaving;
    private int loadingLevels;
    private boolean loadingLighting;
    private float loadingSingleLevelBrightness;
    private int loadingTerrainSlopes;
    private boolean loadingTerrainDepth;
    private boolean loadingRedstone;
    private int loadingColours;
    private boolean loadingTransparency;
    private boolean loadingBiomesVanillaMode;
    private class_5321<class_1937> loadingDimension;
    private boolean loadingIgnoreHeightmaps;
    private int loadingCaveMapsDepth;
    public int loadingLightOverlayType;
    public int loadingLightOverlayMaxLight;
    public int loadingLightOverlayMinLight;
    public int loadingLightOverlayColor;
    private boolean loadingFlowers;
    private boolean loadingAdjustHeightForCarpetLikeBlocks;
    private boolean loadingStainedGlass;
    private boolean loadingLegibleCaveMode;
    private boolean loadingBiomeBlending;
    private boolean loadingNonWorldMap;
    private Long loadingSlimeSeed;
    private int loadingHighlightVersion;
    private int loadedSideInChunks;
    private MinimapChunk[][] loadedBlocks;
    private int loadedMapChunkX;
    private int loadedMapChunkZ;
    private int loadedCaving;
    private int prevLoadedCaving;
    private int loadedLevels;
    private boolean loadedLighting;
    private int loadedTerrainSlopes;
    private boolean loadedTerrainDepth;
    private boolean loadedRedstone;
    private int loadedColours;
    private boolean loadedTransparency;
    private boolean loadedBiomesVanillaMode;
    private class_5321<class_1937> loadedDimension;
    private boolean loadedIgnoreHeightmaps;
    private int loadedCaveMapsDepth;
    public int loadedLightOverlayType;
    public int loadedLightOverlayMaxLight;
    public int loadedLightOverlayMinLight;
    public int loadedLightOverlayColor;
    private boolean loadedFlowers;
    private boolean loadedAdjustHeightForCarpetLikeBlocks;
    private boolean loadedStainedGlass;
    private boolean loadedLegibleCaveMode;
    private boolean loadedBiomeBlending;
    private boolean loadedNonWorldMap;
    private Long loadedSlimeSeed;
    private int loadedHighlightVersion;
    private long loadedTime;
    private boolean settingsChanged;
    private ArrayList<Long> detectedChunkChanges;
    private int workingFrameCount;
    private long framesFreedTime = -1L;
    public long writeFreeSinceLastWrite = -1L;
    private int writeFreeSizeTiles;
    private int writeFreeFullUpdateTargetTime;
    private int updateChunkX;
    private int updateChunkZ;
    private int tileInsideX;
    private int tileInsideZ;
    private int runNumber;
    private boolean previousShouldLoad;
    private int lastCaving;
    private boolean clearBlockColours;
    private final HashMap<String, Integer> textureColours;
    private final HashMap<Integer, Integer> blockColours;
    private final Object2IntMap<class_2680> blockTintIndices;
    private final CachedFunction<class_2688<?, ?>, Boolean> transparentCache;
    private final Map<class_2680, Boolean> glowingCache;
    private long lastWrite = -1L;
    private long lastWriteTry = -1L;
    private boolean forcedRefresh;
    private MinimapChunk oldChunk;
    private class_2680 lastBlockStateForTextureColor = null;
    private int lastBlockStateForTextureColorResult = -1;
    private CachedFunction<class_3610, class_2680> fluidToBlock;
    private int updates;
    private int loads;
    private long before;
    private int processingTime;
    public boolean debugTotalTime = false;
    public long minTime = -1L;
    public long maxTime = -1L;
    public long totalTime;
    public long totalRuns;
    public long lastDebugTime = -1L;
    public long minTimeDebug;
    public long maxTimeDebug;
    public long averageTimeDebug;
    private long currentComparisonCode;
    private final List<Integer> pixelTransparentSizes;
    private final List<class_2680> pixelBlockStates;
    private final List<Integer> pixelBlockLights;
    private int firstBlockY;
    boolean isglowing;
    private final int[] underRed;
    private final int[] underGreen;
    private final int[] underBlue;
    private int sun;
    private float currentTransparencyMultiplier;
    private int blockY;
    private int blockColor;
    private final int[] red;
    private final int[] green;
    private final int[] blue;
    private final float[] brightness;
    private final float[] postBrightness;
    private final int[] tempColor;
    private boolean underair;
    private class_2680 previousTransparentState;
    private int firstTransparentStateY;
    private final class_2338.class_2339 mutableBlockPos;
    private final class_2338.class_2339 mutableBlockPos2;
    private final class_2338.class_2339 mutableBlockPos3;
    private final int[][] intUpdateArrayBuffers;
    private ArrayList<class_2680> buggedStates;
    private final class_310 mc;
    private final BiomeBlendCalculator biomeBlendCalculator;
    private final BlockStateShortShapeCache blockStateShortShapeCache;
    private final HighlighterRegistry highlighterRegistry;
    private class_1937 prevWorld;
    private DimensionHighlighterHandler dimensionHighlightHandler;
    protected List<class_10889> reusableBlockModelPartList;

    public MinimapWriter(IXaeroMinimap modMain, MinimapSession minimapSession, BlockStateShortShapeCache blockStateShortShapeCache, HighlighterRegistry highlighterRegistry) {
        this.modMain = modMain;
        this.minimapSession = minimapSession;
        this.loadingSideInChunks = 9;
        this.updateRadius = 16;
        this.loadingCaving = Integer.MAX_VALUE;
        this.loadedCaving = Integer.MAX_VALUE;
        this.prevLoadedCaving = Integer.MAX_VALUE;
        this.lastCaving = Integer.MAX_VALUE;
        this.textureColours = new HashMap();
        this.blockColours = new HashMap();
        this.loadedCaving = Integer.MAX_VALUE;
        this.red = new int[5];
        this.green = new int[5];
        this.blue = new int[5];
        this.underRed = new int[5];
        this.underGreen = new int[5];
        this.underBlue = new int[5];
        this.brightness = new float[5];
        this.postBrightness = new float[5];
        this.tempColor = new int[3];
        this.helper = new MinimapWriterHelper();
        this.mutableBlockPos = new class_2338.class_2339();
        this.mutableBlockPos2 = new class_2338.class_2339();
        this.mutableBlockPos3 = new class_2338.class_2339();
        this.intUpdateArrayBuffers = new int[5][4096];
        this.pixelBlockStates = new ArrayList<class_2680>();
        this.pixelTransparentSizes = new ArrayList<Integer>();
        this.pixelBlockLights = new ArrayList<Integer>();
        this.transparentCache = new CachedFunction<class_2688, Boolean>(state -> {
            if (state instanceof class_2680) {
                class_2680 blockState = (class_2680)state;
                if (blockState.method_26204() instanceof class_2189 || blockState.method_26204() instanceof class_8923) {
                    return true;
                }
                return this.blockStateHasTranslucentRenderType(blockState);
            }
            class_3610 fluidState = (class_3610)state;
            return class_4696.method_23680((class_3610)fluidState) == class_11515.field_60926;
        });
        this.glowingCache = new HashMap<class_2680, Boolean>();
        this.minimap = modMain.getMinimap();
        this.buggedStates = new ArrayList();
        this.detectedChunkChanges = new ArrayList();
        this.mc = class_310.method_1551();
        this.biomeBlendCalculator = new BiomeBlendCalculator();
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.fluidToBlock = new CachedFunction<class_3610, class_2680>(class_3610::method_15759);
        this.highlighterRegistry = highlighterRegistry;
        this.blockTintIndices = new Object2IntOpenHashMap();
        this.reusableBlockModelPartList = new ArrayList<class_10889>();
    }

    protected abstract boolean blockStateHasTranslucentRenderType(class_2680 var1);

    protected abstract int getBlockStateLightEmission(class_2680 var1, class_1937 var2, class_2338 var3);

    public void setupDimensionHighlightHandler(class_5321<class_1937> dimension) {
        this.dimensionHighlightHandler = new DimensionHighlighterHandler(dimension, this.highlighterRegistry, this);
    }

    private void updateTimeDebug(long before) {
        if (this.debugTotalTime) {
            long debugPassed = System.nanoTime() - before;
            this.totalTime += debugPassed;
            ++this.totalRuns;
            if (debugPassed > this.maxTime) {
                this.maxTime = debugPassed;
            }
            if (this.minTime == -1L || debugPassed < this.minTime) {
                this.minTime = debugPassed;
            }
            long time = System.currentTimeMillis();
            if (this.lastDebugTime == -1L) {
                this.lastDebugTime = time;
            } else if (time - this.lastDebugTime > 1000L) {
                this.maxTimeDebug = this.maxTime;
                this.minTimeDebug = this.minTime;
                this.averageTimeDebug = this.totalTime / this.totalRuns;
                this.maxTime = -1L;
                this.minTime = -1L;
                this.totalTime = 0L;
                this.totalRuns = 0L;
                this.lastDebugTime = time;
            }
        }
    }

    public void onRender() {
        if (!ModSettings.canEditIngameSettings()) {
            return;
        }
        long before = System.nanoTime();
        MinimapProcessor minimapProcessor = this.minimapSession.getProcessor();
        try {
            long passed;
            boolean shouldLoad;
            class_1297 player = class_310.method_1551().method_1560();
            if (player == null) {
                return;
            }
            class_1937 world = player.method_37908();
            if (world != this.prevWorld) {
                if (world != null) {
                    this.setupDimensionHighlightHandler((class_5321<class_1937>)world.method_27983());
                } else {
                    this.dimensionHighlightHandler = null;
                }
                this.loadedDimension = null;
                this.updateChunkZ = 0;
                this.updateChunkX = 0;
                this.tileInsideZ = 0;
                this.tileInsideX = 0;
                this.prevWorld = world;
                if (this.modMain.getSupportMods().framedBlocks()) {
                    this.modMain.getSupportMods().supportFramedBlocks.onWorldChange();
                }
            }
            double playerX = player.method_23317();
            double playerY = player.method_23318();
            double playerZ = player.method_23321();
            if (this.modMain.getSettings() == null || !this.modMain.getSettings().getMinimap() || world == null) {
                this.updateTimeDebug(before);
                return;
            }
            int cavingDestination = this.getCaving(minimapProcessor.isManualCaveMode(), playerX, playerY, playerZ, world);
            boolean attemptUsingWorldMapChunks = this.modMain.getSupportMods().shouldUseWorldMapChunks() && (cavingDestination == Integer.MAX_VALUE || this.modMain.getSupportMods().shouldUseWorldMapCaveChunks()) && this.modMain.getSettings().lightOverlayType <= 0;
            boolean bl = shouldLoad = !this.ignoreWorld(world) && (!attemptUsingWorldMapChunks || this.loadedNonWorldMap || this.loadingNonWorldMap || this.loadedCaving != cavingDestination || this.loadedCaving != this.loadingCaving);
            if (shouldLoad != this.previousShouldLoad) {
                this.updateChunkZ = 0;
                this.updateChunkX = 0;
                this.tileInsideZ = 0;
                this.tileInsideX = 0;
                this.previousShouldLoad = shouldLoad;
            }
            if (!shouldLoad) {
                this.updateTimeDebug(before);
                return;
            }
            XaeroMinimapCore.ensureField();
            int lengthX = Math.min(this.loadingSideInChunks, this.loadingEndX - this.loadingStartX + 1);
            int lengthZ = Math.min(this.loadingSideInChunks, this.loadingEndZ - this.loadingStartZ + 1);
            if (this.lastWriteTry == -1L) {
                lengthX = 3;
                lengthZ = 3;
            } else {
                if (lengthX > this.loadingSideInChunks) {
                    lengthX = this.loadingSideInChunks;
                }
                if (lengthZ > this.loadingSideInChunks) {
                    lengthZ = this.loadingSideInChunks;
                }
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
            int flickeringTimer = this.modMain.getSettings().caveModeToggleTimer;
            if (tilesToUpdate != 0L) {
                if (this.framesFreedTime == -1L) {
                    int timeLimit = (int)(Math.min(sinceLastWrite, 50L) * 86960L);
                    long writeStartNano = System.nanoTime();
                    if (cavingDestination == Integer.MAX_VALUE != (this.loadingCaving == Integer.MAX_VALUE) || attemptUsingWorldMapChunks == this.loadingNonWorldMap) {
                        this.updateChunkZ = 0;
                        this.updateChunkX = 0;
                        this.tileInsideZ = 0;
                        this.tileInsideX = 0;
                        this.loadedTime = time;
                    }
                    int i = 0;
                    while ((long)i < tilesToUpdate && !this.beforeWriting(attemptUsingWorldMapChunks, cavingDestination, flickeringTimer, time)) {
                        if (this.writeChunk(minimapProcessor, playerX, playerY, playerZ, world, cavingDestination, attemptUsingWorldMapChunks)) {
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
        }
        catch (Throwable e) {
            this.minimap.setCrashedWith(e);
        }
        this.updateTimeDebug(before);
    }

    private boolean beforeWriting(boolean attemptUsingWorldMapChunks, int cavingDestination, int flickeringTimer, long time) {
        if (this.tileInsideX == 0 && this.tileInsideZ == 0 && this.updateChunkX == 0 && this.updateChunkZ == 0 && attemptUsingWorldMapChunks) {
            this.loadingCaving = cavingDestination;
            this.loadingNonWorldMap = false;
            if (this.loadedCaving == Integer.MAX_VALUE == (this.loadingCaving == Integer.MAX_VALUE) || this.loadedTime == 0L || time - this.loadedTime >= (long)flickeringTimer) {
                this.loadedCaving = this.loadingCaving;
                this.loadedNonWorldMap = false;
            }
            if (!this.loadedNonWorldMap) {
                this.detectedChunkChanges.clear();
            }
            return true;
        }
        return this.tileInsideX == 3 && this.tileInsideZ == 3 && this.updateChunkX == this.loadingSideInChunks - 1 && this.updateChunkZ == this.loadingSideInChunks - 1 && this.loadingCaving == Integer.MAX_VALUE != (this.loadedCaving == Integer.MAX_VALUE) && this.loadedTime != 0L && time - this.loadedTime < (long)flickeringTimer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean writeChunk(MinimapProcessor minimapProcessor, double playerX, double playerY, double playerZ, class_1937 world, int cavingDestination, boolean attemptUsingWorldMapChunks) {
        MinimapChunk mchunk;
        long processStart = System.nanoTime();
        if (this.tileInsideX == 0 && this.tileInsideZ == 0) {
            if (this.updateChunkX == 0 && this.updateChunkZ == 0) {
                this.settingsChanged = false;
                if (this.clearBlockColours) {
                    this.settingsChanged = true;
                    this.clearBlockColours = false;
                    if (!this.blockColours.isEmpty()) {
                        this.blockColours.clear();
                        this.blockTintIndices.clear();
                        this.textureColours.clear();
                        this.lastBlockStateForTextureColor = null;
                        this.lastBlockStateForTextureColorResult = -1;
                        MinimapLogs.LOGGER.info("Minimap block colour cache cleaned.");
                    }
                }
                this.loadingSideInChunks = this.getLoadSide();
                this.updateRadius = this.getUpdateRadiusInChunks();
                int playerXInt = OptimizedMath.myFloor(playerX);
                int playerZInt = OptimizedMath.myFloor(playerZ);
                this.loadingMapChunkX = this.getMapCoord(this.loadingSideInChunks, playerXInt);
                this.loadingMapChunkZ = this.getMapCoord(this.loadingSideInChunks, playerZInt);
                int loadDistance = (Integer)this.mc.field_1690.method_42503().method_41753();
                int playerTileX = playerXInt >> 4;
                int playerTileZ = playerZInt >> 4;
                int globalStartX = playerTileX - loadDistance >> 2;
                int globalStartZ = playerTileZ - loadDistance >> 2;
                int globalEndX = playerTileX + loadDistance >> 2;
                int globalEndZ = playerTileZ + loadDistance >> 2;
                this.loadingStartX = globalStartX - this.loadingMapChunkX;
                this.loadingStartZ = globalStartZ - this.loadingMapChunkZ;
                this.loadingEndX = globalEndX - this.loadingMapChunkX;
                this.loadingEndZ = globalEndZ - this.loadingMapChunkZ;
                this.loadingCaving = cavingDestination;
                if (this.loadingCaving != Integer.MAX_VALUE || this.loadedCaving != Integer.MAX_VALUE) {
                    int maxDistance = 2;
                    if (this.loadingCaving != Integer.MAX_VALUE && this.loadedCaving == Integer.MAX_VALUE) {
                        maxDistance = 1;
                    }
                    this.loadingStartX = Math.max(this.loadingStartX, this.loadingSideInChunks / 2 - maxDistance);
                    this.loadingStartZ = Math.max(this.loadingStartZ, this.loadingSideInChunks / 2 - maxDistance);
                    this.loadingEndX = Math.min(this.loadingEndX, this.loadingSideInChunks / 2 + maxDistance);
                    this.loadingEndZ = Math.min(this.loadingEndZ, this.loadingSideInChunks / 2 + maxDistance);
                }
                if (this.loadingCaving != this.loadedCaving || this.loadingCaving != Integer.MAX_VALUE && this.prevLoadedCaving == Integer.MAX_VALUE) {
                    this.runNumber = 0;
                }
                this.loadingLighting = this.modMain.getSettings().getLighting();
                this.loadingLevels = this.loadingLighting ? 5 : 1;
                this.loadingSingleLevelBrightness = 1.0f;
                this.loadingLegibleCaveMode = this.modMain.getSettings().isLegibleCaveMaps();
                this.loadingBiomeBlending = this.modMain.getSettings().getBiomeBlending();
                if (((class_638)world).method_28103().method_28114() || this.loadingLegibleCaveMode && this.loadingCaving != Integer.MAX_VALUE) {
                    this.loadingLighting = false;
                    this.loadingLevels = 1;
                } else if (this.loadingLighting && !world.method_8597().comp_642()) {
                    this.loadingLevels = 1;
                    this.loadingSingleLevelBrightness = this.minimap.getMinimapFBORenderer().getSunBrightness(minimapProcessor, true);
                }
                this.loadingTerrainSlopes = this.modMain.getSettings().getTerrainSlopes();
                this.loadingTerrainDepth = this.modMain.getSettings().getTerrainDepth();
                this.loadingRedstone = this.modMain.getSettings().getDisplayRedstone();
                this.loadingColours = this.modMain.getSettings().getBlockColours();
                this.loadingTransparency = this.modMain.getSettings().blockTransparency;
                this.loadingBiomesVanillaMode = this.modMain.getSettings().getBiomeColorsVanillaMode();
                this.loadingDimension = world.method_27983();
                this.loadingCaveMapsDepth = this.modMain.getSettings().getCaveMapsDepth();
                this.loadingIgnoreHeightmaps = this.modMain.getSettings().isIgnoreHeightmaps();
                this.loadingLightOverlayColor = this.modMain.getSettings().lightOverlayColor;
                this.loadingLightOverlayMaxLight = this.modMain.getSettings().lightOverlayMaxLight;
                this.loadingLightOverlayMinLight = this.modMain.getSettings().lightOverlayMinLight;
                this.loadingLightOverlayType = this.modMain.getSettings().lightOverlayType;
                this.loadingFlowers = this.modMain.getSettings().getShowFlowers();
                this.loadingAdjustHeightForCarpetLikeBlocks = this.modMain.getSettings().getAdjustHeightForCarpetLikeBlocks();
                MinimapWorld minimapWorld = this.minimapSession.getWorldManager().getAutoWorld();
                if (minimapWorld != null) {
                    this.loadingSlimeSeed = this.modMain.getSettings().getSlimeChunksSeed(minimapWorld.getFullPath());
                }
                this.loadingHighlightVersion = this.dimensionHighlightHandler.getVersion();
                this.loadingStainedGlass = this.modMain.getSettings().isStainedGlassDisplayed();
                this.loadingNonWorldMap = true;
                this.settingsChanged = this.settingsChanged || this.loadedDimension != this.loadingDimension;
                this.settingsChanged = this.settingsChanged || this.loadedTerrainSlopes != this.loadingTerrainSlopes;
                this.settingsChanged = this.settingsChanged || this.loadedTerrainDepth != this.loadingTerrainDepth;
                this.settingsChanged = this.settingsChanged || this.loadedRedstone != this.loadingRedstone;
                this.settingsChanged = this.settingsChanged || this.loadedColours != this.loadingColours;
                this.settingsChanged = this.settingsChanged || this.loadedTransparency != this.loadingTransparency;
                this.settingsChanged = this.settingsChanged || this.loadingBiomesVanillaMode != this.loadedBiomesVanillaMode;
                this.settingsChanged = this.settingsChanged || this.loadingCaveMapsDepth != this.loadedCaveMapsDepth;
                this.settingsChanged = this.settingsChanged || this.loadingIgnoreHeightmaps != this.loadedIgnoreHeightmaps;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayColor != this.loadedLightOverlayColor;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayMaxLight != this.loadedLightOverlayMaxLight;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayMinLight != this.loadedLightOverlayMinLight;
                this.settingsChanged = this.settingsChanged || this.loadingLightOverlayType != this.loadedLightOverlayType;
                this.settingsChanged = this.settingsChanged || this.loadingFlowers != this.loadedFlowers;
                boolean bl = this.settingsChanged = this.settingsChanged || this.loadingAdjustHeightForCarpetLikeBlocks != this.loadedAdjustHeightForCarpetLikeBlocks;
                this.settingsChanged = this.settingsChanged || this.loadingCaving == Integer.MAX_VALUE != (this.loadedCaving == Integer.MAX_VALUE);
                this.settingsChanged = this.settingsChanged || !Objects.equals(this.loadingSlimeSeed, this.loadedSlimeSeed);
                this.settingsChanged = this.settingsChanged || this.loadingHighlightVersion != this.loadedHighlightVersion;
                this.settingsChanged = this.settingsChanged || this.loadingStainedGlass != this.loadedStainedGlass;
                this.settingsChanged = this.settingsChanged || this.loadingLegibleCaveMode != this.loadedLegibleCaveMode;
                this.settingsChanged = this.settingsChanged || this.loadingLighting != this.loadedLighting;
                this.settingsChanged = this.settingsChanged || this.loadingBiomeBlending != this.loadedBiomeBlending;
                boolean bl2 = this.settingsChanged = this.settingsChanged || this.loadingNonWorldMap != this.loadedNonWorldMap;
                if (this.loadingBlocks == null || this.loadingBlocks.length != this.loadingSideInChunks) {
                    this.loadingBlocks = new MinimapChunk[this.loadingSideInChunks][this.loadingSideInChunks];
                }
                if (this.minimap.usingFBO() && minimapProcessor.isToResetImage()) {
                    this.forcedRefresh = true;
                    minimapProcessor.setToResetImage(false);
                }
                this.biomeBlendCalculator.prepare(world, this.loadingBiomeBlending);
            }
            this.oldChunk = null;
            if (this.loadedBlocks != null) {
                int updateChunkXOld = this.loadingMapChunkX + this.updateChunkX - this.loadedMapChunkX;
                int updateChunkZOld = this.loadingMapChunkZ + this.updateChunkZ - this.loadedMapChunkZ;
                if (updateChunkXOld > -1 && updateChunkXOld < this.loadedBlocks.length && updateChunkZOld > -1 && updateChunkZOld < this.loadedBlocks.length) {
                    this.oldChunk = this.loadedBlocks[updateChunkXOld][updateChunkZOld];
                }
            }
        }
        if ((mchunk = this.loadingBlocks[this.updateChunkX][this.updateChunkZ]) == null) {
            MinimapChunk minimapChunk = new MinimapChunk(this.loadingMapChunkX + this.updateChunkX, this.loadingMapChunkZ + this.updateChunkZ);
            this.loadingBlocks[this.updateChunkX][this.updateChunkZ] = minimapChunk;
            mchunk = minimapChunk;
        } else if (this.tileInsideX == 0 && this.tileInsideZ == 0) {
            mchunk.reset(this.loadingMapChunkX + this.updateChunkX, this.loadingMapChunkZ + this.updateChunkZ);
        }
        boolean onlyLoad = this.runNumber % 5 != 0 && this.loadingLightOverlayType <= 0;
        boolean outsideRender = this.updateChunkX < this.loadingStartX || this.updateChunkX > this.loadingEndX || this.updateChunkZ < this.loadingStartZ || this.updateChunkZ > this.loadingEndZ;
        MinimapChunk topChunk = this.updateChunkZ > 0 ? this.loadingBlocks[this.updateChunkX][this.updateChunkZ - 1] : null;
        MinimapChunk topLeftChunk = this.updateChunkX > 0 && this.updateChunkZ > 0 ? this.loadingBlocks[this.updateChunkX - 1][this.updateChunkZ - 1] : null;
        MinimapChunk leftChunk = this.updateChunkX > 0 ? this.loadingBlocks[this.updateChunkX - 1][this.updateChunkZ] : null;
        boolean wasProperWrite = this.writeTile(minimapProcessor, playerX, playerY, playerZ, world, mchunk, this.oldChunk, topChunk, topLeftChunk, leftChunk, this.updateChunkX, this.updateChunkZ, this.tileInsideX, this.tileInsideZ, onlyLoad, outsideRender);
        if (this.loadingLightOverlayType > 0 && this.loadedLightOverlayType > 0) {
            onlyLoad = true;
        }
        ++this.tileInsideZ;
        if (this.tileInsideZ >= 4) {
            this.tileInsideZ = 0;
            ++this.tileInsideX;
            if (this.tileInsideX >= 4) {
                this.tileInsideX = 0;
                mchunk = this.loadingBlocks[this.updateChunkX][this.updateChunkZ];
                if (this.minimap.usingFBO() && mchunk.isHasSomething() && mchunk.isChanged()) {
                    mchunk.updateBuffers(this.loadingLevels, this.intUpdateArrayBuffers);
                    mchunk.setChanged(false);
                }
                mchunk.setLevelsBuffered(this.loadingLevels);
                if (this.updateChunkX == this.loadingSideInChunks - 1 && this.updateChunkZ == this.loadingSideInChunks - 1) {
                    if (this.runNumber % 5 == 0 && !MinimapTile.recycled.isEmpty()) {
                        MinimapTile.recycled.subList(MinimapTile.recycled.size() / 2, MinimapTile.recycled.size()).clear();
                    }
                    if (this.loadedBlocks != null) {
                        for (int i = 0; i < this.loadedBlocks.length; ++i) {
                            for (int j = 0; j < this.loadedBlocks.length; ++j) {
                                boolean shouldTransfer;
                                MinimapChunk m = this.loadedBlocks[i][j];
                                MinimapChunk lm = null;
                                if (m == null) continue;
                                m.recycleTiles();
                                int loadingX = this.loadedMapChunkX + i - this.loadingMapChunkX;
                                int loadingZ = this.loadedMapChunkZ + j - this.loadingMapChunkZ;
                                if (loadingX > -1 && loadingZ > -1 && loadingX < this.loadingSideInChunks && loadingZ < this.loadingSideInChunks) {
                                    lm = this.loadingBlocks[loadingX][loadingZ];
                                }
                                boolean bl = shouldTransfer = m.getLevelsBuffered() == this.loadingLevels && lm != null;
                                if (shouldTransfer) {
                                    MinimapChunk minimapChunk = m;
                                    synchronized (minimapChunk) {
                                        m.setBlockTextureUpload(true);
                                    }
                                }
                                for (int l = 0; l < m.getLevelsBuffered(); ++l) {
                                    if (m.getGlTexture(l) != null) {
                                        if (shouldTransfer) {
                                            lm.setGlTexture(l, m.getGlTexture(l));
                                        } else {
                                            m.getGlTexture(l).close();
                                        }
                                    }
                                    if (!shouldTransfer || lm.isRefreshRequired(l) || !m.isRefreshRequired(l)) continue;
                                    lm.copyBuffer(l, m.getBuffer(l));
                                    lm.setRefreshRequired(l, true);
                                    m.setRefreshRequired(l, false);
                                }
                            }
                        }
                    }
                    MinimapWriter i = this;
                    synchronized (i) {
                        MinimapChunk[][] bu = this.loadedBlocks;
                        this.loadedSideInChunks = this.loadingSideInChunks;
                        this.loadedBlocks = this.loadingBlocks;
                        this.loadingBlocks = bu;
                        this.loadedMapChunkX = this.loadingMapChunkX;
                        this.loadedMapChunkZ = this.loadingMapChunkZ;
                        this.loadedLevels = this.loadingLevels;
                        this.loadedLighting = this.loadingLighting;
                        this.loadedTerrainSlopes = this.loadingTerrainSlopes;
                        this.loadedTerrainDepth = this.loadingTerrainDepth;
                        this.loadedRedstone = this.loadingRedstone;
                        this.loadedColours = this.loadingColours;
                        this.loadedTransparency = this.loadingTransparency;
                        this.loadedBiomesVanillaMode = this.loadingBiomesVanillaMode;
                        this.loadedDimension = this.loadingDimension;
                        this.loadedCaveMapsDepth = this.loadingCaveMapsDepth;
                        this.loadedIgnoreHeightmaps = this.loadingIgnoreHeightmaps;
                        this.loadedLightOverlayColor = this.loadingLightOverlayColor;
                        this.loadedLightOverlayMaxLight = this.loadingLightOverlayMaxLight;
                        this.loadedLightOverlayMinLight = this.loadingLightOverlayMinLight;
                        this.loadedLightOverlayType = this.loadingLightOverlayType;
                        this.loadedFlowers = this.loadingFlowers;
                        this.loadedAdjustHeightForCarpetLikeBlocks = this.loadingAdjustHeightForCarpetLikeBlocks;
                        this.loadedSlimeSeed = this.loadingSlimeSeed;
                        this.loadedHighlightVersion = this.loadingHighlightVersion;
                        this.loadedStainedGlass = this.loadingStainedGlass;
                        this.loadedLegibleCaveMode = this.loadingLegibleCaveMode;
                        this.loadedBiomeBlending = this.loadingBiomeBlending;
                        this.loadedTime = System.currentTimeMillis();
                    }
                    this.detectedChunkChanges.clear();
                    this.prevLoadedCaving = this.loadedCaving;
                    this.loadedCaving = this.loadingCaving;
                    this.loadedNonWorldMap = true;
                    this.forcedRefresh = false;
                    ++this.runNumber;
                }
                ++this.updateChunkZ;
                if (this.updateChunkZ >= this.loadingSideInChunks) {
                    this.updateChunkZ = 0;
                    this.updateChunkX = (this.updateChunkX + 1) % this.loadingSideInChunks;
                }
            }
        }
        int passed = (int)(System.nanoTime() - processStart);
        return outsideRender && !wasProperWrite;
    }

    private boolean writeTile(MinimapProcessor minimapProcessor, double playerX, double playerY, double playerZ, class_1937 world, MinimapChunk mchunk, MinimapChunk oldChunk, MinimapChunk topChunk, MinimapChunk topLeftChunk, MinimapChunk leftChunk, int canvasX, int canvasZ, int insideX, int insideZ, boolean onlyLoad, boolean outsideRender) {
        int[] highlights;
        int mChunkInsideZ;
        int mChunkInsideX;
        int mRegionZ;
        int tileX = mchunk.getX() * 4 + insideX;
        int tileZ = mchunk.getZ() * 4 + insideZ;
        int halfSide = this.loadingSideInChunks / 2;
        int tileFromCenterX = canvasX - halfSide;
        int tileFromCenterZ = canvasZ - halfSide;
        MinimapTile oldTile = null;
        if (oldChunk != null) {
            oldTile = oldChunk.getTile(insideX, insideZ);
        }
        class_2818 bchunk = (class_2818)world.method_8402(tileX, tileZ, class_2806.field_12803, false);
        boolean neighborsLoaded = true;
        block4: for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                class_2818 nChunk;
                if (i == 0 && j == 0 || (nChunk = world.method_8497(tileX + i, tileZ + j)) != null && !(nChunk instanceof class_2812)) continue;
                neighborsLoaded = false;
                continue block4;
            }
        }
        boolean chunkUpdated = false;
        boolean chunkIsClean = true;
        try {
            chunkIsClean = bchunk == null || (Boolean)XaeroMinimapCore.chunkCleanField.get(bchunk) != false;
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        boolean bl = chunkUpdated = !chunkIsClean;
        if (chunkIsClean && bchunk != null && this.detectedChunkChanges.contains(Misc.getChunkPosAsLong(bchunk))) {
            chunkUpdated = true;
        }
        chunkUpdated = chunkUpdated || oldTile == null || oldTile.caveLevel != this.loadingCaving;
        boolean effectivelyUnloaded = bchunk == null || bchunk instanceof class_2812 || !neighborsLoaded;
        int mRegionX = mchunk.getX() >> 3;
        boolean needsHighlights = this.dimensionHighlightHandler.shouldApplyTileChunkHighlights(mRegionX, mRegionZ = mchunk.getZ() >> 3, mChunkInsideX = mchunk.getX() & 7, mChunkInsideZ = mchunk.getZ() & 7, !effectivelyUnloaded);
        if ((!needsHighlights || oldTile != null) && (outsideRender || effectivelyUnloaded) || (!chunkUpdated || onlyLoad || tileFromCenterX > this.updateRadius || tileFromCenterZ > this.updateRadius || tileFromCenterX < -this.updateRadius || tileFromCenterZ < -this.updateRadius) && oldTile != null && oldTile.isSuccess() && oldChunk.getLevelsBuffered() == this.loadingLevels && !this.settingsChanged) {
            if (oldTile != null && oldChunk.getLevelsBuffered() == this.loadingLevels && !this.settingsChanged) {
                mchunk.setTile(insideX, insideZ, oldTile);
                oldTile.setWasTransfered(true);
                if (oldTile.isHasSomething()) {
                    mchunk.setHasSomething(true);
                }
                if (this.forcedRefresh) {
                    mchunk.setChanged(true);
                }
            }
            return false;
        }
        if (oldTile != null && oldChunk.getLevelsBuffered() != this.loadingLevels) {
            oldTile = null;
        }
        MinimapTile tile = null;
        class_2902.class_2903 typeWorldSurface = class_2902.class_2903.field_13202;
        MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getWorldData((class_638)world);
        float shadowR = worldData.shadowR;
        float shadowG = worldData.shadowG;
        float shadowB = worldData.shadowB;
        int playerYi = (int)playerY;
        boolean sameCaveLevel = oldTile != null && this.loadingCaving == oldTile.caveLevel;
        boolean sameHighlights = oldTile != null && this.loadedDimension == this.loadingDimension && this.loadingHighlightVersion == oldTile.getHighlightVersion();
        boolean settingsChanged = this.settingsChanged;
        int loadingCaving = this.loadingCaving;
        int loadingLevels = this.loadingLevels;
        boolean loadingLighting = this.loadingLighting;
        float loadingSingleLevelBrightness = this.loadingSingleLevelBrightness;
        int loadingTerrainSlopes = this.loadingTerrainSlopes;
        boolean loadingTerrainDepth = this.loadingTerrainDepth;
        List<Integer> pixelTransparentSizes = this.pixelTransparentSizes;
        List<class_2680> pixelBlockStates = this.pixelBlockStates;
        List<Integer> pixelBlockLights = this.pixelBlockLights;
        int[] underRed = this.underRed;
        int[] underGreen = this.underGreen;
        int[] underBlue = this.underBlue;
        float[] postBrightness = this.postBrightness;
        float[] brightness = this.brightness;
        int[] red = this.red;
        int[] green = this.green;
        int[] blue = this.blue;
        int[] tempColor = this.tempColor;
        boolean loadingIgnoreHeightmaps = this.loadingIgnoreHeightmaps;
        int loadingCaveMapsDepth = this.loadingCaveMapsDepth;
        class_2338.class_2339 mutableBlockPos = this.mutableBlockPos;
        class_2338.class_2339 mutableBlockPos2 = this.mutableBlockPos2;
        class_2338.class_2339 mutableBlockPos3 = this.mutableBlockPos3;
        Long loadingSlimeSeed = this.loadingSlimeSeed;
        int loadedLevels = this.loadedLevels;
        IXaeroMinimap modMain = this.modMain;
        MinimapWriterHelper helper = this.helper;
        int loadingColours = this.loadingColours;
        boolean loadingRedstone = this.loadingRedstone;
        boolean loadingTransparency = this.loadingTransparency;
        int loadingLightOverlayType = this.loadingLightOverlayType;
        int loadingLightOverlayMaxLight = this.loadingLightOverlayMaxLight;
        int loadingLightOverlayMinLight = this.loadingLightOverlayMinLight;
        int loadingLightOverlayColor = this.loadingLightOverlayColor;
        boolean loadingFlowers = this.loadingFlowers;
        boolean adjustHeightForCarpetLikeBlocks = this.loadingAdjustHeightForCarpetLikeBlocks;
        boolean loadingStainedGlass = this.loadingStainedGlass;
        boolean loadingLegibleCaveMode = this.loadingLegibleCaveMode;
        boolean framedBlocksExist = modMain.getSupportMods().framedBlocks();
        int[] nArray = highlights = oldTile != null && needsHighlights ? oldTile.getHighlights() : null;
        if (needsHighlights && (highlights == null || !sameHighlights)) {
            highlights = this.dimensionHighlightHandler.applyChunkHighlightColors(tileX, tileZ);
        }
        if (effectivelyUnloaded) {
            tile = MinimapTile.getANewTile(modMain.getSettings(), tileX, tileZ, loadingSlimeSeed);
            for (int l = 0; l < loadingLevels; ++l) {
                float overlayBrightness = this.getBlockBrightness(9.0f, loadingCaving != Integer.MAX_VALUE && (loadingLighting || loadingLegibleCaveMode) ? 0 : 15, l, 0);
                for (int blockX = 0; blockX < 16; ++blockX) {
                    for (int blockZ = 0; blockZ < 16; ++blockZ) {
                        int highlight = highlights[blockZ << 4 | blockX];
                        int hlRed = highlight >> 8 & 0xFF;
                        int hlGreen = highlight >> 16 & 0xFF;
                        int hlBlue = highlight >> 24 & 0xFF;
                        tile.setRGB(l, blockX, blockZ, (int)((float)hlRed * overlayBrightness), (int)((float)hlGreen * overlayBrightness), (int)((float)hlBlue * overlayBrightness));
                        tile.setCode(blockX, blockZ, 0L, (byte)0, (byte)0, (byte)0, (byte)0);
                    }
                }
            }
            mchunk.setTile(this.tileInsideX, this.tileInsideZ, tile);
            tile.setSuccess(false);
            tile.setHasSomething(true);
            mchunk.setHasSomething(true);
            mchunk.setChanged(true);
        } else {
            int sectionBasedHeight = loadingIgnoreHeightmaps ? this.getSectionBasedHeight(bchunk, 64) : 0;
            for (int blockX = 0; blockX < 16; ++blockX) {
                for (int blockZ = 0; blockZ < 16; ++blockZ) {
                    tile = this.loadBlockColor(playerYi, world, blockX, blockZ, bchunk, tileX, tileZ, insideX, insideZ, sectionBasedHeight, typeWorldSurface, oldTile, mchunk, topChunk, topLeftChunk, leftChunk, shadowR, shadowG, shadowB, sameCaveLevel, sameHighlights, canvasX, canvasZ, !needsHighlights ? 0 : highlights[blockZ << 4 | blockX], settingsChanged, loadingCaving, loadingLevels, loadingLighting, loadingSingleLevelBrightness, loadingTerrainSlopes, loadingTerrainDepth, pixelTransparentSizes, pixelBlockStates, pixelBlockLights, underRed, underGreen, underBlue, postBrightness, brightness, red, green, blue, tempColor, loadingIgnoreHeightmaps, loadingCaveMapsDepth, mutableBlockPos, mutableBlockPos2, loadingSlimeSeed, loadedLevels, modMain, helper, loadingColours, loadingRedstone, loadingTransparency, loadingLightOverlayType, loadingLightOverlayMaxLight, loadingLightOverlayMinLight, loadingLightOverlayColor, loadingFlowers, adjustHeightForCarpetLikeBlocks, loadingStainedGlass, loadingLegibleCaveMode, mutableBlockPos3, framedBlocksExist);
                }
            }
            tile.setHasTerrain(true);
        }
        tile.caveLevel = loadingCaving;
        tile.setHighlights(highlights);
        tile.setHighlightVersion(this.loadingHighlightVersion);
        if (!chunkIsClean) {
            long chunkPosLong = Misc.getChunkPosAsLong(bchunk);
            if (!this.detectedChunkChanges.contains(chunkPosLong)) {
                this.detectedChunkChanges.add(chunkPosLong);
            }
            try {
                XaeroMinimapCore.chunkCleanField.set(bchunk, true);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public MinimapTile loadBlockColor(int playerYi, class_1937 world, int insideX, int insideZ, class_2818 bchunk, int tileX, int tileZ, int tileInsideX, int tileInsideZ, int sectionBasedHeight, class_2902.class_2903 typeWorldSurface, MinimapTile oldTile, MinimapChunk mchunk, MinimapChunk topChunk, MinimapChunk topLeftChunk, MinimapChunk leftChunk, float shadowR, float shadowG, float shadowB, boolean sameCaveLevel, boolean sameHighlights, int canvasX, int canvasZ, int highlight, boolean settingsChanged, int loadingCaving, int loadingLevels, boolean loadingLighting, float loadingSingleLevelBrightness, int loadingTerrainSlopes, boolean loadingTerrainDepth, List<Integer> pixelTransparentSizes, List<class_2680> pixelBlockStates, List<Integer> pixelBlockLights, int[] underRed, int[] underGreen, int[] underBlue, float[] postBrightness, float[] brightness, int[] red, int[] green, int[] blue, int[] tempColor, boolean loadingIgnoreHeightmaps, int loadingCaveMapsDepth, class_2338.class_2339 mutableBlockPos, class_2338.class_2339 mutableBlockPos2, Long loadingSlimeSeed, int loadedLevels, IXaeroMinimap modMain, MinimapWriterHelper helper, int loadingColours, boolean loadingRedstone, boolean loadingTransparency, int loadingLightOverlayType, int loadingLightOverlayMaxLight, int loadingLightOverlayMinLight, int loadingLightOverlayColor, boolean loadingFlowers, boolean adjustHeightForCarpetLikeBlocks, boolean loadingStainedGlass, boolean loadingLegibleCaveMode, class_2338.class_2339 mutableBlockPos3, boolean framedBlocksExist) {
        int i;
        boolean reuseColour;
        class_2818 prevChunk2;
        MinimapTile tile;
        class_2680 state;
        int bottom;
        int lowY;
        int highY;
        int worldBottomY = world.method_31607();
        if (loadingCaving != Integer.MAX_VALUE) {
            highY = loadingCaving;
        } else {
            int height = bchunk.method_12005(typeWorldSurface, insideX, insideZ);
            highY = loadingIgnoreHeightmaps || height < worldBottomY ? sectionBasedHeight : height;
        }
        if (highY >= world.method_31600()) {
            highY = world.method_31600() - 1;
        }
        if ((lowY = (bottom = loadingCaving != Integer.MAX_VALUE ? highY + 1 - loadingCaveMapsDepth : worldBottomY)) < worldBottomY) {
            lowY = worldBottomY;
        }
        pixelTransparentSizes.clear();
        pixelBlockStates.clear();
        pixelBlockLights.clear();
        this.currentComparisonCode = 0L;
        byte currentComparisonCodeAdd = 0;
        byte currentComparisonCodeAdd2 = 0;
        this.blockY = 0;
        for (int i2 = 0; i2 < loadingLevels; ++i2) {
            underRed[i2] = 0;
            underGreen[i2] = 0;
            underBlue[i2] = 0;
        }
        this.currentTransparencyMultiplier = 1.0f;
        this.sun = 15;
        this.blockColor = 0;
        this.isglowing = false;
        double secondaryBR = 1.0;
        double secondaryBG = 1.0;
        double secondaryBB = 1.0;
        class_2248 block = this.findBlock(world, bchunk, insideX, insideZ, highY, lowY, loadingCaving, loadingRedstone, mutableBlockPos, mutableBlockPos2, loadingColours, loadingTransparency, pixelBlockLights, pixelBlockStates, loadingLevels, loadingLighting, pixelTransparentSizes, loadingFlowers, loadingStainedGlass, mutableBlockPos3, framedBlocksExist);
        class_2680 class_26802 = state = pixelBlockStates.isEmpty() ? null : pixelBlockStates.get(pixelBlockStates.size() - 1);
        if (adjustHeightForCarpetLikeBlocks && state != null && this.blockStateShortShapeCache.isShort(state)) {
            --this.blockY;
        }
        boolean isglowing = this.isglowing;
        int blockY = this.blockY;
        long currentComparisonCode = this.currentComparisonCode;
        boolean success = true;
        int prevHeight = Integer.MAX_VALUE;
        int prevHeightDiagonal = Integer.MAX_VALUE;
        int prevInsideX = insideX - 1;
        int prevInsideZ = insideZ - 1;
        boolean xEdge = prevInsideX < 0;
        boolean zEdge = prevInsideZ < 0;
        MinimapTile prevHeightSrc = tile = mchunk.getTile(tileInsideX, tileInsideZ);
        MinimapTile prevHeightDiagonalSrc = tile;
        if (zEdge) {
            prevInsideZ = 15;
            if (tileInsideZ > 0) {
                prevHeightSrc = mchunk.getTile(tileInsideX, tileInsideZ - 1);
            } else if (topChunk != null) {
                prevHeightSrc = topChunk.getTile(tileInsideX, 3);
            }
        }
        if (xEdge) {
            prevInsideX = 15;
            if (zEdge) {
                if (tileInsideZ > 0 && tileInsideX > 0) {
                    prevHeightDiagonalSrc = mchunk.getTile(tileInsideX - 1, tileInsideZ - 1);
                } else if (tileInsideX == 0 && tileInsideZ == 0) {
                    if (topLeftChunk != null) {
                        prevHeightDiagonalSrc = topLeftChunk.getTile(3, 3);
                    }
                } else if (tileInsideX == 0) {
                    if (leftChunk != null) {
                        prevHeightDiagonalSrc = leftChunk.getTile(3, tileInsideZ - 1);
                    }
                } else if (topChunk != null) {
                    prevHeightDiagonalSrc = topChunk.getTile(tileInsideX - 1, 3);
                }
            } else if (tileInsideX > 0) {
                prevHeightDiagonalSrc = mchunk.getTile(tileInsideX - 1, tileInsideZ);
            } else if (leftChunk != null) {
                prevHeightDiagonalSrc = leftChunk.getTile(3, tileInsideZ);
            }
        } else {
            prevHeightDiagonalSrc = prevHeightSrc;
        }
        if (prevHeightSrc != null && (prevHeightSrc == tile || prevHeightSrc.hasTerrain())) {
            prevHeight = prevHeightSrc.getHeight(insideX, prevInsideZ);
            if (prevHeightSrc != tile && prevHeightSrc.caveLevel != loadingCaving) {
                success = false;
            }
        } else if (zEdge) {
            prevHeight = blockY;
            if (pixelTransparentSizes.isEmpty()) {
                try {
                    prevChunk2 = world.method_8497(tileX, tileZ - 1);
                    if (prevChunk2 != null) {
                        prevHeight = prevChunk2.method_12005(typeWorldSurface, insideX, prevInsideZ);
                    }
                }
                catch (IllegalStateException prevChunk2) {
                    // empty catch block
                }
            }
            success = false;
        }
        if (prevHeightDiagonalSrc != null && (prevHeightDiagonalSrc == tile || prevHeightDiagonalSrc.hasTerrain())) {
            prevHeightDiagonal = prevHeightDiagonalSrc.getHeight(prevInsideX, prevInsideZ);
            if (prevHeightDiagonalSrc != tile && prevHeightDiagonalSrc.caveLevel != loadingCaving) {
                success = false;
            }
        } else if (xEdge || zEdge) {
            prevHeightDiagonal = blockY;
            if (pixelTransparentSizes.isEmpty()) {
                try {
                    class_2818 class_28182 = xEdge && zEdge ? world.method_8497(tileX - 1, tileZ - 1) : (prevChunk2 = zEdge ? world.method_8497(tileX, tileZ - 1) : world.method_8497(tileX - 1, tileZ));
                    if (prevChunk2 != null) {
                        prevHeightDiagonal = prevChunk2.method_12005(typeWorldSurface, prevInsideX, prevInsideZ);
                    }
                }
                catch (IllegalStateException prevChunk3) {
                    // empty catch block
                }
            }
            success = false;
        }
        int verticalSlope = 0;
        int diagonalSlope = 0;
        if (loadingTerrainSlopes > 0) {
            if (prevHeight != Integer.MAX_VALUE) {
                verticalSlope = Math.max(-128, Math.min(127, blockY - prevHeight));
            }
            if (prevHeightDiagonal != Integer.MAX_VALUE) {
                diagonalSlope = Math.max(-128, Math.min(127, blockY - prevHeightDiagonal));
            }
        }
        for (int i3 = 0; i3 < pixelBlockLights.size(); ++i3) {
            int l = pixelBlockLights.get(i3);
            if (i3 <= 1) {
                currentComparisonCodeAdd = (byte)(currentComparisonCodeAdd | l << 4 * i3 + 1);
            }
            if (i3 < 1) continue;
            currentComparisonCode |= (long)(l << 4 * (i3 - 1) >> 3);
        }
        int add2Calculation = 17;
        for (int i4 = 0; i4 < pixelTransparentSizes.size(); ++i4) {
            add2Calculation = add2Calculation * 37 + pixelTransparentSizes.get(i4);
        }
        currentComparisonCodeAdd = (byte)(currentComparisonCodeAdd | add2Calculation >> 8 & 1);
        currentComparisonCodeAdd2 = (byte)add2Calculation;
        boolean bl = reuseColour = !settingsChanged && sameCaveLevel && sameHighlights && !oldTile.pixelChanged(insideX, insideZ, currentComparisonCode, currentComparisonCodeAdd, currentComparisonCodeAdd2, (byte)verticalSlope, (byte)diagonalSlope);
        if (!reuseColour) {
            int i5;
            boolean hasTransparentLayer;
            if (highlight != 0 && block == null) {
                this.sun = 0;
            }
            int firstSun = this.sun;
            boolean bl2 = hasTransparentLayer = highlight != 0 || !pixelTransparentSizes.isEmpty();
            if (hasTransparentLayer && firstSun != 15) {
                this.sun = 15;
            }
            if (highlight != 0) {
                int hlRed = highlight >> 8 & 0xFF;
                int hlGreen = highlight >> 16 & 0xFF;
                int hlBlue = highlight >> 24 & 0xFF;
                int hlAlpha = highlight & 0xFF;
                float hlAlphaFloat = (float)hlAlpha / 255.0f;
                this.applyTransparentLayer(hlRed, hlGreen, hlBlue, hlAlphaFloat, true);
            }
            boolean legibleCaveMode = loadingLegibleCaveMode && loadingCaving != Integer.MAX_VALUE;
            this.calculateBlockColors(world, bchunk, insideX, insideZ, mutableBlockPos2, pixelTransparentSizes, pixelBlockStates, pixelBlockLights, loadingColours, loadingLightOverlayColor, loadingCaving, loadingLevels, loadingLighting, loadingSingleLevelBrightness, legibleCaveMode);
            int blockColor = this.blockColor;
            float currentTransparencyMultiplier = this.currentTransparencyMultiplier;
            int sun = this.sun;
            if (block == null) {
                sun = 15;
            }
            isglowing = block != null && isglowing;
            int topLight = pixelBlockLights.isEmpty() ? 0 : pixelBlockLights.get(0);
            int cr = blockColor >> 16 & 0xFF;
            int cg = blockColor >> 8 & 0xFF;
            int cb = blockColor & 0xFF;
            if (isglowing) {
                helper.getGlowingColour(cr, cg, cb, tempColor);
                cr = tempColor[0];
                cg = tempColor[1];
                cb = tempColor[2];
                if (hasTransparentLayer && pixelTransparentSizes.isEmpty()) {
                    topLight = 15;
                }
            }
            if (!isglowing || hasTransparentLayer) {
                int blockLight = pixelBlockLights.isEmpty() ? 0 : pixelBlockLights.get(pixelBlockLights.size() - 1);
                int firstBlockY = this.firstBlockY;
                for (i5 = 0; i5 < loadingLevels; ++i5) {
                    postBrightness[i5] = 1.0f;
                    if (legibleCaveMode) {
                        if (!isglowing) {
                            float f = brightness[i5] = block == null ? 1.0f : (1.0f + (float)blockY - (float)bottom) / (float)(1 + highY - bottom);
                        }
                        if (!hasTransparentLayer) continue;
                        float transparentLayerCaveBrightness = block == null && pixelTransparentSizes.isEmpty() ? this.getFixedSkyLightBlockBrightness(9.0f, 0.0f, 0) : (1.0f + (float)firstBlockY - (float)bottom) / (float)(1 + highY - bottom);
                        int n = i5;
                        underRed[n] = (int)((float)underRed[n] * transparentLayerCaveBrightness);
                        int n2 = i5;
                        underGreen[n2] = (int)((float)underGreen[n2] * transparentLayerCaveBrightness);
                        int n3 = i5;
                        underBlue[n3] = (int)((float)underBlue[n3] * transparentLayerCaveBrightness);
                        continue;
                    }
                    if (!isglowing) {
                        brightness[i5] = !hasTransparentLayer ? (block == null ? 1.0f : (loadingLevels != 1 ? this.getBlockBrightness(9.0f, sun, i5, blockLight) : this.getFixedSkyLightBlockBrightness(9.0f, loadingSingleLevelBrightness, blockLight))) : this.getBlockBrightness(9.0f, sun, 0, blockLight);
                    }
                    if (!hasTransparentLayer) continue;
                    postBrightness[i5] = loadingLevels != 1 ? this.getBlockBrightness(9.0f, firstSun, i5, topLight) : this.getFixedSkyLightBlockBrightness(9.0f, loadingSingleLevelBrightness, topLight);
                }
            }
            float depthBrightness = 1.0f;
            if (block != null && !isglowing && loadingTerrainDepth && !legibleCaveMode) {
                float min;
                depthBrightness = loadingCaving != Integer.MAX_VALUE ? 0.7f + 0.3f * (float)(blockY - bottom) / (float)(highY - bottom) : (float)blockY / 63.0f;
                float max = loadingTerrainSlopes >= 2 ? 1.0f : 1.15f;
                float f = min = loadingTerrainSlopes >= 2 ? 0.9f : 0.7f;
                if (depthBrightness > max) {
                    depthBrightness = max;
                } else if (depthBrightness < min) {
                    depthBrightness = min;
                }
            }
            if (block != null && loadingTerrainSlopes > 0) {
                if (loadingTerrainSlopes == 1) {
                    if (!isglowing) {
                        if (verticalSlope > 0) {
                            depthBrightness = (float)((double)depthBrightness * 1.15);
                        } else if (verticalSlope < 0) {
                            depthBrightness = (float)((double)depthBrightness * 0.85);
                        }
                    }
                } else {
                    float ambientLightColored = 0.2f;
                    float ambientLightWhite = 0.5f;
                    float maxDirectLight = 0.6666667f;
                    if (isglowing) {
                        ambientLightColored = 0.0f;
                        ambientLightWhite = 1.0f;
                        maxDirectLight = 0.22222224f;
                    }
                    float cos = 0.0f;
                    if (loadingTerrainSlopes == 2) {
                        float crossZ = -verticalSlope;
                        if (crossZ < 1.0f) {
                            if (verticalSlope == 1 && diagonalSlope == 1) {
                                cos = 1.0f;
                            } else {
                                float crossX = verticalSlope - diagonalSlope;
                                float cast = 1.0f - crossZ;
                                float crossMagnitude = (float)Math.sqrt(crossX * crossX + 1.0f + crossZ * crossZ);
                                cos = (float)((double)(cast / crossMagnitude) / Math.sqrt(2.0));
                            }
                        }
                    } else if (verticalSlope >= 0) {
                        if (verticalSlope == 1) {
                            cos = 1.0f;
                        } else {
                            float surfaceDirectionMagnitude = (float)Math.sqrt(verticalSlope * verticalSlope + 1);
                            float castToMostLit = verticalSlope + 1;
                            cos = (float)((double)(castToMostLit / surfaceDirectionMagnitude) / Math.sqrt(2.0));
                        }
                    }
                    float directLightClamped = 0.0f;
                    if (cos == 1.0f) {
                        directLightClamped = maxDirectLight;
                    } else if (cos > 0.0f) {
                        directLightClamped = (float)Math.ceil(cos * 10.0f) / 10.0f * maxDirectLight * 0.88388f;
                    }
                    float whiteLight = ambientLightWhite + directLightClamped;
                    secondaryBR *= (double)(shadowR * ambientLightColored + whiteLight);
                    secondaryBG *= (double)(shadowG * ambientLightColored + whiteLight);
                    secondaryBB *= (double)(shadowB * ambientLightColored + whiteLight);
                }
            }
            secondaryBR *= (double)depthBrightness;
            secondaryBG *= (double)depthBrightness;
            secondaryBB *= (double)depthBrightness;
            if (loadingLightOverlayType > 0) {
                int testLight;
                int blockLight;
                int n = blockLight = pixelBlockLights.isEmpty() ? 0 : pixelBlockLights.get(0);
                int n4 = loadingLightOverlayType == 1 ? blockLight : (testLight = loadingLightOverlayType == 2 ? firstSun : Math.max(blockLight, firstSun));
                if (testLight >= loadingLightOverlayMinLight && testLight <= loadingLightOverlayMaxLight) {
                    int overlayColor = ModSettings.COLORS[loadingLightOverlayColor];
                    int overlayRed = (overlayColor >> 16 & 0xFF) * 2 / 3;
                    int overlayGreen = (overlayColor >> 8 & 0xFF) * 2 / 3;
                    int overlayBlue = (overlayColor & 0xFF) * 2 / 3;
                    int i6 = 0;
                    while (i6 < loadingLevels) {
                        float destColorScale = (isglowing ? 1.0f : postBrightness[i6]) / 3.0f;
                        int n5 = i6;
                        underRed[n5] = (int)((float)underRed[n5] * destColorScale);
                        int n6 = i6;
                        underGreen[n6] = (int)((float)underGreen[n6] * destColorScale);
                        int n7 = i6;
                        underBlue[n7] = (int)((float)underBlue[n7] * destColorScale);
                        int n8 = i6;
                        brightness[n8] = brightness[n8] * destColorScale;
                        postBrightness[i6] = 1.0f;
                        int n9 = i6;
                        underRed[n9] = underRed[n9] + overlayRed;
                        int n10 = i6;
                        underGreen[n10] = underGreen[n10] + overlayGreen;
                        int n11 = i6++;
                        underBlue[n11] = underBlue[n11] + overlayBlue;
                    }
                    if (isglowing) {
                        secondaryBR /= 3.0;
                        secondaryBG /= 3.0;
                        secondaryBB /= 3.0;
                    }
                }
            }
            for (i5 = 0; i5 < loadingLevels; ++i5) {
                float b;
                if (isglowing) {
                    b = 1.0f;
                    if (!hasTransparentLayer) {
                        postBrightness[i5] = 1.0f;
                    }
                } else {
                    b = brightness[i5];
                }
                red[i5] = (int)(((double)((float)cr * b) * secondaryBR * (double)currentTransparencyMultiplier + (double)underRed[i5]) * (double)postBrightness[i5]);
                if (red[i5] > 255) {
                    red[i5] = 255;
                }
                green[i5] = (int)(((double)((float)cg * b) * secondaryBG * (double)currentTransparencyMultiplier + (double)underGreen[i5]) * (double)postBrightness[i5]);
                if (green[i5] > 255) {
                    green[i5] = 255;
                }
                blue[i5] = (int)(((double)((float)cb * b) * secondaryBB * (double)currentTransparencyMultiplier + (double)underBlue[i5]) * (double)postBrightness[i5]);
                if (blue[i5] <= 255) continue;
                blue[i5] = 255;
            }
        } else {
            for (i = 0; i < loadingLevels; ++i) {
                red[i] = oldTile.getRed(i, insideX, insideZ);
                green[i] = oldTile.getGreen(i, insideX, insideZ);
                blue[i] = oldTile.getBlue(i, insideX, insideZ);
            }
        }
        if (tile == null) {
            tile = MinimapTile.getANewTile(modMain.getSettings(), tileX, tileZ, loadingSlimeSeed);
            mchunk.setTile(tileInsideX, tileInsideZ, tile);
        }
        if (this.notEmptyColor(red, green, blue)) {
            tile.setHasSomething(true);
            mchunk.setHasSomething(true);
        }
        tile.setHeight(insideX, insideZ, blockY);
        tile.setCode(insideX, insideZ, currentComparisonCode, currentComparisonCodeAdd, currentComparisonCodeAdd2, (byte)verticalSlope, (byte)diagonalSlope);
        if (tile.isSuccess()) {
            tile.setSuccess(success);
        }
        if (oldTile != null) {
            int oldTileDarkestLevel = loadedLevels - 1;
            int tileDarkestLevel = loadingLevels - 1;
            if (oldTile.getRed(oldTileDarkestLevel, insideX, insideZ) != red[tileDarkestLevel] || oldTile.getGreen(oldTileDarkestLevel, insideX, insideZ) != green[tileDarkestLevel] || oldTile.getBlue(oldTileDarkestLevel, insideX, insideZ) != blue[tileDarkestLevel]) {
                mchunk.setChanged(true);
            }
        } else {
            mchunk.setChanged(true);
        }
        for (i = 0; i < loadingLevels; ++i) {
            tile.setRGB(i, insideX, insideZ, red[i], green[i], blue[i]);
        }
        return tile;
    }

    private class_2680 unpackFramedBlocks(class_2680 original, class_1937 world, class_2338 globalPos) {
        class_2586 tileEntity;
        if (original.method_26204() instanceof class_2189) {
            return original;
        }
        class_2680 result = original;
        SupportMods supportMods = this.modMain.getSupportMods();
        if (supportMods.supportFramedBlocks.isFrameBlock(world, null, original) && (tileEntity = world.method_8321(globalPos)) != null && ((result = supportMods.supportFramedBlocks.unpackFramedBlock(world, null, original, tileEntity)) == null || result.method_26204() instanceof class_2189)) {
            result = original;
        }
        return result;
    }

    public class_2248 findBlock(class_1937 world, class_2818 bchunk, int insideX, int insideZ, int highY, int lowY, int loadingCaving, boolean loadingRedstone, class_2338.class_2339 mutableBlockPos, class_2338.class_2339 mutableBlockPos2, int loadingColours, boolean loadingTransparency, List<Integer> pixelBlockLights, List<class_2680> pixelBlockStates, int loadingLevels, boolean loadingLighting, List<Integer> pixelTransparentSizes, boolean loadingFlowers, boolean loadingStainedGlass, class_2338.class_2339 mutableBlockPos3, boolean framedBlocksExist) {
        this.underair = loadingCaving == Integer.MAX_VALUE;
        this.previousTransparentState = null;
        if (highY == Integer.MAX_VALUE || lowY == Integer.MAX_VALUE) {
            return null;
        }
        boolean shouldExtendTillTheBottom = false;
        int transparentSkipY = 0;
        int i = highY;
        while (i >= lowY) {
            block14: {
                class_2680 state;
                block13: {
                    mutableBlockPos.method_10103(insideX, i, insideZ);
                    class_2338.class_2339 globalPos = mutableBlockPos2.method_10103((bchunk.method_12004().field_9181 << 4) + insideX, i, (bchunk.method_12004().field_9180 << 4) + insideZ);
                    state = bchunk.method_8320((class_2338)mutableBlockPos);
                    if (state == null) {
                        state = class_2246.field_10124.method_9564();
                    }
                    if (framedBlocksExist) {
                        state = this.unpackFramedBlocks(state, world, (class_2338)globalPos);
                    }
                    class_3610 fluidFluidState = state.method_26227();
                    boolean bl = shouldExtendTillTheBottom = !shouldExtendTillTheBottom && !pixelBlockStates.isEmpty() && this.firstTransparentStateY - i >= 5;
                    if (shouldExtendTillTheBottom) {
                        for (transparentSkipY = i - 1; transparentSkipY >= lowY; --transparentSkipY) {
                            class_3610 traceFluidState;
                            class_2680 traceState = bchunk.method_8320((class_2338)mutableBlockPos3.method_10103(insideX, transparentSkipY, insideZ));
                            if (traceState == null) {
                                traceState = class_2246.field_10124.method_9564();
                            }
                            if (!(traceFluidState = traceState.method_26227()).method_15769()) {
                                if (!this.isTransparent((class_2688<?, ?>)traceFluidState)) break;
                                if (!(traceState.method_26204() instanceof class_2189) && traceState.method_26204() == this.fluidToBlock.apply(traceFluidState).method_26204()) continue;
                            }
                            if (!this.isTransparent((class_2688<?, ?>)traceState)) break;
                        }
                    }
                    if (fluidFluidState.method_15769()) break block13;
                    this.underair = true;
                    class_2680 fluidState = this.fluidToBlock.apply(fluidFluidState);
                    if (this.findBlockHelp(world, (class_2791)bchunk, insideX, i, insideZ, fluidState, fluidFluidState, transparentSkipY, shouldExtendTillTheBottom, loadingCaving, loadingRedstone, mutableBlockPos, mutableBlockPos2, loadingColours, loadingTransparency, pixelBlockLights, pixelBlockStates, loadingLevels, loadingLighting, pixelTransparentSizes, loadingFlowers, loadingStainedGlass)) {
                        return fluidState.method_26204();
                    }
                    if (!(state.method_26204() instanceof class_2189) && state.method_26204() == this.fluidToBlock.apply(fluidFluidState).method_26204()) break block14;
                }
                if ((state.method_26204() instanceof class_2189 || this.underair) && this.findBlockHelp(world, (class_2791)bchunk, insideX, i, insideZ, state, null, transparentSkipY, shouldExtendTillTheBottom, loadingCaving, loadingRedstone, mutableBlockPos, mutableBlockPos2, loadingColours, loadingTransparency, pixelBlockLights, pixelBlockStates, loadingLevels, loadingLighting, pixelTransparentSizes, loadingFlowers, loadingStainedGlass)) {
                    return state.method_26204();
                }
            }
            i = shouldExtendTillTheBottom ? transparentSkipY : i - 1;
        }
        return null;
    }

    private boolean findBlockHelp(class_1937 world, class_2791 bchunk, int insideX, int i, int insideZ, class_2680 state, class_3610 fluidFluidState, int transparentSkipY, boolean shouldExtendTillTheBottom, int loadingCaving, boolean loadingRedstone, class_2338.class_2339 mutableBlockPos, class_2338.class_2339 mutableBlockPos2, int loadingColours, boolean loadingTransparency, List<Integer> pixelBlockLights, List<class_2680> pixelBlockStates, int loadingLevels, boolean loadingLighting, List<Integer> pixelTransparentSizes, boolean loadingFlowers, boolean loadingStainedGlass) {
        class_2248 got = state.method_26204();
        if (!(got instanceof class_2189)) {
            boolean isTransparent;
            boolean isFlower;
            boolean isRedstone = false;
            if (!(got instanceof class_2404) && state.method_26217() == class_2464.field_11455) {
                return false;
            }
            if (got == class_2246.field_10336) {
                return false;
            }
            if (got == class_2246.field_10479) {
                return false;
            }
            if (got == class_2246.field_10033 || got == class_2246.field_10285 || !loadingStainedGlass && (got instanceof class_2506 || got instanceof class_2504)) {
                return false;
            }
            boolean bl = isFlower = got instanceof class_2521 || got instanceof class_2356 || got instanceof class_10735 && state.method_26164(class_3481.field_20339);
            if (got instanceof class_2320 && !isFlower) {
                return false;
            }
            if (isFlower && !loadingFlowers) {
                return false;
            }
            boolean bl2 = isRedstone = got == class_2246.field_10523 || got == class_2246.field_10091 || got instanceof class_2462 || got instanceof class_2286;
            if (!loadingRedstone && isRedstone) {
                return false;
            }
            if (this.buggedStates.contains(state)) {
                return false;
            }
            this.blockY = i;
            class_2338.class_2339 globalPos = mutableBlockPos2;
            class_3620 mapColor = null;
            try {
                mapColor = state.method_26205((class_1922)world, (class_2338)globalPos);
            }
            catch (Throwable t) {
                this.buggedStates.add(state);
                MinimapLogs.LOGGER.info("Broken vanilla map color definition found: " + String.valueOf(((class_2378)world.method_30349().method_46759(class_7924.field_41254).get()).method_10221((Object)got)));
            }
            boolean bl3 = loadingTransparency && (state == this.previousTransparentState || this.isTransparent((class_2688<?, ?>)(fluidFluidState == null ? state : fluidFluidState))) ? true : (isTransparent = false);
            if (!((isTransparent || isRedstone) && loadingColours != 1 || mapColor != null && mapColor.field_16011 != 0)) {
                return false;
            }
            if (!this.underair) {
                return !isTransparent;
            }
            class_2338.class_2339 lightPos = mutableBlockPos.method_10103(globalPos.method_10263(), globalPos.method_10264() + 1, globalPos.method_10260());
            if (this.currentComparisonCode == 0L) {
                this.firstBlockY = i;
                if (loadingLighting && loadingCaving != Integer.MAX_VALUE) {
                    this.sun = world.method_8314(class_1944.field_9284, (class_2338)lightPos);
                }
            }
            if (loadingTransparency && isTransparent) {
                if (!shouldExtendTillTheBottom && pixelBlockStates.size() < 5 && state != this.previousTransparentState) {
                    if (pixelBlockStates.isEmpty()) {
                        this.firstTransparentStateY = i;
                    }
                    this.currentComparisonCode += (long)class_2248.method_9507((class_2680)state) & 0xFFFFFFFFL;
                    pixelBlockStates.add(state);
                    pixelTransparentSizes.add(1);
                    pixelBlockLights.add(!loadingLighting ? 0 : world.method_8314(class_1944.field_9282, (class_2338)lightPos));
                    this.previousTransparentState = state;
                } else {
                    int depthToAdd = 1;
                    if (shouldExtendTillTheBottom) {
                        depthToAdd = i - transparentSkipY;
                    }
                    pixelTransparentSizes.set(pixelTransparentSizes.size() - 1, pixelTransparentSizes.get(pixelTransparentSizes.size() - 1) + depthToAdd);
                }
                return false;
            }
            this.currentComparisonCode += (long)class_2248.method_9507((class_2680)state) & 0xFFFFFFFFL;
            this.currentComparisonCode <<= 29;
            this.currentComparisonCode |= ((long)i & 0xFFFL) << 17;
            pixelBlockLights.add(!loadingLighting ? 0 : world.method_8314(class_1944.field_9282, (class_2338)lightPos));
            pixelBlockStates.add(state);
            this.isglowing = this.isGlowing(state, world, (class_2338)globalPos);
            return true;
        }
        if (got instanceof class_2189) {
            this.underair = true;
        }
        return false;
    }

    private void calculateBlockColors(class_1937 world, class_2818 bchunk, int insideX, int insideZ, class_2338.class_2339 mutableBlockPos2, List<Integer> pixelTransparentSizes, List<class_2680> pixelBlockStates, List<Integer> pixelBlockLights, int loadingColours, int loadingLightOverlayColor, int loadingCaving, int loadingLevels, boolean loadingLighting, float loadingSingleLevelBrightness, boolean legibleCaveMaps) {
        int firstBlockY = this.firstBlockY;
        class_2338.class_2339 globalPos = mutableBlockPos2.method_10103(bchunk.method_12004().field_9181 * 16 + insideX, firstBlockY, bchunk.method_12004().field_9180 * 16 + insideZ);
        if (!pixelTransparentSizes.isEmpty()) {
            for (int i = 0; i < pixelTransparentSizes.size(); ++i) {
                class_2680 state = pixelBlockStates.get(i);
                class_2248 b = state.method_26204();
                int size = pixelTransparentSizes.get(i);
                int opacity = state.method_26193();
                this.applyTransparentLayer(world, bchunk, b, state, opacity * size, (class_2338)globalPos, pixelBlockLights.get(i), loadingLighting, loadingSingleLevelBrightness, legibleCaveMaps);
                int nextY = globalPos.method_10264() - size;
                globalPos.method_33098(nextY);
            }
        }
        if (pixelBlockStates.size() > pixelTransparentSizes.size()) {
            int color;
            class_2680 state = pixelBlockStates.get(pixelBlockStates.size() - 1);
            class_2248 b = state.method_26204();
            if (loadingColours == 1) {
                class_3620 minimapColor = state.method_26205((class_1922)world, (class_2338)globalPos);
                color = minimapColor.field_16011;
            } else {
                color = this.loadBlockColourFromTexture(world, state, b, (class_2338)globalPos, true);
            }
            this.blockColor = this.addBlockColorMultipliers(color, state, world, (class_2338)globalPos);
        } else {
            this.blockColor = loadingCaving != Integer.MAX_VALUE ? 0 : -16121833;
        }
    }

    private boolean isTransparent(class_2688<?, ?> state) {
        return this.transparentCache.apply(state);
    }

    private boolean isGlowing(class_2680 state, class_1937 world, class_2338 pos) {
        Boolean cachedValue = this.glowingCache.get(state);
        if (cachedValue != null) {
            return cachedValue;
        }
        boolean isGlowing = false;
        try {
            isGlowing = this.getBlockStateLightEmission(state, world, pos) > 0;
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.glowingCache.put(state, isGlowing);
        return isGlowing;
    }

    private void applyTransparentLayer(class_1937 world, class_2818 bchunk, class_2248 b, class_2680 state, int opacity, class_2338 globalPos, int blockLight, boolean lighting, float loadingSingleLevelBrightness, boolean legibleCaveMaps) {
        int color;
        float vanillaTransparency;
        int red = 0;
        int green = 0;
        int blue = 0;
        float f = b instanceof class_2404 ? 0.75f : (vanillaTransparency = b instanceof class_2386 ? 0.85f : 0.5f);
        if (this.loadingColours == 0) {
            color = this.loadBlockColourFromTexture(world, state, b, globalPos, true);
        } else {
            color = state.method_26205((class_1922)world, (class_2338)globalPos).field_16011;
            color = (int)(255.0f * vanillaTransparency) << 24 | color & 0xFFFFFF;
        }
        color = this.addBlockColorMultipliers(color, state, world, globalPos);
        red = color >> 16 & 0xFF;
        green = color >> 8 & 0xFF;
        blue = color & 0xFF;
        float transparency = (float)(color >> 24 & 0xFF) / 255.0f;
        if (transparency == 0.0f) {
            transparency = vanillaTransparency;
        }
        if (this.isGlowing(state, bchunk.method_12200(), globalPos)) {
            this.helper.getGlowingColour(red, green, blue, this.tempColor);
            red = this.tempColor[0];
            green = this.tempColor[1];
            blue = this.tempColor[2];
        }
        float brightness = legibleCaveMaps ? 1.0f : (lighting ? this.getBlockBrightness(9.0f, this.sun, 0, blockLight) : this.getFixedSkyLightBlockBrightness(9.0f, loadingSingleLevelBrightness, blockLight));
        this.applyTransparentLayer(red, green, blue, transparency * brightness, false);
        this.sun -= opacity;
        if (this.sun < 0) {
            this.sun = 0;
        }
    }

    private void applyTransparentLayer(int red, int green, int blue, float transparency, boolean premultiplied) {
        float overlayIntensity = this.currentTransparencyMultiplier * (premultiplied ? 1.0f : transparency);
        int i = 0;
        while (i < this.loadingLevels) {
            int n = i;
            this.underRed[n] = (int)((float)this.underRed[n] + (float)red * overlayIntensity);
            int n2 = i;
            this.underGreen[n2] = (int)((float)this.underGreen[n2] + (float)green * overlayIntensity);
            int n3 = i++;
            this.underBlue[n3] = (int)((float)this.underBlue[n3] + (float)blue * overlayIntensity);
        }
        this.currentTransparencyMultiplier *= 1.0f - transparency;
    }

    protected abstract List<class_777> getQuads(class_1087 var1, class_1937 var2, class_2338 var3, class_2680 var4, class_2350 var5);

    protected abstract class_1058 getParticleIcon(class_773 var1, class_1087 var2, class_1937 var3, class_2338 var4, class_2680 var5);

    private int loadBlockColourFromTexture(class_1937 world, class_2680 state, class_2248 b, class_2338 pos, boolean convert) {
        if (state == this.lastBlockStateForTextureColor) {
            return this.lastBlockStateForTextureColorResult;
        }
        int stateHash = class_2248.method_9507((class_2680)state);
        Integer c = this.blockColours.get(stateHash);
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
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
                    upQuads = this.getQuads(model, world, pos, state, class_2350.field_11036);
                }
                class_1058 missingTexture = class_310.method_1551().method_1554().method_24153(class_1059.field_5275).method_4608(class_1047.method_4539());
                if (upQuads == null || upQuads.isEmpty() || upQuads.get(0).comp_3724() == missingTexture) {
                    texture = this.getParticleIcon(bms, model, world, pos, state);
                    tintIndex = 0;
                    if (texture == missingTexture) {
                        for (int i = class_2350.values().length - 1; i >= 0; --i) {
                            List<class_777> quads;
                            if (i == 1 || (quads = this.getQuads(model, world, pos, state, class_2350.values()[i])).isEmpty()) continue;
                            texture = quads.get(0).comp_3724();
                            tintIndex = quads.get(0).comp_3722();
                            if (texture == missingTexture) {
                                continue;
                            }
                            break;
                        }
                    }
                } else {
                    texture = upQuads.get(0).comp_3724();
                    tintIndex = upQuads.get(0).comp_3722();
                }
                if (texture == null) {
                    throw new SilentException("No texture for " + String.valueOf(state));
                }
                name = String.valueOf(texture.method_45851().method_45816()) + ".png";
                c = -1;
                String[] args = name.split(":");
                if (args.length < 2) {
                    args = new String[]{"minecraft", args[0]};
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
                    return this.loadBlockColourFromTexture(world, state, b, pos, false);
                }
                MinimapLogs.LOGGER.info("Block file not found: " + String.valueOf(((class_2378)world.method_30349().method_46759(class_7924.field_41254).get()).method_10221((Object)b)));
                c = 0;
                if (state != null && state.method_26205((class_1922)world, pos) != null) {
                    c = state.method_26205((class_1922)world, (class_2338)pos).field_16011;
                }
                if (name != null) {
                    this.textureColours.put(name, c);
                }
            }
            catch (Exception e) {
                MinimapLogs.LOGGER.info("Exception when loading " + String.valueOf(((class_2378)world.method_30349().method_46759(class_7924.field_41254).get()).method_10221((Object)b)) + " texture, using material colour.");
                c = 0;
                if (state.method_26205((class_1922)world, pos) != null) {
                    c = state.method_26205((class_1922)world, (class_2338)pos).field_16011;
                }
                if (name != null) {
                    this.textureColours.put(name, c);
                }
                if (e instanceof SilentException) {
                    MinimapLogs.LOGGER.info(e.getMessage());
                }
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            if (c != null) {
                this.blockColours.put(stateHash, c);
                this.blockTintIndices.put((Object)state, tintIndex);
            }
        }
        this.lastBlockStateForTextureColor = state;
        this.lastBlockStateForTextureColorResult = c;
        return c;
    }

    private int addBlockColorMultipliers(int c, class_2680 state, class_1937 world, class_2338 pos) {
        if (this.modMain.getSettings().getBlockColours() == 1 && !this.loadingBiomesVanillaMode) {
            return c;
        }
        int grassColor = 0xFFFFFF;
        try {
            grassColor = class_310.method_1551().method_1505().method_1697(state, (class_1920)this.biomeBlendCalculator, pos, this.blockTintIndices.getInt((Object)state));
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("suppressed exception", t);
        }
        if (grassColor != -1 && grassColor != 0xFFFFFF) {
            float rMultiplier = (float)(c >> 16 & 0xFF) / 255.0f;
            float gMultiplier = (float)(c >> 8 & 0xFF) / 255.0f;
            float bMultiplier = (float)(c & 0xFF) / 255.0f;
            int red = (int)((float)(grassColor >> 16 & 0xFF) * rMultiplier);
            int green = (int)((float)(grassColor >> 8 & 0xFF) * gMultiplier);
            int blue = (int)((float)(grassColor & 0xFF) * bMultiplier);
            c = c & 0xFF000000 | red << 16 | green << 8 | blue;
        }
        return c;
    }

    private boolean ignoreWorld(class_1937 world) {
        for (int i = 0; i < dimensionsToIgnore.length; ++i) {
            if (!dimensionsToIgnore[i].equals(world.method_27983().method_29177().method_12832())) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private int getCaving(boolean manualCaveMode, double playerX, double playerY, double playerZ, class_1937 world) {
        int defaultResult;
        if (!this.modMain.getSettings().getCaveMaps(manualCaveMode) || Misc.hasEffect((class_1657)this.mc.field_1724, Effects.NO_CAVE_MAPS) || Misc.hasEffect((class_1657)this.mc.field_1724, Effects.NO_CAVE_MAPS_HARMFUL)) {
            return Integer.MAX_VALUE;
        }
        if (this.ignoreWorld(world)) {
            return this.lastCaving;
        }
        if (manualCaveMode) {
            int customCaveStart = this.modMain.getSettings().getManualCaveModeStart();
            if (customCaveStart != Integer.MAX_VALUE) {
                return customCaveStart;
            }
        } else if (this.modMain.getSupportMods().worldmap() && this.modMain.getSupportMods().worldmapSupport.shouldPreventAutoCaveMode(world)) {
            return Integer.MAX_VALUE;
        }
        int worldBottomY = world.method_31607();
        int worldTopY = world.method_31600() - 1;
        int y = (int)playerY + 1;
        int defaultCaveStart = y + 3;
        int n = defaultResult = manualCaveMode ? defaultCaveStart : Integer.MAX_VALUE;
        if (y > worldTopY || y < worldBottomY) {
            return defaultResult;
        }
        int x = OptimizedMath.myFloor(playerX);
        int z = OptimizedMath.myFloor(playerZ);
        int roofRadius = this.modMain.getSettings().caveMaps - 1;
        int roofDiameter = 1 + roofRadius * 2;
        int startX = x - roofRadius;
        int startZ = z - roofRadius;
        boolean ignoringHeightmaps = this.modMain.getSettings().isIgnoreHeightmaps();
        int bottom = y;
        int top = Integer.MAX_VALUE;
        class_2818 prevBChunk = null;
        int potentialResult = defaultCaveStart;
        for (int o = 0; o < roofDiameter; ++o) {
            block1: for (int p = 0; p < roofDiameter; ++p) {
                int currentX = startX + o;
                int currentZ = startZ + p;
                this.mutableBlockPos.method_10103(currentX, y, currentZ);
                class_2818 bchunk = world.method_8497(currentX >> 4, currentZ >> 4);
                if (bchunk == null) {
                    return defaultResult;
                }
                int skyLight = world.method_8314(class_1944.field_9284, (class_2338)this.mutableBlockPos);
                if (!ignoringHeightmaps) {
                    if (skyLight >= 15) return defaultResult;
                    int insideX = currentX & 0xF;
                    int insideZ = currentZ & 0xF;
                    top = bchunk.method_12005(class_2902.class_2903.field_13202, insideX, insideZ);
                } else if (bchunk != prevBChunk) {
                    class_2826[] sections = bchunk.method_12006();
                    if (sections.length == 0) {
                        return defaultResult;
                    }
                    int playerSection = y - worldBottomY >> 4;
                    boolean foundSomething = false;
                    for (int i = playerSection; i < sections.length; ++i) {
                        class_2826 searchedSection = sections[i];
                        if (searchedSection.method_38292()) continue;
                        if (!foundSomething) {
                            bottom = Math.max(bottom, worldBottomY + (i << 4));
                            foundSomething = true;
                        }
                        top = worldBottomY + (i << 4) + 15;
                    }
                    if (!foundSomething) {
                        return defaultResult;
                    }
                    prevBChunk = bchunk;
                }
                if (top < worldBottomY) {
                    return defaultResult;
                }
                if (top > worldTopY) {
                    top = worldTopY;
                }
                for (int i = bottom; i <= top; ++i) {
                    this.mutableBlockPos.method_33098(i);
                    class_2680 state = world.method_8320((class_2338)this.mutableBlockPos);
                    if (state.method_26215() || state.method_26223() == class_3619.field_15971 || state.method_26204() instanceof class_2404 || state.method_26164(class_3481.field_15503) || this.isTransparent((class_2688<?, ?>)state) || state.method_26204() == class_2246.field_10499) continue;
                    if (o != p || o != roofRadius) continue block1;
                    potentialResult = Math.min(i, defaultCaveStart);
                    continue block1;
                }
                return defaultResult;
            }
        }
        this.lastCaving = potentialResult;
        return this.lastCaving;
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

    public int getLoadSide() {
        return 9;
    }

    public int getUpdateRadiusInChunks() {
        return (int)Math.ceil((double)this.loadingSideInChunks / 2.0 / this.minimapSession.getProcessor().getMinimapZoom());
    }

    public int getMapCoord(int side, int coord) {
        return (coord >> 6) - side / 2;
    }

    public int getLoadedCaving() {
        return this.loadedCaving;
    }

    private boolean notEmptyColor(int[] red, int[] green, int[] blue) {
        return red[0] != 0 || green[0] != 0 || blue[0] != 0;
    }

    public float getFixedSkyLightBlockBrightness(float min, float fixedSun, int blockLight) {
        return (min + Math.max(fixedSun * 15.0f, (float)blockLight)) / (15.0f + min);
    }

    public float getBlockBrightness(float min, int sun, int lightLevel, int blockLight) {
        return (min + Math.max((lightLevel == -1 || lightLevel == 0 ? 1.0f : ((float)this.loadingLevels - 1.0f - (float)lightLevel) / ((float)this.loadingLevels - 1.0f)) * (float)sun, (float)blockLight)) / (15.0f + min);
    }

    public int getLoadingMapChunkX() {
        return this.loadingMapChunkX;
    }

    public int getLoadingMapChunkZ() {
        return this.loadingMapChunkZ;
    }

    public int getLoadingSideInChunks() {
        return this.loadingSideInChunks;
    }

    public MinimapChunk[][] getLoadedBlocks() {
        return this.loadedBlocks;
    }

    public int getLoadedMapChunkZ() {
        return this.loadedMapChunkZ;
    }

    public int getLoadedMapChunkX() {
        return this.loadedMapChunkX;
    }

    public int getLoadedLevels() {
        return this.loadedLevels;
    }

    public void setClearBlockColours(boolean clearBlockColours) {
        this.clearBlockColours = clearBlockColours;
    }

    public void cleanup() {
        if (this.loadedBlocks != null) {
            for (int i = 0; i < this.loadedBlocks.length; ++i) {
                for (int j = 0; j < this.loadedBlocks.length; ++j) {
                    MinimapChunk m = this.loadedBlocks[i][j];
                    if (m == null) continue;
                    m.cleanup(this.minimap);
                }
            }
        }
    }

    public void resetShortBlocks() {
        this.blockStateShortShapeCache.reset();
    }

    public DimensionHighlighterHandler getDimensionHighlightHandler() {
        return this.dimensionHighlightHandler;
    }

    public int getLoadedSideInChunks() {
        return this.loadedSideInChunks;
    }

    public boolean isLoadedNonWorldMap() {
        return this.loadedNonWorldMap;
    }
}

