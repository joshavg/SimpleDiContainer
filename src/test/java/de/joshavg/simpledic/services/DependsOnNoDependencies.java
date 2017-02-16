package de.joshavg.simpledic.services;

public class DependsOnNoDependencies implements ServiceInterface {

    private final NoDependencies b;

    public DependsOnNoDependencies(NoDependencies b) {
        this.b = b;
    }

    public NoDependencies getDependency() {
        return b;
    }
}
