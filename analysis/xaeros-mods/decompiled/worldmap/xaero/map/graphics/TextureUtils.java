/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_1044
 *  net.minecraft.class_276
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package xaero.map.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import net.minecraft.class_1044;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public class TextureUtils {
    public static void setTexture(int index, class_2960 textureLocation) {
        class_1044 textureObject = class_310.method_1551().method_1531().method_4619(textureLocation);
        GpuTextureView texture = textureObject == null ? null : textureObject.method_71659();
        RenderSystem.setShaderTexture((int)index, (GpuTextureView)texture);
    }

    public static void clearRenderTarget(class_276 renderTarget, int color, float depth) {
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(renderTarget.method_30277(), color, renderTarget.method_30278(), (double)depth);
    }

    public static void clearRenderTarget(class_276 renderTarget, int color) {
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(renderTarget.method_30277(), color);
    }

    public static void clearRenderTargetDepth(class_276 renderTarget, float depth) {
        RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(renderTarget.method_30278(), (double)depth);
    }

    public static IntBuffer allocateLittleEndianIntBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
    }
}

