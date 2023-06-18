package core.property;

import core.PropertyDefinerBase;
import core.util.OptionHelper;

import java.io.File;

public class FileExistsPropertyDefiner extends PropertyDefinerBase {

    String path;

    public String getPath() {
        return path;
    }

    /**
     * The path for the file to search for.
     *
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns "true" if the file specified by {@link #setPath(String) path} property exists.
     * Returns "false" otherwise.
     *
     * @return "true"|"false" depending on the existence of file
     */
    public String getPropertyValue() {
        if (OptionHelper.isEmpty(path)) {
            addError("The \"path\" property must be set.");
            return null;
        }

        File file = new File(path);
        return booleanAsStr(file.exists());
    }
}
