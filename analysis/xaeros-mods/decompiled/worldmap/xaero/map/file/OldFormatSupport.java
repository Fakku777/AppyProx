/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2487
 *  net.minecraft.class_2507
 *  net.minecraft.class_2519
 *  net.minecraft.class_2520
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_7225
 *  net.minecraft.class_7923
 */
package xaero.map.file;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_7225;
import net.minecraft.class_7923;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;

public class OldFormatSupport {
    private class_2960 vanillaStatesResource;
    private boolean vanillaStatesLoaded;
    private HashMap<Integer, HashMap<Integer, class_2680>> vanilla_states;
    private ImmutableMap<String, String> blockRename1314;
    private ImmutableMap<String, Fixer> blockFixes1516;
    private ImmutableMap<String, Fixer> blockFixes1617;
    private ImmutableMap<String, Fixer> blockFixes1_21_5;
    private Map<String, String> JIGSAW_ORIENTATION_UPDATES16 = ImmutableMap.builder().put((Object)"", (Object)"north_up").put((Object)"down", (Object)"down_south").put((Object)"up", (Object)"up_north").put((Object)"north", (Object)"north_up").put((Object)"south", (Object)"south_up").put((Object)"west", (Object)"west_up").put((Object)"east", (Object)"east_up").build();
    private Fixer wallFix = nbt -> {
        class_2487 properties;
        properties.method_10582("east", (properties = nbt.method_68568("Properties")).method_10558("east").equals("true") ? "low" : "none");
        properties.method_10582("west", properties.method_10558("west").equals("true") ? "low" : "none");
        properties.method_10582("north", properties.method_10558("north").equals("true") ? "low" : "none");
        properties.method_10582("south", properties.method_10558("south").equals("true") ? "low" : "none");
    };
    public ImmutableMap<String, String> cc2BiomeRenames = ImmutableMap.builder().put((Object)"minecraft:badlands_plateau", (Object)"minecraft:badlands").put((Object)"minecraft:bamboo_jungle_hills", (Object)"minecraft:bamboo_jungle").put((Object)"minecraft:birch_forest_hills", (Object)"minecraft:birch_forest").put((Object)"minecraft:dark_forest_hills", (Object)"minecraft:dark_forest").put((Object)"minecraft:desert_hills", (Object)"minecraft:desert").put((Object)"minecraft:desert_lakes", (Object)"minecraft:desert").put((Object)"minecraft:giant_spruce_taiga_hills", (Object)"minecraft:old_growth_spruce_taiga").put((Object)"minecraft:giant_spruce_taiga", (Object)"minecraft:old_growth_spruce_taiga").put((Object)"minecraft:giant_tree_taiga_hills", (Object)"minecraft:old_growth_pine_taiga").put((Object)"minecraft:giant_tree_taiga", (Object)"minecraft:old_growth_pine_taiga").put((Object)"minecraft:gravelly_mountains", (Object)"minecraft:windswept_gravelly_hills").put((Object)"minecraft:jungle_edge", (Object)"minecraft:sparse_jungle").put((Object)"minecraft:jungle_hills", (Object)"minecraft:jungle").put((Object)"minecraft:modified_badlands_plateau", (Object)"minecraft:badlands").put((Object)"minecraft:modified_gravelly_mountains", (Object)"minecraft:windswept_gravelly_hills").put((Object)"minecraft:modified_jungle_edge", (Object)"minecraft:sparse_jungle").put((Object)"minecraft:modified_jungle", (Object)"minecraft:jungle").put((Object)"minecraft:modified_wooded_badlands_plateau", (Object)"minecraft:wooded_badlands").put((Object)"minecraft:mountain_edge", (Object)"minecraft:windswept_hills").put((Object)"minecraft:mountains", (Object)"minecraft:windswept_hills").put((Object)"minecraft:mushroom_field_shore", (Object)"minecraft:mushroom_fields").put((Object)"minecraft:shattered_savanna", (Object)"minecraft:windswept_savanna").put((Object)"minecraft:shattered_savanna_plateau", (Object)"minecraft:windswept_savanna").put((Object)"minecraft:snowy_mountains", (Object)"minecraft:snowy_plains").put((Object)"minecraft:snowy_taiga_hills", (Object)"minecraft:snowy_taiga").put((Object)"minecraft:snowy_taiga_mountains", (Object)"minecraft:snowy_taiga").put((Object)"minecraft:snowy_tundra", (Object)"minecraft:snowy_plains").put((Object)"minecraft:stone_shore", (Object)"minecraft:stony_shore").put((Object)"minecraft:swamp_hills", (Object)"minecraft:swamp").put((Object)"minecraft:taiga_hills", (Object)"minecraft:taiga").put((Object)"minecraft:taiga_mountains", (Object)"minecraft:taiga").put((Object)"minecraft:tall_birch_forest", (Object)"minecraft:old_growth_birch_forest").put((Object)"minecraft:tall_birch_hills", (Object)"minecraft:old_growth_birch_forest").put((Object)"minecraft:wooded_badlands_plateau", (Object)"minecraft:wooded_badlands").put((Object)"minecraft:wooded_hills", (Object)"minecraft:forest").put((Object)"minecraft:wooded_mountains", (Object)"minecraft:windswept_forest").put((Object)"minecraft:lofty_peaks", (Object)"minecraft:jagged_peaks").put((Object)"minecraft:snowcapped_peaks", (Object)"minecraft:frozen_peaks").build();
    private Int2ObjectMap<String> biomesById = new Int2ObjectOpenHashMap();

