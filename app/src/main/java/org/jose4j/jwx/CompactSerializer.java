package org.jose4j.jwx;

/* loaded from: classes5.dex */
public class CompactSerializer {
    private static final String EMPTY_STRING = "";
    private static final String PERIOD_SEPARATOR = ".";
    private static final String PERIOD_SEPARATOR_REGEX = "\\.";

    public static String[] deserialize(String compactSerialization) {
        String[] parts = compactSerialization.split(PERIOD_SEPARATOR_REGEX);
        if (compactSerialization.endsWith(PERIOD_SEPARATOR)) {
            String[] tempParts = new String[parts.length + 1];
            System.arraycopy(parts, 0, tempParts, 0, parts.length);
            tempParts[parts.length] = "";
            return tempParts;
        }
        return parts;
    }

    public static String serialize(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i] == null ? "" : parts[i];
            sb.append(part);
            if (i != parts.length - 1) {
                sb.append(PERIOD_SEPARATOR);
            }
        }
        return sb.toString();
    }
}
