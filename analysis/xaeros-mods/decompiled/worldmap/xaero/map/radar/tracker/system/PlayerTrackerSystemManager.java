/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.radar.tracker.system;

import java.util.HashMap;
import java.util.Map;
import xaero.map.WorldMap;
import xaero.map.radar.tracker.system.IPlayerTrackerSystem;

public class PlayerTrackerSystemManager {
    private final Map<String, IPlayerTrackerSystem<?>> systems = new HashMap();

    public void register(String name, IPlayerTrackerSystem<?> system) {
        if (this.systems.containsKey(name)) {
            WorldMap.LOGGER.error("Player tracker system with the name " + name + " has already been registered!");
            return;
        }
        this.systems.put(name, system);
        WorldMap.LOGGER.info("Registered player tracker system: " + name);
    }

    public Iterable<IPlayerTrackerSystem<?>> getSystems() {
        return this.systems.values();
    }
}

