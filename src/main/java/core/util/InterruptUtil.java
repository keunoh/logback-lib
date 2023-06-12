package core.util;

import core.Context;
import core.spi.ContextAwareBase;

public class InterruptUtil extends ContextAwareBase {
    final boolean previouslyInterrupted;

    public InterruptUtil(Context context) {
        super();
        setContext(context);
        previouslyInterrupted = Thread.currentThread().isInterrupted();
    }

    public void maskInterruptFlag() {
        if (previouslyInterrupted)
            Thread.interrupted();
    }

    public void unmaskInterruptFlag() {
        if (previouslyInterrupted) {
            try {
                Thread.currentThread().interrupt();
            } catch (SecurityException se) {
                addError("Failed to interrupt current thread", se);
            }
        }
    }
}
