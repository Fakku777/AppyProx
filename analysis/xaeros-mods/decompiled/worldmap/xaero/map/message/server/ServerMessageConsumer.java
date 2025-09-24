/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3222
 *  net.minecraft.server.MinecraftServer
 */
package xaero.map.message.server;

import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import xaero.map.message.WorldMapMessage;

public interface ServerMessageConsumer<T extends WorldMapMessage<T>> {
    public void handle(MinecraftServer var1, class_3222 var2, T var3);
}

