package core.helpers;

import core.AppenderBase;

final public class NOPAppender<E> extends AppenderBase<E> {

    @Override
    protected void append(E eventObject) {}
}
