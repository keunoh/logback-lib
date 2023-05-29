package core;

import core.spi.ContextAware;
import core.spi.LifeCycle;

public interface Appender<E> extends LifeCycle, ContextAware, FilterAttachable<E> {
    String getName();
    void doAppend(E event) throws LogbackException;

    void setName(String name);
}
