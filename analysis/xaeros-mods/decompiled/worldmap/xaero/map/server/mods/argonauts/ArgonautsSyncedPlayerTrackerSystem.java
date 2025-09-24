/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  earth.terrarium.argonauts.api.guild.Guild
 *  earth.terrarium.argonauts.api.guild.GuildApi
 *  earth.terrarium.argonauts.api.party.Party
 *  earth.terrarium.argonauts.api.party.PartyApi
 *  net.minecraft.class_1657
 */
package xaero.map.server.mods.argonauts;

import earth.terrarium.argonauts.api.guild.Guild;
import earth.terrarium.argonauts.api.guild.GuildApi;
import earth.terrarium.argonauts.api.party.Party;
import earth.terrarium.argonauts.api.party.PartyApi;
import net.minecraft.class_1657;
import xaero.map.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class ArgonautsSyncedPlayerTrackerSystem
implements ISyncedPlayerTrackerSystem {
    @Override
    public int getTrackingLevel(class_1657 tracker, class_1657 tracked) {
        int partyTrackingLevel = this.getPartyTrackingLevel(tracker, tracked);
        int guildTrackingLevel = this.getGuildTrackingLevel(tracker, tracked);
        return Math.max(partyTrackingLevel, guildTrackingLevel);
    }

    @Override
    public boolean isPartySystem() {
        return true;
    }

    private int getPartyTrackingLevel(class_1657 tracker, class_1657 tracked) {
        Party trackerParty = PartyApi.API.get(tracker);
        if (trackerParty == null) {
            return 0;
        }
        Party trackedParty = PartyApi.API.get(tracked);
        if (trackerParty == trackedParty) {
            return 2;
        }
        return 0;
    }

    private int getGuildTrackingLevel(class_1657 tracker, class_1657 tracked) {
        Guild trackerGuild = GuildApi.API.getPlayerGuild(tracker.method_5682(), tracker.method_5667());
        if (trackerGuild == null) {
            return 0;
        }
        Guild trackedGuild = GuildApi.API.getPlayerGuild(tracked.method_5682(), tracked.method_5667());
        if (trackerGuild == trackedGuild) {
            return 2;
        }
        return 0;
    }
}

