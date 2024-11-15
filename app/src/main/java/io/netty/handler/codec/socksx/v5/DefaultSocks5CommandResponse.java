package io.netty.handler.codec.socksx.v5;

import androidx.core.app.NotificationCompat;
import io.netty.handler.codec.DecoderResult;
import io.netty.util.NetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

/* loaded from: classes4.dex */
public final class DefaultSocks5CommandResponse extends AbstractSocks5Message implements Socks5CommandResponse {
    private final String bndAddr;
    private final Socks5AddressType bndAddrType;
    private final int bndPort;
    private final Socks5CommandStatus status;

    public DefaultSocks5CommandResponse(Socks5CommandStatus status, Socks5AddressType bndAddrType) {
        this(status, bndAddrType, null, 0);
    }

    public DefaultSocks5CommandResponse(Socks5CommandStatus status, Socks5AddressType bndAddrType, String bndAddr, int bndPort) {
        ObjectUtil.checkNotNull(status, NotificationCompat.CATEGORY_STATUS);
        ObjectUtil.checkNotNull(bndAddrType, "bndAddrType");
        if (bndAddr != null) {
            if (bndAddrType == Socks5AddressType.IPv4) {
                if (!NetUtil.isValidIpV4Address(bndAddr)) {
                    throw new IllegalArgumentException("bndAddr: " + bndAddr + " (expected: a valid IPv4 address)");
                }
            } else if (bndAddrType == Socks5AddressType.DOMAIN) {
                bndAddr = IDN.toASCII(bndAddr);
                if (bndAddr.length() > 255) {
                    throw new IllegalArgumentException("bndAddr: " + bndAddr + " (expected: less than 256 chars)");
                }
            } else if (bndAddrType == Socks5AddressType.IPv6 && !NetUtil.isValidIpV6Address(bndAddr)) {
                throw new IllegalArgumentException("bndAddr: " + bndAddr + " (expected: a valid IPv6 address)");
            }
        }
        if (bndPort < 0 || bndPort > 65535) {
            throw new IllegalArgumentException("bndPort: " + bndPort + " (expected: 0~65535)");
        }
        this.status = status;
        this.bndAddrType = bndAddrType;
        this.bndAddr = bndAddr;
        this.bndPort = bndPort;
    }

    @Override // io.netty.handler.codec.socksx.v5.Socks5CommandResponse
    public Socks5CommandStatus status() {
        return this.status;
    }

    @Override // io.netty.handler.codec.socksx.v5.Socks5CommandResponse
    public Socks5AddressType bndAddrType() {
        return this.bndAddrType;
    }

    @Override // io.netty.handler.codec.socksx.v5.Socks5CommandResponse
    public String bndAddr() {
        return this.bndAddr;
    }

    @Override // io.netty.handler.codec.socksx.v5.Socks5CommandResponse
    public int bndPort() {
        return this.bndPort;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", status: ");
        } else {
            buf.append("(status: ");
        }
        buf.append(status());
        buf.append(", bndAddrType: ");
        buf.append(bndAddrType());
        buf.append(", bndAddr: ");
        buf.append(bndAddr());
        buf.append(", bndPort: ");
        buf.append(bndPort());
        buf.append(')');
        return buf.toString();
    }
}
