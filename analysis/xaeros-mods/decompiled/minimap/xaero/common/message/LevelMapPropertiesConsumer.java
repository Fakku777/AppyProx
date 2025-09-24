/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.message;

import xaero.common.message.client.ClientMessageConsumer;
import xaero.common.server.level.LevelMapProperties;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;

public class LevelMapPropertiesConsumer
implements ClientMessageConsumer<LevelMapProperties> {
    @Override
    public void handle(LevelMapProperties t) {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        minimapSession.getWorldStateUpdater().onServerLevelId(t.getId());
    }
}

