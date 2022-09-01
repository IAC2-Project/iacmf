package org.iac2.common.model.compliancejob.issue;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class ComplianceIssue {
    private String description;
}
