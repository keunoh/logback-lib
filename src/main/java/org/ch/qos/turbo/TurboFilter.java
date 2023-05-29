package org.ch.qos.turbo;

import lib.slf4j.Marker;

public abstract class TurboFilter extends ContextAwareBase implements LifeCycle {

    private String name;
    boolean start = false;

    /**
     * Make a decision based on the multiple parameters passed as arguments.
     * The returned value should be one of <code>{@link FilterReply#DENY}</code>,
     * <code>{@link FilterReply#NEUTRAL}</code>, or <code>{@link FilterReply#ACCEPT}</code>.

     * @param marker
     * @param logger
     * @param level
     * @param format
     * @param params
     * @param t
     * @return
     */
    public abstract FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t);

    public void start() {
        this.start = true;
    }

    public boolean isStarted() {
        return this.start;
    }

    public void stop() {
        this.start = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
