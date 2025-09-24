/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 */
package xaero.map.pool.buffer;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import xaero.map.pool.PoolUnit;

public class PoolTextureDirectBufferUnit
implements PoolUnit {
    private ByteBuffer directBuffer;

    public PoolTextureDirectBufferUnit(Object ... args) {
        this.create(args);
    }

    public ByteBuffer getDirectBuffer() {
        return this.directBuffer;
    }

    public void reset() {
        this.directBuffer.clear();
        BufferUtils.zeroBuffer((ByteBuffer)this.directBuffer);
    }

    @Override
    public void create(Object ... args) {
        if (this.directBuffer == null) {
            this.directBuffer = PoolTextureDirectBufferUnit.createBuffer();
        } else {
            this.directBuffer.clear();
            if (((Boolean)args[0]).booleanValue()) {
                BufferUtils.zeroBuffer((ByteBuffer)this.directBuffer);
            }
        }
    }

    public static ByteBuffer createBuffer() {
        return BufferUtils.createByteBuffer((int)16384);
    }
}

