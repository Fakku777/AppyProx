/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  xaero.common.XaeroMinimapSession
 *  xaero.common.minimap.mcworld.MinimapClientWorldData
 *  xaero.common.minimap.mcworld.MinimapClientWorldDataHelper
 *  xaero.common.server.radar.tracker.SyncedTrackedPlayer
 *  xaero.hud.minimap.player.tracker.synced.ClientSyncedTrackedPlayerManager
 */
package xaero.map.mods.minimap.tracker.system;

import java.util.Iterator;
import net.minecraft.class_310;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.mcworld.MinimapClientWorldData;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;
import xaero.common.server.radar.tracker.SyncedTrackedPlayer;
import xaero.hud.minimap.player.tracker.synced.ClientSyncedTrackedPlayerManager;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.minimap.tracker.system.MinimapSyncedTrackedPlayerReader;
import xaero.map.radar.tracker.system.IPlayerTrackerSystem;
import xaero.map.radar.tracker.system.ITrackedPlayerReader;

public class MinimapSyncedPlayerTrackerSystem
implements IPlayerTrackerSystem<SyncedTrackedPlayer> {
    private final MinimapSyncedTrackedPlayerReader reader;

    public MinimapSyncedPlayerTrackerSystem(SupportXaeroMinimap minimapSupport) {
        this.reader = new MinimapSyncedTrackedPlayerReader(minimapSupport);
    }

    @Override
    public ITrackedPlayerReader<SyncedTrackedPlayer> getReader() {
        return this.reader;
    }

    @Override
    public Iterator<SyncedTrackedPlayer> getTrackedPlayerIterator() {
        XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
        if (session == null) {
            return null;
        }
        if (class_310.method_1551().method_1576() == null) {
            MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
            if (worldData.serverLevelId == null) {
                return null;
            }
        }
        ClientSyncedTrackedPlayerManager manager = session.getMinimapProcessor().getSyncedTrackedPlayerManager();
        return manager.getPlayers().iterator();
    }
}

