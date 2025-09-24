/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  net.minecraft.class_2246
 *  net.minecraft.class_2487
 *  net.minecraft.class_2507
 *  net.minecraft.class_2680
 */
package xaero.map.region.state;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.class_2246;
import net.minecraft.class_2487;
import net.minecraft.class_2507;
import net.minecraft.class_2680;

public class UnknownBlockState
extends class_2680 {
    private class_2487 nbt;
    private String stringRepresentation;

    public UnknownBlockState(class_2487 nbt) {
        super(class_2246.field_10124, new Reference2ObjectArrayMap(), null);
        this.nbt = nbt;
        this.stringRepresentation = "Unknown: " + String.valueOf(nbt);
    }

    public void write(DataOutputStream out) throws IOException {
        class_2507.method_10628((class_2487)this.nbt, (DataOutput)out);
    }

    public String toString() {
        return this.stringRepresentation;
    }
}

