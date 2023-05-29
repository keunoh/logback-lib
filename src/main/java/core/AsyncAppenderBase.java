package core;

import core.spi.AppenderAttachable;
import core.spi.AppenderAttachableImpl;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AsyncAppenderBase<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

    AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<>();
    BlockingQueue<E> blockingQueue;

    public static final int DEFAULT_QUEUE_SIZE = 256;
    int queueSize = DEFAULT_QUEUE_SIZE;
    int appenderCount = 0;
    static final int UNDEFINED = -1;
    int discardingThreshold = UNDEFINED;
    boolean neverBlock = false;
    Worker worker = new Worker();

    public static final int DEFAULT_MAX_FLUSH_TIME = 1000;
    int maxFlushTime = DEFAULT_MAX_FLUSH_TIME;

    protected boolean isDiscardable(E eventObject) {
        return false;
    }

    protected void preprocess(E eventObject) {
    }

    @Override
    public void start() {
        if (isStarted())
            return;
        if (appenderCount == 0) {
            addError("No attached appenders found.");
            return;
        }
        if (queueSize < 1) {
            addError("Invalid queue size [" + queueSize + "]");
            return;
        }
        blockingQueue = new ArrayBlockingQueue<>(queueSize);

        if (discardingThreshold == UNDEFINED)
            discardingThreshold = queueSize / 5;
        addInfo("Setting discardingThreshold to " + discardingThreshold);
        worker.setDaemon(true);
        worker.setName("AsyncAppender-Worker-" + getName());
        super.start();
        worker.start();
    }

    @Override
    public void stop() {
        if (!isStarted())
            return;

        super.stop();
        worker.interrupt();

        InterruptUtil interruptUtil = new InterruptUtil(context);

        try {
            interruptUtil.maskInterruptFlag();

            worker.join(maxFlushTime);

            if (worker.isAlive()) {
                addWarn("Max queue flush timeout (" + maxFlushTime + " ms) exceeded. Approximately " + blockingQueue.size()
                        + " queued events were possibly discarded.");
            } else {
                addInfo("Queue flush finished successfully within timeout.");
            }
        } catch (InterruptedException e) {
            int remaining = blockingQueue.size();
            addError("Failed to join worker thread. " + remaining + " queued events may be discarded.", e);
        } finally {
            interruptUtil.unmaskInterruptFlag();
        }
    }

    @Override
    protected void append(E eventObject) {
        if (isQueueBelowDiscardingThreshold() && isDiscardable(eventObject)) {
            return;
        }
        preprocess(eventObject);
        put(eventObject);
    }

    private boolean isQueueBelowDiscardingThreshold() {
        return (blockingQueue.remainingCapacity() < discardingThreshold);
    }

    private void put(E eventObject) {
        if (neverBlock) {
            blockingQueue.offer(eventObject);
        } else {
            putUninterruptibly(eventObject);
        }
    }

    private void putUninterruptibly(E eventObject) {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    blockingQueue.put(eventObject);
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public int getMaxFlushTime() {
        return maxFlushTime;
    }

    public void setMaxFlushTime(int maxFlushTime) {
        this.maxFlushTime = maxFlushTime;
    }

    /**
     * Returns the number of elements currently in the blocking queue.
     *
     * @return number of elements currently in the queue.
     */
    public int getNumberOfElementsInQueue() {
        return blockingQueue.size();
    }

    public void setNeverBlock(boolean neverBlock) {
        this.neverBlock = neverBlock;
    }

    public boolean isNeverBlock() {
        return neverBlock;
    }

    /**
     * The remaining capacity available in the blocking queue.
     *
     * @return the remaining capacity
     * @see {@link java.util.concurrent.BlockingQueue#remainingCapacity()}
     */
    public int getRemainingCapacity() {
        return blockingQueue.remainingCapacity();
    }

    public void addAppender(Appender<E> newAppender) {
        if (appenderCount == 0) {
            appenderCount++;
            addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
            aai.addAppender(newAppender);
        } else {
            addWarn("One and only one appender may be attached to AsyncAppender.");
            addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender<E> eAppender) {
        return aai.isAttached(eAppender);
    }

    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<E> eAppender) {
        return aai.detachAppender(eAppender);
    }

    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }
    class Worker extends Thread {

        public void run() {
            AsyncAppenderBase<E> parent = AsyncAppenderBase.this;
            AppenderAttachableImpl<E> aai = parent.aai;

            while (parent.isStarted()) {
                try {
                    E e = parent.blockingQueue.take();
                    aai.appendLoopOnAppenders(e);
                } catch (InterruptedException ie) {
                    break;
                }
            }

            addInfo("Worker thread will flush remaining events before exiting. ");

            for (E e : parent.blockingQueue) {
                aai.appendLoopOnAppenders(e);
                parent.blockingQueue.remove(e);
            }

            aai.detachAndStopAllAppenders();
        }
    }
}
