/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5595
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10799
 *  net.minecraft.class_11219
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_9801
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package xaero.map.render.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.class_10799;
import net.minecraft.class_11219;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import xaero.map.graphics.shader.CustomUniforms;
import xaero.map.platform.Services;

public class ImmediateRenderUtil {
    private static Vector4f SHADER_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final String RENDER_PASS_NAME = "xaero wm render pass";

    public static void coloredRectangle(class_4587 matrices, float x1, float y1, float x2, float y2, int color) {
        ImmediateRenderUtil.coloredRectangle(matrices.method_23760().method_23761(), x1, y1, x2, y2, color);
    }

    public static void coloredRectangle(Matrix4f matrix, float x1, float y1, float x2, float y2, int color) {
        ImmediateRenderUtil.coloredRectangle(matrix, x1, y1, x2, y2, color, class_10799.field_56879);
    }

    public static void coloredRectangle(Matrix4f matrix, float x1, float y1, float x2, float y2, int color, RenderPipeline renderPipeline) {
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        class_289 tessellator = class_289.method_1348();
        class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, renderPipeline.getVertexFormat());
        vertexBuffer.method_22918(matrix, x1, y2, 0.0f).method_22915(r, g, b, a);
        vertexBuffer.method_22918(matrix, x2, y2, 0.0f).method_22915(r, g, b, a);
        vertexBuffer.method_22918(matrix, x2, y1, 0.0f).method_22915(r, g, b, a);
        vertexBuffer.method_22918(matrix, x1, y1, 0.0f).method_22915(r, g, b, a);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), renderPipeline);
    }

    public static void gradientRectangle(Matrix4f matrix, float x1, float y1, float x2, float y2, int color1, int color2) {
        float a1 = (float)(color1 >> 24 & 0xFF) / 255.0f;
        float r1 = (float)(color1 >> 16 & 0xFF) / 255.0f;
        float g1 = (float)(color1 >> 8 & 0xFF) / 255.0f;
        float b1 = (float)(color1 & 0xFF) / 255.0f;
        float a2 = (float)(color2 >> 24 & 0xFF) / 255.0f;
        float r2 = (float)(color2 >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(color2 >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(color2 & 0xFF) / 255.0f;
        class_289 tessellator = class_289.method_1348();
        class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        vertexBuffer.method_22918(matrix, x1, y2, 0.0f).method_22915(r2, g2, b2, a2);
        vertexBuffer.method_22918(matrix, x2, y2, 0.0f).method_22915(r2, g2, b2, a2);
        vertexBuffer.method_22918(matrix, x2, y1, 0.0f).method_22915(r1, g1, b1, a1);
        vertexBuffer.method_22918(matrix, x1, y1, 0.0f).method_22915(r1, g1, b1, a1);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), class_10799.field_56879);
    }

    public static void texturedRect(class_4587 matrixStack, float x, float y, int textureX, int textureY, float width, float height, float theight, float factor) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, theight, factor, class_10799.field_56883);
    }

    public static void texturedRect(class_4587 matrixStack, float x, float y, int textureX, int textureY, float width, float height, float textureH, float factor, RenderPipeline renderPipeline) {
        ImmediateRenderUtil.texturedRect(matrixStack, x, y, textureX, textureY, width, height, (float)textureX + width, (float)textureY + textureH, factor, renderPipeline);
    }

    public static void texturedRect(class_4587 matrixStack, float x, float y, float textureX1, float textureY1, float width, float height, float textureX2, float textureY2, float factor, RenderPipeline renderPipeline) {
        float f;
        float f1 = f = 1.0f / factor;
        Matrix4f matrix = matrixStack.method_23760().method_23761();
        class_289 tessellator = class_289.method_1348();
        class_287 vertexBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, renderPipeline.getVertexFormat());
        vertexBuffer.method_22918(matrix, x + 0.0f, y + height, 0.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f).method_22913(textureX1 * f, textureY2 * f1);
        vertexBuffer.method_22918(matrix, x + width, y + height, 0.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f).method_22913(textureX2 * f, textureY2 * f1);
        vertexBuffer.method_22918(matrix, x + width, y + 0.0f, 0.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f).method_22913(textureX2 * f, textureY1 * f1);
        vertexBuffer.method_22918(matrix, x + 0.0f, y + 0.0f, 0.0f).method_22915(1.0f, 1.0f, 1.0f, 1.0f).method_22913(textureX1 * f, textureY1 * f1);
        ImmediateRenderUtil.drawImmediateMeshData(vertexBuffer.method_60794(), renderPipeline);
    }

    public static void drawImmediateMeshData(class_9801 meshData, RenderPipeline renderPipeline) {
        ImmediateRenderUtil.drawImmediateMeshData(meshData, renderPipeline, class_310.method_1551().method_1522());
    }

    public static void drawImmediateMeshData(class_9801 meshData, RenderPipeline renderPipeline, class_276 target) {
        GpuTextureView colorTarget;
        VertexFormat.class_5595 gpuIndexType;
        GpuBuffer gpuIndexBuffer;
        ByteBuffer indexBuffer = meshData.method_60821();
        if (indexBuffer == null) {
            RenderSystem.class_5590 sequentialBuffer = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)meshData.method_60822().comp_752());
            gpuIndexBuffer = sequentialBuffer.method_68274(meshData.method_60822().comp_751());
            gpuIndexType = sequentialBuffer.method_31924();
        } else {
            gpuIndexBuffer = renderPipeline.getVertexFormat().uploadImmediateIndexBuffer(indexBuffer);
            gpuIndexType = meshData.method_60822().comp_753();
        }
        GpuBuffer gpuVertexBuffer = renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(meshData.method_60818());
        GpuTextureView gpuTextureView = colorTarget = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : target.method_71639();
        GpuTextureView depthTarget = target.field_1478 ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : target.method_71640()) : null;
        try (class_9801 class_98012 = meshData;
             RenderPass renderPass = ImmediateRenderUtil.createRenderPass(RENDER_PASS_NAME, renderPipeline, colorTarget, depthTarget);){
            renderPass.setIndexBuffer(gpuIndexBuffer, gpuIndexType);
            renderPass.setVertexBuffer(0, gpuVertexBuffer);
            renderPass.drawIndexed(0, 0, meshData.method_60822().comp_751(), 1);
        }
    }

    private static GpuBufferSlice getUpdatedDynamicUniforms() {
        return RenderSystem.getDynamicUniforms().method_71106((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)SHADER_COLOR, (Vector3fc)RenderSystem.getModelOffset(), (Matrix4fc)RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth());
    }

    private static void prepareRenderPass(RenderPass renderPass, RenderPipeline renderPipeline, GpuBufferSlice dynamicUniformsBuffer) {
        renderPass.setPipeline(renderPipeline);
        class_11219 scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
        if (scissorState.method_72091()) {
            renderPass.enableScissor(scissorState.method_72092(), scissorState.method_72093(), scissorState.method_72094(), scissorState.method_72095());
        }
        renderPass.setUniform("DynamicTransforms", dynamicUniformsBuffer);
        RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
        Services.PLATFORM.getPlatformRenderUtil().onPrepareRenderPass(renderPass);
        for (int textureIndex = 0; textureIndex < 12; ++textureIndex) {
            GpuTextureView gpuTexture = RenderSystem.getShaderTexture((int)textureIndex);
            if (gpuTexture == null) continue;
            renderPass.bindSampler("Sampler" + textureIndex, gpuTexture);
        }
        ImmediateRenderUtil.updateCustomUniforms(renderPipeline, renderPass);
    }

    private static void updateCustomUniforms(RenderPipeline renderPipeline) {
        ImmediateRenderUtil.updateCustomUniforms(renderPipeline, null);
    }

    private static void updateCustomUniforms(RenderPipeline renderPipeline, RenderPass renderPass) {
        for (RenderPipeline.UniformDescription uniform : renderPipeline.getUniforms()) {
            GpuBufferSlice valueBuffer;
            if (!CustomUniforms.isCustom(uniform) || (valueBuffer = CustomUniforms.getUpdatedUniformBuffer(uniform)) == null || renderPass == null) continue;
            renderPass.setUniform(uniform.name(), valueBuffer);
        }
    }

    public static RenderPass createRenderPass(String name, RenderPipeline renderPipeline, GpuTextureView colorTarget, GpuTextureView depthTarget) {
        GpuBufferSlice dynamicUniforms = ImmediateRenderUtil.getUpdatedDynamicUniforms();
        ImmediateRenderUtil.updateCustomUniforms(renderPipeline);
        RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> name, colorTarget, OptionalInt.empty(), depthTarget, OptionalDouble.empty());
        ImmediateRenderUtil.prepareRenderPass(renderPass, renderPipeline, dynamicUniforms);
        return renderPass;
    }

    public static void setShaderColor(float red, float green, float blue, float alpha) {
        SHADER_COLOR.set(red, green, blue, alpha);
    }
}

