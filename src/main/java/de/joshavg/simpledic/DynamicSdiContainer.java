package de.joshavg.simpledic;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class DynamicSdiContainer implements SdiContainerInterface {

    @SuppressWarnings("WeakerAccess")
    public static class UncheckedContainer {

        private final Class<?>[] classes;

        private UncheckedContainer(Class<?>... classes) {
            this.classes = classes;
        }

        public DynamicSdiContainer checkIntegrity() {
            List<ServiceDefinition> definitions = new ClassesAnalyzer(classes).createDefinitions();

            new DependencyCycleCheck(definitions).check();
            new UsableConstructorsCheck(definitions).check();

            return new DynamicSdiContainer(definitions);
        }

    }

    private final List<ServiceDefinition> definitions;

    private DynamicSdiContainer(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
    }

    public static UncheckedContainer fromClasses(Class<?>... classes) {
        return new UncheckedContainer(classes);
    }

    @Override
    public <T> T getInstance(Class<T> clz) {
        return null;
    }

    @Override
    public <T> List<T> getInstancesThatImplement(Class<T> clz) {
        return null;
    }

    @Override
    public <T> List<T> getInstancesThatMatch(Predicate<ServiceDefinition> predicate,
        Class<T> type) {
        return null;
    }

    @Override
    public List<Object> getInstancesThatMatch(Predicate<ServiceDefinition> predicate) {
        return null;
    }

}
