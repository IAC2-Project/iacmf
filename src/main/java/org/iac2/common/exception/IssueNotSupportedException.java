package org.iac2.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

@Setter
@Getter
public class IssueNotSupportedException extends IacmfException {
    private ComplianceIssue issue;

    public IssueNotSupportedException(ComplianceIssue issue) {
        super("The following issue type is not supported: " + issue.toString());
        this.issue = issue;
    }
}
