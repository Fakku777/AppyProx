/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReturnValue
 *  net.minecraft.class_304
 *  net.minecraft.class_4666
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package xaero.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.class_304;
import net.minecraft.class_4666;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_304.class})
public class MixinFabricKeyMapping {
    @ModifyReturnValue(method={"isDown"}, at={@At(value="RETURN")})
    public boolean onIsDown(boolean original) throws Exception {
        if (!(this instanceof class_4666)) {
            return original;
        }
        if (XaeroMinimapCore.onToggleKeyIsDown((class_4666)this)) {
            return true;
        }
        return original;
    }
}

