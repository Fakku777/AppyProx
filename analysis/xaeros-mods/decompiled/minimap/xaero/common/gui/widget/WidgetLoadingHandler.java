/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui.widget;

import java.util.HashMap;
import xaero.common.gui.widget.Widget;
import xaero.common.gui.widget.WidgetScreenHandler;
import xaero.common.gui.widget.WidgetType;
import xaero.common.gui.widget.loader.WidgetLoader;
import xaero.common.patreon.Patreon;
import xaero.hud.minimap.MinimapLogs;

public class WidgetLoadingHandler {
    private static int CURRENT_VERSION = 1;
    private WidgetScreenHandler handler;

    public WidgetLoadingHandler(WidgetScreenHandler destination) {
        this.handler = destination;
    }

    public void loadWidget(String serialized) {
        Widget widget = null;
        String[] args = serialized.split(";");
        HashMap<String, String> parsedArgs = new HashMap<String, String>();
        for (String arg : args) {
            int splitIndex = arg.indexOf(58);
            if (splitIndex == -1) continue;
            String parameter = arg.substring(0, splitIndex);
            String value = arg.substring(splitIndex + 1);
            parsedArgs.put(parameter, value);
        }
        try {
            String min_version = (String)parsedArgs.remove("min_version");
            String max_version = (String)parsedArgs.remove("max_version");
            if (min_version != null && CURRENT_VERSION < Integer.parseInt(min_version) || max_version != null && CURRENT_VERSION > Integer.parseInt(max_version)) {
                return;
            }
            String min_patronage = (String)parsedArgs.remove("min_patronage");
            String max_patronage = (String)parsedArgs.remove("max_patronage");
            if (min_patronage != null && Patreon.getOnlineWidgetLevel() < Integer.parseInt(min_patronage) || max_patronage != null && Patreon.getOnlineWidgetLevel() > Integer.parseInt(max_patronage)) {
                return;
            }
            WidgetType type = WidgetType.valueOf((String)parsedArgs.remove("type"));
            WidgetLoader loader = type.widgetLoader;
            widget = loader.load(parsedArgs);
            this.handler.addWidget(widget);
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("suppressed exception", t);
        }
    }
}

