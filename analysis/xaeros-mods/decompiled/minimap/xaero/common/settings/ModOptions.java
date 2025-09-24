/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_3532
 */
package xaero.common.settings;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_3532;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CursorBox;
import xaero.common.settings.ModSettings;
import xaero.common.settings.Option;
import xaero.common.settings.ScreenEntranceOption;
import xaero.common.settings.XaeroCyclingOption;
import xaero.common.settings.XaeroDoubleOption;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;

public class ModOptions {
    public static CursorBox REQUIRES_INGAME;
    private static CursorBox WORLD_MAP_BOX;
    private static CursorBox WORLD_MAP_HARD_BOX;
    private static CursorBox WORLD_MAP_SCREEN_BOX;
    public static IXaeroMinimap modMain;
    public static ModOptions DEFAULT;
    public static ModOptions MINIMAP;
    public static ModOptions CAVE_MAPS;
    public static ModOptions CAVE_ZOOM;
    public static ModOptions DISPLAY_OTHER_TEAM;
    public static ModOptions WAYPOINTS;
    public static ModOptions INGAME_WAYPOINTS;
    public static ModOptions ZOOM;
    public static ModOptions SIZE;
    public static ModOptions EAMOUNT;
    public static ModOptions NORTH;
    public static ModOptions DEATHPOINTS;
    public static ModOptions OLD_DEATHPOINTS;
    public static ModOptions CHUNK_GRID;
    public static ModOptions SLIME_CHUNKS;
    public static ModOptions SAFE_MAP;
    public static ModOptions OPACITY;
    public static ModOptions WAYPOINTS_ICON_SCALE;
    public static ModOptions WAYPOINTS_DISTANCE_SCALE;
    public static ModOptions WAYPOINTS_NAME_SCALE;
    public static ModOptions WAYPOINTS_CLOSE_SCALE;
    public static ModOptions AA;
    public static ModOptions DISTANCE;
    public static ModOptions COLOURS;
    public static ModOptions LIGHT;
    public static ModOptions REDSTONE;
    public static ModOptions DOTS_SIZE;
    public static ModOptions DOTS_STYLE;
    public static ModOptions HEADS_SCALE;
    public static ModOptions DOT_NAME_SCALE;
    public static ModOptions COMPASS;
    public static ModOptions ENTITY_HEIGHT;
    public static ModOptions START_FADING_AT;
    public static ModOptions FLOWERS;
    public static ModOptions KEEP_WP_NAMES;
    public static ModOptions WAYPOINTS_DISTANCE;
    public static ModOptions WAYPOINTS_DISTANCE_MIN;
    public static ModOptions WAYPOINTS_ALL_SETS;
    public static ModOptions ARROW_SCALE;
    public static ModOptions ARROW_COLOUR;
    public static ModOptions SMOOTH_DOTS;
    public static ModOptions ENTITY_NAMETAGS;
    public static ModOptions HEIGHT_LIMIT;
    public static ModOptions WORLD_MAP;
    public static ModOptions TERRAIN_DEPTH;
    public static ModOptions TERRAIN_SLOPES;
    public static ModOptions MAIN_ENTITY_AS;
    public static ModOptions BLOCK_TRANSPARENCY;
    public static ModOptions WAYPOINT_OPACITY_INGAME;
    public static ModOptions WAYPOINT_OPACITY_MAP;
    public static ModOptions WAYPOINT_LOOKING_ANGLE;
    public static ModOptions WAYPOINT_VERTICAL_LOOKING_ANGLE;
    public static ModOptions HIDE_WORLD_NAMES;
    public static ModOptions OPEN_SLIME_SETTINGS;
    public static ModOptions ALWAYS_SHOW_DISTANCE;
    public static ModOptions CROSS_DIMENSIONAL_TP;
    public static ModOptions BIOMES_VANILLA;
    public static ModOptions CENTERED_ENLARGED;
    public static ModOptions ZOOM_ON_ENLARGE;
    public static ModOptions MINIMAP_TEXT_ALIGN;
    public static ModOptions COMPASS_LOCATION;
    public static ModOptions CAVE_MAPS_DEPTH;
    public static ModOptions HIDE_WP_COORDS;
    public static ModOptions PLAYER_ARROW_OPACITY;
    public static ModOptions IGNORE_HEIGHTMAPS;
    public static ModOptions WAYPOINTS_BOTTOM;
    public static ModOptions MINIMAP_SHAPE;
    public static ModOptions LIGHT_OVERLAY_TYPE;
    public static ModOptions LIGHT_OVERLAY_COLOR;
    public static ModOptions LIGHT_OVERLAY_MAX_LIGHT;
    public static ModOptions LIGHT_OVERLAY_MIN_LIGHT;
    public static ModOptions UI_SCALE;
    public static ModOptions BOSS_HEALTH_PUSHBOX;
    public static ModOptions POTION_EFFECTS_PUSHBOX;
    public static ModOptions MINIMAP_FRAME;
    public static ModOptions MINIMAP_FRAME_COLOR;
    public static ModOptions COMPASS_SCALE;
    public static ModOptions COMPASS_COLOR;
    public static ModOptions ICON_NAME_FALLBACK;
    public static ModOptions MULTIPLE_WAYPOINT_INFO;
    public static ModOptions UPDATE_NOTIFICATION;
    public static ModOptions ADJUST_HEIGHT_FOR_SHORT_BLOCKS;
    public static ModOptions AUTO_CONVERT_TO_KM;
    public static ModOptions WP_DISTANCE_PRECISION;
    public static ModOptions RADAR_Y_DISPLAYED;
    public static ModOptions MANUAL_CAVE_MODE_START;
    public static ModOptions CHUNK_GRID_LINE_WIDTH;
    public static ModOptions RADAR_OVER_FRAME;
    public static ModOptions RADAR_DISPLAYED;
    public static ModOptions RADAR_NAMES_DISPLAYED;
    public static ModOptions RADAR_ICONS_DISPLAYED;
    public static ModOptions MAIN_DOT_SIZE;
    public static ModOptions PARTIAL_Y_TELEPORTATION;
    public static ModOptions DELETE_REACHED_DEATHPOINTS;
    public static ModOptions HIDE_MINIMAP_UNDER_SCREEN;
    public static ModOptions HIDE_MINIMAP_UNDER_F3;
    public static ModOptions NORTH_COMPASS_COLOR;
    public static ModOptions TEMPORARY_WAYPOINTS_GLOBAL;
    public static ModOptions KEEP_ENLARGED_UNLOCKED;
    public static ModOptions TOGGLED_ENLARGED;
    public static ModOptions DISPLAY_STAINED_GLASS;
    public static ModOptions WAYPOINT_ONMAP_SCALE;
    public static ModOptions SWITCH_TO_AUTO_ON_DEATH;
    public static ModOptions INFO_DISPLAY_BG_OPACITY;
    public static ModOptions CAVE_MODE_TOGGLE_TIMER;
    public static ModOptions LEGIBLE_CAVE_MAPS;
    public static ModOptions BIOME_BLENDING;
    public static ModOptions TRACKED_PLAYERS_ON_MAP;
    public static ModOptions TRACKED_PLAYERS_IN_WORLD;
    public static ModOptions TRACKED_PLAYER_WORLD_ICON_SCALE;
    public static ModOptions TRACKED_PLAYER_WORLD_NAME_SCALE;
    public static ModOptions TRACKED_PLAYER_MINIMAP_ICON_SCALE;
    public static ModOptions PAC_CLAIMS;
    public static ModOptions PAC_CLAIMS_FILL_OPACITY;
    public static ModOptions PAC_CLAIMS_BORDER_OPACITY;
    public static ModOptions PAC_CURRENT_CLAIM;
    public static ModOptions SHOW_EFFECTS;
    public static ModOptions SHOW_ARMOR;
    public static ModOptions BETTER_SPRINT;
    public static ModOptions KEEP_SNEAK;
    public static ModOptions ENCHANT_COLOR;
    public static ModOptions DURABILITY;
    public static ModOptions NOTIFICATIONS;
    public static ModOptions NOTIFICATIONS_HUNGER;
    public static ModOptions NOTIFICATIONS_HUNGER_LOW;
    public static ModOptions NOTIFICATIONS_HP;
    public static ModOptions NOTIFICATIONS_HP_LOW;
    public static ModOptions NOTIFICATIONS_TNT;
    public static ModOptions NOTIFICATIONS_ARROW;
    public static ModOptions NOTIFICATIONS_AIR;
    public static ModOptions NOTIFICATIONS_AIR_LOW;
    public static ModOptions XP;
    public static ModOptions NUMBERS;
    public static ModOptions SHOW_ENCHANTS;
    public static ModOptions ARCHERY;
    public static ModOptions POTION_NAMES;
    public static ModOptions POTION_EFFECTS_BLINK;
    public static ModOptions ENTITY_INFO;
    public static ModOptions ENTITY_INFO_STAY;
    public static ModOptions ENTITY_INFO_DISTANCE;
    public static ModOptions ENTITY_INFO_MAX_HEARTS;
    public static ModOptions ENTITY_INFO_NUMBERS;
    public static ModOptions ENTITY_INFO_EFFECTS;
    public static ModOptions ENTITY_INFO_EFFECTS_SCALE;
    public static ModOptions SHOW_FULL_AMOUNT;
    public static ModOptions ENTITY_INFO_ARMOUR_NUMBERS;
    public static ModOptions ENTITY_INFO_ARMOUR;
    public static ModOptions SHOW_ENTITY_MODEL;
    public static ModOptions ITEM_TOOLTIP;
    public static ModOptions ITEM_TOOLTIP_MIN_LINES;
    public static ModOptions ITEM_TOOLTIP_TIME;
    public static ModOptions ARMOUR_MAIN_HAND;
    public static ModOptions ARMOUR_OFF_HAND;
    public static ModOptions HELD_ITEMS_CENTERED_POSITION;
    public static ModOptions SCALED_MAX_WAYPOINT_DISTANCE;
    private final boolean enumDouble;
    public final boolean enumBoolean;
    private final String enumString;
    private double valueMin;
    private double valueMax;
    private double valueStep;
    private Option xOption;
    private CursorBox tooltip;
    private boolean ingameOnly;

