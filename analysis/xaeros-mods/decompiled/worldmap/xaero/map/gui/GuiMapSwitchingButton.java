/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4185$class_4241
 */
package xaero.map.gui;

import net.minecraft.class_4185;
import xaero.map.WorldMap;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiTexturedButton;

public class GuiMapSwitchingButton
extends GuiTexturedButton {
    public static final CursorBox TOOLTIP = new CursorBox("gui.xaero_box_map_switching");

    public GuiMapSwitchingButton(boolean menuActive, int x, int y, class_4185.class_4241 onPress) {
        super(x, y, 20, 20, menuActive ? 97 : 81, 0, 16, 16, WorldMap.guiTextures, onPress, () -> TOOLTIP, 256, 256);
    }
}

