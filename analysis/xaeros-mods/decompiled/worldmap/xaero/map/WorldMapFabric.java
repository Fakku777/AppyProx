/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.api.DedicatedServerModInitializer
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$PlayPayloadHandler
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.fabricmc.loader.api.metadata.ModOrigin
 *  net.fabricmc.loader.api.metadata.ModOrigin$Kind
 *  net.minecraft.class_2378
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 *  net.minecraft.class_9139
 */
package xaero.map;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9139;
import xaero.map.MapWriter;
import xaero.map.MapWriterFabric;
import xaero.map.WorldMap;
import xaero.map.WorldMapClientOnly;
import xaero.map.WorldMapClientOnlyFabric;
import xaero.map.biome.BiomeGetter;
import xaero.map.cache.BlockStateShortShapeCache;
import xaero.map.effects.EffectsRegister;
import xaero.map.events.ClientEventsFabric;
import xaero.map.events.CommonEventsFabric;
import xaero.map.events.ModClientEventsFabric;
import xaero.map.events.ModCommonEventsFabric;
import xaero.map.message.WorldMapMessageHandlerFabric;
import xaero.map.message.WorldMapMessageHandlerFull;
import xaero.map.message.payload.WorldMapMessagePayload;
import xaero.map.message.payload.WorldMapMessagePayloadCodec;
import xaero.map.message.server.WorldMapPayloadServerHandler;
import xaero.map.mods.SupportMods;
import xaero.map.mods.SupportModsFabric;
import xaero.map.region.OverlayManager;
import xaero.map.server.WorldMapServer;
import xaero.map.server.WorldMapServerFabric;

public class WorldMapFabric
extends WorldMap
implements ClientModInitializer,
DedicatedServerModInitializer {
    private final String fileLayoutID = "worldmap_fabric";
    private Throwable firstStageError;
    private boolean loadLaterNeeded;
    private boolean loadLaterDone;

    public void onInitializeClient() {
        try {
            this.loadCommon();
            this.loadClient();
        }
        catch (Throwable e) {
            this.firstStageError = e;
        }
    }

    public void onInitializeServer() {
        try {
            this.loadCommon();
            this.loadServer();
        }
        catch (Throwable e) {
            this.firstStageError = e;
        }
    }

    private void registerClientEvents() {
        events = new ClientEventsFabric();
        modEvents = new ModClientEventsFabric();
    }

    private void registerCommonEvents() {
        CommonEventsFabric commonEventsFabric = new CommonEventsFabric();
        commonEvents = commonEventsFabric;
        modCommonEvents = new ModCommonEventsFabric();
        commonEventsFabric.register();
    }

    @Override
    void loadCommon() {
        messageHandler = new WorldMapMessageHandlerFabric();
        super.loadCommon();
        if (WorldMapFabric.commonConfig.registerStatusEffects) {
            new EffectsRegister().registerEffects(effect -> class_2378.method_47985((class_2378)class_7923.field_41174, (class_2960)effect.getRegistryName(), (Object)effect));
        }
        WorldMapMessagePayloadCodec worldMapMessagePayloadCodec = new WorldMapMessagePayloadCodec((WorldMapMessageHandlerFull)messageHandler);
        PayloadTypeRegistry.playS2C().register(WorldMapMessagePayload.TYPE, (class_9139)worldMapMessagePayloadCodec);
        PayloadTypeRegistry.playC2S().register(WorldMapMessagePayload.TYPE, (class_9139)worldMapMessagePayloadCodec);
        ServerPlayNetworking.registerGlobalReceiver(WorldMapMessagePayload.TYPE, (ServerPlayNetworking.PlayPayloadHandler)new WorldMapPayloadServerHandler());
        this.registerCommonEvents();
    }

    @Override
    void loadClient() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        this.registerClientEvents();
        super.loadClient();
        controlsRegister.register(KeyBindingHelper::registerKeyBinding);
        this.loadLaterNeeded = true;
    }

    @Override
    void loadServer() {
        super.loadServer();
        this.loadLaterNeeded = true;
    }

    public void tryLoadLater() {
        if (this.loadLaterDone) {
            return;
        }
        if (this.firstStageError != null) {
            throw new RuntimeException(this.firstStageError);
        }
        if (!this.loadLaterNeeded) {
            return;
        }
        this.loadLaterDone = true;
        this.loadLater();
    }

    public void tryLoadLaterServer() {
        if (this.loadLaterDone) {
            return;
        }
        if (this.firstStageError != null) {
            throw new RuntimeException(this.firstStageError);
        }
        if (!this.loadLaterNeeded) {
            return;
        }
        this.loadLaterDone = true;
        this.loadLaterServer();
    }

    @Override
    protected Path fetchModFile() {
        Path modFile;
        ModContainer modContainer = FabricLoader.getInstance().getModContainer("xaeroworldmap").orElse(null);
        ModOrigin origin = modContainer.getOrigin();
        Path path = modFile = origin.getKind() == ModOrigin.Kind.PATH ? (Path)origin.getPaths().get(0) : null;
        if (modFile == null) {
            try {
                Class<?> quiltLoaderClass = Class.forName("org.quiltmc.loader.api.QuiltLoader");
                Method quiltGetModContainerMethod = quiltLoaderClass.getDeclaredMethod("getModContainer", String.class);
                Class<?> quiltModContainerAPIClass = Class.forName("org.quiltmc.loader.api.ModContainer");
                Method quiltGetSourcePathsMethod = quiltModContainerAPIClass.getDeclaredMethod("getSourcePaths", new Class[0]);
                Object quiltModContainer = ((Optional)quiltGetModContainerMethod.invoke(null, "xaeroworldmap")).orElse(null);
                List paths = (List)quiltGetSourcePathsMethod.invoke(quiltModContainer, new Object[0]);
                if (!paths.isEmpty() && !((List)paths.get(0)).isEmpty()) {
                    modFile = (Path)((List)paths.get(0)).get(0);
                }
            }
            catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException exception) {
                // empty catch block
            }
        }
        return modFile;
    }

    @Override
    protected String getFileLayoutID() {
        return "worldmap_fabric";
    }

    @Override
    protected SupportMods createSupportMods() {
        return new SupportModsFabric();
    }

    @Override
    protected WorldMapClientOnly createClientLoad() {
        return new WorldMapClientOnlyFabric();
    }

    @Override
    protected WorldMapServer createServerLoad() {
        return new WorldMapServerFabric();
    }

    @Override
    public MapWriter createWriter(OverlayManager overlayManager, BlockStateShortShapeCache blockStateShortShapeCache, BiomeGetter biomeGetter) {
        return new MapWriterFabric(overlayManager, blockStateShortShapeCache, biomeGetter);
    }

    @Override
    protected String getModInfoVersion() {
        ModContainer modContainer = (ModContainer)FabricLoader.getInstance().getModContainer("xaeroworldmap").get();
        return modContainer.getMetadata().getVersion().getFriendlyString() + "_fabric";
    }
}