    public static void init(IXaeroMinimap modMainIn) {
        modMain = modMainIn;
        DEFAULT = new ModOptions("Default", modMainIn, false);
        MINIMAP = new ModOptions("gui.xaero_minimap", new CursorBox("gui.xaero_box_minimap"), false);
        CAVE_MAPS = new ModOptions("gui.xaero_cave_maps", 0, 3, new CursorBox("gui.xaero_box_cave_maps2"), false);
        CAVE_ZOOM = new ModOptions("gui.xaero_cave_zoom", 0, 3, new CursorBox("gui.xaero_box_cave_zoom"), false);
        DISPLAY_OTHER_TEAM = new ModOptions("gui.xaero_display_teams", false);
        WAYPOINTS = new ModOptions("gui.xaero_display_waypoints", false);
        INGAME_WAYPOINTS = new ModOptions("gui.xaero_ingame_waypoints", false);
        ZOOM = new ModOptions("gui.xaero_zoom", 0, ModSettings.zooms.length - 1, false);
        SIZE = new ModOptions("gui.xaero_minimap_size", 54.0, 250.0, 1.0f, new CursorBox("gui.xaero_box_minimap_size"), false);
        EAMOUNT = new ModOptions("gui.xaero_entity_amount", EntityRadarCategorySettings.ENTITY_NUMBER.getUiFirstOption(), EntityRadarCategorySettings.ENTITY_NUMBER.getUiLastOption(), new CursorBox("gui.xaero_box_entity_amount"), false);
        NORTH = new ModOptions("gui.xaero_lock_north", false);
        DEATHPOINTS = new ModOptions("gui.xaero_deathpoints", false);
        OLD_DEATHPOINTS = new ModOptions("gui.xaero_old_deathpoints", false);
        CHUNK_GRID = new ModOptions("gui.xaero_chunkgrid", -1.0, (double)(ModSettings.COLORS.length - 1), 1.0f, false);
        SLIME_CHUNKS = new ModOptions("gui.xaero_slime_chunks", false);
        SAFE_MAP = new ModOptions("gui.xaero_safe_mode", new CursorBox("gui.xaero_safe_mode_box"), false);
        OPACITY = new ModOptions("gui.xaero_opacity", 30.0, 100.0, 1.0f, false);
        WAYPOINTS_ICON_SCALE = new ModOptions("gui.xaero_ingame_waypoint_icon_scale", 0.0, 17.0, 1.0f, new CursorBox("gui.xaero_box_ingame_waypoint_icon_scale"), false);
        WAYPOINTS_DISTANCE_SCALE = new ModOptions("gui.xaero_waypoints_distance_scale", 0.0, 17.0, 1.0f, new CursorBox("gui.xaero_box_waypoints_distance_scale2"), false);
        WAYPOINTS_NAME_SCALE = new ModOptions("gui.xaero_waypoints_name_scale", 0.0, 17.0, 1.0f, new CursorBox("gui.xaero_box_waypoints_name_scale2"), false);
        WAYPOINTS_CLOSE_SCALE = new ModOptions("gui.xaero_ingame_waypoint_close_scale", 0.125, 8.0, 0.025f, new CursorBox("gui.xaero_box_ingame_waypoint_close_scale"), false);
        AA = new ModOptions("gui.xaero_antialiasing", false);
        DISTANCE = new ModOptions("gui.xaero_show_distance", 0, ModSettings.distanceTypes.length - 1, new CursorBox("gui.xaero_box_distance2"), false);
        COLOURS = new ModOptions("gui.xaero_block_colours", 0, ModSettings.blockColourTypes.length - 1, false);
        LIGHT = new ModOptions("gui.xaero_lighting", false);
        REDSTONE = new ModOptions("gui.xaero_display_redstone", false);
        DOTS_SIZE = new ModOptions("gui.xaero_dots_size", (double)EntityRadarCategorySettings.DOT_SIZE.getUiFirstOption(), (double)EntityRadarCategorySettings.DOT_SIZE.getUiLastOption(), 1.0f, false);
        DOTS_STYLE = new ModOptions("gui.xaero_dots_style", 0, ModSettings.DOTS_STYLES.length - 1, false);
        DOT_NAME_SCALE = new ModOptions("gui.xaero_dot_name_scale", 1.0, 3.0, 0.5f, false);
        HEADS_SCALE = new ModOptions("gui.xaero_entity_heads_scale", (double)EntityRadarCategorySettings.ICON_SCALE.getUiFirstOption(), (double)EntityRadarCategorySettings.ICON_SCALE.getUiLastOption(), 1.0f, false);
        COMPASS = new ModOptions("gui.xaero_compass_over_everything", new CursorBox("gui.xaero_box_compass_over_everything"), false);
        ENTITY_HEIGHT = new ModOptions("gui.xaero_entity_depth", new CursorBox("gui.xaero_box_entity_depth"), false);
        START_FADING_AT = new ModOptions("gui.xaero_start_fading_at", EntityRadarCategorySettings.START_FADING_AT.getUiFirstOption(), EntityRadarCategorySettings.START_FADING_AT.getUiLastOption(), 1.0f, new CursorBox("gui.xaero_box_start_fading_at"), false);
        FLOWERS = new ModOptions("gui.xaero_show_flowers", false);
        KEEP_WP_NAMES = new ModOptions("gui.xaero_waypoint_names", false);
        WAYPOINTS_DISTANCE = new ModOptions("gui.xaero_waypoints_distance", 0.0, 20.0, 1.0f, new CursorBox("gui.xaero_box_waypoints_distance2"), false);
        WAYPOINTS_DISTANCE_MIN = new ModOptions("gui.xaero_waypoints_distance_min", 0.0, 100.0, 5.0f, false);
        WAYPOINTS_ALL_SETS = new ModOptions("gui.xaero_render_all_wp_sets", false);
        ARROW_SCALE = new ModOptions("gui.xaero_arrow_scale", 1.0, 2.0, 0.1f, new CursorBox("gui.xaero_box_arrow_scale"), false);
        ARROW_COLOUR = new ModOptions("gui.xaero_arrow_colour", -1, ModSettings.arrowColours.length - 1, new CursorBox("gui.xaero_box_arrow_color"), false);
        SMOOTH_DOTS = new ModOptions("gui.xaero_smooth_dots", false);
        ENTITY_NAMETAGS = new ModOptions("gui.xaero_always_entity_nametags", new CursorBox("gui.xaero_box_always_entity_nametags2"), false);
        HEIGHT_LIMIT = new ModOptions("gui.xaero_height_limit", EntityRadarCategorySettings.HEIGHT_LIMIT.getUiFirstOption(), EntityRadarCategorySettings.HEIGHT_LIMIT.getUiLastOption(), 1.0f, new CursorBox("gui.xaero_box_height_limit"), false);
        WORLD_MAP = new ModOptions("gui.xaero_use_world_map", false);
        TERRAIN_DEPTH = new ModOptions("gui.xaero_terrain_depth", false);
        TERRAIN_SLOPES = new ModOptions("gui.xaero_terrain_slopes", 0, 3, false);
        MAIN_ENTITY_AS = new ModOptions("gui.xaero_main_entity_as", 0, 2, false);
        BLOCK_TRANSPARENCY = new ModOptions("gui.xaero_block_transparency", false);
        WAYPOINT_OPACITY_INGAME = new ModOptions("gui.xaero_waypoint_opacity_ingame", 10.0, 100.0, 1.0f, false);
        WAYPOINT_OPACITY_MAP = new ModOptions("gui.xaero_waypoint_opacity_map", 10.0, 100.0, 1.0f, false);
        WAYPOINT_LOOKING_ANGLE = new ModOptions("gui.xaero_waypoint_distance_visibility_angle", 1.0, 180.0, 1.0f, false);
        WAYPOINT_VERTICAL_LOOKING_ANGLE = new ModOptions("gui.xaero_waypoint_distance_vertical_visibility_angle", 1.0, 180.0, 1.0f, false);
        HIDE_WORLD_NAMES = new ModOptions("gui.xaero_hide_world_names", 0, 2, false);
        OPEN_SLIME_SETTINGS = new ModOptions("gui.xaero_open_slime", false);
        ALWAYS_SHOW_DISTANCE = new ModOptions("gui.xaero_always_show_distance", new CursorBox("gui.xaero_box_always_distance"), false);
        CROSS_DIMENSIONAL_TP = new ModOptions("gui.xaero_cross_tp", false);
        BIOMES_VANILLA = new ModOptions("gui.xaero_biomes_vanilla", false);
        CENTERED_ENLARGED = new ModOptions("gui.xaero_centered_enlarged", false);
        ZOOM_ON_ENLARGE = new ModOptions("gui.xaero_zoom_on_enlarge", 0, 5, false);
        MINIMAP_TEXT_ALIGN = new ModOptions("gui.xaero_minimap_text_align", 0, 2, false);
        COMPASS_LOCATION = new ModOptions("gui.xaero_compass", 0, ModSettings.COMPASS_OPTIONS.length - 1, new CursorBox("gui.xaero_box_compass"), false);
        CAVE_MAPS_DEPTH = new ModOptions("gui.xaero_cave_maps_depth", 0.0, 64.0, 1.0f, false);
        HIDE_WP_COORDS = new ModOptions("gui.xaero_hide_wp_coords", false);
        PLAYER_ARROW_OPACITY = new ModOptions("gui.xaero_player_arrow_opacity", 1.0, 100.0, 1.0f, false);
        IGNORE_HEIGHTMAPS = new ModOptions("gui.xaero_ignore_heightmaps", new CursorBox("gui.xaero_box_ignore_heightmaps"), true);
        WAYPOINTS_BOTTOM = new ModOptions("gui.xaero_waypoints_bottom", new CursorBox("gui.xaero_box_waypoints_bottom"), false);
        MINIMAP_SHAPE = new ModOptions("gui.xaero_minimap_shape", 0, 1, false);
        LIGHT_OVERLAY_TYPE = new ModOptions("gui.xaero_light_overlay_type", 0, 3, new CursorBox("gui.xaero_box_light_overlay_type"), false);
        LIGHT_OVERLAY_COLOR = new ModOptions("gui.xaero_light_overlay_color", 0.0, 15.0, 1.0f, false);
        LIGHT_OVERLAY_MAX_LIGHT = new ModOptions("gui.xaero_light_overlay_max_light", 0.0, 15.0, 1.0f, false);
        LIGHT_OVERLAY_MIN_LIGHT = new ModOptions("gui.xaero_light_overlay_min_light", 0.0, 15.0, 1.0f, false);
        UI_SCALE = new ModOptions("gui.xaero_ui_scale", 1.0, 11.0, 1.0f, new CursorBox("gui.xaero_box_ui_scale"), false);
        BOSS_HEALTH_PUSHBOX = new ModOptions("gui.xaero_pushbox_boss_health", 0, 2, new CursorBox("gui.xaero_box_pushbox_boss_health"), false);
        POTION_EFFECTS_PUSHBOX = new ModOptions("gui.xaero_pushbox_potion_effects", 0, 2, new CursorBox("gui.xaero_box_pushbox_potion_effects"), false);
        MINIMAP_FRAME = new ModOptions("gui.xaero_minimap_frame", 0, ModSettings.FRAME_OPTIONS.length - 1, false);
        MINIMAP_FRAME_COLOR = new ModOptions("gui.xaero_minimap_frame_color", 0.0, ModSettings.COLORS.length - 1, 1.0f, new CursorBox("gui.xaero_box_minimap_frame_color"), false);
        COMPASS_SCALE = new ModOptions("gui.xaero_compass_scale2", 0.0, 16.0, 1.0f, new CursorBox("gui.xaero_box_compass_scale2"), false);
        COMPASS_COLOR = new ModOptions("gui.xaero_compass_color", 0.0, ModSettings.COLORS.length - 1, 1.0f, new CursorBox("gui.xaero_box_compass_color"), false);
        ICON_NAME_FALLBACK = new ModOptions("gui.xaero_entity_icon_name_fallback", false);
        MULTIPLE_WAYPOINT_INFO = new ModOptions("gui.xaero_multiple_waypoint_info", 0, 2, new CursorBox("gui.xaero_box_multiple_waypoint_info"), false);
        RADAR_DISPLAYED = new ModOptions("gui.xaero_radar_setting_displayed", false);
        RADAR_NAMES_DISPLAYED = new ModOptions("gui.xaero_radar_setting_names", EntityRadarCategorySettings.NAMES.getUiFirstOption(), EntityRadarCategorySettings.NAMES.getUiLastOption(), new CursorBox("gui.xaero_box_entity_radar_names"), false);
        RADAR_ICONS_DISPLAYED = new ModOptions("gui.xaero_radar_setting_icons", EntityRadarCategorySettings.ICONS.getUiFirstOption(), EntityRadarCategorySettings.ICONS.getUiLastOption(), new CursorBox("gui.xaero_box_entity_radar_icons"), false);
        UPDATE_NOTIFICATION = new ModOptions("gui.xaero_update_notification", false);
        ADJUST_HEIGHT_FOR_SHORT_BLOCKS = new ModOptions("gui.xaero_adjust_height_for_carpetlike_blocks", new CursorBox("gui.xaero_box_adjust_height_for_carpetlike_blocks"), false);
        AUTO_CONVERT_TO_KM = new ModOptions("gui.xaero_auto_convert_wp_distance_km", -1.0, 10.0, 1.0f, false);
        WP_DISTANCE_PRECISION = new ModOptions("gui.xaero_waypoint_distance_precision", 0.0, 10.0, 1.0f, false);
        MAIN_DOT_SIZE = new ModOptions("gui.xaero_main_entity_dot_size", 1.0, 4.0, 1.0f, false);
        PARTIAL_Y_TELEPORTATION = new ModOptions("gui.xaero_partial_y_teleportation", new CursorBox("gui.xaero_box_partial_y_teleportation"), false);
        DELETE_REACHED_DEATHPOINTS = new ModOptions("gui.xaero_delete_reached_deathpoints", new CursorBox("gui.xaero_box_delete_reached_deathpoints"), false);
        HIDE_MINIMAP_UNDER_SCREEN = new ModOptions("gui.xaero_hide_minimap_under_screen", new CursorBox("gui.xaero_box_hide_minimap_under_screen"), false);
        HIDE_MINIMAP_UNDER_F3 = new ModOptions("gui.xaero_hide_minimap_under_f3", new CursorBox("gui.xaero_box_hide_minimap_under_f3"), false);
        NORTH_COMPASS_COLOR = new ModOptions("gui.xaero_north_compass_color", -1.0, ModSettings.COLORS.length - 1, 1.0f, new CursorBox("gui.xaero_box_north_compass_color"), false);
        RADAR_Y_DISPLAYED = new ModOptions("gui.xaero_entity_display_height_full", EntityRadarCategorySettings.DISPLAY_Y.getUiFirstOption(), EntityRadarCategorySettings.DISPLAY_Y.getUiLastOption(), false);
        MANUAL_CAVE_MODE_START = new ModOptions("gui.xaero_manual_cave_mode_start", 0.0, 48.0, 1.0f, new CursorBox("gui.xaero_box_manual_cave_mode_start"), false);
        CHUNK_GRID_LINE_WIDTH = new ModOptions("gui.xaero_chunk_grid_line_width", 1.0, 8.0, 1.0f, false);
        RADAR_OVER_FRAME = new ModOptions("gui.xaero_radar_render_radar_over_frame", EntityRadarCategorySettings.RENDER_OVER_MINIMAP.getUiFirstOption(), EntityRadarCategorySettings.RENDER_OVER_MINIMAP.getUiLastOption(), new CursorBox("gui.xaero_box_radar_render_over_minimap"), false);
        TEMPORARY_WAYPOINTS_GLOBAL = new ModOptions("gui.xaero_temp_waypoints_global", new CursorBox("gui.xaero_box_temp_waypoints_global"), false);
        KEEP_ENLARGED_UNLOCKED = new ModOptions("gui.xaero_keep_enlarged_minimap_unlocked", new CursorBox("gui.xaero_box_keep_enlarged_minimap_unlocked"), false);
        TOGGLED_ENLARGED = new ModOptions("gui.xaero_enlarged_minimap_a_toggle", new CursorBox("gui.xaero_box_enlarged_minimap_a_toggle"), false);
        DISPLAY_STAINED_GLASS = new ModOptions("gui.xaero_display_stained_glass", false);
        WAYPOINT_ONMAP_SCALE = new ModOptions("gui.xaero_waypoint_onmap_scale", 0.0, 16.0, 1.0f, false);
        SWITCH_TO_AUTO_ON_DEATH = new ModOptions("gui.xaero_switch_to_auto_on_death", new CursorBox("gui.xaero_box_switch_to_auto_on_death"), false);
        INFO_DISPLAY_BG_OPACITY = new ModOptions("gui.xaero_info_display_background_opacity", 1.0, 100.0, 1.0f, new CursorBox("gui.xaero_box_info_display_background_opacity"), false);
        CAVE_MODE_TOGGLE_TIMER = new ModOptions("gui.xaero_cave_mode_toggle_timer", 0.0, 10000.0, 100.0f, new CursorBox("gui.xaero_box_cave_mode_toggle_timer"), false);
        LEGIBLE_CAVE_MAPS = new ModOptions("gui.xaero_legible_cave_maps", new CursorBox("gui.xaero_box_legible_cave_maps"), false);
        BIOME_BLENDING = new ModOptions("gui.xaero_biome_blending", new CursorBox("gui.xaero_box_biome_blending"), false);
        TRACKED_PLAYERS_ON_MAP = new ModOptions("gui.xaero_tracked_players_on_map", new CursorBox("gui.xaero_box_tracked_players_on_map"), false);
        TRACKED_PLAYERS_IN_WORLD = new ModOptions("gui.xaero_tracked_players_in_world", new CursorBox("gui.xaero_box_tracked_players_in_world"), false);
        SCALED_MAX_WAYPOINT_DISTANCE = new ModOptions("gui.xaero_dimension_scaled_max_waypoint_distance", new CursorBox("gui.xaero_box_dimension_scaled_max_waypoint_distance"), false);
        TRACKED_PLAYER_WORLD_ICON_SCALE = new ModOptions("gui.xaero_tracked_player_world_icon_scale", 0.0, 17.0, 1.0f, new CursorBox("gui.xaero_box_tracked_player_world_icon_scale"), false);
        TRACKED_PLAYER_WORLD_NAME_SCALE = new ModOptions("gui.xaero_tracked_player_world_name_scale", 0.0, 17.0, 1.0f, new CursorBox("gui.xaero_box_tracked_player_world_name_scale"), false);
        TRACKED_PLAYER_MINIMAP_ICON_SCALE = new ModOptions("gui.xaero_tracked_player_minimap_icon_scale", 0.0, 17.0, 1.0f, new CursorBox("gui.xaero_box_tracked_player_minimap_icon_scale"), false);
        PAC_CLAIMS = new ModOptions("gui.xaero_pac_claims", new CursorBox("gui.xaero_box_pac_claims"), false);
        PAC_CLAIMS_FILL_OPACITY = new ModOptions("gui.xaero_pac_claims_fill_opacity", 1.0, 100.0, 1.0f, new CursorBox("gui.xaero_box_pac_claims_fill_opacity"), false);
        PAC_CLAIMS_BORDER_OPACITY = new ModOptions("gui.xaero_pac_claims_border_opacity", 1.0, 100.0, 1.0f, new CursorBox("gui.xaero_box_pac_claims_border_opacity"), false);
        PAC_CURRENT_CLAIM = new ModOptions("gui.xaero_pac_current_claim", new CursorBox("gui.xaero_box_pac_current_claim"), false);
        SHOW_EFFECTS = new ModOptions("gui.xaero_potion_status", new CursorBox("gui.xaero_box_potion_effects"), false);
        SHOW_ARMOR = new ModOptions("gui.xaero_armour_status", new CursorBox("gui.xaero_box_armour_status"), false);
        BETTER_SPRINT = new ModOptions("gui.xaero_sprint", new CursorBox("gui.xaero_box_sprint"), false);
        KEEP_SNEAK = new ModOptions("gui.xaero_sneak", new CursorBox("gui.xaero_box_sneak"), false);
        ENCHANT_COLOR = new ModOptions("gui.xaero_enchants_color", 0, ModSettings.ENCHANT_COLORS.length - 1, false);
        DURABILITY = new ModOptions("gui.xaero_durability", 0, 3, false);
        NOTIFICATIONS = new ModOptions("gui.xaero_notifications", new CursorBox("gui.xaero_box_notifications"), false);
        NOTIFICATIONS_HUNGER = new ModOptions("gui.xaero_hunger_setting", false);
        NOTIFICATIONS_HUNGER_LOW = new ModOptions("gui.xaero_hunger_low", 1, 10, false);
        NOTIFICATIONS_HP = new ModOptions("gui.xaero_hp_setting", false);
        NOTIFICATIONS_HP_LOW = new ModOptions("gui.xaero_hp_low", 1, 10, false);
        NOTIFICATIONS_TNT = new ModOptions("gui.xaero_explosion_setting", false);
        NOTIFICATIONS_ARROW = new ModOptions("gui.xaero_being_shot_setting", false);
        NOTIFICATIONS_AIR = new ModOptions("gui.xaero_air_setting", false);
        NOTIFICATIONS_AIR_LOW = new ModOptions("gui.xaero_air_low", 1, 10, false);
        XP = new ModOptions("gui.xaero_xp_setting", new CursorBox("gui.xaero_box_xp"), false);
        NUMBERS = new ModOptions("gui.xaero_quick_use", new CursorBox("gui.xaero_box_quick_use"), false);
        SHOW_ENCHANTS = new ModOptions("gui.xaero_show_enchants", false);
        ARCHERY = new ModOptions("gui.xaero_archery_status", false);
        POTION_NAMES = new ModOptions("gui.xaero_potion_names", false);
        POTION_EFFECTS_BLINK = new ModOptions("gui.xaero_potion_effects_blink", false);
        ENTITY_INFO = new ModOptions("gui.xaero_entity_info", new CursorBox("gui.xaero_box_entity_info"), false);
        ENTITY_INFO_STAY = new ModOptions("gui.xaero_entity_info_stay", 0, 5, false);
        ENTITY_INFO_DISTANCE = new ModOptions("gui.xaero_entity_info_distance", 1.0, 40.0, 1.0f, false);
        ENTITY_INFO_MAX_HEARTS = new ModOptions("gui.xaero_entity_info_max_hearts", 10.0, 1000.0, 10.0f, false);
        ENTITY_INFO_NUMBERS = new ModOptions("gui.xaero_entity_info_numbers", false);
        ENTITY_INFO_EFFECTS = new ModOptions("gui.xaero_entity_info_potion_effects", false);
        ENTITY_INFO_EFFECTS_SCALE = new ModOptions("gui.xaero_entity_info_potion_effects_scale", 1.0, 4.0, 1.0f, false);
        SHOW_FULL_AMOUNT = new ModOptions("gui.xaero_show_full_amount", false);
        ENTITY_INFO_ARMOUR_NUMBERS = new ModOptions("gui.xaero_entity_info_armour_numbers", false);
        ENTITY_INFO_ARMOUR = new ModOptions("gui.xaero_entity_info_armour", false);
        SHOW_ENTITY_MODEL = new ModOptions("gui.xaero_show_entity_model", false);
        ITEM_TOOLTIP = new ModOptions("gui.xaero_item_tooltip", false);
        ITEM_TOOLTIP_MIN_LINES = new ModOptions("gui.xaero_item_tooltip_min_lines", 0.0, 10.0, 1.0f, false);
        ITEM_TOOLTIP_TIME = new ModOptions("gui.xaero_item_tooltip_time", 1.0, 40.0, 1.0f, false);
        ARMOUR_MAIN_HAND = new ModOptions("gui.xaero_armour_main_hand", false);
        ARMOUR_OFF_HAND = new ModOptions("gui.xaero_armour_off_hand", false);
        HELD_ITEMS_CENTERED_POSITION = new ModOptions("gui.xaero_held_centered_position", 0, 1, false);
        REQUIRES_INGAME = new CursorBox("gui.xaero_option_requires_ingame");
        WORLD_MAP_BOX = new CursorBox("gui.xaero_uses_worldmap_value");
        WORLD_MAP_HARD_BOX = new CursorBox("gui.xaero_uses_worldmap_hard_value");
        WORLD_MAP_SCREEN_BOX = new CursorBox("gui.xaero_uses_worldmap_screen_value");
    }

