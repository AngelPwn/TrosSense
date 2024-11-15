package io.netty.channel.kqueue;

import io.netty.channel.unix.Buffer;
import io.netty.channel.unix.Limits;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

/* loaded from: classes4.dex */
final class NativeLongArray {
    private int capacity;
    private ByteBuffer memory;
    private long memoryAddress;
    private int size;

    NativeLongArray(int capacity) {
        this.capacity = ObjectUtil.checkPositive(capacity, "capacity");
        this.memory = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(capacity));
        this.memoryAddress = Buffer.memoryAddress(this.memory);
    }

    private static int idx(int index) {
        return Limits.SIZEOF_JLONG * index;
    }

    private static int calculateBufferCapacity(int capacity) {
        return Limits.SIZEOF_JLONG * capacity;
    }

    void add(long value) {
        reallocIfNeeded();
        if (PlatformDependent.hasUnsafe()) {
            PlatformDependent.putLong(memoryOffset(this.size), value);
        } else {
            this.memory.putLong(idx(this.size), value);
        }
        this.size++;
    }

    void clear() {
        this.size = 0;
    }

    boolean isEmpty() {
        return this.size == 0;
    }

    int size() {
        return this.size;
    }

    void free() {
        Buffer.free(this.memory);
        this.memoryAddress = 0L;
    }

    long memoryAddress() {
        return this.memoryAddress;
    }

    long memoryAddressEnd() {
        return memoryOffset(this.size);
    }

    private long memoryOffset(int index) {
        return this.memoryAddress + idx(index);
    }

    private void reallocIfNeeded() {
        if (this.size == this.capacity) {
            int newLength = this.capacity <= 65536 ? this.capacity << 1 : (this.capacity + this.capacity) >> 1;
            ByteBuffer buffer = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(newLength));
            this.memory.position(0).limit(this.size);
            buffer.put(this.memory);
            buffer.position(0);
            Buffer.free(this.memory);
            this.memory = buffer;
            this.memoryAddress = Buffer.memoryAddress(buffer);
            this.capacity = newLength;
        }
    }

    public String toString() {
        return "memoryAddress: " + this.memoryAddress + " capacity: " + this.capacity + " size: " + this.size;
    }
}
