package org.iac2.service.checking.common.interfaces;

import lombok.Getter;
import lombok.Setter;

public abstract class RuleValidationResult {
    @Getter
    @Setter
    private String description;


    public abstract boolean isValid();
}
