package core.joran.spi;

public class NoAutoStartUtil {
    static public boolean notMarkedWithNoAutoStart(Object o) {
        if (o == null)
            return false;

        Class<?> clazz = o.getClass();
        NoAutoStart a = clazz.getAnnotation(NoAutoStart.class);
        return a == null;
    }
}
