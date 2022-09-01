package org.iac2.checking.common.model.compliancerule.parameter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IntegerComplianceRuleParameter extends ComplianceRuleParameter{
    private int value;

    public IntegerComplianceRuleParameter(String name, int value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return Integer.toString(value);
    }
}
