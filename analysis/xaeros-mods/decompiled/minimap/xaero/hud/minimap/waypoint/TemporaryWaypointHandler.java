/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package xaero.hud.minimap.waypoint;

import net.minecraft.class_310;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.misc.OptimizedMath;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.world.MinimapWorld;

public class TemporaryWaypointHandler {
    private final HudMod modMain;

    public TemporaryWaypointHandler(HudMod modMain) {
        this.modMain = modMain;
    }

    public void createTemporaryWaypoint(MinimapWorld minimapWorld, int x, int y, int z) {
        this.createTemporaryWaypoint(minimapWorld, x, y, z, true);
    }

    public void createTemporaryWaypoint(MinimapWorld minimapWorld, int x, int y, int z, boolean yIncluded) {
        this.createTemporaryWaypoint(minimapWorld, x, y, z, yIncluded, class_310.method_1551().field_1687.method_8597().comp_646());
    }

    public void createTemporaryWaypoint(MinimapWorld minimapWorld, int x, int y, int z, boolean yIncluded, double dimScale) {
        if (minimapWorld == null) {
            return;
        }
        MinimapSession session = minimapWorld.getContainer().getSession();
        if (!this.modMain.getSettings().waypointsGUI(session)) {
            return;
        }
        double waypointDestDimScale = session.getDimensionHelper().getDimCoordinateScale(minimapWorld);
        double dimDiv = dimScale / waypointDestDimScale;
        x = OptimizedMath.myFloor((double)x * dimDiv);
        z = OptimizedMath.myFloor((double)z * dimDiv);
        Waypoint instant = new Waypoint(x, y, z, "Waypoint", "X", WaypointColor.getRandom(), WaypointPurpose.NORMAL, true, yIncluded);
        minimapWorld.getCurrentWaypointSet().add(instant, !this.modMain.getSettings().waypointsBottom);
    }
}

