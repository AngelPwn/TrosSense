package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes4.dex */
public final class FingerprintTrustManagerFactory extends SimpleTrustManagerFactory {
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("^[0-9a-fA-F:]+$");
    private static final Pattern FINGERPRINT_STRIP_PATTERN = Pattern.compile(":");
    private final byte[][] fingerprints;
    private final FastThreadLocal<MessageDigest> tlmd;
    private final TrustManager tm;

    public static FingerprintTrustManagerFactoryBuilder builder(String algorithm) {
        return new FingerprintTrustManagerFactoryBuilder(algorithm);
    }

    @Deprecated
    public FingerprintTrustManagerFactory(Iterable<String> fingerprints) {
        this("SHA1", toFingerprintArray(fingerprints));
    }

    @Deprecated
    public FingerprintTrustManagerFactory(String... fingerprints) {
        this("SHA1", toFingerprintArray(Arrays.asList(fingerprints)));
    }

    @Deprecated
    public FingerprintTrustManagerFactory(byte[]... fingerprints) {
        this("SHA1", fingerprints);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public FingerprintTrustManagerFactory(final String algorithm, byte[][] fingerprints) {
        this.tm = new X509TrustManager() { // from class: io.netty.handler.ssl.util.FingerprintTrustManagerFactory.1
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String s) throws CertificateException {
                checkTrusted("client", chain);
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
                checkTrusted("server", chain);
            }

            private void checkTrusted(String type, X509Certificate[] chain) throws CertificateException {
                int i = 0;
                X509Certificate cert = chain[0];
                byte[] fingerprint = fingerprint(cert);
                boolean found = false;
                byte[][] bArr = FingerprintTrustManagerFactory.this.fingerprints;
                int length = bArr.length;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    byte[] allowedFingerprint = bArr[i];
                    if (!Arrays.equals(fingerprint, allowedFingerprint)) {
                        i++;
                    } else {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new CertificateException(type + " certificate with unknown fingerprint: " + cert.getSubjectDN());
                }
            }

            private byte[] fingerprint(X509Certificate cert) throws CertificateEncodingException {
                MessageDigest md = (MessageDigest) FingerprintTrustManagerFactory.this.tlmd.get();
                md.reset();
                return md.digest(cert.getEncoded());
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return EmptyArrays.EMPTY_X509_CERTIFICATES;
            }
        };
        ObjectUtil.checkNotNull(algorithm, "algorithm");
        ObjectUtil.checkNotNull(fingerprints, "fingerprints");
        if (fingerprints.length == 0) {
            throw new IllegalArgumentException("No fingerprints provided");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            int hashLength = md.getDigestLength();
            ArrayList arrayList = new ArrayList(fingerprints.length);
            for (byte[] f : fingerprints) {
                if (f == null) {
                    break;
                }
                if (f.length != hashLength) {
                    throw new IllegalArgumentException(String.format("malformed fingerprint (length is %d but expected %d): %s", Integer.valueOf(f.length), Integer.valueOf(hashLength), ByteBufUtil.hexDump(Unpooled.wrappedBuffer(f))));
                }
                arrayList.add(f.clone());
            }
            this.tlmd = new FastThreadLocal<MessageDigest>() { // from class: io.netty.handler.ssl.util.FingerprintTrustManagerFactory.2
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // io.netty.util.concurrent.FastThreadLocal
                public MessageDigest initialValue() {
                    try {
                        return MessageDigest.getInstance(algorithm);
                    } catch (NoSuchAlgorithmException e) {
                        throw new IllegalArgumentException(String.format("Unsupported hash algorithm: %s", algorithm), e);
                    }
                }
            };
            this.fingerprints = (byte[][]) arrayList.toArray(new byte[0]);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(String.format("Unsupported hash algorithm: %s", algorithm), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[][] toFingerprintArray(Iterable<String> fingerprints) {
        String f;
        ObjectUtil.checkNotNull(fingerprints, "fingerprints");
        List<byte[]> list = new ArrayList<>();
        Iterator<String> it2 = fingerprints.iterator();
        while (it2.hasNext() && (f = it2.next()) != null) {
            if (!FINGERPRINT_PATTERN.matcher(f).matches()) {
                throw new IllegalArgumentException("malformed fingerprint: " + f);
            }
            list.add(StringUtil.decodeHexDump(FINGERPRINT_STRIP_PATTERN.matcher(f).replaceAll("")));
        }
        return (byte[][]) list.toArray(new byte[0]);
    }

    @Override // io.netty.handler.ssl.util.SimpleTrustManagerFactory
    protected void engineInit(KeyStore keyStore) throws Exception {
    }

    @Override // io.netty.handler.ssl.util.SimpleTrustManagerFactory
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override // io.netty.handler.ssl.util.SimpleTrustManagerFactory
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{this.tm};
    }
}
