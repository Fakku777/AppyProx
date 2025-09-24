/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_5321
 *  net.minecraft.class_638
 */
package xaero.map.world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_5321;
import net.minecraft.class_638;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.file.MapSaveLoad;
import xaero.map.gui.GuiDimensionOptions;
import xaero.map.gui.GuiMapSwitching;
import xaero.map.world.MapConnectionManager;
import xaero.map.world.MapConnectionNode;
import xaero.map.world.MapDimension;

public class MapWorld {
    private MapProcessor mapProcessor;
    private boolean isMultiplayer;
    private String mainId;
    private String oldUnfixedMainId;
    private Hashtable<class_5321<class_1937>, MapDimension> dimensions;
    private class_5321<class_1937> currentDimensionId;
    private class_5321<class_1937> futureDimensionId;
    private class_5321<class_1937> customDimensionId;
    private int futureMultiworldType;
    private int currentMultiworldType;
    private boolean futureMultiworldTypeConfirmed = true;
    private boolean currentMultiworldTypeConfirmed = false;
    private boolean ignoreServerLevelId;
    private boolean ignoreHeightmaps;
    private String playerTeleportCommandFormat = "/tp @s {name}";
    private String normalTeleportCommandFormat = "/tp @s {x} {y} {z}";
    private String dimensionTeleportCommandFormat = "/execute as @s in {d} run tp {x} {y} {z}";
    private boolean teleportAllowed = true;
    private MapConnectionManager mapConnections;

