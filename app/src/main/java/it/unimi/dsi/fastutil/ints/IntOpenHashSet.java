package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.IntCollections;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/* loaded from: classes4.dex */
public class IntOpenHashSet extends AbstractIntSet implements Serializable, Cloneable, Hash {
    private static final boolean ASSERTS = false;
    private static final long serialVersionUID = 0;
    protected transient boolean containsNull;
    protected final float f;
    protected transient int[] key;
    protected transient int mask;
    protected transient int maxFill;
    protected final transient int minN;
    protected transient int n;
    protected int size;

    public IntOpenHashSet(int expected, float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        int arraySize = HashCommon.arraySize(expected, f);
        this.n = arraySize;
        this.minN = arraySize;
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new int[this.n + 1];
    }

    public IntOpenHashSet(int expected) {
        this(expected, 0.75f);
    }

    public IntOpenHashSet() {
        this(16, 0.75f);
    }

    public IntOpenHashSet(Collection<? extends Integer> c, float f) {
        this(c.size(), f);
        addAll(c);
    }

    public IntOpenHashSet(Collection<? extends Integer> c) {
        this(c, 0.75f);
    }

    public IntOpenHashSet(IntCollection c, float f) {
        this(c.size(), f);
        addAll(c);
    }

    public IntOpenHashSet(IntCollection c) {
        this(c, 0.75f);
    }

    public IntOpenHashSet(IntIterator i, float f) {
        this(16, f);
        while (i.hasNext()) {
            add(i.nextInt());
        }
    }

    public IntOpenHashSet(IntIterator i) {
        this(i, 0.75f);
    }

    public IntOpenHashSet(Iterator<?> i, float f) {
        this(IntIterators.asIntIterator(i), f);
    }

    public IntOpenHashSet(Iterator<?> i) {
        this(IntIterators.asIntIterator(i));
    }

    public IntOpenHashSet(int[] a, int offset, int length, float f) {
        this(length < 0 ? 0 : length, f);
        IntArrays.ensureOffsetLength(a, offset, length);
        for (int i = 0; i < length; i++) {
            add(a[offset + i]);
        }
    }

    public IntOpenHashSet(int[] a, int offset, int length) {
        this(a, offset, length, 0.75f);
    }

    public IntOpenHashSet(int[] a, float f) {
        this(a, 0, a.length, f);
    }

    public IntOpenHashSet(int[] a) {
        this(a, 0.75f);
    }

    public static IntOpenHashSet of() {
        return new IntOpenHashSet();
    }

    public static IntOpenHashSet of(int e) {
        IntOpenHashSet result = new IntOpenHashSet(1, 0.75f);
        result.add(e);
        return result;
    }

    public static IntOpenHashSet of(int e0, int e1) {
        IntOpenHashSet result = new IntOpenHashSet(2, 0.75f);
        result.add(e0);
        if (!result.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        return result;
    }

    public static IntOpenHashSet of(int e0, int e1, int e2) {
        IntOpenHashSet result = new IntOpenHashSet(3, 0.75f);
        result.add(e0);
        if (!result.add(e1)) {
            throw new IllegalArgumentException("Duplicate element: " + e1);
        }
        if (!result.add(e2)) {
            throw new IllegalArgumentException("Duplicate element: " + e2);
        }
        return result;
    }

    public static IntOpenHashSet of(int... a) {
        IntOpenHashSet result = new IntOpenHashSet(a.length, 0.75f);
        for (int element : a) {
            if (!result.add(element)) {
                throw new IllegalArgumentException("Duplicate element " + element);
            }
        }
        return result;
    }

    public static IntOpenHashSet toSet(IntStream stream) {
        return (IntOpenHashSet) stream.collect(new Supplier() { // from class: it.unimi.dsi.fastutil.ints.IntOpenHashSet$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                return new IntOpenHashSet();
            }
        }, new IntOpenHashSet$$ExternalSyntheticLambda1(), new IntOpenHashSet$$ExternalSyntheticLambda2());
    }

