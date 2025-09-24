/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10799
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1664
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_922
 */
package xaero.map.radar.tracker;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.class_10799;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1664;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_922;
import xaero.map.element.MapElementGraphics;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.TextureUtils;
import xaero.map.render.util.ImmediateRenderUtil;

public class PlayerTrackerIconRenderer {
    public void renderIcon(MapElementGraphics guiGraphics, class_1657 player, class_2960 skinTextureLocation) {
        class_4587 matrixStack = guiGraphics.pose();
        boolean upsideDown = player != null && class_922.method_38563((class_1309)player);
        int textureY = 8 + (upsideDown ? 8 : 0);
        int textureH = 8 * (upsideDown ? -1 : 1);
        TextureUtils.setTexture(0, skinTextureLocation);
        class_287 bufferbuilder = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, CustomRenderTypes.POSITION_COLOR_TEX);
        MapRenderHelper.blitIntoExistingBuffer(matrixStack.method_23760().method_23761(), (class_4588)bufferbuilder, -4.0f, -4.0f, 8, textureY, 8, 8, 8, textureH, 1.0f, 1.0f, 1.0f, 1.0f, 64, 64);
        if (player != null && player.method_7348(class_1664.field_7563)) {
            textureY = 8 + (upsideDown ? 8 : 0);
            textureH = 8 * (upsideDown ? -1 : 1);
            MapRenderHelper.blitIntoExistingBuffer(matrixStack.method_23760().method_23761(), (class_4588)bufferbuilder, -4.0f, -4.0f, 40, textureY, 8, 8, 8, textureH, 1.0f, 1.0f, 1.0f, 1.0f, 64, 64);
        }
        ImmediateRenderUtil.drawImmediateMeshData(bufferbuilder.method_60794(), CustomRenderTypes.RP_POSITION_COLOR_TEX);
    }

    public void renderIconGUI(class_332 guiGraphics, class_1657 player, class_2960 skinTextureLocation) {
        boolean upsideDown = player != null && class_922.method_38563((class_1309)player);
        int textureY = 8 + (upsideDown ? 8 : 0);
        int textureH = 8 * (upsideDown ? -1 : 1);
        guiGraphics.method_25293(class_10799.field_56883, skinTextureLocation, -4, -4, 8.0f, (float)textureY, 8, 8, 8, textureH, 64, 64, -1);
        if (player != null && player.method_7348(class_1664.field_7563)) {
            textureY = 8 + (upsideDown ? 8 : 0);
            textureH = 8 * (upsideDown ? -1 : 1);
            guiGraphics.method_25293(class_10799.field_56883, skinTextureLocation, -4, -4, 40.0f, (float)textureY, 8, 8, 8, textureH, 64, 64, -1);
        }
    }
}

