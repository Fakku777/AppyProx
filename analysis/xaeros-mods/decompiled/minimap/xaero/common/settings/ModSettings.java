/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_1132
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_437
 *  net.minecraft.class_7923
 */
package xaero.common.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import net.minecraft.class_1074;
import net.minecraft.class_1132;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_7923;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.gui.GuiSettings;
import xaero.common.gui.GuiSlimeSeed;
import xaero.common.minimap.MinimapProcessor;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.settings.ModOptions;
import xaero.hud.HudSession;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.info.BuiltInInfoDisplays;
import xaero.hud.minimap.info.InfoDisplayIO;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.category.EntityRadarBackwardsCompatibilityConfig;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.path.XaeroPath;
import xaero.hud.path.XaeroPathReader;

public class ModSettings {
    public static int defaultSettings;
    public static int ignoreUpdate;
    public static final String format = "\u00a7";
    protected IXaeroMinimap modMain;
    private EntityRadarBackwardsCompatibilityConfig entityRadarBackwardsCompatibilityConfig;
    private boolean foundOldRadarSettings;
    public static final String[] ENCHANT_COLORS;
    public static final String[] ENCHANT_COLOR_NAMES;
    public static final int[] COLORS;
    public static int serverSettings;
    public static String minimapItemId;
    public static class_1792 minimapItem;
    public int zoom = 0;
    public static final float[] zooms;
    public int caveMaps = 2;
    public int caveZoom = 1;
    private boolean showWaypoints = true;
    private boolean deathpoints = true;
    private boolean oldDeathpoints = true;
    public int chunkGrid = -1;
    public boolean slimeChunks = false;
    private static HashMap<XaeroPath, Long> serverSlimeSeeds;
    private boolean showIngameWaypoints = true;
    private boolean lockNorth = false;
    private boolean antiAliasing = true;
    private boolean displayRedstone = true;
    public boolean mapSafeMode = false;
    public int distance = 1;
    public static final String[] distanceTypes;
    private int blockColours = 0;
    public static final String[] blockColourTypes;
    private boolean lighting = true;
    public boolean compassOverEverything = true;
    private int minimapSize = 0;
    public double minimapOpacity = 100.0;
    public double waypointsIngameCloseScale = 1.0;
    private int waypointsIngameIconScale = 0;
    private int waypointsIngameDistanceScale = 0;
    private int waypointsIngameNameScale = 0;
    private static final float DEFAULT_SCALE = 0.8f;
    private static final float MINECRAFT_SCALE = 0.02666667f;
    private static final double WAYPOINT_ICON_WORLD_SCALE = 0.02133333496749401;
    private double dotNameScale = 1.0;
    public static boolean settingsButton;
    public static boolean updateNotification;
    private boolean showFlowers = true;
    public boolean keepWaypointNames = true;
    private int waypointsDistanceExp = 0;
    public double waypointsDistanceMin = 0.0;
    public String defaultWaypointTPCommandFormat = "/tp @s {x} {y} {z}";
    public String defaultWaypointTPCommandRotationFormat = "/tp @s {x} {y} {z} {yaw} ~";
    public double arrowScale = 1.5;
    public int arrowColour = 0;
    public String[] arrowColourNames = new String[]{"gui.xaero_red", "gui.xaero_green", "gui.xaero_blue", "gui.xaero_yellow", "gui.xaero_purple", "gui.xaero_white", "gui.xaero_black", "gui.xaero_legacy_color"};
    public static float[][] arrowColours;
    public boolean smoothDots = true;
    public static final String[] ENTITY_ICONS_OPTIONS;
    private boolean worldMap = true;
    private boolean terrainDepth = true;
    private static final String[] SLOPES_MODES;
    private int terrainSlopes = 2;
    public int mainEntityAs = 0;
    public boolean blockTransparency = true;
    public int waypointOpacityIngame = 80;
    public int waypointOpacityMap = 90;
    public boolean allowWrongWorldTeleportation = false;
    public int hideWorldNames = 1;
    public boolean openSlimeSettings = true;
    public boolean alwaysShowDistance = false;
    public static final String[] ENTITY_NAMES_OPTIONS;
    private static final String[] SHOW_LIGHT_LEVEL_NAMES;
    public int renderLayerIndex = 1;
    public boolean crossDimensionalTp = true;
    public boolean differentiateByServerAddress = true;
    private boolean biomeColorsVanillaMode = false;
    public int lookingAtAngle = 10;
    public int lookingAtAngleVertical = 180;
    public boolean centeredEnlarged = false;
    public int zoomOnEnlarged = 0;
    public int minimapTextAlign = 0;
    public boolean waypointsMutualEdit = true;
    private int caveMapsDepth = 30;
    public boolean hideWaypointCoordinates = false;
    public boolean renderAllSets = false;
    public int playerArrowOpacity = 100;
    public boolean waypointsBottom;
    private static final String[] MINIMAP_SHAPES;
    public int minimapShape;
    public int lightOverlayType;
    public int lightOverlayMaxLight = 7;
    public int lightOverlayMinLight = 0;
    public int lightOverlayColor = 13;
    public static final String[] DOTS_STYLES;
    private int dotsStyle = 0;
    public boolean debugEntityIcons;
    private int uiScale = 0;
    public static final String[] PUSHBOX_OPTIONS;
    public int bossHealthPushBox = 1;
    public int potionEffectPushBox = 1;
    public static final String[] FRAME_OPTIONS;
    public int minimapFrame = 0;
    public int minimapFrameColor = 9;
    public static final String[] COMPASS_OPTIONS;
    public int compassLocation = 1;
    private int compassDirectionScale = 0;
    public int compassColor = 9;
    private int northCompassColor = -1;
    public boolean debugEntityVariantIds;
    private static final String[] MULTIPLE_WAYPOINT_INFO;
    public int displayMultipleWaypointInfo = 1;
    private boolean entityRadar = true;
    public boolean adjustHeightForCarpetLikeBlocks = true;
    public int autoConvertWaypointDistanceToKmThreshold = 10000;
    public int waypointDistancePrecision = 1;
    public int mainDotSize = 2;
    public boolean partialYTeleportation = true;
    public boolean deleteReachedDeathpoints = true;
    public boolean hideMinimapUnderScreen = true;
    public boolean hideMinimapUnderF3 = true;
    public boolean manualCaveModeStartAuto = true;
    public int manualCaveModeStart = -1;
    public int chunkGridLineWidth = 1;
    public boolean temporaryWaypointsGlobal = true;
    public boolean keepUnlockedWhenEnlarged = false;
    public boolean enlargedMinimapAToggle = false;
    public static final String[] RADAR_OVER_MAP_OPTIONS;
    public boolean displayTrackedPlayersOnMap = true;
    public boolean displayTrackedPlayersInWorld = true;
    private boolean displayClaims = true;
    public boolean displayCurrentClaim = true;
    private int claimsFillOpacity = 46;
    private int claimsBorderOpacity = 80;
    public boolean radarHideInvisibleEntities = true;
    private boolean displayStainedGlass = true;
    public int waypointOnMapScale = 0;
    public boolean switchToAutoOnDeath = true;
    public int infoDisplayBackgroundOpacity = 40;
    public int caveModeToggleTimer = 1000;
    private boolean legibleCaveMaps;
    private boolean biomeBlending = true;
    public boolean allowInternetAccess = true;
    public boolean dimensionScaledMaxWaypointDistance = true;
    private int trackedPlayerWorldIconScale;
    private int trackedPlayerWorldNameScale;
    private int trackedPlayerMinimapIconScale;
    private static int[] OLD_MINIMAP_SIZES;

    public ModSettings(IXaeroMinimap modMain) {
        this.modMain = modMain;
        this.entityRadarBackwardsCompatibilityConfig = new EntityRadarBackwardsCompatibilityConfig();
        int n = defaultSettings = modMain.getVersionID().endsWith("fair") ? 16188159 : Integer.MAX_VALUE;
        if (serverSettings == 0) {
            serverSettings = defaultSettings;
        }
    }

    public boolean isKeyRepeat(class_304 kb) {
        return true;
    }

    public boolean getMinimap() {
        return BuiltInHudModules.MINIMAP.isActive() && !this.minimapDisabled() && (minimapItem == null || class_310.method_1551().field_1724 == null || MinimapProcessor.hasMinimapItem((class_1657)class_310.method_1551().field_1724));
    }

    public boolean getCaveMaps(boolean manualCaveMode) {
        return (manualCaveMode || this.caveMaps > 0) && !this.caveMapsDisabled();
    }

    public boolean getShowWaypoints() {
        return this.showWaypoints && !this.showWaypointsDisabled();
    }

    public boolean getDeathpoints() {
        return this.deathpoints && !this.deathpointsDisabled();
    }

    public boolean getOldDeathpoints() {
        return this.oldDeathpoints;
    }

    public void setSlimeChunksSeed(long seed, XaeroPath fullWorldID) {
        serverSlimeSeeds.put(fullWorldID, seed);
    }

