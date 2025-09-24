/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import java.io.IOException;
import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;

public class ZoomFunction
extends KeyMappingFunction {
    private final boolean in;

    protected ZoomFunction(boolean in) {
        super(false);
        this.in = in;
    }

    @Override
    public void onPress() {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession == null) {
            return;
        }
        if (minimapSession.getProcessor().isEnlargedMap() && HudMod.INSTANCE.getSettings().zoomOnEnlarged != 0) {
            return;
        }
        int zoomChange = this.in ? 1 : -1;
        HudMod.INSTANCE.getSettings().changeZoom(zoomChange);
        try {
            HudMod.INSTANCE.getSettings().saveSettings();
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    @Override
    public void onRelease() {
    }
}

