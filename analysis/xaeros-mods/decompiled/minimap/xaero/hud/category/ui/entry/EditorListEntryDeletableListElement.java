/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2583
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.class_2583;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryTextWithAction;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListTextButtonEntry;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSimpleDeletableWrapperNode;

public class EditorListEntryDeletableListElement
extends EditorListRootEntry {
    private final EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback;
    private final EditorNode parent;
    private static final CursorBox DELETE_TOOLTIP = new CursorBox("gui.xaero_category_delete_list_element", class_2583.field_24360, true);

    public EditorListEntryDeletableListElement(int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorSimpleDeletableWrapperNode<?> node, EditorNode parent, EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback, Supplier<CursorBox> tooltipSupplier) {
        super(screenWidth, index, rowList, lineType, node);
        this.deletionCallback = deletionCallback;
        this.parent = parent;
        this.withSubEntry((int x, int y, int w, int h, EditorListRootEntry root) -> new EditorListEntryTextWithAction(x, y, w, h, index, rowList, this, node.getExpandAction(rowList), tooltipSupplier));
        this.withSubEntry((int x, int y, int w, int h, EditorListRootEntry root) -> new EditorListTextButtonEntry(x - 24, y + 2, index, rowList, "x", -5636096, -43691, 5, () -> deletionCallback.delete(parent, node, rowList), this, DELETE_TOOLTIP));
    }

    @Override
    public boolean keyPressed(int i, int j, int k, boolean isRoot) {
        if (i == 261) {
            if (this.deletionCallback.delete(this.parent, (EditorSimpleDeletableWrapperNode)this.node, this.rowList)) {
                this.rowList.restoreScrollAfterUpdate();
                this.rowList.updateEntries();
            }
            return false;
        }
        return super.keyPressed(i, j, k, isRoot);
    }

    @Override
    public String getMessage() {
        return this.node.getDisplayName();
    }
}

