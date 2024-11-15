package io.netty.handler.ssl;

import javax.net.ssl.SSLException;

/* loaded from: classes4.dex */
public class NotSslRecordException extends SSLException {
    private static final long serialVersionUID = -4316784434770656841L;

    public NotSslRecordException() {
        super("");
    }

    public NotSslRecordException(String message) {
        super(message);
    }

    public NotSslRecordException(Throwable cause) {
        super(cause);
    }

    public NotSslRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
