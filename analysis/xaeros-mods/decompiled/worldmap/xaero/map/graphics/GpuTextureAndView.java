/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 */
package xaero.map.graphics;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

public class GpuTextureAndView {
    public final GpuTexture texture;
    public final GpuTextureView view;

    public GpuTextureAndView(GpuTexture texture, GpuTextureView view) {
        this.texture = texture;
        this.view = view;
    }

    public void close() {
        this.view.close();
        this.texture.close();
    }
}

