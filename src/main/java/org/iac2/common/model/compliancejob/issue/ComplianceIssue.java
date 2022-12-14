package org.iac2.common.model.compliancejob.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.iac2.common.model.compliancerule.ComplianceRule;

import java.util.Map;

@Data
@AllArgsConstructor
public class ComplianceIssue {
    private String description;
    private ComplianceRule rule;
    private String type;
    private Map<String, String> properties;
}
