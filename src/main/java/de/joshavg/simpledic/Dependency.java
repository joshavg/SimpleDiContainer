package de.joshavg.simpledic;

class Dependency {

    private final Class<?> type;

    private final boolean useSupplier;

    Dependency(Class<?> type, boolean useSupplier) {
        this.type = type;
        this.useSupplier = useSupplier;
    }

    Class<?> getType() {
        return type;
    }

    boolean isUseSupplier() {
        return useSupplier;
    }
}
