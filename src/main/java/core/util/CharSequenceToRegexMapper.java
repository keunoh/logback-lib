package core.util;

import java.text.DateFormatSymbols;

public class CharSequenceToRegexMapper {

    DateFormatSymbols symbols = DateFormatSymbols.getInstance();

    String toRegex(CharSequenceState css) {
        final int occurrences = css.occurrences;
        final char c = css.c;
        switch (css.c) {
            case 'G':
            case 'z':
                return ".*";
            case 'M':
                if (occurrences <= 2)
                    return number(occurrences);
                else if (occurrences == 3)
                    return getRegexForShortMonths();
                else
                    return getRegexForLongMonths();
            case 'y':
            case 'w':
            case 'W':
            case 'D':
            case 'd':
            case 'F':
            case 'H':
            case 'k':
            case 'K':
            case 'h':
            case 'm':
            case 's':
            case 'S':
                return number(occurrences);
            case 'E':
                if (occurrences >= 4) {
                    return getRegexForLongDaysOfTheWeek();
                } else {
                    return getRegexForShortDaysOfTheWeek();
                }
            case 'a':
                return getRegexForAmPms();
            case 'Z':
                return "(\\+|-)\\d{4}";
            case '.':
                return "\\.";
            case '\\':
                throw new IllegalStateException("Forward slashed are not allowed");
            case '\'':
                if (occurrences == 1) {
                    return "";
                }
                throw new IllegalStateException("Too many single quotes");
            default:
                if (occurrences == 1) {
                    return "" + c;
                } else {
                    return c + "{" + occurrences + "}";
                }
        }
    }

    private String number(int occurrences) {
        return "\\d{" + occurrences + "}";
    }

    private String getRegexForAmPms() {
        return symbolArrayToRegex(symbols.getAmPmStrings());
    }

    private String getRegexForLongDaysOfTheWeek() {
        return symbolArrayToRegex(symbols.getWeekdays());
    }

    private String getRegexForShortDaysOfTheWeek() {
        return symbolArrayToRegex(symbols.getShortWeekdays());
    }

    private String getRegexForLongMonths() {
        return symbolArrayToRegex(symbols.getMonths());
    }

    String getRegexForShortMonths() {
        return symbolArrayToRegex(symbols.getShortMonths());
    }

    private String symbolArrayToRegex(String[] symbolArray) {
        int[] minMax = findMinMaxLengthsInSymbols(symbolArray);
        return ".{" + minMax[0] + "," + minMax[1] + "}";
    }

    static int[] findMinMaxLengthsInSymbols(String[] symbols) {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (String symbol : symbols) {
            int len = symbol.length();
            // some SENTIENL values can be empty strings, the month at index 12 or the weekday at index 0
            if (len == 0)
                continue;
            min = Math.min(min, len);
            max = Math.max(max, len);
        }
        return new int[]{min, max};
    }
}























