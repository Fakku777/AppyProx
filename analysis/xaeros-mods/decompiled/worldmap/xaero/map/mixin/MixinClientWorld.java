/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_638
 *  org.spongepowered.asm.mixin.Mixin
 */
package xaero.map.mixin;

import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import xaero.map.mcworld.IWorldMapClientWorld;
import xaero.map.mcworld.WorldMapClientWorldData;

@Mixin(value={class_638.class})
public class MixinClientWorld
implements IWorldMapClientWorld {
    private WorldMapClientWorldData xaero_worldmapData;

    @Override
    public WorldMapClientWorldData getXaero_worldmapData() {
        return this.xaero_worldmapData;
    }

    @Override
    public void setXaero_worldmapData(WorldMapClientWorldData xaero_worldmapData) {
        this.xaero_worldmapData = xaero_worldmapData;
    }
}

