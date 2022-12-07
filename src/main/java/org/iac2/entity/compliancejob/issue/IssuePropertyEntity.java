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
@DiscriminatorValue(value = IssuePropertyEntity.TYPE_ID)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class IssuePropertyEntity extends KVEntity {
    public static final String TYPE_ID = "2";
    @ManyToOne
    @JoinColumn(name = "compliance_issue_id")
    private ComplianceIssueEntity complianceIssue;

    public IssuePropertyEntity(String key, String value, ComplianceIssueEntity issue) {
        super(key, value);
        this.complianceIssue = issue;
    }
}
