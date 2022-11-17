package org.iac2.service.checking.plugin.implementation.subraphmatching;

import io.github.edmm.model.component.RootComponent;
import lombok.Getter;

enum Outcome {
    INVALID_RULE,
    COMPLIANCE_VIOLATION,
    NO_VIOLATIONS
}

@Getter
public class RuleCheckingResult {
    private Outcome outcome;
    private RootComponent checkerComponent;
    private RootComponent instanceModelComponent;
    
    private RuleCheckingResult(Outcome outcome, RootComponent checkerComponent, RootComponent instanceModelComponent) {
        this.outcome = outcome;
        this.checkerComponent = checkerComponent;
        this.instanceModelComponent = instanceModelComponent;
    }
    
    public static RuleCheckingResult forOutcome(Outcome outcome) {
        return new RuleCheckingResult(outcome, null, null);
    }
    
    public void setDetails(RootComponent checkerComponent, RootComponent instanceModelComponent) {
        this.checkerComponent = checkerComponent;
        this.instanceModelComponent = instanceModelComponent;
    }
    
    
    
}
