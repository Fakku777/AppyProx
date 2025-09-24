/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$Context
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking$PlayPayloadHandler
 *  net.minecraft.class_310
 */
package xaero.map.message.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_310;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.payload.WorldMapMessagePayload;
import xaero.map.message.type.WorldMapMessageType;

public class WorldMapPayloadClientHandler
implements ClientPlayNetworking.PlayPayloadHandler<WorldMapMessagePayload<?>> {
    public void receive(WorldMapMessagePayload<?> payload, ClientPlayNetworking.Context context) {
        this.handleTyped(payload, context);
    }

    private <T extends WorldMapMessage<T>> void handleTyped(WorldMapMessagePayload<T> payload, ClientPlayNetworking.Context context) {
        if (payload == null) {
            return;
        }
        Object message = payload.getMsg();
        WorldMapMessageType type = payload.getType();
        if (type.getClientHandler() == null) {
            return;
        }
        class_310.method_1551().execute(() -> type.getClientHandler().handle((WorldMapMessage)message));
    }
}

