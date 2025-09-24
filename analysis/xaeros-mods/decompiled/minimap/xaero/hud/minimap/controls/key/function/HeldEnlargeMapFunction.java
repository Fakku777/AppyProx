/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class HeldEnlargeMapFunction
extends KeyMappingFunction {
    private boolean active;

    protected HeldEnlargeMapFunction() {
        super(true);
    }

    @Override
    public void onPress() {
        if (HudMod.INSTANCE.getSettings().enlargedMinimapAToggle) {
            return;
        }
        if (this.active) {
            return;
        }
        this.active = true;
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        session.getProcessor().setEnlargedMap(true);
        session.getProcessor().setToResetImage(true);
        session.getProcessor().instantZoom();
    }

    @Override
    public void onRelease() {
        this.active = false;
        if (HudMod.INSTANCE.getSettings().enlargedMinimapAToggle) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        session.getProcessor().setEnlargedMap(false);
        session.getProcessor().setToResetImage(true);
        session.getProcessor().instantZoom();
    }
}

