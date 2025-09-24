/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_315
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_429
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_429;
import net.minecraft.class_437;
import xaero.common.gui.GuiSettings;

public class MyOptions
extends class_429 {
    private GuiSettings settingsScreen;
    private String buttonName;
    class_310 mc = class_310.method_1551();

    public MyOptions(String buttonName, GuiSettings settingsScreen, class_437 par1GuiScreen, class_315 par2GameSettings) {
        super(par1GuiScreen, par2GameSettings);
        this.buttonName = buttonName;
        this.settingsScreen = settingsScreen;
    }

    public void method_25426() {
        super.method_25426();
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)this.buttonName), b -> {
            this.mc.field_1690.method_1640();
            this.mc.method_1507((class_437)this.settingsScreen);
        }).method_46434(this.field_22789 / 2 - 100, this.field_22790 / 6 + 10, 200, 20).method_46431());
    }
}

