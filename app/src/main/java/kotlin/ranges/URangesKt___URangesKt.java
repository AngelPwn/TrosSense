package kotlin.ranges;

import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UInt;
import kotlin.ULong;
import kotlin.UShort;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.random.URandomKt;
import okhttp3.internal.ws.WebSocketProtocol;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: _URanges.kt */
@Metadata(d1 = {"\u0000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\u0010\t\n\u0002\b\n\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0003\u0010\u0004\u001a\u001e\u0010\u0000\u001a\u00020\u0005*\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u0005H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0006\u0010\u0007\u001a\u001e\u0010\u0000\u001a\u00020\b*\u00020\b2\u0006\u0010\u0002\u001a\u00020\bH\u0007ø\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a\u001e\u0010\u0000\u001a\u00020\u000b*\u00020\u000b2\u0006\u0010\u0002\u001a\u00020\u000bH\u0007ø\u0001\u0000¢\u0006\u0004\b\f\u0010\r\u001a\u001e\u0010\u000e\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0010\u0010\u0004\u001a\u001e\u0010\u000e\u001a\u00020\u0005*\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0011\u0010\u0007\u001a\u001e\u0010\u000e\u001a\u00020\b*\u00020\b2\u0006\u0010\u000f\u001a\u00020\bH\u0007ø\u0001\u0000¢\u0006\u0004\b\u0012\u0010\n\u001a\u001e\u0010\u000e\u001a\u00020\u000b*\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000bH\u0007ø\u0001\u0000¢\u0006\u0004\b\u0013\u0010\r\u001a&\u0010\u0014\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016\u001a&\u0010\u0014\u001a\u00020\u0005*\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0017\u0010\u0018\u001a$\u0010\u0014\u001a\u00020\u0005*\u00020\u00052\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00050\u001aH\u0007ø\u0001\u0000¢\u0006\u0004\b\u001b\u0010\u001c\u001a&\u0010\u0014\u001a\u00020\b*\u00020\b2\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\bH\u0007ø\u0001\u0000¢\u0006\u0004\b\u001d\u0010\u001e\u001a$\u0010\u0014\u001a\u00020\b*\u00020\b2\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u001aH\u0007ø\u0001\u0000¢\u0006\u0004\b\u001f\u0010 \u001a&\u0010\u0014\u001a\u00020\u000b*\u00020\u000b2\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000bH\u0007ø\u0001\u0000¢\u0006\u0004\b!\u0010\"\u001a\u001f\u0010#\u001a\u00020$*\u00020%2\u0006\u0010&\u001a\u00020\u0001H\u0087\u0002ø\u0001\u0000¢\u0006\u0004\b'\u0010(\u001a\u001f\u0010#\u001a\u00020$*\u00020%2\b\u0010)\u001a\u0004\u0018\u00010\u0005H\u0087\nø\u0001\u0000¢\u0006\u0002\b*\u001a\u001f\u0010#\u001a\u00020$*\u00020%2\u0006\u0010&\u001a\u00020\bH\u0087\u0002ø\u0001\u0000¢\u0006\u0004\b+\u0010,\u001a\u001f\u0010#\u001a\u00020$*\u00020%2\u0006\u0010&\u001a\u00020\u000bH\u0087\u0002ø\u0001\u0000¢\u0006\u0004\b-\u0010.\u001a\u001f\u0010#\u001a\u00020$*\u00020/2\u0006\u0010&\u001a\u00020\u0001H\u0087\u0002ø\u0001\u0000¢\u0006\u0004\b0\u00101\u001a\u001f\u0010#\u001a\u00020$*\u00020/2\u0006\u0010&\u001a\u00020\u0005H\u0087\u0002ø\u0001\u0000¢\u0006\u0004\b2\u00103\u001a\u001f\u0010#\u001a\u00020$*\u00020/2\b\u0010)\u001a\u0004\u0018\u00010\bH\u0087\nø\u0001\u0000¢\u0006\u0002\b4\u001a\u001f\u0010#\u001a\u00020$*\u00020/2\u0006\u0010&\u001a\u00020\u000bH\u0087\u0002ø\u0001\u0000¢\u0006\u0004\b5\u00106\u001a\u001f\u00107\u001a\u000208*\u00020\u00012\u0006\u00109\u001a\u00020\u0001H\u0087\u0004ø\u0001\u0000¢\u0006\u0004\b:\u0010;\u001a\u001f\u00107\u001a\u000208*\u00020\u00052\u0006\u00109\u001a\u00020\u0005H\u0087\u0004ø\u0001\u0000¢\u0006\u0004\b<\u0010=\u001a\u001f\u00107\u001a\u00020>*\u00020\b2\u0006\u00109\u001a\u00020\bH\u0087\u0004ø\u0001\u0000¢\u0006\u0004\b?\u0010@\u001a\u001f\u00107\u001a\u000208*\u00020\u000b2\u0006\u00109\u001a\u00020\u000bH\u0087\u0004ø\u0001\u0000¢\u0006\u0004\bA\u0010B\u001a\u0014\u0010C\u001a\u00020\u0005*\u000208H\u0007ø\u0001\u0000¢\u0006\u0002\u0010D\u001a\u0014\u0010C\u001a\u00020\b*\u00020>H\u0007ø\u0001\u0000¢\u0006\u0002\u0010E\u001a\u0011\u0010F\u001a\u0004\u0018\u00010\u0005*\u000208H\u0007ø\u0001\u0000\u001a\u0011\u0010F\u001a\u0004\u0018\u00010\b*\u00020>H\u0007ø\u0001\u0000\u001a\u0014\u0010G\u001a\u00020\u0005*\u000208H\u0007ø\u0001\u0000¢\u0006\u0002\u0010D\u001a\u0014\u0010G\u001a\u00020\b*\u00020>H\u0007ø\u0001\u0000¢\u0006\u0002\u0010E\u001a\u0011\u0010H\u001a\u0004\u0018\u00010\u0005*\u000208H\u0007ø\u0001\u0000\u001a\u0011\u0010H\u001a\u0004\u0018\u00010\b*\u00020>H\u0007ø\u0001\u0000\u001a\u0015\u0010I\u001a\u00020\u0005*\u00020%H\u0087\bø\u0001\u0000¢\u0006\u0002\u0010J\u001a\u001c\u0010I\u001a\u00020\u0005*\u00020%2\u0006\u0010I\u001a\u00020KH\u0007ø\u0001\u0000¢\u0006\u0002\u0010L\u001a\u0015\u0010I\u001a\u00020\b*\u00020/H\u0087\bø\u0001\u0000¢\u0006\u0002\u0010M\u001a\u001c\u0010I\u001a\u00020\b*\u00020/2\u0006\u0010I\u001a\u00020KH\u0007ø\u0001\u0000¢\u0006\u0002\u0010N\u001a\u0012\u0010O\u001a\u0004\u0018\u00010\u0005*\u00020%H\u0087\bø\u0001\u0000\u001a\u0019\u0010O\u001a\u0004\u0018\u00010\u0005*\u00020%2\u0006\u0010I\u001a\u00020KH\u0007ø\u0001\u0000\u001a\u0012\u0010O\u001a\u0004\u0018\u00010\b*\u00020/H\u0087\bø\u0001\u0000\u001a\u0019\u0010O\u001a\u0004\u0018\u00010\b*\u00020/2\u0006\u0010I\u001a\u00020KH\u0007ø\u0001\u0000\u001a\f\u0010P\u001a\u000208*\u000208H\u0007\u001a\f\u0010P\u001a\u00020>*\u00020>H\u0007\u001a\u0015\u0010Q\u001a\u000208*\u0002082\u0006\u0010Q\u001a\u00020RH\u0087\u0004\u001a\u0015\u0010Q\u001a\u00020>*\u00020>2\u0006\u0010Q\u001a\u00020SH\u0087\u0004\u001a\u001f\u0010T\u001a\u00020%*\u00020\u00012\u0006\u00109\u001a\u00020\u0001H\u0087\u0004ø\u0001\u0000¢\u0006\u0004\bU\u0010V\u001a\u001f\u0010T\u001a\u00020%*\u00020\u00052\u0006\u00109\u001a\u00020\u0005H\u0087\u0004ø\u0001\u0000¢\u0006\u0004\bW\u0010X\u001a\u001f\u0010T\u001a\u00020/*\u00020\b2\u0006\u00109\u001a\u00020\bH\u0087\u0004ø\u0001\u0000¢\u0006\u0004\bY\u0010Z\u001a\u001f\u0010T\u001a\u00020%*\u00020\u000b2\u0006\u00109\u001a\u00020\u000bH\u0087\u0004ø\u0001\u0000¢\u0006\u0004\b[\u0010\\\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006]"}, d2 = {"coerceAtLeast", "Lkotlin/UByte;", "minimumValue", "coerceAtLeast-Kr8caGY", "(BB)B", "Lkotlin/UInt;", "coerceAtLeast-J1ME1BU", "(II)I", "Lkotlin/ULong;", "coerceAtLeast-eb3DHEI", "(JJ)J", "Lkotlin/UShort;", "coerceAtLeast-5PvTz6A", "(SS)S", "coerceAtMost", "maximumValue", "coerceAtMost-Kr8caGY", "coerceAtMost-J1ME1BU", "coerceAtMost-eb3DHEI", "coerceAtMost-5PvTz6A", "coerceIn", "coerceIn-b33U2AM", "(BBB)B", "coerceIn-WZ9TVnA", "(III)I", "range", "Lkotlin/ranges/ClosedRange;", "coerceIn-wuiCnnA", "(ILkotlin/ranges/ClosedRange;)I", "coerceIn-sambcqE", "(JJJ)J", "coerceIn-JPwROB0", "(JLkotlin/ranges/ClosedRange;)J", "coerceIn-VKSA0NQ", "(SSS)S", "contains", "", "Lkotlin/ranges/UIntRange;", "value", "contains-68kG9v0", "(Lkotlin/ranges/UIntRange;B)Z", "element", "contains-biwQdVI", "contains-fz5IDCE", "(Lkotlin/ranges/UIntRange;J)Z", "contains-ZsK3CEQ", "(Lkotlin/ranges/UIntRange;S)Z", "Lkotlin/ranges/ULongRange;", "contains-ULb-yJY", "(Lkotlin/ranges/ULongRange;B)Z", "contains-Gab390E", "(Lkotlin/ranges/ULongRange;I)Z", "contains-GYNo2lE", "contains-uhHAxoY", "(Lkotlin/ranges/ULongRange;S)Z", "downTo", "Lkotlin/ranges/UIntProgression;", "to", "downTo-Kr8caGY", "(BB)Lkotlin/ranges/UIntProgression;", "downTo-J1ME1BU", "(II)Lkotlin/ranges/UIntProgression;", "Lkotlin/ranges/ULongProgression;", "downTo-eb3DHEI", "(JJ)Lkotlin/ranges/ULongProgression;", "downTo-5PvTz6A", "(SS)Lkotlin/ranges/UIntProgression;", "first", "(Lkotlin/ranges/UIntProgression;)I", "(Lkotlin/ranges/ULongProgression;)J", "firstOrNull", "last", "lastOrNull", "random", "(Lkotlin/ranges/UIntRange;)I", "Lkotlin/random/Random;", "(Lkotlin/ranges/UIntRange;Lkotlin/random/Random;)I", "(Lkotlin/ranges/ULongRange;)J", "(Lkotlin/ranges/ULongRange;Lkotlin/random/Random;)J", "randomOrNull", "reversed", "step", "", "", "until", "until-Kr8caGY", "(BB)Lkotlin/ranges/UIntRange;", "until-J1ME1BU", "(II)Lkotlin/ranges/UIntRange;", "until-eb3DHEI", "(JJ)Lkotlin/ranges/ULongRange;", "until-5PvTz6A", "(SS)Lkotlin/ranges/UIntRange;", "kotlin-stdlib"}, k = 5, mv = {1, 8, 0}, xi = 49, xs = "kotlin/ranges/URangesKt")
/* loaded from: classes.dex */
public class URangesKt___URangesKt {
    public static final int first(UIntProgression $this$first) {
        Intrinsics.checkNotNullParameter($this$first, "<this>");
        if ($this$first.isEmpty()) {
            throw new NoSuchElementException("Progression " + $this$first + " is empty.");
        }
        return $this$first.getFirst();
    }

