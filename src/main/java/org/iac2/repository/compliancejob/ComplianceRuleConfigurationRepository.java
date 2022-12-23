package org.iac2.repository.compliancejob;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "compliance-rule-configurations")
@Tag(name = "compliance-rule-configuration")
public interface ComplianceRuleConfigurationRepository extends CrudRepository<ComplianceRuleConfigurationEntity, Long> {
}
