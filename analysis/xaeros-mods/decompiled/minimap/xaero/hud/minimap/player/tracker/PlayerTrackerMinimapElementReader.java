/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_640
 */
package xaero.hud.minimap.player.tracker;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_640;
import xaero.hud.entity.EntityUtils;
import xaero.hud.minimap.element.render.MinimapElementReader;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElement;
import xaero.hud.minimap.player.tracker.PlayerTrackerMinimapElementRenderContext;

public class PlayerTrackerMinimapElementReader
extends MinimapElementReader<PlayerTrackerMinimapElement<?>, PlayerTrackerMinimapElementRenderContext> {
    @Override
    public boolean isHidden(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context) {
        return context.renderEntityDimId != element.getDimension() && context.mapDimId != element.getDimension();
    }

    @Override
    public double getRenderX(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        class_310 mc = class_310.method_1551();
        class_1657 clientPlayer = mc.field_1687.method_18470(element.getPlayerId());
        return clientPlayer == null ? element.getX() : EntityUtils.getEntityX((class_1297)clientPlayer, partialTicks);
    }

    @Override
    public double getRenderY(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        class_310 mc = class_310.method_1551();
        class_1657 clientPlayer = mc.field_1687.method_18470(element.getPlayerId());
        return clientPlayer == null ? element.getY() : EntityUtils.getEntityY((class_1297)clientPlayer, partialTicks);
    }

    @Override
    public double getRenderZ(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        class_310 mc = class_310.method_1551();
        class_1657 clientPlayer = mc.field_1687.method_18470(element.getPlayerId());
        return clientPlayer == null ? element.getZ() : EntityUtils.getEntityZ((class_1297)clientPlayer, partialTicks);
    }

    @Override
    public double getCoordinateScale(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, MinimapElementRenderInfo renderInfo) {
        if (element.getDimension() == renderInfo.renderEntityDimension) {
            return renderInfo.renderEntityDimensionScale;
        }
        return renderInfo.backgroundCoordinateScale;
    }

    @Override
    public int getInteractionBoxLeft(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -10;
    }

    @Override
    public int getInteractionBoxRight(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 10;
    }

    @Override
    public int getInteractionBoxTop(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -10;
    }

    @Override
    public int getInteractionBoxBottom(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 10;
    }

    @Override
    public int getRenderBoxLeft(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -20;
    }

    @Override
    public int getRenderBoxRight(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 20;
    }

    @Override
    public int getRenderBoxTop(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return -20;
    }

    @Override
    public int getRenderBoxBottom(PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context, float partialTicks) {
        return 20;
    }

    @Override
    public int getLeftSideLength(PlayerTrackerMinimapElement<?> element, class_310 mc) {
        class_640 info = class_310.method_1551().method_1562().method_2871(element.getPlayerId());
        if (info == null) {
            return 9;
        }
        return 9 + mc.field_1772.method_1727(info.method_2966().getName());
    }

    @Override
    public String getMenuName(PlayerTrackerMinimapElement<?> element) {
        class_640 info = class_310.method_1551().method_1562().method_2871(element.getPlayerId());
        if (info == null) {
            return String.valueOf(element.getPlayerId());
        }
        return info.method_2966().getName();
    }

    @Override
    public String getFilterName(PlayerTrackerMinimapElement<?> element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(PlayerTrackerMinimapElement<?> element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(PlayerTrackerMinimapElement<?> element) {
        return -11184641;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public float getBoxScale(MinimapElementRenderLocation location, PlayerTrackerMinimapElement<?> element, PlayerTrackerMinimapElementRenderContext context) {
        return context.iconScale;
    }

    @Override
    public boolean isInteractable(MinimapElementRenderLocation location, PlayerTrackerMinimapElement<?> element) {
        return true;
    }
}

