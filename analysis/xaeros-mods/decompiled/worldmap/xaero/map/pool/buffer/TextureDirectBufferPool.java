/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.pool.buffer;

import xaero.map.pool.MapPool;
import xaero.map.pool.buffer.PoolTextureDirectBufferUnit;

public class TextureDirectBufferPool
extends MapPool<PoolTextureDirectBufferUnit> {
    public TextureDirectBufferPool() {
        super(4096);
    }

    @Override
    protected PoolTextureDirectBufferUnit construct(Object ... args) {
        return new PoolTextureDirectBufferUnit(args);
    }

    public PoolTextureDirectBufferUnit get(boolean zeroFillIfReused) {
        return (PoolTextureDirectBufferUnit)super.get(zeroFillIfReused);
    }
}

