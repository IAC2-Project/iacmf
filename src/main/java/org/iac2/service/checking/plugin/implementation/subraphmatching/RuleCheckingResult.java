package org.iac2.service.checking.plugin.implementation.subraphmatching;

import io.github.edmm.model.component.RootComponent;
import lombok.Getter;

@Getter
public class RuleCheckingResult {
    private final RuleCheckingOutcome outcome;
    private RootComponent checkerComponent;
    private RootComponent instanceModelComponent;

    private String errorMessage;
    
    private RuleCheckingResult(RuleCheckingOutcome outcome, RootComponent checkerComponent, RootComponent instanceModelComponent) {
        this.outcome = outcome;
        this.checkerComponent = checkerComponent;
        this.instanceModelComponent = instanceModelComponent;
    }
    
    public static RuleCheckingResult forOutcome(RuleCheckingOutcome outcome) {
        return new RuleCheckingResult(outcome, null, null);
    }
    
    public void setDetails(RootComponent checkerComponent, RootComponent instanceModelComponent, String errorMessage) {
        this.checkerComponent = checkerComponent;
        this.instanceModelComponent = instanceModelComponent;
        this.errorMessage = errorMessage;
    }

}
