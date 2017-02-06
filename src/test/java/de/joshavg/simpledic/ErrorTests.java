package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.ContainerInitException;
import de.joshavg.simpledic.exception.SdicInstantiationException;
import de.joshavg.simpledic.exception.integrity.DependencyCycleDetected;
import de.joshavg.simpledic.exception.integrity.DependencyNotSatisfied;
import de.joshavg.simpledic.exception.integrity.DuplicatedServiceClassesFound;
import de.joshavg.simpledic.services.Depends1;
import de.joshavg.simpledic.services.PrivateConstructor;
import java.util.Map;
import org.junit.Test;

public class ErrorTests {

    @Test(expected = DuplicatedServiceClassesFound.class)
    public void testDuplicationDetection() {
        SdiContainer.load("duplicated.properties");
    }

    @Test(expected = DependencyCycleDetected.class)
    public void testCycleDetection() {
        SdiContainer.load("cycle.properties").createInstance(Depends1.class);
    }

    @Test(expected = ClassNotRegistered.class)
    public void requestUnknownClass() {
        SdiContainer.load("almostsane.properties").createInstance(Map.class);
    }

    @Test(expected = DependencyNotSatisfied.class)
    public void requestServiceWithUnregisteredDependency() {
        SdiContainer.load("unknowndep.properties").createInstance(Depends1.class);
    }

    @Test(expected = SdicInstantiationException.class)
    public void privateConstructor() {
        SdiContainer.load("almostsane.properties").createInstance(PrivateConstructor.class);
    }

    @Test(expected = NullPointerException.class)
    public void requestNull() {
        SdiContainer.load("almostsane.properties").createInstance(null);
    }

    @Test(expected = ContainerInitException.class)
    public void loadUnknownFile() {
        SdiContainer.load("unknown.properties");
    }
}
