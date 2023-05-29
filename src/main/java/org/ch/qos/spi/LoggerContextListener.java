package org.ch.qos.spi;

import org.ch.qos.Level;
import org.ch.qos.Logger;
import org.ch.qos.LoggerContext;

public interface LoggerContextListener {

    boolean isResetResistant();

    void onStart(LoggerContext context);

    void onReset(LoggerContext context);

    void onStop(LoggerContext context);

    void onLevelChange(Logger logger, Level level);
}
