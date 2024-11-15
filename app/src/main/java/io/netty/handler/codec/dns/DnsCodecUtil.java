package io.netty.handler.codec.dns;

import com.trossense.bl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;

/* loaded from: classes4.dex */
final class DnsCodecUtil {
    private DnsCodecUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void encodeDomainName(String name, ByteBuf buf) {
        if (".".equals(name)) {
            buf.writeByte(0);
            return;
        }
        String[] labels = name.split("\\.");
        for (String label : labels) {
            int labelLen = label.length();
            if (labelLen == 0) {
                break;
            }
            buf.writeByte(labelLen);
            ByteBufUtil.writeAscii(buf, label);
        }
        buf.writeByte(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String decodeDomainName(ByteBuf in) {
        int position = -1;
        int checked = 0;
        int end = in.writerIndex();
        int readable = in.readableBytes();
        if (readable == 0) {
            return ".";
        }
        StringBuilder name = new StringBuilder(readable << 1);
        while (in.isReadable()) {
            int len = in.readUnsignedByte();
            boolean pointer = (len & bl.cq) == 192;
            if (pointer) {
                if (position == -1) {
                    position = in.readerIndex() + 1;
                }
                if (!in.isReadable()) {
                    throw new CorruptedFrameException("truncated pointer in a name");
                }
                int next = ((len & 63) << 8) | in.readUnsignedByte();
                if (next >= end) {
                    throw new CorruptedFrameException("name has an out-of-range pointer");
                }
                in.readerIndex(next);
                checked += 2;
                if (checked >= end) {
                    throw new CorruptedFrameException("name contains a loop.");
                }
            } else {
                if (len == 0) {
                    break;
                }
                if (!in.isReadable(len)) {
                    throw new CorruptedFrameException("truncated label in a name");
                }
                name.append(in.toString(in.readerIndex(), len, CharsetUtil.UTF_8)).append('.');
                in.skipBytes(len);
            }
        }
        if (position != -1) {
            in.readerIndex(position);
        }
        if (name.length() == 0) {
            return ".";
        }
        if (name.charAt(name.length() - 1) != '.') {
            name.append('.');
        }
        return name.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ByteBuf decompressDomainName(ByteBuf compression) {
        String domainName = decodeDomainName(compression);
        ByteBuf result = compression.alloc().buffer(domainName.length() << 1);
        encodeDomainName(domainName, result);
        return result;
    }
}
