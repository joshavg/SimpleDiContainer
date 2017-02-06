package de.joshavg.simpledic;

import java.lang.reflect.Constructor;

class ServiceDefinition {

    private Class<?> clz;
    private String name;
    private Constructor<?> constructor;

    public Class<?> getClz() {
        return clz;
    }

    public ServiceDefinition setClz(Class<?> clz) {
        this.clz = clz;
        return this;
    }

    public String getName() {
        return name;
    }

    public ServiceDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public ServiceDefinition setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
        return this;
    }
}
