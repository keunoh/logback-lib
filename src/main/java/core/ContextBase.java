package core;

import core.spi.LifeCycle;
import core.status.StatusManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static core.CoreConstants.*;

public class ContextBase implements Context, LifeCycle {
    private long birthTime = System.currentTimeMillis();
    private String name;
    private StatusManager sm = new BasicStatusManager();
    Map<String, String> propertyMap = new HashMap<>();
    Map<String, String> objectMap = new HashMap<>();
    LogbackLock configurationLock = new LogbackLock();
    private ScheduledExecutorService scheduledExecutorService;
    protected List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>(1);
    private LifeCycleManager lifeCycleManager;
    private boolean started;

    public ContextBase() {
        initCollisionMaps();
    }

    public StatusManager getStatusManager() {
        return sm;
    }

    /**
     * Set the {@link StatusManager} for this context. Note that by default this
     * context is initialized with a {@link BasicStatusManager}. A null value for
     * the 'statusManager' argument is not allowed.
     * <p/>
     * <p> A malicious attacker can set the status manager to a dummy instance,
     * disabling internal error reporting.
     *
     * @param statusManager the new status manager
     */
    public void setStatusManager(StatusManager statusManager) {
        // this method was added in response to http://jira.qos.ch/browse/LBCORE-35
        if (statusManager == null) {
            throw new IllegalArgumentException("null StatusManager not allowed");
        }
        this.sm = statusManager;
    }

    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap<String, String>(propertyMap);
    }

    public void putProperty(String key, String val) {
        if (HOSTNAME_KEY.equalsIgnoreCase(key)) {
            putHostnameProperty(val);
        } else {
            this.propertyMap.put(key, val);
        }
    }

    protected void initCollisionMaps() {
        putObject(FA_FILENAME_COLLISION_MAP, new HashMap<String, String>());
        putObject(RFA_FILENAME_PATTERN_COLLISION_MAP, new HashMap<String, FileNamePattern>());
    }

    public String getProperty(String key) {
        if (CONTEXT_NAME_KEY.equals(key)) {
            return getName();
        }
        if (HOSTNAME_KEY.equalsIgnoreCase(key)) {
            return lazyGetHostName();
        }

        return (String) this.propertyMap.get(key);
    }

    private String lazyGetHostName() {
        String hostName = (String) this.propertyMap.get(HOSTNAME_KEY);
        if (hostName == null) {
            hostName = new ContextUtil(this).safelyGetLocalHostName();
            putHostnameProperty(hostName);
        }
        return hostName;
    }

    private void putHostnameProperty(String hostname) {
        String existingHostname = (String) this.propertyMap.get(HOSTNAME_KEY);
        if (existingHostname == null) {
            this.propertyMap.put(HOSTNAME_KEY, hostname);
        } else {

        }
    }

    public Object getObject(String key) {
        return objectMap.get(key);
    }

    public void putObject(String key, Object value) {
        objectMap.put(key, value);
    }

    public void removeObject(String key) {
        objectMap.remove(key);
    }

    public String getName() {
        return name;
    }

    public void start() {
        // We'd like to create the executor service here, but we can't;
        // ContextBase has not always implemented LifeCycle and there are *many*
        // uses (mostly in tests) that would need to be modified.
        started = true;
    }

    public void stop() {
        // We don't check "started" here, because the executor service uses
        // lazy initialization, rather than being created in the start method
        stopExecutorService();

        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void reset() {
        removeShutdownHook();
        getLifeCycleManager().reset();
        propertyMap.clear();
        objectMap.clear();
    }

    public void setName(String name) throws IllegalStateException {
        if (name != null && name.equals(this.name)) {
            return;
        }
        if (this.name == null || DEFAULT_CONTEXT_NAME.equals(this.name)) {
            this.name = name;
        } else {
            throw new IllegalStateException("Context has been already given a name");
        }
    }

    public long getBirthTime() {
        return birthTime;
    }

    public Object getConfigurationLock() {
        return configurationLock;
    }

    @Override
    /**
     * @deprecated
     */
    public synchronized ExecutorService getExecutorService() {
        return getScheduledExecutorService();
    }

    @Override
    public synchronized ScheduledExecutorService getScheduledExecutorService() {
        if (scheduledExecutorService == null) {
            scheduledExecutorService = ExecutorServiceUtil.newScheduledExecutorService();
        }
        return scheduledExecutorService;
    }

    private synchronized void stopExecutorService() {
        if (scheduledExecutorService != null) {
            ExecutorServiceUtil.shutdown(scheduledExecutorService);
            scheduledExecutorService = null;
        }
    }

    private void removeShutdownHook() {
        Thread hook = (Thread) getObject(SHUTDOWN_HOOK_THREAD);
        if (hook != null) {
            removeObject(SHUTDOWN_HOOK_THREAD);
            try {
                Runtime.getRuntime().removeShutdownHook(hook);
            } catch (IllegalStateException e) {

            }
        }
    }

    public void register(LifeCycle component) {
        getLifeCycleManager().register(component);
    }


    synchronized LifeCycleManager getLifeCycleManager() {
        if (lifeCycleManager == null) {
            lifeCycleManager = new LifeCycleManager();
        }
        return lifeCycleManager;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void addScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        scheduledFutures.add(scheduledFuture);
    }

    public List<ScheduledFuture<?>> getScheduledFutures() {
        return new ArrayList<ScheduledFuture<?>>(scheduledFutures);
    }
}

























