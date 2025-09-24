/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.common.message;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_2540;
import xaero.common.message.MinimapMessage;
import xaero.common.message.MinimapMessageHandler;
import xaero.common.message.client.ClientMessageConsumer;
import xaero.common.message.server.ServerMessageConsumer;
import xaero.common.message.type.MinimapMessageType;
import xaero.common.message.type.MinimapMessageTypeManager;

public abstract class MinimapMessageHandlerFull
extends MinimapMessageHandler {
    protected final MinimapMessageTypeManager messageTypes = new MinimapMessageTypeManager();

    @Override
    public <T extends MinimapMessage<T>> void register(int index, Class<T> type, ServerMessageConsumer<T> serverHandler, ClientMessageConsumer<T> clientHandler, Function<class_2540, T> decoder, BiConsumer<T, class_2540> encoder) {
        this.messageTypes.register(index, type, serverHandler, clientHandler, decoder, encoder);
    }

    public <T extends MinimapMessage<T>> void encodeMessage(MinimapMessageType<T> type, MinimapMessage<?> message, class_2540 buf) {
        buf.method_52997(type.getIndex());
        MinimapMessage<?> messageCast = message;
        type.getEncoder().accept(messageCast, buf);
    }

    public MinimapMessageType<?> getByIndex(int index) {
        return this.messageTypes.getByIndex(index);
    }
}

