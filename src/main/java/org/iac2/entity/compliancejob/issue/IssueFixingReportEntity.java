package org.iac2.entity.compliancejob.issue;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Entity
@NoArgsConstructor
public class IssueFixingReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private Boolean isSuccessful;

    @Null
    private String description;

    @ManyToOne
    @JoinColumn(name = "compliance_issue_id", nullable = false)
    private ComplianceIssueEntity complianceIssue;

    public IssueFixingReportEntity(boolean isSuccessful, ComplianceIssueEntity issue) {
        this.isSuccessful = isSuccessful;
        this.complianceIssue = issue;
    }

}
