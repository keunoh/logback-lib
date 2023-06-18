package core.net.server;

import core.spi.ContextAware;

public interface ServerRunner<T extends Client> extends ContextAware, Runnable {
    boolean isRunning();
    void stop() throws Exception;
    void accept(ClientVisitor<T> visitor);
}
