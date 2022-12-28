package org.iac2.common.exception;

import lombok.Getter;

@Getter
public class IssueNotSupportedException extends IacmfException {
    private final String issueType;

    public IssueNotSupportedException(String issueType) {
        this(issueType, "The following issue type is not supported: " + issueType);
    }

    public IssueNotSupportedException(String issueType, String message) {
        super(message);
        this.issueType = issueType;
    }
}
