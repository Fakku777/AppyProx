/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint;

import java.util.List;
import xaero.common.HudMod;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorldManager;

public class WaypointCollector {
    private final MinimapSession session;

    public WaypointCollector(MinimapSession session) {
        this.session = session;
    }

    public void collect(List<Waypoint> destination) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorldManager manager = session.getWorldManager();
        if (manager.getCurrentWorld() != null) {
            if (HudMod.INSTANCE.getSettings().renderAllSets) {
                for (WaypointSet set : manager.getCurrentWorld().getIterableWaypointSets()) {
                    set.addTo(destination);
                }
            } else {
                manager.getCurrentWorld().getCurrentWaypointSet().addTo(destination);
            }
        }
        if (!manager.hasCustomWaypoints()) {
            return;
        }
        for (Waypoint waypoint : manager.getCustomWaypoints()) {
            destination.add(waypoint);
        }
    }
}

