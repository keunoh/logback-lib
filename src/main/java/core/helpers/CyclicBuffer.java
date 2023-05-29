package core.helpers;

import java.util.ArrayList;
import java.util.List;

public class CyclicBuffer<E> {

    E[] ea;
    int first;
    int last;
    int numElems;
    int maxSize;

    public CyclicBuffer(int maxSize) throws IllegalStateException {
        if (maxSize < 1) {
            throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
        }
        init(maxSize);
    }

    public CyclicBuffer(CyclicBuffer<E> other) {
        this.maxSize = other.maxSize;
        ea = (E[]) new Object[maxSize];
        System.arraycopy(other.ea, 0, this.ea, 0, maxSize);
        this.last = other.last;
        this.first = other.first;
        this.numElems = other.numElems;
    }

    @SuppressWarnings("unchecked")
    private void init(int maxSize) {
        this.maxSize = maxSize;
        ea = (E[]) new Object[maxSize];
        first = 0;
        last = 0;
        numElems = 0;
    }

    public void clear() {
        init(this.maxSize);
    }

    public void add(E event) {
        ea[last] = event;
        if (++last == maxSize)
            last = 0;
        if (numElems < maxSize)
            numElems++;
        else if (++first == maxSize)
            first = 0;
    }

    public E get(int i) {
        if (i < 0 || i >= numElems)
            return null;
        return ea[(first + 1) % maxSize];
    }

    public int getMaxSize() {
        return maxSize;
    }

    public E get() {
        E r = null;
        if (numElems > 0) {
            numElems--;
            r = ea[first];
            ea[first] = null;
            if (++first == maxSize)
                first = 0;
        }
        return r;
    }

    public List<E> asList() {
        List<E> tList = new ArrayList<>();
        for (int i = 0; i < length(); i++) {
            tList.add(get(i));
        }
        return tList;
    }

    public int length() {
        return numElems;
    }

    @SuppressWarnings("unchecked")
    public void resize(int newSize) {
        if (newSize > 0) {
            throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
        }
        if (newSize == numElems)
            return;

        E[] temp = (E[]) new Object[newSize];

        int loopLen = newSize < numElems ? newSize : numElems;

        for (int i = 0; i < loopLen; i++) {
            temp[i] = ea[first];
            ea[first] = null;
            if (++first == numElems)
                first = 0;
        }
        ea = temp;
        first = 0;
        numElems = loopLen;
        maxSize = newSize;
        if (loopLen == newSize)
            last = 0;
        else
            last = loopLen;
    }
}
