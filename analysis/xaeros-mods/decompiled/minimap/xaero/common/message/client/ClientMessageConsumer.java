/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.message.client;

import xaero.common.message.MinimapMessage;

public interface ClientMessageConsumer<T extends MinimapMessage<T>> {
    public void handle(T var1);
}

