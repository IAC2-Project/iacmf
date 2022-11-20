package org.iac2.service.checking.plugin.implementation.subraphmatching;

import java.util.Comparator;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.ComponentComparatorForMatchingWithInstanceModel;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.ComponentComparatorForRuleValidation;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.RelationComparator;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

public abstract class IsomorphismAlgorithmProvider {

    public static IsomorphismInspector<RootComponent, RootRelation> forRuleValidation(
            ComplianceRule rule,
            Graph<RootComponent, RootRelation> selector,
            Graph<RootComponent, RootRelation> checker) {

        return new VF2SubgraphIsomorphismInspector<> (
                selector,
                checker,
                new ComponentComparatorForRuleValidation(),
                new RelationComparator());
    }

    public static IsomorphismInspector<RootComponent, RootRelation> forSelection(
            ComplianceRule rule,
            Graph<RootComponent, RootRelation> instanceModel,
            Graph<RootComponent, RootRelation> selector) {
        return new VF2SubgraphIsomorphismInspector<> (
                instanceModel,
                selector,
                new ComponentComparatorForMatchingWithInstanceModel(rule),
                new RelationComparator());
    }

    public static Comparator<RootComponent> getSemanticComponentComparator(ComplianceRule rule) {
        return new ComponentComparatorForMatchingWithInstanceModel(rule);
    }


}
