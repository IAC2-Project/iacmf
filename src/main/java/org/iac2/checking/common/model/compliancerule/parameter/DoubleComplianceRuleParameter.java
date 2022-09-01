package org.iac2.checking.common.model.compliancerule.parameter;

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
