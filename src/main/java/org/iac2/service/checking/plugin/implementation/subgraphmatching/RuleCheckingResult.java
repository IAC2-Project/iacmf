package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import io.github.edmm.model.component.RootComponent;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RuleCheckingResult {
    private final RuleCheckingOutcome outcome;
    @Setter
    private RootComponent checkerComponent;
    @Setter
    private RootComponent instanceModelComponent;
    @Setter
    private String errorMessage;

    public RuleCheckingResult(RuleCheckingOutcome outcome,
                              RootComponent checkerComponent,
                              RootComponent instanceModelComponent,
                              String errorMessage) {
        this.outcome = outcome;
        this.checkerComponent = checkerComponent;
        this.instanceModelComponent = instanceModelComponent;
        this.errorMessage = errorMessage;
    }

    public RuleCheckingResult(RuleCheckingOutcome outcome) {
        this(outcome, null, null, null);
    }


}