    public static final long first(ULongProgression $this$first) {
        Intrinsics.checkNotNullParameter($this$first, "<this>");
        if ($this$first.isEmpty()) {
            throw new NoSuchElementException("Progression " + $this$first + " is empty.");
        }
        return $this$first.getFirst();
    }

    public static final UInt firstOrNull(UIntProgression $this$firstOrNull) {
        Intrinsics.checkNotNullParameter($this$firstOrNull, "<this>");
        if ($this$firstOrNull.isEmpty()) {
            return null;
        }
        return UInt.m369boximpl($this$firstOrNull.getFirst());
    }

    public static final ULong firstOrNull(ULongProgression $this$firstOrNull) {
        Intrinsics.checkNotNullParameter($this$firstOrNull, "<this>");
        if ($this$firstOrNull.isEmpty()) {
            return null;
        }
        return ULong.m448boximpl($this$firstOrNull.getFirst());
    }

    public static final int last(UIntProgression $this$last) {
        Intrinsics.checkNotNullParameter($this$last, "<this>");
        if ($this$last.isEmpty()) {
            throw new NoSuchElementException("Progression " + $this$last + " is empty.");
        }
        return $this$last.getLast();
    }

    public static final long last(ULongProgression $this$last) {
        Intrinsics.checkNotNullParameter($this$last, "<this>");
        if ($this$last.isEmpty()) {
            throw new NoSuchElementException("Progression " + $this$last + " is empty.");
        }
        return $this$last.getLast();
    }

