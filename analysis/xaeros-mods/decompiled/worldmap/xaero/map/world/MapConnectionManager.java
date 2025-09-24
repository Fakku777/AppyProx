/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_5321
 *  net.minecraft.class_7924
 */
package xaero.map.world;

import java.io.PrintWriter;
import java.lang.invoke.CallSite;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import xaero.map.world.MapConnectionNode;

public class MapConnectionManager {
    private Map<MapConnectionNode, Set<MapConnectionNode>> allConnections = new HashMap<MapConnectionNode, Set<MapConnectionNode>>();

    public void addConnection(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
        this.addOneWayConnection(mapKey1, mapKey2);
        this.addOneWayConnection(mapKey2, mapKey1);
    }

    private void addOneWayConnection(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
        Set<MapConnectionNode> connections = this.allConnections.get(mapKey1);
        if (connections == null) {
            connections = new HashSet<MapConnectionNode>();
            this.allConnections.put(mapKey1, connections);
        }
        connections.add(mapKey2);
    }

    public void removeConnection(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
        this.removeOneWayConnection(mapKey1, mapKey2);
        this.removeOneWayConnection(mapKey2, mapKey1);
    }

    private void removeOneWayConnection(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
        Set<MapConnectionNode> connections = this.allConnections.get(mapKey1);
        if (connections == null) {
            return;
        }
        connections.remove(mapKey2);
    }

    public boolean isConnected(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
        if (mapKey1 == null || mapKey2 == null) {
            return false;
        }
        if (mapKey1.equals(mapKey2)) {
            return true;
        }
        Set<MapConnectionNode> connections = this.allConnections.get(mapKey1);
        if (connections == null) {
            return false;
        }
        return connections.contains(mapKey2);
    }

    public boolean isEmpty() {
        return this.allConnections.isEmpty();
    }

    public void save(PrintWriter writer) {
        if (!this.allConnections.isEmpty()) {
            HashSet<CallSite> redundantConnections = new HashSet<CallSite>();
            for (Map.Entry<MapConnectionNode, Set<MapConnectionNode>> entry : this.allConnections.entrySet()) {
                MapConnectionNode mapKey = entry.getKey();
                Set<MapConnectionNode> connections = entry.getValue();
                for (MapConnectionNode c : connections) {
                    String fullConnection = String.valueOf(mapKey) + ":" + String.valueOf(c);
                    if (redundantConnections.contains(fullConnection)) continue;
                    writer.println("connection:" + fullConnection);
                    redundantConnections.add((CallSite)((Object)(String.valueOf(c) + ":" + String.valueOf(mapKey))));
                }
            }
        }
    }

    private void swapConnections(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
        HashSet connections1 = new HashSet(this.allConnections.getOrDefault(mapKey1, new HashSet()));
        HashSet connections2 = new HashSet(this.allConnections.getOrDefault(mapKey2, new HashSet()));
        for (MapConnectionNode c : connections1) {
            this.removeConnection(mapKey1, c);
        }
        for (MapConnectionNode c : connections2) {
            this.addConnection(mapKey1, c);
        }
        for (MapConnectionNode c : connections2) {
            this.removeConnection(mapKey2, c);
        }
        for (MapConnectionNode c : connections1) {
            this.addConnection(mapKey2, c);
        }
    }

    public void renameDimension(String oldName, String newName) {
        HashSet<MapConnectionNode> keysCopy = new HashSet<MapConnectionNode>(this.allConnections.keySet());
        for (MapConnectionNode mapKey : keysCopy) {
            if (!mapKey.getDimId().method_29177().toString().equals(oldName)) continue;
            String mwPart = mapKey.getMw();
            this.swapConnections(mapKey, new MapConnectionNode((class_5321<class_1937>)class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)class_2960.method_60654((String)newName)), mwPart));
        }
    }
}

