/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 *  net.minecraft.class_3222
 *  net.minecraft.server.MinecraftServer
 */
package xaero.map.message.basic;

import net.minecraft.class_2540;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.client.ClientMessageConsumer;
import xaero.map.message.server.ServerMessageConsumer;
import xaero.map.server.player.ServerPlayerData;

public class HandshakePacket
extends WorldMapMessage<HandshakePacket> {
    private final int networkVersion;

    public HandshakePacket(int networkVersion) {
        this.networkVersion = networkVersion;
    }

    public HandshakePacket() {
        this(3);
    }

    public void write(class_2540 u) {
        u.method_53002(this.networkVersion);
    }

    public static HandshakePacket read(class_2540 buffer) {
        return new HandshakePacket(buffer.readInt());
    }

    public static class ServerHandler
    implements ServerMessageConsumer<HandshakePacket> {
        @Override
        public void handle(MinecraftServer server, class_3222 player, HandshakePacket message) {
            ServerPlayerData playerData = ServerPlayerData.get(player);
            playerData.setClientModNetworkVersion(message.networkVersion);
        }
    }

    public static class ClientHandler
    implements ClientMessageConsumer<HandshakePacket> {
        @Override
        public void handle(HandshakePacket message) {
            WorldMapSession session = WorldMapSession.getCurrentSession();
            if (session == null) {
                return;
            }
            session.getMapProcessor().setServerModNetworkVersion(message.networkVersion);
            WorldMap.messageHandler.sendToServer(new HandshakePacket());
        }
    }
}

