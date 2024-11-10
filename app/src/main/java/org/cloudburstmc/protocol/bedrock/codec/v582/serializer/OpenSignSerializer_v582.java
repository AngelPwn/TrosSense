package org.cloudburstmc.protocol.bedrock.codec.v582.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.packet.OpenSignPacket;

/* loaded from: classes5.dex */
public class OpenSignSerializer_v582 implements BedrockPacketSerializer<OpenSignPacket> {
    public static final OpenSignSerializer_v582 INSTANCE = new OpenSignSerializer_v582();

    protected OpenSignSerializer_v582() {
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, OpenSignPacket packet) {
        helper.writeBlockPosition(buffer, packet.getPosition());
        buffer.writeBoolean(packet.isFrontSide());
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, OpenSignPacket packet) {
        packet.setPosition(helper.readBlockPosition(buffer));
        packet.setFrontSide(buffer.readBoolean());
    }
}
