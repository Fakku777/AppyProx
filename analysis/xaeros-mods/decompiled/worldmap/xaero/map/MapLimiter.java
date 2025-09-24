/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 */
package xaero.map;

import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.world.MapDimension;
import xaero.map.world.MapWorld;

public class MapLimiter {
    private static final int MIN_LIMIT = 53;
    private static final int DEFAULT_LIMIT = 203;
    private static final int MAX_LIMIT = 403;
    private int availableVRAM = -1;
    private int mostRegionsAtATime;
    private IntBuffer vramBuffer = BufferUtils.createByteBuffer((int)64).asIntBuffer();
    private int driverType = -1;
    private ArrayList<MapDimension> workingDimList = new ArrayList();

    public int getAvailableVRAM() {
        return this.availableVRAM;
    }

    private void determineDriverType() {
        this.driverType = GL.getCapabilities().GL_NVX_gpu_memory_info ? 0 : (GL.getCapabilities().GL_ATI_meminfo ? 1 : 2);
    }

    public void updateAvailableVRAM() {
        if (this.driverType == -1) {
            this.determineDriverType();
        }
        switch (this.driverType) {
            case 0: {
                this.vramBuffer.clear();
                GL11.glGetIntegerv((int)36937, (IntBuffer)this.vramBuffer);
                this.availableVRAM = this.vramBuffer.get(0);
                break;
            }
            case 1: {
                this.vramBuffer.clear();
                GL11.glGetIntegerv((int)34812, (IntBuffer)this.vramBuffer);
                this.availableVRAM = this.vramBuffer.get(0);
            }
        }
    }

    public int getMostRegionsAtATime() {
        return this.mostRegionsAtATime;
    }

    public void setMostRegionsAtATime(int mostRegionsAtATime) {
        this.mostRegionsAtATime = mostRegionsAtATime;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void applyLimit(MapWorld mapWorld, MapProcessor mapProcessor) {
        int limit = Math.max(this.mostRegionsAtATime, 53);
        int vramDetermined = 0;
        int loadedCount = 0;
        this.workingDimList.clear();
        mapWorld.getDimensions(this.workingDimList);
        for (MapDimension dim : this.workingDimList) {
            loadedCount += dim.getLayeredMapRegions().loadedCount();
        }
        if (this.availableVRAM != -1) {
            if (this.availableVRAM < 204800) {
                vramDetermined = Math.min(403, loadedCount) - 6;
            } else {
                if (loadedCount <= 403) return;
                vramDetermined = 397;
            }
        } else {
            int n = vramDetermined = loadedCount > 203 ? 197 : loadedCount;
        }
        if (vramDetermined > limit) {
            limit = vramDetermined;
        }
        int count = 0;
        mapProcessor.pushRenderPause(false, true);
        LeveledRegion<?> nextToLoad = mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
        int currentDimIndex = this.workingDimList.indexOf(mapWorld.getCurrentDimension());
        int dimTotal = this.workingDimList.size();
        int d = (currentDimIndex + 1) % dimTotal;
        for (int dimCount = 0; dimCount < dimTotal && loadedCount > limit; ++dimCount) {
            MapDimension dimension = this.workingDimList.get(d);
            LayeredRegionManager regions = dimension.getLayeredMapRegions();
            for (int i = 0; i < regions.loadedCount() && loadedCount > limit; ++i) {
                LeveledRegion<?> region = regions.getLoadedRegion(i);
                if (!region.isLoaded() || region.shouldBeProcessed() || region.activeBranchUpdateReferences != 0) continue;
                region.onLimiterRemoval(mapProcessor);
                region.deleteTexturesAndBuffers();
                mapProcessor.getMapSaveLoad().removeToCache(region);
                region.afterLimiterRemoval(mapProcessor);
                if (region == nextToLoad) {
                    mapProcessor.getMapSaveLoad().setNextToLoadByViewing(null);
                }
                ++count;
                --i;
                --loadedCount;
            }
            d = (d + 1) % dimTotal;
        }
        if (WorldMap.settings.debug && count > 0) {
            WorldMap.LOGGER.info("Unloaded " + count + " world map regions!");
        }
        mapProcessor.popRenderPause(false, true);
    }

    public void onSessionFinalized() {
        this.workingDimList.clear();
    }
}

