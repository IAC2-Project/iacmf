package org.iac2.service.checking.plugin.implementation.subraphmatching;

import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import lombok.Getter;
import lombok.Setter;
import org.jgrapht.GraphMapping;

@Getter
public class RuleValidationResult {
    private final RuleValidationOutcome outcome;

    @Setter
    private GraphMapping<RootComponent, RootRelation> ruleMapping;

    private RuleValidationResult(RuleValidationOutcome outcome) {
        this.outcome = outcome;
    }

    public static RuleValidationResult forOutcome(RuleValidationOutcome outcome) {
        return new RuleValidationResult(outcome);
    }
}
