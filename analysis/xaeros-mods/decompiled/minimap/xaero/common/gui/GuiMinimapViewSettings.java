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

public class GuiMinimapViewSettings
extends GuiMinimapSettings {
    public GuiMinimapViewSettings(IXaeroMinimap modMain, class_437 backScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_minimap_view_settings"), backScreen, escScreen);
        this.entries = new ISettingEntry[]{new ConfigSettingEntry(ModOptions.SIZE), new ConfigSettingEntry(ModOptions.MINIMAP_SHAPE), new ConfigSettingEntry(ModOptions.NORTH), new ConfigSettingEntry(ModOptions.LIGHT), new ConfigSettingEntry(ModOptions.ZOOM), new ConfigSettingEntry(ModOptions.CAVE_ZOOM), new ConfigSettingEntry(ModOptions.OPACITY), new ConfigSettingEntry(ModOptions.MINIMAP_FRAME), new ConfigSettingEntry(ModOptions.ZOOM_ON_ENLARGE), new ConfigSettingEntry(ModOptions.MINIMAP_FRAME_COLOR), new ConfigSettingEntry(ModOptions.CENTERED_ENLARGED), new ConfigSettingEntry(ModOptions.KEEP_ENLARGED_UNLOCKED), new ConfigSettingEntry(ModOptions.TOGGLED_ENLARGED), new ConfigSettingEntry(ModOptions.HIDE_MINIMAP_UNDER_SCREEN), new ConfigSettingEntry(ModOptions.HIDE_MINIMAP_UNDER_F3), new ConfigSettingEntry(ModOptions.BOSS_HEALTH_PUSHBOX), new ConfigSettingEntry(ModOptions.POTION_EFFECTS_PUSHBOX)};
    }
}

