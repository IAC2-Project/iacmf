package org.iac2.repository.compliancejob;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "fixing-reports")
@Tag(name = "fixing-report")
public interface IssueFixingReportRepository extends CrudRepository<IssueFixingReportEntity, Long> {
    List<IssueFixingReportEntity> findByComplianceIssue(ComplianceIssueEntity issue);

    @Override
    @RestResource(exported = false)
    <S extends IssueFixingReportEntity> S save(S entity);

    @Override
    @RestResource(exported = false)
    <S extends IssueFixingReportEntity> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @RestResource(exported = false)
    void deleteById(Long aLong);

    @Override
    @RestResource(exported = false)
    void delete(IssueFixingReportEntity entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends IssueFixingReportEntity> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
