package org.iac2.repositories.compliancerule.parameter;

import java.util.List;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.springframework.data.repository.CrudRepository;



public interface ParameterRepository extends CrudRepository<ComplianceRuleParameterEntity, Long> {
    List<ComplianceRuleParameterEntity> findByName(String name);
    List<ComplianceRuleParameterEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}
