/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.platform.services;

import java.nio.file.Path;
import xaero.map.controls.IKeyBindingHelper;
import xaero.map.misc.IObfuscatedReflection;
import xaero.map.render.util.IPlatformRenderDeviceUtil;
import xaero.map.render.util.IPlatformRenderUtil;

public interface IPlatformHelper {
    public String getPlatformName();

    public boolean isModLoaded(String var1);

    default public boolean checkModForMixin(String modId) {
        return this.isModLoaded(modId);
    }

    public boolean isDevelopmentEnvironment();

    default public String getEnvironmentName() {
        return this.isDevelopmentEnvironment() ? "development" : "production";
    }

    public IObfuscatedReflection getObfuscatedFieldReflection();

    public IKeyBindingHelper getKeyBindingHelper();

    public IPlatformRenderUtil getPlatformRenderUtil();

    public IPlatformRenderDeviceUtil getPlatformRenderDeviceUtil();

    public boolean isDedicatedServer();

    public Path getGameDir();

    public Path getConfigDir();
}

