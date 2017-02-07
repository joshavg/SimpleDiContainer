package de.joshavg.simpledic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class Instantiator<T> {

    private final Class<T> clz;
    private final SdiContainerInterface container;

    Instantiator(Class<T> clz, SdiContainerInterface container) {
        this.clz = clz;
        this.container = container;
    }

    T createInstance()
        throws IllegalAccessException, InvocationTargetException, InstantiationException {
        @SuppressWarnings("unchecked")
        Constructor<T> constructor = (Constructor<T>) clz.getDeclaredConstructors()[0];

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; ++i) {
            @SuppressWarnings("unchecked")
            Class<Object> paramClz = (Class<Object>) parameterTypes[i];
            parameters[i] = container.getInstance(paramClz);
        }

        return constructor.newInstance(parameters);
    }

    T createSingleton() {
        return null;
    }

}
