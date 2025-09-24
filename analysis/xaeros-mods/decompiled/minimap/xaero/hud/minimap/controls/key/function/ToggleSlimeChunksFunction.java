/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.hud.minimap.controls.key.function;

import java.io.IOException;
import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiSlimeSeed;
import xaero.common.gui.ScreenBase;
import xaero.common.settings.ModOptions;
import xaero.hud.HudSession;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;

public class ToggleSlimeChunksFunction
extends KeyMappingFunction {
    protected ToggleSlimeChunksFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        HudMod modMain = HudMod.INSTANCE;
        HudSession hudSession = HudSession.getCurrentSession();
        if (modMain.getSettings().customSlimeSeedNeeded(hudSession) && modMain.getSettings().getBooleanValue(ModOptions.OPEN_SLIME_SETTINGS)) {
            class_437 current = class_310.method_1551().field_1755;
            class_437 currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
            MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
            class_310.method_1551().method_1507((class_437)new GuiSlimeSeed((IXaeroMinimap)modMain, session, current, currentEscScreen));
            return;
        }
        modMain.getSettings().slimeChunks = !modMain.getSettings().slimeChunks;
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

