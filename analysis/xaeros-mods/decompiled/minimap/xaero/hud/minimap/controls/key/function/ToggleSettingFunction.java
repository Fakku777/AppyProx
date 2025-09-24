/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import java.util.function.Supplier;
import xaero.common.HudMod;
import xaero.common.settings.ModOptions;
import xaero.hud.controls.key.function.KeyMappingFunction;

public class ToggleSettingFunction
extends KeyMappingFunction {
    private final Supplier<ModOptions> settingSupplier;

    protected ToggleSettingFunction(Supplier<ModOptions> settingSupplier) {
        super(false);
        this.settingSupplier = settingSupplier;
    }

    @Override
    public void onPress() {
        HudMod.INSTANCE.getSettings().toggleBooleanOptionValue(this.settingSupplier.get());
    }

    @Override
    public void onRelease() {
    }
}

