package org.iac2.entity.compliancejob.issue;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.entity.KVEntity;

@Entity
@DiscriminatorValue(value = "2")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IssuePropertyEntity extends KVEntity {
    @ManyToOne
    @JoinColumn(name = "compliance_issue_id")
    private ComplianceIssueEntity complianceIssue;

    public IssuePropertyEntity(String key, String value, ComplianceIssueEntity issue) {
        super(key, value);
        this.complianceIssue = issue;
    }
}
