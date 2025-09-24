/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  net.minecraft.class_1923
 *  net.minecraft.class_1937
 *  net.minecraft.class_1959
 *  net.minecraft.class_1972
 *  net.minecraft.class_2189
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2378
 *  net.minecraft.class_2487
 *  net.minecraft.class_2499
 *  net.minecraft.class_2507
 *  net.minecraft.class_2512
 *  net.minecraft.class_2680
 *  net.minecraft.class_2688
 *  net.minecraft.class_2806
 *  net.minecraft.class_2861
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3218
 *  net.minecraft.class_3508
 *  net.minecraft.class_3532
 *  net.minecraft.class_3610
 *  net.minecraft.class_3611
 *  net.minecraft.class_3619
 *  net.minecraft.class_3898
 *  net.minecraft.class_3977
 *  net.minecraft.class_4284
 *  net.minecraft.class_4543
 *  net.minecraft.class_4543$class_4544
 *  net.minecraft.class_5321
 *  net.minecraft.class_6490
 *  net.minecraft.class_7225
 *  net.minecraft.class_7871
 */
package xaero.map.file.worldsave;

import com.mojang.datafixers.DataFixer;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2507;
import net.minecraft.class_2512;
import net.minecraft.class_2680;
import net.minecraft.class_2688;
import net.minecraft.class_2806;
import net.minecraft.class_2861;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3218;
import net.minecraft.class_3508;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_3619;
import net.minecraft.class_3898;
import net.minecraft.class_3977;
import net.minecraft.class_4284;
import net.minecraft.class_4543;
import net.minecraft.class_5321;
import net.minecraft.class_6490;
import net.minecraft.class_7225;
import net.minecraft.class_7871;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.executor.Executor;
import xaero.map.file.worldsave.WorldDataChunkTileEntityLookup;
import xaero.map.file.worldsave.biome.WorldDataBiomeManager;
import xaero.map.file.worldsave.biome.WorldDataReaderSectionBiomeData;
import xaero.map.misc.CachedFunction;
import xaero.map.mods.SupportMods;
import xaero.map.region.MapBlock;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.OverlayBuilder;
import xaero.map.region.OverlayManager;

public class WorldDataReader {
    private MapProcessor mapProcessor;
    private boolean[] shouldEnterGround;
    private boolean[] underair;
    private boolean[] blockFound;
    private byte[] lightLevels;
    private byte[] skyLightLevels;
    private int[] topH;
    private MapBlock buildingObject = new MapBlock();
    private OverlayBuilder[] overlayBuilders;
    private class_2338.class_2339 mutableBlockPos;
    private List<class_2680> blockStatePalette;
    private class_6490 heightMapBitArray;
    private class_6490 blockStatesBitArray;
    private CompletableFuture<Optional<class_2487>>[] chunkNBTCompounds;
    public Object taskCreationSync;
    private BlockStateShortShapeCache blockStateShortShapeCache;
    private class_5321<class_1959> defaultBiomeKey;
    private final CachedFunction<class_2688<?, ?>, Boolean> transparentCache;
    private int[] firstTransparentStateY;
    private boolean[] shouldExtendTillTheBottom;
    private CachedFunction<class_3610, class_2680> fluidToBlock;
    private WorldDataBiomeManager biomeManager;
    private final class_4543 biomeZoomer;

    public WorldDataReader(OverlayManager overlayManager, BlockStateShortShapeCache blockStateShortShapeCache, WorldDataBiomeManager biomeManager, long biomeZoomSeed) {
        this.underair = new boolean[256];
        this.shouldEnterGround = new boolean[256];
        this.blockFound = new boolean[256];
        this.lightLevels = new byte[256];
        this.skyLightLevels = new byte[256];
        this.overlayBuilders = new OverlayBuilder[256];
        this.mutableBlockPos = new class_2338.class_2339();
        this.blockStatePalette = new ArrayList<class_2680>();
        this.heightMapBitArray = new class_3508(9, 256);
        this.taskCreationSync = new Object();
        for (int i = 0; i < this.overlayBuilders.length; ++i) {
            this.overlayBuilders[i] = new OverlayBuilder(overlayManager);
        }
        CompletableFuture[] chunkNBTCompounds = new CompletableFuture[16];
        this.chunkNBTCompounds = chunkNBTCompounds;
        this.topH = new int[256];
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.defaultBiomeKey = class_1972.field_9473;
        this.transparentCache = new CachedFunction<class_2688, Boolean>(state -> this.mapProcessor.getMapWriter().shouldOverlay((class_2688<?, ?>)state));
        this.shouldExtendTillTheBottom = new boolean[256];
        this.firstTransparentStateY = new int[256];
        this.fluidToBlock = new CachedFunction<class_3610, class_2680>(class_3610::method_15759);
        this.biomeManager = biomeManager;
        this.biomeZoomer = new class_4543((class_4543.class_4544)biomeManager, biomeZoomSeed);
    }

