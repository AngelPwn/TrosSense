package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes4.dex */
public final class SmtpResponseDecoder extends LineBasedFrameDecoder {
    private List<CharSequence> details;

    public SmtpResponseDecoder(int maxLineLength) {
        super(maxLineLength);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.netty.handler.codec.LineBasedFrameDecoder
    public SmtpResponse decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, buffer);
        if (frame == null) {
            return null;
        }
        try {
            int readable = frame.readableBytes();
            int readerIndex = frame.readerIndex();
            if (readable < 3) {
                throw newDecoderException(buffer, readerIndex, readable);
            }
            int code = parseCode(frame);
            int separator = frame.readByte();
            CharSequence detail = frame.isReadable() ? frame.toString(CharsetUtil.US_ASCII) : null;
            List<CharSequence> details = this.details;
            switch (separator) {
                case 32:
                    this.details = null;
                    if (details != null) {
                        if (detail != null) {
                            details.add(detail);
                        }
                    } else {
                        details = detail == null ? Collections.emptyList() : Collections.singletonList(detail);
                    }
                    return new DefaultSmtpResponse(code, details);
                case 45:
                    if (detail != null) {
                        if (details == null) {
                            ArrayList arrayList = new ArrayList(4);
                            details = arrayList;
                            this.details = arrayList;
                        }
                        details.add(detail);
                    }
                    return null;
                default:
                    throw newDecoderException(buffer, readerIndex, readable);
            }
        } finally {
            frame.release();
        }
    }

    private static DecoderException newDecoderException(ByteBuf buffer, int readerIndex, int readable) {
        return new DecoderException("Received invalid line: '" + buffer.toString(readerIndex, readable, CharsetUtil.US_ASCII) + '\'');
    }

    private static int parseCode(ByteBuf buffer) {
        int first = parseNumber(buffer.readByte()) * 100;
        int second = parseNumber(buffer.readByte()) * 10;
        int third = parseNumber(buffer.readByte());
        return first + second + third;
    }

    private static int parseNumber(byte b) {
        return Character.digit((char) b, 10);
    }
}
