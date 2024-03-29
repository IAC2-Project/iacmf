package org.iac2.service.checking.common.model;

import lombok.Getter;

public abstract class RuleValidationResult {
    @Getter
    private final String description;

    public RuleValidationResult(String description) {
        this.description = description;
    }


    public abstract boolean isValid();
}
