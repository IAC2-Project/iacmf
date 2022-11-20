package org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators;

import io.github.edmm.model.component.RootComponent;

public interface SemanticComponentComparator {
    ComponentComparisonOutcome compare(RootComponent instanceModelComponent, RootComponent ruleComponent);
}
