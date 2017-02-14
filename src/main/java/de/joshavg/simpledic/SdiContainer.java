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
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdiContainer implements SdiContainerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(SdiContainer.class);

    private final List<ServiceDefinition> definitions;
    private final Map<ServiceDefinition, Object> singletons;

    private SdiContainer(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
        this.singletons = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public static SdiContainer load() {
        return load("sdic.properties");
    }

    @SuppressWarnings("WeakerAccess")
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
        return new SdiContainer(integrityCheck.getDefinitions());
    }

    private <T> ServiceDefinition getDefinition(Class<T> clz) {
        return definitions.stream()
            .filter(d -> d.getClz() == clz)
            .findFirst()
            .orElse(null);
    }

    @Override
    public <T> T getInstance(Class<T> clz) {
        LOG.trace("instance ordered: ", clz);
        ServiceDefinition definition = getDefinition(clz);
        if (definition == null) {
            throw new ClassNotRegistered(clz);
        }

        LOG.debug("service name is {}", definition.getName());

        if (isStoredAsSingleton(definition)) {
            return clz.cast(singletons.get(definition));
        }

        try {
            T instance = new Instantiator<>(clz, this).createInstance();
            handleSingleton(definition, instance);
            return instance;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new SdicInstantiationException(e);
        }
    }

    private <T> void handleSingleton(ServiceDefinition definition, T instance) {
        if (definition.isSingleton()) {
            singletons.put(definition, instance);
        }
    }

    private boolean isStoredAsSingleton(ServiceDefinition def) {
        return singletons.containsKey(def);
    }

}
