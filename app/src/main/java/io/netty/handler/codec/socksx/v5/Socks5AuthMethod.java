package io.netty.handler.codec.socksx.v5;

import io.netty.util.internal.ObjectUtil;

/* loaded from: classes4.dex */
public class Socks5AuthMethod implements Comparable<Socks5AuthMethod> {
    private final byte byteValue;
    private final String name;
    private String text;
    public static final Socks5AuthMethod NO_AUTH = new Socks5AuthMethod(0, "NO_AUTH");
    public static final Socks5AuthMethod GSSAPI = new Socks5AuthMethod(1, "GSSAPI");
    public static final Socks5AuthMethod PASSWORD = new Socks5AuthMethod(2, "PASSWORD");
    public static final Socks5AuthMethod UNACCEPTED = new Socks5AuthMethod(255, "UNACCEPTED");

    public static Socks5AuthMethod valueOf(byte b) {
        switch (b) {
            case -1:
                return UNACCEPTED;
            case 0:
                return NO_AUTH;
            case 1:
                return GSSAPI;
            case 2:
                return PASSWORD;
            default:
                return new Socks5AuthMethod(b);
        }
    }

    public Socks5AuthMethod(int byteValue) {
        this(byteValue, "UNKNOWN");
    }

    public Socks5AuthMethod(int byteValue, String name) {
        this.name = (String) ObjectUtil.checkNotNull(name, "name");
        this.byteValue = (byte) byteValue;
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public int hashCode() {
        return this.byteValue;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Socks5AuthMethod) && this.byteValue == ((Socks5AuthMethod) obj).byteValue;
    }

    @Override // java.lang.Comparable
    public int compareTo(Socks5AuthMethod o) {
        return this.byteValue - o.byteValue;
    }

    public String toString() {
        String text = this.text;
        if (text == null) {
            String text2 = this.name + '(' + (this.byteValue & 255) + ')';
            this.text = text2;
            return text2;
        }
        return text;
    }
}
