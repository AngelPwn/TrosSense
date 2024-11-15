package org.cloudburstmc.protocol.bedrock.codec.v313.serializer;

import io.netty.buffer.ByteBuf;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer;
import org.cloudburstmc.protocol.bedrock.packet.BiomeDefinitionListPacket;

/* loaded from: classes5.dex */
public class BiomeDefinitionListSerializer_v313 implements BedrockPacketSerializer<BiomeDefinitionListPacket> {
    public static final BiomeDefinitionListSerializer_v313 INSTANCE = new BiomeDefinitionListSerializer_v313();

    protected BiomeDefinitionListSerializer_v313() {
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void serialize(ByteBuf buffer, BedrockCodecHelper helper, BiomeDefinitionListPacket packet) {
        helper.writeTag(buffer, packet.getDefinitions());
    }

    @Override // org.cloudburstmc.protocol.bedrock.codec.BedrockPacketSerializer
    public void deserialize(ByteBuf buffer, BedrockCodecHelper helper, BiomeDefinitionListPacket packet) {
        packet.setDefinitions((NbtMap) helper.readTag(buffer, NbtMap.class));
    }
}
