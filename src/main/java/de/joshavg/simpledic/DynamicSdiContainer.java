package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.SdicInstantiationException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class DynamicSdiContainer implements SdiContainerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicSdiContainer.class);

    @SuppressWarnings("WeakerAccess")
    public static class UncheckedContainer {

        private final Class<?>[] classes;

        private UncheckedContainer(Class<?>... classes) {
            this.classes = classes;
        }

        public SdiContainerInterface checkIntegrity() {
            List<ServiceDefinition> definitions = new ClassesAnalyzer(classes).createDefinitions();

            List<Class<?>> dependencyClasses = new DependencyCycleCheck(definitions)
                .collectDependencyTree()
                .check()
                .getDependencyTree();

            List<ServiceDefinition> dependencyDefinitions = new ClassesAnalyzer(dependencyClasses)
                .createDefinitions();
            definitions.addAll(dependencyDefinitions);
            new UsableConstructorsCheck(definitions).check();

            return new DynamicSdiContainer(definitions);
        }

    }

    private final List<ServiceDefinition> definitions;
    private final SingletonCache singletonCache;

    private DynamicSdiContainer(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
        this.singletonCache = new SingletonCache();
    }

    @SuppressWarnings("WeakerAccess")
    public static UncheckedContainer fromClasses(Class<?>... classes) {
        return new UncheckedContainer(classes);
    }

    @Override
    public <T> T getInstance(Class<T> clz) {
        ServiceDefinition def = definitions.stream()
            .filter(d -> d.getClz() == clz)
            .findFirst()
            .orElseThrow(() -> new ClassNotRegistered(clz));

        if (singletonCache.isStored(def)) {
            return clz.cast(singletonCache.get(def));
        }

        try {
            T instance = new Instantiator<>(clz, this).createInstance();
            if (def.isSingleton()) {
                singletonCache.store(def, instance);
            }
            return instance;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new SdicInstantiationException(e);
        }
    }

    @Override
    public <T> List<T> getInstancesThatImplement(Class<T> clz) {
        return getInstancesThatMatch(def -> clz.isAssignableFrom(def.getClz()), clz);
    }

    @Override
    public <T> List<T> getInstancesThatMatch(Predicate<ServiceDefinition> predicate,
        Class<T> type) {
        return definitions.stream()
            .filter(predicate)
            .map(ServiceDefinition::getClz)
            .map(c -> type.cast(getInstance(c)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Object> getInstancesThatMatch(Predicate<ServiceDefinition> predicate) {
        return getInstancesThatMatch(predicate, Object.class);
    }

}
