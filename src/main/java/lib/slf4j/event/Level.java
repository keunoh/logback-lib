package lib.slf4j.event;

import static lib.slf4j.event.EventConstants.DEBUG_INT;
import static lib.slf4j.event.EventConstants.ERROR_INT;
import static lib.slf4j.event.EventConstants.INFO_INT;
import static lib.slf4j.event.EventConstants.TRACE_INT;
import static lib.slf4j.event.EventConstants.WARN_INT;

public enum Level {

    ERROR(ERROR_INT, "ERROR"), WARN(WARN_INT, "WARN"), INFO(INFO_INT, "INFO"), DEBUG(DEBUG_INT, "DEBUG"), TRACE(TRACE_INT, "TRACE");

    private int levelInt;
    private String levelStr;

    Level(int i, String s) {
        this.levelInt = i;
        this.levelStr = s;
    }

    public int toInt() {
        return levelInt;
    }

    public String toString() {
        return levelStr;
    }
}
