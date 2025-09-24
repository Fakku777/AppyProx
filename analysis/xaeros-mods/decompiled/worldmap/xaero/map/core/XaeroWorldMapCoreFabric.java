/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2678
 *  net.minecraft.class_634
 */
package xaero.map.core;

import net.minecraft.class_2678;
import net.minecraft.class_634;
import xaero.map.WorldMap;
import xaero.map.WorldMapFabric;
import xaero.map.core.XaeroWorldMapCore;

public class XaeroWorldMapCoreFabric {
    public static void onPlayNetHandler(class_634 netHandler, class_2678 packet) {
        if (WorldMap.INSTANCE != null) {
            ((WorldMapFabric)WorldMap.INSTANCE).tryLoadLater();
        }
        if (!WorldMap.loaded) {
            return;
        }
        if (WorldMap.crashHandler.getCrashedBy() != null) {
            return;
        }
        XaeroWorldMapCore.onPlayNetHandler(netHandler, packet);
    }

    public static void onMinecraftRunTick() {
        if (WorldMap.INSTANCE != null) {
            ((WorldMapFabric)WorldMap.INSTANCE).tryLoadLater();
        }
        XaeroWorldMapCore.onMinecraftRunTick();
    }
}

