package com.google.thirdparty.publicsuffix;

import io.netty.util.internal.StringUtil;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public enum PublicSuffixType {
    PRIVATE(':', StringUtil.COMMA),
    ICANN('!', '?');

    private final char innerNodeCode;
    private final char leafNodeCode;

    PublicSuffixType(char innerNodeCode, char leafNodeCode) {
        this.innerNodeCode = innerNodeCode;
        this.leafNodeCode = leafNodeCode;
    }

    char getLeafNodeCode() {
        return this.leafNodeCode;
    }

    char getInnerNodeCode() {
        return this.innerNodeCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static PublicSuffixType fromCode(char code) {
        for (PublicSuffixType value : values()) {
            if (value.getInnerNodeCode() == code || value.getLeafNodeCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum corresponding to given code: " + code);
    }

    static PublicSuffixType fromIsPrivate(boolean isPrivate) {
        return isPrivate ? PRIVATE : ICANN;
    }
}
