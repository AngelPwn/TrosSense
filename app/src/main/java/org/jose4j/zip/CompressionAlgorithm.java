package org.jose4j.zip;

import org.jose4j.jwa.Algorithm;
import org.jose4j.lang.JoseException;

/* loaded from: classes5.dex */
public interface CompressionAlgorithm extends Algorithm {
    byte[] compress(byte[] bArr);

    byte[] decompress(byte[] bArr) throws JoseException;
}
