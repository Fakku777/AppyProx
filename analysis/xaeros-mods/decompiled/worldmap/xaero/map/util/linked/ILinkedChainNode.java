/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.util.linked;

public interface ILinkedChainNode<V extends ILinkedChainNode<V>> {
    public void setNext(V var1);

    public void setPrevious(V var1);

    public V getNext();

    public V getPrevious();

    public boolean isDestroyed();

    public void onDestroyed();
}

