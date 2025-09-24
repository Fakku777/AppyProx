/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 */
package xaero.map.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import xaero.map.WorldMap;
import xaero.map.server.IMinecraftServer;
import xaero.map.server.level.LevelMapProperties;
import xaero.map.server.level.LevelMapPropertiesIO;
import xaero.map.server.radar.tracker.SyncedPlayerTracker;
import xaero.map.server.radar.tracker.SyncedPlayerTrackerSystemManager;

public class MinecraftServerData {
    private final SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager;
    private final SyncedPlayerTracker syncedPlayerTracker;
    private final Map<Path, LevelMapProperties> levelProperties;
    private final LevelMapPropertiesIO propertiesIO;

    public MinecraftServerData(SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager, SyncedPlayerTracker syncedPlayerTracker) {
        this.syncedPlayerTrackerSystemManager = syncedPlayerTrackerSystemManager;
        this.syncedPlayerTracker = syncedPlayerTracker;
        this.levelProperties = new HashMap<Path, LevelMapProperties>();
        this.propertiesIO = new LevelMapPropertiesIO();
    }

    public LevelMapProperties getLevelProperties(Path path) {
        LevelMapProperties properties = this.levelProperties.get(path);
        if (properties == null) {
            properties = new LevelMapProperties();
            try {
                this.propertiesIO.load(path, properties);
            }
            catch (FileNotFoundException fnfe) {
                try {
                    this.propertiesIO.save(path, properties);
                }
                catch (IOException e) {
                    properties.setUsable(false);
                    WorldMap.LOGGER.warn("Failed to initialize map properties for a world due to an IO exception. This shouldn't be a problem if it's not a \"real\" world. Message: {}", (Object)e.getMessage());
                    if (WorldMap.settings.debug) {
                        WorldMap.LOGGER.warn("Full exception: ", (Throwable)e);
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.levelProperties.put(path, properties);
        }
        return properties;
    }

    public SyncedPlayerTrackerSystemManager getSyncedPlayerTrackerSystemManager() {
        return this.syncedPlayerTrackerSystemManager;
    }

    public SyncedPlayerTracker getSyncedPlayerTracker() {
        return this.syncedPlayerTracker;
    }

    public static MinecraftServerData get(MinecraftServer server) {
        return ((IMinecraftServer)server).getXaeroWorldMapServerData();
    }
}

