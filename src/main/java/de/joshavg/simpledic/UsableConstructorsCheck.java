package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.integrity.MoreThanOneConstructor;
import de.joshavg.simpledic.exception.integrity.NoVisibleConstructor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

class UsableConstructorsCheck {

    private final List<ServiceDefinition> definitions;

    UsableConstructorsCheck(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
    }

    void check() {
        for(ServiceDefinition def : definitions) {
            Constructor<?>[] arr = def.getConstructors();

            if (arr.length == 0) {
                return;
            }

            Class<?> clz = arr[0].getDeclaringClass();
            if (arr.length > 1) {
                throw new MoreThanOneConstructor(clz);
            }

            if (!Modifier.isPublic(arr[0].getModifiers())) {
                throw new NoVisibleConstructor(clz);
            }
        }
    }
}
