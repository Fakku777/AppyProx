/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiMinimapMain;
import xaero.common.gui.GuiSettings;
import xaero.common.gui.MyOptions;
import xaero.common.gui.ScreenBase;
import xaero.common.settings.ModOptions;

public abstract class GuiHelper {
    protected IXaeroMinimap modMain;

    public GuiHelper(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public void openSettingsGui(ModOptions returnModOptions) {
        class_437 current = class_310.method_1551().field_1755;
        class_437 currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
    }

    public void openMinimapSettingsFromScreen(class_437 parent, class_437 escScreen) {
        class_310.method_1551().method_1507((class_437)new GuiMinimapMain(this.modMain, parent, escScreen));
    }

    public abstract GuiSettings getMainSettingsScreen(class_437 var1);

    public abstract void onResetCancel(class_437 var1, class_437 var2);

    public abstract MyOptions getMyOptions();

    public abstract void openMainSettingsFromScreen(class_437 var1, class_437 var2);
}

