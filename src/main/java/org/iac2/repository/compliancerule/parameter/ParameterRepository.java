package org.iac2.repository.compliancerule.parameter;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "compliance-rule-parameters")
@Tag(name = "compliance-rule-parameter")
public interface ParameterRepository extends CrudRepository<ComplianceRuleParameterEntity, Long> {
    List<ComplianceRuleParameterEntity> findByName(String name);

    List<ComplianceRuleParameterEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}
