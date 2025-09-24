/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.minecraft.class_1049
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3300
 */
package xaero.common.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.nio.IntBuffer;
import net.minecraft.class_1049;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import xaero.common.graphics.GpuTextureAndView;
import xaero.common.graphics.TextureUtils;

public class MinimapTexture
extends class_1049 {
    public IntBuffer buffer = TextureUtils.allocateLittleEndianIntBuffer(0x100000);
    boolean loaded = false;
    private GpuTextureAndView textureAndView;

    public void loadIfNeeded() throws IOException {
        if (!this.loaded) {
            this.load(class_310.method_1551().method_1478());
            this.loaded = true;
        }
    }

    public MinimapTexture(class_2960 location) throws IOException {
        super(location);
    }

    public void load(class_3300 resourceManager_1) throws IOException {
        this.field_56974 = RenderSystem.getDevice().createTexture("minimap_safe_mode", 5, TextureFormat.RGBA8, 512, 512, 1, 1);
        this.field_60597 = RenderSystem.getDevice().createTextureView(this.field_56974);
        this.textureAndView = new GpuTextureAndView(this.field_56974, this.field_60597);
    }

    public GpuTextureAndView getTextureAndView() {
        return this.textureAndView;
    }
}