    private ModOptions(String name, IXaeroMinimap modMain, boolean ingameOnly) {
        this(name, modMain, null, ingameOnly);
    }

    private ModOptions(String name, IXaeroMinimap modMain, CursorBox tooltip, boolean ingameOnly) {
        this.enumString = name;
        this.enumBoolean = false;
        this.enumDouble = false;
        this.tooltip = tooltip;
        this.xOption = new ScreenEntranceOption(name, modMain, this);
        this.ingameOnly = ingameOnly;
    }

    private ModOptions(String par3Str, boolean ingameOnly) {
        this(par3Str, (CursorBox)null, ingameOnly);
    }

    private ModOptions(String par3Str, CursorBox tooltip, boolean ingameOnly) {
        this(par3Str, true, () -> Lists.newArrayList((Object[])new Boolean[]{false, true}), tooltip, ingameOnly);
    }

    private ModOptions(String par3Str, int firstOption, int lastOption, boolean ingameOnly) {
        this(par3Str, firstOption, lastOption, null, ingameOnly);
    }

    private ModOptions(String par3Str, int firstOption, int lastOption, CursorBox tooltip, boolean ingameOnly) {
        this(par3Str, false, () -> {
            List optionsList = IntStream.rangeClosed(firstOption, lastOption).boxed().collect(Collectors.toList());
            return optionsList;
        }, tooltip, ingameOnly);
    }

