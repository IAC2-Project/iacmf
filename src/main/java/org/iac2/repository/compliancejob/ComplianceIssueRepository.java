package org.iac2.repository.compliancejob;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "compliance-issues")
@Tag(name="compliance-issue")
public interface ComplianceIssueRepository extends CrudRepository<ComplianceIssueEntity, Long> {
    List<ComplianceIssueEntity> findByExecution(ExecutionEntity execution);

    @Override
    @RestResource(exported = false)
    <S extends ComplianceIssueEntity> S save(S entity);

    @Override
    @RestResource(exported = false)
    <S extends ComplianceIssueEntity> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @RestResource(exported = false)
    void deleteById(Long aLong);

    @Override
    @RestResource(exported = false)
    void delete(ComplianceIssueEntity entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends ComplianceIssueEntity> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}