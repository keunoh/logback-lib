package core.net;

import core.util.DelayStrategy;
import core.util.FixedDelay;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class DefaultSocketConnector implements SocketConnector {
    private final InetAddress address;
    private final int port;
    private final DelayStrategy delayStrategy;

    private ExceptionHandler exceptionHandler;
    private SocketFactory socketFactory;

    /**
     * Constructs a new connector.
     *
     * @param address      address of remote listener
     * @param port         port of remote listener
     * @param initialDelay delay before initial connection attempt
     * @param retryDelay   delay after failed connection attempt
     */
    public DefaultSocketConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
        this(address, port, new FixedDelay(initialDelay, retryDelay));
    }

    /**
     * Constructs a new connector.
     *
     * @param address       address of remote listener
     * @param port          port of remote listener
     * @param delayStrategy strategy for choosing the delay to impose before
     *                      each connection attempt
     */
    public DefaultSocketConnector(InetAddress address, int port, DelayStrategy delayStrategy) {
        this.address = address;
        this.port = port;
        this.delayStrategy = delayStrategy;
    }

    /**
     * Loops until the desired connection is established and returns the resulting connector.
     */
    public Socket call() throws InterruptedException {
        useDefaultsForMissingFields();
        Socket socket = createSocket();
        while (socket == null && !Thread.currentThread().isInterrupted()) {
            Thread.sleep(delayStrategy.nextDelay());
            socket = createSocket();
        }
        return socket;
    }

    private Socket createSocket() {
        Socket newSocket = null;
        try {
            newSocket = socketFactory.createSocket(address, port);
        } catch (IOException ioex) {
            exceptionHandler.connectionFailed(this, ioex);
        }
        return newSocket;
    }

    private void useDefaultsForMissingFields() {
        if (exceptionHandler == null) {
            exceptionHandler = new ConsoleExceptionHandler();
        }
        if (socketFactory == null) {
            socketFactory = SocketFactory.getDefault();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * {@inheritDoc}
     */
    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    /**
     * A default {@link ExceptionHandler} that writes to {@code System.out}
     */
    private static class ConsoleExceptionHandler implements ExceptionHandler {

        public void connectionFailed(SocketConnector connector, Exception ex) {
            System.out.println(ex);
        }

    }
}
