/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  net.minecraft.class_10789
 */
package xaero.map.graphics.shader;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_10789;
import xaero.map.graphics.shader.BuiltInCustomUniformValueTypes;
import xaero.map.graphics.shader.CustomUniform;
import xaero.map.graphics.shader.CustomUniforms;

public class BuiltInCustomUniforms {
    public static final CustomUniform<Float> BRIGHTNESS = new CustomUniform<Float>(new RenderPipeline.UniformDescription("BrightnessBlock", class_10789.field_60031), BuiltInCustomUniformValueTypes.FLOAT, 32);
    public static final CustomUniform<Integer> WITH_LIGHT = new CustomUniform<Integer>(new RenderPipeline.UniformDescription("WithLightBlock", class_10789.field_60031), BuiltInCustomUniformValueTypes.INT, 32);

    private static void registerAll() {
        CustomUniforms.register(BRIGHTNESS);
        CustomUniforms.register(WITH_LIGHT);
    }

    static {
        BuiltInCustomUniforms.registerAll();
    }
}

