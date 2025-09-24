/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.class_1074
 *  net.minecraft.class_11278
 *  net.minecraft.class_1297
 *  net.minecraft.class_151
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2378
 *  net.minecraft.class_2874
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3611
 *  net.minecraft.class_5218
 *  net.minecraft.class_5321
 *  net.minecraft.class_634
 *  net.minecraft.class_638
 *  net.minecraft.class_642
 *  net.minecraft.class_7134
 *  net.minecraft.class_7225
 *  net.minecraft.class_7924
 */
package xaero.map;

import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import net.minecraft.class_1074;
import net.minecraft.class_11278;
import net.minecraft.class_1297;
import net.minecraft.class_151;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2874;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3611;
import net.minecraft.class_5218;
import net.minecraft.class_5321;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_642;
import net.minecraft.class_7134;
import net.minecraft.class_7225;
import net.minecraft.class_7924;
import xaero.map.MapLimiter;
import xaero.map.MapRunner;
import xaero.map.MapWriter;
import xaero.map.WorldMap;
import xaero.map.biome.BiomeColorCalculator;
import xaero.map.biome.BiomeGetter;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.cache.BrokenBlockTintCache;
import xaero.map.controls.ControlsRegister;
import xaero.map.deallocator.ByteBufferDeallocator;
import xaero.map.exception.OpenGLException;
import xaero.map.file.MapSaveLoad;
import xaero.map.file.RegionDetection;
import xaero.map.file.worldsave.WorldDataHandler;
import xaero.map.graphics.CustomVertexConsumers;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.OpenGlHelper;
import xaero.map.graphics.TextureUploader;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.gui.GuiMap;
import xaero.map.gui.message.MessageBox;
import xaero.map.gui.message.render.MessageBoxRenderer;
import xaero.map.highlight.DimensionHighlighterHandler;
import xaero.map.highlight.HighlighterRegistry;
import xaero.map.highlight.MapRegionHighlightsPreparer;
import xaero.map.mcworld.WorldMapClientWorldData;
import xaero.map.mcworld.WorldMapClientWorldDataHelper;
import xaero.map.minimap.MinimapRenderListener;
import xaero.map.misc.CaveStartCalculator;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.pool.MapTilePool;
import xaero.map.radar.tracker.synced.ClientSyncedTrackedPlayerManager;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapBlock;
import xaero.map.region.MapLayer;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.OverlayManager;
import xaero.map.region.texture.BranchTextureRenderer;
import xaero.map.region.texture.RegionTexture;
import xaero.map.render.util.ImmediateRenderUtil;
import xaero.map.task.MapRunnerTask;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class MapProcessor {
    public static final int ROOT_FOLDER_FORMAT = 4;
    public static final int DEFAULT_LIGHT_LEVELS = 4;
    private MapSaveLoad mapSaveLoad;
    private MapWriter mapWriter;
    private MapLimiter mapLimiter;
    private WorldDataHandler worldDataHandler;
    private ByteBufferDeallocator bufferDeallocator;
    private TextureUploader textureUploader;
    private BranchTextureRenderer branchTextureRenderer;
    private BiomeColorCalculator biomeColorCalculator;
    private final BlockStateShortShapeCache blockStateShortShapeCache;
    private final BiomeGetter biomeGetter;
    private final BrokenBlockTintCache brokenBlockTintCache;
    private final MapRegionHighlightsPreparer mapRegionHighlightsPreparer;
    private final CaveStartCalculator caveStartCalculator;
    private final ClientSyncedTrackedPlayerManager clientSyncedTrackedPlayerManager;
    private class_638 world;
    private class_7225<class_2248> worldBlockLookup;
    private class_2378<class_2248> worldBlockRegistry;
    private class_2378<class_3611> worldFluidRegistry;
    public class_2378<class_1959> worldBiomeRegistry;
    private class_2378<class_2874> worldDimensionTypeRegistry;
    private BlockTintProvider worldBlockTintProvider;
    private class_638 newWorld;
    private class_7225<class_2248> newWorldBlockLookup;
    public class_2378<class_2248> newWorldBlockRegistry;
    private class_2378<class_3611> newWorldFluidRegistry;
    public class_2378<class_1959> newWorldBiomeRegistry;
    public class_2378<class_2874> newWorldDimensionTypeRegistry;
    public final Object mainStuffSync;
    public class_638 mainWorld;
    private class_7225<class_2248> mainWorldBlockLookup;
    public class_2378<class_2248> mainWorldBlockRegistry;
    private class_2378<class_3611> mainWorldFluidRegistry;
    public class_2378<class_1959> mainWorldBiomeRegistry;
    public class_2378<class_2874> mainWorldDimensionTypeRegistry;
    public double mainPlayerX;
    public double mainPlayerY;
    public double mainPlayerZ;
    private boolean mainWorldUnloaded;
    private ArrayList<Double[]> footprints = new ArrayList();
    private int footprintsTimer;
    private boolean mapWorldUsable;
    private MapWorld mapWorld;
    private String currentWorldId;
    private String currentDimId;
    private String currentMWId;
    private FileLock mapLockToRelease;
    private FileChannel mapLockChannelToClose;
    private FileChannel currentMapLockChannel;
    private FileLock currentMapLock;
    private boolean mapWorldUsableRequest;
    public final Object renderThreadPauseSync = new Object();
    private int pauseUploading;
    private int pauseRendering;
    private int pauseWriting;
    public final Object processorThreadPauseSync = new Object();
    private int pauseProcessing;
    private final Object loadingSync = new Object();
    private boolean isLoading;
    public final Object uiSync = new Object();
    private boolean waitingForWorldUpdate;
    public final Object uiPauseSync = new Object();
    private boolean isUIPaused;
    private ArrayList<LeveledRegion<?>>[] toProcessLevels;
    private ArrayList<MapRegion> toRefresh = new ArrayList();
    private static final int SPAWNPOINT_TIMEOUT = 3000;
    private class_2338 spawnToRestore;
    private long mainWorldChangedTime = -1L;
    private MapTilePool tilePool;
    private int firstBranchLevel;
    private long lastRenderProcessTime = -1L;
    private int workingFramesCount;
    public long freeFramePeriod = -1L;
    private int testingFreeFrame = 1;
    private MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers;
    private CustomVertexConsumers cvc;
    private class_11278 mapProjectionCache;
    private final MessageBox messageBox;
    private final MessageBoxRenderer messageBoxRenderer;
    private MinimapRenderListener minimapRenderListener;
    private boolean currentMapNeedsDeletion;
    private OverlayManager overlayManager;
    private long renderStartTime;
    private Field scheduledTasksField;
    private Runnable renderStartTimeUpdaterRunnable;
    private boolean finalizing;
    private int state;
    private HashSet<class_2960> hardcodedNetherlike;
    private final HighlighterRegistry highlighterRegistry;
    private int currentCaveLayer = Integer.MAX_VALUE;
    private long lastLocalCaveModeToggle;
    private int nextLocalCaveMode = Integer.MAX_VALUE;
    private int localCaveMode = Integer.MAX_VALUE;
    private boolean consideringNetherFairPlayMessage;
    private String[] dimensionsToIgnore = new String[]{"FZHammer"};
    public Field selectedField = null;

    public MapProcessor(MapSaveLoad mapSaveLoad, MapWriter mapWriter, MapLimiter mapLimiter, ByteBufferDeallocator bufferDeallocator, MapTilePool tilePool, OverlayManager overlayManager, TextureUploader textureUploader, WorldDataHandler worldDataHandler, BranchTextureRenderer branchTextureRenderer, MultiTextureRenderTypeRendererProvider mtrtrs, CustomVertexConsumers cvc, BiomeColorCalculator biomeColorCalculator, BlockStateShortShapeCache blockStateShortShapeCache, BiomeGetter biomeGetter, BrokenBlockTintCache brokenBlockTintCache, HighlighterRegistry highlighterRegistry, MapRegionHighlightsPreparer mapRegionHighlightsPreparer, MessageBox messageBox, MessageBoxRenderer messageBoxRenderer, CaveStartCalculator caveStartCalculator, ClientSyncedTrackedPlayerManager clientSyncedTrackedPlayerManager) throws NoSuchFieldException {
        this.branchTextureRenderer = branchTextureRenderer;
        this.mapSaveLoad = mapSaveLoad;
        this.mapWriter = mapWriter;
        this.mapLimiter = mapLimiter;
        this.bufferDeallocator = bufferDeallocator;
        this.tilePool = tilePool;
        this.overlayManager = overlayManager;
        this.textureUploader = textureUploader;
        this.worldDataHandler = worldDataHandler;
        this.scheduledTasksField = Misc.getFieldReflection(class_310.class, "progressTasks", "field_17404", "Ljava/util/Queue;", "f_91023_");
        this.renderStartTimeUpdaterRunnable = new Runnable(){

            @Override
            public void run() {
                MapProcessor.this.updateRenderStartTime();
            }
        };
        this.mainStuffSync = new Object();
        this.toProcessLevels = new ArrayList[4];
        for (int i = 0; i < this.toProcessLevels.length; ++i) {
            this.toProcessLevels[i] = new ArrayList();
        }
        this.multiTextureRenderTypeRenderers = mtrtrs;
        this.cvc = cvc;
        this.biomeColorCalculator = biomeColorCalculator;
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.hardcodedNetherlike = Sets.newHashSet((Object[])new class_2960[]{class_7134.field_37671, class_2960.method_60655((String)"undergarden", (String)"undergarden")});
        this.biomeGetter = biomeGetter;
        this.brokenBlockTintCache = brokenBlockTintCache;
        this.highlighterRegistry = highlighterRegistry;
        this.mapRegionHighlightsPreparer = mapRegionHighlightsPreparer;
        this.messageBox = messageBox;
        this.messageBoxRenderer = messageBoxRenderer;
        this.caveStartCalculator = caveStartCalculator;
        this.clientSyncedTrackedPlayerManager = clientSyncedTrackedPlayerManager;
        this.minimapRenderListener = new MinimapRenderListener();
    }

    public void onInit(class_634 connection) {
        String mainId = this.getMainId(4, connection);
        this.fixRootFolder(mainId, connection);
        this.mapWorld = new MapWorld(mainId, this.getMainId(0, connection), this);
        this.mapWorld.load();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run(MapRunner runner) {
        if (this.state < 2) {
            try {
                while (this.state < 2 && WorldMap.crashHandler.getCrashedBy() == null) {
                    Object object = this.processorThreadPauseSync;
                    synchronized (object) {
                        if (!this.isProcessingPaused()) {
                            this.updateWorld();
                            if (this.world != null) {
                                this.updateFootprints(class_310.method_1551().field_1755 instanceof GuiMap ? 1 : 10);
                            }
                            if (this.mapWorldUsable) {
                                this.mapLimiter.applyLimit(this.mapWorld, this);
                                long currentTime = System.currentTimeMillis();
                                block11: for (int l = 0; l < this.toProcessLevels.length; ++l) {
                                    ArrayList<LeveledRegion<?>> regionsToProcess = this.toProcessLevels[l];
                                    for (int i = 0; i < regionsToProcess.size(); ++i) {
                                        LeveledRegion<?> leveledRegion;
                                        ArrayList<LeveledRegion<?>> arrayList = regionsToProcess;
                                        synchronized (arrayList) {
                                            if (i >= regionsToProcess.size()) {
                                                continue block11;
                                            }
                                            leveledRegion = regionsToProcess.get(i);
                                        }
                                        this.mapSaveLoad.updateSave(leveledRegion, currentTime, this.currentCaveLayer);
                                    }
                                }
                            }
                            this.mapSaveLoad.run(this.worldBlockLookup, this.worldBlockRegistry, this.worldFluidRegistry, this.biomeGetter, this.worldBiomeRegistry);
                            this.handleRefresh();
                            runner.doTasks(this);
                            this.releaseLocksIfNeeded();
                        }
                    }
                    try {
                        Thread.sleep(this.world == null || Misc.screenShouldSkipWorldRender(class_310.method_1551().field_1755, true) || this.state > 0 ? 40L : 100L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
            catch (Throwable e) {
                WorldMap.crashHandler.setCrashedBy(e);
            }
            if (this.state < 2) {
                this.forceClean();
            }
        }
        if (this.state == 2) {
            this.state = 3;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onRenderProcess(class_310 mc) throws RuntimeException {
        try {
            this.mapWriter.onRender(this.biomeColorCalculator, this.overlayManager);
            long renderProcessTime = System.nanoTime();
            if (this.testingFreeFrame == 1) {
                this.testingFreeFrame = 2;
            } else {
                Object object = this.renderThreadPauseSync;
                synchronized (object) {
                    if (this.lastRenderProcessTime == -1L) {
                        this.lastRenderProcessTime = renderProcessTime;
                    }
                    long sinceLastProcessTime = renderProcessTime - this.lastRenderProcessTime;
                    if (this.testingFreeFrame == 2) {
                        this.freeFramePeriod = sinceLastProcessTime;
                        this.testingFreeFrame = 0;
                    }
                    if (this.pauseUploading == 0 && this.mapWorldUsable && this.currentWorldId != null) {
                        boolean branchesCatchup;
                        mc.method_22940().method_23000().method_22993();
                        OpenGlHelper.clearErrors(false, "onRenderProcess");
                        OpenGlHelper.resetPixelStore();
                        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        OpenGLException.checkGLError();
                        this.updateCaveStart();
                        MapDimension currentDim = this.mapWorld.getCurrentDimension();
                        if (currentDim.getFullReloader() != null) {
                            currentDim.getFullReloader().onRenderProcess();
                        }
                        DimensionHighlighterHandler highlighterHandler = currentDim.getHighlightHandler();
                        int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                        boolean detailedDebug = WorldMap.settings.detailed_debug;
                        long uploadStart = System.nanoTime();
                        long totalTime = Math.min(sinceLastProcessTime, this.freeFramePeriod);
                        long passed = uploadStart - this.renderStartTime;
                        long timeAvailable = Math.max(3000000L, totalTime - passed);
                        long uploadUntil = uploadStart + timeAvailable / 4L;
                        long gpuLimit = Math.max(1000000L, class_310.method_1551().field_1755 instanceof GuiMap ? totalTime * 5L / 12L : Math.min(totalTime / 5L, timeAvailable));
                        boolean noLimits = false;
                        if (class_310.method_1551().field_1755 instanceof GuiMap) {
                            GuiMap guiMap = (GuiMap)class_310.method_1551().field_1755;
                            noLimits = guiMap.noUploadingLimits;
                            guiMap.noUploadingLimits = false;
                        }
                        int firstLevel = 0;
                        boolean bl = branchesCatchup = (int)(Math.random() * 5.0) == 0;
                        if (branchesCatchup) {
                            firstLevel = 1 + this.firstBranchLevel;
                        }
                        this.firstBranchLevel = (this.firstBranchLevel + 1) % (this.toProcessLevels.length - 1);
                        block14: for (int j = 0; j < this.toProcessLevels.length; ++j) {
                            int level = (firstLevel + j) % this.toProcessLevels.length;
                            ArrayList<LeveledRegion<?>> toProcess = this.toProcessLevels[level];
                            for (int i = 0; i < toProcess.size(); ++i) {
                                LeveledRegion<?> region;
                                Object object2 = toProcess;
                                synchronized (object2) {
                                    if (i >= toProcess.size()) {
                                        continue block14;
                                    }
                                    region = toProcess.get(i);
                                }
                                if (region == null) continue;
                                object2 = region;
                                synchronized (object2) {
                                    if (region.shouldBeProcessed()) {
                                        boolean cleanAndCacheRequestsBlocked = region.cleanAndCacheRequestsBlocked();
                                        boolean allCleaned = true;
                                        boolean allCached = true;
                                        boolean allUploaded = true;
                                        boolean hasLoadedTextures = false;
                                        for (int x = 0; x < 8; ++x) {
                                            for (int z = 0; z < 8; ++z) {
                                                Object texture = region.getTexture(x, z);
                                                if (texture == null) continue;
                                                if (((RegionTexture)texture).canUpload()) {
                                                    hasLoadedTextures = true;
                                                    if (noLimits || gpuLimit > 0L && System.nanoTime() < uploadUntil) {
                                                        ((RegionTexture)texture).preUpload(this, this.worldBlockTintProvider, this.overlayManager, region, detailedDebug, this.blockStateShortShapeCache);
                                                        if (((RegionTexture)texture).shouldUpload()) {
                                                            if (((RegionTexture)texture).getTimer() == 0) {
                                                                gpuLimit -= ((RegionTexture)texture).uploadBuffer(highlighterHandler, this.textureUploader, region, this.branchTextureRenderer, x, z);
                                                            } else {
                                                                ((RegionTexture)texture).decTimer();
                                                            }
                                                        }
                                                    }
                                                    ((RegionTexture)texture).postUpload(this, region, cleanAndCacheRequestsBlocked);
                                                }
                                                if (((RegionTexture)texture).hasSourceData()) {
                                                    allCleaned = false;
                                                }
                                                if (((RegionTexture)texture).shouldIncludeInCache() && !((RegionTexture)texture).isCachePrepared()) {
                                                    allCached = false;
                                                }
                                                if (((RegionTexture)texture).isUploaded()) continue;
                                                allUploaded = false;
                                            }
                                        }
                                        if (hasLoadedTextures) {
                                            region.processWhenLoadedChunksExist(globalRegionCacheHashCode);
                                        }
                                        allUploaded = allUploaded && region.isLoaded() && !cleanAndCacheRequestsBlocked;
                                        boolean bl2 = allCached = allCached && allUploaded;
                                        if ((!region.shouldCache() || !region.recacheHasBeenRequested()) && region.shouldEndProcessingAfterUpload() && allCleaned && allUploaded) {
                                            region.onProcessingEnd();
                                            region.deleteGLBuffers();
                                            ArrayList<LeveledRegion<?>> arrayList = toProcess;
                                            synchronized (arrayList) {
                                                if (i < toProcess.size()) {
                                                    toProcess.remove(i);
                                                    --i;
                                                }
                                            }
                                            if (WorldMap.settings.debug) {
                                                WorldMap.LOGGER.info("Region freed: " + String.valueOf(region) + " " + this.mapWriter.getUpdateCounter() + " " + this.currentWorldId + " " + this.currentDimId);
                                            }
                                        }
                                        if (allCached && !region.isAllCachePrepared()) {
                                            region.setAllCachePrepared(true);
                                        }
                                        if (region.shouldCache() && region.recacheHasBeenRequested() && region.isAllCachePrepared() && !cleanAndCacheRequestsBlocked) {
                                            this.getMapSaveLoad().requestCache(region);
                                        }
                                    }
                                    continue;
                                }
                            }
                        }
                        ++this.workingFramesCount;
                        if (this.workingFramesCount >= 30) {
                            this.testingFreeFrame = 1;
                            this.workingFramesCount = 0;
                        }
                        this.textureUploader.uploadTextures();
                    }
                }
            }
            this.mapLimiter.updateAvailableVRAM();
            this.lastRenderProcessTime = renderProcessTime;
        }
        catch (Throwable e) {
            WorldMap.crashHandler.setCrashedBy(e);
        }
        WorldMap.crashHandler.checkForCrashes();
        MapRenderHelper.restoreDefaultShaderBlendState();
    }

    public void updateCaveStart() {
        int newCaveStart;
        class_310 mc = class_310.method_1551();
        MapDimension dimension = this.mapWorld.getCurrentDimension();
        if (!WorldMap.settings.isCaveMapsAllowed() || dimension.getCaveModeType() == 0) {
            newCaveStart = Integer.MAX_VALUE;
        } else {
            boolean isMapScreen;
            newCaveStart = WorldMap.settings.caveModeStart == Integer.MAX_VALUE ? Integer.MIN_VALUE : WorldMap.settings.caveModeStart;
            boolean customDim = dimension.getDimId() != mc.field_1687.method_27983();
            boolean bl = isMapScreen = mc.field_1755 instanceof GuiMap || Misc.screenShouldSkipWorldRender(mc.field_1755, true);
            if (SupportMods.minimap() && (!customDim && WorldMap.settings.autoCaveMode < 0 && newCaveStart == Integer.MIN_VALUE || !isMapScreen)) {
                newCaveStart = SupportMods.xaeroMinimap.getCaveStart(newCaveStart, isMapScreen);
            }
            if (newCaveStart == Integer.MIN_VALUE) {
                boolean toggling;
                long currentTime = System.currentTimeMillis();
                int nextLocalCaveMode = customDim ? Integer.MAX_VALUE : this.caveStartCalculator.getCaving(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), (class_1937)mc.field_1687);
                boolean bl2 = toggling = this.localCaveMode == Integer.MAX_VALUE != (nextLocalCaveMode == Integer.MAX_VALUE);
                if (!toggling || currentTime - this.lastLocalCaveModeToggle > (long)WorldMap.settings.caveModeToggleTimer) {
                    if (toggling) {
                        this.lastLocalCaveModeToggle = currentTime;
                    }
                    this.localCaveMode = nextLocalCaveMode;
                }
                newCaveStart = this.localCaveMode;
            }
            if (newCaveStart != Integer.MAX_VALUE) {
                newCaveStart = dimension.getCaveModeType() == 2 ? Integer.MIN_VALUE : class_3532.method_15340((int)newCaveStart, (int)this.world.method_31607(), (int)this.world.method_31600());
            }
        }
        int newCaveLayer = this.getCaveLayer(newCaveStart);
        dimension.getLayeredMapRegions().getLayer(newCaveLayer).setCaveStart(newCaveStart);
        this.currentCaveLayer = newCaveLayer;
    }

    public boolean ignoreWorld(class_1937 world) {
        for (int i = 0; i < this.dimensionsToIgnore.length; ++i) {
            if (!this.dimensionsToIgnore[i].equals(world.method_27983().method_29177().method_12832())) continue;
            return true;
        }
        return false;
    }

    public String getDimensionName(class_5321<class_1937> id) {
        if (id == class_1937.field_25179) {
            return "null";
        }
        if (id == class_1937.field_25180) {
            return "DIM-1";
        }
        if (id == class_1937.field_25181) {
            return "DIM1";
        }
        class_2960 identifier = id.method_29177();
        return identifier.method_12836() + "$" + identifier.method_12832().replace('/', '%');
    }

    public class_5321<class_1937> getDimensionIdForFolder(String folderName) {
        if (folderName.equals("null")) {
            return class_1937.field_25179;
        }
        if (folderName.equals("DIM-1")) {
            return class_1937.field_25180;
        }
        if (folderName.equals("DIM1")) {
            return class_1937.field_25181;
        }
        int separatorIndex = folderName.indexOf(36);
        if (separatorIndex == -1) {
            return null;
        }
        String namespace = folderName.substring(0, separatorIndex);
        String path = folderName.substring(separatorIndex + 1).replace('%', '/');
        try {
            class_2960 dimensionId = class_2960.method_60655((String)namespace, (String)path);
            return class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)dimensionId);
        }
        catch (class_151 rse) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void waitForLoadingToFinish(Runnable onFinish) {
        while (true) {
            Object object = this.loadingSync;
            synchronized (object) {
                if (!this.isLoading) {
                    onFinish.run();
                    break;
                }
                this.blockStateShortShapeCache.supplyForIOThread();
                this.worldDataHandler.handleRenderExecutor();
            }
        }
    }

    public synchronized void changeWorld(class_638 world, class_7225<class_2248> blockLookup, class_2378<class_2248> blockRegistry, class_2378<class_3611> fluidRegistry, class_2378<class_1959> biomeRegistry, class_2378<class_2874> dimensionTypeRegistry) {
        this.pushWriterPause();
        if (world != this.newWorld) {
            this.waitForLoadingToFinish(() -> {
                this.waitingForWorldUpdate = true;
            });
        }
        this.newWorld = world;
        this.newWorldBlockLookup = blockLookup;
        this.newWorldBlockRegistry = blockRegistry;
        this.newWorldFluidRegistry = fluidRegistry;
        this.newWorldBiomeRegistry = biomeRegistry;
        this.newWorldDimensionTypeRegistry = dimensionTypeRegistry;
        if (world == null) {
            this.mapWorldUsableRequest = false;
        } else {
            this.mapWorldUsableRequest = true;
            class_5321<class_1937> dimId = this.mapWorld.getPotentialDimId();
            this.mapWorld.setFutureDimensionId(dimId);
            this.updateDimension(world, dimId);
            this.mapWorld.getFutureDimension().resetCustomMultiworldUnsynced();
        }
        this.popWriterPause();
    }

    public void updateVisitedDimension(class_638 world) {
        this.updateDimension(world, (class_5321<class_1937>)world.method_27983());
    }

    public synchronized void updateDimension(class_638 world, class_5321<class_1937> dimId) {
        if (world == null) {
            return;
        }
        Object autoIdBase = this.getAutoIdBase(world);
        MapDimension mapDimension = this.mapWorld.getDimension(dimId);
        if (mapDimension == null) {
            mapDimension = this.mapWorld.createDimensionUnsynced(dimId);
        }
        mapDimension.updateFutureAutomaticUnsynced(class_310.method_1551(), autoIdBase);
    }

    @Deprecated
    private String getMainId(boolean rootFolderFormat, boolean preIP6Fix, class_634 connection) {
        if (!rootFolderFormat) {
            return this.getMainId(0, connection);
        }
        return this.getMainId(preIP6Fix ? 1 : 2, connection);
    }

    private String getMainId(int version, class_634 connection) {
        class_310 mc = class_310.method_1551();
        Object result = null;
        if (mc.method_1576() != null) {
            result = MapWorld.convertWorldFolderToRootId(version, mc.method_1576().method_27050(class_5218.field_24188).getParent().getFileName().toString());
        } else {
            class_642 serverData = connection.method_45734();
            if (serverData != null && serverData.method_52811() && WorldMap.events.getLatestRealm() != null) {
                result = "Realms_" + String.valueOf(WorldMap.events.getLatestRealm().field_22605) + "." + WorldMap.events.getLatestRealm().field_22599;
            } else if (serverData != null) {
                String serverIP = WorldMap.settings.differentiateByServerAddress ? serverData.field_3761 : "Any Address";
                int portDivider = version >= 2 && serverIP.indexOf(":") != serverIP.lastIndexOf(":") ? serverIP.lastIndexOf("]:") + 1 : serverIP.indexOf(":");
                if (portDivider > 0) {
                    serverIP = serverIP.substring(0, portDivider);
                }
                while (version >= 1 && serverIP.endsWith(".")) {
                    serverIP = serverIP.substring(0, serverIP.length() - 1);
                }
                if (version >= 3) {
                    serverIP = serverIP.replace("[", "").replace("]", "");
                }
                result = "Multiplayer_" + serverIP.replaceAll(":", version < 4 ? "\u00a7" : ".");
            } else {
                result = "Multiplayer_Unknown";
            }
        }
        return result;
    }

    public synchronized void toggleMultiworldType(MapDimension dim) {
        if (this.mapWorldUsable && !this.waitingForWorldUpdate && this.mapWorld.isMultiplayer() && this.mapWorld.getCurrentDimension() == dim) {
            this.mapWorld.toggleMultiworldTypeUnsynced();
        }
    }

    public synchronized void quickConfirmMultiworld() {
        if (this.canQuickConfirmUnsynced() && this.mapWorld.getCurrentDimension().hasConfirmedMultiworld()) {
            this.confirmMultiworld(this.mapWorld.getCurrentDimension());
        }
    }

    public synchronized boolean confirmMultiworld(MapDimension dim) {
        if (this.mapWorldUsable && this.mainWorld != null && this.mapWorld.getPotentialDimId() == this.mapWorld.getCurrentDimensionId() && this.mapWorld.getCurrentDimension() == dim) {
            this.mapWorld.confirmMultiworldTypeUnsynced();
            this.mapWorld.getCurrentDimension().confirmMultiworldUnsynced();
            return true;
        }
        return false;
    }

    public synchronized void setMultiworld(MapDimension dimToCompare, String customMW) {
        if (this.mapWorldUsable && dimToCompare.getMapWorld() == this.mapWorld) {
            dimToCompare.setMultiworldUnsynced(customMW);
        }
    }

    public boolean canQuickConfirmUnsynced() {
        return this.mapWorldUsable && !this.mapWorld.getCurrentDimension().futureMultiworldWritable && this.mapWorld.getPotentialDimId() == this.mapWorld.getCurrentDimensionId();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getCrosshairMessage() {
        Object object = this.uiPauseSync;
        synchronized (object) {
            if (this.isUIPaused) {
                return null;
            }
            if (this.canQuickConfirmUnsynced()) {
                String selectedMWName = this.mapWorld.getCurrentDimension().getMultiworldName(this.mapWorld.getCurrentDimension().getFutureMultiworldUnsynced());
                String message = "\u00a72(" + ControlsRegister.keyOpenMap.method_16007().getString().toUpperCase() + ")\u00a7r " + class_1074.method_4662((String)"gui.xaero_map_unconfirmed", (Object[])new Object[0]);
                if (this.mapWorld.getCurrentDimension().hasConfirmedMultiworld()) {
                    message = message + " \u00a72" + ControlsRegister.keyQuickConfirm.method_16007().getString().toUpperCase() + "\u00a7r for map \"" + class_1074.method_4662((String)selectedMWName, (Object[])new Object[0]) + "\"";
                }
                return message;
            }
        }
        return null;
    }

    public synchronized void checkForWorldUpdate() {
        Object autoIdBase;
        if (this.mainWorld != null && (autoIdBase = this.getAutoIdBase(this.mainWorld)) != null) {
            Object updatedAutoIdBase;
            MapDimension mapDimension;
            boolean baseChanged = !autoIdBase.equals(this.getUsedAutoIdBase(this.mainWorld));
            class_5321<class_1937> potentialDimId = this.mapWorld.getPotentialDimId();
            if (baseChanged && this.mapWorldUsableRequest && (mapDimension = this.mapWorld.getDimension(potentialDimId)) != null) {
                boolean serverBasedBefore = mapDimension.isFutureMultiworldServerBased();
                mapDimension.updateFutureAutomaticUnsynced(class_310.method_1551(), autoIdBase);
                if (serverBasedBefore != mapDimension.isFutureMultiworldServerBased()) {
                    mapDimension.resetCustomMultiworldUnsynced();
                }
            }
            if (this.mainWorld != this.world || potentialDimId != this.mapWorld.getFutureDimensionId()) {
                this.changeWorld(this.mainWorld, this.mainWorldBlockLookup, this.mainWorldBlockRegistry, this.mainWorldFluidRegistry, this.mainWorldBiomeRegistry, this.mainWorldDimensionTypeRegistry);
            }
            if ((updatedAutoIdBase = this.getAutoIdBase(this.mainWorld)) != null) {
                this.setUsedAutoIdBase(this.mainWorld, updatedAutoIdBase);
            } else {
                this.removeUsedAutoIdBase(this.mainWorld);
            }
            if (potentialDimId != this.mainWorld.method_27983()) {
                this.updateVisitedDimension(this.mainWorld);
            }
        }
    }

    private void updateWorld() throws IOException, CommandSyntaxException {
        this.pushUIPause();
        this.updateWorldSynced();
        this.popUIPause();
        if (this.mapWorldUsable && !this.mapSaveLoad.isRegionDetectionComplete()) {
            this.mapSaveLoad.detectRegions(10);
            this.mapSaveLoad.setRegionDetectionComplete(true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void updateWorldSynced() throws IOException, CommandSyntaxException {
        Object object = this.uiSync;
        synchronized (object) {
            boolean changedDimension;
            boolean bl = changedDimension = this.mapWorldUsable != this.mapWorldUsableRequest || !this.mapWorldUsableRequest || this.mapWorld.getFutureDimension() != this.mapWorld.getCurrentDimension();
            if (this.mapWorldUsable != this.mapWorldUsableRequest || this.mapWorldUsableRequest && (changedDimension || !this.mapWorld.getFutureDimension().getFutureMultiworldUnsynced().equals(this.mapWorld.getFutureDimension().getCurrentMultiworld()))) {
                String newMWId = !this.mapWorldUsableRequest ? null : this.mapWorld.getFutureMultiworldUnsynced();
                this.pushRenderPause(true, true);
                this.pushWriterPause();
                String newWorldId = !this.mapWorldUsableRequest ? null : this.mapWorld.getMainId();
                boolean shouldClearAllDimensions = this.state == 1;
                boolean shouldClearNewDimension = this.mapWorldUsableRequest && !this.mapWorld.getFutureMultiworldUnsynced().equals(this.mapWorld.getFutureDimension().getCurrentMultiworld());
                this.mapSaveLoad.getToSave().clear();
                if (this.currentMapLock != null) {
                    this.mapLockToRelease = this.currentMapLock;
                    this.mapLockChannelToClose = this.currentMapLockChannel;
                    this.currentMapLock = null;
                    this.currentMapLockChannel = null;
                }
                this.releaseLocksIfNeeded();
                if (this.mapWorld.getCurrentDimensionId() != null) {
                    MapDimension currentDim = this.mapWorld.getCurrentDimension();
                    MapDimension reqDim = !this.mapWorldUsableRequest ? null : this.mapWorld.getFutureDimension();
                    boolean shouldFinishCurrentDim = this.mapWorldUsable && !this.currentMapNeedsDeletion;
                    boolean currentDimChecked = false;
                    if (shouldFinishCurrentDim) {
                        this.mapSaveLoad.saveAll = true;
                    }
                    if (shouldFinishCurrentDim || shouldClearNewDimension && reqDim == currentDim) {
                        for (LeveledRegion<?> leveledRegion : currentDim.getLayeredMapRegions().getUnsyncedSet()) {
                            if (shouldFinishCurrentDim) {
                                if (leveledRegion.getLevel() == 0) {
                                    File potentialCacheFile;
                                    MapRegion leafRegion = (MapRegion)leveledRegion;
                                    if (!leafRegion.isNormalMapData() && !leafRegion.hasLookedForCache() && leafRegion.isOutdatedWithOtherLayers() && (potentialCacheFile = this.mapSaveLoad.getCacheFile(leafRegion, leafRegion.getCaveLayer(), false, false)).exists()) {
                                        leafRegion.setCacheFile(potentialCacheFile);
                                        leafRegion.setLookedForCache(true);
                                    }
                                    if (leafRegion.shouldConvertCacheToOutdatedOnFinishDim() && leafRegion.getCacheFile() != null) {
                                        leafRegion.convertCacheToOutdated(this.mapSaveLoad, "might be outdated");
                                        if (WorldMap.settings.debug) {
                                            WorldMap.LOGGER.info(String.format("Converting cache for region %s because it might be outdated.", leafRegion));
                                        }
                                    }
                                }
                                leveledRegion.setReloadHasBeenRequested(false, "world/dim change");
                                leveledRegion.onCurrentDimFinish(this.mapSaveLoad, this);
                            }
                            if (!shouldClearAllDimensions && (!shouldClearNewDimension || reqDim != currentDim)) continue;
                            leveledRegion.onDimensionClear(this);
                        }
                        currentDimChecked = true;
                    }
                    if (reqDim != currentDim && shouldClearNewDimension) {
                        for (LeveledRegion leveledRegion : reqDim.getLayeredMapRegions().getUnsyncedSet()) {
                            leveledRegion.onDimensionClear(this);
                        }
                    }
                    if (shouldClearAllDimensions) {
                        for (MapDimension mapDimension : this.mapWorld.getDimensionsList()) {
                            if (currentDimChecked && mapDimension == currentDim) continue;
                            for (LeveledRegion<?> region : mapDimension.getLayeredMapRegions().getUnsyncedSet()) {
                                region.onDimensionClear(this);
                            }
                        }
                    }
                    if (this.currentMapNeedsDeletion) {
                        this.mapWorld.getCurrentDimension().deleteMultiworldMapDataUnsynced(this.mapWorld.getCurrentDimension().getCurrentMultiworld());
                    }
                }
                this.currentMapNeedsDeletion = false;
                if (shouldClearAllDimensions) {
                    if (this.mapWorld.getCurrentDimensionId() != null) {
                        for (MapDimension dim : this.mapWorld.getDimensionsList()) {
                            dim.clear();
                        }
                    }
                    if (WorldMap.settings.debug) {
                        WorldMap.LOGGER.info("All map data cleared!");
                    }
                    if (this.state == 1) {
                        WorldMap.LOGGER.info("World map cleaned normally!");
                        this.state = 2;
                    }
                } else if (shouldClearNewDimension) {
                    this.mapWorld.getFutureDimension().clear();
                    if (WorldMap.settings.debug) {
                        WorldMap.LOGGER.info("Dimension map data cleared!");
                    }
                }
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("World changed!");
                }
                this.mapWorldUsable = this.mapWorldUsableRequest;
                if (this.mapWorldUsableRequest) {
                    this.mapWorld.switchToFutureUnsynced();
                }
                this.currentWorldId = newWorldId;
                this.currentDimId = !this.mapWorldUsableRequest ? null : this.getDimensionName(this.mapWorld.getFutureDimensionId());
                this.currentMWId = newMWId;
                Path mapPath = this.mapSaveLoad.getMWSubFolder(this.currentWorldId, this.currentDimId, this.currentMWId);
                if (this.mapWorldUsable) {
                    Files.createDirectories(mapPath, new FileAttribute[0]);
                    Path mapLockPath = mapPath.resolve(".lock");
                    int totalLockAttempts = 10;
                    int lockAttempts = 10;
                    while (lockAttempts-- > 0) {
                        if (lockAttempts < 9) {
                            WorldMap.LOGGER.info("Failed attempt to lock the current world map! Retrying in 50 ms... " + lockAttempts);
                            try {
                                Thread.sleep(50L);
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                        }
                        try {
                            FileChannel lockChannel = FileChannel.open(mapLockPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            this.currentMapLock = lockChannel.tryLock();
                            if (this.currentMapLock == null) continue;
                            this.currentMapLockChannel = lockChannel;
                            break;
                        }
                        catch (Exception e) {
                            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                        }
                    }
                }
                this.checkFootstepsReset((class_1937)this.world, (class_1937)this.newWorld);
                this.mapSaveLoad.clearToLoad();
                this.mapSaveLoad.setNextToLoadByViewing(null);
                this.clearToRefresh();
                for (int i = 0; i < this.toProcessLevels.length; ++i) {
                    this.toProcessLevels[i].clear();
                }
                if (this.mapWorldUsable && !this.isCurrentMapLocked()) {
                    for (LeveledRegion<?> region : this.mapWorld.getCurrentDimension().getLayeredMapRegions().getUnsyncedSet()) {
                        if (!region.shouldBeProcessed()) continue;
                        this.addToProcess(region);
                    }
                }
                this.mapWriter.resetPosition();
                this.world = this.newWorld;
                this.worldBlockLookup = this.newWorldBlockLookup;
                this.worldBlockRegistry = this.newWorldBlockRegistry;
                this.worldFluidRegistry = this.newWorldFluidRegistry;
                this.worldBiomeRegistry = this.newWorldBiomeRegistry;
                this.worldDimensionTypeRegistry = this.newWorldDimensionTypeRegistry;
                BlockTintProvider blockTintProvider = this.worldBlockTintProvider = this.world == null ? null : new BlockTintProvider(this.worldBiomeRegistry, this.biomeColorCalculator, this, this.brokenBlockTintCache, this.mapWriter);
                if (SupportMods.framedBlocks()) {
                    SupportMods.supportFramedBlocks.onWorldChange();
                }
                if (SupportMods.pac()) {
                    SupportMods.xaeroPac.onMapChange(changedDimension);
                    SupportMods.xaeroPac.resetDetection();
                }
                this.mapWorld.onWorldChangeUnsynced(this.world);
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("World/dimension changed to: " + this.currentWorldId + " " + this.currentDimId + " " + this.currentMWId);
                }
                this.worldDataHandler.prepareSingleplayer((class_1937)this.world, this);
                if (this.worldDataHandler.getWorldDir() == null && this.currentWorldId != null && this.mapWorld.getCurrentDimension().isUsingWorldSave()) {
                    this.currentDimId = null;
                    this.currentWorldId = null;
                }
                boolean shouldDetect = this.mapWorldUsable && !this.mapWorld.getCurrentDimension().hasDoneRegionDetection();
                this.mapSaveLoad.setRegionDetectionComplete(!shouldDetect);
                this.popRenderPause(true, true);
                this.popWriterPause();
            } else if (this.newWorld != this.world) {
                this.pushRenderPause(false, true);
                this.pushWriterPause();
                this.checkFootstepsReset((class_1937)this.world, (class_1937)this.newWorld);
                this.world = this.newWorld;
                this.worldBlockLookup = this.newWorldBlockLookup;
                this.worldBlockRegistry = this.newWorldBlockRegistry;
                this.worldFluidRegistry = this.newWorldFluidRegistry;
                this.worldBiomeRegistry = this.newWorldBiomeRegistry;
                this.worldDimensionTypeRegistry = this.newWorldDimensionTypeRegistry;
                BlockTintProvider blockTintProvider = this.worldBlockTintProvider = this.world == null ? null : new BlockTintProvider(this.worldBiomeRegistry, this.biomeColorCalculator, this, this.brokenBlockTintCache, this.mapWriter);
                if (SupportMods.framedBlocks()) {
                    SupportMods.supportFramedBlocks.onWorldChange();
                }
                if (SupportMods.pac()) {
                    SupportMods.xaeroPac.resetDetection();
                }
                this.mapWorld.onWorldChangeUnsynced(this.world);
                this.popRenderPause(false, true);
                this.popWriterPause();
            }
            if (this.mapWorldUsable) {
                this.mapWorld.getCurrentDimension().switchToFutureMultiworldWritableValueUnsynced();
                this.mapWorld.switchToFutureMultiworldTypeUnsynced();
            }
            this.waitingForWorldUpdate = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateFootprints(int step) {
        if (WorldMap.settings.footsteps) {
            if (this.footprintsTimer > 0) {
                this.footprintsTimer -= step;
            } else {
                Double[] coords = new Double[]{this.mainPlayerX, this.mainPlayerZ};
                ArrayList<Double[]> arrayList = this.footprints;
                synchronized (arrayList) {
                    this.footprints.add(coords);
                    if (this.footprints.size() > 32) {
                        this.footprints.remove(0);
                    }
                }
                this.footprintsTimer = 20;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToRefresh(MapRegion region, boolean prepareHighlights) {
        ArrayList<MapRegion> arrayList = this.toRefresh;
        synchronized (arrayList) {
            if (!this.toRefresh.contains(region)) {
                this.toRefresh.add(0, region);
            }
        }
        if (prepareHighlights) {
            this.mapRegionHighlightsPreparer.prepare(region, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeToRefresh(MapRegion region) {
        ArrayList<MapRegion> arrayList = this.toRefresh;
        synchronized (arrayList) {
            this.toRefresh.remove(region);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearToRefresh() {
        ArrayList<MapRegion> arrayList = this.toRefresh;
        synchronized (arrayList) {
            this.toRefresh.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleRefresh() throws RuntimeException {
        this.pushIsLoading();
        if (!this.waitingForWorldUpdate && !this.toRefresh.isEmpty()) {
            MapRegion region = this.toRefresh.get(0);
            if (region.isRefreshing()) {
                boolean regionLoaded;
                int globalReloadVersion = WorldMap.settings.reloadVersion;
                int globalCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                MapRegion mapRegion = region;
                synchronized (mapRegion) {
                    boolean bl = regionLoaded = region.getLoadState() == 2;
                    if (regionLoaded) {
                        region.setRecacheHasBeenRequested(true, "refresh handle");
                        region.setShouldCache(true, "refresh handle");
                        region.setVersion(WorldMap.globalVersion);
                        region.setCacheHashCode(globalCacheHashCode);
                        region.setReloadVersion(globalReloadVersion);
                        region.setHighlightsHash(region.getTargetHighlightsHash());
                    }
                }
                boolean isEmpty = true;
                if (regionLoaded) {
                    MapRegion mapRegion2 = region;
                    synchronized (mapRegion2) {
                        region.setAllCachePrepared(false);
                    }
                    boolean skipRegularRefresh = false;
                    int upToDateCaveStart = region.getUpToDateCaveStart();
                    if (region.isBeingWritten() && region.caveStartOutdated(upToDateCaveStart, WorldMap.settings.caveModeDepth)) {
                        try {
                            this.getWorldDataHandler().buildRegion(region, this.worldBlockLookup, this.worldBlockRegistry, this.worldFluidRegistry, false, null);
                            skipRegularRefresh = true;
                        }
                        catch (Throwable e) {
                            WorldMap.LOGGER.info("Region failed to refresh from world save: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                        }
                    }
                    for (int i = 0; i < 8; ++i) {
                        for (int j = 0; j < 8; ++j) {
                            MapTileChunk chunk = region.getChunk(i, j);
                            if (chunk == null) continue;
                            if (chunk.hasHadTerrain()) {
                                if (!skipRegularRefresh && chunk.getLoadState() == 2) {
                                    for (int tileX = 0; tileX < 4; ++tileX) {
                                        for (int tileZ = 0; tileZ < 4; ++tileZ) {
                                            region.pushWriterPause();
                                            MapTile tile = chunk.getTile(tileX, tileZ);
                                            if (tile != null && tile.isLoaded()) {
                                                for (int o = 0; o < 16; ++o) {
                                                    MapBlock[] column = tile.getBlockColumn(o);
                                                    for (int p = 0; p < 16; ++p) {
                                                        column[p].setSlopeUnknown(true);
                                                    }
                                                }
                                            }
                                            chunk.setTile(tileX, tileZ, tile, this.blockStateShortShapeCache);
                                            region.popWriterPause();
                                        }
                                    }
                                    chunk.setToUpdateBuffers(true);
                                }
                            } else {
                                region.pushWriterPause();
                                if (!(chunk.hasHadTerrain() || chunk.wasChanged() || chunk.getToUpdateBuffers())) {
                                    region.uncountTextureBiomes(chunk.getLeafTexture());
                                    chunk.getLeafTexture().resetBiomes();
                                    if (chunk.hasHighlightsIfUndiscovered()) {
                                        chunk.getLeafTexture().requestHighlightOnlyUpload();
                                    } else {
                                        region.setChunk(i, j, null);
                                        chunk.getLeafTexture().deleteTexturesAndBuffers();
                                    }
                                }
                                region.popWriterPause();
                            }
                            isEmpty = false;
                        }
                    }
                    if (WorldMap.settings.debug) {
                        WorldMap.LOGGER.info("Region refreshed: " + String.valueOf(region) + " " + String.valueOf(region) + " " + this.mapWriter.getUpdateCounter());
                    }
                }
                MapRegion mapRegion3 = region;
                synchronized (mapRegion3) {
                    region.setRefreshing(false);
                    if (isEmpty) {
                        region.setShouldCache(false, "refresh handle");
                        region.setRecacheHasBeenRequested(false, "refresh handle");
                    }
                }
                if (region.isResaving()) {
                    region.setLastSaveTime(-60000L);
                }
            } else {
                throw new RuntimeException(String.format("Trying to refresh region %s, which is not marked as being refreshed!", region));
            }
            this.removeToRefresh(region);
        }
        this.popIsLoading();
    }

    public boolean regionExists(int caveLayer, int x, int z) {
        return this.regionDetectionExists(caveLayer, x, z) || this.mapWorld.getCurrentDimension().getHighlightHandler().shouldApplyRegionHighlights(x, z, false);
    }

    public boolean regionDetectionExists(int caveLayer, int x, int z) {
        if (!this.mapSaveLoad.isRegionDetectionComplete()) {
            return false;
        }
        return this.mapWorld.getCurrentDimension().getLayeredMapRegions().getLayer(caveLayer).regionDetectionExists(x, z);
    }

    public void removeMapRegion(LeveledRegion<?> region) {
        MapDimension regionDim = region.getDim();
        LayeredRegionManager regions = regionDim.getLayeredMapRegions();
        if (region.getLevel() == 0) {
            regions.remove(region.getCaveLayer(), region.getRegionX(), region.getRegionZ(), region.getLevel());
            regions.removeListRegion(region);
        }
        regions.removeLoadedRegion(region);
        this.removeToProcess(region);
    }

    public LeveledRegion<?> getLeveledRegion(int caveLayer, int leveledRegX, int leveledRegZ, int level) {
        MapDimension mapDimension = this.mapWorld.getCurrentDimension();
        LayeredRegionManager regions = mapDimension.getLayeredMapRegions();
        return regions.get(caveLayer, leveledRegX, leveledRegZ, level);
    }

    public void initMinimapRender(int flooredMapCameraX, int flooredMapCameraZ) {
        this.minimapRenderListener.init(this, flooredMapCameraX, flooredMapCameraZ);
    }

    public void beforeMinimapRegionRender(MapRegion region) {
        this.minimapRenderListener.beforeMinimapRender(region);
    }

    public void finalizeMinimapRender() {
        this.minimapRenderListener.finalize(this);
    }

    public MapRegion getLeafMapRegion(int caveLayer, int regX, int regZ, boolean create) {
        if (!this.mapSaveLoad.isRegionDetectionComplete()) {
            return null;
        }
        MapDimension mapDimension = this.mapWorld.getCurrentDimension();
        LayeredRegionManager regions = mapDimension.getLayeredMapRegions();
        MapRegion region = regions.getLeaf(caveLayer, regX, regZ);
        if (region == null) {
            if (create) {
                if (!class_310.method_1551().method_18854()) {
                    throw new IllegalAccessError();
                }
                region = new MapRegion(this.currentWorldId, this.currentDimId, this.currentMWId, mapDimension, regX, regZ, caveLayer, this.getGlobalVersion(), !mapDimension.isUsingWorldSave(), this.worldBiomeRegistry);
                MapLayer mapLayer = regions.getLayer(caveLayer);
                region.updateCaveMode();
                RegionDetection regionDetection = mapLayer.getRegionDetection(regX, regZ);
                if (regionDetection != null) {
                    regionDetection.transferInfoTo(region);
                    mapLayer.removeRegionDetection(regX, regZ);
                } else if (mapLayer.getCompleteRegionDetection(regX, regZ) == null) {
                    RegionDetection perpetualRegionDetection = new RegionDetection(region.getWorldId(), region.getDimId(), region.getMwId(), region.getRegionX(), region.getRegionZ(), region.getRegionFile(), this.getGlobalVersion(), true);
                    mapLayer.tryAddingToCompleteRegionDetection(perpetualRegionDetection);
                    if (!region.isNormalMapData()) {
                        mapLayer.removeRegionDetection(regX, regZ);
                    }
                }
                if (!region.hasHadTerrain()) {
                    regions.getLayer(caveLayer).getRegionHighlightExistenceTracker().stopTracking(regX, regZ);
                    region.setVersion(this.getGlobalVersion());
                    region.setCacheHashCode(WorldMap.settings.getRegionCacheHashCode());
                    region.setReloadVersion(WorldMap.settings.reloadVersion);
                }
                regions.putLeaf(regX, regZ, region);
                regions.addListRegion(region);
                if (regionDetection != null) {
                    regionDetection.transferInfoPostAddTo(region, this);
                }
            } else {
                return null;
            }
        }
        return region;
    }

    public MapRegion getMinimapMapRegion(int regX, int regZ) {
        int renderedCaveLayer = this.minimapRenderListener.getRenderedCaveLayer();
        return this.getLeafMapRegion(renderedCaveLayer, regX, regZ, this.regionExists(renderedCaveLayer, regX, regZ));
    }

    public MapTileChunk getMapChunk(int caveLayer, int chunkX, int chunkZ) {
        int regionX = chunkX >> 3;
        int regionZ = chunkZ >> 3;
        MapRegion region = this.getLeafMapRegion(caveLayer, regionX, regionZ, false);
        if (region == null) {
            return null;
        }
        int localChunkX = chunkX & 7;
        int localChunkZ = chunkZ & 7;
        return region.getChunk(localChunkX, localChunkZ);
    }

    public MapTile getMapTile(int caveLayer, int x, int z) {
        MapTileChunk tileChunk = this.getMapChunk(caveLayer, x >> 2, z >> 2);
        if (tileChunk == null) {
            return null;
        }
        int tileX = x & 3;
        int tileZ = z & 3;
        return tileChunk.getTile(tileX, tileZ);
    }

    public void updateWorldSpawn(class_2338 newSpawn, class_638 world) {
        class_5321 dimId = world.method_27983();
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        worldData.latestSpawn = newSpawn;
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info("Updated spawn for dimension " + String.valueOf(dimId) + " " + String.valueOf(newSpawn));
        }
        this.spawnToRestore = newSpawn;
        if (world == this.mainWorld) {
            this.mainWorldChangedTime = -1L;
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Done waiting for main spawn.");
            }
        }
        this.checkForWorldUpdate();
    }

    public void onServerLevelId(int serverLevelId) {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        worldData.serverLevelId = serverLevelId;
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info("Updated server level id " + serverLevelId);
        }
        this.checkForWorldUpdate();
    }

    public void onWorldUnload() {
        if (this.mainWorldUnloaded) {
            return;
        }
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info("Changing worlds, pausing the world map...");
        }
        this.mainWorldUnloaded = true;
        this.mapWorld.clearAllCachedHighlightHashes();
        this.mainWorldChangedTime = -1L;
        this.changeWorld(null, null, null, null, null, null);
    }

    public void onClientTickStart() throws RuntimeException {
        if (this.mainWorld != null && this.spawnToRestore != null && this.mainWorldChangedTime != -1L && System.currentTimeMillis() - this.mainWorldChangedTime >= 3000L) {
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("SPAWN SET TIME OUT");
            }
            this.updateWorldSpawn(this.spawnToRestore, this.mainWorld);
        }
    }

    private void updateRenderStartTime() {
        if (this.renderStartTime == -1L) {
            this.renderStartTime = System.nanoTime();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pushWriterPause() {
        Object object = this.renderThreadPauseSync;
        synchronized (object) {
            ++this.pauseWriting;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void popWriterPause() {
        Object object = this.renderThreadPauseSync;
        synchronized (object) {
            --this.pauseWriting;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pushRenderPause(boolean rendering, boolean uploading) {
        Object object = this.renderThreadPauseSync;
        synchronized (object) {
            if (rendering) {
                ++this.pauseRendering;
            }
            if (uploading) {
                ++this.pauseUploading;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void popRenderPause(boolean rendering, boolean uploading) {
        Object object = this.renderThreadPauseSync;
        synchronized (object) {
            if (rendering) {
                --this.pauseRendering;
            }
            if (uploading) {
                --this.pauseUploading;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pushIsLoading() {
        Object object = this.loadingSync;
        synchronized (object) {
            this.isLoading = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void popIsLoading() {
        Object object = this.loadingSync;
        synchronized (object) {
            this.isLoading = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void pushUIPause() {
        Object object = this.uiPauseSync;
        synchronized (object) {
            this.isUIPaused = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void popUIPause() {
        Object object = this.uiPauseSync;
        synchronized (object) {
            this.isUIPaused = false;
        }
    }

    public boolean isUIPaused() {
        return this.isUIPaused;
    }

    public boolean isWritingPaused() {
        return this.pauseWriting > 0;
    }

    public boolean isRenderingPaused() {
        return this.pauseRendering > 0;
    }

    public boolean isUploadingPaused() {
        return this.pauseUploading > 0;
    }

    public boolean isProcessingPaused() {
        return this.pauseProcessing > 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isProcessed(LeveledRegion<?> region) {
        ArrayList<LeveledRegion<?>> toProcess;
        ArrayList<LeveledRegion<?>> arrayList = toProcess = this.toProcessLevels[region.getLevel()];
        synchronized (arrayList) {
            return toProcess.contains(region);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToProcess(LeveledRegion<?> region) {
        ArrayList<LeveledRegion<?>> toProcess;
        ArrayList<LeveledRegion<?>> arrayList = toProcess = this.toProcessLevels[region.getLevel()];
        synchronized (arrayList) {
            toProcess.add(region);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeToProcess(LeveledRegion<?> region) {
        ArrayList<LeveledRegion<?>> toProcess;
        ArrayList<LeveledRegion<?>> arrayList = toProcess = this.toProcessLevels[region.getLevel()];
        synchronized (arrayList) {
            toProcess.remove(region);
        }
    }

    public int getProcessedCount() {
        int total = 0;
        for (int i = 0; i < this.toProcessLevels.length; ++i) {
            total += this.toProcessLevels[i].size();
        }
        return total;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getAffectingLoadingFrequencyCount() {
        int total = 0;
        block3: for (int i = 0; i < this.toProcessLevels.length; ++i) {
            ArrayList<LeveledRegion<?>> processed = this.toProcessLevels[i];
            for (int j = 0; j < processed.size(); ++j) {
                ArrayList<LeveledRegion<?>> arrayList = processed;
                synchronized (arrayList) {
                    if (j >= processed.size()) {
                        continue block3;
                    }
                    if (processed.get(j).shouldAffectLoadingRequestFrequency()) {
                        ++total;
                    }
                    continue;
                }
            }
        }
        return total;
    }

    public MapSaveLoad getMapSaveLoad() {
        return this.mapSaveLoad;
    }

    public class_638 getWorld() {
        return this.world;
    }

    public class_638 getNewWorld() {
        return this.newWorld;
    }

    public String getCurrentWorldId() {
        return this.currentWorldId;
    }

    public String getCurrentDimId() {
        return this.currentDimId;
    }

    public String getCurrentMWId() {
        return this.currentMWId;
    }

    public MapWriter getMapWriter() {
        return this.mapWriter;
    }

    public MapLimiter getMapLimiter() {
        return this.mapLimiter;
    }

    public ArrayList<Double[]> getFootprints() {
        return this.footprints;
    }

    public ByteBufferDeallocator getBufferDeallocator() {
        return this.bufferDeallocator;
    }

    public MapTilePool getTilePool() {
        return this.tilePool;
    }

    public OverlayManager getOverlayManager() {
        return this.overlayManager;
    }

    public int getGlobalVersion() {
        return WorldMap.globalVersion;
    }

    public void setGlobalVersion(int globalVersion) {
        WorldMap.globalVersion = globalVersion;
    }

    public long getRenderStartTime() {
        return this.renderStartTime;
    }

    public void resetRenderStartTime() {
        this.renderStartTime = -1L;
    }

    public Queue<Runnable> getMinecraftScheduledTasks() {
        Queue result;
        this.scheduledTasksField.setAccessible(true);
        try {
            result = (Queue)this.scheduledTasksField.get(class_310.method_1551());
        }
        catch (IllegalArgumentException e) {
            result = null;
        }
        catch (IllegalAccessException e) {
            result = null;
        }
        this.scheduledTasksField.setAccessible(false);
        return result;
    }

    public Runnable getRenderStartTimeUpdater() {
        return this.renderStartTimeUpdaterRunnable;
    }

    public boolean isWaitingForWorldUpdate() {
        return this.waitingForWorldUpdate;
    }

    public WorldDataHandler getWorldDataHandler() {
        return this.worldDataHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMainValues() {
        Object object = this.mainStuffSync;
        synchronized (object) {
            class_1297 player = class_310.method_1551().method_1560();
            if (player != null) {
                boolean worldChanging;
                class_638 worldToChangeTo = this.ignoreWorld(player.method_37908()) || !(player.method_37908() instanceof class_638) ? this.mainWorld : (class_638)player.method_37908();
                boolean bl = worldChanging = worldToChangeTo != this.mainWorld;
                if (worldChanging) {
                    this.mainWorldChangedTime = -1L;
                    if (this.spawnToRestore != null) {
                        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(worldToChangeTo);
                        if (worldData.latestSpawn == null) {
                            this.mainWorldChangedTime = System.currentTimeMillis();
                        }
                    }
                    this.mainWorldUnloaded = false;
                    this.mainWorldBlockLookup = worldToChangeTo == null ? null : worldToChangeTo.method_45448(class_7924.field_41254);
                    this.mainWorldBlockRegistry = worldToChangeTo == null ? null : worldToChangeTo.method_30349().method_30530(class_7924.field_41254);
                    this.mainWorldFluidRegistry = worldToChangeTo == null ? null : worldToChangeTo.method_30349().method_30530(class_7924.field_41270);
                    this.mainWorldBiomeRegistry = worldToChangeTo == null ? null : worldToChangeTo.method_30349().method_30530(class_7924.field_41236);
                    this.mainWorldDimensionTypeRegistry = worldToChangeTo == null ? null : worldToChangeTo.method_30349().method_30530(class_7924.field_41241);
                }
                this.mainWorld = worldToChangeTo;
                this.mainPlayerX = player.method_23317();
                this.mainPlayerY = player.method_23318();
                this.mainPlayerZ = player.method_23321();
                if (worldChanging) {
                    this.checkForWorldUpdate();
                }
            } else {
                if (this.mainWorld != null && !this.mainWorldUnloaded) {
                    this.onWorldUnload();
                }
                this.mainWorld = null;
            }
        }
    }

    public float getBrightness() {
        return this.getBrightness(WorldMap.settings.lighting);
    }

    public float getBrightness(boolean lighting) {
        return this.getBrightness(this.currentCaveLayer, this.world, lighting);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public float getBrightness(int layer, class_638 world, boolean lighting) {
        float sunBrightness;
        if (world == null || world != this.world) {
            return 1.0f;
        }
        MapDimension dim = this.mapWorld.getCurrentDimension();
        class_2874 dimType = dim.getDimensionType(this.worldDimensionTypeRegistry);
        if (layer == Integer.MAX_VALUE || dimType != null && !dimType.comp_642()) {
            if (!lighting) return 1.0f;
            if (dimType != null && dim.getDimensionEffects(this.worldDimensionTypeRegistry).method_28114()) {
                return 1.0f;
            }
            sunBrightness = (dim.getSkyDarken(1.0f, world, this.worldDimensionTypeRegistry) - 0.2f) / 0.8f;
        } else {
            if (!lighting) return 1.0f;
            sunBrightness = 0.0f;
        }
        float ambient = this.getAmbientBrightness(dimType);
        return ambient + (1.0f - ambient) * class_3532.method_15363((float)sunBrightness, (float)0.0f, (float)1.0f);
    }

    public float getAmbientBrightness(class_2874 dimType) {
        float result = 0.375f + (dimType == null ? 0.0f : dimType.comp_656());
        if (result > 1.0f) {
            result = 1.0f;
        }
        return result;
    }

    public static boolean isWorldRealms(String world) {
        return world.startsWith("Realms_");
    }

    public static boolean isWorldMultiplayer(boolean realms, String world) {
        return realms || world.startsWith("Multiplayer_");
    }

    public MapWorld getMapWorld() {
        return this.mapWorld;
    }

    public boolean isCurrentMultiworldWritable() {
        return this.mapWorldUsable && this.mapWorld.getCurrentDimension().currentMultiworldWritable;
    }

    public String getCurrentDimension() {
        return "placeholder";
    }

    public void requestCurrentMapDeletion() {
        if (this.currentMapNeedsDeletion) {
            throw new RuntimeException("Requesting map deletion at a weird time!");
        }
        this.currentMapNeedsDeletion = true;
    }

    public boolean isFinalizing() {
        return this.finalizing;
    }

    public void stop() {
        this.finalizing = true;
        WorldMap.mapRunner.addTask(new MapRunnerTask(){

            @Override
            public void run(MapProcessor doNotUse) {
                if (MapProcessor.this.state == 0) {
                    MapProcessor.this.state = 1;
                    if (!MapProcessor.this.mapWorldUsable) {
                        MapProcessor.this.forceClean();
                    } else {
                        MapProcessor.this.changeWorld(null, null, null, null, null, null);
                    }
                }
            }
        });
    }

    private synchronized void forceClean() {
        this.pushRenderPause(true, true);
        this.pushWriterPause();
        if (this.mapWorld != null) {
            for (MapDimension dim : this.mapWorld.getDimensionsList()) {
                for (LeveledRegion<?> region : dim.getLayeredMapRegions().getUnsyncedSet()) {
                    region.onDimensionClear(this);
                }
            }
        }
        this.popRenderPause(true, true);
        this.popWriterPause();
        if (this.currentMapLock != null) {
            if (this.mapLockToRelease != null) {
                this.releaseLocksIfNeeded();
            }
            this.mapLockToRelease = this.currentMapLock;
            this.mapLockChannelToClose = this.currentMapLockChannel;
            this.releaseLocksIfNeeded();
        }
        this.state = 2;
        WorldMap.LOGGER.info("World map force-cleaned!");
    }

    public boolean isMapWorldUsable() {
        return this.mapWorldUsable;
    }

    private Object getAutoIdBase(class_638 world) {
        return this.hasServerLevelId() ? WorldMapClientWorldDataHelper.getCurrentWorldData().serverLevelId : WorldMapClientWorldDataHelper.getWorldData((class_638)world).latestSpawn;
    }

    private Object getUsedAutoIdBase(class_638 world) {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        return this.hasServerLevelId() ? WorldMapClientWorldDataHelper.getCurrentWorldData().usedServerLevelId : worldData.usedSpawn;
    }

    private void setUsedAutoIdBase(class_638 world, Object autoIdBase) {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        if (this.hasServerLevelId()) {
            WorldMapClientWorldDataHelper.getCurrentWorldData().usedServerLevelId = (Integer)autoIdBase;
        } else {
            worldData.usedSpawn = (class_2338)autoIdBase;
        }
    }

    private void removeUsedAutoIdBase(class_638 world) {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        if (this.hasServerLevelId()) {
            WorldMapClientWorldDataHelper.getCurrentWorldData().usedServerLevelId = null;
        } else {
            worldData.usedSpawn = null;
        }
    }

    private boolean hasServerLevelId() {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return false;
        }
        return worldData.serverLevelId != null && !this.mapWorld.isIgnoreServerLevelId();
    }

    public boolean isEqual(String worldId, String dimId, String mwId) {
        return worldId.equals(this.currentWorldId) && dimId.equals(this.currentDimId) && (mwId == this.currentMWId || mwId != null && mwId.equals(this.currentMWId));
    }

    public boolean isFinished() {
        return this.state == 3;
    }

    public MultiTextureRenderTypeRendererProvider getMultiTextureRenderTypeRenderers() {
        return this.multiTextureRenderTypeRenderers;
    }

    public CustomVertexConsumers getCvc() {
        return this.cvc;
    }

    public boolean isCurrentMapLocked() {
        return this.currentMapLock == null;
    }

    private void releaseLocksIfNeeded() {
        if (this.mapLockToRelease != null) {
            int lockAttempts = 10;
            while (lockAttempts-- > 0) {
                try {
                    if (this.mapLockToRelease.isValid()) {
                        this.mapLockToRelease.release();
                    }
                    this.mapLockChannelToClose.close();
                    break;
                }
                catch (Exception e) {
                    WorldMap.LOGGER.error("Failed attempt to release the lock for the world map! Retrying in 50 ms... " + lockAttempts, (Throwable)e);
                    try {
                        Thread.sleep(50L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
            this.mapLockToRelease = null;
            this.mapLockChannelToClose = null;
        }
    }

    private int getCaveLayer(int caveStart) {
        if (caveStart == Integer.MAX_VALUE || caveStart == Integer.MIN_VALUE) {
            return caveStart;
        }
        return caveStart >> 4;
    }

    public int getCurrentCaveLayer() {
        return this.currentCaveLayer;
    }

    public BlockStateShortShapeCache getBlockStateShortShapeCache() {
        return this.blockStateShortShapeCache;
    }

    public BlockTintProvider getWorldBlockTintProvider() {
        return this.worldBlockTintProvider;
    }

    public HighlighterRegistry getHighlighterRegistry() {
        return this.highlighterRegistry;
    }

    public MapRegionHighlightsPreparer getMapRegionHighlightsPreparer() {
        return this.mapRegionHighlightsPreparer;
    }

    public MessageBox getMessageBox() {
        return this.messageBox;
    }

    public MessageBoxRenderer getMessageBoxRenderer() {
        return this.messageBoxRenderer;
    }

    public class_2378<class_2248> getWorldBlockRegistry() {
        return this.worldBlockRegistry;
    }

    public class_7225<class_2248> getWorldBlockLookup() {
        return this.worldBlockLookup;
    }

    public boolean isConsideringNetherFairPlay() {
        return this.consideringNetherFairPlayMessage;
    }

    public void setConsideringNetherFairPlayMessage(boolean consideringNetherFairPlay) {
        this.consideringNetherFairPlayMessage = consideringNetherFairPlay;
    }

    public BiomeColorCalculator getBiomeColorCalculator() {
        return this.biomeColorCalculator;
    }

    public ClientSyncedTrackedPlayerManager getClientSyncedTrackedPlayerManager() {
        return this.clientSyncedTrackedPlayerManager;
    }

    public boolean serverHasMod() {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        return worldData != null && worldData.serverLevelId != null;
    }

    public void setServerModNetworkVersion(int networkVersion) {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return;
        }
        worldData.setServerModNetworkVersion(networkVersion);
    }

    public int getServerModNetworkVersion() {
        WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return 0;
        }
        return worldData.getServerModNetworkVersion();
    }

    public class_2378<class_2874> getWorldDimensionTypeRegistry() {
        return this.worldDimensionTypeRegistry;
    }

    private void checkFootstepsReset(class_1937 oldWorld, class_1937 newWorld) {
        class_5321 newDimId;
        class_5321 oldDimId = oldWorld == null ? null : oldWorld.method_27983();
        class_5321 class_53212 = newDimId = newWorld == null ? null : newWorld.method_27983();
        if (oldDimId != newDimId) {
            this.footprints.clear();
        }
    }

    private void fixRootFolder(String mainId, class_634 connection) {
        for (int format = 3; format >= 1; --format) {
            this.fixRootFolder(mainId, this.getMainId(format, connection));
        }
    }

    private void fixRootFolder(String mainId, String oldMainId) {
        if (!mainId.equals(oldMainId)) {
            Path fixedFolder;
            Path oldFolder;
            try {
                oldFolder = WorldMap.saveFolder.toPath().resolve(oldMainId);
            }
            catch (InvalidPathException ipe) {
                return;
            }
            if (Files.exists(oldFolder, new LinkOption[0]) && !Files.exists(fixedFolder = WorldMap.saveFolder.toPath().resolve(mainId), new LinkOption[0])) {
                try {
                    Files.move(oldFolder, fixedFolder, new CopyOption[0]);
                }
                catch (IOException e) {
                    throw new RuntimeException("failed to auto-restore old world map folder", e);
                }
            }
        }
    }
}

