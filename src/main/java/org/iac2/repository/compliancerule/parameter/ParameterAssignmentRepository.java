package org.iac2.repository.compliancerule.parameter;

import java.util.List;

import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ComplianceRuleParameterAssignmentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "compliance-rule-parameter-assignments")
public interface ParameterAssignmentRepository extends CrudRepository<ComplianceRuleParameterAssignmentEntity, Long> {
    List<ComplianceRuleParameterAssignmentEntity> findByParameter(ComplianceRuleParameterEntity parameter);
}
