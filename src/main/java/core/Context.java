package core;

import core.spi.LifeCycle;
import core.status.StatusManager;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public interface Context extends PropertyContainer {

    StatusManager getStatusManager();

    Object getObject(String key);

    void putObject(String key, Object value);

    String getProperty(String key);

    void putProperty(String key, String value);

    Map<String, String> getCopyOfPropertyMap();

    String getName();

    void setName(String name);

    long getBirthTime();

    Object getConfigurationLock();

    ScheduledExecutorService getScheduledExecutorService();

    ExecutorService getExecutorService();

    void register(LifeCycle component);

    void addScheduledFuture(ScheduledFuture<?> scheduledFuture);
}
