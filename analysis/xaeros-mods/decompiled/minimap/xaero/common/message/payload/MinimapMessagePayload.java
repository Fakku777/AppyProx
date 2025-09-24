/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_8710
 *  net.minecraft.class_8710$class_9154
 */
package xaero.common.message.payload;

import net.minecraft.class_8710;
import xaero.common.message.MinimapMessage;
import xaero.common.message.type.MinimapMessageType;

public class MinimapMessagePayload<T extends MinimapMessage<T>>
implements class_8710 {
    public static class_8710.class_9154<MinimapMessagePayload<?>> TYPE = new class_8710.class_9154(MinimapMessage.MAIN_CHANNEL);
    private final MinimapMessageType<T> type;
    private final T msg;

    public MinimapMessagePayload(MinimapMessageType<T> type, T msg) {
        this.type = type;
        this.msg = msg;
    }

    public MinimapMessageType<T> getType() {
        return this.type;
    }

    public T getMsg() {
        return this.msg;
    }

    public class_8710.class_9154<? extends class_8710> method_56479() {
        return TYPE;
    }
}

