/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.minecraft.class_1937
 *  net.minecraft.class_2561
 *  net.minecraft.class_5250
 *  net.minecraft.class_5321
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package xaero.map.highlight;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.nio.ByteBuffer;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_5321;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import xaero.map.WorldMap;
import xaero.map.highlight.AbstractHighlighter;
import xaero.map.highlight.HighlighterRegistry;
import xaero.map.mods.SupportMods;
import xaero.map.pool.buffer.PoolTextureDirectBufferUnit;
import xaero.map.world.MapDimension;

public class DimensionHighlighterHandler {
    private final MapDimension mapDimension;
    private final class_5321<class_1937> dimension;
    private final HighlighterRegistry registry;
    private final Long2ObjectMap<Integer> hashCodeCache;
    private final class_2561 SUBTLE_TOOLTIP_SEPARATOR = class_2561.method_43470((String)" | ");
    private final class_2561 BLUNT_TOOLTIP_SEPARATOR = class_2561.method_43470((String)" \n ");

    public DimensionHighlighterHandler(MapDimension mapDimension, class_5321<class_1937> dimension, HighlighterRegistry registry) {
        this.mapDimension = mapDimension;
        this.dimension = dimension;
        this.registry = registry;
        this.hashCodeCache = new Long2ObjectOpenHashMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRegionHash(int regionX, int regionZ) {
        DimensionHighlighterHandler dimensionHighlighterHandler = this;
        synchronized (dimensionHighlighterHandler) {
            long key = DimensionHighlighterHandler.getKey(regionX, regionZ);
            Integer cachedHash = (Integer)this.hashCodeCache.get(key);
            if (cachedHash == null) {
                cachedHash = this.recalculateHash(regionX, regionZ);
            }
            return cachedHash;
        }
    }

    public boolean shouldApplyRegionHighlights(int regionX, int regionZ, boolean discovered) {
        class_5321<class_1937> dimension = this.dimension;
        for (AbstractHighlighter hl : this.registry.getHighlighters()) {
            if (!discovered && !hl.isCoveringOutsideDiscovered() || !hl.regionHasHighlights(dimension, regionX, regionZ)) continue;
            return true;
        }
        return false;
    }

    public boolean shouldApplyTileChunkHighlights(int regionX, int regionZ, int insideTileChunkX, int insideTileChunkZ, boolean discovered) {
        int startChunkX = regionX << 5 | insideTileChunkX << 2;
        int startChunkZ = regionZ << 5 | insideTileChunkZ << 2;
        for (AbstractHighlighter hl : this.registry.getHighlighters()) {
            if (!this.shouldApplyTileChunkHighlightsHelp(hl, regionX, regionZ, startChunkX, startChunkZ, discovered)) continue;
            return true;
        }
        return false;
    }

    private boolean shouldApplyTileChunkHighlights(AbstractHighlighter hl, int regionX, int regionZ, int insideTileChunkX, int insideTileChunkZ, boolean discovered) {
        int startChunkX = regionX << 5 | insideTileChunkX << 2;
        int startChunkZ = regionZ << 5 | insideTileChunkZ << 2;
        return this.shouldApplyTileChunkHighlightsHelp(hl, regionX, regionZ, startChunkX, startChunkZ, discovered);
    }

    private boolean shouldApplyTileChunkHighlightsHelp(AbstractHighlighter hl, int regionX, int regionZ, int startChunkX, int startChunkZ, boolean discovered) {
        if (!discovered && !hl.isCoveringOutsideDiscovered()) {
            return false;
        }
        class_5321<class_1937> dimension = this.dimension;
        if (!hl.regionHasHighlights(dimension, regionX, regionZ)) {
            return false;
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (!hl.chunkIsHighlit(dimension, startChunkX | i, startChunkZ | j)) continue;
                return true;
            }
        }
        return false;
    }

