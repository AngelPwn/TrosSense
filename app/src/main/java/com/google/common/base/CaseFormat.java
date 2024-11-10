package com.google.common.base;

import java.io.Serializable;
import javax.annotation.Nullable;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'LOWER_UNDERSCORE' uses external variables
	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:451)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByField(EnumVisitor.java:372)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByWrappedInsn(EnumVisitor.java:337)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:322)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:262)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:151)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:100)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* loaded from: classes.dex */
public abstract class CaseFormat {
    private static final /* synthetic */ CaseFormat[] $VALUES;
    public static final CaseFormat LOWER_CAMEL;
    public static final CaseFormat LOWER_HYPHEN = new CaseFormat("LOWER_HYPHEN", 0, CharMatcher.is('-'), "-") { // from class: com.google.common.base.CaseFormat.1
        @Override // com.google.common.base.CaseFormat
        String normalizeWord(String word) {
            return Ascii.toLowerCase(word);
        }

        @Override // com.google.common.base.CaseFormat
        String convert(CaseFormat format, String s) {
            if (format == LOWER_UNDERSCORE) {
                return s.replace('-', '_');
            }
            if (format == UPPER_UNDERSCORE) {
                return Ascii.toUpperCase(s.replace('-', '_'));
            }
            return super.convert(format, s);
        }
    };
    public static final CaseFormat LOWER_UNDERSCORE;
    public static final CaseFormat UPPER_CAMEL;
    public static final CaseFormat UPPER_UNDERSCORE;
    private final CharMatcher wordBoundary;
    private final String wordSeparator;

    abstract String normalizeWord(String str);

    public static CaseFormat valueOf(String name) {
        return (CaseFormat) Enum.valueOf(CaseFormat.class, name);
    }

    public static CaseFormat[] values() {
        return (CaseFormat[]) $VALUES.clone();
    }

    static {
        String str = "_";
        LOWER_UNDERSCORE = new CaseFormat("LOWER_UNDERSCORE", 1, CharMatcher.is('_'), str) { // from class: com.google.common.base.CaseFormat.2
            @Override // com.google.common.base.CaseFormat
            String normalizeWord(String word) {
                return Ascii.toLowerCase(word);
            }

            @Override // com.google.common.base.CaseFormat
            String convert(CaseFormat format, String s) {
                if (format == LOWER_HYPHEN) {
                    return s.replace('_', '-');
                }
                if (format == UPPER_UNDERSCORE) {
                    return Ascii.toUpperCase(s);
                }
                return super.convert(format, s);
            }
        };
        String str2 = "";
        LOWER_CAMEL = new CaseFormat("LOWER_CAMEL", 2, CharMatcher.inRange('A', 'Z'), str2) { // from class: com.google.common.base.CaseFormat.3
            @Override // com.google.common.base.CaseFormat
            String normalizeWord(String word) {
                return CaseFormat.firstCharOnlyToUpper(word);
            }
        };
        UPPER_CAMEL = new CaseFormat("UPPER_CAMEL", 3, CharMatcher.inRange('A', 'Z'), str2) { // from class: com.google.common.base.CaseFormat.4
            @Override // com.google.common.base.CaseFormat
            String normalizeWord(String word) {
                return CaseFormat.firstCharOnlyToUpper(word);
            }
        };
        UPPER_UNDERSCORE = new CaseFormat("UPPER_UNDERSCORE", 4, CharMatcher.is('_'), str) { // from class: com.google.common.base.CaseFormat.5
            @Override // com.google.common.base.CaseFormat
            String normalizeWord(String word) {
                return Ascii.toUpperCase(word);
            }

            @Override // com.google.common.base.CaseFormat
            String convert(CaseFormat format, String s) {
                if (format == LOWER_HYPHEN) {
                    return Ascii.toLowerCase(s.replace('_', '-'));
                }
                if (format == LOWER_UNDERSCORE) {
                    return Ascii.toLowerCase(s);
                }
                return super.convert(format, s);
            }
        };
        $VALUES = new CaseFormat[]{LOWER_HYPHEN, LOWER_UNDERSCORE, LOWER_CAMEL, UPPER_CAMEL, UPPER_UNDERSCORE};
    }

    private CaseFormat(String str, int i, CharMatcher wordBoundary, String wordSeparator) {
        this.wordBoundary = wordBoundary;
        this.wordSeparator = wordSeparator;
    }

    public final String to(CaseFormat format, String str) {
        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(str);
        return format == this ? str : convert(format, str);
    }

    String convert(CaseFormat format, String s) {
        StringBuilder out = null;
        int i = 0;
        int j = -1;
        while (true) {
            int indexIn = this.wordBoundary.indexIn(s, j + 1);
            j = indexIn;
            if (indexIn == -1) {
                break;
            }
            if (i == 0) {
                out = new StringBuilder(s.length() + (this.wordSeparator.length() * 4));
                out.append(format.normalizeFirstWord(s.substring(i, j)));
            } else {
                out.append(format.normalizeWord(s.substring(i, j)));
            }
            out.append(format.wordSeparator);
            i = j + this.wordSeparator.length();
        }
        if (i == 0) {
            return format.normalizeFirstWord(s);
        }
        return out.append(format.normalizeWord(s.substring(i))).toString();
    }

    public Converter<String, String> converterTo(CaseFormat targetFormat) {
        return new StringConverter(this, targetFormat);
    }

    /* loaded from: classes.dex */
    private static final class StringConverter extends Converter<String, String> implements Serializable {
        private static final long serialVersionUID = 0;
        private final CaseFormat sourceFormat;
        private final CaseFormat targetFormat;

        StringConverter(CaseFormat sourceFormat, CaseFormat targetFormat) {
            this.sourceFormat = (CaseFormat) Preconditions.checkNotNull(sourceFormat);
            this.targetFormat = (CaseFormat) Preconditions.checkNotNull(targetFormat);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.common.base.Converter
        public String doForward(String s) {
            return this.sourceFormat.to(this.targetFormat, s);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.common.base.Converter
        public String doBackward(String s) {
            return this.targetFormat.to(this.sourceFormat, s);
        }

        @Override // com.google.common.base.Converter, com.google.common.base.Function
        public boolean equals(@Nullable Object object) {
            if (!(object instanceof StringConverter)) {
                return false;
            }
            StringConverter that = (StringConverter) object;
            return this.sourceFormat.equals(that.sourceFormat) && this.targetFormat.equals(that.targetFormat);
        }

        public int hashCode() {
            return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
        }

        public String toString() {
            return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
        }
    }

    private String normalizeFirstWord(String word) {
        return this == LOWER_CAMEL ? Ascii.toLowerCase(word) : normalizeWord(word);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String firstCharOnlyToUpper(String word) {
        return word.isEmpty() ? word : Ascii.toUpperCase(word.charAt(0)) + Ascii.toLowerCase(word.substring(1));
    }
}
