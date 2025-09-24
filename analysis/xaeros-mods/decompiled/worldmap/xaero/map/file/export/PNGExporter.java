/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.minecraft.class_10366
 *  net.minecraft.class_11278
 *  net.minecraft.class_1959
 *  net.minecraft.class_2378
 *  net.minecraft.class_276
 *  net.minecraft.class_2874
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 */
package xaero.map.file.export;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.class_10366;
import net.minecraft.class_11278;
import net.minecraft.class_1959;
import net.minecraft.class_2378;
import net.minecraft.class_276;
import net.minecraft.class_2874;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.exception.OpenGLException;
import xaero.map.file.MapRegionInfo;
import xaero.map.file.MapSaveLoad;
import xaero.map.file.OldFormatSupport;
import xaero.map.file.RegionDetection;
import xaero.map.file.export.PNGExportResult;
import xaero.map.file.export.PNGExportResultType;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.ImprovedFramebuffer;
import xaero.map.graphics.OpenGlHelper;
import xaero.map.graphics.TextureUtils;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.graphics.shader.WorldMapShaderHelper;
import xaero.map.gui.GuiMap;
import xaero.map.gui.MapTileSelection;
import xaero.map.misc.Misc;
import xaero.map.mods.SupportMods;
import xaero.map.region.ExportMapRegion;
import xaero.map.region.ExportMapTileChunk;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapLayer;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.OverlayManager;
import xaero.map.region.texture.ExportLeafRegionTexture;
import xaero.map.render.util.ImmediateRenderUtil;
import xaero.map.world.MapDimension;

public class PNGExporter {
    private final Calendar calendar = Calendar.getInstance();
    private Path destinationPath;
    private class_4587 matrixStack;
    private class_11278 projectionCache;

    public PNGExporter(Path destinationPath) {
        this.destinationPath = destinationPath;
        this.matrixStack = new class_4587();
    }

