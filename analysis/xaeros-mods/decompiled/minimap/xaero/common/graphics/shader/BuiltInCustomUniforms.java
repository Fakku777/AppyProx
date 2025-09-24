/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  net.minecraft.class_10789
 *  org.joml.Vector2f
 */
package xaero.common.graphics.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_10789;
import org.joml.Vector2f;
import xaero.common.graphics.shader.BuiltInCustomUniformValueTypes;
import xaero.common.graphics.shader.CustomUniform;
import xaero.common.graphics.shader.CustomUniforms;

public class BuiltInCustomUniforms {
    public static final CustomUniform<Float> DISCARD_ALPHA = new CustomUniform<Float>(new RenderPipeline.UniformDescription("DiscardAlphaBlock", class_10789.field_60031), BuiltInCustomUniformValueTypes.FLOAT, 32);
    public static final CustomUniform<Vector2f> FRAME_SIZE = new CustomUniform<Vector2f>(new RenderPipeline.UniformDescription("FrameSizeBlock", class_10789.field_60031), BuiltInCustomUniformValueTypes.VEC_2F, 32);

    private static void registerAll() {
        CustomUniforms.register(DISCARD_ALPHA);
        CustomUniforms.register(FRAME_SIZE);
    }

    static {
        BuiltInCustomUniforms.registerAll();
    }
}

