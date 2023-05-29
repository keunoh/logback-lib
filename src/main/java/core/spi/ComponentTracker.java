package core.spi;

import core.CoreConstants;

import java.util.Collection;
import java.util.Set;

public interface ComponentTracker<C> {

    public final int DEFAULT_TIMEOUT = (int) (30 * 60 * CoreConstants.MILLIS_IN_ONE_SECOND); // 30 minutes

    int DEFAULT_MAX_COMPONENTS = Integer.MAX_VALUE;

    int getComponentCount();

    C find(String key);

    C getOrCreate(String key, long timestamp);

    void removeStaleComponents(long now);

    void endOfLife(String key);

    Collection<C> allComponents();

    Set<String> allKeys();
}
