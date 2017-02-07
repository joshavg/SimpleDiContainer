package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.ContainerInitException;
import de.joshavg.simpledic.exception.SdicInstantiationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdiContainer implements SdiContainerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(SdiContainer.class);

    private final Properties props;
    private final List<ServiceDefinition> definitions;
    private final Map<Class<?>, Object> singletons;

    private SdiContainer(Properties props, List<ServiceDefinition> definitions) {
        this.props = props;
        this.definitions = definitions;
        this.singletons = new HashMap<>();
    }

    public static SdiContainer load() {
        return load("sdic.properties");
    }

    public static SdiContainer load(String filename) {
        Properties props = new Properties();

        InputStream inputStream = SdiContainer.class.getClassLoader().getResourceAsStream(filename);
        if (inputStream == null) {
            throw new ContainerInitException("config file " + filename + " not found", null);
        }

        try {
            props.load(inputStream);
        } catch (IOException e) {
            throw new ContainerInitException("failed loading properties", e);
        }

        IntegrityCheck integrityCheck = new IntegrityCheck(props);
        integrityCheck.check();
        return new SdiContainer(props, integrityCheck.getDefinitions());
    }

    private List<Class<?>> serviceClasses() {
        return definitions.stream().map(ServiceDefinition::getClz).collect(Collectors.toList());
    }

    @Override
    public <T> T getInstance(Class<T> clz) {
        LOG.trace("instance ordered: ", clz);
        if (!serviceClasses().contains(clz)) {
            throw new ClassNotRegistered(clz);
        }

        if (isStoredAsSingleton(clz)) {
            return clz.cast(singletons.get(clz));
        }

        try {
            T instance = new Instantiator<>(clz, this).createInstance();
            handleSingleton(clz, instance);
            return instance;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new SdicInstantiationException(e);
        }
    }

    private <T> void handleSingleton(Class<T> clz, T instance) {
        String key = props.entrySet().stream()
            .filter(e -> Objects.equals(clz.getName(), String.valueOf(e.getValue())))
            .map(e -> e.getKey().toString())
            .findFirst()
            .orElse("");

        if (props.containsKey(key + ".singleton") && "true".equals(props.get(key + ".singleton"))) {
            singletons.put(clz, instance);
        }
    }

    private <T> boolean isStoredAsSingleton(Class<T> clz) {
        return singletons.containsKey(clz);
    }

}
