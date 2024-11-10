package com.google.common.collect;

import java.util.Iterator;

/* loaded from: classes.dex */
public interface PeekingIterator<E> extends Iterator<E> {
    E next();

    E peek();

    @Override // java.util.Iterator
    void remove();
}
