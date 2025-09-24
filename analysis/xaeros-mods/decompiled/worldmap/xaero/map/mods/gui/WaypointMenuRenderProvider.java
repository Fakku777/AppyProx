/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.mods.gui;

import java.util.ArrayList;
import java.util.Iterator;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointMenuRenderContext;

public class WaypointMenuRenderProvider
extends ElementRenderProvider<Waypoint, WaypointMenuRenderContext> {
    private final SupportXaeroMinimap minimap;
    private Iterator<Waypoint> iterator;

    public WaypointMenuRenderProvider(SupportXaeroMinimap minimap) {
        this.minimap = minimap;
    }

    @Override
    public void begin(ElementRenderLocation location, WaypointMenuRenderContext context) {
        ArrayList<Waypoint> sortedList = this.minimap.getWaypointsSorted();
        this.iterator = sortedList == null ? null : this.minimap.getWaypointsSorted().iterator();
    }

    @Override
    public boolean hasNext(ElementRenderLocation location, WaypointMenuRenderContext context) {
        return this.iterator != null && this.iterator.hasNext();
    }

    @Override
    public Waypoint getNext(ElementRenderLocation location, WaypointMenuRenderContext context) {
        return this.iterator.next();
    }

    @Override
    public void end(ElementRenderLocation location, WaypointMenuRenderContext context) {
    }
}

