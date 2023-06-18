package core.net.ssl;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ConfigurableSSLServerSocketFactory extends ServerSocketFactory {
    private final SSLParametersConfiguration parameters;
    private final SSLServerSocketFactory delegate;

    /**
     * Creates a new factory.
     * @param parameters parameters that will be configured on each
     *    socket created by the factory
     * @param delegate socket factory that will be called upon to create
     *    server sockets before configuration
     */
    public ConfigurableSSLServerSocketFactory(SSLParametersConfiguration parameters, SSLServerSocketFactory delegate) {
        this.parameters = parameters;
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) delegate.createServerSocket(port, backlog, ifAddress);
        parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) delegate.createServerSocket(port, backlog);
        parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) delegate.createServerSocket(port);
        parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }
}
