/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import java.util.ArrayList;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiSettings;
import xaero.common.gui.HudPresetSettingEntry;
import xaero.common.gui.ISettingEntry;
import xaero.hud.preset.HudPreset;

public class GuiChoosePreset
extends GuiSettings {
    public GuiChoosePreset(IXaeroMinimap modMain, class_437 back, class_437 escape) {
        super(modMain, (class_2561)class_2561.method_43471((String)"gui.xaero_choose_a_preset"), back, escape);
        ArrayList<HudPresetSettingEntry> entryList = new ArrayList<HudPresetSettingEntry>();
        for (HudPreset preset : modMain.getHud().getPresetManager().getPresets()) {
            entryList.add(new HudPresetSettingEntry(modMain.getHud().getPresetManager(), preset));
        }
        this.entries = entryList.toArray(new ISettingEntry[0]);
    }
}

