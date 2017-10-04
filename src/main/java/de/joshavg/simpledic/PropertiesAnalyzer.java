package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.integrity.SdicClassNotFound;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PropertiesAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesAnalyzer.class);

    private final Properties props;

    PropertiesAnalyzer(Properties props) {
        this.props = props;
    }

    private boolean isSingleton(String name) {
        return props.containsKey(name + ".singleton") && "true"
            .equals(props.get(name + ".singleton"));
    }

    static boolean isServiceName(String name) {
        return name.matches("^service\\.[^.]+$");
    }

    List<ServiceDefinition> createDefinitions() {
        return props.entrySet().stream()
            .filter(e -> isServiceName(e.getKey().toString()))
            .map(entry -> {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();

                LOG.info("found service class for name {}: {}", key, value);
                try {
                    Class<?> clz = Class.forName(value);
                    Constructor<?>[] cs = clz.getDeclaredConstructors();

                    return new ServiceDefinition()
                        .setClz(clz)
                        .setName(key)
                        .setSingleton(isSingleton(key))
                        .setConstructors(cs);
                } catch (ClassNotFoundException e) {
                    throw new SdicClassNotFound(e);
                }
            }).collect(Collectors.toList());
    }
}
