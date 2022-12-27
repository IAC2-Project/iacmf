package org.iac2.repository.compliancerule.parameter;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterAssignmentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "compliance-rule-parameter-assignments")
@Tag(name = "compliance-rule-parameter-assignment")
public interface ParameterAssignmentRepository extends CrudRepository<ComplianceRuleParameterAssignmentEntity, Long> {
    List<ComplianceRuleParameterAssignmentEntity> findByParameter(ComplianceRuleParameterEntity parameter);
}
