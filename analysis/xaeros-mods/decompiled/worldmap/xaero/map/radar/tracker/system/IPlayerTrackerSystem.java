/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.radar.tracker.system;

import java.util.Iterator;
import xaero.map.radar.tracker.system.ITrackedPlayerReader;

public interface IPlayerTrackerSystem<P> {
    public ITrackedPlayerReader<P> getReader();

    public Iterator<P> getTrackedPlayerIterator();
}

