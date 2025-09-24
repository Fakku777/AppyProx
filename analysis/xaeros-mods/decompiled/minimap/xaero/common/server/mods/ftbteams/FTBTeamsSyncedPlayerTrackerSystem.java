/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ftb.mods.ftbteams.api.FTBTeamsAPI
 *  dev.ftb.mods.ftbteams.api.Team
 *  dev.ftb.mods.ftbteams.api.TeamRank
 *  net.minecraft.class_1657
 */
package xaero.common.server.mods.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamRank;
import net.minecraft.class_1657;
import xaero.common.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class FTBTeamsSyncedPlayerTrackerSystem
implements ISyncedPlayerTrackerSystem {
    @Override
    public int getTrackingLevel(class_1657 tracker, class_1657 tracked) {
        if (FTBTeamsAPI.api().getManager().arePlayersInSameTeam(tracker.method_5667(), tracked.method_5667())) {
            return 2;
        }
        Team trackerTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(tracker.method_5667()).orElse(null);
        if (trackerTeam == null) {
            return 0;
        }
        Team trackedTeam = FTBTeamsAPI.api().getManager().getTeamForPlayerID(tracked.method_5667()).orElse(null);
        if (trackedTeam == null) {
            return 0;
        }
        if (trackerTeam.getRankForPlayer(tracked.method_5667()) == TeamRank.ALLY && trackedTeam.getRankForPlayer(tracker.method_5667()) == TeamRank.ALLY) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isPartySystem() {
        return true;
    }
}

