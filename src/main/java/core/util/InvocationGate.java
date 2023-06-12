package core.util;

public interface InvocationGate {
    final long TIME_UNAVAILABLE = -1;

    public abstract boolean isTooSoon(long currentTime);
}
