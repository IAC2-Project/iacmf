package org.iac2.repository.productionsystem;

import java.util.List;

import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "production-systems")
public interface ProductionSystemRepository extends CrudRepository<ProductionSystemEntity, Long> {
    List<ProductionSystemEntity> findByIsDeleted(Boolean isDeleted);
}
