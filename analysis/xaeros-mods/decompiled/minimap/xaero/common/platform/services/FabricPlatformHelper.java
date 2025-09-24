/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.fabricmc.loader.api.metadata.ModOrigin
 *  net.fabricmc.loader.api.metadata.ModOrigin$Kind
 */
package xaero.common.platform.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import xaero.common.hud.render.util.FabricRenderDeviceUtil;
import xaero.common.hud.render.util.FabricRenderUtil;
import xaero.common.misc.IObfuscatedReflection;
import xaero.common.misc.ObfuscatedReflectionFabric;
import xaero.common.platform.services.IPlatformHelper;
import xaero.hud.controls.key.IKeyBindingHelper;
import xaero.hud.controls.key.KeyBindingHelperFabric;
import xaero.hud.render.util.IPlatformRenderDeviceUtil;
import xaero.hud.render.util.IPlatformRenderUtil;

public class FabricPlatformHelper
implements IPlatformHelper {
    private final IObfuscatedReflection obfuscatedReflectionFabric = new ObfuscatedReflectionFabric();
    private final KeyBindingHelperFabric keyBindingHelperFabric = new KeyBindingHelperFabric();
    private final FabricRenderUtil fabricRenderUtil = new FabricRenderUtil();
    private final FabricRenderDeviceUtil fabricRenderDeviceUtil = new FabricRenderDeviceUtil();

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public IObfuscatedReflection getObfuscatedReflection() {
        return this.obfuscatedReflectionFabric;
    }

    @Override
    public IKeyBindingHelper getKeyBindingHelper() {
        return this.keyBindingHelperFabric;
    }

    @Override
    public IPlatformRenderUtil getPlatformRenderUtil() {
        return this.fabricRenderUtil;
    }

    @Override
    public IPlatformRenderDeviceUtil getPlatformRenderDeviceUtil() {
        return this.fabricRenderDeviceUtil;
    }

    @Override
    public boolean isDedicatedServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir().normalize();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path getModFile(String modId) {
        Path modFile;
        ModContainer modContainer = FabricLoader.getInstance().getModContainer(modId).orElse(null);
        ModOrigin origin = modContainer.getOrigin();
        Path path = modFile = origin.getKind() == ModOrigin.Kind.PATH ? (Path)origin.getPaths().get(0) : null;
        if (modFile == null) {
            try {
                Class<?> quiltLoaderClass = Class.forName("org.quiltmc.loader.api.QuiltLoader");
                Method quiltGetModContainerMethod = quiltLoaderClass.getDeclaredMethod("getModContainer", String.class);
                Class<?> quiltModContainerAPIClass = Class.forName("org.quiltmc.loader.api.ModContainer");
                Method quiltGetSourcePathsMethod = quiltModContainerAPIClass.getDeclaredMethod("getSourcePaths", new Class[0]);
                Object quiltModContainer = ((Optional)quiltGetModContainerMethod.invoke(null, modContainer.getMetadata().getId())).orElse(null);
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
}

