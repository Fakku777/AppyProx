/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3288
 *  net.minecraft.class_7923
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package xaero.map.settings;

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
import java.util.Collection;
import net.minecraft.class_1074;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3288;
import net.minecraft.class_7923;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import xaero.map.MapFullReloader;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.effects.Effects;
import xaero.map.gui.ExportScreen;
import xaero.map.gui.GuiMap;
import xaero.map.mcworld.WorldMapClientWorldData;
import xaero.map.mcworld.WorldMapClientWorldDataHelper;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.settings.ModOptions;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class ModSettings {
    public String[] arrowColourNames = new String[]{"gui.xaero_wm_red", "gui.xaero_wm_green", "gui.xaero_wm_blue", "gui.xaero_wm_yellow", "gui.xaero_wm_purple", "gui.xaero_wm_white", "gui.xaero_wm_black", "gui.xaero_wm_legacy_color"};
    public static float[][] arrowColours = new float[][]{{0.8f, 0.1f, 0.1f, 1.0f}, {0.09f, 0.57f, 0.0f, 1.0f}, {0.0f, 0.55f, 1.0f, 1.0f}, {1.0f, 0.93f, 0.0f, 1.0f}, {0.73f, 0.33f, 0.83f, 1.0f}, {1.0f, 1.0f, 1.0f, 1.0f}, {0.0f, 0.0f, 0.0f, 1.0f}, {0.4588f, 0.0f, 0.0f, 1.0f}};
    public static int ignoreUpdate;
    public static final String format = "\u00a7";
    public static boolean updateNotification;
    private int regionCacheHashCode;
    public boolean debug = false;
    public boolean detailed_debug = false;
    public boolean lighting = true;
    public boolean loadChunks = true;
    public boolean updateChunks = true;
    public int terrainSlopes = 2;
    public String[] slopeNames = new String[]{"gui.xaero_off", "gui.xaero_wm_slopes_legacy", "gui.xaero_wm_slopes_default_3d", "gui.xaero_wm_slopes_default_2d"};
    public boolean terrainDepth = true;
    public boolean footsteps = true;
    public boolean flowers = true;
    public boolean coordinates = true;
    public boolean hoveredBiome = true;
    public int colours = 0;
    public String[] colourNames = new String[]{"gui.xaero_accurate", "gui.xaero_vanilla"};
    public boolean biomeColorsVanillaMode = false;
    public boolean differentiateByServerAddress = true;
    public boolean waypoints = true;
    public boolean renderArrow = true;
    public boolean displayZoom = true;
    public boolean openMapAnimation = true;
    public float worldmapWaypointsScale = 1.0f;
    public int reloadVersion = 0;
    public boolean reloadEverything = false;
    public boolean zoomButtons = true;
    public boolean waypointBackgrounds = true;
    private boolean caveMapsAllowed = true;
    public boolean pauseRequests = false;
    public boolean extraDebug = false;
    public boolean ignoreHeightmaps;
    public static String mapItemId;
    public static class_1792 mapItem;
    public boolean detectAmbiguousY = true;
    public boolean showDisabledWaypoints;
    public boolean closeWaypointsWhenHopping = true;
    public boolean adjustHeightForCarpetLikeBlocks = true;
    public boolean onlyCurrentMapWaypoints = false;
    public double minZoomForLocalWaypoints = 0.0;
    public int arrowColour = -2;
    public boolean minimapRadar = true;
    public boolean renderWaypoints = true;
    public boolean partialYTeleportation = true;
    public boolean displayStainedGlass = true;
    public int caveModeDepth = 30;
    public int autoCaveMode = -1;
    public boolean legibleCaveMaps;
    public int caveModeStart = Integer.MAX_VALUE;
    public boolean displayCaveModeStart = true;
    public int caveModeToggleTimer = 1000;
    public int defaultCaveModeType = 1;
    public boolean biomeBlending = true;
    public boolean multipleImagesExport;
    public boolean nightExport;
    public boolean highlightsExport;
    public int exportScaleDownSquare = 20;
    public boolean allowInternetAccess = true;
    public int mapWritingDistance = -1;
    public boolean trackedPlayers = true;
    public boolean displayClaims = true;
    public int claimsFillOpacity = 46;
    public int claimsBorderOpacity;
    public int claimsOpacity = this.claimsBorderOpacity = 80;

    public boolean isCaveMapsAllowed() {
        if (!this.caveMapsAllowed) {
            return false;
        }
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (SupportMods.minimap() && SupportMods.xaeroMinimap.isFairPlay() && (worldmapSession == null || !worldmapSession.getMapProcessor().isConsideringNetherFairPlay() || worldmapSession.getMapProcessor().getMapWorld().getCurrentDimensionId() != class_1937.field_25180)) {
            return false;
        }
        if (class_310.method_1551().field_1687 == null) {
            return true;
        }
        if (Misc.hasEffect(Effects.NO_CAVE_MAPS) || Misc.hasEffect(Effects.NO_CAVE_MAPS_HARMFUL)) {
            return false;
        }
        WorldMapClientWorldData clientData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        return clientData.getSyncedRules().allowCaveModeOnServer && class_310.method_1551().field_1687.method_27983() != class_1937.field_25180 || clientData.getSyncedRules().allowNetherCaveModeOnServer && class_310.method_1551().field_1687.method_27983() == class_1937.field_25180;
    }

    public void saveSettings() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(WorldMap.optionsFile));
        writer.println("ignoreUpdate:" + ignoreUpdate);
        writer.println("updateNotification:" + updateNotification);
        writer.println("allowInternetAccess:" + this.allowInternetAccess);
        writer.println("differentiateByServerAddress:" + this.differentiateByServerAddress);
        writer.println("caveMapsAllowed:" + this.caveMapsAllowed);
        writer.println("debug:" + this.debug);
        writer.println("lighting:" + this.lighting);
        writer.println("colours:" + this.colours);
        writer.println("loadChunks:" + this.loadChunks);
        writer.println("updateChunks:" + this.updateChunks);
        writer.println("terrainSlopes:" + this.terrainSlopes);
        writer.println("terrainDepth:" + this.terrainDepth);
        writer.println("footsteps:" + this.footsteps);
        writer.println("flowers:" + this.flowers);
        writer.println("coordinates:" + this.coordinates);
        writer.println("hoveredBiome:" + this.hoveredBiome);
        writer.println("biomeColorsVanillaMode:" + this.biomeColorsVanillaMode);
        writer.println("waypoints:" + this.waypoints);
        writer.println("renderArrow:" + this.renderArrow);
        writer.println("displayZoom:" + this.displayZoom);
        writer.println("worldmapWaypointsScale:" + this.worldmapWaypointsScale);
        writer.println("openMapAnimation:" + this.openMapAnimation);
        writer.println("reloadVersion:" + this.reloadVersion);
        writer.println("reloadEverything:" + this.reloadEverything);
        writer.println("zoomButtons:" + this.zoomButtons);
        writer.println("waypointBackgrounds:" + this.waypointBackgrounds);
        if (mapItemId != null) {
            writer.println("mapItemId:" + mapItemId);
        }
        writer.println("detectAmbiguousY:" + this.detectAmbiguousY);
        writer.println("showDisabledWaypoints:" + this.showDisabledWaypoints);
        writer.println("closeWaypointsWhenHopping:" + this.closeWaypointsWhenHopping);
        writer.println("adjustHeightForCarpetLikeBlocks:" + this.adjustHeightForCarpetLikeBlocks);
        writer.println("onlyCurrentMapWaypoints:" + this.onlyCurrentMapWaypoints);
        writer.println("minZoomForLocalWaypoints:" + this.minZoomForLocalWaypoints);
        writer.println("arrowColour:" + this.arrowColour);
        writer.println("minimapRadar:" + this.minimapRadar);
        writer.println("renderWaypoints:" + this.renderWaypoints);
        writer.println("partialYTeleportation:" + this.partialYTeleportation);
        writer.println("displayStainedGlass:" + this.displayStainedGlass);
        writer.println("caveModeDepth:" + this.caveModeDepth);
        writer.println("caveModeStart:" + this.caveModeStart);
        writer.println("autoCaveMode:" + this.autoCaveMode);
        writer.println("legibleCaveMaps:" + this.legibleCaveMaps);
        writer.println("displayCaveModeStart:" + this.displayCaveModeStart);
        writer.println("caveModeToggleTimer:" + this.caveModeToggleTimer);
        writer.println("defaultCaveModeType:" + this.defaultCaveModeType);
        writer.println("biomeBlending:" + this.biomeBlending);
        writer.println("trackedPlayers:" + this.trackedPlayers);
        writer.println("multipleImagesExport:" + this.multipleImagesExport);
        writer.println("nightExport:" + this.nightExport);
        writer.println("highlightsExport:" + this.highlightsExport);
        writer.println("exportScaleDownSquare:" + this.exportScaleDownSquare);
        writer.println("mapWritingDistance:" + this.mapWritingDistance);
        writer.println("displayClaims:" + this.displayClaims);
        writer.println("claimsFillOpacity:" + this.claimsFillOpacity);
        writer.println("claimsBorderOpacity:" + this.claimsBorderOpacity);
        writer.println("globalVersion:" + WorldMap.globalVersion);
        writer.close();
    }

    private void loadDefaultSettings() throws IOException {
        File mainConfigFile = WorldMap.optionsFile;
        File defaultConfigFile = mainConfigFile.toPath().getParent().resolveSibling("defaultconfigs").resolve(mainConfigFile.getName()).toFile();
        if (defaultConfigFile.exists()) {
            this.loadSettingsFile(defaultConfigFile);
        }
    }

    public void loadSettings() throws IOException {
        this.loadDefaultSettings();
        File mainConfigFile = WorldMap.optionsFile;
        Path configFolderPath = mainConfigFile.toPath().getParent();
        if (!Files.exists(configFolderPath, new LinkOption[0])) {
            Files.createDirectories(configFolderPath, new FileAttribute[0]);
        }
        if (mainConfigFile.exists()) {
            this.loadSettingsFile(mainConfigFile);
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
            while ((s = reader.readLine()) != null) {
                String[] args = s.split(":");
                try {
                    if (args[0].equalsIgnoreCase("ignoreUpdate")) {
                        ignoreUpdate = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("updateNotification")) {
                        updateNotification = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("allowInternetAccess")) {
                        this.allowInternetAccess = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("differentiateByServerAddress")) {
                        this.differentiateByServerAddress = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("caveMapsAllowed")) {
                        this.caveMapsAllowed = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("debug")) {
                        this.debug = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("lighting")) {
                        this.lighting = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("colours")) {
                        this.colours = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("loadChunks")) {
                        this.loadChunks = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("updateChunks")) {
                        this.updateChunks = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("terrainSlopes")) {
                        this.terrainSlopes = args[1].equals("true") ? 2 : (args[1].equals("false") ? 0 : Integer.parseInt(args[1]));
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("terrainDepth")) {
                        this.terrainDepth = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("footsteps")) {
                        this.footsteps = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("flowers")) {
                        this.flowers = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("coordinates")) {
                        this.coordinates = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("hoveredBiome")) {
                        this.hoveredBiome = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("biomeColorsVanillaMode")) {
                        this.biomeColorsVanillaMode = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("waypoints")) {
                        this.waypoints = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("renderArrow")) {
                        this.renderArrow = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("displayZoom")) {
                        this.displayZoom = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("worldmapWaypointsScale")) {
                        this.worldmapWaypointsScale = Float.parseFloat(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("openMapAnimation")) {
                        this.openMapAnimation = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("reloadVersion")) {
                        this.reloadVersion = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("reloadEverything")) {
                        this.reloadEverything = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("zoomButtons")) {
                        this.zoomButtons = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("waypointBackgrounds")) {
                        this.waypointBackgrounds = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("mapItemId")) {
                        mapItemId = args[1] + ":" + args[2];
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("detectAmbiguousY")) {
                        this.detectAmbiguousY = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("showDisabledWaypoints")) {
                        this.showDisabledWaypoints = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("closeWaypointsWhenHopping")) {
                        this.closeWaypointsWhenHopping = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("adjustHeightForCarpetLikeBlocks")) {
                        this.adjustHeightForCarpetLikeBlocks = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("onlyCurrentMapWaypoints")) {
                        this.onlyCurrentMapWaypoints = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("minZoomForLocalWaypoints")) {
                        this.minZoomForLocalWaypoints = Double.parseDouble(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("arrowColour")) {
                        this.arrowColour = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("minimapRadar")) {
                        this.minimapRadar = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("renderWaypoints")) {
                        this.renderWaypoints = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("partialYTeleportation")) {
                        this.partialYTeleportation = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("displayStainedGlass")) {
                        this.displayStainedGlass = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("caveModeDepth")) {
                        this.caveModeDepth = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("caveModeStart")) {
                        this.caveModeStart = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("autoCaveMode")) {
                        this.autoCaveMode = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("legibleCaveMaps")) {
                        this.legibleCaveMaps = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("displayCaveModeStart")) {
                        this.displayCaveModeStart = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("caveModeToggleTimer")) {
                        this.caveModeToggleTimer = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("defaultCaveModeType")) {
                        this.defaultCaveModeType = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("biomeBlending")) {
                        this.biomeBlending = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("trackedPlayers") || args[0].equalsIgnoreCase("pacPlayers")) {
                        this.trackedPlayers = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("multipleImagesExport")) {
                        this.multipleImagesExport = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("nightExport")) {
                        this.nightExport = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("highlightsExport")) {
                        this.highlightsExport = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("exportScaleDownSquare")) {
                        this.exportScaleDownSquare = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("mapWritingDistance")) {
                        this.mapWritingDistance = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("displayClaims")) {
                        this.displayClaims = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("claimsOpacity")) {
                        this.claimsOpacity = this.claimsBorderOpacity = Math.min(100, Math.max(0, Integer.parseInt(args[1])));
                        this.claimsFillOpacity = this.claimsBorderOpacity * 58 / 100;
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("claimsBorderOpacity")) {
                        this.claimsBorderOpacity = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (args[0].equalsIgnoreCase("claimsFillOpacity")) {
                        this.claimsFillOpacity = Integer.parseInt(args[1]);
                        continue;
                    }
                    if (!args[0].equalsIgnoreCase("globalVersion")) continue;
                    WorldMap.globalVersion = Integer.parseInt(args[1]);
                }
                catch (Exception e) {
                    WorldMap.LOGGER.info("Skipping setting:" + args[0]);
                }
            }
        }
    }

    public String getOptionValueName(ModOptions par1EnumOptions) {
        if (par1EnumOptions.isDisabledBecauseNotIngame() || par1EnumOptions.isDisabledBecauseMinimap() || par1EnumOptions.isDisabledBecausePac()) {
            return ModSettings.getTranslation(false);
        }
        if (par1EnumOptions.enumBoolean) {
            return ModSettings.getTranslation(this.getClientBooleanValue(par1EnumOptions));
        }
        if (par1EnumOptions == ModOptions.COLOURS) {
            return class_1074.method_4662((String)this.colourNames[this.colours], (Object[])new Object[0]);
        }
        if (par1EnumOptions == ModOptions.SLOPES) {
            return class_1074.method_4662((String)this.slopeNames[this.terrainSlopes], (Object[])new Object[0]);
        }
        if (par1EnumOptions == ModOptions.ARROW_COLOUR) {
            String colourName = "gui.xaero_wm_team_color";
            if (this.arrowColour >= 0) {
                colourName = this.arrowColourNames[this.arrowColour];
            } else if (this.arrowColour == -2) {
                colourName = "gui.xaero_wm_color_minimap";
            }
            return class_1074.method_4662((String)colourName, (Object[])new Object[0]);
        }
        if (par1EnumOptions == ModOptions.AUTO_CAVE_MODE) {
            if (this.autoCaveMode == 0) {
                return class_1074.method_4662((String)"gui.xaero_off", (Object[])new Object[0]);
            }
            if (this.autoCaveMode < 0) {
                return class_1074.method_4662((String)"gui.xaero_auto_cave_mode_minimap", (Object[])new Object[0]);
            }
            int roofSideSize = this.autoCaveMode * 2 - 1;
            return roofSideSize + "x" + roofSideSize + " " + class_1074.method_4662((String)"gui.xaero_wm_ceiling", (Object[])new Object[0]);
        }
        if (par1EnumOptions == ModOptions.DEFAULT_CAVE_MODE_TYPE) {
            return class_1074.method_4662((String)(this.defaultCaveModeType == 0 ? "gui.xaero_off" : (this.defaultCaveModeType == 1 ? "gui.xaero_wm_cave_mode_type_layered" : "gui.xaero_wm_cave_mode_type_full")), (Object[])new Object[0]);
        }
        if (par1EnumOptions == ModOptions.PAC_CLAIMS_FILL_OPACITY) {
            return "" + this.claimsFillOpacity;
        }
        if (par1EnumOptions == ModOptions.PAC_CLAIMS_BORDER_OPACITY) {
            return "" + this.claimsBorderOpacity;
        }
        return "";
    }

    public boolean getClientBooleanValue(ModOptions o) {
        if (o.isDisabledBecauseNotIngame() || o.isDisabledBecauseMinimap() || o.isDisabledBecausePac()) {
            return false;
        }
        if (o == ModOptions.IGNORE_HEIGHTMAPS) {
            return WorldMapSession.getCurrentSession().getMapProcessor().getMapWorld().isIgnoreHeightmaps();
        }
        if (o == ModOptions.DEBUG) {
            return this.debug;
        }
        if (o == ModOptions.LIGHTING) {
            return this.lighting;
        }
        if (o == ModOptions.LOAD) {
            return this.loadChunks;
        }
        if (o == ModOptions.UPDATE) {
            return this.updateChunks;
        }
        if (o == ModOptions.DEPTH) {
            return this.terrainDepth;
        }
        if (o == ModOptions.STEPS) {
            return this.footsteps;
        }
        if (o == ModOptions.FLOWERS) {
            return this.flowers;
        }
        if (o == ModOptions.COORDINATES) {
            return this.coordinates;
        }
        if (o == ModOptions.HOVERED_BIOME) {
            return this.hoveredBiome;
        }
        if (o == ModOptions.BIOMES) {
            return this.biomeColorsVanillaMode;
        }
        if (o == ModOptions.WAYPOINTS) {
            return this.waypoints;
        }
        if (o == ModOptions.ARROW) {
            return this.renderArrow;
        }
        if (o == ModOptions.DISPLAY_ZOOM) {
            return this.displayZoom;
        }
        if (o == ModOptions.OPEN_ANIMATION) {
            return this.openMapAnimation;
        }
        if (o == ModOptions.RELOAD) {
            return this.reloadEverything;
        }
        if (o == ModOptions.ZOOM_BUTTONS) {
            return this.zoomButtons;
        }
        if (o == ModOptions.WAYPOINT_BACKGROUNDS) {
            return this.waypointBackgrounds;
        }
        if (o == ModOptions.DETECT_AMBIGUOUS_Y) {
            return this.detectAmbiguousY;
        }
        if (o == ModOptions.PAUSE_REQUESTS) {
            return this.pauseRequests;
        }
        if (o == ModOptions.EXTRA_DEBUG) {
            return this.extraDebug;
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
        if (o == ModOptions.DISPLAY_STAINED_GLASS) {
            return this.displayStainedGlass;
        }
        if (o == ModOptions.MAP_TELEPORT_ALLOWED) {
            return WorldMapSession.getCurrentSession().getMapProcessor().getMapWorld().isTeleportAllowed();
        }
        if (o == ModOptions.LEGIBLE_CAVE_MAPS) {
            return this.legibleCaveMaps;
        }
        if (o == ModOptions.DISPLAY_CAVE_MODE_START) {
            return this.displayCaveModeStart;
        }
        if (o == ModOptions.BIOME_BLENDING) {
            return this.biomeBlending;
        }
        if (o == ModOptions.FULL_EXPORT) {
            return class_310.method_1551().field_1755 instanceof ExportScreen ? ((ExportScreen)class_310.method_1551().field_1755).fullExport : false;
        }
        if (o == ModOptions.MULTIPLE_IMAGES_EXPORT) {
            return this.multipleImagesExport;
        }
        if (o == ModOptions.NIGHT_EXPORT) {
            return this.nightExport;
        }
        if (o == ModOptions.EXPORT_HIGHLIGHTS) {
            return this.highlightsExport;
        }
        if (o == ModOptions.FULL_RELOAD || o == ModOptions.FULL_RESAVE) {
            MapDimension mapDimension = WorldMapSession.getCurrentSession().getMapProcessor().getMapWorld().getCurrentDimension();
            MapFullReloader reloader = mapDimension == null ? null : mapDimension.getFullReloader();
            return reloader != null && (o == ModOptions.FULL_RELOAD || reloader.isResave());
        }
        if (o == ModOptions.PAC_CLAIMS) {
            return this.displayClaims;
        }
        return false;
    }

    private static String getTranslation(boolean o) {
        return class_1074.method_4662((String)("gui.xaero_" + (o ? "on" : "off")), (Object[])new Object[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOptionValue(ModOptions par1EnumOptions, Object value) {
        MapWorld mapWorld;
        WorldMapSession worldmapSession;
        if (par1EnumOptions.isDisabledBecauseNotIngame() || par1EnumOptions.isDisabledBecauseMinimap() || par1EnumOptions.isDisabledBecausePac()) {
            return;
        }
        if (par1EnumOptions == ModOptions.DEBUG) {
            this.debug = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.COLOURS) {
            this.colours = (Integer)value;
        }
        if (par1EnumOptions == ModOptions.LIGHTING) {
            this.lighting = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.LOAD) {
            this.loadChunks = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.UPDATE) {
            this.updateChunks = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.DEPTH) {
            this.terrainDepth = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.SLOPES) {
            this.terrainSlopes = (Integer)value;
        }
        if (par1EnumOptions == ModOptions.STEPS) {
            this.footsteps = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.FLOWERS) {
            WorldMapSession session;
            this.flowers = (Boolean)value;
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 != null && mc.field_1724 != null && (session = WorldMapSession.getCurrentSession()) != null) {
                session.getMapProcessor().getMapWriter().setDirtyInWriteDistance((class_1657)mc.field_1724, (class_1937)mc.field_1687);
            }
        }
        if (par1EnumOptions == ModOptions.COORDINATES) {
            this.coordinates = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.HOVERED_BIOME) {
            boolean bl = this.hoveredBiome = !this.hoveredBiome;
        }
        if (par1EnumOptions == ModOptions.BIOMES) {
            this.biomeColorsVanillaMode = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.WAYPOINTS) {
            this.waypoints = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.ARROW) {
            this.renderArrow = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.DISPLAY_ZOOM) {
            this.displayZoom = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.IGNORE_HEIGHTMAPS) {
            worldmapSession = WorldMapSession.getCurrentSession();
            mapWorld = worldmapSession.getMapProcessor().getMapWorld();
            mapWorld.setIgnoreHeightmaps((Boolean)value);
            mapWorld.saveConfig();
        }
        if (par1EnumOptions == ModOptions.OPEN_ANIMATION) {
            this.openMapAnimation = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.RELOAD) {
            this.reloadEverything = (Boolean)value;
            if (this.reloadEverything) {
                ++this.reloadVersion;
            }
        }
        if (par1EnumOptions == ModOptions.ZOOM_BUTTONS) {
            this.zoomButtons = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.WAYPOINT_BACKGROUNDS) {
            this.waypointBackgrounds = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.DETECT_AMBIGUOUS_Y) {
            this.detectAmbiguousY = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.PAUSE_REQUESTS) {
            this.pauseRequests = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.EXTRA_DEBUG) {
            this.extraDebug = (Boolean)value;
        }
        if (par1EnumOptions == ModOptions.UPDATE_NOTIFICATION) {
            boolean bl = updateNotification = !updateNotification;
        }
        if (par1EnumOptions == ModOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS) {
            this.adjustHeightForCarpetLikeBlocks = !this.adjustHeightForCarpetLikeBlocks;
        } else if (par1EnumOptions == ModOptions.ARROW_COLOUR) {
            this.arrowColour = -2 + (Integer)value;
        } else {
            if (par1EnumOptions == ModOptions.MAP_TELEPORT_ALLOWED) {
                worldmapSession = WorldMapSession.getCurrentSession();
                mapWorld.setTeleportAllowed(!(mapWorld = worldmapSession.getMapProcessor().getMapWorld()).isTeleportAllowed());
                mapWorld.saveConfig();
                return;
            }
            if (par1EnumOptions == ModOptions.PARTIAL_Y_TELEPORTATION) {
                this.partialYTeleportation = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.DISPLAY_STAINED_GLASS) {
                this.displayStainedGlass = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.LEGIBLE_CAVE_MAPS) {
                this.legibleCaveMaps = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.AUTO_CAVE_MODE) {
                this.autoCaveMode = -1 + (Integer)value;
            } else if (par1EnumOptions == ModOptions.DISPLAY_CAVE_MODE_START) {
                this.displayCaveModeStart = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.DEFAULT_CAVE_MODE_TYPE) {
                this.defaultCaveModeType = (Integer)value;
            } else if (par1EnumOptions == ModOptions.BIOME_BLENDING) {
                this.biomeBlending = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.FULL_EXPORT) {
                if (class_310.method_1551().field_1755 instanceof ExportScreen) {
                    ((ExportScreen)class_310.method_1551().field_1755).fullExport = (Boolean)value;
                }
            } else if (par1EnumOptions == ModOptions.MULTIPLE_IMAGES_EXPORT) {
                this.multipleImagesExport = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.NIGHT_EXPORT) {
                this.nightExport = (Boolean)value;
            } else if (par1EnumOptions == ModOptions.EXPORT_HIGHLIGHTS) {
                this.highlightsExport = (Boolean)value;
            } else {
                if (par1EnumOptions == ModOptions.FULL_RELOAD || par1EnumOptions == ModOptions.FULL_RESAVE) {
                    worldmapSession = WorldMapSession.getCurrentSession();
                    MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                    MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
                    if (mapDimension == null) {
                        return;
                    }
                    if (((Boolean)value).booleanValue() && (mapDimension.getFullReloader() == null || !mapDimension.getFullReloader().isResave() && par1EnumOptions == ModOptions.FULL_RESAVE)) {
                        mapDimension.startFullMapReload(mapProcessor.getCurrentCaveLayer(), par1EnumOptions == ModOptions.FULL_RESAVE, mapProcessor);
                    } else if (!((Boolean)value).booleanValue()) {
                        mapDimension.clearFullMapReload();
                    }
                    if (class_310.method_1551().field_1755 != null) {
                        class_310.method_1551().method_1507(class_310.method_1551().field_1755);
                    }
                    return;
                }
                if (par1EnumOptions == ModOptions.PAC_CLAIMS) {
                    this.displayClaims = (Boolean)value;
                    worldmapSession = WorldMapSession.getCurrentSession();
                    if (worldmapSession != null) {
                        Object object = worldmapSession.getMapProcessor().uiSync;
                        synchronized (object) {
                            worldmapSession.getMapProcessor().getMapWorld().clearAllCachedHighlightHashes();
                        }
                    }
                }
            }
        }
        this.updateRegionCacheHashCode();
        try {
            this.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        if (class_310.method_1551().field_1755 != null) {
            class_310.method_1551().method_1507(class_310.method_1551().field_1755);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOptionDoubleValue(ModOptions options, double f) {
        if (options.isDisabledBecauseNotIngame() || options.isDisabledBecauseMinimap() || options.isDisabledBecausePac()) {
            return;
        }
        if (options == ModOptions.WAYPOINT_SCALE) {
            this.worldmapWaypointsScale = (float)f;
        } else if (options == ModOptions.MIN_ZOOM_LOCAL_WAYPOINTS) {
            this.minZoomForLocalWaypoints = f;
        } else if (options == ModOptions.CAVE_MODE_DEPTH) {
            this.caveModeDepth = (int)f;
        } else if (options == ModOptions.CAVE_MODE_START) {
            int n = this.caveModeStart = f == ModOptions.CAVE_MODE_START.getValueMin() ? Integer.MAX_VALUE : (int)f;
            if (class_310.method_1551().field_1755 instanceof GuiMap) {
                ((GuiMap)class_310.method_1551().field_1755).onCaveModeStartSet();
            }
        } else if (options == ModOptions.CAVE_MODE_TOGGLE_TIMER) {
            this.caveModeToggleTimer = (int)f;
        } else if (options == ModOptions.EXPORT_SCALE_DOWN_SQUARE) {
            this.exportScaleDownSquare = (int)f;
        } else if (options == ModOptions.MAP_WRITING_DISTANCE) {
            this.mapWritingDistance = (int)f;
        } else if (options == ModOptions.PAC_CLAIMS_BORDER_OPACITY) {
            this.claimsOpacity = this.claimsBorderOpacity = (int)f;
            if (this.displayClaims && (worldmapSession = WorldMapSession.getCurrentSession()) != null) {
                Object object = worldmapSession.getMapProcessor().uiSync;
                synchronized (object) {
                    if (worldmapSession.getMapProcessor().getMapWorld().getCurrentDimensionId() != null) {
                        worldmapSession.getMapProcessor().getMapWorld().getCurrentDimension().getHighlightHandler().clearCachedHashes();
                    }
                }
            }
        } else if (options == ModOptions.PAC_CLAIMS_FILL_OPACITY) {
            this.claimsFillOpacity = (int)f;
            if (this.displayClaims && (worldmapSession = WorldMapSession.getCurrentSession()) != null) {
                Object object = worldmapSession.getMapProcessor().uiSync;
                synchronized (object) {
                    if (worldmapSession.getMapProcessor().getMapWorld().getCurrentDimensionId() != null) {
                        worldmapSession.getMapProcessor().getMapWorld().getCurrentDimension().getHighlightHandler().clearCachedHashes();
                    }
                }
            }
        }
        try {
            this.saveSettings();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    public double getOptionDoubleValue(ModOptions options) {
        if (options.isDisabledBecauseNotIngame() || options.isDisabledBecauseMinimap() || options.isDisabledBecausePac()) {
            return 0.0;
        }
        if (options == ModOptions.WAYPOINT_SCALE) {
            return this.worldmapWaypointsScale;
        }
        if (options == ModOptions.MIN_ZOOM_LOCAL_WAYPOINTS) {
            return this.minZoomForLocalWaypoints;
        }
        if (options == ModOptions.CAVE_MODE_DEPTH) {
            return this.caveModeDepth;
        }
        if (options == ModOptions.CAVE_MODE_START) {
            return this.caveModeStart == Integer.MAX_VALUE ? ModOptions.CAVE_MODE_START.getValueMin() : (double)this.caveModeStart;
        }
        if (options == ModOptions.CAVE_MODE_TOGGLE_TIMER) {
            return this.caveModeToggleTimer;
        }
        if (options == ModOptions.EXPORT_SCALE_DOWN_SQUARE) {
            return this.exportScaleDownSquare;
        }
        if (options == ModOptions.MAP_WRITING_DISTANCE) {
            return this.mapWritingDistance;
        }
        if (options == ModOptions.PAC_CLAIMS_BORDER_OPACITY) {
            return this.claimsBorderOpacity;
        }
        if (options == ModOptions.PAC_CLAIMS_FILL_OPACITY) {
            return this.claimsFillOpacity;
        }
        return 1.0;
    }

    public int getRegionCacheHashCode() {
        return this.regionCacheHashCode;
    }

    public void updateRegionCacheHashCode() {
        int currentRegionCacheHashCode = this.regionCacheHashCode;
        if (!class_310.method_1551().method_18854()) {
            throw new RuntimeException("Wrong thread!");
        }
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(this.colours).append(this.terrainDepth).append(this.terrainSlopes).append(false).append(this.colours == 1 && this.biomeColorsVanillaMode).append(this.getClientBooleanValue(ModOptions.IGNORE_HEIGHTMAPS)).append(this.adjustHeightForCarpetLikeBlocks).append(this.displayStainedGlass).append(this.legibleCaveMaps).append(this.biomeBlending);
        Collection enabledResourcePacks = class_310.method_1551().method_1520().method_14444();
        for (class_3288 resourcePack : enabledResourcePacks) {
            hcb.append((Object)resourcePack.method_14463());
        }
        this.regionCacheHashCode = hcb.toHashCode();
        if (currentRegionCacheHashCode != this.regionCacheHashCode) {
            WorldMap.LOGGER.info("New world map region cache hash code: " + this.regionCacheHashCode);
        }
    }

    public Object getOptionValue(ModOptions par1EnumOptions) {
        if (par1EnumOptions.enumBoolean) {
            return this.getClientBooleanValue(par1EnumOptions);
        }
        if (par1EnumOptions.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return 0;
        }
        if (par1EnumOptions == ModOptions.COLOURS) {
            return this.colours;
        }
        if (par1EnumOptions == ModOptions.SLOPES) {
            return this.terrainSlopes;
        }
        if (par1EnumOptions == ModOptions.ARROW_COLOUR) {
            return 2 + this.arrowColour;
        }
        if (par1EnumOptions == ModOptions.AUTO_CAVE_MODE) {
            return 1 + this.autoCaveMode;
        }
        if (par1EnumOptions == ModOptions.DEFAULT_CAVE_MODE_TYPE) {
            return this.defaultCaveModeType;
        }
        return false;
    }

    public String getSliderOptionText(ModOptions par1EnumOptions) {
        Object s = par1EnumOptions.getEnumString() + ": ";
        if (par1EnumOptions == ModOptions.CAVE_MODE_DEPTH) {
            s = (String)s + this.caveModeDepth;
        } else if (par1EnumOptions == ModOptions.CAVE_MODE_TOGGLE_TIMER) {
            s = (String)s + this.caveModeToggleTimer + " s";
        } else if (par1EnumOptions == ModOptions.EXPORT_SCALE_DOWN_SQUARE) {
            s = (String)s + (this.exportScaleDownSquare <= 0 ? class_1074.method_4662((String)"gui.xaero_export_option_scale_down_square_unscaled", (Object[])new Object[0]) : class_1074.method_4662((String)"gui.xaero_export_option_scale_down_square_value", (Object[])new Object[]{this.exportScaleDownSquare}));
        } else if (par1EnumOptions == ModOptions.MAP_WRITING_DISTANCE) {
            s = (String)s + (String)(this.mapWritingDistance < 0 ? class_1074.method_4662((String)"gui.xaero_map_writing_distance_unlimited", (Object[])new Object[0]) : "" + this.mapWritingDistance);
        } else if (par1EnumOptions == ModOptions.CAVE_MODE_START) {
            s = par1EnumOptions.getEnumString();
        } else {
            return this.getEnumFloatSliderText((String)s, "%.2f", par1EnumOptions);
        }
        return s;
    }

    protected String getEnumFloatSliderText(String s, String f, ModOptions par1EnumOptions) {
        String f1 = String.format(f, this.getOptionDoubleValue(par1EnumOptions));
        return s + f1;
    }

    public static boolean canEditIngameSettings() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        return worldmapSession != null && worldmapSession.getMapProcessor().getMapWorld() != null;
    }

    public void findMapItem() {
        mapItem = mapItemId != null ? (class_1792)class_7923.field_41178.method_63535(class_2960.method_60654((String)mapItemId)) : null;
    }

    static {
        updateNotification = true;
        mapItemId = null;
        mapItem = null;
    }
}

