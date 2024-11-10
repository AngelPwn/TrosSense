package com.google.common.collect;

/* loaded from: classes.dex */
public enum BoundType {
    OPEN { // from class: com.google.common.collect.BoundType.1
        @Override // com.google.common.collect.BoundType
        BoundType flip() {
            return CLOSED;
        }
    },
    CLOSED { // from class: com.google.common.collect.BoundType.2
        @Override // com.google.common.collect.BoundType
        BoundType flip() {
            return OPEN;
        }
    };

    abstract BoundType flip();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BoundType forBoolean(boolean inclusive) {
        return inclusive ? CLOSED : OPEN;
    }
}
