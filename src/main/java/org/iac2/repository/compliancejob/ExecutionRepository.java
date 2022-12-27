package org.iac2.repository.compliancejob;

import java.util.List;

import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/***
 * We have dedicated rest controller for this resource.
 */
@RepositoryRestResource(exported = false)
public interface ExecutionRepository extends CrudRepository<ExecutionEntity, Long> {

    List<ExecutionEntity> findByStatus(ExecutionStatus status);

    List<ExecutionEntity> findByCurrentStep(ExecutionStep status);

    List<ExecutionEntity> findByComplianceJob(ComplianceJobEntity complianceJob);
}
