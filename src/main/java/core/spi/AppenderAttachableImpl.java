package core.spi;

import core.Appender;
import core.util.COWArrayList;

import java.util.Iterator;

public class AppenderAttachableImpl<E> implements AppenderAttachable<E> {

    @SuppressWarnings("unchecked")
    final private COWArrayList<Appender<E>> appenderList = new COWArrayList<>(new Appender[0]);

    public void addAppender(Appender<E> newAppender) {
        if (newAppender == null)
            throw new IllegalArgumentException("Null argument disallowed");
        appenderList.addIfAbsent(newAppender);
    }

    public int appendLoopOnAppenders(E e) {
        int size = 0;
        final Appender<E>[] appenderArray = appenderList.asTypedArray();
        final int len = appenderArray.length;
        for (int i = 0; i < len; i++) {
            appenderArray[i].doAppend(e);
            size++;
        }
        return size;
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return appenderList.iterator();
    }

    public Appender<E> getAppender(String name) {
        if (name == null) {
            return null;
        }
        for (Appender<E> appender : appenderList) {
            if (name.equals(appender.getName())) {
                return appender;
            }
        }
        return null;
    }

    public boolean isAttached(Appender<E> appender) {
        if (appender == null)
            return false;
        for (Appender<E> a : appenderList) {
            if (a == appender)
                return true;
        }
        return false;
    }

    public void detachAndStopAllAppenders() {
        for (Appender<E> a : appenderList) {
            a.stop();
        }
        appenderList.clear();
    }

    static final long START = System.currentTimeMillis();

    public boolean detachAppender(Appender<E> appender) {
        if (appender == null)
            return false;
        boolean result;
        result = appenderList.remove(appender);
        return result;
    }


    public boolean detachAppender(String name) {
        if (name == null)
            return false;

        boolean removed = false;
        for (Appender<E> a : appenderList) {
            if (name.equals((a).getName())) {
                removed = appenderList.remove(a);
                break;
            }
        }
        return removed;
    }
}
