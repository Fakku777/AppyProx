/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2960
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package xaero.map;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.class_2960;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.map.CrashHandler;
import xaero.map.MapLimiter;
import xaero.map.MapRunner;
import xaero.map.MapWriter;
import xaero.map.WorldMapClientOnly;
import xaero.map.biome.BiomeGetter;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.cache.UnknownBlockStateCache;
import xaero.map.common.config.CommonConfig;
import xaero.map.common.config.CommonConfigIO;
import xaero.map.common.config.CommonConfigInit;
import xaero.map.controls.ControlsRegister;
import xaero.map.deallocator.ByteBufferDeallocator;
import xaero.map.element.MapElementRenderHandler;
import xaero.map.events.ClientEvents;
import xaero.map.events.CommonEvents;
import xaero.map.events.ModClientEvents;
import xaero.map.events.ModCommonEvents;
import xaero.map.file.OldFormatSupport;
import xaero.map.file.export.PNGExporter;
import xaero.map.graphics.GpuObjectDeleter;
import xaero.map.graphics.TextureUploadBenchmark;
import xaero.map.message.WorldMapMessageHandler;
import xaero.map.message.WorldMapMessageRegister;
import xaero.map.misc.Internet;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.WaypointSymbolCreator;
import xaero.map.patreon.Patreon;
import xaero.map.platform.Services;
import xaero.map.pool.MapTilePool;
import xaero.map.pool.TextureUploadPool;
import xaero.map.pool.buffer.TextureDirectBufferPool;
import xaero.map.radar.tracker.PlayerTrackerMapElementRenderer;
import xaero.map.radar.tracker.PlayerTrackerMenuRenderer;
import xaero.map.radar.tracker.system.PlayerTrackerSystemManager;
import xaero.map.radar.tracker.system.impl.SyncedPlayerTrackerSystem;
import xaero.map.region.OverlayManager;
import xaero.map.server.WorldMapServer;
import xaero.map.server.mods.SupportServerMods;
import xaero.map.server.player.ServerPlayerTickHandler;
import xaero.map.settings.ModOptions;
import xaero.map.settings.ModSettings;

public abstract class WorldMap {
    public static final String MOD_ID = "xaeroworldmap";
    public static boolean loaded = false;
    public static WorldMap INSTANCE;
    public static int MINIMAP_COMPATIBILITY_VERSION;
    public static Logger LOGGER;
    static final String versionID_minecraft = "1.21.8";
    private String versionID;
    public static int newestUpdateID;
    public static boolean isOutdated;
    public static String latestVersion;
    public static String latestVersionMD5;
    public static ClientEvents events;
    public static ModClientEvents modEvents;
    public static ControlsRegister controlsRegister;
    public static WaypointSymbolCreator waypointSymbolCreator;
    public static ByteBufferDeallocator bufferDeallocator;
    public static TextureUploadBenchmark textureUploadBenchmark;
    public static OverlayManager overlayManager;
    public static OldFormatSupport oldFormatSupport;
    public static PNGExporter pngExporter;
    public static TextureUploadPool.Normal normalTextureUploadPool;
    public static TextureUploadPool.BranchUpdate branchUpdatePool;
    public static TextureUploadPool.BranchUpdate branchUpdateAllocatePool;
    public static TextureUploadPool.BranchDownload branchDownloadPool;
    public static TextureUploadPool.SubsequentNormal subsequentNormalTextureUploadPool;
    public static TextureDirectBufferPool textureDirectBufferPool;
    public static MapTilePool tilePool;
    public static MapLimiter mapLimiter;
    public static UnknownBlockStateCache unknownBlockStateCache;
    public static GpuObjectDeleter gpuObjectDeleter;
    public static MapRunner mapRunner;
    public static Thread mapRunnerThread;
    public static CrashHandler crashHandler;
    public static final class_2960 guiTextures;
    public static ModSettings settings;
    public static int globalVersion;
    public static WorldMapClientOnly worldMapClientOnly;
    public static WorldMapServer worldmapServer;
    public static MapElementRenderHandler mapElementRenderHandler;
    public static ServerPlayerTickHandler serverPlayerTickHandler;
    public static PlayerTrackerSystemManager playerTrackerSystemManager;
    public static PlayerTrackerMapElementRenderer trackedPlayerRenderer;
    public static PlayerTrackerMenuRenderer trackedPlayerMenuRenderer;
    public static WorldMapMessageHandler messageHandler;
    public static CommonEvents commonEvents;
    public static ModCommonEvents modCommonEvents;
    public static File modJAR;
    public static File configFolder;
    public static File optionsFile;
    public static File saveFolder;
    public static CommonConfigIO commonConfigIO;
    public static CommonConfig commonConfig;

