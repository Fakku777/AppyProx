/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.serialization.data;

import xaero.hud.category.serialization.data.ObjectCategoryData;

public abstract class ObjectCategoryDataSerializer<D extends ObjectCategoryData<D>> {
    protected ObjectCategoryDataSerializer() {
    }

    public abstract String serialize(D var1);

    public abstract D deserialize(String var1);

    public static abstract class Builder<D extends ObjectCategoryData<D>> {
        public Builder<D> setDefault() {
            return this;
        }

        public abstract ObjectCategoryDataSerializer<D> build();
    }
}

