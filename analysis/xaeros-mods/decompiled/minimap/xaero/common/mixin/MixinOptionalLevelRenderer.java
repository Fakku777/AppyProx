/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.minecraft.class_4184
 *  net.minecraft.class_761
 *  net.minecraft.class_9779
 *  net.minecraft.class_9922
 *  org.joml.Matrix4f
 *  org.joml.Vector4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package xaero.common.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.class_4184;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={class_761.class})
public class MixinOptionalLevelRenderer {
    @Inject(at={@At(value="HEAD")}, method={"renderLevel"})
    public void onRenderLevel(class_9922 graphicsResourceAllocator, class_9779 deltaTracker, boolean bl, class_4184 camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogVector, boolean skyPass, CallbackInfo info) {
        XaeroMinimapCore.onRenderLevelMatrices(modelViewMatrix, projectionMatrix);
    }
}

