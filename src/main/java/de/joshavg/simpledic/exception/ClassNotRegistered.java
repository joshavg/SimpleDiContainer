package de.joshavg.simpledic.exception;

public class ClassNotRegistered extends RuntimeException {

    public ClassNotRegistered(Class<?> clz) {
        super(String.format("class not registered in container: %s", clz));
    }
}
