/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2556$class_7602
 *  net.minecraft.class_2561
 *  net.minecraft.class_7594
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_2556;
import net.minecraft.class_2561;
import net.minecraft.class_7594;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_7594.class})
public class MixinChatListener {
    @Inject(method={"handleDisguisedChatMessage"}, cancellable=true, at={@At(value="HEAD")})
    public void onHandleDisguisedChatMessag(class_2561 component, class_2556.class_7602 bound, CallbackInfo info) {
        if (!XaeroMinimapCore.onHandleDisguisedChatMessage(bound, component)) {
            info.cancel();
        }
    }

    @Inject(method={"handleSystemMessage"}, cancellable=true, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V")})
    public void onHandleSystemChat(class_2561 component, boolean bl, CallbackInfo info) {
        if (XaeroMinimapCore.onSystemChat(component)) {
            info.cancel();
        }
    }
}

