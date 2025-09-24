/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.message.client;

import xaero.map.message.WorldMapMessage;

public interface ClientMessageConsumer<T extends WorldMapMessage<T>> {
    public void handle(T var1);
}

