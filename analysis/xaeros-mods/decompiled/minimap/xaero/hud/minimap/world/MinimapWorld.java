/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_5321
 */
package xaero.hud.minimap.world;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1937;
import net.minecraft.class_5321;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.config.RootConfig;
import xaero.hud.path.XaeroPath;

public final class MinimapWorld {
    private String node;
    private class_5321<class_1937> dimId;
    private MinimapWorldContainer container;
    private final Map<String, WaypointSet> waypointSets;
    private String currentWaypointSetId;
    private final List<String> toRemoveOnSave;

    private MinimapWorld(MinimapWorldContainer container, String node, class_5321<class_1937> dimId) {
        this.container = container;
        this.node = node;
        this.waypointSets = new LinkedHashMap<String, WaypointSet>();
        this.toRemoveOnSave = new ArrayList<String>();
        this.dimId = dimId;
    }

    public WaypointSet getCurrentWaypointSet() {
        return this.waypointSets.get(this.currentWaypointSetId);
    }

    public void addWaypointSet(String s) {
        this.waypointSets.put(s, WaypointSet.Builder.begin().setName(s).build());
    }

    public void cleanupOnSave(Path worldFile) throws IOException {
        Path folder = worldFile.getParent();
        for (String s : this.toRemoveOnSave) {
            Path path = folder.resolve(this.node + "_" + s + ".txt");
            Files.deleteIfExists(path);
        }
    }

    public XaeroPath getLocalWorldKey() {
        XaeroPath containerKey = this.container.getPath();
        if (containerKey.getNodeCount() < 2) {
            return XaeroPath.root(this.node);
        }
        return containerKey.getSubPath(1).resolve(this.node);
    }

    public WaypointSet addWaypointSet(WaypointSet set) {
        return this.waypointSets.put(set.getName(), set);
    }

    public WaypointSet getWaypointSet(String key) {
        return this.waypointSets.get(key);
    }

    public WaypointSet removeWaypointSet(String key) {
        return this.waypointSets.remove(key);
    }

    public Iterable<WaypointSet> getIterableWaypointSets() {
        return this.waypointSets.values();
    }

    public String getCurrentWaypointSetId() {
        return this.currentWaypointSetId;
    }

    public void setCurrentWaypointSetId(String currentWaypointSetId) {
        this.currentWaypointSetId = currentWaypointSetId;
    }

    public String getNode() {
        return this.node;
    }

    public XaeroPath getFullPath() {
        return this.container.getPath().resolve(this.node);
    }

    public void setNode(String node) {
        this.node = node;
    }

    public MinimapWorldContainer getContainer() {
        return this.container;
    }

    public void setContainer(MinimapWorldContainer container) {
        this.container = container;
    }

    public void requestRemovalOnSave(String name) {
        this.toRemoveOnSave.add(name);
    }

    public boolean hasSomethingToRemoveOnSave() {
        return !this.toRemoveOnSave.isEmpty();
    }

    public class_5321<class_1937> getDimId() {
        return this.dimId;
    }

    public void setDimId(class_5321<class_1937> dimId) {
        this.dimId = dimId;
    }

    public int getSetCount() {
        return this.waypointSets.size();
    }

    public RootConfig getRootConfig() {
        return this.getContainer().getRootConfig();
    }

    public static final class Builder {
        private MinimapWorldContainer container;
        private String node;
        private class_5321<class_1937> dimId;

        private Builder() {
        }

        private Builder setDefault() {
            this.setContainer(null);
            this.setNode(null);
            this.setDimId(null);
            return this;
        }

        public Builder setContainer(MinimapWorldContainer container) {
            this.container = container;
            return this;
        }

        public Builder setNode(String node) {
            this.node = node;
            return this;
        }

        public Builder setDimId(class_5321<class_1937> dimId) {
            this.dimId = dimId;
            return this;
        }

        public MinimapWorld build() {
            if (this.container == null || this.node == null) {
                throw new IllegalStateException();
            }
            MinimapWorld result = new MinimapWorld(this.container, this.node, this.dimId);
            result.addWaypointSet("gui.xaero_default");
            result.setCurrentWaypointSetId("gui.xaero_default");
            return result;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

