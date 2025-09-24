/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector2f
 */
package xaero.common.graphics.shader;

import java.nio.ByteBuffer;
import org.joml.Vector2f;
import xaero.common.graphics.shader.CustomUniformValueType;

public class BuiltInCustomUniformValueTypes {
    public static final CustomUniformValueType<Float> FLOAT = new CustomUniformValueType<Float>(4, ByteBuffer::putFloat);
    public static final CustomUniformValueType<Vector2f> VEC_2F = new CustomUniformValueType<Vector2f>(8, (buffer, value) -> {
        buffer.putFloat(value.x);
        buffer.putFloat(value.y);
    });
}

