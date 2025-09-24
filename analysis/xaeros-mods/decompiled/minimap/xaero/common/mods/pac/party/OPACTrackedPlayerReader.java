/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_5321
 *  net.minecraft.class_7924
 *  xaero.pac.common.parties.party.api.IPartyMemberDynamicInfoSyncableAPI
 */
package xaero.common.mods.pac.party;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import xaero.hud.minimap.player.tracker.system.ITrackedPlayerReader;
import xaero.pac.common.parties.party.api.IPartyMemberDynamicInfoSyncableAPI;

public class OPACTrackedPlayerReader
implements ITrackedPlayerReader<IPartyMemberDynamicInfoSyncableAPI> {
    private final Map<class_2960, class_5321<class_1937>> dimensionKeyCache = new HashMap<class_2960, class_5321<class_1937>>();

    @Override
    public UUID getId(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getPlayerId();
    }

    @Override
    public double getX(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getX();
    }

    @Override
    public double getY(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getY();
    }

    @Override
    public double getZ(IPartyMemberDynamicInfoSyncableAPI player) {
        return player.getZ();
    }

    @Override
    public class_5321<class_1937> getDimension(IPartyMemberDynamicInfoSyncableAPI player) {
        if (player.getDimension() == null) {
            return null;
        }
        class_5321 result = this.dimensionKeyCache.get(player.getDimension());
        if (result == null) {
            result = class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)player.getDimension());
            this.dimensionKeyCache.put(player.getDimension(), (class_5321<class_1937>)result);
        }
        return result;
    }
}

