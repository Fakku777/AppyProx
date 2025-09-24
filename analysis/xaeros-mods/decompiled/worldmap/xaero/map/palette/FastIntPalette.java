/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 */
package xaero.map.palette;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;

public final class FastIntPalette {
    private final Int2IntMap indexHelper;
    private final List<Element> elements;
    private final int maxCountPerElement;

    private FastIntPalette(Int2IntMap indexHelper, List<Element> elements, int maxCountPerElement) {
        this.indexHelper = indexHelper;
        this.elements = elements;
        this.maxCountPerElement = maxCountPerElement;
    }

    public synchronized int get(int index, int defaultValue) {
        if (index < 0 || index >= this.elements.size()) {
            return defaultValue;
        }
        Element element = this.elements.get(index);
        if (element == null) {
            return defaultValue;
        }
        return element.getValue();
    }

    public synchronized int add(int elementValue) {
        int existing = this.indexHelper.getOrDefault(elementValue, -1);
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
        this.indexHelper.put(elementValue, newIndex);
        Element element = new Element(elementValue);
        if (add) {
            this.elements.add(element);
        } else {
            this.elements.set(newIndex, element);
        }
        return newIndex;
    }

    public synchronized int add(int elementValue, int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        int index = this.add(elementValue);
        this.elements.get((int)index).count = (short)count;
        return index;
    }

    public synchronized int append(int elementValue, int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        int existing = this.indexHelper.getOrDefault(elementValue, -1);
        if (existing != -1) {
            throw new IllegalArgumentException("duplicate palette element!");
        }
        int newIndex = this.elements.size();
        this.indexHelper.put(elementValue, newIndex);
        Element element = new Element(elementValue);
        element.count = (short)count;
        this.elements.add(element);
        return newIndex;
    }

    public synchronized int getIndex(int elementValue) {
        return this.indexHelper.getOrDefault(elementValue, -1);
    }

    public synchronized int count(int index, boolean up) {
        Element element = this.elements.get(index);
        element.count(up, this.maxCountPerElement);
        return element.getCount();
    }

    public synchronized int getCount(int index) {
        Element element = this.elements.get(index);
        return element.getCount();
    }

    public synchronized void remove(int index) {
        Element previous = this.elements.set(index, null);
        if (previous != null) {
            this.indexHelper.remove(previous.getValue());
        }
        if (index == this.elements.size() - 1) {
            while (!this.elements.isEmpty() && this.elements.get(this.elements.size() - 1) == null) {
                this.elements.remove(this.elements.size() - 1);
            }
        }
    }

    public synchronized boolean replace(int elementValue, int newValue) {
        int index = this.indexHelper.getOrDefault(elementValue, -1);
        if (index == -1) {
            return false;
        }
        return this.replaceAtIndex(index, newValue);
    }

    public synchronized boolean replaceAtIndex(int index, int newValue) {
        Element element = this.elements.get(index);
        int elementValue = element.getValue();
        element.setValue(newValue);
        this.indexHelper.remove(elementValue);
        this.indexHelper.put(newValue, index);
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

    private static class Element {
        private int value;
        private short count;

        private Element(int elementValue) {
            this.value = elementValue;
        }

        private void setValue(int elementValue) {
            this.value = elementValue;
        }

        private int getValue() {
            return this.value;
        }

        private int getCount() {
            return this.count & 0xFFFF;
        }

        private void count(boolean up, int maxCount) {
            if (up && this.count == maxCount || !up && this.count == 0) {
                throw new IllegalStateException();
            }
            this.count = (short)(this.count + (up ? 1 : -1));
        }
    }

    public static final class Builder {
        private int maxCountPerElement;

        private Builder() {
        }

        public Builder setDefault() {
            this.setMaxCountPerElement(0);
            return this;
        }

        public Builder setMaxCountPerElement(int maxCountPerElement) {
            this.maxCountPerElement = maxCountPerElement;
            return this;
        }

        public FastIntPalette build() {
            if (this.maxCountPerElement == 0) {
                throw new IllegalStateException();
            }
            if (this.maxCountPerElement > 65535) {
                throw new IllegalStateException("the max count must be within 0 - 65535");
            }
            Int2IntOpenHashMap indexHelper = new Int2IntOpenHashMap();
            ArrayList<Element> elements = new ArrayList<Element>();
            return new FastIntPalette((Int2IntMap)indexHelper, elements, this.maxCountPerElement);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

