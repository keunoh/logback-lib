package core.filter;

import core.spi.ContextAwareBase;
import core.spi.FilterReply;
import core.spi.LifeCycle;

public abstract class Filter<E> extends ContextAwareBase implements LifeCycle {
    private String name;
    boolean start = false;

    public void start() {
        this.start = true;
    }

    public boolean isStarted() {
        return this.start;
    }

    public void stop() {
        this.start = false;
    }

    public abstract FilterReply decide(E event);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
