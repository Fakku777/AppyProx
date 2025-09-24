/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 */
package xaero.map.message.type;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_2540;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.client.ClientMessageConsumer;
import xaero.map.message.server.ServerMessageConsumer;

public class WorldMapMessageType<T extends WorldMapMessage<T>> {
    private final int index;
    private final Class<T> type;
    private final ClientMessageConsumer<T> clientHandler;
    private final ServerMessageConsumer<T> serverHandler;
    private final Function<class_2540, T> decoder;
    private final BiConsumer<T, class_2540> encoder;

    public WorldMapMessageType(int index, Class<T> type, ServerMessageConsumer<T> serverHandler, ClientMessageConsumer<T> clientHandler, Function<class_2540, T> decoder, BiConsumer<T, class_2540> encoder) {
        this.index = index;
        this.type = type;
        this.serverHandler = serverHandler;
        this.clientHandler = clientHandler;
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public int getIndex() {
        return this.index;
    }

    public Class<T> getType() {
        return this.type;
    }

    public ClientMessageConsumer<T> getClientHandler() {
        return this.clientHandler;
    }

    public ServerMessageConsumer<T> getServerHandler() {
        return this.serverHandler;
    }

    public Function<class_2540, T> getDecoder() {
        return this.decoder;
    }

    public BiConsumer<T, class_2540> getEncoder() {
        return this.encoder;
    }
}

