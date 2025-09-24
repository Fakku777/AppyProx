/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_2561;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.ConfigSettingEntry;
import xaero.common.gui.GuiDefaultTpCommand;
import xaero.common.gui.GuiMinimapSettings;
import xaero.common.gui.ISettingEntry;
import xaero.common.gui.ScreenSwitchSettingEntry;
import xaero.common.settings.ModOptions;

public class GuiWaypointSettings
extends GuiMinimapSettings {
    public GuiWaypointSettings(IXaeroMinimap modMain, class_437 backScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_waypoint_settings"), backScreen, escScreen);
        ScreenSwitchSettingEntry waypointDefaultTPEntry = new ScreenSwitchSettingEntry("gui.xaero_teleport_default_command", (current, escape) -> new GuiDefaultTpCommand(modMain, (class_437)current, (class_437)escape), new CursorBox("gui.xaero_box_teleport_default_command"), true);
        this.entries = new ISettingEntry[]{new ConfigSettingEntry(ModOptions.WAYPOINTS), new ConfigSettingEntry(ModOptions.INGAME_WAYPOINTS), new ConfigSettingEntry(ModOptions.DEATHPOINTS), new ConfigSettingEntry(ModOptions.OLD_DEATHPOINTS), new ConfigSettingEntry(ModOptions.DELETE_REACHED_DEATHPOINTS), new ConfigSettingEntry(ModOptions.SWITCH_TO_AUTO_ON_DEATH), new ConfigSettingEntry(ModOptions.WAYPOINTS_ICON_SCALE), new ConfigSettingEntry(ModOptions.WAYPOINTS_NAME_SCALE), new ConfigSettingEntry(ModOptions.WAYPOINTS_CLOSE_SCALE), new ConfigSettingEntry(ModOptions.WAYPOINTS_DISTANCE_SCALE), new ConfigSettingEntry(ModOptions.MULTIPLE_WAYPOINT_INFO), new ConfigSettingEntry(ModOptions.DISTANCE), new ConfigSettingEntry(ModOptions.KEEP_WP_NAMES), new ConfigSettingEntry(ModOptions.ALWAYS_SHOW_DISTANCE), new ConfigSettingEntry(ModOptions.WAYPOINTS_ALL_SETS), new ConfigSettingEntry(ModOptions.WAYPOINT_VERTICAL_LOOKING_ANGLE), new ConfigSettingEntry(ModOptions.WAYPOINTS_DISTANCE), new ConfigSettingEntry(ModOptions.SCALED_MAX_WAYPOINT_DISTANCE), new ConfigSettingEntry(ModOptions.WAYPOINTS_DISTANCE_MIN), new ConfigSettingEntry(ModOptions.WAYPOINT_LOOKING_ANGLE), new ConfigSettingEntry(ModOptions.AUTO_CONVERT_TO_KM), new ConfigSettingEntry(ModOptions.WP_DISTANCE_PRECISION), new ConfigSettingEntry(ModOptions.WAYPOINT_OPACITY_INGAME), new ConfigSettingEntry(ModOptions.WAYPOINT_OPACITY_MAP), new ConfigSettingEntry(ModOptions.WAYPOINT_ONMAP_SCALE), new ConfigSettingEntry(ModOptions.TEMPORARY_WAYPOINTS_GLOBAL), waypointDefaultTPEntry, new ConfigSettingEntry(ModOptions.CROSS_DIMENSIONAL_TP), new ConfigSettingEntry(ModOptions.PARTIAL_Y_TELEPORTATION), new ConfigSettingEntry(ModOptions.WAYPOINTS_BOTTOM), new ConfigSettingEntry(ModOptions.HIDE_WORLD_NAMES), new ConfigSettingEntry(ModOptions.HIDE_WP_COORDS), new ConfigSettingEntry(ModOptions.COMPASS), new ConfigSettingEntry(ModOptions.COMPASS_LOCATION), new ConfigSettingEntry(ModOptions.COMPASS_COLOR), new ConfigSettingEntry(ModOptions.COMPASS_SCALE), new ConfigSettingEntry(ModOptions.NORTH_COMPASS_COLOR)};
    }
}

