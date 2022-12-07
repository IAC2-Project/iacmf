package org.iac2.repository.compliancerule;

import java.util.List;

import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "compliance-rules")
public interface ComplianceRuleRepository extends CrudRepository<ComplianceRuleEntity, Long> {
    List<ComplianceRuleEntity> findByType(String type);

    List<ComplianceRuleEntity> findByIsDeleted(Boolean isDeleted);

    @Override
    @RestResource(exported = false)
    void deleteById(Long aLong);

    @Override
    @RestResource(exported = false)
    void delete(ComplianceRuleEntity entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends ComplianceRuleEntity> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
