/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 *  net.minecraft.class_4185
 */
package xaero.common.settings;

import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import xaero.common.IXaeroMinimap;
import xaero.common.settings.ModOptions;
import xaero.common.settings.Option;

public class ScreenEntranceOption
extends Option {
    private IXaeroMinimap modMain;

    public ScreenEntranceOption(String key, IXaeroMinimap modMain, ModOptions option) {
        super(option);
        this.modMain = modMain;
    }

    @Override
    public class_339 createButton(int x, int y, int width) {
        return class_4185.method_46430((class_2561)this.getCaption(), b -> this.modMain.getGuiHelper().openSettingsGui(this.option)).method_46434(x, y, width, 20).method_46431();
    }
}

