/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_287
 *  org.joml.Matrix4f
 */
package xaero.hud.render.util;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_287;
import org.joml.Matrix4f;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;

public class MultiTextureRenderUtil {
    public static void prepareTexturedColoredRect(Matrix4f matrix, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, float r, float g, float b, float a, GpuTexture texture, MultiTextureRenderTypeRenderer renderer) {
        float f;
        float f1 = f = 1.0f / factor;
        float textureX0 = ((float)textureX + 0.0f) * f;
        float textureX1 = ((float)textureX + width) * f;
        float textureY0 = ((float)textureY + 0.0f) * f1;
        float textureY1 = ((float)textureY + theight) * f1;
        MultiTextureRenderUtil.prepareTexturedColoredRect(matrix, x, y, width, height, textureX0, textureX1, textureY0, textureY1, r, g, b, a, texture, renderer);
    }

    private static void prepareTexturedColoredRect(Matrix4f matrix, float x, float y, float width, float height, float textureX0, float textureX1, float textureY0, float textureY1, float r, float g, float b, float a, GpuTexture texture, MultiTextureRenderTypeRenderer renderer) {
        class_287 vertexBuffer = renderer.begin(texture);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + height, 0.0f).method_22915(r, g, b, a).method_22913(textureX0, textureY0);
        vertexBuffer.method_22918(matrix, x + width, y + height, 0.0f).method_22915(r, g, b, a).method_22913(textureX1, textureY0);
        vertexBuffer.method_22918(matrix, x + width, y + 0.0f, 0.0f).method_22915(r, g, b, a).method_22913(textureX1, textureY1);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + 0.0f, 0.0f).method_22915(r, g, b, a).method_22913(textureX0, textureY1);
    }

    public static void prepareTexturedRect(Matrix4f matrix, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, GpuTexture texture, MultiTextureRenderTypeRenderer renderer) {
        float f;
        float f1 = f = 1.0f / factor;
        float textureX0 = ((float)textureX + 0.0f) * f;
        float textureX1 = ((float)textureX + width) * f;
        float textureY0 = ((float)textureY + 0.0f) * f1;
        float textureY1 = ((float)textureY + theight) * f1;
        class_287 vertexBuffer = renderer.begin(texture);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + height, 0.0f).method_22913(textureX0, textureY0);
        vertexBuffer.method_22918(matrix, x + width, y + height, 0.0f).method_22913(textureX1, textureY0);
        vertexBuffer.method_22918(matrix, x + width, y + 0.0f, 0.0f).method_22913(textureX1, textureY1);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + 0.0f, 0.0f).method_22913(textureX0, textureY1);
    }
}

