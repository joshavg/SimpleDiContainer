package de.joshavg.simpledic.exception.integrity;

public class NoVisibleConstructor extends RuntimeException {

    public NoVisibleConstructor(Class<?> clz) {
        super(String.format("%s has no visible constructor", clz));
    }
}