    public void setMapProcessor(MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }

    private void updateHeightArray(int bitsPerHeight) {
        if (this.heightMapBitArray.method_34896() != bitsPerHeight) {
            this.heightMapBitArray = new class_3508(bitsPerHeight, 256);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean buildRegion(MapRegion region, class_3218 serverWorld, class_7225<class_2248> blockLookup, class_2378<class_2248> blockRegistry, class_2378<class_3611> fluidRegistry, boolean loading, int[] chunkCountDest, Executor renderExecutor) {
        if (!loading) {
            region.pushWriterPause();
        }
        boolean result = true;
        int prevRegX = region.getRegionX();
        int prevRegZ = region.getRegionZ() - 1;
        MapRegion prevRegion = this.mapProcessor.getLeafMapRegion(region.getCaveLayer(), prevRegX, prevRegZ, false);
        region.updateCaveMode();
        int caveStart = region.getCaveStart();
        int caveDepth = region.getCaveDepth();
        boolean worldHasSkylight = serverWorld.method_8597().comp_642();
        boolean ignoreHeightmaps = this.mapProcessor.getMapWorld().isIgnoreHeightmaps();
        boolean flowers = WorldMap.settings.flowers;
        if (loading || region.getLoadState() == 2) {
            serverWorld.method_8503().method_20493(() -> serverWorld.method_14178().method_17298(false)).join();
            int worldBottomY = serverWorld.method_31607();
            int worldTopY = serverWorld.method_31600() + 1;
            class_3898 chunkManager = serverWorld.method_14178().field_17254;
            class_2378<class_1959> biomeRegistry = region.getBiomeRegistry();
            class_1959 theVoid = (class_1959)biomeRegistry.method_29107(class_1972.field_9473);
            this.biomeManager.resetChunkBiomeData(region.getRegionX(), region.getRegionZ(), theVoid, biomeRegistry);
            CompletableFuture lastFuture = null;
            for (int i = -1; i < 9; ++i) {
                for (int j = -1; j < 9; ++j) {
                    if (i < 0 || j < 0 || i >= 8 || j >= 8) {
                        this.handleTileChunkOutsideRegion(i, j, (region.getRegionX() << 3) + i, (region.getRegionZ() << 3) + j, caveStart, ignoreHeightmaps, biomeRegistry, flowers, (class_3977)chunkManager);
                    } else {
                        MapTileChunk tileChunk = region.getChunk(i, j);
                        if (tileChunk == null) {
                            tileChunk = new MapTileChunk(region, (region.getRegionX() << 3) + i, (region.getRegionZ() << 3) + j);
                            region.setChunk(i, j, tileChunk);
                            MapRegion mapRegion = region;
                            synchronized (mapRegion) {
                                region.setAllCachePrepared(false);
                            }
                        }
                        if (region.isMetaLoaded()) {
                            tileChunk.getLeafTexture().setBufferedTextureVersion(region.getAndResetCachedTextureVersion(i, j));
                        }
                        this.readChunkNBTCompounds((class_3977)chunkManager, tileChunk);
                        this.buildTileChunk(tileChunk, caveStart, caveDepth, worldHasSkylight, ignoreHeightmaps, prevRegion, serverWorld, blockLookup, blockRegistry, fluidRegistry, biomeRegistry, flowers, worldBottomY, worldTopY);
                        if (!tileChunk.includeInSave() && !tileChunk.hasHighlightsIfUndiscovered()) {
                            region.uncountTextureBiomes(tileChunk.getLeafTexture());
                            region.setChunk(i, j, null);
                            tileChunk.getLeafTexture().deleteTexturesAndBuffers();
                            tileChunk = null;
                        } else {
                            if (!loading && !tileChunk.includeInSave() && tileChunk.hasHadTerrain()) {
                                tileChunk.getLeafTexture().deleteColorBuffer();
                                tileChunk.unsetHasHadTerrain();
                                tileChunk.setChanged(false);
                            }
                            if (chunkCountDest != null) {
                                chunkCountDest[0] = chunkCountDest[0] + 1;
                            }
                        }
                    }
                    if (i <= 0 || j <= 0) continue;
                    MapTileChunk topLeftTileChunk = region.getChunk(i - 1, j - 1);
                    if (topLeftTileChunk != null && topLeftTileChunk.includeInSave()) {
                        this.fillBiomes(topLeftTileChunk, this.biomeZoomer, biomeRegistry);
                        lastFuture = renderExecutor.method_20493(() -> {
                            this.transferFilledBiomes(topLeftTileChunk, this.biomeZoomer, biomeRegistry);
                            topLeftTileChunk.setToUpdateBuffers(true);
                            topLeftTileChunk.setChanged(false);
                            topLeftTileChunk.setLoadState((byte)2);
                        });
                    }
                    if (lastFuture == null || i != 8 || j != 8) continue;
                    lastFuture.join();
                }
            }
            this.biomeManager.clear();
            if (region.isNormalMapData()) {
                region.setLastSaveTime(System.currentTimeMillis() - 60000L + 1500L);
            }
        } else {
            result = false;
        }
        if (!loading) {
            region.popWriterPause();
        }
        return result;
    }

    private void readChunkNBTCompounds(class_3977 chunkLoader, MapTileChunk tileChunk) {
        for (int xl = 0; xl < 4; ++xl) {
            for (int zl = 0; zl < 4; ++zl) {
                int i = zl << 2 | xl;
                this.chunkNBTCompounds[i] = chunkLoader.method_23696(new class_1923(tileChunk.getX() * 4 + xl, tileChunk.getZ() * 4 + zl));
            }
        }
    }

    public class_2487 readChunk(class_2861 regionFile, class_1923 pos) throws IOException {
        try (DataInputStream datainputstream = regionFile.method_21873(pos);){
            if (datainputstream != null) {
                class_2487 class_24872 = class_2507.method_10627((DataInput)datainputstream);
                return class_24872;
            }
            class_2487 class_24873 = null;
            return class_24873;
        }
    }

    private void buildTileChunk(MapTileChunk tileChunk, int caveStart, int caveDepth, boolean worldHasSkylight, boolean ignoreHeightmaps, MapRegion prevRegion, class_3218 serverWorld, class_7225<class_2248> blockLookup, class_2378<class_2248> blockRegistry, class_2378<class_3611> fluidRegistry, class_2378<class_1959> biomeRegistry, boolean flowers, int worldBottomY, int worldTopY) {
        tileChunk.unincludeInSave();
        tileChunk.resetHeights();
        for (int insideX = 0; insideX < 4; ++insideX) {
            for (int insideZ = 0; insideZ < 4; ++insideZ) {
                int i;
                DataFixer fixer;
                MapTile tile = tileChunk.getTile(insideX, insideZ);
                int chunkX = (tileChunk.getX() << 2) + insideX;
                int chunkZ = (tileChunk.getZ() << 2) + insideZ;
                class_2487 nbttagcompound = null;
                try {
                    Optional<class_2487> optional = this.chunkNBTCompounds[insideZ << 2 | insideX].get();
                    if (optional.isPresent()) {
                        nbttagcompound = optional.get();
                    }
                }
                catch (InterruptedException interruptedException) {
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (nbttagcompound == null) {
                    if (tile == null) continue;
                    tileChunk.setChanged(true);
                    tileChunk.setTile(insideX, insideZ, null, this.blockStateShortShapeCache);
                    this.mapProcessor.getTilePool().addToPool(tile);
                    continue;
                }
                boolean createdTile = false;
                if (tile == null) {
                    tile = this.mapProcessor.getTilePool().get(this.mapProcessor.getCurrentDimension(), chunkX, chunkZ);
                    createdTile = true;
                }
                if (this.buildTile(nbttagcompound = class_4284.field_19214.method_48130(fixer = class_310.method_1551().method_1543(), nbttagcompound, i = nbttagcompound.method_10550("DataVersion").orElse(-1).intValue()), tile, tileChunk, chunkX, chunkZ, chunkX & 0x1F, chunkZ & 0x1F, caveStart, caveDepth, worldHasSkylight, ignoreHeightmaps, serverWorld, blockLookup, blockRegistry, fluidRegistry, biomeRegistry, flowers, worldBottomY, worldTopY)) {
                    tile.setWrittenCave(caveStart, caveDepth);
                    tileChunk.setTile(insideX, insideZ, tile, this.blockStateShortShapeCache);
                    if (!createdTile) continue;
                    tileChunk.setChanged(true);
                    continue;
                }
                tileChunk.setTile(insideX, insideZ, null, this.blockStateShortShapeCache);
                this.mapProcessor.getTilePool().addToPool(tile);
            }
        }
    }

    private boolean buildTile(class_2487 nbttagcompound, MapTile tile, MapTileChunk tileChunk, int chunkX, int chunkZ, int insideRegionX, int insideRegionZ, int caveStart, int caveDepth, boolean worldHasSkylight, boolean ignoreHeightmaps, class_3218 serverWorld, class_7225<class_2248> blockLookup, class_2378<class_2248> blockRegistry, class_2378<class_3611> fluidRegistry, class_2378<class_1959> biomeRegistry, boolean flowers, int worldBottomY, int worldTopY) {
        boolean heightMapExists;
        class_2487 levelCompound = nbttagcompound;
        boolean oldOptimizedChunk = levelCompound.method_10545("below_zero_retrogen");
        String status = !oldOptimizedChunk ? levelCompound.method_68564("Status", null) : levelCompound.method_68568("below_zero_retrogen").method_68564("target_status", null);
        int chunkStatusIndex = class_2806.method_12168((String)status).method_16559();
        if (chunkStatusIndex < class_2806.field_12794.method_16559()) {
            return false;
        }
        this.handleChunkBiomes(levelCompound, insideRegionX, insideRegionZ);
        if (chunkStatusIndex < class_2806.field_12795.method_16559()) {
            return false;
        }
        class_2499 sectionsList = levelCompound.method_68569("sections");
        int fillCounter = 256;
        int[] topH = this.topH;
        int chunkBottomY = levelCompound.method_10550("yPos").orElse(0) * 16;
        boolean[] shouldExtendTillTheBottom = this.shouldExtendTillTheBottom;
        boolean cave = caveStart != Integer.MAX_VALUE;
        boolean fullCave = caveStart == Integer.MIN_VALUE;
        for (int i = 0; i < this.blockFound.length; ++i) {
            this.overlayBuilders[i].startBuilding();
            this.blockFound[i] = false;
            this.underair[i] = this.shouldEnterGround[i] = fullCave;
            this.lightLevels[i] = 0;
            this.skyLightLevels[i] = worldHasSkylight ? 15 : 0;
            topH[i] = worldBottomY;
            shouldExtendTillTheBottom[i] = false;
        }
        boolean oldHeightMap = !levelCompound.method_10545("Heightmaps");
        int[] oldHeightMapArray = null;
        if (oldHeightMap) {
            oldHeightMapArray = levelCompound.method_10561("HeightMap").orElse(null);
            heightMapExists = oldHeightMapArray != null && oldHeightMapArray.length == 256;
        } else {
            long[] heightMapArray = levelCompound.method_68568("Heightmaps").method_10565("WORLD_SURFACE").orElse(null);
            int potentialBitsPerHeight = heightMapArray == null ? 0 : heightMapArray.length / 4;
            boolean bl = heightMapExists = potentialBitsPerHeight > 0 && potentialBitsPerHeight <= 10;
            if (heightMapExists) {
                this.updateHeightArray(potentialBitsPerHeight);
                System.arraycopy(heightMapArray, 0, this.heightMapBitArray.method_15212(), 0, heightMapArray.length);
            }
        }
        boolean lightIsOn = levelCompound.method_10577("isLightOn").orElse(true);
        int caveStartSectionHeight = (fullCave ? serverWorld.method_31600() : caveStart) >> 4 << 4;
        int lowH = worldBottomY;
        if (cave && !fullCave && (lowH = caveStart + 1 - caveDepth) < worldBottomY) {
            lowH = worldBottomY;
        }
        int lowHSection = lowH >> 4 << 4;
        boolean transparency = true;
        if (sectionsList.size() == 0) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    MapBlock currentPixel = tile.getBlock(i, j);
                    this.buildingObject.prepareForWriting(worldBottomY);
                    this.buildingObject.write(class_2246.field_10124.method_9564(), worldBottomY, worldBottomY, null, (byte)0, false, cave);
                    tile.setBlock(i, j, this.buildingObject);
                    this.buildingObject = currentPixel != null ? currentPixel : new MapBlock();
                }
            }
        } else {
            class_2499 tileEntitiesNbt = levelCompound.method_68569("block_entities");
            WorldDataChunkTileEntityLookup tileEntityLookup = null;
            if (!tileEntitiesNbt.isEmpty()) {
                tileEntityLookup = new WorldDataChunkTileEntityLookup(tileEntitiesNbt);
            }
            int prevSectionHeight = Integer.MAX_VALUE;
            int sectionHeight = Integer.MAX_VALUE;
            for (int i = sectionsList.size() - 1; i >= 0 && fillCounter > 0; --i) {
                class_2487 sectionCompound = sectionsList.method_68582(i);
                sectionHeight = sectionCompound.method_10571("Y").orElse((byte)0) * 16;
                boolean hasBlocks = false;
                class_2487 blockStatesCompound = null;
                if (sectionCompound.method_10545("block_states")) {
                    blockStatesCompound = sectionCompound.method_68568("block_states");
                    boolean bl = hasBlocks = sectionHeight >= lowHSection;
                    if (hasBlocks && !(hasBlocks = blockStatesCompound.method_10545("data")) && blockStatesCompound.method_10545("palette")) {
                        class_2499 paletteList = blockStatesCompound.method_68569("palette");
                        boolean bl2 = hasBlocks = paletteList.size() == 1 && !((class_2487)paletteList.method_10534(0)).method_68564("Name", "").equals("minecraft:air");
                    }
                }
                if (i > 0 && !hasBlocks && !sectionCompound.method_10545("BlockLight") && (!cave || !sectionCompound.method_10545("SkyLight"))) continue;
                boolean previousSectionExists = prevSectionHeight - sectionHeight == 16;
                boolean underAirByDefault = cave && !previousSectionExists && caveStartSectionHeight > sectionHeight;
                int sectionBasedHeight = sectionHeight + 15;
                boolean preparedSectionData = false;
                boolean hasDifferentBlockStates = false;
                byte[] lightMap = null;
                byte[] skyLightMap = null;
                prevSectionHeight = sectionHeight;
                for (int z = 0; z < 16; ++z) {
                    block9: for (int x = 0; x < 16; ++x) {
                        int heightMapValue;
                        int pos_2d = (z << 4) + x;
                        if (this.blockFound[pos_2d]) continue;
                        int n = heightMapExists ? (oldHeightMap ? oldHeightMapArray[pos_2d] : chunkBottomY + this.heightMapBitArray.method_15211(pos_2d)) : (heightMapValue = Integer.MIN_VALUE);
                        int startHeight = cave && !fullCave ? caveStart : (ignoreHeightmaps || heightMapValue < chunkBottomY ? sectionBasedHeight : heightMapValue + 3);
                        if (startHeight >= worldTopY) {
                            startHeight = worldTopY - 1;
                        }
                        if (i > 0 && ++startHeight < sectionHeight) continue;
                        int localStartHeight = 15;
                        if (startHeight >> 4 << 4 == sectionHeight) {
                            localStartHeight = startHeight & 0xF;
                        }
                        if (!preparedSectionData) {
                            if (hasBlocks) {
                                class_2499 paletteList = blockStatesCompound.method_68569("palette");
                                hasDifferentBlockStates = blockStatesCompound.method_10545("data") && paletteList.size() > 1;
                                boolean shouldReadPalette = true;
                                if (hasDifferentBlockStates) {
                                    long[] blockStatesArray = blockStatesCompound.method_10565("data").orElse(null);
                                    int bits = blockStatesArray.length * 64 / 4096;
                                    int bitsOther = Math.max(4, class_3532.method_15342((int)paletteList.size()));
                                    if (bitsOther > 8) {
                                        bits = bitsOther;
                                    }
                                    if (this.blockStatesBitArray == null || this.blockStatesBitArray.method_34896() != bits) {
                                        this.blockStatesBitArray = new class_3508(bits, 4096);
                                    }
                                    if (blockStatesArray.length == this.blockStatesBitArray.method_15212().length) {
                                        System.arraycopy(blockStatesArray, 0, this.blockStatesBitArray.method_15212(), 0, blockStatesArray.length);
                                    } else {
                                        hasDifferentBlockStates = false;
                                        shouldReadPalette = false;
                                    }
                                }
                                this.blockStatePalette.clear();
                                if (shouldReadPalette) {
                                    paletteList.forEach(stateTag -> {
                                        class_2680 state = class_2512.method_10681((class_7871)blockLookup, (class_2487)((class_2487)stateTag));
                                        this.blockStatePalette.add(state);
                                    });
                                }
                            }
                            if ((lightMap = (byte[])sectionCompound.method_10547("BlockLight").orElse(null)) != null && lightMap.length != 2048) {
                                lightMap = null;
                            }
                            if (cave && (skyLightMap = (byte[])sectionCompound.method_10547("SkyLight").orElse(null)) != null && skyLightMap.length != 2048) {
                                skyLightMap = null;
                            }
                            preparedSectionData = true;
                        }
                        if (underAirByDefault) {
                            this.underair[pos_2d] = true;
                        }
                        for (int y = localStartHeight; y >= 0; --y) {
                            byte dataLight;
                            boolean buildResult;
                            class_2487 tileEntityNbt;
                            int h = sectionHeight | y;
                            int pos = y << 8 | pos_2d;
                            class_2680 state = null;
                            if (hasBlocks) {
                                int indexInPalette;
                                int n2 = indexInPalette = hasDifferentBlockStates ? this.blockStatesBitArray.method_15211(pos) : 0;
                                if (indexInPalette < this.blockStatePalette.size()) {
                                    state = this.blockStatePalette.get(indexInPalette);
                                }
                            }
                            if (state != null && tileEntityLookup != null && !(state.method_26204() instanceof class_2189) && SupportMods.framedBlocks() && SupportMods.supportFramedBlocks.isFrameBlock((class_1937)serverWorld, null, state) && (tileEntityNbt = tileEntityLookup.getTileEntityNbt(x, h, z)) != null) {
                                if (tileEntityNbt.method_10545("camo_state")) {
                                    try {
                                        state = class_2512.method_10681(blockLookup, (class_2487)tileEntityNbt.method_68568("camo_state"));
                                    }
                                    catch (IllegalArgumentException iae) {
                                        state = null;
                                    }
                                } else if (tileEntityNbt.method_10545("camo")) {
                                    class_2487 fluidTag;
                                    class_2487 camoNbt = tileEntityNbt.method_68568("camo");
                                    if (camoNbt.method_10545("state")) {
                                        try {
                                            state = class_2512.method_10681(blockLookup, (class_2487)camoNbt.method_68568("state"));
                                        }
                                        catch (IllegalArgumentException iae) {
                                            state = null;
                                        }
                                    } else if (camoNbt.method_10545("fluid") && (fluidTag = camoNbt.method_68568("fluid")).method_10545("Name")) {
                                        String fluidId = fluidTag.method_68564("Name", null);
                                        class_3611 fluid = (class_3611)fluidRegistry.method_63535(class_2960.method_60654((String)fluidId));
                                        class_2680 class_26802 = state = fluid == null ? null : this.fluidToBlock.apply(fluid.method_15785());
                                    }
                                }
                            }
                            if (state == null) {
                                state = class_2246.field_10124.method_9564();
                            }
                            this.mutableBlockPos.method_10103(chunkX << 4 | x, h, chunkZ << 4 | z);
                            OverlayBuilder overlayBuilder = this.overlayBuilders[pos_2d];
                            if (!shouldExtendTillTheBottom[pos_2d] && !overlayBuilder.isEmpty() && this.firstTransparentStateY[pos_2d] - h >= 5) {
                                shouldExtendTillTheBottom[pos_2d] = true;
                            }
                            boolean bl = buildResult = h >= lowH && h < startHeight && this.buildPixel(this.buildingObject, state, x, h, z, pos_2d, this.lightLevels[pos_2d], this.skyLightLevels[pos_2d], null, cave, fullCave, overlayBuilder, serverWorld, blockRegistry, this.mutableBlockPos, biomeRegistry, topH, shouldExtendTillTheBottom[pos_2d], flowers, transparency);
                            if (!buildResult && (y == 0 && i == 0 || h <= lowH)) {
                                this.lightLevels[pos_2d] = 0;
                                if (cave) {
                                    this.skyLightLevels[pos_2d] = 0;
                                }
                                h = worldBottomY;
                                state = class_2246.field_10124.method_9564();
                                buildResult = true;
                            }
                            if (buildResult) {
                                byte skyLight;
                                this.buildingObject.prepareForWriting(worldBottomY);
                                overlayBuilder.finishBuilding(this.buildingObject);
                                boolean glowing = this.mapProcessor.getMapWriter().isGlowing(state);
                                byte light = this.lightLevels[pos_2d];
                                if (cave && light < 15 && this.buildingObject.getNumberOfOverlays() == 0 && (skyLight = this.skyLightLevels[pos_2d]) > light) {
                                    light = skyLight;
                                }
                                this.buildingObject.write(state, h, topH[pos_2d], null, light, glowing, cave);
                                MapBlock currentPixel = tile.getBlock(x, z);
                                boolean equalsSlopesExcluded = this.buildingObject.equalsSlopesExcluded(currentPixel);
                                boolean fullyEqual = this.buildingObject.equals(currentPixel, equalsSlopesExcluded);
                                if (!fullyEqual) {
                                    tile.setBlock(x, z, this.buildingObject);
                                    this.buildingObject = currentPixel != null ? currentPixel : new MapBlock();
                                    if (!equalsSlopesExcluded) {
                                        tileChunk.setChanged(true);
                                    }
                                }
                                this.blockFound[pos_2d] = true;
                                --fillCounter;
                                continue block9;
                            }
                            byte by = dataLight = lightMap == null ? (byte)0 : this.nibbleValue(lightMap, pos);
                            if (cave && dataLight < 15 && worldHasSkylight) {
                                int dataSkyLight = !ignoreHeightmaps && !fullCave && startHeight > heightMapValue ? 15 : (skyLightMap == null ? 0 : this.nibbleValue(skyLightMap, pos));
                                this.skyLightLevels[pos_2d] = dataSkyLight;
                            }
                            this.lightLevels[pos_2d] = dataLight;
                        }
                    }
                }
            }
        }
        tile.setWorldInterpretationVersion(1);
        return true;
    }

