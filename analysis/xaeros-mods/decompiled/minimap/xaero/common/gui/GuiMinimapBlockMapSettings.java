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
import xaero.common.gui.ConfigSettingEntry;
import xaero.common.gui.GuiMinimapSettings;
import xaero.common.gui.ISettingEntry;
import xaero.common.settings.ModOptions;

public class GuiMinimapBlockMapSettings
extends GuiMinimapSettings {
    public GuiMinimapBlockMapSettings(IXaeroMinimap modMain, class_437 backScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_minimap_block_map_settings"), backScreen, escScreen);
        this.entries = new ISettingEntry[]{new ConfigSettingEntry(ModOptions.COLOURS), new ConfigSettingEntry(ModOptions.BIOMES_VANILLA), new ConfigSettingEntry(ModOptions.CAVE_MAPS), new ConfigSettingEntry(ModOptions.BIOME_BLENDING), new ConfigSettingEntry(ModOptions.CAVE_MAPS_DEPTH), new ConfigSettingEntry(ModOptions.MANUAL_CAVE_MODE_START), new ConfigSettingEntry(ModOptions.LEGIBLE_CAVE_MAPS), new ConfigSettingEntry(ModOptions.CAVE_MODE_TOGGLE_TIMER), new ConfigSettingEntry(ModOptions.WORLD_MAP), new ConfigSettingEntry(ModOptions.TERRAIN_DEPTH), new ConfigSettingEntry(ModOptions.REDSTONE), new ConfigSettingEntry(ModOptions.TERRAIN_SLOPES), new ConfigSettingEntry(ModOptions.FLOWERS), new ConfigSettingEntry(ModOptions.BLOCK_TRANSPARENCY), new ConfigSettingEntry(ModOptions.DISPLAY_STAINED_GLASS), new ConfigSettingEntry(ModOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS), new ConfigSettingEntry(ModOptions.IGNORE_HEIGHTMAPS), new ConfigSettingEntry(ModOptions.AA)};
    }
}

