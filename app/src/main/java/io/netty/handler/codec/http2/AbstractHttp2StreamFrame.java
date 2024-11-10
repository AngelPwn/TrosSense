package io.netty.handler.codec.http2;

/* loaded from: classes4.dex */
public abstract class AbstractHttp2StreamFrame implements Http2StreamFrame {
    private Http2FrameStream stream;

    @Override // io.netty.handler.codec.http2.Http2StreamFrame
    public AbstractHttp2StreamFrame stream(Http2FrameStream stream) {
        this.stream = stream;
        return this;
    }

    @Override // io.netty.handler.codec.http2.Http2StreamFrame
    public Http2FrameStream stream() {
        return this.stream;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Http2StreamFrame)) {
            return false;
        }
        Http2StreamFrame other = (Http2StreamFrame) o;
        return this.stream == other.stream() || (this.stream != null && this.stream.equals(other.stream()));
    }

    public int hashCode() {
        Http2FrameStream stream = this.stream;
        if (stream == null) {
            return super.hashCode();
        }
        return stream.hashCode();
    }
}
