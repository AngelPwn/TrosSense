package io.netty.channel.group;

import io.netty.util.internal.ObjectUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: classes4.dex */
final class CombinedIterator<E> implements Iterator<E> {
    private Iterator<E> currentIterator;
    private final Iterator<E> i1;
    private final Iterator<E> i2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CombinedIterator(Iterator<E> i1, Iterator<E> i2) {
        this.i1 = (Iterator) ObjectUtil.checkNotNull(i1, "i1");
        this.i2 = (Iterator) ObjectUtil.checkNotNull(i2, "i2");
        this.currentIterator = i1;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        while (!this.currentIterator.hasNext()) {
            if (this.currentIterator == this.i1) {
                this.currentIterator = this.i2;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Iterator
    public E next() {
        while (true) {
            try {
                return this.currentIterator.next();
            } catch (NoSuchElementException e) {
                if (this.currentIterator == this.i1) {
                    this.currentIterator = this.i2;
                } else {
                    throw e;
                }
            }
        }
    }

    @Override // java.util.Iterator
    public void remove() {
        this.currentIterator.remove();
    }
}
