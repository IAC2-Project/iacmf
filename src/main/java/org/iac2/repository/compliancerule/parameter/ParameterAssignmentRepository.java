package org.iac2.repository.compliancerule.parameter;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterAssignmentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RepositoryRestResource(path = "compliance-rule-parameter-assignments")
@Tag(name = "compliance-rule-parameter-assignment")
@CrossOrigin( origins = "*" , allowedHeaders = "*" , methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.OPTIONS})
public interface ParameterAssignmentRepository extends CrudRepository<ComplianceRuleParameterAssignmentEntity, Long> {
    List<ComplianceRuleParameterAssignmentEntity> findByParameter(ComplianceRuleParameterEntity parameter);
}
