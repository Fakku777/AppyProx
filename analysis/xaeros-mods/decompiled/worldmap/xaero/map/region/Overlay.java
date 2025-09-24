/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2378
 *  net.minecraft.class_2680
 *  net.minecraft.class_2874
 */
package xaero.map.region;

import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2680;
import net.minecraft.class_2874;
import xaero.map.MapProcessor;
import xaero.map.MapWriter;
import xaero.map.biome.BlockTintProvider;
import xaero.map.region.MapBlock;
import xaero.map.region.MapPixel;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.OverlayManager;
import xaero.map.world.MapDimension;

public class Overlay
extends MapPixel {
    private byte opacity;

    public Overlay(class_2680 state, byte light, boolean glowing) {
        this.write(state, light, glowing);
    }

    public void write(class_2680 state, byte light, boolean glowing) {
        this.opacity = 0;
        this.state = state;
        this.light = light;
        this.glowing = glowing;
    }

    public boolean isWater() {
        return this.state.method_26204() == class_2246.field_10382;
    }

    public int getParametres() {
        int parametres = 0;
        parametres |= !this.isWater() ? 1 : 0;
        parametres |= this.light << 4;
        return parametres |= (this.opacity & 0xF) << 11;
    }

    public void getPixelColour(MapBlock block, int[] result_dest, MapWriter mapWriter, class_1937 world, MapDimension dim, class_2378<class_2248> blockRegistry, MapTileChunk tileChunk, MapTileChunk prevChunk, MapTileChunk prevChunkDiagonal, MapTileChunk prevChunkHorisontal, MapTile mapTile, int x, int z, int caveStart, int caveDepth, class_2338.class_2339 mutableGlobalPos, class_2378<class_1959> biomeRegistry, class_2378<class_2874> dimensionTypes, float shadowR, float shadowG, float shadowB, BlockTintProvider blockTintProvider, MapProcessor mapProcessor, OverlayManager overlayManager) {
        super.getPixelColours(result_dest, mapWriter, world, dim, blockRegistry, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, mapTile, x, z, block, -1, -1, caveStart, caveDepth, null, mutableGlobalPos, biomeRegistry, dimensionTypes, shadowR, shadowG, shadowB, blockTintProvider, mapProcessor, overlayManager, null);
    }

    public String toRenderString() {
        return "(S: " + String.valueOf(this.getState()) + ", O: " + this.opacity + ", L: " + this.light + ")";
    }

    public boolean equals(Overlay p) {
        return p != null && this.opacity == p.opacity && this.light == p.light && this.getState() == p.getState();
    }

    void fillManagerKeyHolder(Object[] keyHolder) {
        keyHolder[0] = this.state;
        keyHolder[1] = this.light;
        keyHolder[2] = this.opacity;
    }

    public void increaseOpacity(int toAdd) {
        if (toAdd > 15) {
            toAdd = 15;
        }
        this.opacity = (byte)(this.opacity + toAdd);
        if (this.opacity > 15) {
            this.opacity = (byte)15;
        }
    }

    public int getOpacity() {
        return this.opacity;
    }
}

