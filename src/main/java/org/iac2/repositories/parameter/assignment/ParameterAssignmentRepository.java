package org.iac2.repositories.parameter.assignment;

import java.util.List;

import org.iac2.entity.compliancerule.parameter.ParameterEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ParameterAssignmentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ParameterAssignmentRepository<B extends ParameterAssignmentEntity> extends CrudRepository<B, Long> {
    List<B> findByParameter(ParameterEntity parameter);
}