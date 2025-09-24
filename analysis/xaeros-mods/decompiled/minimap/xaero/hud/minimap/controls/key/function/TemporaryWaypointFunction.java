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
import xaero.common.effect.Effects;
import xaero.common.misc.Misc;
import xaero.common.misc.OptimizedMath;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class TemporaryWaypointFunction
extends KeyMappingFunction {
    protected TemporaryWaypointFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        class_310 mc = class_310.method_1551();
        if (Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_WAYPOINTS) || Misc.hasEffect((class_1657)mc.field_1724, Effects.NO_WAYPOINTS_HARMFUL)) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        session.getWaypointSession().getTemporaryHandler().createTemporaryWaypoint(session.getWorldManager().getCurrentWorld(), OptimizedMath.myFloor(mc.field_1719.method_23317()), OptimizedMath.myFloor(mc.field_1719.method_23318() + 0.0625), OptimizedMath.myFloor(mc.field_1719.method_23321()));
    }

    @Override
    public void onRelease() {
    }
}

