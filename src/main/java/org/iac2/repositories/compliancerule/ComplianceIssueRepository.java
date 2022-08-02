package org.iac2.repositories.compliancerule;

import org.iac2.entity.compliancerule.ComplianceIssueEntity;
import org.springframework.data.repository.CrudRepository;

public interface ComplianceIssueRepository extends CrudRepository<ComplianceIssueEntity, Long> {
}
