package org.iac2.repository.compliancejob;

import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComplianceJobRepository extends CrudRepository<ComplianceJobEntity, Long> {
    List<ComplianceJobEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}
