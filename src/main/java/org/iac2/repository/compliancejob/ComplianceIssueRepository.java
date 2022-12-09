package org.iac2.repository.compliancejob;

import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComplianceIssueRepository extends CrudRepository<ComplianceIssueEntity, Long> {
    List<ComplianceIssueEntity> findByExecution(ExecutionEntity execution);
}
