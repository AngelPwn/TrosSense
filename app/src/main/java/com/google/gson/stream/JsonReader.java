package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import com.trossense.bl;
import io.netty.util.internal.StringUtil;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public class JsonReader implements Closeable {
    static final int BUFFER_SIZE = 1024;
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_EOF = 17;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private final Reader in;
    private int[] pathIndices;
    private String[] pathNames;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int stackSize;
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    int peeked = 0;
    private int[] stack = new int[32];

    public JsonReader(Reader in) {
        this.stackSize = 0;
        int[] iArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        iArr[i] = 6;
        this.pathNames = new String[32];
        this.pathIndices = new int[32];
        this.in = (Reader) Objects.requireNonNull(in, "in == null");
    }

    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 3) {
            push(1);
            this.pathIndices[this.stackSize - 1] = 0;
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString());
    }

    public void endArray() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 4) {
            this.stackSize--;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected END_ARRAY but was " + peek() + locationString());
    }

    public void beginObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 1) {
            push(3);
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + locationString());
    }

    public void endObject() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 2) {
            this.stackSize--;
            this.pathNames[this.stackSize] = null;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            this.peeked = 0;
            return;
        }
        throw new IllegalStateException("Expected END_OBJECT but was " + peek() + locationString());
    }

    public boolean hasNext() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        return (p == 2 || p == 4 || p == 17) ? false : true;
    }

    public JsonToken peek() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        switch (p) {
            case 1:
                return JsonToken.BEGIN_OBJECT;
            case 2:
                return JsonToken.END_OBJECT;
            case 3:
                return JsonToken.BEGIN_ARRAY;
            case 4:
                return JsonToken.END_ARRAY;
            case 5:
            case 6:
                return JsonToken.BOOLEAN;
            case 7:
                return JsonToken.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonToken.STRING;
            case 12:
            case 13:
            case 14:
                return JsonToken.NAME;
            case 15:
            case 16:
                return JsonToken.NUMBER;
            case 17:
                return JsonToken.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    int doPeek() throws IOException {
        int peekStack = this.stack[this.stackSize - 1];
        if (peekStack == 1) {
            this.stack[this.stackSize - 1] = 2;
        } else if (peekStack == 2) {
            switch (nextNonWhitespace(true)) {
                case 44:
                    break;
                case 59:
                    checkLenient();
                    break;
                case 93:
                    this.peeked = 4;
                    return 4;
                default:
                    throw syntaxError("Unterminated array");
            }
        } else {
            if (peekStack == 3 || peekStack == 5) {
                this.stack[this.stackSize - 1] = 4;
                if (peekStack == 5) {
                    switch (nextNonWhitespace(true)) {
                        case 44:
                            break;
                        case 59:
                            checkLenient();
                            break;
                        case bl.bm /* 125 */:
                            this.peeked = 2;
                            return 2;
                        default:
                            throw syntaxError("Unterminated object");
                    }
                }
                int c = nextNonWhitespace(true);
                switch (c) {
                    case 34:
                        this.peeked = 13;
                        return 13;
                    case 39:
                        checkLenient();
                        this.peeked = 12;
                        return 12;
                    case bl.bm /* 125 */:
                        if (peekStack != 5) {
                            this.peeked = 2;
                            return 2;
                        }
                        throw syntaxError("Expected name");
                    default:
                        checkLenient();
                        this.pos--;
                        if (isLiteral((char) c)) {
                            this.peeked = 14;
                            return 14;
                        }
                        throw syntaxError("Expected name");
                }
            }
            if (peekStack == 4) {
                this.stack[this.stackSize - 1] = 5;
                switch (nextNonWhitespace(true)) {
                    case 58:
                        break;
                    case 61:
                        checkLenient();
                        if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>') {
                            this.pos++;
                            break;
                        }
                        break;
                    default:
                        throw syntaxError("Expected ':'");
                }
            } else if (peekStack == 6) {
                if (this.lenient) {
                    consumeNonExecutePrefix();
                }
                this.stack[this.stackSize - 1] = 7;
            } else if (peekStack == 7) {
                if (nextNonWhitespace(false) == -1) {
                    this.peeked = 17;
                    return 17;
                }
                checkLenient();
                this.pos--;
            } else if (peekStack == 8) {
                throw new IllegalStateException("JsonReader is closed");
            }
        }
        switch (nextNonWhitespace(true)) {
            case 34:
                this.peeked = 9;
                return 9;
            case 39:
                checkLenient();
                this.peeked = 8;
                return 8;
            case 44:
            case 59:
                break;
            case 91:
                this.peeked = 3;
                return 3;
            case 93:
                if (peekStack == 1) {
                    this.peeked = 4;
                    return 4;
                }
                break;
            case 123:
                this.peeked = 1;
                return 1;
            default:
                this.pos--;
                int result = peekKeyword();
                if (result != 0) {
                    return result;
                }
                int result2 = peekNumber();
                if (result2 != 0) {
                    return result2;
                }
                if (!isLiteral(this.buffer[this.pos])) {
                    throw syntaxError("Expected value");
                }
                checkLenient();
                this.peeked = 10;
                return 10;
        }
        if (peekStack == 1 || peekStack == 2) {
            checkLenient();
            this.pos--;
            this.peeked = 7;
            return 7;
        }
        throw syntaxError("Unexpected value");
    }

    private int peekKeyword() throws IOException {
        String keyword;
        String keywordUpper;
        int peeking;
        char c = this.buffer[this.pos];
        if (c == 't' || c == 'T') {
            keyword = "true";
            keywordUpper = "TRUE";
            peeking = 5;
        } else if (c == 'f' || c == 'F') {
            keyword = "false";
            keywordUpper = "FALSE";
            peeking = 6;
        } else {
            if (c != 'n' && c != 'N') {
                return 0;
            }
            keyword = "null";
            keywordUpper = "NULL";
            peeking = 7;
        }
        int length = keyword.length();
        for (int i = 1; i < length; i++) {
            if (this.pos + i >= this.limit && !fillBuffer(i + 1)) {
                return 0;
            }
            char c2 = this.buffer[this.pos + i];
            if (c2 != keyword.charAt(i) && c2 != keywordUpper.charAt(i)) {
                return 0;
            }
        }
        int i2 = this.pos;
        if ((i2 + length < this.limit || fillBuffer(length + 1)) && isLiteral(this.buffer[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.peeked = peeking;
        return peeking;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x00d9, code lost:            if (r8 != 2) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x00db, code lost:            if (r7 == false) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x00e1, code lost:            if (r4 != Long.MIN_VALUE) goto L70;     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x00e3, code lost:            if (r6 == false) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x00e9, code lost:            if (r4 != 0) goto L73;     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x00eb, code lost:            if (r6 != false) goto L78;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x00ed, code lost:            if (r6 == false) goto L75;     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x00ef, code lost:            r10 = r4;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x00f2, code lost:            r18.peekedLong = r10;        r18.pos += r9;        r18.peeked = 15;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x00fd, code lost:            return 15;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x00f1, code lost:            r10 = -r4;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x00fe, code lost:            if (r8 == 2) goto L84;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0100, code lost:            if (r8 == 4) goto L84;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0103, code lost:            if (r8 != 7) goto L83;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0106, code lost:            return 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x0107, code lost:            r18.peekedNumberLength = r9;        r18.peeked = 16;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x010d, code lost:            return 16;     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x00d5, code lost:            if (isLiteral(r10) != false) goto L86;     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x010e, code lost:            return 0;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:35:0x002e. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int peekNumber() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 294
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.peekNumber():int");
    }

    private boolean isLiteral(char c) throws IOException {
        switch (c) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case bl.bm /* 125 */:
                return false;
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\':
                checkLenient();
                return false;
            default:
                return true;
        }
    }

    public String nextName() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 14) {
            result = nextUnquotedValue();
        } else if (p == 12) {
            result = nextQuotedValue('\'');
        } else if (p == 13) {
            result = nextQuotedValue('\"');
        } else {
            throw new IllegalStateException("Expected a name but was " + peek() + locationString());
        }
        this.peeked = 0;
        this.pathNames[this.stackSize - 1] = result;
        return result;
    }

    public String nextString() throws IOException {
        String result;
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 10) {
            result = nextUnquotedValue();
        } else if (p == 8) {
            result = nextQuotedValue('\'');
        } else if (p == 9) {
            result = nextQuotedValue('\"');
        } else if (p == 11) {
            result = this.peekedString;
            this.peekedString = null;
        } else if (p == 15) {
            result = Long.toString(this.peekedLong);
        } else if (p == 16) {
            result = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            throw new IllegalStateException("Expected a string but was " + peek() + locationString());
        }
        this.peeked = 0;
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
        return result;
    }

    public boolean nextBoolean() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 5) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return true;
        }
        if (p == 6) {
            this.peeked = 0;
            int[] iArr2 = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr2[i2] = iArr2[i2] + 1;
            return false;
        }
        throw new IllegalStateException("Expected a boolean but was " + peek() + locationString());
    }

    public void nextNull() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 7) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return;
        }
        throw new IllegalStateException("Expected null but was " + peek() + locationString());
    }

    public double nextDouble() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 15) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return this.peekedLong;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9) {
            this.peekedString = nextQuotedValue(p == 8 ? '\'' : '\"');
        } else if (p == 10) {
            this.peekedString = nextUnquotedValue();
        } else if (p != 11) {
            throw new IllegalStateException("Expected a double but was " + peek() + locationString());
        }
        this.peeked = 11;
        double result = Double.parseDouble(this.peekedString);
        if (!this.lenient && (Double.isNaN(result) || Double.isInfinite(result))) {
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + result + locationString());
        }
        this.peekedString = null;
        this.peeked = 0;
        int[] iArr2 = this.pathIndices;
        int i2 = this.stackSize - 1;
        iArr2[i2] = iArr2[i2] + 1;
        return result;
    }

    public long nextLong() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 15) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return this.peekedLong;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9 || p == 10) {
            if (p == 10) {
                this.peekedString = nextUnquotedValue();
            } else {
                this.peekedString = nextQuotedValue(p == 8 ? '\'' : '\"');
            }
            try {
                long result = Long.parseLong(this.peekedString);
                this.peeked = 0;
                int[] iArr2 = this.pathIndices;
                int i2 = this.stackSize - 1;
                iArr2[i2] = iArr2[i2] + 1;
                return result;
            } catch (NumberFormatException e) {
            }
        } else {
            throw new IllegalStateException("Expected a long but was " + peek() + locationString());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        long result2 = (long) asDouble;
        if (result2 != asDouble) {
            throw new NumberFormatException("Expected a long but was " + this.peekedString + locationString());
        }
        this.peekedString = null;
        this.peeked = 0;
        int[] iArr3 = this.pathIndices;
        int i3 = this.stackSize - 1;
        iArr3[i3] = iArr3[i3] + 1;
        return result2;
    }

    private String nextQuotedValue(char quote) throws IOException {
        char[] buffer = this.buffer;
        StringBuilder builder = null;
        do {
            int c = this.pos;
            int l = this.limit;
            int start = c;
            while (c < l) {
                int p = c + 1;
                char c2 = buffer[c];
                if (c2 == quote) {
                    this.pos = p;
                    int len = (p - start) - 1;
                    if (builder == null) {
                        return new String(buffer, start, len);
                    }
                    builder.append(buffer, start, len);
                    return builder.toString();
                }
                if (c2 == '\\') {
                    this.pos = p;
                    int len2 = (p - start) - 1;
                    if (builder == null) {
                        int estimatedLength = (len2 + 1) * 2;
                        builder = new StringBuilder(Math.max(estimatedLength, 16));
                    }
                    builder.append(buffer, start, len2);
                    builder.append(readEscapeCharacter());
                    int p2 = this.pos;
                    l = this.limit;
                    start = p2;
                    c = p2;
                } else {
                    if (c2 == '\n') {
                        this.lineNumber++;
                        this.lineStart = p;
                    }
                    c = p;
                }
            }
            if (builder == null) {
                int estimatedLength2 = (c - start) * 2;
                builder = new StringBuilder(Math.max(estimatedLength2, 16));
            }
            builder.append(buffer, start, c - start);
            this.pos = c;
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    private String nextUnquotedValue() throws IOException {
        StringBuilder builder = null;
        int i = 0;
        while (true) {
            if (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case bl.bm /* 125 */:
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                        break;
                }
            } else if (i < this.buffer.length) {
                if (fillBuffer(i + 1)) {
                }
            } else {
                if (builder == null) {
                    builder = new StringBuilder(Math.max(i, 16));
                }
                builder.append(this.buffer, this.pos, i);
                this.pos += i;
                i = 0;
                if (!fillBuffer(1)) {
                }
            }
        }
        String result = builder == null ? new String(this.buffer, this.pos, i) : builder.append(this.buffer, this.pos, i).toString();
        this.pos += i;
        return result;
    }

    private void skipQuotedValue(char quote) throws IOException {
        char[] buffer = this.buffer;
        do {
            int c = this.pos;
            int l = this.limit;
            while (c < l) {
                int p = c + 1;
                char c2 = buffer[c];
                if (c2 == quote) {
                    this.pos = p;
                    return;
                }
                if (c2 == '\\') {
                    this.pos = p;
                    readEscapeCharacter();
                    int p2 = this.pos;
                    l = this.limit;
                    c = p2;
                } else {
                    if (c2 == '\n') {
                        this.lineNumber++;
                        this.lineStart = p;
                    }
                    c = p;
                }
            }
            this.pos = c;
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    /* JADX WARN: Removed duplicated region for block: B:4:0x0009  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void skipUnquotedValue() throws java.io.IOException {
        /*
            r3 = this;
        L1:
            r0 = 0
        L2:
            int r1 = r3.pos
            int r1 = r1 + r0
            int r2 = r3.limit
            if (r1 >= r2) goto L1f
            char[] r1 = r3.buffer
            int r2 = r3.pos
            int r2 = r2 + r0
            char r1 = r1[r2]
            switch(r1) {
                case 9: goto L19;
                case 10: goto L19;
                case 12: goto L19;
                case 13: goto L19;
                case 32: goto L19;
                case 35: goto L16;
                case 44: goto L19;
                case 47: goto L16;
                case 58: goto L19;
                case 59: goto L16;
                case 61: goto L16;
                case 91: goto L19;
                case 92: goto L16;
                case 93: goto L19;
                case 123: goto L19;
                case 125: goto L19;
                default: goto L13;
            }
        L13:
            int r0 = r0 + 1
            goto L2
        L16:
            r3.checkLenient()
        L19:
            int r1 = r3.pos
            int r1 = r1 + r0
            r3.pos = r1
            return
        L1f:
            int r1 = r3.pos
            int r1 = r1 + r0
            r3.pos = r1
            r0 = 1
            boolean r0 = r3.fillBuffer(r0)
            if (r0 != 0) goto L1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.skipUnquotedValue():void");
    }

    public int nextInt() throws IOException {
        int p = this.peeked;
        if (p == 0) {
            p = doPeek();
        }
        if (p == 15) {
            int result = (int) this.peekedLong;
            if (this.peekedLong != result) {
                throw new NumberFormatException("Expected an int but was " + this.peekedLong + locationString());
            }
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i = this.stackSize - 1;
            iArr[i] = iArr[i] + 1;
            return result;
        }
        if (p == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p == 8 || p == 9 || p == 10) {
            if (p == 10) {
                this.peekedString = nextUnquotedValue();
            } else {
                this.peekedString = nextQuotedValue(p == 8 ? '\'' : '\"');
            }
            try {
                int result2 = Integer.parseInt(this.peekedString);
                this.peeked = 0;
                int[] iArr2 = this.pathIndices;
                int i2 = this.stackSize - 1;
                iArr2[i2] = iArr2[i2] + 1;
                return result2;
            } catch (NumberFormatException e) {
            }
        } else {
            throw new IllegalStateException("Expected an int but was " + peek() + locationString());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        int result3 = (int) asDouble;
        if (result3 != asDouble) {
            throw new NumberFormatException("Expected an int but was " + this.peekedString + locationString());
        }
        this.peekedString = null;
        this.peeked = 0;
        int[] iArr3 = this.pathIndices;
        int i3 = this.stackSize - 1;
        iArr3[i3] = iArr3[i3] + 1;
        return result3;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:6:0x0010. Please report as an issue. */
    public void skipValue() throws IOException {
        int count = 0;
        do {
            int p = this.peeked;
            if (p == 0) {
                p = doPeek();
            }
            switch (p) {
                case 1:
                    push(3);
                    count++;
                    this.peeked = 0;
                    break;
                case 2:
                    if (count == 0) {
                        this.pathNames[this.stackSize - 1] = null;
                    }
                    this.stackSize--;
                    count--;
                    this.peeked = 0;
                    break;
                case 3:
                    push(1);
                    count++;
                    this.peeked = 0;
                    break;
                case 4:
                    this.stackSize--;
                    count--;
                    this.peeked = 0;
                    break;
                case 5:
                case 6:
                case 7:
                case 11:
                case 15:
                default:
                    this.peeked = 0;
                    break;
                case 8:
                    skipQuotedValue('\'');
                    this.peeked = 0;
                    break;
                case 9:
                    skipQuotedValue('\"');
                    this.peeked = 0;
                    break;
                case 10:
                    skipUnquotedValue();
                    this.peeked = 0;
                    break;
                case 12:
                    skipQuotedValue('\'');
                    if (count == 0) {
                        this.pathNames[this.stackSize - 1] = "<skipped>";
                    }
                    this.peeked = 0;
                    break;
                case 13:
                    skipQuotedValue('\"');
                    if (count == 0) {
                        this.pathNames[this.stackSize - 1] = "<skipped>";
                    }
                    this.peeked = 0;
                    break;
                case 14:
                    skipUnquotedValue();
                    if (count == 0) {
                        this.pathNames[this.stackSize - 1] = "<skipped>";
                    }
                    this.peeked = 0;
                    break;
                case 16:
                    this.pos += this.peekedNumberLength;
                    this.peeked = 0;
                    break;
                case 17:
                    return;
            }
        } while (count > 0);
        int[] iArr = this.pathIndices;
        int i = this.stackSize - 1;
        iArr[i] = iArr[i] + 1;
    }

    private void push(int newTop) {
        if (this.stackSize == this.stack.length) {
            int newLength = this.stackSize * 2;
            this.stack = Arrays.copyOf(this.stack, newLength);
            this.pathIndices = Arrays.copyOf(this.pathIndices, newLength);
            this.pathNames = (String[]) Arrays.copyOf(this.pathNames, newLength);
        }
        int[] iArr = this.stack;
        int i = this.stackSize;
        this.stackSize = i + 1;
        iArr[i] = newTop;
    }

    private boolean fillBuffer(int minimum) throws IOException {
        char[] buffer = this.buffer;
        this.lineStart -= this.pos;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            int total = this.in.read(buffer, this.limit, buffer.length - this.limit);
            if (total == -1) {
                return false;
            }
            this.limit += total;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer[0] == 65279) {
                this.pos++;
                this.lineStart++;
                minimum++;
            }
        } while (this.limit < minimum);
        return true;
    }

    private int nextNonWhitespace(boolean throwOnEof) throws IOException {
        char[] buffer = this.buffer;
        int p = this.pos;
        int l = this.limit;
        while (true) {
            if (p == l) {
                this.pos = p;
                if (fillBuffer(1)) {
                    p = this.pos;
                    l = this.limit;
                } else {
                    if (throwOnEof) {
                        throw new EOFException("End of input" + locationString());
                    }
                    return -1;
                }
            }
            int p2 = p + 1;
            char c = buffer[p];
            if (c == '\n') {
                this.lineNumber++;
                this.lineStart = p2;
            } else if (c != ' ' && c != '\r' && c != '\t') {
                if (c == '/') {
                    this.pos = p2;
                    if (p2 == l) {
                        this.pos--;
                        boolean charsLoaded = fillBuffer(2);
                        this.pos++;
                        if (!charsLoaded) {
                            return c;
                        }
                    }
                    checkLenient();
                    char peek = buffer[this.pos];
                    switch (peek) {
                        case '*':
                            this.pos++;
                            if (skipTo("*/")) {
                                int p3 = this.pos + 2;
                                l = this.limit;
                                p = p3;
                                break;
                            } else {
                                throw syntaxError("Unterminated comment");
                            }
                        case '/':
                            this.pos++;
                            skipToEndOfLine();
                            int p4 = this.pos;
                            l = this.limit;
                            p = p4;
                            break;
                        default:
                            return c;
                    }
                } else if (c == '#') {
                    this.pos = p2;
                    checkLenient();
                    skipToEndOfLine();
                    int p5 = this.pos;
                    l = this.limit;
                    p = p5;
                } else {
                    this.pos = p2;
                    return c;
                }
            }
            p = p2;
        }
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        char c;
        do {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                this.pos = i + 1;
                c = cArr[i];
                if (c == '\n') {
                    this.lineNumber++;
                    this.lineStart = this.pos;
                    return;
                }
            } else {
                return;
            }
        } while (c != '\r');
    }

    private boolean skipTo(String toFind) throws IOException {
        int length = toFind.length();
        while (true) {
            if (this.pos + length <= this.limit || fillBuffer(length)) {
                if (this.buffer[this.pos] == '\n') {
                    this.lineNumber++;
                    this.lineStart = this.pos + 1;
                } else {
                    for (int c = 0; c < length; c++) {
                        if (this.buffer[this.pos + c] != toFind.charAt(c)) {
                            break;
                        }
                    }
                    return true;
                }
                int c2 = this.pos;
                this.pos = c2 + 1;
            } else {
                return false;
            }
        }
    }

    public String toString() {
        return getClass().getSimpleName() + locationString();
    }

    String locationString() {
        int line = this.lineNumber + 1;
        int column = (this.pos - this.lineStart) + 1;
        return " at line " + line + " column " + column + " path " + getPath();
    }

    private String getPath(boolean usePreviousPath) {
        StringBuilder result = new StringBuilder().append(Typography.dollar);
        for (int i = 0; i < this.stackSize; i++) {
            switch (this.stack[i]) {
                case 1:
                case 2:
                    int pathIndex = this.pathIndices[i];
                    if (usePreviousPath && pathIndex > 0 && i == this.stackSize - 1) {
                        pathIndex--;
                    }
                    result.append('[').append(pathIndex).append(']');
                    break;
                case 3:
                case 4:
                case 5:
                    result.append('.');
                    if (this.pathNames[i] != null) {
                        result.append(this.pathNames[i]);
                        break;
                    } else {
                        break;
                    }
            }
        }
        return result.toString();
    }

    public String getPreviousPath() {
        return getPath(true);
    }

    public String getPath() {
        return getPath(false);
    }

    private char readEscapeCharacter() throws IOException {
        int i;
        if (this.pos == this.limit && !fillBuffer(1)) {
            throw syntaxError("Unterminated escape sequence");
        }
        char[] cArr = this.buffer;
        int i2 = this.pos;
        this.pos = i2 + 1;
        char escaped = cArr[i2];
        switch (escaped) {
            case '\n':
                this.lineNumber++;
                this.lineStart = this.pos;
            case '\"':
            case '\'':
            case '/':
            case '\\':
                return escaped;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return StringUtil.CARRIAGE_RETURN;
            case 't':
                return '\t';
            case 'u':
                if (this.pos + 4 > this.limit && !fillBuffer(4)) {
                    throw syntaxError("Unterminated escape sequence");
                }
                char result = 0;
                int i3 = this.pos;
                int end = i3 + 4;
                while (i3 < end) {
                    char c = this.buffer[i3];
                    char result2 = (char) (result << 4);
                    if (c >= '0' && c <= '9') {
                        i = c - '0';
                    } else if (c >= 'a' && c <= 'f') {
                        i = (c - 'a') + 10;
                    } else if (c >= 'A' && c <= 'F') {
                        i = (c - 'A') + 10;
                    } else {
                        throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                    }
                    result = (char) (i + result2);
                    i3++;
                }
                this.pos += 4;
                return result;
            default:
                throw syntaxError("Invalid escape sequence");
        }
    }

    private IOException syntaxError(String message) throws IOException {
        throw new MalformedJsonException(message + locationString());
    }

    private void consumeNonExecutePrefix() throws IOException {
        nextNonWhitespace(true);
        this.pos--;
        if (this.pos + 5 > this.limit && !fillBuffer(5)) {
            return;
        }
        int p = this.pos;
        char[] buf = this.buffer;
        if (buf[p] != ')' || buf[p + 1] != ']' || buf[p + 2] != '}' || buf[p + 3] != '\'' || buf[p + 4] != '\n') {
            return;
        }
        this.pos += 5;
    }

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() { // from class: com.google.gson.stream.JsonReader.1
            @Override // com.google.gson.internal.JsonReaderInternalAccess
            public void promoteNameToValue(JsonReader reader) throws IOException {
                if (reader instanceof JsonTreeReader) {
                    ((JsonTreeReader) reader).promoteNameToValue();
                    return;
                }
                int p = reader.peeked;
                if (p == 0) {
                    p = reader.doPeek();
                }
                if (p == 13) {
                    reader.peeked = 9;
                } else if (p == 12) {
                    reader.peeked = 8;
                } else {
                    if (p == 14) {
                        reader.peeked = 10;
                        return;
                    }
                    throw new IllegalStateException("Expected a name but was " + reader.peek() + reader.locationString());
                }
            }
        };
    }
}
