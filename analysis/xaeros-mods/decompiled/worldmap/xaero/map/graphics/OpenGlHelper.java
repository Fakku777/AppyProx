/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.opengl.GlConst
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.minecraft.class_10859
 *  net.minecraft.class_10865
 *  net.minecraft.class_10868
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package xaero.map.graphics;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.class_10859;
import net.minecraft.class_10865;
import net.minecraft.class_10868;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import xaero.map.WorldMap;
import xaero.map.core.IWorldMapGlBuffer;
import xaero.map.exception.OpenGLException;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.PixelBuffers;
import xaero.map.platform.Services;

public class OpenGlHelper {
    public static boolean isUsingOpenGL() {
        return Services.PLATFORM.getPlatformRenderDeviceUtil().getRealDevice() instanceof class_10865;
    }

    public static void resetPixelStore() {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        GlStateManager._pixelStore((int)3333, (int)4);
        GlStateManager._pixelStore((int)3330, (int)0);
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

    public static void generateMipmaps(GpuTexture texture) {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        class_10868 glTexture = (class_10868)texture;
        GlStateManager._activeTexture((int)33984);
        GlStateManager._bindTexture((int)glTexture.method_68427());
        glTexture.method_68424(3553);
        GL30.glGenerateMipmap((int)3553);
    }

    public static void clearErrors(boolean loud, String where) {
        int error;
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        while ((error = GL11.glGetError()) != 0) {
            if (!loud) continue;
            WorldMap.LOGGER.warn("OpenGL error ({}): {}", (Object)where, (Object)error);
        }
    }

    public static void deleteTextures(List<GpuTextureAndView> textures, int count) {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        if (textures == null || textures.isEmpty()) {
            return;
        }
        for (int i = 0; i < count && !textures.isEmpty(); ++i) {
            GpuTextureAndView glTexture = textures.remove(textures.size() - 1);
            glTexture.close();
        }
    }

    public static void deleteBuffers(List<GpuBuffer> buffers, int count) {
        if (!OpenGlHelper.isUsingOpenGL()) {
            return;
        }
        if (buffers == null || buffers.isEmpty()) {
            return;
        }
        for (int i = 0; i < count && !buffers.isEmpty(); ++i) {
            class_10859 glBuffer = (class_10859)buffers.remove(buffers.size() - 1);
            glBuffer.close();
        }
    }

    public static void unbindUnpackBuffer() {
        PixelBuffers.glBindBuffer(35052, 0);
    }

    public static void unbindPackBuffer() {
        PixelBuffers.glBindBuffer(35051, 0);
    }

    public static void uploadBGRABufferToMapTexture(ByteBuffer colorBuffer, GpuTexture texture, TextureFormat internalFormat, int width, int height) {
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        if (!(texture instanceof class_10868)) {
            return;
        }
        class_10868 glTexture = (class_10868)texture;
        GlStateManager._activeTexture((int)33984);
        GlStateManager._bindTexture((int)glTexture.method_68427());
        GL11.glTexImage2D((int)3553, (int)0, (int)GlConst.toGlInternalId((TextureFormat)internalFormat), (int)width, (int)height, (int)0, (int)32993, (int)32821, (ByteBuffer)colorBuffer);
    }

    public static void downloadMapTextureToBGRABuffer(GpuTexture texture, ByteBuffer colorBuffer) {
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        if (!(texture instanceof class_10868)) {
            return;
        }
        class_10868 glTexture = (class_10868)texture;
        GlStateManager._activeTexture((int)33984);
        GlStateManager._bindTexture((int)glTexture.method_68427());
        GL11.glGetTexImage((int)3553, (int)0, (int)32993, (int)33639, (ByteBuffer)colorBuffer);
    }

    public static void copyTextureToBGRAPackBuffer(GpuTexture texture, GpuBuffer packBuffer, long packBufferOffset) {
        if (!(packBuffer instanceof class_10859)) {
            return;
        }
        class_10859 glBuffer = (class_10859)packBuffer;
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        class_10868 glTexture = (class_10868)texture;
        OpenGLException.checkGLError();
        GlStateManager._activeTexture((int)33984);
        GlStateManager._bindTexture((int)glTexture.method_68427());
        PixelBuffers.glBindBuffer(35051, ((IWorldMapGlBuffer)glBuffer).xaero_wm_getHandle());
        GL11.glGetTexImage((int)3553, (int)0, (int)32993, (int)32821, (long)packBufferOffset);
        PixelBuffers.glBindBuffer(35051, 0);
        OpenGLException.checkGLError();
    }

    public static void copyBGRAUnpackBufferToMapTexture(GpuBuffer unpackBuffer, GpuTexture texture, int level, TextureFormat internalFormat, int width, int height, int border, long pixels_buffer_offset) {
        if (!(unpackBuffer instanceof class_10859)) {
            return;
        }
        class_10859 glBuffer = (class_10859)unpackBuffer;
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        class_10868 glTexture = (class_10868)texture;
        GlStateManager._activeTexture((int)33984);
        GlStateManager._bindTexture((int)glTexture.method_68427());
        PixelBuffers.glBindBuffer(35052, ((IWorldMapGlBuffer)glBuffer).xaero_wm_getHandle());
        GL11.glTexImage2D((int)3553, (int)level, (int)GlConst.toGlInternalId((TextureFormat)internalFormat), (int)width, (int)height, (int)border, (int)32993, (int)32821, (long)pixels_buffer_offset);
        PixelBuffers.glBindBuffer(35052, 0);
    }

    public static void copyBGRAUnpackBufferToSubMapTexture(GpuBuffer unpackBuffer, GpuTexture texture, int level, int xOffset, int yOffset, int width, int height, long pixels_buffer_offset) {
        if (!(unpackBuffer instanceof class_10859)) {
            return;
        }
        class_10859 glBuffer = (class_10859)unpackBuffer;
        texture = Services.PLATFORM.getPlatformRenderDeviceUtil().getRealTexture(texture);
        class_10868 glTexture = (class_10868)texture;
        GlStateManager._activeTexture((int)33984);
        GlStateManager._bindTexture((int)glTexture.method_68427());
        PixelBuffers.glBindBuffer(35052, ((IWorldMapGlBuffer)glBuffer).xaero_wm_getHandle());
        GL11.glTexSubImage2D((int)3553, (int)level, (int)xOffset, (int)yOffset, (int)width, (int)height, (int)32993, (int)32821, (long)pixels_buffer_offset);
        PixelBuffers.glBindBuffer(35052, 0);
    }

    public static void fixMaxLod(GpuTexture glColorTexture, int levels) {
        OpenGlHelper.bindTexture(0, glColorTexture);
        GL11.glTexParameterf((int)3553, (int)33083, (float)levels);
    }
}

