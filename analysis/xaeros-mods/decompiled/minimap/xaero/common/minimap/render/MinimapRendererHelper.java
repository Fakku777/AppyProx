/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_1011$class_1012
 *  net.minecraft.class_10366
 *  net.minecraft.class_11278
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 */
package xaero.common.minimap.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.IntBuffer;
import net.minecraft.class_1011;
import net.minecraft.class_10366;
import net.minecraft.class_11278;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.GpuTextureAndView;
import xaero.hud.render.util.ImmediateRenderUtil;

public class MinimapRendererHelper {
    private class_11278 defaultOrthoCache;

    public void drawMyTexturedModalRect(class_4587 matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor) {
        this.drawMyTexturedModalRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, 0.0f);
    }

    public void drawMyTexturedModalRect(class_4587 matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, float discardAlpha) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, discardAlpha);
    }

    public void drawIconOutline(class_4587 matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor, float discardAlpha) {
        ImmediateRenderUtil.drawOutlineLayer(matrixStack, x, y, textureX, textureY, width, height, theight, factor, discardAlpha);
    }

    void drawTexturedElipseInsideRectangle(class_4587 matrixStack, double startAngle, int sides, float x, float y, int textureX, int textureY, float width, float widthFactor) {
        this.drawTexturedElipseInsideRectangle(matrixStack, startAngle, sides, x, y, textureX, textureY, width, width, widthFactor);
    }

    void drawTexturedElipseInsideRectangle(class_4587 matrixStack, double startAngle, int sides, float x, float y, int textureX, int textureY, float width, float theight, float widthFactor) {
        float f;
        float f1 = f = 1.0f / widthFactor;
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        class_289 tessellator = class_289.method_1348();
        float halfWidth = width / 2.0f;
        double centerX = x + halfWidth;
        double centerY = y + halfWidth;
        float centerU = ((float)textureX + halfWidth) * f;
        float centerV = (float)(((double)textureY + (double)theight * 0.5) * (double)f1);
        double fullCircle = Math.PI * 2;
        float prevVertexLocalX = 0.0f;
        float prevVertexLocalY = 0.0f;
        float prevVertexLocalV = 0.0f;
        class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27379, class_290.field_1585);
        for (int i = 0; i <= sides; ++i) {
            double angle = startAngle + (double)i / (double)sides * fullCircle;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            float vertexLocalX = halfWidth + (float)((double)halfWidth * sin);
            float vertexLocalY = (float)((double)halfWidth * (1.0 - cos));
            float vertexLocalV = (float)((double)theight * (1.0 - 0.5 * (1.0 - cos)));
            if (i > 0) {
                vertexBuffer.method_22918(matrix, x + vertexLocalX, y + vertexLocalY, 0.0f).method_22913(((float)textureX + vertexLocalX) * f, ((float)textureY + vertexLocalV) * f1);
                vertexBuffer.method_22918(matrix, x + prevVertexLocalX, y + prevVertexLocalY, 0.0f).method_22913(((float)textureX + prevVertexLocalX) * f, ((float)textureY + prevVertexLocalV) * f1);
                vertexBuffer.method_22918(matrix, (float)centerX, (float)centerY, 0.0f).method_22913(centerU, centerV);
            }
            prevVertexLocalX = vertexLocalX;
            prevVertexLocalY = vertexLocalY;
            prevVertexLocalV = vertexLocalV;
        }
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), CustomRenderTypes.RP_POSITION_TEX_NO_ALPHA);
    }

    void drawTexturedElipseInsideRectangleFrame(class_4587 matrixStack, boolean resetTexture, boolean reverseTexture, double startAngle, int startIndex, int endIndex, int sides, float thickness, float x, float y, int textureX, int textureY, float width, float twidth, float theight, int seamWidth, float widthFactor) {
        float f;
        float f1 = f = 1.0f / widthFactor;
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        class_289 tessellator = class_289.method_1348();
        float halfWidth = width / 2.0f;
        double fullCircle = Math.PI * 2;
        float prevVertexLocalX = 0.0f;
        float prevVertexLocalY = 0.0f;
        float prevVertexLocalOuterX = 0.0f;
        float prevVertexLocalOuterY = 0.0f;
        float prevSegmentTextureX = 0.0f;
        class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, class_290.field_1585);
        float outerRadius = halfWidth + thickness;
        float segmentOuterWidth = (float)(fullCircle / (double)sides * (double)outerRadius);
        startIndex = Math.max(Math.min(startIndex, sides), 0);
        endIndex = Math.max(Math.min(endIndex, sides), startIndex);
        int textureStartIndex = resetTexture ? (reverseTexture ? endIndex : startIndex) : 0;
        float seamThreshold = reverseTexture ? (float)seamWidth + segmentOuterWidth : (float)seamWidth;
        for (int i = startIndex; i <= endIndex; ++i) {
            double angle = startAngle + (double)i / (double)sides * fullCircle;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            float vertexLocalX = halfWidth + (float)((double)halfWidth * sin);
            float vertexLocalY = (float)((double)halfWidth * (1.0 - cos));
            float vertexLocalOuterX = halfWidth + (float)((double)outerRadius * sin);
            float vertexLocalOuterY = (float)((double)halfWidth - (double)outerRadius * cos);
            float segmentTextureStartX = textureX;
            float offsetX = Math.abs(segmentOuterWidth * (float)(i - textureStartIndex));
            if (offsetX >= seamThreshold) {
                segmentTextureStartX = textureX + seamWidth;
                if ((offsetX -= seamThreshold) >= twidth) {
                    offsetX %= twidth;
                }
            }
            float segmentTextureX = segmentTextureStartX + offsetX;
            if (i > startIndex) {
                vertexBuffer.method_22918(matrix, x + prevVertexLocalX, y + prevVertexLocalY, 0.0f).method_22913(prevSegmentTextureX * f, ((float)textureY + theight) * f1);
                vertexBuffer.method_22918(matrix, x + vertexLocalX, y + vertexLocalY, 0.0f).method_22913(segmentTextureX * f, ((float)textureY + theight) * f1);
                vertexBuffer.method_22918(matrix, x + vertexLocalOuterX, y + vertexLocalOuterY, 0.0f).method_22913(segmentTextureX * f, (float)textureY * f1);
                vertexBuffer.method_22918(matrix, x + prevVertexLocalOuterX, y + prevVertexLocalOuterY, 0.0f).method_22913(prevSegmentTextureX * f, (float)textureY * f1);
            }
            prevVertexLocalX = vertexLocalX;
            prevVertexLocalY = vertexLocalY;
            prevVertexLocalOuterX = vertexLocalOuterX;
            prevVertexLocalOuterY = vertexLocalOuterY;
            prevSegmentTextureX = segmentTextureX;
        }
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), CustomRenderTypes.RP_POSITION_TEX_NO_ALPHA);
    }

    public void addTexturedRectToExistingBuffer(Matrix4f matrix, class_4588 vertexBuffer, float x, float y, int u, int v, int w, int h) {
        float f = 0.00390625f;
        float normalizedU1 = (float)u * f;
        float normalizedV1 = (float)v * f;
        float normalizedU2 = (float)(u + w) * f;
        float normalizedV2 = (float)(v + h) * f;
        vertexBuffer.method_22918(matrix, x, y + (float)h, 0.0f).method_22913(normalizedU1, normalizedV2);
        vertexBuffer.method_22918(matrix, x + (float)w, y + (float)h, 0.0f).method_22913(normalizedU2, normalizedV2);
        vertexBuffer.method_22918(matrix, x + (float)w, y, 0.0f).method_22913(normalizedU2, normalizedV1);
        vertexBuffer.method_22918(matrix, x, y, 0.0f).method_22913(normalizedU1, normalizedV1);
    }

    public void drawMyColoredRect(class_4587 matrixStack, float x1, float y1, float x2, float y2) {
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        class_289 tessellator = class_289.method_1348();
        class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, class_290.field_1592);
        vertexBuffer.method_22918(matrix, x1, y2, 0.0f);
        vertexBuffer.method_22918(matrix, x2, y2, 0.0f);
        vertexBuffer.method_22918(matrix, x2, y1, 0.0f);
        vertexBuffer.method_22918(matrix, x1, y1, 0.0f);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), CustomRenderTypes.RP_POSITION);
    }

    public void addColoredLineToExistingBuffer(class_4587.class_4665 matrices, class_4588 vertexBuffer, float x1, float y1, float x2, float y2, float r, float g, float b, float a) {
        vertexBuffer.method_56824(matrices, x1, y1, 0.0f).method_22915(r, g, b, a).method_60831(matrices, x2 - x1, y2 - y1, 0.0f);
        vertexBuffer.method_56824(matrices, x2, y2, 0.0f).method_22915(r, g, b, a).method_60831(matrices, x2 - x1, y2 - y1, 0.0f);
    }

    public void drawMyColoredRect(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        ImmediateRenderUtil.coloredRectangle(matrix, x1, y1, x2, y2, color);
    }

    void bindTextureBuffer(IntBuffer image, int width, int height, GpuTextureAndView texture) {
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture.texture, image, class_1011.class_1012.field_4997, 0, 0, 0, 0, width, height);
        RenderSystem.setShaderTexture((int)0, (GpuTextureView)texture.view);
    }

    void putColor(int[] data, int x, int y, int red, int green, int blue, int size) {
        data[y * size + x] = 0xFF000000 | blue << 16 | green << 8 | red;
    }

    void gridOverlay(int[] result, int grid, int red, int green, int blue) {
        result[0] = (red * 3 + (grid >> 16 & 0xFF)) / 4;
        result[1] = (green * 3 + (grid >> 8 & 0xFF)) / 4;
        result[2] = (blue * 3 + (grid & 0xFF)) / 4;
    }

    void slimeOverlay(int[] result, int red, int green, int blue) {
        result[0] = (red + 82) / 2;
        result[1] = (green + 241) / 2;
        result[2] = (blue + 64) / 2;
    }

    public void defaultOrtho(class_276 framebuffer) {
        if (framebuffer != null) {
            if (this.defaultOrthoCache == null) {
                this.defaultOrthoCache = new class_11278("xaero default ortho", 1000.0f, 21000.0f, true);
            }
            GpuBufferSlice ortho = this.defaultOrthoCache.method_71092((float)framebuffer.field_1482, (float)framebuffer.field_1481);
            RenderSystem.setProjectionMatrix((GpuBufferSlice)ortho, (class_10366)class_10366.field_54954);
        }
    }

    public static void restoreDefaultShaderBlendState() {
    }
}

