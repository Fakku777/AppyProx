/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.region;

import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.ExportLeafRegionTexture;
import xaero.map.region.texture.LeafRegionTexture;

public class ExportMapTileChunk
extends MapTileChunk {
    public ExportMapTileChunk(MapRegion r, int x, int z) {
        super(r, x, z);
    }

    @Override
    protected LeafRegionTexture createLeafTexture() {
        return new ExportLeafRegionTexture(this);
    }

    @Override
    public ExportLeafRegionTexture getLeafTexture() {
        return (ExportLeafRegionTexture)super.getLeafTexture();
    }
}

