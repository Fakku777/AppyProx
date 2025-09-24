/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  javax.annotation.Nullable
 *  net.minecraft.class_10017
 *  net.minecraft.class_1044
 *  net.minecraft.class_1297
 *  net.minecraft.class_2960
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_583
 *  net.minecraft.class_897
 */
package xaero.hud.minimap.radar.icon.creator.render.form.sprite;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_10017;
import net.minecraft.class_1044;
import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_583;
import net.minecraft.class_897;
import xaero.common.graphics.CustomRenderTypes;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.radar.icon.creator.RadarIconCreator;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelRenderTrace;
import xaero.hud.minimap.radar.icon.definition.form.sprite.RadarIconSpriteForm;
import xaero.hud.render.util.ImmediateRenderUtil;

public class RadarIconSpriteFormPrerenderer
implements IRadarIconFormPrerenderer {
    private final boolean flipped;
    private final boolean outlined;

    public RadarIconSpriteFormPrerenderer(boolean flipped, boolean outlined) {
        this.flipped = flipped;
        this.outlined = outlined;
    }

    @Override
    public boolean requiresEntityModel() {
        return false;
    }

    @Override
    public boolean isFlipped() {
        return this.flipped;
    }

    @Override
    public boolean isOutlined() {
        return this.outlined;
    }

    @Override
    public <S extends class_10017> boolean prerender(MinimapElementGraphics guiGraphics, class_897<?, ? super S> entityRenderer, S entityRenderState, @Nullable class_583<S> entityModel, class_1297 entity, @Nullable List<ModelRenderTrace> traceResult, RadarIconCreator.Parameters parameters) {
        class_4587 matrixStack = guiGraphics.pose();
        RadarIconSpriteForm spriteForm = (RadarIconSpriteForm)parameters.form;
        class_2960 sprite = spriteForm.getSpriteLocation();
        class_310.method_1551().field_1773.method_71114().method_71034(class_308.class_11274.field_60026);
        class_1044 texture = class_310.method_1551().method_1531().method_4619(sprite);
        GpuTextureView gpuTextureView = texture.method_71659();
        GpuTexture gpuTexture = texture.method_68004();
        gpuTexture.setTextureFilter(FilterMode.LINEAR, FilterMode.NEAREST, false);
        gpuTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
        int halfIcon = 32;
        matrixStack.method_46416((float)halfIcon, (float)halfIcon, 1.0f);
        float scale = parameters.scale;
        if (scale < 1.0f) {
            matrixStack.method_22905(scale, scale, 1.0f);
        }
        RenderSystem.setShaderTexture((int)0, (GpuTextureView)gpuTextureView);
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        ImmediateRenderUtil.texturedRect(matrixStack, (float)(-halfIcon), (float)(-halfIcon), 0, 64, 64.0f, 64.0f, -64.0f, 64.0f, CustomRenderTypes.RP_POSITION_TEX_ALPHA_NO_CULL);
        return true;
    }
}

