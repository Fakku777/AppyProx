/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_339
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.hud.category.ui.entry.EditorListRootEntry;

public class EditorListEntryExpandingOptions
extends EditorListEntryWidget {
    public EditorListEntryExpandingOptions(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorListRootEntry root, class_339 widget, Supplier<String> messageSupplier, Supplier<CursorBox> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, root, widget, tooltipSupplier);
        if (messageSupplier == null) {
            return;
        }
        String optionTypeName = messageSupplier.get();
        if (!root.node.isExpanded()) {
            widget.method_25355((class_2561)class_2561.method_43470((String)optionTypeName));
            return;
        }
        widget.method_25355((class_2561)class_2561.method_43469((String)"gui.xaero_category_expanded_options", (Object[])new Object[]{optionTypeName}));
    }
}

