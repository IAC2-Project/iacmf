package org.iac2.checking.common.model.compliancerule.parameter;

public class BooleanComplianceRuleParameter extends ComplianceRuleParameter{
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
