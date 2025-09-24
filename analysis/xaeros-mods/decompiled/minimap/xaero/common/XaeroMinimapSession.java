/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_634
 *  net.minecraft.class_746
 */
package xaero.common;

import java.io.IOException;
import net.minecraft.class_634;
import net.minecraft.class_746;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.minimap.MinimapProcessor;
import xaero.hud.HudSession;
import xaero.hud.minimap.BuiltInHudModules;

@Deprecated
public class XaeroMinimapSession
extends HudSession {
    public XaeroMinimapSession(HudMod modMain) {
        super(modMain);
    }

    @Override
    public void init(class_634 connection) throws IOException {
        super.init(connection);
    }

    @Override
    protected void cleanup() {
        super.cleanup();
    }

    public MinimapProcessor getMinimapProcessor() {
        return BuiltInHudModules.MINIMAP.getCurrentSession().getProcessor();
    }

    public static XaeroMinimapSession getCurrentSession() {
        return (XaeroMinimapSession)HudSession.getCurrentSession();
    }

    public static XaeroMinimapSession getForPlayer(class_746 player) {
        return (XaeroMinimapSession)HudSession.getForPlayer(player);
    }

    public IXaeroMinimap getModMain() {
        return this.getHudMod();
    }
}

