/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package xaero.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xaero.common.HudClientOnlyBase;
import xaero.common.HudCommonBase;
import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContext;
import xaero.common.XaeroMinimapSession;
import xaero.common.config.CommonConfig;
import xaero.common.config.CommonConfigIO;
import xaero.common.config.CommonConfigInit;
import xaero.common.core.XaeroMinimapCore;
import xaero.common.events.ClientEvents;
import xaero.common.events.ClientEventsListener;
import xaero.common.events.CommonEvents;
import xaero.common.events.ModClientEvents;
import xaero.common.events.ModCommonEvents;
import xaero.common.file.SimpleBackup;
import xaero.common.gui.GuiHelper;
import xaero.common.gui.widget.WidgetLoadingHandler;
import xaero.common.gui.widget.WidgetScreenHandler;
import xaero.common.message.MinimapMessageHandler;
import xaero.common.misc.Internet;
import xaero.common.mods.SupportMods;
import xaero.common.patreon.Patreon;
import xaero.common.patreon.PatreonMod;
import xaero.common.platform.Services;
import xaero.common.server.XaeroMinimapServer;
import xaero.common.server.core.XaeroMinimapServerCore;
import xaero.common.server.mods.SupportServerMods;
import xaero.common.server.player.ServerPlayerTickHandler;
import xaero.common.settings.ModOptions;
import xaero.common.settings.ModSettings;
import xaero.common.validator.FieldValidatorHolder;
import xaero.common.validator.NumericFieldValidator;
import xaero.common.validator.WaypointCoordinateFieldValidator;
import xaero.hud.Hud;
import xaero.hud.category.setting.ObjectCategoryDefaultSettingsSetter;
import xaero.hud.controls.ControlsRegister;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.io.HudIO;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderer;
import xaero.hud.minimap.player.tracker.synced.SyncedRenderedPlayerTracker;
import xaero.hud.minimap.player.tracker.system.RenderedPlayerTrackerManager;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.render.HudRenderer;

