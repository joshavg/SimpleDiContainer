package de.joshavg.simpledic;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceDefinition {

    private Class<?> clz;
    private String name;
    private Constructor<?> constructor;
    private boolean isSingleton;
    private List<Dependency> dependencies = Collections.emptyList();

    void setDependencies(List<Dependency> types) {
        dependencies = types;
    }

    List<Dependency> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    @SuppressWarnings("WeakerAccess")
    public Class<?> getClz() {
        return clz;
    }

    ServiceDefinition setClz(Class<?> clz) {
        this.clz = clz;
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public String getName() {
        return name;
    }

    ServiceDefinition setName(String name) {
        this.name = name;
        return this;
    }

    Constructor getConstructor() {
        return constructor;
    }

    ServiceDefinition setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSingleton() {
        return isSingleton;
    }

    ServiceDefinition setSingleton(boolean singleton) {
        isSingleton = singleton;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ServiceDefinition that = (ServiceDefinition) o;

        if (isSingleton != that.isSingleton) {
            return false;
        }
        if (!clz.equals(that.clz)) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (!name.equals(that.name)) {
            return false;
        }
        return constructor.equals(that.constructor);
    }

    @Override
    public int hashCode() {
        int result = clz.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + constructor.hashCode();
        result = 31 * result + (isSingleton ? 1 : 0);
        return result;
    }
}
