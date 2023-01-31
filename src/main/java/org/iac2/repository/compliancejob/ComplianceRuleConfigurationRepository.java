package org.iac2.repository.compliancejob;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RepositoryRestResource(path = "compliance-rule-configurations")
@Tag(name = "compliance-rule-configuration")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface ComplianceRuleConfigurationRepository extends CrudRepository<ComplianceRuleConfigurationEntity, Long> {
}
