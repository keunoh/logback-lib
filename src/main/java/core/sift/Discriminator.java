package core.sift;

import core.spi.LifeCycle;

public interface Discriminator<E> extends LifeCycle {

    String getDiscriminatingValue(E e);

    String getKey();
}
