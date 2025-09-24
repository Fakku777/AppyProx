/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.message;

import xaero.map.message.LevelMapPropertiesConsumer;
import xaero.map.message.WorldMapMessageHandler;
import xaero.map.message.basic.ClientboundRulesPacket;
import xaero.map.message.basic.HandshakePacket;
import xaero.map.message.tracker.ClientboundPlayerTrackerResetPacket;
import xaero.map.message.tracker.ClientboundTrackedPlayerPacket;
import xaero.map.server.level.LevelMapProperties;

public class WorldMapMessageRegister {
    public void register(WorldMapMessageHandler messageHandler) {
        messageHandler.register(0, LevelMapProperties.class, null, new LevelMapPropertiesConsumer(), LevelMapProperties::read, LevelMapProperties::write);
        messageHandler.register(1, HandshakePacket.class, new HandshakePacket.ServerHandler(), new HandshakePacket.ClientHandler(), HandshakePacket::read, HandshakePacket::write);
        messageHandler.register(2, ClientboundTrackedPlayerPacket.class, null, new ClientboundTrackedPlayerPacket.Handler(), ClientboundTrackedPlayerPacket::read, ClientboundTrackedPlayerPacket::write);
        messageHandler.register(3, ClientboundPlayerTrackerResetPacket.class, null, new ClientboundPlayerTrackerResetPacket.Handler(), ClientboundPlayerTrackerResetPacket::read, ClientboundPlayerTrackerResetPacket::write);
        messageHandler.register(4, ClientboundRulesPacket.class, null, new ClientboundRulesPacket.ClientHandler(), ClientboundRulesPacket::read, ClientboundRulesPacket::write);
    }
}

