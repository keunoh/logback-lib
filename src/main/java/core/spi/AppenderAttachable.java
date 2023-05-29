package core.spi;

import core.Appender;

import java.util.Iterator;

public interface AppenderAttachable<E> {
    void addAppender(Appender<E> newAppender);

    Iterator<Appender<E>> iteratorForAppenders();

    Appender<E> getAppender(String name);

    boolean isAttached(Appender<E> appender);

    void detachAndStopAllAppenders();

    boolean detachAppender(Appender<E> appender);

    boolean detachAppender(String name);
}