    public PNGExportResult export(MapProcessor mapProcessor, class_2378<class_1959> biomeRegistry, class_2378<class_2874> dimensionTypes, MapTileSelection selection, OldFormatSupport oldFormatSupport) throws IllegalArgumentException, IllegalAccessException, OpenGLException {
        int[] bufferArray;
        ByteBuffer frameDataBuffer;
        ImprovedFramebuffer exportFrameBuffer;
        BufferedImage image;
        int pixelCount;
        boolean multipleImages;
        long maxExportAreaSizeInRegions;
        int scaleDownSquareSquared;
        int n;
        if (!mapProcessor.getMapSaveLoad().isRegionDetectionComplete()) {
            return new PNGExportResult(PNGExportResultType.NOT_PREPARED, null);
        }
        int exportedLayer = mapProcessor.getCurrentCaveLayer();
        MapDimension dim = mapProcessor.getMapWorld().getCurrentDimension();
        Set<LeveledRegion<?>> list = dim.getLayeredMapRegions().getUnsyncedSet();
        if (list.isEmpty()) {
            return new PNGExportResult(PNGExportResultType.EMPTY, null);
        }
        boolean multipleImagesSetting = WorldMap.settings.multipleImagesExport;
        boolean nightExportSetting = WorldMap.settings.nightExport;
        int exportScaleDownSquareSetting = WorldMap.settings.exportScaleDownSquare;
        boolean includingHighlights = WorldMap.settings.highlightsExport;
        boolean full = selection == null;
        Integer minX = null;
        Integer maxX = null;
        Integer minZ = null;
        Integer maxZ = null;
        MapLayer mapLayer = dim.getLayeredMapRegions().getLayer(exportedLayer);
        if (full) {
            for (LeveledRegion<?> region : list) {
                if (region.getLevel() != 0 || !((MapRegion)region).hasHadTerrain() || region.getCaveLayer() != exportedLayer) continue;
                if (minX == null || region.getRegionX() < minX) {
                    minX = region.getRegionX();
                }
                if (maxX == null || region.getRegionX() > maxX) {
                    maxX = region.getRegionX();
                }
                if (minZ == null || region.getRegionZ() < minZ) {
                    minZ = region.getRegionZ();
                }
                if (maxZ != null && region.getRegionZ() <= maxZ) continue;
                maxZ = region.getRegionZ();
            }
            Iterable<Hashtable<Integer, RegionDetection>> regionDetectionIterable = !dim.isUsingWorldSave() ? mapLayer.getDetectedRegions().values() : dim.getWorldSaveDetectedRegions();
            for (Hashtable hashtable : regionDetectionIterable) {
                for (RegionDetection regionDetection : hashtable.values()) {
                    if (!regionDetection.isHasHadTerrain()) continue;
                    if (minX == null || regionDetection.getRegionX() < minX) {
                        minX = regionDetection.getRegionX();
                    }
                    if (maxX == null || regionDetection.getRegionX() > maxX) {
                        maxX = regionDetection.getRegionX();
                    }
                    if (minZ == null || regionDetection.getRegionZ() < minZ) {
                        minZ = regionDetection.getRegionZ();
                    }
                    if (maxZ != null && regionDetection.getRegionZ() <= maxZ) continue;
                    maxZ = regionDetection.getRegionZ();
                }
            }
        } else {
            minX = selection.getLeft() >> 5;
            minZ = selection.getTop() >> 5;
            maxX = selection.getRight() >> 5;
            maxZ = selection.getBottom() >> 5;
        }
        int minBlockX = minX * 512;
        int minBlockZ = minZ * 512;
        int n2 = (maxX + 1) * 512 - 1;
        int maxBlockZ = (maxZ + 1) * 512 - 1;
        if (!full) {
            minBlockX = Math.max(minBlockX, selection.getLeft() << 4);
            minBlockZ = Math.max(minBlockZ, selection.getTop() << 4);
            n = Math.min(n2, (selection.getRight() << 4) + 15);
            maxBlockZ = Math.min(maxBlockZ, (selection.getBottom() << 4) + 15);
        }
        int exportAreaWidthInRegions = maxX - minX + 1;
        int exportAreaHeightInRegions = maxZ - minZ + 1;
        long exportAreaSizeInRegions = (long)exportAreaWidthInRegions * (long)exportAreaHeightInRegions;
        int exportAreaWidth = exportAreaWidthInRegions * 512;
        int exportAreaHeight = exportAreaHeightInRegions * 512;
        if (!full) {
            exportAreaWidth = n - minBlockX + 1;
            exportAreaHeight = maxBlockZ - minBlockZ + 1;
        }
        float scale = exportAreaSizeInRegions < (long)(scaleDownSquareSquared = exportScaleDownSquareSetting * exportScaleDownSquareSetting) || multipleImagesSetting || scaleDownSquareSquared <= 0 ? 1.0f : (float)((double)exportScaleDownSquareSetting / Math.sqrt(exportAreaSizeInRegions));
        int exportImageWidth = (int)((float)exportAreaWidth * scale);
        int exportImageHeight = (int)((float)exportAreaHeight * scale);
        if (!multipleImagesSetting && scaleDownSquareSquared > 0 && (long)exportAreaWidth * (long)exportAreaHeight / 512L / 512L > (maxExportAreaSizeInRegions = (long)scaleDownSquareSquared * 262144L)) {
            return new PNGExportResult(PNGExportResultType.TOO_BIG, null);
        }
        int maxTextureSize = GL11.glGetInteger((int)3379);
        OpenGLException.checkGLError();
        int frameWidth = Math.min(1024, Math.min(maxTextureSize, exportImageWidth));
        int frameHeight = Math.min(1024, Math.min(maxTextureSize, exportImageHeight));
        int horizontalFrames = (int)Math.ceil((double)exportImageWidth / (double)frameWidth);
        int verticalFrames = (int)Math.ceil((double)exportImageHeight / (double)frameHeight);
        boolean bl = multipleImages = multipleImagesSetting && horizontalFrames * verticalFrames > 1;
        if (multipleImages) {
            exportImageWidth = frameWidth;
            exportImageHeight = frameHeight;
        }
        if ((pixelCount = exportImageWidth * exportImageHeight) == Integer.MAX_VALUE || pixelCount / exportImageHeight != exportImageWidth) {
            return new PNGExportResult(PNGExportResultType.IMAGE_TOO_BIG, null);
        }
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info(String.format("Exporting PNG of size %dx%d using a framebuffer of size %dx%d.", exportImageWidth, exportImageHeight, frameWidth, frameHeight));
        }
        try {
            image = new BufferedImage(exportImageWidth, exportImageHeight, 1);
            exportFrameBuffer = new ImprovedFramebuffer(frameWidth, frameHeight, true);
            frameDataBuffer = BufferUtils.createByteBuffer((int)(frameWidth * frameHeight * 4));
            bufferArray = new int[frameWidth * frameHeight];
        }
        catch (OutOfMemoryError oome) {
            return new PNGExportResult(PNGExportResultType.OUT_OF_MEMORY, null);
        }
        if (exportFrameBuffer.method_30277() == null || exportFrameBuffer.method_30278() == null) {
            return new PNGExportResult(PNGExportResultType.BAD_FBO, null);
        }
        class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60026);
        if (this.projectionCache == null) {
            this.projectionCache = new class_11278("png export render", 0.0f, 1000.0f, false);
        }
        GpuBufferSlice ortho = this.projectionCache.method_71092((float)frameWidth, (float)frameHeight);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)ortho, (class_10366)class_10366.field_54954);
        class_4587 matrixStack = this.matrixStack;
        BlockStateShortShapeCache shortShapeCache = mapProcessor.getBlockStateShortShapeCache();
        BlockTintProvider blockTintProvider = mapProcessor.getWorldBlockTintProvider();
        OverlayManager overlayManager = mapProcessor.getOverlayManager();
        MapSaveLoad mapSaveLoad = mapProcessor.getMapSaveLoad();
        MultiTextureRenderTypeRendererProvider rendererProvider = mapProcessor.getMultiTextureRenderTypeRenderers();
        Matrix4fStack shaderMatrixStack = RenderSystem.getModelViewStack();
        shaderMatrixStack.pushMatrix();
        shaderMatrixStack.identity();
        matrixStack.method_22903();
        exportFrameBuffer.bindAsMainTarget(true);
        matrixStack.method_22905(scale, scale, 1.0f);
        boolean[] justMetaDest = new boolean[1];
        Path imageDestination = this.destinationPath;
        if (multipleImages) {
            imageDestination = this.destinationPath.resolve(this.getExportBaseName());
        }
        boolean empty = true;
        PNGExportResultType resultType = PNGExportResultType.SUCCESS;
        for (int i = 0; i < horizontalFrames; ++i) {
            for (int j = 0; j < verticalFrames; ++j) {
                PNGExportResultType saveResult;
                boolean renderedSomething = false;
                TextureUtils.clearRenderTarget((class_276)exportFrameBuffer, -16777216, 1.0f);
                matrixStack.method_22903();
                float frameLeft = (float)minBlockX + (float)(i * frameWidth) / scale;
                float frameRight = (float)minBlockX + (float)((i + 1) * frameWidth) / scale - 1.0f;
                float frameTop = (float)minBlockZ + (float)(j * frameHeight) / scale;
                float frameBottom = (float)minBlockZ + (float)((j + 1) * frameHeight) / scale - 1.0f;
                if (!full) {
                    if ((float)n < frameRight) {
                        frameRight = n;
                    }
                    if ((float)maxBlockZ < frameBottom) {
                        frameBottom = maxBlockZ;
                    }
                }
                int minTileChunkX = (int)Math.floor(frameLeft) >> 6;
                int maxTileChunkX = (int)Math.floor(frameRight) >> 6;
                int minTileChunkZ = (int)Math.floor(frameTop) >> 6;
                int maxTileChunkZ = (int)Math.floor(frameBottom) >> 6;
                int minRegionX = minTileChunkX >> 3;
                int minRegionZ = minTileChunkZ >> 3;
                int maxRegionX = maxTileChunkX >> 3;
                int maxRegionZ = maxTileChunkZ >> 3;
                matrixStack.method_22904(0.1, 0.0, 0.0);
                Matrix4f matrix = matrixStack.method_23760().method_23761();
                for (int regionX = minRegionX; regionX <= maxRegionX; ++regionX) {
                    for (int regionZ = minRegionZ; regionZ <= maxRegionZ; ++regionZ) {
                        boolean loadingFromCache;
                        boolean regionHasHighlightsIfUndiscovered;
                        MapRegion originalRegion = mapProcessor.getLeafMapRegion(exportedLayer, regionX, regionZ, false);
                        MapRegionInfo regionInfo = originalRegion;
                        if (originalRegion == null && mapLayer.regionDetectionExists(regionX, regionZ)) {
                            regionInfo = mapLayer.getRegionDetection(regionX, regionZ);
                        }
                        boolean bl2 = regionHasHighlightsIfUndiscovered = includingHighlights && dim.getHighlightHandler().shouldApplyRegionHighlights(regionX, regionZ, false);
                        if (regionInfo == null && !regionHasHighlightsIfUndiscovered) continue;
                        File cacheFile = null;
                        boolean bl3 = loadingFromCache = regionInfo != null && (originalRegion == null || !originalRegion.isBeingWritten() || originalRegion.getLoadState() != 2);
                        if (loadingFromCache) {
                            cacheFile = regionInfo.getCacheFile();
                            if (cacheFile == null && !regionInfo.hasLookedForCache()) {
                                try {
                                    cacheFile = mapSaveLoad.getCacheFile(regionInfo, exportedLayer, true, false);
                                }
                                catch (IOException iOException) {
                                    // empty catch block
                                }
                            }
                            if (cacheFile == null) {
                                if (!regionHasHighlightsIfUndiscovered) continue;
                                loadingFromCache = false;
                            }
                        }
                        ExportMapRegion region = new ExportMapRegion(dim, regionX, regionZ, exportedLayer, biomeRegistry);
                        if (loadingFromCache) {
                            region.setShouldCache(true, "png");
                            region.setHasHadTerrain();
                            region.setCacheFile(cacheFile);
                            region.loadCacheTextures(mapProcessor, biomeRegistry, false, null, 0, null, justMetaDest, 1, oldFormatSupport);
                        } else if (originalRegion != null) {
                            for (int o = 0; o < 8; ++o) {
                                for (int p = 0; p < 8; ++p) {
                                    MapTileChunk originalTileChunk = originalRegion.getChunk(o, p);
                                    if (originalTileChunk == null || !originalTileChunk.hasHadTerrain()) continue;
                                    MapTileChunk tileChunk = region.createTexture(o, p).getTileChunk();
                                    for (int tx = 0; tx < 4; ++tx) {
                                        for (int tz = 0; tz < 4; ++tz) {
                                            tileChunk.setTile(tx, tz, originalTileChunk.getTile(tx, tz), shortShapeCache);
                                        }
                                    }
                                    tileChunk.setLoadState((byte)2);
                                    tileChunk.updateBuffers(mapProcessor, blockTintProvider, overlayManager, WorldMap.settings.detailed_debug, shortShapeCache);
                                }
                            }
                        }
                        if (includingHighlights) {
                            mapProcessor.getMapRegionHighlightsPreparer().prepare(region, true);
                        }
                        MultiTextureRenderTypeRenderer rendererLight = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP);
                        MultiTextureRenderTypeRenderer rendererNoLight = rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP);
                        ArrayList<GpuTextureAndView> texturesToDelete = new ArrayList<GpuTextureAndView>();
                        for (int localChunkX = 0; localChunkX < 8; ++localChunkX) {
                            for (int localChunkZ = 0; localChunkZ < 8; ++localChunkZ) {
                                ExportLeafRegionTexture tileChunkTexture;
                                ExportMapTileChunk tileChunk = region.getChunk(localChunkX, localChunkZ);
                                if (tileChunk == null || (tileChunkTexture = tileChunk.getLeafTexture()) == null) continue;
                                if (tileChunk.getX() < minTileChunkX || tileChunk.getX() > maxTileChunkX || tileChunk.getZ() < minTileChunkZ || tileChunk.getZ() > maxTileChunkZ) {
                                    tileChunkTexture.deleteColorBuffer();
                                    continue;
                                }
                                GpuTextureAndView textureId = tileChunkTexture.bindColorTexture(true);
                                if (tileChunkTexture.getColorBuffer() == null) {
                                    tileChunkTexture.prepareBuffer();
                                }
                                ByteBuffer colorBuffer = tileChunkTexture.getDirectColorBuffer();
                                if (includingHighlights) {
                                    tileChunkTexture.applyHighlights(dim.getHighlightHandler(), tileChunkTexture.getColorBuffer());
                                }
                                TextureFormat internalFormat = tileChunkTexture.getColorBufferFormat() == null ? ExportLeafRegionTexture.DEFAULT_INTERNAL_FORMAT : tileChunkTexture.getColorBufferFormat();
                                OpenGlHelper.uploadBGRABufferToMapTexture(colorBuffer, textureId.texture, internalFormat, 64, 64);
                                tileChunkTexture.deleteColorBuffer();
                                if (textureId == null) continue;
                                textureId.texture.setTextureFilter(FilterMode.LINEAR, FilterMode.NEAREST, true);
                                OpenGlHelper.generateMipmaps(textureId.texture);
                                GuiMap.renderTexturedModalRectWithLighting3(matrix, (float)(tileChunk.getX() * 64) - frameLeft, (float)(tileChunk.getZ() * 64) - frameTop, 64.0f, 64.0f, textureId.texture, tileChunkTexture.getBufferHasLight(), tileChunkTexture.getBufferHasLight() ? rendererLight : rendererNoLight);
                                renderedSomething = true;
                                texturesToDelete.add(textureId);
                            }
                        }
                        float brightness = nightExportSetting ? mapProcessor.getAmbientBrightness(dim.getDimensionType(dimensionTypes)) : mapProcessor.getBrightness(exportedLayer, mapProcessor.getWorld(), WorldMap.settings.lighting && exportedLayer != Integer.MAX_VALUE);
                        WorldMapShaderHelper.setBrightness(brightness);
                        WorldMapShaderHelper.setWithLight(true);
                        rendererProvider.draw(rendererLight);
                        WorldMapShaderHelper.setWithLight(false);
                        rendererProvider.draw(rendererNoLight);
                        OpenGlHelper.deleteTextures(texturesToDelete, texturesToDelete.size());
                    }
                }
                matrixStack.method_22909();
                ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                if (!renderedSomething) continue;
                empty = false;
                frameDataBuffer.clear();
                OpenGlHelper.downloadMapTextureToBGRABuffer(exportFrameBuffer.method_30277(), frameDataBuffer);
                frameDataBuffer.asIntBuffer().get(bufferArray);
                int insertOffsetX = i * frameWidth;
                int insertOffsetZ = j * frameHeight;
                if (multipleImages) {
                    insertOffsetX = 0;
                    insertOffsetZ = 0;
                }
                int actualFrameWidth = Math.min(frameWidth, exportImageWidth - insertOffsetX);
                int actualFrameHeight = Math.min(frameHeight, exportImageHeight - insertOffsetZ);
                image.setRGB(insertOffsetX, insertOffsetZ, actualFrameWidth, actualFrameHeight, bufferArray, 0, frameWidth);
                if (!multipleImages || (saveResult = this.saveImage(image, imageDestination, i + "_" + j, "_x" + (int)frameLeft + "_z" + (int)frameTop)) == PNGExportResultType.SUCCESS) continue;
                resultType = saveResult;
            }
        }
        class_310 mc = class_310.method_1551();
        exportFrameBuffer.bindDefaultFramebuffer(mc);
        matrixStack.method_22909();
        shaderMatrixStack.popMatrix();
        Misc.minecraftOrtho(mc, SupportMods.vivecraft);
        exportFrameBuffer.method_1238();
        mapProcessor.getBufferDeallocator().deallocate(frameDataBuffer, WorldMap.settings.debug);
        if (empty) {
            return new PNGExportResult(PNGExportResultType.EMPTY, null);
        }
        if (multipleImages) {
            image.flush();
            return new PNGExportResult(resultType, imageDestination);
        }
        resultType = this.saveImage(image, imageDestination, null, "_x" + minBlockX + "_z" + minBlockZ);
        image.flush();
        return new PNGExportResult(resultType, imageDestination);
    }

    private PNGExportResultType saveImage(BufferedImage image, Path destinationPath, String baseName, String suffix) {
        if (baseName == null) {
            baseName = this.getExportBaseName();
        }
        baseName = (String)baseName + suffix;
        int additionalIndex = 1;
        try {
            if (!Files.exists(destinationPath, new LinkOption[0])) {
                Files.createDirectories(destinationPath, new FileAttribute[0]);
            }
            Path imagePath = destinationPath.resolve((String)baseName + ".png");
            while (Files.exists(imagePath, new LinkOption[0])) {
                imagePath = destinationPath.resolve((String)baseName + "_" + ++additionalIndex + ".png");
            }
            ImageIO.write((RenderedImage)image, "png", imagePath.toFile());
            return PNGExportResultType.SUCCESS;
        }
        catch (IOException e1) {
            WorldMap.LOGGER.error("IO exception while exporting PNG: ", (Throwable)e1);
            return PNGExportResultType.IO_EXCEPTION;
        }
    }

    private String getExportBaseName() {
        this.calendar.setTimeInMillis(System.currentTimeMillis());
        int year = this.calendar.get(1);
        int month = 1 + this.calendar.get(2);
        int day = this.calendar.get(5);
        int hours = this.calendar.get(11);
        int minutes = this.calendar.get(12);
        int seconds = this.calendar.get(13);
        return String.format("%d-%02d-%02d_%02d.%02d.%02d", year, month, day, hours, minutes, seconds);
    }
}

