package org.iac2.repositories.compliancejob;

import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExecutionRepository extends CrudRepository<ExecutionEntity, Long> {
    List<ExecutionEntity> findByStatus(ExecutionStatus status);

    List<ExecutionEntity> findByCurrentStep(ExecutionStep status);

    List<ExecutionEntity> findByComplianceJob(ComplianceJobEntity complianceJob);
}
