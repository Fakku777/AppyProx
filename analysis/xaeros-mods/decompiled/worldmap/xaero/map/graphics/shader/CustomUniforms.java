/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 */
package xaero.map.graphics.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.HashMap;
import java.util.Map;
import xaero.map.graphics.shader.CustomUniform;

public class CustomUniforms {
    private static Map<RenderPipeline.UniformDescription, CustomUniform<?>> UNIFORMS = new HashMap();

    public static void register(CustomUniform<?> uniform) {
        if (UNIFORMS.containsKey(uniform.getDescription())) {
            throw new IllegalArgumentException("Custom uniform already registered: " + String.valueOf(uniform.getDescription()));
        }
        UNIFORMS.put(uniform.getDescription(), uniform);
    }

    public static GpuBufferSlice getUpdatedUniformBuffer(RenderPipeline.UniformDescription uniformDescription) {
        CustomUniform<?> uniform = UNIFORMS.get(uniformDescription);
        if (uniform == null) {
            return null;
        }
        return uniform.getBufferSlice();
    }

    public static boolean isCustom(RenderPipeline.UniformDescription uniform) {
        return UNIFORMS.containsKey(uniform);
    }

    public static void endFrame() {
        for (CustomUniform<?> uniform : UNIFORMS.values()) {
            uniform.endFrame();
        }
    }
}

