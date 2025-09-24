/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.gui;

import java.util.ArrayList;
import xaero.map.gui.dropdown.rightclick.RightClickOption;

public interface IRightClickableElement {
    public ArrayList<RightClickOption> getRightClickOptions();

    public boolean isRightClickValid();

    default public int getRightClickTitleBackgroundColor() {
        return -10496;
    }
}

