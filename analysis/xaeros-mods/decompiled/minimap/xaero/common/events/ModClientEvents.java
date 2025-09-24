/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1059
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 */
package xaero.common.events;

import net.minecraft.class_1059;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import xaero.common.IXaeroMinimap;
import xaero.common.XaeroMinimapSession;
import xaero.common.anim.MultiplyAnimationHelper;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.render.util.ImmediateRenderUtil;

public abstract class ModClientEvents {
    protected IXaeroMinimap modMain;

    public ModClientEvents(IXaeroMinimap modMain) {
        this.modMain = modMain;
    }

    public void handleTextureStitchEventPost(class_1059 texture) {
        if (texture.method_24106().equals((Object)class_1059.field_5275)) {
            Minimap minimap;
            XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession != null) {
                minimapSession.getMinimapProcessor().getMinimapWriter().setClearBlockColours(true);
                minimapSession.getMinimapProcessor().getMinimapWriter().resetShortBlocks();
            }
            if ((minimap = this.modMain.getMinimap()) != null) {
                minimap.getMinimapFBORenderer().resetEntityIcons();
                this.handleTextureStitchEventPost_onReset();
            }
        }
    }

    public void handleRenderModOverlay(class_332 guiGraphics, class_9779 deltaTracker) {
        MultiplyAnimationHelper.tick();
        if (class_310.method_1551().field_1690.field_1842) {
            return;
        }
        ImmediateRenderUtil.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (minimapSession != null) {
            this.modMain.getHudRenderer().render(this.modMain.getHud(), guiGraphics, deltaTracker.method_60637(true));
            this.modMain.getMinimap().getWaypointMapRenderer().drawSetChange(minimapSession, guiGraphics, class_310.method_1551().method_22683());
        }
    }

    protected void handleTextureStitchEventPost_onReset() {
    }
}

