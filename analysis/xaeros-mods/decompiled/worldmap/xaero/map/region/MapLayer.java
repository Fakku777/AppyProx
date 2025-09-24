/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.region;

import java.util.Hashtable;
import xaero.map.file.RegionDetection;
import xaero.map.highlight.RegionHighlightExistenceTracker;
import xaero.map.region.LeveledRegionManager;
import xaero.map.util.linked.LinkedChain;
import xaero.map.world.MapDimension;

public class MapLayer {
    private final MapDimension mapDimension;
    private final LeveledRegionManager mapRegions;
    private final RegionHighlightExistenceTracker regionHighlightExistenceTracker;
    private final Hashtable<Integer, Hashtable<Integer, RegionDetection>> detectedRegions;
    private final Hashtable<Integer, Hashtable<Integer, RegionDetection>> completeDetectedRegions;
    private final LinkedChain<RegionDetection> completeDetectedRegionsLinked;
    private int caveStart;

    public MapLayer(MapDimension mapDimension, RegionHighlightExistenceTracker regionHighlightExistenceTracker) {
        this.mapDimension = mapDimension;
        this.mapRegions = new LeveledRegionManager();
        this.regionHighlightExistenceTracker = regionHighlightExistenceTracker;
        this.detectedRegions = new Hashtable();
        this.completeDetectedRegions = new Hashtable();
        this.completeDetectedRegionsLinked = new LinkedChain();
    }

    public boolean regionDetectionExists(int x, int z) {
        return this.getRegionDetection(x, z) != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addRegionDetection(RegionDetection regionDetection) {
        Hashtable<Integer, Hashtable<Integer, RegionDetection>> hashtable = this.detectedRegions;
        synchronized (hashtable) {
            Hashtable<Integer, RegionDetection> column = this.detectedRegions.get(regionDetection.getRegionX());
            if (column == null) {
                column = new Hashtable();
                this.detectedRegions.put(regionDetection.getRegionX(), column);
            }
            column.put(regionDetection.getRegionZ(), regionDetection);
            this.tryAddingToCompleteRegionDetection(regionDetection);
        }
    }

    public RegionDetection getCompleteRegionDetection(int x, int z) {
        if (this.mapDimension.isUsingWorldSave()) {
            return this.mapDimension.getWorldSaveRegionDetection(x, z);
        }
        Hashtable<Integer, RegionDetection> column = this.completeDetectedRegions.get(x);
        if (column != null) {
            return column.get(z);
        }
        return null;
    }

    private boolean completeRegionDetectionContains(RegionDetection regionDetection) {
        return this.getCompleteRegionDetection(regionDetection.getRegionX(), regionDetection.getRegionZ()) != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tryAddingToCompleteRegionDetection(RegionDetection regionDetection) {
        if (this.completeRegionDetectionContains(regionDetection)) {
            return;
        }
        if (this.mapDimension.isUsingWorldSave()) {
            this.mapDimension.addWorldSaveRegionDetection(regionDetection);
            return;
        }
        Hashtable<Integer, Hashtable<Integer, RegionDetection>> hashtable = this.completeDetectedRegions;
        synchronized (hashtable) {
            Hashtable<Integer, RegionDetection> column = this.completeDetectedRegions.get(regionDetection.getRegionX());
            if (column == null) {
                column = new Hashtable();
                this.completeDetectedRegions.put(regionDetection.getRegionX(), column);
            }
            column.put(regionDetection.getRegionZ(), regionDetection);
            this.completeDetectedRegionsLinked.add(regionDetection);
        }
    }

    public RegionDetection getRegionDetection(int x, int z) {
        Hashtable<Integer, RegionDetection> column = this.detectedRegions.get(x);
        RegionDetection result = null;
        if (column != null) {
            result = column.get(z);
        }
        if (result == null) {
            RegionDetection worldSaveDetection = this.mapDimension.getWorldSaveRegionDetection(x, z);
            if (worldSaveDetection != null) {
                result = new RegionDetection(worldSaveDetection.getWorldId(), worldSaveDetection.getDimId(), worldSaveDetection.getMwId(), worldSaveDetection.getRegionX(), worldSaveDetection.getRegionZ(), worldSaveDetection.getRegionFile(), worldSaveDetection.getInitialVersion(), worldSaveDetection.isHasHadTerrain());
                this.addRegionDetection(result);
                return result;
            }
        } else if (result.isRemoved()) {
            return null;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeRegionDetection(int x, int z) {
        if (this.mapDimension.getWorldSaveRegionDetection(x, z) != null) {
            RegionDetection regionDetection = this.getRegionDetection(x, z);
            if (regionDetection != null) {
                regionDetection.setRemoved(true);
            }
            return;
        }
        Hashtable<Integer, Hashtable<Integer, RegionDetection>> hashtable = this.detectedRegions;
        synchronized (hashtable) {
            Hashtable<Integer, RegionDetection> column = this.detectedRegions.get(x);
            if (column != null) {
                column.remove(z);
                if (column.isEmpty()) {
                    this.detectedRegions.remove(x);
                }
            }
        }
    }

    public RegionHighlightExistenceTracker getRegionHighlightExistenceTracker() {
        return this.regionHighlightExistenceTracker;
    }

    public LeveledRegionManager getMapRegions() {
        return this.mapRegions;
    }

    public Hashtable<Integer, Hashtable<Integer, RegionDetection>> getDetectedRegions() {
        return this.detectedRegions;
    }

    public Iterable<RegionDetection> getLinkedCompleteWorldSaveDetectedRegions() {
        return this.mapDimension.isUsingWorldSave() ? this.mapDimension.getLinkedWorldSaveDetectedRegions() : this.completeDetectedRegionsLinked;
    }

    public void preDetection() {
        this.detectedRegions.clear();
        this.completeDetectedRegions.clear();
        this.completeDetectedRegionsLinked.reset();
    }

    public int getCaveStart() {
        return this.caveStart;
    }

    public void setCaveStart(int caveStart) {
        this.caveStart = caveStart;
    }
}

