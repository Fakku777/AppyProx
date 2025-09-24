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

public class GuiMinimapMiscSettings
extends GuiMinimapSettings {
    public GuiMinimapMiscSettings(IXaeroMinimap modMain, class_437 backScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_minimap_misc_settings"), backScreen, escScreen);
        this.entries = new ISettingEntry[]{new ConfigSettingEntry(ModOptions.SAFE_MAP), new ConfigSettingEntry(ModOptions.UPDATE_NOTIFICATION), new ConfigSettingEntry(ModOptions.UI_SCALE)};
    }
}