    public PoolTextureDirectBufferUnit applyChunkHighlightColors(int chunkX, int chunkZ, int innerChunkX, int innerChunkZ, PoolTextureDirectBufferUnit buffer, PoolTextureDirectBufferUnit highlitColorBuffer, boolean highlitBufferPrepared, boolean discovered, boolean separateBuffer) {
        boolean hasSomething = false;
        class_5321<class_1937> dimension = this.dimension;
        if (!separateBuffer) {
            highlitBufferPrepared = true;
            highlitColorBuffer = buffer;
        }
        ByteBuffer highlitColorBufferDirect = highlitColorBuffer == null ? null : highlitColorBuffer.getDirectBuffer();
        for (AbstractHighlighter hl : this.registry.getHighlighters()) {
            int[] highlightColors;
            if (!discovered && !hl.isCoveringOutsideDiscovered() || (highlightColors = hl.getChunkHighlitColor(dimension, chunkX, chunkZ)) == null) continue;
            if (!hasSomething && !highlitBufferPrepared) {
                highlitColorBuffer = WorldMap.textureDirectBufferPool.get(buffer == null);
                highlitColorBufferDirect = highlitColorBuffer.getDirectBuffer();
                if (buffer != null) {
                    highlitColorBufferDirect.put(buffer.getDirectBuffer());
                }
                highlitColorBufferDirect.position(0);
                if (buffer != null) {
                    buffer.getDirectBuffer().position(0);
                }
            }
            hasSomething = true;
            int textureOffset = innerChunkZ << 4 << 6 | innerChunkX << 4;
            for (int i = 0; i < highlightColors.length; ++i) {
                int highlightColor = highlightColors[i];
                int hlAlpha = highlightColor & 0xFF;
                float hlAlphaFloat = (float)hlAlpha / 255.0f;
                float oneMinusHlAlpha = 1.0f - hlAlphaFloat;
                int hlRed = highlightColor >> 8 & 0xFF;
                int hlGreen = highlightColor >> 16 & 0xFF;
                int hlBlue = highlightColor >> 24 & 0xFF;
                int index = textureOffset | i >> 4 << 6 | i & 0xF;
                int originalColor = highlitColorBufferDirect.getInt(index * 4);
                int red = originalColor >> 8 & 0xFF;
                int green = originalColor >> 16 & 0xFF;
                int blue = originalColor >> 24 & 0xFF;
                int alpha = originalColor & 0xFF;
                red = (int)((float)red * oneMinusHlAlpha + (float)hlRed * hlAlphaFloat);
                green = (int)((float)green * oneMinusHlAlpha + (float)hlGreen * hlAlphaFloat);
                blue = (int)((float)blue * oneMinusHlAlpha + (float)hlBlue * hlAlphaFloat);
                if (red > 255) {
                    red = 255;
                }
                if (green > 255) {
                    green = 255;
                }
                if (blue > 255) {
                    blue = 255;
                }
                highlitColorBufferDirect.putInt(index * 4, blue << 24 | green << 16 | red << 8 | alpha);
            }
        }
        if (!hasSomething) {
            return null;
        }
        return highlitColorBuffer;
    }

    private int recalculateHash(int regionX, int regionZ) {
        HashCodeBuilder hashcodeBuilder = new HashCodeBuilder();
        for (AbstractHighlighter hl : this.registry.getHighlighters()) {
            hashcodeBuilder.append(hl.calculateRegionHash(this.dimension, regionX, regionZ));
            hashcodeBuilder.append(hl.isCoveringOutsideDiscovered());
        }
        int builtHash = hashcodeBuilder.build();
        long key = DimensionHighlighterHandler.getKey(regionX, regionZ);
        this.hashCodeCache.put(key, (Object)builtHash);
        return builtHash;
    }

    public void clearCachedHash(int regionX, int regionZ) {
        long key = DimensionHighlighterHandler.getKey(regionX, regionZ);
        this.hashCodeCache.remove(key);
        this.mapDimension.onClearCachedHighlightHash(regionX, regionZ);
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onClearHighlightHash(regionX, regionZ);
        }
    }

    public void clearCachedHashes() {
        this.hashCodeCache.clear();
        this.mapDimension.onClearCachedHighlightHashes();
        if (SupportMods.minimap()) {
            SupportMods.xaeroMinimap.onClearHighlightHashes();
        }
    }

    public class_2561 getBlockHighlightSubtleTooltip(int blockX, int blockZ, boolean discovered) {
        return this.getBlockHighlightTooltip(blockX, blockZ, discovered, true);
    }

    public class_2561 getBlockHighlightBluntTooltip(int blockX, int blockZ, boolean discovered) {
        return this.getBlockHighlightTooltip(blockX, blockZ, discovered, false);
    }

    private class_2561 getBlockHighlightTooltip(int blockX, int blockZ, boolean discovered, boolean subtle) {
        class_5321<class_1937> dimension = this.dimension;
        int tileChunkX = blockX >> 6;
        int regionX = tileChunkX >> 3;
        int tileChunkZ = blockZ >> 6;
        int regionZ = tileChunkZ >> 3;
        if (!this.shouldApplyRegionHighlights(regionX, regionZ, discovered)) {
            return null;
        }
        int localTileChunkX = tileChunkX & 7;
        int localTileChunkZ = tileChunkZ & 7;
        class_5250 result = null;
        for (AbstractHighlighter hl : this.registry.getHighlighters()) {
            class_2561 hlTooltip;
            if (!this.shouldApplyTileChunkHighlights(hl, regionX, regionZ, localTileChunkX, localTileChunkZ, discovered) || (hlTooltip = subtle ? hl.getBlockHighlightSubtleTooltip(dimension, blockX, blockZ) : hl.getBlockHighlightBluntTooltip(dimension, blockX, blockZ)) == null) continue;
            if (result == null) {
                result = class_2561.method_43470((String)"");
            } else {
                result.method_10855().add(subtle ? this.SUBTLE_TOOLTIP_SEPARATOR : this.BLUNT_TOOLTIP_SEPARATOR);
            }
            result.method_10855().add(hlTooltip);
        }
        return result;
    }

    public static long getKey(int regionX, int regionZ) {
        return (long)regionZ << 32 | (long)regionX & 0xFFFFFFFFL;
    }

    public static int getXFromKey(long key) {
        return (int)(key & 0xFFFFFFFFFFFFFFFFL);
    }

    public static int getZFromKey(long key) {
        return (int)(key >> 32);
    }
}

