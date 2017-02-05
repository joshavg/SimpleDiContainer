package de.joshavg.simpledic.exception;

public class NoDefaultConstructorException extends RuntimeException {
    public NoDefaultConstructorException(Throwable cause) {
        super(cause);
    }
}
