/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 */
package xaero.hud.compat.mods;

import net.minecraft.class_4587;
import xaero.hud.render.util.ImmediateRenderUtil;

public class ImmediatelyFastHelper {
    public static void triggerBatchingBuffersFlush(class_4587 matrixStack) {
        ImmediateRenderUtil.coloredRectangle(matrixStack.method_23760().method_23761(), 0.0f, 0.0f, 0.0f, 0.0f, 0);
    }
}