    public WorldMap() {
        INSTANCE = this;
        new CommonConfigInit().init("xaeroworldmap-common.txt");
    }

    protected abstract Path fetchModFile();

    protected abstract String getFileLayoutID();

    void loadClient() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Object patreonEntry;
        LOGGER.info("Loading Xaero's World Map - Stage 1/2");
        trackedPlayerRenderer = PlayerTrackerMapElementRenderer.Builder.begin().build();
        trackedPlayerMenuRenderer = PlayerTrackerMenuRenderer.Builder.begin().setRenderer(trackedPlayerRenderer).build();
        ModOptions.init();
        Path modFile = this.fetchModFile();
        worldMapClientOnly = this.createClientLoad();
        worldMapClientOnly.preInit(MOD_ID);
        String fileName = modFile.getFileName().toString();
        if (fileName.endsWith(".jar")) {
            modJAR = modFile.toFile();
        }
        Path gameDir = Services.PLATFORM.getGameDir();
        Path config = Services.PLATFORM.getConfigDir();
        configFolder = config.toFile();
        optionsFile = config.resolve("xaeroworldmap.txt").toFile();
        Path oldSaveFolder4 = gameDir.resolve("XaeroWorldMap");
        Path xaeroFolder = gameDir.resolve("xaero");
        if (!Files.exists(xaeroFolder, new LinkOption[0])) {
            Files.createDirectories(xaeroFolder, new FileAttribute[0]);
        }
        saveFolder = xaeroFolder.resolve("world-map").toFile();
        if (oldSaveFolder4.toFile().exists() && !saveFolder.exists()) {
            Files.move(oldSaveFolder4, saveFolder.toPath(), new CopyOption[0]);
        }
        Path oldSaveFolder3 = config.getParent().resolve("XaeroWorldMap");
        File oldOptionsFile = gameDir.resolve("xaeroworldmap.txt").toFile();
        File oldSaveFolder = gameDir.resolve("mods").resolve("XaeroWorldMap").toFile();
        File oldSaveFolder2 = gameDir.resolve("config").resolve("XaeroWorldMap").toFile();
        if (oldOptionsFile.exists() && !optionsFile.exists()) {
            Files.move(oldOptionsFile.toPath(), optionsFile.toPath(), new CopyOption[0]);
        }
        if (oldSaveFolder.exists() && !saveFolder.exists()) {
            Files.move(oldSaveFolder.toPath(), saveFolder.toPath(), new CopyOption[0]);
        }
        if (oldSaveFolder2.exists() && !saveFolder.exists()) {
            Files.move(oldSaveFolder2.toPath(), saveFolder.toPath(), new CopyOption[0]);
        }
        if (oldSaveFolder3.toFile().exists() && !saveFolder.exists()) {
            Files.move(oldSaveFolder3, saveFolder.toPath(), new CopyOption[0]);
        }
        if (!saveFolder.exists()) {
            Files.createDirectories(saveFolder.toPath(), new FileAttribute[0]);
        }
        settings = new ModSettings();
        settings.loadSettings();
        Patreon.checkPatreon();
        Internet.checkModVersion();
        if (isOutdated && (patreonEntry = Patreon.getMods().get(this.getFileLayoutID())) != null) {
            Patreon.setModInfo(patreonEntry, modJAR, this.getVersionID(), latestVersion, latestVersionMD5, () -> {
                ModSettings.ignoreUpdate = newestUpdateID;
                try {
                    settings.saveSettings();
                }
                catch (IOException e) {
                    LOGGER.error("suppressed exception", (Throwable)e);
                }
            });
            Patreon.addOutdatedMod(patreonEntry);
        }
        waypointSymbolCreator = new WaypointSymbolCreator();
        if (controlsRegister == null) {
            controlsRegister = new ControlsRegister();
        }
        bufferDeallocator = new ByteBufferDeallocator();
        tilePool = new MapTilePool();
        overlayManager = new OverlayManager();
        oldFormatSupport = new OldFormatSupport();
        pngExporter = new PNGExporter(configFolder.toPath().getParent().resolve("map exports"));
        mapLimiter = new MapLimiter();
        normalTextureUploadPool = new TextureUploadPool.Normal(256);
        branchUpdatePool = new TextureUploadPool.BranchUpdate(256, false);
        branchUpdateAllocatePool = new TextureUploadPool.BranchUpdate(256, true);
        branchDownloadPool = new TextureUploadPool.BranchDownload(256);
        textureDirectBufferPool = new TextureDirectBufferPool();
        subsequentNormalTextureUploadPool = new TextureUploadPool.SubsequentNormal(256);
        textureUploadBenchmark = new TextureUploadBenchmark(512, 512, 512, 256, 256, 256, 256);
        unknownBlockStateCache = new UnknownBlockStateCache();
        gpuObjectDeleter = new GpuObjectDeleter();
        crashHandler = new CrashHandler();
        mapRunner = new MapRunner();
        mapRunnerThread = new Thread(mapRunner);
        mapRunnerThread.start();
    }

    public void loadLater() {
        LOGGER.info("Loading Xaero's World Map - Stage 2/2");
        try {
            settings.findMapItem();
            worldMapClientOnly.postInit();
            settings.updateRegionCacheHashCode();
            playerTrackerSystemManager.register("map_synced", new SyncedPlayerTrackerSystem());
            this.createSupportMods().load();
            mapElementRenderHandler = MapElementRenderHandler.Builder.begin().setPoseStack(worldMapClientOnly.getMapScreenPoseStack()).build();
            oldFormatSupport.loadStates();
            loaded = true;
        }
        catch (Throwable e) {
            LOGGER.error("error", e);
            crashHandler.setCrashedBy(e);
        }
    }

    void loadServer() {
        worldmapServer = this.createServerLoad();
        worldmapServer.load();
    }

    void loadLaterServer() {
        worldmapServer.loadLater();
        loaded = true;
    }

    void loadCommon() {
        this.versionID = "1.21.8_" + this.getModInfoVersion();
        new WorldMapMessageRegister().register(messageHandler);
        serverPlayerTickHandler = new ServerPlayerTickHandler();
        SupportServerMods.check();
    }

    public String getVersionID() {
        return this.versionID;
    }

    public static void onSessionFinalized() {
        mapLimiter.onSessionFinalized();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onSessionFinalized();
        }
    }

    protected abstract SupportMods createSupportMods();

    protected abstract WorldMapClientOnly createClientLoad();

    protected abstract WorldMapServer createServerLoad();

    public abstract MapWriter createWriter(OverlayManager var1, BlockStateShortShapeCache var2, BiomeGetter var3);

    protected abstract String getModInfoVersion();

    static {
        MINIMAP_COMPATIBILITY_VERSION = 26;
        LOGGER = LogManager.getLogger();
        guiTextures = class_2960.method_60655((String)MOD_ID, (String)"gui/gui.png");
        globalVersion = 1;
        playerTrackerSystemManager = new PlayerTrackerSystemManager();
        modJAR = null;
    }
}

