/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.events;

import xaero.common.IXaeroMinimap;
import xaero.common.events.ModClientEvents;

public class ModClientEventsFabric
extends ModClientEvents {
    public ModClientEventsFabric(IXaeroMinimap modMain) {
        super(modMain);
    }

    @Override
    protected void handleTextureStitchEventPost_onReset() {
        super.handleTextureStitchEventPost_onReset();
        this.modMain.getMinimap().getMinimapFBORenderer().resetEntityIconsResources();
    }
}

