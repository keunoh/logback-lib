package core.status;

public class ErrorStatus extends StatusBase {
    public ErrorStatus(String msg, Object origin) {
        super(Status.ERROR, msg, origin);
    }

    public ErrorStatus(String msg, Object origin, Throwable t) {
        super(Status.ERROR, msg, origin, t);
    }
}
