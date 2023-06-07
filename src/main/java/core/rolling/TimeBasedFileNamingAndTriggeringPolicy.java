package core.rolling;

import core.rolling.helper.ArchiveRemover;
import core.spi.ContextAware;

public interface TimeBasedFileNamingAndTriggeringPolicy<E> extends TriggeringPolicy<E>, ContextAware {

    /**
     * Set the host/parent {@link TimeBasedRollingPolicy}.
     *
     * @param tbrp
     *                parent TimeBasedRollingPolicy
     */
    void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> tbrp);

    /**
     * Return the file name for the elapsed periods file name.
     *
     * @return
     */
    String getElapsedPeriodsFileName();

    /**
     * Return the current periods file name without the compression suffix. This
     * value is equivalent to the active file name.
     *
     * @return current period's file name (without compression suffix)
     */
    String getCurrentPeriodsFileNameWithoutCompressionSuffix();

    /**
     * Return the archive remover appropriate for this instance.
     */
    ArchiveRemover getArchiveRemover();

    /**
     * Return the current time which is usually the value returned by
     * System.currentMillis(). However, for <b>testing</b> purposed this value
     * may be different than the real time.
     *
     * @return current time value
     */
    long getCurrentTime();

    /**
     * Set the current time. Only unit tests should invoke this method.
     *
     * @param now
     */
    void setCurrentTime(long now);

    /**
     * Set some date in the current period. Only unit tests should invoke this
     * method.
     *
     * WARNING: method removed. A unit test should not set the
     * date in current period. It is the job of the FNATP to compute that.
     *
     * @param date
     */
    // void setDateInCurrentPeriod(Date date);
}
