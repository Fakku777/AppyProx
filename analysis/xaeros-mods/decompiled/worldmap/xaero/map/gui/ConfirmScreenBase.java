/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_410
 *  net.minecraft.class_437
 */
package xaero.map.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_410;
import net.minecraft.class_437;
import xaero.map.graphics.TextureUtils;
import xaero.map.gui.IScreenBase;
import xaero.map.gui.dropdown.DropDownWidget;
import xaero.map.misc.Misc;
import xaero.map.render.util.GuiRenderUtil;

public class ConfirmScreenBase
extends class_410
implements IScreenBase {
    public class_437 parent;
    public class_437 escape;
    private boolean renderEscapeInBackground;
    protected boolean canSkipWorldRender;

    public ConfirmScreenBase(class_437 parent, class_437 escape, boolean renderEscapeInBackground, BooleanConsumer _callbackFunction, class_2561 _title, class_2561 _messageLine2) {
        super(_callbackFunction, _title, _messageLine2);
        this.parent = parent;
        this.escape = escape;
        this.renderEscapeInBackground = renderEscapeInBackground;
        this.canSkipWorldRender = true;
    }

    public ConfirmScreenBase(class_437 parent, class_437 escape, boolean renderEscapeInBackground, BooleanConsumer p_i232270_1_, class_2561 p_i232270_2_, class_2561 p_i232270_3_, class_2561 p_i232270_4_, class_2561 p_i232270_5_) {
        super(p_i232270_1_, p_i232270_2_, p_i232270_3_, p_i232270_4_, p_i232270_5_);
        this.parent = parent;
        this.escape = escape;
        this.renderEscapeInBackground = renderEscapeInBackground;
        this.canSkipWorldRender = true;
    }

    protected void onExit(class_437 screen) {
        this.field_22787.method_1507(screen);
    }

    protected void goBack() {
        this.onExit(this.parent);
    }

    public void method_25419() {
        this.onExit(this.escape);
    }

    public void renderEscapeScreen(class_332 guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        if (this.escape != null) {
            this.escape.method_47413(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
            GuiRenderUtil.flushGUI();
            TextureUtils.clearRenderTargetDepth(this.field_22787.method_1522(), 1.0f);
        }
    }

    public void method_25420(class_332 guiGraphics, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        if (this.renderEscapeInBackground) {
            this.renderEscapeScreen(guiGraphics, 0, 0, p_230430_4_);
        }
        super.method_25420(guiGraphics, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    public void method_25426() {
        super.method_25426();
        if (this.escape != null) {
            this.escape.method_25423(this.field_22787, this.field_22789, this.field_22790);
        }
    }

    @Override
    public boolean shouldSkipWorldRender() {
        return this.canSkipWorldRender && this.renderEscapeInBackground && Misc.screenShouldSkipWorldRender(this.escape, true);
    }

    @Override
    public void onDropdownOpen(DropDownWidget menu) {
    }

    @Override
    public void onDropdownClosed(DropDownWidget menu) {
    }
}

