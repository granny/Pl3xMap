package net.pl3x.map.core.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class StringUtils {
    // from apache commons StringEscapeUtils
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    // from apache commons StringEscapeUtils
    @SuppressWarnings({"ReassignedVariable", "DataFlowIssue", "RedundantLabeledSwitchRuleCodeBlock"})
    private static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\' -> out.write('\\');
                    case '\'' -> out.write('\'');
                    case '\"' -> out.write('"');
                    case 'r' -> out.write('\r');
                    case 'f' -> out.write('\f');
                    case 't' -> out.write('\t');
                    case 'n' -> out.write('\n');
                    case 'b' -> out.write('\b');
                    case 'u' -> {
                        // uh-oh, we're in unicode country....
                        inUnicode = true;
                    }
                    default -> out.write(ch);
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }
}
