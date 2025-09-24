/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 */
package xaero.map.radar.tracker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import xaero.map.radar.tracker.PlayerTrackerMapElement;
import xaero.map.radar.tracker.system.IPlayerTrackerSystem;
import xaero.map.radar.tracker.system.ITrackedPlayerReader;
import xaero.map.radar.tracker.system.PlayerTrackerSystemManager;

public class PlayerTrackerMapElementCollector {
    private Map<UUID, PlayerTrackerMapElement<?>> elements = new HashMap();
    private final PlayerTrackerSystemManager systemManager;
    private final Runnable onElementsChange;

    public PlayerTrackerMapElementCollector(PlayerTrackerSystemManager systemManager, Runnable onElementsChange) {
        this.systemManager = systemManager;
        this.onElementsChange = onElementsChange;
    }

    public void update(class_310 mc) {
        if (this.elements == null) {
            this.elements = new HashMap();
        }
        HashMap updatedMap = new HashMap();
        boolean hasNewPlayer = false;
        for (IPlayerTrackerSystem<?> system : this.systemManager.getSystems()) {
            hasNewPlayer = this.updateForSystem(system, updatedMap, this.elements) || hasNewPlayer;
        }
        if (hasNewPlayer || updatedMap.size() != this.elements.size()) {
            this.elements = updatedMap;
            this.onElementsChange.run();
        }
    }

    private <P> boolean updateForSystem(IPlayerTrackerSystem<P> system, Map<UUID, PlayerTrackerMapElement<?>> destination, Map<UUID, PlayerTrackerMapElement<?>> current) {
        Iterator<P> playerIterator = system.getTrackedPlayerIterator();
        if (playerIterator == null) {
            return false;
        }
        ITrackedPlayerReader<P> reader = system.getReader();
        boolean hasNewPlayer = false;
        while (playerIterator.hasNext()) {
            P player = playerIterator.next();
            UUID playerId = reader.getId(player);
            PlayerTrackerMapElement<Object> element = current.get(playerId);
            if (destination.containsKey(playerId)) continue;
            if (element == null || element.getPlayer() != player) {
                element = new PlayerTrackerMapElement<P>(player, system);
                hasNewPlayer = true;
            }
            destination.put(element.getPlayerId(), element);
        }
        return hasNewPlayer;
    }

    public boolean playerExists(UUID id) {
        return this.elements != null && this.elements.containsKey(id);
    }

    public Iterable<PlayerTrackerMapElement<?>> getElements() {
        return this.elements.values();
    }

    public void resetRenderedOnRadarFlags() {
        for (PlayerTrackerMapElement<?> e : this.elements.values()) {
            e.setRenderedOnRadar(false);
        }
    }

    public void confirmPlayerRadarRender(class_1657 p) {
        this.elements.get(p.method_5667()).setRenderedOnRadar(true);
    }
}