    private <T> ModOptions(String par3Str, boolean isBoolean, Supplier<List<T>> optionsListSupplier, CursorBox tooltip, boolean ingameOnly) {
        this.enumString = par3Str;
        this.enumBoolean = isBoolean;
        this.enumDouble = false;
        Supplier<Object> valueGetter = () -> modMain.getSettings().getOptionValue(this);
        Consumer<Object> valueSetter = v -> modMain.getSettings().setOptionValue(this, v);
        this.xOption = new XaeroCyclingOption<Object>(this, optionsListSupplier.get(), valueGetter, valueSetter, () -> class_2561.method_43470((String)modMain.getSettings().getOptionValueName(this)));
        this.tooltip = tooltip;
        this.ingameOnly = ingameOnly;
    }

    private ModOptions(String p_i45004_3_, double p_i45004_6_, double p_i45004_7_, float p_i45004_8_, boolean ingameOnly) {
        this(p_i45004_3_, p_i45004_6_, p_i45004_7_, p_i45004_8_, null, ingameOnly);
    }

    private ModOptions(String p_i45004_3_, double p_i45004_6_, double p_i45004_7_, float p_i45004_8_, CursorBox tooltip, boolean ingameOnly) {
        this.enumString = p_i45004_3_;
        this.enumBoolean = false;
        this.enumDouble = true;
        this.valueMin = p_i45004_6_;
        this.valueMax = p_i45004_7_;
        this.valueStep = p_i45004_8_;
        this.xOption = new XaeroDoubleOption(this, p_i45004_6_, p_i45004_7_, p_i45004_8_, () -> modMain.getSettings().getOptionDoubleValue(this), value -> modMain.getSettings().setOptionDoubleValue(this, (double)value), () -> class_2561.method_43470((String)modMain.getSettings().getSliderOptionText(this)));
        this.tooltip = tooltip;
        this.ingameOnly = ingameOnly;
    }

