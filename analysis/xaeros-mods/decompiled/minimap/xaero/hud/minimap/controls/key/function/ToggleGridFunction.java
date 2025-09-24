/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import java.io.IOException;
import xaero.common.HudMod;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.MinimapLogs;

public class ToggleGridFunction
extends KeyMappingFunction {
    protected ToggleGridFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        HudMod modMain = HudMod.INSTANCE;
        modMain.getSettings().chunkGrid = -modMain.getSettings().chunkGrid - 1;
        try {
            modMain.getSettings().saveSettings();
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    @Override
    public void onRelease() {
    }
}

