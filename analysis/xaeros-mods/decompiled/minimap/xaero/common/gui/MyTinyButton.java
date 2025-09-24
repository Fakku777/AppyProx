/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_4185
 *  net.minecraft.class_4185$class_4241
 */
package xaero.common.gui;

import net.minecraft.class_2561;
import net.minecraft.class_4185;
import xaero.common.graphics.CursorBox;
import xaero.common.settings.ModOptions;

public class MyTinyButton
extends class_4185 {
    private final ModOptions modOptions;
    private CursorBox tooltip;

    public MyTinyButton(int par1, int par2, class_2561 par4Str, class_4185.class_4241 onPress) {
        this(null, par1, par2, par4Str, onPress);
    }

    public MyTinyButton(CursorBox tooltip, int par1, int par2, class_2561 par4Str, class_4185.class_4241 onPress) {
        this(tooltip, par1, par2, null, par4Str, onPress);
    }

    public MyTinyButton(int par1, int par2, int par3, int par4, class_2561 par6Str, class_4185.class_4241 onPress) {
        super(par1, par2, par3, par4, par6Str, onPress, field_40754);
        this.modOptions = null;
    }

    public MyTinyButton(int par1, int par2, ModOptions par4EnumOptions, class_2561 par5Str, class_4185.class_4241 onPress) {
        this(null, par1, par2, par4EnumOptions, par5Str, onPress);
    }

    public MyTinyButton(CursorBox tooltip, int par1, int par2, ModOptions par4EnumOptions, class_2561 par5Str, class_4185.class_4241 onPress) {
        super(par1, par2, 75, 20, par5Str, onPress, field_40754);
        this.modOptions = par4EnumOptions;
        this.tooltip = tooltip;
    }

    public ModOptions returnModOptions() {
        return this.modOptions;
    }

    public CursorBox getMyTooltip() {
        return this.tooltip;
    }
}

