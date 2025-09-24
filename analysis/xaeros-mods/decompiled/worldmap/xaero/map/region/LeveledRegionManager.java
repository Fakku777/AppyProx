/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.region;

import java.util.HashMap;
import xaero.map.region.BranchLeveledRegion;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapRegion;

public class LeveledRegionManager {
    public static final int MAX_LEVEL = 3;
    private HashMap<Integer, HashMap<Integer, LeveledRegion<?>>> regionTextureMap = new HashMap();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void putLeaf(int X, int Z, MapRegion leaf) {
        BranchLeveledRegion rootBranch;
        HashMap<Integer, LeveledRegion<Object>> column;
        int maxLevelX = X >> 3;
        int maxLevelZ = Z >> 3;
        HashMap<Integer, HashMap<Integer, LeveledRegion<?>>> hashMap = this.regionTextureMap;
        synchronized (hashMap) {
            column = this.regionTextureMap.get(maxLevelX);
            if (column == null) {
                column = new HashMap();
                this.regionTextureMap.put(maxLevelX, column);
            }
        }
        HashMap<Integer, LeveledRegion<Object>> hashMap2 = column;
        synchronized (hashMap2) {
            rootBranch = column.get(maxLevelZ);
            if (rootBranch == null) {
                rootBranch = new BranchLeveledRegion(leaf.getWorldId(), leaf.getDimId(), leaf.getMwId(), leaf.getDim(), 3, maxLevelX, maxLevelZ, leaf.caveLayer, null);
                column.put(maxLevelZ, rootBranch);
                leaf.getDim().getLayeredMapRegions().addListRegion(rootBranch);
            }
        }
        if (!(rootBranch instanceof MapRegion)) {
            ((LeveledRegion)rootBranch).putLeaf(X, Z, leaf);
        }
    }

    public MapRegion getLeaf(int X, int Z) {
        return (MapRegion)this.get(X, Z, 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LeveledRegion<?> get(int leveledX, int leveledZ, int level) {
        LeveledRegion<?> rootBranch;
        HashMap<Integer, LeveledRegion<?>> column;
        if (level > 3) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        int maxLevelX = leveledX >> 3 - level;
        int maxLevelZ = leveledZ >> 3 - level;
        HashMap<Integer, HashMap<Integer, LeveledRegion<?>>> hashMap = this.regionTextureMap;
        synchronized (hashMap) {
            column = this.regionTextureMap.get(maxLevelX);
        }
        if (column == null) {
            return null;
        }
        HashMap<Integer, LeveledRegion<?>> hashMap2 = column;
        synchronized (hashMap2) {
            rootBranch = column.get(maxLevelZ);
        }
        if (rootBranch == null) {
            return null;
        }
        if (level == 3) {
            return rootBranch;
        }
        return rootBranch.get(leveledX, leveledZ, level);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(int leveledX, int leveledZ, int level) {
        LeveledRegion<?> rootBranch;
        HashMap<Integer, LeveledRegion<?>> column;
        if (level > 3) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        int maxLevelX = leveledX >> 3 - level;
        int maxLevelZ = leveledZ >> 3 - level;
        HashMap<Integer, HashMap<Integer, LeveledRegion<?>>> hashMap = this.regionTextureMap;
        synchronized (hashMap) {
            column = this.regionTextureMap.get(maxLevelX);
        }
        if (column == null) {
            return false;
        }
        HashMap<Integer, LeveledRegion<?>> hashMap2 = column;
        synchronized (hashMap2) {
            rootBranch = column.get(maxLevelZ);
        }
        if (rootBranch == null) {
            return false;
        }
        if (!(rootBranch instanceof MapRegion)) {
            return rootBranch.remove(leveledX, leveledZ, level);
        }
        hashMap2 = column;
        synchronized (hashMap2) {
            column.remove(maxLevelZ);
        }
        return true;
    }
}

