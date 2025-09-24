/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget.init;

import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import xaero.common.gui.widget.ButtonWidget;
import xaero.common.gui.widget.Widget;
import xaero.common.gui.widget.WidgetScreen;
import xaero.common.gui.widget.init.WidgetInitializer;

public class ButtonWidgetInitializer
implements WidgetInitializer {
    @Override
    public void init(WidgetScreen screen, int width, int height, Widget widget) {
        ButtonWidget buttonWidget = (ButtonWidget)widget;
        screen.addButtonVisible((class_339)class_4185.method_46430((class_2561)class_2561.method_43470((String)buttonWidget.getButtonText()), b -> widget.getOnClick().clickHandler.onClick(this.toScreen(screen), widget)).method_46434(widget.getX(width), widget.getY(height), buttonWidget.getW(), buttonWidget.getH()).method_46431());
    }

    private class_437 toScreen(WidgetScreen screen) {
        Object result = screen.getScreen();
        if (result == screen) {
            return result;
        }
        throw new RuntimeException("Incorrect usage of " + String.valueOf(this.getClass()));
    }
}

