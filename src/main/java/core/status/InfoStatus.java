package core.status;

public class InfoStatus extends StatusBase {
    public InfoStatus(String msg, Object origin) {
        super(Status.INFO, msg, origin);
    }

    public InfoStatus(String msg, Object origin, Throwable t) {
        super(Status.INFO, msg, origin, t);
    }
}
