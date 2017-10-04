package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.integrity.DependencyNotSatisfied;
import de.joshavg.simpledic.exception.integrity.DuplicatedServiceClassesFound;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes a loaded Properties file, analyzes the {@link ServiceDefinition}s and
 * checks them for sanity and integrity
 * <p>
 * These Checks are performed:
 * <ol>
 * <li>duplicated FQCNs</li>
 * <li>dependency cycles</li>
 * <li>dependency availability</li>
 * <li>class availability</li>
 * <li>constructor visibility</li>
 * <li>constructor count</li>
 * </ol>
 */
class IntegrityCheck {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrityCheck.class);

    private final List<ServiceDefinition> definitions;

    IntegrityCheck(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
    }

    void check() {
        List<Class<?>> serviceClasses = definitions.stream()
            .map(ServiceDefinition::getClz)
            .collect(Collectors.toList());

        new UsableConstructorsCheck(definitions).check();
        checkConstructorDependencies(serviceClasses);
        new DependencyCycleCheck(definitions).check();
        checkDuplicateServices(serviceClasses);
    }

    private void checkDuplicateServices(List<Class<?>> services) {
        int size = services.size();
        int distinctSize = services.stream().distinct().collect(Collectors.toList()).size();
        if (size != distinctSize) {
            throw new DuplicatedServiceClassesFound();
        }
    }

    private void checkConstructorDependencies(List<Class<?>> serviceClasses) {
        definitions.stream()
            // get the first and only constructor
            .map(ServiceDefinition::getConstructor)
            // get all parameter types
            .map(Constructor::getParameterTypes)
            .collect((Supplier<ArrayList<Class<?>>>) ArrayList::new,
                (l, arr) -> {
                    List<Class<?>> types = Arrays.asList(arr);
                    l.addAll(types);
                },
                List::addAll)
            // search for services with needed types
            .stream()
            .distinct()
            .forEach((Class<?> t) -> {
                LOG.info("found service dependency: {}", t);
                if (!serviceClasses.contains(t)) {
                    throw new DependencyNotSatisfied(t);
                }
            });
    }

}
