/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1959
 *  net.minecraft.class_2378
 */
package xaero.map.region;

import net.minecraft.class_1959;
import net.minecraft.class_2378;
import xaero.map.region.ExportMapTileChunk;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.ExportLeafRegionTexture;
import xaero.map.world.MapDimension;

public class ExportMapRegion
extends MapRegion {
    public ExportMapRegion(MapDimension dim, int x, int z, int caveLayer, class_2378<class_1959> biomeRegistry) {
        super("png", "null", null, dim, x, z, caveLayer, 0, false, biomeRegistry);
    }

    @Override
    protected MapTileChunk createTileChunk(int x, int y) {
        return new ExportMapTileChunk(this, this.regionX * 8 + x, this.regionZ * 8 + y);
    }

    @Override
    public ExportLeafRegionTexture getTexture(int x, int y) {
        return (ExportLeafRegionTexture)super.getTexture(x, y);
    }

    @Override
    public ExportMapTileChunk getChunk(int x, int z) {
        return (ExportMapTileChunk)super.getChunk(x, z);
    }
}

