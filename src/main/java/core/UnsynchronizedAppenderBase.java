package core;

import core.filter.Filter;
import core.spi.ContextAwareBase;
import core.spi.FilterAttachableImpl;
import core.spi.FilterReply;
import core.status.WarnStatus;

import java.util.List;

abstract public class UnsynchronizedAppenderBase<E> extends ContextAwareBase implements Appender<E> {

    protected boolean started = false;
    private ThreadLocal<Boolean> guard = new ThreadLocal<>();
    protected String name;
    private FilterAttachableImpl<E> fai = new FilterAttachableImpl<>();

    public String getName() {
        return name;
    }

    private int statusRepeatCount = 0;
    private int exceptionCount = 0;

    static final int ALLOWED_REPEATS = 3;

    public void doAppend(E eventObject) {
        if (Boolean.TRUE.equals(guard.get())) {
            return;
        }

        try {
            guard.set(Boolean.TRUE);

            if (!this.started) {
                if (statusRepeatCount++ < ALLOWED_REPEATS) {
                    addStatus(new WarnStatus("Attempted to append to non started appender [" + name + "].", this));
                }
                return;
            }

            if (getFilterChainDecision(eventObject) == FilterReply.DENY) {
                return;
            }

            this.append(eventObject);
        } catch (Exception e) {
            if (exceptionCount++ < ALLOWED_REPEATS) {
                addError("Appender [" + name + "] failed to append.", e);
            }
        } finally {
            guard.set(Boolean.FALSE);
        }
    }

    abstract protected void append(E eventObject);

    /**
     * Set the name of this appender.
     */
    public void setName(String name) {
        this.name = name;
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public String toString() {
        return this.getClass().getName() + "[" + name + "]";
    }

    public void addFilter(Filter<E> newFilter) {
        fai.addFilter(newFilter);
    }

    public void clearAllFilters() {
        fai.clearAllFilters();
    }

    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return fai.getCopyOfAttachedFiltersList();
    }

    public FilterReply getFilterChainDecision(E event) {
        return fai.getFilterChainDecision(event);
    }
}
