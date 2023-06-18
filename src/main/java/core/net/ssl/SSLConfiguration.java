package core.net.ssl;

public class SSLConfiguration extends SSLContextFactoryBean {
    private SSLParametersConfiguration parameters;

    /**
     * Gets the SSL parameters configuration.
     * @return parameters configuration; if no parameters object was
     *    configured, a default parameters object is returned
     */
    public SSLParametersConfiguration getParameters() {
        if (parameters == null) {
            parameters = new SSLParametersConfiguration();
        }
        return parameters;
    }

    /**
     * Sets the SSL parameters configuration.
     * @param parameters the parameters configuration to set
     */
    public void setParameters(SSLParametersConfiguration parameters) {
        this.parameters = parameters;
    }
}
