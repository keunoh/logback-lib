package core;

import core.helpers.CyclicBuffer;
import core.spi.LogbackLock;
import core.status.Status;
import core.status.StatusListener;
import core.status.StatusManager;

import java.util.ArrayList;
import java.util.List;

public class BasicStatusManager implements StatusManager {

    public static final int MAX_HEADER_COUNT = 150;
    public static final int TAIL_SIZE = 150;
    int count = 0;

    final protected List<Status> statusList = new ArrayList<>();
    final protected CyclicBuffer<Status> tailBuffer = new CyclicBuffer<>(TAIL_SIZE);
    final protected LogbackLock statusListLock = new LogbackLock();
    int level = Status.INFO;
    final protected List<StatusListener> statusListenerList = new ArrayList<>();
    final protected LogbackLock statusListenerListLock = new LogbackLock();

    public void add(Status newStatus) {
        fireStatusAddEvent(newStatus);

        count++;
        if (newStatus.getLevel() > level) {
            level = newStatus.getLevel();
        }

        synchronized (statusListLock) {
            if (statusList.size() < MAX_HEADER_COUNT) {
                statusList.add(newStatus);
            } else {
                tailBuffer.add(newStatus);
            }
        }
    }

    public List<Status> getCopyOfStatusList() {
        synchronized (statusListLock) {
            List<Status> tList = new ArrayList<>(statusList);
            tList.addAll(tailBuffer.asList());
            return tList;
        }
    }

    public void clear() {
        synchronized (statusListLock) {
            count = 0;
            statusList.clear();
            tailBuffer.clear();
        }
    }

    public int getLevel() {
        return level;
    }

    public int getCount() {
        return count;
    }

    public boolean add(StatusListener listener) {
        synchronized (statusListenerListLock) {
            if (listener instanceof OnConsoleStatusListener) {
                boolean alreadyPresent = checkForPresence(statusListenerList, listener.getClass());
                if (alreadyPresent)
                    return false;
            }
            statusListenerList.add(listener);
        }
        return true;
    }

    private boolean checkForPresence(List<StatusListener> statusListenerList, Class<?> aClass) {
        for (StatusListener e : statusListenerList) {
            if (e.getClass() == aClass)
                return true;
        }
        return false;
    }

    public void remove(StatusListener listener) {
        synchronized (statusListenerListLock) {
            statusListenerList.remove(listener);
        }
    }

    public List<StatusListener> getCopyOfStatusListenerList() {
        synchronized (statusListenerListLock) {
            return new ArrayList<>(statusListenerList);
        }
    }

}
