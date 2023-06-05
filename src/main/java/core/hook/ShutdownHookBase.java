package core.hook;

import core.Context;
import core.ContextBase;
import core.spi.ContextAwareBase;

public abstract class ShutdownHookBase extends ContextAwareBase implements ShutdownHook {

    public ShutdownHookBase() {}

    protected void stop() {
        addInfo("Logback context being closed via shutdown hook");

        Context hookContext = getContext();
        if (hookContext instanceof ContextBase) {
            ContextBase context = (ContextBase) hookContext;
            context.stop();
        }
    }
}