    private boolean buildPixel(MapBlock pixel, class_2680 state, int x, int h, int z, int pos_2d, byte light, byte skyLight, class_5321<class_1959> biome, boolean cave, boolean fullCave, OverlayBuilder overlayBuilder, class_3218 serverWorld, class_2378<class_2248> blockRegistry, class_2338.class_2339 mutableBlockPos, class_2378<class_1959> biomeRegistry, int[] topH, boolean shouldExtendTillTheBottom, boolean flowers, boolean transparency) {
        class_3610 fluidFluidState = state.method_26227();
        class_2248 b = state.method_26204();
        if (!(fluidFluidState.method_15769() || cave && this.shouldEnterGround[pos_2d])) {
            this.underair[pos_2d] = true;
            class_2680 fluidState = this.fluidToBlock.apply(fluidFluidState);
            if (this.buildPixelHelp(pixel, fluidState, fluidState.method_26204(), fluidFluidState, pos_2d, h, cave, light, skyLight, biome, overlayBuilder, serverWorld, blockRegistry, biomeRegistry, topH, shouldExtendTillTheBottom, flowers, transparency)) {
                return true;
            }
        }
        if (b instanceof class_2189) {
            this.underair[pos_2d] = true;
            return false;
        }
        if (!this.underair[pos_2d] && cave) {
            return false;
        }
        if (b == this.fluidToBlock.apply(fluidFluidState).method_26204()) {
            return false;
        }
        if (cave && this.shouldEnterGround[pos_2d]) {
            if (!(state.method_50011() || state.method_45474() || state.method_26223() == class_3619.field_15971 || this.shouldOverlayCached((class_2688<?, ?>)state))) {
                this.underair[pos_2d] = false;
                this.shouldEnterGround[pos_2d] = false;
            }
            return false;
        }
        return this.buildPixelHelp(pixel, state, state.method_26204(), null, pos_2d, h, cave, light, skyLight, biome, overlayBuilder, serverWorld, blockRegistry, biomeRegistry, topH, shouldExtendTillTheBottom, flowers, transparency);
    }

