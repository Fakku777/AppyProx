/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.loader.api.FabricLoader
 */
package xaero.map.platform.services;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import xaero.map.controls.IKeyBindingHelper;
import xaero.map.controls.KeyBindingHelperFabric;
import xaero.map.misc.IObfuscatedReflection;
import xaero.map.misc.ObfuscatedReflectionFabric;
import xaero.map.platform.services.IPlatformHelper;
import xaero.map.render.util.FabricRenderDeviceUtil;
import xaero.map.render.util.FabricRenderUtil;
import xaero.map.render.util.IPlatformRenderDeviceUtil;
import xaero.map.render.util.IPlatformRenderUtil;

public class FabricPlatformHelper
implements IPlatformHelper {
    private final IObfuscatedReflection obfuscatedFieldReflection = new ObfuscatedReflectionFabric();
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
    public IObfuscatedReflection getObfuscatedFieldReflection() {
        return this.obfuscatedFieldReflection;
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
}

