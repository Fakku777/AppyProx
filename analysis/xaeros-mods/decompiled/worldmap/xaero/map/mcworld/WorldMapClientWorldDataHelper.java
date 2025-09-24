/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 */
package xaero.map.mcworld;

import net.minecraft.class_310;
import net.minecraft.class_638;
import xaero.map.mcworld.IWorldMapClientWorld;
import xaero.map.mcworld.WorldMapClientWorldData;

public class WorldMapClientWorldDataHelper {
    public static WorldMapClientWorldData getCurrentWorldData() {
        return WorldMapClientWorldDataHelper.getWorldData(class_310.method_1551().field_1687);
    }

    public static synchronized WorldMapClientWorldData getWorldData(class_638 clientWorld) {
        if (clientWorld == null) {
            return null;
        }
        IWorldMapClientWorld inter = (IWorldMapClientWorld)clientWorld;
        WorldMapClientWorldData worldmapWorldData = inter.getXaero_worldmapData();
        if (worldmapWorldData == null) {
            worldmapWorldData = new WorldMapClientWorldData(clientWorld);
            inter.setXaero_worldmapData(worldmapWorldData);
        }
        return worldmapWorldData;
    }
}