    private boolean buildPixelHelp(MapBlock pixel, class_2680 state, class_2248 b, class_3610 fluidFluidState, int pos_2d, int h, boolean cave, byte light, byte skyLight, class_5321<class_1959> dataBiome, OverlayBuilder overlayBuilder, class_3218 serverWorld, class_2378<class_2248> blockRegistry, class_2378<class_1959> biomeRegistry, int[] topH, boolean shouldExtendTillTheBottom, boolean flowers, boolean transparency) {
        if (this.mapProcessor.getMapWriter().isInvisible(state, b, flowers)) {
            return false;
        }
        if (this.shouldOverlayCached((class_2688<?, ?>)(fluidFluidState == null ? state : fluidFluidState))) {
            if (cave && !this.underair[pos_2d]) {
                return !transparency;
            }
            if (h > topH[pos_2d]) {
                topH[pos_2d] = h;
            }
            byte overlayLight = light;
            if (overlayBuilder.isEmpty()) {
                this.firstTransparentStateY[pos_2d] = h;
                if (cave && skyLight > overlayLight) {
                    overlayLight = skyLight;
                }
            }
            if (shouldExtendTillTheBottom) {
                overlayBuilder.getCurrentOverlay().increaseOpacity(overlayBuilder.getCurrentOverlay().getState().method_26193());
            } else {
                overlayBuilder.build(state, state.method_26193(), overlayLight, this.mapProcessor, dataBiome);
            }
            return !transparency;
        }
        if (!this.mapProcessor.getMapWriter().hasVanillaColor(state, (class_1937)serverWorld, blockRegistry, (class_2338)this.mutableBlockPos)) {
            return false;
        }
        if (cave && !this.underair[pos_2d]) {
            return true;
        }
        if (h > topH[pos_2d]) {
            topH[pos_2d] = h;
        }
        return true;
    }

