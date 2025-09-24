/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  xaero.pac.common.parties.party.api.IPartyMemberDynamicInfoSyncableAPI
 */
package xaero.map.mods.pac.party;

import java.util.Iterator;
import xaero.map.mods.pac.SupportOpenPartiesAndClaims;
import xaero.map.mods.pac.party.OPACTrackedPlayerReader;
import xaero.map.radar.tracker.system.IPlayerTrackerSystem;
import xaero.map.radar.tracker.system.ITrackedPlayerReader;
import xaero.pac.common.parties.party.api.IPartyMemberDynamicInfoSyncableAPI;

public class OPACPlayerTrackerSystem
implements IPlayerTrackerSystem<IPartyMemberDynamicInfoSyncableAPI> {
    private final SupportOpenPartiesAndClaims opac;
    private final OPACTrackedPlayerReader reader;

    public OPACPlayerTrackerSystem(SupportOpenPartiesAndClaims opac) {
        this.opac = opac;
        this.reader = new OPACTrackedPlayerReader();
    }

    @Override
    public ITrackedPlayerReader<IPartyMemberDynamicInfoSyncableAPI> getReader() {
        return this.reader;
    }

    @Override
    public Iterator<IPartyMemberDynamicInfoSyncableAPI> getTrackedPlayerIterator() {
        return this.opac.getAllyIterator();
    }
}

