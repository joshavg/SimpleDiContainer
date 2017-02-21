package de.joshavg.simpledic;

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
        List<Class<?>> services = fetchAllServices();
        checkConstructorDependencies(services);
        checkDuplicateServices(services);
        checkCycles();
    }

    private void checkDuplicateServices(List<Class<?>> services) {
        int size = services.size();
        int distinctSize = services.stream().distinct().collect(Collectors.toList()).size();
        if (size != distinctSize) {
            throw new DuplicatedServiceClassesFound();
        }
    }

    private List<Class<?>> fetchAllServices() {
        return props.entrySet().stream()
            .filter(e -> isServiceName(e.getKey().toString()))
            .map(entry -> {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();

                LOG.info("found service class for name {}: {}", key, value);
                try {
                    Class<?> clz = Class.forName(value);
                    definitions.add(new ServiceDefinition()
                        .setClz(clz)
                        .setName(key)
                        .setSingleton(isSingleton(key)));
                    return clz;
                } catch (ClassNotFoundException e) {
                    throw new SdicClassNotFound(e);
                }
            }).collect(Collectors.toList());
    }

    private boolean isSingleton(String name) {
        return props.containsKey(name + ".singleton") && "true"
            .equals(props.get(name + ".singleton"));
    }

    private void checkConstructorDependencies(List<Class<?>> services) {
        services.stream()
            // get the first and only constructor
            .map(Class::getDeclaredConstructors)
            .collect(ArrayList::new,
                this::collectConstructors,
                List::addAll)
            // get all parameter types
            .stream()
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
                if (!services.contains(t)) {
                    throw new DependencyNotSatisfied(t);
                }
            });
    }

    private void collectConstructors(List<Constructor> l, Constructor<?>[] arr) {
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

        l.add(arr[0]);
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