public abstract class HudMod
implements IXaeroMinimap {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean FAIRPLAY = false;
    public static HudMod INSTANCE;
    protected PlatformContext platformContext;
    protected boolean loadedClient;
    protected boolean loadedServer;
    protected boolean firstStageLoaded;
    protected static final String versionID_minecraft = "1.21.8";
    private String versionID;
    private int newestUpdateID;
    private boolean isOutdated;
    private String latestVersion;
    private String latestVersionMD5;
    private ModSettings settings;
    private String message = "";
    private ControlsRegister controlsRegister;
    protected final ClientEvents events;
    protected final ModClientEvents modClientEvents;
    private GuiHelper guiHelper;
    private FieldValidatorHolder fieldValidators;
    private SupportMods supportMods;
    private SupportServerMods supportServerMods;
    private WidgetScreenHandler widgetScreenHandler;
    private WidgetLoadingHandler widgetLoader;
    private EntityRadarCategoryManager entityRadarCategoryManager;
    private ServerPlayerTickHandler serverPlayerTickHandler;
    private RenderedPlayerTrackerManager renderedPlayerTrackerManager;
    private PlayerTrackerMinimapElementRenderer trackedPlayerRenderer;
    private MinimapMessageHandler messageHandler;
    protected final CommonEvents commonEvents;
    protected final ModCommonEvents modCommonEvents;
    private CommonConfigIO commonConfigIO;
    private CommonConfig commonConfig;
    private ClientEventsListener clientEventsListener;
    protected Hud hud;
    protected HudRenderer hudRenderer;
    protected HudIO hudIO;
    private HudClientOnlyBase clientOnlyBase;
    private Minimap minimap;
    private File modJAR = null;
    private Path configFile;
    public Path waypointsFile;
    public Path minimapFolder;
    private XaeroMinimapServer minimapServer;

    @Override
    public boolean isLoadedClient() {
        return this.loadedClient;
    }

    @Override
    public boolean isLoadedServer() {
        return this.loadedServer;
    }

    protected HudMod() {
        INSTANCE = this;
        this.platformContext = this.createPlatformContext();
        this.renderedPlayerTrackerManager = RenderedPlayerTrackerManager.Builder.begin().build();
        this.commonEvents = this.platformContext.createCommonEvents(this);
        this.modCommonEvents = this.platformContext.createModCommonEvents(this);
        if (!Services.PLATFORM.isDedicatedServer()) {
            this.events = this.platformContext.createClientEvents(this);
            this.clientEventsListener = this.createForgeEventHandlerListener();
            this.modClientEvents = this.platformContext.createModClientEvents(this);
        } else {
            this.events = null;
            this.modClientEvents = null;
        }
        new CommonConfigInit().init(this, this.getCommonConfigFileName());
    }

    protected abstract PlatformContext createPlatformContext();

    protected abstract String getCommonConfigFileName();

    protected abstract ModSettings createModSettings();

    protected abstract ControlsRegister createControlsRegister();

    protected abstract GuiHelper createGuiHelper();

    protected abstract String getOldConfigFileName();

    protected abstract HudClientOnlyBase createClientOnly();

    protected abstract String getModName();

    protected abstract Logger getLogger();

    protected abstract ClientEventsListener createForgeEventHandlerListener();

    protected abstract String getConfigFileName();

    protected void loadClient() throws IOException {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 1/2", (Object)this.getModName());
        ModOptions.init(this);
        String modId = this.getModId();
        Path modFile = Services.PLATFORM.getModFile(modId);
        this.clientOnlyBase = this.createClientOnly();
        this.clientOnlyBase.preInit(modId, this);
        String fileName = modFile.getFileName().toString();
        if (fileName.endsWith(".jar")) {
            this.modJAR = modFile.toFile();
        }
        Path gameDir = Services.PLATFORM.getGameDir();
        Path config = Services.PLATFORM.getConfigDir();
        this.waypointsFile = config.resolve("xaerowaypoints.txt");
        Path wrongWaypointsFolder3 = config.resolve("XaeroWaypoints");
        Path wrongWaypointsFolder2 = this.modJAR != null ? this.modJAR.toPath().getParent().resolve("XaeroWaypoints") : config.getParent().resolve("mods").resolve("XaeroWaypoints");
        Path wrongWaypointsFolder4 = new File(config.toFile().getCanonicalPath()).toPath().getParent().resolve("XaeroWaypoints");
        Path wrongWaypointsFolder5 = config.getParent().resolve("XaeroWaypoints");
        Path preMinimapWorldsFolder = gameDir.resolve("XaeroWaypoints");
        Path xaeroFolder = gameDir.resolve("xaero");
        if (!Files.exists(xaeroFolder, new LinkOption[0])) {
            Files.createDirectories(xaeroFolder, new FileAttribute[0]);
        }
        this.minimapFolder = xaeroFolder.resolve("minimap");
        if (Files.exists(preMinimapWorldsFolder, new LinkOption[0]) && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(preMinimapWorldsFolder, this.minimapFolder, new CopyOption[0]);
        }
        if (wrongWaypointsFile.exists() && !Files.exists(this.waypointsFile, new LinkOption[0])) {
            Files.move(wrongWaypointsFile.toPath(), this.waypointsFile, new CopyOption[0]);
        }
        if (wrongWaypointsFolder.exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder.toPath(), this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder2.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder2, this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder3.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder3, this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder4.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder4, this.minimapFolder, new CopyOption[0]);
        } else if (wrongWaypointsFolder5.toFile().exists() && !Files.exists(this.minimapFolder, new LinkOption[0])) {
            Files.move(wrongWaypointsFolder5, this.minimapFolder, new CopyOption[0]);
        }
        Path waypointsFolderBackup = gameDir.resolve("XaeroWaypoints_BACKUP240807");
        if (!Files.exists(waypointsFolderBackup, new LinkOption[0]) && Files.exists(this.minimapFolder, new LinkOption[0])) {
            LOGGER.info("Backing up Xaero's minimap data...");
            SimpleBackup.copyDirectoryWithContents(this.minimapFolder, waypointsFolderBackup, 32, new CopyOption[0]);
            LOGGER.info("Done backing up Xaero's minimap data!");
        }
        this.configFile = config.resolve(this.getConfigFileName());
        Path oldConfigFile = gameDir.resolve("config").resolve(this.getOldConfigFileName());
        if (Files.exists(oldConfigFile, new LinkOption[0]) && !Files.exists(this.configFile, new LinkOption[0])) {
            Files.move(oldConfigFile, this.configFile, new CopyOption[0]);
        }
        this.widgetScreenHandler = new WidgetScreenHandler();
        this.widgetLoader = new WidgetLoadingHandler(this.widgetScreenHandler);
        this.settings = this.createModSettings();
        this.ensureControlsRegister();
        this.guiHelper = this.createGuiHelper();
        this.fieldValidators = new FieldValidatorHolder(new NumericFieldValidator(), new WaypointCoordinateFieldValidator());
        this.minimap = new Minimap(this);
        Path old_optionsFile = gameDir.resolve(this.getOldConfigFileName());
        if (Files.exists(old_optionsFile, new LinkOption[0]) && !Files.exists(this.configFile, new LinkOption[0])) {
            Path configFileParent = this.configFile.getParent();
            if (!Files.exists(configFileParent, new LinkOption[0])) {
                Files.createDirectories(configFileParent, new FileAttribute[0]);
            }
            Files.move(old_optionsFile, this.configFile, new CopyOption[0]);
        }
        if (Files.exists(old_waypointsFile, new LinkOption[0]) && !Files.exists(this.waypointsFile, new LinkOption[0])) {
            Path waypointFileParent = this.waypointsFile.getParent();
            if (!Files.exists(waypointFileParent, new LinkOption[0])) {
                Files.createDirectories(waypointFileParent, new FileAttribute[0]);
            }
            Files.move(old_waypointsFile, this.waypointsFile, new CopyOption[0]);
        }
        this.trackedPlayerRenderer = PlayerTrackerMinimapElementRenderer.Builder.begin(this).build();
        XaeroMinimapCore.modMain = this;
        this.firstStageLoaded = true;
    }

    public void loadLater() {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 2/2", (Object)this.getModName());
        try {
            PatreonMod patreonEntry;
            this.clientOnlyBase.preLoadLater(this);
            this.controlsRegister.onStage2();
            this.settings.loadSettings();
            this.entityRadarCategoryManager = EntityRadarCategoryManager.Builder.begin().build();
            this.entityRadarCategoryManager.init();
            Patreon.checkPatreon(this);
            Internet.checkModVersion(this);
            if (this.isOutdated && (patreonEntry = this.getPatreon()) != null) {
                patreonEntry.modJar = this.modJAR;
                patreonEntry.currentVersion = this.getVersionID();
                patreonEntry.latestVersion = this.latestVersion;
                patreonEntry.md5 = this.latestVersionMD5;
                patreonEntry.onVersionIgnore = () -> {
                    ModSettings.ignoreUpdate = this.newestUpdateID;
                    try {
                        this.getSettings().saveSettings();
                    }
                    catch (IOException e) {
                        LOGGER.error("suppressed exception", (Throwable)e);
                    }
                };
                Patreon.addOutdatedMod(patreonEntry);
            }
            this.renderedPlayerTrackerManager.register("minimap_synced", new SyncedRenderedPlayerTracker());
            this.supportMods = this.platformContext.createSupportMods(this);
            this.getMinimap().getOverMapRendererHandler().add(this.trackedPlayerRenderer);
            this.getMinimap().getWorldRendererHandler().add(this.trackedPlayerRenderer);
        }
        catch (Throwable e) {
            LOGGER.error("error", e);
            this.minimap.setCrashedWith(e);
        }
        this.loadedClient = true;
    }

    public void loadServer() {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 1/2 (Server)", (Object)this.getModName());
        this.minimapServer = new XaeroMinimapServer(this);
        this.minimapServer.load();
        this.firstStageLoaded = true;
    }

    protected void loadCommon() {
        this.versionID = "1.21.8_" + this.platformContext.getModInfoVersion();
        XaeroMinimapServerCore.modMain = this;
        this.messageHandler = this.platformContext.createMessageHandler(this);
        this.supportServerMods = new SupportServerMods();
        new HudCommonBase().setup(this);
    }

    protected void loadLaterServer() {
        Logger LOGGER = this.getLogger();
        LOGGER.info("Loading {} - Stage 2/2 (Server)", (Object)this.getModName());
        this.minimapServer.loadLater();
        this.loadedServer = true;
    }

    @Override
    public Path getConfigFile() {
        return this.configFile;
    }

    @Override
    public File getModJAR() {
        return this.modJAR;
    }

    @Override
    public ModSettings getSettings() {
        return this.settings;
    }

    @Override
    public void setSettings(ModSettings minimapSettings) {
        this.settings = minimapSettings;
    }

    @Override
    public void resetSettings() throws IOException {
        this.settings = this.createModSettings();
        this.settings.loadDefaultSettings();
        this.getMinimap().getInfoDisplays().getManager().reset();
        ObjectCategoryDefaultSettingsSetter defaultSettings = ObjectCategoryDefaultSettingsSetter.Builder.begin().setSettings(EntityRadarCategorySettings.SETTINGS).build();
        defaultSettings.setDefaultsFor(this.getEntityRadarCategoryManager().getRootCategory(), false);
        this.getEntityRadarCategoryManager().save();
    }

    @Override
    public boolean isOutdated() {
        return this.isOutdated;
    }

    @Override
    public void setOutdated(boolean value) {
        this.isOutdated = value;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String string) {
        this.message = string;
    }

    @Override
    public String getLatestVersion() {
        return this.latestVersion;
    }

    @Override
    public void setLatestVersion(String string) {
        this.latestVersion = string;
    }

    @Override
    public int getNewestUpdateID() {
        return this.newestUpdateID;
    }

    @Override
    public void setNewestUpdateID(int parseInt) {
        this.newestUpdateID = parseInt;
    }

    @Override
    public PatreonMod getPatreon() {
        return (PatreonMod)Patreon.getMods().get(this.getFileLayoutID());
    }

    @Override
    public Path getWaypointsFile() {
        return this.waypointsFile;
    }

    @Override
    public Path getMinimapFolder() {
        return this.minimapFolder;
    }

    @Override
    public WidgetScreenHandler getWidgetScreenHandler() {
        return this.widgetScreenHandler;
    }

    @Override
    public WidgetLoadingHandler getWidgetLoader() {
        return this.widgetLoader;
    }

    @Override
    public SupportMods getSupportMods() {
        return this.supportMods;
    }

    @Override
    public ModClientEvents getModEvents() {
        return this.modClientEvents;
    }

    @Override
    public GuiHelper getGuiHelper() {
        return this.guiHelper;
    }

    @Override
    public FieldValidatorHolder getFieldValidators() {
        return this.fieldValidators;
    }

    @Override
    public ControlsRegister getControlsRegister() {
        return this.controlsRegister;
    }

    @Override
    public ClientEvents getEvents() {
        return this.events;
    }

    @Override
    public void setLatestVersionMD5(String string) {
        this.latestVersionMD5 = string;
    }

    @Override
    public String getLatestVersionMD5() {
        return this.latestVersionMD5;
    }

    @Override
    public boolean isStandalone() {
        return false;
    }

    @Override
    public EntityRadarCategoryManager getEntityRadarCategoryManager() {
        return this.entityRadarCategoryManager;
    }

    @Override
    public boolean isFairPlay() {
        if (this.loadedClient) {
            XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
            return session != null && session.getMinimapProcessor().getForcedFairPlay();
        }
        return false;
    }

    @Override
    public PlayerTrackerMinimapElementRenderer getTrackedPlayerRenderer() {
        return this.trackedPlayerRenderer;
    }

    @Override
    public RenderedPlayerTrackerManager getRenderedPlayerTrackerManager() {
        return this.renderedPlayerTrackerManager;
    }

    @Override
    public ServerPlayerTickHandler getServerPlayerTickHandler() {
        return this.serverPlayerTickHandler;
    }

    @Override
    public void setServerPlayerTickHandler(ServerPlayerTickHandler serverPlayerTickHandler) {
        this.serverPlayerTickHandler = serverPlayerTickHandler;
    }

    @Override
    public MinimapMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public CommonEvents getCommonEvents() {
        return this.commonEvents;
    }

    @Override
    public SupportServerMods getSupportServerMods() {
        return this.supportServerMods;
    }

    @Override
    public void setCommonConfigIO(CommonConfigIO serverConfigIO) {
        this.commonConfigIO = serverConfigIO;
    }

    @Override
    public void setCommonConfig(CommonConfig serverConfig) {
        this.commonConfig = serverConfig;
    }

    @Override
    public CommonConfigIO getCommonConfigIO() {
        return this.commonConfigIO;
    }

    @Override
    public CommonConfig getCommonConfig() {
        return this.commonConfig;
    }

    @Override
    public ClientEventsListener getClientEventsListener() {
        return this.clientEventsListener;
    }

    @Override
    public PlatformContext getPlatformContext() {
        return this.platformContext;
    }

    @Override
    public void ensureControlsRegister() {
        if (this.controlsRegister == null) {
            this.controlsRegister = this.createControlsRegister();
        }
    }

    @Override
    public ModClientEvents getModClientEvents() {
        return this.modClientEvents;
    }

    @Override
    public ModCommonEvents getModCommonEvents() {
        return this.modCommonEvents;
    }

    @Override
    public void tryLoadLater() {
    }

    @Override
    public void tryLoadLaterServer() {
    }

    @Override
    public String getVersionID() {
        return this.versionID;
    }

    @Override
    public Hud getHud() {
        return this.hud;
    }

    @Override
    public HudRenderer getHudRenderer() {
        return this.hudRenderer;
    }

    @Override
    public HudIO getHudIO() {
        return this.hudIO;
    }

    @Override
    public Minimap getMinimap() {
        return this.minimap;
    }

    @Override
    public boolean isFirstStageLoaded() {
        return this.firstStageLoaded;
    }

    public KeyMappingControllerManager getKeyMappingControllers() {
        return this.controlsRegister.getKeyMappingControllers();
    }
}

