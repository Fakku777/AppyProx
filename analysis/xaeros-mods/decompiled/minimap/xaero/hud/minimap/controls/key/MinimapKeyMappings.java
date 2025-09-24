/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_304
 */
package xaero.hud.minimap.controls.key;

import java.util.function.Consumer;
import net.minecraft.class_304;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.minimap.controls.key.function.MinimapKeyMappingFunctions;

public class MinimapKeyMappings {
    public static final String CATEGORY = "Xaero's Minimap";
    public static final class_304 ZOOM_IN = new class_304("gui.xaero_zoom_in", -1, "Xaero's Minimap");
    public static final class_304 ZOOM_OUT = new class_304("gui.xaero_zoom_out", -1, "Xaero's Minimap");
    public static final class_304 ADD_WAYPOINT = new class_304("gui.xaero_new_waypoint", 66, "Xaero's Minimap");
    public static final class_304 WAYPOINT_MENU = new class_304("gui.xaero_waypoints_key", 85, "Xaero's Minimap");
    public static final class_304 ENLARGE_MAP = new class_304("gui.xaero_enlarge_map", 90, "Xaero's Minimap");
    public static final class_304 TOGGLE_MAP = new class_304("gui.xaero_toggle_map", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_WORLD_WAYPOINTS = new class_304("gui.xaero_toggle_waypoints", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_MAP_WAYPOINTS = new class_304("gui.xaero_toggle_map_waypoints", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_SLIME_CHUNKS = new class_304("gui.xaero_toggle_slime", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_GRID = new class_304("gui.xaero_toggle_grid", -1, "Xaero's Minimap");
    public static final class_304 TEMPORARY_WAYPOINT = new class_304("gui.xaero_instant_waypoint", 334, "Xaero's Minimap");
    public static final class_304 SWITCH_WAYPOINT_SET = new class_304("gui.xaero_switch_waypoint_set", -1, "Xaero's Minimap");
    public static final class_304 RENDER_ALL_SETS = new class_304("gui.xaero_display_all_sets", -1, "Xaero's Minimap");
    public static final class_304 LIGHT_OVERLAY = new class_304("gui.xaero_toggle_light_overlay", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_RADAR = new class_304("gui.xaero_toggle_entity_radar", -1, "Xaero's Minimap");
    public static final class_304 REVERSE_ENTITY_RADAR = new class_304("gui.xaero_reverse_entity_radar", -1, "Xaero's Minimap");
    public static final class_304 MANUAL_CAVE_MODE = new class_304("gui.xaero_toggle_manual_cave_mode", -1, "Xaero's Minimap");
    public static final class_304 ALTERNATIVE_LIST_PLAYERS = new class_304("gui.xaero_alternative_list_players", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_TRACKED_PLAYERS_MAP = new class_304("gui.xaero_toggle_tracked_players_on_map", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_TRACKED_PLAYERS_WORLD = new class_304("gui.xaero_toggle_tracked_players_in_world", -1, "Xaero's Minimap");
    public static final class_304 TOGGLE_OPAC_CLAIMS = new class_304("gui.xaero_toggle_pac_chunk_claims", -1, "Xaero's Minimap");

    public static void registerAll(KeyMappingControllerManager controllerManager, Consumer<class_304> registry) {
        controllerManager.registerController(ZOOM_IN, true, registry);
        controllerManager.registerController(ZOOM_OUT, true, registry);
        controllerManager.registerController(ADD_WAYPOINT, true, registry);
        controllerManager.registerController(WAYPOINT_MENU, true, registry);
        controllerManager.registerController(ENLARGE_MAP, true, registry);
        controllerManager.registerController(TOGGLE_MAP, true, registry);
        controllerManager.registerController(TOGGLE_WORLD_WAYPOINTS, true, registry);
        controllerManager.registerController(TOGGLE_MAP_WAYPOINTS, true, registry);
        controllerManager.registerController(TOGGLE_SLIME_CHUNKS, true, registry);
        controllerManager.registerController(TOGGLE_GRID, true, registry);
        controllerManager.registerController(TEMPORARY_WAYPOINT, true, registry);
        controllerManager.registerController(SWITCH_WAYPOINT_SET, true, registry);
        controllerManager.registerController(RENDER_ALL_SETS, true, registry);
        controllerManager.registerController(LIGHT_OVERLAY, true, registry);
        controllerManager.registerController(TOGGLE_RADAR, true, registry);
        controllerManager.registerController(REVERSE_ENTITY_RADAR, true, registry);
        controllerManager.registerController(MANUAL_CAVE_MODE, true, registry);
        controllerManager.registerController(ALTERNATIVE_LIST_PLAYERS, true, registry);
        controllerManager.registerController(TOGGLE_TRACKED_PLAYERS_MAP, true, registry);
        controllerManager.registerController(TOGGLE_TRACKED_PLAYERS_WORLD, true, registry);
        controllerManager.registerController(TOGGLE_OPAC_CLAIMS, true, registry);
        MinimapKeyMappingFunctions.registerAll(controllerManager);
    }
}

