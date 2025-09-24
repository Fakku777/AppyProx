/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$Context
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$PlayPayloadHandler
 *  net.minecraft.class_3222
 */
package xaero.map.message.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_3222;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.payload.WorldMapMessagePayload;
import xaero.map.message.type.WorldMapMessageType;

public class WorldMapPayloadServerHandler
implements ServerPlayNetworking.PlayPayloadHandler<WorldMapMessagePayload<?>> {
    public void receive(WorldMapMessagePayload<?> payload, ServerPlayNetworking.Context context) {
        this.handleTyped(payload, context);
    }

    private <T extends WorldMapMessage<T>> void handleTyped(WorldMapMessagePayload<T> payload, ServerPlayNetworking.Context context) {
        if (payload == null) {
            return;
        }
        Object message = payload.getMsg();
        WorldMapMessageType type = payload.getType();
        if (type.getServerHandler() == null) {
            return;
        }
        class_3222 player = context.player();
        if (player == null) {
            return;
        }
        player.method_5682().method_20493(() -> type.getServerHandler().handle(player.method_5682(), player, (WorldMapMessage)message));
    }
}

