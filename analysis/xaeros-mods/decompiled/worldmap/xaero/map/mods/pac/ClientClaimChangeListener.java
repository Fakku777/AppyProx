/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_5321
 *  net.minecraft.class_7924
 *  xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI
 *  xaero.pac.common.claims.tracker.api.IClaimsManagerListenerAPI
 */
package xaero.map.mods.pac;

import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7924;
import xaero.map.WorldMapSession;
import xaero.map.world.MapDimension;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.claims.tracker.api.IClaimsManagerListenerAPI;

public class ClientClaimChangeListener
implements IClaimsManagerListenerAPI {
    public void onWholeRegionChange(class_2960 dimension, int regionX, int regionZ) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapDimension mapDim = session.getMapProcessor().getMapWorld().getDimension((class_5321<class_1937>)class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)dimension));
        if (mapDim != null) {
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if ((i != 0 || j != 0) && i * i == j * j) continue;
                    mapDim.getHighlightHandler().clearCachedHash(regionX + i, regionZ + j);
                }
            }
        }
    }

    public void onChunkChange(class_2960 dimension, int chunkX, int chunkZ, IPlayerChunkClaimAPI claim) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapDimension mapDim = session.getMapProcessor().getMapWorld().getDimension((class_5321<class_1937>)class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)dimension));
        if (mapDim != null) {
            for (int i = -1; i < 2; ++i) {
                for (int j = -1; j < 2; ++j) {
                    if ((i != 0 || j != 0) && i * i == j * j) continue;
                    mapDim.getHighlightHandler().clearCachedHash(chunkX + i >> 5, chunkZ + j >> 5);
                }
            }
        }
    }

    public void onDimensionChange(class_2960 dimension) {
        WorldMapSession session = WorldMapSession.getCurrentSession();
        MapDimension mapDim = session.getMapProcessor().getMapWorld().getDimension((class_5321<class_1937>)class_5321.method_29179((class_5321)class_7924.field_41223, (class_2960)dimension));
        if (mapDim != null) {
            mapDim.getHighlightHandler().clearCachedHashes();
        }
    }
}

