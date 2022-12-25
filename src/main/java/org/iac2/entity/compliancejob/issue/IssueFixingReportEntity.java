package org.iac2.entity.compliancejob.issue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class IssueFixingReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Boolean isSuccessful;

    @Null
    private String description;

    @ManyToOne
    @JoinColumn(name = "compliance_issue_id", nullable = false)
    private ComplianceIssueEntity complianceIssue;

    public IssueFixingReportEntity(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
