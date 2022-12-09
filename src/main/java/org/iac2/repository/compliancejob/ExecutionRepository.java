package org.iac2.repository.compliancejob;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "executions")
@Tag(name = "execution")
public interface ExecutionRepository extends CrudRepository<ExecutionEntity, Long> {

    List<ExecutionEntity> findByStatus(ExecutionStatus status);

    List<ExecutionEntity> findByCurrentStep(ExecutionStep status);

    List<ExecutionEntity> findByComplianceJob(ComplianceJobEntity complianceJob);

    @Override
    @RestResource(exported = false)
    <S extends ExecutionEntity> S save(S entity);

    @Override
    @RestResource(exported = false)
    <S extends ExecutionEntity> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @RestResource(exported = false)
    void deleteById(Long aLong);

    @Override
    @RestResource(exported = false)
    void delete(ExecutionEntity entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends ExecutionEntity> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
