package core.util;

public class DefaultInvocationGate implements InvocationGate {

    static final int MASK_DECREASE_RIGHT_SHIFT_COUNT = 2;
    private static final int MAX_MASK = 0xFFFF;
    static final int DEFAULT_MASK = 0xF;
    private volatile long mask = DEFAULT_MASK;
    private long invocationCounter = 0;
    private static final long MASK_INCREASE_THRESHOLD = 100;
    private static final long MASK_DECREASE_THRESHOLD = MASK_INCREASE_THRESHOLD * 8;

    public DefaultInvocationGate() {
        this(MASK_INCREASE_THRESHOLD, MASK_DECREASE_THRESHOLD, System.currentTimeMillis());
    }

    public DefaultInvocationGate(long minDelayThreshold, long maxDelayThreshold, long currentTime) {
        this.minDelayThreshold = minDelayThreshold;
        this.maxDelayThreshold = maxDelayThreshold;
        this.lowerLimitForMaskMatch = currentTime + minDelayThreshold;
        this.upperLimitForNoMaskMatch = currentTime + maxDelayThreshold;
    }

    private long minDelayThreshold;
    private long maxDelayThreshold;
    long lowerLimitForMaskMatch;
    long upperLimitForNoMaskMatch;

    @Override
    public boolean isTooSoon(long currentTime) {
        boolean maskMatch = ((invocationCounter++) & mask) == mask;

        if (maskMatch) {
            if (currentTime < this.lowerLimitForMaskMatch) {
                increaseMask();
            }
            updateLimits(currentTime);
        } else {
            if (currentTime > this.upperLimitForNoMaskMatch) {
                decreaseMask();
                updateLimits(currentTime);
                return false;
            }
        }
        return !maskMatch;
    }

    private void updateLimits(long currentTime) {
        this.lowerLimitForMaskMatch = currentTime + minDelayThreshold;
        this.upperLimitForNoMaskMatch = currentTime + maxDelayThreshold;
    }

    long getMask() {
        return mask;
    }

    private void increaseMask() {
        if (mask >= MAX_MASK)
            return;
        mask = (mask << 1) | 1;
    }

    private void decreaseMask() {
        mask = mask >>> MASK_DECREASE_RIGHT_SHIFT_COUNT;
    }

    public long getInvocationCounter() {
        return invocationCounter;
    }
}
