/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_1074
 *  net.minecraft.class_2561
 *  net.minecraft.class_3532
 */
package xaero.map.settings;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_3532;
import xaero.map.WorldMap;
import xaero.map.gui.CursorBox;
import xaero.map.mods.SupportMods;
import xaero.map.settings.ModSettings;
import xaero.map.settings.Option;
import xaero.map.settings.XaeroCyclingOption;
import xaero.map.settings.XaeroDoubleOption;

public class ModOptions {
    public static final CursorBox REQUIRES_MINIMAP = new CursorBox("gui.xaero_wm_option_requires_minimap");
    public static final CursorBox REQUIRES_INGAME = new CursorBox("gui.xaero_wm_option_requires_ingame");
    public static ModOptions DEBUG;
    public static ModOptions COLOURS;
    public static ModOptions LIGHTING;
    public static ModOptions UPDATE;
    public static ModOptions LOAD;
    public static ModOptions DEPTH;
    public static ModOptions SLOPES;
    public static ModOptions STEPS;
    public static ModOptions FLOWERS;
    public static ModOptions COORDINATES;
    public static ModOptions HOVERED_BIOME;
    public static ModOptions BIOMES;
    public static ModOptions WAYPOINTS;
    public static ModOptions ARROW;
    public static ModOptions DISPLAY_ZOOM;
    public static ModOptions IGNORE_HEIGHTMAPS;
    public static ModOptions WAYPOINT_SCALE;
    public static ModOptions OPEN_ANIMATION;
    public static ModOptions RELOAD;
    public static ModOptions ZOOM_BUTTONS;
    public static ModOptions WAYPOINT_BACKGROUNDS;
    public static ModOptions PAUSE_REQUESTS;
    public static ModOptions EXTRA_DEBUG;
    public static ModOptions DETECT_AMBIGUOUS_Y;
    public static ModOptions UPDATE_NOTIFICATION;
    public static ModOptions ADJUST_HEIGHT_FOR_SHORT_BLOCKS;
    public static ModOptions MIN_ZOOM_LOCAL_WAYPOINTS;
    public static ModOptions ARROW_COLOUR;
    public static ModOptions PAC_CLAIMS;
    public static ModOptions PAC_CLAIMS_BORDER_OPACITY;
    public static ModOptions PAC_CLAIMS_FILL_OPACITY;
    public static ModOptions MAP_TELEPORT_ALLOWED;
    public static ModOptions PARTIAL_Y_TELEPORTATION;
    public static ModOptions DISPLAY_STAINED_GLASS;
    public static ModOptions CAVE_MODE_DEPTH;
    public static ModOptions CAVE_MODE_START;
    public static ModOptions LEGIBLE_CAVE_MAPS;
    public static ModOptions AUTO_CAVE_MODE;
    public static ModOptions DISPLAY_CAVE_MODE_START;
    public static ModOptions CAVE_MODE_TOGGLE_TIMER;
    public static ModOptions DEFAULT_CAVE_MODE_TYPE;
    public static ModOptions BIOME_BLENDING;
    public static ModOptions FULL_EXPORT;
    public static ModOptions MULTIPLE_IMAGES_EXPORT;
    public static ModOptions NIGHT_EXPORT;
    public static ModOptions EXPORT_SCALE_DOWN_SQUARE;
    public static ModOptions EXPORT_HIGHLIGHTS;
    public static ModOptions MAP_WRITING_DISTANCE;
    public static ModOptions FULL_RELOAD;
    public static ModOptions FULL_RESAVE;
    private final boolean enumDouble;
    final boolean enumBoolean;
    private final String enumString;
    private double valueMin;
    private double valueMax;
    private double valueStep;
    private Option xOption;
    private CursorBox tooltip;
    private boolean ingameOnly;
    private boolean requiresMinimap;
    private boolean requiresPac;

