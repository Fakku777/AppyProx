/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_758$class_4596
 */
package xaero.map.render.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.reflect.Method;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_758;
import xaero.map.core.IGameRenderer;
import xaero.map.misc.Misc;

public class GuiRenderUtil {
    private static Method submitBlitMethod;

    public static void flushGUI() {
        IGameRenderer gameRenderer = (IGameRenderer)class_310.method_1551().field_1773;
        gameRenderer.xaero_wm_getGuiRenderer().method_70890(gameRenderer.xaero_wm_getFogRenderer().method_71109(class_758.class_4596.field_60101));
    }

    public static void submitBlit(class_332 guiGraphics, RenderPipeline pipeline, GpuTextureView texture, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color) {
        if (submitBlitMethod == null) {
            submitBlitMethod = Misc.getMethodReflection(class_332.class, "submitBlit", "method_70847", "(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;IIIIFFFFI)V", "submitBlit", RenderPipeline.class, GpuTextureView.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE);
        }
        Misc.getReflectMethodValue(guiGraphics, submitBlitMethod, pipeline, texture, x0, y0, x1, y1, Float.valueOf(u0), Float.valueOf(u1), Float.valueOf(v0), Float.valueOf(v1), color);
    }
}

