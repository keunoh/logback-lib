package core.util;

import core.Context;
import core.CoreConstants;
import core.helpers.ThrowableToStringArray;
import core.status.ErrorStatus;
import core.status.Status;
import core.status.StatusManager;
import core.status.StatusUtil;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import static core.status.StatusUtil.filterStatusListByTimeThreshold;

public class StatusPrinter {

    private static PrintStream ps = System.out;

    static CachingDateFormatter cachingDateFormat = new CachingDateFormatter("HH:mm:ss,SSS");
    public static void setPrintStream(PrintStream printStream) {
        ps = printStream;
    }

    public static void printInCaseOfErrorsOrWarnings(Context context) {
        printInCaseOfErrorsOrWarnings(context, 0);
    }

    public static void printInCaseOfErrorsOrWarnings(Context context, long threshold) {
        if (context == null)
            throw new IllegalArgumentException("Context argument cannot be null");

        StatusManager sm = context.getStatusManager();
        if (sm == null) {
            ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
        } else {
            StatusUtil statusUtil = new StatusUtil(context);
            if (statusUtil.getHighestLevel(threshold) >= ErrorStatus.WARN) {
                print(sm, threshold);
            }
        }
    }

    public static void printIfErrorsOccurred(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context argument cannot be null");
        }

        StatusManager sm = context.getStatusManager();
        if (sm == null) {
            ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
        } else {
            StatusUtil statusUtil = new StatusUtil(context);
            if (statusUtil.getHighestLevel(0) == ErrorStatus.ERROR) {
                print(sm);
            }
        }
    }

    public static void print(Context context) {
        print(context, 0);
    }

    public static void print(Context context, long threshold) {
        if (context == null)
            throw new IllegalArgumentException("Context argument cannot be null");

        StatusManager sm = context.getStatusManager();
        if (sm == null) {
            ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
        } else {
            print(sm, threshold);
        }
    }

    public static void print(StatusManager sm) {
        print(sm, 0);
    }

    public static void print(StatusManager sm, long threshold) {
        StringBuilder sb = new StringBuilder();
        List<Status> filteredList = filterStatusListByTimeThreshold(sm.getCopyOfStatusList(), threshold);
        buildStrFromStatusList(sb, filteredList);
        ps.println(sb.toString());
    }

    public static void print(List<Status> statusList) {
        StringBuilder sb = new StringBuilder();
        buildStrFromStatusList(sb, statusList);
        ps.println(sb.toString());
    }

    private static void buildStrFromStatusList(StringBuilder sb, List<Status> statusList) {
        if (statusList == null)
            return;
        for (Status s : statusList) {
            buildStr(sb, "", s);
        }
    }

    private static void appendThrowable(StringBuilder sb, Throwable t) {
        String[] stringRep = ThrowableToStringArray.convert(t);

        for (String s : stringRep) {
            if (s.startsWith(CoreConstants.CAUSED_BY)) {
                // nothing
            } else if (Character.isDigit(s.charAt(0))) {
                sb.append("\t...");
            } else {
                sb.append("\tat ");
            }
            sb.append(s).append(CoreConstants.LINE_SEPARATOR);
        }
    }

    public static void buildStr(StringBuilder sb, String indentation, Status s) {
        String prefix;
        if (s.hasChildren()) {
            prefix = indentation + "+ ";
        } else {
            prefix = indentation + "|-";
        }

        if (cachingDateFormat != null) {
            String dateStr = cachingDateFormat.format(s.getDate());
            sb.append(dateStr).append(" ");
        }
        sb.append(prefix).append(s).append(CoreConstants.LINE_SEPARATOR);

        if (s.getThrowable() != null) {
            appendThrowable(sb, s.getThrowable());
        }
        if (s.hasChildren()) {
            Iterator<Status> ite = s.iterator();
            while (ite.hasNext()) {
                Status child = ite.next();
                buildStr(sb, indentation + "  ", child);
            }
        }
    }
}





















