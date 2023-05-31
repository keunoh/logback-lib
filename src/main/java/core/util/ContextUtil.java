package core.util;

import core.Context;
import core.rolling.helper.FileNamePattern;
import core.spi.ContextAwareBase;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import static core.CoreConstants.*;

public class ContextUtil extends ContextAwareBase {

    public ContextUtil(Context context) {
        setContext(context);
    }

    public static String getLocalHostName() throws UnknownHostException, SocketException {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostName();
        } catch (UnknownHostException e) {
            return getLocalAddressAsString();
        }
    }

    private static String getLocalAddressAsString() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces != null && interfaces.hasMoreElements()) {
            Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
            while (addresses != null && addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (acceptableAddress(address)) {
                    return address.getHostAddress();
                }
            }
        }
        throw new UnknownHostException();
    }

    private static boolean acceptableAddress(InetAddress address) {
        return address != null && !address.isLoopbackAddress() && !address.isAnyLocalAddress() && !address.isLinkLocalAddress();
    }

    public String safelyGetLocalHostName() {
        try {
            String localhostName = getLocalHostName();
            return localhostName;
        } catch (UnknownHostException e) {
            addError("Failed to get local hostname", e);
        } catch (SocketException e) {
            addError("Failed to get local hostname", e);
        } catch (SecurityException e) {
            addError("Failed to get local hostname", e);
        }
        return UNKNOWN_LOCALHOST;
    }

    public void addProperties(Properties props) {
        if (props == null) {
            return;
        }
        @SuppressWarnings("rawtypes")
        Iterator i = props.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            context.putProperty(key, props.getProperty(key));
        }
    }

    public static Map<String, String> getFilenameCollisionMap(Context context) {
        if (context == null)
            return null;
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) context.getObject(FA_FILENAME_COLLISION_MAP);
        return map;
    }

    public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
        if (context == null)
            return null;
        @SuppressWarnings("unchecked")
        Map<String, FileNamePattern> map = (Map<String, FileNamePattern>) context.getObject(RFA_FILENAME_PATTERN_COLLISION_MAP);
        return map;
    }

    public void addGroovyPackages(List<String> frameworkPackages) {
        addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
    }

    public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
        if (!frameworkPackages.contains(packageName)) {
            frameworkPackages.add(packageName);
        }
    }
}
