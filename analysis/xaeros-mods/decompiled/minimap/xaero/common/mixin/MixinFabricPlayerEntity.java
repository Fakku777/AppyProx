/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.server.core.XaeroMinimapServerCore;

@Mixin(value={class_1657.class})
public class MixinFabricPlayerEntity {
    @Inject(at={@At(value="HEAD")}, method={"tick"})
    public void onTickStart(CallbackInfo info) {
        if (!XaeroMinimapServerCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getCommonEvents().handlePlayerTickStart((class_1657)this);
    }
}

