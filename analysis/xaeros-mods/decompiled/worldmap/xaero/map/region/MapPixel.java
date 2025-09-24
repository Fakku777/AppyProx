/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_2189
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2378
 *  net.minecraft.class_2386
 *  net.minecraft.class_2404
 *  net.minecraft.class_2504
 *  net.minecraft.class_2506
 *  net.minecraft.class_2680
 *  net.minecraft.class_2874
 */
package xaero.map.region;

import java.util.ArrayList;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_2189;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2386;
import net.minecraft.class_2404;
import net.minecraft.class_2504;
import net.minecraft.class_2506;
import net.minecraft.class_2680;
import net.minecraft.class_2874;
import xaero.map.MapProcessor;
import xaero.map.MapWriter;
import xaero.map.WorldMap;
import xaero.map.biome.BlockTintProvider;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.region.MapBlock;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.Overlay;
import xaero.map.region.OverlayManager;
import xaero.map.world.MapDimension;

public class MapPixel {
    private static final int VOID_COLOR = -16121833;
    private static final float DEFAULT_AMBIENT_LIGHT = 0.7f;
    private static final float DEFAULT_AMBIENT_LIGHT_COLORED = 0.2f;
    private static final float DEFAULT_AMBIENT_LIGHT_WHITE = 0.5f;
    private static final float DEFAULT_MAX_DIRECT_LIGHT = 0.6666667f;
    private static final float GLOWING_MAX_DIRECT_LIGHT = 0.22222224f;
    protected class_2680 state;
    protected byte light = 0;
    protected boolean glowing = false;

    private int getVanillaTransparency(class_2248 b) {
        return b instanceof class_2404 ? 191 : (b instanceof class_2386 ? 216 : 127);
    }

