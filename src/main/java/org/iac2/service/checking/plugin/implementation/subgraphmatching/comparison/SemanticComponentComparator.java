package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison;

import io.github.edmm.model.component.RootComponent;

public interface SemanticComponentComparator {
    ComponentComparisonOutcome compare(RootComponent instanceModelComponent, RootComponent ruleComponent);
}
