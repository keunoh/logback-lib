package core.sift;

import core.Appender;
import core.Context;

public interface AppenderFactory<E> {

    Appender<E> buildAppender(Context context, String discriminatingValue) throws JoranException;
}