    public MapWorld(String mainId, String oldUnfixedMainId, MapProcessor mapProcessor) {
        this.mainId = mainId;
        this.oldUnfixedMainId = oldUnfixedMainId;
        this.mapProcessor = mapProcessor;
        this.isMultiplayer = MapProcessor.isWorldMultiplayer(MapProcessor.isWorldRealms(mainId), mainId);
        this.dimensions = new Hashtable();
        this.currentMultiworldType = 0;
        this.futureMultiworldType = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapDimension getDimension(class_5321<class_1937> dimId) {
        if (dimId == null) {
            return null;
        }
        Hashtable<class_5321<class_1937>, MapDimension> hashtable = this.dimensions;
        synchronized (hashtable) {
            return this.dimensions.get(dimId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapDimension createDimensionUnsynced(class_5321<class_1937> dimId) {
        Hashtable<class_5321<class_1937>, MapDimension> hashtable = this.dimensions;
        synchronized (hashtable) {
            MapDimension result = this.dimensions.get(dimId);
            if (result == null) {
                result = new MapDimension(this, dimId, this.mapProcessor.getHighlighterRegistry());
                this.dimensions.put(dimId, result);
                result.onCreationUnsynced();
            }
            return result;
        }
    }

    public String getMainId() {
        return this.mainId;
    }

    public String getOldUnfixedMainId() {
        return this.oldUnfixedMainId;
    }

    public String getCurrentMultiworld() {
        MapDimension container = this.getDimension(this.currentDimensionId);
        return container.getCurrentMultiworld();
    }

    public String getFutureMultiworldUnsynced() {
        MapDimension container = this.getDimension(this.futureDimensionId);
        return container.getFutureMultiworldUnsynced();
    }

    public MapDimension getCurrentDimension() {
        class_5321<class_1937> dimId = this.currentDimensionId;
        if (dimId == null) {
            return null;
        }
        return this.getDimension(dimId);
    }

    public MapDimension getFutureDimension() {
        class_5321<class_1937> dimId = this.futureDimensionId;
        if (dimId == null) {
            return null;
        }
        return this.getDimension(dimId);
    }

    public class_5321<class_1937> getCurrentDimensionId() {
        return this.currentDimensionId;
    }

    public class_5321<class_1937> getFutureDimensionId() {
        return this.futureDimensionId;
    }

    public void setFutureDimensionId(class_5321<class_1937> dimension) {
        this.futureDimensionId = dimension;
    }

    public class_5321<class_1937> getCustomDimensionId() {
        return this.customDimensionId;
    }

    public void setCustomDimensionId(class_5321<class_1937> dimension) {
        this.customDimensionId = dimension;
    }

    public void switchToFutureUnsynced() {
        this.currentDimensionId = this.futureDimensionId;
        this.getDimension(this.currentDimensionId).switchToFutureUnsynced();
    }

    public List<MapDimension> getDimensionsList() {
        ArrayList<MapDimension> destList = new ArrayList<MapDimension>();
        this.getDimensions(destList);
        return destList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void getDimensions(List<MapDimension> dest) {
        Hashtable<class_5321<class_1937>, MapDimension> hashtable = this.dimensions;
        synchronized (hashtable) {
            dest.addAll(this.dimensions.values());
        }
    }

    public int getCurrentMultiworldType() {
        return this.currentMultiworldType;
    }

    public boolean isMultiplayer() {
        return this.isMultiplayer;
    }

    public boolean isCurrentMultiworldTypeConfirmed() {
        return this.currentMultiworldTypeConfirmed;
    }

    public int getFutureMultiworldType(MapDimension dim) {
        return dim.isFutureMultiworldServerBased() ? 2 : this.futureMultiworldType;
    }

    public void toggleMultiworldTypeUnsynced() {
        this.unconfirmMultiworldTypeUnsynced();
        this.futureMultiworldType = (this.futureMultiworldType + 1) % 3;
        this.getCurrentDimension().resetCustomMultiworldUnsynced();
        this.saveConfig();
    }

    public void unconfirmMultiworldTypeUnsynced() {
        this.futureMultiworldTypeConfirmed = false;
    }

    public void confirmMultiworldTypeUnsynced() {
        this.futureMultiworldTypeConfirmed = true;
    }

    public boolean isFutureMultiworldTypeConfirmed(MapDimension dim) {
        return dim.isFutureMultiworldServerBased() ? true : this.futureMultiworldTypeConfirmed;
    }

    public void switchToFutureMultiworldTypeUnsynced() {
        MapDimension futureDim = this.getFutureDimension();
        this.currentMultiworldType = this.getFutureMultiworldType(this.getFutureDimension());
        this.currentMultiworldTypeConfirmed = this.isFutureMultiworldTypeConfirmed(futureDim);
    }

    public void load() {
        this.mapConnections = this.isMultiplayer ? new MapConnectionManager() : new MapConnectionManager(this){

            @Override
            public boolean isConnected(MapConnectionNode mapKey1, MapConnectionNode mapKey2) {
                return true;
            }

            @Override
            public void save(PrintWriter writer) {
            }
        };
        Path rootSavePath = MapSaveLoad.getRootFolder(this.mainId);
        this.loadConfig(rootSavePath, 10);
        try (Stream<Path> stream = Files.list(rootSavePath);){
            stream.forEach(folder -> {
                if (!Files.isDirectory(folder, new LinkOption[0])) {
                    return;
                }
                String folderName = folder.getFileName().toString();
                class_5321<class_1937> folderDimensionId = this.mapProcessor.getDimensionIdForFolder(folderName);
                if (folderDimensionId == null) {
                    return;
                }
                this.createDimensionUnsynced(folderDimensionId);
            });
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    private void loadConfig(Path rootSavePath, int attempts) {
        MapProcessor mp = this.mapProcessor;
        BufferedReader reader = null;
        try {
            if (!Files.exists(rootSavePath, new LinkOption[0])) {
                Files.createDirectories(rootSavePath, new FileAttribute[0]);
            }
            Path configFile = rootSavePath.resolve("server_config.txt");
            Path oldOverworldSavePath = mp.getMapSaveLoad().getOldFolder(this.oldUnfixedMainId, "null");
            Path oldConfigFile = oldOverworldSavePath.resolve("server_config.txt");
            if (!Files.exists(configFile, new LinkOption[0]) && Files.exists(oldConfigFile, new LinkOption[0])) {
                Files.move(oldConfigFile, configFile, new CopyOption[0]);
            }
            if (Files.exists(configFile, new LinkOption[0])) {
                String line;
                reader = new BufferedReader(new FileReader(configFile.toFile()));
                while ((line = reader.readLine()) != null) {
                    String[] args = line.split(":");
                    if (this.isMultiplayer) {
                        if (args[0].equals("multiworldType")) {
                            this.futureMultiworldType = Integer.parseInt(args[1]);
                        } else if (args[0].equals("ignoreServerLevelId")) {
                            this.ignoreServerLevelId = args[1].equals("true");
                        }
                    }
                    if (args[0].equals("ignoreHeightmaps")) {
                        this.ignoreHeightmaps = args[1].equals("true");
                        continue;
                    }
                    if (args[0].equals("playerTeleportCommandFormat")) {
                        this.playerTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                        continue;
                    }
                    if (args[0].equals("teleportCommandFormat")) {
                        this.normalTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                        this.dimensionTeleportCommandFormat = "/execute as @s in {d} run " + this.normalTeleportCommandFormat.substring(1);
                        continue;
                    }
                    if (args[0].equals("dimensionTeleportCommandFormat")) {
                        this.dimensionTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                        continue;
                    }
                    if (args[0].equals("normalTeleportCommandFormat")) {
                        this.normalTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                        continue;
                    }
                    if (args[0].equals("teleportAllowed")) {
                        this.teleportAllowed = args[1].equals("true");
                        continue;
                    }
                    if (!this.isMultiplayer || !args[0].equals("connection")) continue;
                    String mapKey1 = args[1];
                    if (args.length <= 2) continue;
                    String mapKey2 = args[2];
                    MapConnectionNode connectionNode1 = MapConnectionNode.fromString(mapKey1);
                    MapConnectionNode connectionNode2 = MapConnectionNode.fromString(mapKey2);
                    if (connectionNode1 == null || connectionNode2 == null) continue;
                    this.mapConnections.addConnection(connectionNode1, connectionNode2);
                }
            } else {
                this.saveConfig();
            }
        }
        catch (IOException e1) {
            if (attempts > 1) {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                WorldMap.LOGGER.warn("IO exception while loading world map config. Retrying... " + attempts);
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                this.loadConfig(rootSavePath, attempts - 1);
                return;
            }
            throw new RuntimeException(e1);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void saveConfig() {
        Path rootSavePath = MapSaveLoad.getRootFolder(this.mainId);
        try (PrintWriter writer = null;){
            writer = new PrintWriter(new FileWriter(rootSavePath.resolve("server_config.txt").toFile()));
            if (this.isMultiplayer) {
                writer.println("multiworldType:" + this.futureMultiworldType);
                writer.println("ignoreServerLevelId:" + this.ignoreServerLevelId);
            }
            writer.println("ignoreHeightmaps:" + this.ignoreHeightmaps);
            writer.println("playerTeleportCommandFormat:" + this.playerTeleportCommandFormat);
            writer.println("normalTeleportCommandFormat:" + this.normalTeleportCommandFormat);
            writer.println("dimensionTeleportCommandFormat:" + this.dimensionTeleportCommandFormat);
            writer.println("teleportAllowed:" + this.teleportAllowed);
            if (this.isMultiplayer) {
                this.mapConnections.save(writer);
            }
        }
    }

    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }

    public boolean isIgnoreServerLevelId() {
        return this.ignoreServerLevelId;
    }

    public boolean isIgnoreHeightmaps() {
        return this.ignoreHeightmaps;
    }

    public void setIgnoreHeightmaps(boolean ignoreHeightmaps) {
        this.ignoreHeightmaps = ignoreHeightmaps;
    }

    public String getPlayerTeleportCommandFormat() {
        return this.playerTeleportCommandFormat;
    }

    public void setPlayerTeleportCommandFormat(String playerTeleportCommandFormat) {
        this.playerTeleportCommandFormat = playerTeleportCommandFormat;
    }

    public String getTeleportCommandFormat() {
        return this.normalTeleportCommandFormat;
    }

    public void setTeleportCommandFormat(String teleportCommandFormat) {
        this.normalTeleportCommandFormat = teleportCommandFormat;
    }

    public String getDimensionTeleportCommandFormat() {
        return this.dimensionTeleportCommandFormat;
    }

    public void setDimensionTeleportCommandFormat(String dimensionTeleportCommandFormat) {
        this.dimensionTeleportCommandFormat = dimensionTeleportCommandFormat;
    }

    public boolean isTeleportAllowed() {
        return this.teleportAllowed;
    }

    public void setTeleportAllowed(boolean teleportAllowed) {
        this.teleportAllowed = teleportAllowed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearAllCachedHighlightHashes() {
        Hashtable<class_5321<class_1937>, MapDimension> hashtable = this.dimensions;
        synchronized (hashtable) {
            for (MapDimension dim : this.dimensions.values()) {
                dim.getHighlightHandler().clearCachedHashes();
            }
        }
    }

    public boolean isUsingCustomDimension() {
        class_638 world = this.mapProcessor.getWorld();
        return world != null && world.method_27983() != this.getCurrentDimensionId();
    }

    public boolean isUsingUnknownDimensionType() {
        return this.getCurrentDimension().isUsingUnknownDimensionType(this.mapProcessor.getWorldDimensionTypeRegistry());
    }

    public boolean isCacheOnlyMode() {
        return this.getCurrentDimension().isCacheOnlyMode(this.mapProcessor.getWorldDimensionTypeRegistry());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onWorldChangeUnsynced(class_638 world) {
        Hashtable<class_5321<class_1937>, MapDimension> hashtable = this.dimensions;
        synchronized (hashtable) {
            for (MapDimension dim : this.dimensions.values()) {
                dim.onWorldChangeUnsynced((class_1937)world);
            }
        }
    }

    public class_5321<class_1937> getPotentialDimId() {
        class_5321 customDimId = this.getCustomDimensionId();
        return customDimId == null ? this.mapProcessor.mainWorld.method_27983() : customDimId;
    }

    public MapConnectionNode getPlayerMapKey() {
        this.mapProcessor.updateVisitedDimension(this.mapProcessor.mainWorld);
        class_5321 dimId = this.mapProcessor.mainWorld.method_27983();
        MapDimension dim = this.getDimension((class_5321<class_1937>)dimId);
        return dim == null ? null : dim.getPlayerMapKey();
    }

    public MapConnectionManager getMapConnections() {
        return this.mapConnections;
    }

    public void toggleDimension(boolean forward) {
        MapDimension futureDimension = this.getFutureDimension();
        if (futureDimension == null) {
            return;
        }
        GuiDimensionOptions dimOptions = GuiMapSwitching.getSortedDimensionOptions(futureDimension);
        int step = forward ? 1 : dimOptions.values.length - 1;
        int nextIndex = dimOptions.selected == -1 ? 0 : (dimOptions.selected + step) % dimOptions.values.length;
        class_5321<class_1937> nextDimId = dimOptions.values[nextIndex];
        if (nextDimId == class_310.method_1551().field_1687.method_27983()) {
            nextDimId = null;
        }
        this.setCustomDimensionId(nextDimId);
        this.mapProcessor.checkForWorldUpdate();
    }

    public static String convertWorldFolderToRootId(int version, String worldFolder) {
        Object rootId = worldFolder.replaceAll("_", "^us^");
        if (MapProcessor.isWorldMultiplayer(MapProcessor.isWorldRealms((String)rootId), (String)rootId)) {
            rootId = "^e^" + (String)rootId;
        }
        if (version >= 3) {
            rootId = ((String)rootId).replace("[", "%lb%").replace("]", "%rb%");
        }
        return rootId;
    }
}

