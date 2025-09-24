/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_10366
 *  net.minecraft.class_11278
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  org.joml.Matrix4fStack
 */
package xaero.map.region.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_10366;
import net.minecraft.class_11278;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Matrix4fStack;
import xaero.map.exception.OpenGLException;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.GpuTextureAndView;
import xaero.map.graphics.ImprovedFramebuffer;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.TextureUtils;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.map.misc.Misc;

public class BranchTextureRenderer {
    private ImprovedFramebuffer renderFBO;
    private GpuTextureAndView glEmptyTexture;
    private class_4587 matrixStack = new class_4587();
    private GpuBufferSlice projectionMatrix;
    private MultiTextureRenderTypeRendererProvider rendererProvider;

    public BranchTextureRenderer() {
        class_11278 orthoProjectionCache = new class_11278("branch renderer", -1.0f, 1.0f, true);
        this.projectionMatrix = orthoProjectionCache.method_71092(64.0f, 64.0f);
        this.rendererProvider = new MultiTextureRenderTypeRendererProvider(1);
    }

    public GpuTextureAndView getEmptyTexture() {
        this.ensureRenderTarget();
        return this.glEmptyTexture;
    }

    private void ensureRenderTarget() {
        if (this.renderFBO == null) {
            this.renderFBO = new ImprovedFramebuffer(64, 64, false);
            this.glEmptyTexture = new GpuTextureAndView(this.renderFBO.method_30277(), this.renderFBO.method_71639());
            TextureUtils.clearRenderTarget((class_276)this.renderFBO, -16777216);
        }
    }

    public void render(GpuTextureAndView destTexture, GpuTextureAndView srcTextureTopLeft, GpuTextureAndView srcTextureTopRight, GpuTextureAndView srcTextureBottomLeft, GpuTextureAndView srcTextureBottomRight, class_276 defaultFramebuffer, boolean justAllocated) {
        this.ensureRenderTarget();
        this.renderFBO.bindAsMainTarget(true);
        this.renderFBO.setColorTexture(destTexture.texture, destTexture.view);
        OpenGLException.checkGLError();
        Matrix4fStack shaderMatrixStack = RenderSystem.getModelViewStack();
        shaderMatrixStack.pushMatrix();
        shaderMatrixStack.identity();
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.projectionMatrix, (class_10366)class_10366.field_54954);
        if (justAllocated) {
            TextureUtils.clearRenderTarget((class_276)this.renderFBO, -16777216);
        }
        MultiTextureRenderTypeRenderer renderer = this.rendererProvider.getRenderer(MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.MAP_BRANCH);
        if (srcTextureTopLeft != null) {
            this.renderCorner(srcTextureTopLeft, 0, 0, renderer);
        }
        if (srcTextureTopRight != null) {
            this.renderCorner(srcTextureTopRight, 1, 0, renderer);
        }
        if (srcTextureBottomLeft != null) {
            this.renderCorner(srcTextureBottomLeft, 0, 1, renderer);
        }
        if (srcTextureBottomRight != null) {
            this.renderCorner(srcTextureBottomRight, 1, 1, renderer);
        }
        this.rendererProvider.draw(renderer);
        OpenGLException.checkGLError(false, "updating a map branch texture");
        shaderMatrixStack.popMatrix();
        class_310 mc = class_310.method_1551();
        Misc.minecraftOrtho(mc, false);
        this.renderFBO.bindDefaultFramebuffer(mc);
        OpenGLException.checkGLError();
    }

    private boolean renderCorner(GpuTextureAndView srcTexture, int cornerX, int cornerY, MultiTextureRenderTypeRenderer renderer) {
        int xOffset = cornerX * 32;
        int yOffset = (1 - cornerY) * 32;
        MapRenderHelper.renderBranchUpdate(srcTexture.texture, xOffset, yOffset, 32.0f, 32.0f, 0, 64, 64.0f, -64.0f, 64.0f, 64.0f, renderer);
        return false;
    }

    public GpuTextureAndView getGlEmptyTexture() {
        return this.glEmptyTexture;
    }
}

