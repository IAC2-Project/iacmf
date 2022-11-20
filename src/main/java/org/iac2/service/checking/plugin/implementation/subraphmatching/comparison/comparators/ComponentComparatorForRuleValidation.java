package org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators;

import java.util.Comparator;

import io.github.edmm.model.component.RootComponent;

public class ComponentComparatorForRuleValidation implements Comparator<RootComponent> {
    /***
     * Considers that the two components are compatible iff the checkerComponent is assignalbe to the selectorCompopnent
     * This allows compliance rules similar to the following: "all DBMSs must be MySQL".
     * IMPORTANT! this class assumes that EDMM type and inheritance information is reflected in the java class types for
     * the provided components.
     * @param selectorComponent the selector component to be compared.
     * @param checkerComponent the checker component to be compared.
     * @return 0 if the components are compatible. Otherwise, returns -1.
     */
    public int compare(RootComponent selectorComponent, RootComponent checkerComponent) {
        boolean compatible = selectorComponent.getClass().isAssignableFrom(checkerComponent.getClass());
        return compatible? 0 : -1;
    }
}
