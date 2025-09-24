/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_350$class_351
 *  net.minecraft.class_364
 *  net.minecraft.class_410
 *  net.minecraft.class_4185
 *  net.minecraft.class_4280
 *  net.minecraft.class_4280$class_4281
 *  net.minecraft.class_437
 *  net.minecraft.class_6381
 *  net.minecraft.class_6382
 *  net.minecraft.class_8016
 *  net.minecraft.class_8023
 *  net.minecraft.class_8023$class_8024
 *  net.minecraft.class_8028
 *  org.joml.Matrix3x2fStack
 *  xaero.hud.category.ui.GuiCategoryEditor$SettingRowList.Entry
 */
package xaero.hud.category.ui;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_350;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_4280;
import net.minecraft.class_437;
import net.minecraft.class_6381;
import net.minecraft.class_6382;
import net.minecraft.class_8016;
import net.minecraft.class_8023;
import net.minecraft.class_8028;
import org.joml.Matrix3x2fStack;
import xaero.common.IXaeroMinimap;
import xaero.common.graphics.CursorBox;
import xaero.common.gui.ScreenBase;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.ui.EditorCategoryNodeConverter;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntry;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSettingsNode;

public abstract class GuiCategoryEditor<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, SD, ED>, CB extends ObjectCategory.Builder<C, CB>, SD extends EditorSettingsNode<?>, SDB extends EditorSettingsNode.Builder<SD, SDB>, EDB extends EditorCategoryNode.Builder<C, ED, SD, SDB, EDB>>
extends ScreenBase {
    private static final int FRAME_TOP_SIZE = 32;
    private static final int FRAME_BOTTOM_SIZE = 48;
    public static final int ROW_HEIGHT = 24;
    public static final int ROW_WIDTH = 220;
    private SettingRowList rowList;
    private final EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter;
    private ED editorData;
    protected ED cutCategory;
    protected ED cutCategorySuper;

    protected GuiCategoryEditor(IXaeroMinimap modMain, class_437 parent, class_437 escape, class_2561 title, EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter) {
        super(modMain, parent, escape, title);
        this.dataConverter = dataConverter;
        this.editorData = this.constructEditorData(dataConverter);
    }

    protected abstract ED constructEditorData(EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> var1);

    protected abstract ED constructDefaultData(EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> var1);

    protected abstract void onConfigConfirmed(C var1);

    @Override
    public void method_25426() {
        super.method_25426();
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_cancel"), b -> this.field_22787.method_1507((class_437)new class_410(result -> {
            if (result) {
                super.onExit(this.parent);
            } else {
                this.field_22787.method_1507((class_437)this);
            }
        }, (class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_cancel_confirm"), (class_2561)class_2561.method_43470((String)"")))).method_46434(this.field_22789 / 2 + 5, this.field_22790 - 32, 150, 20).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_confirm"), b -> this.confirm()).method_46434(this.field_22789 / 2 - 155, this.field_22790 - 32, 150, 20).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_reset"), b -> this.field_22787.method_1507((class_437)new class_410(result -> {
            if (result) {
                this.editorData = this.constructDefaultData(this.dataConverter);
            }
            this.field_22787.method_1507((class_437)this);
        }, (class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_reset_confirm1"), (class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_reset_confirm2")))).method_46434(6, 6, 120, 20).method_46431());
        this.rowList = new SettingRowList(this.dataConverter);
        this.method_25429(this.rowList);
    }

    private void confirm() {
        this.onConfigConfirmed(((ObjectCategory.Builder)this.dataConverter.getConfiguredBuilder(this.editorData)).build());
        super.onExit(this.parent);
    }

    @Override
    protected void onExit(class_437 screen) {
        this.field_22787.method_1507((class_437)new class_410(this, result -> {
            if (result) {
                this.confirm();
            }
            super.onExit(screen);
        }, (class_2561)class_2561.method_43471((String)"gui.xaero_category_settings_save_confirm"), (class_2561)class_2561.method_43470((String)"")){

            public boolean method_25404(int i, int j, int k) {
                if (i == 256) {
                    return true;
                }
                return super.method_25404(i, j, k);
            }
        });
    }

    public void method_25420(class_332 guiGraphics, int i, int j, float f) {
        super.method_25420(guiGraphics, i, j, f);
        this.rowList.method_25394(guiGraphics, i, j, f);
        guiGraphics.method_27534(this.field_22787.field_1772, this.field_22785, this.field_22789 / 2, 5, 0xFFFFFF);
    }

    @Override
    public void method_25394(class_332 guiGraphics, int i, int j, float f) {
        super.method_25394(guiGraphics, i, j, f);
        if (this.rowList.hovered == null) {
            return;
        }
        Supplier<CursorBox> tooltipSupplier = this.rowList.hovered.getTooltipSupplier();
        if (tooltipSupplier == null) {
            return;
        }
        CursorBox tooltip = tooltipSupplier.get();
        if (tooltip == null) {
            return;
        }
        tooltip.drawBox(guiGraphics, i, j, this.field_22789, this.field_22790);
    }

    public boolean method_25404(int i, int j, int k) {
        if (this.rowList.method_25370() && i == 257 && this.rowList.confirmSelection()) {
            return true;
        }
        return super.method_25404(i, j, k);
    }

    public void method_25393() {
        this.rowList.tick();
        super.method_25393();
    }

    public SettingRowList getRowList() {
        return this.rowList;
    }

    public class SettingRowList
    extends class_4280<xaero.hud.category.ui.GuiCategoryEditor$SettingRowList.Entry> {
        private EditorNode lastExpandedData;
        private boolean restoreScrollAfterUpdate;
        private EditorListEntry hovered;
        private final EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter;
        private static final class_2561 USAGE_NARRATION = class_2561.method_43471((String)"narration.selection.usage");
        private static final class_2561 LEFT_RIGHT_USAGE = class_2561.method_43471((String)"narration.xaero_ui_list_left_right_usage");

        public SettingRowList(EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> dataConverter) {
            super(GuiCategoryEditor.this.field_22787, GuiCategoryEditor.this.field_22789, Math.max(4, GuiCategoryEditor.this.field_22790 - 48 - 32), 32, 24);
            this.dataConverter = dataConverter;
            this.updateEntries();
        }

        protected boolean method_25332(int i) {
            return false;
        }

        public boolean hasCut() {
            if (GuiCategoryEditor.this.cutCategory == null) {
                return false;
            }
            if (((EditorCategoryNode)GuiCategoryEditor.this.cutCategorySuper).getSubCategories().contains(GuiCategoryEditor.this.cutCategory)) {
                return true;
            }
            this.setCutCategory(null, null);
            return false;
        }

        public ED getCut() {
            return GuiCategoryEditor.this.cutCategory;
        }

        public boolean isCut(ED category) {
            if (GuiCategoryEditor.this.cutCategory == category) {
                return this.hasCut();
            }
            return false;
        }

        public void setCutCategory(ED cutCategory, ED cutCategorySuper) {
            GuiCategoryEditor.this.cutCategory = cutCategory;
            GuiCategoryEditor.this.cutCategorySuper = cutCategorySuper;
        }

        public void pasteTo(ED destination) {
            if (GuiCategoryEditor.this.cutCategory == null) {
                return;
            }
            if (destination == GuiCategoryEditor.this.cutCategory || destination == GuiCategoryEditor.this.cutCategorySuper) {
                this.setCutCategory(null, null);
                this.updateEntries();
                return;
            }
            ((EditorNode)destination).getExpandAction(this).run();
            this.setLastExpandedData((EditorNode)GuiCategoryEditor.this.cutCategory);
            ((EditorCategoryNode)GuiCategoryEditor.this.cutCategorySuper).getSubCategories().remove(GuiCategoryEditor.this.cutCategory);
            ((EditorCategoryNode)destination).getSubCategories().add(0, GuiCategoryEditor.this.cutCategory);
            this.setCutCategory(null, null);
        }

        public boolean method_25370() {
            return GuiCategoryEditor.this.method_25399() == this;
        }

        public void setLastExpandedData(EditorNode lastExpandedData) {
            this.lastExpandedData = lastExpandedData;
        }

        public void restoreScrollAfterUpdate() {
            this.restoreScrollAfterUpdate = true;
        }

        public void updateEntries() {
            double scrollBackup = this.method_44387();
            this.method_25339();
            ((EditorNode)GuiCategoryEditor.this.editorData).setExpanded(true);
            this.addEntriesForExpanded((EditorNode)GuiCategoryEditor.this.editorData, null);
            if (this.method_25334() != null) {
                this.method_25324((class_350.class_351)((Entry)this.method_25334()));
            }
            if (this.restoreScrollAfterUpdate) {
                this.method_44382(scrollBackup);
                this.restoreScrollAfterUpdate = false;
            }
        }

        private void addEntriesForExpanded(EditorNode data, EditorNode parent) {
            int nextIndex = this.method_25396().size();
            List<EditorNode> subExpandables = data.getSubNodes();
            if (subExpandables == null) {
                return;
            }
            EditorNode expandedData = null;
            for (EditorNode sed : subExpandables) {
                if (!sed.isExpanded()) continue;
                expandedData = sed;
                break;
            }
            EditorListRootEntry wrappedEntry = data.getListEntryFactory().get(data, parent, nextIndex, nextIndex == 0 ? ConnectionLineType.NONE : ConnectionLineType.PATH, this, this.field_22758, expandedData == null);
            Entry currentEntry = new Entry(wrappedEntry, nextIndex++);
            this.method_25321((class_350.class_351)currentEntry);
            if (data == this.lastExpandedData) {
                this.method_25395((class_364)currentEntry);
            }
            if (expandedData != null) {
                this.addEntriesForExpanded(expandedData, data);
                return;
            }
            if (this.lastExpandedData == null && data.isExpanded()) {
                this.method_25395((class_364)currentEntry);
            }
            boolean first = true;
            for (EditorNode sed : subExpandables) {
                wrappedEntry = sed.getListEntryFactory().get(sed, data, nextIndex, first ? ConnectionLineType.HEAD_LEAF : ConnectionLineType.TAIL_LEAF, this, this.field_22758, false);
                Entry leafEntry = new Entry(wrappedEntry, nextIndex++);
                this.method_25321((class_350.class_351)leafEntry);
                if (sed == this.lastExpandedData) {
                    this.method_25395((class_364)leafEntry);
                }
                first = false;
            }
        }

        public boolean method_25402(double d, double e, int i) {
            if (!this.method_25405(d, e)) {
                this.method_25395(null);
            }
            return super.method_25402(d, e, i);
        }

        public void method_16014(double d, double e) {
            if (this.method_25334() != null) {
                ((Entry)this.method_25334()).method_16014(d, e);
            }
            super.method_16014(d, e);
        }

        public boolean method_16803(int i, int j, int k) {
            if (this.method_25334() != null && ((Entry)this.method_25334()).method_16803(i, j, k)) {
                return true;
            }
            return super.method_16803(i, j, k);
        }

        public boolean method_25400(char c, int i) {
            boolean result;
            if (this.method_25334() != null && (result = ((Entry)this.method_25334()).method_25400(c, i))) {
                return true;
            }
            return super.method_25400(c, i);
        }

        public void tick() {
            if (this.method_25334() != null) {
                ((Entry)this.method_25334()).tick();
            }
        }

        public boolean confirmSelection() {
            Entry entry = (Entry)this.method_25334();
            if (entry == null) {
                return false;
            }
            EditorListEntry selectedSubEntry = entry.wrappedEntry.confirmSelection();
            return selectedSubEntry != null;
        }

        public void method_25395(class_364 guiEventListener) {
            Entry entry;
            if (guiEventListener != null && !this.method_25396().contains(guiEventListener) || this.method_25336() == guiEventListener) {
                return;
            }
            if (this.method_25334() != null) {
                ((Entry)this.method_25334()).wrappedEntry.unfocusRecursively();
            }
            if (this.method_25336() != null) {
                ((Entry)this.method_25336()).method_25365(false);
            }
            if ((entry = (Entry)guiEventListener) != null) {
                entry.wrappedEntry.focusFirstRecursively();
            }
            super.method_25395(guiEventListener);
            if (guiEventListener == null) {
                this.setSelected(null);
            }
            this.narrateSelection();
        }

        /*
         * Ignored method signature, as it can't be verified against descriptor
         */
        public void setSelected(Entry entry) {
            super.method_25313((class_350.class_351)entry);
        }

        public int method_25322() {
            return this.field_22758;
        }

        protected int method_65507() {
            return this.field_22758 / 2 + 164;
        }

        public void narrateSelection() {
            GuiCategoryEditor.this.method_37070();
        }

        public void method_47399(class_6382 narrationElementOutput) {
            super.method_47399(narrationElementOutput);
            if (this.method_25370()) {
                narrationElementOutput.method_37035(class_6381.field_33791, new class_2561[]{USAGE_NARRATION, LEFT_RIGHT_USAGE});
            }
        }

        public void method_48579(class_332 guiGraphics, int i, int j, float f) {
            this.hovered = null;
            super.method_48579(guiGraphics, i, j, f);
        }

        public class_8016 method_48205(class_8023 focusNavigationEvent) {
            class_8023.class_8024 arrowNavigation;
            if (focusNavigationEvent instanceof class_8023.class_8024 && ((arrowNavigation = (class_8023.class_8024)focusNavigationEvent).comp_1191() == class_8028.field_41828 || arrowNavigation.comp_1191() == class_8028.field_41829)) {
                return null;
            }
            return super.method_48205(focusNavigationEvent);
        }

        public EditorCategoryNodeConverter<C, ED, CB, SD, SDB, EDB> getDataConverter() {
            return this.dataConverter;
        }

        public class Entry
        extends class_4280.class_4281<xaero.hud.category.ui.GuiCategoryEditor$SettingRowList.Entry> {
            private EditorListRootEntry wrappedEntry;
            private int index;
            private int lastX;
            private int lastY;

            public Entry(EditorListRootEntry entryInfo, int index) {
                this.wrappedEntry = entryInfo;
                this.index = index;
            }

            public void method_25343(class_332 guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
                Matrix3x2fStack poseStack = guiGraphics.method_51448();
                this.lastX = x;
                this.lastY = y;
                poseStack.pushMatrix();
                poseStack.translate((float)x, (float)y);
                boolean includesSelected = SettingRowList.this.method_25334() == this;
                this.wrappedEntry.preRender(guiGraphics, includesSelected, true);
                EditorListEntry hoveredInRow = this.wrappedEntry.render(guiGraphics, index, rowWidth, rowHeight, mouseX - x - this.wrappedEntry.getEntryRelativeX(), mouseY - y - this.wrappedEntry.getEntryRelativeY(), isMouseOver, partialTicks, GuiCategoryEditor.this.field_22793, mouseX, mouseY, includesSelected, true);
                this.wrappedEntry.postRender(guiGraphics);
                poseStack.popMatrix();
                if (hoveredInRow != null) {
                    SettingRowList.this.hovered = hoveredInRow;
                }
            }

            public boolean method_25402(double mouseX, double mouseY, int i) {
                SettingRowList.this.method_25395((class_364)this);
                double relativeMouseX = mouseX - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                double relativeMouseY = mouseY - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY();
                this.wrappedEntry.mouseClicked(this, relativeMouseX, relativeMouseY, i);
                return true;
            }

            public boolean method_25406(double mouseX, double mouseY, int i) {
                double relativeMouseX = mouseX - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                double relativeMouseY = mouseY - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY();
                this.wrappedEntry.mouseReleased(relativeMouseX, relativeMouseY, i);
                return super.method_25406(mouseX, mouseY, i);
            }

            public boolean method_25401(double mouseX, double mouseY, double f, double g) {
                double relativeMouseY;
                double relativeMouseX = mouseX - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                if (this.wrappedEntry.mouseScrolled(relativeMouseX, relativeMouseY = mouseY - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY(), f, g)) {
                    return true;
                }
                return super.method_25401(mouseX, mouseY, f, g);
            }

            public boolean method_25403(double mouseX, double mouseY, int i, double f, double g) {
                double relativeMouseY;
                double relativeMouseX = mouseX - (double)this.lastX - (double)this.wrappedEntry.getEntryRelativeX();
                if (this.wrappedEntry.mouseDragged(relativeMouseX, relativeMouseY = mouseY - (double)this.lastY - (double)this.wrappedEntry.getEntryRelativeY(), i, f, g)) {
                    return true;
                }
                return super.method_25403(mouseX, mouseY, i, f, g);
            }

            public boolean method_25404(int i, int j, int k) {
                if (this.wrappedEntry.keyPressed(i, j, k, true)) {
                    return true;
                }
                return super.method_25404(i, j, k);
            }

            public boolean method_16803(int i, int j, int k) {
                if (this.wrappedEntry.keyReleased(i, j, k)) {
                    return true;
                }
                return super.method_16803(i, j, k);
            }

            public boolean method_25400(char c, int i) {
                if (this.wrappedEntry.charTyped(c, i)) {
                    return true;
                }
                return super.method_25400(c, i);
            }

            public void method_25365(boolean bl) {
                this.wrappedEntry.setFocused(bl);
                super.method_25365(bl);
            }

            public void tick() {
                this.wrappedEntry.tick();
            }

            public class_2561 method_37006() {
                String selectedNarrationString = this.wrappedEntry.getSubNarration();
                if (selectedNarrationString == null) {
                    return class_2561.method_43470((String)"");
                }
                return class_2561.method_43469((String)"narrator.select", (Object[])new Object[]{selectedNarrationString});
            }
        }
    }
}

