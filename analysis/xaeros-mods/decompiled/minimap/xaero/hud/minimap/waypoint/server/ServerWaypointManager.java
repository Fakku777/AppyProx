/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntIterable
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package xaero.hud.minimap.waypoint.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import xaero.common.minimap.waypoints.Waypoint;

public class ServerWaypointManager {
    private final Int2ObjectMap<Waypoint> waypoints = new Int2ObjectOpenHashMap();
    private final Object2IntMap<Waypoint> ids = new Object2IntOpenHashMap();
    private final List<Waypoint> list;
    private final IntSet disabled;

    public ServerWaypointManager() {
        this.ids.defaultReturnValue(-1);
        this.list = new ArrayList<Waypoint>();
        this.disabled = new IntOpenHashSet();
    }

    public void clear() {
        this.waypoints.clear();
        this.ids.clear();
        this.list.clear();
    }

    public void remove(int id) {
        Waypoint waypoint = (Waypoint)this.waypoints.remove(id);
        if (waypoint == null) {
            return;
        }
        this.ids.removeInt((Object)waypoint);
        this.list.remove(waypoint);
        this.disabled.remove(id);
    }

    public void add(int id, Waypoint waypoint) {
        Waypoint oldValue;
        int existingId = this.ids.getInt((Object)waypoint);
        if (existingId != -1) {
            this.remove(existingId);
        }
        if ((oldValue = (Waypoint)this.waypoints.put(id, (Object)waypoint)) != null) {
            this.list.remove(oldValue);
            this.ids.removeInt((Object)oldValue);
        }
        waypoint.setDisabled(this.disabled.contains(id));
        this.list.add(waypoint);
        this.ids.put((Object)waypoint, id);
    }

    public Waypoint getById(int id) {
        return (Waypoint)this.waypoints.get(id);
    }

    public Waypoint getBySlot(int slot) {
        return this.list.get(slot);
    }

    public IntIterable getIds() {
        return this.ids.values();
    }

    public void addDisabled(int id) {
        this.disabled.add(id);
    }

    public boolean isEmpty() {
        return this.waypoints.isEmpty();
    }

    public Iterable<Waypoint> getWaypoints() {
        return this.waypoints.values();
    }

    public int size() {
        return this.waypoints.size();
    }
}

