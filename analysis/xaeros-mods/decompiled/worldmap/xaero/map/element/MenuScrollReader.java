/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_310
 */
package xaero.map.element;

import net.minecraft.class_1074;
import net.minecraft.class_310;
import xaero.map.element.MapElementMenuScroll;
import xaero.map.element.MenuOnlyElementReader;

public class MenuScrollReader
extends MenuOnlyElementReader<MapElementMenuScroll> {
    @Override
    public int getLeftSideLength(MapElementMenuScroll element, class_310 mc) {
        return 9 + mc.field_1772.method_1727(class_1074.method_4662((String)element.getName(), (Object[])new Object[0]));
    }

    @Override
    public String getMenuName(MapElementMenuScroll element) {
        return class_1074.method_4662((String)element.getName(), (Object[])new Object[0]);
    }

    @Override
    public String getFilterName(MapElementMenuScroll element) {
        return this.getMenuName(element);
    }

    @Override
    public int getMenuTextFillLeftPadding(MapElementMenuScroll element) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(MapElementMenuScroll element) {
        return 0;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return false;
    }
}

