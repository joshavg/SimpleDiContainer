package de.joshavg.simpledic;

import de.joshavg.simpledic.exception.ClassNotRegistered;
import de.joshavg.simpledic.exception.ContainerInitException;
import de.joshavg.simpledic.exception.integrity.DependencyCycleDetected;
import de.joshavg.simpledic.exception.integrity.DependencyNotSatisfied;
import de.joshavg.simpledic.exception.integrity.DuplicatedServiceClassesFound;
import de.joshavg.simpledic.exception.integrity.NoVisibleConstructor;
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
        SdiContainer.load("cycle.properties").getInstance(Depends1.class);
    }

    @Test(expected = ClassNotRegistered.class)
    public void requestUnknownClass() {
        SdiContainer.load("sane.properties").getInstance(Map.class);
    }

    @Test(expected = DependencyNotSatisfied.class)
    public void requestServiceWithUnregisteredDependency() {
        SdiContainer.load("unknowndep.properties").getInstance(Depends1.class);
    }

    @Test(expected = NoVisibleConstructor.class)
    public void privateConstructor() {
        SdiContainer.load("invisible.properties").getInstance(PrivateConstructor.class);
    }

    @Test(expected = ContainerInitException.class)
    public void loadUnknownFile() {
        SdiContainer.load("unknown.properties");
    }
}
