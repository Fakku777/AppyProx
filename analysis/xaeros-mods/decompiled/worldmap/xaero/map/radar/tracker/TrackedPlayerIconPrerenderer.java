/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_10366
 *  net.minecraft.class_11278
 *  net.minecraft.class_1657
 *  net.minecraft.class_276
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  org.joml.Matrix4fStack
 */
package xaero.map.radar.tracker;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_10366;
import net.minecraft.class_11278;
import net.minecraft.class_1657;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Matrix4fStack;
import xaero.map.element.MapElementGraphics;
import xaero.map.graphics.ImprovedFramebuffer;
import xaero.map.graphics.TextureUtils;
import xaero.map.icon.XaeroIcon;
import xaero.map.icon.XaeroIconAtlas;
import xaero.map.misc.Misc;
import xaero.map.radar.tracker.PlayerTrackerIconRenderer;
import xaero.map.radar.tracker.PlayerTrackerMapElement;

public class TrackedPlayerIconPrerenderer {
    private ImprovedFramebuffer renderFramebuffer;
    private XaeroIconAtlas lastAtlas;
    private final PlayerTrackerIconRenderer renderer = new PlayerTrackerIconRenderer();
    private GpuBufferSlice orthoProjection;

    public void prerender(MapElementGraphics guiGraphics, XaeroIcon icon, class_1657 player, int iconWidth, class_2960 skinTextureLocation, PlayerTrackerMapElement<?> mapElement) {
        if (this.renderFramebuffer == null) {
            this.renderFramebuffer = new ImprovedFramebuffer(icon.getTextureAtlas().getWidth(), icon.getTextureAtlas().getWidth(), true);
            this.renderFramebuffer.closeColorTexture();
            this.renderFramebuffer.setColorTexture(null, null);
            class_11278 orthoProjectionCache = new class_11278("tracked player icon prerender", -1.0f, 1000.0f, true);
            this.orthoProjection = orthoProjectionCache.method_71092((float)this.renderFramebuffer.field_1480, (float)this.renderFramebuffer.field_1477);
        }
        guiGraphics.flush();
        this.renderFramebuffer.bindAsMainTarget(false);
        this.renderFramebuffer.setColorTexture(icon.getTextureAtlas());
        if (this.lastAtlas != icon.getTextureAtlas()) {
            TextureUtils.clearRenderTarget((class_276)this.renderFramebuffer, 0, 1.0f);
            this.lastAtlas = icon.getTextureAtlas();
        }
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.orthoProjection, (class_10366)class_10366.field_54954);
        Matrix4fStack shaderMatrixStack = RenderSystem.getModelViewStack();
        shaderMatrixStack.pushMatrix();
        shaderMatrixStack.identity();
        class_4587 matrixStack = guiGraphics.pose();
        matrixStack.method_22903();
        matrixStack.method_34426();
        matrixStack.method_46416((float)icon.getOffsetX(), (float)(this.renderFramebuffer.field_1477 - iconWidth - icon.getOffsetY()), 0.0f);
        matrixStack.method_46416((float)(iconWidth / 2), (float)(iconWidth / 2), 0.0f);
        matrixStack.method_22905(3.0f, 3.0f, 1.0f);
        guiGraphics.fill(-5, -5, 5, 5, -1);
        this.renderer.renderIcon(guiGraphics, player, skinTextureLocation);
        matrixStack.method_22909();
        guiGraphics.flush();
        class_310 mc = class_310.method_1551();
        Misc.minecraftOrtho(mc, false);
        shaderMatrixStack.popMatrix();
        this.renderFramebuffer.bindDefaultFramebuffer(mc);
    }
}

