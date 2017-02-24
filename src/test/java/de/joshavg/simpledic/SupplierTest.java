package de.joshavg.simpledic;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import de.joshavg.simpledic.services.NoDependencies;
import de.joshavg.simpledic.services.ServiceWithSuppliedDependency;
import org.junit.Test;

public class SupplierTest {

    @Test
    public void suppliersReturnNewInstances() {
        SdiContainer container = SdiContainer.load("supplier.properties");
        ServiceWithSuppliedDependency service = container.getInstance(ServiceWithSuppliedDependency.class);
        NoDependencies noDep1 = service.loadNoDep();
        NoDependencies noDep2 = service.loadNoDep();

        assertThat(noDep1, not(sameInstance(noDep2)));
    }

    @Test
    public void supplierReturnsOnlyOneSingleton() {
        SdiContainer container = SdiContainer.load("supplier.properties");
        ServiceWithSuppliedDependency service = container.getInstance(ServiceWithSuppliedDependency.class);

        assertThat(service.loadOneDep(), sameInstance(service.loadOneDep()));
    }

}
