package org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators;

import java.util.Comparator;

import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;

public class ComponentComparatorForMatchingWithInstanceModel implements Comparator<RootComponent> {
    public int compare(RootComponent instanceModelComponent, RootComponent ruleComponent) {
        if (!ruleComponent.getClass().isAssignableFrom(instanceModelComponent.getClass())) {
            return -1;
        }

        for (Property property : ruleComponent.getProperties().values()) {

        }

        return 0;
    }
}
