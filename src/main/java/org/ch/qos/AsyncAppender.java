package org.ch.qos;

import org.ch.qos.spi.ILoggingEvent;

public class AsyncAppender extends AsyncAppenderBase<ILoggingEvent> {

    boolean includeCallerData = false;

    protected boolean isDiscardable(ILoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() <= level.INFO_INT;
    }

    protected void preprocess(ILoggingEvent eventObject) {
        eventObject.preparedForDeferredProcessing();
        if (includeCallerData)
            eventObject.getCallerData();
    }

    public boolean isIncludeCallerData() {
        return includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
}
