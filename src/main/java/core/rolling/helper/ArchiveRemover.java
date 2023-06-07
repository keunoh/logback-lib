package core.rolling.helper;

import core.spi.ContextAware;

import java.util.Date;
import java.util.concurrent.Future;

public interface ArchiveRemover extends ContextAware {

    void clean(Date now);

    void setMaxHistory(int maxHistory);

    void setTotalSizeCap(long totalSizeCap);

    Future<?> cleanAsynchronously(Date now);
}
