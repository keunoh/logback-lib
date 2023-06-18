package core.net.ssl;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConfigurableSSLSocketFactory extends SocketFactory {
    private final SSLParametersConfiguration parameters;
    private final SSLSocketFactory delegate;

    /**
     * Creates a new factory.
     * @param parameters parameters that will be configured on each
     *    socket created by the factory
     * @param delegate socket factory that will be called upon to create
     *    sockets before configuration
     */
    public ConfigurableSSLSocketFactory(SSLParametersConfiguration parameters, SSLSocketFactory delegate) {
        this.parameters = parameters;
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        SSLSocket socket = (SSLSocket) delegate.createSocket(address, port, localAddress, localPort);
        parameters.configure(new SSLConfigurableSocket(socket));
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) delegate.createSocket(host, port);
        parameters.configure(new SSLConfigurableSocket(socket));
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        SSLSocket socket = (SSLSocket) delegate.createSocket(host, port, localHost, localPort);
        parameters.configure(new SSLConfigurableSocket(socket));
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        SSLSocket socket = (SSLSocket) delegate.createSocket(host, port);
        parameters.configure(new SSLConfigurableSocket(socket));
        return socket;
    }

}
