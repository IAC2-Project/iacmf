package org.iac2.common.model.compliancerule.parameter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BooleanComplianceRuleParameter extends ComplianceRuleParameter {
    private boolean value;

    public BooleanComplianceRuleParameter(String name, boolean value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return Boolean.toString(value);
    }
}
