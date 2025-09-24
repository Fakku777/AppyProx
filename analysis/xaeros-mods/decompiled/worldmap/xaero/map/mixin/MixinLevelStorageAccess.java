/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_32$class_5143
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.map.mixin;

import net.minecraft.class_32;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.core.XaeroWorldMapCore;

@Mixin(value={class_32.class_5143.class})
public class MixinLevelStorageAccess {
    @Inject(at={@At(value="RETURN")}, method={"deleteLevel"}, cancellable=false)
    public void onDeleteLevel(CallbackInfo info) {
        XaeroWorldMapCore.onDeleteWorld((class_32.class_5143)this);
    }
}

