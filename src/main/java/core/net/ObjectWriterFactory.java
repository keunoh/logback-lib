package core.net;

import core.CoreConstants;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectWriterFactory {

    public AutoFlushingObjectWriter newAutoFlushingObjectWriter(OutputStream outputStream) throws IOException {
        return new AutoFlushingObjectWriter(new ObjectOutputStream(outputStream), CoreConstants.OOS_RESET_FREQUENCY);
    }
}
