package core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class StringCollectionUtil {

    @SuppressWarnings("unchecked")
    public static void retainMatching(Collection<String> values, String... patterns) {
        retainMatching(values, Arrays.asList(patterns));
    }

    public static void retainMatching(Collection<String> values, Collection<String> patterns) {
        if (patterns.isEmpty())
            return;
        List<String> matches = new ArrayList<>(values.size());
        for (String p : patterns) {
            Pattern pattern = Pattern.compile(p);
            for (String value : values) {
                if (pattern.matcher(value).matches()) {
                    matches.add(value);
                }
            }
        }
        values.retainAll(matches);
    }


    @SuppressWarnings("unchecked")
    public static void removeMatching(Collection<String> values, String... patterns) {
        removeMatching(values, Arrays.asList(patterns));
    }

    public static void removeMatching(Collection<String> values, Collection<String> patterns) {
        List<String> matches = new ArrayList<>(values.size());
        for (String p : patterns) {
            Pattern pattern = Pattern.compile(p);
            for (String value : values) {
                if (pattern.matcher(value).matches()) {
                    matches.add(value);
                }
            }
        }
        values.removeAll(matches);
    }
}