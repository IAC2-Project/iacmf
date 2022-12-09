package org.iac2.common.model.compliancerule.parameter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StringComplianceRuleParameter extends ComplianceRuleParameter {
    private String value;

    public StringComplianceRuleParameter(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return value;
    }
}
