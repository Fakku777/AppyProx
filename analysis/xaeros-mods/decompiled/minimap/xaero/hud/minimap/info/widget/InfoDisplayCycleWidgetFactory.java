/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 */
package xaero.hud.minimap.info.widget;

import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.widget.InfoDisplayCycleButton;
import xaero.hud.minimap.info.widget.InfoDisplayWidgetFactory;

public class InfoDisplayCycleWidgetFactory<T>
implements InfoDisplayWidgetFactory<T> {
    private final List<T> values;
    private final List<class_2561> valueNames;

    public InfoDisplayCycleWidgetFactory(List<T> values, List<class_2561> valueNames) {
        this.values = values;
        this.valueNames = valueNames;
    }

    @Override
    public class_339 create(int x, int y, int w, int h, InfoDisplay<T> infoDisplay) {
        return InfoDisplayCycleButton.Builder.begin().setBounds(x, y, w, h).setInfoDisplay(infoDisplay).setValues(this.values, this.valueNames).build();
    }
}

