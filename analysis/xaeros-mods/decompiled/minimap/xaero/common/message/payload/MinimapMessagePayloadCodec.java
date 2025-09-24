/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 *  net.minecraft.class_9139
 */
package xaero.common.message.payload;

import net.minecraft.class_2540;
import net.minecraft.class_9139;
import xaero.common.message.MinimapMessage;
import xaero.common.message.MinimapMessageHandlerFull;
import xaero.common.message.payload.MinimapMessagePayload;
import xaero.common.message.type.MinimapMessageType;

public class MinimapMessagePayloadCodec
implements class_9139<class_2540, MinimapMessagePayload<?>> {
    private final MinimapMessageHandlerFull messageHandler;

    public MinimapMessagePayloadCodec(MinimapMessageHandlerFull messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void encode(class_2540 buf, MinimapMessagePayload<?> payload) {
        this.messageHandler.encodeMessage(payload.getType(), (MinimapMessage<?>)payload.getMsg(), buf);
    }

    public MinimapMessagePayload<?> decode(class_2540 friendlyByteBuf) {
        byte index = friendlyByteBuf.readByte();
        MinimapMessageType<?> messageType = this.messageHandler.getByIndex(index);
        if (messageType == null) {
            return null;
        }
        return this.createTypedPayload(messageType, friendlyByteBuf);
    }

    private <T extends MinimapMessage<T>> MinimapMessagePayload<T> createTypedPayload(MinimapMessageType<T> messageType, class_2540 friendlyByteBuf) {
        return new MinimapMessagePayload<MinimapMessage>(messageType, (MinimapMessage)messageType.getDecoder().apply(friendlyByteBuf));
    }
}

