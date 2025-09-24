/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline$UniformDescription
 *  net.minecraft.class_10789
 *  net.minecraft.class_11280
 */
package xaero.map.graphics.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.class_10789;
import net.minecraft.class_11280;
import xaero.map.graphics.shader.CustomUniformValue;
import xaero.map.graphics.shader.CustomUniformValueType;

public class CustomUniform<T> {
    private final RenderPipeline.UniformDescription description;
    private final CustomUniformValueType<T> valueType;
    private final int initialBlockCount;
    private class_11280<CustomUniformValue<T>> storage;
    private CustomUniformValue<T> value;

    public CustomUniform(RenderPipeline.UniformDescription description, CustomUniformValueType<T> valueType, int initialBlockCount) {
        this.description = description;
        this.valueType = valueType;
        this.initialBlockCount = initialBlockCount;
    }

    public RenderPipeline.UniformDescription getDescription() {
        return this.description;
    }

    public String name() {
        return this.description.name();
    }

    public class_10789 type() {
        return this.description.type();
    }

    public T getValue() {
        return this.value == null ? null : (T)this.value.getValue();
    }

    public void setValue(T value) {
        Object currentValue;
        if (value == null) {
            throw new IllegalArgumentException();
        }
        Object t = currentValue = this.value == null ? null : (Object)this.value.getValue();
        if (currentValue != null && this.valueType.checkEquation(currentValue, value)) {
            return;
        }
        this.value = new CustomUniformValue<T>(value, this.valueType);
    }

    public GpuBufferSlice getBufferSlice() {
        if (this.storage == null) {
            this.storage = new class_11280(this.description.name(), this.valueType.getSize(), this.initialBlockCount);
        }
        return this.storage.method_71102(this.value);
    }

    public void endFrame() {
        if (this.storage != null) {
            this.storage.method_71100();
        }
    }
}

