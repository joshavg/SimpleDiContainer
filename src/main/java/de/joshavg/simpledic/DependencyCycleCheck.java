package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.integrity.DependencyCycleDetected;
import java.util.List;

class DependencyCycleCheck {

    private final List<ServiceDefinition> definitions;

    DependencyCycleCheck(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
    }

    private ServiceDefinition findDefinition(Class<?> clz) {
        return definitions.stream()
            .filter(d -> d.getClz() == clz)
            .findFirst()
            .orElseThrow(() -> new ClassNotRegistered(clz));
    }

    void check() {
        definitions.forEach(def -> checkConstructorParameter(def.getClz(), null));
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
            checkConstructorParameter(rootClz, innerParamClz);
        }
    }

}
