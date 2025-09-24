/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.message;

import xaero.map.WorldMapSession;
import xaero.map.message.client.ClientMessageConsumer;
import xaero.map.server.level.LevelMapProperties;

public class LevelMapPropertiesConsumer
implements ClientMessageConsumer<LevelMapProperties> {
    @Override
    public void handle(LevelMapProperties t) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        worldmapSession.getMapProcessor().onServerLevelId(t.getId());
    }
}

