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
import xaero.common.gui.GuiInfoDisplayEdit;
import xaero.common.gui.GuiMinimapSettings;
import xaero.common.gui.ISettingEntry;
import xaero.common.gui.ScreenSwitchSettingEntry;
import xaero.common.settings.ModOptions;

public class GuiMinimapInfoSettings
extends GuiMinimapSettings {
    public GuiMinimapInfoSettings(IXaeroMinimap modMain, class_437 backScreen, class_437 escScreen) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_minimap_info_settings"), backScreen, escScreen);
        CursorBox tooltip = new CursorBox("gui.xaero_box_minimap_info_display_manager");
        ScreenSwitchSettingEntry newInfoSettingsEntry = new ScreenSwitchSettingEntry("gui.xaero_minimap_info_display_manager", (current, escape) -> new GuiInfoDisplayEdit(modMain, (class_437)current, (class_437)escape), tooltip, true);
        this.entries = new ISettingEntry[]{newInfoSettingsEntry, new ConfigSettingEntry(ModOptions.INFO_DISPLAY_BG_OPACITY), new ConfigSettingEntry(ModOptions.MINIMAP_TEXT_ALIGN), new ConfigSettingEntry(ModOptions.PAC_CURRENT_CLAIM)};
    }
}

