package org.iac2.service.checking.common.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ComplianceRuleTypeNotSupportedException extends ComplianceRuleCheckingException {
    private String complianceRuleType;

    public ComplianceRuleTypeNotSupportedException(String complianceRuleType) {
        super("The following compliance rule type is not supported: " + complianceRuleType);
        this.complianceRuleType = complianceRuleType;
    }
}
