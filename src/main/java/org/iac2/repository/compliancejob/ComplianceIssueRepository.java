package org.iac2.repository.compliancejob;

import java.util.List;

import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.springframework.data.repository.CrudRepository;

public interface ComplianceIssueRepository extends CrudRepository<ComplianceIssueEntity, Long> {
    List<ComplianceIssueEntity> findByExecution(ExecutionEntity execution);
}