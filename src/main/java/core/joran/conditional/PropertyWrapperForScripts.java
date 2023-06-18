package core.joran.conditional;

import core.spi.PropertyContainer;
import core.util.OptionHelper;

public class PropertyWrapperForScripts {
    PropertyContainer local;
    PropertyContainer context;

    // this method is invoked by reflection in PropertyEvalScriptBuilder
    public void setPropertyContainers(PropertyContainer local, PropertyContainer context) {
        this.local = local;
        this.context = context;
    }

    public boolean isNull(String k) {
        String val = OptionHelper.propertyLookup(k, local, context);
        return (val == null);
    }

    public boolean isDefined(String k) {
        String val = OptionHelper.propertyLookup(k, local, context);
        return (val != null);
    }

    public String p(String k) {
        return property(k);
    }

    public String property(String k) {
        String val = OptionHelper.propertyLookup(k, local, context);
        if (val != null)
            return val;
        else
            return "";
    }
}
