package core.util;

import core.Context;
import core.CoreConstants;
import core.spi.ContextAware;
import core.spi.LifeCycle;
import core.status.OnConsoleStatusListener;
import core.status.StatusListener;

public class StatusListenerConfigHelper {

    public static void installIfAsked(Context context) {
        String slClass = OptionHelper.getSystemProperty(CoreConstants.STATUS_LISTENER_CLASS_KEY);
        if (!OptionHelper.isEmpty(slClass)) {
            addStatusListener(context, slClass);
        }
    }

    private static void addStatusListener(Context context, String listenerClassName) {
        StatusListener listener = null;
        if (CoreConstants.SYSOUT.equalsIgnoreCase(listenerClassName)) {
            listener = new OnConsoleStatusListener();
        } else {
            listener = createListenerPerClassName(context, listenerClassName);
        }
        initAndAddListener(context, listener);
    }

    private static void initAndAddListener(Context context, StatusListener listener) {
        if (listener != null) {
            if (listener instanceof ContextAware) // LOGBACK-767
                ((ContextAware) listener).setContext(context);

            boolean effectivelyAdded = context.getStatusManager().add(listener);
            if (effectivelyAdded && (listener instanceof LifeCycle)) {
                ((LifeCycle) listener).start();
            }
        }
    }

    private static StatusListener createListenerPerClassName(Context context, String listenerClass) {
        try {
            return (StatusListener) OptionHelper.instantiateByClassName(listenerClass, StatusListener.class, context);
        } catch (Exception e) {
            // printing on the console is the best we can do
            e.printStackTrace();
            return null;
        }
    }

    static public void addOnConsoleListenerInstance(Context context, OnConsoleStatusListener onConsoleStatusListener) {
        onConsoleStatusListener.setContext(context);
        boolean effectivelyAdded = context.getStatusManager().add(onConsoleStatusListener);
        if (effectivelyAdded)
            onConsoleStatusListener.start();
    }
}