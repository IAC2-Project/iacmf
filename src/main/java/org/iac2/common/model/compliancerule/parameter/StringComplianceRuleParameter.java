package org.iac2.common.model.compliancerule.parameter;

public class StringComplianceRuleParameter extends ComplianceRuleParameter{
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
