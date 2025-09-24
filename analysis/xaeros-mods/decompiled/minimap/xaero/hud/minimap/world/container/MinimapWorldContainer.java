/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  net.minecraft.class_1937
 *  net.minecraft.class_5321
 */
package xaero.hud.minimap.world.container;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1937;
import net.minecraft.class_5321;
import xaero.common.HudMod;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.server.ServerWaypointManager;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.minimap.world.container.config.RootConfig;
import xaero.hud.path.XaeroPath;

public class MinimapWorldContainer {
    private final HudMod modMain;
    protected final MinimapSession session;
    protected final Map<String, MinimapWorldContainer> subContainers;
    protected final Map<String, MinimapWorld> worlds;
    private final Map<String, String> worldNames;
    private final MinimapWorldRootContainer rootContainer;
    private final ServerWaypointManager serverWaypointManager;
    protected XaeroPath path;

    protected MinimapWorldContainer(HudMod modMain, MinimapSession session, Map<String, MinimapWorldContainer> subContainers, Map<String, MinimapWorld> worlds, Map<String, String> worldNames, ServerWaypointManager serverWaypointManager, XaeroPath path, MinimapWorldRootContainer rootContainer) {
        this.subContainers = subContainers;
        this.worlds = worlds;
        this.worldNames = worldNames;
        this.serverWaypointManager = serverWaypointManager;
        if (path.getLastNode().contains(":")) {
            throw new IllegalArgumentException();
        }
        this.modMain = modMain;
        this.session = session;
        this.path = path;
        this.rootContainer = rootContainer;
    }

    public void setPath(XaeroPath path) {
        if (path.getLastNode().contains(":")) {
            throw new IllegalArgumentException();
        }
        this.path = path;
        for (MinimapWorldContainer s : this.subContainers.values()) {
            s.setPath(path.resolve(s.getLastNode()));
        }
    }

    public MinimapWorldContainer addSubContainer(XaeroPath containerPath) {
        if (containerPath.getNodeCount() <= this.path.getNodeCount()) {
            throw new IllegalArgumentException();
        }
        String nextNode = containerPath.getAtIndex(this.path.getNodeCount()).getLastNode();
        MinimapWorldContainer sub = this.subContainers.get(nextNode);
        if (sub == null) {
            sub = ((FinalBuilder)((FinalBuilder)((FinalBuilder)((FinalBuilder)FinalBuilder.begin().setModMain(this.modMain)).setSession(this.session)).setPath(this.path.resolve(nextNode))).setRootContainer(this.getRoot())).build();
            this.subContainers.put(nextNode, sub);
        }
        if (containerPath.getNodeCount() > this.path.getNodeCount() + 1) {
            return sub.addSubContainer(containerPath);
        }
        return sub;
    }

    public boolean containsSubContainer(XaeroPath containerPath) {
        if (containerPath.getNodeCount() <= this.path.getNodeCount()) {
            throw new IllegalArgumentException();
        }
        String nextNode = containerPath.getAtIndex(this.path.getNodeCount()).getLastNode();
        MinimapWorldContainer sub = this.subContainers.get(nextNode);
        if (sub == null) {
            return false;
        }
        if (containerPath.getNodeCount() == this.path.getNodeCount() + 1) {
            return true;
        }
        return sub.containsSubContainer(containerPath);
    }

    public boolean deleteSubContainer(XaeroPath containerPath) {
        if (containerPath.getNodeCount() <= this.path.getNodeCount()) {
            throw new IllegalArgumentException();
        }
        if (containerPath.getNodeCount() == this.path.getNodeCount() + 1) {
            return this.subContainers.remove(containerPath.getLastNode()) != null;
        }
        MinimapWorldContainer sub = this.subContainers.get(containerPath.getAtIndex(this.path.getNodeCount()).getLastNode());
        if (sub == null) {
            return false;
        }
        return sub.deleteSubContainer(containerPath);
    }

    public boolean isEmpty() {
        return this.subContainers.isEmpty() && this.worlds.isEmpty();
    }

