package org.iac2.checking.common.model.compliancerule;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.checking.common.model.compliancerule.parameter.BooleanComplianceRuleParameter;
import org.iac2.checking.common.model.compliancerule.parameter.ComplianceRuleParameter;
import org.iac2.checking.common.model.compliancerule.parameter.DoubleComplianceRuleParameter;
import org.iac2.checking.common.model.compliancerule.parameter.IntegerComplianceRuleParameter;
import org.iac2.checking.common.model.compliancerule.parameter.StringComplianceRuleParameter;

@Setter
@Getter
@NoArgsConstructor
public class ComplianceRule {
    private String type;
    private String location;
    private Collection<ComplianceRuleParameter> parameterAssignments;

    public ComplianceRule(String type, String location) {
        this.type = type;
        this.location = location;
        parameterAssignments = new ArrayList<>();
    }

    public void addStringParameter(String name, String value) {
        this.parameterAssignments.add(new StringComplianceRuleParameter(name, value));
    }

    public void addIntParameter(String name, int value) {
        this.parameterAssignments.add(new IntegerComplianceRuleParameter(name, value));
    }

    public void addBooleanParameter(String name, boolean value) {
        this.parameterAssignments.add(new BooleanComplianceRuleParameter(name, value));
    }

    public void addDoubleParameter(String name, double value) {
        this.parameterAssignments.add(new DoubleComplianceRuleParameter(name, value));
    }

}
