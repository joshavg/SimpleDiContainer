package de.joshavg.simpledic;

import de.joshavg.simpledic.services.DependsOnNoDependencies;
import org.junit.Test;

public class SimpleDependenciesTest {

    private static final String FILENAME = "almostsane.properties";

    @Test
    public void testIntegrityCheck() {
        SdiContainer.load(FILENAME);
    }

    @Test
    public void createOneDependencyService() {
        SdiContainer container = SdiContainer.load(FILENAME);
        container.createInstance(DependsOnNoDependencies.class);
    }
}
