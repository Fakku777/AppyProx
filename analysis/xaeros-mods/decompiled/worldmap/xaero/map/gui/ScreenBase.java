/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_339
 *  net.minecraft.class_357
 *  net.minecraft.class_364
 *  net.minecraft.class_437
 *  net.minecraft.class_6379
 *  org.lwjgl.glfw.GLFW
 */
package xaero.map.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_357;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_6379;
import org.lwjgl.glfw.GLFW;
import xaero.map.graphics.TextureUtils;
import xaero.map.gui.CursorBox;
import xaero.map.gui.ICanTooltip;
import xaero.map.gui.IScreenBase;
import xaero.map.gui.dropdown.DropDownWidget;
import xaero.map.misc.Misc;
import xaero.map.render.util.GuiRenderUtil;

public class ScreenBase
extends class_437
implements IScreenBase {
    public class_437 parent;
    public class_437 escape;
    protected boolean canSkipWorldRender;
    protected DropDownWidget openDropdown;
    private List<DropDownWidget> dropdowns;

    protected ScreenBase(class_437 parent, class_437 escape, class_2561 titleIn) {
        super(titleIn);
        this.parent = parent;
        this.escape = escape;
        this.canSkipWorldRender = true;
        this.dropdowns = new ArrayList<DropDownWidget>();
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

    public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partial) {
        super.method_25394(guiGraphics, mouseX, mouseY, partial);
        this.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        for (DropDownWidget dropdown : this.dropdowns) {
            dropdown.method_25394(guiGraphics, mouseX, mouseY, partial);
        }
        if (this.openDropdown != null) {
            this.openDropdown.render(guiGraphics, mouseX, mouseY, this.field_22790, false);
        }
    }

    protected void renderPreDropdown(class_332 guiGraphics, int mouseX, int mouseY, float partial) {
    }

    protected void method_25426() {
        super.method_25426();
        this.dropdowns.clear();
        this.openDropdown = null;
        if (this.escape != null) {
            this.escape.method_25423(this.field_22787, this.field_22789, this.field_22790);
        }
    }

    public boolean method_25402(double mouseX, double mouseY, int button) {
        if (this.openDropdown != null) {
            if (!this.openDropdown.onDropDown((int)mouseX, (int)mouseY, this.field_22790)) {
                this.openDropdown.setClosed(true);
                this.openDropdown = null;
            } else {
                this.openDropdown.method_25402(mouseX, mouseY, button);
                return true;
            }
        }
        return super.method_25402(mouseX, mouseY, button);
    }

    public boolean method_25401(double mouseX, double mouseY, double g, double wheel) {
        if (this.openDropdown != null) {
            if (this.openDropdown.onDropDown((int)mouseX, (int)mouseY, this.field_22790)) {
                return this.openDropdown.method_25401(mouseX, mouseY, g, wheel);
            }
            return true;
        }
        return super.method_25401(mouseX, mouseY, g, wheel);
    }

    public boolean method_25406(double mouseX, double mouseY, int button) {
        if (this.openDropdown != null && this.openDropdown.method_25406(mouseX, mouseY, button)) {
            return true;
        }
        return super.method_25406(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldSkipWorldRender() {
        return this.canSkipWorldRender && Misc.screenShouldSkipWorldRender(this.escape, true);
    }

    protected boolean renderTooltips(class_332 guiGraphics, int par1, int par2, float par3) {
        boolean result = false;
        boolean mousePressed = GLFW.glfwGetMouseButton((long)this.field_22787.method_22683().method_4490(), (int)0) == 1;
        for (class_364 el : this.method_25396()) {
            CursorBox tooltip;
            class_339 b;
            if (!(el instanceof class_339) || !((b = (class_339)el) instanceof ICanTooltip) || b instanceof class_357 && mousePressed) continue;
            ICanTooltip canTooltip = (ICanTooltip)b;
            if (par1 < b.method_46426() || par2 < b.method_46427() || par1 >= b.method_46426() + b.method_25368() || par2 >= b.method_46427() + b.method_25364() || canTooltip.getXaero_wm_tooltip() == null || (tooltip = canTooltip.getXaero_wm_tooltip().get()) == null) continue;
            tooltip.drawBox(guiGraphics, par1, par2, this.field_22789, this.field_22790);
            result = true;
            break;
        }
        return result;
    }

    public class_437 getEscape() {
        return this.escape;
    }

    @Override
    public void onDropdownOpen(DropDownWidget menu) {
        if (this.openDropdown != null && this.openDropdown != menu) {
            this.openDropdown.setClosed(true);
        }
        this.openDropdown = menu;
    }

    @Override
    public void onDropdownClosed(DropDownWidget menu) {
        if (menu != this.openDropdown && this.openDropdown != null) {
            this.openDropdown.setClosed(true);
        }
        this.openDropdown = null;
    }

    protected <T extends class_364 & class_6379> T method_25429(T guiEventListener) {
        if (guiEventListener instanceof DropDownWidget) {
            this.dropdowns.add((DropDownWidget)guiEventListener);
        }
        return (T)super.method_25429(guiEventListener);
    }

    private void handleDropdownReplacement(class_339 current, class_339 replacement) {
        int dropdownIndex = this.dropdowns.indexOf(current);
        if (dropdownIndex != -1) {
            this.dropdowns.set(dropdownIndex, (DropDownWidget)replacement);
        }
        if (this.method_25399() == current) {
            this.method_25395((class_364)replacement);
        }
    }

    private void replaceWidget(class_339 current, class_339 replacement, boolean renderable) {
        int childIndex = this.method_25396().indexOf(current);
        if (childIndex != -1) {
            super.method_37066((class_364)current);
            if (renderable) {
                super.method_37063((class_364)replacement);
            } else {
                super.method_25429((class_364)replacement);
            }
            this.method_25396().remove(replacement);
            this.method_25396().add(childIndex, replacement);
        }
        this.handleDropdownReplacement(current, replacement);
    }

    public void replaceWidget(class_339 current, class_339 replacement) {
        this.replaceWidget(current, replacement, false);
    }

    public void replaceRenderableWidget(class_339 current, class_339 replacement) {
        this.replaceWidget(current, replacement, true);
    }

    protected void method_37066(class_364 current) {
        this.dropdowns.remove(current);
        super.method_37066(current);
    }
}

