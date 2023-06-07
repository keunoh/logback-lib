package core.rolling;

import core.spi.LifeCycle;

import java.io.File;

public interface TriggeringPolicy<E> extends LifeCycle {

    boolean isTriggeringEvent(final File activeFile, final E event);
}
