package io.netty.handler.address;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

@ChannelHandler.Sharable
/* loaded from: classes4.dex */
public class ResolveAddressHandler extends ChannelOutboundHandlerAdapter {
    private final AddressResolverGroup<? extends SocketAddress> resolverGroup;

    public ResolveAddressHandler(AddressResolverGroup<? extends SocketAddress> resolverGroup) {
        this.resolverGroup = (AddressResolverGroup) ObjectUtil.checkNotNull(resolverGroup, "resolverGroup");
    }

    @Override // io.netty.channel.ChannelOutboundHandlerAdapter, io.netty.channel.ChannelOutboundHandler
    public void connect(final ChannelHandlerContext ctx, SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        AddressResolver<? extends SocketAddress> resolver = this.resolverGroup.getResolver(ctx.executor());
        if (resolver.isSupported(remoteAddress) && !resolver.isResolved(remoteAddress)) {
            resolver.resolve(remoteAddress).addListener(new FutureListener<SocketAddress>() { // from class: io.netty.handler.address.ResolveAddressHandler.1
                @Override // io.netty.util.concurrent.GenericFutureListener
                public void operationComplete(Future<SocketAddress> future) {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        promise.setFailure(cause);
                    } else {
                        ctx.connect(future.getNow(), localAddress, promise);
                    }
                    ctx.pipeline().remove(ResolveAddressHandler.this);
                }
            });
        } else {
            ctx.connect(remoteAddress, localAddress, promise);
            ctx.pipeline().remove(this);
        }
    }
}
