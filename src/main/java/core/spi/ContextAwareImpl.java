package core.spi;

import core.Context;
import core.status.*;

public class ContextAwareImpl implements ContextAware {
    private int noContextWarning = 0;
    protected Context context;
    final Object origin;

    public ContextAwareImpl(Context context, Object origin) {
        this.context = context;
        this.origin = origin;
    }

    protected Object getOrigin() {
        return origin;
    }

    public void setContext(Context context) {
        if (this.context == null) {
            this.context = context;
        } else if (this.context != context) {
            throw new IllegalStateException("Context has been already set");
        }
    }

    public Context getContext() {
        return this.context;
    }

    public StatusManager getStatusManager() {
        if (context == null)
            return null;
        return context.getStatusManager();
    }

    public void addStatus(Status status) {
        if (context == null) {
            if (noContextWarning++ == 0) {
                System.out.println("LOGBACK: No context given for " + this);
            }
            return;
        }
        StatusManager sm = context.getStatusManager();
        if (sm != null) {
            sm.add(status);
        }
    }

    public void addInfo(String msg) {
        addStatus(new InfoStatus(msg, getOrigin()));
    }

    public void addInfo(String msg, Throwable ex) {
        addStatus(new InfoStatus(msg, getOrigin(), ex));
    }

    public void addWarn(String msg) {
        addStatus(new WarnStatus(msg, getOrigin()));
    }

    public void addWarn(String msg, Throwable ex) {
        addStatus(new WarnStatus(msg, getOrigin(), ex));
    }

    public void addError(String msg) {
        addStatus(new ErrorStatus(msg, getOrigin()));
    }

    public void addError(String msg, Throwable ex) {
        addStatus(new ErrorStatus(msg, getOrigin(), ex));
    }
}
