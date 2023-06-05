package core.helpers;

import java.util.regex.Pattern;

public class Transform {
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String CDATA_PSEUDO_END = "]]&gt;";
    private static final String CDATA_EMBEDED_END = CDATA_END + CDATA_PSEUDO_END + CDATA_START;
    private static final int CDATA_END_LEN = CDATA_END.length();
    private static final Pattern UNSAFE_XML_CHARS = Pattern.compile("[\u0000-\u0008\u000b\u000c\u000e-\u001f<>&'\"]");

    public static String escapeTags(final String input) {
        if (input == null || input.length() == 0 || !UNSAFE_XML_CHARS.matcher(input).find()) {
            return input;
        }
        StringBuffer buf = new StringBuffer(input);
        return escapeTags(buf);
    }

    public static String escapeTags(final StringBuffer buf) {
        for (int i = 0; i < buf.length(); i++) {
            char ch = buf.charAt(i);
            switch (ch) {
                case '\t':
                case '\n':
                case '\r':
                    break;
                case '&':
                    buf.replace(i, i + 1, "&amp;");
                    break;
                case '<':
                    buf.replace(i, i + 1, "&lt;");
                    break;
                case '>':
                    buf.replace(i, i + 1, "&gt;");
                    break;
                case '"':
                    buf.replace(i, i + 1, "&quot;");
                    break;
                case '\'':
                    buf.replace(i, i + 1, "&#39;");
                    break;
                default:
                    if (ch < '\u0020') {
                        buf.replace(i, i + 1, "\uFFFD");
                    }
                    break;
            }
        }
        return buf.toString();
    }

    public static void appendEscapingCDATA(StringBuilder output, String str) {
        if (str == null) {
            return;
        }

        int end = str.indexOf(CDATA_END);

        if (end < 0) {
            output.append(str);
            return;
        }

        int start = 0;

        while (end > -1) {
            output.append(str.substring(start, end));
            output.append(CDATA_EMBEDED_END);
            start = end + CDATA_END_LEN;

            if (start < str.length()) {
                end = str.indexOf(CDATA_END, start);
            } else {
                return;
            }
        }

        output.append(str.substring(start));
    }
}