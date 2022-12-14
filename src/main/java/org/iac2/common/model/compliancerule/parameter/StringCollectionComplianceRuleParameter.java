package org.iac2.common.model.compliancerule.parameter;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
public class StringCollectionComplianceRuleParameter extends ComplianceRuleParameter {

    private Collection<String> value;

    public StringCollectionComplianceRuleParameter(String name, Collection<String> value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValueAsString() {
        return String.join(",", value);
    }
}
