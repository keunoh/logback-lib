package core.helpers;

import core.CoreConstants;
import core.status.Status;

import java.util.LinkedList;
import java.util.List;

public class ThrowableToStringArray {

    public static String[] convert(Throwable t) {
        List<String> strList = new LinkedList<>();
        extract(strList, t, null);
        return strList.toArray(new String[0]);
    }

    private static void extract(List<String> strList, Throwable t, StackTraceElement[] parentSTE) {

        StackTraceElement[] ste = t.getStackTrace();
        final int numberOfcommonFrames = findNumberOfCommonFrames(ste, parentSTE);

        strList.add(formatFirstLine(t, parentSTE));
        for (int i = 0; i < (ste.length - numberOfcommonFrames); i++) {
            strList.add("\tat " + ste[i].toString());
        }

        if (numberOfcommonFrames != 0) {
            strList.add("\t... " + numberOfcommonFrames + " common frames omitted");
        }

        Throwable cause = t.getCause();
        if (cause != null) {
            ThrowableToStringArray.extract(strList, cause, ste);
        }
    }

    private static String formatFirstLine(Throwable t, StackTraceElement[] parentSTE) {
        String prefix = "";
        if (parentSTE != null) {
            prefix = CoreConstants.CAUSED_BY;
        }

        String result = prefix + t.getClass().getName();
        if (t.getMessage() != null) {
            result += ": " + t.getMessage();
        }
        return result;
    }

    private static int findNumberOfCommonFrames(StackTraceElement[] ste, StackTraceElement[] parentSTE) {
        if (parentSTE == null) {
            return 0;
        }

        int steIndex = ste.length - 1;
        int parentIndex = parentSTE.length - 1;
        int count = 0;
        while (steIndex >= 0 && parentIndex >= 0) {
            if (ste[steIndex].equals(parentSTE[parentIndex])) {
                count++;
            } else {
                break;
            }
            steIndex--;
            parentIndex--;
        }
        return count;
    }
}