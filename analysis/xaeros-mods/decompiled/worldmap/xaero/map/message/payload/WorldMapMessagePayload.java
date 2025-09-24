/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_8710
 *  net.minecraft.class_8710$class_9154
 */
package xaero.map.message.payload;

import net.minecraft.class_8710;
import xaero.map.message.WorldMapMessage;
import xaero.map.message.type.WorldMapMessageType;

public class WorldMapMessagePayload<T extends WorldMapMessage<T>>
implements class_8710 {
    public static class_8710.class_9154<WorldMapMessagePayload<?>> TYPE = new class_8710.class_9154(WorldMapMessage.MAIN_CHANNEL);
    private final WorldMapMessageType<T> type;
    private final T msg;

    public WorldMapMessagePayload(WorldMapMessageType<T> type, T msg) {
        this.type = type;
        this.msg = msg;
    }

    public WorldMapMessageType<T> getType() {
        return this.type;
    }

    public T getMsg() {
        return this.msg;
    }

    public class_8710.class_9154<? extends class_8710> method_56479() {
        return TYPE;
    }
}

