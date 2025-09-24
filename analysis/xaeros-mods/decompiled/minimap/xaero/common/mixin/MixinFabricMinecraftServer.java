/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3176
 *  net.minecraft.server.MinecraftServer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import java.util.function.BooleanSupplier;
import net.minecraft.class_3176;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.HudMod;

@Mixin(value={MinecraftServer.class})
public class MixinFabricMinecraftServer {
    @Inject(at={@At(value="HEAD")}, method={"tickServer"})
    public void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        if (!(this instanceof class_3176)) {
            return;
        }
        if (HudMod.INSTANCE != null) {
            HudMod.INSTANCE.tryLoadLaterServer();
        }
    }
}

