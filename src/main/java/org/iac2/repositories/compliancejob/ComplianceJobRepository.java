package org.iac2.repositories.compliancejob;

import java.util.List;

import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;

public interface ComplianceJobRepository extends CrudRepository<ComplianceJobEntity, Long> {
    List<ComplianceJobEntity> findByComplianceRule(ComplianceRuleEntity complianceRule);
}
