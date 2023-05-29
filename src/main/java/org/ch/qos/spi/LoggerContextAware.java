package org.ch.qos.spi;

import org.ch.qos.LoggerContext;

public interface LoggerContextAware extends ContextAware {

    void setLoggerContext(LoggerContext context);
}
