/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_310
 *  net.minecraft.class_3417
 *  net.minecraft.class_6880
 */
package xaero.hud.category.ui.entry;

import java.util.function.Supplier;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_6880;
import xaero.common.graphics.CursorBox;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWithIconAndText;
import xaero.hud.category.ui.entry.EditorListRootEntry;

public class EditorListEntryTextWithAction
extends EditorListEntryWithIconAndText {
    private final Runnable action;

    public EditorListEntryTextWithAction(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, EditorListRootEntry root, Runnable action, Supplier<CursorBox> tooltipSupplier) {
        this(entryX, entryY, entryW, entryH, index, rowList, root.node.getDisplayName(), root, action, tooltipSupplier);
    }

    public EditorListEntryTextWithAction(int entryX, int entryY, int entryW, int entryH, int index, GuiCategoryEditor.SettingRowList rowList, String text, EditorListRootEntry root, Runnable action, Supplier<CursorBox> tooltipSupplier) {
        super(entryX, entryY, entryW, entryH, index, rowList, text, root, tooltipSupplier);
        this.action = action;
    }

    @Override
    public boolean selectAction() {
        this.action.run();
        class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_47978((class_6880)class_3417.field_15015, (float)1.0f));
        return false;
    }
}

