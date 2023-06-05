package core.boolex;

import core.spi.ContextAwareBase;

abstract public class EventEvaluatorBase<E> extends ContextAwareBase implements EventEvaluator<E> {

    String name;

    boolean started;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != null) {
            throw new IllegalStateException("name has been already set");
        }
        this.name = name;
    }

    public boolean isStarted() {
        return started;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }
}
