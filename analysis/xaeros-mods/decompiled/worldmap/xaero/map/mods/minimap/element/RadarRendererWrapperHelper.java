/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  xaero.common.IXaeroMinimap
 *  xaero.hud.minimap.radar.render.element.RadarRenderer
 */
package xaero.map.mods.minimap.element;

import xaero.common.IXaeroMinimap;
import xaero.hud.minimap.radar.render.element.RadarRenderer;
import xaero.map.WorldMap;
import xaero.map.mods.minimap.element.MinimapElementRendererWrapper;

public class RadarRendererWrapperHelper {
    public void createWrapper(IXaeroMinimap modMain, RadarRenderer radarRenderer) {
        WorldMap.mapElementRenderHandler.add(MinimapElementRendererWrapper.Builder.begin(radarRenderer).setModMain(modMain).setShouldRenderSupplier(() -> WorldMap.settings.minimapRadar).setOrder(100).build());
    }
}

