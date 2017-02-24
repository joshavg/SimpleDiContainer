package de.joshavg.simpledic;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class ConstructorAnalyzer {

    private final Constructor<?> constructor;

    ConstructorAnalyzer(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    List<Dependency> getTypes() {
        List<Dependency> types = new ArrayList<>();

        Type[] genericParameterTypes = constructor.getGenericParameterTypes();
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        for (int pi = 0; pi < parameterTypes.length; ++pi) {
            Class<?> parameterType = parameterTypes[pi];

            Dependency dep = determineType(genericParameterTypes[pi], parameterType);

            types.add(dep);
        }

        return types;
    }

    private static Dependency determineType(Type genericType, Class<?> parameterType) {
        Class<?> type = parameterType;
        boolean useSupplier = false;

        if (parameterType == Supplier.class) {
            type = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
            useSupplier = true;
        }

        return new Dependency(type, useSupplier);
    }
}
