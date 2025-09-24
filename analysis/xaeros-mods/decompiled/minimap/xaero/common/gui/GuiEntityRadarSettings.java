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
import xaero.common.gui.GuiEntityRadarCategoryEditor;
import xaero.common.gui.GuiMinimapSettings;
import xaero.common.gui.ISettingEntry;
import xaero.common.gui.ScreenSwitchSettingEntry;
import xaero.common.settings.ModOptions;

public class GuiEntityRadarSettings
extends GuiMinimapSettings {
    public GuiEntityRadarSettings(IXaeroMinimap modMain, class_437 backScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_entity_radar_settings"), backScreen, escScreen);
        CursorBox tooltip = new CursorBox("gui.xaero_box_entity_radar_categories");
        ScreenSwitchSettingEntry entityRadarEntry = new ScreenSwitchSettingEntry("gui.xaero_entity_radar_categories", (current, escape) -> new GuiEntityRadarCategoryEditor(modMain, (class_437)current, (class_437)escape), tooltip, true);
        this.entries = new ISettingEntry[]{entityRadarEntry, new ConfigSettingEntry(ModOptions.RADAR_DISPLAYED), new ConfigSettingEntry(ModOptions.DOTS_SIZE), new ConfigSettingEntry(ModOptions.DOTS_STYLE), new ConfigSettingEntry(ModOptions.SMOOTH_DOTS), new ConfigSettingEntry(ModOptions.RADAR_ICONS_DISPLAYED), new ConfigSettingEntry(ModOptions.RADAR_NAMES_DISPLAYED), new ConfigSettingEntry(ModOptions.HEADS_SCALE), new ConfigSettingEntry(ModOptions.DOT_NAME_SCALE), new ConfigSettingEntry(ModOptions.ENTITY_NAMETAGS), new ConfigSettingEntry(ModOptions.ICON_NAME_FALLBACK), new ConfigSettingEntry(ModOptions.EAMOUNT), new ConfigSettingEntry(ModOptions.HEIGHT_LIMIT), new ConfigSettingEntry(ModOptions.ENTITY_HEIGHT), new ConfigSettingEntry(ModOptions.START_FADING_AT), new ConfigSettingEntry(ModOptions.RADAR_Y_DISPLAYED), new ConfigSettingEntry(ModOptions.MAIN_ENTITY_AS), new ConfigSettingEntry(ModOptions.MAIN_DOT_SIZE), new ConfigSettingEntry(ModOptions.ARROW_SCALE), new ConfigSettingEntry(ModOptions.ARROW_COLOUR), new ConfigSettingEntry(ModOptions.PLAYER_ARROW_OPACITY), new ConfigSettingEntry(ModOptions.RADAR_OVER_FRAME), new ConfigSettingEntry(ModOptions.TRACKED_PLAYERS_ON_MAP), new ConfigSettingEntry(ModOptions.TRACKED_PLAYERS_IN_WORLD), new ConfigSettingEntry(ModOptions.TRACKED_PLAYER_MINIMAP_ICON_SCALE), new ConfigSettingEntry(ModOptions.TRACKED_PLAYER_WORLD_ICON_SCALE), new ConfigSettingEntry(ModOptions.TRACKED_PLAYER_WORLD_NAME_SCALE)};
    }
}

