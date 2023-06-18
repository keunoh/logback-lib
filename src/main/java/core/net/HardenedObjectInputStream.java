package core.net;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HardenedObjectInputStream extends ObjectInputStream {
    final List<String> whitelistedClassNames;
    final static String[] JAVA_PACKAGES = new String[] { "java.lang", "java.util" };

    public HardenedObjectInputStream(InputStream in, String[] whilelist) throws IOException {
        super(in);

        this.whitelistedClassNames = new ArrayList<String>();
        if (whilelist != null) {
            for (int i = 0; i < whilelist.length; i++) {
                this.whitelistedClassNames.add(whilelist[i]);
            }
        }
    }

    public HardenedObjectInputStream(InputStream in, List<String> whitelist) throws IOException {
        super(in);

        this.whitelistedClassNames = new ArrayList<String>();
        this.whitelistedClassNames.addAll(whitelist);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass anObjectStreamClass) throws IOException, ClassNotFoundException {

        String incomingClassName = anObjectStreamClass.getName();

        if (!isWhitelisted(incomingClassName)) {
            throw new InvalidClassException("Unauthorized deserialization attempt", anObjectStreamClass.getName());
        }

        return super.resolveClass(anObjectStreamClass);
    }

    private boolean isWhitelisted(String incomingClassName) {
        for (int i = 0; i < JAVA_PACKAGES.length; i++) {
            if (incomingClassName.startsWith(JAVA_PACKAGES[i]))
                return true;
        }
        for (String whiteListed : whitelistedClassNames) {
            if (incomingClassName.equals(whiteListed))
                return true;
        }
        return false;
    }

    protected void addToWhitelist(List<String> additionalAuthorizedClasses) {
        whitelistedClassNames.addAll(additionalAuthorizedClasses);
    }
}
