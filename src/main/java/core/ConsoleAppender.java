package core;

import core.joran.spi.ConsoleTarget;
import core.status.Status;
import core.status.WarnStatus;
import core.util.EnvUtil;
import core.util.OptionHelper;

import java.io.OutputStream;
import java.util.Arrays;

public class ConsoleAppender<E> extends OutputStreamAppender<E> {

    protected ConsoleTarget target = ConsoleTarget.SystemOut;
    protected boolean withJansi = false;
    private final static String WindowsAnsiOutputStream_CLASS_NAME = "org.fusesource.jansi.WindowsAnsiOutputStream";

    public void setTarget(String value) {
        ConsoleTarget t = ConsoleTarget.findByName(value.trim());
        if (t == null)
            targetWarn(value);
        else
            target = t;
    }

    public String getTarget() {
        return target.getName();
    }

    private void targetWarn(String val) {
        Status status = new WarnStatus("[" + val + "] should be one of " + Arrays.toString(ConsoleTarget.values()), this);
        status.add(new WarnStatus("Using previously set target, System.out by default.", this));
        addStatus(status);
    }

    @Override
    public void start() {
        OutputStream targetStream = target.getStream();
        if (EnvUtil.isWindows() && withJansi) {
            targetStream = getTargetStreamForWindows(targetStream);
        }
        setOutputStream(targetStream);
        super.start();
    }

    private OutputStream getTargetStreamForWindows(OutputStream targetStream) {
        try {
            addInfo("Enabling JANSI WindowsAnsiOutputStream for the console.");
            Object windowsAnsiOutputStream = OptionHelper.instantiateByClassNameAndParameter(WindowsAnsiOutputStream_CLASS_NAME, Object.class, context,
                    OutputStream.class, targetStream);
            return (OutputStream) windowsAnsiOutputStream;
        } catch (Exception e) {
            addWarn("Failed to create WindowsAnsiOutputStream. Falling back on the default stream.", e);
        }
        return targetStream;
    }

    public boolean isWithJansi() {
        return withJansi;
    }

    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }
}
