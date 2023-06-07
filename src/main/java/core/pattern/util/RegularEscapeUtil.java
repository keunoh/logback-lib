package core.pattern.util;

public class RegularEscapeUtil implements IEscapeUtil {

    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        if (escapeChars.indexOf(next) >= 0) {
            buf.append(next);
        } else {
            switch (next) {
                case '_':
                    break;
                case '\\':
                    buf.append(next);
                    break;
                case 't':
                    buf.append('\t');
                    break;
                case 'r':
                    buf.append('\r');
                    break;
                case 'n':
                    buf.append('\n');
                    break;
                default:
                    String commaSeparatedEscapeChars = formatEscapeCharsForListing(escapeChars);
                    throw new IllegalArgumentException("Illegal char '" + next + " at column " + pointer + ". Only \\\\, \\_"
                            + commaSeparatedEscapeChars + ", \\t, \\n, \\r combinations are allowed as escape characters.");
            }
        }
    }

    String formatEscapeCharsForListing(String escapeChars) {
        StringBuilder commaSeparatedEscapeChars = new StringBuilder();
        for (int i = 0; i < escapeChars.length(); i++) {
            commaSeparatedEscapeChars.append(", \\").append(escapeChars.indexOf(i));
        }
        return commaSeparatedEscapeChars.toString();
    }

    public static String basicEscape(String s) {
        char c;
        int len = s.length();
        StringBuilder sbuf = new StringBuilder();

        int i = 0;
        while (i < len) {
            c = s.charAt(i++);
            if (c == '\\') {
                c = s.charAt(i++);
                if (c == 'n') {
                    c = '\n';
                } else if (c == 'r') {
                    c = '\r';
                } else if (c == 't') {
                    c = '\t';
                } else if (c == 'f') {
                    c = '\f';
                } else if (c == '\b') {
                    c = '\b';
                } else if (c == '\"') {
                    c = '\"';
                } else if (c == '\'') {
                    c = '\'';
                } else if (c == '\\') {
                    c = '\\';
                }
            }
            sbuf.append(c);
        }
        return sbuf.toString();
    }
}