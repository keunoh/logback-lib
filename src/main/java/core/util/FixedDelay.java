package core.util;

public class FixedDelay implements DelayStrategy {

    private final long subsequentDelay;
    private long nextDelay;

    public FixedDelay(long subsequentDelay, long initialDelay) {
        this.subsequentDelay = subsequentDelay;
        this.nextDelay = initialDelay;
    }

    public FixedDelay(int delay) {
        this(delay, delay);
    }

    @Override
    public long nextDelay() {
        long delay = nextDelay;
        nextDelay = subsequentDelay;
        return delay;
    }
}
