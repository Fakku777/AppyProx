/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.node.EditorNode;

public class EditorListEntryWrapper
extends EditorListRootEntry {
    public EditorListEntryWrapper(EditorListRootEntry.CenteredEntryFactory wrappedFactory, int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorNode node) {
        this(wrappedFactory, screenWidth, index, rowList, lineType, node, null);
    }

    public EditorListEntryWrapper(EditorListRootEntry.CenteredEntryFactory wrappedFactory, int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorNode node, Supplier<CursorBox> helpTooltipSupplier) {
        super(screenWidth, index, rowList, lineType, node);
        this.withSubEntry(wrappedFactory);
        this.addHelpElement(helpTooltipSupplier);
    }

    @Override
    public String getMessage() {
        return "";
    }
}