    private void handleTileChunkOutsideRegion(int relativeX, int relativeZ, int actualX, int actualZ, int caveStart, boolean ignoreHeightmaps, class_2378<class_1959> biomeRegistry, boolean flowers, class_3977 chunkLoader) {
        int insideZ;
        int insideX;
        int minInsideX = relativeX < 0 ? 3 : 0;
        int maxInsideX = relativeX > 7 ? 0 : 3;
        int minInsideZ = relativeZ < 0 ? 3 : 0;
        int maxInsideZ = relativeZ > 7 ? 0 : 3;
        for (insideX = minInsideX; insideX <= maxInsideX; ++insideX) {
            for (insideZ = minInsideZ; insideZ <= maxInsideZ; ++insideZ) {
                this.chunkNBTCompounds[insideZ << 2 | insideX] = chunkLoader.method_23696(new class_1923(actualX << 2 | insideX, actualZ << 2 | insideZ));
            }
        }
        for (insideX = minInsideX; insideX <= maxInsideX; ++insideX) {
            for (insideZ = minInsideZ; insideZ <= maxInsideZ; ++insideZ) {
                class_2487 nbt = null;
                try {
                    nbt = this.chunkNBTCompounds[insideZ << 2 | insideX].get().orElse(null);
                }
                catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                int insideRegionX = relativeX << 2 | insideX;
                int insideRegionZ = relativeZ << 2 | insideZ;
                if (nbt == null) continue;
                DataFixer fixer = class_310.method_1551().method_1543();
                int i = nbt.method_10550("DataVersion").orElse(-1);
                nbt = class_4284.field_19214.method_48130(fixer, nbt, i);
                this.handleTileOutsideRegion(nbt, insideRegionX, insideRegionZ);
            }
        }
    }

