/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_2535
 *  net.minecraft.class_3222
 *  net.minecraft.class_3324
 *  net.minecraft.class_8792
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_2535;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import net.minecraft.class_8792;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.server.core.XaeroMinimapServerCore;

@Mixin(value={class_3324.class})
public class MixinFabricPlayerList {
    @Inject(at={@At(value="TAIL")}, method={"placeNewPlayer"})
    public void onPlaceNewPlayer(class_2535 connection, class_3222 serverPlayer, class_8792 commonListenerCookie, CallbackInfo info) {
        if (!XaeroMinimapServerCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getCommonEvents().onPlayerLogIn((class_1657)serverPlayer);
    }
}

