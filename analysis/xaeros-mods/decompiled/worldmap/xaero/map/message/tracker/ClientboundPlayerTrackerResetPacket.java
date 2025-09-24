/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.map.message.tracker;

import net.minecraft.class_2540;
import xaero.map.WorldMapSession;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.client.ClientMessageConsumer;

public class ClientboundPlayerTrackerResetPacket
extends WorldMapMessage<ClientboundPlayerTrackerResetPacket> {
    public void write(class_2540 buffer) {
    }

    public static ClientboundPlayerTrackerResetPacket read(class_2540 buffer) {
        return new ClientboundPlayerTrackerResetPacket();
    }

    public static class Handler
    implements ClientMessageConsumer<ClientboundPlayerTrackerResetPacket> {
        @Override
        public void handle(ClientboundPlayerTrackerResetPacket t) {
            WorldMapSession session = WorldMapSession.getCurrentSession();
            if (session == null) {
                return;
            }
            session.getMapProcessor().getClientSyncedTrackedPlayerManager().reset();
        }
    }
}

