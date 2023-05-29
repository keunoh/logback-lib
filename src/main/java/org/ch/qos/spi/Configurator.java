package org.ch.qos.spi;

import org.ch.qos.LoggerContext;

public interface Configurator extends ContextAware {
    public void configure(LoggerContext loggerContext);
}
