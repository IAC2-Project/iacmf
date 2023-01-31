package org.iac2.repository.compliancejob;

import java.util.List;

import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

/***
 * We have dedicated rest controller for this resource.
 */
@RepositoryRestResource(exported = false)
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface ExecutionRepository extends CrudRepository<ExecutionEntity, Long> {

    List<ExecutionEntity> findByStatus(ExecutionStatus status);

    List<ExecutionEntity> findByCurrentStep(ExecutionStep status);

    List<ExecutionEntity> findByComplianceJob(ComplianceJobEntity complianceJob);
}