    public static void init() {
        DEBUG = new ModOptions("gui.xaero_debug", false, false, false);
        COLOURS = new ModOptions("gui.xaero_block_colours", 2, false, false, false);
        LIGHTING = new ModOptions("gui.xaero_lighting", false, false, false);
        UPDATE = new ModOptions("gui.xaero_update_chunks", false, false, false);
        LOAD = new ModOptions("gui.xaero_load_chunks", false, false, false);
        DEPTH = new ModOptions("gui.xaero_terrain_depth", false, false, false);
        SLOPES = new ModOptions("gui.xaero_terrain_slopes", 4, false, false, false);
        STEPS = new ModOptions("gui.xaero_footsteps", false, false, false);
        FLOWERS = new ModOptions("gui.xaero_flowers", false, false, false);
        COORDINATES = new ModOptions("gui.xaero_wm_coordinates", false, false, false);
        HOVERED_BIOME = new ModOptions("gui.xaero_wm_hovered_biome", false, false, false);
        BIOMES = new ModOptions("gui.xaero_biome_colors", false, false, false);
        WAYPOINTS = new ModOptions("gui.xaero_worldmap_waypoints", false, true, false);
        ARROW = new ModOptions("gui.xaero_render_arrow", false, false, false);
        DISPLAY_ZOOM = new ModOptions("gui.xaero_display_zoom", false, false, false);
        IGNORE_HEIGHTMAPS = new ModOptions("gui.xaero_wm_ignore_heightmaps", new CursorBox("gui.xaero_wm_box_ignore_heightmaps"), true, false, false);
        WAYPOINT_SCALE = new ModOptions("gui.xaero_wm_waypoint_scale", 0.5, 5.0, 0.5, false, true, false);
        OPEN_ANIMATION = new ModOptions("gui.xaero_open_map_animation", false, false, false);
        RELOAD = new ModOptions("gui.xaero_reload_viewed_regions", new CursorBox("gui.xaero_box_reload_viewed_regions"), false, false, false);
        ZOOM_BUTTONS = new ModOptions("gui.xaero_zoom_buttons", false, false, false);
        WAYPOINT_BACKGROUNDS = new ModOptions("gui.xaero_waypoint_backgrounds", false, true, false);
        PAUSE_REQUESTS = new ModOptions("pause_requests", false, false, false);
        EXTRA_DEBUG = new ModOptions("extra_debug", false, false, false);
        DETECT_AMBIGUOUS_Y = new ModOptions("gui.xaero_wm_detect_ambiguous_y", new CursorBox("gui.xaero_wm_box_detect_ambiguous_y"), false, false, false);
        UPDATE_NOTIFICATION = new ModOptions("gui.xaero_wm_update_notification", false, false, false);
        ADJUST_HEIGHT_FOR_SHORT_BLOCKS = new ModOptions("gui.xaero_wm_adjust_height_for_carpetlike_blocks", new CursorBox("gui.xaero_wm_box_adjust_height_for_carpetlike_blocks"), false, false, false);
        MIN_ZOOM_LOCAL_WAYPOINTS = new ModOptions("gui.xaero_wm_min_zoom_local_waypoints", 0.0, 3.0, 0.01, false, true, false);
        ARROW_COLOUR = new ModOptions("gui.xaero_wm_arrow_colour", ModSettings.arrowColours.length + 2, new CursorBox("gui.xaero_wm_box_arrow_color"), false, false, false);
        PAC_CLAIMS = new ModOptions("gui.xaero_wm_pac_claims", new CursorBox("gui.xaero_wm_box_pac_claims"), false, false, true);
        PAC_CLAIMS_FILL_OPACITY = new ModOptions("gui.xaero_wm_pac_claims_fill_opacity", 1.0, 100.0, 1.0, new CursorBox("gui.xaero_wm_box_pac_claims_fill_opacity"), false, false, true);
        PAC_CLAIMS_BORDER_OPACITY = new ModOptions("gui.xaero_wm_pac_claims_border_opacity", 1.0, 100.0, 1.0, new CursorBox("gui.xaero_wm_box_pac_claims_border_opacity"), false, false, true);
        MAP_TELEPORT_ALLOWED = new ModOptions("gui.xaero_wm_teleport_allowed", new CursorBox("gui.xaero_wm_teleport_allowed_tooltip"), true, false, false);
        PARTIAL_Y_TELEPORTATION = new ModOptions("gui.xaero_wm_partial_y_teleportation", new CursorBox("gui.xaero_wm_box_partial_y_teleportation"), false, false, false);
        DISPLAY_STAINED_GLASS = new ModOptions("gui.xaero_wm_display_stained_glass", false, false, false);
        CAVE_MODE_DEPTH = new ModOptions("gui.xaero_wm_cave_mode_depth", 1.0, 64.0, 1.0, false, false, false);
        CAVE_MODE_START = new ModOptions("gui.xaero_wm_cave_mode_start", -65.0, 319.0, 1.0, false, false, false);
        LEGIBLE_CAVE_MAPS = new ModOptions("gui.xaero_wm_legible_cave_maps", new CursorBox("gui.xaero_wm_box_legible_cave_maps"), false, false, false);
        AUTO_CAVE_MODE = new ModOptions("gui.xaero_auto_cave_mode", 5, new CursorBox("gui.xaero_box_auto_cave_mode"), false, false, false);
        DISPLAY_CAVE_MODE_START = new ModOptions("gui.xaero_wm_display_cave_mode_start", false, false, false);
        CAVE_MODE_TOGGLE_TIMER = new ModOptions("gui.xaero_wm_cave_mode_toggle_timer", 0.0, 10000.0, 100.0, new CursorBox("gui.xaero_wm_box_cave_mode_toggle_timer"), false, false, false);
        DEFAULT_CAVE_MODE_TYPE = new ModOptions("gui.xaero_wm_default_cave_mode_type", 3, new CursorBox("gui.xaero_wm_box_default_cave_mode_type"), false, false, false);
        BIOME_BLENDING = new ModOptions("gui.xaero_wm_biome_blending", new CursorBox("gui.xaero_wm_box_biome_blending"), false, false, false);
        FULL_EXPORT = new ModOptions("gui.xaero_export_option_full", new CursorBox("gui.xaero_box_export_option_full"), false, false, false);
        MULTIPLE_IMAGES_EXPORT = new ModOptions("gui.xaero_export_option_multiple_images", new CursorBox("gui.xaero_box_export_option_multiple_images"), false, false, false);
        NIGHT_EXPORT = new ModOptions("gui.xaero_export_option_nighttime", new CursorBox("gui.xaero_box_export_option_nighttime"), false, false, false);
        EXPORT_SCALE_DOWN_SQUARE = new ModOptions("gui.xaero_export_option_scale_down_square", 0.0, 90.0, 1.0, new CursorBox("gui.xaero_box_export_option_scale_down_square"), false, false, false);
        EXPORT_HIGHLIGHTS = new ModOptions("gui.xaero_export_option_highlights", new CursorBox("gui.xaero_box_export_option_highlights"), false, false, false);
        MAP_WRITING_DISTANCE = new ModOptions("gui.xaero_map_writing_distance", -1.0, 32.0, 1.0, new CursorBox("gui.xaero_box_map_writing_distance"), false, false, false);
        FULL_RELOAD = new ModOptions("gui.xaero_full_reload", new CursorBox("gui.xaero_box_full_reload"), true, false, false);
        FULL_RESAVE = new ModOptions("gui.xaero_full_resave", new CursorBox("gui.xaero_box_full_resave"), true, false, false);
    }

