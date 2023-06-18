package core.net.ssl;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;

public class SSLConfigurableSocket implements SSLConfigurable  {
    private final SSLSocket delegate;

    public SSLConfigurableSocket(SSLSocket delegate) {
        this.delegate = delegate;
    }

    public String[] getDefaultProtocols() {
        return delegate.getEnabledProtocols();
    }

    public String[] getSupportedProtocols() {
        return delegate.getSupportedProtocols();
    }

    public void setEnabledProtocols(String[] protocols) {
        delegate.setEnabledProtocols(protocols);
    }

    public String[] getDefaultCipherSuites() {
        return delegate.getEnabledCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    public void setEnabledCipherSuites(String[] suites) {
        delegate.setEnabledCipherSuites(suites);
    }

    public void setNeedClientAuth(boolean state) {
        delegate.setNeedClientAuth(state);
    }

    public void setWantClientAuth(boolean state) {
        delegate.setWantClientAuth(state);
    }

    @Override
    public void setHostnameVerification(boolean hostnameVerification) {
        if (!hostnameVerification) {
            return;
        }
        SSLParameters sslParameters = delegate.getSSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        delegate.setSSLParameters(sslParameters);
    }
}
