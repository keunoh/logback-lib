package core.status;

import java.util.Iterator;

public interface Status {

    int INFO = 0;
    int WARN = 1;
    int ERROR = 2;

    int getLevel();

    int getEffectiveLevel();

    Object getOrigin();

    String getMessage();

    Throwable getThrowable();

    Long getDate();

    boolean hasChildren();

    void add(Status child);

    boolean remove(Status child);

    Iterator<Status> iterator();
}
