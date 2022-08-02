package org.iac2.repositories.compliancerule.parameter;

import java.util.List;

import org.iac2.entity.compliancerule.parameter.ParameterEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ParameterAssignmentEntity;
import org.springframework.data.repository.CrudRepository;

public interface ParameterAssignmentRepository extends CrudRepository<ParameterAssignmentEntity, Long> {
    List<ParameterAssignmentEntity> findByParameter(ParameterEntity parameter);
}