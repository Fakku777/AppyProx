/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 */
package xaero.common.core;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import xaero.common.core.ICompositeState;

public interface ICompositeRenderType {
    public RenderPipeline xaero_mm_getRenderPipeline();

    public ICompositeState xaero_mm_getState();
}