    private void handleTileOutsideRegion(class_2487 nbt, int insideRegionX, int insideRegionZ) {
        class_2487 levelCompound = nbt.method_68568("Level");
        String status = levelCompound.method_68564("Status", null);
        if (status == null || class_2806.method_12168((String)status).method_16559() < class_2806.field_12794.method_16559()) {
            return;
        }
        this.handleChunkBiomes(levelCompound, insideRegionX, insideRegionZ);
    }

    private void handleChunkBiomes(class_2487 levelCompound, int insideRegionX, int insideRegionZ) {
        class_2499 sectionsList = levelCompound.method_68569("sections");
        for (int i = 0; i < sectionsList.size(); ++i) {
            class_2499 biomePaletteList;
            class_2487 sectionCompound = sectionsList.method_68582(i);
            class_2487 biomesCompound = sectionCompound.method_68568("biomes");
            if (biomesCompound.method_33133() || (biomePaletteList = biomesCompound.method_68569("palette")).isEmpty()) continue;
            long[] biomesLongArray = biomePaletteList.size() == 1 ? null : (long[])biomesCompound.method_10565("data").orElse(null);
            WorldDataReaderSectionBiomeData biomeSection = new WorldDataReaderSectionBiomeData(biomePaletteList, biomesLongArray);
            byte sectionIndex = (Byte)sectionCompound.method_10571("Y").get();
            this.biomeManager.addBiomeSectionForRegionChunk(insideRegionX, insideRegionZ, sectionIndex, biomeSection);
        }
    }

