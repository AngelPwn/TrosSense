package io.netty.handler.codec.dns;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

/* loaded from: classes4.dex */
public class DefaultDnsPtrRecord extends AbstractDnsRecord implements DnsPtrRecord {
    private final String hostname;

    public DefaultDnsPtrRecord(String name, int dnsClass, long timeToLive, String hostname) {
        super(name, DnsRecordType.PTR, dnsClass, timeToLive);
        this.hostname = (String) ObjectUtil.checkNotNull(hostname, "hostname");
    }

    @Override // io.netty.handler.codec.dns.DnsPtrRecord
    public String hostname() {
        return this.hostname;
    }

    @Override // io.netty.handler.codec.dns.AbstractDnsRecord
    public String toString() {
        StringBuilder buf = new StringBuilder(64).append(StringUtil.simpleClassName(this)).append('(');
        DnsRecordType type = type();
        buf.append(name().isEmpty() ? "<root>" : name()).append(' ').append(timeToLive()).append(' ');
        DnsMessageUtil.appendRecordClass(buf, dnsClass()).append(' ').append(type.name());
        buf.append(' ').append(this.hostname);
        return buf.toString();
    }
}
