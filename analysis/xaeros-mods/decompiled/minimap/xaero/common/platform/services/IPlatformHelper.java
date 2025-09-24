/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.platform.services;

import java.nio.file.Path;
import xaero.common.misc.IObfuscatedReflection;
import xaero.hud.controls.key.IKeyBindingHelper;
import xaero.hud.render.util.IPlatformRenderDeviceUtil;
import xaero.hud.render.util.IPlatformRenderUtil;

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

    public IObfuscatedReflection getObfuscatedReflection();

    public IKeyBindingHelper getKeyBindingHelper();

    public IPlatformRenderUtil getPlatformRenderUtil();

    public IPlatformRenderDeviceUtil getPlatformRenderDeviceUtil();

    public boolean isDedicatedServer();

    public Path getGameDir();

    public Path getConfigDir();

    public Path getModFile(String var1);
}

