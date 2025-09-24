/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.radar.tracker;

import java.util.Iterator;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.element.render.ElementRenderProvider;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.radar.tracker.PlayerTrackerMapElementCollector;

public class PlayerTrackerMapElementRenderProvider<C>
extends ElementRenderProvider<PlayerTrackerMapElement<?>, C> {
    private PlayerTrackerMapElementCollector collector;
    private Iterator<PlayerTrackerMapElement<?>> iterator;

    public PlayerTrackerMapElementRenderProvider(PlayerTrackerMapElementCollector collector) {
        this.collector = collector;
    }

    @Override
    public void begin(ElementRenderLocation location, C context) {
        this.iterator = this.collector.getElements().iterator();
    }

    @Override
    public boolean hasNext(ElementRenderLocation location, C context) {
        return this.iterator != null && this.iterator.hasNext();
    }

    @Override
    public PlayerTrackerMapElement<?> getNext(ElementRenderLocation location, C context) {
        return this.iterator.next();
    }

    @Override
    public void end(ElementRenderLocation location, C context) {
        this.iterator = null;
    }
}

