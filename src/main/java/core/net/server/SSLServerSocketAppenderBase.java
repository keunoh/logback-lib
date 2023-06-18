package core.net.server;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

public abstract class SSLServerSocketAppenderBase<E> extends AbstractServerSocketAppender<E> implements SSLComponent {
    private SSLConfiguration ssl;
    private ServerSocketFactory socketFactory;

    @Override
    protected ServerSocketFactory getServerSocketFactory() {
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
            socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
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
