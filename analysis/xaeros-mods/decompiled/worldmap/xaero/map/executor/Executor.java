/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1255
 */
package xaero.map.executor;

import net.minecraft.class_1255;

public class Executor
extends class_1255<Runnable> {
    private final Thread thread;

    public Executor(String name, Thread thread) {
        super(name);
        this.thread = thread;
    }

    public Runnable method_16211(Runnable runnable) {
        return runnable;
    }

    protected boolean method_18856(Runnable runnable) {
        return true;
    }

    protected Thread method_3777() {
        return this.thread;
    }

    public void method_5383() {
        if (!this.method_18854()) {
            throw new RuntimeException("wrong thread!");
        }
        super.method_5383();
    }
}

