/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.class_2487
 *  net.minecraft.class_2499
 */
package xaero.map.file.worldsave;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class WorldDataChunkTileEntityLookup {
    private class_2499 tileEntitiesNbt;
    private Int2ObjectMap<Int2ObjectMap<Int2ObjectMap<class_2487>>> tileEntities;

    public WorldDataChunkTileEntityLookup(class_2499 tileEntitiesNbt) {
        this.tileEntitiesNbt = tileEntitiesNbt;
    }

    private void loadIfNeeded() {
        if (this.tileEntities == null) {
            this.tileEntities = new Int2ObjectOpenHashMap();
            this.tileEntitiesNbt.forEach(tag -> {
                if (tag instanceof class_2487) {
                    Int2ObjectMap byY;
                    class_2487 compoundNbt = (class_2487)tag;
                    if (!compoundNbt.method_10545("x")) {
                        return;
                    }
                    int x = compoundNbt.method_10550("x").orElse(0) & 0xF;
                    if (!compoundNbt.method_10545("y")) {
                        return;
                    }
                    int y = compoundNbt.method_10550("y").orElse(0);
                    if (!compoundNbt.method_10545("z")) {
                        return;
                    }
                    int z = compoundNbt.method_10550("z").orElse(0) & 0xF;
                    Int2ObjectMap byX = (Int2ObjectMap)this.tileEntities.get(x);
                    if (byX == null) {
                        byX = new Int2ObjectOpenHashMap();
                        this.tileEntities.put(x, (Object)byX);
                    }
                    if ((byY = (Int2ObjectMap)byX.get(y)) == null) {
                        byY = new Int2ObjectOpenHashMap();
                        byX.put(y, (Object)byY);
                    }
                    byY.put(z, (Object)compoundNbt);
                }
            });
            this.tileEntitiesNbt = null;
        }
    }

    public class_2487 getTileEntityNbt(int x, int y, int z) {
        this.loadIfNeeded();
        Int2ObjectMap byX = (Int2ObjectMap)this.tileEntities.get(x);
        if (byX == null) {
            return null;
        }
        Int2ObjectMap byY = (Int2ObjectMap)byX.get(y);
        if (byY == null) {
            return null;
        }
        return (class_2487)byY.get(z);
    }
}

