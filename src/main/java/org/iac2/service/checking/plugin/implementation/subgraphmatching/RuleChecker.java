package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.util.Iterator;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.EdmmGraphCreator;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparisonOutcome;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparisonResult;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.SemanticComponentComparator;
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
        SubgraphMatchingRuleValidationResult validationResult = RuleValidator.validateComplianceRule(rule, selector, checker);

        if (validationResult.getOutcome() == RuleValidationOutcome.VALID) {
            final GraphMapping<RootComponent, RootRelation> ruleMapping = validationResult.getRuleMapping();
            final IsomorphismInspector<RootComponent, RootRelation> inspector = IsomorphismAlgorithmProvider.forSelection(
                    rule, instanceModel, selector);
            final SemanticComponentComparator semanticComparator = IsomorphismAlgorithmProvider.getSemanticComponentComparator(rule);

            Iterator<GraphMapping<RootComponent, RootRelation>> iterator = inspector.getMappings();

            while (iterator.hasNext()) {
                GraphMapping<RootComponent, RootRelation> mappingToInstanceModel = iterator.next();

                for (RootComponent selectorComponent : selector.vertexSet()) {
                    RootComponent instanceModelComponent = mappingToInstanceModel.getVertexCorrespondence(selectorComponent, false);
                    RootComponent checkerComponent = ruleMapping.getVertexCorrespondence(selectorComponent, true);

                    // the checker component might be null because the rule does not enforce restrictions on this component.
                    if (checkerComponent != null) {
                        ComponentComparisonResult result = semanticComparator.compare(instanceModelComponent, checkerComponent);

                        if (result.outcome() != ComponentComparisonOutcome.MATCH) {
                            return new RuleCheckingResult(RuleCheckingOutcome.COMPLIANCE_VIOLATION, checkerComponent,
                                    instanceModelComponent, result.propertyName(), result.expression(), String.valueOf(result));
                        }
                    }
                }
            }

            return new RuleCheckingResult(RuleCheckingOutcome.NO_VIOLATIONS);
        }

        return new RuleCheckingResult(RuleCheckingOutcome.INVALID_RULE,
                null, null, null, null, String.valueOf(validationResult.getOutcome()));
    }
}
