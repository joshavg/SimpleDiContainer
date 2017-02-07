package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import de.joshavg.simpledic.services.NoDependencies;
import org.junit.Test;

public class SingletonTest {

    @Test
    public void sameInstances() {
        SdiContainer container = SdiContainer.load("singleton.properties");
        assertThat(container.getInstance(NoDependencies.class),
            sameInstance(container.getInstance(NoDependencies.class)));
    }

}
