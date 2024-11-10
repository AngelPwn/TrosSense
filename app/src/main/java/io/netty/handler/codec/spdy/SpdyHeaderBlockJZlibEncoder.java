package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.util.internal.ObjectUtil;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class SpdyHeaderBlockJZlibEncoder extends SpdyHeaderBlockRawEncoder {
    private boolean finished;
    private final Deflater z;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpdyHeaderBlockJZlibEncoder(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
        super(version);
        this.z = new Deflater();
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        }
        if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        }
        int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
        if (resultCode != 0) {
            throw new CompressionException("failed to initialize an SPDY header block deflater: " + resultCode);
        }
        int resultCode2 = this.z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
        if (resultCode2 != 0) {
            throw new CompressionException("failed to set the SPDY dictionary: " + resultCode2);
        }
    }

    private void setInput(ByteBuf decompressed) {
        byte[] in;
        int offset;
        int len = decompressed.readableBytes();
        if (decompressed.hasArray()) {
            in = decompressed.array();
            offset = decompressed.arrayOffset() + decompressed.readerIndex();
        } else {
            in = new byte[len];
            decompressed.getBytes(decompressed.readerIndex(), in);
            offset = 0;
        }
        this.z.next_in = in;
        this.z.next_in_index = offset;
        this.z.avail_in = len;
    }

    private ByteBuf encode(ByteBufAllocator alloc) {
        boolean release = true;
        ByteBuf out = null;
        try {
            int oldNextInIndex = this.z.next_in_index;
            int oldNextOutIndex = this.z.next_out_index;
            int maxOutputLength = ((int) Math.ceil(this.z.next_in.length * 1.001d)) + 12;
            out = alloc.heapBuffer(maxOutputLength);
            this.z.next_out = out.array();
            this.z.next_out_index = out.arrayOffset() + out.writerIndex();
            this.z.avail_out = maxOutputLength;
            try {
                int resultCode = this.z.deflate(2);
                if (resultCode != 0) {
                    throw new CompressionException("compression failure: " + resultCode);
                }
                int outputLength = this.z.next_out_index - oldNextOutIndex;
                if (outputLength > 0) {
                    out.writerIndex(out.writerIndex() + outputLength);
                }
                release = false;
                return out;
            } finally {
                out.skipBytes(this.z.next_in_index - oldNextInIndex);
            }
        } finally {
            this.z.next_in = null;
            this.z.next_out = null;
            if (release && out != null) {
                out.release();
            }
        }
    }

    @Override // io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder, io.netty.handler.codec.spdy.SpdyHeaderBlockEncoder
    public ByteBuf encode(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        ObjectUtil.checkNotNullWithIAE(alloc, "alloc");
        ObjectUtil.checkNotNullWithIAE(frame, "frame");
        if (this.finished) {
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBuf decompressed = super.encode(alloc, frame);
        try {
            if (!decompressed.isReadable()) {
                return Unpooled.EMPTY_BUFFER;
            }
            setInput(decompressed);
            return encode(alloc);
        } finally {
            decompressed.release();
        }
    }

    @Override // io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder, io.netty.handler.codec.spdy.SpdyHeaderBlockEncoder
    public void end() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        this.z.deflateEnd();
        this.z.next_in = null;
        this.z.next_out = null;
    }
}
