package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.NetUtil;

@ChannelHandler.Sharable
/* loaded from: classes4.dex */
public final class Socks4ServerEncoder extends MessageToByteEncoder<Socks4CommandResponse> {
    public static final Socks4ServerEncoder INSTANCE = new Socks4ServerEncoder();
    private static final byte[] IPv4_HOSTNAME_ZEROED = {0, 0, 0, 0};

    private Socks4ServerEncoder() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // io.netty.handler.codec.MessageToByteEncoder
    public void encode(ChannelHandlerContext ctx, Socks4CommandResponse msg, ByteBuf out) throws Exception {
        out.writeByte(0);
        out.writeByte(msg.status().byteValue());
        ByteBufUtil.writeShortBE(out, msg.dstPort());
        out.writeBytes(msg.dstAddr() == null ? IPv4_HOSTNAME_ZEROED : NetUtil.createByteArrayFromIpAddressString(msg.dstAddr()));
    }
}
