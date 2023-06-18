package core.net;

import java.util.concurrent.LinkedBlockingDeque;

public class QueueFactory {
    public <E>LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
        final int actualCapacity = capacity < 1 ? 1 : capacity;
        return new LinkedBlockingDeque<>(actualCapacity);
    }
}
