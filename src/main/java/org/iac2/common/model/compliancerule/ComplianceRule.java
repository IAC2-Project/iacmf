package org.iac2.common.model.compliancerule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.iac2.common.model.compliancerule.parameter.*;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
public class ComplianceRule {
    private Long id;
    private String type;
    private String location;
    private Collection<ComplianceRuleParameter> parameterAssignments;

    public ComplianceRule(Long id, String type, String location) {
        this.id = id;
        this.type = type;
        this.location = location;
        parameterAssignments = new ArrayList<>();
    }

    public ComplianceRule addStringParameter(String name, String value) {
        this.parameterAssignments.add(new StringComplianceRuleParameter(name, value));

        return this;
    }

    public ComplianceRule addIntParameter(String name, int value) {
        this.parameterAssignments.add(new IntegerComplianceRuleParameter(name, value));

        return this;
    }

    public ComplianceRule addBooleanParameter(String name, boolean value) {
        this.parameterAssignments.add(new BooleanComplianceRuleParameter(name, value));
        return this;
    }

    public ComplianceRule addDoubleParameter(String name, double value) {
        this.parameterAssignments.add(new DoubleComplianceRuleParameter(name, value));
        return this;
    }

    public ComplianceRule addStringCollectionParameter(String name, Collection<String> value) {
        this.parameterAssignments.add(new StringCollectionComplianceRuleParameter(name, value));
        return this;
    }

}
