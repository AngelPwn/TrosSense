package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/* loaded from: classes4.dex */
public class SnappyFrameEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final int MIN_COMPRESSIBLE_LENGTH = 18;
    private static final int SNAPPY_SLICE_JUMBO_SIZE = 65535;
    private static final short SNAPPY_SLICE_SIZE = Short.MAX_VALUE;
    private static final byte[] STREAM_START = {-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    private final int sliceSize;
    private final Snappy snappy;
    private boolean started;

    public SnappyFrameEncoder() {
        this(32767);
    }

    public static SnappyFrameEncoder snappyEncoderWithJumboFrames() {
        return new SnappyFrameEncoder(65535);
    }

    private SnappyFrameEncoder(int sliceSize) {
        this.snappy = new Snappy();
        this.sliceSize = sliceSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.netty.handler.codec.MessageToByteEncoder
    public void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        if (!in.isReadable()) {
            return;
        }
        if (!this.started) {
            this.started = true;
            out.writeBytes(STREAM_START);
        }
        int dataLength = in.readableBytes();
        if (dataLength <= 18) {
            writeUnencodedChunk(in, out, dataLength);
            return;
        }
        while (true) {
            int lengthIdx = out.writerIndex() + 1;
            if (dataLength < 18) {
                writeUnencodedChunk(in.readSlice(dataLength), out, dataLength);
                return;
            }
            out.writeInt(0);
            if (dataLength > this.sliceSize) {
                ByteBuf slice = in.readSlice(this.sliceSize);
                calculateAndWriteChecksum(slice, out);
                this.snappy.encode(slice, out, this.sliceSize);
                setChunkLength(out, lengthIdx);
                dataLength -= this.sliceSize;
            } else {
                ByteBuf slice2 = in.readSlice(dataLength);
                calculateAndWriteChecksum(slice2, out);
                this.snappy.encode(slice2, out, dataLength);
                setChunkLength(out, lengthIdx);
                return;
            }
        }
    }

    private static void writeUnencodedChunk(ByteBuf in, ByteBuf out, int dataLength) {
        out.writeByte(1);
        writeChunkLength(out, dataLength + 4);
        calculateAndWriteChecksum(in, out);
        out.writeBytes(in, dataLength);
    }

    private static void setChunkLength(ByteBuf out, int lengthIdx) {
        int chunkLength = (out.writerIndex() - lengthIdx) - 3;
        if ((chunkLength >>> 24) != 0) {
            throw new CompressionException("compressed data too large: " + chunkLength);
        }
        out.setMediumLE(lengthIdx, chunkLength);
    }

    private static void writeChunkLength(ByteBuf out, int chunkLength) {
        out.writeMediumLE(chunkLength);
    }

    private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
        out.writeIntLE(Snappy.calculateChecksum(slice));
    }
}
