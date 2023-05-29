package org.ch.qos.spi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ThrowableProxy implements IThrowableProxy {

    static final StackTraceElementProxy[] EMPTY_STEP = new StackTraceElementProxy[0];
    private Throwable throwable;
    private String className;
    private String message;
    // package-private because of ThrowableProxyUtil
    StackTraceElementProxy[] stackTraceElementProxyArray;
    // package-private because of ThrowableProxyUtil
    int commonFrames;
    private ThrowableProxy cause;
    private static final ThrowableProxy[] NO_SUPPRESSED = new ThrowableProxy[0];
    private ThrowableProxy[] suppressed = NO_SUPPRESSED;

    // private final Set<Throwable> alreadyProcessedSet;

    private transient PackagingDataCalculator packagingDataCalculator;
    private boolean calculatedPackageData = false;

    private boolean circular;
    private static final Method GET_SUPPRESSED_METHOD;

    static {
        Method method = null;
        try {
            method = Throwable.class.getMethod("getSuppressed");
        } catch (NoSuchMethodException e) {

        }
        GET_SUPPRESSED_METHOD = method;
    }

    public ThrowableProxy(Throwable throwable) {
        this(throwable, Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>()));
    }

    public ThrowableProxy(Throwable circular, boolean isCircular) {
        this.throwable = circular;
        this.className = circular.getClass().getName();
        this.message = circular.getMessage();
        this.stackTraceElementProxyArray = EMPTY_STEP;
        this.circular = true;
    }

    public ThrowableProxy(Throwable throwable, Set<Throwable> alreadyProcessedSet) {

        this.throwable = throwable;
        this.className = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.stackTraceElementProxyArray = ThrowableProxyUtil.steArrayToStepArray(throwable.getStackTrace());

        alreadyProcessedSet.add(throwable);

        Throwable nested = throwable.getCause();
        if (nested != null) {
            if (alreadyProcessedSet.contains(nested)) {
                this.cause = new ThrowableProxy(nested, true);
            } else {
                this.cause = new ThrowableProxy(nested, alreadyProcessedSet);
                this.cause.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(nested.getStackTrace(),
                        stackTraceElementProxyArray);
            }
        }

        if (GET_SUPPRESSED_METHOD != null) {
            Throwable[] throwableSuppressed = extractSuppressedThrowables(throwable);

            if (throwableSuppressed.length > 0) {
                List<ThrowableProxy> suppressedList = new ArrayList<>(throwableSuppressed.length);
                for (Throwable sup : throwableSuppressed) {
                    if (alreadyProcessedSet.contains(sup)) {
                        ThrowableProxy throwableProxy = new ThrowableProxy(sup, true);
                        suppressedList.add(throwableProxy);
                    } else {
                        ThrowableProxy throwableProxy = new ThrowableProxy(sup, alreadyProcessedSet);
                        throwableProxy.commonFrames = ThrowableProxyUtil.findNumberOfCommonFrames(sup.getStackTrace(),
                                stackTraceElementProxyArray);
                        suppressedList.add(throwableProxy);
                    }
                }
                this.suppressed = suppressedList.toArray(new ThrowableProxy[suppressedList.size()]);
            }
        }
    }

    private Throwable[] extractSuppressedThrowables(Throwable t) {
        try {
            Object obj = GET_SUPPRESSED_METHOD.invoke(t);
            if (obj instanceof Throwable[]) {
                Throwable[] throwableSuppressed = (Throwable[]) obj;
                return throwableSuppressed;
            } else {
                return null;
            }
        } catch (IllegalAccessException e) {
            // ignore
        } catch (IllegalArgumentException e) {

        } catch (InvocationTargetException e) {

        }

        return null;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getMessage() {
        return message;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.qos.logback.classic.spi.IThrowableProxy#getClassName()
     */
    public String getClassName() {
        return className;
    }

    public StackTraceElementProxy[] getStackTraceElementProxyArray() {
        return stackTraceElementProxyArray;
    }

    public boolean isCyclic() {
        return circular;
    }

    public int getCommonFrames() {
        return commonFrames;
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.qos.logback.classic.spi.IThrowableProxy#getCause()
     */
    public IThrowableProxy getCause() {
        return cause;
    }

    public IThrowableProxy[] getSuppressed() {
        return suppressed;
    }

    public PackagingDataCalculator getPackagingDataCalculator() {
        // if original instance (non-deserialized), and packagingDataCalculator
        // is not already initialized, then create an instance.
        // here we assume that (throwable == null) for deserialized instances
        if (throwable != null && packagingDataCalculator == null) {
            packagingDataCalculator = new PackagingDataCalculator();
        }
        return packagingDataCalculator;
    }

    public void calculatePackagingData() {
        if (calculatedPackageData)
            return;
        PackagingDataCalculator pdc = this.getPackagingDataCalculator();
        if (pdc != null) {
            calculatedPackageData = true;
            pdc.calculate(this);
        }
    }

    public void fullDump() {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElementProxy step : stackTraceElementProxyArray) {
            String string = step.toString();
            builder.append(CoreConstants.TAB).append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
            builder.append(CoreConstants.LINE_SEPARATOR);
        }
        System.out.println(builder.toString());
    }
}