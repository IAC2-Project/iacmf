package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.util.Comparator;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparatorForMatchingWithInstanceModel;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparatorForRuleValidation;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparisonOutcome;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.RelationComparator;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.SemanticComponentComparator;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

public abstract class IsomorphismAlgorithmProvider {
    public static IsomorphismInspector<RootComponent, RootRelation> forRuleValidation(
            ComplianceRule rule,
            Graph<RootComponent, RootRelation> selector,
            Graph<RootComponent, RootRelation> checker) {

        return new VF2SubgraphIsomorphismInspector<>(
                selector,
                checker,
                new ComponentComparatorForRuleValidation(),
                new RelationComparator());
    }

    public static IsomorphismInspector<RootComponent, RootRelation> forSelection(
            ComplianceRule rule,
            Graph<RootComponent, RootRelation> instanceModel,
            Graph<RootComponent, RootRelation> selector) {
        SemanticComponentComparator comparator = getSemanticComponentComparator(rule);
        Comparator<RootComponent> theComparator =
                (c1, c2) -> comparator.compare(c1, c2).outcome() == ComponentComparisonOutcome.MATCH ? 0 : -1;
        return new VF2SubgraphIsomorphismInspector<>(
                instanceModel,
                selector,
                theComparator,
                new RelationComparator());
    }

    public static SemanticComponentComparator getSemanticComponentComparator(ComplianceRule rule) {
        return new ComponentComparatorForMatchingWithInstanceModel(rule);
    }
}
