package core.spi;

import core.Context;
import core.status.Status;

public interface ContextAware {
    void setContext(Context context);

    Context getContext();
    void addStatus(Status status);
    void addInfo(String msg);

    void addInfo(String msg, Throwable ex);

    void addWarn(String msg);

    void addWarn(String msg, Throwable ex);

    void addError(String msg);

    void addError(String msg, Throwable ex);
}
