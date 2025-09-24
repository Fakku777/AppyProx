/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2561
 *  net.minecraft.class_410
 *  net.minecraft.class_437
 */
package xaero.common.gui;

import net.minecraft.class_2561;
import net.minecraft.class_410;
import net.minecraft.class_437;
import xaero.common.gui.IXaeroConfirmScreenCallback;

public class GuiReset
extends class_410 {
    public GuiReset(IXaeroConfirmScreenCallback callback, class_437 parent, class_437 escScreen) {
        super(r -> callback.accept(r, parent, escScreen), (class_2561)class_2561.method_43471((String)"gui.xaero_reset_message"), (class_2561)class_2561.method_43471((String)"gui.xaero_reset_message2"));
    }
}

