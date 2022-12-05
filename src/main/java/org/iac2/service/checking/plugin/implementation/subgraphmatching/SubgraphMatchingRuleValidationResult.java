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

    public SubgraphMatchingRuleValidationResult(String description, RuleValidationOutcome outcome, GraphMapping<RootComponent, RootRelation> ruleMapping) {
        super(description);
        this.outcome = outcome;
        this.ruleMapping = ruleMapping;
    }

    public SubgraphMatchingRuleValidationResult(RuleValidationOutcome outcome, GraphMapping<RootComponent, RootRelation> ruleMapping) {
        this(String.valueOf(outcome), outcome, ruleMapping);
    }

    public SubgraphMatchingRuleValidationResult(RuleValidationOutcome outcome) {
        this(outcome, null);
    }


    public static SubgraphMatchingRuleValidationResult forOutcome(RuleValidationOutcome outcome) {
        return new SubgraphMatchingRuleValidationResult(outcome);
    }

    @Override
    public boolean isValid() {
        return this.outcome == RuleValidationOutcome.VALID;
    }
}
