package org.iac2.entity.compliancejob.issue;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.iac2.common.model.compliancejob.issue.ArchitecturalIssueType;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;

@Entity
@DiscriminatorValue(value = "1")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ArchitecturalComplianceIssueEntity extends ComplianceIssueEntity {
    private ArchitecturalIssueType issueType;
    private String issuePath;

    public ArchitecturalComplianceIssueEntity(ExecutionEntity execution, String description, String issuePath, ArchitecturalIssueType issueType) {
        super(execution, description);
    }
}
