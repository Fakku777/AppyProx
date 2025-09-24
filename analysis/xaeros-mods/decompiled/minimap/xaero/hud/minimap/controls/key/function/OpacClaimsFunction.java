/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.common.HudMod;
import xaero.common.settings.ModOptions;
import xaero.hud.controls.key.function.KeyMappingFunction;

public class OpacClaimsFunction
extends KeyMappingFunction {
    protected OpacClaimsFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        HudMod modMain = HudMod.INSTANCE;
        if (modMain.getSupportMods().worldmap() && modMain.getSupportMods().shouldUseWorldMapChunks()) {
            modMain.getSupportMods().worldmapSupport.toggleChunkClaims();
            return;
        }
        modMain.getSettings().toggleBooleanOptionValue(ModOptions.PAC_CLAIMS);
    }

    @Override
    public void onRelease() {
    }
}

