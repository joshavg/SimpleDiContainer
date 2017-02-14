package de.joshavg.simpledic;

@SuppressWarnings("WeakerAccess")
public interface SdiContainerInterface {

    <T> T getInstance(Class<T> clz);
}
