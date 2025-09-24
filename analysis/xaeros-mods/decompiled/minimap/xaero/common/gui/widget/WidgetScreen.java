/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_339
 *  net.minecraft.class_437
 */
package xaero.common.gui.widget;

import net.minecraft.class_339;
import net.minecraft.class_437;

public interface WidgetScreen {
    public <S extends class_437> S getScreen();

    public void addButtonVisible(class_339 var1);
}

