/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1922
 *  net.minecraft.class_2189
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_2404
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 */
package xaero.map.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.class_1922;
import net.minecraft.class_2189;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2404;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import xaero.map.WorldMap;
import xaero.map.cache.placeholder.PlaceholderBlockGetter;

public class BlockStateShortShapeCache {
    private Map<class_2680, Boolean> shortBlockStates = new HashMap<class_2680, Boolean>();
    private class_2680 lastShortChecked = null;
    private boolean lastShortCheckedResult = false;
    private PlaceholderBlockGetter placeholderBlockGetter = new PlaceholderBlockGetter();
    private CompletableFuture<Boolean> ioThreadWaitingFor;
    private Supplier<Boolean> ioThreadWaitingForSupplier;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isShort(class_2680 state) {
        Boolean cached;
        if (state == null || state.method_26204() instanceof class_2189 || state.method_26204() instanceof class_2404) {
            return false;
        }
        Map<class_2680, Boolean> map = this.shortBlockStates;
        synchronized (map) {
            if (state == this.lastShortChecked) {
                return this.lastShortCheckedResult;
            }
            cached = this.shortBlockStates.get(state);
        }
        if (cached == null) {
            if (!class_310.method_1551().method_18854()) {
                boolean isIOThread;
                Supplier<Boolean> supplier = () -> this.isShort(state);
                CompletableFuture future = class_310.method_1551().method_5385(supplier);
                boolean bl = isIOThread = Thread.currentThread() == WorldMap.mapRunnerThread;
                if (isIOThread) {
                    this.ioThreadWaitingForSupplier = supplier;
                    this.ioThreadWaitingFor = future;
                }
                Boolean result = (Boolean)future.join();
                if (isIOThread) {
                    this.ioThreadWaitingForSupplier = null;
                    this.ioThreadWaitingFor = null;
                }
                return result;
            }
            try {
                this.placeholderBlockGetter.setPlaceholderState(state);
                class_265 shape = state.method_26218((class_1922)this.placeholderBlockGetter, class_2338.field_10980);
                cached = shape.method_1105(class_2350.class_2351.field_11052) < 0.25;
            }
            catch (Throwable t) {
                WorldMap.LOGGER.info("Defaulting world-dependent block state shape to not short: " + String.valueOf(state));
                cached = false;
            }
            map = this.shortBlockStates;
            synchronized (map) {
                this.shortBlockStates.put(state, cached);
                this.lastShortChecked = state;
                this.lastShortCheckedResult = cached;
            }
        }
        return cached;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        Map<class_2680, Boolean> map = this.shortBlockStates;
        synchronized (map) {
            this.shortBlockStates.clear();
            this.lastShortChecked = null;
            this.lastShortCheckedResult = false;
        }
    }

    public void supplyForIOThread() {
        if (!class_310.method_1551().method_18854()) {
            throw new RuntimeException(new IllegalAccessException("Supplying from the wrong thread!"));
        }
        CompletableFuture<Boolean> waitingFor = this.ioThreadWaitingFor;
        if (waitingFor != null && !waitingFor.isDone()) {
            waitingFor.complete(this.ioThreadWaitingForSupplier.get());
        }
    }
}

