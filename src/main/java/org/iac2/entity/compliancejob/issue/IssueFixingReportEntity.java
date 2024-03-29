package org.iac2.entity.compliancejob.issue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class IssueFixingReportEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Boolean isSuccessful;

    private String description;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @ManyToOne
    @JoinColumn(name = "compliance_issue_id", nullable = false)
    private ComplianceIssueEntity complianceIssue;

    public IssueFixingReportEntity(boolean isSuccessful, ComplianceIssueEntity complianceIssue) {
        this.isSuccessful = isSuccessful;
        this.complianceIssue = complianceIssue;
        this.complianceIssue.getFixingReports().add(this);
    }
}