    public boolean getEnumDouble() {
        return this.enumDouble;
    }

    public boolean getEnumBoolean() {
        return this.enumBoolean;
    }

    public double getValueMin() {
        return this.valueMin;
    }

    public double getValueMax() {
        return this.valueMax;
    }

    public double getValueStep() {
        return this.valueStep;
    }

    public void setValueMax(float p_148263_1_) {
        this.valueMax = p_148263_1_;
    }

    public double normalizeValue(double p_148266_1_) {
        return class_3532.method_15350((double)((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin)), (double)0.0, (double)1.0);
    }

    public double denormalizeValue(double p_148262_1_) {
        return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * class_3532.method_15350((double)p_148262_1_, (double)0.0, (double)1.0));
    }

    public double snapToStepClamp(double p_148268_1_) {
        p_148268_1_ = this.snapToStep(p_148268_1_);
        return class_3532.method_15350((double)p_148268_1_, (double)this.valueMin, (double)this.valueMax);
    }

    protected double snapToStep(double p_148264_1_) {
        if (this.valueStep > 0.0) {
            p_148264_1_ = this.valueStep * (double)Math.round(p_148264_1_ / this.valueStep);
        }
        return p_148264_1_;
    }

    public String getEnumString() {
        return class_1074.method_4662((String)this.enumString, (Object[])new Object[0]);
    }

    public String getEnumStringRaw() {
        return this.enumString;
    }

    public Option getXOption() {
        return this.xOption;
    }

    public CursorBox getTooltip() {
        if (this.isIngameOnly() && !ModSettings.canEditIngameSettings()) {
            return REQUIRES_INGAME;
        }
        return modMain.getSettings().usesWorldMapOptionValue(this) ? WORLD_MAP_BOX : (modMain.getSettings().usesWorldMapHardValue(this) ? WORLD_MAP_HARD_BOX : (modMain.getSettings().usesWorldMapScreenValue(this) ? WORLD_MAP_SCREEN_BOX : this.tooltip));
    }

    public boolean isIngameOnly() {
        return this.ingameOnly;
    }
}

