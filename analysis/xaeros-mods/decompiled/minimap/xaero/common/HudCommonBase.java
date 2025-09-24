/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import xaero.common.IXaeroMinimap;
import xaero.common.message.MinimapMessageRegister;
import xaero.common.server.player.ServerPlayerTickHandler;

public class HudCommonBase {
    public void setup(IXaeroMinimap modMain) {
        modMain.getPlatformContext().getLoaderCommon().setup(modMain);
        new MinimapMessageRegister().register(modMain.getMessageHandler());
        modMain.setServerPlayerTickHandler(new ServerPlayerTickHandler());
        modMain.getSupportServerMods().check(modMain);
    }
}

