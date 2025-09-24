/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.mods.gui;

import java.util.Iterator;
import xaero.map.WorldMap;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointRenderContext;

public class WaypointRenderProvider
extends ElementRenderProvider<Waypoint, WaypointRenderContext> {
    private final SupportXaeroMinimap minimap;
    private Iterator<Waypoint> iterator;

    public WaypointRenderProvider(SupportXaeroMinimap minimap) {
        this.minimap = minimap;
    }

    @Override
    public void begin(ElementRenderLocation location, WaypointRenderContext context) {
        if (!WorldMap.settings.waypoints || this.minimap.getWaypoints() == null) {
            this.iterator = null;
            return;
        }
        this.iterator = this.minimap.getWaypoints().iterator();
        context.worldmapWaypointsScale = WorldMap.settings.worldmapWaypointsScale;
    }

    @Override
    public boolean hasNext(ElementRenderLocation location, WaypointRenderContext context) {
        return this.iterator != null && this.iterator.hasNext();
    }

    @Override
    public Waypoint getNext(ElementRenderLocation location, WaypointRenderContext context) {
        return this.iterator.next();
    }

    @Override
    public void end(ElementRenderLocation location, WaypointRenderContext context) {
    }
}

