package core.net.server;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

public class RemoteReceiverServerRunner extends ConcurrentServerRunner<RemoteReceiverClient> {
    private final int clientQueueSize;

    /**
     * Constructs a new server runner.
     * @param listener the listener from which the server will accept new
     *    clients
     * @param executor that will be used to execute asynchronous tasks
     *    on behalf of the runner.
     * @param queueSize size of the event queue that will be maintained for
     *    each client
     */
    public RemoteReceiverServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor, int clientQueueSize) {
        super(listener, executor);
        this.clientQueueSize = clientQueueSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean configureClient(RemoteReceiverClient client) {
        client.setContext(getContext());
        client.setQueue(new ArrayBlockingQueue<Serializable>(clientQueueSize));
        return true;
    }
}
