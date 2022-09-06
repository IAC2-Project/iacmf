package org.iac2.common.model.compliancerule.parameter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DoubleComplianceRuleParameter extends ComplianceRuleParameter{
    private double value;

    public DoubleComplianceRuleParameter(String name, double value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return Double.toString(value);
    }
}
