/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class ToggledEnlargeMapFunction
extends KeyMappingFunction {
    protected ToggledEnlargeMapFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        if (!HudMod.INSTANCE.getSettings().enlargedMinimapAToggle) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        session.getProcessor().setEnlargedMap(!session.getProcessor().isEnlargedMap());
        session.getProcessor().setToResetImage(true);
        session.getProcessor().instantZoom();
    }

    @Override
    public void onRelease() {
    }
}

