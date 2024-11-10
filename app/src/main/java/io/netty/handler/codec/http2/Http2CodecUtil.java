package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http2.StreamByteDistributor;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.TimeUnit;

/* loaded from: classes4.dex */
public final class Http2CodecUtil {
    public static final int CONNECTION_STREAM_ID = 0;
    public static final int CONTINUATION_FRAME_HEADER_LENGTH = 10;
    public static final int DATA_FRAME_HEADER_LENGTH = 10;
    public static final long DEFAULT_HEADER_LIST_SIZE = 8192;
    public static final int DEFAULT_HEADER_TABLE_SIZE = 4096;
    public static final int DEFAULT_MAX_FRAME_SIZE = 16384;
    public static final int DEFAULT_MAX_QUEUED_CONTROL_FRAMES = 10000;
    static final int DEFAULT_MAX_RESERVED_STREAMS = 100;
    static final int DEFAULT_MIN_ALLOCATION_CHUNK = 1024;
    public static final short DEFAULT_PRIORITY_WEIGHT = 16;
    public static final int DEFAULT_WINDOW_SIZE = 65535;
    public static final int FRAME_HEADER_LENGTH = 9;
    public static final int GO_AWAY_FRAME_HEADER_LENGTH = 17;
    public static final int HEADERS_FRAME_HEADER_LENGTH = 15;
    public static final int HTTP_UPGRADE_STREAM_ID = 1;
    public static final int INT_FIELD_LENGTH = 4;
    public static final long MAX_CONCURRENT_STREAMS = 4294967295L;
    public static final int MAX_FRAME_SIZE_LOWER_BOUND = 16384;
    public static final int MAX_FRAME_SIZE_UPPER_BOUND = 16777215;
    public static final long MAX_HEADER_LIST_SIZE = 4294967295L;
    public static final long MAX_HEADER_TABLE_SIZE = 4294967295L;
    public static final int MAX_INITIAL_WINDOW_SIZE = Integer.MAX_VALUE;
    public static final int MAX_PADDING = 256;
    private static final int MAX_PADDING_LENGTH_LENGTH = 1;
    public static final short MAX_UNSIGNED_BYTE = 255;
    public static final long MAX_UNSIGNED_INT = 4294967295L;
    public static final short MAX_WEIGHT = 256;
    public static final long MIN_CONCURRENT_STREAMS = 0;
    public static final long MIN_HEADER_LIST_SIZE = 0;
    public static final long MIN_HEADER_TABLE_SIZE = 0;
    public static final int MIN_INITIAL_WINDOW_SIZE = 0;
    public static final short MIN_WEIGHT = 1;
    public static final int NUM_STANDARD_SETTINGS = 6;
    public static final int PING_FRAME_PAYLOAD_LENGTH = 8;
    public static final int PRIORITY_ENTRY_LENGTH = 5;
    public static final int PRIORITY_FRAME_LENGTH = 14;
    public static final int PUSH_PROMISE_FRAME_HEADER_LENGTH = 14;
    public static final int RST_STREAM_FRAME_LENGTH = 13;
    public static final char SETTINGS_ENABLE_PUSH = 2;
    public static final char SETTINGS_HEADER_TABLE_SIZE = 1;
    public static final char SETTINGS_INITIAL_WINDOW_SIZE = 4;
    public static final char SETTINGS_MAX_CONCURRENT_STREAMS = 3;
    public static final char SETTINGS_MAX_FRAME_SIZE = 5;
    public static final char SETTINGS_MAX_HEADER_LIST_SIZE = 6;
    public static final int SETTING_ENTRY_LENGTH = 6;
    public static final int SMALLEST_MAX_CONCURRENT_STREAMS = 100;
    public static final int WINDOW_UPDATE_FRAME_LENGTH = 13;
    public static final CharSequence HTTP_UPGRADE_SETTINGS_HEADER = AsciiString.cached("HTTP2-Settings");
    public static final CharSequence HTTP_UPGRADE_PROTOCOL_NAME = "h2c";
    public static final CharSequence TLS_UPGRADE_PROTOCOL_NAME = ApplicationProtocolNames.HTTP_2;
    private static final ByteBuf CONNECTION_PREFACE = Unpooled.unreleasableBuffer(Unpooled.directBuffer(24).writeBytes("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(CharsetUtil.UTF_8))).asReadOnly();
    public static final long DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS = TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS);

    public static long calculateMaxHeaderListSizeGoAway(long maxHeaderListSize) {
        return (maxHeaderListSize >>> 2) + maxHeaderListSize;
    }

    public static boolean isOutboundStream(boolean server, int streamId) {
        boolean even = (streamId & 1) == 0;
        return streamId > 0 && server == even;
    }

    public static boolean isStreamIdValid(int streamId) {
        return streamId >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isStreamIdValid(int streamId, boolean server) {
        if (isStreamIdValid(streamId)) {
            return server == ((streamId & 1) == 0);
        }
        return false;
    }

    public static boolean isMaxFrameSizeValid(int maxFrameSize) {
        return maxFrameSize >= 16384 && maxFrameSize <= 16777215;
    }

    public static ByteBuf connectionPrefaceBuf() {
        return CONNECTION_PREFACE.retainedDuplicate();
    }

    public static Http2Exception getEmbeddedHttp2Exception(Throwable cause) {
        while (cause != null) {
            if (cause instanceof Http2Exception) {
                return (Http2Exception) cause;
            }
            cause = cause.getCause();
        }
        return null;
    }

    public static ByteBuf toByteBuf(ChannelHandlerContext ctx, Throwable cause) {
        if (cause == null || cause.getMessage() == null) {
            return Unpooled.EMPTY_BUFFER;
        }
        return ByteBufUtil.writeUtf8(ctx.alloc(), cause.getMessage());
    }

    public static int readUnsignedInt(ByteBuf buf) {
        return buf.readInt() & Integer.MAX_VALUE;
    }

    public static void writeFrameHeader(ByteBuf out, int payloadLength, byte type, Http2Flags flags, int streamId) {
        out.ensureWritable(payloadLength + 9);
        writeFrameHeaderInternal(out, payloadLength, type, flags, streamId);
    }

    public static int streamableBytes(StreamByteDistributor.StreamState state) {
        return Math.max(0, (int) Math.min(state.pendingBytes(), state.windowSize()));
    }

    public static void headerListSizeExceeded(int streamId, long maxHeaderListSize, boolean onDecode) throws Http2Exception {
        throw Http2Exception.headerListSizeError(streamId, Http2Error.PROTOCOL_ERROR, onDecode, "Header size exceeded max allowed size (%d)", Long.valueOf(maxHeaderListSize));
    }

    public static void headerListSizeExceeded(long maxHeaderListSize) throws Http2Exception {
        throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Header size exceeded max allowed size (%d)", Long.valueOf(maxHeaderListSize));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeFrameHeaderInternal(ByteBuf out, int payloadLength, byte type, Http2Flags flags, int streamId) {
        out.writeMedium(payloadLength);
        out.writeByte(type);
        out.writeByte(flags.value());
        out.writeInt(streamId);
    }

    /* loaded from: classes4.dex */
    static final class SimpleChannelPromiseAggregator extends DefaultChannelPromise {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private Throwable aggregateFailure;
        private boolean doneAllocating;
        private int doneCount;
        private int expectedCount;
        private final ChannelPromise promise;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SimpleChannelPromiseAggregator(ChannelPromise promise, Channel c, EventExecutor e) {
            super(c, e);
            if (promise == null || promise.isDone()) {
                throw new AssertionError();
            }
            this.promise = promise;
        }

        public ChannelPromise newPromise() {
            if (this.doneAllocating) {
                throw new AssertionError("Done allocating. No more promises can be allocated.");
            }
            this.expectedCount++;
            return this;
        }

        public ChannelPromise doneAllocatingPromises() {
            if (!this.doneAllocating) {
                this.doneAllocating = true;
                if (this.doneCount == this.expectedCount || this.expectedCount == 0) {
                    return setPromise();
                }
            }
            return this;
        }

        @Override // io.netty.util.concurrent.DefaultPromise, io.netty.util.concurrent.Promise
        public boolean tryFailure(Throwable cause) {
            if (allowFailure()) {
                this.doneCount++;
                setAggregateFailure(cause);
                if (allPromisesDone()) {
                    return tryPromise();
                }
                return true;
            }
            return false;
        }

        @Override // io.netty.channel.DefaultChannelPromise, io.netty.util.concurrent.DefaultPromise, io.netty.util.concurrent.Promise, io.netty.channel.ChannelPromise
        public ChannelPromise setFailure(Throwable cause) {
            if (allowFailure()) {
                this.doneCount++;
                setAggregateFailure(cause);
                if (allPromisesDone()) {
                    return setPromise();
                }
            }
            return this;
        }

        @Override // io.netty.channel.DefaultChannelPromise, io.netty.util.concurrent.DefaultPromise, io.netty.util.concurrent.Promise, io.netty.util.concurrent.ProgressivePromise
        public ChannelPromise setSuccess(Void result) {
            if (awaitingPromises()) {
                this.doneCount++;
                if (allPromisesDone()) {
                    setPromise();
                }
            }
            return this;
        }

        @Override // io.netty.util.concurrent.DefaultPromise, io.netty.util.concurrent.Promise
        public boolean trySuccess(Void result) {
            if (awaitingPromises()) {
                this.doneCount++;
                if (allPromisesDone()) {
                    return tryPromise();
                }
                return true;
            }
            return false;
        }

        private boolean allowFailure() {
            return awaitingPromises() || this.expectedCount == 0;
        }

        private boolean awaitingPromises() {
            return this.doneCount < this.expectedCount;
        }

        private boolean allPromisesDone() {
            return this.doneCount == this.expectedCount && this.doneAllocating;
        }

        private ChannelPromise setPromise() {
            if (this.aggregateFailure == null) {
                this.promise.setSuccess();
                return super.setSuccess((Void) null);
            }
            this.promise.setFailure(this.aggregateFailure);
            return super.setFailure(this.aggregateFailure);
        }

        private boolean tryPromise() {
            if (this.aggregateFailure == null) {
                this.promise.trySuccess();
                return super.trySuccess((SimpleChannelPromiseAggregator) null);
            }
            this.promise.tryFailure(this.aggregateFailure);
            return super.tryFailure(this.aggregateFailure);
        }

        private void setAggregateFailure(Throwable cause) {
            if (this.aggregateFailure == null) {
                this.aggregateFailure = cause;
            }
        }
    }

    public static void verifyPadding(int padding) {
        if (padding < 0 || padding > 256) {
            throw new IllegalArgumentException(String.format("Invalid padding '%d'. Padding must be between 0 and %d (inclusive).", Integer.valueOf(padding), 256));
        }
    }

    private Http2CodecUtil() {
    }
}
