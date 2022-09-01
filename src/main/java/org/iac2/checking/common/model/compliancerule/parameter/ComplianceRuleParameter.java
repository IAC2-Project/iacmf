package org.iac2.checking.common.model.compliancerule.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class ComplianceRuleParameter {
    private String name;

    public abstract String getValueAsString();
}