    private ModOptions(String par3Str, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this(par3Str, null, ingameOnly, requiresMinimap, requiresPac);
    }

    private ModOptions(String par3Str, CursorBox tooltip, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this(par3Str, true, () -> Lists.newArrayList((Object[])new Boolean[]{false, true}), tooltip, ingameOnly, requiresMinimap, requiresPac);
    }

    private ModOptions(String par3Str, int optionCount, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this(par3Str, optionCount, null, ingameOnly, requiresMinimap, requiresPac);
    }

    private ModOptions(String par3Str, int optionCount, CursorBox tooltip, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this(par3Str, false, () -> {
            List optionsList = IntStream.rangeClosed(0, optionCount - 1).boxed().collect(Collectors.toList());
            return optionsList;
        }, tooltip, ingameOnly, requiresMinimap, requiresPac);
    }

    private <T> ModOptions(String par3Str, boolean isBoolean, Supplier<List<T>> optionsListSupplier, CursorBox tooltip, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this.enumString = par3Str;
        this.enumBoolean = isBoolean;
        this.enumDouble = false;
        Supplier<Object> valueGetter = () -> WorldMap.settings.getOptionValue(this);
        Consumer<Object> valueSetter = v -> WorldMap.settings.setOptionValue(this, v);
        this.xOption = new XaeroCyclingOption<Object>(this, optionsListSupplier.get(), valueGetter, valueSetter, () -> class_2561.method_43470((String)WorldMap.settings.getOptionValueName(this)));
        this.tooltip = tooltip;
        this.ingameOnly = ingameOnly;
        this.requiresMinimap = requiresMinimap;
    }

