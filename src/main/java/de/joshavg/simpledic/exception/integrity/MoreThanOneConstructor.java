package de.joshavg.simpledic.exception.integrity;

public class MoreThanOneConstructor extends RuntimeException {

    public MoreThanOneConstructor(Class<?> clz) {
        super(String.format("class %s has more than one constructor", clz));
    }
}
