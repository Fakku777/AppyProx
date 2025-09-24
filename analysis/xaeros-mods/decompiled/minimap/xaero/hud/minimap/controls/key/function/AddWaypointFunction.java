/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package xaero.hud.minimap.controls.key.function;

import com.google.common.collect.Lists;
import net.minecraft.class_310;
import net.minecraft.class_437;
import xaero.common.HudMod;
import xaero.common.gui.GuiAddWaypoint;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class AddWaypointFunction
extends KeyMappingFunction {
    protected AddWaypointFunction() {
        super(false);
    }

    @Override
    public void onPress() {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (!HudMod.INSTANCE.getSettings().waypointsGUI(session)) {
            return;
        }
        class_310.method_1551().method_1507((class_437)new GuiAddWaypoint(HudMod.INSTANCE, session, null, Lists.newArrayList(), session.getWorldState().getCurrentWorldPath().getRoot(), session.getWorldManager().getCurrentWorld(), true));
    }

    @Override
    public void onRelease() {
    }
}

