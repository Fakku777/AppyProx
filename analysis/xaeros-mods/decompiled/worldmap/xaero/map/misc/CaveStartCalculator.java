/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1937
 *  net.minecraft.class_1944
 *  net.minecraft.class_2246
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2404
 *  net.minecraft.class_2680
 *  net.minecraft.class_2688
 *  net.minecraft.class_2818
 *  net.minecraft.class_2826
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_3481
 *  net.minecraft.class_3532
 *  net.minecraft.class_3619
 */
package xaero.map.misc;

import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2404;
import net.minecraft.class_2680;
import net.minecraft.class_2688;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2902;
import net.minecraft.class_3481;
import net.minecraft.class_3532;
import net.minecraft.class_3619;
import xaero.map.MapWriter;
import xaero.map.WorldMap;
import xaero.map.misc.CachedFunction;

public class CaveStartCalculator {
    private final class_2338.class_2339 mutableBlockPos = new class_2338.class_2339();
    private final CachedFunction<class_2688<?, ?>, Boolean> transparentCache = new CachedFunction<class_2688, Boolean>(state -> mapWriter.shouldOverlay((class_2688<?, ?>)state));

    public CaveStartCalculator(MapWriter mapWriter) {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public int getCaving(double playerX, double playerY, double playerZ, class_1937 world) {
        if (WorldMap.settings.autoCaveMode == 0) {
            return Integer.MAX_VALUE;
        }
        int worldBottomY = world.method_31607();
        int worldTopY = world.method_31600();
        int y = (int)playerY + 1;
        int defaultCaveStart = y + 3;
        int defaultResult = Integer.MAX_VALUE;
        if (y > worldTopY || y < worldBottomY) {
            return defaultResult;
        }
        int x = class_3532.method_15357((double)playerX);
        int z = class_3532.method_15357((double)playerZ);
        int roofRadius = WorldMap.settings.autoCaveMode < 0 ? 1 : WorldMap.settings.autoCaveMode - 1;
        int roofDiameter = 1 + roofRadius * 2;
        int startX = x - roofRadius;
        int startZ = z - roofRadius;
        boolean ignoringHeightmaps = WorldMap.settings.ignoreHeightmaps;
        int bottom = y;
        int top = Integer.MAX_VALUE;
        class_2818 prevBChunk = null;
        int potentialResult = defaultCaveStart;
        for (int o = 0; o < roofDiameter; ++o) {
            block1: for (int p = 0; p < roofDiameter; ++p) {
                int currentX = startX + o;
                int currentZ = startZ + p;
                this.mutableBlockPos.method_10103(currentX, y, currentZ);
                class_2818 bchunk = world.method_8497(currentX >> 4, currentZ >> 4);
                if (bchunk == null) {
                    return defaultResult;
                }
                int skyLight = world.method_8314(class_1944.field_9284, (class_2338)this.mutableBlockPos);
                if (!ignoringHeightmaps) {
                    if (skyLight >= 15) return defaultResult;
                    int insideX = currentX & 0xF;
                    int insideZ = currentZ & 0xF;
                    top = bchunk.method_12005(class_2902.class_2903.field_13202, insideX, insideZ);
                } else if (bchunk != prevBChunk) {
                    class_2826[] sections = bchunk.method_12006();
                    if (sections.length == 0) {
                        return defaultResult;
                    }
                    int playerSection = y - worldBottomY >> 4;
                    boolean foundSomething = false;
                    for (int i = playerSection; i < sections.length; ++i) {
                        class_2826 searchedSection = sections[i];
                        if (searchedSection.method_38292()) continue;
                        if (!foundSomething) {
                            bottom = Math.max(bottom, worldBottomY + (i << 4));
                            foundSomething = true;
                        }
                        top = worldBottomY + (i << 4) + 15;
                    }
                    if (!foundSomething) {
                        return defaultResult;
                    }
                    prevBChunk = bchunk;
                }
                if (top < worldBottomY) {
                    return defaultResult;
                }
                if (top > worldTopY) {
                    top = worldTopY;
                }
                for (int i = bottom; i <= top; ++i) {
                    this.mutableBlockPos.method_33098(i);
                    class_2680 state = world.method_8320((class_2338)this.mutableBlockPos);
                    if (state.method_26215() || state.method_26223() == class_3619.field_15971 || state.method_26204() instanceof class_2404 || state.method_26164(class_3481.field_15503) || this.transparentCache.apply((class_2688<?, ?>)state).booleanValue() || state.method_26204() == class_2246.field_10499) continue;
                    if (o != p || o != roofRadius) continue block1;
                    potentialResult = Math.min(i, defaultCaveStart);
                    continue block1;
                }
                return defaultResult;
            }
        }
        return potentialResult;
    }
}

