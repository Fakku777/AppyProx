/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.server.mods.ftbteams;

import xaero.map.server.mods.ftbteams.FTBTeamsSyncedPlayerTrackerSystem;
import xaero.map.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class SupportFTBTeamsServer {
    private final ISyncedPlayerTrackerSystem syncedPlayerTrackerSystem = new FTBTeamsSyncedPlayerTrackerSystem();

    public ISyncedPlayerTrackerSystem getSyncedPlayerTrackerSystem() {
        return this.syncedPlayerTrackerSystem;
    }
}

