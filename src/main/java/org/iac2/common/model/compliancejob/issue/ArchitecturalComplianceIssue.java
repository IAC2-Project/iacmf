package org.iac2.common.model.compliancejob.issue;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArchitecturalComplianceIssue extends ComplianceIssue {
    private String issuePath;
    private ArchitecturalIssueType issueType;

    public ArchitecturalComplianceIssue(String description, String issuePath, ArchitecturalIssueType issueType) {
        super(description);
        this.issuePath = issuePath;
        this.issueType = issueType;
    }
}
