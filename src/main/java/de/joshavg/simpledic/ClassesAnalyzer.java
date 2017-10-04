package de.joshavg.simpledic;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ClassesAnalyzer {

    private final Class<?>[] classes;

    ClassesAnalyzer(Class<?>... classes) {
        this.classes = classes;
    }

    List<ServiceDefinition> createDefinitions() {
        return Arrays.stream(classes)
            .map(clz -> {
                Constructor<?>[] cs = clz.getDeclaredConstructors();
                boolean isSingleton = clz.getDeclaredAnnotation(OncePerContainer.class) != null;

                return new ServiceDefinition()
                    .setClz(clz)
                    .setName(clz.getName())
                    .setSingleton(isSingleton)
                    .setConstructors(cs);
            })
            .collect(Collectors.toList());
    }

}
