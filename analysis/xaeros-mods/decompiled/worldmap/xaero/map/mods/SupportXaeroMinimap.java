/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_1291
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2378
 *  net.minecraft.class_2874
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 *  xaero.common.HudMod
 *  xaero.common.IXaeroMinimap
 *  xaero.common.effect.Effects
 *  xaero.common.graphics.shader.CustomUniforms
 *  xaero.common.gui.GuiAddWaypoint
 *  xaero.common.gui.GuiWaypoints
 *  xaero.common.gui.GuiWorldTpCommand
 *  xaero.common.minimap.highlight.DimensionHighlighterHandler
 *  xaero.common.minimap.waypoints.Waypoint
 *  xaero.common.misc.Misc
 *  xaero.common.mods.SupportXaeroWorldmap
 *  xaero.common.settings.ModSettings
 *  xaero.hud.controls.key.KeyMappingController
 *  xaero.hud.controls.key.function.KeyMappingFunction
 *  xaero.hud.minimap.BuiltInHudModules
 *  xaero.hud.minimap.controls.key.MinimapKeyMappings
 *  xaero.hud.minimap.module.MinimapSession
 *  xaero.hud.minimap.radar.render.element.RadarRenderer
 *  xaero.hud.minimap.waypoint.WaypointSession
 *  xaero.hud.minimap.waypoint.WaypointTeleport
 *  xaero.hud.minimap.waypoint.set.WaypointSet
 *  xaero.hud.minimap.world.MinimapDimensionHelper
 *  xaero.hud.minimap.world.MinimapWorld
 *  xaero.hud.minimap.world.MinimapWorldManager
 *  xaero.hud.minimap.world.container.MinimapWorldRootContainer
 *  xaero.hud.minimap.world.state.MinimapWorldState
 *  xaero.hud.minimap.world.state.MinimapWorldStateUpdater
 *  xaero.hud.path.XaeroPath
 */
