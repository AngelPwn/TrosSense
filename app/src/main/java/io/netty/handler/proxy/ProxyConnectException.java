package io.netty.handler.proxy;

import java.net.ConnectException;

/* loaded from: classes4.dex */
public class ProxyConnectException extends ConnectException {
    private static final long serialVersionUID = 5211364632246265538L;

    public ProxyConnectException() {
    }

    public ProxyConnectException(String msg) {
        super(msg);
    }

    public ProxyConnectException(Throwable cause) {
        initCause(cause);
    }

    public ProxyConnectException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }
}
