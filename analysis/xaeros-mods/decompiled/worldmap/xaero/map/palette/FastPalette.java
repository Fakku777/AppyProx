/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package xaero.map.palette;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;

public final class FastPalette<T> {
    private final Object2IntMap<T> indexHelper;
    private final List<Element<T>> elements;
    private final int maxCountPerElement;

    private FastPalette(Object2IntMap<T> indexHelper, List<Element<T>> elements, int maxCountPerElement) {
        this.indexHelper = indexHelper;
        this.elements = elements;
        this.maxCountPerElement = maxCountPerElement;
    }

    public synchronized T get(int index) {
        if (index < 0 || index >= this.elements.size()) {
            return null;
        }
        Element<T> element = this.elements.get(index);
        if (element == null) {
            return null;
        }
        return element.getObject();
    }

    public synchronized int add(T elementObject) {
        int existing = this.indexHelper.getOrDefault(elementObject, -1);
        if (existing != -1) {
            return existing;
        }
        int newIndex = this.elements.size();
        boolean add = true;
        for (int i = 0; i < this.elements.size(); ++i) {
            if (this.elements.get(i) != null) continue;
            newIndex = i;
            add = false;
            break;
        }
        this.indexHelper.put(elementObject, newIndex);
        Element<T> element = new Element<T>(elementObject);
        if (add) {
            this.elements.add(element);
        } else {
            this.elements.set(newIndex, element);
        }
        return newIndex;
    }

    public synchronized int add(T elementObject, int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        int index = this.add(elementObject);
        this.elements.get((int)index).count = (short)count;
        return index;
    }

    public synchronized int append(T elementObject, int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        int existing = this.indexHelper.getOrDefault(elementObject, -1);
        if (existing != -1) {
            throw new IllegalArgumentException("duplicate palette element!");
        }
        int newIndex = this.elements.size();
        this.indexHelper.put(elementObject, newIndex);
        Element<T> element = new Element<T>(elementObject);
        element.count = (short)count;
        this.elements.add(element);
        return newIndex;
    }

    public synchronized int getIndex(T elementObject) {
        return this.indexHelper.getOrDefault(elementObject, -1);
    }

    public synchronized int count(int index, boolean up) {
        Element<T> element = this.elements.get(index);
        element.count(up, this.maxCountPerElement);
        return element.getCount();
    }

    public synchronized int getCount(int index) {
        Element<T> element = this.elements.get(index);
        return element.getCount();
    }

    public synchronized void remove(int index) {
        Element previous = this.elements.set(index, null);
        if (previous != null) {
            this.indexHelper.removeInt(previous.getObject());
        }
        if (index == this.elements.size() - 1) {
            while (!this.elements.isEmpty() && this.elements.get(this.elements.size() - 1) == null) {
                this.elements.remove(this.elements.size() - 1);
            }
        }
    }

    public synchronized boolean replace(T elementObject, T newObject) {
        int index = this.indexHelper.getOrDefault(elementObject, -1);
        if (index == -1) {
            return false;
        }
        return this.replace(index, newObject);
    }

    public synchronized boolean replace(int index, T newObject) {
        Element<T> element = this.elements.get(index);
        T elementObject = element.getObject();
        element.setObject(newObject);
        this.indexHelper.removeInt(elementObject);
        this.indexHelper.put(newObject, index);
        return true;
    }

    public synchronized void addNull() {
        this.elements.add(null);
    }

    public int getSize() {
        return this.elements.size();
    }

    public int getNonNullCount() {
        return this.indexHelper.size();
    }

    private static class Element<T> {
        private T object;
        private short count;

        private Element(T elementObject) {
            this.object = elementObject;
        }

        private void setObject(T elementObject) {
            this.object = elementObject;
        }

        private T getObject() {
            return this.object;
        }

        private int getCount() {
            return this.count & 0xFFFF;
        }

        private void count(boolean up, int maxCount) {
            this.count = (short)(this.count + (up ? 1 : -1));
        }
    }

    public static final class Builder<T> {
        private int maxCountPerElement;

        private Builder() {
        }

        public Builder<T> setDefault() {
            this.setMaxCountPerElement(0);
            return this;
        }

        public Builder<T> setMaxCountPerElement(int maxCountPerElement) {
            this.maxCountPerElement = maxCountPerElement;
            return this;
        }

        public FastPalette<T> build() {
            if (this.maxCountPerElement == 0) {
                throw new IllegalStateException();
            }
            if (this.maxCountPerElement > 65535) {
                throw new IllegalStateException("the max count must be within 0 - 65535");
            }
            Object2IntOpenHashMap indexHelper = new Object2IntOpenHashMap();
            ArrayList elements = new ArrayList();
            return new FastPalette(indexHelper, elements, this.maxCountPerElement);
        }

        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}

