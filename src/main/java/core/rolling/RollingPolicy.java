package core.rolling;

import core.FileAppender;
import core.rolling.helper.CompressionMode;
import core.spi.LifeCycle;

public interface RollingPolicy extends LifeCycle {

    void rollover() throws RolloverFailure;

    String getActiveFileName();

    CompressionMode getCompressionMode();

    void setParent(FileAppender<?> appender);
}
