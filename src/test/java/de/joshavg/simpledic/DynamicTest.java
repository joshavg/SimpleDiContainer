package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import de.joshavg.simpledic.services.Depends1;
import de.joshavg.simpledic.services.NoDependencies;
import org.junit.Test;

public class DynamicTest {

    @Test
    public void noDependenciesInstanceCanBeObtained() {
        NoDependencies instance = DynamicSdiContainer
            .fromClasses(NoDependencies.class)
            .checkIntegrity()
            .getInstance(NoDependencies.class);
        assertThat(instance, not(nullValue()));
    }

    @Test
    public void cyclicDependenciesAreRecognized() {
        DynamicSdiContainer
            .fromClasses(Depends1.class)
            .checkIntegrity();
    }

}
