package org.iac2.repositories.compliancerule.parameter;

import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ComplianceRuleParameterAssignmentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParameterAssignmentRepository extends CrudRepository<ComplianceRuleParameterAssignmentEntity, Long> {
    List<ComplianceRuleParameterAssignmentEntity> findByParameter(ComplianceRuleParameterEntity parameter);
}
