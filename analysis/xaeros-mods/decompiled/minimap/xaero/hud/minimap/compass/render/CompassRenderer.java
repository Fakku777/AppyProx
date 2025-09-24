/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5348
 */
package xaero.hud.minimap.compass.render;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5348;
import xaero.common.HudMod;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.misc.Misc;
import xaero.hud.minimap.compass.render.CardinalDirection;
import xaero.hud.minimap.element.render.over.MinimapElementOverMapRendererHandler;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.render.util.RenderBufferUtil;

public class CompassRenderer {
    private final HudMod modMain;
    private final class_310 mc;
    private double[] partialDest;

    public CompassRenderer(HudMod modMain, class_310 mc) {
        this.modMain = modMain;
        this.mc = mc;
        this.partialDest = new double[2];
    }

    public void drawCompass(class_4587 matrixStack, int specW, int specH, double ps, double pc, double zoom, boolean circle, float minimapScale, boolean background, class_4597.class_4598 textRenderTypeBuffer, class_4588 nameBgBuilder) {
        if (this.modMain.getSettings().compassLocation == 0) {
            return;
        }
        WaypointColor defaultColor = WaypointColor.fromIndex(this.modMain.getSettings().compassColor);
        MinimapRendererHelper rendererHelper = this.modMain.getMinimap().getMinimapFBORenderer().getHelper();
        for (int i = 0; i < 4; ++i) {
            WaypointColor effectiveColor;
            double offX = (i & 1) * (i == 1 ? 10000 : -10000);
            double offY = (i + 1 & 1) * (i == 2 ? 10000 : -10000);
            matrixStack.method_22903();
            MinimapElementOverMapRendererHandler.translatePosition(matrixStack, specW, specH, specW, specH, ps, pc, offX, offY, zoom, circle, this.partialDest);
            matrixStack.method_46416(-1.0f, -1.0f, 0.0f);
            matrixStack.method_22905(minimapScale, minimapScale, 1.0f);
            class_2561 initials = CardinalDirection.values()[i].getInitials();
            int halfW = this.mc.field_1772.method_27525((class_5348)initials) / 2 - 1;
            WaypointColor waypointColor = effectiveColor = i == 0 ? WaypointColor.fromIndex(this.modMain.getSettings().getNorthCompassColor()) : defaultColor;
            if (background) {
                int addedFrame = halfW > 3 ? halfW - 3 : 0;
                RenderBufferUtil.addColoredRect(matrixStack.method_23760().method_23761(), nameBgBuilder, -4 - addedFrame, -4 - addedFrame, 9 + 2 * addedFrame, 9 + 2 * addedFrame, 0x90000000 | effectiveColor.getHex() & 0xFFFFFF);
            }
            Misc.drawNormalText(matrixStack, initials, (float)(-halfW + 1), -2.0f, effectiveColor.getHex(), false, textRenderTypeBuffer);
            matrixStack.method_46416(0.0f, 0.0f, 1.0f);
            Misc.drawNormalText(matrixStack, initials, (float)(-halfW), -3.0f, -1, false, textRenderTypeBuffer);
            matrixStack.method_22909();
        }
        matrixStack.method_46416(0.0f, 0.0f, 2.0f);
    }
}

