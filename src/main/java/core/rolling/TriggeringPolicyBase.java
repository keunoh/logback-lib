package core.rolling;

import core.spi.ContextAwareBase;

abstract public class TriggeringPolicyBase<E> extends ContextAwareBase implements TriggeringPolicy<E> {
    private boolean start;

    @Override
    public void start() {
        start = true;
    }

    @Override
    public void stop() {
        start = false;
    }

    @Override
    public boolean isStarted() {
        return start;
    }
}
