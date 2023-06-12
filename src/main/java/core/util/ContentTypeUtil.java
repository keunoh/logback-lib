package core.util;

public class ContentTypeUtil {

    public static boolean isTextual(String contextType) {
        if (contextType == null)
            return false;
        return contextType.startsWith("text");
    }

    public static String getSubType(String contextType) {
        if (contextType == null)
            return null;
        int index = contextType.indexOf('/');
        if (index == -1) {
            return null;
        } else {
            int subTypeStartIndex = index + 1;
            if (subTypeStartIndex < contextType.length()) {
                return contextType.substring(subTypeStartIndex);
            } else {
                return null;
            }
        }
    }
}
