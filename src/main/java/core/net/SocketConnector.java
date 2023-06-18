package core.net;

import javax.net.SocketFactory;
import java.net.Socket;
import java.util.concurrent.Callable;

public interface SocketConnector extends Callable<Socket> {

    public interface ExceptionHandler {
        void connectionFailed(SocketConnector connector, Exception ex);
    }

    Socket call() throws InterruptedException;

    void setExceptionHandler(ExceptionHandler exceptionHandler);

    void setSocketFactory(SocketFactory socketFactory);
}
