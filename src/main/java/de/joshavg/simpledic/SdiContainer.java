package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.ContainerInitException;
import de.joshavg.simpledic.exception.SdicInstantiationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdiContainer {

    private static final Logger LOG = LoggerFactory.getLogger(SdiContainer.class);

    private final Properties props;
    private final List<ServiceDefinition> definitions;

    private SdiContainer(Properties props, List<ServiceDefinition> definitions) {
        this.props = props;
        this.definitions = definitions;
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

    public <T> T createInstance(Class<T> clz) {
        if (clz == null) {
            throw new NullPointerException();
        }

        LOG.trace("instance ordered: ", clz);
        if (!serviceClasses().contains(clz)) {
            throw new ClassNotRegistered(clz);
        }

        try {
            return new Instantiator<>(clz).createInstance();
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new SdicInstantiationException(e);
        }
    }

}
