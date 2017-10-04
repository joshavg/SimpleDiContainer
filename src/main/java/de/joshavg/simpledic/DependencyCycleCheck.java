package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.integrity.DependencyCycleDetected;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class DependencyCycleCheck {

    private final List<ServiceDefinition> definitions;
    private boolean collectTree;
    private List<Class<?>> dependencyClasses;

    DependencyCycleCheck(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
    }

    private ServiceDefinition findDefinition(Class<?> clz) {
        return definitions.stream()
            .filter(d -> d.getClz() == clz)
            .findFirst()
            .orElseThrow(() -> new ClassNotRegistered(clz));
    }

    DependencyCycleCheck collectDependencyTree() {
        collectTree = true;
        dependencyClasses = new ArrayList<>();
        return this;
    }

    DependencyCycleCheck check() {
        definitions.forEach(def -> checkConstructorParameter(def.getClz(), null));
        return this;
    }

    List<Class<?>> getDependencyTree() {
        return dependencyClasses.stream().distinct().collect(Collectors.toList());
    }

    private void checkConstructorParameter(Class<?> rootClz, Class<?> paramClz) {
        if (rootClz == paramClz) {
            throw new DependencyCycleDetected(rootClz);
        }

        ServiceDefinition def;
        if (paramClz == null) {
            def = findDefinition(rootClz);
        } else {
            def = findDefinition(paramClz);
        }

        Class<?>[] parameterTypes = def.getConstructor().getParameterTypes();
        for (Class<?> innerParamClz : parameterTypes) {
            if (collectTree) {
                dependencyClasses.add(innerParamClz);
            }
            checkConstructorParameter(rootClz, innerParamClz);
        }
    }

}
