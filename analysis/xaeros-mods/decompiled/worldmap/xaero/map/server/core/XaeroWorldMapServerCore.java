/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_3222
 */
package xaero.map.server.core;

import net.minecraft.class_1657;
import net.minecraft.class_3222;
import xaero.map.WorldMap;

public class XaeroWorldMapServerCore {
    public static void onServerWorldInfo(class_1657 player) {
        if (!WorldMap.loaded) {
            return;
        }
        WorldMap.commonEvents.onPlayerWorldJoin((class_3222)player);
    }
}