    public static IntOpenHashSet toSetWithExpectedSize(IntStream stream, int expectedSize) {
        if (expectedSize <= 16) {
            return toSet(stream);
        }
        return (IntOpenHashSet) stream.collect(new IntCollections.SizeDecreasingSupplier(expectedSize, new IntFunction() { // from class: it.unimi.dsi.fastutil.ints.IntOpenHashSet$$ExternalSyntheticLambda0
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return IntOpenHashSet.lambda$toSetWithExpectedSize$0(i);
            }
        }), new IntOpenHashSet$$ExternalSyntheticLambda1(), new IntOpenHashSet$$ExternalSyntheticLambda2());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ IntOpenHashSet lambda$toSetWithExpectedSize$0(int size) {
        return size <= 16 ? new IntOpenHashSet() : new IntOpenHashSet(size);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int realSize() {
        return this.containsNull ? this.size - 1 : this.size;
    }

    public void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int) Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long) Math.ceil(((float) capacity) / this.f))));
        if (needed > this.n) {
            rehash(needed);
        }
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, it.unimi.dsi.fastutil.ints.IntCollection
    public boolean addAll(IntCollection c) {
        if (this.f <= 0.5d) {
            ensureCapacity(c.size());
        } else {
            tryCapacity(size() + c.size());
        }
        return super.addAll(c);
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, java.util.AbstractCollection, java.util.Collection
    public boolean addAll(Collection<? extends Integer> c) {
        if (this.f <= 0.5d) {
            ensureCapacity(c.size());
        } else {
            tryCapacity(size() + c.size());
        }
        return super.addAll(c);
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, it.unimi.dsi.fastutil.ints.IntCollection
    public boolean add(int k) {
        int curr;
        if (k == 0) {
            if (this.containsNull) {
                return false;
            }
            this.containsNull = true;
        } else {
            int[] key = this.key;
            int mix = HashCommon.mix(k) & this.mask;
            int pos = mix;
            int curr2 = key[mix];
            if (curr2 != 0) {
                if (curr2 == k) {
                    return false;
                }
                do {
                    int i = (pos + 1) & this.mask;
                    pos = i;
                    curr = key[i];
                    if (curr != 0) {
                    }
                } while (curr != k);
                return false;
            }
            key[pos] = k;
        }
        int i2 = this.size;
        this.size = i2 + 1;
        if (i2 >= this.maxFill) {
            rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return true;
    }

    protected final void shiftKeys(int pos) {
        int curr;
        int[] key = this.key;
        while (true) {
            int last = pos;
            pos = (pos + 1) & this.mask;
            while (true) {
                curr = key[pos];
                if (curr == 0) {
                    key[last] = 0;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last > pos) {
                    if (last >= slot && slot > pos) {
                        break;
                    }
                    pos = (pos + 1) & this.mask;
                } else if (last < slot && slot <= pos) {
                    pos = (pos + 1) & this.mask;
                }
            }
            key[last] = curr;
        }
    }

    private boolean removeEntry(int pos) {
        this.size--;
        shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            rehash(this.n / 2);
        }
        return true;
    }

    private boolean removeNullEntry() {
        this.containsNull = false;
        this.key[this.n] = 0;
        this.size--;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            rehash(this.n / 2);
        }
        return true;
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntSet, it.unimi.dsi.fastutil.ints.IntSet
    public boolean remove(int k) {
        int curr;
        if (k == 0) {
            if (this.containsNull) {
                return removeNullEntry();
            }
            return false;
        }
        int[] key = this.key;
        int mix = HashCommon.mix(k) & this.mask;
        int pos = mix;
        int curr2 = key[mix];
        if (curr2 == 0) {
            return false;
        }
        if (k == curr2) {
            return removeEntry(pos);
        }
        do {
            int i = (pos + 1) & this.mask;
            pos = i;
            curr = key[i];
            if (curr == 0) {
                return false;
            }
        } while (k != curr);
        return removeEntry(pos);
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, it.unimi.dsi.fastutil.ints.IntCollection
    public boolean contains(int k) {
        int curr;
        if (k == 0) {
            return this.containsNull;
        }
        int[] key = this.key;
        int mix = HashCommon.mix(k) & this.mask;
        int pos = mix;
        int curr2 = key[mix];
        if (curr2 == 0) {
            return false;
        }
        if (k == curr2) {
            return true;
        }
        do {
            int i = (pos + 1) & this.mask;
            pos = i;
            curr = key[i];
            if (curr == 0) {
                return false;
            }
        } while (k != curr);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNull = false;
        Arrays.fill(this.key, 0);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.size;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean isEmpty() {
        return this.size == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class SetIterator implements IntIterator {
        int c;
        int last;
        boolean mustReturnNull;
        int pos;
        IntArrayList wrapped;

        private SetIterator() {
            this.pos = IntOpenHashSet.this.n;
            this.last = -1;
            this.c = IntOpenHashSet.this.size;
            this.mustReturnNull = IntOpenHashSet.this.containsNull;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.c != 0;
        }

        @Override // it.unimi.dsi.fastutil.ints.IntIterator, java.util.PrimitiveIterator.OfInt
        public int nextInt() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            this.c--;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = IntOpenHashSet.this.n;
                return IntOpenHashSet.this.key[IntOpenHashSet.this.n];
            }
            int[] key = IntOpenHashSet.this.key;
            do {
                int i = this.pos - 1;
                this.pos = i;
                if (i < 0) {
                    this.last = Integer.MIN_VALUE;
                    return this.wrapped.getInt((-this.pos) - 1);
                }
            } while (key[this.pos] == 0);
            int i2 = this.pos;
            this.last = i2;
            return key[i2];
        }

        private final void shiftKeys(int pos) {
            int curr;
            int[] key = IntOpenHashSet.this.key;
            while (true) {
                int last = pos;
                pos = (pos + 1) & IntOpenHashSet.this.mask;
                while (true) {
                    curr = key[pos];
                    if (curr == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(curr) & IntOpenHashSet.this.mask;
                    if (last > pos) {
                        if (last >= slot && slot > pos) {
                            break;
                        }
                        pos = (pos + 1) & IntOpenHashSet.this.mask;
                    } else if (last >= slot || slot > pos) {
                        break;
                    } else {
                        pos = (pos + 1) & IntOpenHashSet.this.mask;
                    }
                }
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new IntArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == IntOpenHashSet.this.n) {
                IntOpenHashSet.this.containsNull = false;
                IntOpenHashSet.this.key[IntOpenHashSet.this.n] = 0;
            } else {
                if (this.pos < 0) {
                    IntOpenHashSet.this.remove(this.wrapped.getInt((-this.pos) - 1));
                    this.last = -1;
                    return;
                }
                shiftKeys(this.last);
            }
            IntOpenHashSet intOpenHashSet = IntOpenHashSet.this;
            intOpenHashSet.size--;
            this.last = -1;
        }

        @Override // java.util.PrimitiveIterator
        public void forEachRemaining(java.util.function.IntConsumer action) {
            int[] key = IntOpenHashSet.this.key;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.last = IntOpenHashSet.this.n;
                action.accept(key[IntOpenHashSet.this.n]);
                this.c--;
            }
            while (this.c != 0) {
                int i = this.pos - 1;
                this.pos = i;
                if (i < 0) {
                    this.last = Integer.MIN_VALUE;
                    action.accept(this.wrapped.getInt((-this.pos) - 1));
                    this.c--;
                } else if (key[this.pos] != 0) {
                    int i2 = this.pos;
                    this.last = i2;
                    action.accept(key[i2]);
                    this.c--;
                }
            }
        }
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntSet, it.unimi.dsi.fastutil.ints.AbstractIntCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.ints.IntCollection, it.unimi.dsi.fastutil.ints.IntIterable, it.unimi.dsi.fastutil.ints.IntSet, java.util.Set
    public IntIterator iterator() {
        return new SetIterator();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class SetSpliterator implements IntSpliterator {
        private static final int POST_SPLIT_CHARACTERISTICS = 257;
        int c;
        boolean hasSplit;
        int max;
        boolean mustReturnNull;
        int pos;

        SetSpliterator() {
            this.pos = 0;
            this.max = IntOpenHashSet.this.n;
            this.c = 0;
            this.mustReturnNull = IntOpenHashSet.this.containsNull;
            this.hasSplit = false;
        }

        SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
            this.pos = 0;
            this.max = IntOpenHashSet.this.n;
            this.c = 0;
            this.mustReturnNull = IntOpenHashSet.this.containsNull;
            this.hasSplit = false;
            this.pos = pos;
            this.max = max;
            this.mustReturnNull = mustReturnNull;
            this.hasSplit = hasSplit;
        }

        @Override // java.util.Spliterator.OfPrimitive
        public boolean tryAdvance(java.util.function.IntConsumer action) {
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                this.c++;
                action.accept(IntOpenHashSet.this.key[IntOpenHashSet.this.n]);
                return true;
            }
            int[] key = IntOpenHashSet.this.key;
            while (this.pos < this.max) {
                if (key[this.pos] != 0) {
                    this.c++;
                    int i = this.pos;
                    this.pos = i + 1;
                    action.accept(key[i]);
                    return true;
                }
                this.pos++;
            }
            return false;
        }

        @Override // java.util.Spliterator.OfPrimitive
        public void forEachRemaining(java.util.function.IntConsumer action) {
            int[] key = IntOpenHashSet.this.key;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                action.accept(key[IntOpenHashSet.this.n]);
                this.c++;
            }
            while (this.pos < this.max) {
                if (key[this.pos] != 0) {
                    action.accept(key[this.pos]);
                    this.c++;
                }
                this.pos++;
            }
        }

        @Override // java.util.Spliterator
        public int characteristics() {
            return this.hasSplit ? 257 : 321;
        }

        @Override // java.util.Spliterator
        public long estimateSize() {
            if (this.hasSplit) {
                return Math.min(IntOpenHashSet.this.size - this.c, ((long) ((IntOpenHashSet.this.realSize() / IntOpenHashSet.this.n) * (this.max - this.pos))) + (this.mustReturnNull ? 1L : 0L));
            }
            return IntOpenHashSet.this.size - this.c;
        }

        @Override // it.unimi.dsi.fastutil.ints.IntSpliterator, java.util.Spliterator.OfInt, java.util.Spliterator.OfPrimitive, java.util.Spliterator
        public SetSpliterator trySplit() {
            int retLen;
            if (this.pos >= this.max - 1 || (retLen = (this.max - this.pos) >> 1) <= 1) {
                return null;
            }
            int myNewPos = this.pos + retLen;
            int retPos = this.pos;
            SetSpliterator split = new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
            this.pos = myNewPos;
            this.mustReturnNull = false;
            this.hasSplit = true;
            return split;
        }

        @Override // it.unimi.dsi.fastutil.ints.IntSpliterator
        public long skip(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            }
            if (n == 0) {
                return 0L;
            }
            long skipped = 0;
            if (this.mustReturnNull) {
                this.mustReturnNull = false;
                skipped = 0 + 1;
                n--;
            }
            int[] key = IntOpenHashSet.this.key;
            while (this.pos < this.max && n > 0) {
                int i = this.pos;
                this.pos = i + 1;
                if (key[i] != 0) {
                    skipped++;
                    n--;
                }
            }
            return skipped;
        }
    }

    @Override // java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.ints.IntCollection, it.unimi.dsi.fastutil.ints.IntIterable, it.unimi.dsi.fastutil.ints.IntSet, java.util.Set
    public IntSpliterator spliterator() {
        return new SetSpliterator();
    }

    @Override // it.unimi.dsi.fastutil.ints.IntIterable
    public void forEach(java.util.function.IntConsumer action) {
        if (this.containsNull) {
            action.accept(this.key[this.n]);
        }
        int[] key = this.key;
        int pos = this.n;
        while (true) {
            int pos2 = pos - 1;
            if (pos == 0) {
                return;
            }
            if (key[pos2] != 0) {
                action.accept(key[pos2]);
            }
            pos = pos2;
        }
    }

    public boolean trim() {
        return trim(this.size);
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int) Math.ceil(n / this.f));
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            rehash(l);
            return true;
        } catch (OutOfMemoryError e) {
            return false;
        }
    }

    protected void rehash(int newN) {
        int i;
        int[] key = this.key;
        int mask = newN - 1;
        int[] newKey = new int[newN + 1];
        int i2 = this.n;
        int j = realSize();
        while (true) {
            int j2 = j - 1;
            if (j == 0) {
                this.n = newN;
                this.mask = mask;
                this.maxFill = HashCommon.maxFill(this.n, this.f);
                this.key = newKey;
                return;
            }
            do {
                i2--;
            } while (key[i2] == 0);
            int mix = HashCommon.mix(key[i2]) & mask;
            int pos = mix;
            if (newKey[mix] == 0) {
                newKey[pos] = key[i2];
                j = j2;
            }
            do {
                i = (pos + 1) & mask;
                pos = i;
            } while (newKey[i] != 0);
            newKey[pos] = key[i2];
            j = j2;
        }
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public IntOpenHashSet m234clone() {
        try {
            IntOpenHashSet c = (IntOpenHashSet) super.clone();
            c.key = (int[]) this.key.clone();
            c.containsNull = this.containsNull;
            return c;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    @Override // it.unimi.dsi.fastutil.ints.AbstractIntSet, java.util.Collection, java.util.Set
    public int hashCode() {
        int h = 0;
        int j = realSize();
        int i = 0;
        while (true) {
            int j2 = j - 1;
            if (j != 0) {
                while (this.key[i] == 0) {
                    i++;
                }
                h += this.key[i];
                i++;
                j = j2;
            } else {
                return h;
            }
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        IntIterator i = iterator();
        s.defaultWriteObject();
        int j = this.size;
        while (true) {
            int j2 = j - 1;
            if (j == 0) {
                return;
            }
            s.writeInt(i.nextInt());
            j = j2;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        int pos;
        int i;
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        int[] key = new int[this.n + 1];
        this.key = key;
        int k = this.size;
        while (true) {
            int i2 = k - 1;
            if (k != 0) {
                int k2 = s.readInt();
                if (k2 == 0) {
                    pos = this.n;
                    this.containsNull = true;
                } else {
                    int mix = HashCommon.mix(k2) & this.mask;
                    int pos2 = mix;
                    if (key[mix] == 0) {
                        pos = pos2;
                    }
                    do {
                        i = (pos2 + 1) & this.mask;
                        pos2 = i;
                    } while (key[i] != 0);
                    pos = pos2;
                }
                key[pos] = k2;
                k = i2;
            } else {
                return;
            }
        }
    }

    private void checkTable() {
    }
}
