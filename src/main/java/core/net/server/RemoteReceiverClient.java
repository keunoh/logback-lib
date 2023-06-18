package core.net.server;

import core.spi.ContextAware;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public interface RemoteReceiverClient extends Client, ContextAware {
    void setQueue(BlockingQueue<Serializable> queue);

    boolean offer(Serializable event);
}
