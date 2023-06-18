package core.net.ssl;

public interface SSL {
    String DEFAULT_PROTOCOL = "SSL";

    /** Default key store type */
    String DEFAULT_KEYSTORE_TYPE = "JKS";

    /** Default key store passphrase */
    String DEFAULT_KEYSTORE_PASSWORD = "changeit";

    /** Default secure random generator algorithm */
    String DEFAULT_SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
}
