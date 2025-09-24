/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_10366
 *  net.minecraft.class_1044
 *  net.minecraft.class_1074
 *  net.minecraft.class_10799
 *  net.minecraft.class_124
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2378
 *  net.minecraft.class_2561
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_2874
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_339
 *  net.minecraft.class_342
 *  net.minecraft.class_364
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_4068
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5321
 *  net.minecraft.class_6379
 *  net.minecraft.class_6599
 *  org.joml.Matrix4f
 *  org.joml.Vector3fc
 */
package xaero.map.gui;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.class_10366;
import net.minecraft.class_1044;
import net.minecraft.class_1074;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_2874;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_3675;
import net.minecraft.class_4068;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5321;
import net.minecraft.class_6379;
import net.minecraft.class_6599;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.animation.Animation;
import xaero.map.animation.SinAnimation;
import xaero.map.animation.SlowingAnimation;
import xaero.map.controls.ControlsHandler;
import xaero.map.controls.ControlsRegister;
import xaero.map.core.IWorldMapMinecraftClient;
import xaero.map.effects.Effects;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.ImprovedFramebuffer;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.OpenGlHelper;
import xaero.map.graphics.TextureUtils;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.graphics.shader.WorldMapShaderHelper;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ExportScreen;
import xaero.map.gui.GuiCaveModeOptions;
import xaero.map.gui.GuiMapSwitching;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.gui.GuiWorldMapSettings;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.MapMouseButtonPress;
import xaero.map.gui.MapTileSelection;
import xaero.map.gui.ScreenBase;
import xaero.map.gui.dropdown.DropDownWidget;
import xaero.map.gui.dropdown.rightclick.GuiRightClickMenu;
import xaero.map.gui.dropdown.rightclick.RightClickOption;
import xaero.map.misc.Misc;
import xaero.map.misc.OptimizedMath;
import xaero.map.mods.SupportMods;
import xaero.map.mods.gui.Waypoint;
import xaero.map.radar.tracker.PlayerTeleporter;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.region.BranchLeveledRegion;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapBlock;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.RegionTexture;
import xaero.map.render.util.ImmediateRenderUtil;
import xaero.map.settings.ModOptions;
import xaero.map.settings.ModSettings;
import xaero.map.teleport.MapTeleporter;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class GuiMap
extends ScreenBase
implements IRightClickableElement {
    private static final class_2561 FULL_RELOAD_IN_PROGRESS = class_2561.method_43471((String)"gui.xaero_full_reload_in_progress");
    private static final class_2561 UNKNOWN_DIMENSION_TYPE1 = class_2561.method_43471((String)"gui.xaero_unknown_dimension_type1");
    private static final class_2561 UNKNOWN_DIMENSION_TYPE2 = class_2561.method_43471((String)"gui.xaero_unknown_dimension_type2");
    private static final double ZOOM_STEP = 1.2;
    private static final int white = -1;
    private static final int black = -16777216;
    private static int lastAmountOfRegionsViewed = 1;
    private long loadingAnimationStart;
    private class_1297 player;
    private double screenScale = 0.0;
    private int mouseDownPosX = -1;
    private int mouseDownPosY = -1;
    private double mouseDownCameraX = -1.0;
    private double mouseDownCameraZ = -1.0;
    private int mouseCheckPosX = -1;
    private int mouseCheckPosY = -1;
    private long mouseCheckTimeNano = -1L;
    private int prevMouseCheckPosX = -1;
    private int prevMouseCheckPosY = -1;
    private long prevMouseCheckTimeNano = -1L;
    private double cameraX = 0.0;
    private double cameraZ = 0.0;
    private boolean shouldResetCameraPos;
    private int[] cameraDestination = null;
    private SlowingAnimation cameraDestinationAnimX = null;
    private SlowingAnimation cameraDestinationAnimZ = null;
    private double scale;
    private double userScale;
    private static double destScale = 3.0;
    private boolean pauseZoomKeys;
    private int lastZoomMethod;
    private double prevPlayerDimDiv;
    private HoveredMapElementHolder<?, ?> viewed = null;
    private boolean viewedInList;
    private HoveredMapElementHolder<?, ?> viewedOnMousePress = null;
    private boolean overWaypointsMenu;
    private Animation zoomAnim;
    public boolean waypointMenu = false;
    private boolean overPlayersMenu;
    public boolean playersMenu = false;
    private static ImprovedFramebuffer primaryScaleFBO = null;
    private float[] colourBuffer = new float[4];
    private ArrayList<MapRegion> regionBuffer = new ArrayList();
    private ArrayList<BranchLeveledRegion> branchRegionBuffer = new ArrayList();
    private boolean prevWaitingForBranchCache = true;
    private boolean prevLoadingLeaves = true;
    private class_5321<class_1937> lastNonNullViewedDimensionId;
    private class_5321<class_1937> lastViewedDimensionId;
    private String lastViewedMultiworldId;
    private int mouseBlockPosX;
    private int mouseBlockPosY;
    private int mouseBlockPosZ;
    private class_5321<class_1937> mouseBlockDim;
    private double mouseBlockCoordinateScale = 1.0;
    private long lastStartTime;
    private final GuiMapSwitching mapSwitchingGui;
    private MapMouseButtonPress leftMouseButton;
    private MapMouseButtonPress rightMouseButton;
    private MapProcessor mapProcessor;
    private MapDimension futureDimension;
    public boolean noUploadingLimits;
    private boolean[] waitingForBranchCache = new boolean[1];
    private class_4185 settingsButton;
    private class_4185 exportButton;
    private class_4185 waypointsButton;
    private class_4185 playersButton;
    private class_4185 radarButton;
    private class_4185 claimsButton;
    private class_4185 zoomInButton;
    private class_4185 zoomOutButton;
    private class_4185 keybindingsButton;
    private class_4185 caveModeButton;
    private class_4185 dimensionToggleButton;
    private class_4185 buttonPressed;
    private GuiRightClickMenu rightClickMenu;
    private int rightClickX;
    private int rightClickY;
    private int rightClickZ;
    private class_5321<class_1937> rightClickDim;
    private double rightClickCoordinateScale;
    private boolean lastFrameRenderedRootTextures;
    private MapTileSelection mapTileSelection;
    private boolean tabPressed;
    private GuiCaveModeOptions caveModeOptions;
    private static final Matrix4f identityMatrix = new Matrix4f();

    public GuiMap(class_437 parent, class_437 escape, MapProcessor mapProcessor, class_1297 player) {
        super(parent, escape, (class_2561)class_2561.method_43471((String)"gui.xaero_world_map_screen"));
        this.player = player;
        this.shouldResetCameraPos = true;
        this.leftMouseButton = new MapMouseButtonPress();
        this.rightMouseButton = new MapMouseButtonPress();
        this.mapSwitchingGui = new GuiMapSwitching(mapProcessor);
        this.userScale = destScale * (double)(WorldMap.settings.openMapAnimation ? 1.5f : 1.0f);
        this.zoomAnim = new SlowingAnimation(this.userScale, destScale, 0.88, destScale * 0.001);
        this.mapProcessor = mapProcessor;
        this.caveModeOptions = new GuiCaveModeOptions();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onMapConstruct();
        }
    }

    private double getScaleMultiplier(int screenShortSide) {
        return screenShortSide <= 1080 ? 1.0 : (double)screenShortSide / 1080.0;
    }

    public <T extends class_364 & class_4068> T method_37063(T guiEventListener) {
        return (T)super.method_37063(guiEventListener);
    }

    public <T extends class_364 & class_4068> T addButton(T guiEventListener) {
        return this.method_37063(guiEventListener);
    }

    @Override
    public <T extends class_364 & class_6379> T method_25429(T guiEventListener) {
        return super.method_25429(guiEventListener);
    }

    @Override
    public void method_25426() {
        super.method_25426();
        MapWorld mapWorld = this.mapProcessor.getMapWorld();
        this.futureDimension = mapWorld == null || mapWorld.getFutureDimensionId() == null ? null : mapWorld.getFutureDimension();
        this.tabPressed = false;
        boolean waypointsEnabled = SupportMods.minimap() && WorldMap.settings.waypoints;
        this.waypointMenu = this.waypointMenu && waypointsEnabled;
        this.mapSwitchingGui.init(this, this.field_22787, this.field_22789, this.field_22790);
        CursorBox caveModeButtonTooltip = new CursorBox((class_2561)class_2561.method_43471((String)(WorldMap.settings.isCaveMapsAllowed() ? "gui.xaero_box_cave_mode" : "gui.xaero_box_cave_mode_not_allowed")));
        this.caveModeButton = new GuiTexturedButton(0, this.field_22790 - 40, 20, 20, 229, 64, 16, 16, WorldMap.guiTextures, this::onCaveModeButton, () -> caveModeButtonTooltip, 256, 256);
        this.caveModeButton.field_22763 = WorldMap.settings.isCaveMapsAllowed();
        this.addButton(this.caveModeButton);
        this.caveModeOptions.onInit(this, this.mapProcessor);
        CursorBox dimensionToggleButtonTooltip = new CursorBox((class_2561)class_2561.method_43469((String)"gui.xaero_dimension_toggle_button", (Object[])new Object[]{Misc.getKeyName(ControlsRegister.keyToggleDimension)}));
        this.dimensionToggleButton = new GuiTexturedButton(0, this.field_22790 - 60, 20, 20, 197, 80, 16, 16, WorldMap.guiTextures, this::onDimensionToggleButton, () -> dimensionToggleButtonTooltip, 256, 256);
        this.addButton(this.dimensionToggleButton);
        this.loadingAnimationStart = System.currentTimeMillis();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.requestWaypointsRefresh();
        }
        this.screenScale = class_310.method_1551().method_22683().method_4495();
        this.pauseZoomKeys = false;
        CursorBox openSettingsTooltip = new CursorBox((class_2561)class_2561.method_43469((String)"gui.xaero_box_open_settings", (Object[])new Object[]{Misc.getKeyName(ControlsRegister.keyOpenSettings)}));
        this.settingsButton = new GuiTexturedButton(0, 0, 30, 30, 113, 0, 20, 20, WorldMap.guiTextures, this::onSettingsButton, () -> openSettingsTooltip, 256, 256);
        this.addButton(this.settingsButton);
        CursorBox waypointsTooltip = waypointsEnabled ? new CursorBox(this.waypointMenu ? "gui.xaero_box_close_waypoints" : "gui.xaero_box_open_waypoints") : new CursorBox(!SupportMods.minimap() ? "gui.xaero_box_waypoints_minimap_required" : "gui.xaero_box_waypoints_disabled");
        CursorBox playersTooltip = new CursorBox(this.playersMenu ? "gui.xaero_box_close_players" : "gui.xaero_box_open_players");
        CursorBox claimsTooltip = SupportMods.pac() ? new CursorBox((class_2561)class_2561.method_43469((String)(WorldMap.settings.displayClaims ? "gui.xaero_box_pac_displaying_claims" : "gui.xaero_box_pac_not_displaying_claims"), (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(SupportMods.xaeroPac.getPacClaimsKeyBinding())).method_27692(class_124.field_1077)})) : new CursorBox((class_2561)class_2561.method_43471((String)"gui.xaero_box_claims_pac_required"));
        this.waypointsButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 20, 20, 20, 213, 0, 16, 16, WorldMap.guiTextures, this::onWaypointsButton, () -> waypointsTooltip, 256, 256);
        this.addButton(this.waypointsButton);
        this.waypointsButton.field_22763 = waypointsEnabled;
        this.playersButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 40, 20, 20, 197, 32, 16, 16, WorldMap.guiTextures, this::onPlayersButton, () -> playersTooltip, 256, 256);
        this.addButton(this.playersButton);
        CursorBox radarButtonTooltip = new CursorBox((class_2561)class_2561.method_43469((String)(WorldMap.settings.minimapRadar ? "gui.xaero_box_minimap_radar" : "gui.xaero_box_no_minimap_radar"), (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(SupportMods.minimap() ? SupportMods.xaeroMinimap.getToggleRadarKey() : null)).method_27692(class_124.field_1077)}));
        this.radarButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 60, 20, 20, WorldMap.settings.minimapRadar ? 213 : 229, 32, 16, 16, WorldMap.guiTextures, this::onRadarButton, () -> radarButtonTooltip, 256, 256);
        this.addButton(this.radarButton);
        this.getRadarButton().field_22763 = SupportMods.minimap();
        this.claimsButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 80, 20, 20, WorldMap.settings.displayClaims ? 197 : 213, 64, 16, 16, WorldMap.guiTextures, this::onClaimsButton, () -> claimsTooltip, 256, 256);
        this.addButton(this.claimsButton);
        this.claimsButton.field_22763 = SupportMods.pac();
        CursorBox exportButtonTooltip = new CursorBox("gui.xaero_box_export");
        this.exportButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 100, 20, 20, 133, 0, 16, 16, WorldMap.guiTextures, this::onExportButton, () -> exportButtonTooltip, 256, 256);
        this.addButton(this.exportButton);
        CursorBox controlsButtonTooltip = new CursorBox(class_1074.method_4662((String)"gui.xaero_box_controls", (Object[])new Object[]{(SupportMods.minimap() ? SupportMods.xaeroMinimap.getControlsTooltip() : "") + (SupportMods.pac() ? SupportMods.xaeroPac.getControlsTooltip() : "")}));
        controlsButtonTooltip.setStartWidth(400);
        this.keybindingsButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 120, 20, 20, 197, 0, 16, 16, WorldMap.guiTextures, this::onKeybindingsButton, () -> controlsButtonTooltip, 256, 256);
        this.addButton(this.keybindingsButton);
        CursorBox zoomInButtonTooltip = new CursorBox((class_2561)class_2561.method_43469((String)"gui.xaero_box_zoom_in", (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(ControlsRegister.keyZoomIn)).method_27692(class_124.field_1077)}));
        this.zoomInButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 160, 20, 20, 165, 0, 16, 16, WorldMap.guiTextures, this::onZoomInButton, () -> zoomInButtonTooltip, 256, 256);
        CursorBox zoomOutButtonTooltip = new CursorBox((class_2561)class_2561.method_43469((String)"gui.xaero_box_zoom_out", (Object[])new Object[]{class_2561.method_43470((String)Misc.getKeyName(ControlsRegister.keyZoomOut)).method_27692(class_124.field_1077)}));
        this.zoomOutButton = new GuiTexturedButton(this.field_22789 - 20, this.field_22790 - 140, 20, 20, 181, 0, 16, 16, WorldMap.guiTextures, this::onZoomOutButton, () -> zoomOutButtonTooltip, 256, 256);
        if (WorldMap.settings.zoomButtons) {
            this.addButton(this.zoomOutButton);
            this.addButton(this.zoomInButton);
        }
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
            this.rightClickMenu = null;
        }
        if (SupportMods.minimap() && this.waypointMenu) {
            SupportMods.xaeroMinimap.onMapInit(this, this.field_22787, this.field_22789, this.field_22790);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMapInit(this, this.field_22787, this.field_22789, this.field_22790);
        }
    }

    protected void method_56131() {
    }

    private void onCaveModeButton(class_4185 b) {
        this.caveModeOptions.toggle(this);
        this.method_25395((class_364)this.caveModeButton);
    }

    private void onDimensionToggleButton(class_4185 b) {
        this.mapProcessor.getMapWorld().toggleDimension(!GuiMap.method_25442());
        String messageType = this.mapProcessor.getMapWorld().getCustomDimensionId() == null ? "gui.xaero_switched_to_current_dimension" : "gui.xaero_switched_to_dimension";
        class_2960 messageDimLoc = this.mapProcessor.getMapWorld().getFutureDimensionId() == null ? null : this.mapProcessor.getMapWorld().getFutureDimensionId().method_29177();
        this.mapProcessor.getMessageBox().addMessage((class_2561)class_2561.method_43469((String)messageType, (Object[])new Object[]{messageDimLoc.toString()}));
        this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        this.method_25395((class_364)this.dimensionToggleButton);
    }

    private void onSettingsButton(class_4185 b) {
        this.field_22787.method_1507((class_437)new GuiWorldMapSettings(this, this));
    }

    private void onKeybindingsButton(class_4185 b) {
        this.field_22787.method_1507((class_437)new class_6599((class_437)this, this.field_22787.field_1690));
    }

    private void onExportButton(class_4185 b) {
        this.field_22787.method_1507((class_437)new ExportScreen(this, this, this.mapProcessor, this.mapTileSelection));
    }

    private void toggleWaypointMenu() {
        if (this.playersMenu) {
            this.togglePlayerMenu();
        }
        boolean bl = this.waypointMenu = !this.waypointMenu;
        if (!this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().onMenuClosed();
            this.unfocusAll();
        }
    }

    private void togglePlayerMenu() {
        if (this.waypointMenu) {
            this.toggleWaypointMenu();
        }
        boolean bl = this.playersMenu = !this.playersMenu;
        if (!this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMenuClosed();
            this.unfocusAll();
        }
    }

    private void onPlayersButton(class_4185 b) {
        this.togglePlayerMenu();
        this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        this.method_25395((class_364)this.playersButton);
    }

    private void onClaimsButton(class_4185 b) {
        WorldMap.settings.setOptionValue(ModOptions.PAC_CLAIMS, (Boolean)WorldMap.settings.getOptionValue(ModOptions.PAC_CLAIMS) == false);
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        this.method_25395((class_364)this.claimsButton);
    }

    private void onWaypointsButton(class_4185 b) {
        this.toggleWaypointMenu();
        this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        this.method_25395((class_364)this.waypointsButton);
    }

    public void onRadarButton(class_4185 b) {
        WorldMap.settings.minimapRadar = !WorldMap.settings.minimapRadar;
        try {
            WorldMap.settings.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        this.method_25395((class_364)this.radarButton);
    }

    private void onZoomInButton(class_4185 b) {
        this.buttonPressed = this.buttonPressed == null ? b : null;
    }

    private void onZoomOutButton(class_4185 b) {
        this.buttonPressed = this.buttonPressed == null ? b : null;
    }

    @Override
    public boolean method_25402(double par1, double par2, int par3) {
        boolean toReturn = super.method_25402(par1, par2, par3);
        if (!toReturn) {
            if (par3 == 0) {
                this.leftMouseButton.clicked = true;
                this.leftMouseButton.isDown = true;
                this.leftMouseButton.pressedAtX = (int)Misc.getMouseX(this.field_22787, SupportMods.vivecraft);
                this.leftMouseButton.pressedAtY = (int)Misc.getMouseY(this.field_22787, SupportMods.vivecraft);
            } else if (par3 == 1) {
                this.rightMouseButton.clicked = true;
                this.rightMouseButton.isDown = true;
                this.rightMouseButton.pressedAtX = (int)Misc.getMouseX(this.field_22787, SupportMods.vivecraft);
                this.rightMouseButton.pressedAtY = (int)Misc.getMouseY(this.field_22787, SupportMods.vivecraft);
                this.viewedOnMousePress = this.viewed;
                this.rightClickX = this.mouseBlockPosX;
                this.rightClickY = this.mouseBlockPosY;
                this.rightClickZ = this.mouseBlockPosZ;
                this.rightClickDim = this.mouseBlockDim;
                this.rightClickCoordinateScale = this.mouseBlockCoordinateScale;
                if (SupportMods.minimap()) {
                    SupportMods.xaeroMinimap.onRightClick();
                }
                if (this.viewedOnMousePress == null || !this.viewedOnMousePress.isRightClickValid()) {
                    this.mapTileSelection = new MapTileSelection(this.rightClickX >> 4, this.rightClickZ >> 4);
                }
            } else {
                toReturn = this.onInputPress(class_3675.class_307.field_1672, par3);
            }
            if (!toReturn && this.caveModeOptions.isEnabled()) {
                this.caveModeOptions.toggle(this);
                toReturn = true;
            }
        }
        return toReturn;
    }

    @Override
    public boolean method_25406(double par1, double par2, int par3) {
        boolean toReturn;
        this.buttonPressed = null;
        int mouseX = (int)Misc.getMouseX(this.field_22787, SupportMods.vivecraft);
        int mouseY = (int)Misc.getMouseY(this.field_22787, SupportMods.vivecraft);
        if (this.leftMouseButton.isDown && par3 == 0) {
            this.leftMouseButton.isDown = false;
            if (Math.abs(this.leftMouseButton.pressedAtX - mouseX) < 5 && Math.abs(this.leftMouseButton.pressedAtY - mouseY) < 5) {
                this.mapClicked(0, this.leftMouseButton.pressedAtX, this.leftMouseButton.pressedAtY);
            }
            this.leftMouseButton.pressedAtX = -1;
            this.leftMouseButton.pressedAtY = -1;
        }
        if (this.rightMouseButton.isDown && par3 == 1) {
            this.rightMouseButton.isDown = false;
            this.mapClicked(1, mouseX, mouseY);
            this.rightMouseButton.pressedAtX = -1;
            this.rightMouseButton.pressedAtY = -1;
        }
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.onMapMouseRelease(par1, par2, par3);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.onMapMouseRelease(par1, par2, par3);
        }
        if (!(toReturn = super.method_25406(par1, par2, par3))) {
            toReturn = this.onInputRelease(class_3675.class_307.field_1672, par3);
        }
        return toReturn;
    }

    @Override
    public boolean method_25401(double par1, double par2, double g, double wheel) {
        int direction;
        int n = direction = wheel > 0.0 ? 1 : -1;
        if (this.waypointMenu && this.overWaypointsMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().mouseScrolled(direction);
        } else if (this.playersMenu && this.overPlayersMenu) {
            WorldMap.trackedPlayerMenuRenderer.mouseScrolled(direction);
        } else {
            this.changeZoom(wheel, 0);
        }
        return super.method_25401(par1, par2, g, wheel);
    }

    private void changeZoom(double factor, int zoomMethod) {
        this.closeDropdowns();
        this.lastZoomMethod = zoomMethod;
        this.cameraDestinationAnimX = null;
        this.cameraDestinationAnimZ = null;
        if (GuiMap.method_25441()) {
            double destScaleBefore = destScale;
            if (destScale >= 1.0) {
                destScale = factor > 0.0 ? Math.ceil(destScale) : Math.floor(destScale);
                if (destScaleBefore == destScale) {
                    destScale += factor > 0.0 ? 1.0 : -1.0;
                }
                if (destScale == 0.0) {
                    destScale = 0.5;
                }
            } else {
                double reversedScale = 1.0 / destScale;
                double log2 = Math.log(reversedScale) / Math.log(2.0);
                log2 = factor > 0.0 ? Math.floor(log2) : Math.ceil(log2);
                destScale = 1.0 / Math.pow(2.0, log2);
                if (destScaleBefore == destScale) {
                    destScale = 1.0 / Math.pow(2.0, log2 + (double)(factor > 0.0 ? -1 : 1));
                }
            }
        } else {
            destScale *= Math.pow(1.2, factor);
        }
        if (destScale < 0.0625) {
            destScale = 0.0625;
        } else if (destScale > 50.0) {
            destScale = 50.0;
        }
    }

    public void method_25432() {
        super.method_25432();
        this.leftMouseButton.isDown = false;
        this.rightMouseButton.isDown = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void method_25394(class_332 guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks) {
        int direction;
        MapDimension currentFutureDim;
        OpenGlHelper.clearErrors(false, "GuiMap.render");
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        class_310 mc = class_310.method_1551();
        long startTime = System.currentTimeMillis();
        MapDimension mapDimension = currentFutureDim = !this.mapProcessor.isMapWorldUsable() ? null : this.mapProcessor.getMapWorld().getFutureDimension();
        if (currentFutureDim != this.futureDimension) {
            this.method_25423(this.field_22787, this.field_22789, this.field_22790);
        }
        double playerDimDiv = this.prevPlayerDimDiv;
        Object object = this.mapProcessor.renderThreadPauseSync;
        synchronized (object) {
            class_2378<class_2874> dimTypes;
            if (!this.mapProcessor.isRenderingPaused() && (dimTypes = this.mapProcessor.getWorldDimensionTypeRegistry()) != null) {
                playerDimDiv = this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(dimTypes, this.player.method_37908().method_8597());
            }
        }
        double scaledPlayerX = this.player.method_23317() / playerDimDiv;
        double scaledPlayerZ = this.player.method_23321() / playerDimDiv;
        if (this.shouldResetCameraPos) {
            this.cameraX = (float)scaledPlayerX;
            this.cameraZ = (float)scaledPlayerZ;
            this.shouldResetCameraPos = false;
        } else if (this.prevPlayerDimDiv != 0.0 && playerDimDiv != this.prevPlayerDimDiv) {
            double oldScaledPlayerX = this.player.method_23317() / this.prevPlayerDimDiv;
            double oldScaledPlayerZ = this.player.method_23321() / this.prevPlayerDimDiv;
            this.cameraX = this.cameraX - oldScaledPlayerX + scaledPlayerX;
            this.cameraZ = this.cameraZ - oldScaledPlayerZ + scaledPlayerZ;
            this.cameraDestinationAnimX = null;
            this.cameraDestinationAnimZ = null;
            this.cameraDestination = null;
        }
        this.prevPlayerDimDiv = playerDimDiv;
        double cameraXBefore = this.cameraX;
        double cameraZBefore = this.cameraZ;
        double scaleBefore = this.scale;
        this.mapSwitchingGui.preMapRender(this, this.field_22787, this.field_22789, this.field_22790);
        long passed = this.lastStartTime == 0L ? 16L : startTime - this.lastStartTime;
        double passedScrolls = (float)passed / 64.0f;
        int n = this.buttonPressed == this.zoomInButton || ControlsHandler.isDown(ControlsRegister.keyZoomIn) ? 1 : (direction = this.buttonPressed == this.zoomOutButton || ControlsHandler.isDown(ControlsRegister.keyZoomOut) ? -1 : 0);
        if (direction != 0) {
            boolean ctrlKey = GuiMap.method_25441();
            if (!ctrlKey || !this.pauseZoomKeys) {
                this.changeZoom((double)direction * passedScrolls, this.buttonPressed == this.zoomInButton || this.buttonPressed == this.zoomOutButton ? 2 : 1);
                if (ctrlKey) {
                    this.pauseZoomKeys = true;
                }
            }
        } else {
            this.pauseZoomKeys = false;
        }
        this.lastStartTime = startTime;
        if (this.cameraDestination != null) {
            this.cameraDestinationAnimX = new SlowingAnimation(this.cameraX, this.cameraDestination[0], 0.9, 0.01);
            this.cameraDestinationAnimZ = new SlowingAnimation(this.cameraZ, this.cameraDestination[1], 0.9, 0.01);
            this.cameraDestination = null;
        }
        if (this.cameraDestinationAnimX != null) {
            this.cameraX = this.cameraDestinationAnimX.getCurrent();
            if (this.cameraX == this.cameraDestinationAnimX.getDestination()) {
                this.cameraDestinationAnimX = null;
            }
        }
        if (this.cameraDestinationAnimZ != null) {
            this.cameraZ = this.cameraDestinationAnimZ.getCurrent();
            if (this.cameraZ == this.cameraDestinationAnimZ.getDestination()) {
                this.cameraDestinationAnimZ = null;
            }
        }
        this.lastViewedDimensionId = null;
        this.lastViewedMultiworldId = null;
        this.mouseBlockPosY = Short.MAX_VALUE;
        boolean discoveredForHighlights = false;
        Object object2 = this.mapProcessor.renderThreadPauseSync;
        synchronized (object2) {
            if (!this.mapProcessor.isRenderingPaused()) {
                boolean mapLoaded = this.mapProcessor.getCurrentWorldId() != null && !this.mapProcessor.isWaitingForWorldUpdate() && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete();
                boolean noWorldMapEffect = mc.field_1724 == null || Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_WORLD_MAP) || Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_WORLD_MAP_HARMFUL);
                boolean allowedBasedOnItem = ModSettings.mapItem == null || mc.field_1724 != null && Misc.hasItem((class_1657)mc.field_1724, ModSettings.mapItem);
                boolean isLocked = this.mapProcessor.isCurrentMapLocked();
                if (mapLoaded && !noWorldMapEffect && allowedBasedOnItem && !isLocked) {
                    HoveredMapElementHolder<?, ?> hovered;
                    String subWorldNameToRender;
                    LeveledRegion<?> leveledRegion;
                    double secondaryOffsetY;
                    double secondaryOffsetX;
                    RegionTexture tex;
                    MapRegion leafRegion;
                    GpuBufferSlice projectionBU = RenderSystem.getProjectionMatrixBuffer();
                    class_10366 projectionTypeBU = RenderSystem.getProjectionType();
                    Misc.minecraftOrtho(this.field_22787, false);
                    RenderSystem.getModelViewStack().pushMatrix();
                    RenderSystem.getModelViewStack().identity();
                    RenderSystem.getModelViewStack().translate(0.0f, 0.0f, -11000.0f);
                    if (SupportMods.vivecraft) {
                        TextureUtils.clearRenderTarget(this.field_22787.method_1522(), -16777216);
                    }
                    this.mapProcessor.updateCaveStart();
                    this.lastViewedDimensionId = this.mapProcessor.getMapWorld().getCurrentDimension().getDimId();
                    this.lastNonNullViewedDimensionId = this.lastViewedDimensionId;
                    this.lastViewedMultiworldId = this.mapProcessor.getMapWorld().getCurrentDimension().getCurrentMultiworld();
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.checkWaypoints(this.mapProcessor.getMapWorld().isMultiplayer(), this.lastViewedDimensionId, this.lastViewedMultiworldId, this.field_22789, this.field_22790, this, this.mapProcessor.getMapWorld(), this.mapProcessor.getWorldDimensionTypeRegistry());
                    }
                    int mouseXPos = (int)Misc.getMouseX(mc, false);
                    int mouseYPos = (int)Misc.getMouseY(mc, false);
                    double scaleMultiplier = this.getScaleMultiplier(Math.min(mc.method_22683().method_4489(), mc.method_22683().method_4506()));
                    this.scale = this.userScale * scaleMultiplier;
                    if (this.mouseCheckPosX == -1 || System.nanoTime() - this.mouseCheckTimeNano > 30000000L) {
                        this.prevMouseCheckPosX = this.mouseCheckPosX;
                        this.prevMouseCheckPosY = this.mouseCheckPosY;
                        this.prevMouseCheckTimeNano = this.mouseCheckTimeNano;
                        this.mouseCheckPosX = mouseXPos;
                        this.mouseCheckPosY = mouseYPos;
                        this.mouseCheckTimeNano = System.nanoTime();
                    }
                    if (!this.leftMouseButton.isDown) {
                        if (this.mouseDownPosX != -1) {
                            this.mouseDownPosX = -1;
                            this.mouseDownPosY = -1;
                            if (this.prevMouseCheckTimeNano != -1L) {
                                double speed_z;
                                double frameTime60FPS;
                                double downTime = 0.0;
                                int draggedX = 0;
                                int draggedY = 0;
                                draggedX = mouseXPos - this.prevMouseCheckPosX;
                                downTime = System.nanoTime() - this.prevMouseCheckTimeNano;
                                double speedScale = downTime / (frameTime60FPS = 1.6666666666666666E7);
                                double speed_x = (double)(-draggedX) / this.scale / speedScale;
                                double speed = Math.sqrt(speed_x * speed_x + (speed_z = (double)(-(draggedY = mouseYPos - this.prevMouseCheckPosY)) / this.scale / speedScale) * speed_z);
                                if (speed > 0.0) {
                                    double cos = speed_x / speed;
                                    double sin = speed_z / speed;
                                    double maxSpeed = 500.0 / this.userScale;
                                    speed = Math.abs(speed) > maxSpeed ? Math.copySign(maxSpeed, speed) : speed;
                                    double speed_factor = 0.9;
                                    double ln = Math.log(speed_factor);
                                    double move_distance = -speed / ln;
                                    double moveX = cos * move_distance;
                                    double moveZ = sin * move_distance;
                                    this.cameraDestinationAnimX = new SlowingAnimation(this.cameraX, this.cameraX + moveX, 0.9, 0.01);
                                    this.cameraDestinationAnimZ = new SlowingAnimation(this.cameraZ, this.cameraZ + moveZ, 0.9, 0.01);
                                }
                            }
                        }
                    } else if (this.viewed == null || !this.viewedInList || this.mouseDownPosX != -1) {
                        if (this.mouseDownPosX != -1) {
                            this.cameraX = (double)(this.mouseDownPosX - mouseXPos) / this.scale + this.mouseDownCameraX;
                            this.cameraZ = (double)(this.mouseDownPosY - mouseYPos) / this.scale + this.mouseDownCameraZ;
                        } else {
                            this.mouseDownPosX = mouseXPos;
                            this.mouseDownPosY = mouseYPos;
                            this.mouseDownCameraX = this.cameraX;
                            this.mouseDownCameraZ = this.cameraZ;
                            this.cameraDestinationAnimX = null;
                            this.cameraDestinationAnimZ = null;
                        }
                    }
                    int mouseFromCentreX = mouseXPos - mc.method_22683().method_4489() / 2;
                    int mouseFromCentreY = mouseYPos - mc.method_22683().method_4506() / 2;
                    double oldMousePosX = (double)mouseFromCentreX / this.scale + this.cameraX;
                    double oldMousePosZ = (double)mouseFromCentreY / this.scale + this.cameraZ;
                    double preScale = this.scale;
                    if (destScale != this.userScale) {
                        if (this.zoomAnim != null) {
                            this.userScale = this.zoomAnim.getCurrent();
                            this.scale = this.userScale * scaleMultiplier;
                        }
                        if (this.zoomAnim == null || Misc.round(this.zoomAnim.getDestination(), 4) != Misc.round(destScale, 4)) {
                            this.zoomAnim = new SinAnimation(this.userScale, destScale, 100L);
                        }
                    }
                    if (this.scale > preScale && this.lastZoomMethod != 2) {
                        this.cameraX = oldMousePosX - (double)mouseFromCentreX / this.scale;
                        this.cameraZ = oldMousePosZ - (double)mouseFromCentreY / this.scale;
                    }
                    int textureLevel = 0;
                    double fboScale = this.scale >= 1.0 ? Math.max(1.0, Math.floor(this.scale)) : this.scale;
                    if (this.userScale < 1.0) {
                        double reversedScale = 1.0 / this.userScale;
                        double log2 = Math.floor(Math.log(reversedScale) / Math.log(2.0));
                        textureLevel = Math.min((int)log2, 3);
                    }
                    this.mapProcessor.getMapSaveLoad().mainTextureLevel = textureLevel;
                    int leveledRegionShift = 9 + textureLevel;
                    double secondaryScale = this.scale / fboScale;
                    class_4587 matrixStack = WorldMap.worldMapClientOnly.getMapScreenPoseStack();
                    matrixStack.method_22903();
                    matrixStack.method_46416(0.0f, 0.0f, 100.0f);
                    double mousePosX = (double)mouseFromCentreX / this.scale + this.cameraX;
                    double mousePosZ = (double)mouseFromCentreY / this.scale + this.cameraZ;
                    this.mouseBlockPosX = (int)Math.floor(mousePosX);
                    this.mouseBlockPosZ = (int)Math.floor(mousePosZ);
                    this.mouseBlockDim = this.mapProcessor.getMapWorld().getCurrentDimension().getDimId();
                    this.mouseBlockCoordinateScale = this.getCurrentMapCoordinateScale();
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.onBlockHover();
                    }
                    int mouseRegX = this.mouseBlockPosX >> leveledRegionShift;
                    int mouseRegZ = this.mouseBlockPosZ >> leveledRegionShift;
                    int renderedCaveLayer = this.mapProcessor.getCurrentCaveLayer();
                    LeveledRegion<?> reg = this.mapProcessor.getLeveledRegion(renderedCaveLayer, mouseRegX, mouseRegZ, textureLevel);
                    int maxRegBlockCoord = (1 << leveledRegionShift) - 1;
                    int mouseRegPixelX = (this.mouseBlockPosX & maxRegBlockCoord) >> textureLevel;
                    int mouseRegPixelZ = (this.mouseBlockPosZ & maxRegBlockCoord) >> textureLevel;
                    this.mouseBlockPosX = (mouseRegX << leveledRegionShift) + (mouseRegPixelX << textureLevel);
                    this.mouseBlockPosZ = (mouseRegZ << leveledRegionShift) + (mouseRegPixelZ << textureLevel);
                    if (this.mapTileSelection != null && this.rightClickMenu == null) {
                        this.mapTileSelection.setEnd(this.mouseBlockPosX >> 4, this.mouseBlockPosZ >> 4);
                    }
                    MapTileChunk chunk = (leafRegion = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, this.mouseBlockPosX >> 9, this.mouseBlockPosZ >> 9, false)) == null ? null : leafRegion.getChunk(this.mouseBlockPosX >> 6 & 7, this.mouseBlockPosZ >> 6 & 7);
                    int debugTextureX = this.mouseBlockPosX >> leveledRegionShift - 3 & 7;
                    int debugTextureY = this.mouseBlockPosZ >> leveledRegionShift - 3 & 7;
                    RegionTexture regionTexture = tex = reg != null && reg.hasTextures() ? (RegionTexture)reg.getTexture(debugTextureX, debugTextureY) : null;
                    if (WorldMap.settings.debug) {
                        if (reg != null) {
                            ArrayList<String> debugLines = new ArrayList<String>();
                            if (tex != null) {
                                MapBlock block;
                                MapTile mouseTile;
                                tex.addDebugLines(debugLines);
                                MapTile mapTile = mouseTile = chunk == null ? null : chunk.getTile(this.mouseBlockPosX >> 4 & 3, this.mouseBlockPosZ >> 4 & 3);
                                if (mouseTile != null && (block = mouseTile.getBlock(this.mouseBlockPosX & 0xF, this.mouseBlockPosZ & 0xF)) != null) {
                                    guiGraphics.method_25300(mc.field_1772, block.toRenderString(leafRegion.getBiomeRegistry()), this.field_22789 / 2, 22, -1);
                                    if (block.getNumberOfOverlays() != 0) {
                                        for (int i = 0; i < block.getOverlays().size(); ++i) {
                                            guiGraphics.method_25300(mc.field_1772, block.getOverlays().get(i).toRenderString(), this.field_22789 / 2, 32 + i * 10, -1);
                                        }
                                    }
                                }
                            }
                            debugLines.add("");
                            debugLines.add(reg.toString());
                            reg.addDebugLines(debugLines, this.mapProcessor, debugTextureX, debugTextureY);
                            for (int i = 0; i < debugLines.size(); ++i) {
                                guiGraphics.method_25303(mc.field_1772, (String)debugLines.get(i), 5, 15 + 10 * i, -1);
                            }
                        }
                        class_2874 dimType = this.mapProcessor.getMapWorld().getCurrentDimension().getDimensionType(this.mapProcessor.getWorldDimensionTypeRegistry());
                        class_2960 dimTypeId = this.mapProcessor.getMapWorld().getCurrentDimension().getDimensionTypeId();
                        guiGraphics.method_25303(mc.field_1772, "MultiWorld ID: " + this.mapProcessor.getMapWorld().getCurrentMultiworld() + " Dim Type: " + String.valueOf(dimType == null ? "unknown" : dimTypeId), 5, 265, -1);
                        LayeredRegionManager regions = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions();
                        guiGraphics.method_25303(mc.field_1772, String.format("regions: %d loaded: %d processed: %d viewed: %d benchmarks %s", regions.size(), regions.loadedCount(), this.mapProcessor.getProcessedCount(), lastAmountOfRegionsViewed, WorldMap.textureUploadBenchmark.getTotalsString()), 5, 275, -1);
                        guiGraphics.method_25303(mc.field_1772, String.format("toLoad: %d toSave: %d tile pool: %d overlays: %d toLoadBranchCache: %d buffers: %d", this.mapProcessor.getMapSaveLoad().getSizeOfToLoad(), this.mapProcessor.getMapSaveLoad().getToSave().size(), this.mapProcessor.getTilePool().size(), this.mapProcessor.getOverlayManager().getNumberOfUniqueOverlays(), this.mapProcessor.getMapSaveLoad().getSizeOfToLoadBranchCache(), WorldMap.textureDirectBufferPool.size()), 5, 285, -1);
                        long i = Runtime.getRuntime().maxMemory();
                        long j = Runtime.getRuntime().totalMemory();
                        long k = Runtime.getRuntime().freeMemory();
                        long l = j - k;
                        int debugFPS = ((IWorldMapMinecraftClient)mc).getXaeroWorldMap_fps();
                        guiGraphics.method_25303(mc.field_1772, String.format("FPS: %d", debugFPS), 5, 295, -1);
                        guiGraphics.method_25303(mc.field_1772, String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, GuiMap.bytesToMb(l), GuiMap.bytesToMb(i)), 5, 315, -1);
                        guiGraphics.method_25303(mc.field_1772, String.format("Allocated: % 2d%% %03dMB", j * 100L / i, GuiMap.bytesToMb(j)), 5, 325, -1);
                        guiGraphics.method_25303(mc.field_1772, String.format("Available VRAM: %dMB", this.mapProcessor.getMapLimiter().getAvailableVRAM() / 1024), 5, 335, -1);
                    }
                    int pixelInsideTexX = mouseRegPixelX & 0x3F;
                    int pixelInsideTexZ = mouseRegPixelZ & 0x3F;
                    boolean hasAmbiguousHeight = false;
                    int mouseBlockBottomY = Short.MAX_VALUE;
                    int mouseBlockTopY = Short.MAX_VALUE;
                    class_5321<class_1959> pointedAtBiome = null;
                    if (tex != null) {
                        mouseBlockBottomY = this.mouseBlockPosY = tex.getHeight(pixelInsideTexX, pixelInsideTexZ);
                        mouseBlockTopY = tex.getTopHeight(pixelInsideTexX, pixelInsideTexZ);
                        hasAmbiguousHeight = this.mouseBlockPosY != mouseBlockTopY;
                        pointedAtBiome = tex.getBiome(pixelInsideTexX, pixelInsideTexZ);
                    }
                    if (hasAmbiguousHeight) {
                        if (mouseBlockTopY != Short.MAX_VALUE) {
                            this.mouseBlockPosY = mouseBlockTopY;
                        } else if (WorldMap.settings.detectAmbiguousY) {
                            this.mouseBlockPosY = Short.MAX_VALUE;
                        }
                    }
                    if (primaryScaleFBO == null || GuiMap.primaryScaleFBO.field_1480 != mc.method_22683().method_4489() || GuiMap.primaryScaleFBO.field_1477 != mc.method_22683().method_4506()) {
                        primaryScaleFBO = new ImprovedFramebuffer(mc.method_22683().method_4489(), mc.method_22683().method_4506(), true);
                    }
                    if (primaryScaleFBO.method_30277() == null || primaryScaleFBO.method_30278() == null) {
                        matrixStack.method_22909();
                        RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionBU, (class_10366)projectionTypeBU);
                        RenderSystem.getModelViewStack().popMatrix();
                        return;
                    }
                    primaryScaleFBO.bindAsMainTarget(false);
                    TextureUtils.clearRenderTarget((class_276)primaryScaleFBO, -16777216, 1.0f);
                    matrixStack.method_22905((float)(1.0 / this.screenScale), (float)(1.0 / this.screenScale), 1.0f);
                    matrixStack.method_46416((float)(mc.method_22683().method_4489() / 2), (float)(mc.method_22683().method_4506() / 2), 0.0f);
                    matrixStack.method_22903();
                    int flooredCameraX = (int)Math.floor(this.cameraX);
                    int flooredCameraZ = (int)Math.floor(this.cameraZ);
                    double primaryOffsetX = 0.0;
                    double primaryOffsetY = 0.0;
                    if (fboScale < 1.0) {
                        double pixelInBlocks = 1.0 / fboScale;
                        int xInFullPixels = (int)Math.floor(this.cameraX / pixelInBlocks);
                        int zInFullPixels = (int)Math.floor(this.cameraZ / pixelInBlocks);
                        double fboOffsetX = (double)xInFullPixels * pixelInBlocks;
                        double fboOffsetZ = (double)zInFullPixels * pixelInBlocks;
                        flooredCameraX = (int)Math.floor(fboOffsetX);
                        flooredCameraZ = (int)Math.floor(fboOffsetZ);
                        primaryOffsetX = fboOffsetX - (double)flooredCameraX;
                        primaryOffsetY = fboOffsetZ - (double)flooredCameraZ;
                        secondaryOffsetX = (this.cameraX - fboOffsetX) * fboScale;
                        secondaryOffsetY = (this.cameraZ - fboOffsetZ) * fboScale;
                    } else {
                        int offset;
                        secondaryOffsetX = (this.cameraX - (double)flooredCameraX) * fboScale;
                        secondaryOffsetY = (this.cameraZ - (double)flooredCameraZ) * fboScale;
                        if (secondaryOffsetX >= 1.0) {
                            offset = (int)secondaryOffsetX;
                            matrixStack.method_46416((float)(-offset), 0.0f, 0.0f);
                            secondaryOffsetX -= (double)offset;
                        }
                        if (secondaryOffsetY >= 1.0) {
                            offset = (int)secondaryOffsetY;
                            matrixStack.method_46416(0.0f, (float)offset, 0.0f);
                            secondaryOffsetY -= (double)offset;
                        }
                    }
                    matrixStack.method_22905((float)fboScale, (float)(-fboScale), 1.0f);
                    matrixStack.method_22904(-primaryOffsetX, -primaryOffsetY, 0.0);
                    double leftBorder = this.cameraX - (double)(mc.method_22683().method_4489() / 2) / this.scale;
                    double rightBorder = leftBorder + (double)mc.method_22683().method_4489() / this.scale;
                    double topBorder = this.cameraZ - (double)(mc.method_22683().method_4506() / 2) / this.scale;
                    double bottomBorder = topBorder + (double)mc.method_22683().method_4506() / this.scale;
                    int minRegX = (int)Math.floor(leftBorder) >> leveledRegionShift;
                    int maxRegX = (int)Math.floor(rightBorder) >> leveledRegionShift;
                    int minRegZ = (int)Math.floor(topBorder) >> leveledRegionShift;
                    int maxRegZ = (int)Math.floor(bottomBorder) >> leveledRegionShift;
                    int blockToTextureConversion = 6 + textureLevel;
                    int minTextureX = (int)Math.floor(leftBorder) >> blockToTextureConversion;
                    int maxTextureX = (int)Math.floor(rightBorder) >> blockToTextureConversion;
                    int minTextureZ = (int)Math.floor(topBorder) >> blockToTextureConversion;
                    int maxTextureZ = (int)Math.floor(bottomBorder) >> blockToTextureConversion;
                    int minLeafRegX = minTextureX << blockToTextureConversion >> 9;
                    int maxLeafRegX = (maxTextureX + 1 << blockToTextureConversion) - 1 >> 9;
                    int minLeafRegZ = minTextureZ << blockToTextureConversion >> 9;
                    int maxLeafRegZ = (maxTextureZ + 1 << blockToTextureConversion) - 1 >> 9;
                    lastAmountOfRegionsViewed = (maxRegX - minRegX + 1) * (maxRegZ - minRegZ + 1);
                    if (this.mapProcessor.getMapLimiter().getMostRegionsAtATime() < lastAmountOfRegionsViewed) {
                        this.mapProcessor.getMapLimiter().setMostRegionsAtATime(lastAmountOfRegionsViewed);
                    }
                    this.regionBuffer.clear();
                    this.branchRegionBuffer.clear();
                    float brightness = this.mapProcessor.getBrightness();
                    int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                    int globalCaveStart = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().getLayer(renderedCaveLayer).getCaveStart();
                    int globalCaveDepth = WorldMap.settings.caveModeDepth;
                    boolean reloadEverything = WorldMap.settings.reloadEverything;
                    int globalReloadVersion = WorldMap.settings.reloadVersion;
                    int globalVersion = this.mapProcessor.getGlobalVersion();
                    boolean prevWaitingForBranchCache = this.prevWaitingForBranchCache;
                    this.waitingForBranchCache[0] = false;
                    Matrix4f matrix = matrixStack.method_23760().method_23761();
                    class_4597.class_4598 renderTypeBuffers = this.mapProcessor.getCvc().getRenderTypeBuffers();
                    MultiTextureRenderTypeRendererProvider rendererProvider = this.mapProcessor.getMultiTextureRenderTypeRenderers();
                    MultiTextureRenderTypeRenderer withLightRenderer = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP);
                    MultiTextureRenderTypeRenderer noLightRenderer = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP);
                    class_4588 overlayBuffer = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_COLOR_OVERLAY);
                    LeveledRegion.setComparison(this.mouseBlockPosX >> leveledRegionShift, this.mouseBlockPosZ >> leveledRegionShift, textureLevel, this.mouseBlockPosX >> 9, this.mouseBlockPosZ >> 9);
                    LeveledRegion<?> lastUpdatedRootLeveledRegion = null;
                    boolean cacheOnlyMode = this.mapProcessor.getMapWorld().isCacheOnlyMode();
                    boolean frameRenderedRootTextures = false;
                    boolean loadingLeaves = false;
                    for (int leveledRegX = minRegX; leveledRegX <= maxRegX; ++leveledRegX) {
                        for (int leveledRegZ = minRegZ; leveledRegZ <= maxRegZ; ++leveledRegZ) {
                            boolean rootHasTextures;
                            int leveledSideInRegions = 1 << textureLevel;
                            int leveledSideInBlocks = leveledSideInRegions * 512;
                            int leafRegionMinX = leveledRegX * leveledSideInRegions;
                            int leafRegionMinZ = leveledRegZ * leveledSideInRegions;
                            leveledRegion = null;
                            for (int leafX = 0; leafX < leveledSideInRegions; ++leafX) {
                                for (int leafZ = 0; leafZ < leveledSideInRegions; ++leafZ) {
                                    int regZ;
                                    int regX = leafRegionMinX + leafX;
                                    if (regX < minLeafRegX || regX > maxLeafRegX || (regZ = leafRegionMinZ + leafZ) < minLeafRegZ || regZ > maxLeafRegZ) continue;
                                    MapRegion region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, false);
                                    if (region == null) {
                                        region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, this.mapProcessor.regionExists(renderedCaveLayer, regX, regZ));
                                    }
                                    if (region == null) continue;
                                    if (leveledRegion == null) {
                                        leveledRegion = this.mapProcessor.getLeveledRegion(renderedCaveLayer, leveledRegX, leveledRegZ, textureLevel);
                                    }
                                    if (prevWaitingForBranchCache) continue;
                                    MapRegion mapRegion = region;
                                    synchronized (mapRegion) {
                                        if (textureLevel != 0 && region.getLoadState() == 0 && region.loadingNeededForBranchLevel != 0 && region.loadingNeededForBranchLevel != textureLevel) {
                                            region.loadingNeededForBranchLevel = 0;
                                            region.getParent().setShouldCheckForUpdatesRecursive(true);
                                        }
                                        if (region.canRequestReload_unsynced() && (!cacheOnlyMode && (reloadEverything && region.getReloadVersion() != globalReloadVersion || region.getCacheHashCode() != globalRegionCacheHashCode || region.caveStartOutdated(globalCaveStart, globalCaveDepth) || region.getVersion() != globalVersion || region.getLoadState() != 2 && region.shouldCache()) || region.getLoadState() == 0 && (!region.isMetaLoaded() || textureLevel == 0 || region.loadingNeededForBranchLevel == textureLevel) || (region.isMetaLoaded() || region.getLoadState() != 0 || !region.hasHadTerrain()) && region.getHighlightsHash() != region.getDim().getHighlightHandler().getRegionHash(region.getRegionX(), region.getRegionZ()))) {
                                            loadingLeaves = true;
                                            region.calculateSortingDistance();
                                            Misc.addToListOfSmallest(10, this.regionBuffer, region);
                                        }
                                        continue;
                                    }
                                }
                            }
                            if (leveledRegion == null) continue;
                            LeveledRegion<?> rootLeveledRegion = leveledRegion.getRootRegion();
                            if (rootLeveledRegion == leveledRegion) {
                                rootLeveledRegion = null;
                            }
                            if (rootLeveledRegion != null && !rootLeveledRegion.isLoaded()) {
                                if (!rootLeveledRegion.recacheHasBeenRequested() && !rootLeveledRegion.reloadHasBeenRequested()) {
                                    rootLeveledRegion.calculateSortingDistance();
                                    Misc.addToListOfSmallest(10, this.branchRegionBuffer, (BranchLeveledRegion)rootLeveledRegion);
                                }
                                this.waitingForBranchCache[0] = true;
                                rootLeveledRegion = null;
                            }
                            if (!this.mapProcessor.isUploadingPaused() && !WorldMap.settings.pauseRequests) {
                                if (leveledRegion instanceof BranchLeveledRegion) {
                                    BranchLeveledRegion branchRegion = (BranchLeveledRegion)leveledRegion;
                                    branchRegion.checkForUpdates(this.mapProcessor, prevWaitingForBranchCache, this.waitingForBranchCache, this.branchRegionBuffer, textureLevel, minLeafRegX, minLeafRegZ, maxLeafRegX, maxLeafRegZ);
                                }
                                if ((textureLevel != 0 && !prevWaitingForBranchCache || textureLevel == 0 && !this.prevLoadingLeaves) && this.lastFrameRenderedRootTextures && rootLeveledRegion != null && rootLeveledRegion != lastUpdatedRootLeveledRegion) {
                                    BranchLeveledRegion branchRegion = (BranchLeveledRegion)rootLeveledRegion;
                                    branchRegion.checkForUpdates(this.mapProcessor, prevWaitingForBranchCache, this.waitingForBranchCache, this.branchRegionBuffer, textureLevel, minLeafRegX, minLeafRegZ, maxLeafRegX, maxLeafRegZ);
                                    lastUpdatedRootLeveledRegion = rootLeveledRegion;
                                }
                                this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(leveledRegion);
                                if (rootLeveledRegion != null) {
                                    this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(rootLeveledRegion);
                                }
                            } else {
                                this.waitingForBranchCache[0] = prevWaitingForBranchCache;
                            }
                            int minXBlocks = leveledRegX * leveledSideInBlocks;
                            int minZBlocks = leveledRegZ * leveledSideInBlocks;
                            int textureSize = 64 * leveledSideInRegions;
                            int firstTextureX = leveledRegX << 3;
                            int firstTextureZ = leveledRegZ << 3;
                            int levelDiff = 3 - textureLevel;
                            int rootSize = 1 << levelDiff;
                            int maxInsideCoord = rootSize - 1;
                            int firstRootTextureX = firstTextureX >> levelDiff & 7;
                            int firstRootTextureZ = firstTextureZ >> levelDiff & 7;
                            int firstInsideTextureX = firstTextureX & maxInsideCoord;
                            int firstInsideTextureZ = firstTextureZ & maxInsideCoord;
                            boolean hasTextures = leveledRegion.hasTextures();
                            boolean bl = rootHasTextures = rootLeveledRegion != null && rootLeveledRegion.hasTextures();
                            if (hasTextures || rootHasTextures) {
                                for (int o = 0; o < 8; ++o) {
                                    int textureX = minXBlocks + o * textureSize;
                                    if ((double)textureX > rightBorder || (double)(textureX + textureSize) < leftBorder) continue;
                                    for (int p = 0; p < 8; ++p) {
                                        RegionTexture<Object> regionTexture2;
                                        int textureZ = minZBlocks + p * textureSize;
                                        if ((double)textureZ > bottomBorder || (double)(textureZ + textureSize) < topBorder) continue;
                                        RegionTexture regionTexture3 = regionTexture2 = hasTextures ? (RegionTexture)leveledRegion.getTexture(o, p) : null;
                                        if (regionTexture2 == null || regionTexture2.getGlColorTexture() == null) {
                                            GpuTextureAndView texture;
                                            int insideZ;
                                            int rootTextureZ;
                                            int insideX;
                                            int rootTextureX;
                                            if (!rootHasTextures || (regionTexture2 = rootLeveledRegion.getTexture(rootTextureX = firstRootTextureX + ((insideX = firstInsideTextureX + o) >> levelDiff), rootTextureZ = firstRootTextureZ + ((insideZ = firstInsideTextureZ + p) >> levelDiff))) == null || (texture = regionTexture2.getGlColorTexture()) == null) continue;
                                            frameRenderedRootTextures = true;
                                            int insideTextureX = insideX & maxInsideCoord;
                                            int insideTextureZ = insideZ & maxInsideCoord;
                                            float textureX1 = (float)insideTextureX / (float)rootSize;
                                            float textureX2 = (float)(insideTextureX + 1) / (float)rootSize;
                                            float textureY1 = (float)insideTextureZ / (float)rootSize;
                                            float textureY2 = (float)(insideTextureZ + 1) / (float)rootSize;
                                            boolean hasLight = regionTexture2.getTextureHasLight();
                                            GuiMap.renderTexturedModalSubRectWithLighting(matrix, textureX - flooredCameraX, textureZ - flooredCameraZ, textureX1, textureY1, textureX2, textureY2, textureSize, textureSize, texture.texture, hasLight, hasLight ? withLightRenderer : noLightRenderer);
                                            continue;
                                        }
                                        GpuTextureAndView texture = regionTexture2.getGlColorTexture();
                                        if (texture == null) continue;
                                        boolean hasLight = regionTexture2.getTextureHasLight();
                                        GuiMap.renderTexturedModalRectWithLighting3(matrix, textureX - flooredCameraX, textureZ - flooredCameraZ, textureSize, textureSize, texture.texture, hasLight, hasLight ? withLightRenderer : noLightRenderer);
                                    }
                                }
                            }
                            if (leveledRegion.loadingAnimation()) {
                                matrixStack.method_22903();
                                matrixStack.method_22904((double)leveledSideInBlocks * ((double)leveledRegX + 0.5) - (double)flooredCameraX, (double)leveledSideInBlocks * ((double)leveledRegZ + 0.5) - (double)flooredCameraZ, 0.0);
                                float loadingAnimationPassed = System.currentTimeMillis() - this.loadingAnimationStart;
                                if (loadingAnimationPassed > 0.0f) {
                                    int period = 2000;
                                    int numbersOfActors = 3;
                                    float loadingAnimation = loadingAnimationPassed % (float)period / (float)period * 360.0f;
                                    float step = 360.0f / (float)numbersOfActors;
                                    OptimizedMath.rotatePose(matrixStack, loadingAnimation, (Vector3fc)OptimizedMath.ZP);
                                    int numberOfVisibleActors = 1 + (int)loadingAnimationPassed % (3 * period) / period;
                                    matrixStack.method_22905((float)leveledSideInRegions, (float)leveledSideInRegions, 1.0f);
                                    for (int i = 0; i < numberOfVisibleActors; ++i) {
                                        OptimizedMath.rotatePose(matrixStack, step, (Vector3fc)OptimizedMath.ZP);
                                        MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), overlayBuffer, 16, -8, 32, 8, 1.0f, 1.0f, 1.0f, 1.0f);
                                    }
                                }
                                matrixStack.method_22909();
                            }
                            if (WorldMap.settings.debug && leveledRegion instanceof MapRegion) {
                                MapRegion region = (MapRegion)leveledRegion;
                                matrixStack.method_22903();
                                matrixStack.method_46416((float)(512 * region.getRegionX() + 32 - flooredCameraX), (float)(512 * region.getRegionZ() + 32 - flooredCameraZ), 0.0f);
                                matrixStack.method_22905(10.0f, 10.0f, 1.0f);
                                Misc.drawNormalText(matrixStack, "" + region.getLoadState(), 0.0f, 0.0f, -1, true, renderTypeBuffers);
                                matrixStack.method_22909();
                            }
                            if (!WorldMap.settings.debug || textureLevel <= 0) continue;
                            for (int leafX = 0; leafX < leveledSideInRegions; ++leafX) {
                                for (int leafZ = 0; leafZ < leveledSideInRegions; ++leafZ) {
                                    boolean currentlyLoading;
                                    int regX = leafRegionMinX + leafX;
                                    int regZ = leafRegionMinZ + leafZ;
                                    MapRegion region = this.mapProcessor.getLeafMapRegion(renderedCaveLayer, regX, regZ, false);
                                    if (region == null) continue;
                                    boolean bl2 = currentlyLoading = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing() == region;
                                    if (!currentlyLoading && !region.isLoaded() && !region.isMetaLoaded()) continue;
                                    matrixStack.method_22903();
                                    matrixStack.method_46416((float)(512 * region.getRegionX() - flooredCameraX), (float)(512 * region.getRegionZ() - flooredCameraZ), 0.0f);
                                    float r = 0.0f;
                                    float g = 0.0f;
                                    float b = 0.0f;
                                    float a = 0.1569f;
                                    if (currentlyLoading) {
                                        b = 1.0f;
                                        r = 1.0f;
                                    } else if (region.isLoaded()) {
                                        g = 1.0f;
                                    } else {
                                        g = 1.0f;
                                        r = 1.0f;
                                    }
                                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), overlayBuffer, 0, 0, 512, 512, r, g, b, a);
                                    matrixStack.method_22909();
                                }
                            }
                        }
                    }
                    this.lastFrameRenderedRootTextures = frameRenderedRootTextures;
                    WorldMapShaderHelper.setBrightness(brightness);
                    WorldMapShaderHelper.setWithLight(true);
                    rendererProvider.draw(withLightRenderer);
                    WorldMapShaderHelper.setWithLight(false);
                    rendererProvider.draw(noLightRenderer);
                    LeveledRegion<?> nextToLoad = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
                    boolean shouldRequest = false;
                    shouldRequest = nextToLoad != null ? nextToLoad.shouldAllowAnotherRegionToLoad() : true;
                    boolean bl = shouldRequest = shouldRequest && this.mapProcessor.getAffectingLoadingFrequencyCount() < 16;
                    if (shouldRequest && !WorldMap.settings.pauseRequests) {
                        int i;
                        int toRequest = 2;
                        int counter = 0;
                        for (i = 0; i < this.branchRegionBuffer.size() && counter < toRequest; ++i) {
                            BranchLeveledRegion region = this.branchRegionBuffer.get(i);
                            if (region.reloadHasBeenRequested() || region.recacheHasBeenRequested() || region.isLoaded()) continue;
                            region.setReloadHasBeenRequested(true, "Gui");
                            this.mapProcessor.getMapSaveLoad().requestBranchCache(region, "Gui");
                            if (counter == 0) {
                                this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing(region);
                            }
                            ++counter;
                        }
                        toRequest = 1;
                        counter = 0;
                        if (!prevWaitingForBranchCache) {
                            for (i = 0; i < this.regionBuffer.size() && counter < toRequest; ++i) {
                                MapRegion region = this.regionBuffer.get(i);
                                if (region == nextToLoad && this.regionBuffer.size() > 1) continue;
                                leveledRegion = region;
                                synchronized (leveledRegion) {
                                    if (!region.canRequestReload_unsynced()) {
                                        continue;
                                    }
                                    if (region.getLoadState() == 2) {
                                        region.requestRefresh(this.mapProcessor);
                                    } else {
                                        this.mapProcessor.getMapSaveLoad().requestLoad(region, "Gui");
                                    }
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
                    this.prevWaitingForBranchCache = this.waitingForBranchCache[0];
                    this.prevLoadingLeaves = loadingLeaves;
                    int highlightChunkX = this.mouseBlockPosX >> 4;
                    int highlightChunkZ = this.mouseBlockPosZ >> 4;
                    int chunkHighlightLeftX = highlightChunkX << 4;
                    int chunkHighlightRightX = highlightChunkX + 1 << 4;
                    int chunkHighlightTopZ = highlightChunkZ << 4;
                    int chunkHighlightBottomZ = highlightChunkZ + 1 << 4;
                    MapRenderHelper.renderDynamicHighlight(matrixStack, overlayBuffer, flooredCameraX, flooredCameraZ, chunkHighlightLeftX, chunkHighlightRightX, chunkHighlightTopZ, chunkHighlightBottomZ, 0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 1.0f, 1.0f, 0.1569f);
                    MapTileSelection mapTileSelectionToRender = this.mapTileSelection;
                    if (mapTileSelectionToRender == null && this.field_22787.field_1755 instanceof ExportScreen) {
                        mapTileSelectionToRender = ((ExportScreen)this.field_22787.field_1755).getSelection();
                    }
                    if (mapTileSelectionToRender != null) {
                        MapRenderHelper.renderDynamicHighlight(matrixStack, overlayBuffer, flooredCameraX, flooredCameraZ, mapTileSelectionToRender.getLeft() << 4, mapTileSelectionToRender.getRight() + 1 << 4, mapTileSelectionToRender.getTop() << 4, mapTileSelectionToRender.getBottom() + 1 << 4, 0.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.5f, 0.5f, 0.4f);
                        if (SupportMods.pac() && !this.mapProcessor.getMapWorld().isUsingCustomDimension()) {
                            int playerX = (int)Math.floor(this.player.method_23317());
                            int playerZ = (int)Math.floor(this.player.method_23321());
                            int playerChunkX = playerX >> 4;
                            int playerChunkZ = playerZ >> 4;
                            int claimDistance = SupportMods.xaeroPac.getClaimDistance();
                            int claimableAreaLeft = playerChunkX - claimDistance;
                            int claimableAreaTop = playerChunkZ - claimDistance;
                            int claimableAreaRight = playerChunkX + claimDistance;
                            int claimableAreaBottom = playerChunkZ + claimDistance;
                            int claimableAreaHighlightLeftX = claimableAreaLeft << 4;
                            int claimableAreaHighlightRightX = claimableAreaRight + 1 << 4;
                            int claimableAreaHighlightTopZ = claimableAreaTop << 4;
                            int claimableAreaHighlightBottomZ = claimableAreaBottom + 1 << 4;
                            MapRenderHelper.renderDynamicHighlight(matrixStack, overlayBuffer, flooredCameraX, flooredCameraZ, claimableAreaHighlightLeftX, claimableAreaHighlightRightX, claimableAreaHighlightTopZ, claimableAreaHighlightBottomZ, 0.0f, 0.0f, 1.0f, 0.3f, 0.0f, 0.0f, 1.0f, 0.15f);
                        }
                    }
                    renderTypeBuffers.method_22993();
                    primaryScaleFBO.bindDefaultFramebuffer(mc);
                    matrixStack.method_22909();
                    matrixStack.method_22903();
                    matrixStack.method_22905((float)secondaryScale, (float)secondaryScale, 1.0f);
                    primaryScaleFBO.method_30277().setTextureFilter(FilterMode.LINEAR, false);
                    class_4588 colorBackgroundConsumer = renderTypeBuffers.getBuffer(CustomRenderTypes.MAP_COLOR_FILLER);
                    int lineX = -mc.method_22683().method_4489() / 2;
                    int lineY = mc.method_22683().method_4506() / 2 - 5;
                    int lineW = mc.method_22683().method_4489();
                    int lineH = 6;
                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), colorBackgroundConsumer, lineX, lineY, lineX + lineW, lineY + lineH, 0.0f, 0.0f, 0.0f, 1.0f);
                    lineX = mc.method_22683().method_4489() / 2 - 5;
                    lineY = -mc.method_22683().method_4506() / 2;
                    lineW = 6;
                    lineH = mc.method_22683().method_4506();
                    MapRenderHelper.fillIntoExistingBuffer(matrixStack.method_23760().method_23761(), colorBackgroundConsumer, lineX, lineY, lineX + lineW, lineY + lineH, 0.0f, 0.0f, 0.0f, 1.0f);
                    renderTypeBuffers.method_22993();
                    class_1921 mainFrameRenderType = CustomRenderTypes.MAP_FRAME;
                    MultiTextureRenderTypeRenderer mainFrameRenderer = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, mainFrameRenderType);
                    class_287 mainFrameVertexConsumer = mainFrameRenderer.begin(primaryScaleFBO.method_30277());
                    GuiMap.renderTexturedModalRect(matrixStack.method_23760().method_23761(), (class_4588)mainFrameVertexConsumer, (float)(-mc.method_22683().method_4489() / 2) - (float)secondaryOffsetX, (float)(-mc.method_22683().method_4506() / 2) - (float)secondaryOffsetY, 0, 0, GuiMap.primaryScaleFBO.field_1480, GuiMap.primaryScaleFBO.field_1477, GuiMap.primaryScaleFBO.field_1480, GuiMap.primaryScaleFBO.field_1477, 1.0f, 1.0f, 1.0f, 1.0f);
                    rendererProvider.draw(mainFrameRenderer);
                    matrixStack.method_22909();
                    matrixStack.method_22905((float)this.scale, (float)this.scale, 1.0f);
                    double screenSizeBasedScale = scaleMultiplier;
                    WorldMap.trackedPlayerRenderer.update(mc);
                    try {
                        this.viewed = WorldMap.mapElementRenderHandler.render(this, renderTypeBuffers, rendererProvider, this.cameraX, this.cameraZ, mc.method_22683().method_4489(), mc.method_22683().method_4506(), screenSizeBasedScale, this.scale, playerDimDiv, mousePosX, mousePosZ, brightness, renderedCaveLayer != Integer.MAX_VALUE, this.viewed, mc, partialTicks);
                    }
                    catch (Throwable t) {
                        WorldMap.LOGGER.error("error rendering map elements", t);
                        throw t;
                    }
                    this.viewedInList = false;
                    matrixStack.method_22903();
                    matrixStack.method_46416(0.0f, 0.0f, 50.0f);
                    class_4588 regularUIObjectConsumer = renderTypeBuffers.getBuffer(CustomRenderTypes.GUI);
                    if (WorldMap.settings.footsteps) {
                        ArrayList<Double[]> footprints;
                        ArrayList<Double[]> claimableAreaHighlightBottomZ = footprints = this.mapProcessor.getFootprints();
                        synchronized (claimableAreaHighlightBottomZ) {
                            for (int i = 0; i < footprints.size(); ++i) {
                                Double[] coords = footprints.get(i);
                                this.setColourBuffer(1.0f, 0.1f, 0.1f, 1.0f);
                                this.drawDotOnMap(matrixStack, regularUIObjectConsumer, coords[0] / playerDimDiv - this.cameraX, coords[1] / playerDimDiv - this.cameraZ, 0.0f, 1.0 / this.scale);
                            }
                        }
                    }
                    if (WorldMap.settings.renderArrow) {
                        boolean toTheLeft = scaledPlayerX < leftBorder;
                        boolean toTheRight = scaledPlayerX > rightBorder;
                        boolean down = scaledPlayerZ > bottomBorder;
                        boolean up = scaledPlayerZ < topBorder;
                        float configuredR = 1.0f;
                        float configuredG = 1.0f;
                        float configuredB = 1.0f;
                        int effectiveArrowColorIndex = WorldMap.settings.arrowColour;
                        if (effectiveArrowColorIndex == -2 && !SupportMods.minimap()) {
                            effectiveArrowColorIndex = 0;
                        }
                        if (effectiveArrowColorIndex == -2 && SupportMods.xaeroMinimap.getArrowColorIndex() == -1) {
                            effectiveArrowColorIndex = -1;
                        }
                        if (effectiveArrowColorIndex == -1) {
                            int rgb = Misc.getTeamColour((class_1297)(mc.field_1724 == null ? mc.method_1560() : mc.field_1724));
                            if (rgb == -1) {
                                effectiveArrowColorIndex = 0;
                            } else {
                                configuredR = (float)(rgb >> 16 & 0xFF) / 255.0f;
                                configuredG = (float)(rgb >> 8 & 0xFF) / 255.0f;
                                configuredB = (float)(rgb & 0xFF) / 255.0f;
                            }
                        } else if (effectiveArrowColorIndex == -2) {
                            float[] c = SupportMods.xaeroMinimap.getArrowColor();
                            if (c == null) {
                                effectiveArrowColorIndex = 0;
                            } else {
                                configuredR = c[0];
                                configuredG = c[1];
                                configuredB = c[2];
                            }
                        }
                        if (effectiveArrowColorIndex >= 0) {
                            float[] c = ModSettings.arrowColours[effectiveArrowColorIndex];
                            configuredR = c[0];
                            configuredG = c[1];
                            configuredB = c[2];
                        }
                        if (toTheLeft || toTheRight || up || down) {
                            double arrowX = scaledPlayerX;
                            double arrowZ = scaledPlayerZ;
                            float a = 0.0f;
                            if (toTheLeft) {
                                a = up ? 1.5f : (down ? 0.5f : 1.0f);
                                arrowX = leftBorder;
                            } else if (toTheRight) {
                                a = up ? 2.5f : (down ? 3.5f : 3.0f);
                                arrowX = rightBorder;
                            }
                            if (down) {
                                arrowZ = bottomBorder;
                            } else if (up) {
                                if (a == 0.0f) {
                                    a = 2.0f;
                                }
                                arrowZ = topBorder;
                            }
                            this.setColourBuffer(0.0f, 0.0f, 0.0f, 0.9f);
                            this.drawFarArrowOnMap(matrixStack, regularUIObjectConsumer, arrowX - this.cameraX, arrowZ + 2.0 * screenSizeBasedScale / this.scale - this.cameraZ, a, screenSizeBasedScale / this.scale);
                            this.setColourBuffer(configuredR, configuredG, configuredB, 1.0f);
                            this.drawFarArrowOnMap(matrixStack, regularUIObjectConsumer, arrowX - this.cameraX, arrowZ - this.cameraZ, a, screenSizeBasedScale / this.scale);
                        } else {
                            this.setColourBuffer(0.0f, 0.0f, 0.0f, 0.9f);
                            this.drawArrowOnMap(matrixStack, regularUIObjectConsumer, scaledPlayerX - this.cameraX, scaledPlayerZ + 2.0 * screenSizeBasedScale / this.scale - this.cameraZ, this.player.method_36454(), screenSizeBasedScale / this.scale);
                            this.setColourBuffer(configuredR, configuredG, configuredB, 1.0f);
                            this.drawArrowOnMap(matrixStack, regularUIObjectConsumer, scaledPlayerX - this.cameraX, scaledPlayerZ - this.cameraZ, this.player.method_36454(), screenSizeBasedScale / this.scale);
                        }
                    }
                    class_1044 guiTextures = this.field_22787.method_1531().method_4619(WorldMap.guiTextures);
                    guiTextures.method_4527(true, false);
                    renderTypeBuffers.method_22993();
                    guiTextures.method_4527(false, false);
                    matrixStack.method_22909();
                    matrixStack.method_22909();
                    TextureUtils.clearRenderTargetDepth(this.field_22787.method_1522(), 1.0f);
                    int cursorDisplayOffset = 0;
                    if (WorldMap.settings.coordinates) {
                        String coordsString = "X: " + this.mouseBlockPosX;
                        if (mouseBlockBottomY != Short.MAX_VALUE) {
                            coordsString = coordsString + " Y: " + mouseBlockBottomY;
                        }
                        if (hasAmbiguousHeight && mouseBlockTopY != Short.MAX_VALUE) {
                            coordsString = coordsString + " (" + mouseBlockTopY + ")";
                        }
                        coordsString = coordsString + " Z: " + this.mouseBlockPosZ;
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, this.field_22793, coordsString, this.field_22789 / 2, 2 + cursorDisplayOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        cursorDisplayOffset += 10;
                    }
                    if (WorldMap.settings.hoveredBiome && pointedAtBiome != null) {
                        class_2960 biomeRL = pointedAtBiome.method_29177();
                        String biomeText = biomeRL == null ? class_1074.method_4662((String)"gui.xaero_wm_unknown_biome", (Object[])new Object[0]) : class_1074.method_4662((String)("biome." + biomeRL.method_12836() + "." + biomeRL.method_12832()), (Object[])new Object[0]);
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, this.field_22793, biomeText, this.field_22789 / 2, 2 + cursorDisplayOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    int subtleTooltipOffset = 12;
                    if (WorldMap.settings.displayZoom) {
                        String zoomString = (double)Math.round(destScale * 1000.0) / 1000.0 + "x";
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, zoomString, this.field_22789 / 2, this.field_22790 - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (this.mapProcessor.getMapWorld().getCurrentDimension().getFullReloader() != null) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, FULL_RELOAD_IN_PROGRESS, this.field_22789 / 2, this.field_22790 - (subtleTooltipOffset += 12), -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (this.mapProcessor.getMapWorld().isUsingUnknownDimensionType()) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, UNKNOWN_DIMENSION_TYPE2, this.field_22789 / 2, this.field_22790 - (subtleTooltipOffset += 24), -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, UNKNOWN_DIMENSION_TYPE1, this.field_22789 / 2, this.field_22790 - (subtleTooltipOffset += 12), -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    if (WorldMap.settings.displayCaveModeStart) {
                        subtleTooltipOffset += 12;
                        if (globalCaveStart != Integer.MAX_VALUE && globalCaveStart != Integer.MIN_VALUE) {
                            String caveModeStartString = class_1074.method_4662((String)"gui.xaero_wm_cave_mode_start_display", (Object[])new Object[]{globalCaveStart});
                            MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, caveModeStartString, this.field_22789 / 2, this.field_22790 - subtleTooltipOffset, -1, 0.0f, 0.0f, 0.0f, 0.4f);
                        }
                    }
                    if (SupportMods.minimap() && (subWorldNameToRender = SupportMods.xaeroMinimap.getSubWorldNameToRender()) != null) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, subWorldNameToRender, this.field_22789 / 2, this.field_22790 - (subtleTooltipOffset += 24), -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    discoveredForHighlights = mouseBlockBottomY != Short.MAX_VALUE;
                    class_2561 subtleHighlightTooltip = this.mapProcessor.getMapWorld().getCurrentDimension().getHighlightHandler().getBlockHighlightSubtleTooltip(this.mouseBlockPosX, this.mouseBlockPosZ, discoveredForHighlights);
                    if (subtleHighlightTooltip != null) {
                        MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, mc.field_1772, subtleHighlightTooltip, this.field_22789 / 2, this.field_22790 - (subtleTooltipOffset += 12), -1, 0.0f, 0.0f, 0.0f, 0.4f);
                    }
                    this.overWaypointsMenu = false;
                    this.overPlayersMenu = false;
                    if (this.waypointMenu) {
                        HoveredMapElementHolder<?, ?> hovered2;
                        if (SupportMods.xaeroMinimap.getWaypointsSorted() != null && (hovered2 = SupportMods.xaeroMinimap.renderWaypointsMenu(guiGraphics, this, this.scale, this.field_22789, this.field_22790, scaledMouseX, scaledMouseY, this.leftMouseButton.isDown, this.leftMouseButton.clicked, this.viewed, mc)) != null) {
                            this.overWaypointsMenu = true;
                            if (hovered2.getElement() instanceof Waypoint) {
                                this.viewed = hovered2;
                                this.viewedInList = true;
                                if (this.leftMouseButton.clicked) {
                                    this.cameraDestination = new int[]{(int)((Waypoint)this.viewed.getElement()).getRenderX(), (int)((Waypoint)this.viewed.getElement()).getRenderZ()};
                                    this.leftMouseButton.isDown = false;
                                    if (WorldMap.settings.closeWaypointsWhenHopping) {
                                        this.onWaypointsButton(this.waypointsButton);
                                    }
                                }
                            }
                        }
                    } else if (this.playersMenu && (hovered = WorldMap.trackedPlayerMenuRenderer.renderMenu(guiGraphics, this, this.scale, this.field_22789, this.field_22790, scaledMouseX, scaledMouseY, this.leftMouseButton.isDown, this.leftMouseButton.clicked, this.viewed, mc)) != null) {
                        this.overPlayersMenu = true;
                        if (hovered.getElement() instanceof PlayerTrackerMapElement && WorldMap.trackedPlayerMenuRenderer.canJumpTo((PlayerTrackerMapElement)hovered.getElement())) {
                            this.viewed = hovered;
                            this.viewedInList = true;
                            if (this.leftMouseButton.clicked) {
                                PlayerTrackerMapElement clickedPlayer = (PlayerTrackerMapElement)this.viewed.getElement();
                                MapDimension clickedPlayerDim = this.mapProcessor.getMapWorld().getDimension(clickedPlayer.getDimension());
                                class_2874 clickedPlayerDimType = MapDimension.getDimensionType(clickedPlayerDim, clickedPlayer.getDimension(), this.mapProcessor.getWorldDimensionTypeRegistry());
                                double clickedPlayerDimDiv = this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(this.mapProcessor.getWorldDimensionTypeRegistry(), clickedPlayerDimType);
                                double jumpX = clickedPlayer.getX() / clickedPlayerDimDiv;
                                double jumpZ = clickedPlayer.getZ() / clickedPlayerDimDiv;
                                this.cameraDestination = new int[]{(int)jumpX, (int)jumpZ};
                                this.leftMouseButton.isDown = false;
                            }
                        }
                    }
                    if (SupportMods.minimap()) {
                        SupportMods.xaeroMinimap.drawSetChange(guiGraphics);
                    }
                    if (SupportMods.pac()) {
                        SupportMods.xaeroPac.onMapRender(this.field_22787, matrixStack, scaledMouseX, scaledMouseY, partialTicks, this.mapProcessor.getWorld().method_27983().method_29177(), highlightChunkX, highlightChunkZ);
                    }
                    RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionBU, (class_10366)projectionTypeBU);
                    RenderSystem.getModelViewStack().popMatrix();
                } else if (!mapLoaded) {
                    this.renderLoadingScreen(guiGraphics);
                } else if (isLocked) {
                    this.renderMessageScreen(guiGraphics, class_1074.method_4662((String)"gui.xaero_current_map_locked1", (Object[])new Object[0]), class_1074.method_4662((String)"gui.xaero_current_map_locked2", (Object[])new Object[0]));
                } else if (noWorldMapEffect) {
                    this.renderMessageScreen(guiGraphics, class_1074.method_4662((String)"gui.xaero_no_world_map_message", (Object[])new Object[0]));
                } else if (!allowedBasedOnItem) {
                    this.renderMessageScreen(guiGraphics, class_1074.method_4662((String)"gui.xaero_no_world_map_item_message", (Object[])new Object[0]), ModSettings.mapItem.method_63680().getString() + " (" + ModSettings.mapItemId + ")");
                }
            } else {
                this.renderLoadingScreen(guiGraphics);
            }
            this.mapSwitchingGui.renderText(guiGraphics, this.field_22787, scaledMouseX, scaledMouseY, this.field_22789, this.field_22790);
            guiGraphics.method_25290(class_10799.field_56883, WorldMap.guiTextures, this.field_22789 - 34, 2, 0.0f, 37.0f, 32, 32, 256, 256);
        }
        super.method_25394(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        if (this.rightClickMenu != null) {
            this.rightClickMenu.method_25394(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        }
        if (mc.field_1755 == this) {
            if (!(this.renderTooltips(guiGraphics, scaledMouseX, scaledMouseY, partialTicks) || this.leftMouseButton.isDown || this.rightMouseButton.isDown)) {
                if (this.viewed != null) {
                    CursorBox hoveredTooltip = this.hoveredElementTooltipHelper(this.viewed, this.viewedInList);
                    if (hoveredTooltip != null) {
                        hoveredTooltip.drawBox(guiGraphics, scaledMouseX, scaledMouseY, this.field_22789, this.field_22790);
                    }
                } else {
                    object2 = this.mapProcessor.renderThreadPauseSync;
                    synchronized (object2) {
                        class_2561 bluntHighlightTooltip;
                        if (!this.mapProcessor.isRenderingPaused() && this.mapProcessor.getCurrentWorldId() != null && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete() && (bluntHighlightTooltip = this.mapProcessor.getMapWorld().getCurrentDimension().getHighlightHandler().getBlockHighlightBluntTooltip(this.mouseBlockPosX, this.mouseBlockPosZ, discoveredForHighlights)) != null) {
                            new CursorBox(bluntHighlightTooltip).drawBox(guiGraphics, scaledMouseX, scaledMouseY, this.field_22789, this.field_22790);
                        }
                    }
                }
            }
            this.mapProcessor.getMessageBoxRenderer().render(guiGraphics, this.mapProcessor.getMessageBox(), this.field_22793, 1, this.field_22790 / 2, false);
        }
        this.rightMouseButton.clicked = false;
        this.leftMouseButton.clicked = false;
        this.noUploadingLimits = this.cameraX == cameraXBefore && this.cameraZ == cameraZBefore && scaleBefore == this.scale;
        MapRenderHelper.restoreDefaultShaderBlendState();
    }

    public void method_25420(class_332 guiGraphics, int i, int j, float f) {
    }

    @Override
    protected void renderPreDropdown(class_332 guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks) {
        super.renderPreDropdown(guiGraphics, scaledMouseX, scaledMouseY, partialTicks);
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().postMapRender(guiGraphics, this, scaledMouseX, scaledMouseY, this.field_22789, this.field_22790, partialTicks);
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.postMapRender(guiGraphics, this, scaledMouseX, scaledMouseY, this.field_22789, this.field_22790, partialTicks);
        }
        this.mapSwitchingGui.postMapRender(guiGraphics, this.field_22787, scaledMouseX, scaledMouseY, this.field_22789, this.field_22790);
    }

    private <E, C> CursorBox hoveredElementTooltipHelper(HoveredMapElementHolder<E, C> hovered, boolean viewedInList) {
        return hovered.getRenderer().getReader().getTooltip(hovered.getElement(), hovered.getRenderer().getContext(), viewedInList);
    }

    private void renderLoadingScreen(class_332 guiGraphics) {
        this.renderMessageScreen(guiGraphics, "Preparing World Map...");
    }

    private void renderMessageScreen(class_332 guiGraphics, String message) {
        this.renderMessageScreen(guiGraphics, message, null);
    }

    private void renderMessageScreen(class_332 guiGraphics, String message, String message2) {
        guiGraphics.method_25294(0, 0, this.field_22787.method_22683().method_4489(), this.field_22787.method_22683().method_4506(), -16777216);
        guiGraphics.method_25300(this.field_22787.field_1772, message, this.field_22787.method_22683().method_4486() / 2, this.field_22787.method_22683().method_4502() / 2, -1);
        if (message2 != null) {
            guiGraphics.method_25300(this.field_22787.field_1772, message2, this.field_22787.method_22683().method_4486() / 2, this.field_22787.method_22683().method_4502() / 2 + 10, -1);
        }
    }

    public void drawDotOnMap(class_4587 matrixStack, class_4588 guiLinearBuffer, double x, double z, float angle, double sc) {
        this.drawObjectOnMap(matrixStack, guiLinearBuffer, x, z, angle, sc, 2.5f, 2.5f, 0, 69, 5, 5);
    }

    public void drawArrowOnMap(class_4587 matrixStack, class_4588 guiLinearBuffer, double x, double z, float angle, double sc) {
        this.drawObjectOnMap(matrixStack, guiLinearBuffer, x, z, angle, sc, 13.0f, 5.0f, 0, 0, 26, 28);
    }

    public void drawFarArrowOnMap(class_4587 matrixStack, class_4588 guiLinearBuffer, double x, double z, float angle, double sc) {
        this.drawObjectOnMap(matrixStack, guiLinearBuffer, x, z, angle * 90.0f, sc, 27.0f, 13.0f, 26, 0, 54, 13);
    }

    public void drawObjectOnMap(class_4587 matrixStack, class_4588 guiLinearBuffer, double x, double z, float angle, double sc, float offX, float offY, int textureX, int textureY, int w, int h) {
        matrixStack.method_22903();
        matrixStack.method_22904(x, z, 0.0);
        matrixStack.method_22905((float)sc, (float)sc, 1.0f);
        if (angle != 0.0f) {
            OptimizedMath.rotatePose(matrixStack, angle, (Vector3fc)OptimizedMath.ZP);
        }
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        GuiMap.renderTexturedModalRect(matrix, guiLinearBuffer, -offX, -offY, textureX, textureY, w, h, 256.0f, 256.0f, this.colourBuffer[0], this.colourBuffer[1], this.colourBuffer[2], this.colourBuffer[3]);
        matrixStack.method_22909();
    }

    public static void renderTexturedModalRectWithLighting3(Matrix4f matrix, float x, float y, float width, float height, GpuTexture texture, boolean hasLight, MultiTextureRenderTypeRenderer renderer) {
        GuiMap.buildTexturedModalRectWithLighting(matrix, renderer.begin(texture), x, y, width, height);
    }

    public static void renderTexturedModalSubRectWithLighting(Matrix4f matrix, float x, float y, float textureX1, float textureY1, float textureX2, float textureY2, float width, float height, GpuTexture texture, boolean hasLight, MultiTextureRenderTypeRenderer renderer) {
        GuiMap.buildTexturedModalSubRectWithLighting(matrix, renderer.begin(texture), x, y, textureX1, textureY1, textureX2, textureY2, width, height);
    }

    public static void buildTexturedModalRectWithLighting(Matrix4f matrix, class_287 vertexBuffer, float x, float y, float width, float height) {
        vertexBuffer.method_22918(matrix, x + 0.0f, y + height, 0.0f).method_22913(0.0f, 1.0f);
        vertexBuffer.method_22918(matrix, x + width, y + height, 0.0f).method_22913(1.0f, 1.0f);
        vertexBuffer.method_22918(matrix, x + width, y + 0.0f, 0.0f).method_22913(1.0f, 0.0f);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + 0.0f, 0.0f).method_22913(0.0f, 0.0f);
    }

    public static void buildTexturedModalSubRectWithLighting(Matrix4f matrix, class_287 vertexBuffer, float x, float y, float textureX1, float textureY1, float textureX2, float textureY2, float width, float height) {
        vertexBuffer.method_22918(matrix, x + 0.0f, y + height, 0.0f).method_22913(textureX1, textureY2);
        vertexBuffer.method_22918(matrix, x + width, y + height, 0.0f).method_22913(textureX2, textureY2);
        vertexBuffer.method_22918(matrix, x + width, y + 0.0f, 0.0f).method_22913(textureX2, textureY1);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + 0.0f, 0.0f).method_22913(textureX1, textureY1);
    }

    public static void renderTexturedModalRect(Matrix4f matrix, class_4588 vertexBuffer, float x, float y, int textureX, int textureY, float width, float height, float textureWidth, float textureHeight, float r, float g, float b, float a) {
        float normalizedTextureX = (float)textureX / textureWidth;
        float normalizedTextureY = (float)textureY / textureHeight;
        float normalizedTextureX2 = ((float)textureX + width) / textureWidth;
        float normalizedTextureY2 = ((float)textureY + height) / textureHeight;
        vertexBuffer.method_22918(matrix, x + 0.0f, y + height, 0.0f).method_22915(r, g, b, a).method_22913(normalizedTextureX, normalizedTextureY2);
        vertexBuffer.method_22918(matrix, x + width, y + height, 0.0f).method_22915(r, g, b, a).method_22913(normalizedTextureX2, normalizedTextureY2);
        vertexBuffer.method_22918(matrix, x + width, y + 0.0f, 0.0f).method_22915(r, g, b, a).method_22913(normalizedTextureX2, normalizedTextureY);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + 0.0f, 0.0f).method_22915(r, g, b, a).method_22913(normalizedTextureX, normalizedTextureY);
    }

    public void mapClicked(int button, int x, int y) {
        if (button == 1) {
            if (this.viewedOnMousePress != null && this.viewedOnMousePress.isRightClickValid() && (!(this.viewedOnMousePress.getElement() instanceof Waypoint) || SupportMods.xaeroMinimap.waypointExists((Waypoint)this.viewedOnMousePress.getElement()))) {
                this.handleRightClick(this.viewedOnMousePress, (int)((double)x / this.screenScale), (int)((double)y / this.screenScale));
                this.mouseDownPosX = -1;
                this.mouseDownPosY = -1;
                this.mapTileSelection = null;
            } else {
                this.handleRightClick(this, (int)((double)x / this.screenScale), (int)((double)y / this.screenScale));
            }
        }
    }

    private void handleRightClick(IRightClickableElement target, int x, int y) {
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
        }
        this.rightClickMenu = GuiRightClickMenu.getMenu(target, this, x, y, 150);
    }

    public boolean method_25400(char par1, int par2) {
        boolean result = super.method_25400(par1, par2);
        if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().charTyped()) {
            return true;
        }
        if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.charTyped()) {
            return true;
        }
        return result;
    }

    public boolean method_25404(int par1, int par2, int par3) {
        if (par1 == 258) {
            if (this.tabPressed && SupportMods.minimap() && WorldMap.settings.minimapRadar && class_310.method_1551().field_1690.field_1907.method_1417(par1, par2)) {
                return true;
            }
            this.tabPressed = true;
        }
        boolean result = super.method_25404(par1, par2, par3);
        if (this.isUsingTextField()) {
            if (this.waypointMenu && SupportMods.xaeroMinimap.getWaypointMenuRenderer().keyPressed(this, par1)) {
                result = true;
            } else if (this.playersMenu && WorldMap.trackedPlayerMenuRenderer.keyPressed(this, par1)) {
                result = true;
            }
        } else {
            result = this.onInputPress(par1 != -1 ? class_3675.class_307.field_1668 : class_3675.class_307.field_1671, par1 != -1 ? par1 : par2) || result;
        }
        return result;
    }

    public boolean method_16803(int par1, int par2, int par3) {
        if (par1 == 258) {
            this.tabPressed = false;
        }
        if (this.onInputRelease(par1 != -1 ? class_3675.class_307.field_1668 : class_3675.class_307.field_1671, par1 != -1 ? par1 : par2)) {
            return true;
        }
        return super.method_16803(par1, par2, par3);
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }

    private void setColourBuffer(float r, float g, float b, float a) {
        this.colourBuffer[0] = r;
        this.colourBuffer[1] = g;
        this.colourBuffer[2] = b;
        this.colourBuffer[3] = a;
    }

    private boolean isUsingTextField() {
        class_339 currentFocused = (class_339)this.method_25399();
        return currentFocused != null && currentFocused.method_25370() && currentFocused instanceof class_342;
    }

    public void method_25393() {
        super.method_25393();
        if (this.waypointMenu) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().tick();
        }
        if (this.playersMenu) {
            WorldMap.trackedPlayerMenuRenderer.tick();
        }
        this.caveModeOptions.tick(this);
    }

    public class_304 getTrackedPlayerKeyBinding() {
        if (SupportMods.minimap()) {
            return SupportMods.xaeroMinimap.getToggleAllyPlayersKey();
        }
        return ControlsRegister.keyToggleTrackedPlayers;
    }

    private boolean onInputPress(class_3675.class_307 type, int code) {
        IRightClickableElement hoverTarget;
        if (Misc.inputMatchesKeyBinding(type, code, ControlsRegister.keyOpenSettings, 0)) {
            this.onSettingsButton(this.settingsButton);
            return true;
        }
        boolean result = false;
        if (Misc.inputMatchesKeyBinding(type, code, this.field_22787.field_1690.field_1907, 0)) {
            this.field_22787.field_1690.field_1907.method_23481(true);
            result = true;
        }
        if (Misc.inputMatchesKeyBinding(type, code, ControlsRegister.keyOpenMap, 0)) {
            this.goBack();
            result = true;
        }
        if (Misc.inputMatchesKeyBinding(type, code, this.getTrackedPlayerKeyBinding(), 0)) {
            WorldMap.trackedPlayerMenuRenderer.onShowPlayersButton(this, this.field_22789, this.field_22790);
            return true;
        }
        if ((type == class_3675.class_307.field_1668 && code == 257 || Misc.inputMatchesKeyBinding(type, code, ControlsRegister.keyQuickConfirm, 0)) && this.mapSwitchingGui.active) {
            this.mapSwitchingGui.confirm(this, this.field_22787, this.field_22789, this.field_22790);
            result = true;
        }
        if (Misc.inputMatchesKeyBinding(type, code, ControlsRegister.keyToggleDimension, 1)) {
            this.onDimensionToggleButton(this.dimensionToggleButton);
            result = true;
        }
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onMapKeyPressed(type, code, this);
            result = true;
        }
        if (SupportMods.pac()) {
            boolean bl = result = SupportMods.xaeroPac.onMapKeyPressed(type, code, this) || result;
        }
        if ((hoverTarget = this.getHoverTarget()) != null && type == class_3675.class_307.field_1668) {
            boolean isValid = hoverTarget.isRightClickValid();
            if (isValid) {
                if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof Waypoint) {
                    switch (code) {
                        case 72: {
                            SupportMods.xaeroMinimap.disableWaypoint((Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                            break;
                        }
                        case 261: {
                            SupportMods.xaeroMinimap.deleteWaypoint((Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                        }
                    }
                } else if (SupportMods.pac() && hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof PlayerTrackerMapElement) {
                    switch (code) {
                        case 67: {
                            SupportMods.xaeroPac.openPlayerConfigScreen(this, this, (PlayerTrackerMapElement)((HoveredMapElementHolder)hoverTarget).getElement());
                            this.closeRightClick();
                            result = true;
                        }
                    }
                }
            } else {
                this.closeRightClick();
            }
        }
        return result;
    }

    private double getCurrentMapCoordinateScale() {
        return this.mapProcessor.getMapWorld().getCurrentDimension().calculateDimScale(this.mapProcessor.getWorldDimensionTypeRegistry());
    }

    private boolean onInputRelease(class_3675.class_307 type, int code) {
        boolean result = false;
        if (Misc.inputMatchesKeyBinding(type, code, this.field_22787.field_1690.field_1907, 0)) {
            this.field_22787.field_1690.field_1907.method_23481(false);
            result = true;
        }
        if (SupportMods.minimap() && SupportMods.xaeroMinimap.onMapKeyReleased(type, code, this)) {
            result = true;
        }
        if (SupportMods.minimap() && this.lastViewedDimensionId != null && !this.isUsingTextField()) {
            IRightClickableElement hoverTarget;
            int waypointDestinationX = this.mouseBlockPosX;
            int waypointDestinationY = this.mouseBlockPosY;
            int waypointDestinationZ = this.mouseBlockPosZ;
            double waypointDestinationCoordinateScale = this.mouseBlockCoordinateScale;
            boolean waypointDestinationRightClick = false;
            if (this.rightClickMenu != null && this.rightClickMenu.getTarget() == this) {
                waypointDestinationX = this.rightClickX;
                waypointDestinationY = this.rightClickY;
                waypointDestinationZ = this.rightClickZ;
                waypointDestinationCoordinateScale = this.rightClickCoordinateScale;
                waypointDestinationRightClick = true;
            }
            if (Misc.inputMatchesKeyBinding(type, code, SupportMods.xaeroMinimap.getWaypointKeyBinding(), 0) && WorldMap.settings.waypoints) {
                SupportMods.xaeroMinimap.createWaypoint(this, waypointDestinationX, waypointDestinationY == Short.MAX_VALUE ? Short.MAX_VALUE : waypointDestinationY + 1, waypointDestinationZ, waypointDestinationCoordinateScale, waypointDestinationRightClick);
                this.closeRightClick();
                result = true;
            }
            if (Misc.inputMatchesKeyBinding(type, code, SupportMods.xaeroMinimap.getTempWaypointKeyBinding(), 0) && WorldMap.settings.waypoints) {
                this.closeRightClick();
                SupportMods.xaeroMinimap.createTempWaypoint(waypointDestinationX, waypointDestinationY == Short.MAX_VALUE ? Short.MAX_VALUE : waypointDestinationY + 1, waypointDestinationZ, waypointDestinationCoordinateScale, waypointDestinationRightClick);
                result = true;
            }
            if ((hoverTarget = this.getHoverTarget()) != null && !Misc.inputMatchesKeyBinding(type, code, ControlsRegister.keyOpenMap, 0) && type == class_3675.class_307.field_1668) {
                boolean isValid = hoverTarget.isRightClickValid();
                if (isValid) {
                    if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof Waypoint) {
                        switch (code) {
                            case 84: {
                                SupportMods.xaeroMinimap.teleportToWaypoint(this, (Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                                break;
                            }
                            case 69: {
                                SupportMods.xaeroMinimap.openWaypoint(this, (Waypoint)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                            }
                        }
                    } else if (hoverTarget instanceof HoveredMapElementHolder && ((HoveredMapElementHolder)hoverTarget).getElement() instanceof PlayerTrackerMapElement) {
                        switch (code) {
                            case 84: {
                                new PlayerTeleporter().teleportToPlayer(this, this.mapProcessor.getMapWorld(), (PlayerTrackerMapElement)((HoveredMapElementHolder)hoverTarget).getElement());
                                this.closeRightClick();
                                result = true;
                            }
                        }
                    }
                } else {
                    this.closeRightClick();
                }
            }
        }
        return result;
    }

    private IRightClickableElement getHoverTarget() {
        return this.rightClickMenu != null ? this.rightClickMenu.getTarget() : this.viewed;
    }

    private void unfocusAll() {
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.getWaypointMenuRenderer().unfocusAll();
        }
        WorldMap.trackedPlayerMenuRenderer.unfocusAll();
        this.caveModeOptions.unfocusAll();
        this.method_25395(null);
    }

    public void closeRightClick() {
        if (this.rightClickMenu != null) {
            this.rightClickMenu.setClosed(true);
        }
    }

    public void onRightClickClosed() {
        this.rightClickMenu = null;
        this.mapTileSelection = null;
    }

    private void closeDropdowns() {
        if (this.openDropdown != null) {
            this.openDropdown.setClosed(true);
        }
    }

    @Override
    public ArrayList<RightClickOption> getRightClickOptions() {
        ArrayList<RightClickOption> options = new ArrayList<RightClickOption>();
        options.add(new RightClickOption(this, "gui.xaero_right_click_map_title", options.size(), this){

            @Override
            public void onAction(class_437 screen) {
            }
        });
        if (!(!WorldMap.settings.coordinates || SupportMods.minimap() && SupportMods.xaeroMinimap.hidingWaypointCoordinates())) {
            if (this.mapTileSelection != null) {
                String chunkOption = this.mapTileSelection.getStartX() != this.mapTileSelection.getEndX() || this.mapTileSelection.getStartZ() != this.mapTileSelection.getEndZ() ? String.format("C: (%d;%d):(%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop(), this.mapTileSelection.getRight(), this.mapTileSelection.getBottom()) : String.format("C: (%d;%d)", this.mapTileSelection.getLeft(), this.mapTileSelection.getTop());
                options.add(new RightClickOption(this, chunkOption, options.size(), this){

                    @Override
                    public void onAction(class_437 screen) {
                    }
                });
            }
            options.add(new RightClickOption(this, String.format(this.rightClickY != Short.MAX_VALUE ? "X: %1$d, Y: %2$d, Z: %3$d" : "X: %1$d, Z: %3$d", this.rightClickX, this.rightClickY, this.rightClickZ), options.size(), this){

                @Override
                public void onAction(class_437 screen) {
                }
            });
        }
        if (SupportMods.minimap() && WorldMap.settings.waypoints) {
            options.add(new RightClickOption("gui.xaero_right_click_map_create_waypoint", options.size(), this){

                @Override
                public void onAction(class_437 screen) {
                    SupportMods.xaeroMinimap.createWaypoint(GuiMap.this, GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ, GuiMap.this.rightClickCoordinateScale, true);
                }
            }.setNameFormatArgs(Misc.getKeyName(SupportMods.xaeroMinimap.getWaypointKeyBinding())));
            options.add(new RightClickOption("gui.xaero_right_click_map_create_temporary_waypoint", options.size(), this){

                @Override
                public void onAction(class_437 screen) {
                    SupportMods.xaeroMinimap.createTempWaypoint(GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ, GuiMap.this.rightClickCoordinateScale, true);
                }
            }.setNameFormatArgs(Misc.getKeyName(SupportMods.xaeroMinimap.getTempWaypointKeyBinding())));
        }
        MapDimension currentDimension = this.mapProcessor.getMapWorld().getCurrentDimension();
        if (!this.field_22787.field_1761.method_2908() || currentDimension != null) {
            if (this.mapProcessor.getMapWorld().isTeleportAllowed() && (this.rightClickY != Short.MAX_VALUE || !this.field_22787.field_1761.method_2908())) {
                options.add(new RightClickOption("gui.xaero_right_click_map_teleport", options.size(), this){

                    @Override
                    public void onAction(class_437 screen) {
                        MapDimension currentDimension = GuiMap.this.mapProcessor.getMapWorld().getCurrentDimension();
                        if (!(((GuiMap)GuiMap.this).field_22787.field_1761.method_2908() && currentDimension == null || GuiMap.this.rightClickY == Short.MAX_VALUE && ((GuiMap)GuiMap.this).field_22787.field_1761.method_2908())) {
                            class_5321<class_1937> tpDim = GuiMap.this.rightClickDim != ((GuiMap)GuiMap.this).field_22787.field_1687.method_27983() ? GuiMap.this.rightClickDim : null;
                            new MapTeleporter().teleport(GuiMap.this, GuiMap.this.mapProcessor.getMapWorld(), GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ, tpDim);
                        }
                    }
                });
            } else if (!this.mapProcessor.getMapWorld().isTeleportAllowed()) {
                options.add(new RightClickOption(this, "gui.xaero_wm_right_click_map_teleport_not_allowed", options.size(), this){

                    @Override
                    public void onAction(class_437 screen) {
                    }
                });
            } else {
                options.add(new RightClickOption(this, "gui.xaero_right_click_map_cant_teleport", options.size(), this){

                    @Override
                    public void onAction(class_437 screen) {
                    }
                });
            }
        } else {
            options.add(new RightClickOption(this, "gui.xaero_right_click_map_cant_teleport_world", options.size(), this){

                @Override
                public void onAction(class_437 screen) {
                }
            });
        }
        if (SupportMods.minimap()) {
            options.add(new RightClickOption("gui.xaero_right_click_map_share_location", options.size(), this){

                @Override
                public void onAction(class_437 screen) {
                    SupportMods.xaeroMinimap.shareLocation(GuiMap.this, GuiMap.this.rightClickX, GuiMap.this.rightClickY == Short.MAX_VALUE ? Short.MAX_VALUE : GuiMap.this.rightClickY + 1, GuiMap.this.rightClickZ);
                }
            });
            if (WorldMap.settings.waypoints) {
                options.add(new RightClickOption("gui.xaero_right_click_map_waypoints_menu", options.size(), this){

                    @Override
                    public void onAction(class_437 screen) {
                        SupportMods.xaeroMinimap.openWaypointsMenu(GuiMap.this.field_22787, GuiMap.this);
                    }
                }.setNameFormatArgs(Misc.getKeyName(SupportMods.xaeroMinimap.getTempWaypointsMenuKeyBinding())));
            }
        }
        if (SupportMods.pac()) {
            SupportMods.xaeroPac.addRightClickOptions(this, options, this.mapTileSelection, this.mapProcessor);
        }
        options.add(new RightClickOption("gui.xaero_right_click_box_map_export", options.size(), this){

            @Override
            public void onAction(class_437 screen) {
                GuiMap.this.onExportButton(GuiMap.this.exportButton);
            }
        });
        options.add(new RightClickOption("gui.xaero_right_click_box_map_settings", options.size(), this){

            @Override
            public void onAction(class_437 screen) {
                GuiMap.this.onSettingsButton(GuiMap.this.settingsButton);
            }
        }.setNameFormatArgs(Misc.getKeyName(ControlsRegister.keyOpenSettings)));
        return options;
    }

    @Override
    public boolean isRightClickValid() {
        return true;
    }

    @Override
    public int getRightClickTitleBackgroundColor() {
        return -10461088;
    }

    @Override
    public boolean shouldSkipWorldRender() {
        return true;
    }

    public double getUserScale() {
        return this.userScale;
    }

    public class_4185 getRadarButton() {
        return this.radarButton;
    }

    @Override
    public void onDropdownOpen(DropDownWidget menu) {
        super.onDropdownOpen(menu);
        this.unfocusAll();
    }

    @Override
    public void onDropdownClosed(DropDownWidget menu) {
        super.onDropdownClosed(menu);
        if (menu == this.rightClickMenu) {
            this.onRightClickClosed();
        }
    }

    public void onCaveModeStartSet() {
        this.caveModeOptions.onCaveModeStartSet(this);
    }

    public MapDimension getFutureDimension() {
        return this.futureDimension;
    }

    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }

    public void enableCaveModeOptions() {
        if (!this.caveModeOptions.isEnabled()) {
            this.caveModeOptions.toggle(this);
        }
    }

    static {
        identityMatrix.identity();
    }
}

