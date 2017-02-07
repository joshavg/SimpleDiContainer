package de.joshavg.simpledic;

public interface SdiContainerInterface {

    <T> T getInstance(Class<T> clz);
}
