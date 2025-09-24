/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.class_10366
 *  net.minecraft.class_10799
 *  net.minecraft.class_11278
 *  net.minecraft.class_276
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  org.joml.Matrix4fStack
 */
package xaero.map.mods.gui;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_10366;
import net.minecraft.class_10799;
import net.minecraft.class_11278;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Matrix4fStack;
import xaero.map.element.MapElementGraphics;
import xaero.map.exception.OpenGLException;
import xaero.map.graphics.ImprovedFramebuffer;
import xaero.map.graphics.TextureUtils;
import xaero.map.icon.XaeroIcon;
import xaero.map.icon.XaeroIconAtlas;
import xaero.map.icon.XaeroIconAtlasManager;
import xaero.map.misc.Misc;
import xaero.map.render.util.ImmediateRenderUtil;

public class WaypointSymbolCreator {
    private static final int PREFERRED_ATLAS_WIDTH = 1024;
    private static final int ICON_WIDTH = 64;
    public static final class_2960 minimapTextures = class_2960.method_60655((String)"xaerobetterpvp", (String)"gui/guis.png");
    public static final int white = -1;
    private class_310 mc = class_310.method_1551();
    private XaeroIcon deathSymbolTexture;
    private final Map<String, XaeroIcon> charSymbols = new HashMap<String, XaeroIcon>();
    private XaeroIconAtlasManager iconManager;
    private ImprovedFramebuffer atlasRenderFramebuffer;
    private XaeroIconAtlas lastAtlas;
    private class_11278 orthoProjectionCache;

    public XaeroIcon getDeathSymbolTexture(MapElementGraphics guiGraphics) {
        if (this.deathSymbolTexture == null) {
            this.createDeathSymbolTexture(guiGraphics);
        }
        return this.deathSymbolTexture;
    }

    private void createDeathSymbolTexture(MapElementGraphics guiGraphics) {
        this.deathSymbolTexture = this.createCharSymbol(guiGraphics, true, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public XaeroIcon getSymbolTexture(MapElementGraphics guiGraphics, String c) {
        XaeroIcon icon;
        Map<String, XaeroIcon> map = this.charSymbols;
        synchronized (map) {
            icon = this.charSymbols.get(c);
        }
        if (icon == null) {
            icon = this.createCharSymbol(guiGraphics, false, c);
        }
        return icon;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private XaeroIcon createCharSymbol(MapElementGraphics guiGraphics, boolean death, String c) {
        if (this.iconManager == null) {
            int maxTextureSize = RenderSystem.getDevice().getMaxTextureSize();
            int atlasTextureSize = Math.min(maxTextureSize, 1024) / 64 * 64;
            this.atlasRenderFramebuffer = new ImprovedFramebuffer(atlasTextureSize, atlasTextureSize, true);
            this.atlasRenderFramebuffer.closeColorTexture();
            OpenGLException.checkGLError();
            this.atlasRenderFramebuffer.setColorTexture(null, null);
            this.iconManager = new XaeroIconAtlasManager(64, atlasTextureSize, new ArrayList<XaeroIconAtlas>());
            this.orthoProjectionCache = new class_11278("waypoint symbol creator", -1.0f, 1000.0f, true);
        }
        XaeroIconAtlas atlas = this.iconManager.getCurrentAtlas();
        XaeroIcon icon = atlas.createIcon();
        guiGraphics.flush();
        this.atlasRenderFramebuffer.bindAsMainTarget(false);
        this.atlasRenderFramebuffer.setColorTexture(atlas);
        if (this.lastAtlas != atlas) {
            TextureUtils.clearRenderTarget((class_276)this.atlasRenderFramebuffer, 0, 1.0f);
            this.lastAtlas = atlas;
        }
        GpuBufferSlice ortho = this.orthoProjectionCache.method_71092((float)this.atlasRenderFramebuffer.field_1480, (float)this.atlasRenderFramebuffer.field_1477);
        RenderSystem.setProjectionMatrix((GpuBufferSlice)ortho, (class_10366)class_10366.field_54954);
        Matrix4fStack shaderMatrixStack = RenderSystem.getModelViewStack();
        shaderMatrixStack.pushMatrix();
        shaderMatrixStack.identity();
        class_4587 matrixStack = guiGraphics.pose();
        matrixStack.method_22903();
        matrixStack.method_34426();
        matrixStack.method_46416((float)icon.getOffsetX(), (float)(this.atlasRenderFramebuffer.field_1477 - 64 - icon.getOffsetY()), 0.0f);
        matrixStack.method_46416(2.0f, 2.0f, 0.0f);
        if (!death) {
            matrixStack.method_22905(3.0f, 3.0f, 1.0f);
            guiGraphics.drawString(this.mc.field_1772, c, 0, 0, -1);
        } else {
            matrixStack.method_22905(3.0f, 3.0f, 1.0f);
            ImmediateRenderUtil.setShaderColor(0.243f, 0.243f, 0.243f, 1.0f);
            guiGraphics.blit(minimapTextures, 1, 1, 0, 78, 9, 9, 256, class_10799.field_56883);
            ImmediateRenderUtil.setShaderColor(0.988f, 0.988f, 0.988f, 1.0f);
            guiGraphics.blit(minimapTextures, 0, 0, 0, 78, 9, 9, 256, class_10799.field_56883);
            ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        matrixStack.method_22909();
        guiGraphics.flush();
        Misc.minecraftOrtho(this.mc, false);
        shaderMatrixStack.popMatrix();
        this.atlasRenderFramebuffer.bindDefaultFramebuffer(this.mc);
        if (death) {
            this.deathSymbolTexture = icon;
        } else {
            Map<String, XaeroIcon> map = this.charSymbols;
            synchronized (map) {
                this.charSymbols.put(c, icon);
            }
        }
        return icon;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetChars() {
        Map<String, XaeroIcon> map = this.charSymbols;
        synchronized (map) {
            this.charSymbols.clear();
        }
        this.lastAtlas = null;
        this.deathSymbolTexture = null;
        if (this.iconManager != null) {
            this.iconManager.clearAtlases();
            this.atlasRenderFramebuffer.setColorTexture(null, null);
        }
    }
}

