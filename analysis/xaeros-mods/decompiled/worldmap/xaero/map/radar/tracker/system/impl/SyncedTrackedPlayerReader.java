/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_5321
 */
package xaero.map.radar.tracker.system.impl;

import java.util.UUID;
import net.minecraft.class_1937;
import net.minecraft.class_5321;
import xaero.map.radar.tracker.system.ITrackedPlayerReader;
import xaero.map.server.radar.tracker.SyncedTrackedPlayer;

public class SyncedTrackedPlayerReader
implements ITrackedPlayerReader<SyncedTrackedPlayer> {
    @Override
    public UUID getId(SyncedTrackedPlayer player) {
        return player.getId();
    }

    @Override
    public double getX(SyncedTrackedPlayer player) {
        return player.getX();
    }

    @Override
    public double getY(SyncedTrackedPlayer player) {
        return player.getY();
    }

    @Override
    public double getZ(SyncedTrackedPlayer player) {
        return player.getZ();
    }

    @Override
    public class_5321<class_1937> getDimension(SyncedTrackedPlayer player) {
        return player.getDimension();
    }
}

