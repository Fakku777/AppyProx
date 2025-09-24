/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_332
 *  net.minecraft.class_4666
 */
package xaero.common.events;

import net.minecraft.class_332;
import net.minecraft.class_4666;
import xaero.hud.HudSession;

public class ClientEventsListener {
    public void playerTickPost(HudSession hudSession) {
    }

    public void clientTickPost(HudSession hudSession) {
    }

    public boolean handleRenderStatusEffectOverlay(class_332 guiGraphics) {
        return false;
    }

    public boolean handleForceToggleKeyMapping(class_4666 keyMapping) {
        return false;
    }
}

