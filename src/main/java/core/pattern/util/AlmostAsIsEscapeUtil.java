package core.pattern.util;

import static core.CoreConstants.PERCENT_CHAR;
import static core.CoreConstants.RIGHT_PARENTHESIS_CHAR;

public class AlmostAsIsEscapeUtil extends RestrictedEscapeUtil {

    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        super.escape("" + PERCENT_CHAR + RIGHT_PARENTHESIS_CHAR, buf, next, pointer);
    }
}
