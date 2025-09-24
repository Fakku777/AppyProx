/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderPass$class_10884
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5595
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_1921
 *  net.minecraft.class_276
 *  net.minecraft.class_287
 *  net.minecraft.class_9799
 *  net.minecraft.class_9801
 */
package xaero.common.graphics.renderer.multitexture;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1921;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_9799;
import net.minecraft.class_9801;
import xaero.common.core.ICompositeRenderType;
import xaero.common.core.ICompositeState;
import xaero.common.graphics.CustomRenderTypes;
import xaero.hud.render.util.ImmediateRenderUtil;

public class MultiTextureRenderTypeRenderer {
    private static final String RENDER_PASS_NAME = "xaero multitexture render pass";
    private boolean used;
    private class_9799 sharedBuffer = new class_9799(16384);
    private class_287 currentBufferBuilder;
    private List<class_9801> buffersForDrawCalls = new ArrayList<class_9801>();
    private List<RenderPass.class_10884<MultiTextureRenderTypeRenderer>> drawCallBuilder;
    private List<GpuBuffer> immediateVertexBuffers;
    private ArrayList<GpuTexture> texturesForDrawCalls = new ArrayList();
    private Consumer<GpuTexture> textureBinder;
    private Consumer<GpuTexture> textureFinalizer;
    private GpuTexture prevTexture;
    private class_1921 renderType;

    MultiTextureRenderTypeRenderer() {
        this.drawCallBuilder = new ArrayList<RenderPass.class_10884<MultiTextureRenderTypeRenderer>>();
        this.immediateVertexBuffers = new ArrayList<GpuBuffer>();
    }

    void init(Consumer<GpuTexture> textureBinder, Consumer<GpuTexture> textureFinalizer, class_1921 renderType) {
        if (this.used) {
            throw new IllegalStateException("Multi-texture renderer already in use!");
        }
        if (!(renderType instanceof ICompositeRenderType)) {
            throw new IllegalArgumentException("Not a usable render type!");
        }
        this.used = true;
        this.textureBinder = textureBinder;
        this.textureFinalizer = textureFinalizer;
        this.prevTexture = null;
        this.renderType = renderType;
    }

    void draw() {
        if (!this.used) {
            throw new IllegalStateException("Multi-texture renderer is not in use!");
        }
        if (!this.texturesForDrawCalls.isEmpty()) {
            GpuTextureView colorTarget;
            Consumer<GpuTexture> textureBinder = this.textureBinder;
            Consumer<GpuTexture> textureFinalizer = this.textureFinalizer;
            boolean hasTextureFinalizer = textureFinalizer != null;
            this.endBuffer(this.currentBufferBuilder);
            RenderPipeline renderPipeline = ((ICompositeRenderType)this.renderType).xaero_mm_getRenderPipeline();
            ICompositeState compositeState = ((ICompositeRenderType)this.renderType).xaero_mm_getState();
            class_276 target = CustomRenderTypes.getOutputStateTarget(compositeState.xaero_mm_getOutputState());
            for (int i = 0; i < this.texturesForDrawCalls.size(); ++i) {
                GpuTexture texture = this.texturesForDrawCalls.get(i);
                class_9801 meshData = this.buffersForDrawCalls.get(i);
                this.drawCallBuilder.add(this.createDrawCall(i, meshData, texture, renderPipeline));
                meshData.close();
            }
            this.renderType.method_23516();
            GpuTextureView gpuTextureView = colorTarget = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : target.method_71639();
            GpuTextureView depthTarget = target.field_1478 ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : target.method_71640()) : null;
            try (RenderPass renderPass = ImmediateRenderUtil.createRenderPass(RENDER_PASS_NAME, renderPipeline, colorTarget, depthTarget);){
                renderPass.drawMultipleIndexed(this.drawCallBuilder, null, null, Collections.emptyList(), (Object)this);
            }
            if (hasTextureFinalizer) {
                for (int i = 0; i < this.texturesForDrawCalls.size(); ++i) {
                    GpuTexture texture = this.texturesForDrawCalls.get(i);
                    textureFinalizer.accept(texture);
                }
            }
            textureBinder.accept(null);
            this.renderType.method_23518();
        }
        this.drawCallBuilder.clear();
        this.texturesForDrawCalls.clear();
        this.buffersForDrawCalls.clear();
        this.used = false;
        this.renderType = null;
    }

    private RenderPass.class_10884<MultiTextureRenderTypeRenderer> createDrawCall(int index, class_9801 meshData, GpuTexture texture, RenderPipeline renderPipeline) {
        ByteBuffer indexBuffer = meshData.method_60821();
        if (indexBuffer != null) {
            throw new IllegalArgumentException();
        }
        RenderSystem.class_5590 sequentialBuffer = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)meshData.method_60822().comp_752());
        GpuBuffer gpuIndexBuffer = sequentialBuffer.method_68274(meshData.method_60822().comp_751());
        VertexFormat.class_5595 gpuIndexType = sequentialBuffer.method_31924();
        GpuBuffer gpuVertexBuffer = this.uploadImmediateVertexBuffer(index, meshData.method_60818());
        return new RenderPass.class_10884(0, gpuVertexBuffer, gpuIndexBuffer, gpuIndexType, 0, meshData.method_60822().comp_751(), (context, uu) -> this.textureBinder.accept(texture));
    }

    private GpuBuffer uploadImmediateVertexBuffer(int index, ByteBuffer vertexBuffer) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        while (index >= this.immediateVertexBuffers.size()) {
            this.immediateVertexBuffers.add(null);
        }
        GpuBuffer currentImmediateBuffer = this.immediateVertexBuffers.get(index);
        if (currentImmediateBuffer != null && currentImmediateBuffer.size < vertexBuffer.remaining()) {
            currentImmediateBuffer.close();
            currentImmediateBuffer = null;
            this.immediateVertexBuffers.set(index, null);
        }
        if (currentImmediateBuffer == null) {
            currentImmediateBuffer = gpuDevice.createBuffer(null, 46, vertexBuffer);
            this.immediateVertexBuffers.set(index, currentImmediateBuffer);
            return currentImmediateBuffer;
        }
        gpuDevice.createCommandEncoder().writeToBuffer(currentImmediateBuffer.slice(), vertexBuffer);
        return currentImmediateBuffer;
    }

    private void endBuffer(class_287 builder) {
        this.buffersForDrawCalls.add(builder.method_60794());
    }

    public class_287 begin(GpuTexture texture) {
        if (!this.used) {
            throw new IllegalStateException("Multi-texture renderer is not in use!");
        }
        if (texture == null) {
            throw new IllegalStateException("Attempted to use the multi-texture renderer with texture null!");
        }
        if (texture != this.prevTexture) {
            if (this.prevTexture != null) {
                this.endBuffer(this.currentBufferBuilder);
            }
            this.currentBufferBuilder = new class_287(this.sharedBuffer, this.renderType.method_23033(), this.renderType.method_23031());
            this.prevTexture = texture;
            this.texturesForDrawCalls.add(texture);
        }
        return this.currentBufferBuilder;
    }
}

