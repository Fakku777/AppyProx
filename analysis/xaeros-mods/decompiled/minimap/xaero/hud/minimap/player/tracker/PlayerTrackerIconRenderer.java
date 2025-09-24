/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_1044
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1664
 *  net.minecraft.class_287
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_922
 */
package xaero.hud.minimap.player.tracker;

import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_1044;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1664;
import net.minecraft.class_287;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_922;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.hud.render.util.RenderBufferUtil;

public class PlayerTrackerIconRenderer {
    public void renderIcon(class_310 mc, MultiTextureRenderTypeRenderer renderer, class_4587 matrixStack, class_1657 player, class_2960 skinTextureLocation, float alpha) {
        boolean upsideDown = player != null && class_922.method_38563((class_1309)player);
        int textureY = 8 + (!upsideDown ? 8 : 0);
        int textureH = 8 * (!upsideDown ? -1 : 1);
        class_1044 texture = mc.method_1531().method_4619(skinTextureLocation);
        if (texture == null) {
            return;
        }
        GpuTexture textureId = texture.method_68004();
        class_287 bufferbuilder = renderer.begin(textureId);
        textureId.setTextureFilter(FilterMode.NEAREST, false);
        RenderBufferUtil.addTexturedColoredRect(matrixStack.method_23760().method_23761(), (class_4588)bufferbuilder, -4.0f, -4.0f, 8, textureY, 8, 8, 8, textureH, 1.0f, 1.0f, 1.0f, alpha, 64.0f);
        if (player == null) {
            return;
        }
        if (!player.method_7348(class_1664.field_7563)) {
            return;
        }
        textureY = 8 + (!upsideDown ? 8 : 0);
        textureH = 8 * (!upsideDown ? -1 : 1);
        RenderBufferUtil.addTexturedColoredRect(matrixStack.method_23760().method_23761(), (class_4588)bufferbuilder, -4.0f, -4.0f, 40, textureY, 8, 8, 8, textureH, 1.0f, 1.0f, 1.0f, alpha, 64.0f);
    }
}

