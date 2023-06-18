package core.property;

import core.PropertyDefinerBase;
import core.util.Loader;
import core.util.OptionHelper;

import java.net.URL;

public class ResourceExistsPropertyDefiner extends PropertyDefinerBase {
    String resourceStr;

    public String getResource() {
        return resourceStr;
    }

    /**
     * The resource to search for on the class path.
     *
     * @param resource
     */
    public void setResource(String resource) {
        this.resourceStr = resource;
    }

    /**
     * Returns the string "true" if the {@link #setResource(String) resource} specified by the
     * user is available on the class path, "false" otherwise.
     *
     * @return "true"|"false" depending on the availability of resource on the classpath
     */
    public String getPropertyValue() {
        if (OptionHelper.isEmpty(resourceStr)) {
            addError("The \"resource\" property must be set.");
            return null;
        }

        URL resourceURL = Loader.getResourceBySelfClassLoader(resourceStr);
        return booleanAsStr(resourceURL != null);
    }
}
