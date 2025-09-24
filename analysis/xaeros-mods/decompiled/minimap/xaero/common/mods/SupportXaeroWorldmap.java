/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1937
 *  net.minecraft.class_2378
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_5321
 *  net.minecraft.class_746
 *  org.joml.Matrix4f
 *  xaero.map.MapProcessor
 *  xaero.map.WorldMap
 *  xaero.map.WorldMapSession
 *  xaero.map.graphics.CustomRenderTypes
 *  xaero.map.graphics.GpuTextureAndView
 *  xaero.map.graphics.shader.WorldMapShaderHelper
 *  xaero.map.gui.GuiMap
 *  xaero.map.gui.GuiWorldMapSettings
 *  xaero.map.misc.Misc
 *  xaero.map.mods.SupportMods
 *  xaero.map.region.MapRegion
 *  xaero.map.region.MapTileChunk
 *  xaero.map.region.texture.LeafRegionTexture
 *  xaero.map.settings.ModOptions
 *  xaero.map.world.MapDimension
 */
package xaero.common.mods;

import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2378;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_5321;
import net.minecraft.class_746;
import org.joml.Matrix4f;
import xaero.common.IXaeroMinimap;
import xaero.common.effect.Effects;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.gui.ScreenBase;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.region.MinimapTile;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.misc.Misc;
import xaero.common.settings.ModOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.render.element.RadarRenderer;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.render.util.ImmediateRenderUtil;
import xaero.hud.render.util.MultiTextureRenderUtil;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.WorldMapSession;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.shader.WorldMapShaderHelper;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiWorldMapSettings;
import xaero.map.mods.SupportMods;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.LeafRegionTexture;
import xaero.map.world.MapDimension;

public class SupportXaeroWorldmap {
    public static int WORLDMAP_COMPATIBILITY_VERSION = 20;
    public static final String MINIMAP_MW = "minimap";
    public int compatibilityVersion;
    private static final HashMap<MapTileChunk, Long> seedsUsed = new HashMap();
    public static final int black = -16777216;
    public static final int slime = -2142047936;
    private IXaeroMinimap modMain;
    private int destinationCaving = Integer.MAX_VALUE;
    private long lastDestinationCavingSwitch;
    private int previousRenderedCaveLayer = Integer.MAX_VALUE;
    private int lastRenderedCaveLayer = Integer.MAX_VALUE;
    private ArrayList<MapRegion> regionBuffer = new ArrayList();

