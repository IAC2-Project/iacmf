package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison;

import io.github.edmm.model.component.RootComponent;

public interface SemanticComponentComparator {
    ComponentComparisonResult compare(RootComponent instanceModelComponent, RootComponent ruleComponent);
}
