/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.map.message;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_2540;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.WorldMapMessageHandler;
import xaero.map.message.client.ClientMessageConsumer;
import xaero.map.message.server.ServerMessageConsumer;
import xaero.map.message.type.WorldMapMessageType;
import xaero.map.message.type.WorldMapMessageTypeManager;

public abstract class WorldMapMessageHandlerFull
extends WorldMapMessageHandler {
    protected final WorldMapMessageTypeManager messageTypes = new WorldMapMessageTypeManager();

    @Override
    public <T extends WorldMapMessage<T>> void register(int index, Class<T> type, ServerMessageConsumer<T> serverHandler, ClientMessageConsumer<T> clientHandler, Function<class_2540, T> decoder, BiConsumer<T, class_2540> encoder) {
        this.messageTypes.register(index, type, serverHandler, clientHandler, decoder, encoder);
    }

    public <T extends WorldMapMessage<T>> void encodeMessage(WorldMapMessageType<T> type, WorldMapMessage<?> message, class_2540 buf) {
        buf.method_52997(type.getIndex());
        WorldMapMessage<?> messageCast = message;
        type.getEncoder().accept(messageCast, buf);
    }

    public WorldMapMessageType<?> getByIndex(int index) {
        return this.messageTypes.getByIndex(index);
    }
}

