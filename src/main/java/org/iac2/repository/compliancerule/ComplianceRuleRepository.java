package org.iac2.repository.compliancerule;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComplianceRuleRepository extends CrudRepository<ComplianceRuleEntity, Long> {
    List<ComplianceRuleEntity> findByType(String type);

    List<ComplianceRuleEntity> findByIsDeleted(Boolean isDeleted);
}
