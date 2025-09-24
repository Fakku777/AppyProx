/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1657
 *  net.minecraft.class_3218
 *  net.minecraft.class_3222
 *  net.minecraft.class_3324
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.map.mixin;

import net.minecraft.class_1657;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.server.core.XaeroWorldMapServerCore;

@Mixin(value={class_3324.class})
public class MixinPlayerList {
    @Inject(at={@At(value="HEAD")}, method={"sendLevelInfo"})
    public void onSendWorldInfo(class_3222 player, class_3218 world, CallbackInfo info) {
        XaeroWorldMapServerCore.onServerWorldInfo((class_1657)player);
    }
}

