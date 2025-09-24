/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_329
 *  net.minecraft.class_332
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_329.class})
public class MixinOptionalInGameHud {
    @Inject(at={@At(value="HEAD")}, method={"renderHotbarAndDecorations"})
    public void onRenderHotbarAndDecorations(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo info) {
        XaeroMinimapCore.handleRenderModOverlay(guiGraphics, deltaTracker);
    }

    @Inject(at={@At(value="RETURN")}, method={"render"})
    public void onRenderEnd(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo info) {
        XaeroMinimapCore.afterIngameGuiRender(guiGraphics, deltaTracker);
    }

    @Inject(at={@At(value="HEAD")}, method={"renderCrosshair"}, cancellable=true)
    public void onRenderCrosshair(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo info) {
        if (XaeroMinimapCore.onRenderCrosshair(guiGraphics)) {
            info.cancel();
        }
    }

    @Inject(at={@At(value="RETURN")}, method={"renderEffects"})
    public void postRenderStatusEffectOverlay(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo info) {
        XaeroMinimapCore.onRenderStatusEffectOverlayPost(guiGraphics);
    }

    @Inject(at={@At(value="HEAD")}, method={"render"})
    public void onRenderStart(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo info) {
        XaeroMinimapCore.beforeIngameGuiRender(guiGraphics, deltaTracker);
    }

    @Inject(at={@At(value="HEAD")}, method={"renderEffects"}, cancellable=true)
    public void onRenderStatusEffectOverlay(class_332 guiGraphics, class_9779 deltaTracker, CallbackInfo info) {
        if (XaeroMinimapCore.onRenderStatusEffectOverlay(guiGraphics)) {
            info.cancel();
        }
    }
}

