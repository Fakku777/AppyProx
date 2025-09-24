/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_339
 *  net.minecraft.class_3417
 *  net.minecraft.class_357
 *  net.minecraft.class_4264
 *  net.minecraft.class_6880
 *  org.joml.Matrix3x2fStack
 *  org.joml.Matrix3x2fc
 *  org.joml.Vector3f
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.class_1074;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3417;
import net.minecraft.class_357;
import net.minecraft.class_4264;
import net.minecraft.class_6880;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.joml.Vector3f;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListEntryWithRootReference;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.widget.EditorTextField;

public class EditorListEntryWidget
extends EditorListEntryWithRootReference {
    protected class_339 widget;
    private boolean widgetPressed;

    public EditorListEntryWidget(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorListRootEntry root, class_339 widget, Supplier<CursorBox> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, root, tooltipSupplier);
        this.widget = widget;
    }

    /*
     * Ignored method signature, as it can't be verified against descriptor
     */
    @Override
    public boolean mouseClicked(GuiCategoryEditor.SettingRowList.Entry entry, double relativeMouseX, double relativeMouseY, int i) {
        boolean result = super.mouseClicked(entry, relativeMouseX, relativeMouseY, i);
        if (result) {
            return true;
        }
        if (this.widget instanceof class_4264) {
            return false;
        }
        if (!this.widget.method_25405(relativeMouseX, relativeMouseY)) {
            return false;
        }
        this.widgetPressed = true;
        return this.widget.method_25402(relativeMouseX, relativeMouseY, i);
    }

    @Override
    public boolean mouseReleased(double relativeMouseX, double relativeMouseY, int i) {
        if (this.widgetPressed) {
            this.widget.method_25406(relativeMouseX, relativeMouseY, i);
        }
        this.widgetPressed = false;
        super.mouseReleased(relativeMouseX, relativeMouseY, i);
        return false;
    }

    @Override
    public boolean mouseDragged(double relativeMouseX, double relativeMouseY, int i, double f, double g) {
        if (this.widgetPressed && this.widget.method_25403(relativeMouseX, relativeMouseY, i, f, g)) {
            return true;
        }
        return super.mouseDragged(relativeMouseX, relativeMouseY, i, f, g);
    }

    @Override
    public boolean mouseScrolled(double relativeMouseX, double relativeMouseY, double f, double g) {
        if (this.widget.method_25405(relativeMouseX, relativeMouseY) && this.widget.method_25401(relativeMouseX, relativeMouseY, f, g)) {
            return true;
        }
        return super.mouseScrolled(relativeMouseX, relativeMouseY, f, g);
    }

    @Override
    public void mouseMoved(double relativeMouseX, double relativeMouseY) {
        this.widget.method_16014(relativeMouseX, relativeMouseY);
        super.mouseMoved(relativeMouseX, relativeMouseY);
    }

    @Override
    public boolean keyPressed(int i, int j, int k, boolean isRoot) {
        if (this.widget.method_25404(i, j, k)) {
            return true;
        }
        return super.keyPressed(i, j, k, isRoot);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if (this.widget.method_16803(i, j, k)) {
            return true;
        }
        return super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.widget.method_25400(c, i)) {
            return true;
        }
        return super.charTyped(c, i);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public String getNarration() {
        return super.getNarration();
    }

    @Override
    public String getHoverNarration() {
        return this.getNarration();
    }

    @Override
    public String getMessage() {
        return this.widget.method_25369().getString();
    }

    @Override
    public String getNarrationMessage() {
        if (this.widget instanceof EditorTextField) {
            return ((EditorTextField)this.widget).method_25360().getString();
        }
        if (this.widget instanceof class_357) {
            return class_1074.method_4662((String)"gui.narrate.slider", (Object[])new Object[]{this.getMessage()});
        }
        return class_1074.method_4662((String)"gui.narrate.button", (Object[])new Object[]{this.getMessage()});
    }

    @Override
    public EditorListEntry render(class_332 guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, class_327 font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        EditorListEntry result = super.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX, relativeMouseY, isMouseOver, partialTicks, font, globalMouseX, globalMouseY, includesSelected, isRoot);
        Matrix3x2fStack poseStack = guiGraphics.method_51448();
        Vector3f widgetPos = new Vector3f((float)this.widget.method_46426(), (float)this.widget.method_46427(), 1.0f);
        widgetPos.mul((Matrix3x2fc)poseStack);
        int xBU = this.widget.method_46426();
        int yBU = this.widget.method_46427();
        this.widget.method_46421((int)widgetPos.x());
        this.widget.method_46419((int)widgetPos.y());
        poseStack.pushMatrix();
        poseStack.identity();
        this.widget.method_25394(guiGraphics, globalMouseX, globalMouseY, partialTicks);
        poseStack.popMatrix();
        this.widget.method_46421(xBU);
        this.widget.method_46419(yBU);
        return this.widgetPressed ? null : result;
    }

    @Override
    public void setFocused(boolean bl) {
        if (this.widget.field_22763 && this.widget.field_22764 && this.widget.method_25370() != bl) {
            this.widget.method_25365(bl);
        }
        super.setFocused(bl);
    }

    @Override
    protected boolean selectAction() {
        if (!(this.widget instanceof class_4264) || !this.widget.field_22763) {
            return false;
        }
        ((class_4264)this.widget).method_25306();
        class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_15015, (float)1.0f));
        return false;
    }
}

