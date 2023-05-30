package core;

import core.encoder.Encoder;
import core.encoder.LayoutWrappingEncoder;
import core.spi.DeferredProcessingAware;
import core.status.ErrorStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import static core.CoreConstants.CODES_URL;

public class OutputStreamAppender<E> extends UnsynchronizedAppenderBase<E> {

    protected Encoder<E> encoder;
    protected final ReentrantLock lock = new ReentrantLock(false);
    private OutputStream outputStream;
    boolean immediateFlush = true;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void start() {
        int errors = 0;
        if (this.encoder == null) {
            addStatus(new ErrorStatus("No encoder set for the appender named \"" + name + "\".", this));
            errors++;
        }

        if (this.outputStream == null) {
            addStatus(new ErrorStatus("No output stream set for the appender named \"" + name + "\".", this));
            errors++;
        }

        if (errors == 0) {
            super.start();
        }
    }

    public void setLayout(Layout<E> layout) {
        addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
        addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
        addWarn("See also " + CODES_URL + "#layoutInsteadOfEncoder for details");
        LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder<E>();
        lwe.setLayout(layout);
        lwe.setContext(context);
        this.encoder = lwe;
    }

    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }

        subAppend(eventObject);
    }

    public void stop() {
        lock.lock();
        try {
            closeOutputStream();
            super.stop();
        } finally {
            lock.unlock();
        }
    }

    protected void closeOutputStream() {
        if (this.outputStream != null) {
            try {
                encoderClose();
                this.outputStream.close();
                this.outputStream = null;
            } catch (IOException e) {
                addStatus(new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, e));
            }
        }
    }

    void encoderClose() {
        if (encoder != null && this.outputStream != null) {
            try {
                byte[] footer = encoder.footerBytes();
                writeBytes(footer);
            } catch (IOException ioe) {
                this.started = false;
                addStatus(new ErrorStatus("Failed to write footer for appender named [" + name + "].", this, ioe));
            }
        }
    }

    public void setOutputStream(OutputStream outputStream) {
        lock.lock();
        try {
            closeOutputStream();
            this.outputStream = outputStream;
            if (encoder == null) {
                addWarn("Encoder has not been set. Cannot invoke its init method.");
                return;
            }

            encoderInit();
        } finally {
            lock.unlock();
        }
    }

    void encoderInit() {
        if (encoder != null && this.outputStream != null) {
            try {
                byte[] header = encoder.headerBytes();
                writeBytes(header);
            } catch (IOException ioe) {
                this.started = false;
                addStatus(new ErrorStatus("Failed to initialize encoder for appender named [" + name + "].", this, ioe));
            }
        }
    }

    protected void writeOut(E event) throws IOException {
        byte[] byteArray = this.encoder.encode(event);
        writeBytes(byteArray);
    }

    private void writeBytes(byte[] byteArray) throws IOException {
        if (byteArray == null || byteArray.length == 0)
            return;

        lock.lock();
        try {
            this.outputStream.write(byteArray);
            if (immediateFlush) {
                this.outputStream.flush();
            }
        } finally {
            lock.unlock();
        }
    }

    protected void subAppend(E event) {
        if (!isStarted())
            return;
        try {
            if (event instanceof DeferredProcessingAware) {
                ((DeferredProcessingAware) event).prepareForDeferredProcessing();
            }

            byte[] byteArray = this.encoder.encode(event);
            writeBytes(byteArray);
        } catch (IOException ioe) {
            this.started = false;
            addStatus(new ErrorStatus("IO failure in appender", this, ioe));
        }
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public boolean isImmediateFlush() {
        return immediateFlush;
    }

    public void setImmediateFlush(boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }

}
