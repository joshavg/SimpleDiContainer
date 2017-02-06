package de.joshavg.simpledic.exception.integrity;

public class DependencyCycleDetected extends RuntimeException {

    public DependencyCycleDetected(Class<?> clz) {
        super(String.format("%s has a dependency cycle", clz));
    }
}
