package de.joshavg.simpledic.exception.integrity;

public class DependencyNotSatisfied extends RuntimeException {
    public DependencyNotSatisfied(Class<?> dependency) {
        super(String.format("Dependency %s not satisfied", dependency.getName()));
    }
}