    private ModOptions(String p_i45004_3_, double p_i45004_6_, double p_i45004_7_, double p_i45004_8_, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this(p_i45004_3_, p_i45004_6_, p_i45004_7_, p_i45004_8_, null, ingameOnly, requiresMinimap, requiresPac);
    }

    private ModOptions(String p_i45004_3_, double p_i45004_6_, double p_i45004_7_, double p_i45004_8_, CursorBox tooltip, boolean ingameOnly, boolean requiresMinimap, boolean requiresPac) {
        this.enumString = p_i45004_3_;
        this.enumBoolean = false;
        this.enumDouble = true;
        this.valueMin = p_i45004_6_;
        this.valueMax = p_i45004_7_;
        this.valueStep = p_i45004_8_;
        this.xOption = new XaeroDoubleOption(this, p_i45004_6_, p_i45004_7_, (float)p_i45004_8_, () -> WorldMap.settings.getOptionDoubleValue(this), value -> WorldMap.settings.setOptionDoubleValue(this, (double)value), () -> class_2561.method_43470((String)WorldMap.settings.getSliderOptionText(this)));
        this.tooltip = tooltip;
        this.ingameOnly = ingameOnly;
        this.requiresMinimap = requiresMinimap;
        this.requiresPac = requiresPac;
    }

    public boolean getEnumDouble() {
        return this.enumDouble;
    }

    public boolean getEnumBoolean() {
        return this.enumBoolean;
    }

    public double getValueMax() {
        return this.valueMax;
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
        if (this.isDisabledBecauseNotIngame()) {
            return REQUIRES_INGAME;
        }
        if (this.isDisabledBecauseMinimap()) {
            return REQUIRES_MINIMAP;
        }
        return this.tooltip;
    }

    public boolean isIngameOnly() {
        return this.ingameOnly;
    }

    public boolean requiresMinimap() {
        return this.requiresMinimap;
    }

    public boolean requiresPac() {
        return this.requiresPac;
    }

    public boolean isDisabledBecauseNotIngame() {
        return this.isIngameOnly() && !ModSettings.canEditIngameSettings();
    }

    public boolean isDisabledBecauseMinimap() {
        return this.requiresMinimap() && !SupportMods.minimap();
    }

    public double getValueMin() {
        return this.valueMin;
    }

    public boolean isDisabledBecausePac() {
        return this.requiresPac() && !SupportMods.pac();
    }
}

