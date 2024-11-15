package org.jose4j.json.internal.json_simple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

/* loaded from: classes5.dex */
public class JSONValue {
    public static Object parse(Reader in) {
        try {
            JSONParser parser = new JSONParser();
            return parser.parse(in);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        return parse(in);
    }

    public static Object parseWithException(Reader in) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(in);
    }

    public static Object parseWithException(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(s);
    }

    public static void writeJSONString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }
        if (value instanceof String) {
            out.write(34);
            out.write(escape((String) value));
            out.write(34);
            return;
        }
        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
                out.write("null");
                return;
            } else {
                out.write(value.toString());
                return;
            }
        }
        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN()) {
                out.write("null");
                return;
            } else {
                out.write(value.toString());
                return;
            }
        }
        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }
        if (value instanceof JSONStreamAware) {
            ((JSONStreamAware) value).writeJSONString(out);
            return;
        }
        if (value instanceof JSONAware) {
            out.write(((JSONAware) value).toJSONString());
            return;
        }
        if (value instanceof Map) {
            JSONObject.writeJSONString((Map) value, out);
            return;
        }
        if (value instanceof Collection) {
            JSONArray.writeJSONString((Collection) value, out);
            return;
        }
        if (value instanceof byte[]) {
            JSONArray.writeJSONString((byte[]) value, out);
            return;
        }
        if (value instanceof short[]) {
            JSONArray.writeJSONString((short[]) value, out);
            return;
        }
        if (value instanceof int[]) {
            JSONArray.writeJSONString((int[]) value, out);
            return;
        }
        if (value instanceof long[]) {
            JSONArray.writeJSONString((long[]) value, out);
            return;
        }
        if (value instanceof float[]) {
            JSONArray.writeJSONString((float[]) value, out);
            return;
        }
        if (value instanceof double[]) {
            JSONArray.writeJSONString((double[]) value, out);
            return;
        }
        if (value instanceof boolean[]) {
            JSONArray.writeJSONString((boolean[]) value, out);
            return;
        }
        if (value instanceof char[]) {
            JSONArray.writeJSONString((char[]) value, out);
        } else if (value instanceof Object[]) {
            JSONArray.writeJSONString((Object[]) value, out);
        } else {
            writeJSONString(value.toString(), out);
        }
    }

    public static String toJSONString(Object value) {
        StringWriter writer = new StringWriter();
        try {
            writeJSONString(value, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        escape(s, sb);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(String s, StringBuilder sb) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if ((ch >= 0 && ch <= 31) || ((ch >= 127 && ch <= 159) || (ch >= 8192 && ch <= 8447))) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                        break;
                    } else {
                        sb.append(ch);
                        break;
                    }
                    break;
            }
        }
    }
}
