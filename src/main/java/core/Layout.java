package core;

import core.spi.ContextAware;
import core.spi.LifeCycle;

public interface Layout<E> extends ContextAware, LifeCycle {
    String doLayout(E event);

    String getFileHeader();

    String getPresentationHeader();

    String getPresentationFooter();

    String getFileFooter();

    String getContentType();
}
