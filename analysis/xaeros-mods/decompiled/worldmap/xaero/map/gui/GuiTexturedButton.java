/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_10799
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_332
 *  net.minecraft.class_4185$class_4241
 */
package xaero.map.gui;

import java.util.function.Supplier;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import xaero.map.gui.CursorBox;
import xaero.map.gui.TooltipButton;

public class GuiTexturedButton
extends TooltipButton {
    protected int textureX;
    protected int textureY;
    protected int textureW;
    protected int textureH;
    protected final int factorW;
    protected final int factorH;
    protected class_2960 texture;

    public GuiTexturedButton(int x, int y, int w, int h, int textureX, int textureY, int textureW, int textureH, class_2960 texture, class_4185.class_4241 onPress, Supplier<CursorBox> tooltip, int factorW, int factorH) {
        super(x, y, w, h, (class_2561)class_2561.method_43470((String)""), onPress, tooltip);
        this.textureX = textureX;
        this.textureY = textureY;
        this.textureW = textureW;
        this.textureH = textureH;
        this.texture = texture;
        this.factorW = factorW;
        this.factorH = factorH;
    }

    public class_2561 method_25369() {
        if (this.tooltipSupplier != null) {
            return class_2561.method_43470((String)((CursorBox)this.tooltipSupplier.get()).getPlainText());
        }
        return super.method_25369();
    }

    public void method_48579(class_332 guiGraphics, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        int iconX = this.method_46426() + this.field_22758 / 2 - this.textureW / 2;
        int iconY = this.method_46427() + this.field_22759 / 2 - this.textureH / 2;
        int color = -12566464;
        if (this.field_22763) {
            if (this.field_22762) {
                --iconY;
                color = -1644826;
            } else {
                color = -197380;
            }
        }
        if (this.method_25370()) {
            guiGraphics.method_25294(iconX, iconY, iconX + this.textureW, iconY + this.textureH, 0x55FFFFFF);
        }
        guiGraphics.method_25291(class_10799.field_56883, this.texture, iconX, iconY, (float)this.textureX, (float)this.textureY, this.textureW, this.textureH, this.factorW, this.factorH, color);
    }
}

