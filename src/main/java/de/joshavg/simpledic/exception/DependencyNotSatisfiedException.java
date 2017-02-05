package de.joshavg.simpledic.exception;

public class DependencyNotSatisfiedException extends RuntimeException {
    public DependencyNotSatisfiedException(Class<?> dependency) {
        super(String.format("Dependency %s not satisfied", dependency.getName()));
    }
}
