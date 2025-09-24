/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.minecraft.class_3222
 */
package xaero.common.message;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_3222;
import xaero.common.message.MinimapMessage;
import xaero.common.message.MinimapMessageHandlerFull;
import xaero.common.message.payload.MinimapMessagePayload;

public class MinimapMessageHandlerFabric
extends MinimapMessageHandlerFull {
    @Override
    public <T extends MinimapMessage<T>> void sendToPlayer(class_3222 player, T message) {
        ServerPlayNetworking.send((class_3222)player, new MinimapMessagePayload<T>(this.messageTypes.getType(message), message));
    }

    @Override
    public <T extends MinimapMessage<T>> void sendToServer(T message) {
        ClientPlayNetworking.send(new MinimapMessagePayload<T>(this.messageTypes.getType(message), message));
    }
}

