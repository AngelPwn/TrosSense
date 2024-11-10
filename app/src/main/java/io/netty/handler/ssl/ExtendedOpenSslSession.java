package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.security.cert.X509Certificate;
import org.jose4j.jws.RsaUsingShaAlgorithm;

/* loaded from: classes4.dex */
abstract class ExtendedOpenSslSession extends ExtendedSSLSession implements OpenSslSession {
    private static final String[] LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS = {"SHA512withRSA", "SHA512withECDSA", "SHA384withRSA", "SHA384withECDSA", "SHA256withRSA", "SHA256withECDSA", "SHA224withRSA", "SHA224withECDSA", "SHA1withRSA", "SHA1withECDSA", RsaUsingShaAlgorithm.RSASSA_PSS};
    private final OpenSslSession wrapped;

    @Override // javax.net.ssl.ExtendedSSLSession
    public abstract List getRequestedServerNames();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExtendedOpenSslSession(OpenSslSession wrapped) {
        this.wrapped = wrapped;
    }

    public List<byte[]> getStatusResponses() {
        return Collections.emptyList();
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public void prepareHandshake() {
        this.wrapped.prepareHandshake();
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public Map<String, Object> keyValueStorage() {
        return this.wrapped.keyValueStorage();
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public OpenSslSessionId sessionId() {
        return this.wrapped.sessionId();
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public void setSessionDetails(long creationTime, long lastAccessedTime, OpenSslSessionId id, Map<String, Object> keyValueStorage) {
        this.wrapped.setSessionDetails(creationTime, lastAccessedTime, id, keyValueStorage);
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public final void setLocalCertificate(Certificate[] localCertificate) {
        this.wrapped.setLocalCertificate(localCertificate);
    }

    @Override // javax.net.ssl.ExtendedSSLSession
    public String[] getPeerSupportedSignatureAlgorithms() {
        return EmptyArrays.EMPTY_STRINGS;
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public final void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
        this.wrapped.tryExpandApplicationBufferSize(packetLengthDataOnly);
    }

    @Override // javax.net.ssl.ExtendedSSLSession
    public final String[] getLocalSupportedSignatureAlgorithms() {
        return (String[]) LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS.clone();
    }

    @Override // javax.net.ssl.SSLSession
    public final byte[] getId() {
        return this.wrapped.getId();
    }

    @Override // javax.net.ssl.SSLSession
    public final OpenSslSessionContext getSessionContext() {
        return this.wrapped.getSessionContext();
    }

    @Override // javax.net.ssl.SSLSession
    public final long getCreationTime() {
        return this.wrapped.getCreationTime();
    }

    @Override // javax.net.ssl.SSLSession
    public final long getLastAccessedTime() {
        return this.wrapped.getLastAccessedTime();
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public void setLastAccessedTime(long time) {
        this.wrapped.setLastAccessedTime(time);
    }

    @Override // javax.net.ssl.SSLSession
    public final void invalidate() {
        this.wrapped.invalidate();
    }

    @Override // javax.net.ssl.SSLSession
    public final boolean isValid() {
        return this.wrapped.isValid();
    }

    @Override // javax.net.ssl.SSLSession
    public final void putValue(String name, Object value) {
        if (value instanceof SSLSessionBindingListener) {
            value = new SSLSessionBindingListenerDecorator((SSLSessionBindingListener) value);
        }
        this.wrapped.putValue(name, value);
    }

    @Override // javax.net.ssl.SSLSession
    public final Object getValue(String s) {
        Object value = this.wrapped.getValue(s);
        if (value instanceof SSLSessionBindingListenerDecorator) {
            return ((SSLSessionBindingListenerDecorator) value).delegate;
        }
        return value;
    }

    @Override // javax.net.ssl.SSLSession
    public final void removeValue(String s) {
        this.wrapped.removeValue(s);
    }

    @Override // javax.net.ssl.SSLSession
    public final String[] getValueNames() {
        return this.wrapped.getValueNames();
    }

    @Override // javax.net.ssl.SSLSession
    public final Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        return this.wrapped.getPeerCertificates();
    }

    @Override // javax.net.ssl.SSLSession
    public final Certificate[] getLocalCertificates() {
        return this.wrapped.getLocalCertificates();
    }

    @Override // javax.net.ssl.SSLSession
    public final X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        return this.wrapped.getPeerCertificateChain();
    }

    @Override // javax.net.ssl.SSLSession
    public final Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return this.wrapped.getPeerPrincipal();
    }

    @Override // javax.net.ssl.SSLSession
    public final Principal getLocalPrincipal() {
        return this.wrapped.getLocalPrincipal();
    }

    @Override // javax.net.ssl.SSLSession
    public final String getCipherSuite() {
        return this.wrapped.getCipherSuite();
    }

    @Override // javax.net.ssl.SSLSession
    public String getProtocol() {
        return this.wrapped.getProtocol();
    }

    @Override // javax.net.ssl.SSLSession
    public final String getPeerHost() {
        return this.wrapped.getPeerHost();
    }

    @Override // javax.net.ssl.SSLSession
    public final int getPeerPort() {
        return this.wrapped.getPeerPort();
    }

    @Override // javax.net.ssl.SSLSession
    public final int getPacketBufferSize() {
        return this.wrapped.getPacketBufferSize();
    }

    @Override // javax.net.ssl.SSLSession
    public final int getApplicationBufferSize() {
        return this.wrapped.getApplicationBufferSize();
    }

    /* loaded from: classes4.dex */
    private final class SSLSessionBindingListenerDecorator implements SSLSessionBindingListener {
        final SSLSessionBindingListener delegate;

        SSLSessionBindingListenerDecorator(SSLSessionBindingListener delegate) {
            this.delegate = delegate;
        }

        @Override // javax.net.ssl.SSLSessionBindingListener
        public void valueBound(SSLSessionBindingEvent event) {
            this.delegate.valueBound(new SSLSessionBindingEvent(ExtendedOpenSslSession.this, event.getName()));
        }

        @Override // javax.net.ssl.SSLSessionBindingListener
        public void valueUnbound(SSLSessionBindingEvent event) {
            this.delegate.valueUnbound(new SSLSessionBindingEvent(ExtendedOpenSslSession.this, event.getName()));
        }
    }

    @Override // io.netty.handler.ssl.OpenSslSession
    public void handshakeFinished(byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout) throws SSLException {
        this.wrapped.handshakeFinished(id, cipher, protocol, peerCertificate, peerCertificateChain, creationTime, timeout);
    }

    public boolean equals(Object o) {
        return this.wrapped.equals(o);
    }

    public int hashCode() {
        return this.wrapped.hashCode();
    }

    public String toString() {
        return "ExtendedOpenSslSession{wrapped=" + this.wrapped + '}';
    }
}
