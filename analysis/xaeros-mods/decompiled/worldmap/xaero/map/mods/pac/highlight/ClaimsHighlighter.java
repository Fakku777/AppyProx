/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_124
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_5321
 *  xaero.pac.client.claims.api.IClientClaimsManagerAPI
 *  xaero.pac.client.claims.api.IClientDimensionClaimsManagerAPI
 *  xaero.pac.client.claims.api.IClientRegionClaimsAPI
 *  xaero.pac.client.claims.player.api.IClientPlayerClaimInfoAPI
 *  xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI
 *  xaero.pac.common.claims.player.api.IPlayerClaimPosListAPI
 *  xaero.pac.common.claims.player.api.IPlayerDimensionClaimsAPI
 *  xaero.pac.common.server.player.config.PlayerConfig
 */
package xaero.map.mods.pac.highlight;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.class_1074;
import net.minecraft.class_124;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_5321;
import xaero.map.WorldMap;
import xaero.map.highlight.ChunkHighlighter;
import xaero.pac.client.claims.api.IClientClaimsManagerAPI;
import xaero.pac.client.claims.api.IClientDimensionClaimsManagerAPI;
import xaero.pac.client.claims.api.IClientRegionClaimsAPI;
import xaero.pac.client.claims.player.api.IClientPlayerClaimInfoAPI;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.claims.player.api.IPlayerClaimPosListAPI;
import xaero.pac.common.claims.player.api.IPlayerDimensionClaimsAPI;
import xaero.pac.common.server.player.config.PlayerConfig;