    public static final UInt lastOrNull(UIntProgression $this$lastOrNull) {
        Intrinsics.checkNotNullParameter($this$lastOrNull, "<this>");
        if ($this$lastOrNull.isEmpty()) {
            return null;
        }
        return UInt.m369boximpl($this$lastOrNull.getLast());
    }

    public static final ULong lastOrNull(ULongProgression $this$lastOrNull) {
        Intrinsics.checkNotNullParameter($this$lastOrNull, "<this>");
        if ($this$lastOrNull.isEmpty()) {
            return null;
        }
        return ULong.m448boximpl($this$lastOrNull.getLast());
    }

    private static final int random(UIntRange $this$random) {
        Intrinsics.checkNotNullParameter($this$random, "<this>");
        return URangesKt.random($this$random, Random.INSTANCE);
    }

    private static final long random(ULongRange $this$random) {
        Intrinsics.checkNotNullParameter($this$random, "<this>");
        return URangesKt.random($this$random, Random.INSTANCE);
    }

    public static final int random(UIntRange $this$random, Random random) {
        Intrinsics.checkNotNullParameter($this$random, "<this>");
        Intrinsics.checkNotNullParameter(random, "random");
        try {
            return URandomKt.nextUInt(random, $this$random);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    public static final long random(ULongRange $this$random, Random random) {
        Intrinsics.checkNotNullParameter($this$random, "<this>");
        Intrinsics.checkNotNullParameter(random, "random");
        try {
            return URandomKt.nextULong(random, $this$random);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    private static final UInt randomOrNull(UIntRange $this$randomOrNull) {
        Intrinsics.checkNotNullParameter($this$randomOrNull, "<this>");
        return URangesKt.randomOrNull($this$randomOrNull, Random.INSTANCE);
    }

    private static final ULong randomOrNull(ULongRange $this$randomOrNull) {
        Intrinsics.checkNotNullParameter($this$randomOrNull, "<this>");
        return URangesKt.randomOrNull($this$randomOrNull, Random.INSTANCE);
    }

    public static final UInt randomOrNull(UIntRange $this$randomOrNull, Random random) {
        Intrinsics.checkNotNullParameter($this$randomOrNull, "<this>");
        Intrinsics.checkNotNullParameter(random, "random");
        if ($this$randomOrNull.isEmpty()) {
            return null;
        }
        return UInt.m369boximpl(URandomKt.nextUInt(random, $this$randomOrNull));
    }

    public static final ULong randomOrNull(ULongRange $this$randomOrNull, Random random) {
        Intrinsics.checkNotNullParameter($this$randomOrNull, "<this>");
        Intrinsics.checkNotNullParameter(random, "random");
        if ($this$randomOrNull.isEmpty()) {
            return null;
        }
        return ULong.m448boximpl(URandomKt.nextULong(random, $this$randomOrNull));
    }

    /* renamed from: contains-biwQdVI, reason: not valid java name */
    private static final boolean m1552containsbiwQdVI(UIntRange contains, UInt element) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return element != null && contains.m1520containsWZ4Q5Ns(element.getData());
    }

    /* renamed from: contains-GYNo2lE, reason: not valid java name */
    private static final boolean m1548containsGYNo2lE(ULongRange contains, ULong element) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return element != null && contains.m1529containsVKZWuLQ(element.getData());
    }

    /* renamed from: contains-68kG9v0, reason: not valid java name */
    public static final boolean m1547contains68kG9v0(UIntRange contains, byte value) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return contains.m1520containsWZ4Q5Ns(UInt.m375constructorimpl(value & 255));
    }

    /* renamed from: contains-ULb-yJY, reason: not valid java name */
    public static final boolean m1550containsULbyJY(ULongRange contains, byte value) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return contains.m1529containsVKZWuLQ(ULong.m454constructorimpl(value & 255));
    }

