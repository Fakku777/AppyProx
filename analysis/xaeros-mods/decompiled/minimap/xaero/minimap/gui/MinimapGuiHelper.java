/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.minimap.gui;

import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiHelper;
import xaero.common.gui.GuiMinimapMain;
import xaero.common.gui.GuiSettings;
import xaero.common.gui.MyOptions;

public class MinimapGuiHelper
extends GuiHelper {
    public MinimapGuiHelper(IXaeroMinimap modMain) {
        super(modMain);
    }

    @Override
    public GuiSettings getMainSettingsScreen(class_437 parent) {
        return new GuiMinimapMain(this.modMain, parent, null);
    }

    @Override
    public void onResetCancel(class_437 parent, class_437 escScreen) {
        class_310.method_1551().method_1507((class_437)new GuiMinimapMain(this.modMain, parent, escScreen));
    }

    @Override
    public MyOptions getMyOptions() {
        return new MyOptions("gui.xaero_minimap_settings", this.getMainSettingsScreen(class_310.method_1551().field_1755), null, class_310.method_1551().field_1690);
    }

    @Override
    public void openMainSettingsFromScreen(class_437 parent, class_437 escScreen) {
        this.openMinimapSettingsFromScreen(parent, escScreen);
    }
}

