/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package xaero.map.element;

import net.minecraft.class_310;
import xaero.map.element.MapElementMenuHitbox;
import xaero.map.element.MenuOnlyElementReader;

public class MenuHitboxReader
extends MenuOnlyElementReader<MapElementMenuHitbox> {
    @Override
    public int getLeftSideLength(MapElementMenuHitbox element, class_310 mc) {
        return 0;
    }

    @Override
    public boolean isMouseOverMenuElement(MapElementMenuHitbox element, int menuX, int menuY, int mouseX, int mouseY, class_310 mc) {
        int hitboxMinX = menuX + element.getX();
        int hitboxMinY = menuY + element.getY();
        int hitboxMaxX = hitboxMinX + element.getW();
        int hitboxMaxY = hitboxMinY + element.getH();
        return mouseX >= hitboxMinX && mouseX < hitboxMaxX && mouseY >= hitboxMinY && mouseY < hitboxMaxY;
    }

    @Override
    public String getMenuName(MapElementMenuHitbox element) {
        return "";
    }

    @Override
    public int getMenuTextFillLeftPadding(MapElementMenuHitbox element) {
        return 0;
    }

    @Override
    public String getFilterName(MapElementMenuHitbox element) {
        return this.getMenuName(element);
    }

    @Override
    public int getRightClickTitleBackgroundColor(MapElementMenuHitbox element) {
        return 0;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return false;
    }
}

