/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 */
package xaero.map.core;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import xaero.map.core.ICompositeState;

public interface ICompositeRenderType {
    public RenderPipeline xaero_wm_getRenderPipeline();

    public ICompositeState xaero_wm_getState();
}

