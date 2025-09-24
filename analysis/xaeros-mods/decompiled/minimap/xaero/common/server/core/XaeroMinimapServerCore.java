/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_3222
 */
package xaero.common.server.core;

import net.minecraft.class_1657;
import net.minecraft.class_3222;
import xaero.common.IXaeroMinimap;

public class XaeroMinimapServerCore {
    public static IXaeroMinimap modMain;

    public static void onServerWorldInfo(class_1657 player) {
        if (!XaeroMinimapServerCore.isModLoaded()) {
            return;
        }
        modMain.getCommonEvents().onPlayerWorldJoin((class_3222)player);
    }

    public static boolean isModLoaded() {
        return modMain != null && (modMain.isLoadedServer() || modMain.isLoadedClient());
    }
}