    public void getPixelColours(int[] result_dest, MapWriter mapWriter, class_1937 world, MapDimension dim, class_2378<class_2248> blockRegistry, MapTileChunk tileChunk, MapTileChunk prevChunk, MapTileChunk prevChunkDiagonal, MapTileChunk prevChunkHorisontal, MapTile mapTile, int x, int z, MapBlock block, int height, int topHeight, int caveStart, int caveDepth, ArrayList<Overlay> overlays, class_2338.class_2339 mutableGlobalPos, class_2378<class_1959> biomeRegistry, class_2378<class_2874> dimensionTypes, float shadowR, float shadowG, float shadowB, BlockTintProvider blockTintProvider, MapProcessor mapProcessor, OverlayManager overlayManager, BlockStateShortShapeCache blockStateShortShapeCache) {
        int colour = block != null && caveStart != Integer.MAX_VALUE ? 0 : -16121833;
        int topLightValue = this.light;
        int lightMin = 9;
        float brightnessR = 1.0f;
        float brightnessG = 1.0f;
        float brightnessB = 1.0f;
        mutableGlobalPos.method_10103(mapTile.getChunkX() * 16 + x, height, mapTile.getChunkZ() * 16 + z);
        class_2680 state = this.state;
        boolean isAir = state.method_26204() instanceof class_2189;
        boolean isFinalBlock = this instanceof MapBlock;
        if (!isAir) {
            if (WorldMap.settings.colours == 0) {
                colour = mapWriter.loadBlockColourFromTexture(state, true, world, blockRegistry, (class_2338)mutableGlobalPos);
            } else {
                try {
                    class_2248 b = state.method_26204();
                    int a = this.getVanillaTransparency(b);
                    colour = state.method_26205((class_1922)world, (class_2338)mutableGlobalPos).field_16011;
                    if (!isFinalBlock && colour == 0) {
                        result_dest[0] = -1;
                        return;
                    }
                    colour = a << 24 | colour & 0xFFFFFF;
                }
                catch (Exception b) {
                    // empty catch block
                }
            }
            if (!isFinalBlock && !WorldMap.settings.displayStainedGlass && (state.method_26204() instanceof class_2506 || state.method_26204() instanceof class_2504)) {
                result_dest[0] = -1;
                return;
            }
        }
        int r = colour >> 16 & 0xFF;
        int g = colour >> 8 & 0xFF;
        int b = colour & 0xFF;
        if (WorldMap.settings.biomeColorsVanillaMode || WorldMap.settings.colours == 0) {
            int c = blockTintProvider.getBiomeColor((class_2338)mutableGlobalPos, state, !isFinalBlock, mapTile, tileChunk.getInRegion().getCaveLayer());
            float rMultiplier = (float)r / 255.0f;
            float gMultiplier = (float)g / 255.0f;
            float bMultiplier = (float)b / 255.0f;
            r = (int)((float)(c >> 16 & 0xFF) * rMultiplier);
            g = (int)((float)(c >> 8 & 0xFF) * gMultiplier);
            b = (int)((float)(c & 0xFF) * bMultiplier);
        }
        if (this.glowing) {
            int total = r + g + b;
            float minBrightness = 407.0f;
            float brightener = Math.max(1.0f, minBrightness / (float)total);
            r = (int)((float)r * brightener);
            g = (int)((float)g * brightener);
            b = (int)((float)b * brightener);
            topLightValue = 15;
        }
        int overlayRed = 0;
        int overlayGreen = 0;
        int overlayBlue = 0;
        float currentTransparencyMultiplier = 1.0f;
        boolean legibleCaveMaps = WorldMap.settings.legibleCaveMaps && caveStart != Integer.MAX_VALUE;
        boolean hasValidOverlay = false;
        if (overlays != null && !overlays.isEmpty()) {
            int sun = 15;
            for (int i = 0; i < overlays.size(); ++i) {
                Overlay o = overlays.get(i);
                o.getPixelColour(block, result_dest, mapWriter, world, dim, blockRegistry, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, mapTile, x, z, caveStart, caveDepth, mutableGlobalPos, biomeRegistry, dimensionTypes, shadowR, shadowG, shadowB, blockTintProvider, mapProcessor, overlayManager);
                if (result_dest[0] == -1) continue;
                hasValidOverlay = true;
                if (i == 0) {
                    topLightValue = o.light;
                }
                float transparency = (float)result_dest[3] / 255.0f;
                float overlayIntensity = this.getBlockBrightness(lightMin, o.light, sun) * transparency * currentTransparencyMultiplier;
                overlayRed = (int)((float)overlayRed + (float)result_dest[0] * overlayIntensity);
                overlayGreen = (int)((float)overlayGreen + (float)result_dest[1] * overlayIntensity);
                overlayBlue = (int)((float)overlayBlue + (float)result_dest[2] * overlayIntensity);
                if ((sun -= o.getOpacity()) < 0) {
                    sun = 0;
                }
                currentTransparencyMultiplier *= 1.0f - transparency;
            }
            if (!legibleCaveMaps && hasValidOverlay && !this.glowing && !isAir) {
                brightnessG = brightnessB = this.getBlockBrightness(lightMin, this.light, sun);
                brightnessR = brightnessB;
            }
        }
        if (isFinalBlock) {
            if (block.slopeUnknown) {
                if (!isAir) {
                    block.fixHeightType(x, z, mapTile, tileChunk, prevChunk, prevChunkDiagonal, prevChunkHorisontal, height, false, blockStateShortShapeCache);
                } else {
                    block.setVerticalSlope((byte)0);
                    block.setDiagonalSlope((byte)0);
                    block.slopeUnknown = false;
                }
            }
            float depthBrightness = 1.0f;
            int slopes = WorldMap.settings.terrainSlopes;
            if (legibleCaveMaps) {
                topLightValue = 15;
            }
            if (height != Short.MAX_VALUE) {
                if (legibleCaveMaps && (!isAir || hasValidOverlay)) {
                    float caveBrightness;
                    int depthCalculationBase = 0;
                    int depthCalculationHeight = height;
                    int depthCalculationBottom = caveStart + 1 - caveDepth;
                    int depthCalculationTop = caveStart;
                    if (caveStart == Integer.MIN_VALUE) {
                        depthCalculationBottom = 0;
                        depthCalculationTop = 63;
                        int odd = depthCalculationHeight >> 6 & 1;
                        depthCalculationHeight = 63 * odd + (1 - 2 * odd) * (depthCalculationHeight & 0x3F);
                        depthCalculationBase = 16;
                    }
                    int caveRange = 1 + depthCalculationTop - depthCalculationBottom;
                    if (!isAir && !this.glowing) {
                        caveBrightness = (1.0f + (float)depthCalculationBase + (float)depthCalculationHeight - (float)depthCalculationBottom) / (float)(depthCalculationBase + caveRange);
                        brightnessR *= caveBrightness;
                        brightnessG *= caveBrightness;
                        brightnessB *= caveBrightness;
                    }
                    if (hasValidOverlay) {
                        depthCalculationHeight = topHeight;
                        if (caveStart == Integer.MIN_VALUE) {
                            int odd = depthCalculationHeight >> 6 & 1;
                            depthCalculationHeight = 63 * odd + (1 - 2 * odd) * (depthCalculationHeight & 0x3F);
                        }
                        caveBrightness = (1.0f + (float)depthCalculationBase + (float)depthCalculationHeight - (float)depthCalculationBottom) / (float)(depthCalculationBase + caveRange);
                        overlayRed = (int)((float)overlayRed * caveBrightness);
                        overlayGreen = (int)((float)overlayGreen * caveBrightness);
                        overlayBlue = (int)((float)overlayBlue * caveBrightness);
                    }
                } else if (!isAir && !this.glowing && WorldMap.settings.terrainDepth) {
                    float min;
                    if (caveStart == Integer.MAX_VALUE) {
                        depthBrightness = (float)height / 63.0f;
                    } else if (caveStart == Integer.MIN_VALUE) {
                        depthBrightness = 0.7f + 0.3f * (float)height / (float)dim.getDimensionType(dimensionTypes).comp_653();
                    } else {
                        int caveBottom = caveStart - caveDepth;
                        depthBrightness = 0.7f + 0.3f * (float)(height - caveBottom) / (float)caveDepth;
                    }
                    float max = slopes >= 2 ? 1.0f : 1.15f;
                    float f = min = slopes >= 2 ? 0.9f : 0.7f;
                    if (depthBrightness > max) {
                        depthBrightness = max;
                    } else if (depthBrightness < min) {
                        depthBrightness = min;
                    }
                }
            }
            if (!isAir && slopes > 0 && !block.slopeUnknown) {
                byte verticalSlope = block.getVerticalSlope();
                if (slopes == 1) {
                    if (verticalSlope > 0) {
                        depthBrightness = (float)((double)depthBrightness * 1.15);
                    } else if (verticalSlope < 0) {
                        depthBrightness = (float)((double)depthBrightness * 0.85);
                    }
                } else {
                    byte diagonalSlope = block.getDiagonalSlope();
                    float ambientLightColored = 0.2f;
                    float ambientLightWhite = 0.5f;
                    float maxDirectLight = 0.6666667f;
                    if (this.glowing) {
                        ambientLightColored = 0.0f;
                        ambientLightWhite = 1.0f;
                        maxDirectLight = 0.22222224f;
                    }
                    float cos = 0.0f;
                    if (slopes == 2) {
                        float crossZ = -verticalSlope;
                        if (crossZ < 1.0f) {
                            if (verticalSlope == 1 && diagonalSlope == 1) {
                                cos = 1.0f;
                            } else {
                                float crossX = verticalSlope - diagonalSlope;
                                float cast = 1.0f - crossZ;
                                float crossMagnitude = (float)Math.sqrt(crossX * crossX + 1.0f + crossZ * crossZ);
                                cos = (float)((double)(cast / crossMagnitude) / Math.sqrt(2.0));
                            }
                        }
                    } else if (verticalSlope >= 0) {
                        if (verticalSlope == 1) {
                            cos = 1.0f;
                        } else {
                            float surfaceDirectionMagnitude = (float)Math.sqrt(verticalSlope * verticalSlope + 1);
                            float castToMostLit = verticalSlope + 1;
                            cos = (float)((double)(castToMostLit / surfaceDirectionMagnitude) / Math.sqrt(2.0));
                        }
                    }
                    float directLightClamped = 0.0f;
                    if (cos == 1.0f) {
                        directLightClamped = maxDirectLight;
                    } else if (cos > 0.0f) {
                        directLightClamped = (float)Math.ceil(cos * 10.0f) / 10.0f * maxDirectLight * 0.88388f;
                    }
                    float whiteLight = ambientLightWhite + directLightClamped;
                    brightnessR *= shadowR * ambientLightColored + whiteLight;
                    brightnessG *= shadowG * ambientLightColored + whiteLight;
                    brightnessB *= shadowB * ambientLightColored + whiteLight;
                }
            }
            brightnessR *= depthBrightness;
            brightnessG *= depthBrightness;
            brightnessB *= depthBrightness;
            result_dest[3] = (int)(this.getPixelLight(lightMin, topLightValue) * 255.0f);
        } else {
            result_dest[3] = colour >> 24 & 0xFF;
            if (result_dest[3] == 0) {
                result_dest[3] = this.getVanillaTransparency(state.method_26204());
            }
        }
        result_dest[0] = (int)((float)r * brightnessR * currentTransparencyMultiplier + (float)overlayRed);
        if (result_dest[0] > 255) {
            result_dest[0] = 255;
        }
        result_dest[1] = (int)((float)g * brightnessG * currentTransparencyMultiplier + (float)overlayGreen);
        if (result_dest[1] > 255) {
            result_dest[1] = 255;
        }
        result_dest[2] = (int)((float)b * brightnessB * currentTransparencyMultiplier + (float)overlayBlue);
        if (result_dest[2] > 255) {
            result_dest[2] = 255;
        }
    }

    public float getBlockBrightness(float min, int l, int sun) {
        return (min + (float)Math.max(sun, l)) / (15.0f + min);
    }

    private float getPixelLight(float min, int topLightValue) {
        return topLightValue == 0 ? 0.0f : this.getBlockBrightness(min, topLightValue, 0);
    }

    public class_2680 getState() {
        return this.state;
    }

    public void setState(class_2680 state) {
        this.state = state;
    }

    public void setLight(byte light) {
        this.light = light;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }
}

