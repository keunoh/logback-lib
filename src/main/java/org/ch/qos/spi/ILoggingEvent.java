package org.ch.qos.spi;

import lib.slf4j.Marker;
import org.ch.qos.Level;

import java.util.Map;

public interface ILoggingEvent extends DeferredProcessingAware {

    String getTreadName();

    Level getLevel();

    String getMessage();

    Object[] getArgumentArray();

    String getFormattedMessage();

    String getLoggerName();

    LoggerContextVO getLoggerContextVO();

    IThrowableProxy getThrowableProxy();

    StackTraceElement[] getCallerData();

    boolean hasCallerData();

    Marker getMarker();

    Map<String, String> getMDCPropertyMap();

    Map<String, String> getMdc();

    long getTimeStamp();

    void preparedForDeferredProcessing();
}
