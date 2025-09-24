/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  javax.annotation.Nonnull
 *  net.minecraft.class_1921
 *  net.minecraft.class_1921$class_4688
 *  net.minecraft.class_4668$class_4678
 *  net.minecraft.class_9801
 */
package xaero.map.graphics;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import javax.annotation.Nonnull;
import net.minecraft.class_1921;
import net.minecraft.class_4668;
import net.minecraft.class_9801;
import xaero.map.core.ICompositeRenderType;
import xaero.map.core.ICompositeState;
import xaero.map.render.util.ImmediateRenderUtil;

public class ImprovedCompositeRenderType
extends class_1921
implements ICompositeRenderType {
    private final class_1921 vanillaCompositeRenderType;
    private final RenderPipeline renderPipeline;
    private final class_1921.class_4688 compositeState;
    private final class_4668.class_4678 outputStateShard;

    public ImprovedCompositeRenderType(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, RenderPipeline renderPipeline, class_4668.class_4678 outputStateShard, class_1921.class_4688 compositeState, class_1921 vanillaCompositeRenderType) {
        super(name, bufferSize, affectsCrumbling, sortOnUpload, () -> ((class_1921)vanillaCompositeRenderType).method_23516(), () -> ((class_1921)vanillaCompositeRenderType).method_23518());
        this.renderPipeline = renderPipeline;
        this.outputStateShard = outputStateShard;
        this.compositeState = compositeState;
        this.vanillaCompositeRenderType = vanillaCompositeRenderType;
    }

    public void method_60895(@Nonnull class_9801 meshData) {
        this.method_23516();
        ImmediateRenderUtil.drawImmediateMeshData(meshData, this.renderPipeline, this.outputStateShard.method_68491());
        this.method_23518();
    }

    @Nonnull
    public VertexFormat method_23031() {
        return this.vanillaCompositeRenderType.method_23031();
    }

    @Nonnull
    public VertexFormat.class_5596 method_23033() {
        return this.vanillaCompositeRenderType.method_23033();
    }

    @Override
    public RenderPipeline xaero_wm_getRenderPipeline() {
        return this.renderPipeline;
    }

    @Override
    public ICompositeState xaero_wm_getState() {
        return (ICompositeState)this.compositeState;
    }
}

