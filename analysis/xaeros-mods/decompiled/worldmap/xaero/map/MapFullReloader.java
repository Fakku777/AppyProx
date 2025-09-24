/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package xaero.map;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import net.minecraft.class_310;
import xaero.map.MapProcessor;
import xaero.map.file.RegionDetection;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiWorldMapSettings;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapRegion;
import xaero.map.world.MapDimension;

public class MapFullReloader {
    public static final String CONVERTED_WORLD_SAVE_MW = "cm$converted";
    private final int caveLayer;
    private final boolean resave;
    private final Iterator<RegionDetection> regionDetectionIterator;
    private final Deque<RegionDetection> retryLaterDeque;
    private final MapDimension mapDimension;
    private final MapProcessor mapProcessor;
    private MapRegion lastRequestedRegion;

    public MapFullReloader(int caveLayer, boolean resave, Iterator<RegionDetection> regionDetectionIterator, MapDimension mapDimension, MapProcessor mapProcessor) {
        this.caveLayer = caveLayer;
        this.resave = resave;
        this.regionDetectionIterator = regionDetectionIterator;
        this.retryLaterDeque = new LinkedBlockingDeque<RegionDetection>();
        this.mapDimension = mapDimension;
        this.mapProcessor = mapProcessor;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onRenderProcess() {
        RegionDetection next;
        LeveledRegion<?> nextToLoad = this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
        if ((nextToLoad == null || nextToLoad.shouldAllowAnotherRegionToLoad()) && (next = !this.regionDetectionIterator.hasNext() ? (this.retryLaterDeque.isEmpty() ? null : this.retryLaterDeque.removeFirst()) : this.regionDetectionIterator.next()) != null) {
            MapRegion nextRegionToReload = this.mapProcessor.getLeafMapRegion(this.caveLayer, next.getRegionX(), next.getRegionZ(), true);
            if (nextRegionToReload == null) {
                this.retryLaterDeque.add(next);
                return;
            }
            nextRegionToReload.setHasHadTerrain();
            MapRegion mapRegion = nextRegionToReload;
            synchronized (mapRegion) {
                if (!nextRegionToReload.canRequestReload_unsynced()) {
                    this.retryLaterDeque.add(next);
                    return;
                }
                if (this.resave) {
                    nextRegionToReload.setResaving(true);
                    nextRegionToReload.setBeingWritten(true);
                }
                if (nextRegionToReload.getLoadState() == 2) {
                    nextRegionToReload.requestRefresh(this.mapProcessor);
                } else {
                    this.mapProcessor.getMapSaveLoad().requestLoad(nextRegionToReload, "full reload");
                }
                this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing(nextRegionToReload);
                this.lastRequestedRegion = nextRegionToReload;
            }
            return;
        }
        if (!this.regionDetectionIterator.hasNext() && this.retryLaterDeque.isEmpty() && (this.lastRequestedRegion == null || this.lastRequestedRegion.shouldAllowAnotherRegionToLoad())) {
            this.mapDimension.clearFullMapReload();
            if (this.resave && this.mapDimension.isUsingWorldSave()) {
                this.mapDimension.addMultiworldChecked(CONVERTED_WORLD_SAVE_MW);
                this.mapDimension.setMultiworldName(CONVERTED_WORLD_SAVE_MW, "gui.xaero_converted_world_save");
                this.mapDimension.saveConfigUnsynced();
            }
            if (class_310.method_1551().field_1755 instanceof GuiWorldMapSettings || class_310.method_1551().field_1755 instanceof GuiMap) {
                class_310.method_1551().method_1507(class_310.method_1551().field_1755);
            }
        }
    }

    public boolean isPartOfReload(MapRegion region) {
        return region.getDim() == this.mapDimension && region.getCaveLayer() == this.caveLayer;
    }

    public boolean isResave() {
        return this.resave;
    }
}

