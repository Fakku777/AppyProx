/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui.widget.loader;

import java.io.IOException;
import java.util.Map;
import xaero.common.gui.GuiSettings;
import xaero.common.gui.widget.ClickAction;
import xaero.common.gui.widget.HoverAction;
import xaero.common.gui.widget.Widget;
import xaero.common.gui.widget.WidgetBuilder;
import xaero.common.patreon.GuiUpdateAll;

public abstract class WidgetLoader {
    public abstract Widget load(Map<String, String> var1) throws IOException;

    protected void commonLoad(WidgetBuilder builder, Map<String, String> parsedArgs) {
        String location = parsedArgs.get("location");
        String anchor_hor = parsedArgs.get("anchor_hor");
        String anchor_vert = parsedArgs.get("anchor_vert");
        String on_click = parsedArgs.get("on_click");
        String on_hover = parsedArgs.get("on_hover");
        String x = parsedArgs.get("x");
        String y = parsedArgs.get("y");
        String url = parsedArgs.get("url");
        String tooltip = parsedArgs.get("tooltip");
        if (location != null) {
            if (location.equals("SETTINGS")) {
                builder.setLocation(GuiSettings.class);
            } else if (location.equals("UPDATE")) {
                builder.setLocation(GuiUpdateAll.class);
            }
        }
        if (anchor_hor != null) {
            builder.setHorizontalAnchor(Float.parseFloat(anchor_hor));
        }
        if (anchor_vert != null) {
            builder.setVerticalAnchor(Float.parseFloat(anchor_vert));
        }
        if (on_click != null) {
            builder.setOnClick(ClickAction.valueOf(on_click));
        }
        if (on_hover != null) {
            builder.setOnHover(HoverAction.valueOf(on_hover));
        }
        if (x != null) {
            builder.setX(Integer.parseInt(x));
        }
        if (y != null) {
            builder.setY(Integer.parseInt(y));
        }
        if (url != null) {
            builder.setUrl(url.replace("%semi%", ";"));
        }
        if (tooltip != null) {
            builder.setTooltip(tooltip.replace("%semi%", ";"));
        }
    }
}

