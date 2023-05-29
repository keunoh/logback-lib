package core;

import core.spi.LifeCycle;

import java.util.HashSet;
import java.util.Set;

public class LifeCycleManager {

    private final Set<LifeCycle> components = new HashSet<>();

    public void register(LifeCycle component) {
        components.add(component);
    }

    public void reset() {
        for (LifeCycle component : components) {
            if (component.isStarted()) {
                component.stop();
            }
        }
        components.clear();
    }
}
