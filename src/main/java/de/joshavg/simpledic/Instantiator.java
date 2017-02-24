package de.joshavg.simpledic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class Instantiator<T> {

    private final ServiceDefinition definition;
    private final SdiContainerInterface container;

    Instantiator(ServiceDefinition definition, SdiContainerInterface container) {
        this.definition = definition;
        this.container = container;
    }

    T createInstance()
        throws IllegalAccessException, InvocationTargetException, InstantiationException {

        List<Dependency> dependencies = definition.getDependencies();
        List<Object> params = new ArrayList<>();

        for (Dependency dep : dependencies) {
            @SuppressWarnings("unchecked")
            Class<Object> paramClz = (Class<Object>) dep.getType();

            if (dep.isUseSupplier()) {
                params.add((Supplier) () -> container.getInstance(dep.getType()));
            } else {
                params.add(container.getInstance(paramClz));
            }
        }

        @SuppressWarnings("unchecked")
        Constructor<T> constructor = definition.getConstructor();

        return constructor.newInstance(params.toArray());
    }

}
