/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.message;

import xaero.common.message.LevelMapPropertiesConsumer;
import xaero.common.message.MinimapMessageHandler;
import xaero.common.message.basic.ClientboundRulesPacket;
import xaero.common.message.basic.HandshakePacket;
import xaero.common.message.tracker.ClientboundPlayerTrackerResetPacket;
import xaero.common.message.tracker.ClientboundTrackedPlayerPacket;
import xaero.common.server.level.LevelMapProperties;

public class MinimapMessageRegister {
    public void register(MinimapMessageHandler messageHandler) {
        messageHandler.register(0, LevelMapProperties.class, null, new LevelMapPropertiesConsumer(), LevelMapProperties::read, LevelMapProperties::write);
        messageHandler.register(1, HandshakePacket.class, new HandshakePacket.ServerHandler(), new HandshakePacket.ClientHandler(), HandshakePacket::read, HandshakePacket::write);
        messageHandler.register(2, ClientboundTrackedPlayerPacket.class, null, new ClientboundTrackedPlayerPacket.Handler(), ClientboundTrackedPlayerPacket::read, ClientboundTrackedPlayerPacket::write);
        messageHandler.register(3, ClientboundPlayerTrackerResetPacket.class, null, new ClientboundPlayerTrackerResetPacket.Handler(), ClientboundPlayerTrackerResetPacket::read, ClientboundPlayerTrackerResetPacket::write);
        messageHandler.register(4, ClientboundRulesPacket.class, null, new ClientboundRulesPacket.ClientHandler(), ClientboundRulesPacket::read, ClientboundRulesPacket::write);
    }
}

