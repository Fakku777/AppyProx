/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.hud.minimap.controls.key.function;

import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.HudMod;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.ScreenBase;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class WaypointMenuFunction
extends KeyMappingFunction {
    protected WaypointMenuFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        class_310 mc = class_310.method_1551();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (!HudMod.INSTANCE.getSettings().waypointsGUI(session)) {
            return;
        }
        class_437 current = mc.field_1755;
        class_437 currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        mc.method_1507((class_437)new GuiWaypoints(HudMod.INSTANCE, session, current, currentEscScreen));
    }

    @Override
    public void onRelease() {
    }
}

