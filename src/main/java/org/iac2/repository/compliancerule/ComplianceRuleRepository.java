package org.iac2.repository.compliancerule;

import java.util.List;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "compliance-rules")
public interface ComplianceRuleRepository extends CrudRepository<ComplianceRuleEntity, Long> {
    List<ComplianceRuleEntity> findByType(String type);

    List<ComplianceRuleEntity> findByIsDeleted(Boolean isDeleted);
}
