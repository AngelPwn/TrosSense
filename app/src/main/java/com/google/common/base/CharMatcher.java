package com.google.common.base;

import com.trossense.bl;
import java.util.Arrays;
import java.util.BitSet;

/* loaded from: classes.dex */
public abstract class CharMatcher implements Predicate<Character> {
    private static final int DISTINCT_CHARS = 65536;

    @Deprecated
    public static final CharMatcher WHITESPACE = whitespace();

    @Deprecated
    public static final CharMatcher BREAKING_WHITESPACE = breakingWhitespace();

    @Deprecated
    public static final CharMatcher ASCII = ascii();

    @Deprecated
    public static final CharMatcher DIGIT = digit();

    @Deprecated
    public static final CharMatcher JAVA_DIGIT = javaDigit();

    @Deprecated
    public static final CharMatcher JAVA_LETTER = javaLetter();

    @Deprecated
    public static final CharMatcher JAVA_LETTER_OR_DIGIT = javaLetterOrDigit();

    @Deprecated
    public static final CharMatcher JAVA_UPPER_CASE = javaUpperCase();

    @Deprecated
    public static final CharMatcher JAVA_LOWER_CASE = javaLowerCase();

    @Deprecated
    public static final CharMatcher JAVA_ISO_CONTROL = javaIsoControl();

    @Deprecated
    public static final CharMatcher INVISIBLE = invisible();

    @Deprecated
    public static final CharMatcher SINGLE_WIDTH = singleWidth();

    @Deprecated
    public static final CharMatcher ANY = any();

    @Deprecated
    public static final CharMatcher NONE = none();

    public abstract boolean matches(char c);

    public static CharMatcher any() {
        return Any.INSTANCE;
    }

    public static CharMatcher none() {
        return None.INSTANCE;
    }

    public static CharMatcher whitespace() {
        return Whitespace.INSTANCE;
    }

    public static CharMatcher breakingWhitespace() {
        return BreakingWhitespace.INSTANCE;
    }

    public static CharMatcher ascii() {
        return Ascii.INSTANCE;
    }

    public static CharMatcher digit() {
        return Digit.INSTANCE;
    }

    public static CharMatcher javaDigit() {
        return JavaDigit.INSTANCE;
    }

    public static CharMatcher javaLetter() {
        return JavaLetter.INSTANCE;
    }

    public static CharMatcher javaLetterOrDigit() {
        return JavaLetterOrDigit.INSTANCE;
    }

    public static CharMatcher javaUpperCase() {
        return JavaUpperCase.INSTANCE;
    }

    public static CharMatcher javaLowerCase() {
        return JavaLowerCase.INSTANCE;
    }

    public static CharMatcher javaIsoControl() {
        return JavaIsoControl.INSTANCE;
    }

    public static CharMatcher invisible() {
        return Invisible.INSTANCE;
    }

    public static CharMatcher singleWidth() {
        return SingleWidth.INSTANCE;
    }

    public static CharMatcher is(char match) {
        return new Is(match);
    }

    public static CharMatcher isNot(char match) {
        return new IsNot(match);
    }

    public static CharMatcher anyOf(CharSequence sequence) {
        switch (sequence.length()) {
            case 0:
                return none();
            case 1:
                return is(sequence.charAt(0));
            case 2:
                return isEither(sequence.charAt(0), sequence.charAt(1));
            default:
                return new AnyOf(sequence);
        }
    }

    public static CharMatcher noneOf(CharSequence sequence) {
        return anyOf(sequence).negate();
    }

    public static CharMatcher inRange(char startInclusive, char endInclusive) {
        return new InRange(startInclusive, endInclusive);
    }

    public static CharMatcher forPredicate(Predicate<? super Character> predicate) {
        return predicate instanceof CharMatcher ? (CharMatcher) predicate : new ForPredicate(predicate);
    }

    @Override // java.util.function.Predicate
    public CharMatcher negate() {
        return new Negated(this);
    }

