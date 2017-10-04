package de.joshavg.simpledic;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ClassesAnalyzer {

    private final List<Class<?>> classes;

    ClassesAnalyzer(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    ClassesAnalyzer(List<Class<?>> classes) {
        this.classes = classes;
    }

    List<ServiceDefinition> createDefinitions() {
        List<ServiceDefinition> defs = classes.stream()
            .map(clz -> {
                Constructor<?>[] cs = clz.getDeclaredConstructors();
                boolean isSingleton = isSingleton(clz);

                return new ServiceDefinition()
                    .setClz(clz)
                    .setName(clz.getName())
                    .setSingleton(isSingleton)
                    .setConstructors(cs);
            })
            .collect(Collectors.toList());

//        for (ServiceDefinition def : defs) {
//            if (def.getConstructor() != null) {
//                defs.addAll(
//                    findTransitiveDependencies(def)
//                        .stream()
//                        .distinct()
//                        .collect(Collectors.toList()));
//            }
//        }

        return defs;
    }

    private static boolean isSingleton(Class<?> clz) {
        return clz.getDeclaredAnnotation(OncePerContainer.class) != null;
    }

    private List<ServiceDefinition> findTransitiveDependencies(ServiceDefinition def) {
        List<ServiceDefinition> defs = new ArrayList<>();

        for (Class<?> clz : def.getConstructor().getParameterTypes()) {
            Constructor<?>[] constructors = clz.getDeclaredConstructors();

            ServiceDefinition paramDef = new ServiceDefinition()
                .setClz(clz)
                .setName(clz.getName())
                .setSingleton(isSingleton(clz))
                .setConstructors(constructors);
            defs.add(paramDef);

            if (paramDef.getConstructor() != null) {
                defs.addAll(findTransitiveDependencies(paramDef));
            }
        }

        return defs;
    }

}
