package de.joshavg.simpledic;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
public interface SdiContainerInterface {

    <T> T getInstance(Class<T> clz);

    <T> List<T> getInstancesThatImplement(Class<T> clz);

    <T> List<T> getInstancesThatMatch(Predicate<ServiceDefinition> predicate, Class<T> type);

    List<Object> getInstancesThatMatch(Predicate<ServiceDefinition> predicate);
}
