package org.iac2.service.fixing.common.model;

import lombok.Getter;

@Getter
public class IssueFixingReport {
    private final boolean isSuccessful;
    private final String description;

    public IssueFixingReport(boolean isSuccessful, String description) {
        this.isSuccessful = isSuccessful;
        this.description = description;
    }

    public IssueFixingReport(boolean isSuccessful) {
        this(isSuccessful, "");
    }
}
