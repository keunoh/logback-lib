package core.joran.spi;

public class ActionException extends Exception {

    private static final long serialVersionUID = 2743349809995319806L;

    public ActionException() {
    }

    public ActionException(final Throwable rootCause) {
        super(rootCause);
    }
}
