package de.joshavg.simpledic;

import java.lang.reflect.Constructor;

class ServiceDefinition {

    private Class<?> clz;
    private String name;
    private Constructor<?> constructor;
    private Constructor<?>[] constructors;
    private boolean isSingleton;

    Class<?> getClz() {
        return clz;
    }

    ServiceDefinition setClz(Class<?> clz) {
        this.clz = clz;
        return this;
    }

    String getName() {
        return name;
    }

    ServiceDefinition setName(String name) {
        this.name = name;
        return this;
    }

    Constructor getConstructor() {
        return constructor;
    }

    ServiceDefinition setConstructors(Constructor<?>... constructors) {
        this.constructors = constructors;

        if (constructors.length > 0) {
            constructor = constructors[0];
        }

        return this;
    }

    Constructor[] getConstructors() {
        return constructors;
    }

    boolean isSingleton() {
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
