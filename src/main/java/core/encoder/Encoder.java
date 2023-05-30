package core.encoder;

import core.spi.ContextAware;
import core.spi.LifeCycle;

public interface Encoder<E> extends ContextAware, LifeCycle {

    byte[] headerBytes();

    byte[] encode(E event);

    byte[] footerBytes();
}
