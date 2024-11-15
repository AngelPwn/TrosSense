package io.netty.channel.sctp;

import com.sun.nio.sctp.MessageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.ObjectUtil;

/* loaded from: classes4.dex */
public final class SctpMessage extends DefaultByteBufHolder {
    private final MessageInfo msgInfo;
    private final int protocolIdentifier;
    private final int streamIdentifier;
    private final boolean unordered;

    public SctpMessage(int protocolIdentifier, int streamIdentifier, ByteBuf payloadBuffer) {
        this(protocolIdentifier, streamIdentifier, false, payloadBuffer);
    }

    public SctpMessage(int protocolIdentifier, int streamIdentifier, boolean unordered, ByteBuf payloadBuffer) {
        super(payloadBuffer);
        this.protocolIdentifier = protocolIdentifier;
        this.streamIdentifier = streamIdentifier;
        this.unordered = unordered;
        this.msgInfo = null;
    }

    public SctpMessage(MessageInfo msgInfo, ByteBuf payloadBuffer) {
        super(payloadBuffer);
        this.msgInfo = (MessageInfo) ObjectUtil.checkNotNull(msgInfo, "msgInfo");
        this.streamIdentifier = msgInfo.streamNumber();
        this.protocolIdentifier = msgInfo.payloadProtocolID();
        this.unordered = msgInfo.isUnordered();
    }

    public int streamIdentifier() {
        return this.streamIdentifier;
    }

    public int protocolIdentifier() {
        return this.protocolIdentifier;
    }

    public boolean isUnordered() {
        return this.unordered;
    }

    public MessageInfo messageInfo() {
        return this.msgInfo;
    }

    public boolean isComplete() {
        if (this.msgInfo != null) {
            return this.msgInfo.isComplete();
        }
        return true;
    }

    @Override // io.netty.buffer.DefaultByteBufHolder
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SctpMessage sctpFrame = (SctpMessage) o;
        if (this.protocolIdentifier != sctpFrame.protocolIdentifier || this.streamIdentifier != sctpFrame.streamIdentifier || this.unordered != sctpFrame.unordered) {
            return false;
        }
        return content().equals(sctpFrame.content());
    }

    @Override // io.netty.buffer.DefaultByteBufHolder
    public int hashCode() {
        int result = this.streamIdentifier;
        return (((((result * 31) + this.protocolIdentifier) * 31) + (this.unordered ? 1231 : 1237)) * 31) + content().hashCode();
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.buffer.ByteBufHolder
    public SctpMessage copy() {
        return (SctpMessage) super.copy();
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.buffer.ByteBufHolder
    public SctpMessage duplicate() {
        return (SctpMessage) super.duplicate();
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.buffer.ByteBufHolder
    public SctpMessage retainedDuplicate() {
        return (SctpMessage) super.retainedDuplicate();
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.buffer.ByteBufHolder
    public SctpMessage replace(ByteBuf content) {
        if (this.msgInfo == null) {
            return new SctpMessage(this.protocolIdentifier, this.streamIdentifier, this.unordered, content);
        }
        return new SctpMessage(this.msgInfo, content);
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.util.ReferenceCounted
    public SctpMessage retain() {
        super.retain();
        return this;
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.util.ReferenceCounted
    public SctpMessage retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.util.ReferenceCounted
    public SctpMessage touch() {
        super.touch();
        return this;
    }

    @Override // io.netty.buffer.DefaultByteBufHolder, io.netty.util.ReferenceCounted
    public SctpMessage touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override // io.netty.buffer.DefaultByteBufHolder
    public String toString() {
        return "SctpFrame{streamIdentifier=" + this.streamIdentifier + ", protocolIdentifier=" + this.protocolIdentifier + ", unordered=" + this.unordered + ", data=" + contentToString() + '}';
    }
}
