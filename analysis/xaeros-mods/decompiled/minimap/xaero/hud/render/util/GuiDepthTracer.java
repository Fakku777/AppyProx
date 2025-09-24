/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  net.minecraft.class_10799
 *  net.minecraft.class_11231
 *  net.minecraft.class_11244
 *  net.minecraft.class_4588
 *  net.minecraft.class_8030
 *  org.jetbrains.annotations.Nullable
 */
package xaero.hud.render.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.class_10799;
import net.minecraft.class_11231;
import net.minecraft.class_11244;
import net.minecraft.class_4588;
import net.minecraft.class_8030;
import org.jetbrains.annotations.Nullable;

public class GuiDepthTracer
implements class_11244 {
    private final FloatConsumer listener;
    private final class_8030 bounds;

    public GuiDepthTracer(FloatConsumer listener, class_8030 bounds) {
        this.listener = listener;
        this.bounds = bounds;
    }

    public void method_70917(class_4588 vertexConsumer, float var2) {
        vertexConsumer.method_22912(0.0f, 0.0f, 0.0f).method_1336(0, 0, 0, 0);
        vertexConsumer.method_22912(0.0f, 0.0f, 0.0f).method_1336(0, 0, 0, 0);
        vertexConsumer.method_22912(0.0f, 0.0f, 0.0f).method_1336(0, 0, 0, 0);
        vertexConsumer.method_22912(0.0f, 0.0f, 0.0f).method_1336(0, 0, 0, 0);
        this.listener.accept(var2);
    }

    public RenderPipeline comp_4055() {
        return class_10799.field_56879;
    }

    public class_11231 comp_4056() {
        return class_11231.method_70899();
    }

    @Nullable
    public class_8030 comp_4069() {
        return null;
    }

    @Nullable
    public class_8030 comp_4274() {
        return this.bounds;
    }
}