    private void fillBiomes(MapTileChunk tileChunk, class_4543 biomeZoomer, class_2378<class_1959> biomeRegistry) {
        try {
            for (int insideX = 0; insideX < 4; ++insideX) {
                for (int insideZ = 0; insideZ < 4; ++insideZ) {
                    MapTile mapTile = tileChunk.getTile(insideX, insideZ);
                    if (mapTile == null) continue;
                    mapTile.setLoaded(true);
                    for (int x = 0; x < 16; ++x) {
                        for (int z = 0; z < 16; ++z) {
                            class_1959 biome;
                            class_5321 biomeKey;
                            MapBlock mapBlock = mapTile.getBlock(x, z);
                            int topHeight = mapBlock.getTopHeight();
                            if (topHeight == Short.MAX_VALUE) {
                                topHeight = mapBlock.getHeight();
                            }
                            if ((biomeKey = (class_5321)biomeRegistry.method_29113((Object)(biome = this.biomeManager.getBiome(biomeZoomer, mapTile.getChunkX() << 4 | x, topHeight, mapTile.getChunkZ() << 4 | z))).orElse(null)) == null) continue;
                            mapBlock.setBiome((class_5321<class_1959>)biomeKey);
                        }
                    }
                }
            }
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("Error filling tile chunk with zoomed biomes", t);
        }
    }

    private void transferFilledBiomes(MapTileChunk tileChunk, class_4543 biomeZoomer, class_2378<class_1959> biomeRegistry) {
        try {
            for (int insideX = 0; insideX < 4; ++insideX) {
                for (int insideZ = 0; insideZ < 4; ++insideZ) {
                    MapTile mapTile = tileChunk.getTile(insideX, insideZ);
                    if (mapTile == null || !mapTile.isLoaded()) continue;
                    for (int x = 0; x < 16; ++x) {
                        for (int z = 0; z < 16; ++z) {
                            MapBlock mapBlock = mapTile.getBlock(x, z);
                            tileChunk.getLeafTexture().setBiome(insideX << 4 | x, insideZ << 4 | z, mapBlock.getBiome());
                        }
                    }
                }
            }
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("Error transferring filled tile chunk with zoomed biomes", t);
        }
    }

    private boolean shouldOverlayCached(class_2688<?, ?> state) {
        return this.transparentCache.apply(state);
    }

    private byte nibbleValue(byte[] array, int index) {
        byte b = array[index >> 1];
        if ((index & 1) == 0) {
            return (byte)(b & 0xF);
        }
        return (byte)(b >> 4 & 0xF);
    }
}

