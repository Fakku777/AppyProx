/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$Context
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$PlayPayloadHandler
 *  net.minecraft.class_310
 */
package xaero.common.message.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_310;
import xaero.common.message.MinimapMessage;
import xaero.common.message.payload.MinimapMessagePayload;
import xaero.common.message.type.MinimapMessageType;

public class MinimapPayloadClientHandler
implements ClientPlayNetworking.PlayPayloadHandler<MinimapMessagePayload<?>> {
    public void receive(MinimapMessagePayload<?> payload, ClientPlayNetworking.Context context) {
        this.handleTyped(payload, context);
    }

    private <T extends MinimapMessage<T>> void handleTyped(MinimapMessagePayload<T> payload, ClientPlayNetworking.Context context) {
        if (payload == null) {
            return;
        }
        Object message = payload.getMsg();
        MinimapMessageType type = payload.getType();
        if (type.getClientHandler() == null) {
            return;
        }
        class_310.method_1551().execute(() -> type.getClientHandler().handle((MinimapMessage)message));
    }
}

