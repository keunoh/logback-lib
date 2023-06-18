package core.layout;

import core.CoreConstants;
import core.LayoutBase;

public class EchoLayout<E> extends LayoutBase<E> {
    public String doLayout(E event) {
        return event + CoreConstants.LINE_SEPARATOR;
    }
}
