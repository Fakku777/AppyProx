/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2540
 *  net.minecraft.class_3222
 */
package xaero.common.message;

import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_2540;
import net.minecraft.class_3222;
import xaero.common.message.MinimapMessage;
import xaero.common.message.client.ClientMessageConsumer;
import xaero.common.message.server.ServerMessageConsumer;

public abstract class MinimapMessageHandler {
    public static final int NETWORK_COMPATIBILITY = 3;

    public abstract <T extends MinimapMessage<T>> void register(int var1, Class<T> var2, ServerMessageConsumer<T> var3, ClientMessageConsumer<T> var4, Function<class_2540, T> var5, BiConsumer<T, class_2540> var6);

    public abstract <T extends MinimapMessage<T>> void sendToPlayer(class_3222 var1, T var2);

    public abstract <T extends MinimapMessage<T>> void sendToServer(T var1);
}

