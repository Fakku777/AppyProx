/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_1044
 *  net.minecraft.class_1058
 *  net.minecraft.class_10799
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5348
 *  net.minecraft.class_5481
 *  net.minecraft.class_9848
 */
package xaero.hud.minimap.element.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.function.Supplier;
import net.minecraft.class_1044;
import net.minecraft.class_1058;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_9848;
import xaero.hud.render.util.ImmediateRenderUtil;

public class MinimapElementGraphics {
    private final class_4587 poseStack;
    private final Supplier<class_4597.class_4598> vanillaBufferSourceSupplier;

    public MinimapElementGraphics(class_4587 poseStack, Supplier<class_4597.class_4598> vanillaBufferSourceSupplier) {
        this.poseStack = poseStack;
        this.vanillaBufferSourceSupplier = vanillaBufferSourceSupplier;
    }

    public final void fill(int x1, int y1, int x2, int y2, int color) {
        this.fill(class_10799.field_56879, x1, y1, x2, y2, color);
    }

    public final void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
        if (x1 < x2) {
            int x1bu = x1;
            x1 = x2;
            x2 = x1bu;
        }
        if (y1 < y2) {
            int y1bu = y1;
            y1 = y2;
            y2 = y1bu;
        }
        ImmediateRenderUtil.coloredRectangle(this.pose().method_23760().method_23761(), x1, y1, x2, y2, color, pipeline);
    }

    public final void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
        ImmediateRenderUtil.gradientRectangle(this.pose().method_23760().method_23761(), x1, y1, x2, y2, color1, color2);
    }

    public final void blit(class_1058 sprite, int x, int y, int width, int height, RenderPipeline renderPipeline) {
        class_1044 abstractTexture = class_310.method_1551().method_1531().method_4619(sprite.method_45852());
        if (abstractTexture == null) {
            return;
        }
        RenderSystem.setShaderTexture((int)0, (GpuTextureView)abstractTexture.method_71659());
        ImmediateRenderUtil.texturedRect(this.pose(), x, y, sprite.method_4594(), sprite.method_4575(), width, height, sprite.method_4577(), sprite.method_4593(), 1.0f, renderPipeline);
    }

    public final void blit(class_2960 texture, int x, int y, int u1, int v1, int width, int height, int u2, int v2, int textureSize, RenderPipeline renderPipeline) {
        class_1044 abstractTexture = class_310.method_1551().method_1531().method_4619(texture);
        if (abstractTexture == null) {
            return;
        }
        RenderSystem.setShaderTexture((int)0, (GpuTextureView)abstractTexture.method_71659());
        ImmediateRenderUtil.texturedRect(this.pose(), x, y, u1, v2, width, height, u2, v1, textureSize, renderPipeline);
    }

    public final void blit(GpuTextureView texture, int x, int y, int u1, int v1, int width, int height, int u2, int v2, int textureSize, RenderPipeline renderPipeline) {
        RenderSystem.setShaderTexture((int)0, (GpuTextureView)texture);
        ImmediateRenderUtil.texturedRect(this.pose(), x, y, u1, v2, width, height, u2, v1, textureSize, renderPipeline);
    }

    public final void drawCenteredString(class_327 font, String text, int x, int y, int color) {
        this.drawString(font, text, x - font.method_1727(text) / 2, y, color);
    }

    public final void drawCenteredString(class_327 font, class_2561 text, int x, int y, int color) {
        this.drawString(font, text, x - font.method_27525((class_5348)text) / 2, y, color);
    }

    public final void drawCenteredString(class_327 font, class_5481 charSequence, int x, int y, int color) {
        this.drawString(font, charSequence, x - font.method_30880(charSequence) / 2, y, color);
    }

    public final void drawString(class_327 font, String text, int x, int y, int color) {
        this.drawString(font, text, x, y, color, true);
    }

    public final void drawString(class_327 font, String text, int x, int y, int color, boolean shadow) {
        if (class_9848.method_61320((int)color) == 0) {
            return;
        }
        class_4597.class_4598 vanillaBufferSource = this.getBufferSource();
        font.method_27521(text, (float)x, (float)y, color, shadow, this.pose().method_23760().method_23761(), (class_4597)vanillaBufferSource, class_327.class_6415.field_33993, 0, 0xF000F0);
        vanillaBufferSource.method_37104();
    }

    public final void drawString(class_327 font, class_2561 text, int x, int y, int color) {
        this.drawString(font, text, x, y, color, true);
    }

    public final void drawString(class_327 font, class_2561 text, int x, int y, int color, boolean shadow) {
        if (class_9848.method_61320((int)color) == 0) {
            return;
        }
        class_4597.class_4598 vanillaBufferSource = this.getBufferSource();
        font.method_27522(text, (float)x, (float)y, color, shadow, this.pose().method_23760().method_23761(), (class_4597)vanillaBufferSource, class_327.class_6415.field_33993, 0, 0xF000F0);
        vanillaBufferSource.method_37104();
    }

    public final void drawString(class_327 font, class_5481 charSequence, int x, int y, int color) {
        this.drawString(font, charSequence, x, y, color, true);
    }

    public final void drawString(class_327 font, class_5481 charSequence, int x, int y, int color, boolean shadow) {
        if (class_9848.method_61320((int)color) == 0) {
            return;
        }
        class_4597.class_4598 vanillaBufferSource = this.getBufferSource();
        font.method_22942(charSequence, (float)x, (float)y, color, shadow, this.pose().method_23760().method_23761(), (class_4597)vanillaBufferSource, class_327.class_6415.field_33993, 0, 0xF000F0);
        vanillaBufferSource.method_37104();
    }

    public class_4597.class_4598 getBufferSource() {
        return this.vanillaBufferSourceSupplier.get();
    }

    public class_4587 pose() {
        return this.poseStack;
    }

    public void flush() {
        this.getBufferSource().method_22993();
    }
}

