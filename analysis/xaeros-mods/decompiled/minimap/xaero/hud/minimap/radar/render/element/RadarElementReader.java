/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 */
package xaero.hud.minimap.radar.render.element;

import net.minecraft.class_1297;
import net.minecraft.class_310;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;

public final class RadarElementReader
extends MinimapElementReader<class_1297, RadarRenderContext> {
    @Override
    public double getRenderX(class_1297 element, RadarRenderContext context, float partialTicks) {
        return EntityUtils.getEntityX(element, partialTicks);
    }

    @Override
    public double getRenderY(class_1297 element, RadarRenderContext context, float partialTicks) {
        return EntityUtils.getEntityY(element, partialTicks);
    }

    @Override
    public double getRenderZ(class_1297 element, RadarRenderContext context, float partialTicks) {
        return EntityUtils.getEntityZ(element, partialTicks);
    }

    @Override
    public boolean isHidden(class_1297 element, RadarRenderContext context) {
        return false;
    }

    @Override
    public int getInteractionBoxLeft(class_1297 element, RadarRenderContext context, float partialTicks) {
        return context.icon ? -16 : -6;
    }

    @Override
    public int getInteractionBoxRight(class_1297 element, RadarRenderContext context, float partialTicks) {
        return context.icon ? 16 : 6;
    }

    @Override
    public int getInteractionBoxTop(class_1297 element, RadarRenderContext context, float partialTicks) {
        return context.icon ? -16 : -6;
    }

    @Override
    public int getInteractionBoxBottom(class_1297 element, RadarRenderContext context, float partialTicks) {
        return context.icon ? 16 : 6;
    }

    @Override
    public int getRenderBoxLeft(class_1297 element, RadarRenderContext context, float partialTicks) {
        return -64;
    }

    @Override
    public int getRenderBoxRight(class_1297 element, RadarRenderContext context, float partialTicks) {
        return 64;
    }

    @Override
    public int getRenderBoxTop(class_1297 element, RadarRenderContext context, float partialTicks) {
        return -32;
    }

    @Override
    public int getRenderBoxBottom(class_1297 element, RadarRenderContext context, float partialTicks) {
        return 32;
    }

    @Override
    public int getLeftSideLength(class_1297 element, class_310 mc) {
        return 0;
    }

    @Override
    public String getMenuName(class_1297 element) {
        return "n/a";
    }

    @Override
    public String getFilterName(class_1297 element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(class_1297 element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(class_1297 element) {
        return 0;
    }

    @Override
    public float getBoxScale(MinimapElementRenderLocation location, class_1297 element, RadarRenderContext context) {
        return (location == MinimapElementRenderLocation.OVER_MINIMAP ? 0.5f : 1.0f) * (float)(context.icon ? context.iconScale : context.dotScale);
    }

    @Override
    public boolean isInteractable(MinimapElementRenderLocation location, class_1297 element) {
        return true;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }
}