    public OldFormatSupport() {
        this.vanillaStatesResource = class_2960.method_60655((String)"xaeroworldmap", (String)"vanilla_states.dat");
        this.vanilla_states = new HashMap();
        this.blockRename1314 = ImmutableMap.of((Object)"minecraft:stone_slab", (Object)"minecraft:smooth_stone_slab", (Object)"minecraft:sign", (Object)"minecraft:oak_sign", (Object)"minecraft:wall_sign", (Object)"minecraft:oak_wall_sign");
        this.blockFixes1516 = ImmutableMap.builder().put((Object)"minecraft:jigsaw", nbt -> {
            class_2487 properties = nbt.method_68568("Properties");
            String facing = properties.method_10558("facing").orElse(null);
            properties.method_10551("facing");
            properties.method_10582("orientation", this.JIGSAW_ORIENTATION_UPDATES16.get(facing));
        }).put((Object)"minecraft:redstone_wire", nbt -> {
            class_2487 properties = nbt.method_68568("Properties");
            String east = properties.method_10558("east").orElse(null);
            String west = properties.method_10558("west").orElse(null);
            String north = properties.method_10558("north").orElse(null);
            String south = properties.method_10558("south").orElse(null);
            if (east.equals("")) {
                east = "none";
            }
            if (west.equals("")) {
                west = "none";
            }
            if (north.equals("")) {
                north = "none";
            }
            if (south.equals("")) {
                south = "none";
            }
            boolean hasEast = !east.equals("none");
            boolean hasWest = !west.equals("none");
            boolean hasNorth = !north.equals("none");
            boolean hasSouth = !south.equals("none");
            boolean hasHorizontal = hasWest || hasEast;
            boolean hasVertical = hasNorth || hasSouth;
            east = !hasEast && !hasVertical ? "side" : east;
            west = !hasWest && !hasVertical ? "side" : west;
            north = !hasNorth && !hasHorizontal ? "side" : north;
            south = !hasSouth && !hasHorizontal ? "side" : south;
            properties.method_10582("east", east);
            properties.method_10582("west", west);
            properties.method_10582("north", north);
            properties.method_10582("south", south);
        }).put((Object)"minecraft:andesite_wall", (Object)this.wallFix).put((Object)"minecraft:brick_wall", (Object)this.wallFix).put((Object)"minecraft:cobblestone_wall", (Object)this.wallFix).put((Object)"minecraft:diorite_wall", (Object)this.wallFix).put((Object)"minecraft:end_stone_brick_wall", (Object)this.wallFix).put((Object)"minecraft:granite_wall", (Object)this.wallFix).put((Object)"minecraft:mossy_cobblestone_wall", (Object)this.wallFix).put((Object)"minecraft:mossy_stone_brick_wall", (Object)this.wallFix).put((Object)"minecraft:nether_brick_wall", (Object)this.wallFix).put((Object)"minecraft:prismarine_wall", (Object)this.wallFix).put((Object)"minecraft:red_nether_brick_wall", (Object)this.wallFix).put((Object)"minecraft:red_sandstone_wall", (Object)this.wallFix).put((Object)"minecraft:sandstone_wall", (Object)this.wallFix).put((Object)"minecraft:stone_brick_wall", (Object)this.wallFix).build();
        this.blockFixes1617 = ImmutableMap.builder().put((Object)"minecraft:cauldron", nbt -> {
            class_2487 properties = nbt.method_68568("Properties");
            if (!properties.method_33133()) {
                class_2520 level = properties.method_10580("level");
                if (level == null || level.toString().equals("0") || level.toString().equals("0b")) {
                    nbt.method_10551("Properties");
                } else {
                    nbt.method_10566("Name", (class_2520)class_2519.method_23256((String)"minecraft:water_cauldron"));
                }
            }
        }).put((Object)"minecraft:grass_path", nbt -> nbt.method_10566("Name", (class_2520)class_2519.method_23256((String)"minecraft:dirt_path"))).build();
        this.blockFixes1_21_5 = ImmutableMap.builder().put((Object)"minecraft:creaking_heart", nbt -> {
            class_2487 properties = nbt.method_68568("Properties");
            if (properties.method_33133()) {
                return;
            }
            String active = properties.method_10558("active").orElse(null);
            if (active == null) {
                return;
            }
            properties.method_10551("active");
            properties.method_10582("creaking_heart_state", active.equals("true") ? "awake" : "uprooted");
        }).build();
        this.biomesById.put(0, (Object)"minecraft:ocean");
        this.biomesById.put(1, (Object)"minecraft:plains");
        this.biomesById.put(2, (Object)"minecraft:desert");
        this.biomesById.put(3, (Object)"minecraft:mountains");
        this.biomesById.put(4, (Object)"minecraft:forest");
        this.biomesById.put(5, (Object)"minecraft:taiga");
        this.biomesById.put(6, (Object)"minecraft:swamp");
        this.biomesById.put(7, (Object)"minecraft:river");
        this.biomesById.put(8, (Object)"minecraft:nether_wastes");
        this.biomesById.put(9, (Object)"minecraft:the_end");
        this.biomesById.put(10, (Object)"minecraft:frozen_ocean");
        this.biomesById.put(11, (Object)"minecraft:frozen_river");
        this.biomesById.put(12, (Object)"minecraft:snowy_tundra");
        this.biomesById.put(13, (Object)"minecraft:snowy_mountains");
        this.biomesById.put(14, (Object)"minecraft:mushroom_fields");
        this.biomesById.put(15, (Object)"minecraft:mushroom_field_shore");
        this.biomesById.put(16, (Object)"minecraft:beach");
        this.biomesById.put(17, (Object)"minecraft:desert_hills");
        this.biomesById.put(18, (Object)"minecraft:wooded_hills");
        this.biomesById.put(19, (Object)"minecraft:taiga_hills");
        this.biomesById.put(20, (Object)"minecraft:mountain_edge");
        this.biomesById.put(21, (Object)"minecraft:jungle");
        this.biomesById.put(22, (Object)"minecraft:jungle_hills");
        this.biomesById.put(23, (Object)"minecraft:jungle_edge");
        this.biomesById.put(24, (Object)"minecraft:deep_ocean");
        this.biomesById.put(25, (Object)"minecraft:stone_shore");
        this.biomesById.put(26, (Object)"minecraft:snowy_beach");
        this.biomesById.put(27, (Object)"minecraft:birch_forest");
        this.biomesById.put(28, (Object)"minecraft:birch_forest_hills");
        this.biomesById.put(29, (Object)"minecraft:dark_forest");
        this.biomesById.put(30, (Object)"minecraft:snowy_taiga");
        this.biomesById.put(31, (Object)"minecraft:snowy_taiga_hills");
        this.biomesById.put(32, (Object)"minecraft:giant_tree_taiga");
        this.biomesById.put(33, (Object)"minecraft:giant_tree_taiga_hills");
        this.biomesById.put(34, (Object)"minecraft:wooded_mountains");
        this.biomesById.put(35, (Object)"minecraft:savanna");
        this.biomesById.put(36, (Object)"minecraft:savanna_plateau");
        this.biomesById.put(37, (Object)"minecraft:badlands");
        this.biomesById.put(38, (Object)"minecraft:wooded_badlands_plateau");
        this.biomesById.put(39, (Object)"minecraft:badlands_plateau");
        this.biomesById.put(40, (Object)"minecraft:small_end_islands");
        this.biomesById.put(41, (Object)"minecraft:end_midlands");
        this.biomesById.put(42, (Object)"minecraft:end_highlands");
        this.biomesById.put(43, (Object)"minecraft:end_barrens");
        this.biomesById.put(44, (Object)"minecraft:warm_ocean");
        this.biomesById.put(45, (Object)"minecraft:lukewarm_ocean");
        this.biomesById.put(46, (Object)"minecraft:cold_ocean");
        this.biomesById.put(47, (Object)"minecraft:deep_warm_ocean");
        this.biomesById.put(48, (Object)"minecraft:deep_lukewarm_ocean");
        this.biomesById.put(49, (Object)"minecraft:deep_cold_ocean");
        this.biomesById.put(50, (Object)"minecraft:deep_frozen_ocean");
        this.biomesById.put(127, (Object)"minecraft:the_void");
        this.biomesById.put(129, (Object)"minecraft:sunflower_plains");
        this.biomesById.put(130, (Object)"minecraft:desert_lakes");
        this.biomesById.put(131, (Object)"minecraft:gravelly_mountains");
        this.biomesById.put(132, (Object)"minecraft:flower_forest");
        this.biomesById.put(133, (Object)"minecraft:taiga_mountains");
        this.biomesById.put(134, (Object)"minecraft:swamp_hills");
        this.biomesById.put(140, (Object)"minecraft:ice_spikes");
        this.biomesById.put(149, (Object)"minecraft:modified_jungle");
        this.biomesById.put(151, (Object)"minecraft:modified_jungle_edge");
        this.biomesById.put(155, (Object)"minecraft:tall_birch_forest");
        this.biomesById.put(156, (Object)"minecraft:tall_birch_hills");
        this.biomesById.put(157, (Object)"minecraft:dark_forest_hills");
        this.biomesById.put(158, (Object)"minecraft:snowy_taiga_mountains");
        this.biomesById.put(160, (Object)"minecraft:giant_spruce_taiga");
        this.biomesById.put(161, (Object)"minecraft:giant_spruce_taiga_hills");
        this.biomesById.put(162, (Object)"minecraft:modified_gravelly_mountains");
        this.biomesById.put(163, (Object)"minecraft:shattered_savanna");
        this.biomesById.put(164, (Object)"minecraft:shattered_savanna_plateau");
        this.biomesById.put(165, (Object)"minecraft:eroded_badlands");
        this.biomesById.put(166, (Object)"minecraft:modified_wooded_badlands_plateau");
        this.biomesById.put(167, (Object)"minecraft:modified_badlands_plateau");
        this.biomesById.put(168, (Object)"minecraft:bamboo_jungle");
        this.biomesById.put(169, (Object)"minecraft:bamboo_jungle_hills");
        this.biomesById.put(170, (Object)"minecraft:soul_sand_valley");
        this.biomesById.put(171, (Object)"minecraft:crimson_forest");
        this.biomesById.put(172, (Object)"minecraft:warped_forest");
        this.biomesById.put(173, (Object)"minecraft:basalt_deltas");
        this.biomesById.put(174, (Object)"minecraft:dripstone_caves");
        this.biomesById.put(175, (Object)"minecraft:lush_caves");
        this.biomesById.put(177, (Object)"minecraft:meadow");
        this.biomesById.put(178, (Object)"minecraft:grove");
        this.biomesById.put(179, (Object)"minecraft:snowy_slopes");
        this.biomesById.put(180, (Object)"minecraft:snowcapped_peaks");
        this.biomesById.put(181, (Object)"minecraft:lofty_peaks");
        this.biomesById.put(182, (Object)"minecraft:stony_peaks");
    }

