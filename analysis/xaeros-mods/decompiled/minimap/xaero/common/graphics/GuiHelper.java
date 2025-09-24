/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_4587
 *  org.joml.Matrix4f
 */
package xaero.common.graphics;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import org.joml.Matrix4f;
import xaero.common.graphics.CustomRenderTypes;
import xaero.hud.render.util.ImmediateRenderUtil;

public class GuiHelper {
    public static void blit(class_4587 pose, int x, int y, float u, float v, int w, int h) {
        GuiHelper.blit(pose, x, x + w, y, y + h, 0, w, h, u, v, 256, 256);
    }

    public static void blit(class_4587 pose, int x, int y, int z, float u, float v, int w, int h, int textureW, int textureH) {
        GuiHelper.blit(pose, x, x + w, y, y + h, z, w, h, u, v, textureW, textureH);
    }

    static void blit(class_4587 pose, int left, int right, int top, int bottom, int z, int uw, int vh, float u, float v, int textureW, int textureH) {
        GuiHelper.innerBlit(pose, left, right, top, bottom, z, (u + 0.0f) / (float)textureW, (u + (float)uw) / (float)textureW, (v + 0.0f) / (float)textureH, (v + (float)vh) / (float)textureH);
    }

    static void innerBlit(class_4587 pose, int left, int right, int top, int bottom, int z, float uLeft, float uRight, float vTop, float vBottom) {
        Matrix4f matrix4f = pose.method_23760().method_23761();
        class_287 bufferBuilder = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1585);
        bufferBuilder.method_22918(matrix4f, (float)left, (float)top, (float)z).method_22913(uLeft, vTop);
        bufferBuilder.method_22918(matrix4f, (float)left, (float)bottom, (float)z).method_22913(uLeft, vBottom);
        bufferBuilder.method_22918(matrix4f, (float)right, (float)bottom, (float)z).method_22913(uRight, vBottom);
        bufferBuilder.method_22918(matrix4f, (float)right, (float)top, (float)z).method_22913(uRight, vTop);
        ImmediateRenderUtil.drawImmediateMeshData(bufferBuilder.method_60794(), CustomRenderTypes.RP_POSITION_TEX_NO_ALPHA);
    }
}

