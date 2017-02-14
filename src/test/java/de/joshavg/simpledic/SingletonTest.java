package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import de.joshavg.simpledic.services.DependsOnNoDependencies;
import de.joshavg.simpledic.services.NoDependencies;
import org.junit.Test;

public class SingletonTest {

    @Test
    public void sameInstances() {
        SdiContainer container = SdiContainer.load("singleton.properties");
        assertThat(container.getInstance(NoDependencies.class),
            sameInstance(container.getInstance(NoDependencies.class)));
    }

    @Test
    public void sameInstanceViaDependency() {
        SdiContainer container = SdiContainer.load("singleton.properties");

        DependsOnNoDependencies i1 = container.getInstance(DependsOnNoDependencies.class);
        DependsOnNoDependencies i2 = container.getInstance(DependsOnNoDependencies.class);

        assertThat(i1, not(sameInstance(i2)));
        assertThat(i1.getDependency(), sameInstance(i2.getDependency()));
    }

}
