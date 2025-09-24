/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.hud.xminimap.controls.key.function;

import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.HudMod;
import xaero.common.gui.GuiMinimapMain;
import xaero.common.gui.ScreenBase;
import xaero.hud.controls.key.function.KeyMappingFunction;

public class MinimapSettingsFunction
extends KeyMappingFunction {
    protected MinimapSettingsFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        class_437 current = class_310.method_1551().field_1755;
        class_437 currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        class_310.method_1551().method_1507((class_437)new GuiMinimapMain(HudMod.INSTANCE, current, currentEscScreen));
    }

    @Override
    public void onRelease() {
    }
}