package xaero.map.mods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.class_1074;
import net.minecraft.class_1291;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2378;
import net.minecraft.class_2874;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.effect.Effects;
import xaero.common.graphics.shader.CustomUniforms;
import xaero.common.gui.GuiAddWaypoint;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.GuiWorldTpCommand;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.common.misc.Misc;
import xaero.common.mods.SupportXaeroWorldmap;
import xaero.common.settings.ModSettings;
import xaero.hud.controls.key.KeyMappingController;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.render.element.RadarRenderer;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.waypoint.WaypointTeleport;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapDimensionHelper;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.minimap.world.state.MinimapWorldState;
import xaero.hud.minimap.world.state.MinimapWorldStateUpdater;
import xaero.hud.path.XaeroPath;
import xaero.map.WorldMap;
import xaero.map.element.HoveredMapElementHolder;
import xaero.map.element.MapElementGraphics;
import xaero.map.graphics.shader.BuiltInCustomUniforms;
import xaero.map.gui.GuiMap;
import xaero.map.misc.KeySortableByOther;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointMenuRenderContext;
import xaero.map.mods.gui.WaypointMenuRenderProvider;
import xaero.map.mods.gui.WaypointMenuRenderer;
import xaero.map.mods.gui.WaypointRenderer;
import xaero.map.mods.minimap.element.MinimapElementGraphicsWrapper;
import xaero.map.mods.minimap.element.RadarRendererWrapperHelper;
import xaero.map.mods.minimap.shader.CustomUniformWrapper;
import xaero.map.mods.minimap.tracker.system.MinimapSyncedPlayerTrackerSystem;
import xaero.map.radar.tracker.system.IPlayerTrackerSystem;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class SupportXaeroMinimap {
    HudMod modMain;
    public int compatibilityVersion;
    private boolean deathpoints = true;
    private boolean refreshWaypoints = true;
    private MinimapWorld waypointWorld;
    private MinimapWorld mapWaypointWorld;
    private class_5321<class_1937> mapDimId;
    private double dimDiv;
    private WaypointSet waypointSet;
    private boolean allSets;
    private ArrayList<Waypoint> waypoints;
    private ArrayList<Waypoint> waypointsSorted;
    private WaypointMenuRenderer waypointMenuRenderer;
    private final WaypointRenderer waypointRenderer;
    private IPlayerTrackerSystem<?> minimapSyncedPlayerTrackerSystem;
    private MinimapWorld mouseBlockWaypointWorld;
    private MinimapWorld rightClickWaypointWorld;
    private MinimapElementGraphicsWrapper elementGraphicsWrapper;

    public SupportXaeroMinimap() {
        try {
            Class<?> mmClassTest = Class.forName("xaero.pvp.BetterPVP");
            this.modMain = HudMod.INSTANCE;
            WorldMap.LOGGER.info("Xaero's WorldMap Mod: Better PVP found!");
        }
        catch (ClassNotFoundException e) {
            try {
                Class<?> mmClassTest = Class.forName("xaero.minimap.XaeroMinimap");
                this.modMain = HudMod.INSTANCE;
                WorldMap.LOGGER.info("Xaero's WorldMap Mod: Xaero's minimap found!");
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        if (this.modMain != null) {
            try {
                this.compatibilityVersion = SupportXaeroWorldmap.WORLDMAP_COMPATIBILITY_VERSION;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            if (this.compatibilityVersion < 3) {
                throw new RuntimeException("Xaero's Minimap 20.23.0 or newer required!");
            }
            this.elementGraphicsWrapper = new MinimapElementGraphicsWrapper();
        }
        this.waypointRenderer = WaypointRenderer.Builder.begin().setMinimap(this).setSymbolCreator(WorldMap.waypointSymbolCreator).build();
    }

    public void register() {
        WorldMap.playerTrackerSystemManager.register("minimap_synced", this.getMinimapSyncedPlayerTrackerSystem());
        this.registerShaderUniforms();
    }

    public ArrayList<Waypoint> convertWaypoints(double dimDiv) {
        if (this.waypointSet == null) {
            return null;
        }
        ArrayList<Waypoint> result = new ArrayList<Waypoint>();
        if (!this.allSets) {
            this.convertSet(this.waypointSet, result, dimDiv);
        } else {
            for (WaypointSet set : this.waypointWorld.getIterableWaypointSets()) {
                this.convertSet(set, result, dimDiv);
            }
        }
        this.deathpoints = this.modMain.getSettings().getDeathpoints();
        return result;
    }

    private void convertSet(WaypointSet set, ArrayList<Waypoint> result, double dimDiv) {
        String setName = set.getName();
        boolean showingDisabled = WorldMap.settings.showDisabledWaypoints;
        for (xaero.common.minimap.waypoints.Waypoint w : set.getWaypoints()) {
            if (!showingDisabled && w.isDisabled()) continue;
            result.add(this.convertWaypoint(w, true, setName, dimDiv));
        }
    }

    public Waypoint convertWaypoint(xaero.common.minimap.waypoints.Waypoint w, boolean editable, String setName, double dimDiv) {
        int waypointType = w.getWaypointType();
        Waypoint converted = new Waypoint(w, w.getX(), w.getY(), w.getZ(), w.getName(), w.getSymbol(), ModSettings.COLORS[w.getColor()], waypointType, editable, setName, w.isYIncluded(), dimDiv);
        converted.setDisabled(w.isDisabled());
        converted.setYaw(w.getYaw());
        converted.setRotation(w.isRotation());
        converted.setTemporary(w.isTemporary());
        converted.setGlobal(w.isGlobal());
        return converted;
    }

    public void openWaypoint(GuiMap parent, Waypoint waypoint) {
        if (!waypoint.isEditable()) {
            return;
        }
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        ArrayList<xaero.common.minimap.waypoints.Waypoint> waypointsEdited = new ArrayList<xaero.common.minimap.waypoints.Waypoint>();
        waypointsEdited.add((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal());
        GuiAddWaypoint addScreen = new GuiAddWaypoint(this.modMain, minimapSession, (class_437)parent, (class_437)parent, waypointsEdited, this.waypointWorld.getContainer().getRoot().getPath(), this.waypointWorld, waypoint.getSetName(), false);
        class_310.method_1551().method_1507((class_437)addScreen);
    }

    public void createWaypoint(GuiMap parent, int x, int y, int z, double coordDimensionScale, boolean rightClick) {
        if (this.waypointWorld == null) {
            return;
        }
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorld coordSourceWaypointWorld = rightClick ? this.rightClickWaypointWorld : this.mouseBlockWaypointWorld;
        GuiAddWaypoint addScreen = new GuiAddWaypoint(this.modMain, minimapSession, (class_437)parent, (class_437)parent, new ArrayList(), this.waypointWorld.getContainer().getRoot().getPath(), this.waypointWorld, this.waypointWorld.getCurrentWaypointSetId(), true, true, x, y, z, coordDimensionScale, coordSourceWaypointWorld);
        class_310.method_1551().method_1507((class_437)addScreen);
    }

    public void createTempWaypoint(int x, int y, int z, double mapDimensionScale, boolean rightClick) {
        if (this.waypointWorld == null) {
            return;
        }
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorld coordSourceWaypointWorld = rightClick ? this.rightClickWaypointWorld : this.mouseBlockWaypointWorld;
        minimapSession.getWaypointSession().getTemporaryHandler().createTemporaryWaypoint(this.waypointWorld, x, y, z, y != Short.MAX_VALUE && coordSourceWaypointWorld == this.waypointWorld, mapDimensionScale);
        this.requestWaypointsRefresh();
    }

    public boolean canTeleport(MinimapWorld world) {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        WaypointSession waypointSession = minimapSession.getWaypointSession();
        WaypointTeleport waypointTeleport = waypointSession.getTeleport();
        return world != null && waypointTeleport.canTeleport(waypointTeleport.isWorldTeleportable(world), world);
    }

    public void teleportToWaypoint(class_437 screen, Waypoint w) {
        this.teleportToWaypoint(screen, w, this.waypointWorld);
    }

    public void teleportToWaypoint(class_437 screen, Waypoint w, MinimapWorld world) {
        if (world == null) {
            return;
        }
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        WaypointSession waypointSession = minimapSession.getWaypointSession();
        WaypointTeleport waypointTeleport = waypointSession.getTeleport();
        waypointTeleport.teleportToWaypoint((xaero.common.minimap.waypoints.Waypoint)w.getOriginal(), world, screen);
    }

    public void disableWaypoint(Waypoint waypoint) {
        ((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).setDisabled(!((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isDisabled());
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        try {
            minimapSession.getWorldManagerIO().saveWorld(this.waypointWorld);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        waypoint.setDisabled(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isDisabled());
        waypoint.setTemporary(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isTemporary());
    }

    public void deleteWaypoint(Waypoint waypoint) {
        if (!this.allSets) {
            this.waypointSet.remove((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal());
        } else {
            for (WaypointSet set : this.waypointWorld.getIterableWaypointSets()) {
                set.remove((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal());
            }
        }
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        try {
            minimapSession.getWorldManagerIO().saveWorld(this.waypointWorld);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.waypoints.remove(waypoint);
        this.waypointsSorted.remove(waypoint);
        this.waypointMenuRenderer.updateFilteredList();
    }

    public void checkWaypoints(boolean multiplayer, class_5321<class_1937> dimId, String multiworldId, int width, int height, GuiMap screen, MapWorld mapWorld, class_2378<class_2874> dimensionTypes) {
        WaypointSet checkingSet;
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorldManager worldManager = minimapSession.getWorldManager();
        MinimapWorldState worldState = minimapSession.getWorldState();
        MinimapWorldStateUpdater worldStateUpdater = minimapSession.getWorldStateUpdater();
        MinimapDimensionHelper dimensionHelper = minimapSession.getDimensionHelper();
        XaeroPath containerPath = worldState.getAutoRootContainerPath().resolve(dimensionHelper.getDimensionDirectoryName(dimId));
        XaeroPath mapBasedWorldPath = containerPath.resolve(!multiplayer ? "waypoints" : multiworldId);
        this.mapWaypointWorld = worldManager.getWorld(mapBasedWorldPath);
        MinimapWorld checkingWaypointWorld = WorldMap.settings.onlyCurrentMapWaypoints ? this.mapWaypointWorld : worldManager.getCurrentWorld();
        class_310 mc = class_310.method_1551();
        if (xaero.map.misc.Misc.hasEffect((class_1657)mc.field_1724, (class_6880<class_1291>)Effects.NO_WAYPOINTS) || xaero.map.misc.Misc.hasEffect((class_1657)mc.field_1724, (class_6880<class_1291>)Effects.NO_WAYPOINTS_HARMFUL)) {
            checkingWaypointWorld = null;
        }
        boolean shouldRefresh = this.refreshWaypoints;
        if (dimId != this.mapDimId) {
            shouldRefresh = true;
            this.mapDimId = dimId;
        }
        if (checkingWaypointWorld != this.waypointWorld) {
            this.waypointWorld = checkingWaypointWorld;
            screen.closeRightClick();
            if (screen.waypointMenu) {
                screen.method_25423(class_310.method_1551(), width, height);
            }
            shouldRefresh = true;
        }
        WaypointSet waypointSet = checkingSet = checkingWaypointWorld == null ? null : checkingWaypointWorld.getCurrentWaypointSet();
        if (checkingSet != this.waypointSet) {
            this.waypointSet = checkingSet;
            shouldRefresh = true;
        }
        if (this.allSets != this.modMain.getSettings().renderAllSets) {
            this.allSets = this.modMain.getSettings().renderAllSets;
            shouldRefresh = true;
        }
        if (shouldRefresh) {
            this.dimDiv = this.waypointWorld == null ? 1.0 : this.getDimensionDivision(mapWorld, dimensionTypes, dimensionHelper, this.waypointWorld.getContainer().getPath(), dimId);
            this.waypoints = this.convertWaypoints(this.dimDiv);
            if (this.waypoints != null) {
                Collections.sort(this.waypoints);
                this.waypointsSorted = new ArrayList();
                ArrayList<KeySortableByOther<Waypoint>> sortingList = new ArrayList<KeySortableByOther<Waypoint>>();
                for (Waypoint waypoint : this.waypoints) {
                    sortingList.add(new KeySortableByOther<Waypoint>(waypoint, new Comparable[]{waypoint.getComparisonName(), waypoint.getName()}));
                }
                Collections.sort(sortingList);
                for (KeySortableByOther keySortableByOther : sortingList) {
                    this.waypointsSorted.add((Waypoint)keySortableByOther.getKey());
                }
            } else {
                this.waypointsSorted = null;
            }
            this.waypointMenuRenderer.updateFilteredList();
        }
        this.refreshWaypoints = false;
    }

    private double getDimensionDivision(MapWorld mapWorld, class_2378<class_2874> dimensionTypes, MinimapDimensionHelper dimensionHelper, XaeroPath worldContainerID, class_5321<class_1937> mapDimId) {
        if (worldContainerID == null || class_310.method_1551().field_1687 == null) {
            return 1.0;
        }
        String dimPart = worldContainerID.getLastNode();
        class_5321 waypointDimId = dimensionHelper.getDimensionKeyForDirectoryName(dimPart);
        MapDimension waypointMapDimension = mapWorld.getDimension((class_5321<class_1937>)waypointDimId);
        MapDimension mapDimension = mapWorld.getDimension(mapDimId);
        class_2874 waypointDimType = MapDimension.getDimensionType(waypointMapDimension, (class_5321<class_1937>)waypointDimId, dimensionTypes);
        class_2874 mapDimType = MapDimension.getDimensionType(mapDimension, mapDimId, dimensionTypes);
        double waypointDimScale = waypointDimType == null ? 1.0 : waypointDimType.comp_646();
        double mapDimScale = mapDimType == null ? 1.0 : mapDimType.comp_646();
        return mapDimScale / waypointDimScale;
    }

    public HoveredMapElementHolder<?, ?> renderWaypointsMenu(class_332 guiGraphics, GuiMap gui, double scale, int width, int height, int mouseX, int mouseY, boolean leftMousePressed, boolean leftMouseClicked, HoveredMapElementHolder<?, ?> hovered, class_310 mc) {
        return this.waypointMenuRenderer.renderMenu(guiGraphics, gui, scale, width, height, mouseX, mouseY, leftMousePressed, leftMouseClicked, hovered, mc);
    }

    public void requestWaypointsRefresh() {
        this.refreshWaypoints = true;
    }

    public class_304 getWaypointKeyBinding() {
        return MinimapKeyMappings.ADD_WAYPOINT;
    }

    public class_304 getTempWaypointKeyBinding() {
        return MinimapKeyMappings.TEMPORARY_WAYPOINT;
    }

    public class_304 getTempWaypointsMenuKeyBinding() {
        return MinimapKeyMappings.WAYPOINT_MENU;
    }

    public void onMapKeyPressed(class_3675.class_307 type, int code, GuiMap screen) {
        class_304 listPlayerAlternative;
        class_304 minimapSettingsKB;
        class_304 kb = null;
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, this.getToggleRadarKey(), 0)) {
            screen.onRadarButton(screen.getRadarButton());
        }
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, MinimapKeyMappings.TOGGLE_MAP_WAYPOINTS, 0)) {
            this.getWaypointMenuRenderer().onRenderWaypointsButton(screen, screen.field_22789, screen.field_22790);
        }
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, MinimapKeyMappings.REVERSE_ENTITY_RADAR, 0)) {
            MinimapKeyMappings.REVERSE_ENTITY_RADAR.method_23481(true);
        }
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, MinimapKeyMappings.SWITCH_WAYPOINT_SET, 0)) {
            kb = MinimapKeyMappings.SWITCH_WAYPOINT_SET;
        }
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, MinimapKeyMappings.RENDER_ALL_SETS, 0)) {
            kb = MinimapKeyMappings.RENDER_ALL_SETS;
        }
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, MinimapKeyMappings.WAYPOINT_MENU, 0)) {
            kb = MinimapKeyMappings.WAYPOINT_MENU;
        }
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, minimapSettingsKB = (class_304)this.modMain.getSettingsKey(), 0)) {
            kb = minimapSettingsKB;
        }
        if ((listPlayerAlternative = this.getMinimapListPlayersAlternative()) != null && xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, listPlayerAlternative, 0)) {
            listPlayerAlternative.method_23481(true);
        }
        class_310 mc = class_310.method_1551();
        if (kb != null) {
            if (kb == MinimapKeyMappings.WAYPOINT_MENU) {
                this.openWaypointsMenu(mc, screen);
                return;
            }
            if (minimapSettingsKB != null && kb == minimapSettingsKB) {
                this.openSettingsScreen(mc, screen, screen);
                return;
            }
            this.handleMinimapKeyBinding(kb, screen);
        }
    }

    public boolean onMapKeyReleased(class_3675.class_307 type, int code, GuiMap screen) {
        class_304 listPlayerAlternative;
        boolean result = false;
        if (xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, MinimapKeyMappings.REVERSE_ENTITY_RADAR, 0)) {
            MinimapKeyMappings.REVERSE_ENTITY_RADAR.method_23481(false);
            result = true;
        }
        if ((listPlayerAlternative = this.getMinimapListPlayersAlternative()) != null && xaero.map.misc.Misc.inputMatchesKeyBinding(type, code, listPlayerAlternative, 0)) {
            listPlayerAlternative.method_23481(false);
            result = true;
        }
        return result;
    }

    public void handleMinimapKeyBinding(class_304 kb, GuiMap screen) {
        KeyMappingController controller = this.modMain.getKeyMappingControllers().getController(kb);
        for (KeyMappingFunction keyFunction : controller) {
            if (keyFunction.isHeld()) continue;
            keyFunction.onPress();
        }
        for (KeyMappingFunction keyFunction : controller) {
            if (keyFunction.isHeld()) continue;
            keyFunction.onRelease();
        }
        if ((kb == MinimapKeyMappings.SWITCH_WAYPOINT_SET || kb == MinimapKeyMappings.RENDER_ALL_SETS) && screen.waypointMenu) {
            screen.method_25423(class_310.method_1551(), screen.field_22789, screen.field_22790);
        }
    }

    public void drawSetChange(class_332 guiGraphics) {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        this.modMain.getMinimap().getWaypointMapRenderer().drawSetChange(minimapSession, guiGraphics, class_310.method_1551().method_22683());
    }

    public class_437 openSettingsScreen(class_310 mc, class_437 current, class_437 escape) {
        this.modMain.getGuiHelper().openMinimapSettingsFromScreen(current, escape);
        return class_310.method_1551().field_1755;
    }

    public String getControlsTooltip() {
        return class_1074.method_4662((String)"gui.xaero_box_controls_minimap", (Object[])new Object[]{xaero.map.misc.Misc.getKeyName(MinimapKeyMappings.ADD_WAYPOINT), xaero.map.misc.Misc.getKeyName(MinimapKeyMappings.TEMPORARY_WAYPOINT), xaero.map.misc.Misc.getKeyName(MinimapKeyMappings.SWITCH_WAYPOINT_SET), xaero.map.misc.Misc.getKeyName(MinimapKeyMappings.RENDER_ALL_SETS), xaero.map.misc.Misc.getKeyName(MinimapKeyMappings.WAYPOINT_MENU)});
    }

    public void onMapMouseRelease(double par1, double par2, int par3) {
        this.waypointMenuRenderer.onMapMouseRelease(par1, par2, par3);
    }

    public void onMapConstruct() {
        this.waypointMenuRenderer = new WaypointMenuRenderer(new WaypointMenuRenderContext(), new WaypointMenuRenderProvider(this), this.waypointRenderer);
    }

    public void onMapInit(GuiMap mapScreen, class_310 mc, int width, int height) {
        this.waypointMenuRenderer.onMapInit(mapScreen, mc, width, height, this.waypointWorld, (IXaeroMinimap)this.modMain, (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession());
    }

    public ArrayList<Waypoint> getWaypointsSorted() {
        return this.waypointsSorted;
    }

    public boolean waypointExists(Waypoint w) {
        return this.waypoints != null && this.waypoints.contains(w);
    }

    public void toggleTemporaryWaypoint(Waypoint waypoint) {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        ((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).setTemporary(!((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isTemporary());
        try {
            minimapSession.getWorldManagerIO().saveWorld(this.waypointWorld);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        waypoint.setDisabled(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isDisabled());
        waypoint.setTemporary(((xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal()).isTemporary());
    }

    public void openWaypointsMenu(class_310 mc, GuiMap screen) {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        mc.method_1507((class_437)new GuiWaypoints(this.modMain, minimapSession, (class_437)screen, (class_437)screen));
    }

    public boolean screenShouldSkipWorldRender(class_437 currentScreen) {
        return Misc.screenShouldSkipWorldRender((IXaeroMinimap)this.modMain, (class_437)currentScreen, (boolean)false);
    }

    public boolean hidingWaypointCoordinates() {
        return this.modMain.getSettings().hideWaypointCoordinates;
    }

    public void shareWaypoint(Waypoint waypoint, GuiMap screen, MinimapWorld world) {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        minimapSession.getWaypointSession().getSharing().shareWaypoint((class_437)screen, (xaero.common.minimap.waypoints.Waypoint)waypoint.getOriginal(), world);
    }

    public void shareLocation(GuiMap guiMap, int rightClickX, int rightClickY, int rightClickZ) {
        int wpColor = (int)((double)ModSettings.COLORS.length * Math.random());
        xaero.common.minimap.waypoints.Waypoint minimapLocationWaypoint = new xaero.common.minimap.waypoints.Waypoint(rightClickX, rightClickY == Short.MAX_VALUE ? 0 : rightClickY, rightClickZ, "Shared Location", "S", wpColor, 0, false, rightClickY != Short.MAX_VALUE);
        Waypoint locationWaypoint = this.convertWaypoint(minimapLocationWaypoint, false, "", 1.0);
        this.shareWaypoint(locationWaypoint, guiMap, this.rightClickWaypointWorld);
    }

    public MinimapWorld getMapWaypointWorld() {
        return this.mapWaypointWorld;
    }

    public MinimapWorld getWaypointWorld() {
        return this.waypointWorld;
    }

    public double getDimDiv() {
        return this.dimDiv;
    }

    public int getArrowColorIndex() {
        return this.modMain.getSettings().arrowColour;
    }

    public float[] getArrowColor() {
        block3: {
            block2: {
                if (this.modMain.getSettings().arrowColour < 0) break block2;
                int n = this.modMain.getSettings().arrowColour;
                this.modMain.getSettings();
                if (n < ModSettings.arrowColours.length) break block3;
            }
            return null;
        }
        this.modMain.getSettings();
        return ModSettings.arrowColours[this.modMain.getSettings().arrowColour];
    }

    public String getSubWorldNameToRender() {
        if (WorldMap.settings.onlyCurrentMapWaypoints || this.waypointWorld == null) {
            return null;
        }
        if (this.waypointWorld != this.mapWaypointWorld) {
            return class_1074.method_4662((String)"gui.xaero_wm_using_custom_subworld", (Object[])new Object[]{this.waypointWorld.getContainer().getSubName()});
        }
        return null;
    }

    public void registerMinimapHighlighters(Object highlighterRegistry) {
    }

    public ArrayList<Waypoint> getWaypoints() {
        return this.waypoints;
    }

    public boolean getDeathpoints() {
        return this.deathpoints;
    }

    public WaypointRenderer getWaypointRenderer() {
        return this.waypointRenderer;
    }

    public WaypointMenuRenderer getWaypointMenuRenderer() {
        return this.waypointMenuRenderer;
    }

    public void onClearHighlightHash(int regionX, int regionZ) {
        DimensionHighlighterHandler highlightHandler;
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null && (highlightHandler = minimapSession.getProcessor().getMinimapWriter().getDimensionHighlightHandler()) != null) {
            highlightHandler.requestRefresh(regionX, regionZ);
        }
    }

    public void createRadarRendererWrapper(Object radarRenderer) {
        new RadarRendererWrapperHelper().createWrapper((IXaeroMinimap)this.modMain, (RadarRenderer)radarRenderer);
    }

    public class_304 getToggleRadarKey() {
        return MinimapKeyMappings.TOGGLE_RADAR;
    }

    public void onClearHighlightHashes() {
        DimensionHighlighterHandler highlightHandler;
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null && (highlightHandler = minimapSession.getProcessor().getMinimapWriter().getDimensionHighlightHandler()) != null) {
            highlightHandler.requestRefresh();
        }
    }

    public class_304 getToggleAllyPlayersKey() {
        return MinimapKeyMappings.TOGGLE_TRACKED_PLAYERS_MAP;
    }

    public class_304 getToggleClaimsKey() {
        return MinimapKeyMappings.TOGGLE_OPAC_CLAIMS;
    }

    public void onSessionFinalized() {
        this.waypointWorld = null;
        this.mapWaypointWorld = null;
    }

    public void openWaypointWorldTeleportCommandScreen(class_437 parent, class_437 escape) {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        XaeroPath containerId = minimapSession.getWorldState().getAutoRootContainerPath();
        MinimapWorldRootContainer container = minimapSession.getWorldManager().getWorldContainerNullable(containerId).getRoot();
        if (container != null) {
            class_310.method_1551().method_1507((class_437)new GuiWorldTpCommand((IXaeroMinimap)this.modMain, parent, escape, container));
        }
    }

    public class_304 getMinimapListPlayersAlternative() {
        return MinimapKeyMappings.ALTERNATIVE_LIST_PLAYERS;
    }

    public int getCaveStart(int defaultWorldMapStart, boolean isMapScreen) {
        if (!this.modMain.getSettings().getMinimap() || this.isFairPlay()) {
            return defaultWorldMapStart;
        }
        if (xaero.map.misc.Misc.hasEffect((class_6880<class_1291>)Effects.NO_CAVE_MAPS) || xaero.map.misc.Misc.hasEffect((class_6880<class_1291>)Effects.NO_CAVE_MAPS_HARMFUL) || this.modMain.getSettings().caveMapsDisabled()) {
            return isMapScreen ? defaultWorldMapStart : Integer.MAX_VALUE;
        }
        int usedCaving = this.getUsedCaving();
        if (usedCaving == Integer.MAX_VALUE) {
            return WorldMap.settings.caveModeStart;
        }
        return usedCaving;
    }

    public int getUsedCaving() {
        MinimapSession minimapSession = (MinimapSession)BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            return minimapSession.getProcessor().getMinimapWriter().getLoadedCaving();
        }
        return Integer.MAX_VALUE;
    }

    public boolean isFairPlay() {
        return this.modMain.isFairPlay();
    }

    public IPlayerTrackerSystem<?> getMinimapSyncedPlayerTrackerSystem() {
        if (this.minimapSyncedPlayerTrackerSystem == null) {
            this.minimapSyncedPlayerTrackerSystem = new MinimapSyncedPlayerTrackerSystem(this);
        }
        return this.minimapSyncedPlayerTrackerSystem;
    }

    public void onBlockHover() {
        this.mouseBlockWaypointWorld = this.mapWaypointWorld;
    }

    public void onRightClick() {
        this.rightClickWaypointWorld = this.mouseBlockWaypointWorld;
    }

    public void registerShaderUniforms() {
        CustomUniforms.register(new CustomUniformWrapper<Float>(BuiltInCustomUniforms.BRIGHTNESS));
        CustomUniforms.register(new CustomUniformWrapper<Integer>(BuiltInCustomUniforms.WITH_LIGHT));
    }

    public MinimapElementGraphicsWrapper wrapElementGraphics(MapElementGraphics graphics) {
        return this.elementGraphicsWrapper.setGraphics(graphics);
    }
}

