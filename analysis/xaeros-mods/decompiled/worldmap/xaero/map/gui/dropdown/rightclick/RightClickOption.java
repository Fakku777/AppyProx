/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1074
 *  net.minecraft.class_437
 */
package xaero.map.gui.dropdown.rightclick;

import net.minecraft.class_1074;
import net.minecraft.class_437;
import xaero.map.gui.IRightClickableElement;

public abstract class RightClickOption {
    protected String name;
    protected int index;
    protected boolean active;
    protected IRightClickableElement target;
    protected Object[] nameFormatArgs;

    public RightClickOption(String name, int index, IRightClickableElement target) {
        this.name = name;
        this.index = index;
        this.active = true;
        this.target = target;
        this.nameFormatArgs = new Object[0];
    }

    public abstract void onAction(class_437 var1);

    public boolean onSelected(class_437 screen) {
        boolean active = this.isActive();
        if (active && this.target.isRightClickValid()) {
            this.onAction(screen);
        }
        return active;
    }

    protected String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return (this.isActive() ? "" : "\u00a78") + class_1074.method_4662((String)this.getName(), (Object[])this.nameFormatArgs);
    }

    public boolean isActive() {
        return this.active;
    }

    public RightClickOption setActive(boolean isActive) {
        this.active = isActive;
        return this;
    }

    public RightClickOption setNameFormatArgs(Object ... nameFormatArgs) {
        this.nameFormatArgs = nameFormatArgs;
        return this;
    }
}

