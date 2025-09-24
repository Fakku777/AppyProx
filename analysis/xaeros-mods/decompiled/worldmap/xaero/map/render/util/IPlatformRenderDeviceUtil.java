/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 */
package xaero.map.render.util;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;

public interface IPlatformRenderDeviceUtil {
    default public GpuDevice getRealDevice() {
        return RenderSystem.getDevice();
    }

    default public GpuTexture getRealTexture(GpuTexture texture) {
        return texture;
    }
}

