/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package xaero.map.region;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import xaero.map.highlight.RegionHighlightExistenceTracker;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapLayer;
import xaero.map.region.MapRegion;
import xaero.map.world.MapDimension;

public class LayeredRegionManager {
    private final MapDimension mapDimension;
    private final Int2ObjectMap<MapLayer> mapLayers;
    private Set<LeveledRegion<?>> regionsListAll;
    private List<LeveledRegion<?>> regionsListLoaded;

    public LayeredRegionManager(MapDimension mapDimension) {
        this.mapDimension = mapDimension;
        this.mapLayers = new Int2ObjectOpenHashMap();
        this.regionsListAll = new HashSet();
        this.regionsListLoaded = new ArrayList();
    }

    public void putLeaf(int X, int Z, MapRegion leaf) {
        this.getLayer(leaf.caveLayer).getMapRegions().putLeaf(X, Z, leaf);
    }

    public MapRegion getLeaf(int caveLayer, int X, int Z) {
        return this.getLayer(caveLayer).getMapRegions().getLeaf(X, Z);
    }

    public LeveledRegion<?> get(int caveLayer, int leveledX, int leveledZ, int level) {
        return this.getLayer(caveLayer).getMapRegions().get(leveledX, leveledZ, level);
    }

    public boolean remove(int caveLayer, int leveledX, int leveledZ, int level) {
        return this.getLayer(caveLayer).getMapRegions().remove(leveledX, leveledZ, level);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapLayer getLayer(int caveLayer) {
        MapLayer mapLayer;
        Int2ObjectMap<MapLayer> int2ObjectMap = this.mapLayers;
        synchronized (int2ObjectMap) {
            mapLayer = (MapLayer)this.mapLayers.get(caveLayer);
            if (mapLayer == null) {
                mapLayer = new MapLayer(this.mapDimension, new RegionHighlightExistenceTracker(this.mapDimension, caveLayer));
                this.mapLayers.put(caveLayer, (Object)mapLayer);
            }
        }
        return mapLayer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        Object object = this.mapLayers;
        synchronized (object) {
            this.mapLayers.clear();
        }
        object = this.regionsListAll;
        synchronized (object) {
            this.regionsListAll.clear();
        }
        object = this.regionsListLoaded;
        synchronized (object) {
            this.regionsListLoaded.clear();
        }
    }

    public int loadedCount() {
        return this.regionsListLoaded.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeListRegion(LeveledRegion<?> reg) {
        Set<LeveledRegion<?>> set = this.regionsListAll;
        synchronized (set) {
            this.regionsListAll.remove(reg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addListRegion(LeveledRegion<?> reg) {
        Set<LeveledRegion<?>> set = this.regionsListAll;
        synchronized (set) {
            this.regionsListAll.add(reg);
        }
    }

    public void bumpLoadedRegion(MapRegion reg) {
        this.bumpLoadedRegion((LeveledRegion<?>)reg);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bumpLoadedRegion(LeveledRegion<?> reg) {
        List<LeveledRegion<?>> list = this.regionsListLoaded;
        synchronized (list) {
            if (this.regionsListLoaded.remove(reg)) {
                this.regionsListLoaded.add(reg);
            }
        }
    }

    public List<LeveledRegion<?>> getLoadedListUnsynced() {
        return this.regionsListLoaded;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LeveledRegion<?> getLoadedRegion(int index) {
        List<LeveledRegion<?>> list = this.regionsListLoaded;
        synchronized (list) {
            return this.regionsListLoaded.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addLoadedRegion(LeveledRegion<?> reg) {
        List<LeveledRegion<?>> list = this.regionsListLoaded;
        synchronized (list) {
            this.regionsListLoaded.add(reg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeLoadedRegion(LeveledRegion<?> reg) {
        List<LeveledRegion<?>> list = this.regionsListLoaded;
        synchronized (list) {
            this.regionsListLoaded.remove(reg);
        }
    }

    public int size() {
        return this.regionsListAll.size();
    }

    public Set<LeveledRegion<?>> getUnsyncedSet() {
        return this.regionsListAll;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onClearCachedHighlightHash(int regionX, int regionZ) {
        Int2ObjectMap<MapLayer> int2ObjectMap = this.mapLayers;
        synchronized (int2ObjectMap) {
            this.mapLayers.forEach((i, layer) -> layer.getRegionHighlightExistenceTracker().onClearCachedHash(regionX, regionZ));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onClearCachedHighlightHashes() {
        Int2ObjectMap<MapLayer> int2ObjectMap = this.mapLayers;
        synchronized (int2ObjectMap) {
            this.mapLayers.forEach((i, layer) -> layer.getRegionHighlightExistenceTracker().onClearCachedHashes());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyToEachLoadedLayer(BiConsumer<Integer, MapLayer> consumer) {
        Int2ObjectMap<MapLayer> int2ObjectMap = this.mapLayers;
        synchronized (int2ObjectMap) {
            this.mapLayers.forEach(consumer::accept);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void preDetection() {
        Int2ObjectMap<MapLayer> int2ObjectMap = this.mapLayers;
        synchronized (int2ObjectMap) {
            this.mapLayers.forEach((i, layer) -> layer.preDetection());
        }
    }
}

