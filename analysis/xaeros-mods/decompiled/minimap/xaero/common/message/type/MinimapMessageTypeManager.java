/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.common.message.type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_2540;
import xaero.common.message.MinimapMessage;
import xaero.common.message.client.ClientMessageConsumer;
import xaero.common.message.server.ServerMessageConsumer;
import xaero.common.message.type.MinimapMessageType;

public class MinimapMessageTypeManager {
    private final Map<Integer, MinimapMessageType<?>> typeByIndex = new HashMap();
    private final Map<Class<?>, MinimapMessageType<?>> typeByClass = new HashMap();

    public <T extends MinimapMessage<T>> void register(int index, Class<T> type, ServerMessageConsumer<T> serverHandler, ClientMessageConsumer<T> clientHandler, Function<class_2540, T> decoder, BiConsumer<T, class_2540> encoder) {
        MinimapMessageType<T> messageType = new MinimapMessageType<T>(index, type, serverHandler, clientHandler, decoder, encoder);
        this.typeByIndex.put(index, messageType);
        this.typeByClass.put(type, messageType);
    }

    public MinimapMessageType<?> getByIndex(int index) {
        return this.typeByIndex.get(index);
    }

    public MinimapMessageType<?> getByClass(Class<?> clazz) {
        return this.typeByClass.get(clazz);
    }

    public <T extends MinimapMessage<T>> MinimapMessageType<T> getType(T message) {
        return this.typeByClass.get(message.getClass());
    }
}

