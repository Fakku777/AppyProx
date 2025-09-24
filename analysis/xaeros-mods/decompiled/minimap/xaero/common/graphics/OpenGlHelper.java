/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_10865
 *  net.minecraft.class_10868
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package xaero.common.graphics;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_10865;
import net.minecraft.class_10868;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import xaero.common.platform.Services;

public class OpenGlHelper {
    public static boolean isUsingOpenGL() {
        return Services.PLATFORM.getPlatformRenderDeviceUtil().getRealDevice() instanceof class_10865;
    }

    public static void resetPixelStore() {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        GlStateManager._pixelStore((int)3317, (int)4);
        GlStateManager._pixelStore((int)3316, (int)0);
        GlStateManager._pixelStore((int)3315, (int)0);
        GlStateManager._pixelStore((int)3314, (int)0);
    }

    public static void bindTexture(int index, GpuTexture texture) {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        class_10868 glTexture = (class_10868)texture;
        GlStateManager._activeTexture((int)(33984 + index));
        GlStateManager._bindTexture((int)(glTexture == null ? 0 : glTexture.method_68427()));
    }

    public static void generateMipmaps() {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        GL30.glGenerateMipmap((int)3553);
    }

    public static void fixOtherMods() {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        GlStateManager._blendFuncSeparate((int)1, (int)771, (int)1, (int)771);
        GlStateManager._blendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        GlStateManager._disableDepthTest();
        GlStateManager._enableDepthTest();
        GlStateManager._depthFunc((int)516);
        GlStateManager._depthFunc((int)515);
        GlStateManager._colorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        GlStateManager._colorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GlStateManager._depthMask((boolean)false);
        GlStateManager._depthMask((boolean)true);
    }

    public static void clearErrors() {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        while (GL11.glGetError() != 0) {
        }
    }
}

