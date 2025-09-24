/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1059
 *  net.minecraft.class_7766$class_7767
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import net.minecraft.class_1059;
import net.minecraft.class_7766;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_1059.class})
public class MixinFabricSpriteAtlasTexture {
    @Inject(at={@At(value="RETURN")}, method={"upload"})
    public void onUpload(class_7766.class_7767 spriteAtlasTexture$Data_1, CallbackInfo info) {
        if (!XaeroMinimapCore.isModLoaded()) {
            return;
        }
        HudMod.INSTANCE.getModEvents().handleTextureStitchEventPost((class_1059)this);
    }
}

