package org.cloudburstmc.protocol.bedrock.codec.v486.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.packet.PlayerStartItemCooldownPacket;
import org.cloudburstmc.protocol.common.util.VarInts;

/* loaded from: classes5.dex */
public class PlayerStartItemCooldownSerializer_v486 implements BedrockPacketSerializer<PlayerStartItemCooldownPacket> {
    public static final PlayerStartItemCooldownSerializer_v486 INSTANCE = new PlayerStartItemCooldownSerializer_v486();

    protected PlayerStartItemCooldownSerializer_v486() {
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, PlayerStartItemCooldownPacket packet) {
        helper.writeString(buffer, packet.getItemCategory());
        VarInts.writeInt(buffer, packet.getCooldownDuration());
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, PlayerStartItemCooldownPacket packet) {
        packet.setItemCategory(helper.readString(buffer));
        packet.setCooldownDuration(VarInts.readInt(buffer));
    }
}
