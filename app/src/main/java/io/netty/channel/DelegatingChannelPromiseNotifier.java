package io.netty.channel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes4.dex */
public final class DelegatingChannelPromiseNotifier implements ChannelPromise, ChannelFutureListener {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance((Class<?>) DelegatingChannelPromiseNotifier.class);
    private final ChannelPromise delegate;
    private final boolean logNotifyFailure;

    public DelegatingChannelPromiseNotifier(ChannelPromise delegate) {
        this(delegate, !(delegate instanceof VoidChannelPromise));
    }

    public DelegatingChannelPromiseNotifier(ChannelPromise delegate, boolean logNotifyFailure) {
        this.delegate = (ChannelPromise) ObjectUtil.checkNotNull(delegate, "delegate");
        this.logNotifyFailure = logNotifyFailure;
    }

    @Override // io.netty.util.concurrent.GenericFutureListener
    public void operationComplete(ChannelFuture future) throws Exception {
        InternalLogger internalLogger = this.logNotifyFailure ? logger : null;
        if (future.isSuccess()) {
            Void result = (Void) future.get();
            PromiseNotificationUtil.trySuccess(this.delegate, result, internalLogger);
        } else if (future.isCancelled()) {
            PromiseNotificationUtil.tryCancel(this.delegate, internalLogger);
        } else {
            Throwable cause = future.cause();
            PromiseNotificationUtil.tryFailure(this.delegate, cause, internalLogger);
        }
    }

    @Override // io.netty.channel.ChannelPromise, io.netty.channel.ChannelFuture
    public Channel channel() {
        return this.delegate.channel();
    }

    @Override // io.netty.util.concurrent.Promise, io.netty.util.concurrent.ProgressivePromise
    public ChannelPromise setSuccess(Void result) {
        this.delegate.setSuccess(result);
        return this;
    }

    @Override // io.netty.channel.ChannelPromise
    public ChannelPromise setSuccess() {
        this.delegate.setSuccess();
        return this;
    }

    @Override // io.netty.channel.ChannelPromise
    public boolean trySuccess() {
        return this.delegate.trySuccess();
    }

    @Override // io.netty.util.concurrent.Promise
    public boolean trySuccess(Void result) {
        return this.delegate.trySuccess(result);
    }

    @Override // io.netty.util.concurrent.Promise, io.netty.channel.ChannelPromise
    public ChannelPromise setFailure(Throwable cause) {
        this.delegate.setFailure(cause);
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        this.delegate.addListener(listener);
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
        this.delegate.addListeners(listeners);
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        this.delegate.removeListener(listener);
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
        this.delegate.removeListeners(listeners);
        return this;
    }

    @Override // io.netty.util.concurrent.Promise
    public boolean tryFailure(Throwable cause) {
        return this.delegate.tryFailure(cause);
    }

    @Override // io.netty.util.concurrent.Promise
    public boolean setUncancellable() {
        return this.delegate.setUncancellable();
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> await() throws InterruptedException {
        this.delegate.await();
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> awaitUninterruptibly() {
        this.delegate.awaitUninterruptibly();
        return this;
    }

    @Override // io.netty.channel.ChannelFuture
    public boolean isVoid() {
        return this.delegate.isVoid();
    }

    @Override // io.netty.channel.ChannelPromise
    public ChannelPromise unvoid() {
        return isVoid() ? new DelegatingChannelPromiseNotifier(this.delegate.unvoid()) : this;
    }

    @Override // io.netty.util.concurrent.Future
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.await(timeout, unit);
    }

    @Override // io.netty.util.concurrent.Future
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.delegate.await(timeoutMillis);
    }

    @Override // io.netty.util.concurrent.Future
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return this.delegate.awaitUninterruptibly(timeout, unit);
    }

    @Override // io.netty.util.concurrent.Future
    public boolean awaitUninterruptibly(long timeoutMillis) {
        return this.delegate.awaitUninterruptibly(timeoutMillis);
    }

    @Override // io.netty.util.concurrent.Future
    public Void getNow() {
        return this.delegate.getNow();
    }

    @Override // io.netty.util.concurrent.Future, java.util.concurrent.Future
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.delegate.cancel(mayInterruptIfRunning);
    }

    @Override // java.util.concurrent.Future
    public boolean isCancelled() {
        return this.delegate.isCancelled();
    }

    @Override // java.util.concurrent.Future
    public boolean isDone() {
        return this.delegate.isDone();
    }

    @Override // java.util.concurrent.Future
    public Void get() throws InterruptedException, ExecutionException {
        return (Void) this.delegate.get();
    }

    @Override // java.util.concurrent.Future
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (Void) this.delegate.get(timeout, unit);
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> sync() throws InterruptedException {
        this.delegate.sync();
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public Future<Void> syncUninterruptibly() {
        this.delegate.syncUninterruptibly();
        return this;
    }

    @Override // io.netty.util.concurrent.Future
    public boolean isSuccess() {
        return this.delegate.isSuccess();
    }

    @Override // io.netty.util.concurrent.Future
    public boolean isCancellable() {
        return this.delegate.isCancellable();
    }

    @Override // io.netty.util.concurrent.Future
    public Throwable cause() {
        return this.delegate.cause();
    }
}
