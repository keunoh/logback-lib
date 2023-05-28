package org.ch.qos;

import lib.slf4j.Marker;
import lib.slf4j.MarkerFactory;

public class ClassicConstants {
    public static final String USER_MDC_KEY = "user";

    public static final String LOGBACK_CONTEXT_SELECTOR = "logback.ContextSelector";

    public static final String JNDI_CONFIGURATION_RESOURCE = JNDI_JAVA_NAMESPACE + "comp/env/logback/configuration-resource";
    public static final String JNDI_CONTEXT_NAME = JNDI_JAVA_NAMESPACE + "comp/env/logback/context-name";

    /**
     * The maximum number of package separators (dots) that abbreviation
     * algorithms can handle. Class or logger names with more separators will have
     * their first MAX_DOTS parts shortened.
     *
     */
    public static final int MAX_DOTS = 16;

    /**
     * The default stack data depth computed during caller data extraction.
     */
    public static final int DEFAULT_MAX_CALLEDER_DATA_DEPTH = 8;

    public static final String REQUEST_REMOTE_HOST_MDC_KEY = "req.remoteHost";
    public static final String REQUEST_USER_AGENT_MDC_KEY = "req.userAgent";
    public static final String REQUEST_REQUEST_URI = "req.requestURI";
    public static final String REQUEST_QUERY_STRING = "req.queryString";
    public static final String REQUEST_REQUEST_URL = "req.requestURL";
    public static final String REQUEST_METHOD = "req.method";
    public static final String REQUEST_X_FORWARDED_FOR = "req.xForwardedFor";

    public static final String GAFFER_CONFIGURATOR_FQCN = "ch.qos.logback.classic.gaffer.GafferConfigurator";

    public static final String FINALIZE_SESSION = "FINALIZE_SESSION";
    public static final Marker FINALIZE_SESSION_MARKER = MarkerFactory.getMarker(FINALIZE_SESSION);
}