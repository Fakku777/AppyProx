/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 */
package xaero.map.gui.dropdown.rightclick;

import java.util.ArrayList;
import net.minecraft.class_437;
import xaero.map.gui.GuiMap;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.DropDownWidget;
import xaero.map.gui.dropdown.IDropDownContainer;
import xaero.map.gui.dropdown.rightclick.RightClickOption;

public class GuiRightClickMenu
extends DropDownWidget {
    private IRightClickableElement target;
    private ArrayList<RightClickOption> actionOptions;
    private class_437 screen;
    private boolean removed;

    private GuiRightClickMenu(IRightClickableElement target, ArrayList<RightClickOption> options, class_437 screen, int x, int y, int w, int titleBackgroundColor, IDropDownContainer container) {
        super(options.stream().map(o -> o.getDisplayName()).collect(ArrayList::new, ArrayList::add, ArrayList::addAll).toArray(new String[0]), x - (GuiRightClickMenu.shouldOpenLeft(options.size(), x, w, screen.field_22789) ? w : 0), y, w, -1, false, null, container, false, null);
        this.openingUp = false;
        this.target = target;
        this.screen = screen;
        this.setClosed(false);
        this.actionOptions = options;
        this.selectedBackground = this.selectedHoveredBackground = titleBackgroundColor;
        this.shortenFromTheRight = true;
    }

    private static boolean shouldOpenLeft(int optionCount, int x, int w, int screenWidth) {
        return x + w - screenWidth > 0;
    }

    private static boolean shouldOpenUp(int optionCount, int y, int screenHeight) {
        int potentialHeight = 11 * optionCount;
        return y + potentialHeight - screenHeight > potentialHeight / 2;
    }

    @Override
    public void setClosed(boolean closed) {
        if (!this.isClosed() && closed) {
            this.removed = true;
        }
        super.setClosed(closed);
    }

    @Override
    public void selectId(int id, boolean callCallback) {
        if (id == -1) {
            return;
        }
        if (this.removed) {
            return;
        }
        this.actionOptions.get(id).onSelected(this.screen);
        this.setClosed(true);
    }

    public static GuiRightClickMenu getMenu(IRightClickableElement rightClickable, GuiMap screen, int x, int y, int w) {
        return new GuiRightClickMenu(rightClickable, rightClickable.getRightClickOptions(), screen, x, y, w, rightClickable.getRightClickTitleBackgroundColor(), screen);
    }

    public IRightClickableElement getTarget() {
        return this.target;
    }
}

