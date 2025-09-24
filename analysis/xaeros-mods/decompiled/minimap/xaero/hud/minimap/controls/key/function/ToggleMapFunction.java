/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 */
package xaero.hud.minimap.controls.key.function;

import net.minecraft.class_1657;
import net.minecraft.class_310;
import xaero.common.HudMod;
import xaero.common.effect.Effects;
import xaero.common.misc.Misc;
import xaero.common.settings.ModOptions;
import xaero.hud.controls.key.function.KeyMappingFunction;

public class ToggleMapFunction
extends KeyMappingFunction {
    protected ToggleMapFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        class_310 mc = class_310.method_1551();
        if (Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_MINIMAP) || Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_MINIMAP_HARMFUL)) {
            return;
        }
        HudMod.INSTANCE.getSettings().toggleBooleanOptionValue(ModOptions.MINIMAP);
    }

    @Override
    public void onRelease() {
    }
}

