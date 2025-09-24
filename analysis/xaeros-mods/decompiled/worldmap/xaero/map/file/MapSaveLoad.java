/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_156
 *  net.minecraft.class_1959
 *  net.minecraft.class_1972
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2378
 *  net.minecraft.class_2487
 *  net.minecraft.class_2507
 *  net.minecraft.class_2512
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3611
 *  net.minecraft.class_437
 *  net.minecraft.class_5321
 *  net.minecraft.class_7225
 *  net.minecraft.class_7924
 */
package xaero.map.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.minecraft.class_156;
import net.minecraft.class_1959;
import net.minecraft.class_1972;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_2512;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3611;
import net.minecraft.class_437;
import net.minecraft.class_5321;
import net.minecraft.class_7225;
import net.minecraft.class_7924;
import xaero.map.MapProcessor;
import xaero.map.WorldMap;
import xaero.map.biome.BiomeGetter;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.file.MapRegionInfo;
import xaero.map.file.OldFormatSupport;
import xaero.map.file.RegionDetection;
import xaero.map.file.export.PNGExportResult;
import xaero.map.file.export.PNGExporter;
import xaero.map.file.worldsave.WorldDataHandler;
import xaero.map.gui.ExportScreen;
import xaero.map.gui.MapTileSelection;
import xaero.map.misc.Misc;
import xaero.map.region.BranchLeveledRegion;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapBlock;
import xaero.map.region.MapLayer;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTile;
import xaero.map.region.MapTileChunk;
import xaero.map.region.Overlay;
import xaero.map.region.OverlayBuilder;
import xaero.map.region.OverlayManager;
import xaero.map.region.state.UnknownBlockState;
import xaero.map.task.MapRunnerTask;
import xaero.map.world.MapDimension;

public class MapSaveLoad {
    private static final int currentSaveMajorVersion = 7;
    private static final int currentSaveMinorVersion = 8;
    public static final int SAVE_TIME = 60000;
    public static final int currentCacheSaveMajorVersion = 2;
    public static final int currentCacheSaveMinorVersion = 24;
    private ArrayList<MapRegion> toSave = new ArrayList();
    private ArrayList<MapRegion> toLoad = new ArrayList();
    private ArrayList<BranchLeveledRegion> toLoadBranchCache = new ArrayList();
    private ArrayList<File> cacheToConvertFromTemp = new ArrayList();
    private LeveledRegion<?> nextToLoadByViewing;
    private boolean regionDetectionComplete;
    private Path lastRealmOwnerPath;
    public boolean loadingFiles;
    private OverlayBuilder overlayBuilder;
    private PNGExporter pngExporter;
    private OldFormatSupport oldFormatSupport;
    private HashMap<class_2680, Integer> regionSavePalette;
    private ArrayList<class_2680> regionLoadPalette;
    private HashMap<class_5321<class_1959>, Integer> regionSaveBiomePalette;
    private ArrayList<class_5321<class_1959>> regionLoadBiomePalette;
    private List<MapDimension> workingDimList;
    public boolean saveAll;
    private MapProcessor mapProcessor;
    public int mainTextureLevel;
    private BlockStateShortShapeCache blockStateShortShapeCache;
    private boolean exporting;

    public MapSaveLoad(OverlayManager overlayManager, PNGExporter pngExporter, OldFormatSupport oldFormatSupport, BlockStateShortShapeCache blockStateShortShapeCache) {
        this.overlayBuilder = new OverlayBuilder(overlayManager);
        this.pngExporter = pngExporter;
        this.oldFormatSupport = oldFormatSupport;
        this.regionSavePalette = new HashMap();
        this.regionLoadPalette = new ArrayList();
        this.regionSaveBiomePalette = new HashMap();
        this.regionLoadBiomePalette = new ArrayList();
        this.workingDimList = new ArrayList<MapDimension>();
        this.blockStateShortShapeCache = blockStateShortShapeCache;
    }

    public void setMapProcessor(MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }

    public boolean exportPNG(final ExportScreen destScreen, final MapTileSelection selection) {
        if (this.exporting) {
            return false;
        }
        this.exporting = true;
        WorldMap.mapRunner.addTask(new MapRunnerTask(){

            @Override
            public void run(MapProcessor mapProcessor) {
                class_310.method_1551().method_20493(() -> {
                    try {
                        PNGExportResult result = MapSaveLoad.this.pngExporter.export(mapProcessor, mapProcessor.worldBiomeRegistry, mapProcessor.getWorldDimensionTypeRegistry(), selection, MapSaveLoad.this.oldFormatSupport);
                        WorldMap.LOGGER.info(result.getMessage().getString());
                        if (destScreen != null) {
                            destScreen.onExportDone(result);
                        }
                        if (result.getFolderToOpen() != null && Files.exists(result.getFolderToOpen(), new LinkOption[0])) {
                            class_156.method_668().method_672(result.getFolderToOpen().toFile());
                        }
                    }
                    catch (Throwable e) {
                        WorldMap.LOGGER.error("Failed to export PNG with exception!", e);
                        WorldMap.crashHandler.setCrashedBy(e);
                    }
                    MapSaveLoad.this.exporting = false;
                    class_310.method_1551().method_1507((class_437)destScreen);
                });
                while (MapSaveLoad.this.exporting) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
        });
        return true;
    }

    private File getSecondaryFile(String extension, File realFile) {
        if (realFile == null) {
            return null;
        }
        String p = realFile.getPath();
        if (p.endsWith(".outdated")) {
            p = p.substring(0, p.length() - ".outdated".length());
        }
        return new File(p.substring(0, p.lastIndexOf(".")) + extension);
    }

    public File getTempFile(File realFile) {
        return this.getSecondaryFile(".zip.temp", realFile);
    }

    private Path getCacheFolder(Path subFolder) {
        if (subFolder != null) {
            return subFolder.resolve("cache_" + this.mapProcessor.getGlobalVersion());
        }
        return null;
    }

    public File getCacheFile(MapRegionInfo region, int caveLayer, boolean checkOutdated, boolean requestCache) throws IOException {
        Path outdatedCacheFile;
        Path subFolder = this.getMWSubFolder(region.getWorldId(), region.getDimId(), region.getMwId());
        Path layerFolder = this.getCaveLayerFolder(caveLayer, subFolder);
        Path latestCacheFolder = this.getCacheFolder(layerFolder);
        if (latestCacheFolder == null) {
            return null;
        }
        if (!Files.exists(latestCacheFolder, new LinkOption[0])) {
            Files.createDirectories(latestCacheFolder, new FileAttribute[0]);
        }
        Path cacheFile = latestCacheFolder.resolve(region.getRegionX() + "_" + region.getRegionZ() + ".xwmc");
        if (!checkOutdated || Files.exists(cacheFile, new LinkOption[0])) {
            return cacheFile.toFile();
        }
        if (requestCache) {
            region.setShouldCache(true, "cache file");
        }
        if (Files.exists(outdatedCacheFile = cacheFile.resolveSibling(cacheFile.getFileName().toString() + ".outdated"), new LinkOption[0])) {
            return outdatedCacheFile.toFile();
        }
        return cacheFile.toFile();
    }

    public File getFile(MapRegion region) {
        if (region.getWorldId() == null) {
            return null;
        }
        File detectedFile = region.getRegionFile();
        boolean normalMapData = region.isNormalMapData();
        if (!normalMapData) {
            if (detectedFile != null) {
                return detectedFile;
            }
            return this.mapProcessor.getWorldDataHandler().getWorldDir().resolve("region").resolve("r." + region.getRegionX() + "." + region.getRegionZ() + ".mca").toFile();
        }
        return this.getNormalFile(region);
    }

    public File getNormalFile(MapRegion region) {
        Path subFolder;
        if (region.getWorldId() == null) {
            return null;
        }
        File detectedFile = region.isNormalMapData() ? region.getRegionFile() : null;
        boolean realms = MapProcessor.isWorldRealms(region.getWorldId());
        String mwId = region.isNormalMapData() ? region.getMwId() : "cm$converted";
        Path mainFolder = this.getMainFolder(region.getWorldId(), region.getDimId());
        Path layerFolder = subFolder = this.getMWSubFolder(region.getWorldId(), mainFolder, mwId);
        if (region.getCaveLayer() != Integer.MAX_VALUE) {
            layerFolder = layerFolder.resolve("caves").resolve("" + region.getCaveLayer());
        }
        try {
            File subFolderFile = layerFolder.toFile();
            if (!subFolderFile.exists()) {
                Path ownerPath;
                Files.createDirectories(subFolderFile.toPath(), new FileAttribute[0]);
                if (realms && WorldMap.events.getLatestRealm() != null && !(ownerPath = mainFolder.resolve(WorldMap.events.getLatestRealm().field_22604 + ".owner")).equals(this.lastRealmOwnerPath)) {
                    if (!Files.exists(ownerPath, new LinkOption[0])) {
                        Files.createFile(ownerPath, new FileAttribute[0]);
                    }
                    this.lastRealmOwnerPath = ownerPath;
                }
            }
        }
        catch (IOException e1) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e1);
        }
        if (detectedFile != null && detectedFile.getName().endsWith(".xaero")) {
            File zipFile = layerFolder.resolve(region.getRegionX() + "_" + region.getRegionZ() + ".zip").toFile();
            if (detectedFile.exists() && !zipFile.exists()) {
                this.xaeroToZip(detectedFile);
            }
            region.setRegionFile(zipFile);
            return zipFile;
        }
        return detectedFile == null ? layerFolder.resolve(region.getRegionX() + "_" + region.getRegionZ() + ".zip").toFile() : detectedFile;
    }

    public static Path getRootFolder(String world) {
        if (world == null) {
            return null;
        }
        return WorldMap.saveFolder.toPath().resolve(world);
    }

    public Path getMainFolder(String world, String dim) {
        if (world == null) {
            return null;
        }
        return WorldMap.saveFolder.toPath().resolve(world).resolve(dim);
    }

    Path getMWSubFolder(String world, Path mainFolder, String mw) {
        if (world == null) {
            return null;
        }
        if (mw == null) {
            return mainFolder;
        }
        return mainFolder.resolve(mw);
    }

    public Path getCaveLayerFolder(int caveLayer, Path subFolder) {
        Path layerFolder = subFolder;
        if (caveLayer != Integer.MAX_VALUE) {
            layerFolder = subFolder.resolve("caves").resolve("" + caveLayer);
        }
        return layerFolder;
    }

    public Path getMWSubFolder(String world, String dim, String mw) {
        if (world == null) {
            return null;
        }
        return this.getMWSubFolder(world, this.getMainFolder(world, dim), mw);
    }

    public Path getOldFolder(String oldUnfixedMainId, String dim) {
        if (oldUnfixedMainId == null) {
            return null;
        }
        return WorldMap.saveFolder.toPath().resolve(oldUnfixedMainId + "_" + dim);
    }

    private void xaeroToZip(File xaero) {
        File zipFile = xaero.toPath().getParent().resolve(xaero.getName().substring(0, xaero.getName().lastIndexOf(46)) + ".zip").toFile();
        try {
            int got;
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(xaero), 1024);
            ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            ZipEntry e = new ZipEntry("region.xaero");
            zipOutput.putNextEntry(e);
            byte[] bytes = new byte[1024];
            while ((got = in.read(bytes)) > 0) {
                zipOutput.write(bytes, 0, got);
            }
            zipOutput.closeEntry();
            zipOutput.flush();
            zipOutput.close();
            in.close();
            Files.deleteIfExists(xaero.toPath());
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            return;
        }
    }

    public void detectRegions(int attempts) {
        MapDimension mapDimension = this.mapProcessor.getMapWorld().getCurrentDimension();
        mapDimension.preDetection();
        String worldId = this.mapProcessor.getCurrentWorldId();
        if (worldId == null || this.mapProcessor.isCurrentMapLocked()) {
            return;
        }
        String dimId = this.mapProcessor.getCurrentDimId();
        String mwId = this.mapProcessor.getCurrentMWId();
        boolean usingNormalMapData = !mapDimension.isUsingWorldSave();
        Path mapFolder = this.getMWSubFolder(worldId, dimId, mwId);
        boolean mapFolderExists = mapFolder.toFile().exists();
        String multiplayerMapRegex = "^(-?\\d+)_(-?\\d+)\\.(zip|xaero)$";
        MapLayer mainLayer = mapDimension.getLayeredMapRegions().getLayer(Integer.MAX_VALUE);
        if (usingNormalMapData) {
            if (mapFolderExists) {
                this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, mapFolder, multiplayerMapRegex, 1, 2, 0, 20, mainLayer::addRegionDetection);
            }
        } else {
            Path worldDir = this.mapProcessor.getWorldDataHandler().getWorldDir();
            if (worldDir == null) {
                return;
            }
            Path worldFolder = worldDir.resolve("region");
            if (!worldFolder.toFile().exists()) {
                return;
            }
            this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, worldFolder, "^r\\.(-{0,1}[0-9]+)\\.(-{0,1}[0-9]+)\\.mc[ar]$", 1, 2, 8192, 20, mapDimension::addWorldSaveRegionDetection);
        }
        if (mapFolderExists) {
            Path cavesFolder = mapFolder.resolve("caves");
            try {
                if (!Files.exists(cavesFolder, new LinkOption[0])) {
                    Files.createDirectories(cavesFolder, new FileAttribute[0]);
                }
                try (Stream<Path> cavesFolderStream = Files.list(cavesFolder);){
                    cavesFolderStream.forEach(layerFolder -> {
                        if (!Files.isDirectory(layerFolder, new LinkOption[0])) {
                            return;
                        }
                        String folderName = layerFolder.getFileName().toString();
                        try {
                            int layerInt = Integer.parseInt(folderName);
                            MapLayer layer = mapDimension.getLayeredMapRegions().getLayer(layerInt);
                            if (usingNormalMapData) {
                                this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, (Path)layerFolder, multiplayerMapRegex, 1, 2, 0, 20, layer::addRegionDetection);
                            }
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                    });
                }
            }
            catch (IOException e) {
                WorldMap.LOGGER.error("IOException trying to detect map layers!");
                if (attempts > 1) {
                    WorldMap.LOGGER.error("Retrying... " + --attempts);
                    try {
                        Thread.sleep(30L);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    this.detectRegions(attempts);
                    return;
                }
                throw new RuntimeException("Couldn't detect map layers after multiple attempts.", e);
            }
        }
    }

    public void detectRegionsFromFiles(MapDimension mapDimension, String worldId, String dimId, String mwId, Path folder, String regex, int xIndex, int zIndex, int emptySize, int attempts, Consumer<RegionDetection> detectionConsumer) {
        int total = 0;
        Pattern fileRegexPattern = Pattern.compile(regex);
        long before = System.currentTimeMillis();
        try {
            Stream<Path> files = Files.list(folder);
            Iterator iter = files.iterator();
            while (!this.mapProcessor.isFinalizing() && iter.hasNext()) {
                Path file = (Path)iter.next();
                String regionName = file.getFileName().toString();
                Matcher matcher = fileRegexPattern.matcher(regionName);
                if (!matcher.matches()) continue;
                int x = Integer.parseInt(matcher.group(xIndex));
                int z = Integer.parseInt(matcher.group(zIndex));
                RegionDetection regionDetection = new RegionDetection(worldId, dimId, mwId, x, z, file.toFile(), this.mapProcessor.getGlobalVersion(), true);
                detectionConsumer.accept(regionDetection);
                ++total;
            }
            files.close();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("IOException trying to detect map files!");
            if (attempts > 1) {
                WorldMap.LOGGER.error("Retrying... " + --attempts);
                try {
                    Thread.sleep(30L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                this.detectRegionsFromFiles(mapDimension, worldId, dimId, mwId, folder, regex, xIndex, zIndex, emptySize, attempts, detectionConsumer);
                return;
            }
            throw new RuntimeException("Couldn't detect map files after multiple attempts.", e);
        }
        if (WorldMap.settings.debug) {
            WorldMap.LOGGER.info(String.format("%d regions detected in %d ms!", total, System.currentTimeMillis() - before));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean saveRegion(MapRegion region, int extraAttempts) {
        try {
            if (!region.hasHadTerrain()) {
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("Save not required for highlight-only region: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId());
                }
                return region.countChunks() > 0;
            }
            if (!region.isResaving() && !region.isNormalMapData()) {
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("Save not required for world save map: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId());
                }
                return region.countChunks() > 0;
            }
            File permFile = this.getNormalFile(region);
            if (!permFile.toPath().startsWith(WorldMap.saveFolder.toPath())) {
                throw new IllegalArgumentException();
            }
            File file = this.getTempFile(permFile);
            if (file == null) {
                return true;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            boolean hasAnything = false;
            boolean regionWasSavedEmpty = true;
            try (FilterOutputStream out = null;){
                ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                out = new DataOutputStream(zipOut);
                ZipEntry e = new ZipEntry("region.xaero");
                zipOut.putNextEntry(e);
                int fullVersion = 458760;
                ((DataOutputStream)out).write(255);
                ((DataOutputStream)out).writeInt(fullVersion);
                this.regionSavePalette.clear();
                this.regionSaveBiomePalette.clear();
                class_2378<class_1959> biomeRegistry = region.getBiomeRegistry();
                for (int o = 0; o < 8; ++o) {
                    for (int p = 0; p < 8; ++p) {
                        BranchLeveledRegion parentRegion;
                        MapTileChunk chunk = region.getChunk(o, p);
                        if (chunk == null) continue;
                        hasAnything = true;
                        if (chunk.includeInSave()) {
                            ((DataOutputStream)out).write(o << 4 | p);
                            boolean chunkIsEmpty = true;
                            for (int i = 0; i < 4; ++i) {
                                for (int j = 0; j < 4; ++j) {
                                    MapTile tile = chunk.getTile(i, j);
                                    if (tile != null && tile.isLoaded()) {
                                        chunkIsEmpty = false;
                                        for (int x = 0; x < 16; ++x) {
                                            MapBlock[] c = tile.getBlockColumn(x);
                                            for (int z = 0; z < 16; ++z) {
                                                this.savePixel(c[z], (DataOutputStream)out, biomeRegistry);
                                            }
                                        }
                                        ((DataOutputStream)out).write(tile.getWorldInterpretationVersion());
                                        ((DataOutputStream)out).writeInt(tile.getWrittenCaveStart());
                                        ((DataOutputStream)out).write(tile.getWrittenCaveDepth());
                                        continue;
                                    }
                                    ((DataOutputStream)out).writeInt(-1);
                                }
                            }
                            if (chunkIsEmpty) continue;
                            regionWasSavedEmpty = false;
                            continue;
                        }
                        if (!chunk.hasHighlightsIfUndiscovered()) {
                            region.uncountTextureBiomes(chunk.getLeafTexture());
                            region.setChunk(o, p, null);
                            MapTileChunk chunkIsEmpty = chunk;
                            synchronized (chunkIsEmpty) {
                                chunk.getLeafTexture().deleteTexturesAndBuffers();
                            }
                        }
                        if ((parentRegion = region.getParent()) == null) continue;
                        parentRegion.setShouldCheckForUpdatesRecursive(true);
                    }
                }
                zipOut.closeEntry();
            }
            if (regionWasSavedEmpty) {
                this.safeDelete(permFile.toPath(), ".zip");
                this.safeDelete(file.toPath(), ".temp");
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("Save cancelled because the region would be saved empty: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                }
                return hasAnything;
            }
            this.safeMoveAndReplace(file.toPath(), permFile.toPath(), ".temp", ".zip");
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Region saved: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + this.mapProcessor.getMapWriter().getUpdateCounter());
            }
            return true;
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.error("IO exception while trying to save " + String.valueOf(region), (Throwable)ioe);
            if (extraAttempts > 0) {
                WorldMap.LOGGER.info("Retrying...");
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return this.saveRegion(region, extraAttempts - 1);
            }
            return true;
        }
    }

    private Path getBackupFolder(Path filePath, int saveVersion, int backupVersion) {
        return filePath.getParent().resolve(saveVersion + "_backup_" + backupVersion);
    }

    public void backupFile(File file, int saveVersion) throws IOException {
        if (file.getName().endsWith(".mca") || file.getName().endsWith(".mcr")) {
            throw new RuntimeException("World save protected: " + String.valueOf(file));
        }
        Path filePath = file.toPath();
        int backupVersion = 0;
        Path backupFolder = this.getBackupFolder(filePath, saveVersion, backupVersion);
        String backupName = filePath.getFileName().toString();
        Path backup = backupFolder.resolve(backupName);
        while (Files.exists(backup, new LinkOption[0])) {
            backupFolder = this.getBackupFolder(filePath, saveVersion, ++backupVersion);
            backup = backupFolder.resolve(backupName);
        }
        if (!Files.exists(backupFolder, new LinkOption[0])) {
            Files.createDirectories(backupFolder, new FileAttribute[0]);
        }
        Files.move(file.toPath(), backup, new CopyOption[0]);
        WorldMap.LOGGER.info("File " + file.getPath() + " backed up to " + backupFolder.toFile().getPath());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    public boolean loadRegion(MapRegion region, class_7225<class_2248> blockLookup, class_2378<class_2248> blockRegistry, class_2378<class_3611> fluidRegistry, BiomeGetter biomeGetter, int extraAttempts) {
        boolean multiplayer = region.isNormalMapData();
        int emptySize = multiplayer ? 0 : 8192;
        int minorSaveVersion = -1;
        int majorSaveVersion = 0;
        boolean versionReached = false;
        try {
            boolean result;
            File file = this.getFile(region);
            if (!region.hasHadTerrain() || file == null || !file.exists() || Files.size(file.toPath()) <= (long)emptySize) {
                if (region.getLoadState() == 4 || region.hasHadTerrain()) {
                    region.setSaveExists(null);
                }
                if (region.hasHadTerrain()) {
                    return false;
                }
                MapRegion mapRegion = region;
                synchronized (mapRegion) {
                    region.setLoadState((byte)1);
                }
                region.restoreBufferUpdateObjects();
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("Highlight region fake-loaded: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                }
                return true;
            }
            MapRegion mapRegion = region;
            synchronized (mapRegion) {
                region.setLoadState((byte)1);
            }
            region.setSaveExists(true);
            region.restoreBufferUpdateObjects();
            int totalChunks = 0;
            if (multiplayer) {
                this.regionLoadPalette.clear();
                this.regionLoadBiomePalette.clear();
                try (FilterInputStream in = null;){
                    MapTileChunk chunk;
                    int p;
                    int o;
                    ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(file), 2048));
                    in = new DataInputStream(zipIn);
                    zipIn.getNextEntry();
                    int firstByte = in.read();
                    boolean is115not114 = false;
                    if (firstByte == 255) {
                        int n = ((DataInputStream)in).readInt();
                        minorSaveVersion = n & 0xFFFF;
                        majorSaveVersion = n >> 16 & 0xFFFF;
                        if (majorSaveVersion == 2 && minorSaveVersion >= 5) {
                            boolean bl = is115not114 = in.read() == 1;
                        }
                        if (8 < minorSaveVersion || 7 < majorSaveVersion) {
                            zipIn.closeEntry();
                            in.close();
                            WorldMap.LOGGER.info("Trying to load a newer region " + String.valueOf(region) + " save using an older version of Xaero's World Map!");
                            this.backupFile(file, n);
                            region.setSaveExists(null);
                            boolean bl = false;
                            return bl;
                        }
                        firstByte = -1;
                    }
                    versionReached = true;
                    LeveledRegion leveledRegion = region.getLevel() == 3 ? region : region.getParent();
                    synchronized (leveledRegion) {
                        MapRegion mapRegion2 = region;
                        synchronized (mapRegion2) {
                            for (o = 0; o < 8; ++o) {
                                for (p = 0; p < 8; ++p) {
                                    chunk = region.getChunk(o, p);
                                    if (chunk == null) continue;
                                    chunk.setLoadState((byte)1);
                                }
                            }
                        }
                    }
                    class_2378<class_1959> class_23782 = region.getBiomeRegistry();
                    while (true) {
                        int chunkCoords;
                        int n = chunkCoords = firstByte == -1 ? in.read() : firstByte;
                        if (chunkCoords == -1) break;
                        firstByte = -1;
                        o = chunkCoords >> 4;
                        p = chunkCoords & 0xF;
                        chunk = region.getChunk(o, p);
                        if (chunk == null) {
                            chunk = new MapTileChunk(region, region.getRegionX() * 8 + o, region.getRegionZ() * 8 + p);
                            region.setChunk(o, p, chunk);
                        } else if (chunk.getLoadState() >= 2) {
                            throw new Exception("Map data for region " + String.valueOf(region) + " is probably corrupt! Has the same map tile chunk saved twice.");
                        }
                        if (region.isMetaLoaded()) {
                            chunk.getLeafTexture().setBufferedTextureVersion(region.getAndResetCachedTextureVersion(o, p));
                        }
                        chunk.resetHeights();
                        for (int i = 0; i < 4; ++i) {
                            for (int j = 0; j < 4; ++j) {
                                Integer nextTile = ((DataInputStream)in).readInt();
                                if (nextTile == -1) continue;
                                MapTile tile = this.mapProcessor.getTilePool().get(this.mapProcessor.getCurrentDimension(), chunk.getX() * 4 + i, chunk.getZ() * 4 + j);
                                for (int x = 0; x < 16; ++x) {
                                    MapBlock[] c = tile.getBlockColumn(x);
                                    for (int z = 0; z < 16; ++z) {
                                        if (c[z] == null) {
                                            c[z] = new MapBlock();
                                        } else {
                                            c[z].prepareForWriting(0);
                                        }
                                        this.loadPixel(nextTile, c[z], (DataInputStream)in, minorSaveVersion, majorSaveVersion, is115not114, blockLookup, biomeGetter, class_23782);
                                        nextTile = null;
                                    }
                                }
                                if (minorSaveVersion >= 4) {
                                    tile.setWorldInterpretationVersion(in.read());
                                }
                                if (minorSaveVersion >= 6) {
                                    tile.setWrittenCave(((DataInputStream)in).readInt(), minorSaveVersion >= 7 ? in.read() : 32);
                                }
                                chunk.setTile(i, j, tile, this.blockStateShortShapeCache);
                                tile.setLoaded(true);
                            }
                        }
                        if (!chunk.includeInSave()) {
                            if (chunk.hasHighlightsIfUndiscovered()) continue;
                            region.uncountTextureBiomes(chunk.getLeafTexture());
                            region.setChunk(o, p, null);
                            chunk.getLeafTexture().deleteTexturesAndBuffers();
                            chunk = null;
                            continue;
                        }
                        region.pushWriterPause();
                        ++totalChunks;
                        chunk.setToUpdateBuffers(true);
                        chunk.setLoadState((byte)2);
                        region.popWriterPause();
                    }
                    zipIn.closeEntry();
                }
                if (totalChunks > 0) {
                    if (WorldMap.settings.debug) {
                        WorldMap.LOGGER.info("Region loaded: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + majorSaveVersion + " " + minorSaveVersion);
                    }
                    return true;
                }
                region.setSaveExists(null);
                this.safeDelete(file.toPath(), ".zip");
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("Cancelled loading an empty region: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + majorSaveVersion + " " + minorSaveVersion);
                }
                return false;
            }
            int[] chunkCount = new int[1];
            WorldDataHandler.Result buildResult = this.mapProcessor.getWorldDataHandler().buildRegion(region, blockLookup, blockRegistry, fluidRegistry, true, chunkCount);
            if (buildResult == WorldDataHandler.Result.CANCEL) {
                if (region.hasHadTerrain()) {
                    RegionDetection restoredDetection = new RegionDetection(region.getWorldId(), region.getDimId(), region.getMwId(), region.getRegionX(), region.getRegionZ(), region.getRegionFile(), this.mapProcessor.getGlobalVersion(), true);
                    restoredDetection.transferInfoFrom(region);
                    region.getDim().getLayeredMapRegions().getLayer(region.getCaveLayer()).addRegionDetection(restoredDetection);
                }
                this.mapProcessor.removeMapRegion(region);
                WorldMap.LOGGER.info("Region cancelled from world save: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                return false;
            }
            region.setRegionFile(file);
            boolean bl = result = buildResult == WorldDataHandler.Result.SUCCESS && chunkCount[0] > 0;
            if (!result) {
                region.setSaveExists(null);
                if (WorldMap.settings.debug) {
                    WorldMap.LOGGER.info("Region failed to load from world save: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                }
            } else if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Region loaded from world save: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
            }
            return result;
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.error("IO exception while trying to load " + String.valueOf(region), (Throwable)ioe);
            if (extraAttempts > 0) {
                MapRegion mapRegion = region;
                synchronized (mapRegion) {
                    region.setLoadState((byte)4);
                }
                WorldMap.LOGGER.info("Retrying...");
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return this.loadRegion(region, blockLookup, blockRegistry, fluidRegistry, biomeGetter, extraAttempts - 1);
            }
            region.setSaveExists(null);
            return false;
        }
        catch (Throwable e) {
            region.setSaveExists(null);
            WorldMap.LOGGER.error("Region failed to load: " + String.valueOf(region) + (String)(versionReached ? " " + majorSaveVersion + " " + minorSaveVersion : ""), e);
            return false;
        }
    }

    public boolean beingSaved(MapDimension dim, int regX, int regZ) {
        for (int i = 0; i < this.toSave.size(); ++i) {
            MapRegion r = this.toSave.get(i);
            if (r == null || r.getDim() != dim || r.getRegionX() != regX || r.getRegionZ() != regZ) continue;
            return true;
        }
        return false;
    }

    public void requestLoad(MapRegion region, String reason) {
        this.requestLoad(region, reason, true);
    }

    public void requestLoad(MapRegion region, String reason, boolean prioritize) {
        this.addToLoad(region, reason, prioritize);
    }

    public void requestBranchCache(BranchLeveledRegion region, String reason) {
        this.requestBranchCache(region, reason, true);
        if (WorldMap.settings.debug && reason != null) {
            WorldMap.LOGGER.info("Requesting branch load for: " + String.valueOf(region) + ", " + reason);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void requestBranchCache(BranchLeveledRegion region, String reason, boolean prioritize) {
        ArrayList<BranchLeveledRegion> arrayList = this.toLoadBranchCache;
        synchronized (arrayList) {
            if (prioritize) {
                this.toLoadBranchCache.remove(region);
                this.toLoadBranchCache.add(0, region);
            } else if (!this.toLoadBranchCache.contains(region)) {
                this.toLoadBranchCache.add(region);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addToLoad(MapRegion region, String reason, boolean prioritize) {
        ArrayList<MapRegion> arrayList = this.toLoad;
        synchronized (arrayList) {
            if (prioritize) {
                region.setReloadHasBeenRequested(true, reason);
                this.toLoad.remove(region);
                this.toLoad.add(0, region);
                if (WorldMap.settings.debug && reason != null) {
                    WorldMap.LOGGER.info("Requesting load for: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + reason);
                }
            } else if (!this.loadingFiles && !this.toLoad.contains(region)) {
                region.setReloadHasBeenRequested(true, reason);
                this.toLoad.add(region);
                if (WorldMap.settings.debug && reason != null) {
                    WorldMap.LOGGER.info("Requesting load for: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + ", " + reason);
                }
            }
        }
        this.mapProcessor.getMapRegionHighlightsPreparer().prepare(region, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeToLoad(MapRegion region) {
        ArrayList<MapRegion> arrayList = this.toLoad;
        synchronized (arrayList) {
            this.toLoad.remove(region);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearToLoad() {
        ArrayList<LeveledRegion> arrayList = this.toLoad;
        synchronized (arrayList) {
            this.toLoad.clear();
        }
        arrayList = this.toLoadBranchCache;
        synchronized (arrayList) {
            this.toLoadBranchCache.clear();
        }
    }

    public int getSizeOfToLoad() {
        return this.toLoad.size();
    }

    public boolean saveExists(MapRegion region) {
        if (region.getSaveExists() != null) {
            return region.getSaveExists();
        }
        boolean result = true;
        File file = this.getFile(region);
        if (file == null || !file.exists()) {
            result = false;
        }
        region.setSaveExists(result);
        return result;
    }

    public void updateSave(LeveledRegion<?> leveledRegion, long currentTime, int currentLayer) {
        if (leveledRegion.getLevel() == 0) {
            MapRegion region = (MapRegion)leveledRegion;
            int saveTime = 60000;
            if (region.getCaveLayer() != currentLayer) {
                saveTime /= 100;
            }
            if (region.getLoadState() == 2 && region.isBeingWritten() && currentTime - region.getLastSaveTime() >= (long)saveTime && !this.beingSaved(region.getDim(), region.getRegionX(), region.getRegionZ())) {
                this.toSave.add(region);
                region.setSaveExists(true);
                region.setLastSaveTime(currentTime);
            }
        } else {
            BranchLeveledRegion region = (BranchLeveledRegion)leveledRegion;
            if (region.eligibleForSaving(currentTime)) {
                region.startDownloadingTexturesForCache(this.mapProcessor);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run(class_7225<class_2248> blockLookup, class_2378<class_2248> blockRegistry, class_2378<class_3611> fluidRegistry, BiomeGetter biomeGetter, class_2378<class_1959> biomeRegistry) throws Exception {
        if (!this.toLoad.isEmpty()) {
            boolean loaded = false;
            this.mapProcessor.pushIsLoading();
            this.loadingFiles = true;
            boolean reloadEverything = WorldMap.settings.reloadEverything;
            int limit = this.toLoad.size();
            while (!(limit <= 0 || this.mapProcessor.isWaitingForWorldUpdate() || loaded || this.toLoad.isEmpty())) {
                boolean needsLoading;
                MapRegion region;
                --limit;
                ArrayList<MapRegion> arrayList = this.toLoad;
                synchronized (arrayList) {
                    if (this.toLoad.isEmpty()) {
                        break;
                    }
                    region = this.toLoad.get(0);
                }
                if (region.hasHadTerrain() && region.getCacheFile() == null && !region.hasLookedForCache()) {
                    File potentialCacheFile = this.getCacheFile(region, region.getCaveLayer(), true, true);
                    if (potentialCacheFile.exists()) {
                        region.setCacheFile(potentialCacheFile);
                    }
                    region.setLookedForCache(true);
                }
                int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                int globalReloadVersion = WorldMap.settings.reloadVersion;
                int globalCaveDepth = WorldMap.settings.caveModeDepth;
                MapRegion mapRegion = region;
                synchronized (mapRegion) {
                    boolean bl = needsLoading = region.getLoadState() == 0 || region.getLoadState() == 4;
                    if (needsLoading) {
                        if (region.hasVersion() && region.getVersion() != this.mapProcessor.getGlobalVersion() || !region.hasVersion() && region.getInitialVersion() != this.mapProcessor.getGlobalVersion() || region.getLoadState() == 4 && reloadEverything && region.getReloadVersion() != globalReloadVersion || (region.getLoadState() == 4 || region.isMetaLoaded() && this.mainTextureLevel != region.getLevel()) && (globalRegionCacheHashCode != region.getCacheHashCode() || region.caveStartOutdated(region.getUpToDateCaveStart(), globalCaveDepth)) || region.getDim().getFullReloader() != null && region.getDim().getFullReloader().isPartOfReload(region)) {
                            region.setShouldCache(true, "loading");
                        }
                        region.setVersion(this.mapProcessor.getGlobalVersion());
                    }
                }
                if (needsLoading) {
                    BranchLeveledRegion parentRegion;
                    mapRegion = region;
                    synchronized (mapRegion) {
                        region.setAllCachePrepared(false);
                    }
                    boolean cacheOnlyMode = region.getDim().getMapWorld().isCacheOnlyMode();
                    boolean fromNothing = region.getLoadState() == 0;
                    boolean hasSomething = false;
                    boolean justMetaData = false;
                    boolean[] leafShouldAffectBranchesDest = new boolean[1];
                    int targetHighlightsHash = region.getTargetHighlightsHash();
                    boolean[] metaLoadedDest = new boolean[1];
                    boolean[][] textureLoaded = null;
                    if (cacheOnlyMode || region.getLoadState() == 0 && (!region.shouldCache() || !region.isMetaLoaded() || this.mainTextureLevel == region.getLevel()) || !region.shouldCache() && region.getLoadState() == 4) {
                        textureLoaded = new boolean[8][8];
                        justMetaData = region.loadCacheTextures(this.mapProcessor, biomeRegistry, !region.isMetaLoaded() && this.mainTextureLevel != region.getLevel(), textureLoaded, targetHighlightsHash, leafShouldAffectBranchesDest, metaLoadedDest, 10, this.oldFormatSupport);
                    }
                    if (justMetaData) {
                        hasSomething = this.cleanupLoadedCache(region, textureLoaded, justMetaData, hasSomething, targetHighlightsHash, metaLoadedDest[0]);
                        if (WorldMap.settings.debug) {
                            WorldMap.LOGGER.info("Loaded meta data for " + String.valueOf(region));
                        }
                    } else {
                        boolean shouldLoadProperly;
                        region.setHighlightsHash(targetHighlightsHash);
                        boolean shouldAddToLoaded = region.getLoadState() == 0;
                        MapRegion mapRegion2 = region;
                        synchronized (mapRegion2) {
                            boolean goingToPrepareCache;
                            boolean bl = goingToPrepareCache = region.shouldCache() && (region.isMetaLoaded() && this.mainTextureLevel != region.getLevel() || region.getLoadState() == 4 || region.getCacheFile() == null || !region.getCacheFile().exists());
                            if (!goingToPrepareCache) {
                                goingToPrepareCache = region.getDim().getFullReloader() != null && region.getDim().getFullReloader().isPartOfReload(region);
                            }
                            boolean bl2 = shouldLoadProperly = region.getLoadState() == 4 && region.isBeingWritten() || goingToPrepareCache;
                            if (cacheOnlyMode) {
                                shouldLoadProperly = false;
                            }
                            if (!shouldLoadProperly) {
                                if (leafShouldAffectBranchesDest[0]) {
                                    region.setRecacheHasBeenRequested(true, "cache affects branches");
                                    region.setShouldCache(true, "cache affects branches");
                                }
                                region.setLoadState((byte)3);
                            } else if (region.shouldCache()) {
                                region.setRecacheHasBeenRequested(true, "loading");
                            }
                        }
                        if (!shouldLoadProperly && textureLoaded != null) {
                            hasSomething = this.cleanupLoadedCache(region, textureLoaded, justMetaData, hasSomething, targetHighlightsHash, metaLoadedDest[0]);
                        }
                        this.mapProcessor.addToProcess(region);
                        if (shouldAddToLoaded) {
                            this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().addLoadedRegion(region);
                        }
                        if (shouldLoadProperly) {
                            region.setCacheHashCode(globalRegionCacheHashCode);
                            region.setReloadVersion(globalReloadVersion);
                            loaded = this.loadRegion(region, blockLookup, blockRegistry, fluidRegistry, biomeGetter, 10);
                            hasSomething = false;
                            if (!loaded) {
                                region.setShouldCache(false, "couldn't load");
                                region.setRecacheHasBeenRequested(false, "couldn't load");
                                if (region.getSaveExists() == null) {
                                    mapRegion2 = region;
                                    synchronized (mapRegion2) {
                                        region.setLoadState((byte)4);
                                    }
                                    region.deleteTexturesAndBuffers();
                                    this.mapProcessor.removeMapRegion(region);
                                }
                            } else {
                                for (int i = 0; i < 8; ++i) {
                                    for (int j = 0; j < 8; ++j) {
                                        MapRegion mapRegion3;
                                        MapTileChunk mapTileChunk = region.getChunk(i, j);
                                        if (mapTileChunk != null) {
                                            if (!mapTileChunk.includeInSave()) {
                                                region.uncountTextureBiomes(mapTileChunk.getLeafTexture());
                                                mapTileChunk.getLeafTexture().resetBiomes();
                                                if (!mapTileChunk.hasHighlightsIfUndiscovered()) {
                                                    region.setChunk(i, j, null);
                                                    mapTileChunk.getLeafTexture().deleteTexturesAndBuffers();
                                                    continue;
                                                }
                                                mapTileChunk.setLoadState((byte)2);
                                                mapTileChunk.unsetHasHadTerrain();
                                                mapTileChunk.getLeafTexture().requestHighlightOnlyUpload();
                                                hasSomething = true;
                                                mapRegion3 = region;
                                                synchronized (mapRegion3) {
                                                    region.updateLeafTextureVersion(i, j, targetHighlightsHash);
                                                    continue;
                                                }
                                            }
                                            hasSomething = true;
                                            continue;
                                        }
                                        if (region.leafTextureVersionSum[i][j] == 0) continue;
                                        mapRegion3 = region;
                                        synchronized (mapRegion3) {
                                            region.updateLeafTextureVersion(i, j, 0);
                                            continue;
                                        }
                                    }
                                }
                                if (!hasSomething) {
                                    MapRegion i = region;
                                    synchronized (i) {
                                        if (!region.isBeingWritten() && region.getLoadState() <= 1) {
                                            region.setLoadState((byte)3);
                                        }
                                    }
                                    loaded = false;
                                }
                            }
                            MapRegion i = region;
                            synchronized (i) {
                                if (region.getLoadState() <= 1) {
                                    region.setLoadState((byte)2);
                                }
                                region.setLastSaveTime(region.isResaving() ? -60000L : System.currentTimeMillis());
                            }
                            BranchLeveledRegion parentRegion2 = region.getParent();
                            if (parentRegion2 != null) {
                                parentRegion2.setShouldCheckForUpdatesRecursive(true);
                            }
                        } else if (WorldMap.settings.debug) {
                            WorldMap.LOGGER.info("Loaded from cache only for " + String.valueOf(region));
                        }
                        region.loadingNeededForBranchLevel = 0;
                    }
                    if (fromNothing && !hasSomething && (parentRegion = region.getParent()) != null) {
                        parentRegion.setShouldCheckForUpdatesRecursive(true);
                    }
                }
                region.setReloadHasBeenRequested(false, "loading");
                this.removeToLoad(region);
            }
            this.loadingFiles = false;
            this.mapProcessor.popIsLoading();
        }
        int regionsToSave = 3;
        while (!this.toSave.isEmpty() && (this.saveAll || regionsToSave > 0)) {
            boolean regionLoaded;
            MapRegion region;
            MapRegion region2 = region = this.toSave.get(0);
            synchronized (region2) {
                regionLoaded = region.getLoadState() == 2;
            }
            if (regionLoaded) {
                if (!region.isBeingWritten()) {
                    throw new Exception("Saving a weird region: " + String.valueOf(region));
                }
                region.pushWriterPause();
                boolean notEmpty = this.saveRegion(region, 20);
                region.setResaving(false);
                if (notEmpty) {
                    if (!region.isAllCachePrepared()) {
                        MapRegion needsLoading = region;
                        synchronized (needsLoading) {
                            if (!region.isAllCachePrepared()) {
                                region.requestRefresh(this.mapProcessor, false);
                            }
                        }
                    }
                    region.setRecacheHasBeenRequested(true, "saving");
                    region.setShouldCache(true, "saving");
                    region.setBeingWritten(false);
                    --regionsToSave;
                } else {
                    this.mapProcessor.removeMapRegion(region);
                }
                region.popWriterPause();
                if (region.getWorldId() == null || !this.mapProcessor.isEqual(region.getWorldId(), region.getDimId(), region.getMwId())) {
                    if (region.getCacheFile() != null) {
                        region.convertCacheToOutdated(this, "is outdated");
                        if (WorldMap.settings.debug) {
                            WorldMap.LOGGER.info(String.format("Converting cache for region %s because it IS outdated.", region));
                        }
                    }
                    region.clearRegion(this.mapProcessor);
                }
            } else if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Tried to save a weird region: " + String.valueOf(region) + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId() + " " + region.getLoadState());
            }
            this.toSave.remove(region);
        }
        this.saveAll = false;
        if (!this.toLoadBranchCache.isEmpty()) {
            int limit = this.toLoadBranchCache.size();
            this.mapProcessor.pushIsLoading();
            if (!this.mapProcessor.isWaitingForWorldUpdate()) {
                while (limit > 0) {
                    BranchLeveledRegion region;
                    --limit;
                    ArrayList<BranchLeveledRegion> notEmpty = this.toLoadBranchCache;
                    synchronized (notEmpty) {
                        if (this.toLoadBranchCache.isEmpty()) {
                            break;
                        }
                        region = this.toLoadBranchCache.get(0);
                    }
                    region.preCacheLoad();
                    LayeredRegionManager regionManager = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions();
                    regionManager.addLoadedRegion(region);
                    region.setCacheFile(region.findCacheFile(this));
                    boolean[] metaLoadedDest = new boolean[1];
                    boolean[][] textureLoaded = new boolean[8][8];
                    region.loadCacheTextures(this.mapProcessor, biomeRegistry, false, textureLoaded, 0, null, metaLoadedDest, 10, this.oldFormatSupport);
                    if (metaLoadedDest[0]) {
                        region.confirmMetaLoaded();
                    }
                    this.mapProcessor.addToProcess(region);
                    if (region.getCacheFile() == null) {
                        region.setShouldCheckForUpdatesRecursive(true);
                    } else {
                        region.setShouldCheckForUpdatesSingle(true);
                    }
                    region.setShouldCache(false, "branch loading");
                    region.setLoaded(true);
                    if (WorldMap.settings.debug) {
                        WorldMap.LOGGER.info("Loaded cache for branch region " + String.valueOf(region));
                    }
                    region.setReloadHasBeenRequested(false, "loading");
                    ArrayList<BranchLeveledRegion> globalReloadVersion = this.toLoadBranchCache;
                    synchronized (globalReloadVersion) {
                        this.toLoadBranchCache.remove(region);
                    }
                }
            }
            this.mapProcessor.popIsLoading();
        }
        if (this.mapProcessor.getMapWorld().getCurrentDimensionId() != null) {
            this.workingDimList.clear();
            this.mapProcessor.getMapWorld().getDimensions(this.workingDimList);
            for (int d = 0; d < this.workingDimList.size(); ++d) {
                MapDimension dim = this.workingDimList.get(d);
                while (!dim.regionsToCache.isEmpty()) {
                    File permFile;
                    File tempFile;
                    boolean successfullySaved;
                    LeveledRegion<?> region = this.removeToCache(dim, 0);
                    region.preCache();
                    boolean skipCaching = region.skipCaching(this.mapProcessor);
                    if (!region.shouldCache() || !region.recacheHasBeenRequested() || skipCaching) {
                        if (WorldMap.settings.detailed_debug) {
                            WorldMap.LOGGER.info("toCache cancel: " + String.valueOf(region) + " " + !region.shouldCache() + " " + !region.recacheHasBeenRequested() + " " + !region.isAllCachePrepared() + " " + skipCaching + " " + this.mapProcessor.getGlobalVersion());
                        }
                        if (region.shouldCache()) {
                            region.deleteBuffers();
                        }
                        region.setShouldCache(false, "toCache cancel");
                        region.setRecacheHasBeenRequested(false, "toCache cancel");
                        region.postCache(null, this, false);
                        continue;
                    }
                    if (!region.isAllCachePrepared()) {
                        throw new RuntimeException("Trying to save cache for a region with cache not prepared: " + String.valueOf(region) + " " + region.getExtraInfo());
                    }
                    if (region.getCacheFile() != null) {
                        this.removeTempCacheRequest(region.getCacheFile());
                    }
                    if (successfullySaved = region.saveCacheTextures(tempFile = this.getSecondaryFile(".xwmc.temp", permFile = region.findCacheFile(this)), 10)) {
                        this.cacheToConvertFromTemp.add(permFile);
                        region.setCacheFile(permFile);
                    }
                    region.setShouldCache(false, "toCache normal");
                    region.setRecacheHasBeenRequested(false, "toCache normal");
                    region.postCache(permFile, this, successfullySaved);
                }
            }
        }
        for (int i = 0; i < this.cacheToConvertFromTemp.size(); ++i) {
            File permFile = this.cacheToConvertFromTemp.get(i);
            File tempFile = this.getSecondaryFile(".xwmc.temp", permFile);
            try {
                if (Files.exists(tempFile.toPath(), new LinkOption[0])) {
                    Misc.safeMoveAndReplace(tempFile.toPath(), permFile.toPath(), true);
                }
                this.cacheToConvertFromTemp.remove(i);
                --i;
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    public boolean removeTempCacheRequest(File file) {
        boolean result = false;
        while (this.cacheToConvertFromTemp.remove(file)) {
            result = true;
        }
        return result;
    }

    public void addTempCacheRequest(File file) {
        this.cacheToConvertFromTemp.add(file);
    }

    private boolean cleanupLoadedCache(MapRegion region, boolean[][] textureLoaded, boolean justMetaData, boolean hasSomething, int targetHighlightsHash, boolean metaLoaded) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                boolean loaded = textureLoaded[i][j];
                if (justMetaData || !loaded) {
                    MapTileChunk mapTileChunk = region.getChunk(i, j);
                    if (mapTileChunk != null) {
                        if (justMetaData || !mapTileChunk.hasHighlightsIfUndiscovered()) {
                            region.setChunk(i, j, null);
                            if (!justMetaData) {
                                mapTileChunk.getLeafTexture().deleteTexturesAndBuffers();
                            }
                        } else {
                            mapTileChunk.getLeafTexture().requestHighlightOnlyUpload();
                            hasSomething = true;
                        }
                        if (loaded || !mapTileChunk.hasHighlightsIfUndiscovered()) continue;
                        region.updateLeafTextureVersion(i, j, targetHighlightsHash);
                        continue;
                    }
                    if (loaded || region.leafTextureVersionSum[i][j] == 0) continue;
                    region.updateLeafTextureVersion(i, j, 0);
                    continue;
                }
                hasSomething = true;
            }
        }
        if (metaLoaded) {
            region.confirmMetaLoaded();
        }
        return hasSomething;
    }

    private void savePixel(MapBlock pixel, DataOutputStream out, class_2378<class_1959> biomeRegistry) throws IOException {
        boolean isGrass = pixel.isGrass();
        boolean inPalette = false;
        boolean biomeInPalette = false;
        class_2680 state = null;
        int parametres = pixel.getParametres();
        if (!isGrass && !(inPalette = this.regionSavePalette.containsKey(state = pixel.getState()))) {
            parametres |= 0x200000;
        }
        class_5321<class_1959> pixelBiome = pixel.getBiome();
        String pixelBiomeString = null;
        if (pixelBiome != null && !(biomeInPalette = this.regionSaveBiomePalette.containsKey(pixelBiome))) {
            parametres |= 0x400000;
            class_2960 biomeIdentifier = pixelBiome.method_29177();
            String string = pixelBiomeString = biomeIdentifier == null ? null : biomeIdentifier.toString();
            if (pixelBiomeString == null) {
                pixelBiomeString = class_1972.field_9451.method_29177().toString();
            }
        }
        out.writeInt(parametres);
        if (!isGrass) {
            if (inPalette) {
                out.writeInt(this.regionSavePalette.get(state));
            } else {
                if (state instanceof UnknownBlockState) {
                    ((UnknownBlockState)state).write(out);
                } else {
                    class_2507.method_10628((class_2487)class_2512.method_10686((class_2680)state), (DataOutput)out);
                }
                this.regionSavePalette.put(state, this.regionSavePalette.size());
            }
        }
        if ((parametres & 0x1000000) != 0) {
            out.write(pixel.getTopHeight());
        }
        if (pixel.getNumberOfOverlays() != 0) {
            out.write(pixel.getOverlays().size());
            for (int i = 0; i < pixel.getOverlays().size(); ++i) {
                this.saveOverlay(pixel.getOverlays().get(i), out);
            }
        }
        if (pixelBiome != null) {
            if (biomeInPalette) {
                out.writeInt(this.regionSaveBiomePalette.get(pixelBiome));
            } else {
                out.writeUTF(pixelBiomeString);
                this.regionSaveBiomePalette.put(pixelBiome, this.regionSaveBiomePalette.size());
            }
        }
    }

    private void loadPixel(Integer next, MapBlock pixel, DataInputStream in, int minorSaveVersion, int majorSaveVersion, boolean is115not114, class_7225<class_2248> blockLookup, BiomeGetter biomeGetter, class_2378<class_1959> biomeRegistry) throws IOException {
        int savedColourType;
        boolean topHeightIsDifferent;
        int parametres = next != null ? next.intValue() : in.readInt();
        if ((parametres & 1) != 0) {
            if (majorSaveVersion == 0) {
                int state = in.readInt();
                pixel.setState(this.oldFormatSupport.getStateForId(state));
            } else {
                class_2680 state;
                boolean paletteNew;
                boolean bl = paletteNew = (parametres & 0x200000) != 0;
                if (paletteNew) {
                    class_2487 nbt = class_2507.method_10627((DataInput)in);
                    if (majorSaveVersion < 7) {
                        this.oldFormatSupport.fixBlock(nbt, majorSaveVersion);
                    }
                    state = WorldMap.unknownBlockStateCache.getBlockStateFromNBT(blockLookup, nbt);
                    this.regionLoadPalette.add(state);
                } else {
                    int paletteIndex = in.readInt();
                    state = this.regionLoadPalette.get(paletteIndex);
                }
                pixel.setState(state);
            }
        } else {
            pixel.setState(class_2246.field_10219.method_9564());
        }
        if ((parametres & 0x40) != 0) {
            pixel.setHeight(in.read());
        } else {
            int heightSecondPartOffset = minorSaveVersion >= 4 ? 25 : 24;
            int heightBitsCombined = parametres >> 12 & 0xFF | (parametres >> heightSecondPartOffset & 0xF) << 8;
            int signedHeight12Bit = heightBitsCombined << 20 >> 20;
            pixel.setHeight(signedHeight12Bit);
        }
        boolean bl = minorSaveVersion < 4 ? false : (topHeightIsDifferent = (parametres & 0x1000000) != 0);
        if (topHeightIsDifferent) {
            pixel.setTopHeight(in.read());
        } else {
            pixel.setTopHeight(pixel.getHeight());
        }
        boolean stillUsesColorTypes = minorSaveVersion < 5 || majorSaveVersion <= 2 && !is115not114;
        this.overlayBuilder.startBuilding();
        if ((parametres & 2) != 0) {
            int amount = in.read();
            for (int i = 0; i < amount; ++i) {
                this.loadOverlay(pixel, in, minorSaveVersion, majorSaveVersion, stillUsesColorTypes, blockLookup, biomeGetter);
            }
        }
        this.overlayBuilder.finishBuilding(pixel);
        int n = savedColourType = stillUsesColorTypes ? parametres >> 2 & 3 : 0;
        if (savedColourType == 3) {
            in.readInt();
        }
        class_5321 biomeKey = null;
        if (savedColourType != 0 && savedColourType != 3 || (parametres & 0x100000) != 0) {
            if (majorSaveVersion < 4) {
                int biomeByte = in.read();
                int oldBiomeByte = minorSaveVersion < 3 || biomeByte < 255 ? biomeByte : in.readInt();
                String biomeStringId = this.oldFormatSupport.fixBiome(oldBiomeByte, majorSaveVersion);
                biomeKey = class_5321.method_29179((class_5321)class_7924.field_41236, (class_2960)class_2960.method_60654((String)biomeStringId));
            } else {
                boolean paletteNew;
                boolean bl2 = paletteNew = (parametres & 0x400000) != 0;
                if (paletteNew) {
                    String biomeIdentifier;
                    boolean biomeAsInt;
                    boolean bl3 = biomeAsInt = (parametres & 0x800000) != 0;
                    if (biomeAsInt) {
                        int biomeId = in.readInt();
                        biomeIdentifier = this.oldFormatSupport.fixBiome(biomeId, majorSaveVersion);
                    } else {
                        biomeIdentifier = this.oldFormatSupport.fixBiome(in.readUTF(), majorSaveVersion);
                    }
                    biomeKey = class_5321.method_29179((class_5321)class_7924.field_41236, (class_2960)class_2960.method_60654((String)biomeIdentifier));
                    this.regionLoadBiomePalette.add((class_5321<class_1959>)biomeKey);
                } else {
                    int paletteIndex = in.readInt();
                    biomeKey = this.regionLoadBiomePalette.get(paletteIndex);
                }
            }
        }
        pixel.setBiome(biomeKey);
        if (minorSaveVersion == 2) {
            boolean hasSlope;
            boolean bl4 = hasSlope = (parametres & 0x10) != 0;
            if (hasSlope) {
                pixel.setVerticalSlope((byte)in.read());
                pixel.setSlopeUnknown(false);
            }
        }
        pixel.setLight((byte)(parametres >> 8 & 0xF));
        pixel.setGlowing(this.mapProcessor.getMapWriter().isGlowing(pixel.getState()));
    }

    private void saveOverlay(Overlay o, DataOutputStream out) throws IOException {
        boolean isWater = o.isWater();
        boolean inPalette = false;
        class_2680 state = null;
        int parametres = o.getParametres();
        if (!isWater && !(inPalette = this.regionSavePalette.containsKey(state = o.getState()))) {
            parametres |= 0x400;
        }
        out.writeInt(parametres);
        if (!isWater) {
            if (inPalette) {
                out.writeInt(this.regionSavePalette.get(state));
            } else {
                if (state instanceof UnknownBlockState) {
                    ((UnknownBlockState)state).write(out);
                } else {
                    class_2507.method_10628((class_2487)class_2512.method_10686((class_2680)state), (DataOutput)out);
                }
                this.regionSavePalette.put(state, this.regionSavePalette.size());
            }
        }
    }

    private void loadOverlay(MapBlock pixel, DataInputStream in, int minorSaveVersion, int majorSaveVersion, boolean stillUsesColorTypes, class_7225<class_2248> blockLookup, BiomeGetter biomeGetter) throws IOException {
        int savedColourType;
        class_2680 state;
        int parametres = in.readInt();
        if ((parametres & 1) != 0) {
            if (majorSaveVersion == 0) {
                state = this.oldFormatSupport.getStateForId(in.readInt());
            } else {
                boolean paletteNew;
                boolean bl = paletteNew = (parametres & 0x400) != 0;
                if (paletteNew) {
                    class_2487 nbt = class_2507.method_10627((DataInput)in);
                    state = WorldMap.unknownBlockStateCache.getBlockStateFromNBT(blockLookup, nbt);
                    this.regionLoadPalette.add(state);
                } else {
                    int paletteIndex = in.readInt();
                    state = this.regionLoadPalette.get(paletteIndex);
                }
            }
        } else {
            state = class_2246.field_10382.method_9564();
        }
        int opacity = 1;
        if (minorSaveVersion < 1 && (parametres & 2) != 0) {
            in.readInt();
        }
        int n = savedColourType = stillUsesColorTypes ? (int)(parametres >> 8 & 3) : 0;
        if (savedColourType == 2 || (parametres & 4) != 0) {
            in.readInt();
        }
        if (minorSaveVersion < 8) {
            if ((parametres & 8) != 0) {
                opacity = in.readInt();
            }
        } else {
            opacity = parametres >> 11 & 0xF;
        }
        byte light = (byte)(parametres >> 4 & 0xF);
        this.overlayBuilder.build(state, opacity, light, this.mapProcessor, null);
    }

    public boolean isRegionDetectionComplete() {
        return this.regionDetectionComplete;
    }

    public void setRegionDetectionComplete(boolean regionDetectionComplete) {
        this.regionDetectionComplete = regionDetectionComplete;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void requestCache(LeveledRegion<?> region) {
        if (!this.toCacheContains(region)) {
            ArrayList<LeveledRegion<?>> arrayList = region.getDim().regionsToCache;
            synchronized (arrayList) {
                region.getDim().regionsToCache.add(region);
            }
            if (WorldMap.settings.debug) {
                WorldMap.LOGGER.info("Requesting cache! " + String.valueOf(region));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LeveledRegion<?> removeToCache(MapDimension mapDim, int index) {
        ArrayList<LeveledRegion<?>> arrayList = mapDim.regionsToCache;
        synchronized (arrayList) {
            return mapDim.regionsToCache.remove(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeToCache(LeveledRegion<?> region) {
        ArrayList<LeveledRegion<?>> arrayList = region.getDim().regionsToCache;
        synchronized (arrayList) {
            region.getDim().regionsToCache.remove(region);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean toCacheContains(LeveledRegion<?> region) {
        ArrayList<LeveledRegion<?>> arrayList = region.getDim().regionsToCache;
        synchronized (arrayList) {
            return region.getDim().regionsToCache.contains(region);
        }
    }

    public ArrayList<MapRegion> getToSave() {
        return this.toSave;
    }

    public LeveledRegion<?> getNextToLoadByViewing() {
        return this.nextToLoadByViewing;
    }

    public void setNextToLoadByViewing(LeveledRegion<?> nextToLoadByViewing) {
        this.nextToLoadByViewing = nextToLoadByViewing;
    }

    public OldFormatSupport getOldFormatSupport() {
        return this.oldFormatSupport;
    }

    public void safeDelete(Path filePath, String extension) throws IOException {
        if (!filePath.getFileName().toString().endsWith(extension)) {
            throw new RuntimeException("Incorrect file extension: " + String.valueOf(filePath));
        }
        Files.deleteIfExists(filePath);
    }

    public void safeMoveAndReplace(Path fromPath, Path toPath, String fromExtension, String toExtension) throws IOException {
        if (!toPath.getFileName().toString().endsWith(toExtension) || !fromPath.getFileName().toString().endsWith(fromExtension)) {
            throw new RuntimeException("Incorrect file extension: " + String.valueOf(fromPath) + " " + String.valueOf(toPath));
        }
        Misc.safeMoveAndReplace(fromPath, toPath, true);
    }

    public int getSizeOfToLoadBranchCache() {
        return this.toLoadBranchCache.size();
    }
}

