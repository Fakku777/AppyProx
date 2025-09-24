/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 *  net.minecraft.class_9129
 *  net.minecraft.class_9139
 */
package xaero.map.message.payload;

import net.minecraft.class_2540;
import net.minecraft.class_9129;
import net.minecraft.class_9139;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.WorldMapMessageHandlerFull;
import xaero.map.message.payload.WorldMapMessagePayload;
import xaero.map.message.type.WorldMapMessageType;

public class WorldMapMessagePayloadCodec
implements class_9139<class_9129, WorldMapMessagePayload<?>> {
    private final WorldMapMessageHandlerFull messageHandler;

    public WorldMapMessagePayloadCodec(WorldMapMessageHandlerFull messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void encode(class_9129 buf, WorldMapMessagePayload<?> payload) {
        this.messageHandler.encodeMessage(payload.getType(), (WorldMapMessage<?>)payload.getMsg(), (class_2540)buf);
    }

    public WorldMapMessagePayload<?> decode(class_9129 friendlyByteBuf) {
        byte index = friendlyByteBuf.readByte();
        WorldMapMessageType<?> messageType = this.messageHandler.getByIndex(index);
        if (messageType == null) {
            return null;
        }
        return this.createTypedPayload(messageType, (class_2540)friendlyByteBuf);
    }

    private <T extends WorldMapMessage<T>> WorldMapMessagePayload<T> createTypedPayload(WorldMapMessageType<T> messageType, class_2540 friendlyByteBuf) {
        return new WorldMapMessagePayload<WorldMapMessage>(messageType, (WorldMapMessage)messageType.getDecoder().apply(friendlyByteBuf));
    }
}

