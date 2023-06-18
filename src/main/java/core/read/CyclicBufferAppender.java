package core.read;

import core.AppenderBase;
import core.helpers.CyclicBuffer;

public class CyclicBufferAppender<E> extends AppenderBase<E> {
    CyclicBuffer<E> cb;
    int maxSize = 512;

    public void start() {
        cb = new CyclicBuffer<E>(maxSize);
        super.start();
    }

    public void stop() {
        cb = null;
        super.stop();
    }

    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }
        cb.add(eventObject);
    }

    public int getLength() {
        if (isStarted()) {
            return cb.length();
        } else {
            return 0;
        }
    }

    public E get(int i) {
        if (isStarted()) {
            return cb.get(i);
        } else {
            return null;
        }
    }

    public void reset() {
        cb.clear();
    }

    /**
     * Set the size of the cyclic buffer.
     */
    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
