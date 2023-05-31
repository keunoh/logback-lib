package core.recovery;

public class RecoveryCoordinator {

    public final static long BACKOFF_COEFFICIENT_MIN = 20;
    public final static long BACKOFF_MULTIPLIER = 4;
    static long BACKOFF_COEFFICIENT_MAX = 327680;
    private long backOffCoefficient = BACKOFF_COEFFICIENT_MIN;
    private static long UNSET = -1;
    private long currentTime = UNSET;
    private long next;

    public RecoveryCoordinator() {
        next = getCurrentTime() + getBackoffCoefficient();
    }

    public RecoveryCoordinator(long currentTime) {
        this.currentTime = currentTime;
        next = getCurrentTime() + getBackoffCoefficient();
    }

    public boolean isTooSoon() {
        long now = getCurrentTime();
        if (now > next) {
            next = now + getBackoffCoefficient();
            return false;
        } else {
            return true;
        }
    }

    void setCurrentTime(long forcedTime) {
        currentTime = forcedTime;
    }

    private long getCurrentTime() {
        if (currentTime != UNSET) {
            return currentTime;
        }
        return System.currentTimeMillis();
    }

    private long getBackoffCoefficient() {
        long currentCoeff = backOffCoefficient;
        if (backOffCoefficient < BACKOFF_COEFFICIENT_MAX) {
            backOffCoefficient *= BACKOFF_MULTIPLIER;
        }
        return currentCoeff;
    }
}