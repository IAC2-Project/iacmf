package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import lombok.Getter;
import lombok.Setter;
import org.iac2.service.checking.common.interfaces.RuleValidationResult;
import org.jgrapht.GraphMapping;

@Getter
public class SubgraphMatchingRuleValidationResult extends RuleValidationResult {
    private final RuleValidationOutcome outcome;

    @Setter
    private GraphMapping<RootComponent, RootRelation> ruleMapping;

    private SubgraphMatchingRuleValidationResult(RuleValidationOutcome outcome) {
        this.setDescription(String.valueOf(outcome));
        this.outcome = outcome;
    }

    public static SubgraphMatchingRuleValidationResult forOutcome(RuleValidationOutcome outcome) {
        return new SubgraphMatchingRuleValidationResult(outcome);
    }

    @Override
    public boolean isValid() {
        return this.outcome == RuleValidationOutcome.VALID;
    }
}
