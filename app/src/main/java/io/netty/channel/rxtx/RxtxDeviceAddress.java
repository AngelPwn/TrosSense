package io.netty.channel.rxtx;

import java.net.SocketAddress;

@Deprecated
/* loaded from: classes4.dex */
public class RxtxDeviceAddress extends SocketAddress {
    private static final long serialVersionUID = -2907820090993709523L;
    private final String value;

    public RxtxDeviceAddress(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
