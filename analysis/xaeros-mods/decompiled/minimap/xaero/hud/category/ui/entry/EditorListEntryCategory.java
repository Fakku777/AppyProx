/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_2583
 *  net.minecraft.class_5250
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryTextWithAction;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListTextButtonEntry;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorSettingsNode;

public class EditorListEntryCategory<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, ?, ED>>
extends EditorListRootEntry {
    private static final CursorBox HELP_TOOLTIP = new CursorBox("gui.xaero_category_help2", class_2583.field_24360, true);
    private static final CursorBox PROTECTED_TOOLTIP = new CursorBox("gui.xaero_category_protected_category", class_2583.field_24360, true);
    private static final CursorBox UP_TOOLTIP = new CursorBox("gui.xaero_category_category_move_up", class_2583.field_24360, true);
    private static final CursorBox DOWN_TOOLTIP = new CursorBox("gui.xaero_category_category_move_down", class_2583.field_24360, true);

    public EditorListEntryCategory(int screenWidth, int index, GuiCategoryEditor.SettingRowList rowList, ConnectionLineType lineType, EditorCategoryNode<?, ?, ?> node, EditorCategoryNode<?, ?, ?> parent, Supplier<CursorBox> tooltipSupplier, boolean isFinalExpanded) {
        super(screenWidth, index, rowList, lineType, node);
        int subIndex = parent == null ? -1 : parent.getSubCategories().indexOf(node);
        EditorCategoryNode<?, ?, ?> dataCast = node;
        EditorCategoryNode<?, ?, ?> parentCast = parent;
        GuiCategoryEditor.SettingRowList rowListCast = rowList;
        boolean isCut = rowListCast.isCut(dataCast);
        Object currentCut = rowListCast.getCut();
        this.withSubEntry(this.getCategoryNameEntryFactory(dataCast, rowListCast, isCut, tooltipSupplier));
        EditorListRootEntry.CenteredEntryFactory pasteEntryFactory = this.getPasteEntryFactory(currentCut, isCut, dataCast, rowListCast);
        if (dataCast.isExpanded() || !dataCast.isMovable()) {
            if (rowListCast.hasCut()) {
                this.withSubEntry(pasteEntryFactory);
            }
            if (isFinalExpanded) {
                this.addHelpElement(HELP_TOOLTIP);
            }
            return;
        }
        if (!((EditorSettingsNode)dataCast.getSettingsNode()).getProtection()) {
            this.withSubEntry(this.getDuplicateEntryFactory(subIndex, parentCast, rowListCast));
        }
        if (rowListCast.hasCut()) {
            this.withSubEntry(pasteEntryFactory);
        }
        if (((EditorSettingsNode)dataCast.getSettingsNode()).getProtection()) {
            this.withSubEntry(this.getProtectedEntryFactory());
            return;
        }
        if (!rowListCast.hasCut()) {
            this.withSubEntry(this.getCutEntryFactory(dataCast, parentCast, rowListCast));
        }
        if (parent.getSubCategories().size() <= 1) {
            return;
        }
        this.withSubEntry(this.getPriorityEntryFactory(-1, parentCast, subIndex));
        this.withSubEntry(this.getPriorityEntryFactory(1, parentCast, subIndex));
    }

    private EditorListRootEntry.CenteredEntryFactory getCategoryNameEntryFactory(ED dataCast, GuiCategoryEditor.SettingRowList rowListCast, boolean isCut, Supplier<CursorBox> tooltipSupplier) {
        return (x, y, w, h, root) -> {
            Runnable action = isCut ? () -> rowListCast.pasteTo(dataCast) : dataCast.getExpandAction(rowListCast);
            EditorListEntryTextWithAction result = new EditorListEntryTextWithAction(x, y, w, h, this.index, this.rowList, this, action, tooltipSupplier);
            if (isCut) {
                result.setColor(-5636096);
                result.setHoverColor(-43691);
            }
            return result;
        };
    }

    private EditorListRootEntry.CenteredEntryFactory getPasteEntryFactory(ED currentCut, boolean isCut, ED dataCast, GuiCategoryEditor.SettingRowList rowListCast) {
        CursorBox pasteTooltip = this.getPasteTooltip(currentCut, isCut);
        if (pasteTooltip != null) {
            pasteTooltip.setAutoLinebreak(false);
        }
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x + 248, y + 2, this.index, this.rowList, "\u2190", -5592406, -1, 5, dataCast.getPasteAction(rowListCast), this, pasteTooltip);
    }

    private CursorBox getPasteTooltip(ED currentCut, boolean isCut) {
        if (currentCut == null) {
            return null;
        }
        if (isCut) {
            return new CursorBox("gui.xaero_category_paste_cancel", class_2583.field_24360, true);
        }
        class_5250 component = class_2561.method_43469((String)"gui.xaero_category_paste", (Object[])new Object[]{class_2561.method_43471((String)((EditorCategoryNode)currentCut).getDisplayName()), class_2561.method_43471((String)this.node.getDisplayName())});
        return new CursorBox((class_2561)component, true);
    }

    private EditorListRootEntry.CenteredEntryFactory getDuplicateEntryFactory(int subIndex, ED parentCast, GuiCategoryEditor.SettingRowList rowListCast) {
        class_5250 duplicateTooltipComponent = class_2561.method_43469((String)"gui.xaero_category_duplicate", (Object[])new Object[]{class_2561.method_43471((String)this.node.getDisplayName())});
        CursorBox duplicateTooltip = new CursorBox((class_2561)duplicateTooltipComponent, true);
        duplicateTooltip.setAutoLinebreak(false);
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x + 230, y + 2, this.index, this.rowList, "+", -5592406, -1, 5, parentCast.getDuplicateAction(subIndex, rowListCast), this, duplicateTooltip);
    }

    private EditorListRootEntry.CenteredEntryFactory getProtectedEntryFactory() {
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x - 24, y + 2, this.index, this.rowList, "!", -1644980, -171, 5, () -> false, this, PROTECTED_TOOLTIP);
    }

    private EditorListRootEntry.CenteredEntryFactory getCutEntryFactory(ED dataCast, ED parentCast, GuiCategoryEditor.SettingRowList rowListCast) {
        class_5250 cutTooltipComponent = class_2561.method_43469((String)"gui.xaero_category_cut", (Object[])new Object[]{class_2561.method_43471((String)this.node.getDisplayName())});
        CursorBox cutTooltip = new CursorBox((class_2561)cutTooltipComponent, true);
        cutTooltip.setAutoLinebreak(false);
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x + 248, y + 2, this.index, this.rowList, "\u2194", -5592406, -1, 5, dataCast.getCutAction(parentCast, rowListCast), this, cutTooltip);
    }

    private EditorListRootEntry.CenteredEntryFactory getPriorityEntryFactory(int direction, ED parentCast, int subIndex) {
        String label = direction < 0 ? "\u2191" : "\u2193";
        CursorBox tooltip = direction < 0 ? UP_TOOLTIP : DOWN_TOOLTIP;
        return (x, y, w, h, root) -> new EditorListTextButtonEntry(x - 32 + 8 * direction, y + 2, this.index, this.rowList, label, -5592406, -1, 5, parentCast.getMoveAction(subIndex, direction, this.rowList), this, tooltip);
    }

    @Override
    public String getMessage() {
        return "";
    }
}

