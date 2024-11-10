package com.google.common.util.concurrent;

import com.google.common.util.concurrent.AbstractFuture;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public final class SettableFuture<V> extends AbstractFuture.TrustedFuture<V> {
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<>();
    }

    @Override // com.google.common.util.concurrent.AbstractFuture
    public boolean set(@Nullable V value) {
        return super.set(value);
    }

    @Override // com.google.common.util.concurrent.AbstractFuture
    public boolean setException(Throwable throwable) {
        return super.setException(throwable);
    }

    @Override // com.google.common.util.concurrent.AbstractFuture
    public boolean setFuture(ListenableFuture<? extends V> future) {
        return super.setFuture(future);
    }

    private SettableFuture() {
    }
}
