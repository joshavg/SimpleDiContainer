package de.joshavg.simpledic.services;

import java.util.function.Supplier;

public class ServiceWithSuppliedDependency {

    private final Supplier<NoDependencies> supp;
    private final Supplier<DependsOnNoDependencies> supp2;

    public ServiceWithSuppliedDependency(Supplier<NoDependencies> supp,
        Supplier<DependsOnNoDependencies> supp2) {
        this.supp = supp;
        this.supp2 = supp2;
    }

    public NoDependencies loadNoDep() {
        return supp.get();
    }

    public DependsOnNoDependencies loadOneDep() {
        return supp2.get();
    }

}
