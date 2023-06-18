package core.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RemoteReceiverServerListener extends ServerSocketListener<RemoteReceiverClient> {
    public RemoteReceiverServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RemoteReceiverClient createClient(String id, Socket socket) throws IOException {
        return new RemoteReceiverStreamClient(id, socket);
    }
}
