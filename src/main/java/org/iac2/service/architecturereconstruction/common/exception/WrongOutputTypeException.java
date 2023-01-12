package org.iac2.service.architecturereconstruction.common.exception;

import lombok.Getter;
import org.iac2.common.model.compliancerule.ParameterType;

@Getter
public class WrongOutputTypeException extends ArchitectureReconstructionException {
    private String value;
    private String script;
    private String componentId;
    private ParameterType expectedType;

    public WrongOutputTypeException(String value, String script, String componentId, ParameterType expectedType) {
        super("The output of a bash script (value: %s) is of wrong type (expected: %s)".formatted(value, expectedType.name()));
        this.value = value;
        this.script = script;
        this.componentId = componentId;
        this.expectedType = expectedType;
    }
}
