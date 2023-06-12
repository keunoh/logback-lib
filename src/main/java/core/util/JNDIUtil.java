package core.util;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Hashtable;

import static core.CoreConstants.JNDI_JAVA_NAMESPACE;

public class JNDIUtil {

    static final String RESTRICTION_MSG = "JNDI name must start with " + JNDI_JAVA_NAMESPACE + " but was ";

    public static Context getInitialContext() throws NamingException {
        return new InitialContext();
    }

    public static Context getInitialContext(Hashtable<?,?> props) throws NamingException {
        return new InitialContext(props);
    }

    public static Object lookupObject(Context ctx, String name) throws NamingException {
        if (ctx == null)
            return null;

        if (OptionHelper.isEmpty(name))
            return null;

        jndiNameSecurityCheck(name);

        Object lookup = ctx.lookup(name);
        return lookup;
    }

    private static void jndiNameSecurityCheck(String name) throws NamingException {
        if (!name.startsWith(JNDI_JAVA_NAMESPACE)) {
            throw new NamingException(RESTRICTION_MSG + name);
        }
    }

    public static String lookupString(Context ctx, String name) throws NamingException {
        Object lookup = lookupObject(ctx, name);
        return (String) lookup;
    }
}




















