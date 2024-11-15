package org.jose4j.jwa;

import org.jose4j.keys.KeyPersuasion;

/* loaded from: classes5.dex */
public interface Algorithm {
    String getAlgorithmIdentifier();

    String getJavaAlgorithm();

    KeyPersuasion getKeyPersuasion();

    String getKeyType();

    boolean isAvailable();
}
