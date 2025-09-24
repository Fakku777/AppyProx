/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  net.minecraft.class_10789
 *  xaero.common.graphics.shader.CustomUniform
 */
package xaero.map.mods.minimap.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_10789;
import xaero.common.graphics.shader.CustomUniform;

public class CustomUniformWrapper<T>
extends CustomUniform<T> {
    private final xaero.map.graphics.shader.CustomUniform<T> worldMapUniform;

    public CustomUniformWrapper(xaero.map.graphics.shader.CustomUniform<T> worldMapUniform) {
        super(worldMapUniform.getDescription(), null, 1);
        this.worldMapUniform = worldMapUniform;
    }

    public RenderPipeline.UniformDescription getDescription() {
        return this.worldMapUniform.getDescription();
    }

    public String name() {
        return this.worldMapUniform.name();
    }

    public class_10789 type() {
        return this.worldMapUniform.type();
    }

    public T getValue() {
        return this.worldMapUniform.getValue();
    }

    public void setValue(T value) {
        this.worldMapUniform.setValue(value);
    }

    public GpuBufferSlice getBufferSlice() {
        return this.worldMapUniform.getBufferSlice();
    }
}

