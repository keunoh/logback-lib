package core.status;

public class WarnStatus extends StatusBase {
    public WarnStatus(String msg, Object origin) {
        super(Status.WARN, msg, origin);
    }

    public WarnStatus(String msg, Object origin, Throwable t) {
        super(Status.WARN, msg, origin, t);
    }
}