    public MinimapWorld addWorld(String worldNode) {
        MinimapWorld world = this.worlds.get(worldNode);
        if (world != null) {
            return world;
        }
        MinimapWorld defaultWorld = this.worlds.get("waypoints");
        if (defaultWorld == null) {
            class_5321<class_1937> dimId = this.path.getNodeCount() < 2 ? null : this.session.getDimensionHelper().getDimensionKeyForDirectoryName(this.path.getAtIndex(1).getLastNode());
            world = MinimapWorld.Builder.begin().setContainer(this).setNode(worldNode).setDimId(dimId).build();
            this.worlds.put(worldNode, world);
            return world;
        }
        this.worlds.put(worldNode, defaultWorld);
        try {
            Path defaultFile = this.session.getWorldManagerIO().getWorldFile(defaultWorld);
            defaultWorld.setNode(worldNode);
            Path fixedFile = this.session.getWorldManagerIO().getWorldFile(defaultWorld);
            if (Files.exists(defaultFile, new LinkOption[0])) {
                Files.move(defaultFile, fixedFile, new CopyOption[0]);
            }
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        this.worlds.remove("waypoints");
        world = defaultWorld;
        return world;
    }

    public void addWorld(MinimapWorld world) {
        if (this.worlds.containsKey(world.getNode())) {
            throw new IllegalArgumentException();
        }
        this.worlds.put(world.getNode(), world);
    }

    public void removeWorld(String worldNode) {
        this.worlds.remove(worldNode);
    }

    public void setName(String worldNode, String name) {
        String current = this.worldNames.get(worldNode);
        if (current != null && !current.equals(name)) {
            this.worlds.get(worldNode).requestRemovalOnSave(current);
        }
        this.worldNames.put(worldNode, name);
    }

    public String getName(String worldNode) {
        if (worldNode.equals("waypoints")) {
            return null;
        }
        Object name = this.worldNames.get(worldNode);
        if (name != null) {
            return name;
        }
        int numericName = this.worldNames.size() + 1;
        while (this.worldNames.containsValue(name = "" + numericName++)) {
        }
        this.setName(worldNode, (String)name);
        return name;
    }

    public void removeName(String worldNode) {
        this.worldNames.remove(worldNode);
    }

    public String getLastNode() {
        return this.path.getLastNode();
    }

    public String getSubName() {
        String subName = this.getLastNode();
        if (!subName.startsWith("dim%")) {
            return subName;
        }
        class_5321<class_1937> dimensionKey = this.session.getDimensionHelper().getDimensionKeyForDirectoryName(subName);
        if (dimensionKey == null) {
            return "Dim. " + subName.substring(4);
        }
        if (dimensionKey.method_29177().method_12836().equals("minecraft")) {
            return dimensionKey.method_29177().method_12832();
        }
        return dimensionKey.method_29177().toString();
    }

    public String getFullWorldName(String worldNode, String containerName) {
        String worldMapMWName;
        class_5321<class_1937> dimId;
        String dimNode;
        if (this.worlds.size() < 2 && !containerName.isEmpty()) {
            return containerName;
        }
        String worldName = this.getName(worldNode);
        String string = dimNode = this.path.getNodeCount() < 2 ? "" : this.path.getAtIndex(1).getLastNode();
        if (dimNode.startsWith("dim%") && (dimId = this.session.getDimensionHelper().getDimensionKeyForDirectoryName(dimNode)) != null && this.modMain.getSupportMods().worldmap() && this.getRoot().getPath().equals(this.session.getWorldState().getAutoRootContainerPath()) && (worldMapMWName = this.modMain.getSupportMods().worldmapSupport.tryToGetMultiworldName(dimId, worldNode)) != null && !worldMapMWName.equals(worldNode)) {
            worldName = worldMapMWName;
        }
        if (worldName == null) {
            return containerName;
        }
        return !containerName.isEmpty() ? worldName + " - " + containerName : worldName;
    }

    public XaeroPath getPath() {
        return this.path;
    }

    public MinimapWorld getFirstWorld() {
        if (!this.worlds.isEmpty()) {
            return this.worlds.values().stream().findFirst().orElse(null);
        }
        for (MinimapWorldContainer sub : this.subContainers.values()) {
            MinimapWorld subFirst = sub.getFirstWorld();
            if (subFirst == null) continue;
            return subFirst;
        }
        return null;
    }

    public MinimapWorld getFirstWorldConnectedTo(MinimapWorld refWorld) {
        if (!this.worlds.isEmpty()) {
            MinimapWorldRootContainer rootContainer = this.getRoot();
            for (MinimapWorld world : this.worlds.values()) {
                if (!rootContainer.getSubWorldConnections().isConnected(refWorld, world)) continue;
                return world;
            }
        }
        for (MinimapWorldContainer sub : this.subContainers.values()) {
            MinimapWorld subFirst = sub.getFirstWorldConnectedTo(refWorld);
            if (subFirst == null) continue;
            return subFirst;
        }
        return null;
    }

    public String toString() {
        return String.valueOf(this.path) + " sc:" + this.subContainers.size() + " w:" + this.worlds.size();
    }

    public Iterable<MinimapWorld> getWorlds() {
        return this.worlds.values();
    }

    public List<MinimapWorld> getWorldsCopy() {
        return new ArrayList<MinimapWorld>(this.worlds.values());
    }

    public Iterable<MinimapWorldContainer> getSubContainers() {
        return this.subContainers.values();
    }

    public Iterable<MinimapWorld> getAllWorldsIterable() {
        Iterable<MinimapWorld> allWorlds = this.worlds.values();
        for (MinimapWorldContainer sub : this.subContainers.values()) {
            allWorlds = Iterables.concat(allWorlds, sub.getAllWorldsIterable());
        }
        return allWorlds;
    }

    public XaeroPath fixPathCharacterCases(XaeroPath containerPath) {
        if (containerPath.equals(this.path)) {
            return this.path;
        }
        if (!containerPath.isSubOf(this.path)) {
            return null;
        }
        for (Map.Entry<String, MinimapWorldContainer> entry : this.subContainers.entrySet()) {
            XaeroPath subSearch = entry.getValue().fixPathCharacterCases(containerPath);
            if (subSearch == null) continue;
            return subSearch;
        }
        XaeroPath fixedContainerPath = this.path;
        for (int i = this.path.getNodeCount(); i < containerPath.getNodeCount(); ++i) {
            fixedContainerPath = fixedContainerPath.resolve(containerPath.getAtIndex(i).getLastNode());
        }
        return fixedContainerPath;
    }

    public MinimapWorldRootContainer getRoot() {
        return this.rootContainer;
    }

    public RootConfig getRootConfig() {
        return this.getRoot().getConfig();
    }

    public Path getDirectoryPath() {
        Path worldFolder = this.modMain.getMinimapFolder();
        return this.path.applyToFilePath(worldFolder);
    }

    public MinimapSession getSession() {
        return this.session;
    }

    public ServerWaypointManager getServerWaypointManager() {
        return this.serverWaypointManager;
    }

    public static final class FinalBuilder
    extends Builder<FinalBuilder> {
        private FinalBuilder() {
        }

        @Override
        public MinimapWorldContainer build() {
            if (this.rootContainer == null) {
                throw new IllegalStateException();
            }
            return super.build();
        }

        @Override
        protected MinimapWorldContainer buildInternally(Map<String, MinimapWorldContainer> subContainers, Map<String, MinimapWorld> worlds, Map<String, String> worldNames, ServerWaypointManager serverWaypointManager) {
            return new MinimapWorldContainer(this.modMain, this.session, subContainers, worlds, worldNames, serverWaypointManager, this.path, this.rootContainer);
        }

        public static FinalBuilder begin() {
            return (FinalBuilder)new FinalBuilder().setDefault();
        }
    }

    public static abstract class Builder<B extends Builder> {
        protected final B self = this;
        protected HudMod modMain;
        protected MinimapSession session;
        protected XaeroPath path;
        protected MinimapWorldRootContainer rootContainer;

        protected Builder() {
        }

        public B setDefault() {
            this.setModMain(null);
            this.setSession(null);
            this.setPath(null);
            this.setRootContainer(null);
            return this.self;
        }

        public B setModMain(HudMod modMain) {
            this.modMain = modMain;
            return this.self;
        }

        public B setSession(MinimapSession session) {
            this.session = session;
            return this.self;
        }

        public B setPath(XaeroPath path) {
            this.path = path;
            return this.self;
        }

        public B setRootContainer(MinimapWorldRootContainer rootContainer) {
            this.rootContainer = rootContainer;
            return this.self;
        }

        public MinimapWorldContainer build() {
            if (this.modMain == null || this.session == null || this.path == null) {
                throw new IllegalStateException();
            }
            return this.buildInternally(new HashMap<String, MinimapWorldContainer>(), new HashMap<String, MinimapWorld>(), new HashMap<String, String>(), new ServerWaypointManager());
        }

        protected abstract MinimapWorldContainer buildInternally(Map<String, MinimapWorldContainer> var1, Map<String, MinimapWorld> var2, Map<String, String> var3, ServerWaypointManager var4);
    }
}

