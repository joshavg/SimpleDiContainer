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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main container that loads a specific properties file
 * and creates instances of defined services
 * <p>
 * Services are given arbitrary names and can be defined as singletons:
 * <code><pre>
 * service.servicename: tld.vendor.project.ServiceClass
 * service.servicename.singleton: true
 * </pre></code>
 * Service names must match the regular expression <code>^service\.[^.]+$</code>
 * <p>
 * Services declare their dependencies via their constructor. Only one
 * constructor per class is allowed.
 * All dependencies must be declared in the same container as the declaring
 * service.
 * <p>
 * After loading the properties file, an {@link IntegrityCheck} will be performed.
 */
public class SdiContainer implements SdiContainerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(SdiContainer.class);

    private final List<ServiceDefinition> definitions;
    private final Map<ServiceDefinition, Object> singletons;

    private SdiContainer(List<ServiceDefinition> definitions) {
        this.definitions = definitions;
        this.singletons = new HashMap<>();
    }

    /**
     * Creates a Container using the file <code>sdic.properties</code> from the classpath.
     *
     * @return the loaded and integrity checked container
     */
    @SuppressWarnings("unused")
    public static SdiContainer load() {
        return load("sdic.properties");
    }

    /**
     * Creates a Container using the properties file at the designated location
     *
     * @param filename the filename that shall be loaded
     * @return the loaded and integrity checked container
     */
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

    /**
     * Creates and returns a service instance of the given class
     * <p>
     * Dependency services are automatically created. Services marked
     * as singletons will be only created once, either as transient
     * or direct dependency.
     *
     * @param clz the type which shall be created
     * @return the created type with declared dependencies fulfilled
     */
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

    /**
     * returns all registered services that implement or extend the given class
     *
     * @param clz parent class or interface
     * @return the list with instances, or an empty list if nothing is found
     */
    @Override
    @SuppressWarnings("WeakerAccess")
    public <T> List<T> getInstancesThatImplement(Class<T> clz) {
        LOG.trace("instances of interface {} ordered", clz);
        return getInstancesThatMatch(def -> clz.isAssignableFrom(def.getClz()), clz);
    }

    /**
     * returns all registered services that match the given predicate on the services' definition
     *
     * @param predicate match criteria
     * @return all services that match the given predicate
     */
    @Override
    public List<Object> getInstancesThatMatch(Predicate<ServiceDefinition> predicate) {
        LOG.trace("testing all known services against the given predicate");
        return getInstancesThatMatch(predicate, Object.class);
    }

    /**
     * returns all registered services that match the given predicate on the services' definition
     *
     * @param predicate match criteria
     * @param clz the type that is common to all services that shall be returned
     * @param <T> the type of the returned objects
     * @return all services that match the given predicate
     */
    @Override
    public <T> List<T> getInstancesThatMatch(Predicate<ServiceDefinition> predicate, Class<T> clz) {
        LOG.trace("testing all known services against the given predicate");
        return definitions.stream()
            .filter(predicate)
            .map(ServiceDefinition::getClz)
            .map(c -> clz.cast(getInstance(c)))
            .collect(Collectors.toList());
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
