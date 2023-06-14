package core.sift;

import core.CoreConstants;

import java.util.Map;

public abstract class SiftingJoranConfiguratorBase<E> extends GenericConfigurator {
    //TODO: 조란 하고올게염
    protected final String key;
    protected final String value;
    protected final Map<String, String> parentPropertyMap;

    protected SiftingJoranConfiguratorBase(String key, String value, Map<String, String> parentPropertyMap) {
        this.key = key;
        this.value = value;
        this.parentPropertyMap = parentPropertyMap;
    }

    final static String ONE_AND_ONLY_ONE_URL = CoreConstants.CODES_URL + "#1andOnly1";

    @Override
    protected void addImplicitRules(Interpreter interpreter) {
    }
}























