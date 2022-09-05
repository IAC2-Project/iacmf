package org.iac2.fixing.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

@Setter
@Getter
public class IssueNotSupported extends IssueFixingException {
    private ComplianceIssue issue;

    public IssueNotSupported(ComplianceIssue issue) {
        super("The plugin does not support the following issue: " + issue.toString());
        this.issue = issue;
    }
}
