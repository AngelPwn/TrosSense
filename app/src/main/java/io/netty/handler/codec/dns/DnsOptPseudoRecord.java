package io.netty.handler.codec.dns;

/* loaded from: classes4.dex */
public interface DnsOptPseudoRecord extends DnsRecord {
    int extendedRcode();

    int flags();

    int version();
}
