package de.joshavg.simpledic;

import com.google.common.annotations.VisibleForTesting;
import de.joshavg.simpledic.exception.ContainerInitException;
import de.joshavg.simpledic.exception.DependencyNotSatisfiedException;
import de.joshavg.simpledic.exception.SdicClassNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SdiContainer {

    private final Properties props;

    @VisibleForTesting
    SdiContainer(Properties props) {
        this.props = props;
    }

    public static SdiContainer load() {
        return load("sdic.properties");
    }

    public static SdiContainer load(String filename) {
        Properties props = new Properties();

        InputStream inputStream = SdiContainer.class.getClassLoader().getResourceAsStream(filename);
        try {
            props.load(inputStream);
        } catch (IOException e) {
            throw new ContainerInitException("failed loading properties", e);
        }

        SdiContainer container = new SdiContainer(props);
        container.integrityCheck();
        return container;
    }

    private void integrityCheck() {
        List<Class<?>> services = fetchAllServices();
        checkConstructorDependencies(services);
    }

    private List<Class<?>> fetchAllServices() {
        return props.entrySet().stream()
                    .filter(e -> isServiceName(e.getKey().toString()))
                    .map(e -> e.getValue().toString())
                    .map(n -> {
                        try {
                            return Class.forName(n);
                        } catch (ClassNotFoundException e) {
                            throw new SdicClassNotFoundException(e);
                        }
                    }).collect(Collectors.toList());
    }

    private void checkConstructorDependencies(List<Class<?>> services) {
        for(Class<?> c : services) {
            Constructor<?>[] constructors = c.getDeclaredConstructors();
            for(Constructor constructor : constructors) {
                Class[] parameterTypes = constructor.getParameterTypes();
                System.out.println(Arrays.toString(parameterTypes));
                System.out.println(Arrays.asList(parameterTypes));
            }
        }
      /*  services.stream()
                // get all constructors
                .map(Class::getDeclaredConstructors)
                .reduce(new ArrayList<Constructor>(),
                        (l, arr) -> {
                            l.addAll(Arrays.asList(arr));
                            return l;
                        },
                        (l1, l2) -> l1)
                // get all parameter types
                .stream()
                .map(Constructor::getParameterTypes)
                .reduce(new ArrayList<Class<?>>(),
                        (l, arr) -> {
                            l.addAll(Arrays.asList(arr));
                            return l;
                        },
                        (l1, l2) -> l1)
                // search for services with needed types
                .stream()
                .distinct()
                .forEach(t -> {
                    if (!services.contains(t)) {
                        throw new DependencyNotSatisfiedException(t);
                    }
                });*/
    }

    @VisibleForTesting
    static boolean isServiceName(String name) {
        return name.startsWith("service.");
    }
}
