/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_6367
 */
package xaero.map.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.reflect.Field;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_6367;
import xaero.map.graphics.OpenGlHelper;
import xaero.map.icon.XaeroIconAtlas;
import xaero.map.misc.Misc;

public class ImprovedFramebuffer
extends class_6367 {
    private static Field MAIN_RENDER_TARGET_FIELD = Misc.getFieldReflection(class_310.class, "mainRenderTarget", "field_1689", "Lnet/minecraft/class_276;", "f_91042_");
    private static class_276 mainRenderTargetBackup;

    public ImprovedFramebuffer(int width, int height, boolean useDepthIn) {
        super(null, width, height, useDepthIn);
    }

    public void bindDefaultFramebuffer(class_310 mc) {
        ImprovedFramebuffer.restoreMainRenderTarget();
    }

    public void bindRead() {
        RenderSystem.setShaderTexture((int)0, (GpuTextureView)this.field_60567);
    }

    public void generateMipmaps() {
        OpenGlHelper.generateMipmaps(this.field_1475);
    }

    private void forceAsMainRenderTarget() {
        if (mainRenderTargetBackup == null) {
            mainRenderTargetBackup = (class_276)Misc.getReflectFieldValue(class_310.method_1551(), MAIN_RENDER_TARGET_FIELD);
        }
        Misc.setReflectFieldValue(class_310.method_1551(), MAIN_RENDER_TARGET_FIELD, this);
    }

    public static void restoreMainRenderTarget() {
        if (mainRenderTargetBackup != null) {
            Misc.setReflectFieldValue(class_310.method_1551(), MAIN_RENDER_TARGET_FIELD, mainRenderTargetBackup);
        }
    }

    public void bindAsMainTarget(boolean viewport) {
        this.forceAsMainRenderTarget();
    }

    public void setColorTexture(GpuTexture texture, GpuTextureView textureView) {
        this.field_1475 = texture;
        this.field_60567 = textureView;
    }

    public void setColorTexture(XaeroIconAtlas atlas) {
        this.setColorTexture(atlas.getTextureId(), atlas.getTextureView());
    }

    public void setDepthTexture(GpuTexture depthTexture, GpuTextureView textureView) {
        this.field_56739 = depthTexture;
        this.field_60568 = textureView;
    }

    public void closeColorTexture() {
        this.field_60567.close();
        this.field_1475.close();
    }

    public void closeDepthTexture() {
        this.field_60568.close();
        this.field_56739.close();
    }
}

