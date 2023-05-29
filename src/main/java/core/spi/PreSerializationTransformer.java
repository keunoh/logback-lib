package core.spi;

import java.io.Serializable;

public interface PreSerializationTransformer<E> {

    Serializable transform(E event);
}
