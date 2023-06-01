package core;

import core.spi.ContextAwareBase;
import core.spi.PropertyDefiner;

public abstract class PropertyDefinerBase extends ContextAwareBase implements PropertyDefiner {

    static protected String booleanAsStr(boolean bool) {
        return bool ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
    }
}
