/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.minecraft.class_1921$class_4688
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  xaero.map.graphics.ImprovedCompositeRenderType
 */
package xaero.common.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_1921;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.ICompositeRenderType;
import xaero.common.core.ICompositeState;
import xaero.map.graphics.ImprovedCompositeRenderType;

@Mixin(value={ImprovedCompositeRenderType.class})
public class MixinWorldMapCompositeRenderType
implements ICompositeRenderType {
    @Shadow
    private RenderPipeline renderPipeline;
    @Shadow
    private class_1921.class_4688 compositeState;

    @Override
    public RenderPipeline xaero_mm_getRenderPipeline() {
        return this.renderPipeline;
    }

    @Override
    public ICompositeState xaero_mm_getState() {
        return (ICompositeState)this.compositeState;
    }
}

