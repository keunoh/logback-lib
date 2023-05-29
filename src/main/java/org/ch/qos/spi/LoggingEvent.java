package org.ch.qos.spi;

import lib.slf4j.MDC;
import lib.slf4j.Marker;
import lib.slf4j.helpers.MessageFormatter;
import lib.slf4j.spi.MDCAdapter;
import org.ch.qos.Level;
import org.ch.qos.Logger;
import org.ch.qos.LoggerContext;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;

public class LoggingEvent implements ILoggingEvent {

    transient String fqnOfLoggerClass;
    private String threadName;
    private String loggerName;
    private LoggerContext loggerContext;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxy throwableProxy;
    private StackTraceElement[] callerDataArray;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private long timeStamp;

    public LoggingEvent() {}

    public LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray) {
        this.fqnOfLoggerClass = fqcn;
        this.loggerName = logger.getName();
        this.loggerContext = logger.getLoggerContext();
        this.loggerContextVO = loggerContext.getLoggerContextRemoteView();
        this.level = level;

        this.message = message;
        this.argumentArray = argArray;

        if (throwable == null) {
            throwable = extractThrowableAnRearrangeArguments(argArray);
        }

        if (throwable != null) {
            this.throwableProxy = new ThrowableProxy(throwable);
            LoggerContext lc = logger.getLoggerContext();
            if (lc.isPackagingDataEnabled()) {
                this.throwableProxy.calculatePackagingData();
            }
        }

        timeStamp = System.currentTimeMillis();
    }

    private Throwable extractThrowableAnRearrangeArguments(Object[] argArray) {
        Throwable extractedThrowable = EventArgUtil.extractThrowable(argArray);
        if (EventArgUtil.successfulExtraction(extractedThrowable)) {
            this.argumentArray = EventArgUtil.trimmedCopy(argArray);
        }
        return extractedThrowable;
    }

    public void setArgumentArray(Object[] argArray) {
        if (this.argumentArray != null) {
            throw new IllegalStateException("argArray has been already set");
        }
        this.argumentArray = argArray;
    }

    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    public Level getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getThreadName() {
        if (threadName == null) {
            threadName = (Thread.currentThread().getName());
        }
        return threadName;
    }
    public void setThreadName(String threadName) throws IllegalStateException {
        if (this.threadName != null) {
            throw new IllegalStateException("threadName has been already set");
        }
        this.threadName = threadName;
    }

    /**
     * Returns the throwable information contained within this event. May be
     * <code>null</code> if there is no such information.
     */
    public IThrowableProxy getThrowableProxy() {
        return throwableProxy;
    }

    /**
     * Set this event's throwable information.
     */
    public void setThrowableProxy(ThrowableProxy tp) {
        if (throwableProxy != null) {
            throw new IllegalStateException("ThrowableProxy has been already set.");
        } else {
            throwableProxy = tp;
        }
    }

    public void prepareForDeferredProcessing() {
        this.getFormattedMessage();
        this.getThreadName();
        this.getMDCPropertyMap();
    }

    public LoggerContextVO getLoggerContextVO() {
        return loggerContextVO;
    }

    public void setLoggerContextRemoteView(LoggerContextVO loggerContextVO) {
        this.loggerContextVO = loggerContextVO;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (this.message != null) {
            throw new IllegalStateException("The message for this event has been set already.");
        }
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setLevel(Level level) {
        if (this.level != null) {
            throw new IllegalStateException("The level has been already set for this event.");
        }
        this.level = level;
    }

    public StackTraceElement[] getCallerData() {
        if (callerDataArray == null) {
            callerDataArray = CallerData
                    .extract(new Throwable(), fqnOfLoggerClass, loggerContext.getMaxCallerDataDepth(), loggerContext.getFrameworkPackages());
        }
        return callerDataArray;
    }

    public boolean hasCallerData() {
        return (callerDataArray != null);
    }

    public void setCallerData(StackTraceElement[] callerDataArray) {
        this.callerDataArray = callerDataArray;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        if (this.marker != null) {
            throw new IllegalStateException("The marker has been already set for this event.");
        }
        this.marker = marker;
    }

    public long getContextBirthTime() {
        return loggerContextVO.getBirthTime();
    }

    public String getFormattedMessage() {
        if (formattedMessage != null) {
            return formattedMessage;
        }
        if (argumentArray != null) {
            formattedMessage = MessageFormatter.arrayFormat(message, argumentArray).getMessage();
        } else {
            formattedMessage = message;
        }

        return formattedMessage;
    }

    public Map<String, String> getMdcPropertyMap() {
        if (mdcPropertyMap == null) {
            MDCAdapter mdc = MDC.getMdcAdapter();
            if (mdc instanceof LogbackMDCAdapter)
                mdcPropertyMap = ((LogbackMDCAdapter) mdc).getPropertyMap();
            else
                mdcPropertyMap = mdc.getCopyOfContextMap();
        }
        if (mdcPropertyMap == null)
            mdcPropertyMap = Collections.emptyMap();

        return mdcPropertyMap;
    }

    public void setMDCPropertyMap(Map<String, String> map) {
        if (mdcPropertyMap != null) {
            throw new IllegalStateException("The MDCPropertyMap has been already set for this event.");
        }
        this.mdcPropertyMap = map;

    }

    /**
     * Synonym for [@link #getMDCPropertyMap}.
     *
     * @deprecated Replaced by [@link #getMDCPropertyMap}
     */
    public Map<String, String> getMdc() {
        return getMDCPropertyMap();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(level).append("] ");
        sb.append(getFormattedMessage());
        return sb.toString();
    }

    /**
     * LoggerEventVO instances should be used for serialization. Use
     * {@link LoggingEventVO#build(ILoggingEvent) build} method to create the LoggerEventVO instance.
     *
     * @since 1.0.11
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new UnsupportedOperationException(this.getClass() + " does not support serialization. "
                + "Use LoggerEventVO instance instead. See also LoggerEventVO.build method.");
    }
}



























