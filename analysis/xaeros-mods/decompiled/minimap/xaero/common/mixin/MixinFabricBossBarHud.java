/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1259
 *  net.minecraft.class_332
 *  net.minecraft.class_337
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_1259;
import net.minecraft.class_332;
import net.minecraft.class_337;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_337.class})
public class MixinFabricBossBarHud {
    @Inject(at={@At(value="HEAD")}, method={"drawBar"})
    public void onRenderBossBar(class_332 guiGraphics, int x, int y, class_1259 bossBar, CallbackInfo info) {
        XaeroMinimapCore.onBossHealthRender(y + 19);
    }
}

