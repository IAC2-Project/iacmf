package org.iac2.common.model.compliancerule;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import org.iac2.common.model.compliancerule.parameter.BooleanComplianceRuleParameter;
import org.iac2.common.model.compliancerule.parameter.ComplianceRuleParameter;
import org.iac2.common.model.compliancerule.parameter.DoubleComplianceRuleParameter;
import org.iac2.common.model.compliancerule.parameter.IntegerComplianceRuleParameter;
import org.iac2.common.model.compliancerule.parameter.StringCollectionComplianceRuleParameter;
import org.iac2.common.model.compliancerule.parameter.StringComplianceRuleParameter;

@Getter
public class ComplianceRule {
    private final Long id;
    private final String type;
    private final String location;
    /**
     * The type of the issue to be "thrown" if this compliance rule is found to be violated (as configured in the compliance job).
     */
    private final String issueType;
    @Setter
    private Collection<ComplianceRuleParameter> parameterAssignments;

    public ComplianceRule(Long id, String type, String location, String issueType) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.issueType = issueType;
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
