/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.map.message.type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_2540;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.client.ClientMessageConsumer;
import xaero.map.message.server.ServerMessageConsumer;
import xaero.map.message.type.WorldMapMessageType;

public class WorldMapMessageTypeManager {
    private final Map<Integer, WorldMapMessageType<?>> typeByIndex = new HashMap();
    private final Map<Class<?>, WorldMapMessageType<?>> typeByClass = new HashMap();

    public <T extends WorldMapMessage<T>> void register(int index, Class<T> type, ServerMessageConsumer<T> serverHandler, ClientMessageConsumer<T> clientHandler, Function<class_2540, T> decoder, BiConsumer<T, class_2540> encoder) {
        WorldMapMessageType<T> messageType = new WorldMapMessageType<T>(index, type, serverHandler, clientHandler, decoder, encoder);
        this.typeByIndex.put(index, messageType);
        this.typeByClass.put(type, messageType);
    }

    public WorldMapMessageType<?> getByIndex(int index) {
        return this.typeByIndex.get(index);
    }

    public WorldMapMessageType<?> getByClass(Class<?> clazz) {
        return this.typeByClass.get(clazz);
    }

    public <T extends WorldMapMessage<T>> WorldMapMessageType<T> getType(T message) {
        return this.typeByClass.get(message.getClass());
    }
}

