package org.cloudburstmc.protocol.bedrock.data;

/* loaded from: classes5.dex */
public enum InputMode {
    UNDEFINED,
    MOUSE,
    TOUCH,
    GAMEPAD,
    MOTION_CONTROLLER;

    private static final InputMode[] VALUES = values();

    public static InputMode from(int id) {
        return VALUES[id];
    }
}
