package de.joshavg.simpledic.services;

public class DependsOnNoDependencies {

    private final NoDependencies b;

    public DependsOnNoDependencies(NoDependencies b) {
        this.b = b;
    }

    public NoDependencies getDependency() {
        return b;
    }
}
