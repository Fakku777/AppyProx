/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.class_2561
 */
package xaero.hud.minimap.info.widget;

import com.google.common.collect.Lists;
import net.minecraft.class_2561;
import xaero.hud.minimap.info.widget.InfoDisplayCycleWidgetFactory;
import xaero.hud.minimap.info.widget.InfoDisplayWidgetFactory;

public class InfoDisplayCommonWidgetFactories {
    public static final InfoDisplayCycleWidgetFactory<Boolean> OFF_ON = new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Boolean[]{false, true}), Lists.newArrayList((Object[])new class_2561[]{class_2561.method_43471((String)"gui.xaero_off"), class_2561.method_43471((String)"gui.xaero_on")}));
    public static final InfoDisplayWidgetFactory<Boolean> ALWAYS_ON = (x, y, w, h, infoDisplay) -> null;
}

