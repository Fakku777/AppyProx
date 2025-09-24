/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.class_1074;
import net.minecraft.class_327;
import net.minecraft.class_332;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListEntryWithIcon;
import xaero.hud.category.ui.entry.EditorListRootEntry;

public class EditorListEntryWithIconAndText
extends EditorListEntryWithIcon {
    protected String text;
    protected int color;
    protected int hoverColor;

    public EditorListEntryWithIconAndText(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, String text, EditorListRootEntry root, Supplier<CursorBox> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, 0, 0, 0, 0, root, tooltipSupplier);
        this.text = class_1074.method_4662((String)text.replaceAll("%", "%%"), (Object[])new Object[0]);
        this.color = -5592406;
        this.hoverColor = -1;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setHoverColor(int hoverColor) {
        this.hoverColor = hoverColor;
    }

    public int getColor() {
        return this.color;
    }

    public int getHoverColor() {
        return this.hoverColor;
    }

    @Override
    public EditorListEntry render(class_332 guiGraphics, int index, int rowWidth, int rowHeight, int relativeMouseX, int relativeMouseY, boolean isMouseOver, float partialTicks, class_327 font, int globalMouseX, int globalMouseY, boolean includesSelected, boolean isRoot) {
        EditorListEntry result = super.render(guiGraphics, index, rowWidth, rowHeight, relativeMouseX, relativeMouseY, isMouseOver, partialTicks, font, globalMouseX, globalMouseY, includesSelected, isRoot);
        int textColor = isMouseOver ? this.getHoverColor() : this.getColor();
        guiGraphics.method_25303(font, this.text, 4, 8, textColor);
        return result;
    }

    @Override
    public String getMessage() {
        return this.text;
    }
}

