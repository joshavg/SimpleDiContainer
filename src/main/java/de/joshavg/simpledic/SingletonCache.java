package de.joshavg.simpledic;

import java.util.HashMap;
import java.util.Map;

class SingletonCache {

    private final Map<ServiceDefinition, Object> instances;

    SingletonCache() {
        this.instances = new HashMap<>();
    }

    Object get(ServiceDefinition def) {
        return instances.get(def);
    }

    boolean isStored(ServiceDefinition def) {
        return instances.containsKey(def);
    }

    void store(ServiceDefinition def, Object instance) {
        if (!isStored(def)) {
            instances.put(def, instance);
        }
    }

}
