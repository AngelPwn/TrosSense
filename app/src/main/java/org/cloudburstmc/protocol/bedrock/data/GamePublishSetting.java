package org.cloudburstmc.protocol.bedrock.data;

/* loaded from: classes5.dex */
public enum GamePublishSetting {
    NO_MULTI_PLAY,
    INVITE_ONLY,
    FRIENDS_ONLY,
    FRIENDS_OF_FRIENDS,
    PUBLIC;

    public static GamePublishSetting byId(int id) {
        return values()[id];
    }
}
