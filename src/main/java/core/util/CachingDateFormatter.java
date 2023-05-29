package core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CachingDateFormatter {
    long lastTimestamp = -1;
    String cachedStr = null;
    final SimpleDateFormat sdf;

    public CachingDateFormatter(String pattern) {
        sdf = new SimpleDateFormat(pattern);
    }

    public final String format(long now) {
        synchronized (this) {
            if (now != lastTimestamp) {
                lastTimestamp = now;
                cachedStr = sdf.format(new Date(now));
            }
            return cachedStr;
        }
    }

    public void setTimeZone(TimeZone tz) {
        sdf.setTimeZone(tz);
    }
}
