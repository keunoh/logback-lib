package core.spi;

import core.helpers.CyclicBuffer;

import java.util.ArrayList;
import java.util.List;

public class CyclicBufferTracker<E> extends AbstractComponentTracker<CyclicBuffer<E>> {

    static final int DEFAULT_NUMBER_OF_BUFFERS = 64;
    static final int DEFAULT_BUFFER_SIZE = 256;
    int bufferSize = DEFAULT_BUFFER_SIZE;

    public CyclicBufferTracker() {
        super();
        setMaxComponents(DEFAULT_NUMBER_OF_BUFFERS);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    protected void processPriorToRemoval(CyclicBuffer<E> component) {
        component.clear();
    }

    @Override
    protected CyclicBuffer<E> buildComponent(String key) {
        return new CyclicBuffer<E>(bufferSize);
    }

    @Override
    protected boolean isComponentStale(CyclicBuffer<E> eCyclicBuffer) {
        return false;
    }

    // for testing purposes
    List<String> liveKeysAsOrderedList() {
        return new ArrayList<String>(liveMap.keySet());
    }

    List<String> lingererKeysAsOrderedList() {
        return new ArrayList<String>(lingerersMap.keySet());

    }
}
