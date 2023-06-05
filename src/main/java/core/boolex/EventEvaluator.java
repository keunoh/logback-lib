package core.boolex;

import core.spi.ContextAware;
import core.spi.LifeCycle;

public interface EventEvaluator<E> extends ContextAware, LifeCycle {

    boolean evaluate(E event) throws NullPointerException, EvaluationException;

    String getName();

    void setName(String name);
}