    public Long getSlimeChunksSeed(XaeroPath fullWorldID) {
        class_1132 sp = class_310.method_1551().method_1576();
        if (sp == null) {
            return serverSlimeSeeds.get(fullWorldID);
        }
        try {
            if (class_310.method_1551().field_1687.method_27983() != class_1937.field_25179) {
                return null;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        long seed = sp.method_3847(class_1937.field_25179).method_8412();
        return seed;
    }

    public boolean customSlimeSeedNeeded(HudSession minimapSession) {
        return !(class_310.method_1551().field_1755 instanceof GuiSlimeSeed) && class_310.method_1551().method_1576() == null && minimapSession != null;
    }

    public boolean getSlimeChunks(MinimapSession session) {
        return this.slimeChunks && (class_310.method_1551().method_1576() != null || this.getSlimeChunksSeed(session.getWorldState().getCurrentWorldPath()) != null);
    }

    public boolean getShowIngameWaypoints() {
        return this.showIngameWaypoints && !this.showWaypointsDisabled() && (minimapItem == null || class_310.method_1551().field_1724 == null || MinimapProcessor.hasMinimapItem((class_1657)class_310.method_1551().field_1724));
    }

    public boolean waypointsGUI(MinimapSession waypointSession) {
        return class_310.method_1551().field_1724 != null && waypointSession.getWorldState().getAutoWorldPath() != null && (minimapItem == null || class_310.method_1551().field_1724 == null || MinimapProcessor.hasMinimapItem((class_1657)class_310.method_1551().field_1724));
    }

    public boolean getLockNorth(int mapSize, int shape) {
        if (mapSize > 180 && shape == 0) {
            return true;
        }
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession == null) {
            return this.lockNorth;
        }
        return this.lockNorth || !this.keepUnlockedWhenEnlarged && minimapSession.getMinimapProcessor().isEnlargedMap();
    }

    public boolean getAntiAliasing() {
        return this.antiAliasing && this.assumeUsingFBO();
    }

    public boolean getDisplayRedstone() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.displayRedstone;
        }
        return this.displayRedstone;
    }

    public int getBlockColours() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapColours();
        }
        return this.blockColours;
    }

    public boolean getLighting() {
        return this.lighting;
    }

    public int getMinimapSize() {
        int width;
        if (this.minimapSize > 0) {
            return this.minimapSize;
        }
        int height = class_310.method_1551().method_22683().method_4506();
        int size = (int)((float)(height <= (width = class_310.method_1551().method_22683().method_4489()) ? height : width) / this.getMinimapScale());
        return Math.min(Math.max((int)(ModOptions.SIZE.getValueMin() + ModOptions.SIZE.getValueStep()), 2 * size * 130 / 1080), (int)ModOptions.SIZE.getValueMax());
    }

    public float getMinimapScale() {
        return this.getUIScale(this.uiScale, 1, 11);
    }

    public float getUIScale(int optionValue, int min, int max) {
        if (optionValue <= min) {
            return this.getAutoUIScale();
        }
        if (optionValue == max) {
            return class_310.method_1551().method_22683().method_4495();
        }
        return optionValue;
    }

    public int getAutoUIScale() {
        int width;
        int size;
        int height = class_310.method_1551().method_22683().method_4506();
        int n = size = height <= (width = class_310.method_1551().method_22683().method_4489()) ? height : width;
        if (size >= 1500) {
            int steps = size / 500;
            return steps;
        }
        return 2;
    }

    public float getWaypointsIngameIconScale() {
        return this.getUIScale(this.waypointsIngameIconScale, 0, 17);
    }

    public float getWaypointsIngameDistanceScale() {
        return this.getUIScale(this.waypointsIngameDistanceScale, 0, 17);
    }

    public int getWaypointsIngameNameScale() {
        int scale = (int)this.getUIScale(this.waypointsIngameNameScale, 0, 17);
        if (this.waypointsIngameNameScale <= 0) {
            return (int)Math.ceil(scale / 2);
        }
        return scale;
    }

    public float getTrackedPlayerWorldIconScale() {
        return this.getUIScale(this.trackedPlayerWorldIconScale, 0, 17);
    }

    public float getTrackedPlayerWorldNameScale() {
        return this.getUIScale(this.trackedPlayerWorldNameScale, 0, 17);
    }

    public float getTrackedPlayerMinimapIconScale() {
        return this.getUIScale(this.trackedPlayerMinimapIconScale, 0, 17);
    }

    public double getWaypointsClampDepth(double fov, int height) {
        int baseIconScale = (int)this.getWaypointsIngameIconScale();
        double frameSizeAtClampDepth = this.waypointsIngameCloseScale * 0.02133333496749401 * (double)height / (double)baseIconScale;
        double fovMultiplier = 2.0 * Math.tan(Math.toRadians(fov / 2.0));
        return frameSizeAtClampDepth / fovMultiplier;
    }

    public double getDotNameScale() {
        return this.dotNameScale * (class_310.method_1551().method_1573() ? 2.0 : 1.0);
    }

    public boolean getShowFlowers() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapFlowers();
        }
        return this.showFlowers;
    }

    public double getMaxWaypointsDistance() {
        if (this.waypointsDistanceExp <= 0) {
            return 0.0;
        }
        return Math.pow(2.0, 2 + this.waypointsDistanceExp);
    }

    public boolean getSmoothDots() {
        return this.smoothDots && this.assumeUsingFBO();
    }

    public boolean getUseWorldMap() {
        return this.worldMap && this.assumeUsingFBO();
    }

    private boolean assumeUsingFBO() {
        return !this.modMain.getMinimap().getMinimapFBORenderer().isTriedFBO() && !this.mapSafeMode || this.modMain.getMinimap().usingFBO();
    }

    public boolean getTerrainDepth() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapTerrainDepth();
        }
        return this.terrainDepth;
    }

    public int getTerrainSlopes() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapTerrainSlopes();
        }
        return this.terrainSlopes;
    }

    public boolean getBiomeColorsVanillaMode() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapBiomeColorsVanillaMode();
        }
        return this.biomeColorsVanillaMode;
    }

    public int getCaveMapsDepth() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getCaveModeDepth();
        }
        return this.caveMapsDepth;
    }

    public int getCompassScale() {
        return this.compassDirectionScale;
    }

    public int getNorthCompassColor() {
        if (this.northCompassColor < 0) {
            return this.compassColor;
        }
        return this.northCompassColor;
    }

    public boolean getPartialYTeleportation() {
        if (!this.modMain.getSupportMods().worldmap()) {
            return this.partialYTeleportation;
        }
        return this.modMain.getSupportMods().worldmapSupport.getPartialYTeleport();
    }

    public boolean getDisplayClaims() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getDisplayClaims();
        }
        return this.displayClaims;
    }

    public int getClaimsFillOpacity() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getClaimsFillOpacity();
        }
        return this.claimsFillOpacity;
    }

    public int getClaimsBorderOpacity() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getClaimsBorderOpacity();
        }
        return this.claimsBorderOpacity;
    }

    public boolean getBiomeBlending() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getBiomeBlending();
        }
        return this.biomeBlending;
    }

    public boolean isLegibleCaveMaps() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.isLegibleCaveMaps();
        }
        return this.legibleCaveMaps;
    }

    public boolean isStainedGlassDisplayed() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.isStainedGlassDisplayed();
        }
        return this.displayStainedGlass;
    }

    public boolean getAdjustHeightForCarpetLikeBlocks() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getAdjustHeightForCarpetLikeBlocks();
        }
        return this.adjustHeightForCarpetLikeBlocks;
    }

    public boolean getEntityRadar() {
        return this.entityRadar && !this.modMain.isFairPlay() && (class_310.method_1551().field_1687 == null || MinimapClientWorldDataHelper.getCurrentWorldData().getSyncedRules().allowRadarOnServer);
    }

    public int getDotsStyle() {
        if (this.assumeUsingFBO()) {
            return this.dotsStyle;
        }
        return 1;
    }

    public boolean isIgnoreHeightmaps() {
        if (this.modMain.getSupportMods().shouldUseWorldMapChunks()) {
            return this.modMain.getSupportMods().worldmapSupport.getWorldMapIgnoreHeightmaps();
        }
        MinimapWorldRootContainer currentRootContainer = BuiltInHudModules.MINIMAP.getCurrentSession().getWorldManager().getAutoRootContainer();
        return currentRootContainer.getConfig().isIgnoreHeightmaps();
    }

    public void writeSettings(PrintWriter writer) {
        writer.println("#CONFIG ONLY OPTIONS");
        writer.println("ignoreUpdate:" + ignoreUpdate);
        writer.println("settingsButton:" + settingsButton);
        if (minimapItemId != null) {
            writer.println("minimapItemId:" + minimapItemId);
        }
        writer.println("allowWrongWorldTeleportation:" + this.allowWrongWorldTeleportation);
        writer.println("differentiateByServerAddress:" + this.differentiateByServerAddress);
        writer.println("debugEntityIcons:" + this.debugEntityIcons);
        writer.println("debugEntityVariantIds:" + this.debugEntityVariantIds);
        writer.println("radarHideInvisibleEntities:" + this.radarHideInvisibleEntities);
        writer.println("allowInternetAccess:" + this.allowInternetAccess);
        writer.println("#INGAME SETTINGS (DO NOT EDIT!)");
        writer.println("updateNotification:" + updateNotification);
        writer.println("minimap:" + BuiltInHudModules.MINIMAP.isActive());
        writer.println("caveMaps:" + this.caveMaps);
        writer.println("caveZoom:" + this.caveZoom);
        writer.println("showWaypoints:" + this.showWaypoints);
        writer.println("showIngameWaypoints:" + this.showIngameWaypoints);
        writer.println("displayRedstone:" + this.displayRedstone);
        writer.println("deathpoints:" + this.deathpoints);
        writer.println("oldDeathpoints:" + this.oldDeathpoints);
        writer.println("distance:" + this.distance);
        writer.println("lockNorth:" + this.lockNorth);
        writer.println("zoom:" + this.zoom);
        writer.println("minimapSize:" + this.minimapSize);
        writer.println("chunkGrid:" + this.chunkGrid);
        writer.println("slimeChunks:" + this.slimeChunks);
        writer.println("mapSafeMode:" + this.mapSafeMode);
        writer.println("minimapOpacity:" + this.minimapOpacity);
        writer.println("waypointsIngameIconScale:" + this.waypointsIngameIconScale);
        writer.println("waypointsIngameDistanceScale:" + this.waypointsIngameDistanceScale);
        writer.println("waypointsIngameNameScale:" + this.waypointsIngameNameScale);
        writer.println("waypointsIngameCloseScale:" + this.waypointsIngameCloseScale);
        writer.println("antiAliasing:" + this.antiAliasing);
        writer.println("blockColours:" + this.blockColours);
        writer.println("lighting:" + this.lighting);
        writer.println("dotsStyle:" + this.dotsStyle);
        writer.println("dotNameScale:" + this.dotNameScale);
        writer.println("compassOverEverything:" + this.compassOverEverything);
        writer.println("showFlowers:" + this.showFlowers);
        writer.println("keepWaypointNames:" + this.keepWaypointNames);
        writer.println("waypointsDistanceExp:" + this.waypointsDistanceExp);
        writer.println("waypointsDistanceMin:" + this.waypointsDistanceMin);
        writer.println("defaultWaypointTPCommandFormat:" + this.defaultWaypointTPCommandFormat.replace(":", "^col^"));
        writer.println("defaultWaypointTPCommandRotationFormat:" + this.defaultWaypointTPCommandRotationFormat.replace(":", "^col^"));
        writer.println("arrowScale:" + this.arrowScale);
        writer.println("arrowColour:" + this.arrowColour);
        writer.println("smoothDots:" + this.smoothDots);
        writer.println("worldMap:" + this.worldMap);
        writer.println("terrainDepth:" + this.terrainDepth);
        writer.println("terrainSlopes:" + this.terrainSlopes);
        writer.println("mainEntityAs:" + this.mainEntityAs);
        writer.println("blockTransparency:" + this.blockTransparency);
        writer.println("waypointOpacityIngame:" + this.waypointOpacityIngame);
        writer.println("waypointOpacityMap:" + this.waypointOpacityMap);
        writer.println("hideWorldNames:" + this.hideWorldNames);
        writer.println("openSlimeSettings:" + this.openSlimeSettings);
        writer.println("alwaysShowDistance:" + this.alwaysShowDistance);
        writer.println("renderLayerIndex:" + this.renderLayerIndex);
        writer.println("crossDimensionalTp:" + this.crossDimensionalTp);
        writer.println("biomeColorsVanillaMode:" + this.biomeColorsVanillaMode);
        writer.println("lookingAtAngle:" + this.lookingAtAngle);
        writer.println("lookingAtAngleVertical:" + this.lookingAtAngleVertical);
        writer.println("centeredEnlarged:" + this.centeredEnlarged);
        writer.println("zoomOnEnlarged:" + this.zoomOnEnlarged);
        writer.println("minimapTextAlign:" + this.minimapTextAlign);
        writer.println("waypointsMutualEdit:" + this.waypointsMutualEdit);
        writer.println("compassLocation:" + this.compassLocation);
        writer.println("compassDirectionScale:" + this.compassDirectionScale);
        writer.println("caveMapsDepth:" + this.caveMapsDepth);
        writer.println("hideWaypointCoordinates:" + this.hideWaypointCoordinates);
        writer.println("renderAllSets:" + this.renderAllSets);
        writer.println("playerArrowOpacity:" + this.playerArrowOpacity);
        writer.println("waypointsBottom:" + this.waypointsBottom);
        writer.println("minimapShape:" + this.minimapShape);
        writer.println("lightOverlayType:" + this.lightOverlayType);
        writer.println("lightOverlayMaxLight:" + this.lightOverlayMaxLight);
        writer.println("lightOverlayMinLight:" + this.lightOverlayMinLight);
        writer.println("lightOverlayColor:" + this.lightOverlayColor);
        writer.println("uiScale:" + this.uiScale);
        writer.println("bossHealthPushBox:" + this.bossHealthPushBox);
        writer.println("potionEffectPushBox:" + this.potionEffectPushBox);
        writer.println("minimapFrame:" + this.minimapFrame);
        writer.println("minimapFrameColor:" + this.minimapFrameColor);
        writer.println("compassColor:" + this.compassColor);
        writer.println("northCompassColor:" + this.northCompassColor);
        writer.println("displayMultipleWaypointInfo:" + this.displayMultipleWaypointInfo);
        writer.println("entityRadar:" + this.entityRadar);
        writer.println("adjustHeightForCarpetLikeBlocks:" + this.adjustHeightForCarpetLikeBlocks);
        writer.println("autoConvertWaypointDistanceToKmThreshold:" + this.autoConvertWaypointDistanceToKmThreshold);
        writer.println("waypointDistancePrecision:" + this.waypointDistancePrecision);
        writer.println("mainDotSize:" + this.mainDotSize);
        writer.println("partialYTeleportation:" + this.partialYTeleportation);
        writer.println("deleteReachedDeathpoints:" + this.deleteReachedDeathpoints);
        writer.println("hideMinimapUnderScreen:" + this.hideMinimapUnderScreen);
        writer.println("hideMinimapUnderF3:" + this.hideMinimapUnderF3);
        writer.println("manualCaveModeStartAuto:" + this.manualCaveModeStartAuto);
        writer.println("manualCaveModeStart:" + this.manualCaveModeStart);
        writer.println("chunkGridLineWidth:" + this.chunkGridLineWidth);
        writer.println("temporaryWaypointsGlobal:" + this.temporaryWaypointsGlobal);
        writer.println("keepUnlockedWhenEnlarged:" + this.keepUnlockedWhenEnlarged);
        writer.println("enlargedMinimapAToggle:" + this.enlargedMinimapAToggle);
        writer.println("displayStainedGlass:" + this.displayStainedGlass);
        writer.println("waypointOnMapScale:" + this.waypointOnMapScale);
        writer.println("switchToAutoOnDeath:" + this.switchToAutoOnDeath);
        writer.println("infoDisplayBackgroundOpacity:" + this.infoDisplayBackgroundOpacity);
        writer.println("caveModeToggleTimer:" + this.caveModeToggleTimer);
        writer.println("legibleCaveMaps:" + this.legibleCaveMaps);
        writer.println("biomeBlending:" + this.biomeBlending);
        writer.println("displayTrackedPlayersOnMap:" + this.displayTrackedPlayersOnMap);
        writer.println("displayTrackedPlayersInWorld:" + this.displayTrackedPlayersInWorld);
        writer.println("dimensionScaledMaxWaypointDistance:" + this.dimensionScaledMaxWaypointDistance);
        writer.println("trackedPlayerWorldIconScale:" + this.trackedPlayerWorldIconScale);
        writer.println("trackedPlayerWorldNameScale:" + this.trackedPlayerWorldNameScale);
        writer.println("trackedPlayerMinimapIconScale:" + this.trackedPlayerMinimapIconScale);
        writer.println("displayClaims:" + this.displayClaims);
        writer.println("displayCurrentClaim:" + this.displayCurrentClaim);
        writer.println("claimsFillOpacity:" + this.claimsFillOpacity);
        writer.println("claimsBorderOpacity:" + this.claimsBorderOpacity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveSettings() throws IOException {
        try (PrintWriter writer = null;){
            writer = new PrintWriter(new FileWriter(this.modMain.getConfigFile().toFile()));
            this.writeSettings(writer);
            this.modMain.getMinimap().getInfoDisplays().getIo().save(writer);
            Object[] keys = serverSlimeSeeds.keySet().toArray();
            Object[] values = serverSlimeSeeds.values().toArray();
            for (int i = 0; i < keys.length; ++i) {
                writer.println("seed:" + String.valueOf(keys[i]) + ":" + String.valueOf(values[i]));
            }
            this.modMain.getHudIO().save(writer);
        }
    }

    public void readSetting(String[] args) {
        String valueString;
        String string = valueString = args.length < 2 ? "" : args[1];
        if (args[0].equalsIgnoreCase("ignoreUpdate")) {
            ignoreUpdate = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("updateNotification")) {
            updateNotification = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("settingsButton")) {
            settingsButton = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("minimapItemId")) {
            minimapItemId = valueString + ":" + args[2];
            minimapItem = (class_1792)class_7923.field_41178.method_63535(class_2960.method_60655((String)valueString, (String)args[2]));
            MinimapLogs.LOGGER.info("Minimap item set: " + minimapItem.method_63680().getString());
        } else if (args[0].equalsIgnoreCase("allowWrongWorldTeleportation")) {
            this.allowWrongWorldTeleportation = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("differentiateByServerAddress")) {
            this.differentiateByServerAddress = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("debugEntityIcons")) {
            this.debugEntityIcons = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("debugEntityVariantIds")) {
            this.debugEntityVariantIds = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("radarHideInvisibleEntities")) {
            this.radarHideInvisibleEntities = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("allowInternetAccess")) {
            this.allowInternetAccess = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("minimap")) {
            BuiltInHudModules.MINIMAP.setActive(valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("caveMaps")) {
            this.caveMaps = valueString.equals("true") ? 1 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("caveZoom")) {
            this.caveZoom = valueString.equals("true") ? 2 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("showWaypoints")) {
            this.showWaypoints = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("deathpoints")) {
            this.deathpoints = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("oldDeathpoints")) {
            this.oldDeathpoints = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("showIngameWaypoints")) {
            this.showIngameWaypoints = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("displayRedstone")) {
            this.displayRedstone = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("distance")) {
            this.distance = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("showCoords")) {
            BuiltInInfoDisplays.COORDINATES.setState(valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("lockNorth")) {
            this.lockNorth = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("zoom")) {
            this.zoom = Integer.parseInt(valueString);
            if (this.zoom >= zooms.length) {
                this.zoom = zooms.length - 1;
            }
        } else if (args[0].equalsIgnoreCase("mapSize")) {
            int oldSize = Integer.parseInt(valueString);
            this.minimapSize = oldSize == -1 ? 0 : OLD_MINIMAP_SIZES[oldSize];
        } else if (args[0].equalsIgnoreCase("minimapSize")) {
            this.minimapSize = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("chunkGrid")) {
            this.chunkGrid = valueString.equals("true") ? 0 : (valueString.equals("false") ? -1 : Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("slimeChunks")) {
            this.slimeChunks = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("mapSafeMode")) {
            this.mapSafeMode = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("minimapOpacity")) {
            this.minimapOpacity = Double.valueOf(valueString);
        } else if (args[0].equalsIgnoreCase("waypointsIngameIconScale")) {
            this.waypointsIngameIconScale = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("waypointsIngameDistanceScale")) {
            this.waypointsIngameDistanceScale = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("waypointsIngameNameScale")) {
            this.waypointsIngameNameScale = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("waypointsIngameCloseScale")) {
            this.waypointsIngameCloseScale = Double.valueOf(valueString);
        } else if (args[0].equalsIgnoreCase("antiAliasing")) {
            this.antiAliasing = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("blockColours")) {
            this.blockColours = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("lighting")) {
            this.lighting = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("dotsStyle")) {
            this.dotsStyle = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("dotNameScale")) {
            this.dotNameScale = Double.valueOf(valueString);
        } else if (args[0].equalsIgnoreCase("compassOverEverything")) {
            this.compassOverEverything = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("showBiome")) {
            BuiltInInfoDisplays.BIOME.setState(valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("showFlowers")) {
            this.showFlowers = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("keepWaypointNames")) {
            this.keepWaypointNames = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("waypointsDistance")) {
            double oldValue = Double.valueOf(valueString);
            this.waypointsDistanceExp = oldValue <= 0.0 ? 0 : (int)Math.max(3.0, Math.ceil(Math.log(oldValue) / Math.log(2.0))) - 2;
        } else if (args[0].equalsIgnoreCase("waypointsDistanceExp")) {
            this.waypointsDistanceExp = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("waypointsDistanceMin")) {
            this.waypointsDistanceMin = Double.valueOf(valueString);
        } else if (args[0].equalsIgnoreCase("waypointTp")) {
            this.defaultWaypointTPCommandFormat = "/" + valueString + " {x} {y} {z}";
            this.defaultWaypointTPCommandRotationFormat = "/" + valueString + " {x} {y} {z} {yaw} ~";
        } else if (args[0].equalsIgnoreCase("waypointTPCommand")) {
            this.defaultWaypointTPCommandFormat = valueString.replace("^col^", ":") + " {x} {y} {z}";
            this.defaultWaypointTPCommandRotationFormat = valueString.replace("^col^", ":") + " {x} {y} {z} {yaw} ~";
        } else if (args[0].equalsIgnoreCase("defaultWaypointTPCommandFormat")) {
            this.defaultWaypointTPCommandFormat = valueString.replace("^col^", ":");
        } else if (args[0].equalsIgnoreCase("defaultWaypointTPCommandRotationFormat")) {
            this.defaultWaypointTPCommandRotationFormat = valueString.replace("^col^", ":");
        } else if (args[0].equalsIgnoreCase("arrowScale")) {
            this.arrowScale = Double.valueOf(valueString);
        } else if (args[0].equalsIgnoreCase("arrowColour")) {
            this.arrowColour = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("seed")) {
            serverSlimeSeeds.put(new XaeroPathReader().read(valueString), Long.parseLong(args[2]));
        } else if (args[0].equalsIgnoreCase("smoothDots")) {
            this.smoothDots = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("worldMap")) {
            this.worldMap = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("terrainDepth")) {
            this.terrainDepth = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("terrainSlopes")) {
            this.terrainSlopes = valueString.equals("true") ? 2 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("alwaysArrow") && valueString.equals("true")) {
            this.mainEntityAs = 2;
        } else if (args[0].equalsIgnoreCase("mainEntityAs")) {
            this.mainEntityAs = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("blockTransparency")) {
            this.blockTransparency = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("waypointOpacityIngame")) {
            this.waypointOpacityIngame = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("waypointOpacityMap")) {
            this.waypointOpacityMap = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("hideWorldNames")) {
            this.hideWorldNames = valueString.equals("true") ? 2 : (valueString.equals("false") ? 1 : Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("openSlimeSettings")) {
            this.openSlimeSettings = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("alwaysShowDistance")) {
            this.alwaysShowDistance = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("showLightLevel")) {
            BuiltInInfoDisplays.LIGHT_LEVEL.setState(valueString.equals("true") ? 1 : (valueString.equals("false") ? 0 : Integer.parseInt(valueString)));
        } else if (args[0].equalsIgnoreCase("renderLayerIndex")) {
            this.renderLayerIndex = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("crossDimensionalTp")) {
            this.crossDimensionalTp = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("showTime")) {
            BuiltInInfoDisplays.TIME.setState(Integer.parseInt(valueString));
        } else if (args[0].equalsIgnoreCase("biomeColorsVanillaMode")) {
            this.biomeColorsVanillaMode = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("lookingAtAngle")) {
            this.lookingAtAngle = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("lookingAtAngleVertical")) {
            this.lookingAtAngleVertical = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("centeredEnlarged")) {
            this.centeredEnlarged = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("zoomedOutEnlarged")) {
            this.zoomOnEnlarged = valueString.equals("true") ? 1 : 0;
        } else if (args[0].equalsIgnoreCase("zoomOnEnlarged")) {
            this.zoomOnEnlarged = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("minimapTextAlign")) {
            this.minimapTextAlign = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("showAngles")) {
            BuiltInInfoDisplays.ANGLES.setState(valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("waypointsMutualEdit")) {
            this.waypointsMutualEdit = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("compass")) {
            this.compassLocation = valueString.equals("true") ? 1 : 0;
        } else if (args[0].equalsIgnoreCase("compassLocation")) {
            this.compassLocation = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("compassDirectionScale")) {
            this.compassDirectionScale = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("caveMapsDepth")) {
            this.caveMapsDepth = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("hideWaypointCoordinates")) {
            this.hideWaypointCoordinates = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("renderAllSets")) {
            this.renderAllSets = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("playerArrowOpacity")) {
            this.playerArrowOpacity = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("waypointsBottom")) {
            this.waypointsBottom = valueString.equals("true");
        } else if (args[0].equalsIgnoreCase("minimapShape")) {
            this.minimapShape = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("lightOverlayType")) {
            this.lightOverlayType = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("lightOverlayMaxLight")) {
            this.lightOverlayMaxLight = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("lightOverlayMinLight")) {
            this.lightOverlayMinLight = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("lightOverlayColor")) {
            this.lightOverlayColor = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("uiScale")) {
            this.uiScale = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("bossHealthPushBox")) {
            this.bossHealthPushBox = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("potionEffectPushBox")) {
            this.potionEffectPushBox = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("minimapFrame")) {
            this.minimapFrame = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("minimapFrameColor")) {
            this.minimapFrameColor = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("compassColor")) {
            this.compassColor = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("northCompassColor")) {
            this.northCompassColor = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("showDimensionName")) {
            BuiltInInfoDisplays.DIMENSION.setState(valueString.equals("true"));
        } else if (args[0].equalsIgnoreCase("displayMultipleWaypointInfo")) {
            this.displayMultipleWaypointInfo = Integer.parseInt(valueString);
        } else if (args[0].equalsIgnoreCase("entityRadar")) {
            this.entityRadar = valueString.equals("true");
        } else {
            if (this.entityRadarBackwardsCompatibilityConfig.readSetting(args)) {
                this.foundOldRadarSettings = true;
                return;
            }
            if (args[0].equalsIgnoreCase("adjustHeightForCarpetLikeBlocks")) {
                this.adjustHeightForCarpetLikeBlocks = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("autoConvertWaypointDistanceToKmThreshold")) {
                this.autoConvertWaypointDistanceToKmThreshold = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("waypointDistancePrecision")) {
                this.waypointDistancePrecision = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("mainDotSize")) {
                this.mainDotSize = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("partialYTeleportation")) {
                this.partialYTeleportation = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("deleteReachedDeathpoints")) {
                this.deleteReachedDeathpoints = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("hideMinimapUnderScreen")) {
                this.hideMinimapUnderScreen = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("manualCaveModeStart")) {
                this.manualCaveModeStart = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("manualCaveModeStartAuto")) {
                this.manualCaveModeStartAuto = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("chunkGridLineWidth")) {
                this.chunkGridLineWidth = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("hideMinimapUnderF3")) {
                this.hideMinimapUnderF3 = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("temporaryWaypointsGlobal")) {
                this.temporaryWaypointsGlobal = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("keepUnlockedWhenEnlarged")) {
                this.keepUnlockedWhenEnlarged = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("enlargedMinimapAToggle")) {
                this.enlargedMinimapAToggle = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("displayStainedGlass")) {
                this.displayStainedGlass = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("waypointOnMapScale")) {
                this.waypointOnMapScale = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("switchToAutoOnDeath")) {
                this.switchToAutoOnDeath = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("displayWeatherInfo")) {
                BuiltInInfoDisplays.WEATHER.setState(valueString.equals("true"));
            } else if (args[0].equalsIgnoreCase("infoDisplayBackgroundOpacity")) {
                this.infoDisplayBackgroundOpacity = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("caveModeToggleTimer")) {
                this.caveModeToggleTimer = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("legibleCaveMaps")) {
                this.legibleCaveMaps = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("biomeBlending")) {
                this.biomeBlending = args[1].equals("true");
            } else if (args[0].equalsIgnoreCase("displayPacPlayers") || args[0].equalsIgnoreCase("displayTrackedPlayers")) {
                this.displayTrackedPlayersOnMap = this.displayTrackedPlayersInWorld = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("displayTrackedPlayersOnMap")) {
                this.displayTrackedPlayersOnMap = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("displayTrackedPlayersInWorld")) {
                this.displayTrackedPlayersInWorld = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("dimensionScaledMaxWaypointDistance")) {
                this.dimensionScaledMaxWaypointDistance = args[1].equals("true");
            } else if (args[0].equalsIgnoreCase("trackedPlayerWorldIconScale")) {
                this.trackedPlayerWorldIconScale = Integer.parseInt(args[1]);
            } else if (args[0].equalsIgnoreCase("trackedPlayerWorldNameScale")) {
                this.trackedPlayerWorldNameScale = Integer.parseInt(args[1]);
            } else if (args[0].equalsIgnoreCase("trackedPlayerMinimapIconScale")) {
                this.trackedPlayerMinimapIconScale = Integer.parseInt(args[1]);
            } else if (args[0].equalsIgnoreCase("displayClaims")) {
                this.displayClaims = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("displayCurrentClaim")) {
                this.displayCurrentClaim = valueString.equals("true");
            } else if (args[0].equalsIgnoreCase("claimsOpacity")) {
                this.claimsBorderOpacity = Integer.parseInt(valueString);
                this.claimsFillOpacity = this.claimsBorderOpacity * 58 / 100;
            } else if (args[0].equalsIgnoreCase("claimsBorderOpacity")) {
                this.claimsBorderOpacity = Integer.parseInt(valueString);
            } else if (args[0].equalsIgnoreCase("claimsFillOpacity")) {
                this.claimsFillOpacity = Integer.parseInt(valueString);
            }
        }
    }

    public void loadDefaultSettings() throws IOException {
        Path mainConfigFile = this.modMain.getConfigFile();
        File defaultConfigFile = mainConfigFile.getParent().resolveSibling("defaultconfigs").resolve(mainConfigFile.getFileName()).toFile();
        if (defaultConfigFile.exists()) {
            this.loadSettingsFile(defaultConfigFile);
        }
    }

    public void loadSettings() throws IOException {
        this.loadDefaultSettings();
        Path mainConfigFile = this.modMain.getConfigFile();
        Path configFolderPath = mainConfigFile.getParent();
        if (!Files.exists(configFolderPath, new LinkOption[0])) {
            Files.createDirectories(configFolderPath, new FileAttribute[0]);
        }
        if (Files.exists(mainConfigFile, new LinkOption[0])) {
            this.loadSettingsFile(mainConfigFile.toFile());
        }
        this.saveSettings();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadSettingsFile(File file) throws IOException {
        try (BufferedReader reader = null;){
            String s;
            reader = new BufferedReader(new FileReader(file));
            InfoDisplayIO infoDisplayIO = this.modMain.getMinimap().getInfoDisplays().getIo();
            while ((s = reader.readLine()) != null) {
                if (this.modMain.getHudIO().load(s)) continue;
                String[] args = s.split(":");
                try {
                    if (args[0].equalsIgnoreCase("interface") && args[1].equals("gui.xaero_minimap")) {
                        BuiltInHudModules.MINIMAP.setTransform(this.modMain.getHud().getOldSystemCompatibility().loadOldTransform(args));
                        continue;
                    }
                    if (args[0].equals("infoDisplayOrder")) {
                        infoDisplayIO.loadInfoDisplayOrderLine(args);
                        continue;
                    }
                    if (args[0].equals("infoDisplay")) {
                        infoDisplayIO.loadInfoDisplayLine(args);
                        continue;
                    }
                    this.readSetting(args);
                }
                catch (Exception e) {
                    MinimapLogs.LOGGER.info("Skipping setting:" + args[0]);
                }
            }
        }
    }

    public String getMoreOptionValueNames(ModOptions par1EnumOptions) {
        return "undefined";
    }

    private String getBooleanName(ModOptions par1EnumOptions) {
        boolean clientSetting = this.getClientBooleanValue(par1EnumOptions);
        boolean serverSetting = this.getBooleanValue(par1EnumOptions);
        return ModSettings.getTranslation(clientSetting) + (String)(serverSetting != clientSetting ? "\u00a7e (" + ModSettings.getTranslation(serverSetting) + ")" : "");
    }

    public Object getOptionValue(ModOptions par1EnumOptions) {
        if (par1EnumOptions.enumBoolean) {
            return this.getClientBooleanValue(par1EnumOptions);
        }
        if (par1EnumOptions.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return 0;
        }
        if (par1EnumOptions == ModOptions.ZOOM) {
            return this.zoom;
        }
        if (par1EnumOptions == ModOptions.DISTANCE) {
            return this.distance;
        }
        if (par1EnumOptions == ModOptions.SLIME_CHUNKS && this.customSlimeSeedNeeded(XaeroMinimapSession.getCurrentSession())) {
            return -1;
        }
        if (par1EnumOptions == ModOptions.CAVE_MAPS) {
            return this.caveMaps;
        }
        if (par1EnumOptions == ModOptions.CAVE_ZOOM) {
            return this.caveZoom;
        }
        if (par1EnumOptions == ModOptions.HIDE_WORLD_NAMES) {
            return this.hideWorldNames;
        }
        if (par1EnumOptions == ModOptions.MINIMAP_TEXT_ALIGN) {
            return this.minimapTextAlign;
        }
        if (par1EnumOptions == ModOptions.ARROW_COLOUR) {
            return this.arrowColour;
        }
        if (par1EnumOptions == ModOptions.COLOURS) {
            return this.blockColours;
        }
        if (par1EnumOptions == ModOptions.TERRAIN_SLOPES) {
            return this.terrainSlopes;
        }
        if (par1EnumOptions == ModOptions.MAIN_ENTITY_AS) {
            return this.mainEntityAs;
        }
        if (par1EnumOptions == ModOptions.MINIMAP_SHAPE) {
            return this.minimapShape;
        }
        if (par1EnumOptions == ModOptions.LIGHT_OVERLAY_TYPE) {
            return this.lightOverlayType < 0 ? 0 : this.lightOverlayType;
        }
        if (par1EnumOptions == ModOptions.DOTS_STYLE) {
            return this.dotsStyle;
        }
        if (par1EnumOptions == ModOptions.BOSS_HEALTH_PUSHBOX) {
            return this.bossHealthPushBox;
        }
        if (par1EnumOptions == ModOptions.POTION_EFFECTS_PUSHBOX) {
            return this.potionEffectPushBox;
        }
        if (par1EnumOptions == ModOptions.MINIMAP_FRAME) {
            return this.minimapFrame;
        }
        if (par1EnumOptions == ModOptions.COMPASS_LOCATION) {
            return this.compassLocation;
        }
        if (par1EnumOptions == ModOptions.MULTIPLE_WAYPOINT_INFO) {
            return this.displayMultipleWaypointInfo;
        }
        if (par1EnumOptions == ModOptions.ZOOM_ON_ENLARGE) {
            return this.zoomOnEnlarged;
        }
        if (par1EnumOptions == ModOptions.EAMOUNT) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.ENTITY_NUMBER);
        }
        if (par1EnumOptions == ModOptions.RADAR_ICONS_DISPLAYED) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.ICONS);
        }
        if (par1EnumOptions == ModOptions.RADAR_NAMES_DISPLAYED) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.NAMES);
        }
        if (par1EnumOptions == ModOptions.RADAR_OVER_FRAME) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.RENDER_OVER_MINIMAP);
        }
        if (par1EnumOptions == ModOptions.RADAR_Y_DISPLAYED) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.DISPLAY_Y);
        }
        return this.getMoreOptionValues(par1EnumOptions);
    }

    protected Object getMoreOptionValues(ModOptions par1EnumOptions) {
        return false;
    }

    public String getOptionValueName(ModOptions par1EnumOptions) {
        if (par1EnumOptions.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return ModSettings.getTranslation(false);
        }
        if (this.usesWorldMapScreenValue(par1EnumOptions) || this.usesWorldMapOptionValue(par1EnumOptions) || this.usesWorldMapHardValue(par1EnumOptions)) {
            return "\u00a7e" + class_1074.method_4662((String)"gui.xaero_world_map", (Object[])new Object[0]);
        }
        if (par1EnumOptions.enumBoolean) {
            return this.getBooleanName(par1EnumOptions);
        }
        Object s = "";
        if (par1EnumOptions == ModOptions.ZOOM) {
            s = (String)s + zooms[this.zoom] + "x";
        } else if (par1EnumOptions == ModOptions.DISTANCE) {
            s = (String)s + class_1074.method_4662((String)distanceTypes[this.distance], (Object[])new Object[0]);
        } else if (par1EnumOptions == ModOptions.SLIME_CHUNKS && this.customSlimeSeedNeeded(XaeroMinimapSession.getCurrentSession())) {
            s = par1EnumOptions.getEnumString();
        } else if (par1EnumOptions == ModOptions.CAVE_MAPS) {
            if (this.caveMaps == 0) {
                s = (String)s + class_1074.method_4662((String)"gui.xaero_off", (Object[])new Object[0]);
            } else {
                int roofSideSize = this.caveMaps * 2 - 1;
                s = (String)s + roofSideSize + "x" + roofSideSize + " " + class_1074.method_4662((String)"gui.xaero_roof", (Object[])new Object[0]);
                if (!this.getCaveMaps(false)) {
                    s = (String)s + "\u00a7e (" + ModSettings.getTranslation(false) + ")";
                }
            }
        } else if (par1EnumOptions == ModOptions.CAVE_ZOOM) {
            s = this.caveZoom == 0 ? (String)s + class_1074.method_4662((String)"gui.xaero_off", (Object[])new Object[0]) : (String)s + (1 + this.caveZoom) + "x";
        } else if (par1EnumOptions == ModOptions.HIDE_WORLD_NAMES) {
            s = (String)s + (this.hideWorldNames == 0 ? class_1074.method_4662((String)"gui.xaero_off", (Object[])new Object[0]) : (this.hideWorldNames == 1 ? class_1074.method_4662((String)"gui.xaero_partial", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_full", (Object[])new Object[0])));
        } else if (par1EnumOptions == ModOptions.MINIMAP_TEXT_ALIGN) {
            s = (String)s + (this.minimapTextAlign == 0 ? class_1074.method_4662((String)"gui.xaero_center", (Object[])new Object[0]) : (this.minimapTextAlign == 1 ? class_1074.method_4662((String)"gui.xaero_left", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_right", (Object[])new Object[0])));
        } else if (par1EnumOptions == ModOptions.ARROW_COLOUR) {
            String colourName = "gui.xaero_team";
            if (this.arrowColour != -1) {
                colourName = this.arrowColourNames[this.arrowColour];
            }
            s = (String)s + class_1074.method_4662((String)colourName, (Object[])new Object[0]);
        } else {
            s = par1EnumOptions == ModOptions.COLOURS ? (String)s + class_1074.method_4662((String)blockColourTypes[this.getBlockColours()], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.TERRAIN_SLOPES ? (String)s + class_1074.method_4662((String)SLOPES_MODES[this.terrainSlopes], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.MAIN_ENTITY_AS ? (String)s + (this.mainEntityAs == 0 ? class_1074.method_4662((String)"gui.xaero_crosshair", (Object[])new Object[0]) : (this.mainEntityAs == 1 ? class_1074.method_4662((String)"gui.xaero_dot", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_arrow", (Object[])new Object[0]))) : (par1EnumOptions == ModOptions.MINIMAP_SHAPE ? (String)s + class_1074.method_4662((String)MINIMAP_SHAPES[this.minimapShape], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.LIGHT_OVERLAY_TYPE ? (String)s + class_1074.method_4662((String)SHOW_LIGHT_LEVEL_NAMES[this.lightOverlayType < 0 ? 0 : this.lightOverlayType], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.DOTS_STYLE ? (String)s + class_1074.method_4662((String)DOTS_STYLES[this.dotsStyle], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.BOSS_HEALTH_PUSHBOX ? (String)s + class_1074.method_4662((String)PUSHBOX_OPTIONS[this.bossHealthPushBox], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.POTION_EFFECTS_PUSHBOX ? (String)s + class_1074.method_4662((String)PUSHBOX_OPTIONS[this.potionEffectPushBox], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.MINIMAP_FRAME ? (String)s + class_1074.method_4662((String)FRAME_OPTIONS[this.minimapFrame], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.COMPASS_LOCATION ? (String)s + class_1074.method_4662((String)COMPASS_OPTIONS[this.compassLocation], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.MULTIPLE_WAYPOINT_INFO ? (String)s + class_1074.method_4662((String)MULTIPLE_WAYPOINT_INFO[this.displayMultipleWaypointInfo], (Object[])new Object[0]) : (par1EnumOptions == ModOptions.ZOOM_ON_ENLARGE ? (String)s + (String)(this.zoomOnEnlarged <= 0 ? class_1074.method_4662((String)"gui.xaero_zoom_on_enlarge_auto", (Object[])new Object[0]) : zooms[this.zoomOnEnlarged - 1] + "x") : (par1EnumOptions == ModOptions.EAMOUNT ? (String)s + this.getRadarSettingOptionName(EntityRadarCategorySettings.ENTITY_NUMBER) : (par1EnumOptions == ModOptions.RADAR_ICONS_DISPLAYED ? (String)s + this.getRadarSettingOptionName(EntityRadarCategorySettings.ICONS) : (par1EnumOptions == ModOptions.RADAR_NAMES_DISPLAYED ? (String)s + this.getRadarSettingOptionName(EntityRadarCategorySettings.NAMES) : (par1EnumOptions == ModOptions.RADAR_OVER_FRAME ? (String)s + this.getRadarSettingOptionName(EntityRadarCategorySettings.RENDER_OVER_MINIMAP) : (par1EnumOptions == ModOptions.RADAR_Y_DISPLAYED ? (String)s + this.getRadarSettingOptionName(EntityRadarCategorySettings.DISPLAY_Y) : this.getMoreOptionValueNames(par1EnumOptions)))))))))))))))));
        }
        return s;
    }

    public boolean usesWorldMapOptionValue(ModOptions par1EnumOptions) {
        return this.modMain.getSupportMods().shouldUseWorldMapChunks() && (par1EnumOptions == ModOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS || par1EnumOptions == ModOptions.COLOURS || par1EnumOptions == ModOptions.IGNORE_HEIGHTMAPS || par1EnumOptions == ModOptions.FLOWERS || par1EnumOptions == ModOptions.BIOMES_VANILLA || par1EnumOptions == ModOptions.TERRAIN_DEPTH || par1EnumOptions == ModOptions.TERRAIN_SLOPES || par1EnumOptions == ModOptions.DISPLAY_STAINED_GLASS || par1EnumOptions == ModOptions.PAC_CLAIMS || par1EnumOptions == ModOptions.PAC_CLAIMS_FILL_OPACITY || par1EnumOptions == ModOptions.PAC_CLAIMS_BORDER_OPACITY || par1EnumOptions == ModOptions.CAVE_MAPS_DEPTH || par1EnumOptions == ModOptions.LEGIBLE_CAVE_MAPS || par1EnumOptions == ModOptions.BIOME_BLENDING) || this.modMain.getSupportMods().worldmap() && par1EnumOptions == ModOptions.PARTIAL_Y_TELEPORTATION;
    }

    public boolean usesWorldMapHardValue(ModOptions par1EnumOptions) {
        return this.modMain.getSupportMods().shouldUseWorldMapChunks() && par1EnumOptions == ModOptions.REDSTONE;
    }

    public String getSliderOptionText(ModOptions par1EnumOptions) {
        boolean usingSafeMode;
        String s = par1EnumOptions.getEnumString() + ": ";
        boolean bl = usingSafeMode = this.modMain.getMinimap().getMinimapFBORenderer().isTriedFBO() && !this.modMain.getMinimap().usingFBO() || this.mapSafeMode;
        if (this.usesWorldMapScreenValue(par1EnumOptions) || this.usesWorldMapOptionValue(par1EnumOptions) || this.usesWorldMapHardValue(par1EnumOptions)) {
            s = s + "\u00a7e" + class_1074.method_4662((String)"gui.xaero_world_map", (Object[])new Object[0]);
        } else if (par1EnumOptions == ModOptions.CHUNK_GRID) {
            s = s + (String)(this.chunkGrid > -1 ? format + ENCHANT_COLORS[this.chunkGrid] + class_1074.method_4662((String)ENCHANT_COLOR_NAMES[this.chunkGrid], (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_off", (Object[])new Object[0]));
        } else if (par1EnumOptions == ModOptions.LIGHT_OVERLAY_COLOR) {
            s = s + format + ENCHANT_COLORS[this.lightOverlayColor] + class_1074.method_4662((String)ENCHANT_COLOR_NAMES[this.lightOverlayColor], (Object[])new Object[0]);
        } else if (par1EnumOptions == ModOptions.UI_SCALE) {
            s = s + String.valueOf(this.uiScale <= 1 ? class_1074.method_4662((String)"gui.xaero_ui_scale_auto", (Object[])new Object[0]) + " (" + this.getAutoUIScale() + ")" : (this.uiScale == 11 ? class_1074.method_4662((String)"gui.xaero_ui_scale_mc", (Object[])new Object[0]) + " (" + class_310.method_1551().method_22683().method_4495() + ")" : Integer.valueOf(this.uiScale)));
        } else if (par1EnumOptions == ModOptions.LIGHT_OVERLAY_MAX_LIGHT) {
            s = s + this.lightOverlayMaxLight;
        } else if (par1EnumOptions == ModOptions.LIGHT_OVERLAY_MIN_LIGHT) {
            s = s + this.lightOverlayMinLight;
        } else if (par1EnumOptions == ModOptions.MINIMAP_FRAME_COLOR) {
            s = s + format + ENCHANT_COLORS[this.minimapFrameColor] + class_1074.method_4662((String)ENCHANT_COLOR_NAMES[this.minimapFrameColor], (Object[])new Object[0]);
        } else if (par1EnumOptions == ModOptions.COMPASS_COLOR) {
            s = s + format + ENCHANT_COLORS[this.compassColor] + class_1074.method_4662((String)ENCHANT_COLOR_NAMES[this.compassColor], (Object[])new Object[0]);
        } else if (par1EnumOptions == ModOptions.NORTH_COMPASS_COLOR) {
            int effectiveColor = this.getNorthCompassColor();
            s = s + (String)(effectiveColor != this.northCompassColor ? class_1074.method_4662((String)"gui.xaero_north_compass_color_default", (Object[])new Object[0]) : format + ENCHANT_COLORS[effectiveColor] + class_1074.method_4662((String)ENCHANT_COLOR_NAMES[effectiveColor], (Object[])new Object[0]));
        } else if (par1EnumOptions == ModOptions.DOTS_SIZE) {
            s = s + String.valueOf(this.modMain.getEntityRadarCategoryManager().getRootCategory().getSettingValue(EntityRadarCategorySettings.DOT_SIZE));
            if (usingSafeMode) {
                s = s + "\u00a7e (" + ModSettings.getTranslation(false) + ")";
            }
        } else if (par1EnumOptions == ModOptions.SIZE) {
            s = s + (this.minimapSize > 0 ? "" + this.minimapSize : class_1074.method_4662((String)"gui.xaero_auto_map_size", (Object[])new Object[0]) + " (" + this.getMinimapSize() + ")");
        } else if (par1EnumOptions == ModOptions.WAYPOINTS_ICON_SCALE || par1EnumOptions == ModOptions.WAYPOINTS_DISTANCE_SCALE) {
            int settingValue;
            int n = settingValue = par1EnumOptions == ModOptions.WAYPOINTS_ICON_SCALE ? this.waypointsIngameIconScale : this.waypointsIngameDistanceScale;
            s = s + String.valueOf(settingValue <= 0 ? class_1074.method_4662((String)"gui.xaero_ui_scale_auto", (Object[])new Object[0]) + " (" + this.getAutoUIScale() + ")" : (settingValue == 17 ? class_1074.method_4662((String)"gui.xaero_ui_scale_mc", (Object[])new Object[0]) + " (" + class_310.method_1551().method_22683().method_4495() + ")" : Integer.valueOf(settingValue)));
        } else if (par1EnumOptions == ModOptions.WAYPOINTS_NAME_SCALE) {
            int settingValue = this.waypointsIngameNameScale;
            s = s + String.valueOf(settingValue <= 0 ? class_1074.method_4662((String)"gui.xaero_ui_scale_auto", (Object[])new Object[0]) + " (" + this.getWaypointsIngameNameScale() + ")" : (settingValue == 17 ? class_1074.method_4662((String)"gui.xaero_ui_scale_mc", (Object[])new Object[0]) + " (" + class_310.method_1551().method_22683().method_4495() + ")" : Integer.valueOf(settingValue)));
        } else if (par1EnumOptions == ModOptions.TRACKED_PLAYER_WORLD_ICON_SCALE || par1EnumOptions == ModOptions.TRACKED_PLAYER_WORLD_NAME_SCALE || par1EnumOptions == ModOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE) {
            int settingValue;
            int n = par1EnumOptions == ModOptions.TRACKED_PLAYER_WORLD_ICON_SCALE ? this.trackedPlayerWorldIconScale : (settingValue = par1EnumOptions == ModOptions.TRACKED_PLAYER_WORLD_NAME_SCALE ? this.trackedPlayerWorldNameScale : this.trackedPlayerMinimapIconScale);
            s = s + String.valueOf(settingValue <= 0 ? class_1074.method_4662((String)"gui.xaero_ui_scale_auto", (Object[])new Object[0]) + " (" + this.getAutoUIScale() + ")" : ((double)settingValue == par1EnumOptions.getValueMax() ? class_1074.method_4662((String)"gui.xaero_ui_scale_mc", (Object[])new Object[0]) + " (" + class_310.method_1551().method_22683().method_4495() + ")" : Integer.valueOf(settingValue)));
        } else {
            if (par1EnumOptions == ModOptions.HEADS_SCALE) {
                return s + this.getRadarSettingOptionName(EntityRadarCategorySettings.ICON_SCALE);
            }
            if (par1EnumOptions == ModOptions.HEIGHT_LIMIT) {
                return s + this.getRadarSettingOptionName(EntityRadarCategorySettings.HEIGHT_LIMIT);
            }
            if (par1EnumOptions == ModOptions.START_FADING_AT) {
                return s + this.getRadarSettingOptionName(EntityRadarCategorySettings.START_FADING_AT);
            }
            if (par1EnumOptions == ModOptions.WAYPOINTS_CLOSE_SCALE) {
                return this.getEnumFloatSliderText(s, "%.3f", par1EnumOptions);
            }
            if (par1EnumOptions == ModOptions.AUTO_CONVERT_TO_KM) {
                s = s + String.valueOf(this.autoConvertWaypointDistanceToKmThreshold == -1 ? class_1074.method_4662((String)"gui.xaero_auto_convert_wp_distance_km_never", (Object[])new Object[0]) : Integer.valueOf(this.autoConvertWaypointDistanceToKmThreshold));
            } else if (par1EnumOptions == ModOptions.WP_DISTANCE_PRECISION) {
                s = s + this.waypointDistancePrecision;
            } else if (par1EnumOptions == ModOptions.MANUAL_CAVE_MODE_START) {
                s = s + String.valueOf(this.manualCaveModeStartAuto ? class_1074.method_4662((String)"gui.xaero_manual_cave_mode_start_auto", (Object[])new Object[0]) : Integer.valueOf(this.manualCaveModeStart));
            } else if (par1EnumOptions == ModOptions.CHUNK_GRID_LINE_WIDTH) {
                s = s + this.chunkGridLineWidth;
            } else if (par1EnumOptions == ModOptions.WAYPOINT_ONMAP_SCALE) {
                s = s + String.valueOf(this.waypointOnMapScale <= 0 ? class_1074.method_4662((String)"gui.xaero_waypoint_onmap_scale_auto", (Object[])new Object[0]) : Integer.valueOf(this.waypointOnMapScale));
            } else if (par1EnumOptions == ModOptions.INFO_DISPLAY_BG_OPACITY) {
                s = s + this.infoDisplayBackgroundOpacity;
            } else if (par1EnumOptions == ModOptions.CAVE_MODE_TOGGLE_TIMER) {
                s = s + this.caveModeToggleTimer + " ms";
            } else if (par1EnumOptions == ModOptions.MAIN_DOT_SIZE) {
                s = s + this.mainDotSize;
                if (usingSafeMode) {
                    s = s + "\u00a7e (" + ModSettings.getTranslation(false) + ")";
                }
            } else if (par1EnumOptions == ModOptions.COMPASS_SCALE) {
                s = s + String.valueOf(this.compassDirectionScale <= 0 ? class_1074.method_4662((String)"gui.xaero_compass_scale_auto", (Object[])new Object[0]) : Integer.valueOf(this.compassDirectionScale));
            } else {
                return this.getEnumFloatSliderText(s, "%.1f", par1EnumOptions);
            }
        }
        return s;
    }

    public boolean usesWorldMapScreenValue(ModOptions par1EnumOptions) {
        return this.modMain.getSupportMods().shouldUseWorldMapChunks() && par1EnumOptions == ModOptions.MANUAL_CAVE_MODE_START && this.modMain.getSupportMods().worldmapSupport.caveLayersAreUsable();
    }

    protected String getEnumFloatSliderText(String s, String f, ModOptions par1EnumOptions) {
        Object f1 = String.format(f, this.getOptionDoubleValue(par1EnumOptions));
        if (par1EnumOptions == ModOptions.WAYPOINTS_DISTANCE) {
            double waypointsDistance = this.getMaxWaypointsDistance();
            f1 = waypointsDistance == 0.0 ? class_1074.method_4662((String)"gui.xaero_unlimited", (Object[])new Object[0]) : (int)waypointsDistance + "m";
        } else if (par1EnumOptions == ModOptions.WAYPOINTS_DISTANCE_MIN) {
            f1 = this.waypointsDistanceMin == 0.0 ? class_1074.method_4662((String)"gui.xaero_off", (Object[])new Object[0]) : (int)this.waypointsDistanceMin + "m";
        } else if (par1EnumOptions == ModOptions.ARROW_SCALE) {
            f1 = (String)f1 + "x";
        }
        return s + (String)f1;
    }

    public boolean getBooleanValue(ModOptions o) {
        if (o == ModOptions.MINIMAP) {
            return this.getMinimap();
        }
        if (o == ModOptions.CAVE_MAPS) {
            return this.getCaveMaps(false);
        }
        if (o == ModOptions.WAYPOINTS) {
            return this.getShowWaypoints();
        }
        if (o == ModOptions.DEATHPOINTS) {
            return this.getDeathpoints();
        }
        if (o == ModOptions.OLD_DEATHPOINTS) {
            return this.getOldDeathpoints();
        }
        if (o == ModOptions.INGAME_WAYPOINTS) {
            return this.getShowIngameWaypoints();
        }
        if (o == ModOptions.NORTH) {
            return this.getLockNorth(this.getMinimapSize(), this.minimapShape);
        }
        if (o == ModOptions.SAFE_MAP) {
            return this.mapSafeMode || this.modMain.getMinimap().getMinimapFBORenderer().isTriedFBO() && !this.modMain.getMinimap().getMinimapFBORenderer().isLoadedFBO();
        }
        if (o == ModOptions.AA) {
            return this.getAntiAliasing();
        }
        if (o == ModOptions.SMOOTH_DOTS) {
            return this.getSmoothDots();
        }
        if (o == ModOptions.WORLD_MAP) {
            return this.getUseWorldMap();
        }
        if (o == ModOptions.TERRAIN_DEPTH) {
            return this.getTerrainDepth();
        }
        if (o == ModOptions.RADAR_DISPLAYED) {
            return this.getEntityRadar();
        }
        if (o == ModOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS) {
            return this.getAdjustHeightForCarpetLikeBlocks();
        }
        if (o == ModOptions.BIOME_BLENDING) {
            return this.getBiomeBlending();
        }
        return this.getClientBooleanValue(o);
    }

    public boolean getClientBooleanValue(ModOptions o) {
        if (o.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return false;
        }
        if (o == ModOptions.IGNORE_HEIGHTMAPS) {
            return this.isIgnoreHeightmaps();
        }
        if (o == ModOptions.MINIMAP) {
            return BuiltInHudModules.MINIMAP.isActive();
        }
        if (o == ModOptions.WAYPOINTS) {
            return this.showWaypoints;
        }
        if (o == ModOptions.DEATHPOINTS) {
            return this.deathpoints;
        }
        if (o == ModOptions.OLD_DEATHPOINTS) {
            return this.oldDeathpoints;
        }
        if (o == ModOptions.INGAME_WAYPOINTS) {
            return this.showIngameWaypoints;
        }
        if (o == ModOptions.REDSTONE) {
            return this.displayRedstone;
        }
        if (o == ModOptions.NORTH) {
            return this.lockNorth;
        }
        if (o == ModOptions.SLIME_CHUNKS) {
            return this.slimeChunks;
        }
        if (o == ModOptions.SAFE_MAP) {
            return this.mapSafeMode;
        }
        if (o == ModOptions.AA) {
            return this.antiAliasing;
        }
        if (o == ModOptions.LIGHT) {
            return this.lighting;
        }
        if (o == ModOptions.COMPASS) {
            return this.compassOverEverything;
        }
        if (o == ModOptions.FLOWERS) {
            return this.showFlowers;
        }
        if (o == ModOptions.KEEP_WP_NAMES) {
            return this.keepWaypointNames;
        }
        if (o == ModOptions.SMOOTH_DOTS) {
            return this.smoothDots;
        }
        if (o == ModOptions.WORLD_MAP) {
            return this.worldMap;
        }
        if (o == ModOptions.TERRAIN_DEPTH) {
            return this.terrainDepth;
        }
        if (o == ModOptions.BLOCK_TRANSPARENCY) {
            return this.blockTransparency;
        }
        if (o == ModOptions.OPEN_SLIME_SETTINGS) {
            return this.openSlimeSettings;
        }
        if (o == ModOptions.ALWAYS_SHOW_DISTANCE) {
            return this.alwaysShowDistance;
        }
        if (o == ModOptions.CROSS_DIMENSIONAL_TP) {
            return this.crossDimensionalTp;
        }
        if (o == ModOptions.BIOMES_VANILLA) {
            return this.biomeColorsVanillaMode;
        }
        if (o == ModOptions.CENTERED_ENLARGED) {
            return this.centeredEnlarged;
        }
        if (o == ModOptions.HIDE_WP_COORDS) {
            return this.hideWaypointCoordinates;
        }
        if (o == ModOptions.WAYPOINTS_ALL_SETS) {
            return this.renderAllSets;
        }
        if (o == ModOptions.WAYPOINTS_BOTTOM) {
            return this.waypointsBottom;
        }
        if (o == ModOptions.RADAR_DISPLAYED) {
            return this.entityRadar;
        }
        if (o == ModOptions.ENTITY_HEIGHT) {
            return this.modMain.getEntityRadarCategoryManager().getRootCategory().getSettingValue(EntityRadarCategorySettings.HEIGHT_FADE);
        }
        if (o == ModOptions.ENTITY_NAMETAGS) {
            return this.modMain.getEntityRadarCategoryManager().getRootCategory().getSettingValue(EntityRadarCategorySettings.ALWAYS_NAMETAGS);
        }
        if (o == ModOptions.ICON_NAME_FALLBACK) {
            return this.modMain.getEntityRadarCategoryManager().getRootCategory().getSettingValue(EntityRadarCategorySettings.ICON_NAME_FALLBACK);
        }
        if (o == ModOptions.UPDATE_NOTIFICATION) {
            return updateNotification;
        }
        if (o == ModOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS) {
            return this.adjustHeightForCarpetLikeBlocks;
        }
        if (o == ModOptions.PARTIAL_Y_TELEPORTATION) {
            return this.partialYTeleportation;
        }
        if (o == ModOptions.DELETE_REACHED_DEATHPOINTS) {
            return this.deleteReachedDeathpoints;
        }
        if (o == ModOptions.HIDE_MINIMAP_UNDER_SCREEN) {
            return this.hideMinimapUnderScreen;
        }
        if (o == ModOptions.HIDE_MINIMAP_UNDER_F3) {
            return this.hideMinimapUnderF3;
        }
        if (o == ModOptions.TEMPORARY_WAYPOINTS_GLOBAL) {
            return this.temporaryWaypointsGlobal;
        }
        if (o == ModOptions.KEEP_ENLARGED_UNLOCKED) {
            return this.keepUnlockedWhenEnlarged;
        }
        if (o == ModOptions.TOGGLED_ENLARGED) {
            return this.enlargedMinimapAToggle;
        }
        if (o == ModOptions.DISPLAY_STAINED_GLASS) {
            return this.displayStainedGlass;
        }
        if (o == ModOptions.SWITCH_TO_AUTO_ON_DEATH) {
            return this.switchToAutoOnDeath;
        }
        if (o == ModOptions.LEGIBLE_CAVE_MAPS) {
            return this.legibleCaveMaps;
        }
        if (o == ModOptions.BIOME_BLENDING) {
            return this.biomeBlending;
        }
        if (o == ModOptions.TRACKED_PLAYERS_ON_MAP) {
            return this.displayTrackedPlayersOnMap;
        }
        if (o == ModOptions.TRACKED_PLAYERS_IN_WORLD) {
            return this.displayTrackedPlayersInWorld;
        }
        if (o == ModOptions.SCALED_MAX_WAYPOINT_DISTANCE) {
            return this.dimensionScaledMaxWaypointDistance;
        }
        if (o == ModOptions.PAC_CLAIMS) {
            return this.displayClaims;
        }
        if (o == ModOptions.PAC_CURRENT_CLAIM) {
            return this.displayCurrentClaim;
        }
        return false;
    }

    public static String getTranslation(boolean o) {
        return class_1074.method_4662((String)("gui.xaero_" + (o ? "on" : "off")), (Object[])new Object[0]);
    }

    private void changeZoomUnchecked(int direction) {
        this.zoom = class_3532.method_15387((int)(this.zoom + direction), (int)zooms.length);
    }

    public void changeZoom(int direction) {
        XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
        if (minimapSession != null) {
            double targetBefore = minimapSession.getMinimapProcessor().getTargetZoom();
            int attempts = 0;
            do {
                this.changeZoomUnchecked(direction);
            } while (++attempts < zooms.length && targetBefore == minimapSession.getMinimapProcessor().getTargetZoom());
            if (attempts == zooms.length) {
                this.changeZoomUnchecked(direction);
            }
        } else {
            this.changeZoomUnchecked(direction);
        }
    }

    public void toggleBooleanOptionValue(ModOptions par1EnumOptions) {
        if (par1EnumOptions.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return;
        }
        if (!par1EnumOptions.enumBoolean) {
            return;
        }
        this.setOptionValue(par1EnumOptions, (Boolean)this.getOptionValue(par1EnumOptions) == false);
    }

    public void setOptionValue(ModOptions par1EnumOptions, Object value) {
        if (par1EnumOptions.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return;
        }
        if (this.usesWorldMapOptionValue(par1EnumOptions)) {
            this.modMain.getSupportMods().worldmapSupport.openSettings();
            return;
        }
        if (this.usesWorldMapScreenValue(par1EnumOptions)) {
            this.modMain.getSupportMods().worldmapSupport.openScreenForOption(par1EnumOptions);
            return;
        }
        if (par1EnumOptions == ModOptions.ZOOM) {
            this.changeZoomUnchecked((Integer)value - this.zoom);
            this.refreshScreen();
        } else if (par1EnumOptions == ModOptions.MINIMAP) {
            BuiltInHudModules.MINIMAP.setActive((Boolean)value);
        } else if (par1EnumOptions == ModOptions.CAVE_MAPS) {
            this.caveMaps = (Integer)value;
        } else if (par1EnumOptions == ModOptions.CAVE_ZOOM) {
            this.caveZoom = (Integer)value;
        } else if (par1EnumOptions == ModOptions.WAYPOINTS) {
            this.showWaypoints = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.DEATHPOINTS) {
            this.deathpoints = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.OLD_DEATHPOINTS) {
            this.oldDeathpoints = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.INGAME_WAYPOINTS) {
            this.showIngameWaypoints = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.REDSTONE) {
            this.displayRedstone = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.DISTANCE) {
            this.distance = (Integer)value;
        } else if (par1EnumOptions == ModOptions.NORTH) {
            this.lockNorth = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.SLIME_CHUNKS) {
            this.slimeChunks = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.SAFE_MAP) {
            this.mapSafeMode = (Boolean)value;
            minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                minimapSession.getMinimapProcessor().setToResetImage(true);
            }
            this.refreshScreen();
        } else if (par1EnumOptions == ModOptions.AA) {
            this.antiAliasing = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.COLOURS) {
            this.blockColours = (Integer)value;
        } else if (par1EnumOptions == ModOptions.LIGHT) {
            this.lighting = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.COMPASS) {
            this.compassOverEverything = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.FLOWERS) {
            this.showFlowers = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.KEEP_WP_NAMES) {
            this.keepWaypointNames = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.ARROW_COLOUR) {
            this.arrowColour = (Integer)value;
        } else if (par1EnumOptions == ModOptions.SMOOTH_DOTS) {
            this.smoothDots = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.DOTS_STYLE) {
            this.dotsStyle = (Integer)value;
        } else if (par1EnumOptions == ModOptions.WORLD_MAP) {
            this.worldMap = (Boolean)value;
            this.refreshScreen();
        } else if (par1EnumOptions == ModOptions.TERRAIN_DEPTH) {
            this.terrainDepth = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.TERRAIN_SLOPES) {
            this.terrainSlopes = (Integer)value;
        } else if (par1EnumOptions == ModOptions.MAIN_ENTITY_AS) {
            this.mainEntityAs = (Integer)value;
        } else if (par1EnumOptions == ModOptions.BLOCK_TRANSPARENCY) {
            this.blockTransparency = (Boolean)value;
            minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                minimapSession.getMinimapProcessor().setToResetImage(true);
            }
        } else if (par1EnumOptions == ModOptions.HIDE_WORLD_NAMES) {
            this.hideWorldNames = (Integer)value;
        } else if (par1EnumOptions == ModOptions.OPEN_SLIME_SETTINGS) {
            this.openSlimeSettings = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.ALWAYS_SHOW_DISTANCE) {
            this.alwaysShowDistance = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.CROSS_DIMENSIONAL_TP) {
            this.crossDimensionalTp = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.BIOMES_VANILLA) {
            this.biomeColorsVanillaMode = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.CENTERED_ENLARGED) {
            this.centeredEnlarged = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.ZOOM_ON_ENLARGE) {
            this.zoomOnEnlarged = (Integer)value;
        } else if (par1EnumOptions == ModOptions.MINIMAP_TEXT_ALIGN) {
            this.minimapTextAlign = (Integer)value;
        } else if (par1EnumOptions == ModOptions.COMPASS_LOCATION) {
            this.compassLocation = (Integer)value;
        } else if (par1EnumOptions == ModOptions.HIDE_WP_COORDS) {
            this.hideWaypointCoordinates = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.WAYPOINTS_ALL_SETS) {
            this.renderAllSets = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.IGNORE_HEIGHTMAPS) {
            MinimapWorldRootContainer currentRootContainer = BuiltInHudModules.MINIMAP.getCurrentSession().getWorldManager().getAutoRootContainer();
            currentRootContainer.getConfig().setIgnoreHeightmaps((Boolean)value);
            BuiltInHudModules.MINIMAP.getCurrentSession().getWorldManagerIO().getRootConfigIO().save(currentRootContainer);
        } else if (par1EnumOptions == ModOptions.WAYPOINTS_BOTTOM) {
            this.waypointsBottom = (Boolean)value;
        } else if (par1EnumOptions == ModOptions.MINIMAP_SHAPE) {
            this.minimapShape = (Integer)value;
        } else if (par1EnumOptions == ModOptions.LIGHT_OVERLAY_TYPE) {
            this.lightOverlayType = (Integer)value;
        } else if (par1EnumOptions == ModOptions.BOSS_HEALTH_PUSHBOX) {
            this.bossHealthPushBox = (Integer)value;
        } else if (par1EnumOptions == ModOptions.POTION_EFFECTS_PUSHBOX) {
            this.potionEffectPushBox = (Integer)value;
        } else if (par1EnumOptions == ModOptions.MINIMAP_FRAME) {
            this.minimapFrame = (Integer)value;
        } else if (par1EnumOptions == ModOptions.MULTIPLE_WAYPOINT_INFO) {
            this.displayMultipleWaypointInfo = (Integer)value;
        } else if (par1EnumOptions == ModOptions.RADAR_DISPLAYED) {
            this.entityRadar = (Boolean)value;
        } else {
            if (par1EnumOptions == ModOptions.EAMOUNT) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.ENTITY_NUMBER, (Integer)value);
                this.modMain.getEntityRadarCategoryManager().save();
                return;
            }
            if (par1EnumOptions == ModOptions.ENTITY_HEIGHT) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.HEIGHT_FADE, (Boolean)value != false ? 1 : 0);
                this.modMain.getEntityRadarCategoryManager().save();
                return;
            }
            if (par1EnumOptions == ModOptions.ENTITY_NAMETAGS) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.ALWAYS_NAMETAGS, (Boolean)value != false ? 1 : 0);
                this.modMain.getEntityRadarCategoryManager().save();
                return;
            }
            if (par1EnumOptions == ModOptions.RADAR_Y_DISPLAYED) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.DISPLAY_Y, (Integer)value);
                return;
            }
            if (par1EnumOptions == ModOptions.ICON_NAME_FALLBACK) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.ICON_NAME_FALLBACK, (Boolean)value != false ? 1 : 0);
                this.modMain.getEntityRadarCategoryManager().save();
                return;
            }
            if (par1EnumOptions == ModOptions.RADAR_ICONS_DISPLAYED) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.ICONS, (Integer)value);
                this.modMain.getEntityRadarCategoryManager().save();
                return;
            }
            if (par1EnumOptions == ModOptions.RADAR_NAMES_DISPLAYED) {
                this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.NAMES, (Integer)value);
                this.modMain.getEntityRadarCategoryManager().save();
                return;
            }
            if (par1EnumOptions == ModOptions.UPDATE_NOTIFICATION) {
                updateNotification = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS) {
                this.adjustHeightForCarpetLikeBlocks = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.PARTIAL_Y_TELEPORTATION) {
                this.partialYTeleportation = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.DELETE_REACHED_DEATHPOINTS) {
                this.deleteReachedDeathpoints = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.HIDE_MINIMAP_UNDER_SCREEN) {
                this.hideMinimapUnderScreen = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.HIDE_MINIMAP_UNDER_F3) {
                this.hideMinimapUnderF3 = (Boolean)value;
            } else {
                if (par1EnumOptions == ModOptions.RADAR_OVER_FRAME) {
                    this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.RENDER_OVER_MINIMAP, (Integer)value);
                    return;
                }
                if (par1EnumOptions == ModOptions.TEMPORARY_WAYPOINTS_GLOBAL) {
                    this.temporaryWaypointsGlobal = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.KEEP_ENLARGED_UNLOCKED) {
                    this.keepUnlockedWhenEnlarged = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.TOGGLED_ENLARGED) {
                    this.enlargedMinimapAToggle = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.DISPLAY_STAINED_GLASS) {
                    this.displayStainedGlass = (Boolean)value;
                    minimapSession = XaeroMinimapSession.getCurrentSession();
                    if (minimapSession != null) {
                        minimapSession.getMinimapProcessor().setToResetImage(true);
                    }
                } else if (par1EnumOptions == ModOptions.SWITCH_TO_AUTO_ON_DEATH) {
                    this.switchToAutoOnDeath = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.LEGIBLE_CAVE_MAPS) {
                    this.legibleCaveMaps = (Boolean)value;
                    minimapSession = XaeroMinimapSession.getCurrentSession();
                    if (minimapSession != null) {
                        minimapSession.getMinimapProcessor().setToResetImage(true);
                    }
                } else if (par1EnumOptions == ModOptions.BIOME_BLENDING) {
                    this.biomeBlending = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.TRACKED_PLAYERS_ON_MAP) {
                    this.displayTrackedPlayersOnMap = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.TRACKED_PLAYERS_IN_WORLD) {
                    this.displayTrackedPlayersInWorld = (Boolean)value;
                } else if (par1EnumOptions == ModOptions.SCALED_MAX_WAYPOINT_DISTANCE) {
                    this.dimensionScaledMaxWaypointDistance = !this.dimensionScaledMaxWaypointDistance;
                } else if (par1EnumOptions == ModOptions.PAC_CLAIMS) {
                    DimensionHighlighterHandler hh;
                    this.displayClaims = !this.displayClaims;
                    minimapSession = XaeroMinimapSession.getCurrentSession();
                    if (minimapSession != null && (hh = minimapSession.getMinimapProcessor().getMinimapWriter().getDimensionHighlightHandler()) != null) {
                        hh.requestRefresh();
                    }
                } else if (par1EnumOptions == ModOptions.PAC_CURRENT_CLAIM) {
                    this.displayCurrentClaim = !this.displayCurrentClaim;
                }
            }
        }
        try {
            this.saveSettings();
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    private void refreshScreen() {
        class_437 currentScreen = class_310.method_1551().field_1755;
        GuiSettings settingsScreen = currentScreen instanceof GuiSettings ? (GuiSettings)currentScreen : null;
        int focusedIndex = -1;
        if (settingsScreen != null) {
            focusedIndex = settingsScreen.getIndex(settingsScreen.method_25399());
        }
        class_310.method_1551().method_1507(currentScreen);
        settingsScreen.restoreFocus(focusedIndex);
    }

    public void setOptionDoubleValue(ModOptions options, double d) {
        DimensionHighlighterHandler hh;
        XaeroMinimapSession minimapSession;
        if (options.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return;
        }
        if (this.usesWorldMapOptionValue(options)) {
            this.modMain.getSupportMods().worldmapSupport.openSettings();
            return;
        }
        if (this.usesWorldMapScreenValue(options)) {
            this.modMain.getSupportMods().worldmapSupport.openScreenForOption(options);
            return;
        }
        if (options == ModOptions.OPACITY) {
            this.minimapOpacity = d;
        }
        if (options == ModOptions.WAYPOINTS_ICON_SCALE) {
            this.waypointsIngameIconScale = (int)d;
        }
        if (options == ModOptions.WAYPOINTS_DISTANCE_SCALE) {
            this.waypointsIngameDistanceScale = (int)d;
        }
        if (options == ModOptions.WAYPOINTS_NAME_SCALE) {
            this.waypointsIngameNameScale = (int)d;
        }
        if (options == ModOptions.WAYPOINTS_CLOSE_SCALE) {
            this.waypointsIngameCloseScale = d;
        }
        if (options == ModOptions.DOT_NAME_SCALE) {
            this.dotNameScale = d;
        }
        if (options == ModOptions.WAYPOINTS_DISTANCE) {
            this.waypointsDistanceExp = (int)d;
        }
        if (options == ModOptions.WAYPOINTS_DISTANCE_MIN) {
            this.waypointsDistanceMin = (int)d;
        }
        if (options == ModOptions.ARROW_SCALE) {
            this.arrowScale = d;
        }
        if (options == ModOptions.WAYPOINT_OPACITY_INGAME) {
            this.waypointOpacityIngame = (int)d;
        }
        if (options == ModOptions.WAYPOINT_OPACITY_MAP) {
            this.waypointOpacityMap = (int)d;
        }
        if (options == ModOptions.WAYPOINT_LOOKING_ANGLE) {
            this.lookingAtAngle = (int)d;
        }
        if (options == ModOptions.WAYPOINT_VERTICAL_LOOKING_ANGLE) {
            this.lookingAtAngleVertical = (int)d;
        }
        if (options == ModOptions.CAVE_MAPS_DEPTH) {
            this.caveMapsDepth = (int)d;
        }
        if (options == ModOptions.CHUNK_GRID) {
            this.chunkGrid = (int)d;
        }
        if (options == ModOptions.PLAYER_ARROW_OPACITY) {
            this.playerArrowOpacity = (int)d;
        }
        if (options == ModOptions.LIGHT_OVERLAY_COLOR) {
            this.lightOverlayColor = (int)d;
        }
        if (options == ModOptions.LIGHT_OVERLAY_MAX_LIGHT) {
            this.lightOverlayMaxLight = (int)d;
        }
        if (options == ModOptions.LIGHT_OVERLAY_MIN_LIGHT) {
            this.lightOverlayMinLight = (int)d;
        }
        if (options == ModOptions.SIZE) {
            this.minimapSize = d == 54.0 ? 0 : (int)d;
        }
        if (options == ModOptions.UI_SCALE) {
            this.uiScale = (int)d;
        }
        if (options == ModOptions.MINIMAP_FRAME_COLOR) {
            this.minimapFrameColor = (int)d;
        }
        if (options == ModOptions.COMPASS_SCALE) {
            this.compassDirectionScale = (int)d;
        }
        if (options == ModOptions.COMPASS_COLOR) {
            this.compassColor = (int)d;
        }
        if (options == ModOptions.NORTH_COMPASS_COLOR) {
            this.northCompassColor = (int)d;
        }
        if (options == ModOptions.DOTS_SIZE) {
            this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.DOT_SIZE, (int)d);
            return;
        }
        if (options == ModOptions.HEADS_SCALE) {
            double currentScale = this.modMain.getEntityRadarCategoryManager().getRootCategory().getSettingValue(EntityRadarCategorySettings.ICON_SCALE);
            this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.ICON_SCALE, (int)d);
            double newScale = this.modMain.getEntityRadarCategoryManager().getRootCategory().getSettingValue(EntityRadarCategorySettings.ICON_SCALE);
            if (newScale < 1.0 || newScale < 1.0 != currentScale < 1.0) {
                this.modMain.getMinimap().getMinimapFBORenderer().resetEntityIcons();
            }
            return;
        }
        if (options == ModOptions.HEIGHT_LIMIT) {
            this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.HEIGHT_LIMIT, (int)d);
            return;
        }
        if (options == ModOptions.START_FADING_AT) {
            this.setOptionIndexForRadarSetting(EntityRadarCategorySettings.START_FADING_AT, (int)d);
            return;
        }
        if (options == ModOptions.AUTO_CONVERT_TO_KM) {
            int n = this.autoConvertWaypointDistanceToKmThreshold = d <= 0.0 ? (int)d : (int)Math.pow(10.0, d - 1.0);
        }
        if (options == ModOptions.WP_DISTANCE_PRECISION) {
            this.waypointDistancePrecision = (int)d;
        }
        if (options == ModOptions.MAIN_DOT_SIZE) {
            this.mainDotSize = (int)d;
        }
        if (options == ModOptions.MANUAL_CAVE_MODE_START) {
            this.manualCaveModeStart = (int)d * 8 - 65;
            boolean bl = this.manualCaveModeStartAuto = (int)d == 0;
        }
        if (options == ModOptions.CHUNK_GRID_LINE_WIDTH) {
            this.chunkGridLineWidth = (int)d;
        }
        if (options == ModOptions.WAYPOINT_ONMAP_SCALE) {
            this.waypointOnMapScale = (int)d;
        }
        if (options == ModOptions.INFO_DISPLAY_BG_OPACITY) {
            this.infoDisplayBackgroundOpacity = (int)d;
        }
        if (options == ModOptions.CAVE_MODE_TOGGLE_TIMER) {
            this.caveModeToggleTimer = (int)d;
        }
        if (options == ModOptions.TRACKED_PLAYER_WORLD_ICON_SCALE) {
            this.trackedPlayerWorldIconScale = (int)d;
        }
        if (options == ModOptions.TRACKED_PLAYER_WORLD_NAME_SCALE) {
            this.trackedPlayerWorldNameScale = (int)d;
        }
        if (options == ModOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE) {
            this.trackedPlayerMinimapIconScale = (int)d;
        }
        if (options == ModOptions.PAC_CLAIMS_BORDER_OPACITY) {
            this.claimsBorderOpacity = (int)d;
            if (this.displayClaims && (minimapSession = XaeroMinimapSession.getCurrentSession()) != null && (hh = minimapSession.getMinimapProcessor().getMinimapWriter().getDimensionHighlightHandler()) != null) {
                hh.requestRefresh();
            }
        }
        if (options == ModOptions.PAC_CLAIMS_FILL_OPACITY) {
            this.claimsFillOpacity = (int)d;
            if (this.displayClaims && (minimapSession = XaeroMinimapSession.getCurrentSession()) != null && (hh = minimapSession.getMinimapProcessor().getMinimapWriter().getDimensionHighlightHandler()) != null) {
                hh.requestRefresh();
            }
        }
        try {
            this.saveSettings();
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    public double getOptionDoubleValue(ModOptions options) {
        if (options.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return 0.0;
        }
        if (options == ModOptions.OPACITY) {
            return this.minimapOpacity;
        }
        if (options == ModOptions.WAYPOINTS_ICON_SCALE) {
            return this.waypointsIngameIconScale;
        }
        if (options == ModOptions.WAYPOINTS_DISTANCE_SCALE) {
            return this.waypointsIngameDistanceScale;
        }
        if (options == ModOptions.WAYPOINTS_NAME_SCALE) {
            return this.waypointsIngameNameScale;
        }
        if (options == ModOptions.WAYPOINTS_CLOSE_SCALE) {
            return this.waypointsIngameCloseScale;
        }
        if (options == ModOptions.DOT_NAME_SCALE) {
            return this.dotNameScale;
        }
        if (options == ModOptions.WAYPOINTS_DISTANCE) {
            return this.waypointsDistanceExp;
        }
        if (options == ModOptions.WAYPOINTS_DISTANCE_MIN) {
            return this.waypointsDistanceMin;
        }
        if (options == ModOptions.ARROW_SCALE) {
            return this.arrowScale;
        }
        if (options == ModOptions.WAYPOINT_OPACITY_INGAME) {
            return this.waypointOpacityIngame;
        }
        if (options == ModOptions.WAYPOINT_OPACITY_MAP) {
            return this.waypointOpacityMap;
        }
        if (options == ModOptions.WAYPOINT_LOOKING_ANGLE) {
            return this.lookingAtAngle;
        }
        if (options == ModOptions.WAYPOINT_VERTICAL_LOOKING_ANGLE) {
            return this.lookingAtAngleVertical;
        }
        if (options == ModOptions.CAVE_MAPS_DEPTH) {
            return this.caveMapsDepth;
        }
        if (options == ModOptions.CHUNK_GRID) {
            return this.chunkGrid;
        }
        if (options == ModOptions.PLAYER_ARROW_OPACITY) {
            return this.playerArrowOpacity;
        }
        if (options == ModOptions.LIGHT_OVERLAY_COLOR) {
            return this.lightOverlayColor;
        }
        if (options == ModOptions.LIGHT_OVERLAY_MAX_LIGHT) {
            return this.lightOverlayMaxLight;
        }
        if (options == ModOptions.LIGHT_OVERLAY_MIN_LIGHT) {
            return this.lightOverlayMinLight;
        }
        if (options == ModOptions.SIZE) {
            return this.minimapSize;
        }
        if (options == ModOptions.UI_SCALE) {
            return this.uiScale;
        }
        if (options == ModOptions.MINIMAP_FRAME_COLOR) {
            return this.minimapFrameColor;
        }
        if (options == ModOptions.COMPASS_SCALE) {
            return this.compassDirectionScale;
        }
        if (options == ModOptions.COMPASS_COLOR) {
            return this.compassColor;
        }
        if (options == ModOptions.NORTH_COMPASS_COLOR) {
            return this.northCompassColor;
        }
        if (options == ModOptions.DOTS_SIZE) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.DOT_SIZE);
        }
        if (options == ModOptions.HEADS_SCALE) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.ICON_SCALE);
        }
        if (options == ModOptions.HEIGHT_LIMIT) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.HEIGHT_LIMIT);
        }
        if (options == ModOptions.START_FADING_AT) {
            return this.getOptionIndexForRadarSetting(EntityRadarCategorySettings.START_FADING_AT);
        }
        if (options == ModOptions.AUTO_CONVERT_TO_KM) {
            return this.autoConvertWaypointDistanceToKmThreshold <= 0 ? (double)this.autoConvertWaypointDistanceToKmThreshold : 1.0 + Math.log10(this.autoConvertWaypointDistanceToKmThreshold);
        }
        if (options == ModOptions.WP_DISTANCE_PRECISION) {
            return this.waypointDistancePrecision;
        }
        if (options == ModOptions.MAIN_DOT_SIZE) {
            return this.mainDotSize;
        }
        if (options == ModOptions.MANUAL_CAVE_MODE_START) {
            return (this.manualCaveModeStart + 65) / 8;
        }
        if (options == ModOptions.CHUNK_GRID_LINE_WIDTH) {
            return this.chunkGridLineWidth;
        }
        if (options == ModOptions.WAYPOINT_ONMAP_SCALE) {
            return this.waypointOnMapScale;
        }
        if (options == ModOptions.INFO_DISPLAY_BG_OPACITY) {
            return this.infoDisplayBackgroundOpacity;
        }
        if (options == ModOptions.CAVE_MODE_TOGGLE_TIMER) {
            return this.caveModeToggleTimer;
        }
        if (options == ModOptions.TRACKED_PLAYER_WORLD_ICON_SCALE) {
            return this.trackedPlayerWorldIconScale;
        }
        if (options == ModOptions.TRACKED_PLAYER_WORLD_NAME_SCALE) {
            return this.trackedPlayerWorldNameScale;
        }
        if (options == ModOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE) {
            return this.trackedPlayerMinimapIconScale;
        }
        if (options == ModOptions.PAC_CLAIMS_BORDER_OPACITY) {
            return this.claimsBorderOpacity;
        }
        if (options == ModOptions.PAC_CLAIMS_FILL_OPACITY) {
            return this.claimsFillOpacity;
        }
        return 1.0;
    }

    private <T> int getOptionIndexForRadarSetting(ObjectCategorySetting<T> setting) {
        EntityRadarCategory rootCategory = this.modMain.getEntityRadarCategoryManager().getRootCategory();
        return setting.getIndexWriter().apply(rootCategory.getSettingValue(setting));
    }

    public boolean minimapDisabled() {
        return (serverSettings & 1) != 1;
    }

    public boolean caveMapsDisabled() {
        return (serverSettings & 0x4000) != 16384 || this.modMain.isFairPlay() || class_310.method_1551().field_1687 != null && (!MinimapClientWorldDataHelper.getCurrentWorldData().getSyncedRules().allowCaveModeOnServer && class_310.method_1551().field_1687.method_27983() != class_1937.field_25180 || !MinimapClientWorldDataHelper.getCurrentWorldData().getSyncedRules().allowNetherCaveModeOnServer && class_310.method_1551().field_1687.method_27983() == class_1937.field_25180);
    }

    public boolean showWaypointsDisabled() {
        return (serverSettings & 0x10000) != 65536;
    }

    public boolean deathpointsDisabled() {
        return (serverSettings & 0x200000) == 0;
    }

    public void resetServerSettings() {
        serverSettings = defaultSettings;
    }

    public static void setServerSettings() {
    }

    public static boolean canEditIngameSettings() {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        return minimapSession != null && minimapSession.getWorldState().getAutoWorldPath() != null;
    }

    private <T> String getRadarSettingOptionName(ObjectCategorySetting<T> setting) {
        EntityRadarCategory rootCategory = this.modMain.getEntityRadarCategoryManager().getRootCategory();
        return setting.getWidgetValueNameProvider().apply(rootCategory.getSettingValue(setting));
    }

    private <T> void setOptionIndexForRadarSetting(ObjectCategorySetting<T> setting, int index) {
        EntityRadarCategory rootCategory = this.modMain.getEntityRadarCategoryManager().getRootCategory();
        rootCategory.setSettingValue(setting, setting.getIndexReader().apply(index));
        if (class_310.method_1551().field_1755 instanceof GuiSettings) {
            ((GuiSettings)class_310.method_1551().field_1755).setShouldSaveRadar();
        }
    }

    public EntityRadarBackwardsCompatibilityConfig getEntityRadarBackwardsCompatibilityConfig() {
        return this.entityRadarBackwardsCompatibilityConfig;
    }

    public void resetEntityRadarBackwardsCompatibilityConfig() {
        this.entityRadarBackwardsCompatibilityConfig = new EntityRadarBackwardsCompatibilityConfig();
        this.foundOldRadarSettings = false;
    }

    public boolean foundOldRadarSettings() {
        return this.foundOldRadarSettings;
    }

    public int getManualCaveModeStart() {
        return this.usesWorldMapScreenValue(ModOptions.MANUAL_CAVE_MODE_START) ? this.modMain.getSupportMods().worldmapSupport.getManualCaveStart() : (this.manualCaveModeStartAuto ? Integer.MAX_VALUE : this.manualCaveModeStart);
    }

    static {
        ENCHANT_COLORS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        ENCHANT_COLOR_NAMES = new String[]{"gui.xaero_black", "gui.xaero_dark_blue", "gui.xaero_dark_green", "gui.xaero_dark_aqua", "gui.xaero_dark_red", "gui.xaero_dark_purple", "gui.xaero_gold", "gui.xaero_gray", "gui.xaero_dark_gray", "gui.xaero_blue", "gui.xaero_green", "gui.xaero_aqua", "gui.xaero_red", "gui.xaero_purple", "gui.xaero_yellow", "gui.xaero_white"};
        COLORS = new int[]{-16777216, -16777046, -16733696, -16733526, -5636096, -5635926, -22016, -5592406, -11184811, -11184641, -11141291, -11141121, -65536, -43521, -171, -1};
        minimapItemId = null;
        minimapItem = null;
        zooms = new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        serverSlimeSeeds = new HashMap();
        distanceTypes = new String[]{"gui.xaero_off", "gui.xaero_looking_at", "gui.xaero_all"};
        blockColourTypes = new String[]{"gui.xaero_accurate", "gui.xaero_vanilla"};
        settingsButton = false;
        updateNotification = true;
        arrowColours = new float[][]{{0.8f, 0.1f, 0.1f, 1.0f}, {0.09f, 0.57f, 0.0f, 1.0f}, {0.0f, 0.55f, 1.0f, 1.0f}, {1.0f, 0.93f, 0.0f, 1.0f}, {0.73f, 0.33f, 0.83f, 1.0f}, {1.0f, 1.0f, 1.0f, 1.0f}, {0.0f, 0.0f, 0.0f, 1.0f}, {0.4588f, 0.0f, 0.0f, 1.0f}};
        ENTITY_ICONS_OPTIONS = new String[]{"gui.xaero_icons_off", "gui.xaero_icons_list", "gui.xaero_icons_always", "-"};
        SLOPES_MODES = new String[]{"gui.xaero_off", "gui.xaero_slopes_legacy", "gui.xaero_slopes_default_3d", "gui.xaero_slopes_default_2d"};
        ENTITY_NAMES_OPTIONS = new String[]{"gui.xaero_names_off", "gui.xaero_names_list", "gui.xaero_names_always", "-"};
        SHOW_LIGHT_LEVEL_NAMES = new String[]{"gui.xaero_off", "gui.xaero_light_block", "gui.xaero_light_sky", "gui.xaero_light_all", "gui.xaero_light_both2"};
        MINIMAP_SHAPES = new String[]{"gui.xaero_minimap_shape_square", "gui.xaero_minimap_shape_circle"};
        DOTS_STYLES = new String[]{"gui.xaero_dots_style_default", "gui.xaero_dots_style_legacy"};
        PUSHBOX_OPTIONS = new String[]{"gui.xaero_off", "gui.xaero_pushbox_normal", "gui.xaero_pushbox_screen_height"};
        FRAME_OPTIONS = new String[]{"gui.xaero_minimap_frame_default", "gui.xaero_minimap_frame_colored_thick", "gui.xaero_minimap_frame_colored_thin", "gui.xaero_off"};
        COMPASS_OPTIONS = new String[]{"gui.xaero_off", "gui.xaero_minimap_compass_inside_frame", "gui.xaero_minimap_compass_on_frame"};
        MULTIPLE_WAYPOINT_INFO = new String[]{"gui.xaero_off", "gui.xaero_while_sneaking", "gui.xaero_multiple_waypoints_always"};
        RADAR_OVER_MAP_OPTIONS = new String[]{"gui.xaero_radar_over_map_never", "gui.xaero_radar_over_map_list", "gui.xaero_radar_over_map_always", "-"};
        OLD_MINIMAP_SIZES = new int[]{57, 85, 113, 169};
    }
}

