package org.iac2.fixing.common.model;

import lombok.Getter;

@Getter
public class IssueFixingReport {
    private final boolean isFixed;
    private final String description;

    public IssueFixingReport(boolean isFixed, String description) {
        this.isFixed = isFixed;
        this.description = description;
    }

    public IssueFixingReport(boolean isFixed) {
        this(isFixed, "");
    }
}
