package core.net.server;

import java.io.Closeable;

public interface Client extends Runnable, Closeable {
    void close();
}
