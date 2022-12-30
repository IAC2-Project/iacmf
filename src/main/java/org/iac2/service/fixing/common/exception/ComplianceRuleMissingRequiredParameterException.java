package org.iac2.service.fixing.common.exception;

import lombok.Getter;
import org.iac2.common.exception.IacmfException;
import org.iac2.common.model.compliancerule.ComplianceRule;

@Getter
public class ComplianceRuleMissingRequiredParameterException extends IacmfException {
    private final ComplianceRule complianceRule;
    private final String missingParameter;

    public ComplianceRuleMissingRequiredParameterException(ComplianceRule complianceRule, String missingParameter) {
        super("The compliance rule with the id: '%s' is missing a parameter: '%s'".formatted(complianceRule.getId(), missingParameter));
        this.complianceRule = complianceRule;
        this.missingParameter = missingParameter;
    }
}
