/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 */
package xaero.hud.render.util;

import net.minecraft.class_4588;
import org.joml.Matrix4f;

public class RenderBufferUtil {
    public static void addColoredRect(Matrix4f matrix, class_4588 vertexBuffer, float x, float y, int w, int h, int color) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        RenderBufferUtil.addColoredRect(matrix, vertexBuffer, x, y, w, h, r, g, b, a);
    }

    public static void addColoredRect(Matrix4f matrix, class_4588 vertexBuffer, float x, float y, int w, int h, float r, float g, float b, float a) {
        vertexBuffer.method_22918(matrix, x, y + (float)h, 0.0f).method_22915(r, g, b, a);
        vertexBuffer.method_22918(matrix, x + (float)w, y + (float)h, 0.0f).method_22915(r, g, b, a);
        vertexBuffer.method_22918(matrix, x + (float)w, y, 0.0f).method_22915(r, g, b, a);
        vertexBuffer.method_22918(matrix, x, y, 0.0f).method_22915(r, g, b, a);
    }

    public static void addTexturedColoredRect(Matrix4f matrix, class_4588 vertexBuffer, float x, float y, int u, int v, int w, int h, float r, float g, float b, float a, float factor) {
        RenderBufferUtil.addTexturedColoredRect(matrix, vertexBuffer, x, y, u, v, w, h, w, h, r, g, b, a, factor);
    }

    public static void addTexturedColoredRect(Matrix4f matrix, class_4588 vertexBuffer, float x, float y, int u, int v, int w, int h, int tw, int th, float r, float g, float b, float a, float factor) {
        float f = 1.0f / factor;
        float normalizedU1 = (float)u * f;
        float normalizedV1 = (float)v * f;
        float normalizedU2 = (float)(u + tw) * f;
        float normalizedV2 = (float)(v + th) * f;
        vertexBuffer.method_22918(matrix, x, y + (float)h, 0.0f).method_22915(r, g, b, a).method_22913(normalizedU1, normalizedV1);
        vertexBuffer.method_22918(matrix, x + (float)w, y + (float)h, 0.0f).method_22915(r, g, b, a).method_22913(normalizedU2, normalizedV1);
        vertexBuffer.method_22918(matrix, x + (float)w, y, 0.0f).method_22915(r, g, b, a).method_22913(normalizedU2, normalizedV2);
        vertexBuffer.method_22918(matrix, x, y, 0.0f).method_22915(r, g, b, a).method_22913(normalizedU1, normalizedV2);
    }
}

