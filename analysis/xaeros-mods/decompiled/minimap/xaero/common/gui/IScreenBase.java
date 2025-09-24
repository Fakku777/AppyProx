/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_437;
import xaero.common.gui.dropdown.IDropDownContainer;

public interface IScreenBase
extends IDropDownContainer {
    public boolean shouldSkipWorldRender();

    public class_437 getEscape();
}

