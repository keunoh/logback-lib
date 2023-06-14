package core.sift;

import core.spi.ContextAwareBase;

public abstract class AbstractDiscriminator<E> extends ContextAwareBase implements Discriminator<E> {

    protected boolean started;

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
