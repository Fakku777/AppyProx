/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1921
 *  net.raphimc.immediatelyfast.feature.core.BatchableBufferSource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package xaero.common.mixin;

import net.minecraft.class_1921;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xaero.common.core.IBufferSource;
import xaero.common.core.XaeroMinimapCore;

@Mixin(value={BatchableBufferSource.class})
public class MixinBatchableBufferSource
implements IBufferSource {
    private class_1921 xaero_lastRenderType;

    @Override
    public class_1921 getXaero_lastRenderType() {
        return this.xaero_lastRenderType;
    }

    @Override
    public void setXaero_lastRenderType(class_1921 lastRenderType) {
        this.xaero_lastRenderType = lastRenderType;
    }

    @ModifyVariable(method={"getBuffer"}, index=1, at=@At(value="HEAD"))
    public class_1921 onGetBuffer(class_1921 argument) {
        XaeroMinimapCore.onBufferSourceGetBuffer(this, argument);
        return argument;
    }
}

