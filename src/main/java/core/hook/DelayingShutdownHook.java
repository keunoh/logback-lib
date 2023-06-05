package core.hook;


import core.util.Duration;

public class DelayingShutdownHook extends ShutdownHookBase {

    public static final Duration DEFAULT_DELAY = Duration.buildByMilliseconds(0);

    private Duration delay = DEFAULT_DELAY;

    public DelayingShutdownHook() {}

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    public void run() {
        addInfo("Sleeping for " + delay);
        try {
            Thread.sleep(delay.getMilliseconds());
        } catch (InterruptedException e) {
        }
        super.stop();
    }
}
