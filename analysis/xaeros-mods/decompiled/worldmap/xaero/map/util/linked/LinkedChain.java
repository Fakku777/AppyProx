/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package xaero.map.util.linked;

import com.google.common.collect.Streams;
import java.util.Iterator;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import xaero.map.util.linked.ILinkedChainNode;

public class LinkedChain<V extends ILinkedChainNode<V>>
implements Iterable<V> {
    private boolean destroyed;
    private V head;

    public void add(V element) {
        if (this.destroyed) {
            throw new RuntimeException(new IllegalAccessException("Trying to use a destroyed chain!"));
        }
        if (element.isDestroyed()) {
            throw new IllegalArgumentException("Trying to reintroduce a removed chain element!");
        }
        if (this.head != null) {
            element.setNext(this.head);
            this.head.setPrevious(element);
        }
        this.head = element;
    }

    public void remove(V element) {
        if (this.destroyed) {
            throw new RuntimeException(new IllegalAccessException("Trying to use a cleared chain!"));
        }
        if (element.isDestroyed()) {
            return;
        }
        Object prev = element.getPrevious();
        Object next = element.getNext();
        if (prev != null) {
            prev.setNext(next);
        }
        if (next != null) {
            next.setPrevious(prev);
        }
        if (element == this.head) {
            this.head = next;
        }
        element.onDestroyed();
    }

    public void destroy() {
        this.head = null;
        this.destroyed = true;
    }

    public void reset() {
        this.head = null;
        this.destroyed = false;
    }

    @Override
    @Nonnull
    public Iterator<V> iterator() {
        return new Iterator<V>(){
            private V next;
            {
                this.next = LinkedChain.this.head;
            }

            private V reachValidNext() {
                if (LinkedChain.this.destroyed) {
                    this.next = null;
                    return null;
                }
                while (this.next != null && this.next.isDestroyed()) {
                    this.next = this.next.getNext();
                }
                return this.next;
            }

            @Override
            public boolean hasNext() {
                return this.reachValidNext() != null;
            }

            @Override
            @Nullable
            public V next() {
                Object result = this.reachValidNext();
                if (result != null) {
                    this.next = result.getNext();
                }
                return result;
            }
        };
    }

    @Nonnull
    public Stream<V> stream() {
        return Streams.stream((Iterable)this);
    }
}

