package io.netty.handler.ssl;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

/* loaded from: classes4.dex */
public abstract class SslMasterKeyHandler extends ChannelInboundHandlerAdapter {
    private static final Class<?> SSL_SESSIONIMPL_CLASS;
    private static final Field SSL_SESSIONIMPL_MASTER_SECRET_FIELD;
    public static final String SYSTEM_PROP_KEY = "io.netty.ssl.masterKeyHandler";
    private static final Throwable UNAVAILABILITY_CAUSE;
    private static final InternalLogger logger = InternalLoggerFactory.getInstance((Class<?>) SslMasterKeyHandler.class);

    protected abstract void accept(SecretKey secretKey, SSLSession sSLSession);

    static {
        Throwable cause;
        Class<?> clazz = null;
        Field field = null;
        try {
            clazz = Class.forName("sun.security.ssl.SSLSessionImpl");
            field = clazz.getDeclaredField("masterSecret");
            cause = ReflectionUtil.trySetAccessible(field, true);
        } catch (Throwable e) {
            if (logger.isTraceEnabled()) {
                logger.debug("sun.security.ssl.SSLSessionImpl is unavailable.", e);
            } else {
                logger.debug("sun.security.ssl.SSLSessionImpl is unavailable: {}", e.getMessage());
            }
            cause = e;
        }
        UNAVAILABILITY_CAUSE = cause;
        SSL_SESSIONIMPL_CLASS = clazz;
        SSL_SESSIONIMPL_MASTER_SECRET_FIELD = field;
    }

    protected SslMasterKeyHandler() {
    }

    public static void ensureSunSslEngineAvailability() {
        if (UNAVAILABILITY_CAUSE != null) {
            throw new IllegalStateException("Failed to find SSLSessionImpl on classpath", UNAVAILABILITY_CAUSE);
        }
    }

    public static Throwable sunSslEngineUnavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    public static boolean isSunSslEngineAvailable() {
        return UNAVAILABILITY_CAUSE == null;
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt == SslHandshakeCompletionEvent.SUCCESS && masterKeyHandlerEnabled()) {
            SslHandler handler = (SslHandler) ctx.pipeline().get(SslHandler.class);
            SSLEngine engine = handler.engine();
            SSLSession sslSession = engine.getSession();
            if (isSunSslEngineAvailable() && sslSession.getClass().equals(SSL_SESSIONIMPL_CLASS)) {
                try {
                    SecretKey secretKey = (SecretKey) SSL_SESSIONIMPL_MASTER_SECRET_FIELD.get(sslSession);
                    accept(secretKey, sslSession);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Failed to access the field 'masterSecret' via reflection.", e);
                }
            } else if (OpenSsl.isAvailable() && (engine instanceof ReferenceCountedOpenSslEngine)) {
                SecretKeySpec secretKey2 = ((ReferenceCountedOpenSslEngine) engine).masterKey();
                accept(secretKey2, sslSession);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    protected boolean masterKeyHandlerEnabled() {
        return SystemPropertyUtil.getBoolean(SYSTEM_PROP_KEY, false);
    }

    public static SslMasterKeyHandler newWireSharkSslMasterKeyHandler() {
        return new WiresharkSslMasterKeyHandler();
    }

    /* loaded from: classes4.dex */
    private static final class WiresharkSslMasterKeyHandler extends SslMasterKeyHandler {
        private static final InternalLogger wireshark_logger = InternalLoggerFactory.getInstance("io.netty.wireshark");

        private WiresharkSslMasterKeyHandler() {
        }

        @Override // io.netty.handler.ssl.SslMasterKeyHandler
        protected void accept(SecretKey masterKey, SSLSession session) {
            if (masterKey.getEncoded().length != 48) {
                throw new IllegalArgumentException("An invalid length master key was provided.");
            }
            byte[] sessionId = session.getId();
            wireshark_logger.warn("RSA Session-ID:{} Master-Key:{}", ByteBufUtil.hexDump(sessionId).toLowerCase(), ByteBufUtil.hexDump(masterKey.getEncoded()).toLowerCase());
        }
    }
}
