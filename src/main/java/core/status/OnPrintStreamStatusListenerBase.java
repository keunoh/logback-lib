package core.status;

import core.spi.ContextAwareBase;
import core.spi.LifeCycle;
import core.util.StatusPrinter;

import java.io.PrintStream;
import java.util.List;

abstract public class OnPrintStreamStatusListenerBase extends ContextAwareBase implements StatusListener, LifeCycle {

    boolean isStarted = false;

    static final long DEFAULT_RETROSPECTIVE = 300;
    long retrospectiveThreshold = DEFAULT_RETROSPECTIVE;
    String prefix;

    abstract protected PrintStream getPrintStream();

    private void print(Status status) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null)
            sb.append(prefix);

        StatusPrinter.buildStr(sb, "", status);
        getPrintStream().print(sb);
    }

    public void addStatusEvent(Status status) {
        if (!isStarted)
            return;
        print(status);
    }

    private void retrospectivePrint() {
        if (context == null)
            return;
        long now = System.currentTimeMillis();
        StatusManager sm = context.getStatusManager();
        List<Status> statusList = sm.getCopyOfStatusList();
        for (Status status : statusList) {
            long timestampOfStatusMessage = status.getDate();
            if (isElapsedTimeLongerThanThreshold(now, timestampOfStatusMessage)) {
                print(status);
            }
        }
    }

    private boolean isElapsedTimeLongerThanThreshold(long now, long timestamp) {
        long elapsedTime = now - timestamp;
        return elapsedTime < retrospectiveThreshold;
    }

    public void start() {
        isStarted = true;
        if (retrospectiveThreshold > 0)
            retrospectivePrint();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setRetrospective(long retrospective) {
        this.retrospectiveThreshold = retrospective;
    }

    public long getRetrospective() {
        return retrospectiveThreshold;
    }

    public void stop() {
        isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }
}