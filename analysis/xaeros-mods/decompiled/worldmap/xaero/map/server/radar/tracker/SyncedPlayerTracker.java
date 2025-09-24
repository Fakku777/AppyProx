/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_3222
 *  net.minecraft.server.MinecraftServer
 */
package xaero.map.server.radar.tracker;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import xaero.map.WorldMap;
import xaero.map.message.tracker.ClientboundTrackedPlayerPacket;
import xaero.map.server.MinecraftServerData;
import xaero.map.server.mods.SupportServerMods;
import xaero.map.server.player.ServerPlayerData;
import xaero.map.server.radar.tracker.ISyncedPlayerTrackerSystem;
import xaero.map.server.radar.tracker.SyncedTrackedPlayer;

public class SyncedPlayerTracker {
    public void onTick(MinecraftServer server, class_3222 player, MinecraftServerData serverData, ServerPlayerData playerData) {
        boolean opacReceiveMutualAllies;
        boolean shouldSyncToOthers;
        boolean playerHasMod;
        long currentTime = System.currentTimeMillis();
        if (currentTime - playerData.getLastTrackedPlayerSync() < 250L) {
            return;
        }
        playerData.setLastTrackedPlayerSync(currentTime);
        boolean shouldSyncToPlayer = playerHasMod = playerData.hasMod();
        if (SupportServerMods.hasMinimap() && SupportServerMods.getMinimap().supportsTrackedPlayers() && SupportServerMods.getMinimap().playerSupportsTrackedPlayers(player)) {
            if (playerData.getCurrentlySyncedPlayers() != null && !playerData.getCurrentlySyncedPlayers().isEmpty()) {
                for (UUID id : playerData.getCurrentlySyncedPlayers()) {
                    this.sendRemovePacket(player, playerData, id);
                }
                playerData.getCurrentlySyncedPlayers().clear();
            }
            shouldSyncToPlayer = false;
        }
        boolean everyoneIsTracked = WorldMap.commonConfig.everyoneTracksEveryone;
        Iterable<ISyncedPlayerTrackerSystem> playerTrackerSystems = serverData.getSyncedPlayerTrackerSystemManager().getSystems();
        Set<UUID> syncedPlayers = playerData.ensureCurrentlySyncedPlayers();
        HashSet<UUID> leftoverPlayers = new HashSet<UUID>(syncedPlayers);
        SyncedTrackedPlayer toSync = playerData.getLastSyncedData();
        boolean bl = shouldSyncToOthers = toSync == null || !toSync.matchesEnough((class_1657)player, 0.0);
        if (shouldSyncToOthers) {
            toSync = playerData.ensureLastSyncedData();
            toSync.update((class_1657)player);
        }
        boolean opacReceiveParty = SupportServerMods.hasOpac() && SupportServerMods.getOpac().getReceiveLocationsFromPartyConfigValue(player);
        boolean bl2 = opacReceiveMutualAllies = SupportServerMods.hasOpac() && SupportServerMods.getOpac().getReceiveLocationsFromMutualAlliesConfigValue(player);
        if (SupportServerMods.hasOpac()) {
            SupportServerMods.getOpac().updateShareLocationConfigValues(player, playerData);
        }
        for (class_3222 otherPlayer : server.method_3760().method_14571()) {
            Set<UUID> otherPlayerSyncedPlayers;
            if (otherPlayer == player) continue;
            leftoverPlayers.remove(otherPlayer.method_5667());
            ServerPlayerData otherPlayerData = ServerPlayerData.get(otherPlayer);
            if (shouldSyncToOthers && (otherPlayerSyncedPlayers = otherPlayerData.getCurrentlySyncedPlayers()) != null && otherPlayerSyncedPlayers.contains(player.method_5667())) {
                this.sendTrackedPlayerPacket(otherPlayer, otherPlayerData, toSync);
            }
            if (!shouldSyncToPlayer) continue;
            boolean tracked = everyoneIsTracked;
            if (!tracked) {
                boolean opacConfigsAllowPartySync = !SupportServerMods.hasOpac() || SupportServerMods.getOpac().isPositionSyncAllowed(2, otherPlayerData, opacReceiveParty);
                boolean opacConfigsAllowAllySync = !SupportServerMods.hasOpac() || SupportServerMods.getOpac().isPositionSyncAllowed(1, otherPlayerData, opacReceiveMutualAllies);
                for (ISyncedPlayerTrackerSystem system : playerTrackerSystems) {
                    int trackingLevel = system.getTrackingLevel((class_1657)player, (class_1657)otherPlayer);
                    if (trackingLevel <= 0 || system.isPartySystem() && (trackingLevel != 1 || !opacConfigsAllowAllySync) && (trackingLevel <= 1 || !opacConfigsAllowPartySync)) continue;
                    tracked = true;
                    break;
                }
            }
            boolean alreadySynced = syncedPlayers.contains(otherPlayer.method_5667());
            if (!tracked) {
                if (!alreadySynced) continue;
                syncedPlayers.remove(otherPlayer.method_5667());
                this.sendRemovePacket(player, playerData, otherPlayer.method_5667());
                continue;
            }
            if (alreadySynced || otherPlayerData.getLastSyncedData() == null) continue;
            syncedPlayers.add(otherPlayer.method_5667());
            this.sendTrackedPlayerPacket(player, playerData, otherPlayerData.getLastSyncedData());
        }
        for (UUID offlineId : leftoverPlayers) {
            syncedPlayers.remove(offlineId);
            this.sendRemovePacket(player, playerData, offlineId);
        }
    }

    private void sendRemovePacket(class_3222 player, ServerPlayerData playerData, UUID toRemove) {
        WorldMap.messageHandler.sendToPlayer(player, new ClientboundTrackedPlayerPacket(true, toRemove, 0.0, 0.0, 0.0, null, playerData.getClientModNetworkVersion()));
    }

    private void sendTrackedPlayerPacket(class_3222 player, ServerPlayerData playerData, SyncedTrackedPlayer tracked) {
        WorldMap.messageHandler.sendToPlayer(player, new ClientboundTrackedPlayerPacket(false, tracked.getId(), tracked.getX(), tracked.getY(), tracked.getZ(), tracked.getDimension().method_29177(), playerData.getClientModNetworkVersion()));
    }
}

