/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.minecraft.class_1921$class_4688
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.common.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_1921;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.ICompositeRenderType;
import xaero.common.core.ICompositeState;

@Mixin(targets={"net.minecraft.client.renderer.RenderType$CompositeRenderType"})
public class MixinCompositeRenderType
implements ICompositeRenderType {
    @Shadow
    private RenderPipeline field_56922;
    @Shadow
    private class_1921.class_4688 field_21403;

    @Override
    public RenderPipeline xaero_mm_getRenderPipeline() {
        return this.field_56922;
    }

    @Override
    public ICompositeState xaero_mm_getState() {
        return (ICompositeState)this.field_21403;
    }
}

