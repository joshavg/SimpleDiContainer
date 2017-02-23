package de.joshavg.simpledic;

import com.google.common.annotations.VisibleForTesting;
import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.integrity.DependencyCycleDetected;
import de.joshavg.simpledic.exception.integrity.DependencyNotSatisfied;
import de.joshavg.simpledic.exception.integrity.DuplicatedServiceClassesFound;
import de.joshavg.simpledic.exception.integrity.MoreThanOneConstructor;
import de.joshavg.simpledic.exception.integrity.NoVisibleConstructor;
import de.joshavg.simpledic.exception.integrity.SdicClassNotFound;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
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

    private final Properties props;
    private final List<ServiceDefinition> definitions;

    IntegrityCheck(Properties props) {
        this.props = props;
        this.definitions = new ArrayList<>();
    }

    void check() {
        buildDefinitions();
        fetchConstructors();

        checkConstructorDependencies();
        checkDuplicateServices();
        checkCycles();
    }

    private void buildDefinitions() {
        props.entrySet().stream()
            .filter(e -> isServiceName(e.getKey().toString()))
            .forEach(entry -> {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();

                LOG.info("found service class for name {}: {}", key, value);
                try {
                    Class<?> clz = Class.forName(value);
                    definitions.add(new ServiceDefinition()
                        .setClz(clz)
                        .setName(key)
                        .setSingleton(isSingleton(props, key)));
                } catch (ClassNotFoundException e) {
                    throw new SdicClassNotFound(e);
                }
            });
    }

    @VisibleForTesting
    static boolean isSingleton(Properties props, String name) {
        return props.containsKey(name + ".singleton") && "true"
            .equals(props.get(name + ".singleton"));
    }

    private void fetchConstructors() {
        List<Class<?>> services = definitions.stream()
            .map(ServiceDefinition::getClz)
            .collect(Collectors.toList());

        services.stream()
            .map(Class::getDeclaredConstructors)
            .forEach(this::collectConstructors);
    }

    private void collectConstructors(Constructor<?>[] arr) {
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

        findDefinition(clz).setConstructor(arr[0]);
    }

    private ServiceDefinition findDefinition(Class<?> clz) {
        Optional<ServiceDefinition> first = definitions.stream()
            .filter(d -> d.getClz() == clz).findFirst();

        if (!first.isPresent()) {
            throw new ClassNotRegistered(clz);
        }

        return first.get();
    }

    private void checkConstructorDependencies() {
        definitions.stream()
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
                boolean typeKnown = definitions.stream()
                    .map(ServiceDefinition::getClz)
                    .anyMatch(c -> c == t);
                LOG.info("found service dependency: {}", t);
                if (!typeKnown) {
                    throw new DependencyNotSatisfied(t);
                }
            });
    }

    private void checkDuplicateServices() {
        int size = definitions.size();
        int distinctSize = definitions.stream()
            .map(ServiceDefinition::getClz)
            .distinct()
            .collect(Collectors.toList())
            .size();
        if (size != distinctSize) {
            throw new DuplicatedServiceClassesFound();
        }
    }

    private void checkCycles() {
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

    static boolean isServiceName(String name) {
        return name.matches("^service\\.[^.]+$");
    }

    List<ServiceDefinition> getDefinitions() {
        return definitions;
    }
}
