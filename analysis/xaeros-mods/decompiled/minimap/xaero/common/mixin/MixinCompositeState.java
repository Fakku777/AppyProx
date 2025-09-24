/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.class_1921$class_4688
 *  net.minecraft.class_4668
 *  net.minecraft.class_4668$class_4678
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package xaero.common.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.class_1921;
import net.minecraft.class_4668;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xaero.common.core.ICompositeState;

@Mixin(value={class_1921.class_4688.class})
public class MixinCompositeState
implements ICompositeState {
    @Shadow
    ImmutableList<class_4668> field_21422;
    @Shadow
    class_4668.class_4678 field_57931;

    @Override
    public ImmutableList<class_4668> xaero_mm_getStates() {
        return this.field_21422;
    }

    @Override
    public Object xaero_mm_getOutputState() {
        return this.field_57931;
    }
}

