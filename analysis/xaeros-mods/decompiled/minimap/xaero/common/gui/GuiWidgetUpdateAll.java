/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 *  net.minecraft.class_339
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_437;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.widget.WidgetScreen;
import xaero.common.patreon.GuiUpdateAll;

public class GuiWidgetUpdateAll
extends GuiUpdateAll
implements WidgetScreen {
    private IXaeroMinimap modMain;

    public GuiWidgetUpdateAll(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public void method_25426() {
        super.method_25426();
        this.modMain.getWidgetScreenHandler().initialize(this, this.field_22789, this.field_22790);
    }

    public void method_25420(class_332 guiGraphics, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.method_25420(guiGraphics, p_render_1_, p_render_2_, p_render_3_);
        int mouseX = (int)(this.field_22787.field_1729.method_1603() * (double)this.field_22787.method_22683().method_4486() / (double)this.field_22787.method_22683().method_4480());
        int mouseY = (int)(this.field_22787.field_1729.method_1604() * (double)this.field_22787.method_22683().method_4502() / (double)this.field_22787.method_22683().method_4507());
        this.modMain.getWidgetScreenHandler().render(guiGraphics, this, this.field_22789, this.field_22790, mouseX, mouseY, this.field_22787.method_22683().method_4495());
    }

    public void method_25394(class_332 guiGraphics, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.method_25394(guiGraphics, p_render_1_, p_render_2_, p_render_3_);
        this.modMain.getWidgetScreenHandler().renderTooltips(guiGraphics, (class_437)this, this.field_22789, this.field_22790, p_render_1_, p_render_2_, this.field_22787.method_22683().method_4495());
    }

    @Override
    public void addButtonVisible(class_339 button) {
        this.method_37063((class_364)button);
    }

    @Override
    public <S extends class_437> S getScreen() {
        return (S)this;
    }

    public boolean method_25402(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        this.modMain.getWidgetScreenHandler().handleClick((class_437)this, this.field_22789, this.field_22790, (int)p_mouseClicked_1_, (int)p_mouseClicked_3_, this.field_22787.method_22683().method_4495());
        return super.method_25402(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }
}