    public CharMatcher and(CharMatcher other) {
        return new And(this, other);
    }

    public CharMatcher or(CharMatcher other) {
        return new Or(this, other);
    }

    public CharMatcher precomputed() {
        return Platform.precomputeCharMatcher(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CharMatcher precomputedInternal() {
        String negatedDescription;
        BitSet table = new BitSet();
        setBits(table);
        int totalCharacters = table.cardinality();
        if (totalCharacters * 2 <= 65536) {
            return precomputedPositive(totalCharacters, table, toString());
        }
        table.flip(0, 65536);
        int negatedCharacters = 65536 - totalCharacters;
        final String description = toString();
        if (description.endsWith(".negate()")) {
            negatedDescription = description.substring(0, description.length() - ".negate()".length());
        } else {
            negatedDescription = description + ".negate()";
        }
        return new NegatedFastMatcher(precomputedPositive(negatedCharacters, table, negatedDescription)) { // from class: com.google.common.base.CharMatcher.1
            @Override // com.google.common.base.CharMatcher.Negated, com.google.common.base.CharMatcher
            public String toString() {
                return description;
            }
        };
    }

    private static CharMatcher precomputedPositive(int totalCharacters, BitSet table, String description) {
        switch (totalCharacters) {
            case 0:
                return none();
            case 1:
                return is((char) table.nextSetBit(0));
            case 2:
                char c1 = (char) table.nextSetBit(0);
                char c2 = (char) table.nextSetBit(c1 + 1);
                return isEither(c1, c2);
            default:
                return isSmall(totalCharacters, table.length()) ? SmallCharMatcher.from(table, description) : new BitSetMatcher(table, description);
        }
    }

    private static boolean isSmall(int totalCharacters, int tableLength) {
        return totalCharacters <= 1023 && tableLength > (totalCharacters * 4) * 16;
    }

    void setBits(BitSet table) {
        for (int c = 65535; c >= 0; c--) {
            if (matches((char) c)) {
                table.set(c);
            }
        }
    }

    public boolean matchesAnyOf(CharSequence sequence) {
        return !matchesNoneOf(sequence);
    }

    public boolean matchesAllOf(CharSequence sequence) {
        for (int i = sequence.length() - 1; i >= 0; i--) {
            if (!matches(sequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean matchesNoneOf(CharSequence sequence) {
        return indexIn(sequence) == -1;
    }

    public int indexIn(CharSequence sequence) {
        return indexIn(sequence, 0);
    }

    public int indexIn(CharSequence sequence, int start) {
        int length = sequence.length();
        Preconditions.checkPositionIndex(start, length);
        for (int i = start; i < length; i++) {
            if (matches(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public int lastIndexIn(CharSequence sequence) {
        for (int i = sequence.length() - 1; i >= 0; i--) {
            if (matches(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public int countIn(CharSequence sequence) {
        int count = 0;
        for (int i = 0; i < sequence.length(); i++) {
            if (matches(sequence.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    public String removeFrom(CharSequence sequence) {
        String string = sequence.toString();
        int pos = indexIn(string);
        if (pos == -1) {
            return string;
        }
        char[] chars = string.toCharArray();
        int spread = 1;
        while (true) {
            while (true) {
                pos++;
                if (pos != chars.length) {
                    if (matches(chars[pos])) {
                        break;
                    }
                    chars[pos - spread] = chars[pos];
                } else {
                    return new String(chars, 0, pos - spread);
                }
            }
            spread++;
        }
    }

    public String retainFrom(CharSequence sequence) {
        return negate().removeFrom(sequence);
    }

    public String replaceFrom(CharSequence sequence, char replacement) {
        String string = sequence.toString();
        int pos = indexIn(string);
        if (pos == -1) {
            return string;
        }
        char[] chars = string.toCharArray();
        chars[pos] = replacement;
        for (int i = pos + 1; i < chars.length; i++) {
            if (matches(chars[i])) {
                chars[i] = replacement;
            }
        }
        return new String(chars);
    }

    public String replaceFrom(CharSequence sequence, CharSequence replacement) {
        int replacementLen = replacement.length();
        if (replacementLen == 0) {
            return removeFrom(sequence);
        }
        if (replacementLen == 1) {
            return replaceFrom(sequence, replacement.charAt(0));
        }
        String string = sequence.toString();
        int pos = indexIn(string);
        if (pos == -1) {
            return string;
        }
        int len = string.length();
        StringBuilder buf = new StringBuilder(((len * 3) / 2) + 16);
        int oldpos = 0;
        do {
            buf.append((CharSequence) string, oldpos, pos);
            buf.append(replacement);
            oldpos = pos + 1;
            pos = indexIn(string, oldpos);
        } while (pos != -1);
        buf.append((CharSequence) string, oldpos, len);
        return buf.toString();
    }

    public String trimFrom(CharSequence sequence) {
        int len = sequence.length();
        int first = 0;
        while (first < len && matches(sequence.charAt(first))) {
            first++;
        }
        int last = len - 1;
        while (last > first && matches(sequence.charAt(last))) {
            last--;
        }
        return sequence.subSequence(first, last + 1).toString();
    }

    public String trimLeadingFrom(CharSequence sequence) {
        int len = sequence.length();
        for (int first = 0; first < len; first++) {
            if (!matches(sequence.charAt(first))) {
                return sequence.subSequence(first, len).toString();
            }
        }
        return "";
    }

    public String trimTrailingFrom(CharSequence sequence) {
        int len = sequence.length();
        for (int last = len - 1; last >= 0; last--) {
            if (!matches(sequence.charAt(last))) {
                return sequence.subSequence(0, last + 1).toString();
            }
        }
        return "";
    }

    public String collapseFrom(CharSequence sequence, char replacement) {
        int len = sequence.length();
        int i = 0;
        while (i < len) {
            char c = sequence.charAt(i);
            if (matches(c)) {
                if (c == replacement && (i == len - 1 || !matches(sequence.charAt(i + 1)))) {
                    i++;
                } else {
                    StringBuilder builder = new StringBuilder(len).append(sequence, 0, i).append(replacement);
                    return finishCollapseFrom(sequence, i + 1, len, replacement, builder, true);
                }
            }
            i++;
        }
        return sequence.toString();
    }

    public String trimAndCollapseFrom(CharSequence sequence, char replacement) {
        int len = sequence.length();
        int first = 0;
        int last = len - 1;
        while (first < len && matches(sequence.charAt(first))) {
            first++;
        }
        while (last > first && matches(sequence.charAt(last))) {
            last--;
        }
        if (first == 0 && last == len - 1) {
            return collapseFrom(sequence, replacement);
        }
        return finishCollapseFrom(sequence, first, last + 1, replacement, new StringBuilder((last + 1) - first), false);
    }

    private String finishCollapseFrom(CharSequence sequence, int start, int end, char replacement, StringBuilder builder, boolean inMatchingGroup) {
        for (int i = start; i < end; i++) {
            char c = sequence.charAt(i);
            if (matches(c)) {
                if (!inMatchingGroup) {
                    builder.append(replacement);
                    inMatchingGroup = true;
                }
            } else {
                builder.append(c);
                inMatchingGroup = false;
            }
        }
        return builder.toString();
    }

    @Override // com.google.common.base.Predicate
    @Deprecated
    public boolean apply(Character character) {
        return matches(character.charValue());
    }

    public String toString() {
        return super.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String showCharacter(char c) {
        char[] tmp = {'\\', 'u', 0, 0, 0, 0};
        for (int i = 0; i < 4; i++) {
            tmp[5 - i] = "0123456789ABCDEF".charAt(c & 15);
            c = (char) (c >> 4);
        }
        return String.copyValueOf(tmp);
    }

    /* loaded from: classes.dex */
    static abstract class FastMatcher extends CharMatcher {
        FastMatcher() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher
        public final CharMatcher precomputed() {
            return this;
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public CharMatcher negate() {
            return new NegatedFastMatcher(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static abstract class NamedFastMatcher extends FastMatcher {
        private final String description;

        /* JADX INFO: Access modifiers changed from: package-private */
        public NamedFastMatcher(String description) {
            this.description = (String) Preconditions.checkNotNull(description);
        }

        @Override // com.google.common.base.CharMatcher
        public final String toString() {
            return this.description;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class NegatedFastMatcher extends Negated {
        NegatedFastMatcher(CharMatcher original) {
            super(original);
        }

        @Override // com.google.common.base.CharMatcher
        public final CharMatcher precomputed() {
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class BitSetMatcher extends NamedFastMatcher {
        private final BitSet table;

        private BitSetMatcher(BitSet table, String description) {
            super(description);
            this.table = table.length() + 64 < table.size() ? (BitSet) table.clone() : table;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return this.table.get(c);
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet bitSet) {
            bitSet.or(this.table);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Any extends NamedFastMatcher {
        static final Any INSTANCE = new Any();

        private Any() {
            super("CharMatcher.any()");
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return true;
        }

        @Override // com.google.common.base.CharMatcher
        public int indexIn(CharSequence sequence) {
            return sequence.length() == 0 ? -1 : 0;
        }

        @Override // com.google.common.base.CharMatcher
        public int indexIn(CharSequence sequence, int start) {
            int length = sequence.length();
            Preconditions.checkPositionIndex(start, length);
            if (start == length) {
                return -1;
            }
            return start;
        }

        @Override // com.google.common.base.CharMatcher
        public int lastIndexIn(CharSequence sequence) {
            return sequence.length() - 1;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matchesAllOf(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return true;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matchesNoneOf(CharSequence sequence) {
            return sequence.length() == 0;
        }

        @Override // com.google.common.base.CharMatcher
        public String removeFrom(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return "";
        }

        @Override // com.google.common.base.CharMatcher
        public String replaceFrom(CharSequence sequence, char replacement) {
            char[] array = new char[sequence.length()];
            Arrays.fill(array, replacement);
            return new String(array);
        }

        @Override // com.google.common.base.CharMatcher
        public String replaceFrom(CharSequence sequence, CharSequence replacement) {
            StringBuilder result = new StringBuilder(sequence.length() * replacement.length());
            for (int i = 0; i < sequence.length(); i++) {
                result.append(replacement);
            }
            return result.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String collapseFrom(CharSequence sequence, char replacement) {
            return sequence.length() == 0 ? "" : String.valueOf(replacement);
        }

        @Override // com.google.common.base.CharMatcher
        public String trimFrom(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return "";
        }

        @Override // com.google.common.base.CharMatcher
        public int countIn(CharSequence sequence) {
            return sequence.length();
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher and(CharMatcher other) {
            return (CharMatcher) Preconditions.checkNotNull(other);
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher or(CharMatcher other) {
            Preconditions.checkNotNull(other);
            return this;
        }

        @Override // com.google.common.base.CharMatcher.FastMatcher, com.google.common.base.CharMatcher, java.util.function.Predicate
        public CharMatcher negate() {
            return none();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class None extends NamedFastMatcher {
        static final None INSTANCE = new None();

        private None() {
            super("CharMatcher.none()");
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return false;
        }

        @Override // com.google.common.base.CharMatcher
        public int indexIn(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return -1;
        }

        @Override // com.google.common.base.CharMatcher
        public int indexIn(CharSequence sequence, int start) {
            int length = sequence.length();
            Preconditions.checkPositionIndex(start, length);
            return -1;
        }

        @Override // com.google.common.base.CharMatcher
        public int lastIndexIn(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return -1;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matchesAllOf(CharSequence sequence) {
            return sequence.length() == 0;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matchesNoneOf(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return true;
        }

        @Override // com.google.common.base.CharMatcher
        public String removeFrom(CharSequence sequence) {
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String replaceFrom(CharSequence sequence, char replacement) {
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String replaceFrom(CharSequence sequence, CharSequence replacement) {
            Preconditions.checkNotNull(replacement);
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String collapseFrom(CharSequence sequence, char replacement) {
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String trimFrom(CharSequence sequence) {
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String trimLeadingFrom(CharSequence sequence) {
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public String trimTrailingFrom(CharSequence sequence) {
            return sequence.toString();
        }

        @Override // com.google.common.base.CharMatcher
        public int countIn(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return 0;
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher and(CharMatcher other) {
            Preconditions.checkNotNull(other);
            return this;
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher or(CharMatcher other) {
            return (CharMatcher) Preconditions.checkNotNull(other);
        }

        @Override // com.google.common.base.CharMatcher.FastMatcher, com.google.common.base.CharMatcher, java.util.function.Predicate
        public CharMatcher negate() {
            return any();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Whitespace extends NamedFastMatcher {
        static final int MULTIPLIER = 1682554634;
        static final String TABLE = "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001  \f\u2009\u3000\u2004\u3000\u3000\u2028\n \u3000";
        static final int SHIFT = Integer.numberOfLeadingZeros(TABLE.length() - 1);
        static final Whitespace INSTANCE = new Whitespace();

        Whitespace() {
            super("CharMatcher.whitespace()");
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return TABLE.charAt((MULTIPLIER * c) >>> SHIFT) == c;
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            for (int i = 0; i < TABLE.length(); i++) {
                table.set(TABLE.charAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class BreakingWhitespace extends CharMatcher {
        static final CharMatcher INSTANCE = new BreakingWhitespace();

        private BreakingWhitespace() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            switch (c) {
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                case ' ':
                case bl.bu /* 133 */:
                case 5760:
                case 8232:
                case 8233:
                case 8287:
                case 12288:
                    return true;
                case 8199:
                    return false;
                default:
                    return c >= 8192 && c <= 8202;
            }
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.breakingWhitespace()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Ascii extends NamedFastMatcher {
        static final Ascii INSTANCE = new Ascii();

        Ascii() {
            super("CharMatcher.ascii()");
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return c <= 127;
        }
    }

    /* loaded from: classes.dex */
    private static class RangesMatcher extends CharMatcher {
        private final String description;
        private final char[] rangeEnds;
        private final char[] rangeStarts;

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        RangesMatcher(String description, char[] rangeStarts, char[] rangeEnds) {
            this.description = description;
            this.rangeStarts = rangeStarts;
            this.rangeEnds = rangeEnds;
            Preconditions.checkArgument(rangeStarts.length == rangeEnds.length);
            for (int i = 0; i < rangeStarts.length; i++) {
                Preconditions.checkArgument(rangeStarts[i] <= rangeEnds[i]);
                if (i + 1 < rangeStarts.length) {
                    Preconditions.checkArgument(rangeEnds[i] < rangeStarts[i + 1]);
                }
            }
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            int index = Arrays.binarySearch(this.rangeStarts, c);
            if (index >= 0) {
                return true;
            }
            int index2 = (~index) - 1;
            return index2 >= 0 && c <= this.rangeEnds[index2];
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return this.description;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Digit extends RangesMatcher {
        static final Digit INSTANCE = new Digit();
        private static final String ZEROES = "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０";

        private static char[] zeroes() {
            return ZEROES.toCharArray();
        }

        private static char[] nines() {
            char[] nines = new char[ZEROES.length()];
            for (int i = 0; i < ZEROES.length(); i++) {
                nines[i] = (char) (ZEROES.charAt(i) + '\t');
            }
            return nines;
        }

        private Digit() {
            super("CharMatcher.digit()", zeroes(), nines());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class JavaDigit extends CharMatcher {
        static final JavaDigit INSTANCE = new JavaDigit();

        private JavaDigit() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return Character.isDigit(c);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.javaDigit()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class JavaLetter extends CharMatcher {
        static final JavaLetter INSTANCE = new JavaLetter();

        private JavaLetter() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return Character.isLetter(c);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.javaLetter()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class JavaLetterOrDigit extends CharMatcher {
        static final JavaLetterOrDigit INSTANCE = new JavaLetterOrDigit();

        private JavaLetterOrDigit() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return Character.isLetterOrDigit(c);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.javaLetterOrDigit()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class JavaUpperCase extends CharMatcher {
        static final JavaUpperCase INSTANCE = new JavaUpperCase();

        private JavaUpperCase() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return Character.isUpperCase(c);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.javaUpperCase()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class JavaLowerCase extends CharMatcher {
        static final JavaLowerCase INSTANCE = new JavaLowerCase();

        private JavaLowerCase() {
        }

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return Character.isLowerCase(c);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.javaLowerCase()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class JavaIsoControl extends NamedFastMatcher {
        static final JavaIsoControl INSTANCE = new JavaIsoControl();

        private JavaIsoControl() {
            super("CharMatcher.javaIsoControl()");
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return c <= 31 || (c >= 127 && c <= 159);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Invisible extends RangesMatcher {
        static final Invisible INSTANCE = new Invisible();
        private static final String RANGE_ENDS = "  \u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f \u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb";
        private static final String RANGE_STARTS = "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa";

        private Invisible() {
            super("CharMatcher.invisible()", RANGE_STARTS.toCharArray(), RANGE_ENDS.toCharArray());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class SingleWidth extends RangesMatcher {
        static final SingleWidth INSTANCE = new SingleWidth();

        private SingleWidth() {
            super("CharMatcher.singleWidth()", "\u0000־א׳\u0600ݐ\u0e00Ḁ℀ﭐﹰ｡".toCharArray(), "ӹ־ת״ۿݿ\u0e7f₯℺﷿\ufeffￜ".toCharArray());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Negated extends CharMatcher {
        final CharMatcher original;

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        Negated(CharMatcher original) {
            this.original = (CharMatcher) Preconditions.checkNotNull(original);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return !this.original.matches(c);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matchesAllOf(CharSequence sequence) {
            return this.original.matchesNoneOf(sequence);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matchesNoneOf(CharSequence sequence) {
            return this.original.matchesAllOf(sequence);
        }

        @Override // com.google.common.base.CharMatcher
        public int countIn(CharSequence sequence) {
            return sequence.length() - this.original.countIn(sequence);
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            BitSet tmp = new BitSet();
            this.original.setBits(tmp);
            tmp.flip(0, 65536);
            table.or(tmp);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public CharMatcher negate() {
            return this.original;
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return this.original + ".negate()";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class And extends CharMatcher {
        final CharMatcher first;
        final CharMatcher second;

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        And(CharMatcher a, CharMatcher b) {
            this.first = (CharMatcher) Preconditions.checkNotNull(a);
            this.second = (CharMatcher) Preconditions.checkNotNull(b);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return this.first.matches(c) && this.second.matches(c);
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            BitSet tmp1 = new BitSet();
            this.first.setBits(tmp1);
            BitSet tmp2 = new BitSet();
            this.second.setBits(tmp2);
            tmp1.and(tmp2);
            table.or(tmp1);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.and(" + this.first + ", " + this.second + ")";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Or extends CharMatcher {
        final CharMatcher first;
        final CharMatcher second;

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        Or(CharMatcher a, CharMatcher b) {
            this.first = (CharMatcher) Preconditions.checkNotNull(a);
            this.second = (CharMatcher) Preconditions.checkNotNull(b);
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            this.first.setBits(table);
            this.second.setBits(table);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return this.first.matches(c) || this.second.matches(c);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.or(" + this.first + ", " + this.second + ")";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class Is extends FastMatcher {
        private final char match;

        Is(char match) {
            this.match = match;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return c == this.match;
        }

        @Override // com.google.common.base.CharMatcher
        public String replaceFrom(CharSequence sequence, char replacement) {
            return sequence.toString().replace(this.match, replacement);
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher and(CharMatcher other) {
            return other.matches(this.match) ? this : none();
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher or(CharMatcher other) {
            return other.matches(this.match) ? other : super.or(other);
        }

        @Override // com.google.common.base.CharMatcher.FastMatcher, com.google.common.base.CharMatcher, java.util.function.Predicate
        public CharMatcher negate() {
            return isNot(this.match);
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            table.set(this.match);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.is('" + CharMatcher.showCharacter(this.match) + "')";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class IsNot extends FastMatcher {
        private final char match;

        IsNot(char match) {
            this.match = match;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return c != this.match;
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher and(CharMatcher other) {
            return other.matches(this.match) ? super.and(other) : other;
        }

        @Override // com.google.common.base.CharMatcher
        public CharMatcher or(CharMatcher other) {
            return other.matches(this.match) ? any() : this;
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            table.set(0, this.match);
            table.set(this.match + 1, 65536);
        }

        @Override // com.google.common.base.CharMatcher.FastMatcher, com.google.common.base.CharMatcher, java.util.function.Predicate
        public CharMatcher negate() {
            return is(this.match);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.isNot('" + CharMatcher.showCharacter(this.match) + "')";
        }
    }

    private static IsEither isEither(char c1, char c2) {
        return new IsEither(c1, c2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class IsEither extends FastMatcher {
        private final char match1;
        private final char match2;

        IsEither(char match1, char match2) {
            this.match1 = match1;
            this.match2 = match2;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return c == this.match1 || c == this.match2;
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            table.set(this.match1);
            table.set(this.match2);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.anyOf(\"" + CharMatcher.showCharacter(this.match1) + CharMatcher.showCharacter(this.match2) + "\")";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class AnyOf extends CharMatcher {
        private final char[] chars;

        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        @Deprecated
        public /* bridge */ /* synthetic */ boolean apply(Character ch) {
            return super.apply(ch);
        }

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        public AnyOf(CharSequence chars) {
            this.chars = chars.toString().toCharArray();
            Arrays.sort(this.chars);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return Arrays.binarySearch(this.chars, c) >= 0;
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            for (char c : this.chars) {
                table.set(c);
            }
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            StringBuilder description = new StringBuilder("CharMatcher.anyOf(\"");
            for (char c : this.chars) {
                description.append(CharMatcher.showCharacter(c));
            }
            description.append("\")");
            return description.toString();
        }
    }

    /* loaded from: classes.dex */
    private static final class InRange extends FastMatcher {
        private final char endInclusive;
        private final char startInclusive;

        InRange(char startInclusive, char endInclusive) {
            Preconditions.checkArgument(endInclusive >= startInclusive);
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return this.startInclusive <= c && c <= this.endInclusive;
        }

        @Override // com.google.common.base.CharMatcher
        void setBits(BitSet table) {
            table.set(this.startInclusive, this.endInclusive + 1);
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.inRange('" + CharMatcher.showCharacter(this.startInclusive) + "', '" + CharMatcher.showCharacter(this.endInclusive) + "')";
        }
    }

    /* loaded from: classes.dex */
    private static final class ForPredicate extends CharMatcher {
        private final Predicate<? super Character> predicate;

        @Override // com.google.common.base.CharMatcher, java.util.function.Predicate
        public /* bridge */ /* synthetic */ java.util.function.Predicate negate() {
            return super.negate();
        }

        ForPredicate(Predicate<? super Character> predicate) {
            this.predicate = (Predicate) Preconditions.checkNotNull(predicate);
        }

        @Override // com.google.common.base.CharMatcher
        public boolean matches(char c) {
            return this.predicate.apply(Character.valueOf(c));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.google.common.base.CharMatcher, com.google.common.base.Predicate
        public boolean apply(Character character) {
            return this.predicate.apply(Preconditions.checkNotNull(character));
        }

        @Override // com.google.common.base.CharMatcher
        public String toString() {
            return "CharMatcher.forPredicate(" + this.predicate + ")";
        }
    }
}
