/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.class_3222
 */
package xaero.map.message;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_3222;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.WorldMapMessageHandlerFull;
import xaero.map.message.payload.WorldMapMessagePayload;

public class WorldMapMessageHandlerFabric
extends WorldMapMessageHandlerFull {
    public static final int NETWORK_COMPATIBILITY = 2;

    @Override
    public <T extends WorldMapMessage<T>> void sendToPlayer(class_3222 player, T message) {
        ServerPlayNetworking.send((class_3222)player, new WorldMapMessagePayload<T>(this.messageTypes.getType(message), message));
    }

    @Override
    public <T extends WorldMapMessage<T>> void sendToServer(T message) {
        ClientPlayNetworking.send(new WorldMapMessagePayload<T>(this.messageTypes.getType(message), message));
    }
}

