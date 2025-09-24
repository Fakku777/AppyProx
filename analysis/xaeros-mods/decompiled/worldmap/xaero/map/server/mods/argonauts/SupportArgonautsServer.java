/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.server.mods.argonauts;

import xaero.map.server.mods.argonauts.ArgonautsSyncedPlayerTrackerSystem;
import xaero.map.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class SupportArgonautsServer {
    private final ISyncedPlayerTrackerSystem syncedPlayerTrackerSystem = new ArgonautsSyncedPlayerTrackerSystem();

    public ISyncedPlayerTrackerSystem getSyncedPlayerTrackerSystem() {
        return this.syncedPlayerTrackerSystem;
    }
}

