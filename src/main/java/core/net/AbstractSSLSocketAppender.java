package core.net;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

public abstract class AbstractSSLSocketAppender<E> extends AbstractSocketAppender<E> implements SSLComponent {
    private SSLConfiguration ssl;
    private SocketFactory socketFactory;

    /**
     * Constructs a new appender.
     */
    protected AbstractSSLSocketAppender() {
    }

    /**
     * Gets an {@link SocketFactory} that produces SSL sockets using an
     * {@link SSLContext} that is derived from the appender's configuration.
     * @return socket factory
     */
    @Override
    protected SocketFactory getSocketFactory() {
        return socketFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        try {
            SSLContext sslContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
            super.start();
        } catch (Exception ex) {
            addError(ex.getMessage(), ex);
        }
    }

    /**
     * Gets the SSL configuration.
     * @return SSL configuration; if no configuration has been set, a
     *    default configuration is returned
     */
    public SSLConfiguration getSsl() {
        if (ssl == null) {
            ssl = new SSLConfiguration();
        }
        return ssl;
    }

    /**
     * Sets the SSL configuration.
     * @param ssl the SSL configuration to set
     */
    public void setSsl(SSLConfiguration ssl) {
        this.ssl = ssl;
    }
}
