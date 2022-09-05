package org.iac2.repository.compliancerule.parameter;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ParameterRepository extends CrudRepository<ComplianceRuleParameterEntity, Long> {
    List<ComplianceRuleParameterEntity> findByName(String name);

    List<ComplianceRuleParameterEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}
