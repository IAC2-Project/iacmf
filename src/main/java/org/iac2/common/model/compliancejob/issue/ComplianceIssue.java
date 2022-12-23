package org.iac2.common.model.compliancejob.issue;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.iac2.common.model.compliancerule.ComplianceRule;

@Data
@AllArgsConstructor
public class ComplianceIssue {
    private String description;
    private ComplianceRule rules;
    private String type;
    private Map<String, String> properties;
}
