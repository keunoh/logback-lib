package core.net.ssl;

import core.spi.ContextAware;

import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLContextFactoryBean {
    private static final String JSSE_KEY_STORE_PROPERTY = "javax.net.ssl.keyStore";
    private static final String JSSE_TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

    private KeyStoreFactoryBean keyStore;
    private KeyStoreFactoryBean trustStore;
    private SecureRandomFactoryBean secureRandom;
    private KeyManagerFactoryFactoryBean keyManagerFactory;
    private TrustManagerFactoryFactoryBean trustManagerFactory;
    private String protocol;
    private String provider;

    public SSLContext createContext(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException,
            UnrecoverableKeyException, KeyStoreException, CertificateException {

        SSLContext sslContext = getProvider() != null ? SSLContext.getInstance(getProtocol(), getProvider()) : SSLContext.getInstance(getProtocol());

        context.addInfo("SSL protocol '" + sslContext.getProtocol() + "' provider '" + sslContext.getProvider() + "'");

        KeyManager[] keyManagers = createKeyManagers(context);
        TrustManager[] trustManagers = createTrustManagers(context);
        SecureRandom secureRandom = createSecureRandom(context);
        sslContext.init(keyManagers, trustManagers, secureRandom);
        return sslContext;
    }

    private KeyManager[] createKeyManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException,
            KeyStoreException {

        if (getKeyStore() == null)
            return null;

        KeyStore keyStore = getKeyStore().createKeyStore();
        context.addInfo("key store of type '" + keyStore.getType() + "' provider '" + keyStore.getProvider() + "': " + getKeyStore().getLocation());

        KeyManagerFactory kmf = getKeyManagerFactory().createKeyManagerFactory();
        context.addInfo("key manager algorithm '" + kmf.getAlgorithm() + "' provider '" + kmf.getProvider() + "'");

        char[] passphrase = getKeyStore().getPassword().toCharArray();
        kmf.init(keyStore, passphrase);
        return kmf.getKeyManagers();
    }

    /**
     * Creates trust managers using the receiver's trust store configuration.
     * @param context context for status messages
     * @return an array of trust managers or {@code null} if no trust store
     *    configuration was provided
     * @throws NoSuchProviderException if a provider specified for one
     *    of the trust manager components is not known to the platform
     * @throws NoSuchAlgorithmException if an algorithm specified for
     *    one of the trust manager components is not known to the relevant
     *    provider
     * @throws KeyStoreException if an error occurs in reading a key
     *    store containing trust anchors
     */
    private TrustManager[] createTrustManagers(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException {

        if (getTrustStore() == null)
            return null;

        KeyStore trustStore = getTrustStore().createKeyStore();
        context.addInfo("trust store of type '" + trustStore.getType() + "' provider '" + trustStore.getProvider() + "': " + getTrustStore().getLocation());

        TrustManagerFactory tmf = getTrustManagerFactory().createTrustManagerFactory();
        context.addInfo("trust manager algorithm '" + tmf.getAlgorithm() + "' provider '" + tmf.getProvider() + "'");

        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }

    private SecureRandom createSecureRandom(ContextAware context) throws NoSuchProviderException, NoSuchAlgorithmException {

        SecureRandom secureRandom = getSecureRandom().createSecureRandom();
        context.addInfo("secure random algorithm '" + secureRandom.getAlgorithm() + "' provider '" + secureRandom.getProvider() + "'");

        return secureRandom;
    }

    /**
     * Gets the key store configuration.
     * @return key store factory bean or {@code null} if no key store
     *    configuration was provided
     */
    public KeyStoreFactoryBean getKeyStore() {
        if (keyStore == null) {
            keyStore = keyStoreFromSystemProperties(JSSE_KEY_STORE_PROPERTY);
        }
        return keyStore;
    }

    /**
     * Sets the key store configuration.
     * @param keyStore the key store factory bean to set
     */
    public void setKeyStore(KeyStoreFactoryBean keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Gets the trust store configuration.
     * @return trust store factory bean or {@code null} if no trust store
     *    configuration was provided
     */
    public KeyStoreFactoryBean getTrustStore() {
        if (trustStore == null) {
            trustStore = keyStoreFromSystemProperties(JSSE_TRUST_STORE_PROPERTY);
        }
        return trustStore;
    }

    /**
     * Sets the trust store configuration.
     * @param trustStore the trust store factory bean to set
     */
    public void setTrustStore(KeyStoreFactoryBean trustStore) {
        this.trustStore = trustStore;
    }



    private String locationFromSystemProperty(String name) {
        String location = System.getProperty(name);
        if (location != null && !location.startsWith("file:")) {
            location = "file:" + location;
        }
        return location;
    }

    /**
     * Gets the secure random generator configuration.
     * @return secure random factory bean; if no secure random generator
     *    configuration has been set, a default factory bean is returned
     */
    public SecureRandomFactoryBean getSecureRandom() {
        if (secureRandom == null) {
            return new SecureRandomFactoryBean();
        }
        return secureRandom;
    }

    /**
     * Sets the secure random generator configuration.
     * @param secureRandom the secure random factory bean to set
     */
    public void setSecureRandom(SecureRandomFactoryBean secureRandom) {
        this.secureRandom = secureRandom;
    }

    /**
     * Gets the key manager factory configuration.
     * @return factory bean; if no key manager factory
     *    configuration has been set, a default factory bean is returned
     */
    public KeyManagerFactoryFactoryBean getKeyManagerFactory() {
        if (keyManagerFactory == null) {
            return new KeyManagerFactoryFactoryBean();
        }
        return keyManagerFactory;
    }

    /**
     * Sets the key manager factory configuration.
     * @param keyManagerFactory the key manager factory factory bean to set
     */
    public void setKeyManagerFactory(KeyManagerFactoryFactoryBean keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
    }

    /**
     * Gets the trust manager factory configuration.
     * @return factory bean; if no trust manager factory
     *    configuration has been set, a default factory bean is returned
     */
    public TrustManagerFactoryFactoryBean getTrustManagerFactory() {
        if (trustManagerFactory == null) {
            return new TrustManagerFactoryFactoryBean();
        }
        return trustManagerFactory;
    }

    /**
     * Sets the trust manager factory configuration.
     * @param trustManagerFactory the factory bean to set
     */
    public void setTrustManagerFactory(TrustManagerFactoryFactoryBean trustManagerFactory) {
        this.trustManagerFactory = trustManagerFactory;
    }

    /**
     * Gets the secure transport protocol name.
     * @return protocol name (e.g. {@code SSL}, {@code TLS}); the
     *    {@link SSL#DEFAULT_PROTOCOL} is returned if no protocol has been
     *    configured
     */
    public String getProtocol() {
        if (protocol == null) {
            return SSL.DEFAULT_PROTOCOL;
        }
        return protocol;
    }

    /**
     * Sets the secure transport protocol name.
     * @param protocol a protocol name, which must be recognized by the provider
     *    specified by {@link #setProvider(String)} or by the platform's
     *    default provider if no platform was specified.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the JSSE provider name for the SSL context.
     * @return JSSE provider name
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the JSSE provider name for the SSL context.
     * @param provider name of the JSSE provider to use in creating the
     *    SSL context
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }
}
