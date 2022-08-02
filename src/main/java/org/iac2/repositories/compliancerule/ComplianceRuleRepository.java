package org.iac2.repositories.compliancerule;

import java.util.List;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;

public interface ComplianceRuleRepository extends CrudRepository<ComplianceRuleEntity, Long> {
    List<ComplianceRuleEntity> findByType(String type);
}
