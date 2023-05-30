package core.encoder;

import core.spi.ContextAwareBase;

abstract public class EncoderBase<E> extends ContextAwareBase implements Encoder<E> {
    protected boolean started;

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
