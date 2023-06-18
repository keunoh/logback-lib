package core.joran.spi;

import core.status.StatusManager;

import java.net.URL;

public class XMLUtil {
    static public final int ILL_FORMED = 1;
    static public final int UNRECOVERABLE_ERROR = 2;

    static public int checkIfWellFormed(URL url, StatusManager sm) { return 0; }
}