public class ClaimsHighlighter
extends ChunkHighlighter {
    private final IClientClaimsManagerAPI<IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>>, IClientDimensionClaimsManagerAPI<IClientRegionClaimsAPI>> claimsManager;
    private class_2561 cachedTooltip;
    private IPlayerChunkClaimAPI cachedTooltipFor;
    private String cachedForCustomName;
    private int cachedForClaimsColor;

    public ClaimsHighlighter(IClientClaimsManagerAPI<IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>>, IClientDimensionClaimsManagerAPI<IClientRegionClaimsAPI>> claimsManager) {
        super(true);
        this.claimsManager = claimsManager;
    }

    @Override
    public boolean regionHasHighlights(class_5321<class_1937> dimension, int regionX, int regionZ) {
        if (!WorldMap.settings.displayClaims) {
            return false;
        }
        IClientDimensionClaimsManagerAPI claimsDimension = this.claimsManager.getDimension(dimension.method_29177());
        if (claimsDimension == null) {
            return false;
        }
        return claimsDimension.getRegion(regionX, regionZ) != null;
    }

    @Override
    protected int[] getColors(class_5321<class_1937> dimension, int chunkX, int chunkZ) {
        if (!WorldMap.settings.displayClaims) {
            return null;
        }
        IPlayerChunkClaimAPI currentClaim = this.claimsManager.get(dimension.method_29177(), chunkX, chunkZ);
        if (currentClaim == null) {
            return null;
        }
        IPlayerChunkClaimAPI topClaim = this.claimsManager.get(dimension.method_29177(), chunkX, chunkZ - 1);
        IPlayerChunkClaimAPI rightClaim = this.claimsManager.get(dimension.method_29177(), chunkX + 1, chunkZ);
        IPlayerChunkClaimAPI bottomClaim = this.claimsManager.get(dimension.method_29177(), chunkX, chunkZ + 1);
        IPlayerChunkClaimAPI leftClaim = this.claimsManager.get(dimension.method_29177(), chunkX - 1, chunkZ);
        IClientPlayerClaimInfoAPI claimInfo = this.claimsManager.getPlayerInfo(currentClaim.getPlayerId());
        int claimColor = this.getClaimsColor(currentClaim, (IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>>)claimInfo);
        int claimColorFormatted = (claimColor & 0xFF) << 24 | (claimColor >> 8 & 0xFF) << 16 | (claimColor >> 16 & 0xFF) << 8;
        int fillOpacity = WorldMap.settings.claimsFillOpacity;
        int borderOpacity = WorldMap.settings.claimsBorderOpacity;
        int centerColor = claimColorFormatted | 255 * fillOpacity / 100;
        int sideColor = claimColorFormatted | 255 * borderOpacity / 100;
        this.resultStore[0] = centerColor;
        this.resultStore[1] = topClaim != currentClaim ? sideColor : centerColor;
        this.resultStore[2] = rightClaim != currentClaim ? sideColor : centerColor;
        this.resultStore[3] = bottomClaim != currentClaim ? sideColor : centerColor;
        this.resultStore[4] = leftClaim != currentClaim ? sideColor : centerColor;
        return this.resultStore;
    }

    @Override
    public int calculateRegionHash(class_5321<class_1937> dimension, int regionX, int regionZ) {
        if (!WorldMap.settings.displayClaims) {
            return 0;
        }
        IClientDimensionClaimsManagerAPI claimsDimension = this.claimsManager.getDimension(dimension.method_29177());
        if (claimsDimension == null) {
            return 0;
        }
        IClientRegionClaimsAPI claimsRegion = claimsDimension.getRegion(regionX, regionZ);
        if (claimsRegion == null) {
            return 0;
        }
        IClientRegionClaimsAPI topRegion = claimsDimension.getRegion(regionX, regionZ - 1);
        IClientRegionClaimsAPI rightRegion = claimsDimension.getRegion(regionX + 1, regionZ);
        IClientRegionClaimsAPI bottomRegion = claimsDimension.getRegion(regionX, regionZ + 1);
        IClientRegionClaimsAPI leftRegion = claimsDimension.getRegion(regionX - 1, regionZ);
        long accumulator = 0L;
        accumulator = accumulator * 37L + (long)WorldMap.settings.claimsBorderOpacity;
        accumulator = accumulator * 37L + (long)WorldMap.settings.claimsFillOpacity;
        for (int i = 0; i < 32; ++i) {
            accumulator = this.accountClaim(accumulator, topRegion != null ? topRegion.get(i, 31) : null);
            accumulator = this.accountClaim(accumulator, rightRegion != null ? rightRegion.get(0, i) : null);
            accumulator = this.accountClaim(accumulator, bottomRegion != null ? bottomRegion.get(i, 0) : null);
            accumulator = this.accountClaim(accumulator, leftRegion != null ? leftRegion.get(31, i) : null);
            for (int j = 0; j < 32; ++j) {
                IPlayerChunkClaimAPI claim = claimsRegion.get(i, j);
                accumulator = this.accountClaim(accumulator, claim);
            }
        }
        return (int)(accumulator >> 32) * 37 + (int)(accumulator & 0xFFFFFFFFFFFFFFFFL);
    }

    private long accountClaim(long accumulator, IPlayerChunkClaimAPI claim) {
        if (claim != null) {
            UUID playerId = claim.getPlayerId();
            accumulator += playerId.getLeastSignificantBits();
            accumulator *= 37L;
            accumulator += claim.getPlayerId().getMostSignificantBits();
            accumulator *= 37L;
            IClientPlayerClaimInfoAPI claimInfo = this.claimsManager.getPlayerInfo(playerId);
            accumulator += (long)this.getClaimsColor(claim, (IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>>)claimInfo);
            accumulator *= 37L;
            accumulator += claim.isForceloadable() ? 1L : 0L;
            accumulator *= 37L;
            accumulator += (long)claim.getSubConfigIndex();
        }
        return accumulator *= 37L;
    }

    @Override
    public boolean chunkIsHighlit(class_5321<class_1937> dimension, int chunkX, int chunkZ) {
        return this.claimsManager.get(dimension.method_29177(), chunkX, chunkZ) != null;
    }

    @Override
    public class_2561 getChunkHighlightSubtleTooltip(class_5321<class_1937> dimension, int chunkX, int chunkZ) {
        IPlayerChunkClaimAPI currentClaim = this.claimsManager.get(dimension.method_29177(), chunkX, chunkZ);
        if (currentClaim == null) {
            return null;
        }
        UUID currentClaimId = currentClaim.getPlayerId();
        IClientPlayerClaimInfoAPI claimInfo = this.claimsManager.getPlayerInfo(currentClaimId);
        String customName = this.getClaimsName(currentClaim, (IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>>)claimInfo);
        int actualClaimsColor = this.getClaimsColor(currentClaim, (IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>>)claimInfo);
        int claimsColor = actualClaimsColor | 0xFF000000;
        if (!Objects.equals(currentClaim, this.cachedTooltipFor) || this.cachedForClaimsColor != claimsColor || !Objects.equals(customName, this.cachedForCustomName)) {
            this.cachedTooltip = class_2561.method_43470((String)"\u25a1 ").method_27694(s -> s.method_36139(claimsColor));
            if (Objects.equals(currentClaimId, PlayerConfig.SERVER_CLAIM_UUID)) {
                this.cachedTooltip.method_10855().add(class_2561.method_43469((String)"gui.xaero_wm_pac_server_claim_tooltip", (Object[])new Object[]{currentClaim.isForceloadable() ? class_2561.method_43471((String)"gui.xaero_wm_pac_marked_for_forceload") : ""}).method_27692(class_124.field_1068));
            } else if (Objects.equals(currentClaimId, PlayerConfig.EXPIRED_CLAIM_UUID)) {
                this.cachedTooltip.method_10855().add(class_2561.method_43469((String)"gui.xaero_wm_pac_expired_claim_tooltip", (Object[])new Object[]{currentClaim.isForceloadable() ? class_2561.method_43471((String)"gui.xaero_wm_pac_marked_for_forceload") : ""}).method_27692(class_124.field_1068));
            } else {
                this.cachedTooltip.method_10855().add(class_2561.method_43469((String)"gui.xaero_wm_pac_claim_tooltip", (Object[])new Object[]{claimInfo.getPlayerUsername(), currentClaim.isForceloadable() ? class_2561.method_43471((String)"gui.xaero_wm_pac_marked_for_forceload") : ""}).method_27692(class_124.field_1068));
            }
            if (!customName.isEmpty()) {
                this.cachedTooltip.method_10855().add(0, class_2561.method_43470((String)(class_1074.method_4662((String)customName, (Object[])new Object[0]) + " - ")).method_27692(class_124.field_1068));
            }
            this.cachedTooltipFor = currentClaim;
            this.cachedForCustomName = customName;
            this.cachedForClaimsColor = claimsColor;
        }
        return this.cachedTooltip;
    }

    @Override
    public class_2561 getChunkHighlightBluntTooltip(class_5321<class_1937> dimension, int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public void addMinimapBlockHighlightTooltips(List<class_2561> list, class_5321<class_1937> dimension, int blockX, int blockZ, int width) {
    }

    private String getClaimsName(IPlayerChunkClaimAPI currentClaim, IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>> claimInfo) {
        int subConfigIndex = currentClaim.getSubConfigIndex();
        String customName = claimInfo.getClaimsName(subConfigIndex);
        if (subConfigIndex != -1 && customName == null) {
            customName = claimInfo.getClaimsName();
        }
        return customName;
    }

    private int getClaimsColor(IPlayerChunkClaimAPI currentClaim, IClientPlayerClaimInfoAPI<IPlayerDimensionClaimsAPI<IPlayerClaimPosListAPI>> claimInfo) {
        int subConfigIndex = currentClaim.getSubConfigIndex();
        Integer actualClaimsColor = claimInfo.getClaimsColor(subConfigIndex);
        if (subConfigIndex != -1 && actualClaimsColor == null) {
            actualClaimsColor = claimInfo.getClaimsColor();
        }
        return actualClaimsColor;
    }
}