    public void loadVanillaStates() throws IOException, CommandSyntaxException {
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info("Loading vanilla states...");
        }
        this.loadStates(this.vanilla_states, ((class_3298)class_310.method_1551().method_1478().method_14486(this.vanillaStatesResource).get()).method_14482());
        this.vanillaStatesLoaded = true;
    }

    public void loadModdedStates(MapProcessor mapProcessor, String worldId, String dimId, String mwId) throws FileNotFoundException, IOException, CommandSyntaxException {
        if (worldId == null) {
            return;
        }
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info("Loading modded states for the world...");
        }
        Path mainFolder = mapProcessor.getMapSaveLoad().getMainFolder(worldId, dimId);
        Path subFolder = mapProcessor.getMapSaveLoad().getMWSubFolder(worldId, mainFolder, mwId);
        Path inputPath = subFolder.resolve("states.dat");
        if (!Files.exists(subFolder, new LinkOption[0])) {
            Files.createDirectories(subFolder, new FileAttribute[0]);
        }
    }

    private void loadStates(HashMap<Integer, HashMap<Integer, class_2680>> stateMap, InputStream inputStream) throws IOException, CommandSyntaxException {
        DataInputStream input = new DataInputStream(new BufferedInputStream(inputStream));
        try {
            while (true) {
                int stateId = input.readInt();
                int blockId = stateId & 0xFFF;
                int meta = stateId >> 12 & 0xFFFFF;
                class_2487 nbtCompound = class_2507.method_10627((DataInput)input);
                this.fixBlock(nbtCompound, 1);
                class_2680 blockState = WorldMap.unknownBlockStateCache.getBlockStateFromNBT((class_7225<class_2248>)class_7923.field_41175, nbtCompound);
                this.putState(stateMap, blockId, meta, blockState);
            }
        }
        catch (EOFException eOFException) {
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Done.");
            }
            input.close();
            return;
        }
    }

    public void loadStates() throws IOException, CommandSyntaxException {
        if (!this.vanillaStatesLoaded) {
            this.loadVanillaStates();
        }
    }

    private void putState(HashMap<Integer, HashMap<Integer, class_2680>> stateMap, int blockId, int meta, class_2680 blockState) {
        HashMap<Integer, Object> blockStates = stateMap.get(blockId);
        if (blockStates == null) {
            blockStates = new HashMap();
            stateMap.put(blockId, blockStates);
        }
        blockStates.put(meta, blockState);
    }

    private class_2680 getStateForId(int stateId, HashMap<Integer, HashMap<Integer, class_2680>> stateMap) {
        int blockId = stateId & 0xFFF;
        HashMap<Integer, class_2680> blockStates = stateMap.get(blockId);
        if (blockStates == null) {
            return null;
        }
        int meta = stateId >> 12 & 0xFFFFF;
        return blockStates.getOrDefault(meta, null);
    }

    public class_2680 getStateForId(int stateId) {
        class_2680 vanillaState = this.getStateForId(stateId, this.vanilla_states);
        if (vanillaState != null) {
            return vanillaState;
        }
        return class_2246.field_10124.method_9564();
    }

    public void fixBlock(class_2487 nbt, int version) {
        if (version == 1) {
            this.fixBlockName1314(nbt);
        }
        if (version < 3) {
            this.fixBlock1516(nbt);
        }
        if (version < 5) {
            this.fixBlock1617(nbt);
        }
        if (version < 7) {
            this.fixBlock1215(nbt);
        }
    }

    private void fixBlockName1314(class_2487 nbt) {
        String name = nbt.method_10558("Name").orElse(null);
        nbt.method_10582("Name", (String)this.blockRename1314.getOrDefault((Object)name, (Object)name));
    }

    private void fixBlock1516(class_2487 nbt) {
        String name = nbt.method_10558("Name").orElse(null);
        Fixer fixer = (Fixer)this.blockFixes1516.get((Object)name);
        if (fixer != null) {
            fixer.fix(nbt);
        }
    }

    private void fixBlock1617(class_2487 nbt) {
        String name = nbt.method_10558("Name").orElse(null);
        Fixer fixer = (Fixer)this.blockFixes1617.get((Object)name);
        if (fixer != null) {
            fixer.fix(nbt);
        }
    }

    private void fixBlock1215(class_2487 nbt) {
        String name = nbt.method_10558("Name").orElse(null);
        Fixer fixer = (Fixer)this.blockFixes1_21_5.get((Object)name);
        if (fixer != null) {
            fixer.fix(nbt);
        }
    }

    public String fixBiome(int id, int version) {
        return this.fixBiome(id, version, "minecraft:plains");
    }

    public String fixBiome(int id, int version, String defaultValue) {
        String biomeStringId = (String)this.biomesById.get(id);
        if (biomeStringId == null) {
            biomeStringId = defaultValue;
        }
        if (biomeStringId == null) {
            return null;
        }
        return this.fixBiome(biomeStringId, version);
    }

    public String fixBiome(String id, int version) {
        if (version < 6) {
            return this.fixBiome1718(id);
        }
        return id;
    }

    private String fixBiome1718(String id) {
        if ("minecraft:deep_warm_ocean".equals(id)) {
            return "minecraft:warm_ocean";
        }
        return (String)this.cc2BiomeRenames.getOrDefault((Object)id, (Object)id);
    }

    public static interface Fixer {
        public void fix(class_2487 var1);
    }
}

