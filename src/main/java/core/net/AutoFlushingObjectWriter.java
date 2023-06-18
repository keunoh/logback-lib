package core.net;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class AutoFlushingObjectWriter implements ObjectWriter {
    private final ObjectOutputStream objectOutputStream;
    private final int resetFrequency;
    private int writeCounter = 0;

    /**
     * Creates a new instance for the given {@link java.io.ObjectOutputStream}.
     *
     * @param objectOutputStream the stream to write to
     * @param resetFrequency the frequency with which the given stream will be
     *                       automatically reset to prevent a memory leak
     */
    public AutoFlushingObjectWriter(ObjectOutputStream objectOutputStream, int resetFrequency) {
        this.objectOutputStream = objectOutputStream;
        this.resetFrequency = resetFrequency;
    }

    @Override
    public void write(Object object) throws IOException {
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        preventMemoryLeak();
    }

    /**
     * Failing to reset the object output stream every now and then creates a serious memory leak which
     * is why the underlying stream will be reset according to the {@code resetFrequency}.
     */
    private void preventMemoryLeak() throws IOException {
        if (++writeCounter >= resetFrequency) {
            objectOutputStream.reset();
            writeCounter = 0;
        }
    }
}
