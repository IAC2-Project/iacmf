package org.iac2.service.checking.plugin.implementation.subraphmatching;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.apache.commons.collections4.IteratorUtils;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;

public class RuleChecker {
    private final Graph<RootComponent, RootRelation> instanceModel;


    public RuleChecker(InstanceModel instanceModel) {
        this.instanceModel = EdmmGraphCreator.of(instanceModel.getDeploymentModel());
    }

    public RuleCheckingResult checkCompliance(ComplianceRule rule, Graph<RootComponent, RootRelation> selector,
                                              Graph<RootComponent, RootRelation> checker) {

        final IsomorphismInspector<RootComponent, RootRelation> inspector1 = IsomorphismAlgorithmProvider.forRuleValidation(
                rule, selector, checker);
        final IsomorphismInspector<RootComponent, RootRelation> inspector2 = IsomorphismAlgorithmProvider.forSelection(
                rule, instanceModel, selector);
        final Comparator<RootComponent> semanticComparator = IsomorphismAlgorithmProvider.getSemanticComponentComparator(rule);

        List<GraphMapping<RootComponent, RootRelation>> ruleMappingList = IteratorUtils.toList(inspector1.getMappings());

        if (ruleMappingList.size() != 1) {
            return RuleCheckingResult.forOutcome(Outcome.INVALID_RULE);
        }

        GraphMapping<RootComponent, RootRelation> ruleMapping = ruleMappingList.get(0);
        Iterator<GraphMapping<RootComponent, RootRelation>> iterator = inspector2.getMappings();

        while (iterator.hasNext()) {
            GraphMapping<RootComponent, RootRelation> mappingToInstanceModel = iterator.next();

            for (RootComponent selectorComponent : selector.vertexSet()) {
                RootComponent instanceModelComponent = mappingToInstanceModel.getVertexCorrespondence(selectorComponent, false);
                RootComponent checkerComponent = ruleMapping.getVertexCorrespondence(selectorComponent, false);

                if (semanticComparator.compare(instanceModelComponent, checkerComponent) != 0) {
                    RuleCheckingResult result = RuleCheckingResult.forOutcome(Outcome.COMPLIANCE_VIOLATION);
                    result.setDetails(checkerComponent, instanceModelComponent);

                    return result;
                }
            }

        }

        return RuleCheckingResult.forOutcome(Outcome.NO_VIOLATIONS);
    }
}