    public SupportXaeroWorldmap(IXaeroMinimap modMain) {
        this.modMain = modMain;
        try {
            this.compatibilityVersion = WorldMap.MINIMAP_COMPATIBILITY_VERSION;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        if (this.compatibilityVersion < 3) {
            throw new RuntimeException("Xaero's World Map 1.11.0 or newer required!");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drawMinimap(MinimapSession minimapSession, class_4587 matrixStack, MinimapRendererHelper helper, int xFloored, int zFloored, int minViewX, int minViewZ, int maxViewX, int maxViewZ, boolean zooming, double zoom, double mapDimensionScale, class_4588 overlayBufferBuilder, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.renderThreadPauseSync;
        synchronized (object) {
            if (!mapProcessor.isRenderingPaused()) {
                Consumer<GpuTexture> binder;
                class_746 player;
                if (mapProcessor.getCurrentDimension() == null) {
                    return;
                }
                int compatibilityVersion = this.compatibilityVersion;
                String worldString = mapProcessor.getCurrentWorldId();
                if (worldString == null) {
                    return;
                }
                int mapX = xFloored >> 4;
                int mapZ = zFloored >> 4;
                int chunkX = mapX >> 2;
                int chunkZ = mapZ >> 2;
                int tileX = mapX & 3;
                int tileZ = mapZ & 3;
                int insideX = xFloored & 0xF;
                int insideZ = zFloored & 0xF;
                ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                int minX = (mapX >> 2) - 4;
                int maxX = (mapX >> 2) + 4;
                int minZ = (mapZ >> 2) - 4;
                int maxZ = (mapZ >> 2) + 4;
                boolean slimeChunks = this.modMain.getSettings().getSlimeChunks(minimapSession);
                mapProcessor.initMinimapRender(xFloored, zFloored);
                int renderedCaveLayer = mapProcessor.getCurrentCaveLayer();
                float brightness = this.getMinimapBrightness();
                if (renderedCaveLayer != this.lastRenderedCaveLayer) {
                    this.previousRenderedCaveLayer = this.lastRenderedCaveLayer;
                }
                boolean noCaveMaps = Misc.hasEffect((class_1657)(player = class_310.method_1551().field_1724), Effects.NO_CAVE_MAPS) || Misc.hasEffect((class_1657)player, Effects.NO_CAVE_MAPS_HARMFUL);
                Consumer<GpuTexture> finalizer = null;
                if (zooming) {
                    binder = t -> {
                        if (t != null) {
                            t.setTextureFilter(FilterMode.NEAREST, FilterMode.LINEAR, false);
                        }
                        MultiTextureRenderTypeRendererProvider.defaultTextureBind(t);
                    };
                    finalizer = t -> t.setTextureFilter(FilterMode.NEAREST, FilterMode.NEAREST, false);
                } else {
                    binder = MultiTextureRenderTypeRendererProvider::defaultTextureBind;
                }
                MultiTextureRenderTypeRenderer mapWithLightRenderer = multiTextureRenderTypeRenderers.getRenderer(binder, finalizer, CustomRenderTypes.MAP);
                MultiTextureRenderTypeRenderer mapNoLightRenderer = multiTextureRenderTypeRenderers.getRenderer(binder, finalizer, CustomRenderTypes.MAP);
                MinimapWorld world = minimapSession.getWorldManager().getAutoWorld();
                Long seed = !slimeChunks || world == null ? null : this.modMain.getSettings().getSlimeChunksSeed(world.getFullPath());
                this.renderChunks(matrixStack, minX, maxX, minZ, maxZ, minViewX, maxViewX, minViewZ, maxViewZ, mapProcessor, noCaveMaps, slimeChunks, chunkX, chunkZ, tileX, tileZ, insideX, insideZ, seed, mapWithLightRenderer, mapNoLightRenderer, helper, overlayBufferBuilder);
                WorldMapShaderHelper.setBrightness((float)brightness);
                WorldMapShaderHelper.setWithLight((boolean)true);
                multiTextureRenderTypeRenderers.draw(mapWithLightRenderer);
                WorldMapShaderHelper.setWithLight((boolean)false);
                multiTextureRenderTypeRenderers.draw(mapNoLightRenderer);
                this.lastRenderedCaveLayer = renderedCaveLayer;
                mapProcessor.finalizeMinimapRender();
            }
        }
    }

    private void renderChunks(class_4587 matrixStack, int minX, int maxX, int minZ, int maxZ, int minViewX, int maxViewX, int minViewZ, int maxViewZ, MapProcessor mapProcessor, boolean noCaveMaps, boolean slimeChunks, int chunkX, int chunkZ, int tileX, int tileZ, int insideX, int insideZ, Long seed, MultiTextureRenderTypeRenderer mapWithLightRenderer, MultiTextureRenderTypeRenderer mapNoLightRenderer, MinimapRendererHelper helper, class_4588 overlayBufferBuilder) {
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        for (int i = minX; i <= maxX; ++i) {
            for (int j = minZ; j <= maxZ; ++j) {
                MapTileChunk previousLayerChunk;
                MapRegion previousLayerRegion;
                boolean chunkIsVisible;
                MapRegion region = mapProcessor.getMinimapMapRegion(i >> 3, j >> 3);
                mapProcessor.beforeMinimapRegionRender(region);
                if (i < minViewX || i > maxViewX || j < minViewZ || j > maxViewZ) continue;
                MapTileChunk chunk = region == null ? null : region.getChunk(i & 7, j & 7);
                boolean bl = chunkIsVisible = chunk != null && chunk.getLeafTexture().getGlColorTexture() != null;
                if (!(chunkIsVisible || noCaveMaps && this.previousRenderedCaveLayer != Integer.MAX_VALUE || (previousLayerRegion = mapProcessor.getLeafMapRegion(this.previousRenderedCaveLayer, i >> 3, j >> 3, false)) == null || (previousLayerChunk = previousLayerRegion.getChunk(i & 7, j & 7)) == null || previousLayerChunk.getLeafTexture().getGlColorTexture() == null)) {
                    region = previousLayerRegion;
                    chunk = previousLayerChunk;
                    chunkIsVisible = true;
                }
                if (!chunkIsVisible) continue;
                this.bumpLoadedRegion(mapProcessor, region);
                int drawX = 64 * (chunk.getX() - chunkX) - 16 * tileX - insideX;
                int drawZ = 64 * (chunk.getZ() - chunkZ) - 16 * tileZ - insideZ;
                this.prepareMapTexturedRect(matrix, drawX, drawZ, 0, 0, 64.0f, 64.0f, chunk, mapNoLightRenderer, mapWithLightRenderer, helper);
                if (!slimeChunks) continue;
                this.renderSlimeChunks(chunk, seed, drawX, drawZ, matrixStack, helper, overlayBufferBuilder);
            }
        }
    }

    public void bumpLoadedRegion(MapProcessor mapProcessor, MapRegion region) {
        if (!mapProcessor.isUploadingPaused() && region.isLoaded()) {
            mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().bumpLoadedRegion(region);
        }
    }

    public void renderSlimeChunks(MapTileChunk chunk, Long seed, int drawX, int drawZ, class_4587 matrixStack, MinimapRendererHelper helper, class_4588 overlayBufferBuilder) {
        boolean newSeed;
        Long savedSeed = seedsUsed.get(chunk);
        boolean bl = newSeed = seed == null && savedSeed != null || seed != null && !seed.equals(savedSeed);
        if (newSeed) {
            seedsUsed.put(chunk, seed);
        }
        for (int t = 0; t < 16; ++t) {
            if (newSeed || (chunk.getTileGridsCache()[t % 4][t / 4] & 1) == 0) {
                chunk.getTileGridsCache()[t % 4][t / 4] = (byte)(1 | (MinimapTile.isSlimeChunk(this.modMain.getSettings(), chunk.getX() * 4 + t % 4, chunk.getZ() * 4 + t / 4, seed) ? 2 : 0));
            }
            if ((chunk.getTileGridsCache()[t % 4][t / 4] & 2) == 0) continue;
            int slimeDrawX = drawX + 16 * (t % 4);
            int slimeDrawZ = drawZ + 16 * (t / 4);
            RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), overlayBufferBuilder, slimeDrawX, slimeDrawZ, 16, 16, -2142047936);
        }
    }

    public boolean getWorldMapWaypoints() {
        return WorldMap.settings.waypoints;
    }

    public int getWorldMapColours() {
        return WorldMap.settings.colours;
    }

    public boolean getWorldMapFlowers() {
        return WorldMap.settings.flowers;
    }

    public boolean getWorldMapTerrainDepth() {
        return WorldMap.settings.terrainDepth;
    }

    public int getWorldMapTerrainSlopes() {
        int wmSetting = WorldMap.settings.terrainSlopes;
        return wmSetting;
    }

    public boolean getWorldMapBiomeColorsVanillaMode() {
        return WorldMap.settings.biomeColorsVanillaMode;
    }

    public boolean getWorldMapIgnoreHeightmaps() {
        return WorldMap.settings.getClientBooleanValue(xaero.map.settings.ModOptions.IGNORE_HEIGHTMAPS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String tryToGetMultiworldId(class_5321<class_1937> dimId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiPauseSync;
        synchronized (object) {
            if (mapProcessor.isUIPaused()) {
                return null;
            }
            return this.getMultiworldIdUnsynced(mapProcessor, dimId);
        }
    }

    private String getMultiworldIdUnsynced(MapProcessor mapProcessor, class_5321<class_1937> dimId) {
        MapDimension mapDim;
        MapDimension mapDimension = mapDim = !mapProcessor.isMapWorldUsable() || mapProcessor.isWaitingForWorldUpdate() ? null : mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
        return mapDim == null ? null : (!mapDim.currentMultiworldWritable ? MINIMAP_MW : mapDim.getCurrentMultiworld());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getPotentialMultiworldIds(class_5321<class_1937> dimId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiSync;
        synchronized (object) {
            MapDimension mapDim = mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
            return mapDim == null || !mapProcessor.isWaitingForWorldUpdate() && mapDim.currentMultiworldWritable ? null : mapDim.getMultiworldIdsCopy();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getMultiworldIds(class_5321<class_1937> dimId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiSync;
        synchronized (object) {
            MapDimension mapDim = mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
            return mapDim == null ? null : mapDim.getMultiworldIdsCopy();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String tryToGetMultiworldName(class_5321<class_1937> dimId, String multiworldId) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiPauseSync;
        synchronized (object) {
            if (mapProcessor.isUIPaused()) {
                return null;
            }
            return this.getMultiworldNameUnsynced(mapProcessor, dimId, multiworldId);
        }
    }

    private String getMultiworldNameUnsynced(MapProcessor mapProcessor, class_5321<class_1937> dimId, String multiworldId) {
        MapDimension mapDim = !mapProcessor.isMapWorldUsable() ? null : mapProcessor.getMapWorld().createDimensionUnsynced(dimId);
        return mapDim == null ? null : mapDim.getMultiworldName(multiworldId);
    }

    public void openSettings() {
        class_437 current = class_310.method_1551().field_1755;
        class_437 currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        class_310.method_1551().method_1507((class_437)new GuiWorldMapSettings(current, currentEscScreen));
    }

    public float getMinimapBrightness() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return 1.0f;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        return mapProcessor.getBrightness(this.modMain.getSettings().getLighting());
    }

    public boolean screenShouldSkipWorldRender(class_437 screen) {
        return xaero.map.misc.Misc.screenShouldSkipWorldRender((class_437)screen, (boolean)false);
    }

    public void prepareMapTexturedRect(Matrix4f matrix, float x, float y, int textureX, int textureY, float width, float height, MapTileChunk chunk, MultiTextureRenderTypeRenderer noLightRenderer, MultiTextureRenderTypeRenderer withLightrenderer, MinimapRendererHelper helper) {
        LeafRegionTexture leafTexture = chunk.getLeafTexture();
        GpuTextureAndView texture = leafTexture.getGlColorTexture();
        if (texture == null) {
            return;
        }
        MultiTextureRenderUtil.prepareTexturedRect(matrix, x, y, textureX, (int)height, width, height, -height, 64.0f, texture.texture, leafTexture.getTextureHasLight() ? withLightrenderer : noLightRenderer);
    }

    public boolean getAdjustHeightForCarpetLikeBlocks() {
        return WorldMap.settings.adjustHeightForCarpetLikeBlocks;
    }

    public void registerHighlighters(HighlighterRegistry highlighterRegistry) {
        SupportMods.xaeroMinimap.registerMinimapHighlighters((Object)highlighterRegistry);
    }

    public void createRadarRenderWrapper(RadarRenderer radarRenderer) {
        SupportMods.xaeroMinimap.createRadarRendererWrapper((Object)radarRenderer);
    }

    public boolean worldMapIsRenderingRadar() {
        return WorldMap.settings.minimapRadar;
    }

    public boolean getPartialYTeleport() {
        return WorldMap.settings.partialYTeleportation;
    }

    public boolean isStainedGlassDisplayed() {
        return WorldMap.settings.displayStainedGlass;
    }

    public boolean isMultiplayerMap() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return false;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        return mapProcessor.getMapWorld().isMultiplayer();
    }

    public int getManualCaveStart() {
        return WorldMap.settings.caveModeStart == Integer.MAX_VALUE ? Integer.MAX_VALUE : WorldMap.settings.caveModeStart;
    }

    public boolean hasEnabledCaveLayers() {
        return this.getCaveModeType() == 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCaveModeType() {
        if (!WorldMap.settings.isCaveMapsAllowed()) {
            return 0;
        }
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return WorldMap.settings.defaultCaveModeType;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.uiPauseSync;
        synchronized (object) {
            if (mapProcessor.isUIPaused()) {
                return WorldMap.settings.defaultCaveModeType;
            }
            MapDimension mapDim = mapProcessor.getMapWorld().getCurrentDimension();
            if (mapDim != null) {
                return mapDim.getCaveModeType();
            }
        }
        return WorldMap.settings.defaultCaveModeType;
    }

    public void openScreenForOption(ModOptions option) {
        class_437 currentEscScreen;
        if (class_310.method_1551().field_1687 == null) {
            return;
        }
        class_437 current = class_310.method_1551().field_1755;
        class_437 class_4372 = currentEscScreen = current instanceof ScreenBase ? ((ScreenBase)current).escape : null;
        if (currentEscScreen instanceof GuiMap) {
            currentEscScreen = null;
        }
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        GuiMap screen = new GuiMap(current, currentEscScreen, mapProcessor, class_310.method_1551().method_1560());
        if (option == ModOptions.MANUAL_CAVE_MODE_START) {
            screen.enableCaveModeOptions();
        }
        class_310.method_1551().method_1507((class_437)screen);
    }

    public int getCaveModeDepth() {
        return WorldMap.settings.caveModeDepth;
    }

    public boolean isLegibleCaveMaps() {
        return WorldMap.settings.legibleCaveMaps;
    }

    public boolean getBiomeBlending() {
        return WorldMap.settings.biomeBlending;
    }

    public void confirmPlayerRadarRender(class_1657 e) {
        if (WorldMap.trackedPlayerRenderer.getCollector().playerExists(e.method_5667())) {
            WorldMap.trackedPlayerRenderer.getCollector().confirmPlayerRadarRender(e);
        }
    }

    public boolean getDisplayClaims() {
        return WorldMap.settings.displayClaims;
    }

    public int getClaimsBorderOpacity() {
        return WorldMap.settings.claimsBorderOpacity;
    }

    public int getClaimsFillOpacity() {
        return WorldMap.settings.claimsFillOpacity;
    }

    public void toggleChunkClaims() {
        WorldMap.settings.setOptionValue(xaero.map.settings.ModOptions.PAC_CLAIMS, (Object)((Boolean)WorldMap.settings.getOptionValue(xaero.map.settings.ModOptions.PAC_CLAIMS) == false ? 1 : 0));
    }

    public boolean caveLayersAreUsable() {
        boolean result = this.hasEnabledCaveLayers();
        if (result) {
            WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession == null) {
                return result;
            }
            class_1297 player = class_310.method_1551().method_1560();
            if (player == null) {
                return result;
            }
            MapProcessor mapProcessor = worldmapSession.getMapProcessor();
            MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
            if (mapDimension == null) {
                return result;
            }
            if (mapDimension.getDimId() != player.method_37908().method_27983()) {
                return false;
            }
        }
        return result;
    }

    public boolean shouldPreventAutoCaveMode(class_1937 world) {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return false;
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        if (mapDimension == null) {
            return false;
        }
        return mapDimension.getDimId() != world.method_27983();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double getMapDimensionScale() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return class_310.method_1551().field_1687.method_8597().comp_646();
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        Object object = mapProcessor.renderThreadPauseSync;
        synchronized (object) {
            if (mapProcessor.isRenderingPaused()) {
                return 0.0;
            }
            class_2378 dimTypes = mapProcessor.getWorldDimensionTypeRegistry();
            if (dimTypes == null) {
                return 0.0;
            }
            return mapProcessor.getMapWorld().getCurrentDimension().calculateDimScale(dimTypes);
        }
    }

    public class_5321<class_1937> getMapDimension() {
        WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return class_310.method_1551().field_1687.method_27983();
        }
        MapProcessor mapProcessor = worldmapSession.getMapProcessor();
        MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        return mapDimension == null ? class_310.method_1551().field_1687.method_27983() : mapDimension.getDimId();
    }
}

