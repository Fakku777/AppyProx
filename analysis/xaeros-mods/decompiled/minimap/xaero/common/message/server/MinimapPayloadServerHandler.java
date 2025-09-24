/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$Context
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$PlayPayloadHandler
 *  net.minecraft.class_3222
 */
package xaero.common.message.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_3222;
import xaero.common.message.MinimapMessage;
import xaero.common.message.payload.MinimapMessagePayload;
import xaero.common.message.type.MinimapMessageType;

public class MinimapPayloadServerHandler
implements ServerPlayNetworking.PlayPayloadHandler<MinimapMessagePayload<?>> {
    public void receive(MinimapMessagePayload<?> payload, ServerPlayNetworking.Context context) {
        this.handleTyped(payload, context);
    }

    private <T extends MinimapMessage<T>> void handleTyped(MinimapMessagePayload<T> payload, ServerPlayNetworking.Context context) {
        if (payload == null) {
            return;
        }
        Object message = payload.getMsg();
        MinimapMessageType type = payload.getType();
        if (type.getServerHandler() == null) {
            return;
        }
        class_3222 player = context.player();
        if (player == null) {
            return;
        }
        player.method_5682().method_20493(() -> type.getServerHandler().handle(player.method_5682(), player, (MinimapMessage)message));
    }
}