    /* renamed from: contains-Gab390E, reason: not valid java name */
    public static final boolean m1549containsGab390E(ULongRange contains, int value) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return contains.m1529containsVKZWuLQ(ULong.m454constructorimpl(value & 4294967295L));
    }

    /* renamed from: contains-fz5IDCE, reason: not valid java name */
    public static final boolean m1553containsfz5IDCE(UIntRange contains, long value) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return ULong.m454constructorimpl(value >>> 32) == 0 && contains.m1520containsWZ4Q5Ns(UInt.m375constructorimpl((int) value));
    }

    /* renamed from: contains-ZsK3CEQ, reason: not valid java name */
    public static final boolean m1551containsZsK3CEQ(UIntRange contains, short value) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return contains.m1520containsWZ4Q5Ns(UInt.m375constructorimpl(65535 & value));
    }

    /* renamed from: contains-uhHAxoY, reason: not valid java name */
    public static final boolean m1554containsuhHAxoY(ULongRange contains, short value) {
        Intrinsics.checkNotNullParameter(contains, "$this$contains");
        return contains.m1529containsVKZWuLQ(ULong.m454constructorimpl(value & WebSocketProtocol.PAYLOAD_SHORT_MAX));
    }

    /* renamed from: downTo-Kr8caGY, reason: not valid java name */
    public static final UIntProgression m1557downToKr8caGY(byte $this$downTo_u2dKr8caGY, byte to) {
        return UIntProgression.INSTANCE.m1517fromClosedRangeNkh28Cs(UInt.m375constructorimpl($this$downTo_u2dKr8caGY & 255), UInt.m375constructorimpl(to & 255), -1);
    }

    /* renamed from: downTo-J1ME1BU, reason: not valid java name */
    public static final UIntProgression m1556downToJ1ME1BU(int $this$downTo_u2dJ1ME1BU, int to) {
        return UIntProgression.INSTANCE.m1517fromClosedRangeNkh28Cs($this$downTo_u2dJ1ME1BU, to, -1);
    }

    /* renamed from: downTo-eb3DHEI, reason: not valid java name */
    public static final ULongProgression m1558downToeb3DHEI(long $this$downTo_u2deb3DHEI, long to) {
        return ULongProgression.INSTANCE.m1526fromClosedRange7ftBX0g($this$downTo_u2deb3DHEI, to, -1L);
    }

    /* renamed from: downTo-5PvTz6A, reason: not valid java name */
    public static final UIntProgression m1555downTo5PvTz6A(short $this$downTo_u2d5PvTz6A, short to) {
        return UIntProgression.INSTANCE.m1517fromClosedRangeNkh28Cs(UInt.m375constructorimpl($this$downTo_u2d5PvTz6A & UShort.MAX_VALUE), UInt.m375constructorimpl(65535 & to), -1);
    }

    public static final UIntProgression reversed(UIntProgression $this$reversed) {
        Intrinsics.checkNotNullParameter($this$reversed, "<this>");
        return UIntProgression.INSTANCE.m1517fromClosedRangeNkh28Cs($this$reversed.getLast(), $this$reversed.getFirst(), -$this$reversed.getStep());
    }

    public static final ULongProgression reversed(ULongProgression $this$reversed) {
        Intrinsics.checkNotNullParameter($this$reversed, "<this>");
        return ULongProgression.INSTANCE.m1526fromClosedRange7ftBX0g($this$reversed.getLast(), $this$reversed.getFirst(), -$this$reversed.getStep());
    }

    public static final UIntProgression step(UIntProgression $this$step, int step) {
        Intrinsics.checkNotNullParameter($this$step, "<this>");
        RangesKt.checkStepIsPositive(step > 0, Integer.valueOf(step));
        return UIntProgression.INSTANCE.m1517fromClosedRangeNkh28Cs($this$step.getFirst(), $this$step.getLast(), $this$step.getStep() > 0 ? step : -step);
    }

    public static final ULongProgression step(ULongProgression $this$step, long step) {
        Intrinsics.checkNotNullParameter($this$step, "<this>");
        RangesKt.checkStepIsPositive(step > 0, Long.valueOf(step));
        return ULongProgression.INSTANCE.m1526fromClosedRange7ftBX0g($this$step.getFirst(), $this$step.getLast(), $this$step.getStep() > 0 ? step : -step);
    }

    /* renamed from: until-Kr8caGY, reason: not valid java name */
    public static final UIntRange m1561untilKr8caGY(byte $this$until_u2dKr8caGY, byte to) {
        return Intrinsics.compare(to & 255, 0) <= 0 ? UIntRange.INSTANCE.getEMPTY() : new UIntRange(UInt.m375constructorimpl($this$until_u2dKr8caGY & 255), UInt.m375constructorimpl(UInt.m375constructorimpl(to & 255) - 1), null);
    }

    /* renamed from: until-J1ME1BU, reason: not valid java name */
    public static final UIntRange m1560untilJ1ME1BU(int $this$until_u2dJ1ME1BU, int to) {
        int compare;
        compare = Integer.compare(to ^ Integer.MIN_VALUE, 0 ^ Integer.MIN_VALUE);
        return compare <= 0 ? UIntRange.INSTANCE.getEMPTY() : new UIntRange($this$until_u2dJ1ME1BU, UInt.m375constructorimpl(to - 1), null);
    }

    /* renamed from: until-eb3DHEI, reason: not valid java name */
    public static final ULongRange m1562untileb3DHEI(long $this$until_u2deb3DHEI, long to) {
        int compare;
        compare = Long.compare(to ^ Long.MIN_VALUE, 0 ^ Long.MIN_VALUE);
        return compare <= 0 ? ULongRange.INSTANCE.getEMPTY() : new ULongRange($this$until_u2deb3DHEI, ULong.m454constructorimpl(to - ULong.m454constructorimpl(1 & 4294967295L)), null);
    }

    /* renamed from: until-5PvTz6A, reason: not valid java name */
    public static final UIntRange m1559until5PvTz6A(short $this$until_u2d5PvTz6A, short to) {
        return Intrinsics.compare(to & UShort.MAX_VALUE, 0) <= 0 ? UIntRange.INSTANCE.getEMPTY() : new UIntRange(UInt.m375constructorimpl($this$until_u2d5PvTz6A & UShort.MAX_VALUE), UInt.m375constructorimpl(UInt.m375constructorimpl(65535 & to) - 1), null);
    }

    /* renamed from: coerceAtLeast-J1ME1BU, reason: not valid java name */
    public static final int m1534coerceAtLeastJ1ME1BU(int $this$coerceAtLeast_u2dJ1ME1BU, int minimumValue) {
        int compare;
        compare = Integer.compare($this$coerceAtLeast_u2dJ1ME1BU ^ Integer.MIN_VALUE, minimumValue ^ Integer.MIN_VALUE);
        return compare < 0 ? minimumValue : $this$coerceAtLeast_u2dJ1ME1BU;
    }

    /* renamed from: coerceAtLeast-eb3DHEI, reason: not valid java name */
    public static final long m1536coerceAtLeasteb3DHEI(long $this$coerceAtLeast_u2deb3DHEI, long minimumValue) {
        int compare;
        compare = Long.compare($this$coerceAtLeast_u2deb3DHEI ^ Long.MIN_VALUE, minimumValue ^ Long.MIN_VALUE);
        return compare < 0 ? minimumValue : $this$coerceAtLeast_u2deb3DHEI;
    }

    /* renamed from: coerceAtLeast-Kr8caGY, reason: not valid java name */
    public static final byte m1535coerceAtLeastKr8caGY(byte $this$coerceAtLeast_u2dKr8caGY, byte minimumValue) {
        return Intrinsics.compare($this$coerceAtLeast_u2dKr8caGY & 255, minimumValue & 255) < 0 ? minimumValue : $this$coerceAtLeast_u2dKr8caGY;
    }

    /* renamed from: coerceAtLeast-5PvTz6A, reason: not valid java name */
    public static final short m1533coerceAtLeast5PvTz6A(short $this$coerceAtLeast_u2d5PvTz6A, short minimumValue) {
        return Intrinsics.compare($this$coerceAtLeast_u2d5PvTz6A & UShort.MAX_VALUE, 65535 & minimumValue) < 0 ? minimumValue : $this$coerceAtLeast_u2d5PvTz6A;
    }

    /* renamed from: coerceAtMost-J1ME1BU, reason: not valid java name */
    public static final int m1538coerceAtMostJ1ME1BU(int $this$coerceAtMost_u2dJ1ME1BU, int maximumValue) {
        int compare;
        compare = Integer.compare($this$coerceAtMost_u2dJ1ME1BU ^ Integer.MIN_VALUE, maximumValue ^ Integer.MIN_VALUE);
        return compare > 0 ? maximumValue : $this$coerceAtMost_u2dJ1ME1BU;
    }

    /* renamed from: coerceAtMost-eb3DHEI, reason: not valid java name */
    public static final long m1540coerceAtMosteb3DHEI(long $this$coerceAtMost_u2deb3DHEI, long maximumValue) {
        int compare;
        compare = Long.compare($this$coerceAtMost_u2deb3DHEI ^ Long.MIN_VALUE, maximumValue ^ Long.MIN_VALUE);
        return compare > 0 ? maximumValue : $this$coerceAtMost_u2deb3DHEI;
    }

    /* renamed from: coerceAtMost-Kr8caGY, reason: not valid java name */
    public static final byte m1539coerceAtMostKr8caGY(byte $this$coerceAtMost_u2dKr8caGY, byte maximumValue) {
        return Intrinsics.compare($this$coerceAtMost_u2dKr8caGY & 255, maximumValue & 255) > 0 ? maximumValue : $this$coerceAtMost_u2dKr8caGY;
    }

    /* renamed from: coerceAtMost-5PvTz6A, reason: not valid java name */
    public static final short m1537coerceAtMost5PvTz6A(short $this$coerceAtMost_u2d5PvTz6A, short maximumValue) {
        return Intrinsics.compare($this$coerceAtMost_u2d5PvTz6A & UShort.MAX_VALUE, 65535 & maximumValue) > 0 ? maximumValue : $this$coerceAtMost_u2d5PvTz6A;
    }

    /* renamed from: coerceIn-WZ9TVnA, reason: not valid java name */
    public static final int m1543coerceInWZ9TVnA(int $this$coerceIn_u2dWZ9TVnA, int minimumValue, int maximumValue) {
        int compare;
        int compare2;
        int compare3;
        compare = Integer.compare(minimumValue ^ Integer.MIN_VALUE, maximumValue ^ Integer.MIN_VALUE);
        if (compare > 0) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + ((Object) UInt.m421toStringimpl(maximumValue)) + " is less than minimum " + ((Object) UInt.m421toStringimpl(minimumValue)) + '.');
        }
        compare2 = Integer.compare($this$coerceIn_u2dWZ9TVnA ^ Integer.MIN_VALUE, minimumValue ^ Integer.MIN_VALUE);
        if (compare2 < 0) {
            return minimumValue;
        }
        compare3 = Integer.compare($this$coerceIn_u2dWZ9TVnA ^ Integer.MIN_VALUE, maximumValue ^ Integer.MIN_VALUE);
        return compare3 > 0 ? maximumValue : $this$coerceIn_u2dWZ9TVnA;
    }

    /* renamed from: coerceIn-sambcqE, reason: not valid java name */
    public static final long m1545coerceInsambcqE(long $this$coerceIn_u2dsambcqE, long minimumValue, long maximumValue) {
        int compare;
        int compare2;
        int compare3;
        compare = Long.compare(minimumValue ^ Long.MIN_VALUE, maximumValue ^ Long.MIN_VALUE);
        if (compare > 0) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + ((Object) ULong.m500toStringimpl(maximumValue)) + " is less than minimum " + ((Object) ULong.m500toStringimpl(minimumValue)) + '.');
        }
        compare2 = Long.compare($this$coerceIn_u2dsambcqE ^ Long.MIN_VALUE, minimumValue ^ Long.MIN_VALUE);
        if (compare2 < 0) {
            return minimumValue;
        }
        compare3 = Long.compare($this$coerceIn_u2dsambcqE ^ Long.MIN_VALUE, maximumValue ^ Long.MIN_VALUE);
        return compare3 > 0 ? maximumValue : $this$coerceIn_u2dsambcqE;
    }

    /* renamed from: coerceIn-b33U2AM, reason: not valid java name */
    public static final byte m1544coerceInb33U2AM(byte $this$coerceIn_u2db33U2AM, byte minimumValue, byte maximumValue) {
        if (Intrinsics.compare(minimumValue & 255, maximumValue & 255) <= 0) {
            return Intrinsics.compare($this$coerceIn_u2db33U2AM & 255, minimumValue & 255) < 0 ? minimumValue : Intrinsics.compare($this$coerceIn_u2db33U2AM & 255, maximumValue & 255) > 0 ? maximumValue : $this$coerceIn_u2db33U2AM;
        }
        throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + ((Object) UByte.m342toStringimpl(maximumValue)) + " is less than minimum " + ((Object) UByte.m342toStringimpl(minimumValue)) + '.');
    }

    /* renamed from: coerceIn-VKSA0NQ, reason: not valid java name */
    public static final short m1542coerceInVKSA0NQ(short $this$coerceIn_u2dVKSA0NQ, short minimumValue, short maximumValue) {
        if (Intrinsics.compare(minimumValue & UShort.MAX_VALUE, maximumValue & UShort.MAX_VALUE) <= 0) {
            return Intrinsics.compare($this$coerceIn_u2dVKSA0NQ & UShort.MAX_VALUE, minimumValue & UShort.MAX_VALUE) < 0 ? minimumValue : Intrinsics.compare($this$coerceIn_u2dVKSA0NQ & UShort.MAX_VALUE, 65535 & maximumValue) > 0 ? maximumValue : $this$coerceIn_u2dVKSA0NQ;
        }
        throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + ((Object) UShort.m605toStringimpl(maximumValue)) + " is less than minimum " + ((Object) UShort.m605toStringimpl(minimumValue)) + '.');
    }

    /* renamed from: coerceIn-wuiCnnA, reason: not valid java name */
    public static final int m1546coerceInwuiCnnA(int $this$coerceIn_u2dwuiCnnA, ClosedRange<UInt> range) {
        int compare;
        int compare2;
        Intrinsics.checkNotNullParameter(range, "range");
        if (range instanceof ClosedFloatingPointRange) {
            return ((UInt) RangesKt.coerceIn(UInt.m369boximpl($this$coerceIn_u2dwuiCnnA), (ClosedFloatingPointRange<UInt>) range)).getData();
        }
        if (range.isEmpty()) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + '.');
        }
        compare = Integer.compare($this$coerceIn_u2dwuiCnnA ^ Integer.MIN_VALUE, range.getStart().getData() ^ Integer.MIN_VALUE);
        if (compare < 0) {
            return range.getStart().getData();
        }
        compare2 = Integer.compare($this$coerceIn_u2dwuiCnnA ^ Integer.MIN_VALUE, range.getEndInclusive().getData() ^ Integer.MIN_VALUE);
        return compare2 > 0 ? range.getEndInclusive().getData() : $this$coerceIn_u2dwuiCnnA;
    }

    /* renamed from: coerceIn-JPwROB0, reason: not valid java name */
    public static final long m1541coerceInJPwROB0(long $this$coerceIn_u2dJPwROB0, ClosedRange<ULong> range) {
        int compare;
        int compare2;
        Intrinsics.checkNotNullParameter(range, "range");
        if (range instanceof ClosedFloatingPointRange) {
            return ((ULong) RangesKt.coerceIn(ULong.m448boximpl($this$coerceIn_u2dJPwROB0), (ClosedFloatingPointRange<ULong>) range)).getData();
        }
        if (range.isEmpty()) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + '.');
        }
        compare = Long.compare($this$coerceIn_u2dJPwROB0 ^ Long.MIN_VALUE, range.getStart().getData() ^ Long.MIN_VALUE);
        if (compare < 0) {
            return range.getStart().getData();
        }
        compare2 = Long.compare($this$coerceIn_u2dJPwROB0 ^ Long.MIN_VALUE, range.getEndInclusive().getData() ^ Long.MIN_VALUE);
        return compare2 > 0 ? range.getEndInclusive().getData() : $this$coerceIn_u2dJPwROB0;
    }
}
