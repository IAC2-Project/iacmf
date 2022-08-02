package org.iac2.repositories.compliancerule.parameter;

import java.util.List;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ParameterEntity;
import org.springframework.data.repository.CrudRepository;



public interface ParameterRepository extends CrudRepository<ParameterEntity, Long> {
    List<ParameterEntity> findByName(String name);
    List<ParameterEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}