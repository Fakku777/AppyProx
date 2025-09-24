/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.class_2556$class_7602
 *  net.minecraft.class_2561
 *  net.minecraft.class_7471
 *  net.minecraft.class_7594
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package xaero.common.mixin;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_7471;
import net.minecraft.class_7594;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_7594.class})
public class MixinFabricChatListener {
    @Inject(method={"showMessageToPlayer"}, cancellable=true, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V")})
    public void onShowMessageToPlayer(class_2556.class_7602 bound, class_7471 playerChatMessage, class_2561 component, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        if (HudMod.INSTANCE.getEvents().handleClientPlayerChatReceivedEvent(bound, component, gameProfile)) {
            info.setReturnValue((Object)false);
        }
    }
}

