package org.iac2.repository.compliancejob;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "compliance-jobs")
@Tag(name = "compliance-job")
public interface ComplianceJobRepository extends CrudRepository<ComplianceJobEntity, Long> {
    List<ComplianceJobEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}
