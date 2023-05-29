package core.spi;

import core.CoreConstants;

import java.util.*;

abstract public class AbstractComponentTracker<C> implements ComponentTracker<C> {

    private static final boolean ACCESS_ORDERED = true;

    final public static long LINGERING_TIMEOUT = 10 * CoreConstants.MILLIS_IN_ONE_SECOND;
    final public static long WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS = CoreConstants.MILLIS_IN_ONE_SECOND;
    protected int maxComponents = DEFAULT_MAX_COMPONENTS;
    protected long timeout = DEFAULT_TIMEOUT;

    LinkedHashMap<String, Entry<C>> liveMap = new LinkedHashMap<>(32, .75f, ACCESS_ORDERED);
    LinkedHashMap<String, Entry<C>> lingerersMap = new LinkedHashMap<>(16, .75f, ACCESS_ORDERED);
    long lastCheck = 0;

    abstract protected void processPriorToRemoval(C component);
    abstract protected C buildComponent(String key);
    protected abstract boolean isComponentStale(C c);

    public int getComponentCount() {
        return liveMap.size() + lingerersMap.size();
    }

    private Entry<C> getFromEitherMap(String key) {
        Entry<C> entry = liveMap.get(key);
        if (entry != null)
            return entry;
        else
            return lingerersMap.get(key);
    }

    public synchronized C find(String key) {
        Entry<C> entry = getFromEitherMap(key);
        if (entry == null)
            return null;
        else
            return entry.component;
    }

    public synchronized C getOrCreate(String key, long timestamp) {
        Entry<C> entry = getFromEitherMap(key);
        if (entry == null) {
            C c = buildComponent(key);
            entry = new Entry<>(key, c, timestamp);
            liveMap.put(key, entry);
        } else {
            entry.setTimestamp(timestamp);
        }
        return entry.component;
    }

    public void endOfLife(String key) {
        Entry<C> entry = liveMap.remove(key);
        if (entry == null)
            return;
        lingerersMap.put(key, entry);
    }

    public synchronized void removeStaleComponents(long now) {
        if (isTooSoonForRemovalIteration(now))
            return;
        removeExcedentComponents();
        removeStaleComponentsFromMainMap(now);
        removeStaleComponentsFromLingerersMap(now);
    }

    private void removeExcedentComponents() {
        genericStaleComponentRemover(liveMap, 0, byExcedent);
    }

    private void removeStaleComponentsFromMainMap(long now) {
        genericStaleComponentRemover(liveMap, now, byTimeout);
    }

    private void removeStaleComponentsFromLingerersMap(long now) {
        genericStaleComponentRemover(lingerersMap, now, byLingering);
    }

    private void genericStaleComponentRemover(LinkedHashMap<String, Entry<C>> map, long now, RemovalPredicator<C> removalPredicator) {
        Iterator<Map.Entry<String, Entry<C>>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Entry<C>> mapEntry = iter.next();
            Entry<C> entry = mapEntry.getValue();
            if (removalPredicator.isSlatedForRemoval(entry, now)) {
                iter.remove();
                C c = entry.component;
                processPriorToRemoval(c);
            } else {
                break;
            }
        }
    }

    private RemovalPredicator<C> byExcedent = (entry, timestamp) -> {
        return (liveMap.size() > maxComponents);
    };

    private RemovalPredicator<C> byTimeout = (entry, timestamp) -> {
        return isEntryStale(entry, timestamp);
    };

    private RemovalPredicator<C> byLingering = (entry, timestamp) -> {
        return isEntryDoneLingering(entry, timestamp);
    };

    private boolean isTooSoonForRemovalIteration(long now) {
        if (lastCheck + WAIT_BETWEEN_SUCCESSIVE_REMOVAL_ITERATIONS > now) {
            return true;
        }
        lastCheck = now;
        return false;
    }

    private boolean isEntryStale(Entry<C> entry, long now) {
        C c = entry.component;
        if (isComponentStale(c))
            return true;
        return ((entry.timestamp + timeout) < now);
    }

    private boolean isEntryDoneLingering(Entry<C> entry, long now) {
        return ((entry.timestamp + LINGERING_TIMEOUT) < now);
    }

    public Set<String> allKeys() {
        HashSet<String> allKeys = new HashSet<>(liveMap.keySet());
        allKeys.addAll(lingerersMap.keySet());
        return allKeys;
    }

    public Collection<C> allComponents() {
        List<C> allComponents = new ArrayList<>();
        for (Entry<C> e : liveMap.values())
            allComponents.add(e.component);
        for (Entry<C> e : lingerersMap.values())
            allComponents.add(e.component);

        return allComponents;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxComponents() {
        return maxComponents;
    }

    public void setMaxComponents(int maxComponents) {
        this.maxComponents = maxComponents;
    }

    private interface RemovalPredicator<C> {
        boolean isSlatedForRemoval(Entry<C> entry, long timestamp);
    }

    private static class Entry<C> {
        String key;
        C component;
        long timestamp;

        Entry(String k, C c, long timestamp) {
            this.key = k;
            this.component = c;
            this.timestamp = timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            @SuppressWarnings("unchecked")
            final Entry<C> other = (Entry<C>) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (component == null) {
                if (other.component != null)
                    return false;
            } else if (!component.equals(other.component)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + key + ", " + component + ")";
        }
    }
}
