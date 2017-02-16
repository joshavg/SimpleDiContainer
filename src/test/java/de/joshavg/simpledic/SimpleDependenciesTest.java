package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import de.joshavg.simpledic.services.DependsOnNoDependencies;
import de.joshavg.simpledic.services.ServiceInterface;
import java.util.List;
import org.junit.Test;

public class SimpleDependenciesTest {

    private static final String FILENAME = "sane.properties";

    @Test
    public void testIntegrityCheck() {
        SdiContainer.load(FILENAME);
    }

    @Test
    public void createOneDependencyService() {
        SdiContainer container = SdiContainer.load(FILENAME);
        container.getInstance(DependsOnNoDependencies.class);
    }

    @Test
    public void serviceImplementationsAreReturned() {
        SdiContainer container = SdiContainer.load(FILENAME);
        List<ServiceInterface> list = container.getInstancesThatImplement(ServiceInterface.class);

        assertThat(list, hasSize(2));
        list.forEach(s -> assertThat(s, instanceOf(ServiceInterface.class)));
    }

    @Test
    public void emptyListIsReturnedOnUnknownInterface() {
        SdiContainer container = SdiContainer.load(FILENAME);
        List<List> list = container.getInstancesThatImplement(List.class);

        assertThat(list, allOf(empty(), not(nullValue())));
    }
}
