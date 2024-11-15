package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes4.dex */
public final class BinaryMemcacheClientCodec extends CombinedChannelDuplexHandler<BinaryMemcacheResponseDecoder, BinaryMemcacheRequestEncoder> {
    private final boolean failOnMissingResponse;
    private final AtomicLong requestResponseCounter;

    public BinaryMemcacheClientCodec() {
        this(8192);
    }

    public BinaryMemcacheClientCodec(int decodeChunkSize) {
        this(decodeChunkSize, false);
    }

    public BinaryMemcacheClientCodec(int decodeChunkSize, boolean failOnMissingResponse) {
        this.requestResponseCounter = new AtomicLong();
        this.failOnMissingResponse = failOnMissingResponse;
        init(new Decoder(decodeChunkSize), new Encoder());
    }

    /* loaded from: classes4.dex */
    private final class Encoder extends BinaryMemcacheRequestEncoder {
        private Encoder() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // io.netty.handler.codec.memcache.AbstractMemcacheObjectEncoder, io.netty.handler.codec.MessageToMessageEncoder
        public void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
            super.encode(ctx, msg, out);
            if (BinaryMemcacheClientCodec.this.failOnMissingResponse && (msg instanceof LastMemcacheContent)) {
                BinaryMemcacheClientCodec.this.requestResponseCounter.incrementAndGet();
            }
        }
    }

    /* loaded from: classes4.dex */
    private final class Decoder extends BinaryMemcacheResponseDecoder {
        Decoder(int chunkSize) {
            super(chunkSize);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheDecoder, io.netty.handler.codec.ByteToMessageDecoder
        public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            int oldSize = out.size();
            super.decode(ctx, in, out);
            if (BinaryMemcacheClientCodec.this.failOnMissingResponse) {
                int size = out.size();
                for (int i = oldSize; i < size; i++) {
                    Object msg = out.get(i);
                    if (msg instanceof LastMemcacheContent) {
                        BinaryMemcacheClientCodec.this.requestResponseCounter.decrementAndGet();
                    }
                }
            }
        }

        @Override // io.netty.handler.codec.memcache.binary.AbstractBinaryMemcacheDecoder, io.netty.handler.codec.ByteToMessageDecoder, io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            if (BinaryMemcacheClientCodec.this.failOnMissingResponse) {
                long missingResponses = BinaryMemcacheClientCodec.this.requestResponseCounter.get();
                if (missingResponses > 0) {
                    ctx.fireExceptionCaught((Throwable) new PrematureChannelClosureException("channel gone inactive with " + missingResponses + " missing response(s)"));
                }
            }
        }
    }
}
