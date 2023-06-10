package core.util;

import java.util.ArrayList;
import java.util.List;

public class DatePatternToRegexUtil {

    final String datePattern;
    final int datePatternLength;
    final CharSequenceToRegexMapper regexMapper = new CharSequenceToRegexMapper();

    public DatePatternToRegexUtil(String datePattern) {
        this.datePattern = datePattern;
        datePatternLength = datePattern.length();
    }

    public String toRegex() {
        List<CharSequenceState> charSequenceList = tokenize();
        StringBuilder sb = new StringBuilder();
        for (CharSequenceState seq : charSequenceList) {
            sb.append(regexMapper.toRegex(seq));
        }
        return sb.toString();
    }

    private List<CharSequenceState> tokenize() {
        List<CharSequenceState> sequenceList = new ArrayList<>();

        CharSequenceState lastCharSequenceState = null;

        for (int i = 0; i < datePatternLength; i++) {
            char t = datePattern.charAt(i);
            if (lastCharSequenceState == null || lastCharSequenceState.c != t) {
                lastCharSequenceState = new CharSequenceState(t);
                sequenceList.add(lastCharSequenceState);
            } else {
                lastCharSequenceState.incrementOccurrences();
            }
        }
        return sequenceList;
    }

}