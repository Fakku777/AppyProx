/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.pool;

import xaero.map.pool.MapPool;
import xaero.map.region.MapTile;

public class MapTilePool
extends MapPool<MapTile> {
    public MapTilePool() {
        super(2048);
    }

    @Override
    protected MapTile construct(Object ... args) {
        return new MapTile(args);
    }

    public MapTile get(String dimension, int chunkX, int chunkZ) {
        return (MapTile)super.get(dimension, chunkX, chunkZ);
    }
}

